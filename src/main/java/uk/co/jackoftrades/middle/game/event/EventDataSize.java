package uk.co.jackoftrades.middle.game.event;

public class EventDataSize implements GameEventData {
    private int width;
    private int height;

    public EventDataSize(int height, int width) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
