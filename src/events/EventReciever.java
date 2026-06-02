package events;

public class EventReciever {
    private final EventRepository eventRepository;

    public EventReciever(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }
    
    public Event recieve(String userId, EventType eventType){
        // return new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis());

        Event event = new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis());

        eventRepository.save(event);

        return event;
    }
}
