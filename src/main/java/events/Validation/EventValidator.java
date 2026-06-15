package events.Validation;

import events.Event;

public interface EventValidator {
    ValidationResult validate(Event event);
}
