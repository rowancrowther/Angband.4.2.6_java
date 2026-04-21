package uk.co.jackoftrades.background.strings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuarkTest {
    private Quark quark;
    private Quark qInit;

    @BeforeEach
    void setUp() {
        quark = new Quark();
        quark.init();
    }

    @AfterEach
    void clearUp() {
        quark.cleanup();
        quark = null;
    }

    @Test
    void add() {
        int hello = quark.add("Hello");
        int i = quark.add("I");
        int am = quark.add("am");
        int a = quark.add("a");
        int test = quark.add("test.");

        assertAll(
                () -> assertEquals("Hello", quark.getQuark(hello)),
                () -> assertEquals("I", quark.getQuark(i)),
                () -> assertEquals("am", quark.getQuark(am)),
                () -> assertEquals("a", quark.getQuark(a)),
                () -> assertEquals("test.", quark.getQuark(test))
        );
    }

    @Test
    void getQuark() {
        int i1 = quark.add("One");
        int i2 = quark.add("Two");
        int error = 100;

        assertAll(
                () -> assertEquals("One", quark.getQuark(i1)),
                () -> assertEquals("Two", quark.getQuark(i2)),
                () -> assertNull(quark.getQuark(error))
        );
    }

    @Test
    void getName() {
        String name = "Quark";
        assertEquals(name, quark.getName());
    }

    @Test
    void init() {
        qInit = new Quark();
        String name = "Quark";
        qInit.init();
        assertEquals(name, qInit.getName());
        assertDoesNotThrow(() -> {
                    qInit.add("Hi there");
                }
        );
        qInit.cleanup();
        assertThrows(NullPointerException.class, () -> {
            qInit.add("Hi there");
        });
    }

    @Test
    void cleanup() {
        init();
    }
}