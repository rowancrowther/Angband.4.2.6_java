package uk.co.jackoftrades.background.io.parsers;

/**
 * Abstract class to hold a value for a C-union.
 */
public abstract class ParserValueUnion {
    /**
     * Set the value for this ParserValueUnion
     *
     * @param value the value to set this union to. Note, this can be one of class
     *              char, int, uint, string, random or null.
     * @throws IllegalArgumentException when value in is not of the correct type.
     */
    public abstract void setValue(Object value) throws IllegalArgumentException;

    /**
     * Gets the value of this Union instance
     * @return the value of this Union instance as an object
     */
    public abstract Object getValue();
}