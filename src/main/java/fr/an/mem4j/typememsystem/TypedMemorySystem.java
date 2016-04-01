package fr.an.mem4j.typememsystem;

import fr.an.mem4j.datatypesystem.DataTypeSystem;
import fr.an.mem4j.datatypesystem.reflect.FixedLenDataType;
import fr.an.mem4j.datatypesystem.reflect.ParametricLenArrayDataType;
import fr.an.mem4j.offheap.Malloc;
import fr.an.mem4j.offheap.impl.Mem4jUnsafe;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class TypedMemorySystem {

    private final DataTypeSystem typeSystem;
    
    private final Malloc malloc;

    private static final int mallocDataTypeHeaderLen = 32;
    private static final int arraySizeLen = 32; 
    private static final int mallocArrayHeaderLen = mallocDataTypeHeaderLen + arraySizeLen;
    
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
        int allocLen = mallocDataTypeHeaderLen + dataLen;
        long addr = malloc.calloc(allocLen);
        unsafe.putInt(addr, dataType.getTypeId());
        return new TypedMemPointer(addr, dataType);
    }
    
    public TypedMemPointer mallocArray(ParametricLenArrayDataType arrayDataType, int arrayLen) {
        int eltDataLen = arrayDataType.getElementType().getDataLength();
        int allocLen = mallocArrayHeaderLen + arrayLen * eltDataLen;
        long addr = malloc.calloc(allocLen);
        unsafe.putInt(addr, arrayDataType.getTypeId());
        unsafe.putInt(addr+arraySizeLen, arrayDataType.getTypeId());
//        for(int i = 0; i < aarrayLen; i++) {
//        }
        return new TypedMemPointer(addr, arrayDataType);
    }
    
}
