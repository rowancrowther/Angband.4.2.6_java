package uk.co.jackoftrades.io.parsers;

/**
 * Abstract class to extend the ParserValueUnion* classes
 */
public abstract class ParserValueUnion {
    private Object value;

    /**
     * Abstract class to return the object value
     * @return The value of this ParserValueUnion
     */
    public abstract Object getValue();
}
