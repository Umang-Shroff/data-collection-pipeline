package events;

import events.Routing.EventCategory;

public enum EventType {
    USER_LOGIN(EventCategory.USER),
    APP_OPEN(EventCategory.ANALYTICS),
    PURCHASE(EventCategory.COMMERCE),
    CAMPAIGN_CLICK(EventCategory.ENGAGEMENT);

    private final EventCategory category;

    EventType(EventCategory category){
        this.category = category;
    }

    public EventCategory getCategory(){
        return category;
    }
}
