package uk.co.jackoftrades.background.io.parsers;

/**
 * A concrete ParserValueUnion object of type signed int (Integer).
 */
public class ParserValueInt implements ParserUnionValue {
    private int value;
    /**
     * Set the value for this ParserValueUnion
     *
     * @param obj the value to set this union to. Note, this can be one of class
     *            char, int, uint, string, random or null.
     * @throws IllegalArgumentException when value in is not of the correct type.
     */
    public void setValue(Object obj) throws IllegalArgumentException {
        if (obj instanceof Integer)
            this.value = (int) obj;
        else if (null == obj) {
            throw new IllegalArgumentException("Illegal argument. Expected signed int (int) but received null");
        }
        else {
            this.value = -1;
            throw new IllegalArgumentException("Illegal argument. Expected signed int (int) but received " + obj.getClass());
        }
    }

    /**
     * Get the integer value of this class
     * @return the value of this class
     */
    public Object getValue() {
        return value;
    }
}
