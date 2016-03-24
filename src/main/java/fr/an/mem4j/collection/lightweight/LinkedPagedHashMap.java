package fr.an.mem4j.collection.lightweight;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import fr.an.mem4j.collection.util.MutableMapEntry;

/**
 * <PRE>
 *                         +--------------------+     +---
 * +----------------+  <---|-prevPage, nextPage-|---> |
 * | 4: int size    |      | key0,  key1,  ...  |     |
 * | 4/8 ptr: head -|----> | value0,value1,...  |     |
 * | 4/8 ptr: tail -|--    +--------------------+     +----
 * |                |  \                   \     \       /
 * |                |   \                   |     |     /
 * |                |     ------------------------------
 * |                |                       |     |
 * | hashUsedCount0 |    +------------------+-----+----------------+
 * | hashPages0    -|--> |(size+ n* ptr)  page0 page1 page2 ...    |
 * |                |    +-----------------------------------------+
 * |                |    +-----------------------------------------+
 * | hashOffsets0  -|--> |                off0  off1  off2  ...    |
 * |                |    +-----------------------------------------+
 * |                |
 * | hashUsedCount1 |    +------------------+-----+--------------------+
 * | hashPages1    -|--> |(size+ n* ptr)  page0 page1 page2 ...        |
 * |                |    +---------------------------------------------+
 * |                |    +---------------------------------------------+
 * | hashOffsets1  -|--> |                off0  off1  off2  ...        |
 * |                |    +---------------------------------------------+
 * +----------------+
 * </PRE>
 */
public class LinkedPagedHashMap<K,V> extends AbstractMap<K,V> {

    private static final int NONE = -1;
    private static final int INIT_HASH_LEN = 23;
    
    protected int size;
    
    protected LinkedEntriesPagedNode<K,V> headPage;
    protected LinkedEntriesPagedNode<K,V> tailPage;

    
    protected int hash0UsedCount;
    protected LinkedEntriesPagedNode<K,V>[] hash0Pages;
    protected int[] hash0Offsets;
    
    protected int hash1UsedCount;
    protected LinkedEntriesPagedNode<K,V>[] hash1Pages;
    protected int[] hash1Offsets;

    protected int hash2UsedCount;
    protected LinkedEntriesPagedNode<K,V>[] hash2Pages;
    protected int[] hash2Offsets;

    // ------------------------------------------------------------------------

    public LinkedPagedHashMap() {
    }
    
    // implements java.util.Map
    // ------------------------------------------------------------------------
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(Object key) {
        return containsKey0((K) key);
    }
    
    public boolean containsKey0(K key) {
        int keyHash = hashCodeOf(key);
        HashSlot hashSlot = lookupHashSlot(key, keyHash);
        return hashSlot != null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        return get0((K) key);
    }
    
    protected V get0(K key) {
        V res;
        int keyHash = hashCodeOf(key);
        HashSlot hashSlot = lookupHashSlot(key, keyHash);
        if (hashSlot != null) {
            res = getValueAtIndirectHashSlot(hashSlot);
        } else {
            res = null;
        }
        return res;
    }
    
