package uk.co.jackoftrades.middle.game.event;

public class EventDataString implements GameEventData {
    private String string;

    public EventDataString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
