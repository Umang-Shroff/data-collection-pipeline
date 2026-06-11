package events.Routing;

import events.Partition.PartitionManager;

import java.util.HashMap;
import java.util.Map;

public class TopicManager {
    
    private final Map<String, Topic> topics;
    private final int partitionCount;
    private final int queueCapacity;

    public TopicManager(int partitionCount, int queueCapacity){
        this.partitionCount = partitionCount;
        this.queueCapacity = queueCapacity;

        this.topics = new HashMap<>();
    }

    public Topic getOrCreateTopic(String topicName){
        return topics.computeIfAbsent(
            topicName, 
            name -> new Topic(
                name,
                new PartitionManager(partitionCount, queueCapacity)
            )
        );
    }

    public Map<String, Topic> getTopics(){
        return topics;
    }
}
