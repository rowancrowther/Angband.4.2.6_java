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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.ArrayList;
import java.util.List;

/**
 * One named colour-cycling animation: an ordered list of {@link ColourType}
 * steps that a glyph cycles through, one step per animation frame. This is the
 * Java port of the C original's colour-cycling support ({@code src/ui-visuals.c}),
 * used to make certain monsters/terrain shimmer.
 *
 * @author ClaudeCode
 */
public class VisualsColourCycle implements Cloneable {
    /**
     * Logger used to report cloning failures.
     *
     * @author ClaudeCode
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The ordered colours this cycle steps through, one per frame.
     *
     * @author ClaudeCode
     */
    private List<ColourType> steps;
    /**
     * The name identifying this cycle within its group.
     *
     * @author ClaudeCode
     */
    private String cycleName;
    /**
     * The colour used as a marker for an unused/invalid entry.
     *
     * @author ClaudeCode
     */
    private ColourType invalidColour;

    /**
     * Constructor
     *
     * @param name          The name of the current Visuals Colour Cycle
     * @param invalidColour The colour used as a marker for an unused/invalid entry
     */
    public VisualsColourCycle(@NotNull String name, @NotNull ColourType invalidColour) {
        steps = new ArrayList<>();
        cycleName = name;
        this.invalidColour = invalidColour;
    }

    /**
     * Add a new ColourType to this cycle
     *
     * @param step the ColourType to add
     */
    @Contract(mutates = "this")
    public void addStep(@NotNull ColourType step) {
        steps.add(step);
    }

    /**
     * Create a deep copy of this VisualsColourCycle and return it
     *
     * @return A deep copy of this VisualsColourCycle
     */
    @Nullable
    @CheckReturnValue
    public VisualsColourCycle copy() {
        try {
            return (VisualsColourCycle) this.clone();
        } catch (Exception e) {
            logger.error("Error while attempting to clone VisualsColourCycle", e);
        }

        return null;
    }

    /**
     * Creates and returns a deep copy of this object
     *
     * @return A deep copy of this object
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        VisualsColourCycle cloned = (VisualsColourCycle) super.clone();

        cloned.cycleName = this.cycleName;
        cloned.invalidColour = this.invalidColour;
        cloned.steps = new ArrayList<ColourType>();

        cloned.steps.addAll(this.steps);

        return cloned;
    }

    /**
     * Get the colour type that is at the given frame for this cycle
     *
     * @param frame the frame of this cycle
     * @return a ColourType used to display the resultant character in
     */
    @NotNull
    @Contract(pure = true)
    @CheckReturnValue
    public ColourType attrForFrame(int frame) {
        frame = frame % steps.size();
        return steps.get(frame);
    }

    /**
     * Accessor
     *
     * @return the name of this VisualsColourCycle
     */
    public String getCycleName() {
        return cycleName;
    }
}