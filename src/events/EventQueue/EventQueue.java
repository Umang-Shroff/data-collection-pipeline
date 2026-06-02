package events.EventQueue;

// thread safe queue
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import events.Event;


public class EventQueue {
    
    private final BlockingQueue<Event> queue;

    public EventQueue(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    public void publish(Event event) throws InterruptedException {
        queue.put(event);
    }

    public Event consume() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }

    public Object[] snapshot() {
        return queue.toArray();
    }
    
}
