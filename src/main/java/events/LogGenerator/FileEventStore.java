package events.LogGenerator;

import events.Event;
import events.EventRepository;
import events.EventType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileEventStore implements EventRepository {
    
    private final Path filePath;

    public FileEventStore(String fileName){

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

    private String toLine(Event event){
        return event.eventId() + "|"
               + event.userId() + "|"
               + event.eventType() + "|"
               + event.timestamp() + "|"
               + event.partitionId();
    }

    private Event parseLog(String line){
        String[] parts = line.split("\\|");
        return new Event(Long.parseLong(parts[0]), parts[1], EventType.valueOf(parts[2]), Long.parseLong(parts[3]), Integer.parseInt(parts[4]));
    }

    @Override
    public synchronized void saveBatch(List<Event> events) {
        try(
            BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        ){
            System.out.println("[FILE_STORE] Persisting " + events.size() + " events");
            for(Event event : events){
                writer.write(toLine(event));
                writer.newLine();
            }
        } catch(IOException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Event event) {
        saveBatch(List.of(event));
    }

    @Override
    public List<Event> getAllEvents() {
        try{
            List<String> lines = Files.readAllLines(filePath);
            return lines.stream().map(this::parseLog).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
