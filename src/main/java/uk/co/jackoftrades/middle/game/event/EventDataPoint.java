package uk.co.jackoftrades.middle.game.event;

import uk.co.jackoftrades.middle.cave.Loc;

public class EventDataPoint implements GameEventData {
    private Loc loc;

    public EventDataPoint(Loc loc) {
        this.loc = loc;
    }

    public EventDataPoint(int x, int y) {
        loc = new Loc(x, y);
    }

    public Loc getLoc() {
        return loc;
    }
}
