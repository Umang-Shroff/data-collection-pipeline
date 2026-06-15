package events;

import java.util.Map;
// import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsGenerator {
    private final EventRepository eventRepository;

    public StatsGenerator(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public long countAllEvents(){
        return eventRepository.getAllEvents().size();
    }

    public Map<EventType, Integer> countAllByType(){
        Map<EventType, Integer> counts = new HashMap<>();

        for(Event e: eventRepository.getAllEvents()){
            EventType type = e.eventType();
            counts.put(type, counts.getOrDefault(type, 0)+1);
        }

        return counts;
    }

    public long countByType(EventType eventType){
        long count = 0;
        List<Event> events = eventRepository.getAllEvents();
        for(Event e : events){
            if(e.eventType() == eventType){
                count++;
            }
        }
        return count;
    }
}
