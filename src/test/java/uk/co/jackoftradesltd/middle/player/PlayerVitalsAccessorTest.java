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
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the accessors {@link PlayerCalcs} writes the recalculated character through: the hit-point
 * and spell-point triples, the rolled hit-point table, and the two calculated states.
 *
 * <p>These are storage, so the interesting assertions are not that a value comes back. They are the
 * three things a reader would otherwise have to assume:
 *
 * <ul>
 *   <li><b>Nothing clamps.</b> Each pool is three separate fields — a current value, a ceiling and a
 *       sixteen-bit fraction — and C keeps the clamping in {@code calc_mana} and
 *       {@code calc_hitpoints}, not in the writes. A setter that quietly capped would hide a
 *       calculation bug rather than expose one.</li>
 *   <li><b>The near-identical names do not cross.</b> {@code setPlayerMaxHP} writes the derived
 *       ceiling while {@code getPlayerHP} reads the rolled table; {@code setCurSp} and
 *       {@code setCspFrac} differ by three letters.</li>
 *   <li><b>The states are held by reference.</b> {@code updateBonuses} calculates into copies and
 *       swaps them in at the end, which only works if the swap is an assignment and not a copy.</li>
 * </ul>
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerVitalsAccessorTest {

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
     * Reads one of the player's private fields, for the state with no getter.
     *
     * @param name the field's name
     * @return the stored value
     * @throws Exception if the field cannot be reached
     */
    private Object get(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(player);
    }

    /**
     * Spell points: current, maximum, and the fraction between whole points.
     */
    @Nested
    @DisplayName("spell points")
    class SpellPoints {

        /**
         * A new player has no mana at all, which is also how the port asks whether a character has a
         * realm — {@code calcBonuses} turns on {@code PF_NO_MANA} when the maximum is zero, matching
         * C's {@code if (!p->msp)} at {@code player-calcs.c:2335}.
         */
        @Test
        @DisplayName("a new player has no mana")
        void newPlayerHasNoMana() {
            assertEquals(0, player.getCurSp());
            assertEquals(0, player.getMaxSP());
        }

        /**
         * The three are separate fields, and writing one leaves the other two where they were. Worth
         * pinning because {@code curSp} and {@code cspFrac} are adjacent {@code int}s whose names
         * differ by three letters, so a crossed setter would compile and read plausibly.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("current, maximum and fractional mana are separate")
        void manaFieldsAreSeparate() throws Exception {
            player.setMaxSP(30);
            player.setCurSp(12);
            player.setCspFrac(500);

            assertEquals(30, player.getMaxSP());
            assertEquals(12, player.getCurSp());
            assertEquals(500, get("cspFrac"));

            player.setCurSp(13);

            assertEquals(30, player.getMaxSP(), "the ceiling was not disturbed");
            assertEquals(500, get("cspFrac"), "nor was the fraction");
        }

        /**
         * The setter does not clamp to the maximum. C does not either: {@code calc_mana}
         * ({@code player-calcs.c:1551}) pulls the current value down only in the branch where the
         * ceiling has just changed, so a value above the ceiling is stored exactly as asked for. A
         * setter that capped here would silently repair a miscalculated ceiling instead of letting
         * it show.
         */
        @Test
        @DisplayName("current mana is not clamped to the maximum")
        void currentManaIsNotClamped() {
            player.setMaxSP(10);

            player.setCurSp(99);

            assertEquals(99, player.getCurSp());
            assertEquals(10, player.getMaxSP());
        }
    }

    /**
     * Hit points: the rolled table, the derived ceiling, the current total and its fraction.
     */
    @Nested
    @DisplayName("hit points")
    class HitPoints {

        /**
         * A freshly constructed player has the table allocated and empty, not absent. The
         * constructor sizes it at {@code PY_MAX_LEVEL + 1} entries and Java zeroes them, so a read
         * before birth has rolled anything answers zero rather than throwing.
         *
         * <p>That is not the same as C, and the difference is deliberate rather than a divergence
         * to fix. C's {@code player_hp} is an inline {@code int16_t[PY_MAX_LEVEL]} inside the player
         * struct ({@code player.h:583}), so it exists from the moment the struct does and reads zero
         * until {@code player_generate} seeds entry zero ({@code player-birth.c:1003}) and
         * {@code roll_hp} fills the rest ({@code player-birth.c:296}). An allocated Java array is
         * the closer match to that; a null one was the port's earlier state.
         *
         * <p>The extra slot is the one place the shapes part company: C's array has fifty entries
         * for fifty levels, indexed zero to forty-nine, and the port's has fifty-one. Nothing writes
         * or reads the top one — {@link PlayerBirth#rollHP(Player)} stops one below it — so it is
         * slack rather than an off-by-one, and {@link PlayerBirthRollHPTest} pins that it stays
         * untouched.
         */
        @Test
        @DisplayName("a new player's rolled table is allocated and zeroed")
        void newPlayerHasAnEmptyRolledTable() {
            assertEquals(0, player.getPlayerHP(0));
            assertEquals(0, player.getPlayerHP(49), "the last level C has an entry for");
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> player.getPlayerHP(51),
                    "the table is sized PY_MAX_LEVEL + 1, so 51 is past its end");
        }

        /**
         * The table is indexed from zero and holds cumulative totals, so the figure for a
         * first-level character is entry zero and the subtraction lives at the call site — C reads
         * {@code p->player_hp[p->lev - 1]} ({@code player-calcs.c:1577}) and
         * {@link PlayerCalcs#calcHitpoints} passes {@code getLevel() - 1} in. An accessor that
         * subtracted internally would double the correction.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the rolled table is read by zero-based index")
        void rolledTableIsZeroBased() throws Exception {
            set("playerHP", new int[]{9, 17, 24});

            assertEquals(9, player.getPlayerHP(0), "a first-level character reads entry zero");
            assertEquals(17, player.getPlayerHP(1));
            assertEquals(24, player.getPlayerHP(2));
        }

        /**
         * Reading past the rolled levels throws rather than answering zero, which is what keeps a
         * level-index mistake loud.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("reading past the table throws")
        void readingPastTheTableThrows() throws Exception {
            set("playerHP", new int[]{9});

            assertThrows(ArrayIndexOutOfBoundsException.class, () -> player.getPlayerHP(1));
        }

        /**
         * The rolled table and the derived ceiling are different things with confusable names:
         * {@code setPlayerMaxHP} writes {@code mhp}, the number the health bar is drawn against,
         * while {@code getPlayerHP} reads the birth roll. Writing the one must not touch the other.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the maximum is derived, and does not write the rolled table")
        void maximumDoesNotWriteTheRolledTable() throws Exception {
            set("playerHP", new int[]{9, 17});

            player.setPlayerMaxHP(21);

            assertEquals(21, player.getMaxHP());
            assertEquals(9, player.getPlayerHP(0), "the roll is unchanged");
            assertEquals(17, player.getPlayerHP(1));
        }

        /**
         * Current, maximum and fractional hit points are three separate fields, and this is the pair
         * most likely to be crossed.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("current, maximum and fractional hit points are separate")
        void hitPointFieldsAreSeparate() throws Exception {
            player.setPlayerMaxHP(40);
            player.setCurrentHP(12);
            player.setChpFrac(700);

            assertEquals(40, player.getMaxHP());
            assertEquals(12, player.getCurrentHP());
            assertEquals(700, get("chpFrac"));

            player.setChpFrac(0);

            assertEquals(40, player.getMaxHP(), "the ceiling was not disturbed");
            assertEquals(12, player.getCurrentHP(), "nor was the current total");
        }

        /**
         * Neither end is clamped. Above the ceiling matters because {@code calc_hitpoints} is what
         * enforces the new limit ({@code player-calcs.c:1588}); below zero matters because death is
         * the damage code's decision, and a setter that floored at zero here would make a killing
         * blow unrecognisable.
         */
        @Test
        @DisplayName("current hit points are clamped at neither end")
        void currentHitPointsAreNotClamped() {
            player.setPlayerMaxHP(40);

            player.setCurrentHP(99);
            assertEquals(99, player.getCurrentHP());

            player.setCurrentHP(-5);
            assertEquals(-5, player.getCurrentHP());
        }
    }

    /**
     * The two calculated states, and the swap {@code updateBonuses} ends on.
     */
    @Nested
    @DisplayName("calculated states")
    class CalculatedStates {

        /**
         * A new player has neither state. Both are created by the first bonus calculation, so
         * anything reading them has to cope with null — {@code updateBonuses} opens by filling them
         * in for exactly that reason.
         */
        @Test
        @DisplayName("a new player has neither state")
        void newPlayerHasNeitherState() {
            assertNull(player.getPlayerState());
            assertNull(player.getKnownState());
        }

        /**
         * Both are stored by identity, not copied. That is what makes the calculate-into-a-copy,
         * swap-at-the-end shape of {@code updateBonuses} work: the state the player answers from
         * after the swap must be the object the calculation filled.
         */
        @Test
        @DisplayName("the states are stored by identity")
        void statesAreStoredByIdentity() {
            PlayerState state = new PlayerState();
            PlayerState known = new PlayerState();

            player.setState(state);
            player.setKnownState(known);

            assertSame(state, player.getPlayerState());
            assertSame(known, player.getKnownState());
        }

        /**
         * The real state and the known state are two objects and two fields. They are calculated by
         * the same method from the same character — the known one with the unlearned runes of the
         * gear left out — so they are easy to conflate, and a setter that wrote both would make the
         * character sheet report what the game actually resolves attacks with.
         */
        @Test
        @DisplayName("the real and known states are independent")
        void realAndKnownStatesAreIndependent() {
            PlayerState state = new PlayerState();
            PlayerState known = new PlayerState();
            player.setState(state);
            player.setKnownState(known);

            PlayerState replacement = new PlayerState();
            player.setState(replacement);

            assertSame(replacement, player.getPlayerState());
            assertSame(known, player.getKnownState(), "the known state was not disturbed");
            assertNotSame(player.getPlayerState(), player.getKnownState());
        }

        /**
         * Either may be set back to null. Nothing in the port does, but the setters take what they
         * are given, and {@code updateBonuses} tests for null on entry rather than trusting that a
         * state exists.
         */
        @Test
        @DisplayName("the states may be cleared")
        void statesMayBeCleared() {
            player.setState(new PlayerState());
            player.setKnownState(new PlayerState());

            player.setState(null);
            player.setKnownState(null);

            assertNull(player.getPlayerState());
            assertNull(player.getKnownState());
        }
    }
}
