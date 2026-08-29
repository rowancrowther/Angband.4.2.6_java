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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player}'s experience, options, timed-effect reads and the several methods still
 * awaiting their subsystems.
 *
 * <p>{@code expLose} is the one with arithmetic, and its cap is the part worth pinning: a loss
 * larger than the player's experience takes all of it rather than driving the total negative, and
 * the same cap applies to the maximum when the loss is permanent — so a drained character ends at
 * zero and not below.
 *
 * <p>The stubs are covered too, and stated as stubs. C's {@code player_set_timed} is the sink both
 * {@code incTimed} and {@code decTimed} funnel through, so while it returns {@code false} without
 * doing anything, the whole timed-effect family is inert — and {@code decTimed}, which is written,
 * therefore reports no change even when it has computed one. That is worth recording, because it
 * looks like a bug in {@code decTimed} rather than an unported sink.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerProgressionTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A new player for each test.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Writes one of the player's private fields.
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
     * Reads one of the player's private int fields.
     *
     * @param name the field's name
     * @return its value
     * @throws Exception if the field cannot be reached
     */
    private int intField(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * Experience loss, which caps rather than going negative.
     */
    @Nested
    @DisplayName("expLose")
    class ExperienceLoss {

        /**
         * An ordinary loss is subtracted, and leaves the maximum alone when it is not permanent —
         * which is what lets a drained character earn the same experience back.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a temporary loss leaves the maximum alone")
        void temporaryLossKeepsTheMaximum() throws Exception {
            set("exp", 1000);
            set("maxExp", 1000);

            player.expLose(300, false);

            assertEquals(700, player.getExp());
            assertEquals(1000, intField("maxExp"), "the maximum is what can be earned back");
        }

        /**
         * A permanent loss takes the maximum down with it, so the experience is gone for good.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a permanent loss takes the maximum too")
        void permanentLossTakesTheMaximum() throws Exception {
            set("exp", 1000);
            set("maxExp", 1000);

            player.expLose(300, true);

            assertEquals(700, player.getExp());
            assertEquals(700, intField("maxExp"));
        }

        /**
         * A loss larger than the player has takes everything and stops — experience never goes
         * negative, however hard the drain.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a loss larger than the total takes everything and stops at zero")
        void oversizedLossStopsAtZero() throws Exception {
            set("exp", 400);
            set("maxExp", 1000);

            player.expLose(9999, false);

            assertEquals(0, player.getExp());
        }

        /**
         * And the cap applies to the maximum as well: a permanent drain of everything reduces the
         * maximum by what was actually lost, not by what was asked for.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the cap applies to the maximum on a permanent loss")
        void capAppliesToTheMaximum() throws Exception {
            set("exp", 400);
            set("maxExp", 1000);

            player.expLose(9999, true);

            assertEquals(0, player.getExp());
            assertEquals(600, intField("maxExp"),
                    "the maximum lost the 400 actually taken, not the 9999 asked for");
        }
    }

    /**
     * The timed-effect reads, which answer from the player's own table.
     */
    @Nested
    @DisplayName("timed effects")
    class Timed {

        /**
         * An effect the player is not under reads as zero turns rather than as absent, which is what
         * lets every caller treat the answer as a count.
         */
        @Test
        @DisplayName("an effect not in force reads as zero turns")
        void absentEffectReadsZero() {
            assertEquals(0, player.getTimedEffect(TimedEffect.TMD_FAST));
        }

        /**
         * One in force reads its remaining turns.
         *
         * @throws Exception if the table cannot be reached
         */
        @Test
        @DisplayName("an effect in force reads its remaining turns")
        void presentEffectReadsItsTurns() throws Exception {
            Map<TimedEffect, Integer> table = new HashMap<>();
            table.put(TimedEffect.TMD_FAST, 12);
            set("timed", table);

            assertEquals(12, player.getTimedEffect(TimedEffect.TMD_FAST));
            assertEquals(0, player.getTimedEffect(TimedEffect.TMD_SLOW), "and the others still read zero");
        }

        /**
         * <b>Outstanding.</b> {@code setTimed} is now written, and {@code decTimed} funnels through
         * it, so both are covered by suites of their own. The two that remain unwritten are
         * {@code incTimed} and {@code clearTimed}, and what is worth holding about them is that they
         * are stubs which report no change and make none.
         *
         * <p>A stub returning {@code false} reads to a caller as "the effect did not take", which is
         * the right answer for the wrong reason. Pinning the untouched duration alongside it is what
         * separates the two: when either is ported, this fails, and that is the point at which it
         * should be rewritten around the real transitions.
         *
         * @throws Exception if the table cannot be reached
         */
        @Test
        @DisplayName("incTimed and clearTimed are still stubs that change nothing")
        void theRemainingTimedStubsChangeNothing() throws Exception {
            Map<TimedEffect, Integer> table = new HashMap<>();
            table.put(TimedEffect.TMD_FAST, 12);
            set("timed", table);

            assertFalse(player.incTimed(TimedEffect.TMD_FAST, 5, false, false, false),
                    "incTimed is still a stub and reports no change");
            assertFalse(player.clearTimed(TimedEffect.TMD_FAST, false, false),
                    "clearTimed is still a stub and reports no change");

            assertEquals(12, player.getTimedEffect(TimedEffect.TMD_FAST),
                    "the duration is untouched by either of them");
        }

        /**
         * The table is not sparse: the constructor fills it with a zero for every effect, so
         * {@code decTimed} can read it directly without a containment test and an effect the player
         * has never been under still has an entry.
         *
         * <p>The probe that used to stand here called {@code decTimed} on {@link TimedEffect#TMD_NONE}
         * to show that unguarded read surviving. It cannot any more: {@code decTimed} hands the
         * computed duration to {@code setTimed}, which rejects an effect the registry holds no
         * definition for, and nothing defines {@code TMD_NONE}. The density itself is what this test
         * is for, and the assertion below reads every entry to establish it.
         */
        @Test
        @DisplayName("every effect has an entry from the start")
        void everyEffectHasAnEntry() {
            for (TimedEffect effect : TimedEffect.values()) {
                assertEquals(0, player.getTimedEffect(effect), effect + " should start at zero");
            }
        }
    }

    /**
     * The options, gear and class accessors.
     */
    @Nested
    @DisplayName("accessors")
    class Accessors {

        /**
         * Options answer from the player's own set, which the constructor fills with the defaults —
         * so a fresh player answers rather than throwing, and an option that defaults off reads off.
         */
        @Test
        @DisplayName("options answer from the player's own set")
        void optionsAnswer() {
            assertFalse(player.opt(PlayerOptionEnum.OP_rogue_like_commands),
                    "the roguelike keyset is off by default");
        }

        /**
         * The gear starts empty rather than null, so the pack walkers need no null check.
         */
        @Test
        @DisplayName("a new player carries nothing")
        void gearStartsEmpty() {
            assertTrue(player.getGear().isEmpty());
        }

        /**
         * The class is unset until the character is built.
         */
        @Test
        @DisplayName("a new player has no class")
        void classStartsUnset() {
            assertNull(player.getPlayerClass());
        }

        /**
         * The bloodlust-coercion skip is a count rather than a flag — C keeps it as an integer so
         * that nested suppressions can be balanced — and it starts at zero.
         */
        @Test
        @DisplayName("the coercion skip starts at zero")
        void coercionSkipStartsAtZero() {
            assertEquals(0, player.getSkipCmdCoercion());
        }
    }

    /**
     * The methods awaiting their subsystems. Each is called and asserted to be harmless, which
     * records what is outstanding and fails the day one starts doing something untested.
     */
    @Nested
    @DisplayName("stubs")
    class Stubs {

        /**
         * Draining a stat reports no change, so nothing that depends on a successful drain happens.
         */
        @Test
        @DisplayName("statDec reports no change")
        void statDecIsAStub() {
            assertFalse(player.statDec(Stats.STAT_STR, false));
            assertFalse(player.statDec(Stats.STAT_STR, true));
        }

        /**
         * The recall depth is not yet recorded by its own method, so a word of recall would return
         * the player to the default rather than to their deepest level.
         *
         * <p>Worth knowing that the field is written elsewhere — {@code updateDungeonDepth} sets it
         * whenever a new deepest level is reached — so the value is not always zero, it is just not
         * this method that puts it there.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("setRecallDepth does nothing yet")
        void setRecallDepthIsAStub() throws Exception {
            player.setRecallDepth();

            assertEquals(0, intField("recallDepth"));
        }

        /**
         * The ignore test for a known item always answers false, so nothing is hidden by that route
         * yet. Its comment says as much.
         */
        @Test
        @DisplayName("ignoreKnownItemOk always answers false")
        void ignoreKnownItemIsAStub() {
            assertFalse(player.ignoreKnownItemOk(new ItemObject()));
        }

        /**
         * The three turn-loop entry points still awaiting their subsystems do nothing when called.
         * {@code handleStuff} and {@code redrawStuff} await the display side, and {@code calcSpells}
         * awaits the spell tables.
         *
         * <p>{@code updateStuff} used to be tested here alongside them and no longer is: it is
         * written, and has its own suite in {@code PlayerUpdateStuffTest}. Calling it here would
         * pass for the wrong reason — with no update flag raised it returns at its leading guard,
         * which proves nothing either way.
         */
        @Test
        @DisplayName("handleStuff, redrawStuff and calcSpells do nothing yet")
        void unportedTurnLoopEntryPointsAreStubbed() {
            player.calcSpells();
            player.handleStuff();
            player.redrawStuff();

            assertNull(player.getPlayerState(), "no state was calculated");
        }
    }

    /**
     * The light calculation, which is written rather than stubbed — it walks the equipment for
     * whatever is glowing and writes the brightest result into the state it is handed.
     */
    @Nested
    @DisplayName("calcLight")
    class Light {

        /**
         * The constants holder as it was before this test, put back afterwards.
         */
        private Object savedConstants;

        /**
         * A player carrying nothing has no light of their own, whatever else is going on. The
         * calculation writes into the state it is given rather than the player's own, which is what
         * lets the bonus recalculation work on a scratch copy before installing it.
         *
         * @throws Exception if the world constants cannot be seeded
         */
        @Test
        @DisplayName("a player carrying nothing has no light")
        void emptyHandedPlayerHasNoLight() throws Exception {
            seedWorld();
            PlayerState scratch = new PlayerState();
            scratch.setCurLight(5);

            player.calcLight(scratch, false);

            assertEquals(0, scratch.getCurLight());
            assertNull(player.getPlayerState(), "the player's own state was not installed");
        }

        /**
         * Fills in the world constants the daytime test reads, whatever was there before.
         *
         * <p>The calculation asks whether it is day in the town before it looks at the equipment,
         * and that question needs the day length — so the constants have to be loaded even for a
         * player standing in the dark with nothing. Overwritten rather than filled in only when
         * absent, because another test class may have installed a partial structure with no world
         * block in it.
         *
         * @throws Exception if the constants holder cannot be reached
         */
        private void seedWorld() throws Exception {
            Field data = uk.co.jackoftrades.middle.game.globals.GameConstants.class
                    .getDeclaredField("data");
            data.setAccessible(true);
            savedConstants = data.get(null);

            data.set(null, new uk.co.jackoftrades.middle.game.globals.data.GameConstantsData(
                    null, null, null, null,
                    new uk.co.jackoftrades.middle.game.globals.data.WorldData(
                            128, 10000, 0, 0, 0, 0, 0, 0, 0, 0),
                    null, null, null, null, null, java.util.List.of(),
                    null, java.util.List.of(), null, java.util.List.of(),
                    null, java.util.List.of()));
        }

        /**
         * Puts the constants back.
         *
         * @throws Exception if the constants holder cannot be reached
         */
        @org.junit.jupiter.api.AfterEach
        void restoreWorld() throws Exception {
            if (savedConstants == null) return;

            Field data = uk.co.jackoftrades.middle.game.globals.GameConstants.class
                    .getDeclaredField("data");
            data.setAccessible(true);
            data.set(null, savedConstants);
        }
    }
}
