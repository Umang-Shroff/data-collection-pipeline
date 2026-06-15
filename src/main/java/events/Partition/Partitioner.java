package events.Partition;

public interface Partitioner {
    int getPartition(String userId);
}
