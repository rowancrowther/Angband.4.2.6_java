package uk.co.jackoftrades.io.parsers.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.io.parsers.enums.ParseTypeEnum;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

class ParseTypeEnumTest {
    private final ParseTypeEnum none = ParseTypeEnum.PARSE_T_NONE;
    private final ParseTypeEnum pint = ParseTypeEnum.PARSE_T_INT;
    private final ParseTypeEnum sym  = ParseTypeEnum.PARSE_T_SYM;
    private final ParseTypeEnum str  = ParseTypeEnum.PARSE_T_STR;
    private final ParseTypeEnum rand = ParseTypeEnum.PARSE_T_RAND;
    private final ParseTypeEnum uint = ParseTypeEnum.PARSE_T_UINT;
    private final ParseTypeEnum pchr = ParseTypeEnum.PARSE_T_CHAR;
    private final ParseTypeEnum opt  = ParseTypeEnum.PARSE_T_OPT;
    private ArrayList<ParseTypeEnum> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(none);
        allValues.add(pint);
        allValues.add(sym);
        allValues.add(str);
        allValues.add(rand);
        allValues.add(uint);
        allValues.add(pchr);
        allValues.add(opt);
    }

    @Test
    void values() {
        ParseTypeEnum [] values = ParseTypeEnum.values();
        for (ParseTypeEnum type : values) {
            if (!allValues.contains(type)) {
                fail(type.name() + " not found in manually created list.");
            }
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(none, ParseTypeEnum.valueOf("PARSE_T_NONE")),
                () -> assertEquals(pint, ParseTypeEnum.valueOf("PARSE_T_INT")),
                () -> assertEquals(sym,  ParseTypeEnum.valueOf("PARSE_T_SYM")),
                () -> assertEquals(str,  ParseTypeEnum.valueOf("PARSE_T_STR")),
                () -> assertEquals(rand, ParseTypeEnum.valueOf("PARSE_T_RAND")),
                () -> assertEquals(uint, ParseTypeEnum.valueOf("PARSE_T_UINT")),
                () -> assertEquals(pchr, ParseTypeEnum.valueOf("PARSE_T_CHAR")),
                () -> assertEquals(opt,  ParseTypeEnum.valueOf("PARSE_T_OPT"))
        );
    }
}