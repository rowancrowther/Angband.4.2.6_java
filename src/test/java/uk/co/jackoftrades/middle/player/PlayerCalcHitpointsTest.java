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
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerCalcs#calcHitpoints()}, the port of C's {@code calc_hitpoints}
 * ({@code player-calcs.c:1562-1588}) — the maximum every wound, potion and killing blow is measured
 * against.
 *
 * <p><b>What is worth pinning here is the arithmetic's edges, not the happy path.</b> The bonus from
 * {@code adjConMhp} is in hundredths of a hit point per level and is negative for poor constitution,
 * so the division has to truncate toward zero rather than floor; a Java port that reached for
 * {@code Math.floorDiv}, or a C reader who assumed rounding, would give a weak character one hit
 * point less than the original at most levels. The level floor catches the case where that penalty
 * drives the total negative.
 *
 * <p><b>The clamp is guarded twice and both guards matter.</b> The body runs only when the maximum
 * actually moved — otherwise every recalculation would repaint and re-clamp — and inside it the
 * comparison is {@code >=}, so a character sitting exactly on the new maximum still has its
 * hit-point fraction cleared. The fraction is sub-hitpoint regeneration credit, so the difference
 * between {@code >} and {@code >=} is a free hit point at the next tick: invisible in play, and
 * exactly the kind of thing a port loses silently.
 *
 * <p><b>Fixture note.</b> {@link Player} exposes no setters for level, hit points or the hit-dice
 * table — the real routes in are character creation and levelling, neither of which exists yet — and
 * its constructor leaves {@code state} null. Both are handled by reflection here rather than by
 * adding setters to production code for a test's benefit, following
 * {@code uk.co.jackoftrades.testsupport.CalcBonusesFixture}. {@link SeededPlayerRegistry} supplies
 * the body and race the constructor demands, so the class runs on its own.
 *
 * <p><b>Stat indices are set directly.</b> {@code calcHitpoints} reads the derived index out of
 * {@link PlayerState}, which in play is filled by {@code calcBonuses}; setting it here asks this
 * method's question without dragging the whole derivation in. Index 0 is the bottom of
 * {@code adjConMhp} (-250), index 8 is the zero entry, index 19 is 18/40 (+250).
 *
 * <p>Class PlayerCalcHitpointsTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCalcHitpointsTest {

    /**
     * The {@code adjConMhp} index whose bonus is -250, the table's harshest penalty.
     */
    private static final int CON_WORST = 0;
    /**
     * The {@code adjConMhp} index whose bonus is zero, so the dice total stands alone.
     */
    private static final int CON_NEUTRAL = 8;
    /**
     * The {@code adjConMhp} index whose bonus is +250 — a constitution of 18/40.
     */
    private static final int CON_STRONG = 19;

    /**
     * The character under test, rebuilt for each test so no test sees another's hit points.
     */
    private Player player;

    /**
     * Writes one of {@link Player}'s private fields.
     *
     * @param name  the field
     * @param value the value
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private void set(String name, Object value) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * Reads one of {@link Player}'s private fields.
     *
     * @param name the field
     * @return its value
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private int read(String name) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return (int) field.get(player);
    }

    /**
     * Puts the character at a level with a given hit-dice total and constitution.
     *
     * <p>The dice table is filled so that only the entry for the level under test carries the total;
     * an out-of-bounds read would then be a failure rather than a plausible number.
     *
     * @param level     the character level
     * @param diceTotal the hit dice rolled up to that level, C's {@code p->player_hp[lev - 1]}
     * @param conIndex  the derived constitution index into {@code adjConMhp}
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    private void character(int level, int diceTotal, int conIndex) throws ReflectiveOperationException {
        int[] dice = new int[level];
        dice[level - 1] = diceTotal;
        set("level", level);
        set("playerHP", dice);
        PlayerState state = new PlayerState();
        state.setStatInd(Stats.STAT_CON, conIndex);
        set("state", state);
    }

    /**
     * Sets the hit points the character starts the calculation with.
     *
     * @param max     the current maximum, C's {@code p->mhp}
     * @param current the current total, C's {@code p->chp}
     * @param frac    the sub-hitpoint fraction, C's {@code p->chp_frac}
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    private void hitPoints(int max, int current, int frac) throws ReflectiveOperationException {
        set("maxHP", max);
        set("currentHP", current);
        set("chpFrac", frac);
    }

    /**
     * @return whether the calculation asked for a hit-point repaint
     */
    private boolean repaintAsked() {
        return player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_HP);
    }

    @BeforeEach
    void buildPlayer() {
        player = new Player();
    }

    /**
     * The maximum itself — dice total, constitution bonus and the level floor.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the maximum")
    class Maximum {

        /**
         * A neutral constitution contributes nothing, so the maximum is the hit dice alone.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("is the hit dice total when constitution is neutral")
        void neutralConstitution() throws ReflectiveOperationException {
            character(10, 100, CON_NEUTRAL);
            hitPoints(0, 0, 0);

            PlayerCalcs.calcHitpoints(player);

            assertEquals(100, read("maxHP"));
        }

        /**
         * The bonus is hundredths of a hit point <em>per level</em>: +250 at level 10 is 25 points,
         * and the same constitution at level 2 is only 5. Asserting both pins the scaling, which a
         * port that divided once at the wrong place would get right at exactly one level.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("adds the constitution bonus scaled by level")
        void constitutionScalesWithLevel() throws ReflectiveOperationException {
            character(10, 100, CON_STRONG);
            hitPoints(0, 0, 0);
            PlayerCalcs.calcHitpoints(player);
            int atTen = read("maxHP");

            character(2, 100, CON_STRONG);
            hitPoints(0, 0, 0);
            PlayerCalcs.calcHitpoints(player);
            int atTwo = read("maxHP");

            assertAll(
                    () -> assertEquals(125, atTen),
                    () -> assertEquals(105, atTwo));
        }

        /**
         * The penalty case, and the reason the division must truncate toward zero. -250 at level 3
         * is -7.5 hit points; C's integer division discards the half, giving -7 and a maximum of 23.
         * Flooring would give -8 and 22.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("truncates a negative bonus toward zero rather than flooring it")
        void negativeBonusTruncatesTowardZero() throws ReflectiveOperationException {
            character(3, 30, CON_WORST);
            hitPoints(0, 0, 0);

            PlayerCalcs.calcHitpoints(player);

            assertEquals(23, read("maxHP"));
        }

        /**
         * When the constitution penalty outweighs the dice the total goes negative, and the floor of
         * one hit point per level — {@code level + 1}, so a level 20 character floors at 21 — is what
         * stops the character being unkillable-by-arithmetic dead.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("never falls below one hit point per level")
        void levelFloor() throws ReflectiveOperationException {
            character(20, 10, CON_WORST);
            hitPoints(0, 0, 0);

            PlayerCalcs.calcHitpoints(player);

            assertEquals(21, read("maxHP"));
        }
    }

    /**
     * What happens to the current total once the maximum has moved.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the current total")
    class CurrentTotal {

        /**
         * A wounded character keeps its wounds when the maximum rises: the current total is only ever
         * clamped downward, never topped up.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("is left alone when it is below the new maximum")
        void belowIsUntouched() throws ReflectiveOperationException {
            character(10, 100, CON_NEUTRAL);
            hitPoints(50, 30, 500);

            PlayerCalcs.calcHitpoints(player);

            assertAll(
                    () -> assertEquals(100, read("maxHP")),
                    () -> assertEquals(30, read("currentHP")),
                    () -> assertEquals(500, read("chpFrac")));
        }

        /**
         * Constitution drained — a ring taken off, say — drops the maximum below what the character
         * currently holds, and the excess is cut away along with the fraction.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("is clamped down, fraction and all, when the maximum falls below it")
        void aboveIsClamped() throws ReflectiveOperationException {
            character(10, 100, CON_NEUTRAL);
            hitPoints(125, 125, 900);

            PlayerCalcs.calcHitpoints(player);

            assertAll(
                    () -> assertEquals(100, read("maxHP")),
                    () -> assertEquals(100, read("currentHP")),
                    () -> assertEquals(0, read("chpFrac")));
        }

        /**
         * <b>The {@code >=} case.</b> The character lands exactly on the new maximum, so no hit points
         * change hands — but C still clears the fraction, and a port using {@code >} would leave the
         * old sub-hitpoint credit in place for regeneration to cash in.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("loses its fraction when it sits exactly on the new maximum")
        void exactlyAtMaximumLosesItsFraction() throws ReflectiveOperationException {
            character(10, 100, CON_NEUTRAL);
            hitPoints(125, 100, 900);

            PlayerCalcs.calcHitpoints(player);

            assertAll(
                    () -> assertEquals(100, read("maxHP")),
                    () -> assertEquals(100, read("currentHP")),
                    () -> assertEquals(0, read("chpFrac")));
        }
    }

    /**
     * The guard on the maximum having changed, which governs both the clamp and the repaint.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the changed guard")
    class ChangedGuard {

        /**
         * A recalculation that arrives at the maximum already held must do nothing at all — and the
         * fraction is the visible proof, since an unguarded clamp would clear it on every call.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("leaves everything untouched when the maximum has not moved")
        void unchangedMaximumDoesNothing() throws ReflectiveOperationException {
            character(10, 100, CON_NEUTRAL);
            hitPoints(100, 100, 900);

            PlayerCalcs.calcHitpoints(player);

            assertAll(
                    () -> assertEquals(100, read("currentHP")),
                    () -> assertEquals(900, read("chpFrac")),
                    () -> assertFalse(repaintAsked(), "no repaint should be asked for"));
        }

        /**
         * A maximum that did move asks the UI to repaint the hit points, rather than touching the
         * display itself.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("raises PR_HP when the maximum moves")
        void changedMaximumAsksForARepaint() throws ReflectiveOperationException {
            character(10, 100, CON_NEUTRAL);
            hitPoints(90, 90, 0);

            PlayerCalcs.calcHitpoints(player);

            assertTrue(repaintAsked(), "PR_HP should be raised");
        }
    }
}
