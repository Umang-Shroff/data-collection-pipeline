package events.Workers;

import events.Event;
import events.EventRepository;
import events.EventQueue.EventQueue;
import events.Batch.BatchConfig;

import java.util.ArrayList;
import java.util.List;

public class BatchWorker implements Runnable {
    
    private final EventQueue eventQueue;
    private final EventRepository eventRepository;
    private final List<Event> batch;

    private volatile boolean running = true;
    private final String workerName;

    public BatchWorker(String workerName, EventQueue eventQueue, EventRepository eventRepository) {
        this.workerName = workerName;
        this.eventQueue = eventQueue;
        this.eventRepository = eventRepository;
        this.batch = new ArrayList<>();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while(running){
            try{
                // Event event = eventQueue.consume();
                Event event = eventQueue.poll(BatchConfig.FLUSH_INTERVAL_SECONDS);
                if (event != null) {
                    
                    batch.add(event);
                
                    System.out.println("["+ workerName +"] Processing Event: " + event.eventId());
                    System.out.println("["+ workerName +"] Added event " + event.eventId() + " to batch.");
                    
                    if(batch.size() >= BatchConfig.BATCH_SIZE){
                        System.out.println("\n["+ workerName +"] Batch size reached.");
                        flushBatch();
                    }
                }else{

                    if(!batch.isEmpty()){
                        System.out.println("\n["+ workerName +"] Timeout reached.");
                        flushBatch();
                    }
                }
            } catch (InterruptedException e) {
                if(!batch.isEmpty()){
                    System.out.println("\n["+ workerName +"] Flushing remaining events before shutdown.");
                    flushBatch();
                }
                Thread.currentThread().interrupt();
                System.out.println("["+ workerName +"] Interrupted, shutting down.");
                break; // Exit the loop if interrupted
            }
        }
    }

    private void flushBatch() {

        if(batch.isEmpty()){
            return;
        }

        eventRepository.saveBatch(batch);

        System.out.println("\n["+ workerName +"] Flushed " + batch.size() + " events\n");
        
        batch.clear();
    }

    public int currentBatchSize() {
        return batch.size();
    }
}
