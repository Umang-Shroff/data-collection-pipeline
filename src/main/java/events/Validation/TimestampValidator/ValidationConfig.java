package events.Validation.TimestampValidator;

public class ValidationConfig {
    // allowing events up to 5 mins in the future for any network delays
    public static final long MAX_FUTURE_TIME_MS = 5 * 60 * 1000;
}
