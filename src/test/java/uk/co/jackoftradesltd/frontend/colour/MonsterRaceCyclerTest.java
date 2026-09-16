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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link MonsterRaceCycler}, the port of C's anonymous
 * {@code visuals_color_cycles_by_race} table and its
 * {@code visuals_cycler_set_cycle_for_race} / {@code visuals_cycler_get_attr_for_race} accessors
 * ({@code src/ui-visuals.c}).
 *
 * <p>Each test uses a race name unique to that test - {@link MonsterRaceCycler}'s backing map is
 * static with no reset between tests, so reusing a name across tests would leak state.
 *
 * @author Rowan Crowther
 */
class MonsterRaceCyclerTest {

    @Test
    void frameWrapsAroundTheCycleLikeCsFrameModMaxSteps() {
        // visuals_color_cycle_attr_for_frame: step = frame % cycle->max_steps.
        MonsterRaceCycler.addCycler("wraps-around", ColourCycle.colourCycle("three-step",
                List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_BLUE)));

        assertEquals(ColourEnum.COLOUR_RED, MonsterRaceCycler.getAttrForRace("wraps-around", 0));
        assertEquals(ColourEnum.COLOUR_GREEN, MonsterRaceCycler.getAttrForRace("wraps-around", 1));
        assertEquals(ColourEnum.COLOUR_BLUE, MonsterRaceCycler.getAttrForRace("wraps-around", 2));
        // Boundary: frame == max_steps wraps back to step 0, same as C.
        assertEquals(ColourEnum.COLOUR_RED, MonsterRaceCycler.getAttrForRace("wraps-around", 3));
        assertEquals(ColourEnum.COLOUR_GREEN, MonsterRaceCycler.getAttrForRace("wraps-around", 7));
    }

    @Test
    void singleStepCycleAlwaysReturnsThatStep() {
        // Boundary: max_steps == 1 makes frame % max_steps == 0 for every frame.
        MonsterRaceCycler.addCycler("single-step",
                ColourCycle.colourCycle("one-step", List.of(ColourEnum.COLOUR_WHITE)));

        assertEquals(ColourEnum.COLOUR_WHITE, MonsterRaceCycler.getAttrForRace("single-step", 0));
        assertEquals(ColourEnum.COLOUR_WHITE, MonsterRaceCycler.getAttrForRace("single-step", 41));
    }

    @Test
    void unregisteredRaceReturnsNullLikeCsBasicColorsSentinel() {
        // visuals_cycler_get_attr_for_race: race->ridx outside visuals_color_cycles_by_race's
        // range returns BASIC_COLORS. This port returns null instead of a sentinel colour.
        assertNull(MonsterRaceCycler.getAttrForRace("never-registered", 0));
        assertNull(MonsterRaceCycler.getCycler("never-registered"));
    }

    @Test
    void nullRaceIsIgnoredLikeCsNullGuard() {
        // visuals_cycler_set_cycle_for_race returns immediately if race == NULL.
        MonsterRaceCycler.addCycler(null,
                ColourCycle.colourCycle("orphan", List.of(ColourEnum.COLOUR_RED)));

        assertNull(MonsterRaceCycler.getCycler(null));
    }

    @Test
    void nullCycleIsIgnoredLikeCsUnresolvedCycleGuard() {
        // visuals_cycler_set_cycle_for_race stores nothing if the named (group, cycle) fails to
        // resolve to a cycle.
        MonsterRaceCycler.addCycler("null-cycle-race", null);

        assertNull(MonsterRaceCycler.getCycler("null-cycle-race"));
        assertNull(MonsterRaceCycler.getAttrForRace("null-cycle-race", 0));
    }

    @Test
    void reRegisteringARaceReplacesItsCycle() {
        // C's set function assigns straight into the ridx slot -
        // visuals_color_cycles_by_race->race[race->ridx] = cycle - with no guard against
        // overwriting an earlier entry.
        MonsterRaceCycler.addCycler("re-registered",
                ColourCycle.colourCycle("first", List.of(ColourEnum.COLOUR_RED)));
        MonsterRaceCycler.addCycler("re-registered",
                ColourCycle.colourCycle("second", List.of(ColourEnum.COLOUR_BLUE)));

        assertEquals(ColourEnum.COLOUR_BLUE, MonsterRaceCycler.getAttrForRace("re-registered", 0));
    }

    @Test
    void getCyclerReturnsTheRegisteredCycleUnchanged() {
        ColourCycle cycle = ColourCycle.colourCycle("round-trip", List.of(ColourEnum.COLOUR_GREEN));
        MonsterRaceCycler.addCycler("round-trip-race", cycle);

        assertSame(cycle, MonsterRaceCycler.getCycler("round-trip-race"));
    }
}
