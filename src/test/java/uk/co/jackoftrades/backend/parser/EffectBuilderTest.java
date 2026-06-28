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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.Test;

import uk.co.jackoftrades.middle.effect.Effect;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EffectBuilder}, which assembles {@link Effect} objects from the
 * twelve-string tuples extracted by the grammar.
 *
 * <p>Covered: the empty case, a known-good tuple (the real {@code CURE:POISONED} effect,
 * which we know loads cleanly from activation.txt), and the data-error case — an unknown
 * effect type, which must surface as an {@link IllegalArgumentException} (the exception the
 * reader catches per record to report file + line).
 *
 * @author ClaudeCode
 */
class EffectBuilderTest {

    /**
     * Builds a twelve-element effect tuple from the supplied leading values, padding the
     * remainder with empty strings. {@link EffectBuilder} indexes all twelve slots, so the
     * tuple must always be full length.
     */
    private static List<String> tuple(String... leading) {
        List<String> t = new ArrayList<>(List.of(leading));
        while (t.size() < 12) {
            t.add("");
        }
        return t;
    }

    @Test
    void emptyInputReturnsEmptyList() {
        assertTrue(EffectBuilder.buildEffects(List.of()).isEmpty());
    }

    @Test
    void validTupleBuildsOneEffect() {
        // type CURE, subtype POISONED — taken from activation.txt's CURE_POISON record.
        List<List<String>> input = List.of(tuple("CURE", "POISONED"));

        List<Effect> effects = EffectBuilder.buildEffects(input);

        assertEquals(1, effects.size());
        assertNotNull(effects.get(0));
    }

    @Test
    void multipleTuplesBuildOneEffectEach() {
        List<List<String>> input = List.of(tuple("CURE", "POISONED"), tuple("CURE", "BLIND"));

        assertEquals(2, EffectBuilder.buildEffects(input).size());
    }

    @Test
    void unknownEffectTypeThrows() {
        List<List<String>> input = List.of(tuple("NOTAREALEFFECT"));

        assertThrows(IllegalArgumentException.class, () -> EffectBuilder.buildEffects(input));
    }
}
