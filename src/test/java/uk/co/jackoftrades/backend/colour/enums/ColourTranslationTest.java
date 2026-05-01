package uk.co.jackoftrades.backend.colour.enums;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ColourTranslationTest {
    private static final ColourTranslation full = ColourTranslation.ATTR_FULL;
    private static final ColourTranslation mono = ColourTranslation.ATTR_MONO;
    private static final ColourTranslation vga = ColourTranslation.ATTR_VGA;
    private static final ColourTranslation blind = ColourTranslation.ATTR_BLIND;
    private static final ColourTranslation light = ColourTranslation.ATTR_LIGHT;
    private static final ColourTranslation dark = ColourTranslation.ATTR_DARK;
    private static final ColourTranslation high = ColourTranslation.ATTR_HIGH;
    private static final ColourTranslation metal = ColourTranslation.ATTR_METAL;
    private static final ColourTranslation misc = ColourTranslation.ATTR_MISC;
    private static final ColourTranslation max = ColourTranslation.ATTR_MAX;

    private static ArrayList<ColourTranslation> allValues;

    @BeforeAll
    static void setUpAll() {
        allValues = new ArrayList<>();

        allValues.add(full);
        allValues.add(mono);
        allValues.add(vga);
        allValues.add(blind);
        allValues.add(light);
        allValues.add(dark);
        allValues.add(high);
        allValues.add(metal);
        allValues.add(misc);
        allValues.add(max);
    }

    @Test
    void getValue() {
        assertAll(
                () -> assertEquals(0, full.getValue()),
                () -> assertEquals(1, mono.getValue()),
                () -> assertEquals(2, vga.getValue()),
                () -> assertEquals(3, blind.getValue()),
                () -> assertEquals(4, light.getValue()),
                () -> assertEquals(5, dark.getValue()),
                () -> assertEquals(6, high.getValue()),
                () -> assertEquals(7, metal.getValue()),
                () -> assertEquals(8, misc.getValue()),
                () -> assertEquals(9, max.getValue())
        );
    }

    @Test
    void values() {
        ColourTranslation[] values = ColourTranslation.values();

        for (ColourTranslation value : values)
            if (!allValues.contains(value))
                fail("Could not find Colour Translation " + value + " in manually created list.");
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(full, ColourTranslation.valueOf("ATTR_FULL")),
                () -> assertEquals(mono, ColourTranslation.valueOf("ATTR_MONO")),
                () -> assertEquals(vga, ColourTranslation.valueOf("ATTR_VGA")),
                () -> assertEquals(blind, ColourTranslation.valueOf("ATTR_BLIND")),
                () -> assertEquals(light, ColourTranslation.valueOf("ATTR_LIGHT")),
                () -> assertEquals(dark, ColourTranslation.valueOf("ATTR_DARK")),
                () -> assertEquals(high, ColourTranslation.valueOf("ATTR_HIGH")),
                () -> assertEquals(metal, ColourTranslation.valueOf("ATTR_METAL")),
                () -> assertEquals(misc, ColourTranslation.valueOf("ATTR_MISC")),
                () -> assertEquals(max, ColourTranslation.valueOf("ATTR_MAX"))
        );
    }
}