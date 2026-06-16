package events.Validation.PayloadValidator;

import events.Event;
import events.Validation.EventValidator;
import events.Validation.ValidationResult;

public class PayloadValidator implements EventValidator {

    @Override
    public ValidationResult validate(Event event){
        if(event.payload() == null){
            return new ValidationResult(false, "Payload missing");
        }
        return new ValidationResult(true, null);
    }
}
