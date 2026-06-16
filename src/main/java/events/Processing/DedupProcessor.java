// package events.Processing;

// import events.Event;
// import events.EventType;

// import java.io.BufferedReader;
// import java.io.BufferedWriter;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.StandardOpenOption;
// import java.util.HashSet;
// import java.util.Set;

// public class DedupProcessor {

//     private final Path sourceFile;
//     private final Path outputFile;

//     public DedupProcessor(Path sourceFile, Path outputFile) {
            
//         this.sourceFile = sourceFile;
//         this.outputFile = outputFile;
            
//         try {

//             Path parent = outputFile.getParent();
    
//             if(parent != null) {
//                 Files.createDirectories(parent);
//             }

//             if(!Files.exists(outputFile)) {
//                 Files.createFile(outputFile);
//             }
//         } catch(IOException e) {
//             throw new RuntimeException("Failed to initialize output file",e);
//         }
//     }

//     public void process() throws IOException {

//             Set<Long> seenEventIds =
//                     new HashSet<>();
        
//             int totalEvents = 0;
//             int duplicates = 0;
        
//             try (
//                     BufferedReader reader =
//                             Files.newBufferedReader(sourceFile);
        
//                     BufferedWriter writer =
//                             Files.newBufferedWriter(
//                                     outputFile,
//                                     StandardOpenOption.CREATE,
//                                     StandardOpenOption.TRUNCATE_EXISTING
//                             )
//             ) {
        
//                 String line;
        
//                 while((line = reader.readLine()) != null) {
                
//                     totalEvents++;
                
//                     Event event =
//                             parseEvent(line);
                
//                     if(seenEventIds.contains(
//                             event.eventId())) {
                        
//                         duplicates++;
                        
//                         continue;
//                     }
            
//                     seenEventIds.add(
//                             event.eventId());
                
//                     writer.write(line);
                
//                     writer.newLine();
//                 }
//             }
    
//             System.out.println();
//             System.out.println("====== DEDUP REPORT ======");
    
//             System.out.println(
//                     "Total Events      : "
//                             + totalEvents);
        
//             System.out.println(
//                     "Unique Events     : "
//                             + seenEventIds.size());
        
//             System.out.println(
//                     "Duplicate Events  : "
//                             + duplicates);
        
//             System.out.println("==========================");
//         }

//     private Event parseEvent(
//             String line) {

//         String[] parts =
//                 line.split("\\|");

//         return new Event(
//                 Long.parseLong(parts[0]),
//                 parts[1],
//                 EventType.valueOf(parts[2]),
//                 Long.parseLong(parts[3]),
//                 Integer.parseInt(parts[4])
//         );
//     }
// }