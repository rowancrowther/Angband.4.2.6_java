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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#playerGenerate(Player, PlayerRace, PlayerClass, boolean)}, the port of
 * C's {@code player_generate} ({@code player-birth.c:980-1028}).
 *
 * <p>The C is a short sequence of assignments, and every expected value below is read off it
 * rather than off the port:
 *
 * <pre>{@code
 * if (!c) c = p->class;
 * if (!r) r = p->race;
 * p->class = c;
 * p->race = r;
 * p->max_lev = p->lev = 1;
 * p->expfact = p->race->r_exp + p->class->c_exp;
 * p->hitdie = p->race->r_mhp + p->class->c_mhp;
 * p->player_hp[0] = p->hitdie;
 * for (i = 1; i < p->lev; i++)
 *         p->player_hp[i] = p->player_hp[i - 1] + p->hitdie;
 * p->mhp = p->player_hp[p->lev - 1];
 * get_ahw(p);
 * p->timed[TMD_FOOD] = PY_FOOD_FULL - 1;
 * if (!old_history) { ...; p->history = get_history(p->race->history); }
 * }</pre>
 *
 * <p><b>The fixture is a Human Warrior</b> — {@code r_mhp} 10 and {@code c_mhp} 9 from
 * {@code p_race.txt} and {@code class.txt}, so a hit die of 19; {@code r_exp} 100 and
 * {@code c_exp} 0, so an experience factor of 100. Both are real records, which means a wrong
 * arithmetic here shows up as a wrong character rather than as a wrong invented number.
 *
 * <p><b>The two clauses worth pressing on.</b> The loop over levels never runs, because the level
 * was set to 1 two lines earlier — so maximum hit points come entirely from the entry the loop
 * skips, and dropping {@code player_hp[0] = hitdie} leaves a character with none. And the method is
 * called again for every choice made on the birth screen ({@code player-birth.c:1099, 1110}), so
 * regeneration after a change of race has to overwrite that entry rather than leave the previous
 * race's behind. Both are tested directly.
 *
 * <p>Age, height and weight are rolled by {@code get_ahw}, which has its own suite; all that is
 * asserted here is that the call happened, by way of a fixture whose ranges cannot include zero.
 *
 * <p>{@code PY_FOOD_FULL} is written into the registry rather than loaded, and restored afterwards,
 * since it is global state the rest of the suite shares.
 *
 * <p>Class PlayerBirthPlayerGenerateTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthPlayerGenerateTest {

    /**
     * The scaled "Fed" threshold a loaded game holds — {@code player_timed.txt}'s 90 times
     * {@code constants.txt}'s food value of 100.
     */
    private static final int PY_FOOD_FULL = 9000;

    /**
     * The Human race's {@code hitdie:} from {@code p_race.txt}.
     */
    private static final int HUMAN_MHP = 10;

    /**
     * The Warrior class's {@code hitdie:} from {@code class.txt}.
     */
    private static final int WARRIOR_MHP = 9;

    /**
     * The threshold as it was before this class ran.
     */
    private static int savedFoodFull;

    /**
     * The character being generated, fresh for each test.
     */
    private Player player;

    /**
     * Puts a known "Fed" threshold in the registry, keeping the old one.
     *
     * @throws Exception if the field cannot be reached
     */
    @BeforeAll
    static void seedFoodThreshold() throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField("PY_FOOD_FULL");
        field.setAccessible(true);
        savedFoodFull = field.getInt(null);
        field.setInt(null, PY_FOOD_FULL);
    }

    /**
     * Puts the threshold back.
     *
     * @throws Exception if the field cannot be reached
     */
    @AfterAll
    static void restoreFoodThreshold() throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField("PY_FOOD_FULL");
        field.setAccessible(true);
        field.setInt(null, savedFoodFull);
    }

    /**
     * Builds a chart with the given entries.
     *
     * @param chartNumber     the chart's own number
     * @param successorNumber the number of the chart to move to next, or zero for none
     * @param rollsAndTexts   alternating cut-off and phrase
     * @return the chart
     */
    private static PlayerHistoryChart chart(int chartNumber, int successorNumber,
                                            Object... rollsAndTexts) {
        PlayerHistoryChart chart = new PlayerHistoryChart(chartNumber, successorNumber);
        for (int i = 0; i < rollsAndTexts.length; i += 2) {
            chart.addEntry(new PlayerHistoryEntry((Integer) rollsAndTexts[i],
                    (String) rollsAndTexts[i + 1]));
        }
        return chart;
    }

    /**
     * Builds a race from the Human record of {@code p_race.txt}, with the hit die, experience factor
     * and background phrase varied so that a test can tell one race's contribution from another's.
     * The age, height and weight bases are the Human's, and none of their ranges can produce zero.
     *
     * @param name    the race's name
     * @param raceMhp the {@code hitdie:} value
     * @param raceExp the {@code exp:} value
     * @param phrase  the single background phrase this race's chart yields
     * @return the race
     */
    private static PlayerRace race(String name, int raceMhp, int raceExp, String phrase) {
        List<EquipSlot> slots = new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        Map<ElementEnum, ElementInfo> resists = new EnumMap<>(ElementEnum.class);
        return new PlayerRace(name, 0, raceMhp, raceExp, 14, 6, 72, 6, 180, 25, 0,
                new PlayerBody("Humanoid", slots),
                new EnumMap<>(Stats.class), new EnumMap<>(PlayerSkill.class),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                chart(1, 0, 100, phrase), resists);
    }

    /**
     * The Human record, as {@code p_race.txt} carries it.
     *
     * @return the race
     */
    private static PlayerRace human() {
        return race("Human", HUMAN_MHP, 100, "You are the first child of a Serf.  ");
    }

    /**
     * Builds a class from the Warrior record of {@code class.txt}, with the hit die and experience
     * factor varied.
     *
     * @param name   the class's name
     * @param hpAdj  the {@code hitdie:} value
     * @param expAdj the value C's {@code c_exp} would hold
     * @return the class
     */
    private static PlayerClass playerClass(String name, int hpAdj, int expAdj) {
        return new PlayerClass(name, new ArrayList<>(List.of("Rookie")),
                new EnumMap<>(Stats.class), new EnumMap<>(PlayerSkill.class),
                new EnumMap<>(PlayerSkill.class), hpAdj, expAdj,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                6, 30, 5, new ArrayList<>(), null);
    }

    /**
     * The Warrior record, as {@code class.txt} carries it.
     *
     * @return the class
     */
    private static PlayerClass warrior() {
        return playerClass("Warrior", WARRIOR_MHP, 0);
    }

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * Reads one of the player's private {@code int} fields, for the two values C sets that the port
     * has no reader for.
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
     * Reads the character's background text, which has a writer but no reader.
     *
     * @return the background text
     * @throws Exception if the field cannot be reached
     */
    private String history() throws Exception {
        Field field = Player.class.getDeclaredField("history");
        field.setAccessible(true);
        return (String) field.get(player);
    }

    /**
     * The straight-through path: a Human Warrior generated from nothing.
     */
    @Nested
    @DisplayName("a freshly generated character")
    class FreshCharacter {

        /**
         * Generates a Human Warrior before each assertion in this group.
         */
        @BeforeEach
        void generate() {
            PlayerBirth.playerGenerate(player, human(), warrior(), false);
        }

        /**
         * Both levels are set to 1 by C's single {@code p->max_lev = p->lev = 1}. They are asserted
         * together because that is how C writes them: a character who begins with a maximum above
         * their current level has had one of the two missed.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("begins at level 1, current and maximum")
        void beginsAtLevelOne() throws Exception {
            assertAll(
                    () -> assertEquals(1, player.getLevel(), "lev"),
                    () -> assertEquals(1, intField("maxLevel"), "max_lev"));
        }

        /**
         * The experience factor is the race's plus the class's. For the shipped data that is
         * 100 + 0, so the Human's figure survives untouched.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("takes the summed experience factor")
        void takesExperienceFactor() throws Exception {
            assertEquals(100, intField("expFact"));
        }

        /**
         * The hit die is the race's plus the class's: 10 + 9.
         */
        @Test
        @DisplayName("takes the summed hit die")
        void takesHitDie() {
            assertEquals(HUMAN_MHP + WARRIOR_MHP, player.getHitDie());
        }

        /**
         * The level-1 entry of the hit point table is the whole hit die — a character does not roll
         * for their first level. This is C's {@code p->player_hp[0] = p->hitdie}.
         */
        @Test
        @DisplayName("seeds the hit point table with the whole die")
        void seedsTheHitPointTable() {
            assertEquals(19, player.getPlayerHP(0));
        }

        /**
         * Maximum hit points are read back out of that entry, so they equal the die. A character
         * whose table entry was never written would arrive here with nothing.
         */
        @Test
        @DisplayName("starts with the die's worth of hit points")
        void startsWithHitPoints() {
            assertEquals(19, player.getMaxHP());
        }

        /**
         * The overestimate loop runs from 1 up to the level, which is 1, so it never executes and
         * the rest of the table stays empty. The real figures are {@link PlayerBirth#rollHP} 's
         * job; filling them here would let a player reroll by resetting the birth screen.
         */
        @Test
        @DisplayName("leaves the rest of the table unfilled")
        void leavesTheRestUnfilled() {
            assertAll(
                    () -> assertEquals(0, player.getPlayerHP(1), "level 2"),
                    () -> assertEquals(0, player.getPlayerHP(49), "level 50"));
        }

        /**
         * The character starts one point below a full stomach, which is C's
         * {@code PY_FOOD_FULL - 1} exactly — not a rounded figure and not the maximum.
         */
        @Test
        @DisplayName("starts one point short of well fed")
        void startsWellFed() {
            assertEquals(PY_FOOD_FULL - 1, player.getTimedEffect(TimedEffect.TMD_FOOD));
        }

        /**
         * {@code get_ahw} ran. The fixture's ranges start at 14, 72 and 180 with no negative
         * modifier reaching zero, so a non-zero value in all three is evidence the call was made
         * rather than evidence of any particular roll.
         */
        @Test
        @DisplayName("rolls age, height and weight")
        void rollsAgeHeightWeight() {
            assertAll(
                    () -> assertTrue(intField("age") > 0, "age " + intField("age")),
                    () -> assertTrue(player.getHeight() > 0, "height " + player.getHeight()),
                    () -> assertTrue(player.getWeight() > 0, "weight " + player.getWeight()));
        }

        /**
         * The background is generated from the race's chart. The fixture chart has one entry, so
         * the roll has nothing to choose between and the phrase is fixed.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("generates a background from the race's chart")
        void generatesBackground() throws Exception {
            assertEquals("You are the first child of a Serf.  ", history());
        }
    }

    /**
     * How the character takes on the race and class it was handed.
     */
    @Nested
    @DisplayName("race and class")
    class RaceAndClass {

        /**
         * Both are recorded on the character, which is what everything derived below them reads
         * from.
         */
        @Test
        @DisplayName("are recorded on the character")
        void areRecorded() {
            PlayerBirth.playerGenerate(player, human(), warrior(), false);

            assertAll(
                    () -> assertEquals("Human", player.getRace().getName()),
                    () -> assertEquals(WARRIOR_MHP, player.getPlayerClass().getMaxHitDie()));
        }

        /**
         * The character gets copies rather than the definitions themselves. C assigns the pointer
         * to the shared record; the port gives each character its own, so nothing a character
         * accumulates can reach back into the registry's data.
         */
        @Test
        @DisplayName("are copied, not shared")
        void areCopied() {
            PlayerRace race = human();
            PlayerClass playerClass = warrior();
            PlayerBirth.playerGenerate(player, race, playerClass, false);

            assertAll(
                    () -> assertNotSame(race, player.getRace()),
                    () -> assertNotSame(playerClass, player.getPlayerClass()));
        }

        /**
         * A null class means "keep the one the character has", which is how C's race-choice caller
         * works — {@code player_generate(player, player_id2race(choice), NULL, false)}
         * ({@code player-birth.c:1099}). The kept class still contributes to the recomputed hit
         * die, so the assertion is on the arithmetic and not only on the name.
         */
        @Test
        @DisplayName("a null class keeps the character's own")
        void nullClassKeepsOwn() {
            PlayerBirth.playerGenerate(player, human(), warrior(), false);
            PlayerBirth.playerGenerate(player, race("Hobbit", 7, 120, "x"), null, false);

            assertAll(
                    () -> assertEquals(WARRIOR_MHP, player.getPlayerClass().getMaxHitDie()),
                    () -> assertEquals("Hobbit", player.getRace().getName()),
                    () -> assertEquals(7 + WARRIOR_MHP, player.getHitDie()));
        }

        /**
         * A null race means the same the other way about, which is the class-choice caller
         * ({@code player-birth.c:1110}).
         */
        @Test
        @DisplayName("a null race keeps the character's own")
        void nullRaceKeepsOwn() {
            PlayerBirth.playerGenerate(player, human(), warrior(), false);
            PlayerBirth.playerGenerate(player, null, playerClass("Mage", 0, 30), false);

            assertAll(
                    () -> assertEquals("Human", player.getRace().getName()),
                    () -> assertEquals(0, player.getPlayerClass().getMaxHitDie()),
                    () -> assertEquals(HUMAN_MHP, player.getHitDie()));
        }

        /**
         * A null player is refused. C has no such guard — it would dereference and crash — so this
         * is the port turning undefined behaviour into a stated failure.
         */
        @Test
        @DisplayName("a null player is refused")
        void nullPlayerRefused() {
            assertThrows(RuntimeException.class,
                    () -> PlayerBirth.playerGenerate(null, human(), warrior(), false));
        }
    }

    /**
     * The birth screen calls this repeatedly, so the second call is a case in its own right.
     */
    @Nested
    @DisplayName("regenerating an already generated character")
    class Regeneration {

        /**
         * Every derived figure is replaced, not accumulated. Switching a Human Warrior to a Hobbit
         * Mage has to give a die of 7 and a factor of 120, not a running total of both characters.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("replaces the derived figures")
        void replacesDerivedFigures() throws Exception {
            PlayerBirth.playerGenerate(player, human(), warrior(), false);
            PlayerBirth.playerGenerate(player, race("Hobbit", 7, 120, "y"),
                    playerClass("Mage", 0, 0), false);

            assertAll(
                    () -> assertEquals(7, player.getHitDie(), "hitdie"),
                    () -> assertEquals(120, intField("expFact"), "expfact"),
                    () -> assertEquals(1, player.getLevel(), "lev"));
        }

        /**
         * The level-1 table entry is rewritten with the new die. This is the clause the port
         * originally lacked: without it the second call would leave the Warrior's 19 in place and
         * hand the Hobbit Mage a stranger's hit points.
         */
        @Test
        @DisplayName("rewrites the level-1 hit point entry")
        void rewritesTheTableEntry() {
            PlayerBirth.playerGenerate(player, human(), warrior(), false);
            PlayerBirth.playerGenerate(player, race("Hobbit", 7, 120, "y"),
                    playerClass("Mage", 0, 0), false);

            assertAll(
                    () -> assertEquals(7, player.getPlayerHP(0), "player_hp[0]"),
                    () -> assertEquals(7, player.getMaxHP(), "mhp"));
        }

        /**
         * The entry is also rewritten over a table that {@code rollHP} has already filled in, which
         * is the state a character is in if the birth screen is revisited. Only index zero is C's
         * to touch here; the rolled entries above it are left alone until {@code roll_hp} runs
         * again.
         */
        @Test
        @DisplayName("overwrites a rolled table entry without disturbing the rest")
        void overwritesARolledEntry() {
            PlayerBirth.playerGenerate(player, human(), warrior(), false);
            player.setPlayerHitpoint(0, 19);
            player.setPlayerHitpoint(1, 31);

            PlayerBirth.playerGenerate(player, race("Hobbit", 7, 120, "y"),
                    playerClass("Mage", 0, 0), false);

            assertAll(
                    () -> assertEquals(7, player.getPlayerHP(0), "player_hp[0] replaced"),
                    () -> assertEquals(31, player.getPlayerHP(1), "player_hp[1] untouched"));
        }
    }

    /**
     * The {@code old_history} flag, which is the one thing the method can be asked not to do.
     */
    @Nested
    @DisplayName("the old-history flag")
    class OldHistory {

        /**
         * With the flag set the existing text is left standing. This is the quickstart path
         * ({@code player-birth.c:1042}), where a restored character's background must not be
         * silently rerolled.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("keeps the background already there")
        void keepsTheBackground() throws Exception {
            player.setPlayerHistory("a background worth keeping");
            PlayerBirth.playerGenerate(player, human(), warrior(), true);

            assertEquals("a background worth keeping", history());
        }

        /**
         * With the flag clear the text is replaced, even when there is already one. Choosing a new
         * race on the birth screen has to give the character that race's background.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("replaces the background when clear")
        void replacesTheBackground() throws Exception {
            player.setPlayerHistory("a background worth keeping");
            PlayerBirth.playerGenerate(player, human(), warrior(), false);

            assertEquals("You are the first child of a Serf.  ", history());
        }

        /**
         * The flag governs the background and nothing else: everything derived above it is still
         * recomputed. C's guard wraps only the history assignment.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("does not hold back the rest of the generation")
        void doesNotHoldBackTheRest() throws Exception {
            PlayerBirth.playerGenerate(player, human(), warrior(), true);

            assertAll(
                    () -> assertEquals(19, player.getHitDie(), "hitdie"),
                    () -> assertEquals(19, player.getMaxHP(), "mhp"),
                    () -> assertEquals(100, intField("expFact"), "expfact"),
                    () -> assertSame(null, history(), "history untouched"));
        }
    }
}
