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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player}'s scalar state — the counters, flags and countdowns the turn loop reads and
 * writes, and the accessors that delegate to the calculated {@link PlayerState}.
 *
 * <p>Two groups are worth separating. The plain fields are storage, and are checked as a set because
 * several are adjacent {@code int}s with similar names — energy and total energy, current and
 * maximum hit points — where a crossed accessor would compile and read plausibly.
 *
 * <p>The delegating ones are not storage: {@code hasObjectFlag}, {@code hasPlayerFlag} and
 * {@code getStateLight} all read the calculated state rather than the player, which means they
 * answer about the character <em>as recalculated</em> and not as the data files describe them. That
 * distinction is the reason they exist, so it is asserted rather than assumed.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerScalarStateTest {

    /**
     * The player under test, fresh for each test since all of this is mutable.
     */
    private Player player;

    /**
     * A new player, as the constructor leaves one.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Writes one of the player's private fields, for the state with no setter.
     *
     * @param name  the field's name
     * @param value the value to store
     * @throws Exception if the field cannot be reached
     */
    private void set(String name, Object value) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * The energy pair, which decides when the player next acts.
     */
    @Nested
    @DisplayName("energy")
    class Energy {

        /**
         * The two are separate counters: one is the energy available now, the other the total spent
         * across the game. Setting either must leave the other alone.
         */
        @Test
        @DisplayName("current and total energy are separate counters")
        void energyCountersAreSeparate() {
            player.setEnergy(50);
            player.setTotalEnergy(4000);

            assertEquals(50, player.getEnergy());
            assertEquals(4000, player.getTotalEnergy());

            player.setEnergy(60);

            assertEquals(60, player.getEnergy());
            assertEquals(4000, player.getTotalEnergy(), "the total was not disturbed");
        }
    }

    /**
     * The countdowns, which tick towards an event rather than away from one.
     */
    @Nested
    @DisplayName("countdowns")
    class Countdowns {

        /**
         * Word of recall counts down by one a turn, and the decrement is its own method because
         * nothing else may write it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("word of recall counts down by one")
        void wordRecallCountsDown() throws Exception {
            set("wordRecall", 3);

            player.decrementWordRecall();

            assertEquals(2, player.getWordRecall());
        }

        /**
         * Deep descent has its own counter and its own decrement, and the two countdowns do not
         * touch each other — a player can be waiting on both at once.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("deep descent counts down independently")
        void deepDescentCountsDown() throws Exception {
            set("wordRecall", 3);
            set("deepDescent", 5);

            player.decrementDeepDescent();

            assertEquals(4, player.getDeepDescent());
            assertEquals(3, player.getWordRecall(), "the other countdown was not disturbed");
        }

        /**
         * The decrement does not floor at zero — it is called only while the counter is positive,
         * and the turn loop is what stops it. Worth pinning, because a reader might expect the guard
         * to be here.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the decrement does not floor at zero")
        void decrementDoesNotFloor() throws Exception {
            set("wordRecall", 0);

            player.decrementWordRecall();

            assertEquals(-1, player.getWordRecall());
        }
    }

    /**
     * The state of play: hit points, experience, depth and the several booleans.
     */
    @Nested
    @DisplayName("state of play")
    class StateOfPlay {

        /**
         * A new player starts at zero everywhere, since the character is built afterwards.
         */
        @Test
        @DisplayName("a new player starts at zero")
        void newPlayerStartsAtZero() {
            assertEquals(0, player.getCurrentHP());
            assertEquals(0, player.getMaxHP());
            assertEquals(0, player.getExp());
            assertEquals(0, player.getDepth());
            assertEquals(0, player.getEnergy());
        }

        /**
         * Current and maximum hit points are separate fields, which is the pair most likely to be
         * crossed.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("current and maximum hit points are separate")
        void hitPointsAreSeparate() throws Exception {
            set("currentHP", 12);
            set("maxHP", 40);

            assertEquals(12, player.getCurrentHP());
            assertEquals(40, player.getMaxHP());
        }

        /**
         * A new player is alive, not resting, not on a quest level and in their own shape — every
         * one of these reads false to begin with.
         */
        @Test
        @DisplayName("a new player is alive and doing nothing")
        void newPlayerIsIdle() {
            assertFalse(player.isDead());
            assertFalse(player.isResting());
            assertFalse(PlayerQuest.isQuest(player, 1), "no quest is registered on any level");
            assertFalse(PlayerQuest.isQuest(player, 0), "and the town level never has one");
            assertFalse(player.isShapeChanged());
        }

        /**
         * The shape test is not a null check: a player in the "normal" shape has one, and is not
         * changed. That is why the method compares the name rather than the reference.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the normal shape does not count as changed")
        void normalShapeIsNotChanged() throws Exception {
            set("shape", shapeNamed("normal"));
            assertFalse(player.isShapeChanged());

            set("shape", shapeNamed("bear"));
            assertTrue(player.isShapeChanged());
        }

        /**
         * A fresh player has no shape at all, which is a state C never reaches: {@code player_init}
         * ({@code player-birth.c:457}) assigns the "normal" shape at birth, and that assignment is
         * not ported. So {@code getShape} answering null is the port's normal condition, not an
         * error case, and everything that reads it has to cope.
         */
        @Test
        @DisplayName("a new player has no shape")
        void newPlayerHasNoShape() {
            assertNull(player.getShape());
        }

        /**
         * The shape is handed back by identity, not copied. It is the registry's own description of
         * the form — C's {@code p->shape} points into the {@code shapes} list — so a caller that
         * wrote through it would change that form for every character assuming it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the shape is handed back by identity")
        void shapeIsHandedBackByIdentity() throws Exception {
            PlayerShape bat = shapeNamed("bat");

            set("shape", bat);

            assertSame(bat, player.getShape());
        }

        /**
         * Builds a shape with only its name filled in.
         *
         * @param name the shape's name
         * @return the shape
         * @throws Exception if the name field cannot be reached
         */
        private PlayerShape shapeNamed(String name) {
            return new PlayerShape(name, 0, 0, 0, Map.of(),
                    new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                    Map.of(), Map.of(), List.of(), 1, List.of());
        }
    }

    /**
     * The level the player is standing on, and their remembered copy of it.
     */
    @Nested
    @DisplayName("the cave")
    class Cave {

        /**
         * A new player has no remembered level, matching C's {@code init_player}, which leaves
         * {@code p->cave} null until a level is generated.
         */
        @Test
        @DisplayName("a new player remembers no level")
        void newPlayerHasNoCave() {
            assertNull(player.getCave());
        }

        /**
         * Setting it stores it, by identity — the remembered level is that chunk and not a copy.
         */
        @Test
        @DisplayName("the remembered level round-trips")
        void caveRoundTrips() {
            Chunk known = new Chunk("known", 0, 0, 0, 0, 0, false,
                    5, 5, 0, 1, 1, 0, 0, 0, player);

            player.setCave(known);

            assertSame(known, player.getCave());
        }
    }

    /**
     * The accessors that read the calculated state rather than the player.
     */
    @Nested
    @DisplayName("delegating accessors")
    class Delegating {

        /**
         * A new player has no calculated state at all: it is created by the bonus calculation, not
         * by the constructor. So these accessors throw rather than answering a default, and nothing
         * may ask about a character's flags before their bonuses have been worked out.
         */
        @Test
        @DisplayName("a new player has no calculated state, and the accessors say so")
        void newPlayerHasNoState() {
            assertNull(player.getPlayerState());

            assertThrows(NullPointerException.class,
                    () -> player.hasObjectFlag(ObjectFlag.OF_FEATHER));
            assertThrows(NullPointerException.class,
                    () -> player.hasPlayerFlag(PlayerFlag.PF_FAST_SHOT));
            assertThrows(NullPointerException.class, player::getStateLight);
        }

        /**
         * Once a state exists, the accessors read it. The player's flags are whatever the last
         * recalculation concluded, not what any one item says — which is the reason these delegate
         * rather than walking the gear.
         *
         * <p>The state has no setter and its flag set no public mutator, so both are reached by
         * reflection.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the flag tests read the calculated state")
        void flagTestsReadTheState() throws Exception {
            PlayerState state = new PlayerState();
            set("state", state);

            assertFalse(player.hasObjectFlag(ObjectFlag.OF_FEATHER));
            assertEquals(0, player.getStateLight());

            Field field = PlayerState.class.getDeclaredField("flags");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Flag<ObjectFlag> flags = (Flag<ObjectFlag>) field.get(state);
            flags.on(ObjectFlag.OF_FEATHER);

            assertTrue(player.hasObjectFlag(ObjectFlag.OF_FEATHER),
                    "the player answers from the recalculated state, not from its gear");
        }
    }

    /**
     * {@code nonCurseRunesKnown}, which asks whether everything on an item bar its curses has been
     * learned. Static, and the one method here with real logic.
     */
    @Nested
    @DisplayName("nonCurseRunesKnown")
    class RunesKnown {

        /**
         * An item with no known half cannot have had anything learned from it, and neither can a
         * null one — the guard the ignore code relies on.
         */
        @Test
        @DisplayName("an item with no knowledge is not known")
        void noKnowledgeIsNotKnown() {
            assertFalse(PlayerKnowledge.nonCurseRunesKnown(null));
            assertFalse(PlayerKnowledge.nonCurseRunesKnown(new ItemObject()));
        }
    }
}
