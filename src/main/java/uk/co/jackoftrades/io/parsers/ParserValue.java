package uk.co.jackoftrades.io.parsers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.background.Random;

import java.util.ArrayList;

/**
 * Class to hold ParserValues, not fully implemented yet.
 */
public class ParserValue {
    private ArrayList<ParserSpec> parserSpecs;
    private ParserUnionValue value;
    private final Logger logger = LogManager.getLogger();

    public Object getValue() {
        if (null == value) {
            return null;
        }

        return value.getValue();
    }

    public void setValue(Object value)
    {
        if (value == null) {
            this.value = null;
            return;
        }

        if (value instanceof Character && this.value instanceof ParserValueChar) {
            this.value.setValue(value);
            return;
        }

        if (value instanceof Integer && this.value instanceof ParserValueInt) {
            this.value.setValue(value);
            return;
        }

        if (value instanceof Random && this.value instanceof ParserValueRandom) {
            this.value.setValue(value);
            return;
        }

        if (value instanceof String && this.value instanceof ParserValueString) {
            this.value.setValue(value);
            return;
        }

        if (value instanceof Long && this.value instanceof ParserValueUInt) {
            this.value.setValue(value);
            return;
        }

        if (this.value == null || this.value.getValue() == null) {
            logger.error("Illegal argument. Expected null but received " + value.getClass());
        } else {
            logger.error("Illegal argument. Expected " + this.value.getValue().getClass() + " but received " + value.getClass());
        }
        throw new IllegalArgumentException("Illegal argument. Expected " + this.value.getValue().getClass() + " but received " + value.getClass());
    }

    private ParserValue() {
        parserSpecs = new ArrayList<>();
        value = null;
    }

    public ParserValue(char c) {
        this();
        value = new ParserValueChar();
        value.setValue(c);
    }

    public ParserValue(int i) {
        this();
        value = new ParserValueInt();
        value.setValue(i);
    }

    public ParserValue(Random r) {
        this();
        value = new ParserValueRandom();
        value.setValue(r);
    }

    public ParserValue(String s) {
        this();
        value = new ParserValueString();
        value.setValue(s);
    }

    public ParserValue(Long l) {
        this();
        value = new ParserValueUInt();
        value.setValue(l);
    }
}