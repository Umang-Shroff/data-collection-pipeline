package events.Validation;

public record ValidationResult(
    boolean valid, 
    String reason
){}
