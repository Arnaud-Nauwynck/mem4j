package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

public abstract class FixedLenDataType extends NamedDataType {

    protected final int dataLength;
    
    // ------------------------------------------------------------------------
    
    public FixedLenDataType(DataTypeSystem owner, String name, int typeId, int dataLength) {
        super(owner, name, typeId);
        this.dataLength = dataLength;
    }

    // ------------------------------------------------------------------------
    
    public int getDataLength() {
        return dataLength;
    }
    
    // ------------------------------------------------------------------------

    public static abstract class Builder extends NamedDataType.Builder {
        
    }
    
}
