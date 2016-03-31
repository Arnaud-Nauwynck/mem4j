package fr.an.mem4j.typememsystem;

import fr.an.mem4j.datatypesystem.DataTypeSystem;
import fr.an.mem4j.datatypesystem.reflect.FixedLenDataType;
import fr.an.mem4j.offheap.Malloc;
import fr.an.mem4j.offheap.impl.Mem4jUnsafe;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class TypedMemorySystem {

    private final DataTypeSystem typeSystem;
    
    private final Malloc malloc;

    private static final int dataTypeHeaderLen = 32;
    
    private static final Unsafe unsafe = Mem4jUnsafe.getUnsafe();
    
    // ------------------------------------------------------------------------
    
    public TypedMemorySystem(DataTypeSystem typeSystem, Malloc malloc) {
        this.typeSystem = typeSystem;
        this.malloc = malloc;
    }

    // ------------------------------------------------------------------------
    
    public DataTypeSystem getTypeSystem() {
        return typeSystem;
    }

    public Malloc getMalloc() {
        return malloc;
    }
    
    public TypedMemPointer malloc(FixedLenDataType dataType) {
        int dataLen = dataType.getDataLength();
        int allocLen = dataTypeHeaderLen + dataLen;
        long addr = malloc.calloc(allocLen);
        unsafe.putInt(addr, dataType.getTypeId());
        return new TypedMemPointer(addr, dataType);
    }
    
}
