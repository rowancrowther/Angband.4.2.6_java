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

import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-race colour-cycle lookup, keyed by race name. This is the runtime tier-1 check in the
 * animated-monster fallback - a race with a registered cycle is drawn from it every frame, ahead of
 * the base-attribute {@link FlickerTable} (tier 2) and the monster's plain static colour (tier 3).
 * Java port of the anonymous {@code visuals_color_cycles_by_race} table and its two accessors,
 * {@code visuals_cycler_set_cycle_for_race} and {@code visuals_cycler_get_attr_for_race}
 * ({@code [C] src/ui-visuals.c}), which {@code do_animation} ({@code [C] src/ui-display.c}) consults
 * before falling back to the flicker table and then the static attribute.
 *
 * <p>The C original resolves a {@code (group, cycle)} name pair against the module-wide colour-cycle
 * table itself, inside {@code visuals_cycler_set_cycle_for_race}, and stores only the result. This
 * port pushes that resolution to the caller - {@link #addCycler} takes an already-resolved
 * {@link ColourCycle} directly, the same primitive {@link VisualsCycler} and {@link FlickerTable} use,
 * differing only in how it's looked up. C also indexes by {@code race->ridx} in a manually-grown
 * array (capped at 10000 entries by a {@code quit()} call); this port keys by race name in a plain
 * {@link HashMap}, which needs no growth bookkeeping or ceiling.
 *
 * <p>Class MonsterRaceCycler coded on 260902, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class MonsterRaceCycler {
    /**
     * The registered cycle for each race, by race name. Written only by {@link #addCycler}, read by
     * {@link #getCycler} and {@link #getAttrForRace}. Java port of the {@code race} array inside C's
     * anonymous {@code visuals_color_cycles_by_race} table ({@code [C] src/ui-visuals.c}), minus its
     * manual reallocate-to-fit-{@code ridx} loop - a {@link HashMap} needs no equivalent.
     *
     * <p>Field monsterCyclerByRace coded on 260902, commented in full on 260916.
     */
    private static Map<String, ColourCycle> monsterCyclerByRace = new HashMap<>();

    /**
     * Prevents instantiation of this static lookup; every member is static, mirroring the C
     * original's module-level static table.
     *
     * <p>Function MonsterRaceCycler() coded on 260902, commented in full on 260916.
     */
    private MonsterRaceCycler() {
    }

    /**
     * Registers the resolved colour cycle to use for a race. A null {@code race} or {@code
     * colourCycle} is silently ignored and nothing is stored. Java port of
     * {@code visuals_cycler_set_cycle_for_race} ({@code [C] src/ui-visuals.c}), minus the by-name
     * {@code (group, cycle)} resolution step, which this port's caller performs before calling here
     * (see the class documentation), and minus the {@code ridx} array-growth loop, which a
     * {@link HashMap} makes unnecessary. Still mirrors C's null guards and its "cycle didn't resolve
     * -> nothing stored" behaviour, now expressed as "caller passes null -> nothing stored".
     *
     * <p>Function addCycler(String, ColourCycle) coded on 260902, commented in full on 260916,
     * updated on 260916 to guard a null race as well as a null cycle.
     *
     * @param race        the race name to register the cycle for
     * @param colourCycle the resolved cycle to use for that race; ignored if null
     */
    public static void addCycler(String race, ColourCycle colourCycle) {
        if (colourCycle == null || race == null)
            return;

        monsterCyclerByRace.put(race, colourCycle);
    }

    /**
     * Looks up the colour cycle registered for a race, if any. A plain accessor with no C-side
     * counterpart function of its own - C only ever reads {@code visuals_color_cycles_by_race}
     * through {@code visuals_cycler_get_attr_for_race}, which resolves straight to a colour; this
     * port exposes the underlying {@link ColourCycle} as well, for a caller that wants the whole
     * cycle rather than one frame's colour.
     *
     * <p>Function getCycler(String) coded on 260902, commented in full on 260916, updated on 260916
     * (return type changed from {@link VisualsCycler} to {@link ColourCycle}, matching the fix to
     * {@link #addCycler}).
     *
     * @param race the race name to look up
     * @return the cycle registered for {@code race}, or {@code null} if none is registered
     */
    public static ColourCycle getCycler(String race) {
        return monsterCyclerByRace.get(race);
    }

    /**
     * Resolves the colour a race's colour cycle shows on a given animation frame. Returns
     * {@code null} if no cycle is registered for the race, or (defensively; {@link ColourCycle}
     * already refuses to be built with no steps) if the registered cycle has none. Java port of
     * {@code visuals_cycler_get_attr_for_race} composed with {@code visuals_color_cycle_attr_for_frame}
     * ({@code [C] src/ui-visuals.c}): C looks the race's cycle up by {@code ridx}, guarding null and
     * out-of-range indices, then wraps {@code frame % cycle->max_steps} into its fixed-width steps
     * array, returning the {@code BASIC_COLORS} sentinel on any miss. This port folds both steps into
     * one call keyed by race name, and returns {@code null} instead of the sentinel, matching the
     * pattern already established for {@link FlickerTable}. {@code frame} corresponds to C's
     * {@code flicker} frame counter, passed in by {@code do_animation} ({@code [C] src/ui-display.c}).
     *
     * <p>Outstanding: C's {@code frame} parameter is an unsigned {@code size_t}, so it can never be
     * negative; this port's {@code int frame} isn't restricted the same way; a negative frame would
     * make {@code frame % size} negative and throw indexing into {@link ColourCycle#getColour}. Not
     * currently reachable - the one C call site is a {@code uint8_t} counter that only ever increments
     * from zero - but nothing in this method enforces it.
     *
     * <p>Function getAttrForRace(String, int) coded on 260916, commented in full on 260916.
     *
     * @param race  the race name to look up
     * @param frame the animation frame to resolve a colour for
     * @return the colour to show for {@code race} on {@code frame}, or {@code null} if the race has
     * no usable registered cycle
     */
    public static ColourEnum getAttrForRace(String race, int frame) {
        if (!monsterCyclerByRace.containsKey(race)) return null;

        ColourCycle cycle = monsterCyclerByRace.get(race);

        int size = cycle.getSize();

        if (size == 0)
            return null;

        int index = frame % cycle.getSize();
        return cycle.getColour(index);
    }
}
