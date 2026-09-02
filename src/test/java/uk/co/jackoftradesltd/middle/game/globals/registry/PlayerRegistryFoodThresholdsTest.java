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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.TimedGrade;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link PlayerRegistry#setPlayerTimedEffects(List)} and the six {@code getPyFood*}
 * accessors — the port of C filling its {@code PY_FOOD_*} globals from the {@code FOOD} timed
 * effect ({@code player-timed.c:321-336}).
 *
 * <p><b>Where the expected values come from.</b> The grade maxima in
 * {@code lib/gamedata/player_timed.txt:280-285} are {@code 1 / 4 / 8 / 15 / 90 / 100}, and C
 * multiplies each by {@code z_info->food_value}, which {@code lib/gamedata/constants.txt:204} sets
 * to 100 ({@code player-timed.c:263, 322}). So a loaded game holds {@code 100 / 400 / 800 / 1500 /
 * 9000 / 10000}. The fixtures below feed in already-scaled maxima, because that is what the port's
 * assembler produces — the scale is applied there, as C applies it in its parser — and the
 * assertions are those six products taken from the C data rather than from the port.
 *
 * <p><b>Name matching, not position.</b> C compares each grade's name against a fixed list of six
 * strings, so the mapping is by name and a grade whose name is not one of them contributes
 * nothing. The interesting cases are therefore grades in an unexpected order, a grade with an
 * unrecognised name, and a dummy name — C reduces a one-character name to {@code NULL} and skips
 * it entirely. Each is tested, because a port that mapped by index instead would pass on the
 * shipped file and fail on all three.
 *
 * <p><b>Global state.</b> The thresholds are static and the whole suite shares them, so every test
 * here restores what it found. The registry's effect list is saved and put back too, since seeding
 * it is how the thresholds get written at all.
 *
 * <p>Class PlayerRegistryFoodThresholdsTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
class PlayerRegistryFoodThresholdsTest {

    /**
     * The names of the six threshold fields, in the ascending order the grades appear in.
     */
    private static final String[] FIELDS = {
            "PY_FOOD_STARVE", "PY_FOOD_FAINT", "PY_FOOD_WEAK",
            "PY_FOOD_HUNGRY", "PY_FOOD_FULL", "PY_FOOD_MAX"};

    /**
     * The thresholds as they were before this class ran, restored after each test.
     */
    private static int[] savedThresholds;

    /**
     * The registry's effect list as it was before this class ran.
     */
    private static List<PlayerTimedEffect> savedEffects;

    /**
     * Takes a copy of the global state this class is about to overwrite.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeAll
    @SuppressWarnings("unchecked")
    static void saveGlobals() throws Exception {
        savedThresholds = new int[FIELDS.length];
        for (int i = 0; i < FIELDS.length; i++) {
            savedThresholds[i] = field(FIELDS[i]).getInt(null);
        }
        savedEffects = (List<PlayerTimedEffect>) field("playerTimedEffects").get(null);
    }

    /**
     * Puts the effect list back once the class is done.
     *
     * @throws Exception if the field cannot be reached
     */
    @AfterAll
    static void restoreEffects() throws Exception {
        field("playerTimedEffects").set(null, savedEffects);
    }

    /**
     * Reaches one of the registry's private static fields.
     *
     * @param name the field's name
     * @return the accessible field
     * @throws Exception if it cannot be reached
     */
    private static Field field(String name) throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /**
     * Builds one grade. Only the maximum and the name matter to the code under test; the colour and
     * messages are carried along because a real grade has them.
     *
     * @param grade  the grade's ordinal within its effect
     * @param max    the already-scaled maximum
     * @param status the grade's name, as C's {@code l->name}
     * @return the grade
     */
    private static TimedGrade grade(int grade, int max, String status) {
        return new TimedGrade(grade, ColourEnum.COLOUR_GREEN, max, status, "up", "down");
    }

    /**
     * Builds a timed effect carrying the given grades.
     *
     * @param name   the effect this is the definition of
     * @param grades its grades, in ascending order
     * @return the effect
     */
    private static PlayerTimedEffect effect(TimedEffect name, List<TimedGrade> grades) {
        return new PlayerTimedEffect(name, "test", null, null, null, null, List.of(), grades,
                (Effect) null, (Effect) null, false, 0, ObjectFlag.OF_NONE, false,
                ElementEnum.ELEM_NONE, null, null);
    }

