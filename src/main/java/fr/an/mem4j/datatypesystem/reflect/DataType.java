package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

/**
 * 
 */
public abstract class DataType {

    protected final DataTypeSystem owner;

    // ------------------------------------------------------------------------
    
    public DataType(DataTypeSystem owner) {
        this.owner = owner;
    }

    // ------------------------------------------------------------------------

    public DataTypeSystem getOwner() {
        return owner;
    }
    
}
