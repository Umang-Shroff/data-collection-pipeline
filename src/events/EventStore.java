package events;

import java.util.ArrayList;
import java.util.List;

public class EventStore implements EventRepository {
    private List<Event> events;

    public EventStore () {
        events = new ArrayList<>();
    }

    public void save(Event event) {
        events.add(event);
    }

    public List<Event> getAllEvents(){
        return new ArrayList<>(events);
    }
}
