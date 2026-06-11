package events.Routing;

import events.Event;

public interface EventRouter {
    String route(Event event);
}
