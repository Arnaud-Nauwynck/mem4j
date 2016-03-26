package fr.an.mem4j.collection.lightweight;

import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.Test;

public class LinkedPagedHashMapTest {

    protected LinkedPagedHashMap<String,Integer> sut = new LinkedPagedHashMap<>();
    protected LinkedHashMap<String,Integer> check = new LinkedHashMap<>();
    
    @Test
    public void testPut_remove() {
        Integer res = sut.put("123", 123);
        check.put("123", 123);
        Assert.assertNull(res);
        Assert.assertEquals(1, sut.size());
        Assert.assertTrue(sut.containsKey("123"));
        Assert.assertEquals(123, sut.get("123").intValue());
        Assert.assertEquals("{123=123}", sut.toString());
        Assert.assertEquals(check, sut);
        Assert.assertEquals(check.toString(), sut.toString());
        
        res = sut.put("123", 123456);
        check.put("123", 123456);
        Assert.assertEquals(123, res.intValue());
        Assert.assertEquals(1, sut.size());
        Assert.assertEquals("{123=123456}", sut.toString());
        res = sut.put("123", 123);
        check.put("123", 123);
        Assert.assertTrue(sut.containsKey("123"));
        Assert.assertEquals(check, sut);
        Assert.assertEquals(check.toString(), sut.toString());
        
        res = sut.put("234", 234);
        check.put("234", 234);
        Assert.assertNull(res);
        Assert.assertEquals(2, sut.size());
        Assert.assertTrue(sut.containsKey("123"));
        Assert.assertTrue(sut.containsKey("234"));
        Assert.assertEquals(123, sut.get("123").intValue());
        Assert.assertEquals(234, sut.get("234").intValue());
        Assert.assertEquals("{123=123, 234=234}", sut.toString());
        Assert.assertEquals(check.toString(), sut.toString());
        
        int splitPut = 35;
        for(int i = 0; i < splitPut; i++) {
            String keyI = Integer.toString(i);
            sut.put(keyI, i);
            check.put(keyI, i);
            Assert.assertTrue(sut.containsKey(keyI));
            Assert.assertEquals(i, sut.get(keyI).intValue());
            Assert.assertEquals(check.toString(), sut.toString());
            for(int j = 0; j <= i; j++) {
                Assert.assertEquals(j, sut.get(Integer.toString(j)).intValue());
            }
        }
        for(int i = splitPut; i < 100; i++) {
            String keyI = Integer.toString(i);
            sut.put(keyI, i);
            check.put(keyI, i);
            Assert.assertTrue(sut.containsKey(keyI));
            Assert.assertEquals(i, sut.get(keyI).intValue());
            Assert.assertEquals(check.toString(), sut.toString());
            for(int j = 0; j <= i; j++) {
                Assert.assertEquals(j, sut.get(Integer.toString(j)).intValue());
            }
        }
        
        
        
        sut.get("0");
        sut.get("1");
        int split = 27;
        for(int i = 0; i < split; i++) {
            String keyI = Integer.toString(i);
            sut.remove(keyI);
            check.remove(keyI);
        }
        Assert.assertEquals(check.toString(), sut.toString());
        for(int i = split; i < 100; i++) {
            String keyI = Integer.toString(i);
            sut.remove(keyI);
            check.remove(keyI);
            Assert.assertFalse(sut.containsKey(keyI));
            Assert.assertEquals(check.toString(), sut.toString());
            for(int j = 0; j <= i; j++) {
                Assert.assertFalse(sut.containsKey(Integer.toString(j)));
            }
        }
    }
}
