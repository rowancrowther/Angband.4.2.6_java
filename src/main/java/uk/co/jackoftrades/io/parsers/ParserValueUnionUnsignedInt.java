package uk.co.jackoftrades.io.parsers;

/**
 * This is a stem class atm
 */
public class ParserValueUnionUnsignedInt extends ParserValueUnion {
    private long uint; // signed int is a long to distinguish it from an unsigned int (int)

    /**
     * Get the unsigned value of this ParserValueUnion
     * @return
     */
    @Override
    public Object getValue() {
        return uint;
    }
}
