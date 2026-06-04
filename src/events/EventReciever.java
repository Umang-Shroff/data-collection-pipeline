package events;

import events.Partition.Partitioner;
import events.Partition.PartitionManager;

public class EventReciever {

    private final Partitioner partitioner;
    private final PartitionManager partitionManager;

    public EventReciever(PartitionManager partitionManager, Partitioner partitioner) {
        this.partitionManager = partitionManager;
        this.partitioner = partitioner;
    }
    
    public Event recieve(String userId, EventType eventType) throws InterruptedException {
        // return new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis());

        int partition = partitioner.getPartition(userId);

        Event event = new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis(),partition);

        partitionManager.getQueue(partition).publish(event);

        return event;
    }
}
