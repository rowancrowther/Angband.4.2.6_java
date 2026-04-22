package uk.co.jackoftrades.middle.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GlyphTypeTest {
    private final GlyphType none = GlyphType.GLYPH_NONE;
    private final GlyphType warding = GlyphType.GLYPH_WARDING;
    private final GlyphType decoy = GlyphType.GLYPH_DECOY;

    private ArrayList<GlyphType> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(none);
        allValues.add(warding);
        allValues.add(decoy);
    }

    @Test
    void values() {
        for (GlyphType glyphType : GlyphType.values()) {
            if (!allValues.contains(glyphType))
                fail("Glyph type " + glyphType.toString() + " not found in manually created list.");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(none, GlyphType.valueOf("GLYPH_NONE")),
                () -> assertEquals(warding, GlyphType.valueOf("GLYPH_WARDING")),
                () -> assertEquals(decoy, GlyphType.valueOf("GLYPH_DECOY"))
        );
    }
}