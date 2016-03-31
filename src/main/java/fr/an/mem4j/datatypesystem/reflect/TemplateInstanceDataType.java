package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

public abstract class TemplateInstanceDataType extends DataType {

    protected final TemplateDataType templateDataType;
    
    // ------------------------------------------------------------------------

    public TemplateInstanceDataType(DataTypeSystem owner, TemplateDataType templateDataType) {
        super(owner);
        this.templateDataType = templateDataType;
    }

    // ------------------------------------------------------------------------
    
    public TemplateDataType getTemplateDataType() {
        return templateDataType;
    }

    
    
}
