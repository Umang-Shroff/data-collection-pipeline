package events.Routing;

import events.Partition.PartitionManager;

public class Topic {

    private final String topicName;
    private final PartitionManager partitionManager;

    public Topic(String topicName, PartitionManager partitionManager){
        this.topicName = topicName;
        this.partitionManager = partitionManager;
    }

    public String getTopicName(){
        return topicName;
    }

    public PartitionManager getPartitionManager(){
        return partitionManager;
    }
}
