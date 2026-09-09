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
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectUtils#flavourAssignRandom}, the port of C's {@code flavor_assign_random}
 * ({@code obj-util.c:77}).
 *
 * <p>C counts the flavours of a tval whose {@code sval} is still {@code SV_UNKNOWN}, then for
 * every {@code k_info} entry of that tval with no flavour yet, draws a random index into that
 * count and walks the flavour list in file order to claim the matching one, shrinking the count
 * as each is claimed. Unlike {@code flavor_assign_fixed}, the outer loop's guard is
 * {@code if (k_info[i].tval != tval || k_info[i].flavor) continue;} — a kind of the wrong tval, or
 * one already flavoured, is left untouched. Private, so reached by reflection rather than through
 * its only prospective caller, {@link ObjectUtils#flavourInit}, which does not call it yet.
 *
 * <p>Draws do not use a fixed seed and precompute the expected choice with a second
 * {@link Random} constructed from the same seed via {@link RandomValueUtils#stateInit(long)} —
 * the port's generator does not reproduce C's stream (see {@link RandomValueUtils#randDiv}), so
 * the only thing worth pinning down here is that the method claims whichever candidate its own
 * draw names, in file order, and shrinks the pool as it goes. The fatal, out-of-flavours branch
 * calls {@link System#exit}, so it is not exercised here.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsFlavourAssignRandomTest {

    private List<ObjectKind> savedObjectKinds;
    private String[] savedScrollAdj;

    private static Field objectKindsField() throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField("objectKinds");
        f.setAccessible(true);
        return f;
    }

    private static Field scrollAdjField() throws Exception {
        Field f = ObjectUtils.class.getDeclaredField("scrollAdj");
        f.setAccessible(true);
        return f;
    }

    /**
     * Runs the method under test.
     */
    private static void flavourAssignRandom(TValue tValue) {
        try {
            Method method = ObjectUtils.class.getDeclaredMethod("flavourAssignRandom", TValue.class);
            method.setAccessible(true);
            method.invoke(null, tValue);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("flavourAssignRandom threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("flavourAssignRandom is no longer reachable", e);
        }
    }

    /**
     * A random flavour, whose resolved sval stays {@code SV_UNKNOWN} (0).
     */
    private static Flavour unresolvedRandomFlavour(String text, int index) {
        return new Flavour(text, ColourEnum.COLOUR_WHITE, index);
    }

    /**
     * A flavour already resolved by a prior fixed-assignment pass, so it is no longer a candidate
     * for a random draw.
     */
    private static Flavour resolvedFixedFlavour(String text, int sVal, int index) {
        Flavour flavour = new Flavour(text, "some sval symbol", ColourEnum.COLOUR_WHITE, index);
        flavour.setsVal(sVal);
        return flavour;
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
        savedScrollAdj = (String[]) scrollAdjField().get(null);
    }

    @AfterEach
    void restore() throws Exception {
        objectKindsField().set(null, savedObjectKinds);
        scrollAdjField().set(null, savedScrollAdj);
    }

    /**
     * The ordinary path: one unflavoured kind, one candidate flavour. {@code randInt0(1)} always
     * returns 0 (see {@link RandomValueUtils#randDiv}), so the draw itself needs no seeding here.
     */
    @Nested
    @DisplayName("a single unflavoured kind with one candidate flavour")
    class OrdinaryPath {

        @Test
        @DisplayName("is bound to the kind, and the flavour's sval resolves to the kind's")
        void bindsTheOnlyCandidate() throws Exception {
            Flavour azure = unresolvedRandomFlavour("Azure", 4);
            MiscRegistry.setFlavours(List.of(new FlavourKind(TValue.TV_POTION, '!', List.of(azure))));

            ObjectKind potion = objectKind(TValue.TV_POTION, 7);
            ObjectRegistry.setObjectKinds(List.of(potion));

            flavourAssignRandom(TValue.TV_POTION);

            assertSame(azure, potion.getFlavour());
            assertEquals(7, azure.getsVal());
        }
    }

    /**
     * The outer guard, {@code if (k_info[i].tval != tval || k_info[i].flavor) continue;} — the
     * exact clause this port's outer loop mismatched across several drafts before matching C.
     */
    @Nested
    @DisplayName("the outer kind guard")
    class OuterGuard {

        @Test
        @DisplayName("leaves a kind of a different tval untouched, and still flavours the matching one")
        void leavesWrongTvalKindUntouched() throws Exception {
            Flavour ringFlavour = unresolvedRandomFlavour("Ruby", 3);
            MiscRegistry.setFlavours(List.of(new FlavourKind(TValue.TV_RING, '=', List.of(ringFlavour))));

            ObjectKind wrongTval = objectKind(TValue.TV_POTION, 1);
            ObjectKind matchingTval = objectKind(TValue.TV_RING, 12);
            ObjectRegistry.setObjectKinds(List.of(wrongTval, matchingTval));

            flavourAssignRandom(TValue.TV_RING);

            assertNull(wrongTval.getFlavour());
            assertSame(ringFlavour, matchingTval.getFlavour());
        }

        @Test
        @DisplayName("leaves a kind that already has a flavour untouched")
        void leavesAlreadyFlavouredKindUntouched() throws Exception {
            Flavour existing = unresolvedRandomFlavour("Stale", 1);
            Flavour candidate = unresolvedRandomFlavour("Fresh", 2);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_POTION, '!', List.of(candidate))));

            ObjectKind alreadyFlavoured = objectKind(TValue.TV_POTION, 5);
            alreadyFlavoured.setFlavour(existing);
            ObjectRegistry.setObjectKinds(List.of(alreadyFlavoured));

            flavourAssignRandom(TValue.TV_POTION);

            assertSame(existing, alreadyFlavoured.getFlavour());
            assertEquals(0, candidate.getsVal());
        }
    }

    /**
     * The candidate filter, {@code f->sval != SV_UNKNOWN}, on both the count and the walk — a
     * flavour a fixed pass has already resolved must never be handed out again.
     */
    @Nested
    @DisplayName("a flavour already resolved by a fixed pass")
    class FixedFlavourExclusion {

        @Test
        @DisplayName("is skipped by both the count and the draw, leaving only the unresolved one")
        void skippedFromCountAndDraw() throws Exception {
            Flavour fixedRuby = resolvedFixedFlavour("Ruby", 12, 3);
            Flavour randomJade = unresolvedRandomFlavour("Jade", 16);
            MiscRegistry.setFlavours(List.of(
                    new FlavourKind(TValue.TV_RING, '=', List.of(fixedRuby, randomJade))));

            ObjectKind ring = objectKind(TValue.TV_RING, 40);
            ObjectRegistry.setObjectKinds(List.of(ring));

            flavourAssignRandom(TValue.TV_RING);

            assertSame(randomJade, ring.getFlavour());
            assertEquals(40, randomJade.getsVal());
            assertEquals(12, fixedRuby.getsVal(), "the fixed flavour's own sval must be untouched");
        }
    }

    /**
     * The shrinking pool: two unflavoured kinds sharing a two-candidate pool must end up with the
     * two different flavours, never the same one twice, matching C's {@code flavor_count--} as
     * each candidate is claimed.
     */
    @Nested
    @DisplayName("a pool shared between several kinds")
    class ShrinkingPool {

        @Test
        @DisplayName("hands out every candidate exactly once, across kinds")
        void everyCandidateClaimedExactlyOnce() throws Exception {
            Flavour first = unresolvedRandomFlavour("Alexandrite", 2);
            Flavour second = unresolvedRandomFlavour("Amethyst", 3);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_RING, '=', List.of(first, second))));

            ObjectKind ringA = objectKind(TValue.TV_RING, 20);
            ObjectKind ringB = objectKind(TValue.TV_RING, 21);
            ObjectRegistry.setObjectKinds(List.of(ringA, ringB));

            long seed = 42L;
            RandomValueUtils.stateInit(seed);
            int firstDraw = new Random(seed).nextInt(0, 2);

            flavourAssignRandom(TValue.TV_RING);

            Flavour expectedForA = firstDraw == 0 ? first : second;
            Flavour expectedForB = firstDraw == 0 ? second : first;

            assertSame(expectedForA, ringA.getFlavour());
            assertSame(expectedForB, ringB.getFlavour());
            assertNotSame(ringA.getFlavour(), ringB.getFlavour());
            assertEquals(20, expectedForA.getsVal());
            assertEquals(21, expectedForB.getsVal());
        }
    }

    /**
     * The scroll-only text overwrite, {@code f->text = scroll_adj[k_info[i].sval]}.
     */
    @Nested
    @DisplayName("assigning a scroll flavour")
    class ScrollText {

        @Test
        @DisplayName("overwrites the flavour's text from scrollAdj, keyed by the kind's sval")
        void overwritesTextFromScrollAdj() throws Exception {
            String[] titles = new String[]{"\"Foo\"", "\"Bar\"", "\"Baz\""};
            scrollAdjField().set(null, titles);

            Flavour blank = unresolvedRandomFlavour(null, 9);
            MiscRegistry.setFlavours(
                    List.of(new FlavourKind(TValue.TV_SCROLL, '?', List.of(blank))));

            ObjectKind scroll = objectKind(TValue.TV_SCROLL, 2);
            ObjectRegistry.setObjectKinds(List.of(scroll));

            flavourAssignRandom(TValue.TV_SCROLL);

            assertEquals("\"Baz\"", blank.getText());
        }

        @Test
        @DisplayName("leaves a non-scroll flavour's text alone")
        void leavesNonScrollTextAlone() throws Exception {
            Flavour azure = unresolvedRandomFlavour("Azure", 4);
            MiscRegistry.setFlavours(List.of(new FlavourKind(TValue.TV_POTION, '!', List.of(azure))));

            ObjectKind potion = objectKind(TValue.TV_POTION, 7);
            ObjectRegistry.setObjectKinds(List.of(potion));

            flavourAssignRandom(TValue.TV_POTION);

            assertEquals("Azure", azure.getText());
        }
    }

    /**
     * Several {@link FlavourKind} groups at once, confirming a group for a different tval neither
     * inflates the count nor supplies a candidate.
     */
    @Nested
    @DisplayName("multiple flavour kind groups")
    class MultipleFlavourKindGroups {

        @Test
        @DisplayName("only the matching group's flavours are counted and drawn from")
        void onlyMatchingGroupContributes() throws Exception {
            Flavour ringFlavour = unresolvedRandomFlavour("Ruby", 3);
            Flavour potionFlavour = unresolvedRandomFlavour("Azure", 4);
            MiscRegistry.setFlavours(List.of(
                    new FlavourKind(TValue.TV_RING, '=', List.of(ringFlavour)),
                    new FlavourKind(TValue.TV_POTION, '!', List.of(potionFlavour))));

            ObjectKind ring = objectKind(TValue.TV_RING, 12);
            ObjectRegistry.setObjectKinds(List.of(ring));

            flavourAssignRandom(TValue.TV_RING);

            assertSame(ringFlavour, ring.getFlavour());
            assertTrue(potionFlavour.getsVal() == 0, "the potion group's flavour must be untouched");
        }
    }
}
