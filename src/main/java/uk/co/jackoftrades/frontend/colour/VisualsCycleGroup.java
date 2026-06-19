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

import java.util.ArrayList;
import java.util.List;

public class VisualsCycleGroup {
    List<VisualsColourCycle> cycles;
    String groupName;

    /**
     * Constructor
     */
    @Contract(mutates = "this")
    public VisualsCycleGroup() {
        this.cycles = new ArrayList<>();
    }

    @Contract(mutates = "this")
    public void setGroupName(@NotNull String groupName) {
        this.groupName = groupName;
    }

    @Contract(mutates = "this")
    public void addCycle(@NotNull VisualsColourCycle cycle) {
        cycles.add(cycle);
    }

    /**
     * Accessor
     *
     * @return the name of this VisualsCycleGroup
     */
    @Contract(pure = true)
    @NotNull
    @CheckReturnValue
    String getGroupName() {
        return groupName;
    }

    /**
     * Accessor
     *
     * @return the list of VisualsColourCycles
     */
    @Contract(pure = true)
    @NotNull
    @CheckReturnValue
    public List<VisualsColourCycle> getCycles() {
        return cycles;
    }
}