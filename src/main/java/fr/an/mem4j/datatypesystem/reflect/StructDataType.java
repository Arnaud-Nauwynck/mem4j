package fr.an.mem4j.datatypesystem.reflect;

import java.util.ArrayList;
import java.util.List;

import fr.an.mem4j.datatypesystem.DataTypeSystem;

/**
 * 
 */
public final class StructDataType extends FixedLenDataType {

    private final StructDataField[] fields;

    // ------------------------------------------------------------------------
    
    public StructDataType(DataTypeSystem owner, String name, int typeId, Builder builder) {
        super(owner, name, typeId, builder.computeDataLength());
        StructDataField[] tmpfields = new StructDataField[builder.fields.size()];
        int fieldIndex = 0;
        int offset = 0;
        for(StructDataField.Builder fb : builder.fields) {
            FixedLenDataType fieldType = fb.getDataType();
            int fieldLen = fieldType.getDataLength();
            tmpfields[fieldIndex++] = new StructDataField(this, fb, offset);
            offset += fieldLen;
        }
        this.fields = tmpfields;
    }

    // ------------------------------------------------------------------------

    public int getFieldCount() {
        return fields.length;
    }
    
    public StructDataField getField(int i) {
        return fields[i];
    }
    
    
    // ------------------------------------------------------------------------

    public static class Builder extends FixedLenDataType.Builder {
        
        private List<StructDataField.Builder> fields = new ArrayList<>();
        
        @Override
        public StructDataType build(DataTypeSystem owner, int typeId) {
            return new StructDataType(owner, name, typeId, this);
        }
        
        public void addField(StructDataField.Builder field) {
            fields.add(field);
        }

        public int computeDataLength() {
            int res = 0;
            for(StructDataField.Builder fb : fields) {
                FixedLenDataType fieldType = fb.getDataType();
                int fieldLen = fieldType.getDataLength();
                res += fieldLen;
            }
            return res;
        }

    }
    
}
