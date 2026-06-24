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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.frontend.colour;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.ArrayList;
import java.util.List;

/**
 * The top-level registry of colour-cycling animations, holding all
 * {@link VisualsCycleGroup}s loaded from the visuals data file and resolving a
 * cycle (or a specific frame's colour) by group + cycle name. This is the
 * aggregate object produced by the visuals parser and queried by the rest of the
 * colour-cycling system.
 *
 * @author ClaudeCode
 */
public class VisualsCycler {
    /**
     * All colour-cycle groups known to this cycler.
     *
     * @author ClaudeCode
     */
    private List<VisualsCycleGroup> groups;

    /**
     * Create an empty cycler with no groups.
     *
     * @author ClaudeCode
     */
    public VisualsCycler() {
        groups = new ArrayList<>();
    }

    /**
     * Register a colour-cycle group.
     *
     * @param group the group to add
     * @author ClaudeCode
     */
    @Contract(mutates = "this")
    public void addVisualsCycleGroup(@NotNull VisualsCycleGroup group) {
        groups.add(group);
    }

    /**
     * Look up a colour cycle by its group name and cycle name.
     *
     * @param groupName the containing group's name
     * @param cycleName the cycle's name within that group
     * @return the matching cycle, or {@code null} if either name is empty or no
     * match is found
     * @author ClaudeCode
     */
    @Nullable
    @Contract(pure = true)
    @CheckReturnValue
    public VisualsColourCycle cycleByName(@NotNull String groupName, @NotNull String cycleName) {
        if (groupName.isEmpty() || cycleName.isEmpty())
            return null;

        VisualsCycleGroup group = groups.stream().filter(gr -> groupName.equals(gr.groupName))
                .findFirst().orElse(null);

        if (group == null) return null;

        return group.cycles.stream().filter(c -> cycleName.equals(c.getCycleName()))
                .findFirst().orElse(null);
    }

    /**
     * Resolve the colour for a given animation frame of a named cycle.
     *
     * @param groupName the containing group's name
     * @param cycleName the cycle's name within that group
     * @param frame     the animation frame
     * @return the colour for that frame, or {@code null} if the cycle is not found
     * @author ClaudeCode
     */
    @Nullable
    @Contract(pure = true)
    @CheckReturnValue
    public ColourType getAttrForFrame(String groupName, String cycleName, int frame) {
        VisualsColourCycle cycle = cycleByName(groupName, cycleName);
        if (cycle == null) return null;

        return cycle.attrForFrame(frame);
    }
}