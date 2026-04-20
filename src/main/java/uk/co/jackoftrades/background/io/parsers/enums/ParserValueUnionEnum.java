package uk.co.jackoftrades.background.io.parsers.enums;

/**
 * THe various types that a Parser Value Union can take
 */
public enum ParserValueUnionEnum {
    /**
     * A wide character
     */
    PARSER_VALUE_UNION_CHAR,

    /**
     * A signed integer
     */
    PARSER_VALUE_UNION_INT,

    /**
     * A Random value
     */
    PARSER_VALUE_UNION_RANDOM,

    /**
     * A String
     */
    PARSER_VALUE_UNION_STRING,

    /**
     * An unsigned integer
     */
    PARSER_VALUE_UNION_UNSIGNED_INT,

    /**
     * A value which hasn't been set or is an error
     */
    PARSER_VALUE_UNION_NULL
}
