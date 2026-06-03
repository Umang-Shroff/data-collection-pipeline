package events;

// import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import events.Partition.Partitioner;
import events.Partition.SimplePartitioner;
import events.EventQueue.EventQueue;
import events.EventQueue.QueueInspector;
import events.Workers.BatchWorker;

public class Main {
    public static void main(String[] args) {
        try{

            Random random = new Random();

            EventRepository eventStore = new EventStore();

            EventQueue queue = new EventQueue(100);

            QueueInspector inspector = new QueueInspector(queue);

            Partitioner partitioner = new SimplePartitioner(4);

            EventReciever eventReciever = new EventReciever(queue, partitioner);

            StatsGenerator statsGenerator = new StatsGenerator(eventStore);

            BatchWorker worker = new BatchWorker(queue, eventStore);
            
            Thread workerThread = new Thread(worker);
        
            // Event e1 = eventReciever.recieve("abcd123", EventType.APP_OPEN);
            // Event e2 = eventReciever.recieve("dbxy6654", EventType.PURCHASE);
            // Event e3 = eventReciever.recieve("rrtx87", EventType.PURCHASE);
            // Event e4 = eventReciever.recieve("iop0945", EventType.USER_LOGIN);
            // System.out.println(e1 + "\n" + e2 + "\n" + e3 + "\n" + e4);

            for(int i=0;i<12;i++){
                String userId = "user"+i;
                EventType eventType = EventType.values()[random.nextInt(EventType.values().length)];
                eventReciever.recieve(userId, eventType);
            }

            //Printing queue stats
            inspector.printStats();

            workerThread.start();

            Thread.sleep(1000); // Sleep for demo

            worker.stop();
            workerThread.interrupt();

            // Wait until worker has flushed
            workerThread.join();

            //Printing all events
            System.out.println("Total events = "+statsGenerator.countAllEvents());

            //Printing all events by type 
            System.out.println("Events by type: ");
            Map<EventType, Integer> counts = statsGenerator.countAllByType();

            for(Map.Entry<EventType, Integer> entry : counts.entrySet()){
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }  

            // Printing events of specified type
            System.out.println("Purchase events: "+ statsGenerator.countByType(EventType.PURCHASE));

        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
