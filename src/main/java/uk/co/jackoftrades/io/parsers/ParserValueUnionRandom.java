package uk.co.jackoftrades.io.parsers;

import uk.co.jackoftrades.background.Random;

/**
 * This is a stem class atm.
 */
public class ParserValueUnionRandom extends ParserValueUnion {
    private Random random;

    /**
     * Return the Random value of this ParserValueUnion
     * @return the Random value of this ParserValueUnion
     */
    @Override
    public Object getValue() {
        return random;
    }
}
