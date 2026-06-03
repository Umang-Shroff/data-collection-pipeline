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

    public BatchWorker(EventQueue eventQueue, EventRepository eventRepository) {
        this.eventQueue = eventQueue;
        this.eventRepository = eventRepository;
        this.batch = new ArrayList<>();
    }

    @Override
    public void run() {
        while(true){
            try{
                Event event = eventQueue.consume();
                System.out.println("[WORKER] Processing Event: " + event.eventId());
                batch.add(event);
                System.out.println("[WORKER] Added event " + event.eventId() + " to batch.");
                if(batch.size() >= BatchConfig.BATCH_SIZE){
                    flushBatch();
                }
            } catch (InterruptedException e) {
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

        System.out.println("\n[WORKER] Flushed " + batch.size() + " events\n");
        
        batch.clear();
    }
}
