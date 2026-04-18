package uk.co.jackoftrades.middle.cave;

import org.jspecify.annotations.NonNull;

public class Loc {
    private int x;
    private int y;

    /**
     * Quick pointer to a location which is the origin (0, 0)
     */
    public static Loc zero = new Loc(0, 0);

    /**
     * Constructor
     *
     * @param x the x coordinate of this location
     * @param y the y coordinate of this location
     */
    public Loc(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns true if the incoming location is equivalent to this one, false otherwise
     *
     * @param other the incoming location to compare with this one
     * @return true if these two locations are equivalent, false otherwise
     */
    public boolean equals(@NonNull Loc other) {
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Determines if this location is the origin location (0, 0)
     *
     * @return true if this is equivalent to the origin location, false otherwise
     */
    public boolean isZero() {
        return equals(zero);
    }

    /**
     * Adds two locations together and return the result
     *
     * @param other The location to sum with this one
     * @return A location which consists of (x1 + x2, y1 + y2)
     */
    public Loc sum(@NonNull Loc other) {
        return new Loc(x + other.x, y + other.y);
    }

    /**
     * Create a location which is the difference between the two points this (x1, y1) and other (x2, y2)
     *
     * @param other the other point to work out the difference from this point
     * @return A new location (x1 - x2, y1 - y2).
     */
    public Loc diff(@NonNull Loc other) {
        return new Loc(this.x - other.x, this.y - other.y);
    }


}