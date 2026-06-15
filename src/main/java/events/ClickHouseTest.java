package events;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class ClickHouseTest {

    public static void main(String[] args) throws Exception {

        String url = "jdbc:clickhouse://localhost:8123/analytics";

        Properties props = new Properties();
        props.setProperty("user", "event_user");
        props.setProperty("password", "event123");

        Connection conn = DriverManager.getConnection(url, props);

        Statement stmt = conn.createStatement();

        String sql = """
            CREATE TABLE IF NOT EXISTS events (
                eventId UInt64,
                userId String,
                eventType String,
                timestamp DateTime,
                partitionId UInt8
            ) ENGINE = MergeTree()
            ORDER BY (timestamp, eventId)
        """;

        stmt.execute(sql);

        System.out.println("Table created successfully!");

        conn.close();
    }
}