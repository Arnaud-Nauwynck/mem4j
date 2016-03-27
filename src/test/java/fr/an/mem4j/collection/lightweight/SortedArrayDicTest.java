package fr.an.mem4j.collection.lightweight;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SortedArrayDicTest {

    protected SortedArrayDic<String> sut = new SortedArrayDic<String>();
    
    @Test
    public void testPut() {
        sut.put("key-a", "value-a");
        Assert.assertEquals("value-a", sut.get("key-a"));
        sut.put("key-c", "value-c");
        sut.put("key-b", "value-b");
        Assert.assertNull(sut.get("key-z"));
        Assert.assertEquals(3, sut.size());
        Iterator<Entry<String, String>> iter = sut.entrySet().iterator();
        Entry<String, String> e1 = iter.next();
        Assert.assertEquals("key-a", e1.getKey());
        Assert.assertEquals("value-a", e1.getValue());
        e1 = iter.next();
        Assert.assertEquals("key-b", e1.getKey());
        Assert.assertEquals("value-b", e1.getValue());
        e1 = iter.next();
        Assert.assertEquals("key-c", e1.getKey());
        Assert.assertEquals("value-c", e1.getValue());
        
        // replace
        sut.put("key-b", "value-new-b");
        Assert.assertEquals("value-new-b", sut.get("key-b"));
    }
    
    @Test
    public void testRemove() {
        SortedArrayDic<String> sut = new SortedArrayDic<String>(1);
        sut.remove(null);
        sut.remove("key");
        sut.put("key-c", "value-c");
        sut.remove("key-c");
        sut.put("key-c", "value-c");
        sut.put("key-b", "value-b");
        sut.put("key-a", "value-a");
        sut.remove("key-b");
        Assert.assertEquals(2, sut.size());
        Iterator<Entry<String, String>> iter = sut.entrySet().iterator();
        Entry<String, String> e1 = iter.next();
        Assert.assertEquals("key-a", e1.getKey());
        Assert.assertEquals("value-a", e1.getValue());
        e1 = iter.next();
        Assert.assertEquals("key-c", e1.getKey());
        Assert.assertEquals("value-c", e1.getValue());
        sut.remove((Object) "key-a");
        sut.remove((Object) null);
        sut.remove((String) null);
        Assert.assertNull(sut.get("key-a"));
    }

    @Test
    public void testGet() {
        Assert.assertNull(sut.get((Object) "key-z"));
        sut.put("key-a", "value-a");
        Assert.assertEquals("value-a", sut.get("key-a"));
        Assert.assertNull(sut.get("key-z"));
        sut.put("key-b", "value-b");
        Assert.assertNull(sut.get("key-z"));
    }
    
    @Test
    public void testSize_isEmpty() {
        Assert.assertTrue(sut.isEmpty());
        Assert.assertEquals(0, sut.size());
        sut.put("key-c", "value-c");
        Assert.assertFalse(sut.isEmpty());
        Assert.assertEquals(1, sut.size());
    }
    
    @Test
    public void testContainsValue() {
        Assert.assertFalse(sut.containsValue("value-a"));
        Assert.assertFalse(sut.containsValue(null));
        sut.put("key-a", "value-a");
        sut.put("key-null", null);
        Assert.assertTrue(sut.containsValue("value-a"));
        Assert.assertTrue(sut.containsValue(null));
    }
    
    @Test
    public void testContainsKey() {
        Assert.assertFalse(sut.containsKey("key-a"));
        Assert.assertFalse(sut.containsKey(null));
        sut.put("key-a", "value-a");
        sut.put(null, null);
        Assert.assertTrue(sut.containsKey("key-a"));
        Assert.assertTrue(sut.containsKey(null));
    }
    
    @Test
    public void testClear() {
        sut.put("key-a", "value-a");
        sut.clear();
        Assert.assertEquals(0,  sut.size());
    }
    
    @Test
    public void testKeySet() {
        Set<String> ks = sut.keySet();
        Assert.assertEquals(0, ks.size());
        sut.put("key-b", "value-b");
        ks = sut.keySet();
        Assert.assertEquals(1, ks.size());
        Iterator<String> iter = ks.iterator();
        Assert.assertEquals("key-b", iter.next());
        sut.put("key-a", "value-a");
        ks = sut.keySet();
        Assert.assertEquals(2, ks.size());
        iter = ks.iterator();
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("key-a", iter.next());
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("key-b", iter.next());
        Assert.assertFalse(iter.hasNext());
        try {
            iter.next();
        } catch(NoSuchElementException ex) {
            // ok
        }
        
        ks = sut.keySet();
        iter = ks.iterator();
        iter.next();
        iter.remove();
        Assert.assertNull(sut.get("key-a"));
    }
    
    @Test
    public void testValues() {
        Collection<String> values = sut.values();
        Assert.assertEquals(0, values.size());
        sut.put("key-b", "value-b");
        values = sut.values();
        Assert.assertEquals(1, values.size());
        Iterator<String> iter = values.iterator();
        Assert.assertEquals("value-b", iter.next());
        sut.put("key-a", "value-a");
        values = sut.values();
        Assert.assertEquals(2, values.size());
        iter = values.iterator();
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("value-a", iter.next());
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals("value-b", iter.next());
        Assert.assertFalse(iter.hasNext());
        try {
            iter.next();
        } catch(NoSuchElementException ex) {
            // ok
        }
        
        values = sut.keySet();
        iter = values.iterator();
        iter.next();
        iter.remove();
        Assert.assertNull(sut.get("key-a"));
    }
    
    @Test
    public void testEntrySet() {
        Set<Map.Entry<String, String>> entries = sut.entrySet();
        Assert.assertEquals(0, entries.size());
        sut.put("key-b", "value-b");
        entries = sut.entrySet();
        Assert.assertEquals(1, entries.size());
        Iterator<Map.Entry<String, String>> iter = entries.iterator();
        Entry<String, String> e = iter.next();
        Assert.assertEquals("value-b", e.getValue());
        sut.put("key-a", "value-a");
        entries = sut.entrySet();
        Assert.assertEquals(2, entries.size());
        iter = entries.iterator();
        Assert.assertTrue(iter.hasNext());
        e = iter.next();
        Assert.assertEquals("key-a", e.getKey());
        Assert.assertEquals("value-a", e.getValue());
        Assert.assertTrue(iter.hasNext());
        e = iter.next();
        Assert.assertEquals("key-b", e.getKey());
        Assert.assertEquals("value-b", e.getValue());
        Assert.assertFalse(iter.hasNext());
        try {
            iter.next();
        } catch(NoSuchElementException ex) {
            // ok
        }
        
        entries = sut.entrySet();
        iter = entries.iterator();
        iter.next();
        iter.remove();
        Assert.assertNull(sut.get("key-a"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testClone() {
        SortedArrayDic<String> res = (SortedArrayDic<String>) sut.clone();
        Assert.assertEquals(0, res.size());
        sut.put("key-b", "value-b");
        res = (SortedArrayDic<String>) sut.clone();
        Assert.assertEquals(1, res.size());
    }
    
    @Test
    public void testToString() {
        sut.put("key-b", "value-b");
        sut.put("key-a", "value-a");
        Assert.assertEquals("{key-a:value-a, key-b:value-b}", sut.toString());
    }
    
    @Test
    public void testHashcode_equals() {
        SortedArrayDic<String> other = new SortedArrayDic<String>(); 
        sut.put("key-a", "value-a");
        sut.put("key-b", "value-b");
        other.put("key-b", "value-b");
        other.put("key-a", "value-a");
        Assert.assertEquals(other.hashCode(), sut.hashCode());
        Assert.assertEquals(other, sut);
        Assert.assertEquals(other.hashCode(), sut.hashCode());
        Assert.assertEquals(other, sut);
    }
    
}
