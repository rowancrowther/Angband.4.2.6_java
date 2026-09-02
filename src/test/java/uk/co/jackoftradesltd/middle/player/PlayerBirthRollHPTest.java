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
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#rollHP(Player)}, the port of C's {@code roll_hp}
 * ({@code player-birth.c:279-308}), together with the two accessors it drives —
 * {@link Player#getHitDie()} and {@link Player#setPlayerHitpoint(int, int)}.
 *
 * <p>The C is a rejection sampler:
 *
 * <pre>{@code
 * min_value = (PY_MAX_LEVEL * (player->hitdie - 1) * 3) / 8;
 * min_value += PY_MAX_LEVEL;
 * max_value = (PY_MAX_LEVEL * (player->hitdie - 1) * 5) / 8;
 * max_value += PY_MAX_LEVEL;
 * while (true) {
 *     for (i = 1; i < PY_MAX_LEVEL; i++) {
 *         j = randint1(player->hitdie);
 *         player->player_hp[i] = player->player_hp[i-1] + j;
 *     }
 *     if (player->player_hp[PY_MAX_LEVEL-1] < min_value) continue;
 *     if (player->player_hp[PY_MAX_LEVEL-1] > max_value) continue;
 *     break;
 * }
 * }</pre>
 *
 * <p><b>Testing something that rolls dice.</b> The port draws from a shared {@code Random} that a
 * test cannot swap out — {@code RandomValueUtils.random} is {@code private static final}. So the
 * expected values here are not single numbers but the properties the C guarantees, and one hit die
 * for which the C is fully determined.
 *
 * <p><b>The determined case.</b> A one-sided die leaves nothing to chance. C's
 * {@code randint1(1)} is {@code Rand_div(1) + 1}, and {@code Rand_div} returns zero outright for
 * {@code m <= 1} ({@code z-rand.c}), so every roll is exactly one and the table is
 * {@code player_hp[i] = player_hp[0] + i}. The window collapses with it: {@code (50 * 0 * 3) / 8 + 50}
 * and {@code (50 * 0 * 5) / 8 + 50} are both 50, so the one table the loop can produce is the one
 * table it will accept — provided index zero holds the seed {@code player_generate} gives it
 * ({@code player-birth.c:1003}), which for a one-sided die is 1. That case pins the whole table
 * against arithmetic, with no sampling involved.
 *
 * <p><b>The window.</b> Its two bounds are integer divisions that truncate, and the shipped hit dice
 * make the truncation visible. A two-sided die gives {@code (50 * 1 * 3) / 8 = 18} rather than
 * 18.75 and {@code (50 * 1 * 5) / 8 = 31} rather than 31.25, so the window is 68 to 81 inclusive —
 * where rounding to nearest would have given 69 to 82, and rounding up 69 to 82 as well. Both
 * operands stay positive for any hit die a data file can express, so C's truncation toward zero and
 * Java's agree; what the test has to catch is a port that rounded rather than truncated, or that
 * treated a bound as exclusive.
 *
 * <p>A two-sided die is what makes those bounds reachable. Forty-nine rolls of one or two, on top of
 * a seed of 2, put the top entry anywhere in 51 to 100, with the window cutting off about five per
 * cent of that range; the accepted extremes 68 and 81 each turn up in a percent or two of runs, so a
 * couple of thousand births see both many times over. The same runs are what show the rejection loop
 * doing its work at all: without it, one birth in twenty would land outside.
 *
 * <p><b>Index zero.</b> {@code roll_hp} starts its loop at one and never writes index zero, because
 * {@code player_generate} has already put the full hit die there. That is not tidiness — it is what
 * keeps the level-one total the same across every rejected attempt. A port that seeded index zero
 * itself, or that started the loop at zero, would shift every entry, so the seed is planted here as
 * a sentinel and read back afterwards.
 *
 * <p>Class PlayerBirthRollHPTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthRollHPTest {

    /**
     * C's {@code PY_MAX_LEVEL} ({@code player.h:81}), repeated here rather than read from the port
     * so that a test does not agree with a constant it is meant to be checking.
     */
    private static final int PY_MAX_LEVEL = 50;

    /**
     * The character being born, fresh for each test.
     */
    private Player player;

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * Writes the player's private {@code hitDie} field, which has no setter — in C it is filled by
     * {@code player_generate} from the race's and the class's contributions
     * ({@code player-birth.c:998}), and the port has not reached that function yet.
     *
     * @param sides the number of sides on the hit die
     * @throws Exception if the field cannot be reached
     */
    private void setHitDie(int sides) throws Exception {
        Field field = Player.class.getDeclaredField("hitDie");
        field.setAccessible(true);
        field.setInt(player, sides);
    }

    /**
     * Puts a character in the state {@code roll_hp} expects to be called in: a hit die of the given
     * size, with index zero of the table already seeded with it, exactly as
     * {@code player_generate} leaves things ({@code player-birth.c:1003}).
     *
     * @param sides the number of sides on the hit die
     * @throws Exception if the hit die field cannot be reached
     */
    private void generate(int sides) throws Exception {
        setHitDie(sides);
        player.setPlayerHitpoint(0, sides);
    }

    /**
     * {@link Player#getHitDie()} and {@link Player#setPlayerHitpoint(int, int)}, the two accessors
     * the rolling code reads and writes the character through.
     */
    @Nested
    @DisplayName("the hit point accessors")
    class Accessors {

        /**
         * The hit die reads back the figure {@code player_generate} would have put there — the sum
         * of a race's {@code r_mhp} and a class's {@code c_mhp}. The shipped Human race carries 10
         * and the shipped Warrior class 9 ({@code p_race.txt}, {@code class.txt}), so 19 is a real
         * character's die rather than an invented number.
         *
         * @throws Exception if the hit die field cannot be reached
         */
        @Test
        @DisplayName("getHitDie returns the die player_generate set")
        void getHitDieReturnsTheDie() throws Exception {
            setHitDie(19);
            assertEquals(19, player.getHitDie());
        }

        /**
         * A fresh character's die is zero, so a test that finds a die has found one that was set.
         */
        @Test
        @DisplayName("getHitDie starts at zero")
        void getHitDieStartsAtZero() {
            assertEquals(0, player.getHitDie());
        }

        /**
         * The setter writes the entry it is given and reads back through {@link Player#getPlayerHP}
         * at the same index — the two are the port's halves of C's bare array access.
         */
        @Test
        @DisplayName("setPlayerHitpoint writes the entry getPlayerHP reads")
        void setterAndGetterAgree() {
            player.setPlayerHitpoint(0, 10);
            player.setPlayerHitpoint(1, 17);
            assertEquals(10, player.getPlayerHP(0));
            assertEquals(17, player.getPlayerHP(1));
        }

        /**
         * The setter stores what it is handed, without deriving it. C's caller does the cumulative
         * addition ({@code player-birth.c:296}); a port that added inside the accessor would double
         * it.
         */
        @Test
        @DisplayName("setPlayerHitpoint stores rather than accumulates")
        void setterDoesNotAccumulate() {
            player.setPlayerHitpoint(3, 40);
            player.setPlayerHitpoint(3, 40);
            assertEquals(40, player.getPlayerHP(3));
        }
    }

    /**
     * {@code rollHP}, the port of C's {@code roll_hp}.
     */
    @Nested
    @DisplayName("PlayerBirth.rollHP")
    class RollHP {

        /**
         * The one hit die for which the C has a single answer. Every roll of a one-sided die is one,
         * so the table counts up from the seed by one a level, and its top entry is 50 — the sole
         * value the collapsed window admits.
         *
         * @throws Exception if the hit die field cannot be reached
         */
        @Test
        @DisplayName("a one-sided die gives exactly one table")
        void oneSidedDieIsDetermined() throws Exception {
            generate(1);
            PlayerBirth.rollHP(player);
            for (int index = 0; index < PY_MAX_LEVEL; index++) {
                assertEquals(1 + index, player.getPlayerHP(index),
                        "entry " + index);
            }
        }

        /**
         * Index zero belongs to {@code player_generate}, not to this method. The loop starts at one,
         * so whatever was seeded there survives the call — including across the attempts a rejected
         * table costs.
         *
         * @throws Exception if the hit die field cannot be reached
         */
        @Test
        @DisplayName("leaves index zero alone")
        void leavesIndexZeroAlone() throws Exception {
            generate(10);
            PlayerBirth.rollHP(player);
            assertEquals(10, player.getPlayerHP(0));
        }

        /**
         * C's array runs to {@code PY_MAX_LEVEL} entries ({@code player.h:583}) and its loop stops
         * at {@code PY_MAX_LEVEL - 1}. The port's array carries one slot more, and nothing should
         * reach it — an off-by-one in the loop bound would show up here and nowhere else.
         *
         * @throws Exception if the hit die field cannot be reached
         */
        @Test
        @DisplayName("does not write past the last level")
        void doesNotWritePastTheLastLevel() throws Exception {
            generate(10);
            player.setPlayerHitpoint(PY_MAX_LEVEL, -1);
            PlayerBirth.rollHP(player);
            assertEquals(-1, player.getPlayerHP(PY_MAX_LEVEL));
        }

        /**
         * The table is cumulative and each step is a single roll of the die, so consecutive entries
         * differ by between one and {@code hitdie} inclusive. A gap of zero would mean a roll of
         * zero — {@code randint1} cannot produce one — and a gap above the die would mean the roll
         * was taken against the wrong bound.
         *
         * @throws Exception if the hit die field cannot be reached
         */
        @Test
        @DisplayName("every level gains between one and hitdie")
        void everyStepIsOneRoll() throws Exception {
            generate(10);
            for (int birth = 0; birth < 200; birth++) {
                PlayerBirth.rollHP(player);
                for (int index = 1; index < PY_MAX_LEVEL; index++) {
                    int gain = player.getPlayerHP(index) - player.getPlayerHP(index - 1);
                    assertTrue(gain >= 1 && gain <= 10,
                            "gain at entry " + index + " was " + gain);
                }
            }
        }

        /**
         * The acceptance window for a two-sided die is 68 to 81, from
         * {@code (50 * 1 * 3) / 8 + 50} and {@code (50 * 1 * 5) / 8 + 50}. Both bounds are
         * inclusive: C rejects on {@code <} and {@code >}, not on {@code <=} and {@code >=}.
         *
         * <p>Two thousand births are enough for the extremes to appear — they are a percent or two
         * of accepted tables each — and for the loop's rejections to be visible, since an
         * unconstrained run of a two-sided die reaches from 51 to 100 and would leave the window
         * about one birth in twenty.
         *
         * @throws Exception if the hit die field cannot be reached
         */
        @Test
        @DisplayName("keeps the top entry inside the truncated window, bounds included")
        void topEntryLandsInTheWindow() throws Exception {
            int lowest = Integer.MAX_VALUE;
            int highest = Integer.MIN_VALUE;
            for (int birth = 0; birth < 2000; birth++) {
                generate(2);
                PlayerBirth.rollHP(player);
                int top = player.getPlayerHP(PY_MAX_LEVEL - 1);
                lowest = Math.min(lowest, top);
                highest = Math.max(highest, top);
            }
            assertTrue(lowest >= 68, "a birth fell below the window at " + lowest);
            assertTrue(highest <= 81, "a birth rose above the window at " + highest);
            assertEquals(68, lowest, "the lower bound was never reached, so it is not 68");
            assertEquals(81, highest, "the upper bound was never reached, so it is not 81");
        }

        /**
         * The same window for the commonest die size. A ten-sided die gives
         * {@code (50 * 9 * 3) / 8 = 168} rather than 168.75 and {@code (50 * 9 * 5) / 8 = 281}
         * rather than 281.25, so the bounds are 218 and 331 against an unconstrained range of 60 to
         * 510. This checks the truncation at a second die size, where a rounding error would move
         * the bounds by one in the other direction from the two-sided case.
         *
         * @throws Exception if the hit die field cannot be reached
         */
        @Test
        @DisplayName("a ten-sided die lands between 218 and 331")
        void tenSidedDieWindow() throws Exception {
            for (int birth = 0; birth < 200; birth++) {
                generate(10);
                PlayerBirth.rollHP(player);
                int top = player.getPlayerHP(PY_MAX_LEVEL - 1);
                assertTrue(top >= 218 && top <= 331, "top entry was " + top);
            }
        }

        /**
         * The whole table is re-rolled on rejection rather than patched, so a second birth for the
         * same character overwrites the first. Two tables from a ten-sided die agreeing at all fifty
         * entries would be a port that had stopped rolling.
         *
         * @throws Exception if the hit die field cannot be reached
         */
        @Test
        @DisplayName("re-rolls the whole table on a second call")
        void secondCallRerolls() throws Exception {
            generate(10);
            PlayerBirth.rollHP(player);
            int[] first = new int[PY_MAX_LEVEL];
            for (int index = 0; index < PY_MAX_LEVEL; index++) {
                first[index] = player.getPlayerHP(index);
            }
            boolean differs = false;
            for (int attempt = 0; attempt < 20 && !differs; attempt++) {
                PlayerBirth.rollHP(player);
                for (int index = 1; index < PY_MAX_LEVEL; index++) {
                    if (player.getPlayerHP(index) != first[index]) {
                        differs = true;
                        break;
                    }
                }
            }
            assertTrue(differs, "twenty births produced the same table every time");
        }

        /**
         * The port's {@code PY_MAX_LEVEL} is C's ({@code player.h:81}). Every bound above is derived
         * from 50, so a port that had changed it would make the rest of this class meaningless
         * rather than fail.
         */
        @Test
        @DisplayName("PY_MAX_LEVEL is fifty")
        void maxLevelIsFifty() {
            assertEquals(PY_MAX_LEVEL, PlayerRegistry.PY_MAX_LEVEL);
        }
    }
}
