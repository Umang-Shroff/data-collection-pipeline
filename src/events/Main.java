package events;

// import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        EventRepository eventStore = new EventStore();

        EventReciever eventReciever = new EventReciever(eventStore);

        StatsGenerator statsGenerator = new StatsGenerator(eventStore);

        Event e1 = eventReciever.recieve("abcd123", EventType.APP_OPEN);
        Event e2 = eventReciever.recieve("dbxy6654", EventType.PURCHASE);
        Event e3 = eventReciever.recieve("rrtx87", EventType.PURCHASE);
        Event e4 = eventReciever.recieve("iop0945", EventType.USER_LOGIN);

        System.out.println(e1 + "\n" + e2 + "\n" + e3 + "\n" + e4);

        //Printing all events
        System.out.println("Total events = "+statsGenerator.countAllEvents());

        //Printing all events by type 
        System.out.println("Events by type: ");
        Map<EventType, Integer> counts = statsGenerator.countAllByType();

        for(Map.Entry<EventType, Integer> entry : counts.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }  

        //Printing events of specified type
        System.out.println("Purchase events: "+ statsGenerator.countByType(EventType.PURCHASE));
    }
}
