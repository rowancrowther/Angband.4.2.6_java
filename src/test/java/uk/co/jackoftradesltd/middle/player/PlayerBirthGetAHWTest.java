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
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#getAHW}, the port of C's {@code get_ahw}
 * ({@code player-birth.c:353-361}):
 *
 * <pre>{@code
 * p->age = p->race->b_age + randint1(p->race->m_age);
 * p->ht = p->ht_birth = Rand_normal(p->race->base_hgt, p->race->mod_hgt);
 * p->wt = p->wt_birth = Rand_normal(p->race->base_wgt, p->race->mod_wgt);
 * }</pre>
 *
 * <p>Two of those three lines are chained assignments, and that is the property worth testing. The
 * roll happens <em>once</em> and lands in both the working field and the birth copy; a port that
 * rolled separately for each would produce characters whose recorded birth height was not the
 * height they were born at, and would pass any test that only checked the ranges. Each pair is
 * therefore asserted equal, over many seeds, rather than asserted to be any particular number.
 *
 * <p>The age is different in kind and is checked against C's arithmetic directly.
 * {@code randint1(m_age)} returns {@code 1 .. m_age} ({@code z-rand.h:85}), so the age lands in
 * {@code b_age + 1 .. b_age + m_age} — the base is a floor the character is always at least a year
 * past, unlike the height and weight bases, which are means. The Human's {@code 14:6}
 * ({@code p_race.txt}) therefore gives ages of 15 to 20 and never 14, and a race with a spread of
 * zero still ages a year, because {@code randint1} of zero is one.
 *
 * <p>The rolls are driven through {@link RandomValueUtils#stateInit(long)} so each case is
 * reproducible, and the ranged assertions are run over a sweep of seeds rather than one.
 *
 * <p>What is deliberately <em>not</em> asserted here is the shape of the height and weight
 * distributions. Those come from {@link RandomValueUtils#normal}, which is a separate port with its
 * own tests; pinning its output here would test that method twice and this one not at all.
 *
 * <p>Class PlayerBirthGetAHWTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthGetAHWTest {

    /**
     * The player being born, fresh for each test.
     */
    private Player player;

    /**
     * A race carrying the three pairs {@code get_ahw} reads.
     *
     * @param name       the race's display name
     * @param baseAge    C's {@code b_age}
     * @param modAge     C's {@code m_age}
     * @param baseHeight C's {@code base_hgt}
     * @param modHeight  C's {@code mod_hgt}
     * @param baseWeight C's {@code base_wgt}
     * @param modWeight  C's {@code mod_wgt}
     * @return the race
     */
    private static PlayerRace race(String name, int baseAge, int modAge, int baseHeight,
                                   int modHeight, int baseWeight, int modWeight) {
        return new PlayerRace(name, 0, 10, 100, baseAge, modAge, baseHeight, modHeight, baseWeight,
                modWeight, 0, null, Map.of(), Map.of(), new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class), null, Map.of());
    }

    /**
     * The Human's shipped numbers: {@code age:14:6}, {@code height:69:10}, {@code weight:165:35}
     * ({@code p_race.txt}).
     *
     * @return the race
     */
    private static PlayerRace human() {
        return race("Human", 14, 6, 69, 10, 165, 35);
    }

    /**
     * A new player with no race yet.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Reads one of the player's private int fields, none of which have getters yet.
     *
     * @param name the field's name
     * @return its value
     * @throws Exception if the field cannot be reached
     */
    private int field(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * Gives the player a race, which C has already asserted is set by the time {@code get_ahw} runs.
     *
     * @param race the race to be born into
     * @throws Exception if the field cannot be reached
     */
    private void giveRace(PlayerRace race) throws Exception {
        Field field = Player.class.getDeclaredField("race");
        field.setAccessible(true);
        field.set(player, race);
    }

    /**
     * The age line, {@code p->age = p->race->b_age + randint1(p->race->m_age)}.
     */
    @Nested
    @DisplayName("the age roll")
    class Age {

        /**
         * The Human's age lands in 15 to 20 inclusive and never on the base itself, because
         * {@code randint1} starts at one. Swept over seeds so a single lucky roll cannot hide a
         * {@code randint0} in place of {@code randint1}.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("lands strictly above the base, within the spread")
        void ageRange() throws Exception {
            giveRace(human());

            for (long seed = 0; seed < 200; seed++) {
                RandomValueUtils.stateInit(seed);
                PlayerBirth.getAHW(player);

                int age = field("age");
                assertTrue(age >= 15, "seed " + seed + " gave age " + age);
                assertTrue(age <= 20, "seed " + seed + " gave age " + age);
            }
        }

        /**
         * Both ends of the Human's range are actually reached across the sweep — a port that
         * clipped a bound would still satisfy the range assertion above.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("reaches both ends of the range")
        void ageReachesBothEnds() throws Exception {
            giveRace(human());

            boolean sawLow = false;
            boolean sawHigh = false;
            for (long seed = 0; seed < 500 && !(sawLow && sawHigh); seed++) {
                RandomValueUtils.stateInit(seed);
                PlayerBirth.getAHW(player);

                if (field("age") == 15) sawLow = true;
                if (field("age") == 20) sawHigh = true;
            }

            assertTrue(sawLow, "never rolled the lowest age, 15");
            assertTrue(sawHigh, "never rolled the highest age, 20");
        }

        /**
         * The High-Elf's {@code age:100:30} ({@code p_race.txt}) is the widest shipped spread, and
         * the Elf's {@code age:75:75} the one where base and spread are equal. Neither is special to
         * the arithmetic, which is the point: the same {@code b_age + 1 .. b_age + m_age} holds.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("holds for the wide-spread races")
        void ageWideSpreads() throws Exception {
            for (long seed = 0; seed < 100; seed++) {
                giveRace(race("High-Elf", 100, 30, 90, 10, 190, 20));
                RandomValueUtils.stateInit(seed);
                PlayerBirth.getAHW(player);
                assertTrue(field("age") >= 101 && field("age") <= 130,
                        "seed " + seed + " gave High-Elf age " + field("age"));

                giveRace(race("Elf", 75, 75, 90, 10, 90, 10));
                RandomValueUtils.stateInit(seed);
                PlayerBirth.getAHW(player);
                assertTrue(field("age") >= 76 && field("age") <= 150,
                        "seed " + seed + " gave Elf age " + field("age"));
            }
        }

        /**
         * A spread of zero still ages the character a year: {@code randint1(0)} is one, not zero
         * ({@code z-rand.h:85} over {@code Rand_div}'s degenerate case). No shipped race does this,
         * but it is the boundary the port's {@code randInt1} has to agree with C on.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a spread of zero still adds a year")
        void ageZeroSpread() throws Exception {
            giveRace(race("Fixed", 20, 0, 69, 10, 165, 35));

            for (long seed = 0; seed < 20; seed++) {
                RandomValueUtils.stateInit(seed);
                PlayerBirth.getAHW(player);
                assertEquals(21, field("age"), "seed " + seed);
            }
        }
    }

    /**
     * The two chained assignments, {@code p->ht = p->ht_birth = ...} and {@code p->wt = ...}.
     */
    @Nested
    @DisplayName("the height and weight rolls")
    class HeightAndWeight {

        /**
         * One roll, two fields. The working height and its birth copy must come out equal, and so
         * must the weight pair — this is what C's chained assignment guarantees and what a port
         * rolling twice would break invisibly.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("each pair gets a single roll")
        void pairsAgree() throws Exception {
            giveRace(human());

            for (long seed = 0; seed < 200; seed++) {
                RandomValueUtils.stateInit(seed);
                PlayerBirth.getAHW(player);

                assertEquals(player.getHeight(), field("htBirth"), "height pair, seed " + seed);
                assertEquals(player.getWeight(), field("wtBirth"), "weight pair, seed " + seed);
            }
        }

        /**
         * Height and weight are rolled from their own race fields and do not cross. The check is
         * that the two pairs are independent of each other: a race whose height numbers differ
         * wildly from its weight numbers must not produce a height equal to its weight across a
         * whole sweep, which is what a copy-pasted line reading the wrong pair would give.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("height and weight read their own race fields")
        void heightAndWeightAreIndependent() throws Exception {
            giveRace(race("Lopsided", 14, 6, 40, 3, 400, 40));

            boolean sawDifference = false;
            for (long seed = 0; seed < 100 && !sawDifference; seed++) {
                RandomValueUtils.stateInit(seed);
                PlayerBirth.getAHW(player);
                if (player.getHeight() != player.getWeight()) sawDifference = true;
            }

            assertTrue(sawDifference, "height and weight never differed - one pair is reading the other's fields");
        }
    }

    /**
     * What calling it twice does, and what calling it without a race does.
     */
    @Nested
    @DisplayName("the call itself")
    class CallSemantics {

        /**
         * Nothing is preserved across calls. C calls {@code get_ahw} once per generated character
         * ({@code player-birth.c:1018}) and again for every candidate the roller produces
         * ({@code player-birth.c:1173}), so a second call must reroll all three — birth copies
         * included, since they are written by the same statements.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a second call rerolls everything")
        void secondCallRerolls() throws Exception {
            giveRace(human());

            RandomValueUtils.stateInit(1L);
            PlayerBirth.getAHW(player);
            int firstAge = field("age");
            int firstHeight = player.getHeight();
            int firstWeight = player.getWeight();

            boolean changed = false;
            for (long seed = 2; seed < 40 && !changed; seed++) {
                RandomValueUtils.stateInit(seed);
                PlayerBirth.getAHW(player);
                changed = field("age") != firstAge
                        || player.getHeight() != firstHeight
                        || player.getWeight() != firstWeight;
            }

            assertTrue(changed, "repeated calls never produced a different character");
            assertEquals(player.getHeight(), field("htBirth"), "birth height not rerolled with the working height");
            assertEquals(player.getWeight(), field("wtBirth"), "birth weight not rerolled with the working weight");
        }

        /**
         * The race must already be chosen. C asserts it upstream rather than guarding here, and the
         * port dereferences it the same way — a character with no race is a caller's bug, not a case
         * to be tolerated quietly the way {@code embody} tolerates it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a player with no race is a caller error")
        void noRaceThrows() throws Exception {
            giveRace(null);
            assertThrows(NullPointerException.class, () -> PlayerBirth.getAHW(player));
        }
    }
}
