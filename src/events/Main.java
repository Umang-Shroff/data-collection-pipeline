package events;

// import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

import events.Partition.Partitioner;
import events.Partition.SimplePartitioner;
import events.Workers.BatchWorker;
import events.LogGenerator.FileEventStore;
import events.Partition.PartitionManager;
import events.Validation.EventValidator;
import events.Validation.BasicEventValidator;

public class Main {
    public static void main(String[] args) {
        try{
            // set number of partitions and queues / workers for that partition each
            int partitionCount = 4;
            int queueCapacity = 100;
            String logFile = "EventLogs/events.log";

            Random random = new Random();

            EventRepository eventStore = new FileEventStore(logFile);

            // EventQueue queue = new EventQueue(queueCapacity);
            PartitionManager partitionManager = new PartitionManager(partitionCount, queueCapacity);

            Partitioner partitioner = new SimplePartitioner(partitionCount);

            EventValidator validator = new BasicEventValidator();

            EventReciever eventReciever = new EventReciever(partitionManager, partitioner, validator);

            StatsGenerator statsGenerator = new StatsGenerator(eventStore);

            List<BatchWorker> workers = new ArrayList<>();
            List<Thread> workerThreads = new ArrayList<>();
        
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

            // Start one worker per partition
            for(int i=0; i<partitionManager.getPartitionCount(); i++){
                BatchWorker worker = new BatchWorker("Worker-"+i, partitionManager.getQueue(i), eventStore);
                Thread workerThread = new Thread(worker);
                workers.add(worker);
                workerThreads.add(workerThread);
                workerThread.start();
            }
            
            System.out.println("Total queued events = " + partitionManager.totalQueuedEvents());

            while(!partitionManager.allQueuesEmpty()){
                Thread.sleep(1000);
            }
            
            for(BatchWorker worker : workers){
                worker.stop();
            }

            for(Thread thread : workerThreads){
                thread.interrupt();
            }

            for(Thread thread : workerThreads){
                thread.join();
            }



            //Printing all events
            System.out.println("Total events = "+statsGenerator.countAllEvents());

            //Printing all events by type 
            System.out.println("Events by type: ");
            Map<EventType, Integer> counts = statsGenerator.countAllByType();

            for(Map.Entry<EventType, Integer> entry : counts.entrySet()){
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }  

            // Printing events of specified type
            // System.out.println("Purchase events: "+ statsGenerator.countByType(EventType.PURCHASE));

        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
