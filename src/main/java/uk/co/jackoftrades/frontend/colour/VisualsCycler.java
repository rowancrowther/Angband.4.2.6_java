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

public class VisualsCycler {
    private List<VisualsCycleGroup> groups;

    public VisualsCycler() {
        groups = new ArrayList<>();
    }

    @Contract(mutates = "this")
    public void addVisualsCycleGroup(@NotNull VisualsCycleGroup group) {
        groups.add(group);
    }

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

    @Nullable
    @Contract(pure = true)
    @CheckReturnValue
    public ColourType getAttrForFrame(String groupName, String cycleName, int frame) {
        VisualsColourCycle cycle = cycleByName(groupName, cycleName);
        if (cycle == null) return null;

        return cycle.attrForFrame(frame);
    }
}