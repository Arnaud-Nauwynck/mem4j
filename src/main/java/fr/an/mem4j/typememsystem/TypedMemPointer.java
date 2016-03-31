package fr.an.mem4j.typememsystem;

import fr.an.mem4j.datatypesystem.reflect.DataType;

public class TypedMemPointer {

    private final long addr;
    private final DataType dataType;
    
    // ------------------------------------------------------------------------
    
    public TypedMemPointer(long addr, DataType dataType) {
        this.addr = addr;
        this.dataType = dataType;
    }

    // ------------------------------------------------------------------------
    
    public long getAddr() {
        return addr;
    }

    public DataType getDataType() {
        return dataType;
    }

    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (addr ^ (addr >>> 32));
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
        TypedMemPointer other = (TypedMemPointer) obj;
        if (addr != other.addr)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "0x" + Long.toHexString(addr) + "(" + dataType + ")";
    }
    
}
