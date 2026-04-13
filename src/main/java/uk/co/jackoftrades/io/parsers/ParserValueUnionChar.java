package uk.co.jackoftrades.io.parsers;

/**
 * This is currently a stem of a class
 */
public class ParserValueUnionChar extends ParserValueUnion {
    private char wide;

    /**
     * Get the value of the wide character in this ParserValueUnion
     * @return The wide character stored in this ParserValueUnion
     */
    @Override
    public Object getValue() {
        return wide;
    }
}
