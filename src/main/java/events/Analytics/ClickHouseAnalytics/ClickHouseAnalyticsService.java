package events.Analytics.ClickHouseAnalytics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class ClickHouseAnalyticsService implements AnalyticsService {
    
    private static final String URL = "jdbc:clickhouse://localhost:8123/analytics";

    private static final String USER = "event_user";

    private static final String PASSWORD = "event123";

    private Connection connection;

    public ClickHouseAnalyticsService() {
        try{
            Properties props = new Properties();

            props.setProperty("user",USER);
            props.setProperty("password",PASSWORD);

            connection = DriverManager.getConnection(URL,props);

            System.out.println("[ANALYTICS] Connected to Clickhouse");
        } catch (Exception e) {
            throw new RuntimeException("Analytics connection Failed");
        }
    }

    @Override
    public long getTotalEvents() {
        String sql = 
                """
                SELECT count(*)
                FROM events
                """;

        try(
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()
        ){
            if(rs.next()){
                return rs.getLong(1);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Map<String, Long> getEventCountPerType(){
        String sql =
                """
                SELECT
                    eventType,
                    count(*)
                FROM events
                GROUP BY eventType
                ORDER BY count(*) DESC
                """;

        Map<String, Long> result = new LinkedHashMap<>();
        try(
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()
        ){
            while(rs.next()){
                result.put(rs.getString("eventType"), rs.getLong(2));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Double> getEventTypePercentages() {

        String sql =
                """
                SELECT
                    eventType,
                    round(
                        count() * 100.0 /
                        (
                            SELECT count()
                            FROM events
                        ),
                        2
                    ) AS percentage
                FROM events
                GROUP BY eventType
                ORDER BY percentage DESC
                """;

        Map<String, Double> result =
                new LinkedHashMap<>();

        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while(rs.next()) {
                result.put(
                        rs.getString("eventType"),
                        rs.getDouble("percentage")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Long> getTopUsers(int limit) {

        String sql =
                """
                SELECT
                    userId,
                    count(*) AS total
                FROM events
                GROUP BY userId
                ORDER BY total DESC
                LIMIT ?
                """;

        Map<String, Long> result =
                new LinkedHashMap<>();

        try ( 
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    limit
            );

            try(
                ResultSet rs = statement.executeQuery()
            ) {

                while(rs.next()) {
                    result.put(
                            rs.getString("userId"),
                            rs.getLong("total")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Long> getEventsPerHour() {

        String sql =
                """
                SELECT
                    toString(
                        toStartOfHour(
                            eventTimestamp
                        )
                    ) AS hour,
                    count(*) AS total
                FROM events
                GROUP BY hour
                ORDER BY hour
                """;

        Map<String, Long> result =
                new LinkedHashMap<>();

        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while(rs.next()) {
                result.put(
                        rs.getString("hour"),
                        rs.getLong("total")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<Integer, Long> getPartitionDistribution() {

        String sql =
                """
                SELECT
                    partitionId,
                    count(*) AS total
                FROM events
                GROUP BY partitionId
                ORDER BY partitionId
                """;

        Map<Integer, Long> result =
                new LinkedHashMap<>();

        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while(rs.next()) {
                result.put(
                        rs.getInt("partitionId"),
                        rs.getLong("total")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void close() {

        try {

            if(connection != null) {
                connection.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
