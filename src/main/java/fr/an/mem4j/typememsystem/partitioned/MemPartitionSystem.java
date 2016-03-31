package fr.an.mem4j.typememsystem.partitioned;

import java.util.List;

public abstract class MemPartitionSystem {

    public abstract List<MemPartition> partitions();
    
    public abstract MemPartition partitionOf(long virtualAddr);
    
    public abstract long virtualToPartitionAddr(long virtualAddr);
    
    public abstract long partitionToVirtualAddr(long localAddr, MemPartition partition);
    
}
