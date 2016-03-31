package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

public class ParametricLenArrayDataType extends TemplateDataType {

    private final FixedLenDataType elementType;
    
    // ------------------------------------------------------------------------

    public ParametricLenArrayDataType(DataTypeSystem owner, String name, int typeId, Builder builder) {
        super(owner, name, typeId);
        this.elementType = builder.elementType;
    }

    // ------------------------------------------------------------------------
    
    public FixedLenDataType getElementType() {
        return elementType;
    }

    // ------------------------------------------------------------------------

    public static class Builder extends TemplateDataType.Builder {
        
        private FixedLenDataType elementType;
        
        public Builder() {
        }
        
        public Builder(FixedLenDataType elementType) {
            this.elementType = elementType;
        }
        
        @Override
        public ParametricLenArrayDataType build(DataTypeSystem owner, int typeId) {
            return new ParametricLenArrayDataType(owner, name, typeId, this);
        }

        public FixedLenDataType getElementType() {
            return elementType;
        }
        public void setElementType(FixedLenDataType elementType) {
            this.elementType = elementType;
        }
        
    }
}
