package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

public abstract class NamedDataType extends DataType {

    protected final String name;
    protected final int typeId;
    
    // ------------------------------------------------------------------------
    
    /** to be called by DataTypeSystem only */
    public NamedDataType(DataTypeSystem owner, String name, int typeId) {
        super(owner);
        this.name = name;
        this.typeId = typeId;
    }

    // ------------------------------------------------------------------------
    
    public DataTypeSystem getOwner() {
        return owner;
    }
    
    public String getName() {
        return name;
    }
    
    public int getTypeId() {
        return typeId;
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        return typeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NamedDataType other = (NamedDataType) obj;
        if (owner != other.owner)
            return false;
        if (typeId != other.typeId)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return name;
    }

    // ------------------------------------------------------------------------
    
    public static abstract class Builder {
        String name;
        
        public abstract NamedDataType build(DataTypeSystem owner, int typeId);
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        
    }
    
}
