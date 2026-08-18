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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.player.PlayerTimedEffect;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link PlayerRegistry#lookupPlayerTimedEffect}, added on 260818.
 *
 * <p>This is the port of C's {@code &timed_effects[idx]}, and the shape differs in a way the tests
 * are here to hold down. C indexes a fixed array by the {@code TMD_*} constant itself, so the
 * lookup cannot fail. The port searches a list loaded from {@code player_timed.txt} and keyed by
 * enum identity, so an effect the data file never defined has no entry at all — and the method
 * answers {@code null} rather than throwing. Callers such as
 * {@link uk.co.jackoftrades.middle.player.Player#timedGradeEq} guard on that, so the null has to
 * keep arriving.
 *
 * <p>{@link TimedEffect#TMD_NONE} is the standing example of an effect with no definition: it is a
 * sentinel the parsers hold before a name is resolved, not a status, so nothing ever loads a record
 * for it.
 *
 * <p>The uninitialised case is the opposite decision and worth separating: reading the registry
 * before the loaders have run is a wiring fault rather than a missing datum, so it throws where the
 * missing datum returns null.
 *
 * <p>The registry is global static state shared with the reader suites, so the loaded list is saved
 * and put back around every test.
 *
 * <p>Class PlayerRegistryTimedEffectTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class PlayerRegistryTimedEffectTest {

    /**
     * Whatever the registry held before this test, put back afterwards.
     */
    private Object saved;

    /**
     * @return the registry's private list of loaded timed effects, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field field() throws Exception {
        Field f = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        f.setAccessible(true);
        return f;
    }

    /**
     * Builds a bare definition for an effect; only its identity matters here.
     *
     * @param effect the effect the definition is for
     * @return the definition
     */
    private static PlayerTimedEffect definitionFor(TimedEffect effect) {
        return new PlayerTimedEffect(effect, "test effect", null, null, null, null,
                List.of(), List.of(), null, null, false, 0, null, false, null, null, null);
    }

    /**
     * Loads the registry with definitions for the given effects.
     *
     * @param effects the effects to load
     */
    private static void load(TimedEffect... effects) throws Exception {
        List<PlayerTimedEffect> all = new ArrayList<>();
        for (TimedEffect effect : effects) {
            all.add(definitionFor(effect));
        }
        field().set(null, all);
    }

    @BeforeEach
    void snapshot() throws Exception {
        saved = field().get(null);
    }

    @AfterEach
    void restore() throws Exception {
        field().set(null, saved);
    }

    @Test
    void findsTheDefinitionForALoadedEffect() throws Exception {
        load(TimedEffect.TMD_STUN, TimedEffect.TMD_CUT);

        PlayerTimedEffect found = PlayerRegistry.lookupPlayerTimedEffect(TimedEffect.TMD_CUT);

        assertEquals(TimedEffect.TMD_CUT, found.getName());
    }

    @Test
    void handsBackTheStoredInstanceRatherThanACopy() throws Exception {
        PlayerTimedEffect stored = definitionFor(TimedEffect.TMD_STUN);
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(stored);
        field().set(null, all);

        assertSame(stored, PlayerRegistry.lookupPlayerTimedEffect(TimedEffect.TMD_STUN));
    }

    @Test
    void answersNullForAnEffectWithNoLoadedDefinition() throws Exception {
        load(TimedEffect.TMD_STUN);

        assertNull(PlayerRegistry.lookupPlayerTimedEffect(TimedEffect.TMD_CUT));
    }

    @Test
    void answersNullForTheSentinelWhichIsNeverLoaded() throws Exception {
        load(TimedEffect.TMD_STUN, TimedEffect.TMD_CUT);

        assertNull(PlayerRegistry.lookupPlayerTimedEffect(TimedEffect.TMD_NONE));
    }

    @Test
    void answersNullWhenNothingHasBeenLoaded() throws Exception {
        field().set(null, new ArrayList<PlayerTimedEffect>());

        assertNull(PlayerRegistry.lookupPlayerTimedEffect(TimedEffect.TMD_STUN));
    }

    @Test
    void findsTheFirstDefinitionWhenAnEffectIsLoadedTwice() throws Exception {
        PlayerTimedEffect first = definitionFor(TimedEffect.TMD_STUN);
        PlayerTimedEffect second = definitionFor(TimedEffect.TMD_STUN);
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(first);
        all.add(second);
        field().set(null, all);

        assertSame(first, PlayerRegistry.lookupPlayerTimedEffect(TimedEffect.TMD_STUN));
    }

    /**
     * Reading before the loaders have run is a wiring fault, and is reported as one.
     */
    @Test
    void throwsRatherThanReturningNullWhenTheRegistryIsUninitialised() throws Exception {
        field().set(null, null);

        assertThrows(IllegalStateException.class,
                () -> PlayerRegistry.lookupPlayerTimedEffect(TimedEffect.TMD_STUN));
    }
}
