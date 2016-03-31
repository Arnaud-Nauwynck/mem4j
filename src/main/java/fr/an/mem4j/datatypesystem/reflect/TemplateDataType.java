package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

public abstract class TemplateDataType extends NamedDataType {

    public TemplateDataType(DataTypeSystem owner, String name, int typeId) {
        super(owner, name, typeId);
    }

    // ------------------------------------------------------------------------

    public static abstract class Builder extends NamedDataType.Builder {
        
    }
    
}
