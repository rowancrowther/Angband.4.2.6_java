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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link ObjectUtils#flavourAssignFixed}, the port of C's {@code flavor_assign_fixed}
 * ({@code obj-util.c:58}).
 *
 * <p>C walks the flat {@code flavors} list, skips any flavour whose {@code sval} is still
 * {@code SV_UNKNOWN}, and for the rest scans every {@code k_info} entry, binding the flavour onto
 * any kind sharing its {@code tval}/{@code sval}. The port walks
 * {@link MiscRegistry#getFlavours()}, one {@link FlavourKind} per tval group, and the same
 * unconditional scan of {@link ObjectRegistry#getObjectKinds()} — {@link FlavourKind#getValue()}
 * standing in for the tval C copies onto each {@code struct flavor} at parse time. Private, so
 * reached by reflection rather than through its only caller, {@link ObjectUtils#flavourInit}.
 *
 * <p>C's inner loop carries no guard against a kind that already has a flavour (contrast
 * {@code flavor_assign_random}'s {@code if (... || k_info[i].flavor) continue;}), so a fixed
 * flavour always overwrites whatever a kind already carries; that is covered below alongside the
 * ordinary path, the {@code SV_UNKNOWN} skip, non-matching kinds, multiple matches for one
 * flavour, and multiple flavour groups.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsFlavourAssignFixedTest {

    private List<ObjectKind> savedObjectKinds;

    private static Field objectKindsField() throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField("objectKinds");
        f.setAccessible(true);
        return f;
    }

    /**
     * Runs the method under test.
     */
    private static void flavourAssignFixed() {
        try {
            Method method = ObjectUtils.class.getDeclaredMethod("flavourAssignFixed");
            method.setAccessible(true);
            method.invoke(null);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("flavourAssignFixed threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("flavourAssignFixed is no longer reachable", e);
        }
    }

    /**
     * A fixed flavour with its resolved sval already set, as it would be once
     * {@code lookup_sval}/the port's equivalent has resolved {@link Flavour#getsValStr()}.
     */
    private static Flavour resolvedFixedFlavour(String text, int sVal, int index) {
        Flavour flavour = new Flavour(text, "some sval symbol", ColourEnum.COLOUR_WHITE, index);
        flavour.setsVal(sVal);
        return flavour;
    }

    /**
     * A random flavour, whose resolved sval stays {@code SV_UNKNOWN} (0).
     */
    private static Flavour unresolvedRandomFlavour(String text, int index) {
        return new Flavour(text, ColourEnum.COLOUR_WHITE, index);
    }

    private static ObjectKind objectKind(TValue tValue, int sVal) throws Exception {
        ObjectKind kind = new ObjectKind();
        Field tValueField = ObjectKind.class.getDeclaredField("tValue");
        tValueField.setAccessible(true);
        tValueField.set(kind, tValue);
        kind.setsVal(sVal);
        return kind;
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void isolate() throws Exception {
        savedObjectKinds = (List<ObjectKind>) objectKindsField().get(null);
    }

    @AfterEach
    void restore() throws Exception {
        objectKindsField().set(null, savedObjectKinds);
    }

    /**
     * The ordinary path: a fixed flavour is bound onto the one kind sharing its tval/sval.
     */
    @Nested
    @DisplayName("a resolved fixed flavour")
    class OrdinaryFixedFlavour {

        /**
         * A ring flavour with sval 12 is bound onto a ring kind with sval 12, matching C setting
         * {@code k->flavor = f} when {@code k->tval == f->tval && k->sval == f->sval}.
         */
        @Test
        @DisplayName("is bound onto the object kind sharing its tval and sval")
        void boundOntoMatchingKind() throws Exception {
            Flavour rubyRing = resolvedFixedFlavour("Ruby", 12, 3);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_RING, '=', List.of(rubyRing))));

            ObjectKind matching = objectKind(TValue.TV_RING, 12);
            ObjectRegistry.setObjectKinds(List.of(matching));

            flavourAssignFixed();

            assertSame(rubyRing, matching.getFlavour());
        }

        /**
         * A kind whose sval matches but whose tval doesn't is left untouched, and vice versa —
         * both halves of C's {@code &&} guard must hold.
         */
        @Test
        @DisplayName("leaves a kind with a different tval or a different sval untouched")
        void leavesNonMatchingKindsUntouched() throws Exception {
            Flavour ringFlavour = resolvedFixedFlavour("Ruby", 12, 3);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_RING, '=', List.of(ringFlavour))));

            ObjectKind sameSvalDifferentTval = objectKind(TValue.TV_AMULET, 12);
            ObjectKind sameTvalDifferentSval = objectKind(TValue.TV_RING, 99);
            ObjectRegistry.setObjectKinds(List.of(sameSvalDifferentTval, sameTvalDifferentSval));

            flavourAssignFixed();

            assertNull(sameSvalDifferentTval.getFlavour());
            assertNull(sameTvalDifferentSval.getFlavour());
        }

        /**
         * A fixed flavour overwrites a kind's existing flavour unconditionally — C's inner loop
         * carries no {@code k_info[i].flavor} guard (contrast {@code flavor_assign_random}), so a
         * kind that already carries a flavour still gets reassigned when it matches.
         */
        @Test
        @DisplayName("overwrites a kind's existing flavour, unlike flavor_assign_random's guard")
        void overwritesExistingFlavour() throws Exception {
            Flavour rubyRing = resolvedFixedFlavour("Ruby", 12, 3);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_RING, '=', List.of(rubyRing))));

            ObjectKind alreadyFlavoured = objectKind(TValue.TV_RING, 12);
            alreadyFlavoured.setFlavour(unresolvedRandomFlavour("Stale", 99));
            ObjectRegistry.setObjectKinds(List.of(alreadyFlavoured));

            flavourAssignFixed();

            assertSame(rubyRing, alreadyFlavoured.getFlavour());
        }

        /**
         * Every kind sharing the tval/sval is bound, not just the first found — C's inner loop
         * never breaks early.
         */
        @Test
        @DisplayName("binds every matching kind, not just the first")
        void bindsEveryMatchingKind() throws Exception {
            Flavour rubyRing = resolvedFixedFlavour("Ruby", 12, 3);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_RING, '=', List.of(rubyRing))));

            ObjectKind first = objectKind(TValue.TV_RING, 12);
            ObjectKind second = objectKind(TValue.TV_RING, 12);
            ObjectRegistry.setObjectKinds(List.of(first, second));

            flavourAssignFixed();

            assertSame(rubyRing, first.getFlavour());
            assertSame(rubyRing, second.getFlavour());
        }
    }

    /**
     * The skip branch: a flavour whose resolved sval is still {@code SV_UNKNOWN} is never bound
     * onto anything, matching C's {@code if (f->sval == SV_UNKNOWN) continue;}.
     */
    @Nested
    @DisplayName("an unresolved random flavour")
    class UnresolvedRandomFlavour {

        @Test
        @DisplayName("is skipped, even when a kind's tval/sval would otherwise match")
        void skippedRegardlessOfMatchingKind() throws Exception {
            Flavour random = unresolvedRandomFlavour("Boldness", 5);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_POTION, '!', List.of(random))));

            ObjectKind zeroSvalKind = objectKind(TValue.TV_POTION, 0);
            ObjectRegistry.setObjectKinds(List.of(zeroSvalKind));

            flavourAssignFixed();

            assertNull(zeroSvalKind.getFlavour());
        }
    }

    /**
     * Several tval groups at once, confirming every {@link FlavourKind} in the registry is walked,
     * not just the first.
     */
    @Nested
    @DisplayName("multiple flavour kinds")
    class MultipleKinds {

        @Test
        @DisplayName("every group is walked and bound onto its own matching kind")
        void everyGroupIsWalked() throws Exception {
            Flavour rubyRing = resolvedFixedFlavour("Ruby", 12, 3);
            Flavour boldnessPotion = resolvedFixedFlavour("Boldness", 5, 7);
            MiscRegistry.setFlavours(List.of(
                    new FlavourKind(TValue.TV_RING, '=', List.of(rubyRing)),
                    new FlavourKind(TValue.TV_POTION, '!', List.of(boldnessPotion))));

            ObjectKind ringKind = objectKind(TValue.TV_RING, 12);
            ObjectKind potionKind = objectKind(TValue.TV_POTION, 5);
            ObjectRegistry.setObjectKinds(List.of(ringKind, potionKind));

            flavourAssignFixed();

            assertSame(rubyRing, ringKind.getFlavour());
            assertSame(boldnessPotion, potionKind.getFlavour());
        }
    }
}
