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

import java.util.List;
import java.util.Map;

/**
 * The named-cycle table: colour cycles keyed by (group, name). This is the tier-1 lookup for
 * animated colours - a monster with an explicit {@code color-cycle:<group>:<name>} assignment
 * resolves it here, taking priority over the base-attribute {@link FlickerTable}. Port of the C
 * {@code visuals_cycler} plus its {@code visuals_cycle_group} buckets ({@code ui-visuals.c}).
 * <p>
 * The two-level map mirrors the data file's two-part key: {@code cycle:fancy:rainbow} lands under
 * group {@code fancy}, name {@code rainbow}. Same {@link ColourCycle} primitive as the flicker
 * table - only the lookup differs (by name here, by base attribute there). A missing group or name
 * is a lookup miss, the faithful port of C's {@code BASIC_COLORS} sentinel; C's fixed group/cycle
 * count ceilings are dropped as allocation artefacts.
 *
 * @author Rowan Crowther
 */
public final class VisualsCycler {
    /**
     * Cycles nested group-then-name. The outer key is the {@code cycle:} group; the inner map holds
     * that group's cycles by their block name.
     *
     * @author Rowan Crowther
     */
    private final Map<String, Map<String, ColourCycle>> byGroupThenName;

    /**
     * Wrap an already-built group-then-name map. Ownership of the map passes to the cycler.
     *
     * @param byGroupThenName the nested cycles, as assembled from the {@code cycle:} blocks
     * @author Rowan Crowther
     */
    public VisualsCycler(Map<String, Map<String, ColourCycle>> byGroupThenName) {
        this.byGroupThenName = byGroupThenName;
    }

    /**
     * List the groups present in this cycler (the outer keys, e.g. {@code fancy}, {@code flicker}).
     *
     * @return the group names, in no guaranteed order
     * @author Rowan Crowther
     */
    public List<String> getKeys() {
        return byGroupThenName.keySet().stream().toList();
    }

    /**
     * Fetch one group's cycles by their block name.
     *
     * @param group the group name to look up
     * @return the group's name-to-cycle map, or {@code null} if no such group exists
     * @author Rowan Crowther
     */
    public Map<String, ColourCycle> getByGroup(String group) {
        return byGroupThenName.get(group);
    }
}
