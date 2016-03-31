package fr.an.mem4j.datatypesystem.reflect;

public final class StructDataField {

    private final StructDataType owner;
    private final String fieldName;
    private final int offset;

    private final FixedLenDataType dataType;
    
    // ------------------------------------------------------------------------
    
    public StructDataField(StructDataType owner, Builder builder, int offset) {
        super();
        this.owner = owner;
        this.fieldName = builder.fieldName;
        this.offset = offset;
        this.dataType = builder.dataType;
    }

    // ------------------------------------------------------------------------
    
    public StructDataType getOwner() {
        return owner;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getOffset() {
        return offset;
    }
    
    public FixedLenDataType getDataType() {
        return dataType;
    }
    
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return fieldName;
    }
    
    // ------------------------------------------------------------------------

    public static class Builder {
        private String fieldName;
        private FixedLenDataType dataType;
        
        public String getFieldName() {
            return fieldName;
        }
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
        public FixedLenDataType getDataType() {
            return dataType;
        }
        public void setDataType(FixedLenDataType dataType) {
            this.dataType = dataType;
        }        
        
    }
    
}
