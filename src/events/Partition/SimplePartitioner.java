package events.Partition;

public class SimplePartitioner implements Partitioner{
    
    private final int partitionCount;

    public SimplePartitioner(int partitionCount) {
        this.partitionCount = partitionCount;
    }

    @Override
    public int getPartition(String userId) {
        return Math.abs(userId.hashCode()) % partitionCount;
    }

}
