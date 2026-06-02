package events;

import events.Partition.Partitioner;
import events.EventQueue.EventQueue;

public class EventReciever {

    private final EventQueue eventQueue;
    private final Partitioner partitioner;

    public EventReciever(EventQueue eventQueue, Partitioner partitioner) {
        this.eventQueue = eventQueue;
        this.partitioner = partitioner;
    }
    
    public Event recieve(String userId, EventType eventType) throws InterruptedException {
        // return new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis());

        int partition = partitioner.getPartition(userId);

        Event event = new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis(),partition);

        eventQueue.publish(event);

        return event;
    }
}
