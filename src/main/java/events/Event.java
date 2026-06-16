package events;

import java.util.Map;

public record Event(
    long eventId,
    String tenantId,
    String userId,
    String productId,
    EventType eventType,
    long timestamp,
    int partitionId, // for sharding
    Map<String, Object> payload,

    double amount,
    String device,
    String campaignId
) {
    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", tenantId=" + tenantId + '\'' +
                ", userId='" + userId + '\'' +
                ", productId='" + productId + '\'' +
                ", eventType=" + eventType +
                ", timestamp=" + timestamp +
                ", partitionId=" + partitionId +
                ", payload=" + payload +
                ", amount=" + amount +
                ", device='" + device + '\'' +
                ", campaignId='" + campaignId + '\'' +
                '}';
    }
}
