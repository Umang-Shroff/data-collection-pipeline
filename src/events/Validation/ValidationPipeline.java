package events.Validation;

import events.Event;

import java.util.List;
import java.util.ArrayList;

public class ValidationPipeline implements EventValidator {
    
    private final List<EventValidator> validators;

    public ValidationPipeline() {
        validators = new ArrayList<>();
    }

    public void addValidator(EventValidator validator) {
        validators.add(validator);
    }

    @Override
    public ValidationResult validate(Event event) {

        for(EventValidator validator : validators) {
            ValidationResult result = validator.validate(event);

            if(!result.valid()) {
                return result;
            }
        }

        return new ValidationResult(true, null);
    }
}
