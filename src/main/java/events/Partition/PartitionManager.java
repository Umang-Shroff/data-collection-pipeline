package events.Partition;

import java.util.ArrayList;
import java.util.List;
import events.EventQueue.EventQueue;

public class PartitionManager {
    
    private final List<EventQueue> queues;

    public PartitionManager(int partitionCount, int queueCapacity) {
        queues = new ArrayList<>();
        
        for(int i=0; i<partitionCount; i++){
            queues.add(new EventQueue(queueCapacity));
        }
    }

    public EventQueue getQueue(int partitionId){
        return queues.get(partitionId);
    }

    public int getPartitionCount(){
        return queues.size();
    }

    public int totalQueuedEvents() {
        int total = 0;
        for(EventQueue queue : queues){
            total += queue.size();
        }
        return total;
    }

    public boolean allQueuesEmpty() {
        for(EventQueue queue : queues){
            if(queue.size() > 0){
                return false;
            }
        }
        return true;
    }
}
