package events.Routing;

import events.Event;

public class CategoryRouter implements EventRouter {
    
    @Override
    public String route(Event event){
        return event
                .eventType()
                .getCategory()
                .name()
                .toLowerCase()
                + "-topic";
    }
}
