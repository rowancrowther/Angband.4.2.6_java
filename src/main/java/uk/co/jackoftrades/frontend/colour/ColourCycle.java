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

package uk.co.jackoftrades.frontend.colour;

import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.List;

/**
 * One animated colour cycle: an ordered list of {@link ColourType} frames plus a name. This is the
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
 * @author Rowan Crowther
 */
public final class ColourCycle {
    /**
     * The cycle's name. For a {@link VisualsCycler} entry this is the {@code cycle:} block name (the
     * inner lookup key); for a {@link FlickerTable} entry there is no meaningful name, so the flicker
     * assembler passes the base-attribute code here purely as a label.
     *
     * @author Rowan Crowther
     */
    private final String name;

    /**
     * The ordered animation frames. Index {@code i} is the colour shown at cycle position {@code i};
     * the intended runtime lookup wraps with {@code floorMod(frame, steps.size())}.
     *
     * @author Rowan Crowther
     */
    private final List<ColourType> steps;

    /**
     * Build an immutable cycle from a name and its frames.
     *
     * @param name  the cycle name (or, for a flicker entry, its base-attribute label)
     * @param steps the ordered frames; defensively copied, so the caller may keep mutating its list
     * @author Rowan Crowther
     */
    public ColourCycle(String name, List<ColourType> steps) {
        this.name = name;
        this.steps = List.copyOf(steps);
    }
}
