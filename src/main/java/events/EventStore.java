package events;

// import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class EventStore implements EventRepository {
    private List<Event> events;

    public EventStore () {
        this.events = new CopyOnWriteArrayList<>();
    }

    @Override
    public void save(Event event) {
        events.add(event);
    }

    @Override
    public void saveBatch(List<Event> batch) {
        events.addAll(batch);
    }

    @Override
    public List<Event> getAllEvents(){
        return new CopyOnWriteArrayList<>(events);
    }
}
