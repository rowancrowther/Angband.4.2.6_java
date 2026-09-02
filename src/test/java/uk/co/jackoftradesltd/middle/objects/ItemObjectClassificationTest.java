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
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link ItemObject} accessors that classify an item rather than store something about it
 * — the ignore category it falls into, whether the player is aware of its flavour, whether it gives
 * everything up at a glance, and whether it is fully known.
 *
 * <p>{@link ItemObject#getIgnoreTypeOf()} is the one with real logic. It walks a table whose rows
 * are matched on the object type <em>and</em>, for some rows, on the kind's name — so a sword is
 * "sharp weapons" unless it is a Sword of Chaos, which is "great weapons". The identifier rows come
 * first in the table, which is what lets the specific case win; a table walked in the other order
 * would answer "sharp" for everything.
 *
 * <p>The awareness accessors are guarded against an item with no kind, which is the shape the
 * knowledge code builds counterparts in, and answer {@code false} rather than throwing.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectClassificationTest {

    /**
     * An item of the given type, built the way the parser does so that its collections exist.
     *
     * @param tValue the object type
     * @param kind   the kind to build it on, which may be {@code null}
     * @return the item
     */
    private static ItemObject item(TValue tValue, ObjectKind kind) {
        return new ItemObject(kind, null, null, null, Loc.zero, tValue, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
    }

    /**
     * A kind of the given type and name.
     *
     * @param tValue the object type
     * @param name   the kind's name, which some ignore-table rows match on
     * @return the kind
     */
    private static ObjectKind kind(TValue tValue, String name) {
        return new ObjectKind(null, 0, 0, 0, 0, name, tValue, "test", null, false);
    }

    /**
     * Switches on one of a kind's flags. The accessor hands out a read-only view, so the flag goes
     * on through the field.
     *
     * @param kind the kind to mark
     * @param flag the flag to switch on
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private static void setKindFlag(ObjectKind kind, ObjectKindFlag flag) throws Exception {
        Field field = ObjectKind.class.getDeclaredField("kindFlags");
        field.setAccessible(true);
        ((Flag<ObjectKindFlag>) field.get(kind)).on(flag);
    }

    /**
     * Marks a kind as one the player has learned.
     *
     * @param kind the kind to mark
     * @throws Exception if the field cannot be reached
     */
    private static void setAware(ObjectKind kind) throws Exception {
        Field field = ObjectKind.class.getDeclaredField("aware");
        field.setAccessible(true);
        field.setBoolean(kind, true);
    }

    /**
     * The slay remover, whose twin is already covered — included here because it creates the set on
     * demand like its siblings, and removing from an item that has none must be harmless.
     */
    @Test
    @DisplayName("removing a slay from an item that has none is harmless")
    void removingSlayFromNoneIsHarmless() {
        ItemObject sword = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, "Long Sword"));

        sword.removeSlay(null);

        assertTrue(sword.getSlays().isEmpty());
    }

    /**
     * The ignore category, which decides which of the player's "hide these" settings covers an item.
     */
    @Nested
    @DisplayName("getIgnoreTypeOf")
    class IgnoreCategory {

        /**
         * A type with one unqualified row answers it, whatever the kind is called.
         */
        @Test
        @DisplayName("a type with one row answers it regardless of name")
        void unqualifiedRowMatches() {
            assertEquals(IgnoreType.ITYPE_BLUNT,
                    item(TValue.TV_HAFTED, kind(TValue.TV_HAFTED, "Mace")).getIgnoreTypeOf());
            assertEquals(IgnoreType.ITYPE_ARROW,
                    item(TValue.TV_ARROW, kind(TValue.TV_ARROW, "Arrow")).getIgnoreTypeOf());
        }

        /**
         * A kind whose name matches a qualified row takes that row instead — the specific case
         * winning over the general one, which works only because the qualified rows come first.
         */
        @Test
        @DisplayName("a named kind takes the qualified row")
        void namedKindTakesTheQualifiedRow() {
            assertEquals(IgnoreType.ITYPE_GREAT,
                    item(TValue.TV_HAFTED, kind(TValue.TV_HAFTED, "Disruption")).getIgnoreTypeOf());
            assertEquals(IgnoreType.ITYPE_SHARP,
                    item(TValue.TV_SWORD, kind(TValue.TV_SWORD, "Long Sword")).getIgnoreTypeOf());
            assertEquals(IgnoreType.ITYPE_GREAT,
                    item(TValue.TV_SWORD, kind(TValue.TV_SWORD, "Chaos")).getIgnoreTypeOf());
        }

        /**
         * The three launchers share a type and are told apart entirely by name, so every one of them
         * goes through the qualified path.
         */
        @Test
        @DisplayName("the launchers are told apart by name alone")
        void launchersDifferByNameOnly() {
            assertEquals(IgnoreType.ITYPE_SLING,
                    item(TValue.TV_BOW, kind(TValue.TV_BOW, "Sling")).getIgnoreTypeOf());
            assertEquals(IgnoreType.ITYPE_BOW,
                    item(TValue.TV_BOW, kind(TValue.TV_BOW, "Bow")).getIgnoreTypeOf());
            assertEquals(IgnoreType.ITYPE_CROSSBOW,
                    item(TValue.TV_BOW, kind(TValue.TV_BOW, "Crossbow")).getIgnoreTypeOf());
        }

        /**
         * A type the table does not cover answers the count sentinel, which the ignore code reads as
         * "no category" and stops on.
         */
        @Test
        @DisplayName("an uncovered type answers the sentinel")
        void uncoveredTypeAnswersSentinel() {
            assertEquals(IgnoreType.ITYPE_MAX,
                    item(TValue.TV_POTION, kind(TValue.TV_POTION, "Cure Light Wounds")).getIgnoreTypeOf());
        }

        /**
         * A type that has qualified rows only, with a name matching none of them, falls off the end
         * of the table and answers the sentinel too — the launcher type with an unexpected name is
         * the case that reaches it.
         */
        @Test
        @DisplayName("a name matching no qualified row answers the sentinel")
        void unmatchedNameAnswersSentinel() {
            assertEquals(IgnoreType.ITYPE_MAX,
                    item(TValue.TV_BOW, kind(TValue.TV_BOW, "Ballista")).getIgnoreTypeOf());
        }
    }

    /**
     * The awareness and knowledge tests, all of which are guarded against a missing kind.
     */
    @Nested
    @DisplayName("awareness and knowledge")
    class Awareness {

        /**
         * Awareness lives on the kind, not the item: learning what one blue potion is teaches the
         * player about every blue potion.
         *
         * @throws Exception if the kind's awareness cannot be set
         */
        @Test
        @DisplayName("awareness is a property of the kind")
        void awarenessIsOnTheKind() throws Exception {
            ObjectKind potion = kind(TValue.TV_POTION, "Cure Light Wounds");
            ItemObject first = item(TValue.TV_POTION, potion);
            ItemObject second = item(TValue.TV_POTION, potion);

            assertFalse(first.flavourIsAware());

            setAware(potion);

            assertTrue(first.flavourIsAware());
            assertTrue(second.flavourIsAware(), "the other item of the same kind learned it too");
        }

        /**
         * An item with no kind answers false rather than throwing, which is what lets the knowledge
         * code ask about a counterpart it has not finished building.
         */
        @Test
        @DisplayName("an item with no kind is not aware and not easy to know")
        void noKindAnswersFalse() {
            ItemObject kindless = item(TValue.TV_POTION, null);

            assertFalse(kindless.flavourIsAware());
            assertFalse(kindless.easyKnow());
        }

        /**
         * Easy-know needs both halves: the player aware of the flavour, and the kind marked as one
         * that gives everything up at a glance. Either alone is not enough.
         *
         * @throws Exception if the kind's fields cannot be set
         */
        @Test
        @DisplayName("easy-know needs awareness and the kind flag together")
        void easyKnowNeedsBoth() throws Exception {
            ObjectKind potion = kind(TValue.TV_POTION, "Cure Light Wounds");
            ItemObject item = item(TValue.TV_POTION, potion);

            setAware(potion);
            assertFalse(item.easyKnow(), "aware, but the kind is not marked easy-know");

            setKindFlag(potion, ObjectKindFlag.KF_EASY_KNOW);
            assertTrue(item.easyKnow());
        }

        /**
         * An item with no known half is not fully known — the first guard in the chain, and the one
         * every unidentified object takes.
         */
        @Test
        @DisplayName("an item with no knowledge is not fully known")
        void noKnowledgeIsNotFullyKnown() {
            assertFalse(item(TValue.TV_SWORD, kind(TValue.TV_SWORD, "Long Sword")).isFullyKnown());
        }
    }

    /**
     * The ego accessor pair, which is storage but with a knowledge test beside it that reads
     * differently.
     */
    @Nested
    @DisplayName("ego")
    class Ego {

        /**
         * Setting an ego stores it, and the item then reports as an ego item.
         */
        @Test
        @DisplayName("an ego set on an item is reported")
        void egoRoundTrips() {
            ItemObject sword = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, "Long Sword"));
            EgoItem sharpness = new EgoItem("of Sharpness", "", 1, 0,
                    new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class),
                    new Flag<>(ObjectKindFlag.class),
                    new HashMap<>(), new HashMap<>(), new HashMap<>(),
                    new HashSet<>(), new HashSet<>(), new HashMap<>(),
                    0, 0, 0, 0, new java.util.ArrayList<ObjectKind>(),
                    null, null, null, 0, 0, 0, null, null, false);

            assertFalse(sword.isEgo());

            sword.setEgo(sharpness);

            assertTrue(sword.isEgo());
            assertSame(sharpness, sword.getEgo());
        }

        /**
         * The ignore test reads the item's real ego, and answers false when there is none —
         * the guard its caller gates on the <em>known</em> ego relies on.
         */
        @Test
        @DisplayName("an item with no ego is not ignored by ego")
        void noEgoIsNotIgnoredByEgo() {
            ItemObject sword = item(TValue.TV_SWORD, kind(TValue.TV_SWORD, "Long Sword"));

            assertFalse(sword.egoIsIgnored(IgnoreType.ITYPE_SHARP));
        }
    }
}
