package events;

public class EventIdGenerator {
    private static long sequence;

    static {
        sequence = 1;
        System.out.println("Event ID Generator initialized");
    }
    public static long generateId() {
        return sequence++;
    }
}
