package fr.an.mem4j.offheap;

import fr.an.mem4j.offheap.impl.MallocImpl;
import fr.an.mem4j.offheap.impl.MappedFile;

/**
 * Facade for creating Malloc heap using direct memory / mapped file
 */
public final class OffheapMallocs {

    /** private to force all static */
    private OffheapMallocs() {
    }
    
    public static Malloc createMallocHeap(long capacity) {
        return new MallocImpl(capacity);
    }

    public static Malloc wrapMallocHeap(long base, long capacity) {
        return new MallocImpl(base, capacity);
    }

    public static Malloc wrapMappedFileMallocHeap(MappedFile mmap) {
        return new MallocImpl(mmap);
    }

}
