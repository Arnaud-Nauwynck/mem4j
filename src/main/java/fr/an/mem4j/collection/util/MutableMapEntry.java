package fr.an.mem4j.collection.util;

import java.util.Map;

public class MutableMapEntry<K,V> implements Map.Entry<K,V> {
    
    protected K key;
    protected V value;

    // ------------------------------------------------------------------------
    
    public MutableMapEntry() {
    }
    
    public MutableMapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    // ------------------------------------------------------------------------

    public K getKey() {
        return key;
    }
    
    public V getValue() {
        return value;
    }
    
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * helper method for re-using ligthweight object while iterating...
     * @param key
     * @param value
     */
    public void _setCurr(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
    
}
