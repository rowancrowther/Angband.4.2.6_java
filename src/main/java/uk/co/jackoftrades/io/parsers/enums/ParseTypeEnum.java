package uk.co.jackoftrades.io.parsers.enums;

/**
 * An enum to store a parser type flag
 */
public enum ParseTypeEnum {
    /**
     * No parser
     */
    PARSE_T_NONE,

    /**
     * Parsing a signed int - stored in Java as an int
     */
    PARSE_T_INT,

    /**m
     * Parsing a symbol
     */
    PARSE_T_SYM,

    /**
     * Parsing a string
     */
    PARSE_T_STR,

    /**
     * Parsing a random in the format u + v * w 'd' x
     */
    PARSE_T_RAND,

    /**
     * Parsing an unsigned int - stored in Java as an int
     */
    PARSE_T_UINT,

    /**
     * Parsing a character
     */
    PARSE_T_CHAR,

    /**
     * Parsing an optional flag
     */
    PARSE_T_OPT
}
