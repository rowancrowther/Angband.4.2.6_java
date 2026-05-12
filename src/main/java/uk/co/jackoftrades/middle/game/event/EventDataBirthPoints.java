package uk.co.jackoftrades.middle.game.event;

import uk.co.jackoftrades.middle.enums.Stats;

import java.util.HashMap;

public class EventDataBirthPoints implements GameEventData {
    private HashMap<Stats, Integer> points;
    private HashMap<Stats, Integer> incPoints;
    private int remaining;

    public EventDataBirthPoints(HashMap<Stats, Integer> points, HashMap<Stats, Integer> incPoints, int remaining) {
        this.points = points;
        this.incPoints = incPoints;
        this.remaining = remaining;
    }

    public HashMap<Stats, Integer> getPoints() {
        return points;
    }

    public HashMap<Stats, Integer> getIncPoints() {
        return incPoints;
    }

    public int getRemaining() {
        return remaining;
    }
}
