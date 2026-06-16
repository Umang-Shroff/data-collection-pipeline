package events;

// import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.Paths;
import java.io.IOException;

import events.Partition.Partitioner;
import events.Partition.SimplePartitioner;
import events.Workers.BatchWorker;
// import events.LogGenerator.FileEventStore;
import events.Partition.PartitionManager;
import events.Routing.TopicManager;
import events.Storage.ClickHouseEventStore;
import events.Tenant.TenantResolver;
import events.Tenant.TenantStore;
import events.Routing.CategoryRouter;
import events.Routing.EventRouter;
import events.Routing.Topic;
import events.Validation.ValidationPipeline;
import events.Validation.EventTypeValidator.EventTypeValidator;
import events.Validation.PayloadValidator.PayloadValidator;
import events.Validation.TenantValidator.TenantValidator;
import events.Validation.UserValidator.UserValidator;
import events.Validation.TimestampValidator.TimestampValidator;
// import events.Processing.DedupProcessor;
import events.LogGenerator.FileConfig;
import events.Analytics.ClickHouseAnalytics.AnalyticsService;
import events.Analytics.ClickHouseAnalytics.ClickHouseAnalyticsService;
import events.Analytics.LogFileAnalytics.AnalyticsReport;
import events.Analytics.LogFileAnalytics.AnalyticsWriter;
// import events.Processing.AggregationProcessor;

