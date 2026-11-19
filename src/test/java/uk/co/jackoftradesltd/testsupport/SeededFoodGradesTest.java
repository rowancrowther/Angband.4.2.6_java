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

package uk.co.jackoftradesltd.testsupport;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.TimedGrade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins what {@link SeededFoodGrades} puts in the registry: the six {@code PY_FOOD_*} thresholds
 * {@code player_timed.txt} gives after {@code food-value:100} scaling, the grade that the birth code's
 * starting food of {@code PY_FOOD_FULL - 1} falls in, and that the extension puts the thresholds back
 * when it finishes.
 *
 * <p>The threshold pins hold whether the extension seeds the registry or finds the real
 * {@code player_timed.txt} already loaded by an earlier suite, since the two agree. The restore pin
 * drives the extension by hand in a class with no {@code @ExtendWith}, so that nothing else has
 * seeded the registry first.
 *
 * <p>Class SeededFoodGradesTest coded on 261119, commented in full on 261119.
 *
 * @author Rowan Crowther
 */
class SeededFoodGradesTest {

    /**
     * Checks the six thresholds the extension leaves behind while a class is running.
     */
    @Nested
    @ExtendWith(SeededFoodGrades.class)
    class WhileSeeded {

        @Test
        void theSixThresholdsAreTheScaledShippedValues() {
            assertEquals(100, PlayerRegistry.getPyFoodStarve());
            assertEquals(400, PlayerRegistry.getPyFoodFaint());
            assertEquals(800, PlayerRegistry.getPyFoodWeak());
            assertEquals(1500, PlayerRegistry.getPyFoodHungry());
            assertEquals(9000, PlayerRegistry.getPyFoodFull());
            assertEquals(10000, PlayerRegistry.getPyFoodMax());
        }

        @Test
        void aBirthStartingFoodOfFullMinusOneIsInTheFedGrade() {
            // PlayerBirth sets food to getPyFoodFull() - 1; calcBonuses skips the hunger block only if that is "Fed".
            int food = PlayerRegistry.getPyFoodFull() - 1;
            List<TimedGrade> grades = SeededFoodGrades.foodEffect().getGrade();

            TimedGrade found = grades.stream().filter(g -> g.max() >= food).findFirst().orElseThrow();

            assertEquals("Fed", found.status());
        }

        @Test
        void theHungerDivisorIsNotZero() {
            assertTrue(PlayerRegistry.getPyFoodHungry() > 0);
        }
    }

    /**
     * Drives the extension by hand, so no other extension has seeded the registry first.
     */
    @Nested
    class AroundTheExtension {

        @Test
        void theThresholdsAreSeededAndThenPutBack() throws Exception {
            int hungryBefore = PlayerRegistry.getPyFoodHungry();
            int fullBefore = PlayerRegistry.getPyFoodFull();
            SeededFoodGrades extension = new SeededFoodGrades();

            extension.beforeAll(null);
            int hungryDuring = PlayerRegistry.getPyFoodHungry();
            int fullDuring = PlayerRegistry.getPyFoodFull();
            extension.afterAll(null);

            assertEquals(1500, hungryDuring);
            assertEquals(9000, fullDuring);
            assertEquals(hungryBefore, PlayerRegistry.getPyFoodHungry());
            assertEquals(fullBefore, PlayerRegistry.getPyFoodFull());
        }

        @Test
        void aSecondBeforeAllWhileSeededChangesNothing() throws Exception {
            // A nested test class re-enters beforeAll with the same extension instance.
            SeededFoodGrades extension = new SeededFoodGrades();
            int hungryBefore = PlayerRegistry.getPyFoodHungry();

            extension.beforeAll(null);
            extension.beforeAll(null);
            extension.afterAll(null);

            assertEquals(hungryBefore, PlayerRegistry.getPyFoodHungry());
        }

        @Test
        void theSeededEntryIsTheFoodEffectWithSixGrades() {
            PlayerTimedEffect food = SeededFoodGrades.foodEffect();

            assertEquals(6, food.getGrade().size());
            assertEquals(1, food.getLowerBound());
        }
    }
}
