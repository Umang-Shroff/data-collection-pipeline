package events;

public class EventReciever {

    private final EventRepository eventRepository;
    private final Partitioner partitioner;

    public EventReciever(EventRepository eventRepository, Partitioner partitioner) {
        this.eventRepository = eventRepository;
        this.partitioner = partitioner;
    }
    
    public Event recieve(String userId, EventType eventType){
        // return new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis());

        int partition = partitioner.getPartition(userId);

        Event event = new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis(),partition);

        eventRepository.save(event);

        return event;
    }
}
