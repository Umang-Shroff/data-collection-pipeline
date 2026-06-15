package events.Analytics.LogFileAnalytics;

import events.EventType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AnalyticsWriter {

    private final Path filePath;
    public AnalyticsWriter(String fileName){
        this.filePath = Paths.get(fileName);
        try{
            Path parent = filePath.getParent();
            if(parent != null){
                Files.createDirectories(parent);
            }
            if(!Files.exists(filePath)){
                Files.createFile(filePath);
            }
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void write(AnalyticsReport report){
        try(BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.TRUNCATE_EXISTING)){
            writer.write(
                    "===================================="
            );
            writer.newLine();

            writer.write(
                    "EVENT ANALYTICS REPORT"
            );
            writer.newLine();

            writer.write(
                    "===================================="
            );
            writer.newLine();
            writer.newLine();

            writer.write(
                    "Total Events : "
                            + report.totalEvents()
            );
            writer.newLine();

            writer.write(
                    "Unique Users : "
                            + report.uniqueUsers()
            );
            writer.newLine();
            writer.newLine();

            writer.write(
                    "Events By Type"
            );
            writer.newLine();

            for(var entry : report.eventsPerType().entrySet()){
                writer.write(entry.getKey() + " -> " + entry.getValue());
                writer.newLine();
            }

            writer.newLine();
            writer.write("Event Type Percentages");
            writer.newLine();

            for(var entry : report.eventPercentages().entrySet()){
                writer.write(entry.getKey() + " -> " + String.format("%.2f%%", entry.getValue()));
                writer.newLine();
            }
            writer.newLine();
            writer.write("Top 5 Users");

            writer.newLine();

            for(var entry : report.topUsers().entrySet()){
                writer.write(entry.getKey() + " -> " + entry.getValue());
                writer.newLine();
            }
            writer.newLine();
            writer.write("Events Per Hour");
            writer.newLine();

            for(var entry : report.eventsPerHour().entrySet()){
                writer.write(entry.getKey() + " -> " + entry.getValue());
                writer.newLine();
            }
            writer.newLine();
            writer.write("Partition Distribution");
            writer.newLine();

            for(var entry : report.partitionDistribution().entrySet()){
                writer.write("Partition" + entry.getKey() + " -> " + entry.getValue());
                writer.newLine();
            }
            
        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
