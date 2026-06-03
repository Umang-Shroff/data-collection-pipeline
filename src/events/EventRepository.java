package events;

import java.util.List;

public interface EventRepository {
    public void save(Event event);
    public void saveBatch(List<Event> events);
    public List<Event> getAllEvents();
}
