package uk.co.jackoftrades.io.parsers.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.io.parsers.enums.ParserValueUnionEnum;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

class ParserValueUnionEnumTest {
    ParserValueUnionEnum sint = ParserValueUnionEnum.PARSER_VALUE_UNION_INT;
    ParserValueUnionEnum uint = ParserValueUnionEnum.PARSER_VALUE_UNION_UNSIGNED_INT;
    ParserValueUnionEnum charv = ParserValueUnionEnum.PARSER_VALUE_UNION_CHAR;
    ParserValueUnionEnum string = ParserValueUnionEnum.PARSER_VALUE_UNION_STRING;
    ParserValueUnionEnum random = ParserValueUnionEnum.PARSER_VALUE_UNION_RANDOM;
    ParserValueUnionEnum nullValue = ParserValueUnionEnum.PARSER_VALUE_UNION_NULL;
    ArrayList<ParserValueUnionEnum> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(sint);
        allValues.add(uint);
        allValues.add(charv);
        allValues.add(string);
        allValues.add(random);
        allValues.add(nullValue);
    }

    @Test
    void values() {
        for (ParserValueUnionEnum value : ParserValueUnionEnum.values()) {
            if (!allValues.contains(value))
                fail(value.toString() + " not found in manually created list.");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(sint, ParserValueUnionEnum.valueOf("PARSER_VALUE_UNION_INT")),
                () -> assertEquals(charv, ParserValueUnionEnum.valueOf("PARSER_VALUE_UNION_CHAR")),
                () -> assertEquals(uint, ParserValueUnionEnum.valueOf("PARSER_VALUE_UNION_UNSIGNED_INT")),
                () -> assertEquals(string, ParserValueUnionEnum.valueOf("PARSER_VALUE_UNION_STRING")),
                () -> assertEquals(random, ParserValueUnionEnum.valueOf("PARSER_VALUE_UNION_RANDOM")),
                () -> assertEquals(nullValue, ParserValueUnionEnum.valueOf("PARSER_VALUE_UNION_NULL"))
        );
    }
}