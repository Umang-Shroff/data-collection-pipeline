package events.EventQueue;

public class QueueInspector {
    
    private final EventQueue queue;

    public QueueInspector(EventQueue queue){
        this.queue = queue;
    }

    public void printStats() {
        System.out.println("Queue Size = " + queue.size());

        Object[] events = queue.snapshot();

        for(Object obj : events){
            System.out.println(obj);
        }
        System.out.println();
    }

}
