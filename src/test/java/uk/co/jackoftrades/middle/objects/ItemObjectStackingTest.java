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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.CarryCapData;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectStackEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.EquipSlot;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerBody;
import uk.co.jackoftrades.middle.player.PlayerRace;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.co.jackoftrades.testsupport.ItemFixture.set;
import static uk.co.jackoftrades.testsupport.ItemFixture.setStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the three rules that decide whether two stacks may become one — {@code checkForInscription},
 * {@link ItemObject#objectStackable} and {@link ItemObject#mergeable} — the ports of C's
 * {@code check_for_inscrip} ({@code obj-util.c:423}), {@code object_stackable}
 * ({@code obj-pile.c:499}) and {@code object_mergeable} ({@code obj-pile.c:512}).
 *
 * <p>The three sit in a line. {@code similar} settles whether the objects are the same thing,
 * {@code objectStackable} adds the inscription rule on top of it, and {@code mergeable} adds
 * capacity on top of that. {@link ItemObjectSimilarTest} covers the first, so this suite starts one
 * step further along and tests only what each of the outer two adds.
 *
 * <p><b>Why the inscription rule is worth its own tests.</b> It is three clauses joined by
 * {@code ||}, two of them null tests, and every way of getting it wrong fails quietly. Reversed, it
 * declares any inscribed item compatible with anything, so a player's carefully tagged
 * {@code @f1} arrows vanish into an untagged stack; and the case it exists to allow — two
 * uninscribed items — is the one that then dereferences a null. Neither shows up as an error, so
 * both directions are asserted here rather than only the true one.
 *
 * <p><b>Why the capacity rules are worth their own tests.</b> {@code carry-cap:quiver-size} (10) and
 * {@code carry-cap:quiver-slot-size} (40) are both small integers about the quiver, and the port has
 * to reach for the second in every capacity calculation. Reaching for the first instead caps
 * ammunition at 10 rather than 40 — a limit the game still works under, just wrongly, which is why
 * the tests below assert the boundary values rather than merely that some limit exists.
 *
 * <p>Class ItemObjectStackingTest coded on 260824, commented in full on 260824.
 *
 * @author Rowan Crowther
 */
@DisplayName("ItemObject stacking rules")
class ItemObjectStackingTest {

    /**
     * The real quiver-slot size, as {@code constants.txt} gives it. Named so a failure reads as the
     * rule it broke rather than as a bare 40.
     */
    private static final int SLOT_SIZE = 40;

    /**
     * The real thrown multiplier. A thrown weapon takes this much room per item, so a quiver slot
     * holds {@code SLOT_SIZE / THROWN_MULT} of them.
     */
    private static final int THROWN_MULT = 5;

    /**
     * The kind's maximum stack, chosen well above {@link #SLOT_SIZE} so that a test hitting the
     * quiver limit cannot be passing because it hit this one instead.
     */
    private static final int MAX_STACK = 99;

    private static Player savedPlayer;
    private static Object savedConstants;
    private static Object savedBodies;
    private static Object savedRaces;

    /**
     * The kind every fixture item shares. {@code similar} compares kinds by identity, so one
     * instance is what makes two items the same kind.
     */
    private static ObjectKind kind;

    private ItemObject first;
    private ItemObject second;
    private Flag<ObjectStackEnum> packMode;
    private Flag<ObjectStackEnum> quiverMode;
    private Flag<ObjectStackEnum> storeMode;

    /**
     * Seeds the carry-cap constants and installs a player, both of which the code under test reads
     * through process-wide state.
     *
     * <p>Only {@code carryCap} is filled in. Seeding the field directly rather than running
     * {@code GameConstants.init()} keeps a test about two integers from loading every data file.
     * Whatever was there is put back afterwards.
     */
    @BeforeAll
    static void seedGlobals() throws Exception {
        savedConstants = setStatic(GameConstants.class, "data", new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, SLOT_SIZE, THROWN_MULT, 16),
                null, null, null, null, null, null, null, null, null, null, null));

        savedBodies = registryField("playerBodies").get(null);
        savedRaces = registryField("playerRaces").get(null);
        PlayerBody humanoid = new PlayerBody("test",
                List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        registryField("playerBodies").set(null, new ArrayList<>(List.of(humanoid)));
        registryField("playerRaces").set(null, new ArrayList<>(List.of(testRace(humanoid))));

        savedPlayer = GameState.getPlayer();
        GameState.setPlayer(new Player());

        kind = ItemFixture.kindWithBase(TValue.TV_ARROW, "arrow", MAX_STACK);
    }

    @AfterAll
    static void restoreGlobals() throws Exception {
        GameState.setPlayer(savedPlayer);
        setStatic(GameConstants.class, "data", savedConstants);
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
    }

    /**
     * Builds a stack of arrows: ammunition, so the quiver counts it one slot-unit apiece.
     *
     * @param number how many are in the stack
     * @return the stack
     */
    private static ItemObject arrows(int number) {
        return item(TValue.TV_ARROW, number);
    }

    /**
     * Builds a stack of throwing flasks: not ammunition, so each costs
     * {@code carry-cap:thrown-quiver-mult} of the slot.
     *
     * @param number how many are in the stack
     * @return the stack
     */
    private static ItemObject thrown(int number) {
        return ItemFixture.item(TValue.TV_FLASK).kind(kind).number(number)
                .flags(ObjectFlag.OF_THROWING).build();
    }

    /**
     * Builds an item with every field {@code similar} reads, so that the pair stacks and each test
     * can break exactly one thing.
     *
     * @param tValue the item type
     * @param number the stack size
     * @return the item
     */
    private static ItemObject item(TValue tValue, int number) {
        return ItemFixture.item(tValue).kind(kind).number(number).build();
    }

    /**
     * A single stacking mode as a flag set.
     *
     * @param stackMode the mode to raise
     * @return a flag set with that one mode on
     */
    private static Flag<ObjectStackEnum> mode(ObjectStackEnum stackMode) {
        Flag<ObjectStackEnum> flags = new Flag<>(ObjectStackEnum.class);
        flags.on(stackMode);
        return flags;
    }

    /**
     * Builds the one race the seeded registry holds; only its body is read.
     *
     * @param body the body the race presents
     * @return the race
     */
    private static PlayerRace testRace(PlayerBody body) {
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body,
                Map.of(), Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, Map.of());
    }

    /**
     * @param fieldName the registry field to reach
     * @return that private static field, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field registryField(String fieldName) throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    @BeforeEach
    void buildPair() {
        first = arrows(1);
        second = arrows(1);
        packMode = mode(ObjectStackEnum.OSTACK_PACK);
        quiverMode = mode(ObjectStackEnum.OSTACK_QUIVER);
        storeMode = mode(ObjectStackEnum.OSTACK_STORE);
    }

    /**
     * The counting of inscription tags, which every later rule is phrased in terms of.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("checkForInscription")
    class Inscriptions {

        @Test
        @DisplayName("an uninscribed item holds no tags")
        void noNoteIsZero() {
            assertEquals(0, first.checkForInscription("!d"));
        }

        @Test
        @DisplayName("a tag that is not there counts zero")
        void absentTagIsZero() {
            first.setNote("@f1");

            assertEquals(0, first.checkForInscription("!d"));
        }

        @Test
        @DisplayName("a tag that is there counts once")
        void presentTagCountsOnce() {
            first.setNote("@f1!d");

            assertEquals(1, first.checkForInscription("!d"));
        }

        @Test
        @DisplayName("a tag appearing twice counts twice")
        void repeatedTagCountsTwice() {
            first.setNote("!d and later !d");

            assertEquals(2, first.checkForInscription("!d"));
        }

        /**
         * Overlapping matches each count, because C resumes the scan one character past the start of
         * a match rather than past the whole of it. {@code "!!!"} holds {@code "!!"} twice, not once,
         * and a port that resumed past the match would answer 1.
         */
        @Test
        @DisplayName("overlapping occurrences each count")
        void overlappingOccurrencesCount() {
            first.setNote("!!!");

            assertEquals(2, first.checkForInscription("!!"));
        }

        @Test
        @DisplayName("a tag at the very start counts")
        void tagAtStartCounts() {
            first.setNote("!dabc");

            assertEquals(1, first.checkForInscription("!d"));
        }

        /**
         * An empty needle would otherwise match endlessly, since {@code indexOf} finds it at every
         * position. Answering zero is what keeps the loop finite.
         */
        @Test
        @DisplayName("an empty or null tag counts zero rather than looping")
        void emptyTagIsZero() {
            first.setNote("!d");

            assertEquals(0, first.checkForInscription(""));
            assertEquals(0, first.checkForInscription(null));
        }
    }

    /**
     * The inscription rule {@code objectStackable} adds on top of {@code similar}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("objectStackable")
    class InscriptionCompatibility {

        /**
         * The baseline. Every other test here breaks exactly one thing about this pair, so a failure
         * naming the baseline says the fixture is wrong rather than the rule.
         */
        @Test
        @DisplayName("two uninscribed items stack")
        void twoUninscribedStack() {
            assertTrue(first.objectStackable(second, packMode));
        }

        /**
         * The case the two null clauses exist for. A port that had them the wrong way round would
         * fall through to {@code equals} on a null receiver and throw here rather than answer.
         */
        @Test
        @DisplayName("an inscribed item stacks with an uninscribed one")
        void inscribedStacksWithUninscribed() {
            first.setNote("@f1");

            assertTrue(first.objectStackable(second, packMode));
            assertTrue(second.objectStackable(first, packMode));
        }

        @Test
        @DisplayName("two items with the same inscription stack")
        void matchingInscriptionsStack() {
            first.setNote("@f1");
            second.setNote("@f1");

            assertTrue(first.objectStackable(second, packMode));
        }

        /**
         * The refusal the rule exists for, and the one that fails silently when the clauses are
         * reversed: the player's two tags would be merged into one stack under a single tag.
         */
        @Test
        @DisplayName("two items with different inscriptions do not stack")
        void differingInscriptionsDoNotStack() {
            first.setNote("@f1");
            second.setNote("@f2");

            assertFalse(first.objectStackable(second, packMode));
        }
    }

    /**
     * The capacity rules {@code mergeable} adds on top of {@code objectStackable}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("mergeable")
    class Capacity {

        @Test
        @DisplayName("two small stacks merge")
        void smallStacksMerge() {
            assertTrue(first.mergeable(second, packMode));
        }

        @Test
        @DisplayName("a pack stack may fill exactly to the kind's max stack")
        void packFillsToMaxStack() {
            set(first, "number", MAX_STACK - 1);
            set(second, "number", 1);

            assertTrue(first.mergeable(second, packMode));
        }

        @Test
        @DisplayName("a pack stack one over the kind's max stack is refused")
        void packRefusesOverMaxStack() {
            set(first, "number", MAX_STACK);
            set(second, "number", 1);

            assertFalse(first.mergeable(second, packMode));
        }

        /**
         * The boundary that separates the slot size from the slot count. At 40 the merge is allowed
         * and at 41 it is not; a port reading {@code carry-cap:quiver-size} instead would refuse
         * everything above 10 and pass neither of these.
         */
        @Test
        @DisplayName("ammunition fills a quiver slot to quiver-slot-size")
        void ammoFillsQuiverSlot() {
            set(first, "number", SLOT_SIZE - 1);
            set(second, "number", 1);

            assertTrue(first.mergeable(second, quiverMode));
        }

        @Test
        @DisplayName("ammunition one over quiver-slot-size is refused")
        void ammoRefusedOverSlotSize() {
            set(first, "number", SLOT_SIZE);
            set(second, "number", 1);

            assertFalse(first.mergeable(second, quiverMode));
        }

        /**
         * A thrown weapon takes {@code thrown-quiver-mult} times the room of an arrow, so a slot
         * holds 8 rather than 40 of them.
         */
        @Test
        @DisplayName("thrown weapons fill a quiver slot to slot-size over the thrown multiplier")
        void thrownFillsQuiverSlot() {
            ItemObject flask = thrown(SLOT_SIZE / THROWN_MULT - 1);
            ItemObject more = thrown(1);

            assertTrue(flask.mergeable(more, quiverMode));
        }

        @Test
        @DisplayName("thrown weapons one over that limit are refused")
        void thrownRefusedOverSlotLimit() {
            ItemObject flask = thrown(SLOT_SIZE / THROWN_MULT);
            ItemObject more = thrown(1);

            assertFalse(flask.mergeable(more, quiverMode));
        }

        /**
         * A store has no limits at all, which is why the quiver test is nested inside the store test
         * rather than standing beside it. With both flags set, a port that had them side by side
         * would apply a quiver cap C waives.
         */
        @Test
        @DisplayName("store mode waives the max-stack limit")
        void storeWaivesMaxStack() {
            set(first, "number", MAX_STACK);
            set(second, "number", MAX_STACK);

            assertTrue(first.mergeable(second, storeMode));
        }

        @Test
        @DisplayName("store mode waives the quiver limit too, even with both flags set")
        void storeWaivesQuiverLimit() {
            Flag<ObjectStackEnum> both = mode(ObjectStackEnum.OSTACK_STORE);
            both.on(ObjectStackEnum.OSTACK_QUIVER);
            set(first, "number", SLOT_SIZE);
            set(second, "number", SLOT_SIZE);

            assertTrue(first.mergeable(second, both));
        }

        /**
         * Capacity is not the only rule: an incompatible inscription still refuses a pair that would
         * otherwise fit, because {@code mergeable} ends by asking {@code objectStackable}.
         */
        @Test
        @DisplayName("differing inscriptions refuse a merge that would otherwise fit")
        void inscriptionStillApplies() {
            first.setNote("@f1");
            second.setNote("@f2");

            assertFalse(first.mergeable(second, packMode));
        }
    }
}
