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
                    sum(count)
                FROM event_type_counts
                GROUP BY eventType
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
                    sum(count) AS total
                FROM event_type_counts
                GROUP BY eventType
                """;

        Map<String, Long> counts = new LinkedHashMap<>();
        
        long totalEvents = 0;
        
        try (
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while(rs.next()) {
                long count = rs.getLong("total");
                counts.put(
                        rs.getString("eventType"), count
                );
                totalEvents += count;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Double> percentages = new LinkedHashMap<>();

        for(Map.Entry<String, Long> entry : counts.entrySet()){
            double percentage = totalEvents == 0 ? 0 : (entry.getValue() * 100.0) / totalEvents;

            percentages.put(entry.getKey(), Math.round(percentage * 100.0) / 100.0);
        }
        return percentages;
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

    @Override
    public Map<String, Double> getRevenueByDate(){

        String sql = 
                """
                SELECT
                    eventDate,
                    revenue
                FROM revenue_stats
                ORDER BY eventDate
                """;

        Map<String, Double> result = new LinkedHashMap<>();

        try(
            PreparedStatement statement = connection.prepareStatement(sql); 
            ResultSet rs = statement.executeQuery()
        ){
            while(rs.next()){
                result.put(rs.getString("eventDate"), rs.getDouble("revenue"));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Long> getTenantDistribution(){

        String sql = 
                """
                SELECT
                    tenantId,
                    sum(count) AS total
                FROM tenant_event_counts
                GROUP BY tenantId
                ORDER BY total DESC
                """;

        Map<String, Long> result = new LinkedHashMap<>();

        try(
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery()
        ) {
            while(rs.next()){
                result.put(rs.getString("tenantId"), rs.getLong("total"));
            }
        } catch(Exception e){
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
