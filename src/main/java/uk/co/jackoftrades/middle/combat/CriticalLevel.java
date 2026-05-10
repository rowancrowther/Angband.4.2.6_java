package uk.co.jackoftrades.middle.combat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.enums.MessageEnum;

/**
 * This is a STEM class - TODO: Extend it
 */

public class CriticalLevel {
    private int cutOff;
    private int mult;
    private int add;
    private MessageEnum msgt;
    private final Logger logger = LogManager.getLogger();

    public CriticalLevel(int cutOff, int mult, int add, MessageEnum msgt) {
        this.cutOff = cutOff;
        this.mult = mult;
        this.add = add;
        this.msgt = msgt;
    }

    public int getCutOff() {
        return cutOff;
    }

    public int getMult() {
        return mult;
    }

    public int getAdd() {
        return add;
    }

    public MessageEnum getMsgt() {
        return msgt;
    }
}
