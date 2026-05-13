package uk.co.jackoftrades.frontend.events;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.events.enums.UiEventType;

public interface Event {
    Flag<UiEventType> uiEventType = new Flag<>(UiEventType.class);
}
