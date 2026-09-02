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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link PlayerRace#getMaxHitDie()}, {@link PlayerClass#getMaxHitDie()} and
 * {@link Player#setHitDie(int)} — the two halves of C's
 * {@code p->hitdie = p->race->r_mhp + p->class->c_mhp} ({@code player-birth.c:1000}) and the field
 * the sum lands in.
 *
 * <p>The expected values are taken from the C data files rather than from the port's parsers. The
 * race figures are the {@code hitdie:} lines of {@code lib/gamedata/p_race.txt} — Hobbit 7, Human
 * 10, Half-Troll 12, which are the lowest, the commonest and the highest in the file — and the
 * class figures are the {@code hitdie:} lines of {@code lib/gamedata/class.txt}, where Mage is 0
 * and Warrior 9, the two ends of that range.
 *
 * <p>Those two ranges are worth stating side by side, because they are what makes the addition
 * interesting: the race band is narrow (7 to 12) and the class band is wide relative to it (0 to
 * 9), so the class chosen moves the die further than the race does. A Hobbit Mage is a die of 7
 * and a Half-Troll Warrior a die of 21 — three times the size, from data that never multiplies
 * anything.
 *
 * <p>Zero is tested on the class side because C ships it: a Mage really does contribute nothing,
 * so an accessor that quietly substituted a floor would be wrong for a real character rather than
 * for an invented one.
 *
 * <p>Class PlayerHitDieTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerHitDieTest {

    /**
     * The character the die is written to, fresh for each test.
     */
    private Player player;

    /**
     * Builds a race carrying the given hit-die contribution, with the remaining fields filled from
     * the Human record of {@code p_race.txt} so the fixture is a real record rather than zeroes.
     *
     * @param name    the race's name
     * @param raceMhp the {@code hitdie:} value from {@code p_race.txt}
     * @return a race whose only interesting field is its hit-die contribution
     */
    private static PlayerRace race(String name, int raceMhp) {
        List<EquipSlot> slots = new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        Map<ElementEnum, ElementInfo> resists = new EnumMap<>(ElementEnum.class);
        return new PlayerRace(name, 0, raceMhp, 100, 14, 6, 69, 10, 165, 35, 0,
                new PlayerBody("Humanoid", slots),
                new EnumMap<>(Stats.class), new EnumMap<>(PlayerSkill.class),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                new PlayerHistoryChart(1, 2), resists);
    }

    /**
     * Builds a class carrying the given hit-die contribution, with the remaining fields filled from
     * the Warrior record of {@code class.txt}.
     *
     * @param name  the class's name
     * @param hpAdj the value C's {@code c_mhp} would hold
     * @return a class whose only interesting field is its hit-die contribution
     */
    private static PlayerClass playerClass(String name, int hpAdj) {
        return new PlayerClass(name, new ArrayList<>(List.of("Rookie")),
                new EnumMap<>(Stats.class), new EnumMap<>(PlayerSkill.class),
                new EnumMap<>(PlayerSkill.class), hpAdj, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                6, 30, 5, new ArrayList<>(), null);
    }

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * The race half of the die, which covers the narrower of the two bands.
     */
    @Nested
    @DisplayName("race hit-die contribution")
    class RaceContribution {

        /**
         * The three shipped values that bracket the range: the smallest race, the value most races
         * share, and the largest.
         */
        @Test
        @DisplayName("reports the hitdie: value from p_race.txt")
        void reportsShippedValues() {
            assertAll(
                    () -> assertEquals(7, race("Hobbit", 7).getMaxHitDie(),
                            "Hobbit, p_race.txt"),
                    () -> assertEquals(10, race("Human", 10).getMaxHitDie(),
                            "Human, p_race.txt"),
                    () -> assertEquals(12, race("Half-Troll", 12).getMaxHitDie(),
                            "Half-Troll, p_race.txt"));
        }

        /**
         * The contribution survives {@link PlayerRace#copy()}, which is the form
         * {@code playerGenerate} hands the player — a copy that dropped it would give every
         * character a die of nothing but their class.
         */
        @Test
        @DisplayName("survives a copy")
        void survivesACopy() {
            assertEquals(12, race("Half-Troll", 12).copy().getMaxHitDie());
        }
    }

    /**
     * The class half of the die, which is where the shipped data puts the wider variation.
     */
    @Nested
    @DisplayName("class hit-die contribution")
    class ClassContribution {

        /**
         * The two ends of the shipped range, plus one in between. Zero is not a placeholder here:
         * {@code class.txt} gives the Mage exactly that.
         */
        @Test
        @DisplayName("reports the hitdie: value from class.txt")
        void reportsShippedValues() {
            assertAll(
                    () -> assertEquals(0, playerClass("Mage", 0).getMaxHitDie(),
                            "Mage, class.txt"),
                    () -> assertEquals(4, playerClass("Rogue", 4).getMaxHitDie(),
                            "Rogue, class.txt"),
                    () -> assertEquals(9, playerClass("Warrior", 9).getMaxHitDie(),
                            "Warrior, class.txt"));
        }

        /**
         * The contribution survives {@link PlayerClass#copy()}, for the same reason the race's has
         * to.
         */
        @Test
        @DisplayName("survives a copy")
        void survivesACopy() {
            assertEquals(9, playerClass("Warrior", 9).copy().getMaxHitDie());
        }
    }

    /**
     * The addition itself, and the field it is stored in.
     */
    @Nested
    @DisplayName("the summed hit die")
    class SummedDie {

        /**
         * A fresh character's die is zero, so a test that finds a die has found one that was set.
         */
        @Test
        @DisplayName("starts at zero")
        void startsAtZero() {
            assertEquals(0, player.getHitDie());
        }

        /**
         * C adds the two figures; it does not multiply them, take the larger, or weight either.
         * The four combinations here are the corners of the shipped data — the smallest and
         * largest race against the smallest and largest class — so any operation other than
         * addition disagrees with at least one of them.
         */
        @Test
        @DisplayName("is the race's figure plus the class's")
        void sumsTheTwoContributions() {
            assertAll(
                    () -> assertEquals(7, sum(race("Hobbit", 7), playerClass("Mage", 0)),
                            "Hobbit Mage"),
                    () -> assertEquals(16, sum(race("Hobbit", 7), playerClass("Warrior", 9)),
                            "Hobbit Warrior"),
                    () -> assertEquals(12, sum(race("Half-Troll", 12), playerClass("Mage", 0)),
                            "Half-Troll Mage"),
                    () -> assertEquals(21, sum(race("Half-Troll", 12), playerClass("Warrior", 9)),
                            "Half-Troll Warrior"));
        }

        /**
         * The die is assigned rather than accumulated. C's {@code player_generate} runs again for
         * every choice made on the birth screen, so a second write has to replace the first — a
         * character who switched from Warrior to Mage must not keep the Warrior's nine.
         */
        @Test
        @DisplayName("a second write replaces the first")
        void secondWriteReplaces() {
            player.setHitDie(19);
            player.setHitDie(10);
            assertEquals(10, player.getHitDie());
        }

        /**
         * Writes the sum the way {@code player_generate} does and reads it back.
         *
         * @param race        the race supplying {@code r_mhp}
         * @param playerClass the class supplying {@code c_mhp}
         * @return the die the player ends up with
         */
        private int sum(PlayerRace race, PlayerClass playerClass) {
            player.setHitDie(race.getMaxHitDie() + playerClass.getMaxHitDie());
            return player.getHitDie();
        }
    }
}
