package events.Validation.TenantValidator;

import events.Event;
import events.Tenant.TenantResolver;
import events.Validation.EventValidator;
import events.Validation.ValidationResult;

public class TenantValidator implements EventValidator {
    
    private final TenantResolver tenantResolver;

    public TenantValidator(TenantResolver tenantResolver){
        this.tenantResolver = tenantResolver;
    }

    @Override
    public ValidationResult validate(Event event){
        try{
            tenantResolver.resolve(event.tenantId());
            return new ValidationResult(true, null);
        } catch(Exception e) {
            return new ValidationResult(false, e.getMessage());
        }
    }

}
