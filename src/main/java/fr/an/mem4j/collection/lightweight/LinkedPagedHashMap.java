package fr.an.mem4j.collection.lightweight;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static final Logger LOG = LoggerFactory.getLogger(LinkedPagedHashMap.class);
    
    private static final boolean DEBUG_CHECK_INVARIANTS = false;
    
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
            nthHashPartition(hashSlot.hashPart).setPageOffsetAtSlot(hashSlot.slot, page, pageOffset);
            nthHashUsedCountIncr(hashSlot.hashPart, +1);
        }
        if (DEBUG_CHECK_INVARIANTS) checkInvariants();
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
            HashPartition<K, V> hashPart = nthHashPartition(hashSlot.hashPart);
            res = hashPart.getValueAtIndirectSlot(hashSlot.slot);
            
            // remove indirect page/offset from hash (unshift conflicts if any)
            nthHashUsedCountIncr(hashSlot.hashPart, -1);

            LinkedEntriesPagedNode<K, V> page = hashPart.getPageAtSlot(hashSlot.slot);
            int pageOffset = hashPart.getPageOffsetAtSlot(hashSlot.slot);
            hashPart.setPageOffsetAtSlot(hashSlot.slot, null, 0);
            hashPart.unshiftNextHashConflicts(this, hashSlot.slot);

            // update remaining page offset in corresponding hashs pages+offsets
            for(int i = pageOffset + 1, len = page.size; i < len; i++) {
                K reindexKey = page.getKeyAt(i);
                int reindexKeyHash = hashCodeOf(reindexKey);
                HashSlot reindexHashSlot = lookupHashSlot(reindexKey, reindexKeyHash);
                assert reindexHashSlot != null;
                if (reindexHashSlot == null) {
                    LOG.error("interal err: slot not found");
                }
                LinkedEntriesPagedNode<K, V>[] reindexNthHashPages = nthHashPages(reindexHashSlot.hashPart);
                reindexNthHashPages[reindexHashSlot.slot] = page;
                nthHashOffsets(reindexHashSlot.hashPart)[reindexHashSlot.slot] = i - 1;
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
        if (DEBUG_CHECK_INVARIANTS) checkInvariants();
        return res;
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

    protected void checkInvariants() {
        if (hash0Pages != null) {
            new HashPartition<>(this, 0, hash0Pages, hash0Offsets).checkInvariants();
        }
        if (hash1Pages != null) {
            new HashPartition<>(this, 1, hash1Pages, hash1Offsets).checkInvariants();
        }
        if (hash2Pages != null) {
            new HashPartition<>(this, 2, hash2Pages, hash2Offsets).checkInvariants();
        }
    }
    
    /** temporary wrapper for {hashPart,slot} */
    protected static class HashSlot {
        protected int hashPart;
        protected int slot;
        // LinkedEntriesPagedNode/*<K,V>*/ nthHashPages;
        // K key;
        // V value;
        
        public HashSlot() {
        }
        public HashSlot(int hashPart, int offset) {
            this.hashPart = hashPart;
            this.slot = offset;
        }
        
    }

    /** temporary wrapper for {hashPart,  hashPages=..(hashPart),hashOffsets=..(hashPart) } */
    protected static class HashPartition<K,V> {
        protected LinkedPagedHashMap<K,V> owner;
        protected int hashPart;
        protected LinkedEntriesPagedNode<K, V>[] hashPages;
        protected int[] hashOffsets;
        
        public HashPartition(LinkedPagedHashMap<K,V> owner, int hashPart, LinkedEntriesPagedNode<K,V>[] hashPages, int[] hashOffsets) {
            this.owner = owner;
            this.hashPart = hashPart;
            this.hashPages = hashPages;
            this.hashOffsets = hashOffsets;
        }

        public void setPageOffsetAtSlot(int slot, LinkedEntriesPagedNode<K, V> page, int pageOffset) {
            hashPages[slot] = page;
            hashOffsets[slot] = pageOffset;            
        }

        public LinkedEntriesPagedNode<K,V> getPageAtSlot(int slot) {
            return hashPages[slot];
        }
        public int getPageOffsetAtSlot(int slot) {
            return hashOffsets[slot];            
        }

        public K getKeyAtIndirectSlot(int slot) {
            LinkedEntriesPagedNode<K, V> page = hashPages[slot];
            int pageOffset = hashOffsets[slot];
            return page.getKeyAt(pageOffset);
        }

        public V getValueAtIndirectSlot(int slot) {
            LinkedEntriesPagedNode<K, V> page = hashPages[slot];
            int pageOffset = hashOffsets[slot];
            return page.getValueAt(pageOffset);
        }
        
        /* rehash all keys in same cluster (Linear Probing algorithm)  */
        protected void unshiftNextHashConflicts(LinkedPagedHashMap<K, V> owner, final int slotStart) {
            final int hashLen = hashPages.length;
            for (int i = nextModulo(slotStart, hashLen); ; i = nextModulo(i, hashLen)) {
                LinkedEntriesPagedNode<K, V> page = hashPages[i];
                if (page == null) {
                    break;
                }
                int pageOffset = hashOffsets[i];
                K key = page.getKeyAt(pageOffset);
                int keyHash = owner.hashCodeOf(key);
                int keyPos = posOf(keyHash, hashPages.length);
                if (keyPos != i) {
                    // delete and reinsert
                    setPageOffsetAtSlot(i, null, 0);
                    // TODO inneficient re-scan
                    int hashSlot = owner.insertionHashSlot(key, keyHash, hashPages, hashOffsets);
                    setPageOffsetAtSlot(hashSlot, page, pageOffset);
                }
            }
        }
        
        protected void __BUG_unshiftNextHashConflicts(LinkedPagedHashMap<K, V> owner, final int slotStart) {
            final int hashLen = hashPages.length;
            int freeSlot = slotStart;
            int fullSlotStart = slotStart;
            int fullSlotEnd = nextModulo(slotStart, hashLen);
            for(;;) {
                int lastSlotConflict = fullSlotStart;
                for(int currSlot = fullSlotEnd; currSlot != fullSlotStart; currSlot = nextModulo(currSlot, hashLen)) {
                    LinkedEntriesPagedNode<K, V> page = hashPages[currSlot];
                    if (page == null) {
                        break;
                    }
                    int pageOffset = hashOffsets[currSlot];
                    K currKey = page.getKeyAt(pageOffset);
                    int currKeyHash = owner.hashCodeOf(currKey);
                    int currKeyPos = posOf(currKeyHash, hashPages.length);
                    if (currKeyPos <= slotStart) { // BUG ...
                        lastSlotConflict = currSlot; 
                    }
                }
                if (lastSlotConflict != fullSlotStart) {
                    // found conflict: solve by writing start(empty) with last, continue from last
                    hashPages[freeSlot] = hashPages[lastSlotConflict];
                    hashOffsets[freeSlot] = hashOffsets[lastSlotConflict];
                    hashPages[lastSlotConflict] = null;
                    hashOffsets[lastSlotConflict] = 0;
                    freeSlot = lastSlotConflict;
                    fullSlotStart = nextModulo(freeSlot, hashLen);
                    fullSlotEnd = nextModulo(lastSlotConflict, hashLen);
                } else {
                    break;
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("HashPartition " + hashPart + ": ");
            for (int i = 0; i < hashPages.length; i++) {
                LinkedEntriesPagedNode<K, V> p = hashPages[i];
                if (p != null) {
                    K key = p.getKeyAt(hashOffsets[i]);
                    int keyHash = owner.hashCodeOf(key);
                    int keyPos = posOf(keyHash, hashPages.length); 
                    sb.append("[" + i);
                    if (keyPos != i) {
                        sb.append("!=" + keyPos);
                    }
                    sb.append("]");
                    
                    sb.append("p,off:" + hashOffsets[i] + " -> ");
                    sb.append(key);
                    sb.append(", ");
                }
            }
            return sb.toString();
        }

        protected void checkInvariants() {
            boolean prevEmpty = hashPages[hashPages.length-1] == null;
            for (int i = 0; i < hashPages.length; i++) {
                LinkedEntriesPagedNode<K, V> p = hashPages[i];
                if (p != null) {
                    K key = p.getKeyAt(hashOffsets[i]);
                    int keyHash = owner.hashCodeOf(key);
                    int keyPos = posOf(keyHash, hashPages.length); 
                    if (keyPos != i) {
                        if (prevEmpty) {
                            throw new RuntimeException("internal invariant violation: conflict in " + i + " while prev slot is empty!");
                        }
                    }
                }
                prevEmpty = p == null;
            }
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
            final int firstPos = posOf(keyHash, hashLen);
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
        // TODO : should alloc new / realloc+re-hash increase size if loadFactor > 0.5  (else too many conflicts!! ... extremely slow) 
        if (hash0Pages != null && (hash0UsedCount+hash0UsedCount) < hash0Pages.length) {
            int offset = insertionHashSlot(key, keyHash, hash0Pages, hash0Offsets);
            if (offset != NONE) {
                return new HashSlot(0, offset); 
            }
        }
        if (hash1Pages != null && (hash1UsedCount+hash1UsedCount) < hash1Pages.length) {
            int offset = insertionHashSlot(key, keyHash, hash1Pages, hash1Offsets);
            if (offset != NONE) {
                return new HashSlot(1, offset); 
            }
        }
        if (hash2Pages != null && (hash2UsedCount+hash2UsedCount) < hash2Pages.length) {
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
            return new HashSlot(0, posOf(keyHash, hash0Pages.length));
        } else if (hash1Pages == null) {
            int allocSize = Math.max(INIT_HASH_LEN, size);
            hash1Pages = new LinkedEntriesPagedNode[allocSize];
            hash1Offsets = new int[allocSize];
            return new HashSlot(1, posOf(keyHash, hash1Pages.length));
        } else if (hash2Pages == null) {
            int allocSize = Math.max(INIT_HASH_LEN, size);
            hash2Pages = new LinkedEntriesPagedNode[allocSize];
            hash2Offsets = new int[allocSize];
            return new HashSlot(2, posOf(keyHash, hash2Pages.length));
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
            if (page != null) {
                int pageOffset = nthOffsets[i];
                K reindexKey = page.getKeyAt(pageOffset);
                int reindexKeyHash = hashCodeOf(reindexKey);
                int newI = insertionHashSlot(reindexKey, reindexKeyHash, newNthPages, newNthOffsets);
                newNthPages[newI] = page;
                newNthOffsets[newI] = pageOffset;
            }
            nthPages[i] = null; // gc friendly: clear prev refs
        }
        setNthHashPageAndOffsets(nth, newNthPages, newNthOffsets);
        
        int offset = insertionHashSlot(key, keyHash, newNthPages, newNthOffsets);
        return new HashSlot(nth, offset);
    }
    
    protected static int posOf(int keyHash, int len) {
        return (keyHash & 0x7fffffff) % len;
    }
    
    protected int insertionHashSlot(K key, int keyHash, LinkedEntriesPagedNode<K,V>[] hashPages, int[] hashOffsets) {
        if (hashPages != null) {
            final int hashLen = hashPages.length;
            final int firstPos = posOf(keyHash, hashLen);
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
    
    protected HashPartition<K,V> nthHashPartition(int n) {
        switch(n) {
        case 0: return new HashPartition<K,V>(this, 0, hash0Pages, hash0Offsets);
        case 1: return new HashPartition<K,V>(this, 1, hash1Pages, hash1Offsets);
        case 2: return new HashPartition<K,V>(this, 2, hash2Pages, hash2Offsets);
        default: throw new IllegalArgumentException();
        }
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
        LinkedEntriesPagedNode<K, V> page = nthHashPages(hashSlot.hashPart)[hashSlot.slot];
        int pageOffset = nthHashOffsets(hashSlot.hashPart)[hashSlot.slot];
        return page.getKeyAt(pageOffset);
    }

    protected V getValueAtIndirectHashSlot(HashSlot hashSlot) {
        LinkedEntriesPagedNode<K, V> page = nthHashPages(hashSlot.hashPart)[hashSlot.slot];
        int pageOffset = nthHashOffsets(hashSlot.hashPart)[hashSlot.slot];
        return page.getValueAt(pageOffset);
    }
    
    protected V setKeyValueAtIndirectHashSlot(HashSlot hashSlot, K key, V value) {
        LinkedEntriesPagedNode<K, V> page = nthHashPages(hashSlot.hashPart)[hashSlot.slot];
        int pageOffset = nthHashOffsets(hashSlot.hashPart)[hashSlot.slot];
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
