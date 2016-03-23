package fr.an.mem4j.offheap.impl;

/**
 * Runtime Exception for OutOfMemory (not Error as in OOME)... when using malloc managed heap
 */
public class OutOfMemoryRuntimeException extends RuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    public OutOfMemoryRuntimeException(String message) {
        super(message);
    }

}
