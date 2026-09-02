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
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
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

/**
 * Tests the five writers {@code player_generate} uses to stamp a character's identity onto a
 * player: {@link Player#setRace}, {@link Player#setClass}, {@link Player#setMaxLevel},
 * {@link Player#setLevel} and {@link Player#setExpFact}.
 *
 * <p>Together they are the port of five consecutive lines of C:
 *
 * <pre>
 *     p-&gt;class = c;                                        player-birth.c:989
 *     p-&gt;race  = r;                                        player-birth.c:990
 *     p-&gt;max_lev = p-&gt;lev = 1;                             player-birth.c:993
 *     p-&gt;expfact = p-&gt;race-&gt;r_exp + p-&gt;class-&gt;c_exp;    player-birth.c:997
 * </pre>
 *
 * <p>C assigns; there is no arithmetic to check and no branch to cover, so the expected values are
 * the ones those lines produce and the interesting question is what the writers do <em>not</em> do.
 * Three things are worth pinning down.
 *
 * <p>First, storage identity. C aliases the registry's read-only record, while the port expects the
 * caller to pass a private copy — so the writer must store the instance it was handed and not copy
 * it again, or a caller could no longer tell which object the player holds.
 *
 * <p>Second, independence. The five fields sit next to each other on {@link Player} and four of them
 * are small integers with similar names; a crossed assignment would compile and read plausibly.
 *
 * <p>Third, that no invariant is smuggled in. C's {@code lev} and {@code max_lev} part company as
 * soon as experience is drained, so a writer that quietly clamped one to the other would break
 * draining rather than protect it.
 *
 * <p>{@code maxLevel} and {@code expFact} have no accessors, so they are read by reflection, as
 * {@code PlayerScalarStateTest} writes fields the same way.
 *
 * <p>Class PlayerIdentityAccessorTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerIdentityAccessorTest {

    /**
     * The player under test, fresh for each test since all of this is mutable.
     */
    private Player player;

    /**
     * Builds a race carrying the given experience factor, otherwise the Human record of
     * {@code p_race.txt}.
     *
     * @param name    the race's name
     * @param raceExp the {@code exp:} value from {@code p_race.txt}
     * @return a race fit to be handed to {@link Player#setRace}
     */
    private static PlayerRace race(String name, int raceExp) {
        List<EquipSlot> slots = new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        Map<ElementEnum, ElementInfo> resists = new EnumMap<>(ElementEnum.class);
        return new PlayerRace(name, 0, 10, raceExp, 14, 6, 69, 10, 165, 35, 0,
                new PlayerBody("Humanoid", slots),
                new EnumMap<>(Stats.class), new EnumMap<>(PlayerSkill.class),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                new PlayerHistoryChart(1, 2), resists);
    }

    /**
     * Builds a class carrying the given experience factor, otherwise the Warrior record of
     * {@code class.txt}.
     *
     * @param name   the class's name
     * @param expAdj the value C's {@code c_exp} would hold; zero for every shipped class
     * @return a class fit to be handed to {@link Player#setClass}
     */
    private static PlayerClass playerClass(String name, int expAdj) {
        return new PlayerClass(name, new ArrayList<>(List.of("Rookie")),
                new EnumMap<>(Stats.class), new EnumMap<>(PlayerSkill.class),
                new EnumMap<>(PlayerSkill.class), 9, expAdj,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                6, 30, 5, new ArrayList<>(), null);
    }

    /**
     * A new player, as the constructor leaves one — already holding the registry's first race, which
     * is what makes the "the writer replaced it" assertions meaningful.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Reads one of the player's private fields, for the two with no accessor.
     *
     * @param name the field's name
     * @return the field's value
     * @throws ReflectiveOperationException if the field has been renamed
     */
    private int intField(String name) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * The two reference writers, which stand in for C's two pointer assignments.
     */
    @Nested
    @DisplayName("race and class")
    class RaceAndClass {

        /**
         * The player holds the very object it was given. The port's copies are made by the caller —
         * {@code PlayerRace.copy} and {@code PlayerClass.copy} — so a writer that copied again would
         * leave the caller holding an object the player does not have, and the birth screen's
         * re-roll could no longer reach the character's own record.
         */
        @Test
        @DisplayName("store the instance given, without copying it")
        void storeTheGivenInstance() {
            PlayerRace highElf = race("High-Elf", 145);
            PlayerClass mage = playerClass("Mage", 0);

            player.setRace(highElf);
            player.setClass(mage);

            assertAll(
                    () -> assertSame(highElf, player.getRace()),
                    () -> assertSame(mage, player.getPlayerClass()));
        }

        /**
         * A character keeps its class when it changes race and the other way about. C writes the two
         * on consecutive lines from separate arguments, and {@code player_generate} is called for a
         * change of either.
         */
        @Test
        @DisplayName("are independent of one another")
        void areIndependent() {
            PlayerRace human = race("Human", 100);
            PlayerClass warrior = playerClass("Warrior", 0);
            player.setRace(human);
            player.setClass(warrior);

            PlayerRace dwarf = race("Dwarf", 120);
            player.setRace(dwarf);

            assertAll(
                    () -> assertSame(dwarf, player.getRace()),
                    () -> assertNotSame(human, player.getRace()),
                    () -> assertSame(warrior, player.getPlayerClass(),
                            "the class was not disturbed"));
        }

        /**
         * Neither writer recomputes anything. C's {@code player_generate} goes on to reset the
         * level, the factor and the hit dice in the same call, but those are separate writers in the
         * port — so a class change made through this method alone leaves the old factor in place,
         * and the caller is responsible for the rest of the sequence.
         */
        @Test
        @DisplayName("do not refresh the values C derives from them")
        void deriveNothing() throws ReflectiveOperationException {
            player.setRace(race("Human", 100));
            player.setClass(playerClass("Warrior", 0));
            player.setExpFact(100);

            player.setRace(race("High-Elf", 145));

            assertEquals(100, intField("expFact"),
                    "the factor still describes the previous race until setExpFact is called");
        }
    }

    /**
     * The level pair, which C sets together at birth and lets diverge afterwards.
     */
    @Nested
    @DisplayName("level and maximum level")
    class Levels {

        /**
         * Birth writes both to 1, which is C's {@code p->max_lev = p->lev = 1}.
         */
        @Test
        @DisplayName("birth leaves both at 1")
        void birthValues() throws ReflectiveOperationException {
            player.setMaxLevel(1);
            player.setLevel(1);

            assertAll(
                    () -> assertEquals(1, player.getLevel()),
                    () -> assertEquals(1, intField("maxLevel")));
        }

        /**
         * They are two counters, not one. Writing the current level leaves the maximum where it was,
         * which is what lets a drained character keep the record of what it earned.
         */
        @Test
        @DisplayName("are separate counters")
        void areSeparateCounters() throws ReflectiveOperationException {
            player.setMaxLevel(20);
            player.setLevel(20);

            player.setLevel(14);

            assertAll(
                    () -> assertEquals(14, player.getLevel()),
                    () -> assertEquals(20, intField("maxLevel"),
                            "draining experience does not lower the high-water mark"));
        }

        /**
         * Nothing clamps the current level to the maximum on the way in. A level above the recorded
         * maximum simply leaves the two out of step, and {@link Player#updateMaxLevel} is what pulls
         * the maximum up — C does the same, raising {@code max_lev} where the level changes rather
         * than inside the assignment.
         */
        @Test
        @DisplayName("a level above the maximum is stored, and updateMaxLevel reconciles it")
        void noClampOnTheWayIn() throws ReflectiveOperationException {
            player.setMaxLevel(5);
            player.setLevel(9);

            assertAll(
                    () -> assertEquals(9, player.getLevel()),
                    () -> assertEquals(5, intField("maxLevel"), "not raised by setLevel"));

            player.updateMaxLevel();

            assertEquals(9, intField("maxLevel"), "raised to meet the level");
        }

        /**
         * The maximum is written directly too, for the savefile-load path, and does not drag the
         * current level with it.
         */
        @Test
        @DisplayName("setting the maximum leaves the current level alone")
        void maximumDoesNotMoveTheLevel() throws ReflectiveOperationException {
            player.setLevel(7);
            player.setMaxLevel(30);

            assertAll(
                    () -> assertEquals(7, player.getLevel()),
                    () -> assertEquals(30, intField("maxLevel")));
        }
    }

    /**
     * The experience factor, and the birth sequence that produces it.
     */
    @Nested
    @DisplayName("experience factor")
    class ExpFactor {

        /**
         * The writer stores what it is given; it does not compute the sum itself. C computes the sum
         * at the point of assignment, so the addition is the caller's in the port.
         */
        @Test
        @DisplayName("stores the value it is given")
        void storesTheValue() throws ReflectiveOperationException {
            player.setExpFact(120);

            assertEquals(120, intField("expFact"));
        }

        /**
         * The whole of C's five lines, run in order for two characters at opposite ends of the
         * shipped range. The factors are those of {@code p_race.txt} plus a class contribution of
         * zero, because no class in {@code class.txt} carries an {@code exp:} line.
         */
        @Test
        @DisplayName("the birth sequence reproduces player_generate")
        void birthSequence() throws ReflectiveOperationException {
            PlayerRace human = race("Human", 100);
            PlayerClass warrior = playerClass("Warrior", 0);

            player.setClass(warrior);
            player.setRace(human);
            player.setMaxLevel(1);
            player.setLevel(1);
            player.setExpFact(human.getExpFactor() + warrior.getExpFactor());

            assertAll(
                    () -> assertSame(human, player.getRace()),
                    () -> assertSame(warrior, player.getPlayerClass()),
                    () -> assertEquals(1, player.getLevel()),
                    () -> assertEquals(1, intField("maxLevel")),
                    () -> assertEquals(100, intField("expFact"),
                            "a Human Warrior levels at the table price"));

            PlayerRace highElf = race("High-Elf", 145);
            PlayerClass mage = playerClass("Mage", 0);

            player.setClass(mage);
            player.setRace(highElf);
            player.setExpFact(highElf.getExpFactor() + mage.getExpFactor());

            assertEquals(145, intField("expFact"),
                    "a High-Elf Mage pays 45% more for every level");
        }

        /**
         * The factor survives a level change, and a level change survives a re-rolled factor. They
         * are read together by the level arithmetic, which is exactly why a crossed write here would
         * be hard to spot later.
         */
        @Test
        @DisplayName("is independent of the level pair")
        void independentOfLevels() throws ReflectiveOperationException {
            player.setExpFact(145);
            player.setMaxLevel(11);
            player.setLevel(11);

            player.setExpFact(100);

            assertAll(
                    () -> assertEquals(100, intField("expFact")),
                    () -> assertEquals(11, player.getLevel()),
                    () -> assertEquals(11, intField("maxLevel")));
        }
    }
}
