package uk.co.jackoftrades.io.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.io.enums.FileModeEnum;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileModeEnumTest {
    private final FileModeEnum append = FileModeEnum.MODE_APPEND;
    private final FileModeEnum write  = FileModeEnum.MODE_WRITE;
    private final FileModeEnum read   = FileModeEnum.MODE_READ;
    private ArrayList<FileModeEnum> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(append);
        allValues.add(write);
        allValues.add(read);
    }

    @Test
    void values() {
        FileModeEnum [] values = FileModeEnum.values();

        for (FileModeEnum mode : values) {
            if (!allValues.contains(mode))
                fail("FileModeEnum " + mode.toString() + " not found in constructed set.");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(read, FileModeEnum.valueOf("MODE_READ")),
                () -> assertEquals(write, FileModeEnum.valueOf("MODE_WRITE")),
                () -> assertEquals(append, FileModeEnum.valueOf("MODE_APPEND"))
        );
    }
}