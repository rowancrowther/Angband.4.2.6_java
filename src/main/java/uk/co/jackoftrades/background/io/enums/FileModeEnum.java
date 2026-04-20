package uk.co.jackoftrades.background.io.enums;

/**
 * The type of access required for a file as used in fileOpen()
 */
public enum FileModeEnum {
    /**
     * Write access required
     */
    MODE_WRITE,

    /**
     * Read access required
     */
    MODE_READ,

    /**
     * Append access required
     */
    MODE_APPEND
}
