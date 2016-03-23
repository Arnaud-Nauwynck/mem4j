package fr.an.mem4j.offheap;

/**
 * Malloc API malloc()/free()  for managing native memory (direct memory or mapped file)
 *
 */
public abstract class Malloc {

    public abstract long base();

    public abstract long getTotalMemory();

    public abstract long getFreeMemory();

    public long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    public abstract long calloc(int size);

    public abstract long malloc(int size);

    public abstract void free(long address);
    
    // Get the actual size of the allocated block or 0 if the given address is not allocated
    public abstract int allocatedSize(long address);

    // Verify the layout of the heap. Expensive operation, used only for debug purposes
    public abstract void verify();
    
}
