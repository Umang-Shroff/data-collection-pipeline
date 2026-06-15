package events.Validation.TimestampValidator;

import events.Event;
import events.Validation.EventValidator;
import events.Validation.ValidationResult;

public class TimestampValidator implements EventValidator {
    
    @Override
    public ValidationResult validate(Event event) {
        long timestamp = event.timestamp();

        if(timestamp <= 0){
            return new ValidationResult(false, "Timestamp must be positive");
        }

        long currentTime = System.currentTimeMillis();
        if(timestamp > currentTime + ValidationConfig.MAX_FUTURE_TIME_MS){
            return new ValidationResult(false, "Timestamp too far in future");
        }

        return new ValidationResult(true, null);
    }

}
