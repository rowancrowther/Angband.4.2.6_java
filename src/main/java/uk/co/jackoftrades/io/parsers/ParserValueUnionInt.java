package uk.co.jackoftrades.io.parsers;

/**
 * This is a stem class atm
 */
public class ParserValueUnionInt extends ParserValueUnion{
    private int pint;

    /**
     * Gets the signed int value of this ParserValueUnion
     * @return the value as a signed int
     */
    @Override
    public Object getValue() {
        return pint;
    }
}
