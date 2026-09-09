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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests {@link ObjectUtils#flavourResetFixed}, the port of C's {@code flavor_reset_fixed}
 * ({@code obj-util.c:121}).
 *
 * <p>C walks the flat {@code flavors} list and clears {@code sval} to {@code SV_UNKNOWN} on every
 * entry, skipping only the one ring flavour whose {@code tval} is {@code TV_RING} and whose
 * {@code text} contains {@code "Plain Gold"} ({@code obj-util.c:126}). The port walks
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry#getFlavours()}, one
 * {@link FlavourKind} per tval group, and applies the same two-part test —
 * {@link TValue#isRing()} and {@link String#equals} against {@code "Plain Gold"} — before calling
 * {@link Flavour#setsVal} with {@code 0}, this port's {@code SV_UNKNOWN}. Private, so reached by
 * reflection rather than through its only caller, {@link ObjectUtils#flavourInit}.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsFlavourResetFixedTest {

    /**
     * Runs the method under test.
     */
    private static void flavourResetFixed() {
        try {
            Method method = ObjectUtils.class.getDeclaredMethod("flavourResetFixed");
            method.setAccessible(true);
            method.invoke(null);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("flavourResetFixed threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("flavourResetFixed is no longer reachable", e);
        }
    }

    /**
     * A fixed flavour with its resolved sval already set, as it would be after
     * {@link ObjectUtils#flavourInit()}'s fixed-assignment pass has run once.
     *
     * @param text  the flavour text
     * @param index the flavour's file index
     * @return the flavour, with {@link Flavour#getsVal()} pre-set to a non-zero value
     */
    private static Flavour resolvedFixedFlavour(String text, int index) {
        Flavour flavour = new Flavour(text, "some sval symbol", ColourEnum.COLOUR_WHITE, index);
        flavour.setsVal(7);
        return flavour;
    }

    /**
     * The ordinary path: a fixed flavour that is not the One Ring loses its resolved sval.
     */
    @Nested
    @DisplayName("a fixed flavour that isn't the One Ring")
    class OrdinaryFixedFlavour {

        /**
         * Any ring flavour whose text isn't "Plain Gold" is reset to {@code SV_UNKNOWN} (0), the
         * same as C clearing every entry that fails its {@code strstr} test.
         */
        @Test
        @DisplayName("a differently-named ring flavour is reset to SV_UNKNOWN")
        void differentlyNamedRingFlavourIsReset() {
            Flavour ruby = resolvedFixedFlavour("Ruby", 28);
            MiscRegistry.setFlavours(List.of(new FlavourKind(TValue.TV_RING, '=', List.of(ruby))));

            flavourResetFixed();

            assertEquals(0, ruby.getsVal());
        }

        /**
         * A fixed flavour on a non-ring tval is reset regardless of its text — the tval half of
         * C's guard applies even when the text half alone would have matched.
         */
        @Test
        @DisplayName("a non-ring flavour named 'Plain Gold' is still reset")
        void nonRingFlavourNamedPlainGoldIsStillReset() {
            Flavour impostor = resolvedFixedFlavour("Plain Gold", 1);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_POTION, '!', List.of(impostor))));

            flavourResetFixed();

            assertEquals(0, impostor.getsVal());
        }
    }

    /**
     * The boundary C's guard exists for: the One Ring, which lives through randarts.
     */
    @Nested
    @DisplayName("the One Ring's flavour")
    class OneRing {

        /**
         * A ring flavour whose text is exactly "Plain Gold" keeps its resolved sval untouched.
         */
        @Test
        @DisplayName("a ring flavour named 'Plain Gold' keeps its resolved sval")
        void plainGoldRingFlavourIsPreserved() {
            Flavour theOneRing = resolvedFixedFlavour("Plain Gold", 1);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_RING, '=', List.of(theOneRing))));

            flavourResetFixed();

            assertEquals(7, theOneRing.getsVal());
        }
    }

    /**
     * Several tval groups at once, confirming every {@link FlavourKind} in the registry is walked,
     * not just the first.
     */
    @Nested
    @DisplayName("multiple flavour kinds")
    class MultipleKinds {

        /**
         * A ring group holding the One Ring alongside a potion group: the ring's Plain Gold entry
         * survives, every other entry in both groups is reset.
         */
        @Test
        @DisplayName("every group is walked, only the One Ring is spared")
        void everyGroupIsWalked() {
            Flavour theOneRing = resolvedFixedFlavour("Plain Gold", 1);
            Flavour ruby = resolvedFixedFlavour("Ruby", 28);
            Flavour potion = resolvedFixedFlavour("Boldness", 5);

            MiscRegistry.setFlavours(List.of(
                    new FlavourKind(TValue.TV_RING, '=', List.of(theOneRing, ruby)),
                    new FlavourKind(TValue.TV_POTION, '!', List.of(potion))));

            flavourResetFixed();

            assertEquals(7, theOneRing.getsVal(), "the One Ring alone must be spared");
            assertEquals(0, ruby.getsVal());
            assertEquals(0, potion.getsVal());
            assertNotEquals(theOneRing.getsVal(), ruby.getsVal());
        }
    }
}
