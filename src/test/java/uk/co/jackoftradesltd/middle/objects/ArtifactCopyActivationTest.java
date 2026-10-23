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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the activation arm of {@link Artifact#copy()}: the activation, its message, and the
 * recharge time.
 *
 * <p>The expected values come from {@code copy_artifact} in {@code obj-randart.c}, which
 * {@code memcpy}s the whole {@code struct artifact} and then sets {@code activation} and
 * {@code alt_msg} to {@code NULL} on the copy. The recharge time, a plain {@code random_value}
 * struct, survives the {@code memcpy}; the activation and its message never do, whatever the
 * source held.
 *
 * <p>Class ArtifactCopyActivationTest coded on 261008, commented in full on 261008, rewritten for
 * the activation being dropped as in C on 261023.
 *
 * @author Rowan Crowther
 */
class ArtifactCopyActivationTest {

    /**
     * Builds an artifact with the given activation, message and recharge time and nothing else of
     * interest.
     *
     * @param activation the activation, or {@code null}
     * @param message    the activation message, or {@code null}
     * @param time       the recharge time, or {@code null}
     * @return the artifact
     */
    private static Artifact artifact(Activation activation, String message, Random time) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        Map<ObjectModifier, Integer> modifiers = new HashMap<>();
        Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
        Set<Brand> brands = new HashSet<>();
        Set<Slay> slays = new HashSet<>();
        TreeMap<Curse, CurseData> curses = new TreeMap<>(Curse.CURSE_ORDER);
        return new Artifact("Test Blade", "It gleams.", TValue.TV_SWORD, "long sword",
                5, 6, 7, 8, "3d5", 120, 4500, flags, modifiers, elInfo, brands, slays, curses,
                40, 11, 12, 13, activation, message, time);
    }

    /**
     * An activation with a recognisable name and power.
     *
     * @return the activation
     */
    private static Activation activation() {
        return new Activation("test activation", 1, false, 5, 30, new ArrayList<>(), "It fires.", "fires");
    }

    /**
     * C sets {@code alt_msg} to {@code NULL} on the copy, so a message on the source does not
     * reach it.
     */
    @Test
    @DisplayName("the activation message is dropped")
    void messageDropped() {
        Artifact copy = artifact(activation(), "The blade glows.", new Random(0, 1, 1, 20, false)).copy();

        assertNull(copy.getActivationMessage());
    }

    /**
     * C sets {@code activation} to {@code NULL} on the copy, so an activation on the source does
     * not reach it.
     */
    @Test
    @DisplayName("the activation is dropped")
    void activationDropped() {
        Artifact copy = artifact(activation(), "The blade glows.", new Random(0, 1, 1, 20, false)).copy();

        assertNull(copy.getActivation());
    }

    /**
     * A source with no activation and no message gives the same nulls: the copy does not invent
     * either.
     */
    @Test
    @DisplayName("no activation stays no activation")
    void nullActivationStaysNull() {
        Artifact copy = artifact(null, null, new Random(0, 0, 0, 0, false)).copy();

        assertNull(copy.getActivation());
        assertNull(copy.getActivationMessage());
    }

    /**
     * The recharge time is untouched by the {@code NULL}ing and keeps its resolved terms: base 3,
     * 2d10 here. It is a separate instance, as C's struct copy is.
     */
    @Test
    @DisplayName("the recharge time keeps its terms")
    void timeKeepsTerms() {
        Artifact original = artifact(activation(), "m", new Random(3, 0, 2, 10, false));
        Artifact copy = original.copy();

        assertNotSame(original.getTime(), copy.getTime());
        assertEquals(3, copy.getTime().getBase());
        assertEquals(2, copy.getTime().getDice());
        assertEquals(10, copy.getTime().getSides());
    }

    /**
     * A null recharge time becomes a zero value: C's {@code random_value} has no null state, so the
     * copy always has one to read.
     */
    @Test
    @DisplayName("a null recharge time becomes zero")
    void nullTimeBecomesZero() {
        Artifact copy = artifact(null, null, null).copy();

        assertNotNull(copy.getTime());
        assertEquals(0, copy.getTime().getBase());
        assertEquals(0, copy.getTime().getDice());
        assertEquals(0, copy.getTime().getSides());
    }

    /**
     * C writes the {@code NULL}s into the destination only, so copying must not disturb the
     * original's activation or message.
     */
    @Test
    @DisplayName("copying leaves the original untouched")
    void originalUntouched() {
        Activation activation = activation();
        Artifact original = artifact(activation, "The blade glows.", new Random(0, 1, 1, 20, false));

        original.copy();

        assertEquals("The blade glows.", original.getActivationMessage());
        assertSame(activation, original.getActivation());
    }

    /**
     * Upkeep is per-instance play state, not definition data: the copy starts with created, seen and
     * ever-seen all clear whatever the original's flags were.
     */
    @Test
    @DisplayName("the copy starts with a clear upkeep")
    void upkeepIsFresh() {
        Artifact original = artifact(activation(), "m", new Random(0, 1, 1, 20, false));
        original.getAup().setCreated(true);
        original.getAup().setSeen(true);
        original.getAup().setEverseen(true);

        Artifact copy = original.copy();

        assertNotSame(original.getAup(), copy.getAup());
        assertFalse(copy.getAup().isCreated());
        assertFalse(copy.getAup().isSeen());
        assertFalse(copy.getAup().isEverseen());
        assertEquals(true, original.getAup().isCreated(), "the original keeps its own upkeep");
    }
}
