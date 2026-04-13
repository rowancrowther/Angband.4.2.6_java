package uk.co.jackoftrades.io.parsers;

// This is a stem class atm
public class ParserValueUnionString extends ParserValueUnion {
    private String string;

    /**
     * Gets the string value of this ParserValueUnion
     * @return the String value of this ParserValueUnion
     */
    @Override
    public Object getValue() {
        return string;
    }
}
