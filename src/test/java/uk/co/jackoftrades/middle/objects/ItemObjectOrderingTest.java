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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.magic.MagicBook;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerClass;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#earlierObject}, the port of C's {@code earlier_object}
 * ({@code obj-gear.c}) — the comparison the pack ordering is built from.
 *
 * <p>It answers for one slot at a time: given the object holding a position and a candidate for it,
 * {@code true} means the candidate belongs earlier. The two null tests come first and are
 * deliberately asymmetric — a null candidate never displaces anything, while a null incumbent is
 * always displaced, which is how the first candidate for an empty slot is accepted.
 *
 * <p>The comparisons run in C's order and each returns as soon as it separates the two, so a test
 * for a later rule has to make every earlier one tie. That is why the objects below share a type
 * where the test is about sub-type, and a sub-type where it is about value — a fixture that differed
 * in an earlier field would be answered before reaching the rule under test.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectOrderingTest {

    /**
     * The sub-type the player's class can read.
     */
    private static final int READABLE_SVAL = 3;

    /**
     * The player the game held before each test.
     */
    private Player savedPlayer;

    /**
     * A class that can read the one magic book.
     *
     * @return the class
     * @throws Exception if the book's sub-type cannot be set
     */
    private static PlayerClass casterClass() throws Exception {
        MagicRealm arcane = new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell",
                TValue.TV_MAGIC_BOOK);
        MagicBook book = new MagicBook(TValue.TV_MAGIC_BOOK, "Magic for Beginners", false,
                1, arcane, null, 0, 0, 0, 0, List.of());

        // The book resolves its own sub-type from the object registry, which a unit test has not
        // loaded, so it is set directly here.
        Field sVal = MagicBook.class.getDeclaredField("sVal");
        sVal.setAccessible(true);
        sVal.setInt(book, READABLE_SVAL);

        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<PlayerSkill, Integer> extra = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
            extra.put(skill, 0);
        }

        return new PlayerClass("Test Caster", List.of(), stats, skills, extra, 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(), new ClassMagic(1, 300, 1, List.of(book)));
    }

    /**
     * An item of the given type and sub-type.
     *
     * @param tValue the object type
     * @param sVal   the sub-type within it
     * @return the item
     * @throws Exception if a field cannot be reached
     */
    private static ItemObject item(TValue tValue, int sVal) throws Exception {
        ObjectKind kind = kind(tValue, sVal);

        ItemObject item = new ItemObject();
        Field kindField = ItemObject.class.getDeclaredField("kind");
        kindField.setAccessible(true);
        kindField.set(item, kind);
        Field tValueField = ItemObject.class.getDeclaredField("tValue");
        tValueField.setAccessible(true);
        tValueField.set(item, tValue);
        // The comparison reads the item's own sub-type rather than its kind's, so both are set.
        item.setsValue(sVal);

        return item;
    }

    /**
     * An object kind of the given type and sub-type.
     *
     * @param tValue the object type
     * @param sVal   the sub-type within it
     * @return the kind
     */
    private static ObjectKind kind(TValue tValue, int sVal) throws Exception {
        ObjectKind kind = new ObjectKind(null, 0, 0, 0, 0, "test", tValue, "test", null, false);
        kind.setsVal(sVal);

        // A kind with no flavour to disguise it is marked aware when the data files are loaded, and
        // the awareness rule in the comparison runs before the sub-type one — so a kind left unaware
        // here would be separated before the rule under test was reached.
        Field aware = ObjectKind.class.getDeclaredField("aware");
        aware.setAccessible(true);
        aware.setBoolean(kind, true);

        return kind;
    }

    /**
     * Installs a player whose class can read one particular book.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void installCaster() throws Exception {
        savedPlayer = GameState.getPlayer();

        Player player = new Player();
        Field field = Player.class.getDeclaredField("playerClass");
        field.setAccessible(true);
        field.set(player, casterClass());

        GameState.setPlayer(player);
    }

    /**
     * Puts the game's player back.
     */
    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    /**
     * The null tests, which come first and are not symmetrical.
     */
    @Nested
    @DisplayName("null handling")
    class Nulls {

        /**
         * A null candidate never displaces anything — there is nothing to put in the slot.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a null candidate never comes earlier")
        void nullCandidateNeverWins() throws Exception {
            assertFalse(ItemObject.earlierObject(item(TValue.TV_POTION, 1), null, false));
            assertFalse(ItemObject.earlierObject(null, null, false));
        }

        /**
         * A null incumbent is always displaced, which is how the first candidate for an empty slot
         * is accepted. Tested after the pair above, because the order of the two guards is what
         * makes null-against-null answer false rather than true.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a null incumbent is always displaced")
        void nullIncumbentAlwaysLoses() throws Exception {
            assertTrue(ItemObject.earlierObject(null, item(TValue.TV_POTION, 1), false));
        }
    }

    /**
     * The readable-book preference, which runs first.
     */
    @Nested
    @DisplayName("readable books")
    class ReadableBooks {

        /**
         * A book the player's class can read displaces one it cannot, so the useful books gather at
         * the top of the pack. Both are magic books, so the comparison turns on the sub-type alone.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a readable book comes before an unreadable one")
        void readableBookComesFirst() throws Exception {
            ItemObject readable = item(TValue.TV_MAGIC_BOOK, READABLE_SVAL);
            ItemObject unreadable = item(TValue.TV_MAGIC_BOOK, READABLE_SVAL + 1);

            assertTrue(ItemObject.earlierObject(unreadable, readable, false));
        }

        /**
         * And the reverse does not: an unreadable book does not displace a readable one.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("an unreadable book does not displace a readable one")
        void unreadableBookDoesNotDisplace() throws Exception {
            ItemObject readable = item(TValue.TV_MAGIC_BOOK, READABLE_SVAL);
            ItemObject unreadable = item(TValue.TV_MAGIC_BOOK, READABLE_SVAL + 1);

            assertFalse(ItemObject.earlierObject(readable, unreadable, false));
        }

        /**
         * The preference is suppressed in a store, whose stock is listed by its own rules rather
         * than by what this particular character can read. With it suppressed the comparison falls
         * through to the sub-type ordering, which puts the lower sval first.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a store ignores the readable-book preference")
        void storeIgnoresThePreference() throws Exception {
            // The readable book has the higher sub-type, so the two rules disagree: readability
            // would put it first, sub-type ordering would not.
            ItemObject readable = item(TValue.TV_MAGIC_BOOK, READABLE_SVAL);
            ItemObject lowerSval = item(TValue.TV_MAGIC_BOOK, READABLE_SVAL - 1);

            assertTrue(ItemObject.earlierObject(lowerSval, readable, false),
                    "in the pack, readability wins over sub-type");
            assertFalse(ItemObject.earlierObject(lowerSval, readable, true),
                    "in a store, readability is suppressed and the lower sub-type keeps its place");
        }

        /**
         * <b>Outstanding.</b> Browsability is decided by sub-type and the mere fact of being a book,
         * not by the book's own type — so a prayer book sharing a sval with the class's magic book
         * reads as browsable to a mage.
         *
         * <p>C compares both halves: {@code kind->tval == book.tval && kind->sval == book.sval}
         * ({@code obj-util.c:766}). The port's {@code ObjectKind.canBrowse} tests
         * {@code tValue.isBook()} instead of the book's own type, so the tval half of C's test is
         * missing.
         *
         * <p>Asserted as it behaves, so the test starts failing the day the comparison is
         * tightened — at which point it becomes a test that the two types are told apart.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a book of another realm with the same sub-type reads as browsable")
        void otherRealmWithSameSvalIsBrowsable() throws Exception {
            ObjectKind magic = kind(TValue.TV_MAGIC_BOOK, READABLE_SVAL);
            ObjectKind prayer = kind(TValue.TV_PRAYER_BOOK, READABLE_SVAL);

            assertTrue(magic.canBrowse(), "the class's own book, as expected");
            assertTrue(prayer.canBrowse(),
                    "and a prayer book of the same sub-type, which C would refuse");
        }
    }

    /**
     * The orderings that run once readability has tied.
     */
    @Nested
    @DisplayName("later comparisons")
    class LaterComparisons {

        /**
         * Objects sort by decreasing type ordinal, so the type declared later in the enum comes
         * first in the pack.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("objects sort by decreasing type")
        void typeOrdersFirst() throws Exception {
            ItemObject earlierType = item(TValue.TV_SWORD, 1);
            ItemObject laterType = item(TValue.TV_POTION, 1);

            boolean laterTypeWins = TValue.TV_POTION.ordinal() > TValue.TV_SWORD.ordinal();

            assertEquals(laterTypeWins, ItemObject.earlierObject(earlierType, laterType, false));
        }

        /**
         * Within one type, objects sort by increasing sub-type — the lower sval first.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("within a type, the lower sub-type comes first")
        void subTypeOrdersWithinAType() throws Exception {
            // Swords rather than potions: a flavoured type is separated by the awareness rules
            // before the sub-type comparison is reached.
            ItemObject low = item(TValue.TV_SWORD, 1);
            ItemObject high = item(TValue.TV_SWORD, 5);

            assertTrue(ItemObject.earlierObject(high, low, false),
                    "the lower sub-type displaces the higher");
            assertFalse(ItemObject.earlierObject(low, high, false));
        }

        /**
         * Two objects alike in every compared field have no preference either way, which leaves the
         * pack holding them in the order they went in.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("two identical objects have no preference either way")
        void identicalObjectsHaveNoPreference() throws Exception {
            ItemObject first = item(TValue.TV_SWORD, 1);
            ItemObject second = item(TValue.TV_SWORD, 1);

            assertFalse(ItemObject.earlierObject(first, second, false));
            assertFalse(ItemObject.earlierObject(second, first, false));
        }
    }
}
