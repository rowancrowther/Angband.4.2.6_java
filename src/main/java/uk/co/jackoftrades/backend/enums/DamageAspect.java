package uk.co.jackoftrades.backend.enums;

public enum DamageAspect {
    /**
     * Minimize the roll rather than rolling
     */
    MINIMIZE,

    /**
     * Take the average roll rather than rolling
     */
    AVERAGE,

    /**
     * Maximize the roll rather than rolling
     */
    MAXIMIZE,

    /**
     * Similar to MAXIMIZE
     */
    EXTREMIFY,

    /**
     * Roll a random number
     */
    RANDOMIZE
}
