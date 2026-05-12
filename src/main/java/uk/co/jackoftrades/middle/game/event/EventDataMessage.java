package uk.co.jackoftrades.middle.game.event;

public class EventDataMessage implements GameEventData {
    private int type;
    private String message;

    public EventDataMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
