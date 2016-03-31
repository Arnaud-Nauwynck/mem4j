package fr.an.mem4j.datatypesystem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.an.mem4j.collection.util.CopyOnWriteUtils;
import fr.an.mem4j.datatypesystem.reflect.DataType;
import fr.an.mem4j.datatypesystem.reflect.NamedDataType;
import fr.an.mem4j.datatypesystem.reflect.PrimitiveDataType;

/**
 * Registry of DataType (struct layout)<br/>
 *  
 * This is equivalent to a java.lang.ClassLoader, but for data type layout only (no type hierarchy?)<BR/>
 * 
 * data type are defined equivalently as in an IDL (=Interface Definition Langage, like rpc, corba, protobuf, ..)  
 * data type can then be persisted binary mapped file, so Off-Heap ... and can be wrapped in a "Pointer Accessor" class
 * but this is NOT the goal of the DataTypeSystem to define accessor classes.
 * 
 */
public class DataTypeSystem {

    private final Object lock = new Object();
    
    private int typeIdGenerator = 1;
    
    /**
     * immutable, copy-on-write
     */
    private Map<Integer,DataType> typeId2DataType = Collections.emptyMap();

    /**
     * immutable, copy-on-write
     */
    private Map<String,DataType> name2DataType = new HashMap<>();
    
    // ------------------------------------------------------------------------

    public DataTypeSystem() {
        registerBuiltinDataTypes();
    }

    // ------------------------------------------------------------------------

    public DataType findDataTypeById(int id) {
        return typeId2DataType.get(id);
    }

    public DataType findDataTypeByName(String name) {
        return name2DataType.get(name);
    }

    public void registerDataType(NamedDataType.Builder builder) {
        synchronized(lock) {
            if (builder.getName() == null || name2DataType.get(builder.getName()) != null) {
                throw new IllegalArgumentException("data type name '" + builder.getName() + "' already registered");
            }
            NamedDataType dataType = builder.build(this, newTypeId());
            doRegisterDataType(dataType);
        }
    }
    
    // ------------------------------------------------------------------------

    private int newTypeId() {
        return typeIdGenerator++;
    }
    
    private void registerBuiltinDataTypes() {
        doRegisterDataType(new PrimitiveDataType(this, "bool", newTypeId(), 8, Boolean.class));
        doRegisterDataType(new PrimitiveDataType(this, "byte", newTypeId(), 8, Byte.class));
        doRegisterDataType(new PrimitiveDataType(this, "char", newTypeId(), 16, Character.class));
        doRegisterDataType(new PrimitiveDataType(this, "short", newTypeId(), 16, Short.class));
        doRegisterDataType(new PrimitiveDataType(this, "int", newTypeId(), 32, Integer.class));
        doRegisterDataType(new PrimitiveDataType(this, "long", newTypeId(), 64, Long.class));
        doRegisterDataType(new PrimitiveDataType(this, "float", newTypeId(), 32, Float.class));
        doRegisterDataType(new PrimitiveDataType(this, "double", newTypeId(), 64, Double.class));
    }

    private void doRegisterDataType(NamedDataType dataType) {
        this.typeId2DataType = CopyOnWriteUtils.immutableCopyWithPut(typeId2DataType, dataType.getTypeId(), dataType);
        this.name2DataType = CopyOnWriteUtils.immutableCopyWithPut(name2DataType, dataType.getName(), dataType);
    }
    
}
