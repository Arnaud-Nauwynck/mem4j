package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

public final class PrimitiveDataType extends FixedLenDataType {

    private final Class<?> javaType;
    
    // ------------------------------------------------------------------------
    
    public PrimitiveDataType(DataTypeSystem owner, String name, int typeId, int dataLength, Class<?> javaType) {
        super(owner, name, typeId, dataLength);
        this.javaType = javaType;
    }

    // ------------------------------------------------------------------------
    
    public Class<?> getJavaType() {
        return javaType;
    }
    
}
