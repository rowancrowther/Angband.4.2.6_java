package uk.co.jackoftrades.background.io.parsers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A concrete ParserValueUnion object of type uint (long).
 */
public class ParserValueUInt implements ParserUnionValue {
    private long value;
    Logger logger = LogManager.getLogger();

    /**
     * Set the value for this ParserValueUnion
     * @param obj the value to set this union to.
     * @throws IllegalArgumentException when value in is not of the correct type.
     */
    public void setValue(Object obj) throws IllegalArgumentException {
        if (obj instanceof Long)
        {
            value = (long) obj;
        } else {
            value = -1L;
            if (obj == null) {
                logger.error("Illegal argument. Expected unsigned int (long) but received null.");
            } else {
                logger.error("Illegal argument. Expected unsigned int (long) but received " + obj.getClass());
                throw new IllegalArgumentException("Illegal argument. Expected unsigned int (long) but received " + obj.getClass());
            }
        }
    }

    /**
     * Return the value of this instance as an object
     * @return the value of this instance as an object
     */
    public Object getValue() {
        return value;
    }
}