    /**
     * The six grades of {@code player_timed.txt}'s {@code FOOD} effect, with the maxima already
     * multiplied by the shipped {@code player:food-value} of 100.
     *
     * @return the grades, in file order
     */
    private static List<TimedGrade> shippedFoodGrades() {
        return List.of(
                grade(1, 100, "Starving"),
                grade(2, 400, "Faint"),
                grade(3, 800, "Weak"),
                grade(4, 1500, "Hungry"),
                grade(5, 9000, "Fed"),
                grade(6, 10000, "Full"));
    }

    /**
     * Reads all six thresholds in ascending order.
     *
     * @return the thresholds
     */
    private static int[] thresholds() {
        return new int[]{
                PlayerRegistry.getPyFoodStarve(), PlayerRegistry.getPyFoodFaint(),
                PlayerRegistry.getPyFoodWeak(), PlayerRegistry.getPyFoodHungry(),
                PlayerRegistry.getPyFoodFull(), PlayerRegistry.getPyFoodMax()};
    }

    /**
     * Zeroes the thresholds so that a test can tell a value that was written from one that was
     * left behind by an earlier test or by the real data loader.
     *
     * @throws Exception if a field cannot be reached
     */
    private static void clearThresholds() throws Exception {
        for (String name : FIELDS) {
            field(name).setInt(null, 0);
        }
    }

    /**
     * Puts the thresholds back after every test, so neither a later test in this class nor a later
     * class sees a fixture's numbers.
     *
     * @throws Exception if a field cannot be reached
     */
    @AfterEach
    void restoreThresholds() throws Exception {
        for (int i = 0; i < FIELDS.length; i++) {
            field(FIELDS[i]).setInt(null, savedThresholds[i]);
        }
    }

    /**
     * The shipped data, which is what the game actually runs on.
     */
    @Nested
    @DisplayName("the shipped FOOD effect")
    class ShippedData {

        /**
         * Every threshold takes the maximum of the grade whose name it is spelled after. These are
         * the {@code player_timed.txt} percentages times the {@code constants.txt} food value.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("fills all six thresholds from the grade maxima")
        void fillsAllSix() throws Exception {
            clearThresholds();
            PlayerRegistry.setPlayerTimedEffects(
                    List.of(effect(TimedEffect.TMD_FOOD, shippedFoodGrades())));

            assertAll(
                    () -> assertEquals(100, PlayerRegistry.getPyFoodStarve(), "Starving"),
                    () -> assertEquals(400, PlayerRegistry.getPyFoodFaint(), "Faint"),
                    () -> assertEquals(800, PlayerRegistry.getPyFoodWeak(), "Weak"),
                    () -> assertEquals(1500, PlayerRegistry.getPyFoodHungry(), "Hungry"),
                    () -> assertEquals(9000, PlayerRegistry.getPyFoodFull(), "Fed"),
                    () -> assertEquals(10000, PlayerRegistry.getPyFoodMax(), "Full"));
        }

        /**
         * The effect list itself is registered as well as read, since the accessors are only half
         * of what this method is for.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("registers the effects it was given")
        void registersTheEffects() throws Exception {
            clearThresholds();
            PlayerTimedEffect food = effect(TimedEffect.TMD_FOOD, shippedFoodGrades());
            PlayerRegistry.setPlayerTimedEffects(List.of(food));

            assertEquals(List.of(food), PlayerRegistry.getPlayerTimedEffects());
        }

        /**
         * The order of the six thresholds is ascending, which is not decoration: {@code calcBonuses}
         * subtracts {@code PY_FOOD_FULL} from the counter and divides by the gap up to
         * {@code PY_FOOD_MAX}, so a pair out of order would divide by a negative number.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("leaves the thresholds strictly ascending")
        void thresholdsAscend() throws Exception {
            clearThresholds();
            PlayerRegistry.setPlayerTimedEffects(
                    List.of(effect(TimedEffect.TMD_FOOD, shippedFoodGrades())));

            int[] values = thresholds();
            for (int i = 1; i < values.length; i++) {
                assertEquals(true, values[i] > values[i - 1],
                        FIELDS[i] + " (" + values[i] + ") must exceed "
                                + FIELDS[i - 1] + " (" + values[i - 1] + ")");
            }
        }
    }

    /**
     * The cases that separate matching by name from matching by position.
     */
    @Nested
    @DisplayName("grade name matching")
    class NameMatching {

