package events.Validation;

import events.Event;

public class BasicEventValidator implements EventValidator {
    @Override
    public ValidationResult validate(Event event){
        if(event.userId() == null){
            return new ValidationResult(false, "UserId is null");
        }

        if(event.userId().isBlank()){
            return new ValidationResult(false, "UserId is empty");
        }

        if(event.eventType() == null){
            return new ValidationResult(false, "EventType is null");
        }

        return new ValidationResult(true, null);
    }
}
