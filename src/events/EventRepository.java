package events;

import java.util.List;

public interface EventRepository {
    public void save(Event event);
    public List<Event> getAllEvents();
}
