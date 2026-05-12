package uk.co.jackoftrades.middle.game.event;

import uk.co.jackoftrades.middle.objects.ItemObject;

public class EventDataMissile implements GameEventData {
    private ItemObject itemObject;
    private boolean seen;
    private int y;
    private int x;

    public EventDataMissile(ItemObject itemObject, boolean seen, int y, int x) {
        this.itemObject = itemObject;
        this.seen = seen;
        this.y = y;
        this.x = x;
    }

    public ItemObject getItemObject() {
        return itemObject;
    }

    public boolean isSeen() {
        return seen;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
