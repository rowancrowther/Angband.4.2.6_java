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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link PlayerRace#getExpFactor()} and {@link PlayerClass#getExpFactor()}, the two halves of
 * C's {@code p->expfact = p->race->r_exp + p->class->c_exp} ({@code player-birth.c:997}).
 *
 * <p>The expected values are taken from the C data files, not from the port's parsers: the race
 * figures are the {@code exp:} lines of {@code lib/gamedata/p_race.txt} (Human 100, Half-Troll 120,
 * High-Elf 145), and the class figure is zero because {@code lib/gamedata/class.txt} carries no
 * {@code exp:} line for any class in 4.2.6 — the parser accepts one ({@code init.c:3483}) but the
 * shipped data never supplies it, so in the base game the race decides the whole factor.
 *
 * <p>That last point is why the class side is tested twice: once at zero, which is what the game
 * actually ships, and once at a value C would only see from variant data, to confirm the accessor
 * reports what it was given rather than a constant.
 *
 * <p>The factor is a percentage applied to the level table, so the sum is asserted directly against
 * C's addition. Nothing here exercises {@link Player}; the writer that stores the sum is covered by
 * {@code PlayerIdentityAccessorTest}.
 *
 * <p>Class PlayerExpFactorTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
class PlayerExpFactorTest {

    /**
     * Builds a race carrying the given experience factor, with the remaining fields filled from the
     * Human record of {@code p_race.txt} so the fixture is a real record rather than zeroes.
     *
     * @param name    the race's name
     * @param raceExp the {@code exp:} value from {@code p_race.txt}
     * @return a race whose only interesting field is its experience factor
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
     * Builds a class carrying the given experience factor, with the remaining fields filled from the
     * Warrior record of {@code class.txt}.
     *
     * @param name   the class's name
     * @param expAdj the value C's {@code c_exp} would hold
     * @return a class whose only interesting field is its experience factor
     */
    private static PlayerClass playerClass(String name, int expAdj) {
        return new PlayerClass(name, new ArrayList<>(List.of("Rookie")),
                new EnumMap<>(Stats.class), new EnumMap<>(PlayerSkill.class),
                new EnumMap<>(PlayerSkill.class), 9, expAdj,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                6, 30, 5, new ArrayList<>(), null);
    }

    /**
     * The race half of the factor, which is where the shipped data puts all the variation.
     */
    @Nested
    @DisplayName("race experience factor")
    class RaceFactor {

        /**
         * The three shipped values that bracket the range: the baseline, the value most races
         * share, and the highest in the file.
         */
        @Test
        @DisplayName("reports the exp: value from p_race.txt")
        void reportsShippedValues() {
            assertAll(
                    () -> assertEquals(100, race("Human", 100).getExpFactor(),
                            "Human, p_race.txt:80"),
                    () -> assertEquals(120, race("Half-Troll", 120).getExpFactor(),
                            "Half-Troll, p_race.txt:230"),
                    () -> assertEquals(145, race("High-Elf", 145).getExpFactor(),
                            "High-Elf, p_race.txt:272"));
        }

        /**
         * A copy is a character's own race record, and a character that levelled at a different
         * rate from its template would be a copy that had lost a field. The factor is a plain
         * {@code int}, so this is really a check that {@code copy} carries the argument through in
         * the right position of an eighteen-argument constructor.
         */
        @Test
        @DisplayName("a copied race keeps the factor")
        void copyKeepsFactor() {
            assertEquals(145, race("High-Elf", 145).copy().getExpFactor());
        }
    }

    /**
     * The class half, which the base game leaves at zero.
     */
    @Nested
    @DisplayName("class experience factor")
    class ClassFactor {

        /**
         * No class in {@code class.txt} has an {@code exp:} line, so a class assembled from the
         * shipped data has {@code c_exp} at its zero default and contributes nothing to the sum.
         */
        @Test
        @DisplayName("is zero for a class built from shipped data")
        void zeroForShippedData() {
            assertEquals(0, playerClass("Warrior", 0).getExpFactor());
        }

        /**
         * The field is still read and stored, so a variant supplying one gets it back unchanged.
         */
        @Test
        @DisplayName("reports a non-zero factor unchanged")
        void reportsNonZero() {
            assertEquals(30, playerClass("Variant", 30).getExpFactor());
        }

        /**
         * As for the race, a copy that dropped the factor would produce a character levelling at a
         * different rate from every other of its class.
         */
        @Test
        @DisplayName("a copied class keeps the factor")
        void copyKeepsFactor() {
            assertEquals(30, playerClass("Variant", 30).copy().getExpFactor());
        }
    }

    /**
     * The sum the two accessors exist to feed.
     */
    @Nested
    @DisplayName("the birth-time sum")
    class Sum {

        /**
         * {@code player-birth.c:997} adds the two, and does not multiply or clamp them. For the
         * shipped data that means the character's factor is simply their race's.
         */
        @Test
        @DisplayName("race plus class, as player_generate computes it")
        void sumMatchesC() {
            assertAll(
                    () -> assertEquals(100,
                            race("Human", 100).getExpFactor()
                                    + playerClass("Warrior", 0).getExpFactor(),
                            "Human Warrior pays the table price"),
                    () -> assertEquals(145,
                            race("High-Elf", 145).getExpFactor()
                                    + playerClass("Mage", 0).getExpFactor(),
                            "High-Elf Mage pays 45% more for every level"),
                    () -> assertEquals(175,
                            race("High-Elf", 145).getExpFactor()
                                    + playerClass("Variant", 30).getExpFactor(),
                            "a variant class adds on top, and still fits C's uint8_t expfact"));
        }
    }
}
