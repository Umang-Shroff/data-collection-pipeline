package events.Validation.EventTypeValidator;

import events.Event;
import events.Validation.EventValidator;
import events.Validation.ValidationResult;

public class EventTypeValidator implements EventValidator {
    
    @Override
    public ValidationResult validate(Event event) {
        
        if(event.eventType() == null){
            return new ValidationResult(false, "EventType is null");
        }

        return new ValidationResult(true, null);
    }
}
