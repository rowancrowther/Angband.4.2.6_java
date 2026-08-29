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

package uk.co.jackoftrades.backend.parser.playertimed;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.data.PlayerData;
import uk.co.jackoftrades.middle.player.PlayerTimedEffect;
import uk.co.jackoftrades.middle.player.TimedGrade;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@code grade:} handling in {@link PlayerTimedAssembler} — the port of C's
 * {@code parse_player_timed_grade} ({@code player-timed.c:243-336}).
 *
 * <p><b>The FOOD scaling is the part that has already gone wrong twice.</b> C multiplies a grade
 * maximum by {@code z_info->food_value} for the {@code FOOD} effect and by one for every other
 * ({@code player-timed.c:263, 321-322}), so the {@code FOOD} bands live on a scale a hundred times
 * larger than the numbers in {@code player_timed.txt} while every other effect's bands do not.
 * Applying the scale everywhere inflates the stun bands by the same factor, and then
 * {@code timedGradeEq(TMD_STUN, "Heavy Stun")} can never be true for any realistic stun counter —
 * the to-hit, to-damage and device penalties simply stop happening, with nothing to see. Applying it
 * nowhere leaves the food constants and the food bands on two different scales. Both halves are
 * pinned below.
 *
 * <p>The validation is worth as much as the scaling. C rejects a grade maximum that is not positive
 * or that would not fit an {@code int16_t} <em>after</em> scaling — the limit is
 * {@code 32767 / food_scl}, so a FOOD grade is capped at 327 rather than 32767 — and it rejects a
 * grade that does not sit strictly above the one before it. Getting the ceiling's direction wrong
 * is easy and invisible: it only shows on a data file nobody has written yet.
 *
 * <p>Class PlayerTimedAssemblerGradeTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class PlayerTimedAssemblerGradeTest {

    /**
     * The food-value the shipped {@code constants.txt} sets, and the scale C applies to FOOD.
     */
    private static final int FOOD_SCALE = 100;

    /**
     * Whatever {@link GameConstants} held before this class ran.
     */
    private static Object savedConstants;

    /**
     * Seeds the one constant the assembler reads, so the class does not depend on another suite
     * having loaded {@code constants.txt} first.
     *
     * @throws Exception if the backing field cannot be reached
     */
    @BeforeAll
    static void seedConstants() throws Exception {
        Field f = GameConstants.class.getDeclaredField("data");
        f.setAccessible(true);
        savedConstants = f.get(null);
        if (savedConstants == null) {
            f.set(null, new GameConstantsData(null, null, null, null, null, null, null, null,
                    new PlayerData(20, 10, 600, FOOD_SCALE), null, List.of(), null, List.of(),
                    null, List.of(), null, List.of()));
        }
    }

    @AfterAll
    static void restoreConstants() throws Exception {
        Field f = GameConstants.class.getDeclaredField("data");
        f.setAccessible(true);
        f.set(null, savedConstants);
    }

    /**
     * One {@code grade:} line, still textual, as the parser hands it over.
     *
     * @param max    the maximum, as written in the data file
     * @param status the band's name
     * @return the parse record
     */
    private static PlayerTimedParseRecord.PlayerTimedGradeParseRecord grade(String max, String status) {
        return new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("w", max, status, "down", "up");
    }

    /**
     * A whole effect record carrying the given grades and nothing else of interest.
     *
     * @param name   the effect's name, which decides whether the food scale applies
     * @param grades its grade lines, in file order
     * @return the parse record
     */
    private static PlayerTimedParseRecord record(String name,
                                                 PlayerTimedParseRecord.PlayerTimedGradeParseRecord... grades) {
        return new PlayerTimedParseRecord(name, "a description", List.of(grades), "", "", "", "",
                List.of(), null, null, "", "", "", "", "", "", List.of(), 1);
    }

    /**
     * Runs the assembler over one record.
     *
     * @param record the record to assemble
     * @param errors the soft-error channel to fill
     * @return the effects that survived
     */
    private static List<PlayerTimedEffect> assemble(PlayerTimedParseRecord record, List<String> errors) {
        return new PlayerTimedAssembler().assemble(List.of(record), errors);
    }

    /**
     * The scale, and the fact that it applies to one effect only.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("food scaling")
    class FoodScaling {

        /**
         * The shipped {@code FOOD} bands, scaled: 1, 4, 8, 15, 90, 100 in the file become 100, 400,
         * 800, 1500, 9000, 10000 in the registry — the same numbers {@code Food} holds, which is
         * what lets the food block in {@code calcBonuses} compare the two.
         *
         * <p>The bands are indexed from one, index zero being the implicit "off" band; see
         * {@link OffGrade}.
         */
        @Test
        @DisplayName("FOOD grades are multiplied by the food-value constant")
        void foodGradesAreScaled() {
            List<String> errors = new ArrayList<>();
            List<PlayerTimedEffect> effects = assemble(record("FOOD",
                    grade("1", "Starving"), grade("4", "Faint"), grade("8", "Weak"),
                    grade("15", "Hungry"), grade("90", "Fed"), grade("100", "Full")), errors);

            List<TimedGrade> grades = effects.get(0).getGrade();

            assertAll(
                    () -> assertTrue(errors.isEmpty(), errors.toString()),
                    () -> assertEquals(1 * FOOD_SCALE, grades.get(1).max()),
                    () -> assertEquals(15 * FOOD_SCALE, grades.get(4).max()),
                    () -> assertEquals(90 * FOOD_SCALE, grades.get(5).max()),
                    () -> assertEquals(100 * FOOD_SCALE, grades.get(6).max()),
                    () -> assertEquals(0, grades.get(0).max(),
                            "the off band is never scaled"));
        }

        /**
         * Every other effect keeps its file values untouched. The shipped stun bands are the case
         * that matters: scaled, {@code timedGradeEq} could never place a stunned player in a band
         * again, and the stun penalties would quietly stop applying.
         */
        @Test
        @DisplayName("other effects keep their grade maxima unscaled")
        void otherEffectsAreNotScaled() {
            List<String> errors = new ArrayList<>();
            List<PlayerTimedEffect> effects = assemble(record("STUN",
                    grade("50", "Stun"), grade("150", "Heavy Stun"),
                    grade("10000", "Knocked Out")), errors);

            List<TimedGrade> grades = effects.get(0).getGrade();

            assertAll(
                    () -> assertTrue(errors.isEmpty(), errors.toString()),
                    () -> assertEquals(50, grades.get(1).max()),
                    () -> assertEquals(150, grades.get(2).max()),
                    () -> assertEquals(10000, grades.get(3).max()));
        }
    }

    /**
     * The implicit "off" band that heads every effect's list.
     *
     * <p>C never writes this band in {@code player_timed.txt}: {@code parse_player_timed_grade}
     * allocates a zeroed node the first time an effect declares a {@code grade:} line
     * ({@code player-timed.c:270-273}) and numbers the declared bands from one
     * ({@code player-timed.c:287}). It carries grade 0, maximum 0, and a null name and pair of
     * messages.
     *
     * <p><b>Three separate pieces of behaviour rest on it,</b> which is why it is pinned rather
     * than treated as a parser artefact. It is the band a counter of zero maps to, so the search
     * {@code while (v > grade->max) grade = grade->next;} is total over the whole range and needs
     * no "is the effect off?" special case — both {@code player_set_timed} and
     * {@code player_timed_grade_eq} rely on that. Its null {@code down_msg} is what makes a lapse
     * fall past the grade-message arms of {@code player_set_timed} and reach the {@code on_end}
     * text with {@code MSG_RECOVER}. And because the declared bands start at one, the onset of an
     * effect reads as {@code new_grade->grade > current_grade->grade} and prints the up-message:
     * numbering the first declared band zero instead would collide with the head and silently
     * lose every "You are blind."-style message, while leaving the lapse path working.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the implicit off band")
    class OffGrade {

        /**
         * The head's own fields, against a single-band effect where it is unambiguous.
         */
        @Test
        @DisplayName("a zeroed, unnamed band heads the list")
        void headIsZeroedAndUnnamed() {
            List<String> errors = new ArrayList<>();
            List<PlayerTimedEffect> effects =
                    assemble(record("BLIND", grade("10000", "Blind")), errors);

            List<TimedGrade> grades = effects.get(0).getGrade();

            assertAll(
                    () -> assertTrue(errors.isEmpty(), errors.toString()),
                    () -> assertEquals(2, grades.size(), "one declared band plus the head"),
                    () -> assertEquals(0, grades.get(0).Grade()),
                    () -> assertEquals(0, grades.get(0).max()),
                    () -> assertNull(grades.get(0).status()),
                    () -> assertNull(grades.get(0).upMsg()),
                    () -> assertNull(grades.get(0).downMsg()),
                    () -> assertEquals(ColourEnum.COLOUR_DARK, grades.get(0).colour(),
                            "C's mem_zalloc leaves the colour at 0, which is COLOUR_DARK"));
        }

        /**
         * Declared bands are numbered from one. If they started at zero the first band would share
         * its number with the head, and the onset comparison in {@code player_set_timed} would
         * never see a rise out of the off band.
         */
        @Test
        @DisplayName("declared bands are numbered from one")
        void declaredBandsAreNumberedFromOne() {
            List<String> errors = new ArrayList<>();
            List<PlayerTimedEffect> effects = assemble(record("STUN",
                    grade("50", "Stun"), grade("150", "Heavy Stun"),
                    grade("10000", "Knocked Out")), errors);

            List<TimedGrade> grades = effects.get(0).getGrade();

            assertAll(
                    () -> assertTrue(errors.isEmpty(), errors.toString()),
                    () -> assertEquals(4, grades.size()),
                    () -> assertEquals(0, grades.get(0).Grade()),
                    () -> assertEquals(1, grades.get(1).Grade()),
                    () -> assertEquals(2, grades.get(2).Grade()),
                    () -> assertEquals(3, grades.get(3).Grade()),
                    () -> assertEquals("Stun", grades.get(1).status()),
                    () -> assertEquals("Knocked Out", grades.get(3).status()));
        }

        /**
         * One head per effect, not one per load: the list is built inside the per-record loop, so
         * two effects assembled together must not share or double up on it.
         */
        @Test
        @DisplayName("each effect gets exactly one head")
        void eachEffectGetsItsOwnHead() {
            List<String> errors = new ArrayList<>();
            List<PlayerTimedEffect> effects = new PlayerTimedAssembler().assemble(
                    List.of(record("BLIND", grade("10000", "Blind")),
                            record("STUN", grade("50", "Stun"), grade("150", "Heavy Stun"))),
                    errors);

            assertAll(
                    () -> assertTrue(errors.isEmpty(), errors.toString()),
                    () -> assertEquals(2, effects.size()),
                    () -> assertEquals(2, effects.get(0).getGrade().size()),
                    () -> assertEquals(3, effects.get(1).getGrade().size()),
                    () -> assertEquals(0, effects.get(1).getGrade().get(0).max(),
                            "the second effect is headed too"),
                    () -> assertEquals(1, effects.get(1).getGrade().get(1).Grade(),
                            "and its numbering restarts at one"));
        }

        /**
         * The head is the only band with a zero maximum — the reason C rejects a declared
         * {@code max} of zero, which {@link Validation#rejectsNonPositive()} covers from the other
         * side. A second zero-maximum band would be unreachable, since the search stops at the
         * first maximum the value does not exceed.
         */
        @Test
        @DisplayName("no declared band can share the head's zero maximum")
        void headsZeroMaximumIsUnique() {
            List<String> errors = new ArrayList<>();
            List<PlayerTimedEffect> effects = assemble(record("STUN",
                    grade("50", "Stun"), grade("150", "Heavy Stun")), errors);

            List<TimedGrade> grades = effects.get(0).getGrade();

            assertAll(
                    () -> assertTrue(errors.isEmpty(), errors.toString()),
                    () -> assertEquals(1, grades.stream().filter(g -> g.max() == 0).count()));
        }
    }

    /**
     * The two checks C makes on every {@code grade:} line.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("validation")
    class Validation {

        /**
         * Zero would collide with the implicit "off" band, whose maximum is zero, and a negative
         * maximum is meaningless. C rejects both with the same test.
         */
        @Test
        @DisplayName("a non-positive maximum is rejected")
        void rejectsNonPositive() {
            List<String> zero = new ArrayList<>();
            List<String> negative = new ArrayList<>();
            assemble(record("STUN", grade("0", "Stun")), zero);
            assemble(record("STUN", grade("-5", "Stun")), negative);

            assertAll(
                    () -> assertFalse(zero.isEmpty(), "a zero maximum should be reported"),
                    () -> assertFalse(negative.isEmpty(), "a negative maximum should be reported"));
        }

        /**
         * The ceiling is on the value <em>after</em> scaling, because C stores a timed effect's
         * counter in an {@code int16_t}. For FOOD that caps a data-file number at 327; a test that
         * only checked 32767 would pass an implementation that multiplied where it should divide.
         */
        @Test
        @DisplayName("the ceiling applies to the scaled value, so FOOD is capped far lower")
        void ceilingAppliesAfterScaling() {
            List<String> foodTooBig = new ArrayList<>();
            List<String> foodJustFits = new ArrayList<>();
            List<String> stunSameNumber = new ArrayList<>();

            assemble(record("FOOD", grade("400", "Fed")), foodTooBig);
            assemble(record("FOOD", grade("327", "Fed")), foodJustFits);
            assemble(record("STUN", grade("400", "Stun")), stunSameNumber);

            assertAll(
                    () -> assertFalse(foodTooBig.isEmpty(),
                            "400 scales to 40000, which does not fit an int16_t"),
                    () -> assertTrue(foodJustFits.isEmpty(), foodJustFits.toString()),
                    () -> assertTrue(stunSameNumber.isEmpty(),
                            "the same number is fine unscaled"));
        }

        /**
         * Bands are a partition of one counter, so each maximum must sit strictly above the last.
         * Equality is rejected too — two bands with the same ceiling would make the second
         * unreachable, since the band search stops at the first maximum the value does not exceed.
         */
        @Test
        @DisplayName("grades must strictly ascend")
        void gradesMustAscend() {
            List<String> descending = new ArrayList<>();
            List<String> equal = new ArrayList<>();
            List<String> ascending = new ArrayList<>();

            assemble(record("STUN", grade("150", "Stun"), grade("50", "Heavy Stun")), descending);
            assemble(record("STUN", grade("50", "Stun"), grade("50", "Heavy Stun")), equal);
            assemble(record("STUN", grade("50", "Stun"), grade("150", "Heavy Stun")), ascending);

            assertAll(
                    () -> assertFalse(descending.isEmpty(), "a descending maximum should be reported"),
                    () -> assertFalse(equal.isEmpty(), "an equal maximum should be reported"),
                    () -> assertTrue(ascending.isEmpty(), ascending.toString()));
        }

        /**
         * A bad grade takes its whole effect with it rather than leaving a gap in the band ladder.
         * C returns a parse error, which drops the record; dropping only the offending line would
         * load an effect whose bands no longer cover the counter.
         */
        @Test
        @DisplayName("a bad grade drops the whole effect, not just the line")
        void badGradeDropsTheRecord() {
            List<String> errors = new ArrayList<>();
            List<PlayerTimedEffect> effects = assemble(
                    record("STUN", grade("50", "Stun"), grade("0", "Heavy Stun")), errors);

            assertAll(
                    () -> assertFalse(errors.isEmpty()),
                    () -> assertTrue(effects.isEmpty(),
                            "the effect should not load with one band missing"));
        }

        /**
         * A malformed maximum is a parse failure rather than a validation one, and is reported the
         * same way — the assembler's contract is that one bad record never aborts the load.
         */
        @Test
        @DisplayName("a non-numeric maximum is reported rather than thrown")
        void malformedMaximumIsReported() {
            List<String> errors = new ArrayList<>();
            List<PlayerTimedEffect> effects = assemble(record("STUN", grade("lots", "Stun")), errors);

            assertAll(
                    () -> assertFalse(errors.isEmpty()),
                    () -> assertTrue(effects.isEmpty()));
        }
    }
}
