package events.Processing;

import events.Analytics.AnalyticsReport;
import events.Event;
import events.EventType;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AggregationProcessor {
    
    private final Path sourceFile;
    
    public AggregationProcessor(Path sourceFile){
        this.sourceFile = sourceFile;
    }

    public AnalyticsReport process() throws IOException {
        long totalEvents = 0;

        Set<String> uniqueUsers = new HashSet<>();
        Map<EventType, Long> eventsPerType = new HashMap<>();
        Map<String, Long> userCounts = new HashMap<>();
        Map<String, Long> eventsPerHour = new HashMap<>();
        Map<Integer, Long> partitionDistribution = new HashMap<>();
         try(BufferedReader reader = Files.newBufferedReader(sourceFile)){
             
            String line;
            while((line = reader.readLine()) != null){
                Event event = parseEvent(line);
                totalEvents++;
                uniqueUsers.add(event.userId());
                eventsPerType.merge(event.eventType(), 1L, Long::sum);
                userCounts.merge(event.userId(), 1L, Long::sum);
                partitionDistribution.merge(event.partitionId(), 1L, Long::sum);
                String hourKey = getHourBucket(event.timestamp());
                eventsPerHour.merge(hourKey, 1L, Long::sum);
            }
         }

        Map<EventType, Double> percentages = calculatePercentages(eventsPerType, totalEvents);
        Map<String, Long> topUsers = getTopUsers(userCounts, 5);


        return new AnalyticsReport(totalEvents, uniqueUsers.size(), eventsPerType, percentages, topUsers, eventsPerHour, partitionDistribution);
    }

    private Map<EventType, Double> calculatePercentages(Map<EventType, Long> counts, long totalEvents){
        
        Map<EventType, Double> percentages = new HashMap<>();

        for(Map.Entry<EventType, Long> entry : counts.entrySet()){
            double value = ((double) entry.getValue() / totalEvents) * 100.0;

            percentages.put(entry.getKey(), value);
        }
        return percentages;
    }

    private Map<String, Long> getTopUsers(Map<String, Long> userCounts, int limit){
        return userCounts
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(limit)
            .collect(LinkedHashMap::new, 
                (map, entry) -> 
                    map.put(
                        entry.getKey(), entry.getValue()
                    ), 
                    LinkedHashMap::putAll
                );
    }

    private String getHourBucket(long timestamp){
        LocalDateTime dateTime = Instant.ofEpochMilli(timestamp)
                                        .atZone(ZoneId.systemDefault()).toLocalDateTime();

        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
    }

    private Event parseEvent(String line){
        
        String[] parts = line.split("\\|");
        return new Event(Long.parseLong(parts[0]), parts[1], EventType.valueOf(parts[2]), Long.parseLong(parts[3]), Integer.parseInt(parts[4]));
    }
}
