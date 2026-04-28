package uk.co.jackoftrades.background.io.parsers;

/**
 * A concrete ParserValueUnion object of type String.
 */
public class ParserValueString implements ParserUnionValue {
    private String value;

    /**
     * Set the value for this ParserValueUnion
     *
     * @param value the value to set this union to. Note, this can be one of class
     *              char, int, uint, string, random or null.
     * @throws IllegalArgumentException when value in is not of the correct type.
     */
    public void setValue(Object value) throws IllegalArgumentException {
        if (value instanceof String)
            this.value = (String) value;
        else if (this.value == null ) {
            throw new IllegalArgumentException("Illegal argument. Expected String but received null");
        } else {
            this.value = null;
            throw new IllegalArgumentException("Illegal argument. Expected String but received " + value.getClass());
        }
    }

    /**
     * Get the String value of this ParserValueString
     * @return the value of this instance
     */
    public String getValue()
    {
        return value;
    }
}
