package events;

public record Event(
    long eventId,
    String tenantId,
    String userId,
    EventType eventType,
    long timestamp,
    int partitionId // for sharding
) {
    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", tenantId=" + tenantId +
                ", userId='" + userId + '\'' +
                ", eventType=" + eventType +
                ", timestamp=" + timestamp +
                ", partitionId=" + partitionId +
                '}';
    }
}
