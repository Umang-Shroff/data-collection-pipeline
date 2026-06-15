package events.Storage;

import events.Event;
import events.EventRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ClickHouseEventStore implements EventRepository {

    private static final String URL =
            "jdbc:clickhouse://localhost:8123/analytics";

    private static final String USER =
            "event_user";

    private static final String PASSWORD =
            "event123";

    private Connection connection;

    public ClickHouseEventStore() {

        try {

            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);

            connection =
                    DriverManager.getConnection(
                            URL,
                            props
                    );

            System.out.println(
                    "[CLICKHOUSE] Connected successfully."
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to connect to ClickHouse",
                    e
            );
        }
    }

    @Override
    public synchronized void save(Event event) {

        saveBatch(List.of(event));
    }

    @Override
    public synchronized void saveBatch(
            List<Event> events) {

        if(events == null || events.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO events
                (
                    eventId,
                    userId,
                    eventType,
                    eventTimestamp,
                    partitionId
                )
                VALUES
                (
                    ?, ?, ?, ?, ?
                )
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {

            for(Event event : events) {

                statement.setLong(
                        1,
                        event.eventId()
                );

                statement.setString(
                        2,
                        event.userId()
                );

                statement.setString(
                        3,
                        event.eventType().name()
                );

                statement.setTimestamp(
                        4,
                        new Timestamp(
                                event.timestamp()
                        )
                );

                statement.setInt(
                        5,
                        event.partitionId()
                );

                statement.addBatch();
            }

            statement.executeBatch();

            System.out.println(
                    "[CLICKHOUSE] Inserted "
                            + events.size()
                            + " events"
            );

        } catch(Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public List<Event> getAllEvents() {

        return new ArrayList<>();
    }

    public void close() {

        try {

            if(connection != null) {
                connection.close();
            }

        } catch(Exception e) {

            e.printStackTrace();
        }
    }
}