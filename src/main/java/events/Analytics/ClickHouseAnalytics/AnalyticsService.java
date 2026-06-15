package events.Analytics.ClickHouseAnalytics;

import java.util.Map;

public interface AnalyticsService {
    
    long getTotalEvents();

    Map<String, Long> getEventCountPerType();

    Map<String, Double> getEventTypePercentages();

    Map<String, Long> getTopUsers(int limit);

    Map<String, Long> getEventsPerHour();

    Map<Integer, Long> getPartitionDistribution();
}
