package uk.co.jackoftrades.io.parsers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A concrete ParserValueUnion object of type char (Character).
 */
public class ParserValueChar extends ParserValueUnion {
    private char value;
    private Logger logger = LogManager.getLogger();

    /**
     * Set the character value for this ParserValueUnion
     *
     * @param obj the value to set this union to.
     * @throws IllegalArgumentException when value in is not of the correct type.
     */
    @Override
    public void setValue(Object obj) throws IllegalArgumentException {
        if (obj instanceof Character)
            this.value = (char) obj;
        else if (null == obj) {
            throw new IllegalArgumentException("Illegal argument. Expected char but received null");
        } else {
            this.value = '\0';
            throw new IllegalArgumentException("Illegal argument. Expected char but received " + obj.getClass());
        }
    }

    /**
     * Gets the value of this ParserValueUnion item
     * @return the char that is this ParserValueUnion item
     */
    @Override
    public Object getValue() {
        return value;
    }
}