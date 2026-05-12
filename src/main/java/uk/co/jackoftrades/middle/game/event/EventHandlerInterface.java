package uk.co.jackoftrades.middle.game.event;

import uk.co.jackoftrades.middle.game.enums.GameEventType;

public interface EventHandlerInterface {
    public void dispatch(GameEventType eventType, GameEventData data, EventUser user);
}
