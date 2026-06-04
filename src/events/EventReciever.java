package events;

import events.Partition.Partitioner;
import events.Partition.PartitionManager;
import events.Validation.EventValidator;
import events.Validation.ValidationResult;

public class EventReciever {

    private final Partitioner partitioner;
    private final PartitionManager partitionManager;
    private final EventValidator validator;

    public EventReciever(PartitionManager partitionManager, Partitioner partitioner, EventValidator validator) {
        this.partitionManager = partitionManager;
        this.partitioner = partitioner;
        this.validator = validator;
    }
    
    public Event recieve(String userId, EventType eventType) throws InterruptedException {
        // return new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis());

        int partition = partitioner.getPartition(userId);
        
        Event event = new Event(EventIdGenerator.generateId(), userId, eventType, System.currentTimeMillis(),partition);
        
        ValidationResult result = validator.validate(event);
        if(!result.valid()){
            System.out.println("[VALIDATION FAILED] " + result.reason());
            return null;
        }

        partitionManager.getQueue(partition).publish(event);

        return event;
    }
}
