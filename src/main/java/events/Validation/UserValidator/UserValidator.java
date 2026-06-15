package events.Validation.UserValidator;

import events.Event;
import events.Validation.EventValidator;
import events.Validation.ValidationResult;

public class UserValidator implements EventValidator {

    @Override
    public ValidationResult validate(Event event) {

        if (event.userId() == null) {
            return new ValidationResult(false, "UserId is null");
        }

        if (event.userId().isBlank()) {
            return new ValidationResult(false, "UserId is empty");
        }

        return new ValidationResult(true, null);
    }
}