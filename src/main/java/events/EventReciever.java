package events;

import events.Partition.Partitioner;
import events.Partition.PartitionManager;
import events.Validation.EventValidator;
import events.Validation.ValidationResult;
import events.Routing.EventRouter;
import events.Routing.Topic;
import events.Routing.TopicManager;

public class EventReciever {

    private final Partitioner partitioner;
    private final TopicManager topicManager;
    private final EventValidator validator;
    private final EventRouter router;

    public EventReciever(TopicManager topicManager, Partitioner partitioner, EventValidator validator, EventRouter router) {
        this.topicManager = topicManager;
        this.partitioner = partitioner;
        this.validator = validator;
        this.router = router;
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

        String topicName = router.route(event);

        Topic topic = topicManager.getOrCreateTopic(topicName);

        PartitionManager partitionManager = topic.getPartitionManager();

        partitionManager.getQueue(partition).publish(event);

        // System.out.println("[ROUTER] Event " + event.eventId() + " -> " + topicName + " -> Partition " + partition);

        return event;
    }
}
