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
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link PlayerCalcs#calcBlows}, the port of C's {@code calc_blows} ({@code player-calcs.c}):
 * blows per turn, scaled by 100, from strength, dexterity, the weapon's weight and the class.
 *
 * <p><b>The expected values come from C's tables, worked by hand.</b> The test class is the fixture's
 * plain class: at most five attacks, a minimum weapon weight of 30 tenth-pounds and an attack
 * multiplier of 5. Each case below names the {@code adj_str_blow[]} and {@code adj_dex_blow[]} rungs
 * it lands on and the {@code blows_table[][]} cell that results, so a wrong figure can be traced to
 * the table it came from rather than to the Java.
 *
 * <p>The stat indices are set on the state directly. {@code calcBlows} reads only
 * {@code stat_ind[STAT_STR]} and {@code stat_ind[STAT_DEX]}, and going through {@code calcBonuses}
 * to produce them would make every case also a test of the stat-index conversion.
 *
 * <p>Class PlayerCalcBlowsTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCalcBlowsTest {

    /**
     * The character whose class supplies the multiplier, minimum weight and attack ceiling.
     */
    private Player player;

    /**
     * The state whose strength and dexterity indices are read.
     */
    private PlayerState state;

    /**
     * Builds the plain character and a wiped state.
     *
     * @throws Exception if the fixture cannot reach a field
     */
    @BeforeEach
    void setUp() throws Exception {
        player = CalcBonusesFixture.plainCharacter().player();
        state = new PlayerState();
        state.wipe();
    }

    /**
     * Sets the two indices {@code calcBlows} reads.
     *
     * @param str the strength index, 0 to 37
     * @param dex the dexterity index, 0 to 37
     */
    private void indices(int str, int dex) {
        state.setStatInd(Stats.STAT_STR, str);
        state.setStatInd(Stats.STAT_DEX, dex);
    }

    /**
     * A sword of the given weight.
     *
     * @param weight the weight in tenth-pounds
     * @return the weapon
     */
    private static ItemObject weapon(int weight) {
        ItemObject sword = CalcBonusesFixture.item(TValue.TV_SWORD);
        sword.setWeight(weight);
        return sword;
    }

    /**
     * Turns on the birth option that doubles the blows floor, as C's {@code OPT(p, birth_percent_damage)}
     * would read it.
     *
     * @throws Exception if the option set cannot be reached
     */
    @SuppressWarnings("unchecked")
    private void percentDamage() throws Exception {
        Field field = PlayerOptions.class.getDeclaredField("options");
        field.setAccessible(true);
        ((Flag<PlayerOptionEnum>) field.get(player.getPlayerOptions())).on(PlayerOptionEnum.OP_birth_percent_damage);
    }

    /**
     * The ordinary path: strength and dexterity against the weight of the weapon.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the table lookup")
    class TableLookup {

        /**
         * Strength 10 and dexterity 10 are index 7 each. {@code adj_str_blow[7]} is 10, so the
         * strength rung is {@code 10 * 5 / 30 = 1}; {@code adj_dex_blow[7]} is 1. Cell
         * {@code blows_table[1][1]} is 95 energy, and {@code 10000 / 95} is 105.
         */
        @Test
        @DisplayName("an unarmed character of ordinary strength gets 1.05 blows")
        void unarmed() {
            indices(7, 7);

            assertEquals(105, PlayerCalcs.calcBlows(player, null, state, 0));
        }

        /**
         * A weapon of weight 100 raises the divisor from the class minimum of 30 to 100, so the
         * strength rung falls to {@code 50 / 100 = 0}. Cell {@code blows_table[0][1]} is 100, which
         * gives exactly one blow.
         */
        @Test
        @DisplayName("a heavy weapon drops the strength rung")
        void heavyWeapon() {
            indices(7, 7);

            assertEquals(100, PlayerCalcs.calcBlows(player, weapon(100), state, 0));
        }

        /**
         * Dexterity index 3 is {@code adj_dex_blow[3] = 0}, so the dexterity column is 0. Cell
         * {@code blows_table[1][0]} is 100, so one blow where dexterity 10 gave 105.
         */
        @Test
        @DisplayName("low dexterity moves to the first column")
        void lowDexterity() {
            indices(7, 3);

            assertEquals(100, PlayerCalcs.calcBlows(player, null, state, 0));
        }
    }

    /**
     * The weapon's weight is floored at the class minimum, and the boundary is at 30.
     *
     * <p>Strength index 19 gives {@code adj_str_blow[19] = 60}, so the numerator is
     * {@code 60 * 5 = 300}. Dexterity index 7 is column 1.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the minimum weight")
    class MinimumWeight {

        /**
         * Divisor 30: rung {@code 300 / 30 = 10}, cell {@code blows_table[10][1] = 30}, so
         * {@code 10000 / 30 = 333}.
         */
        @Test
        @DisplayName("an unarmed character is treated as holding the minimum weight")
        void unarmedUsesTheMinimum() {
            indices(19, 7);

            assertEquals(333, PlayerCalcs.calcBlows(player, null, state, 0));
        }

        /**
         * A weapon lighter than the minimum divides by the minimum, so it gives the same answer
         * as an empty hand: C's {@code div = (weight < min_weight) ? min_weight : weight}.
         */
        @Test
        @DisplayName("a weapon lighter than the minimum gives the minimum's answer")
        void lighterThanTheMinimum() {
            indices(19, 7);

            assertEquals(333, PlayerCalcs.calcBlows(player, weapon(29), state, 0));
        }

        /**
         * A weapon exactly at the minimum is the same divisor.
         */
        @Test
        @DisplayName("a weapon exactly at the minimum gives the minimum's answer")
        void atTheMinimum() {
            indices(19, 7);

            assertEquals(333, PlayerCalcs.calcBlows(player, weapon(30), state, 0));
        }

        /**
         * One tenth-pound over the minimum is the first weight that counts: {@code 300 / 31 = 9},
         * cell {@code blows_table[9][1] = 32}, and {@code 10000 / 32 = 312}.
         */
        @Test
        @DisplayName("one tenth-pound over the minimum is the first that counts")
        void justOverTheMinimum() {
            indices(19, 7);

            assertEquals(312, PlayerCalcs.calcBlows(player, weapon(31), state, 0));
        }

        /**
         * Divisor 100: {@code 300 / 100 = 3}, cell {@code blows_table[3][1] = 75}, and
         * {@code 10000 / 75 = 133}.
         */
        @Test
        @DisplayName("a heavy weapon is divided by its own weight")
        void heavy() {
            indices(19, 7);

            assertEquals(133, PlayerCalcs.calcBlows(player, weapon(100), state, 0));
        }
    }

    /**
     * The two ceilings: the strength rung saturates at 11, and the result at the class maximum.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the ceilings")
    class Ceilings {

        /**
         * Strength index 24 gives {@code adj_str_blow[24] = 110}, so {@code 550 / 30 = 18} — past
         * the table, so it is clamped to 11. Cell {@code blows_table[11][1]} is 29, giving
         * {@code 10000 / 29 = 344}.
         */
        @Test
        @DisplayName("a strength rung past the table is clamped to 11")
        void strengthRungClamped() {
            indices(24, 7);

            assertEquals(344, PlayerCalcs.calcBlows(player, null, state, 0));
        }

        /**
         * Strength index 37 is far beyond the clamp and must give exactly the same answer as index
         * 24: everything past rung 11 is the same row.
         */
        @Test
        @DisplayName("the strongest index gives the same row as one just past the table")
        void strongestIndexSameRow() {
            indices(37, 7);

            assertEquals(344, PlayerCalcs.calcBlows(player, null, state, 0));
        }

        /**
         * Strength 37 and dexterity 37 (column {@code adj_dex_blow[37] = 11}) reach
         * {@code blows_table[11][11] = 15}, which is {@code 10000 / 15 = 666} — but the class allows
         * five attacks, so the result is held at 500.
         */
        @Test
        @DisplayName("the result is held at the class's maximum attacks")
        void classCeiling() {
            indices(37, 37);

            assertEquals(500, PlayerCalcs.calcBlows(player, null, state, 0));
        }

        /**
         * Extra blows from equipment are added <em>after</em> the cap, so two of them take the
         * capped 500 to 700 — past the class ceiling, as C allows.
         */
        @Test
        @DisplayName("extra blows are added after the ceiling")
        void extraBlowsPassTheCeiling() {
            indices(37, 37);

            assertEquals(700, PlayerCalcs.calcBlows(player, null, state, 2));
        }
    }

    /**
     * Extra blows and the floor.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("extra blows and the floor")
    class ExtraAndFloor {

        /**
         * One extra blow is a hundred more: 105 becomes 205.
         */
        @Test
        @DisplayName("each extra blow adds a hundred")
        void extraBlowAddsAHundred() {
            indices(7, 7);

            assertEquals(205, PlayerCalcs.calcBlows(player, null, state, 1));
        }

        /**
         * A penalty that would take the total below one blow leaves it at 100: 105 less 100 is 5,
         * floored to 100.
         */
        @Test
        @DisplayName("the floor is one blow")
        void floorIsOneBlow() {
            indices(7, 7);

            assertEquals(100, PlayerCalcs.calcBlows(player, null, state, -1));
        }

        /**
         * Under the percentage-damage birth option the floor is two blows, so the 105 that would
         * otherwise stand is lifted to 200.
         *
         * @throws Exception if the option cannot be set
         */
        @Test
        @DisplayName("under birth_percent_damage the floor is two blows")
        void percentDamageFloor() throws Exception {
            indices(7, 7);
            percentDamage();

            assertEquals(200, PlayerCalcs.calcBlows(player, null, state, 0));
        }

        /**
         * The raised floor lifts only totals beneath it: the capped 500 is untouched.
         *
         * @throws Exception if the option cannot be set
         */
        @Test
        @DisplayName("the raised floor does not lower a larger total")
        void percentDamageLeavesLargeTotals() throws Exception {
            indices(37, 37);
            percentDamage();

            assertEquals(500, PlayerCalcs.calcBlows(player, null, state, 0));
        }
    }
}
