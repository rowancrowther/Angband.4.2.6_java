package uk.co.jackoftrades.middle.game.event;

import org.jetbrains.annotations.Contract;

public class EventDataBoolean implements GameEventData {
    public boolean value;

    public EventDataBoolean(boolean value) {
        this.value = value;
    }

    public EventDataBoolean() {
        this.value = false;
    }

    @Contract(pure = true)
    public boolean getValue() {
        return value;
    }
}