public class Main {
    public static void main(String[] args) {
        try{
            // set number of partitions and queues / workers for that partition each
            int partitionCount = 5;
            int queueCapacity = 1000;

            Random random = new Random();

            // EventRepository eventStore = new FileEventStore(FileConfig.EVENT_LOG);
            EventRepository eventStore = new ClickHouseEventStore();

            // DedupProcessor dedupProcessor = new DedupProcessor(
            //     Paths.get(FileConfig.EVENT_LOG),Paths.get(FileConfig.CLEAN_EVENT_LOG)
            // );

            // EventQueue queue = new EventQueue(queueCapacity);
            TopicManager topicManager = new TopicManager(partitionCount, queueCapacity);

            EventRouter router = new CategoryRouter();

            Partitioner partitioner = new SimplePartitioner(partitionCount);

            TenantStore tenantStore = new TenantStore();
            TenantResolver tenantResolver = new TenantResolver(tenantStore);

            ValidationPipeline validator = new ValidationPipeline();
            validator.addValidator(new TenantValidator(tenantResolver));
            validator.addValidator(new UserValidator());
            validator.addValidator(new EventTypeValidator());
            validator.addValidator(new TimestampValidator());
            validator.addValidator(new PayloadValidator());

            EventReceiver eventReceiver = new EventReceiver(topicManager, partitioner, validator, router);

            StatsGenerator statsGenerator = new StatsGenerator(eventStore);

            List<BatchWorker> workers = new ArrayList<>();
            List<Thread> workerThreads = new ArrayList<>();
        
            // Event e1 = eventReceiver.receive("tenant-1","abcd123", EventType.APP_OPEN);
            // Event e2 = eventReceiver.receive("tenant-2","dbxy6654", EventType.PURCHASE);
            // Event e3 = eventReceiver.receive("tenant-3","rrtx87", EventType.PURCHASE);
            // Event e4 = eventReceiver.receive("tenant-4","iop0945", EventType.USER_LOGIN);
            // System.out.println(e1 + "\n" + e2 + "\n" + e3 + "\n" + e4);

            for(int i=0;i<1000;i++){
                String tenantId = "tenant-"+(random.nextInt(4)+1);
                String userId = "user"+ random.nextInt(500);
                EventType eventType = EventType.values()[random.nextInt(EventType.values().length)];
                Map<String, Object> payload = createPayload(eventType, random);
                
                double amount = payload.containsKey("amount") ? ((Number) payload.get("amount")).doubleValue() : 0;
                String device = payload.containsKey("device") ? payload.get("device").toString() : "";
                String campaignId = payload.containsKey("campaignId") ? payload.get("campaignId").toString() : "";

                eventReceiver.receive(tenantId, userId, eventType, payload, amount, device, campaignId);
            }

            // Start one worker per partition
            for(Topic topic : topicManager.getTopics().values()){

                PartitionManager pm = topic.getPartitionManager();
                
                for(int i=0;i<pm.getPartitionCount();i++){
                    
                    String workerName = topic.getTopicName() + "-Worker-" + i;

                    BatchWorker worker = new BatchWorker(workerName, pm.getQueue(i), eventStore);

                    Thread workerThread = new Thread(worker);
                    workers.add(worker);
                    workerThreads.add(workerThread);
                    workerThread.start();
                }
            }
            
            System.out.println("Total queued events = " + topicManager.totalQueuedEvents());

            while(!topicManager.allQueuesEmpty()){
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

            // try{
            //     dedupProcessor.process();
            // } catch(IOException e) {
            //     System.err.println("Error processing file: " + e.getMessage());
            //     e.printStackTrace();
            // }

            // try{
            //     AggregationProcessor aggregationProcessor = new AggregationProcessor(Paths.get("EventLogs/clean-events.log"));
            //     AnalyticsReport report = aggregationProcessor.process();
            //     AnalyticsWriter analyticsWriter = new AnalyticsWriter("EventLogs/metrics.log");
            //     analyticsWriter.write(report);
            // } catch(IOException e){
            //     System.err.println("Error processing file: " + e.getMessage());
            //     e.printStackTrace();
            // }

            // //Printing all events
            // System.out.println("Total events = "+statsGenerator.countAllEvents());

            // //Printing all events by type 
            // System.out.println("Events by type: ");
            // Map<EventType, Integer> counts = statsGenerator.countAllByType();

            // for(Map.Entry<EventType, Integer> entry : counts.entrySet()){
            //     System.out.println(entry.getKey() + " -> " + entry.getValue());
            // }  

            // Printing events of specified type
            // System.out.println("Purchase events: "+ statsGenerator.countByType(EventType.PURCHASE));

            // AnalyticsService analytics =
            // new ClickHouseAnalyticsService();

            // System.out.println();
            // System.out.println("===== ANALYTICS =====");

            // System.out.println(
            //         "Total Events: "
            //         + analytics.getTotalEvents()
            // );

            // System.out.println(
            //         "Event Counts: "
            //         + analytics.getEventCountPerType()
            // );

            // System.out.println(
            //         "Percentages: "
            //         + analytics.getEventTypePercentages()
            // );

            // System.out.println(
            //         "Top Users: "
            //         + analytics.getTopUsers(5)
            // );

            // System.out.println(
            //         "Events Per Hour: "
            //         + analytics.getEventsPerHour()
            // );

            // System.out.println(
            //         "Partition Distribution: "
            //         + analytics.getPartitionDistribution()
            // );
            
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
    private static Map<String, Object> createPayload(EventType eventType, Random random){
        Map<String, Object> payload = new HashMap<>();

        switch(eventType){
            case PURCHASE -> {
                payload.put("amount", random.nextInt(5000)+100);
                payload.put("currency", "INR");
                payload.put("productId", "product-" + random.nextInt(100));
            }

            case USER_LOGIN -> {
                String[] devices = {"android", "ios", "web"};
                payload.put("device", devices[random.nextInt(devices.length)]);
            }

            case APP_OPEN -> {
                String[] sources = {"notification", "organic", "deep-link"};
                payload.put("source", sources[random.nextInt(sources.length)]);
            }

            case CAMPAIGN_CLICK -> {
                payload.put("campaignId", "campaign-" + random.nextInt(20));
                payload.put("channel", random.nextBoolean() ? "push" : "email");
            }
        }
        return payload;
    }
}
