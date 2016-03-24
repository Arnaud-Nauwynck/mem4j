package fr.an.mem4j.collection.lightweight;

public class LinkedEntriesPagedNode<K,V> {

    LinkedEntriesPagedNode<K,V> prevPage;
    LinkedEntriesPagedNode<K,V> nextPage;
    
    int size;
    
    protected K key0;
    protected V value0;
    protected K key1;
    protected V value1;
    protected K key2;
    protected V value2;
    protected K key3;
    protected V value3;
    protected K key4;
    protected V value4;
    protected K key5;
    protected V value5;
    protected K key6;
    protected V value6;
    protected K key7;
    protected V value7;
    protected K key8;
    protected V value8;
    protected K key9;
    protected V value9;
    protected K key10;
    protected V value10;
    protected K key11;
    protected V value11;
    protected K key12;
    protected V value12;
    protected K key13;
    protected V value13;
    protected K key14;
    protected V value14;
    protected K key15;
    protected V value15;

    // ------------------------------------------------------------------------

    public LinkedEntriesPagedNode() {
    }

    // ------------------------------------------------------------------------
    
    public int size() {
        return size;
    }

    public boolean isFull() {
        return size == 16;
    }

    public int availableSlotCount() {
        return 16 - size;
    }

    
    public K getKeyAt(int i) {
        switch(i) {
        case 0: return key0;
        case 1: return key1;
        case 2: return key2;
        case 3: return key3;
        case 4: return key4;
        case 5: return key5;
        case 6: return key6;
        case 7: return key7;
        case 8: return key8;
        case 9: return key9;
        case 10: return key10;
        case 11: return key11;
        case 12: return key12;
        case 13: return key13;
        case 14: return key14;
        case 15: return key15;
        default: throw new IllegalArgumentException();
        }
    }

    public V getValueAt(int i) {
        switch(i) {
        case 0: return value0;
        case 1: return value1;
        case 2: return value2;
        case 3: return value3;
        case 4: return value4;
        case 5: return value5;
        case 6: return value6;
        case 7: return value7;
        case 8: return value8;
        case 9: return value9;
        case 10: return value10;
        case 11: return value11;
        case 12: return value12;
        case 13: return value13;
        case 14: return value14;
        case 15: return value15;
        default: throw new IllegalArgumentException();
        }
    }
    
    public void setKeyValueAt(int i, K key, V value) {
        switch(i) {
        case 0: key0 = key; value0 = value; break;
        case 1: key1 = key; value1 = value; break;
        case 2: key2 = key; value2 = value; break;
        case 3: key3 = key; value3 = value; break;
        case 4: key4 = key; value4 = value; break;
        case 5: key5 = key; value5 = value; break;
        case 6: key6 = key; value6 = value; break;
        case 7: key7 = key; value7 = value; break;
        case 8: key8 = key; value8 = value; break;
        case 9: key9 = key; value9 = value; break;
        case 10: key10 = key; value10 = value; break;
        case 11: key11 = key; value11 = value; break;
        case 12: key12 = key; value12 = value; break;
        case 13: key13 = key; value13 = value; break;
        case 14: key14 = key; value14 = value; break;
        case 15: key15 = key; value15 = value; break;
        default: throw new IllegalArgumentException();
        }
    }

    public void removeAt(int i) {
        size--;
        switch(i) {
        case 0: key0 = key1; value0 = value1; // no break;
        case 1: key1 = key2; value1 = value2; // no break;
        case 2: key2 = key3; value2 = value3; // no break;
        case 3: key3 = key4; value3 = value4; // no break;
        case 4: key4 = key5; value4 = value5; // no break;
        case 5: key5 = key6; value5 = value6; // no break;
        case 6: key6 = key7; value6 = value7; // no break;
        case 7: key7 = key8; value7 = value8; // no break;
        case 8: key8 = key9; value8 = value9; // no break;
        case 9: key9 = key10; value9 = value10; // no break;
        case 10: key10 = key11; value10 = value11; // no break;
        case 11: key11 = key12; value11 = value12; // no break;
        case 12: key12 = key13; value12 = value13; // no break;
        case 13: key13 = key14; value13 = value14; // no break;
        case 14: key14 = key15; value14 = value15; // no break;
        case 15: key15 = null; value15 = null; // no break;
        break;
        }
    }
    
    public void insertAt(int i, K key, V value) {
        size++;
        switch(i) {
        case 15: key15 = key14; value15 = value14; break;
        case 14: key14 = key13; value14 = value13; break;
        case 13: key13 = key12; value13 = value12; break;
        case 12: key12 = key11; value12 = value11; break;
        case 11: key11 = key10; value11 = value10; break;
        case 10: key10 = key9; value10 = value9; break;
        case 9: key9 = key8; value9 = value8; break;
        case 8: key8 = key7; value8 = value7; break;
        case 7: key7 = key6; value7 = value6; break;
        case 6: key6 = key5; value6 = value5; break;
        case 5: key5 = key4; value5 = value4; break;
        case 4: key4 = key3; value4 = value3; break;
        case 3: key3 = key2; value3 = value2; break;
        case 2: key2 = key1; value2 = value1; break;
        case 1: key1 = key0; value1 = value0; break;
        case 0: key0 = null; value0 = null; break;
        default: throw new IllegalArgumentException();
        }
        setKeyValueAt(i, key, value);
    }
        
}
