package uk.co.jackoftrades.io.parsers;


import uk.co.jackoftrades.background.Random;

/**
 * A concrete ParserValueUnion object of type Random.
 */
public class ParserValueRandom extends ParserValueUnion {
    private Random value;

    /**
     * Set the value for this ParserValueUnion
     *
     * @param value the value to set this union to.
     * @throws IllegalArgumentException when value in is not of the correct type.
     */
    @Override
    public void setValue(Object value) throws IllegalArgumentException {
        if (value instanceof Random)
            this.value = (Random) value;
        else if (value == null) {
            throw new IllegalArgumentException("Illegal argument. Expected Random but received null");
        } else {
            this.value = null;
            throw new IllegalArgumentException("Illegal argument. Expected Random but received " + value.getClass());
        }
    }

    /**
     * Gets the value of this Union instance
     *
     * @return the value of this Union instance as an object
     */
    @Override
    public Object getValue() {
        return value;
    }
}