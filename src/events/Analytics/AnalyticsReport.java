package events.Analytics;

import events.EventType;
import java.util.Map;

public record AnalyticsReport (
    long totalEvents,
    long uniqueUsers,
    Map<EventType, Long> eventsPerType,
    Map<EventType, Double> eventPercentages,
    Map<String, Long> topUsers,
    Map<String, Long> eventsPerHour,
    Map<Integer, Long> partitionDistribution
){}
