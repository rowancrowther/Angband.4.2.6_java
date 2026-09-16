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

import java.util.List;

/**
 * One animated colour cycle: an ordered list of {@link ColourEnum} frames plus a name. This is the
 * single primitive underpinning <em>both</em> visuals mechanisms - a named cycle in a
 * {@link VisualsCycler} and a base-attribute entry in a {@link FlickerTable} are the same object,
 * differing only in how they are looked up. Port of the C {@code visuals_color_cycle}
 * ({@code ui-visuals.c}).
 * <p>
 * <strong>Deliberate divergence from C.</strong> The C original allocates every cycle at a fixed
 * {@code VISUALS_STEPS_MAX} width and advances with {@code frame % MAX}, leaving unfilled trailing
 * slots that return an invalid-colour sentinel. This port stores only the {@code steps} that were
 * actually declared and is intended to index {@code frame % steps.size()} over that filled list, so
 * there is no sentinel and no need for the wrap width - which is why no invalid-colour field exists
 * here.
 *
 * <p>Class ColourCycle coded before 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public final class ColourCycle {
    /**
     * The cycle's name. For a {@link VisualsCycler} entry this is the {@code cycle:} block name (the
     * inner lookup key); for a {@link FlickerTable} entry there is no meaningful name, so the flicker
     * assembler passes the base-attribute code here purely as a label.
     *
     * <p>Field name coded before 260916, commented in full on 260916.
     */
    private final String name;

    /**
     * The ordered animation frames. Index {@code i} is the colour shown at cycle position {@code i};
     * the intended runtime lookup wraps with {@code floorMod(frame, steps.size())}.
     *
     * <p>Field steps coded before 260916, commented in full on 260916.
     */
    private final List<ColourEnum> steps;

    /**
     * Build an immutable cycle from a name and its frames. Java port of the value-copying half of
     * {@code visuals_color_cycle_new} ({@code [C] src/ui-visuals.c}), minus the fixed-{@code
     * step_count} allocation and invalid-colour fill - this constructor stores exactly the frames it
     * is given, per the class-level "Deliberate divergence" note.
     *
     * <p>Function ColourCycle(String, List) coded before 260916, commented in full on 260916.
     *
     * @param name  the cycle name (or, for a flicker entry, its base-attribute label)
     * @param steps the ordered frames; defensively copied, so the caller may keep mutating its list
     */
    private ColourCycle(String name, List<ColourEnum> steps) {
        this.name = name;
        this.steps = List.copyOf(steps);
    }

    /**
     * Build a cycle from a name and its resolved frames, or refuse to build one with no frames at
     * all. Java port of {@code visuals_color_cycle_new}'s {@code step_count == 0} guard ({@code [C]
     * src/ui-visuals.c}) - C returns {@code NULL} rather than allocate a zero-length {@code steps}
     * array; this factory does the same rather than construct a {@link ColourCycle} that could never
     * answer {@link #getColour}.
     *
     * <p>Function colourCycle(String, List) coded before 260916, commented in full on 260916.
     *
     * @param name  the cycle name (or, for a flicker entry, its base-attribute label)
     * @param steps the ordered frames to build the cycle from
     * @return a new cycle wrapping {@code steps}, or {@code null} if {@code steps} is empty
     */
    public static ColourCycle colourCycle(String name, List<ColourEnum> steps) {
        if (steps.isEmpty())
            return null;

        return new ColourCycle(name, steps);
    }

    /**
     * Fetch the frame at a given position in the cycle. Java port of the array-indexing step of
     * {@code visuals_color_cycle_attr_for_frame} ({@code [C] src/ui-visuals.c}) - {@code cycle->steps
     * [step]} - minus the {@code frame % max_steps} wrap and the null-cycle {@code BASIC_COLORS}
     * fallback, both of which C performs in that one function; per the class-level "Deliberate
     * divergence" note, this port expects the caller to have already wrapped {@code index} into
     * {@code [0, getSize())} (typically via {@code floorMod(frame, getSize())}), since there is no
     * invalid-colour sentinel here to fall back to.
     *
     * <p>Function getColour(int) coded before 260916, commented in full on 260916.
     *
     * @param index the position to fetch, expected already wrapped to {@code [0, getSize())}
     * @return the colour at {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is negative or {@code >= getSize()}
     */
    public ColourEnum getColour(int index) {
        return steps.get(index);
    }

    /**
     * The number of frames in this cycle. Java port of reading the C {@code visuals_color_cycle}
     * struct's {@code max_steps} field directly ({@code [C] src/ui-visuals.c}), rather than a
     * ported function - C has no accessor of its own, since {@code max_steps} is only ever read
     * internally for the {@code frame % max_steps} wrap inside
     * {@code visuals_color_cycle_attr_for_frame}. This port exposes it so a caller can perform that
     * same wrap before calling {@link #getColour}.
     *
     * <p>Function getSize() coded before 260916, commented in full on 260916.
     *
     * @return the number of steps in this cycle; always at least 1, since {@link #colourCycle} refuses
     * to build a cycle with none
     */
    public int getSize() {
        return steps.size();
    }
}
