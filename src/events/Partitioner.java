package events;

public interface Partitioner {
    int getPartition(String userId);
}
