package uk.co.jackoftrades.middle.cave;

import java.util.ArrayList;

public class PointSet {
    private final ArrayList<Loc> points;

    /**
     * Constructor
     */
    public PointSet() {
        points = new ArrayList<>();
    }

    /**
     * Add a particular location to the ArrayList of locations
     *
     * @param location the location to add
     */
    public void add(Loc location) {
        points.add(location);
    }

    /**
     * Returns true if and only if the points ArrayList contains one element p such that p.equals(location)
     *
     * @param location the location we are looking for in the ArrayList
     * @return true if the ArrayList contains the location, false otherwise
     */
    public boolean contains(Loc location) {
        return points.contains(location);
    }

    /**
     * Gets the current size of this point set
     *
     * @return The current size of this point set
     */
    public int size() {
        return points.size();
    }
}
