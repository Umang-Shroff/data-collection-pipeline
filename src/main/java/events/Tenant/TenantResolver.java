package events.Tenant;

public class TenantResolver {
    
    private final TenantStore tenantStore;

    public TenantResolver(TenantStore tenantStore){
        this.tenantStore = tenantStore;
    }

    public Tenant resolve(String tenantId){
        if(tenantId == null || tenantId.isBlank()){
            throw new RuntimeException("TenantId is missing");
        }

        Tenant tenant = tenantStore.getTenant(tenantId);

        if(tenant == null){
            throw new RuntimeException("Unknown Tenant : " + tenantId);
        }
        return tenant;
    }

}
