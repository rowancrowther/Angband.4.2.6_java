/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.middle.monsters.enums;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Pins {@link BlowEffectType} against the {@code effect-type:} values in
 * {@code blow_effects.txt} and the {@code streq} chain in {@code blow_color()}
 * ({@code mon-lore.c}).
 *
 * <p>Expected spellings are the seven strings {@code blow_color()} compares
 * {@code effect_type} with, which are also the seven distinct {@code effect-type:} values in the
 * shipped data file. C has no branch for any other word, so every other input - the empty
 * string, a wrong case, a hyphen/underscore swap, a near-miss - must come back {@code null}.
 *
 * <p>Class BlowEffectTypeTest coded on 261009, commented in full on 261009.
 */
class BlowEffectTypeTest {

    private static final Path REAL_FILE = Path.of("lib/gamedata/blow_effects.txt");

    @Test
    void sevenConstantsInTheDataFilesOrderOfUse() {
        assertEquals(7, BlowEffectType.values().length, "C has no NONE or MAX value");
        assertEquals(List.of("element", "flag", "drain", "theft", "eat-food", "eat-light", "all_sustains"),
                java.util.Arrays.stream(BlowEffectType.values()).map(BlowEffectType::getType)
                        .collect(Collectors.toList()));
    }

    @Test
    void eachSpellingResolvesToItsConstant() {
        assertSame(BlowEffectType.BET_ELEMENT, BlowEffectType.getFromString("element"));
        assertSame(BlowEffectType.BET_FLAG, BlowEffectType.getFromString("flag"));
        assertSame(BlowEffectType.BET_DRAIN, BlowEffectType.getFromString("drain"));
        assertSame(BlowEffectType.BET_THEFT, BlowEffectType.getFromString("theft"));
        assertSame(BlowEffectType.BET_EAT_FOOD, BlowEffectType.getFromString("eat-food"));
        assertSame(BlowEffectType.BET_EAT_LIGHT, BlowEffectType.getFromString("eat-light"));
        assertSame(BlowEffectType.BET_ALL_SUSTAINS, BlowEffectType.getFromString("all_sustains"));
    }

    @Test
    void getTypeRoundTripsThroughGetFromString() {
        for (BlowEffectType t : BlowEffectType.values()) {
            assertSame(t, BlowEffectType.getFromString(t.getType()));
        }
    }

    @Test
    void hyphenAndUnderscoreAreNotInterchangeable() {
        // The data file hyphenates eat-food/eat-light but underscores all_sustains; streq does
        // not forgive the swap.
        assertNull(BlowEffectType.getFromString("eat_food"));
        assertNull(BlowEffectType.getFromString("eat_light"));
        assertNull(BlowEffectType.getFromString("all-sustains"));
    }

    @Test
    void matchIsCaseSensitive() {
        assertNull(BlowEffectType.getFromString("Element"));
        assertNull(BlowEffectType.getFromString("FLAG"));
        assertNull(BlowEffectType.getFromString("BET_FLAG"));
    }

    @Test
    void emptyNearMissAndNullInputsResolveToNull() {
        // An effect that names no effect-type: reaches the assembler as the empty string.
        assertNull(BlowEffectType.getFromString(""));
        assertNull(BlowEffectType.getFromString(" flag"));
        assertNull(BlowEffectType.getFromString("flag "));
        assertNull(BlowEffectType.getFromString("elements"));
        assertNull(BlowEffectType.getFromString(null));
    }

    @Test
    void everyEffectTypeInTheShippedDataFileResolves() throws IOException {
        List<String> values = Files.readAllLines(REAL_FILE).stream()
                .filter(l -> l.startsWith("effect-type:"))
                .map(l -> l.substring("effect-type:".length()).trim())
                .collect(Collectors.toList());

        // 13 flag, 3 element, 2 theft, and one each of drain, eat-food, eat-light, all_sustains.
        assertEquals(22, values.size());
        for (String v : values) {
            BlowEffectType resolved = BlowEffectType.getFromString(v);
            assertEquals(v, resolved == null ? "unresolved" : resolved.getType(), "unresolved: " + v);
        }
    }
}
