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

    public volatile boolean running = true;

    public BatchWorker(EventQueue eventQueue, EventRepository eventRepository) {
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
                
                    System.out.println("[WORKER] Processing Event: " + event.eventId());
                    System.out.println("[WORKER] Added event " + event.eventId() + " to batch.");
                    
                    if(batch.size() >= BatchConfig.BATCH_SIZE){
                        System.out.println("\n[WORKER] Batch size reached.");
                        flushBatch();
                    }
                }else{

                    if(!batch.isEmpty()){
                        System.out.println("\n[WORKER] Timeout reached.");
                        flushBatch();
                    }
                }
            } catch (InterruptedException e) {
                if(!batch.isEmpty()){
                    System.out.println("\n[WORKER] Flushing remaining events before shutdown.");
                    flushBatch();
                }
                Thread.currentThread().interrupt();
                System.out.println("[WORKER] Interrupted, shutting down.");
                break; // Exit the loop if interrupted
            }
        }
    }

    private void flushBatch() {

        if(batch.isEmpty()){
            return;
        }

        eventRepository.saveBatch(batch);

        System.out.println("[WORKER] Flushed " + batch.size() + " events\n");
        
        batch.clear();
    }
}
