package uk.co.jackoftrades.middle.game.event;

public class EventDataBolt implements GameEventData {
    private int projType;        // Probably going to be replaced by Enum
    private boolean drawing;
    private boolean seen;
    private boolean beam;
    private int oy;
    private int ox;
    private int y;
    private int x;

    public EventDataBolt(int projType, boolean drawing, boolean seen, boolean beam, int oy, int ox, int y, int x) {
        this.projType = projType;
        this.drawing = drawing;
        this.seen = seen;
        this.beam = beam;
        this.oy = oy;
        this.ox = ox;
        this.y = y;
        this.x = x;
    }

    public int getProjType() {
        return projType;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public boolean isSeen() {
        return seen;
    }

    public boolean isBeam() {
        return beam;
    }

    public int getOy() {
        return oy;
    }

    public int getOx() {
        return ox;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