    @Override
    public V put(K key, V value) {
        V res;
        int keyHash = hashCodeOf(key);
        HashSlot hashSlot = lookupHashSlot(key, keyHash);
        if (hashSlot != null) {
            // replace existing
            res = setKeyValueAtIndirectHashSlot(hashSlot, key, value);
        } else {
            // insert .. 
            res = null;
            size++;
            // insert key-value in linked pages list, get page+offset
            LinkedEntriesPagedNode<K,V> page = null;
            if (tailPage == null) {
                headPage = tailPage = new LinkedEntriesPagedNode<>();
            }
            page = tailPage;
            if (tailPage.isFull()) {
                // TODO (may move some elts to prev page if empty room ... + update offset in corresponding hashs)
                // alloc new page
                LinkedEntriesPagedNode<K,V> newPage = new LinkedEntriesPagedNode<>();
                newPage.prevPage = tailPage;
                tailPage.nextPage = newPage;
                this.tailPage = newPage;
                page = newPage;
            }
            assert !page.isFull();
            int pageOffset = page.size();
            page.insertAt(pageOffset, key, value);
            
            // insert page+offset in hash: find empty insertion slot, maybe alloc/realloc(+reindex) hash
            hashSlot = insertionHashSlot(key, keyHash);
            nthHashPages(hashSlot.nthHash)[hashSlot.slot] = page;
            nthHashOffsets(hashSlot.nthHash)[hashSlot.slot] = pageOffset;
            nthHashUsedCountIncr(hashSlot.nthHash, +1);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        return removeKey((K) key);
    }

    public V removeKey(K key) {
        V res;
        int keyHash = hashCodeOf(key);
        HashSlot hashSlot = lookupHashSlot(key, keyHash);
        if (hashSlot != null) {
            size--;
            LinkedEntriesPagedNode<K, V> page = nthHashPages(hashSlot.nthHash)[hashSlot.slot];
            int pageOffset = nthHashOffsets(hashSlot.nthHash)[hashSlot.slot];
            res = page.getValueAt(pageOffset);
            
            // remove indirect page/offset from hash (unshift conflicts if any)
            nthHashUsedCountIncr(hashSlot.nthHash, -1);
            nthHashPages(hashSlot.nthHash)[hashSlot.slot] = null;
            unshiftNextHashConflicts(nthHashPages(hashSlot.nthHash), nthHashOffsets(hashSlot.nthHash), hashSlot.slot);

            // update remaining page offset in corresponding hashs pages+offsets
            for(int i = pageOffset + 1, len = page.size; i < len; i++) {
                K reindexKey = page.getKeyAt(i);
                int reindexKeyHash = hashCodeOf(reindexKey);
                HashSlot reindexHashSlot = lookupHashSlot(reindexKey, reindexKeyHash);
                assert reindexHashSlot != null;
                LinkedEntriesPagedNode<K, V>[] reindexNthHashPages = nthHashPages(reindexHashSlot.nthHash);
                reindexNthHashPages[reindexHashSlot.slot] = page;
                nthHashOffsets(reindexHashSlot.nthHash)[reindexHashSlot.slot] = i - 1;
            }

            page.removeAt(pageOffset);
            // empty page => remove page (TODO if almost empty => rebalance)
            if (page.size() == 0) {
                LinkedEntriesPagedNode<K, V> prevPage = page.prevPage;
                LinkedEntriesPagedNode<K, V> nextPage = page.nextPage;
                if (prevPage != null) {
                    prevPage.nextPage = nextPage;
                } else {
                    headPage = nextPage;
                }
                if (nextPage != null) {
                    nextPage.prevPage = prevPage;
                } else {
                    tailPage = prevPage;
                }
            }
        } else {
            // not found, nothing to remove
            res = null;
        }
        return res;
    }

    protected void unshiftNextHashConflicts(LinkedEntriesPagedNode<K, V>[] hashPages, int[] hashPageOffsets, int startSlot) {
        final int hashLen = hashPages.length;
        for(;;) {
            int lastSlotConflict = startSlot;
            for(int currSlot = nextModulo(startSlot, hashLen); currSlot != startSlot; currSlot = nextModulo(currSlot, hashLen)) {
                LinkedEntriesPagedNode<K, V> page = hashPages[currSlot];
                if (page == null) {
                    break;
                }
                int pageOffset = hashPageOffsets[currSlot];
                K currKey = page.getKeyAt(pageOffset);
                int currKeyHash = hashCodeOf(currKey);
                int currKeySlot = posOf(currKeyHash, hashPages);
                if (currKeySlot == startSlot) {
                    lastSlotConflict = currKeySlot; 
                }
            }
            if (lastSlotConflict != startSlot) {
                // found conflict: solve by writing start(empty) with last, continue from last
                hashPages[startSlot] = hashPages[lastSlotConflict];
                hashPageOffsets[startSlot] = hashPageOffsets[lastSlotConflict];
                hashPages[lastSlotConflict] = null;
                hashPageOffsets[lastSlotConflict] = 0;
                startSlot = lastSlotConflict;
            } else {
                break;
            }
        }
    }

    @Override
    public void clear() {
        size = 0;
        headPage = tailPage = null;
        hash0Pages = hash1Pages = hash2Pages = null;
        hash0Offsets = hash1Offsets = hash2Offsets = null;
        hash0UsedCount = hash1UsedCount = hash2UsedCount = 0;
    }

    @Override
    public Set<K> keySet() {
        return new AbstractSet<K>() {
            @Override
            public Iterator<K> iterator() {
                return new InnerKeyIterator(headPage, -1);
            }
            @Override
            public int size() {
                return size;
            }            
        };
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return new AbstractSet<java.util.Map.Entry<K, V>>() {
            @Override
            public Iterator<java.util.Map.Entry<K, V>> iterator() {
                return new InnerEntryIterator(headPage, -1);
            }
            @Override
            public int size() {
                return size;
            }            
        };
    }

    protected class InnerKeyIterator implements Iterator<K> {
        private LinkedEntriesPagedNode<K,?> currPage;
        private int currPageOffset;
        
        public InnerKeyIterator(LinkedEntriesPagedNode<K,?> currPage, int currPageOffset) {
            this.currPage = currPage;
            this.currPageOffset = currPageOffset;
        }

        @Override
        public boolean hasNext() {
            return currPage != null && (currPageOffset + 1 < currPage.size || currPage.nextPage != null);
        }

        @Override
        public K next() {
            if (currPageOffset + 1 < currPage.size) {
                currPageOffset++;
            } else if (currPage.nextPage != null) {
                currPage = currPage.nextPage;
                currPageOffset = 0;
            }
            return currPage.getKeyAt(currPageOffset);
        }
        
    }
    
    protected class InnerEntryIterator implements Iterator<java.util.Map.Entry<K, V>> {
        private LinkedEntriesPagedNode<K,V> currPage;
        private int currPageOffset;
        private MutableMapEntry<K,V> entry = new MutableMapEntry<K,V>();
        
        public InnerEntryIterator(LinkedEntriesPagedNode<K,V> currPage, int currPageOffset) {
            this.currPage = currPage;
            this.currPageOffset = currPageOffset;
        }

        @Override
        public boolean hasNext() {
            return currPage != null && (currPageOffset + 1 < currPage.size || currPage.nextPage != null);
        }

        @Override
        public java.util.Map.Entry<K, V> next() {
            if (currPageOffset + 1 < currPage.size) {
                currPageOffset++;
            } else if (currPage.nextPage != null) {
                currPage = currPage.nextPage;
                currPageOffset = 0;
            }
            K key = currPage.getKeyAt(currPageOffset);
            V value = currPage.getValueAt(currPageOffset);
            entry._setCurr(key, value);
            return entry;
        }
    }
    
    
    // ------------------------------------------------------------------------

    protected static class HashSlot {
        int nthHash;
        int slot;
        // LinkedEntriesPagedNode/*<K,V>*/ nthHashPages;
        // K key;
        // V value;
        
        public HashSlot() {
        }
        public HashSlot(int nthHash, int offset) {
            this.nthHash = nthHash;
            this.slot = offset;
        }
        
    }
    
    
    private static int nextModulo(int pos, int len) {
        int res = pos + 1;
        if (res == len) {
            res = 0;
        }
        return res;
    }

    protected int hashCodeOf(Object key) {
        return (key != null)? key.hashCode() : 0;
    }

    protected boolean keyEquals(K key1, K key2) {
        return (key1 == key2 || // both null or same 
                (key1 != null && key2 != null && key1.equals(key2)));
    }
    
    protected HashSlot lookupHashSlot(K key, int keyHash) {
        // TOCHANGE? may iterate in order on hash0,1,2... to minimize conflicts
        if (hash0Pages != null) {
            int offset = lookupHashSlot(key, keyHash, hash0Pages, hash0Offsets);
            if (offset != NONE) {
                return new HashSlot(0, offset); 
            }
        }
        if (hash1Pages != null) {
            int offset = lookupHashSlot(key, keyHash, hash1Pages, hash1Offsets);
            if (offset != NONE) {
                return new HashSlot(1, offset); 
            }
        }
        if (hash2Pages != null) {
            int offset = lookupHashSlot(key, keyHash, hash2Pages, hash2Offsets);
            if (offset != NONE) {
                return new HashSlot(2, offset); 
            }
        }
        return null;
    }
    

    protected int lookupHashSlot(K key, int keyHash, LinkedEntriesPagedNode<K,V>[] hashPages, int[] hashOffsets) {
        if (hashPages != null) {
            final int hashLen = hashPages.length;
            final int firstPos = keyHash % hashLen;
            if (hashPages[firstPos] != null) {
                K posKey = hashPages[firstPos].getKeyAt(hashOffsets[firstPos]);
                if (keyEquals(key, posKey)) {
                    return firstPos;
                } else {
                    // conflict
                    for(int pos = nextModulo(firstPos, hashLen); pos != firstPos; pos=nextModulo(pos, hashLen)) {
                        if (hashPages[pos] != null) {
                            posKey = hashPages[pos].getKeyAt(hashOffsets[pos]);
                            if (keyEquals(key, posKey)) {
                                return pos;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return NONE;
    }
    
    @SuppressWarnings("unchecked")
    protected HashSlot insertionHashSlot(K key, int keyHash) {
        // TOCHANGE? may iterate in order on hash0,1,2... to minimize conflicts
        // may also use arg min (hash0123.length - hash0123UsedCount)
        if (hash0Pages != null) {
            int offset = insertionHashSlot(key, keyHash, hash0Pages, hash0Offsets);
            if (offset != NONE) {
                return new HashSlot(0, offset); 
            }
        }
        if (hash1Pages != null) {
            int offset = insertionHashSlot(key, keyHash, hash1Pages, hash1Offsets);
            if (offset != NONE) {
                return new HashSlot(1, offset); 
            }
        }
        if (hash2Pages != null) {
            int offset = insertionHashSlot(key, keyHash, hash2Pages, hash2Offsets);
            if (offset != NONE) {
                return new HashSlot(2, offset); 
            }
        }
        
        // full.. alloc/realloc(+reindex) hash
        if (hash0Pages == null) {
            int allocSize = Math.max(INIT_HASH_LEN, size);
            hash0Pages = new LinkedEntriesPagedNode[allocSize];
            hash0Offsets = new int[allocSize];
            return new HashSlot(0, posOf(keyHash, hash0Pages));
        } else if (hash1Pages == null) {
            int allocSize = Math.max(INIT_HASH_LEN, size);
            hash1Pages = new LinkedEntriesPagedNode[allocSize];
            hash1Offsets = new int[allocSize];
            return new HashSlot(1, posOf(keyHash, hash1Pages));
        } else if (hash2Pages == null) {
            int allocSize = Math.max(INIT_HASH_LEN, size);
            hash2Pages = new LinkedEntriesPagedNode[allocSize];
            hash2Offsets = new int[allocSize];
            return new HashSlot(2, posOf(keyHash, hash2Pages));
        }
        
        // all full.. need realloc + reindex! 
        // choose smaller one to reindex, or 2-smallers, or all?
        int nthSize = hash0Pages.length;
        int nth = 0;
        LinkedEntriesPagedNode<K,V>[] nthPages = hash0Pages;
        int[] nthOffsets = hash0Offsets;
        if (hash1Pages.length < nthSize) {
            nth = 1;
            nthSize = hash1Pages.length;
            nthPages = hash1Pages;
            nthOffsets = hash1Offsets;
        }
        if (hash2Pages.length < nthSize) {
            nth = 2;
            nthSize = hash2Pages.length;
            nthPages = hash2Pages;
            nthOffsets = hash2Offsets;
        }
        
        int allocSize = nthSize*2+1; // heuristic increase size
        LinkedEntriesPagedNode<K,V>[] newNthPages = new LinkedEntriesPagedNode[allocSize];
        int[] newNthOffsets = new int[allocSize];
        for(int i = 0; i < nthPages.length; i++) {
            LinkedEntriesPagedNode<K,V> page = nthPages[i];
            nthPages[i] = null; // gc friendly: clear prev refs
            int pageOffset = nthOffsets[i];
            K reindexKey = page.getKeyAt(pageOffset);
            int reindexKeyHash = hashCodeOf(reindexKey);
            int newI = insertionHashSlot(reindexKey, reindexKeyHash, newNthPages, newNthOffsets);
            newNthPages[newI] = page;
            newNthOffsets[newI] = pageOffset;
        }
        setNthHashPageAndOffsets(nth, newNthPages, newNthOffsets);
        
        int offset = insertionHashSlot(key, keyHash, newNthPages, newNthOffsets);
        return new HashSlot(nth, offset);
    }
    
    protected int posOf(int keyHash, LinkedEntriesPagedNode<K,V>[] hashPages) {
        return keyHash % hashPages.length;
    }
    
    protected int insertionHashSlot(K key, int keyHash, LinkedEntriesPagedNode<K,V>[] hashPages, int[] hashOffsets) {
        if (hashPages != null) {
            final int hashLen = hashPages.length;
            final int firstPos = keyHash % hashLen;
            if (hashPages[firstPos] == null) {
                return firstPos;
            } else {
                // conflict
                for(int pos = nextModulo(firstPos, hashLen); pos != firstPos; pos=nextModulo(pos, hashLen)) {
                    if (hashPages[pos] == null) {
                        return pos;
                    }
                }
            }
        }
        return NONE;
    }
    
    protected LinkedEntriesPagedNode<K,V>[] nthHashPages(int n) {
        switch(n) {
        case 0: return hash0Pages;
        case 1: return hash1Pages;
        case 2: return hash2Pages;
        default: throw new IllegalArgumentException();
        }
    }
    protected int[] nthHashOffsets(int n) {
        switch(n) {
        case 0: return hash0Offsets;
        case 1: return hash1Offsets;
        case 2: return hash2Offsets;
        default: throw new IllegalArgumentException();
        }
    }
    
    protected void setNthHashPageOffsetAt(int n, int pos, LinkedEntriesPagedNode<K,V> page, int pageOffset) {
        nthHashPages(n)[pos] = page;
        nthHashOffsets(n)[pos] = pageOffset;
    }

    protected K getKeyAtIndirectHashSlot(HashSlot hashSlot) {
        LinkedEntriesPagedNode<K, V> page = nthHashPages(hashSlot.nthHash)[hashSlot.slot];
        int pageOffset = nthHashOffsets(hashSlot.nthHash)[hashSlot.slot];
        return page.getKeyAt(pageOffset);
    }

    protected V getValueAtIndirectHashSlot(HashSlot hashSlot) {
        LinkedEntriesPagedNode<K, V> page = nthHashPages(hashSlot.nthHash)[hashSlot.slot];
        int pageOffset = nthHashOffsets(hashSlot.nthHash)[hashSlot.slot];
        return page.getValueAt(pageOffset);
    }
    
    protected V setKeyValueAtIndirectHashSlot(HashSlot hashSlot, K key, V value) {
        LinkedEntriesPagedNode<K, V> page = nthHashPages(hashSlot.nthHash)[hashSlot.slot];
        int pageOffset = nthHashOffsets(hashSlot.nthHash)[hashSlot.slot];
        V res = page.getValueAt(pageOffset);
        page.setKeyValueAt(pageOffset, key, value);
        return res;
    }
    
    protected void setNthHashPageAndOffsets(int n, LinkedEntriesPagedNode<K,V>[] hashPages, int[] hashOffsets) {
        switch(n) {
        case 0: hash0Pages = hashPages; hash0Offsets = hashOffsets; break;
        case 1: hash1Pages = hashPages; hash1Offsets = hashOffsets; break;
        case 2: hash2Pages = hashPages; hash2Offsets = hashOffsets; break;
        default: throw new IllegalArgumentException();
        }
    }

    protected void nthHashUsedCountIncr(int n, int incr) {
        switch(n) {
        case 0: hash0UsedCount += incr; break;
        case 1: hash1UsedCount += incr; break;
        case 2: hash2UsedCount += incr; break;
        default: throw new IllegalArgumentException();
        }   
    }
    protected int nthHashUsedCount(int n) {
        switch(n) {
        case 0: return hash0UsedCount;
        case 1: return hash1UsedCount;
        case 2: return hash2UsedCount;
        default: throw new IllegalArgumentException();
        }   
    }
}
