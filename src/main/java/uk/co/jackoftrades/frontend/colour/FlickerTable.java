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

import java.util.Map;

/**
 * The flicker table: colour cycles keyed by <strong>base attribute</strong>. When an
 * {@code ATTR_FLICKER} monster has no explicitly assigned named cycle, its animation is driven by
 * looking its static base colour up here and stepping the returned {@link ColourCycle} by the global
 * frame counter. Port of the C {@code visuals_flicker} ({@code ui-visuals.c}); this is tier 2 of the
 * three-tier fallback in C's {@code do_animation} ({@code ui-display.c}), between an assigned named
 * cycle (tier 1) and the plain static colour (tier 3).
 * <p>
 * <strong>This half is live, not dead.</strong> The shipped monster.txt has 46 {@code ATTR_FLICKER}
 * monsters but only 9 {@code color-cycle:} assignments, so most flickering monsters fall through to
 * this table - it is the {@code cycle:flicker:} <em>named</em> group in visuals.txt that nothing
 * requests, not the flicker blocks.
 * <p>
 * A missing key means "no cycle for that base attribute" - the faithful port of C returning its
 * {@code BASIC_COLORS} sentinel; the intended getter yields {@code null} rather than a sentinel
 * colour, and C's fixed table-size ceilings are dropped as allocation artefacts with no semantics.
 *
 * @author Rowan Crowther
 */
public final class FlickerTable {
    /**
     * Cycles indexed by the base attribute they animate. The key is the monster's static colour; the
     * value is the frame sequence to show instead while it flickers.
     *
     * @author Rowan Crowther
     */
    private final Map<ColourType, ColourCycle> byAttr;

    /**
     * Wrap an already-built base-attribute-to-cycle map. Ownership of the map passes to the table.
     *
     * @param byAttr the base-attribute-keyed cycles, as assembled from the {@code flicker:} blocks
     * @author Rowan Crowther
     */
    public FlickerTable(Map<ColourType, ColourCycle> byAttr) {
        this.byAttr = byAttr;
    }
}
