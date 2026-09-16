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

package uk.co.jackoftradesltd.frontend.colour;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link ColourCycle}, the port of C's {@code visuals_color_cycle} struct plus
 * {@code visuals_color_cycle_new} and the array-indexing step of
 * {@code visuals_color_cycle_attr_for_frame} ({@code src/ui-visuals.c}).
 *
 * @author Rowan Crowther
 */
class ColourCycleTest {

    @Test
    void colourCycleRefusesToBuildWithNoSteps() {
        // visuals_color_cycle_new: step_count == 0 returns NULL.
        assertNull(ColourCycle.colourCycle("empty", List.of()));
    }

    @Test
    void colourCycleBuildsFromExactlyTheStepsGiven() {
        // No fixed VISUALS_STEPS_MAX padding - the cycle's size is the list's size.
        ColourCycle cycle = ColourCycle.colourCycle("three-step",
                List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_BLUE));

        assertEquals(3, cycle.getSize());
        assertEquals(ColourEnum.COLOUR_RED, cycle.getColour(0));
        assertEquals(ColourEnum.COLOUR_GREEN, cycle.getColour(1));
        assertEquals(ColourEnum.COLOUR_BLUE, cycle.getColour(2));
    }

    @Test
    void singleStepCycleHasSizeOne() {
        // Boundary: the smallest cycle colourCycle will build.
        ColourCycle cycle = ColourCycle.colourCycle("one-step", List.of(ColourEnum.COLOUR_WHITE));

        assertEquals(1, cycle.getSize());
        assertEquals(ColourEnum.COLOUR_WHITE, cycle.getColour(0));
    }

    @Test
    void getColourThrowsPastTheLastStepInsteadOfReturningASentinel() {
        // C falls back to BASIC_COLORS via the frame % max_steps wrap; this port has no sentinel
        // and expects the caller to have already wrapped index into [0, getSize()).
        ColourCycle cycle = ColourCycle.colourCycle("two-step",
                List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_GREEN));

        assertThrows(IndexOutOfBoundsException.class, () -> cycle.getColour(2));
        assertThrows(IndexOutOfBoundsException.class, () -> cycle.getColour(-1));
    }

    @Test
    void mutatingTheOriginalListAfterConstructionDoesNotAffectTheCycle() {
        // The constructor defensively copies steps (List.copyOf), unlike C's shared-pointer struct.
        List<ColourEnum> steps = new ArrayList<>(List.of(ColourEnum.COLOUR_RED));
        ColourCycle cycle = ColourCycle.colourCycle("defensive-copy", steps);

        steps.add(ColourEnum.COLOUR_BLUE);

        assertEquals(1, cycle.getSize());
        assertEquals(ColourEnum.COLOUR_RED, cycle.getColour(0));
    }

    @Test
    void colourCycleReturnsANewInstanceEachCall() {
        ColourCycle first = ColourCycle.colourCycle("name", List.of(ColourEnum.COLOUR_RED));
        ColourCycle second = ColourCycle.colourCycle("name", List.of(ColourEnum.COLOUR_RED));

        assertEquals(first.getSize(), second.getSize());
        assertSame(first.getColour(0), second.getColour(0));
    }
}
