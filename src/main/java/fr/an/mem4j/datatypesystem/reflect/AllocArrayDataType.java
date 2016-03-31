package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

/**
 * 
 */
public class AllocArrayDataType extends TemplateInstanceDataType {

    private final int allocArrayLen;

    // ------------------------------------------------------------------------

    public AllocArrayDataType(DataTypeSystem owner, ParametricLenArrayDataType templateArrayDataType, int allocArrayLen) {
        super(owner, templateArrayDataType);
        this.allocArrayLen = allocArrayLen;
    }

    // ------------------------------------------------------------------------

    public ParametricLenArrayDataType getTemplateArrayDataType() {
        return (ParametricLenArrayDataType) super.templateDataType;
    }

    public FixedLenDataType getElementDataType() {
        return getTemplateArrayDataType().getElementType();
    }
    
    public int getAllocArrayLen() {
        return allocArrayLen;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = allocArrayLen * prime + templateDataType.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AllocArrayDataType other = (AllocArrayDataType) obj;
        if (templateDataType != other.templateDataType)
            return false;
        if (allocArrayLen != other.allocArrayLen)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "alloc(" + allocArrayLen + "*" + getElementDataType().getName() + ")";
    }
    
}
