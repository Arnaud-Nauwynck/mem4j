package fr.an.mem4j.collection.lightweight;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class LinkedHashMapBenchmarkTest {
    
       
    @Test
    public void testPutRemove() {
        Random rand = new Random(0);
        LinkedPagedHashMap<String,Integer> sut = new LinkedPagedHashMap<>();
        LinkedHashMap<String,Integer> check = new LinkedHashMap<>();
        
        for(int repeat = 0; repeat < 100000; repeat++) {
            int randValue = rand.nextInt();
            boolean addOrRemove = true;
            if (check.size() > 10) {
                addOrRemove = (repeat % 3) != 0;
            }
            randValue = rand.nextInt();
            String key = Integer.toString(randValue);
            Integer value = randValue;
            if (addOrRemove) {
                sut.put(key, value);
                check.put(key, value);
            } else {
                sut.remove(key);
                check.remove(key);
            }
            if (repeat%100 == 0) {
                Assert.assertEquals(sut, check);
            }
            // Assert.assertEquals(sut.toString(), check.toString());
        }
        Assert.assertEquals(sut, check);
    }
    
    
    
    @Test
    public void testBench() {
        LinkedPagedHashMap<String,Integer> sut = new LinkedPagedHashMap<>();
        LinkedHashMap<String,Integer> check = new LinkedHashMap<>();
        
        int[] benchArraySizes = { 5, 10, 20, 50, 100, 1000, 10000, 100000 };
        for (int i = 0; i < benchArraySizes.length; i++) {
            int benchArraySize = benchArraySizes[i];
            int repeatCount = 1000 + 10 * benchArraySizes[benchArraySizes.length-1 - i];
            
            doTestBench(sut, benchArraySize, repeatCount, "LinkedPagedHashMap");
            doTestBench(check, benchArraySize, repeatCount, "j.u.LinkedHashMap");
            System.out.println();
            
        }
    }
    
    @Test
    public void testBench_repeat() {
        int benchArraySize = 10000;
        for (int i = 0; i < 4; i++) {
            LinkedPagedHashMap<String,Integer> sut = new LinkedPagedHashMap<>();
            doTestBench(sut, benchArraySize, 10000, "LinkedPagedHashMap");
        }
    }

    private static final boolean DEBUG = false;
    
    protected void doTestBench(Map<String,Integer> sut, int benchArraySize, int repeatCount, String display) {
        int distinctEltsCount = benchArraySize*2;
        String[] keys = new String[distinctEltsCount];
        Integer[] values = new Integer[distinctEltsCount];
        for (int i = 0; i < distinctEltsCount; i++) {
            int v = i * 3;
            keys[i] = String.valueOf(v);
            values[i] = Integer.valueOf(v);
        }
        Random rand = new Random(0);
        
        for (int i = 0; i < benchArraySize; i++) {
            sut.put(keys[i], values[i]);
        }
        
        if (DEBUG) System.out.print("sizes: ");
        int countAdded = 0;
        int countRemoved = 0;
        long startMillis = System.currentTimeMillis();
        for(int repeat = 0; repeat < repeatCount; repeat++) {
            int randValue = rand.nextInt(distinctEltsCount);
            String key = keys[randValue];
            Integer value = values[randValue];
            
            if (! sut.containsKey(key)) {
                sut.put(key, value);
                countAdded++;
            } else {
                sut.remove(key);
                countRemoved++;
            }
            
            if (DEBUG) {
                if ((repeat-1) % 100 == 0) {
                    System.out.print(sut.size() + " ");
                }
            }
        }
        if (DEBUG) System.out.println();
        
        long time = System.currentTimeMillis() - startMillis;
        System.out.println("bench " + display + " [size:" + benchArraySize + " x repeat:" + repeatCount + "] " + time + " ms"  
            + " (= " + repeatCount + " containsKey(), " + countAdded + " put(), " + countRemoved + " remove() )");
    }
    
    
    
}
