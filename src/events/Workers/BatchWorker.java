package events.Workers;

import events.Event;
import events.EventRepository;
import events.EventQueue.EventQueue;

public class BatchWorker implements Runnable {
    
    private final EventQueue eventQueue;
    private final EventRepository eventRepository;

    public BatchWorker(EventQueue eventQueue, EventRepository eventRepository) {
        this.eventQueue = eventQueue;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run() {
        while(true){
            try{
                Event event = eventQueue.consume();
                System.out.println("[WORKER] Processing Event: " + event.eventId());
                eventRepository.save(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[WORKER] Interrupted, shutting down.");
                break; // Exit the loop if interrupted
            }
        }
    }
}
