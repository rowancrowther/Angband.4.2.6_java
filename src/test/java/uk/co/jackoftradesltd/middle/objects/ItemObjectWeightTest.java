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

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link ItemObject#weightOne()}, the port of C's {@code object_weight_one}
 * ({@code obj-util.c:274}) — one item's weight after its curses have had their say.
 *
 * <p>Curses compose rather than override: each active curse is applied in turn to the running
 * result, so two weight curses both take effect and neither wins outright. That is the behaviour
 * worth pinning, along with the two ways it can be skipped — a curse recorded at zero power is
 * present on the object but inactive, and the base weight is floored before any curse sees it.
 *
 * <p>The multiplying rule has a rounding step that is easy to get wrong: the scaled figure is
 * divided by a hundred and rounded up at a half, so a curse of weight 150 on a 5-pound object gives
 * 8 rather than 7. Both directions of that rounding are covered.
 *
 * @author Rowan Crowther
 */
class ItemObjectWeightTest {

    /**
     * Builds a curse whose only interesting property is what it does to weight.
     *
     * @param weight   the curse's weight figure
     * @param multiply whether it carries {@code OF_MULTIPLY_WEIGHT}
     * @return the curse
     */
    private static Curse curse(int weight, boolean multiply) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        if (multiply) {
            flags.set(List.of(ObjectFlag.OF_MULTIPLY_WEIGHT));
        }

        return new Curse("weighty", List.of(), weight, null, flags,
                Map.of(), Map.of(), 0, 0, 0, List.of(),
                new Flag<>(ObjectFlag.class), "", "");
    }

    /**
     * Builds an item of a given base weight carrying the given curses, each at the given power.
     *
     * @param weight the item's own weight, in tenth-pounds
     * @param curses the curses to record on it, mapped to their power
     * @return the item
     * @throws Exception if a field cannot be reached
     */
    private static ItemObject item(int weight, Map<Curse, CurseData> curses) throws Exception {
        ItemObject item = new ItemObject();
        set(item, "weight", weight);
        set(item, "curses", new LinkedHashMap<>(curses));
        return item;
    }

    /**
     * Writes a private field.
     *
     * @param item  the item to write to
     * @param name  the field's name
     * @param value the value to store
     * @throws Exception if the field cannot be reached
     */
    private static void set(ItemObject item, String name, Object value) throws Exception {
        Field field = ItemObject.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(item, value);
    }

    /**
     * An uncursed item weighs what it says it weighs.
     */
    @Nested
    @DisplayName("without curses")
    class Uncursed {

        /**
         * The plain case.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an uncursed item weighs its own weight")
        void uncursedWeighsItsOwn() throws Exception {
            assertEquals(50, item(50, Map.of()).weightOne());
        }

        /**
         * An item with no curse map at all is the same case — the accessor answers an empty map, so
         * the loop simply does not run.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an item with no curse map weighs its own weight")
        void noCurseMapWeighsItsOwn() throws Exception {
            ItemObject item = new ItemObject();
            set(item, "weight", 50);

            assertEquals(50, item.weightOne());
        }

        /**
         * A negative base weight is floored at zero before anything else happens, so no item ever
         * weighs less than nothing.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a negative base weight is floored at zero")
        void negativeBaseIsFloored() throws Exception {
            assertEquals(0, item(-30, Map.of()).weightOne());
        }
    }

    /**
     * The additive rule, for a curse without the multiply flag.
     */
    @Nested
    @DisplayName("additive curses")
    class Additive {

        /**
         * A positive weight is added.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a positive curse weight is added")
        void positiveIsAdded() throws Exception {
            assertEquals(70, item(50, Map.of(curse(20, false), new CurseData(10, 0))).weightOne());
        }

        /**
         * A negative one is subtracted, and the result floored at zero rather than going negative.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a negative curse weight subtracts, flooring at zero")
        void negativeSubtractsAndFloors() throws Exception {
            assertEquals(30, item(50, Map.of(curse(-20, false), new CurseData(10, 0))).weightOne());
            assertEquals(0, item(50, Map.of(curse(-80, false), new CurseData(10, 0))).weightOne());
        }
    }

    /**
     * The multiplying rule, which scales by a percentage and rounds at a half.
     */
    @Nested
    @DisplayName("multiplying curses")
    class Multiplying {

        /**
         * A curse of weight 100 means "no change" — the value the curse pricing filters on, and the
         * reason a weight-100 multiply curse is not treated as weight-affecting.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a multiplier of 100 changes nothing")
        void hundredIsNoChange() throws Exception {
            assertEquals(50, item(50, Map.of(curse(100, true), new CurseData(10, 0))).weightOne());
        }

        /**
         * Above 100 makes the item heavier, below makes it lighter.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the multiplier scales the weight")
        void multiplierScales() throws Exception {
            assertEquals(100, item(50, Map.of(curse(200, true), new CurseData(10, 0))).weightOne());
            assertEquals(25, item(50, Map.of(curse(50, true), new CurseData(10, 0))).weightOne());
        }

        /**
         * The division rounds up at a half and down below it. A 5-pound object at 150% is 7.5, which
         * becomes 8; the same object at 149% is 7.45, which stays 7.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the division rounds up at a half")
        void divisionRoundsAtAHalf() throws Exception {
            assertEquals(8, item(5, Map.of(curse(150, true), new CurseData(10, 0))).weightOne());
            assertEquals(7, item(5, Map.of(curse(149, true), new CurseData(10, 0))).weightOne());
        }

        /**
         * A multiplier above 100 keeps the item weighing at least a tenth of a pound, so a heavy
         * curse never reduces something to nothing.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a heavier-making multiplier keeps at least a minimum weight")
        void heavierKeepsAMinimum() throws Exception {
            assertEquals(2, item(0, Map.of(curse(150, true), new CurseData(10, 0))).weightOne());
        }
    }

    /**
     * Several curses at once, and the power test that decides which of them count.
     */
    @Nested
    @DisplayName("several curses")
    class Several {

        /**
         * Two curses compose: each is applied to what the last one left, so the second sees the
         * first's result rather than the original weight.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("two curses compose in turn")
        void twoCursesCompose() throws Exception {
            Map<Curse, CurseData> both = new LinkedHashMap<>();
            both.put(curse(50, false), new CurseData(10, 0));
            both.put(curse(200, true), new CurseData(10, 0));

            assertEquals(200, item(50, both).weightOne(),
                    "the multiplier saw the added weight, not the original");
        }

        /**
         * A curse recorded at zero power is inactive: it is on the object but has no effect, which
         * is how a curse that has been suppressed is represented.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a curse at zero power has no effect")
        void zeroPowerCurseIsInactive() throws Exception {
            assertEquals(50, item(50, Map.of(curse(20, false), new CurseData(0, 0))).weightOne());
        }

        /**
         * And an active curse alongside an inactive one still applies, so the power test is per
         * curse rather than a switch over the whole map.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("an active curse applies alongside an inactive one")
        void activeAppliesBesideInactive() throws Exception {
            Map<Curse, CurseData> mixed = new LinkedHashMap<>();
            mixed.put(curse(20, false), new CurseData(0, 0));
            mixed.put(curse(30, false), new CurseData(10, 0));

            assertEquals(80, item(50, mixed).weightOne());
        }
    }
}
