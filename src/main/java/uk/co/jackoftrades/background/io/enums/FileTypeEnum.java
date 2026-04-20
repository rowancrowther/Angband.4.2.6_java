package uk.co.jackoftrades.background.io.enums;

/**
 * Specifies what type a file is when writing. Used in fileOpen()
 */
@SuppressWarnings("SpellCheckingInspection")
public enum FileTypeEnum {
    /**
     * A text file
     */
    FTYPE_TEXT,

    /**
     * A save file
     */
    FTYPE_SAVE,

    /**
     * A raw binary file
     */
    FTYPE_RAW,

    /**
     * An HTML file
     */
    FTYPE_HTML,

    /**
     * A null type returned if the file doesn't exist
     */
    FTYPE_NULL
}