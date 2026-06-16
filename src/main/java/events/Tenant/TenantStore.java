package events.Tenant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantStore {

    private final Map<String, Tenant> tenants = new ConcurrentHashMap<>();

    public TenantStore(){
        tenants.put("tenant-1", new Tenant("tenant-1", "Zomato"));
        tenants.put("tenant-2", new Tenant("tenant-2", "Blinkit"));
        tenants.put("tenant-3", new Tenant("tenant-3", "Siggy"));
        tenants.put("tenant-4", new Tenant("tenant-4", "Myntra"));
    }    

    public Tenant getTenant(String tenantId){
        return tenants.get(tenantId);
    }

    public boolean exists(String tenantId){
        return tenants.containsKey(tenantId);
    }

}
