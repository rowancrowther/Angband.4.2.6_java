package uk.co.jackoftrades.background.objects.enums;

public class Grouper {
    private final TValue tValue;
    private final String name;

    /**
     * Constructor
     *
     * @param tValue the TValue of this grouper
     * @param name   the string associated with this grouper
     */
    public Grouper(TValue tValue, String name) {
        this.name = name;
        this.tValue = tValue;
    }

    /**
     * Returns the string name associated with this grouper
     *
     * @return the string name associated with this grouper
     */
    public String getName() {
        return name;
    }

    /**
     * Get the TValue of this Grouper
     *
     * @return the TValue of this grouper
     */
    public TValue gettValue() {
        return tValue;
    }
}
