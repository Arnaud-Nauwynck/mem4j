package fr.an.mem4j.collection.lightweight;

import java.util.Iterator;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

public class LinkedPagedCollectionTest {

    protected static Supplier<LinkedPagedCollection<Integer>> FACTORY_16 = () -> LinkedPagedCollection.new16Paged(); 
    protected static Supplier<LinkedPagedCollection<Integer>> FACTORY_32 = () -> LinkedPagedCollection.new32Paged(); 
    
    @SuppressWarnings("unchecked")
    protected static Supplier<LinkedPagedCollection<Integer>>[] FACTORIES = new Supplier[] {
        FACTORY_16, FACTORY_32
    };
    
    @SuppressWarnings("unchecked")
    protected static LinkedPagedCollection<Integer>[] suts() {
        return new LinkedPagedCollection[] {
            LinkedPagedCollection.new16Paged(), LinkedPagedCollection.new32Paged()
        };
    }
    
    @Test
    public void testAdd() {
        for(LinkedPagedCollection<Integer> sut : suts()) {
            // Prepare
            // Perform
            sut.add(0);
            Assert.assertEquals("[0]", sut.toString());
            sut.add(1);
            Assert.assertEquals("[0, 1]", sut.toString());
            sut.add(2);
            Assert.assertEquals("[0, 1, 2]", sut.toString());
            for(int i = 3; i < 100; i++) {
                sut.add(i);
            }
            // Post-check
            int index = 0;
            for(Iterator<Integer> iter = sut.iterator(); iter.hasNext(); index++) {
                Integer val = iter.next();
                Assert.assertEquals(index, val.intValue());
            }
        }
    }
    
    @Test
    public void testRemove() {
        for(LinkedPagedCollection<Integer> sut : suts()) {
            // Prepare
            for(int i = 0; i < 100; i++) {
                sut.add(i);
            }
            for(int i = 99; i >= 50; i--) {
                // Perform
                sut.remove(i);
                // Post-check
                Assert.assertFalse(sut.contains(i));
                Assert.assertEquals(i, sut.size());
            }
            for(int i = 0; i < 50; i++) {
                // Perform
                sut.remove(i);
                // Post-check
                Assert.assertFalse(sut.contains(i));
                Assert.assertEquals(50-1-i, sut.size());
            }
            // Post-check
            Assert.assertTrue(sut.isEmpty());
        }
    }
    
    @Test
    public void testClear() {
        for(LinkedPagedCollection<Integer> sut : suts()) {
            // Prepare
            for(int i = 0; i < 50; i++) {
                sut.add(i);
            }
            // Perform
            sut.clear();
            // Post-check
            Assert.assertTrue(sut.isEmpty());
        }
    }
    
}
