package fr.an.mem4j.datatypesystem.reflect;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

/**
 * 
 */
public final class FixedLenArrayDataType extends FixedLenDataType {

    private final FixedLenDataType elementType;
    private final int arrayLen;
    
    // ------------------------------------------------------------------------
    
    public FixedLenArrayDataType(DataTypeSystem owner, String name, int typeId, Builder builder) {
        super(owner, name, typeId, builder.computeDataLength());
        this.elementType = builder.elementType;
        this.arrayLen = builder.arrayLen;
    }

    // ------------------------------------------------------------------------
    
    public FixedLenDataType getElementType() {
        return elementType;
    }

    public int getArrayLen() {
        return arrayLen;
    }

    // ------------------------------------------------------------------------

    public static class Builder extends FixedLenDataType.Builder {
        
        private FixedLenDataType elementType;
        private int arrayLen;
        
        public Builder() {
        }
        
        public Builder(FixedLenDataType elementType, int arrayLen) {
            this.elementType = elementType;
            this.arrayLen = arrayLen;
        }
        
        @Override
        public FixedLenArrayDataType build(DataTypeSystem owner, int typeId) {
            return new FixedLenArrayDataType(owner, name, typeId, this);
        }

        protected int computeDataLength() {
            return arrayLen * elementType.dataLength;
        }
        
        public FixedLenDataType getElementType() {
            return elementType;
        }
        public void setElementType(FixedLenDataType elementType) {
            this.elementType = elementType;
        }
        public int getArrayLen() {
            return arrayLen;
        }
        public void setArrayLen(int arrayLen) {
            this.arrayLen = arrayLen;
        }
        
        
    }
    
}
