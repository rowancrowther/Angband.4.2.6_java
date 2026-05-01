package uk.co.jackoftrades.io.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.io.parsers.ParserSpec;

class ParserSpecTest {
    private ParserSpec spec;
    private int type;
    private String name;

    @BeforeEach
    void setUp() {
        spec = new ParserSpec();
        type = 14;
        name = "Hello";
    }

    @Test
    void getType() {
        setType();
    }

    @Test
    void setType() {
        spec.setType(type);
        assertEquals(type, spec.getType());
    }

    @Test
    void getName() {
        setName();
    }

    @Test
    void setName() {
        spec.setName(name);
        assertEquals(name, spec.getName());
    }
}