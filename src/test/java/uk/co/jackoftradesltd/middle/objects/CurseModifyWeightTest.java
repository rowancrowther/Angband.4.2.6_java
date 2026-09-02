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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link Curse#modifyWeightForCurse(int)}, the port of C's
 * {@code modify_weight_for_curse} ({@code obj-curse.c:382-430}).
 *
 * <p><b>One field, two meanings.</b> A curse's weight is a flat addend normally and a percentage
 * when the curse carries {@link ObjectFlag#OF_MULTIPLY_WEIGHT}, and nothing but that flag
 * distinguishes them. A curse of weight 150 either adds fifteen pounds or makes the item half again
 * as heavy, so reading the flag wrongly is not a small error — and since the two branches agree at
 * no useful value, a test that used one number for both would not notice.
 *
 * <p>The rest is edge cases that all have reasons in the original: rounding to nearest rather than
 * truncating, coercing a weightless item up to one so that a multiplier has something to work on,
 * clamping a negative result to zero, and saturating at {@link Short#MAX_VALUE} because C holds an
 * object's weight in an {@code int16_t}. Every one of them is a line in C that would be easy to drop
 * as an over-complication.
 *
 * <p>Class CurseModifyWeightTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class CurseModifyWeightTest {

    /**
     * A curse whose only interesting properties are its weight and whether it multiplies.
     *
     * @param weight   the curse's weight field
     * @param multiply whether it carries {@link ObjectFlag#OF_MULTIPLY_WEIGHT}
     * @return the curse
     */
    private static Curse curse(int weight, boolean multiply) {
        return new Curse("weighty", List.of(), weight, null,
                multiply ? objectFlags(ObjectFlag.OF_MULTIPLY_WEIGHT) : objectFlags(),
                Map.of(), Map.of(), 0, 0, 0, List.of(), objectFlags(), "", "");
    }

    /**
     * The additive branch — the curse's weight is tenth-pounds to add.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("without OF_MULTIPLY_WEIGHT")
    class Additive {

        /**
         * A plain addition, in both directions. A negative curse weight makes the item lighter,
         * which is an ordinary case rather than an error.
         */
        @Test
        @DisplayName("the weight is added, and may be negative")
        void addsAndSubtracts() {
            assertAll(
                    () -> assertEquals(15, curse(5, false).modifyWeightForCurse(10)),
                    () -> assertEquals(5, curse(-5, false).modifyWeightForCurse(10)));
        }

        /**
         * A reduction cannot take an item below weightless. C clamps rather than allowing a negative
         * weight, which would otherwise propagate into the carrying calculation as a credit.
         */
        @Test
        @DisplayName("a reduction cannot drive the weight below zero")
        void clampsAtZero() {
            assertEquals(0, curse(-20, false).modifyWeightForCurse(10));
        }

        /**
         * The ceiling is C's {@code int16_t}. Saturating rather than wrapping is the whole point: a
         * wrap would turn an absurdly heavy item into a weightless one.
         */
        @Test
        @DisplayName("an addition saturates rather than overflowing")
        void saturatesAtShortMax() {
            assertEquals(Short.MAX_VALUE, curse(10, false).modifyWeightForCurse(Short.MAX_VALUE - 1));
        }
    }

    /**
     * The multiplicative branch — the curse's weight is a percentage.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("with OF_MULTIPLY_WEIGHT")
    class Multiplicative {

        /**
         * The field is read as a percentage: 150 means half again as heavy, and 50 means half. The
         * same field with the same value means something entirely different here than it does in the
         * additive branch, which is the reason this class exists.
         */
        @Test
        @DisplayName("the weight is a percentage, not an addend")
        void scalesByPercentage() {
            assertAll(
                    () -> assertEquals(15, curse(150, true).modifyWeightForCurse(10)),
                    () -> assertEquals(5, curse(50, true).modifyWeightForCurse(10)));
        }

        /**
         * Rounding is to nearest, not truncation — C's {@code >= 50} test on the remainder. Three at
         * 150% is 4.5 and rounds up to 5; three at 116% is 3.48 and rounds down to 3. Truncation
         * would give 4 and 3, so only the upward case distinguishes the two.
         */
        @Test
        @DisplayName("the result rounds to nearest rather than truncating")
        void roundsToNearest() {
            assertAll(
                    () -> assertEquals(5, curse(150, true).modifyWeightForCurse(3)),
                    () -> assertEquals(3, curse(116, true).modifyWeightForCurse(3)));
        }

        /**
         * A weightless item is coerced up to one before a multiplier above 100% is applied, so that
         * making something heavier has an effect even when it started at nothing. C comments the
         * coercion at {@code obj-curse.c:393-396}; without it, no percentage however large could
         * move a weightless item.
         */
        @Test
        @DisplayName("a factor above 100% lifts a weightless item off zero")
        void coercesWeightlessItems() {
            assertAll(
                    () -> assertEquals(2, curse(200, true).modifyWeightForCurse(0)),
                    () -> assertEquals(0, curse(50, true).modifyWeightForCurse(0),
                            "a factor at or below 100% leaves it weightless"));
        }

        /**
         * A negative percentage is meaningless and C asserts against it. The port logs and throws
         * rather than aborting the process, so a malformed data file spoils one calculation instead
         * of the session.
         */
        @Test
        @DisplayName("a negative multiplier is rejected, as C asserts")
        void rejectsNegativeMultiplier() {
            assertThrows(IllegalArgumentException.class,
                    () -> curse(-50, true).modifyWeightForCurse(10));
        }
    }

    /**
     * Builds a {@link Flag} set from a handful of object flags. The curse constructor took a
     * {@link java.util.List} when these tests were written and now takes a flag set; this keeps the
     * call sites reading the way they did.
     *
     * @param flags the flags to switch on
     * @return a flag set carrying exactly those flags
     */
    private static Flag<ObjectFlag> objectFlags(ObjectFlag... flags) {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        if (flags.length > 0) result.set(java.util.List.of(flags));
        return result;
    }

}
