package events;

import java.util.Map;

public record Event(
    long eventId,
    String tenantId,
    String userId,
    EventType eventType,
    long timestamp,
    int partitionId, // for sharding
    Map<String, Object> payload
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
                ", payload=" + payload +
                '}';
    }
}
