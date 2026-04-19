package uk.co.jackoftrades.background.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DamageAspectTest {
    private final DamageAspect minimize = DamageAspect.MINIMIZE;
    private final DamageAspect average = DamageAspect.AVERAGE;
    private final DamageAspect maximize = DamageAspect.MAXIMIZE;
    private final DamageAspect extremify = DamageAspect.EXTREMIFY;
    private final DamageAspect randomize = DamageAspect.RANDOMIZE;
    private ArrayList<DamageAspect> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(minimize);
        allValues.add(average);
        allValues.add(maximize);
        allValues.add(extremify);
        allValues.add(randomize);
    }

    @Test
    void values() {
        for (DamageAspect damageAspect : DamageAspect.values()) {
            if (!allValues.contains(damageAspect))
                fail("DamageAspect." + damageAspect.name() + " not found in manually created list");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(minimize, DamageAspect.valueOf("MINIMIZE")),
                () -> assertEquals(average, DamageAspect.valueOf("AVERAGE")),
                () -> assertEquals(maximize, DamageAspect.valueOf("MAXIMIZE")),
                () -> assertEquals(extremify, DamageAspect.valueOf("EXTREMIFY")),
                () -> assertEquals(randomize, DamageAspect.valueOf("RANDOMIZE"))
        );
    }
}