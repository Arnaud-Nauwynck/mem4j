package fr.an.mem4j.typememsystem.partitioned;

import fr.an.mem4j.offheap.Malloc;

public class MemPartition {

    private final int partitionId;
    private final String partitionName; 
    private final long partitionFlag;
    
    private final Malloc malloc;

    // ------------------------------------------------------------------------
    
    public MemPartition(int partitionId, String partitionName, long partitionFlag, Malloc malloc) {
        this.partitionId = partitionId;
        this.partitionName = partitionName;
        this.partitionFlag = partitionFlag;
        this.malloc = malloc;
    }

    // ------------------------------------------------------------------------
    
    public int getPartitionId() {
        return partitionId;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public long getPartitionFlag() {
        return partitionFlag;
    }

    public Malloc getMalloc() {
        return malloc;
    }

    // ------------------------------------------------------------------------
    
    
}
