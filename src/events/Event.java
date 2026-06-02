package events;

public record Event(
    long eventId,
    String userId,
    EventType eventType,
    long timestamp,
    int partitionId // for sharding
) {}
