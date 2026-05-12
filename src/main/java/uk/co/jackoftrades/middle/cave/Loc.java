package uk.co.jackoftrades.middle.cave;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.RandomValueUtils;

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
     * Getter for y
     *
     * @return the current value of y
     */
    public int getY() {
        return y;
    }

    /**
     * Getter for x
     *
     * @return the current value of x
     */
    public int getX() {
        return x;
    }

    /**
     * Returns true if the incoming location is equivalent to this one, false otherwise
     *
     * @param other the incoming location to compare with this one
     * @return true if these two locations are equivalent, false otherwise
     */
    public boolean equals(@NotNull Loc other) {
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
    public Loc sum(@NotNull Loc other) {
        return new Loc(x + other.x, y + other.y);
    }

    /**
     * Create a location which is the difference between the two points this (x1, y1) and other (x2, y2)
     *
     * @param other the other point to work out the difference from this point
     * @return A new location (x1 - x2, y1 - y2).
     */
    public Loc diff(@NotNull Loc other) {
        return new Loc(this.x - other.x, this.y - other.y);
    }

    /**
     * Create a random location with the given spread variables on x and y
     *
     * @param xSpread The x spread value - new value should be between this.x - xSpread and this.x + xSpread
     * @param ySpread The y spread value - new value should be between this.y - ySpread and this.y + ySpread
     * @return A random location where new.x is between this.x - xSpread and this.x + xSpread
     * and new.y is between this.y - ySpread and this.y + ySpread
     */
    public Loc randLoc(int xSpread, int ySpread) {
        return new Loc(RandomValueUtils.randSpread(x, xSpread), RandomValueUtils.randSpread(y, ySpread));
    }

    /**
     * Returns a new location offset from this location by dx and dy
     *
     * @param dx The amount that the x coordinate is offset
     * @param dy The amount that the y coordinate is offset
     * @return A new location of the form (x + dx, y + dy)
     */
    public Loc locOffset(int dx, int dy) {
        return new Loc(x + dx, y + dy);
    }
}