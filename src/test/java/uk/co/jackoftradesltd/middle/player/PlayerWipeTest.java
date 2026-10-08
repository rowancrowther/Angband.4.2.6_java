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
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#wipe()} and the constructor it repeats, against the C they stand in for:
 * the {@code memset(p, 0, sizeof(struct player))} at the head of {@code player_init}
 * ({@code player-birth.c}) and the allocations of {@code init_player} ({@code player.c}).
 *
 * <p>The expected values are C's zero, not whatever the Java happens to leave. A struct wiped by
 * {@code memset} has every number at 0, every flag false and every pointer null, so each field
 * below is dirtied first and then required to read as that. The three things the Java does not
 * leave at zero are the ones it initialises on purpose, and each is asserted by name: the race
 * (the first loaded one, which {@code player_init} assigns straight afterwards), the body (copied
 * from the registry) and the timed-effect and stat tables (full, every slot at zero or at itself).
 *
 * <p>The exception paths are the port's own: C assigns {@code p->race = races} without looking at
 * it, so there is no C behaviour for a missing race to be compared against, and the tests only pin
 * the contract in the Javadoc.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerWipeTest {

    /**
     * The player under test, built before each test and dirtied by {@link #dirty()}.
     */
    private Player player;

    /**
     * A fresh player for each test, since the point is to compare one before and after a wipe.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Reads one of the player's private fields, for the state with no getter.
     *
     * @param name the field's name
     * @return the field's value
     * @throws Exception if the field cannot be reached
     */
    private Object read(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(player);
    }

    /**
     * Writes one of the player's private fields, for the state with no setter.
     *
     * @param name  the field's name
     * @param value the value to store
     * @throws Exception if the field cannot be reached
     */
    private void write(String name, Object value) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * Puts a non-zero value in every field that a played character would have changed, so that a
     * field the wipe forgets shows up as a failure and not as a coincidence of both being zero.
     *
     * @throws Exception if a field cannot be reached
     */
    private void dirty() throws Exception {
        player.setAge(31);
        player.setHeight(70);
        player.setWeight(180);
        player.setHeightBirth(69);
        player.setWeightBirth(175);
        player.setAU(1234L);
        player.setAUBirth(600L);
        player.setLevel(20);
        player.setMaxLevel(22);
        player.setExp(9000L);
        player.setMaxExp(9500L);
        player.setCurrentHP(80);
        player.setPlayerMaxHP(100);
        player.setChpFrac(7);
        player.setCurSp(12);
        player.setMaxSP(30);
        player.setCspFrac(9);
        player.setDepth(15);
        player.setEnergy(77);
        player.setTotalEnergy(5000);
        player.setRestingTurn(40);
        player.setSkipCmdCoercion(2);
        player.setHitDie(19);
        player.setExpFact(150);
        player.setFullName("Dirty Hero");
        player.setHistoryBirth("A long and tedious past.");
        player.setIsDead(true);
        player.putTimed(TimedEffect.TMD_IMAGE, 25);
        player.putTimed(TimedEffect.TMD_FOOD, 10000);
        player.setItemKnowledge(new KnownObject());
        player.setPlayerHitpoint(0, 10);
        player.setPlayerHitpoint(19, 190);
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            player.setStatMax(stat, 17);
            player.setCurrStatValue(stat, 15);
            player.setStatBirth(stat, 16);
        }
        player.setCurrStatMap(Stats.STAT_STR, Stats.STAT_DEX);

        write("maxDepth", 30);
        write("recallDepth", 25);
        write("wordRecall", 11);
        write("deepDescent", 4);
        write("expFrac", 3);
        write("food", 5000);
        write("unignoring", 1);
        write("diedFrom", "a trap");
        write("noScore", 2);
        write("totalWinner", true);
        write("isWizard", true);
        write("oldGrid", Loc.zero.offset(4, 5));
        write("grid", Loc.zero.offset(6, 7));
        write("spellFlags", new ArrayList<>(List.of(1, 2, 3)));
        write("spellOrder", new ArrayList<>(List.of(4, 5, 6)));
    }

    /**
     * Everything C's {@code memset} zeroes: the numbers, the flags and the strings.
     */
    @Nested
    @DisplayName("members C's memset zeroes")
    class Zeroed {

        /**
         * The character's identity: name, history, age, build and cause of death all go back to
         * unset. A null string rather than an empty one, because C's pointers are null after the
         * memset and the port tests for a missing history with {@code null}.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("identity: name, history, age, height, weight and birth copies")
        void identityIsCleared() throws Exception {
            dirty();
            player.wipe();

            assertNull(player.getFullName());
            assertNull(player.getHistoryBirth());
            assertNull(read("diedFrom"));
            assertEquals(0, player.getAge());
            assertEquals(0, player.getHeight());
            assertEquals(0, player.getWeight());
            assertEquals(0, player.getHeightBirth());
            assertEquals(0, player.getWeightBirth());
            assertEquals(0L, player.getAUBirth());
            assertEquals(0, player.getHitDie());
            assertEquals(0, player.getExpFact());
        }

        /**
         * Level, experience and the fractions that go with them. {@code max_lev} and
         * {@code max_exp} are cleared too, not only the working values.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("progression: levels, experience and the fraction")
        void progressionIsCleared() throws Exception {
            dirty();
            player.wipe();

            assertEquals(0, player.getLevel());
            assertEquals(0, player.getMaxLevel());
            assertEquals(0L, player.getExp());
            assertEquals(0L, player.getMaxExp());
            assertEquals(0, read("expFrac"));
        }

        /**
         * Hit points and spell points, each with its current, maximum and fractional part.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("vitals: hit points, spell points and their fractions")
        void vitalsAreCleared() throws Exception {
            dirty();
            player.wipe();

            assertEquals(0, player.getCurrentHP());
            assertEquals(0, player.getMaxHP());
            assertEquals(0, read("chpFrac"));
            assertEquals(0, player.getCurSp());
            assertEquals(0, player.getMaxSP());
            assertEquals(0, read("cspFrac"));
        }

        /**
         * Gold, the three depths and the countdowns that tick towards a depth change.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("position and purse: gold, depths and the recall and descent counters")
        void positionAndPurseAreCleared() throws Exception {
            dirty();
            player.wipe();

            assertEquals(0L, player.getAU());
            assertEquals(0, player.getDepth());
            assertEquals(0, player.getMaxDepth());
            assertEquals(0, player.getRecallDepth());
            assertEquals(0, player.getWordRecall());
            assertEquals(0, player.getDeepDescent());
            assertEquals(Loc.zero, player.getGrid());
            assertEquals(Loc.zero, read("oldGrid"));
        }

        /**
         * Energy and the counters the turn loop keeps, including the two that have no getter.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("turn counters: energy, totals, resting, food and the coercion skip")
        void countersAreCleared() throws Exception {
            dirty();
            player.wipe();

            assertEquals(0, player.getEnergy());
            assertEquals(0, player.getTotalEnergy());
            assertEquals(0, read("restingTurn"));
            assertEquals(0, read("food"));
            assertEquals(0, player.getSkipCmdCoercion());
            assertEquals(0, player.isUnignoring());
        }

        /**
         * The flags: dead, wizard, winner and the cheating word all go back to their clean values.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("flags: dead, wizard, total winner and noscore")
        void flagsAreCleared() throws Exception {
            dirty();
            player.wipe();

            assertFalse(player.isDead());
            assertFalse(player.isWizard());
            assertFalse(player.isWinner());
            assertEquals(0, read("noScore"));
        }

        /**
         * The rolled hit-point table is an array, so a wipe that merely re-pointed it would pass
         * the other tests; every entry is checked, including the two that were dirtied and the one
         * past C's last that the port allocates.
         */
        @Test
        @DisplayName("the rolled hit-point table is zero throughout")
        void hitPointTableIsCleared() throws Exception {
            dirty();
            player.wipe();

            for (int i = 0; i <= PlayerRegistry.PY_MAX_LEVEL; i++) {
                assertEquals(0, player.getPlayerHP(i), "player_hp[" + i + "]");
            }
        }

        /**
         * The five stats, in all three places C keeps them: the maximal and natural values, and the
         * birth copy. The remapping table is not zero - it is the identity, which is what the
         * constructor builds and what an unscrambled character has.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("stats: maximal, natural and birth values are zero, the map is the identity")
        @SuppressWarnings("unchecked")
        void statsAreCleared() throws Exception {
            dirty();
            player.wipe();

            HashMap<Stats, Stats> map = (HashMap<Stats, Stats>) read("statMap");
            for (Stats stat : Stats.values()) {
                if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
                assertEquals(0, player.getMaxStatValue(stat), stat + " maximal");
                assertEquals(0, player.getCurStatValue(stat), stat + " natural");
                assertEquals(0, player.getStatBirth(stat), stat + " birth");
                assertSame(stat, map.get(stat), stat + " should map to itself");
            }
        }

        /**
         * The two spell-tracking lists are emptied, as C frees and nulls them.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("spell lists are emptied")
        void spellListsAreCleared() throws Exception {
            dirty();
            player.wipe();

            assertTrue(((List<?>) read("spellFlags")).isEmpty());
            assertTrue(((List<?>) read("spellOrder")).isEmpty());
        }
    }

    /**
     * The members the wipe rebuilds rather than zeroes, where "null" is not C's answer either: C
     * follows the memset with allocations, and the port makes them here.
     */
    @Nested
    @DisplayName("members rebuilt in place")
    class Rebuilt {

        /**
         * {@code init_player} allocates a fresh timed-effect table with every slot at zero, so an
         * effect that was running before the wipe must read as not running, and every effect must
         * have a slot - the lookup code treats a missing key as a half-built character.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the timed-effect table is new, full, and all zero")
        void timedTableIsFullAndZero() throws Exception {
            dirty();
            assertEquals(25, player.getTimedEffect(TimedEffect.TMD_IMAGE), "the fixture did not dirty it");

            player.wipe();

            for (TimedEffect effect : TimedEffect.values()) {
                assertTrue(player.playerTimedContains(effect), effect + " has no slot");
                assertEquals(0, player.getTimedEffect(effect), effect + " should not be running");
            }
        }

        /**
         * The gear piles, the upkeep and the history ledger are replaced, not emptied: a reference
         * held from before the wipe must no longer be the player's.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("gear, known gear, upkeep, history and quests are fresh objects")
        void containersAreReplaced() throws Exception {
            var gear = player.getGear();
            var gearKnown = player.getGearKnown();
            var upkeep = player.getPlayerUpkeep();
            var history = player.getPlayerHistory();
            player.setQuests(new ArrayList<>(List.of(new Quest(0, "Stale", 1, null, 0, 1))));

            player.wipe();

            assertNotSame(gear, player.getGear());
            assertNotSame(gearKnown, player.getGearKnown());
            assertNotSame(upkeep, player.getPlayerUpkeep());
            assertNotSame(history, player.getPlayerHistory());
            assertTrue(player.getGear().isEmpty());
            assertTrue(player.getGearKnown().isEmpty());
            assertTrue(player.getQuests().isEmpty());
        }

        /**
         * The body is copied from the registry's first body, so the wiped player owns one of its own
         * and the registry's template is untouched.
         */
        @Test
        @DisplayName("the body is a private copy of the registry's first body")
        void bodyIsPrivateCopy() {
            var before = player.getPlayerBody();

            player.wipe();

            assertNotSame(before, player.getPlayerBody());
            assertNotSame(PlayerRegistry.lookupPlayerBody(0), player.getPlayerBody());
        }

        /**
         * C's {@code init_player} calls {@code options_init_defaults}, and {@code player_init} puts
         * the saved options back afterwards. {@code wipe} is the first half only, so a customised
         * option is lost here and it is {@code PlayerBirth.playerInit}'s job to restore it - which
         * {@code PlayerBirthPlayerInitTest} checks.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("options go back to the defaults; restoring them is playerInit's job")
        @SuppressWarnings("unchecked")
        void optionsGoBackToDefault() throws Exception {
            Field optionFlags = PlayerOptions.class.getDeclaredField("options");
            optionFlags.setAccessible(true);
            ((Flag<PlayerOptionEnum>) optionFlags.get(player.getPlayerOptions())).off(PlayerOptionEnum.OP_pickup_inven);
            assertFalse(player.opt(PlayerOptionEnum.OP_pickup_inven), "the fixture did not change it");

            player.wipe();

            assertTrue(player.opt(PlayerOptionEnum.OP_pickup_inven));
        }
    }

    /**
     * The members left unset on purpose: C leaves them null after the memset and {@code player_init}
     * assigns them afterwards, so a wiped player has none until that runs.
     */
    @Nested
    @DisplayName("members left for playerInit to assign")
    class LeftForPlayerInit {

        /**
         * Class, shape, calculated state, known state, object knowledge and the remembered level are
         * all null after a wipe. The object knowledge is the only one the fixture can dirty first,
         * since the others need registry data the test does not load; the rest are null from
         * construction and the wipe must leave them so. The race is the exception: it is the first
         * one loaded, so a wiped player always has one.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("class, shape, state, known state and object knowledge are null")
        void unassignedMembersAreNull() throws Exception {
            dirty();
            assertNotNull(player.getItemKnowledge(), "the fixture did not dirty it");

            player.wipe();

            assertNull(player.getPlayerClass());
            assertNull(player.getShape());
            assertNull(player.getPlayerState());
            assertNull(player.getKnownState());
            assertNull(player.getItemKnowledge());
            assertNull(player.getCave());
        }

        /**
         * The wipe clears the race, as C's {@code memset} in {@code player_init} does; setting
         * {@code p->race = races} is {@code playerInit}'s job, after the wipe.
         */
        @Test
        @DisplayName("the race is cleared")
        void raceIsCleared() {
            player.wipe();

            assertNull(player.getRace());
        }

        /**
         * A wiped player with no shape is not shapechanged: the null guard in {@code isShapeChanged}
         * is the port's, where C would dereference a missing shape, and this is the state it exists
         * for.
         */
        @Test
        @DisplayName("a wiped player is not shapechanged, though it has no shape")
        void wipedPlayerIsNotShapechanged() {
            player.wipe();

            assertNull(player.getShape());
            assertFalse(player.isShapeChanged());
        }
    }

    /**
     * The one failure the port adds to C's: no player race loaded. C points {@code p->race} at the
     * head of its race list without looking, so there is nothing to compare against, and these
     * tests pin the contract the Javadoc states.
     */
    @Nested
    @DisplayName("no player race loaded")
    class NoRaces {

        /**
         * Replaces the registry's race list for the duration of one action and puts it back.
         *
         * @param races  the list to install
         * @param action what to run while it is installed
         * @throws Exception if the registry cannot be reached
         */
        private void withRaces(List<PlayerRace> races, Runnable action) throws Exception {
            Field field = PlayerRegistry.class.getDeclaredField("playerRaces");
            field.setAccessible(true);
            Object saved = field.get(null);
            try {
                PlayerRegistry.setPlayerRaces(races);
                action.run();
            } finally {
                field.set(null, saved);
            }
        }

        /**
         * C's {@code init_player} reads no race, so building a player does not depend on the race
         * list; the race is simply left unset.
         */
        @Test
        @DisplayName("the constructor builds a raceless player even when the race list is empty")
        void constructorToleratesAnEmptyList() throws Exception {
            withRaces(List.of(), () -> assertNull(new Player().getRace()));
        }

        /**
         * Nor does C's {@code memset} in {@code player_init}; the wipe clears the race without
         * looking at the list.
         */
        @Test
        @DisplayName("wipe clears the race even when the race list is empty")
        void wipeToleratesAnEmptyList() throws Exception {
            withRaces(List.of(), () -> {
                player.wipe();
                assertNull(player.getRace());
            });
        }
    }
}