        /**
         * Grades presented out of their usual order still land in the right thresholds, because C
         * keys on the name and not on where the grade sits in the list.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("follows the name, not the position")
        void followsTheName() throws Exception {
            clearThresholds();
            List<TimedGrade> shuffled = new ArrayList<>(shippedFoodGrades());
            java.util.Collections.reverse(shuffled);
            PlayerRegistry.setPlayerTimedEffects(List.of(effect(TimedEffect.TMD_FOOD, shuffled)));

            assertAll(
                    () -> assertEquals(100, PlayerRegistry.getPyFoodStarve(), "Starving"),
                    () -> assertEquals(10000, PlayerRegistry.getPyFoodMax(), "Full"));
        }

        /**
         * A grade whose name is none of the six contributes nothing, and leaves the thresholds it
         * did not name alone. C's chain of comparisons simply falls through.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("ignores an unrecognised grade name")
        void ignoresUnknownName() throws Exception {
            clearThresholds();
            PlayerRegistry.setPlayerTimedEffects(List.of(effect(TimedEffect.TMD_FOOD,
                    List.of(grade(1, 100, "Starving"), grade(2, 7777, "Peckish")))));

            assertAll(
                    () -> assertEquals(100, PlayerRegistry.getPyFoodStarve(), "Starving"),
                    () -> assertEquals(0, PlayerRegistry.getPyFoodFaint(), "Faint untouched"),
                    () -> assertEquals(0, PlayerRegistry.getPyFoodMax(), "Full untouched"));
        }

        /**
         * A dummy name — the single character C nulls out — matches nothing either. The FOOD effect
         * in {@code player_timed.txt} has no dummy grade, but the mechanism is shared with the
         * effects that do.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("ignores a dummy grade name")
        void ignoresDummyName() throws Exception {
            clearThresholds();
            PlayerRegistry.setPlayerTimedEffects(List.of(effect(TimedEffect.TMD_FOOD,
                    List.of(grade(1, 100, " "), grade(2, 400, "Faint")))));

            assertAll(
                    () -> assertEquals(0, PlayerRegistry.getPyFoodStarve(), "Starving untouched"),
                    () -> assertEquals(400, PlayerRegistry.getPyFoodFaint(), "Faint"));
        }
    }

    /**
     * The cases in which nothing should be written at all.
     */
    @Nested
    @DisplayName("effects other than FOOD")
    class OtherEffects {

        /**
         * Another effect's grades are not consulted, however they are named. C tests the effect's
         * own name before looking at a single grade, so a {@code STUN} grade called "Fed" would
         * still be passed over.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("leave the thresholds alone")
        void leaveThresholdsAlone() throws Exception {
            clearThresholds();
            PlayerRegistry.setPlayerTimedEffects(List.of(effect(TimedEffect.TMD_STUN,
                    List.of(grade(1, 50, "Fed"), grade(2, 100, "Full")))));

            assertAll(
                    () -> assertEquals(0, PlayerRegistry.getPyFoodFull(), "Fed"),
                    () -> assertEquals(0, PlayerRegistry.getPyFoodMax(), "Full"));
        }

        /**
         * With no FOOD effect at all the thresholds stay at zero rather than throwing. C never
         * enters the branch; the port's search finds nothing. A game in that state is unplayable
         * either way, but the loader is not the place it should fall over.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a list without FOOD leaves the thresholds at zero")
        void noFoodEffect() throws Exception {
            clearThresholds();
            PlayerRegistry.setPlayerTimedEffects(List.of(effect(TimedEffect.TMD_STUN, List.of())));

            assertAll(
                    () -> assertEquals(0, PlayerRegistry.getPyFoodStarve(), "Starving"),
                    () -> assertEquals(0, PlayerRegistry.getPyFoodMax(), "Full"));
        }

        /**
         * An empty list is tolerated for the same reason.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an empty list leaves the thresholds at zero")
        void emptyList() throws Exception {
            clearThresholds();
            PlayerRegistry.setPlayerTimedEffects(List.of());

            assertEquals(0, PlayerRegistry.getPyFoodFull());
        }
    }
}
