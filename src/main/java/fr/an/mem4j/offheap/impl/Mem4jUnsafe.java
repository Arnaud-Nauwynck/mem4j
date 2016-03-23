package fr.an.mem4j.offheap.impl;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
class Mem4jUnsafe {

    /** private to force all static */
    private Mem4jUnsafe() {
    }
    
    // public static final Unsafe unsafe = getUnsafe();
    
    public static final long byteArrayOffset = getUnsafe().arrayBaseOffset(byte[].class);

    public static Unsafe getUnsafe() {
        try {
            return (Unsafe) JavaInternals.getField(Unsafe.class, "theUnsafe").get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
