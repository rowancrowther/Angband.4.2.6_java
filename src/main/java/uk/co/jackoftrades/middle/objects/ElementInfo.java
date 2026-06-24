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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;

/**
 * How an object kind relates to a single damage element: the
 * {@link ElementInfoEnum} flags (hates/ignores/random) plus a resistance level.
 * One of these exists per element on an object kind. This is the Java port of the
 * C original's {@code struct element_info} ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public class ElementInfo {
    /**
     * The hates/ignores/random flags for this element.
     *
     * @author ClaudeCode
     */
    private Flag<ElementInfoEnum> flags;
    /**
     * The resistance level against this element.
     *
     * @author ClaudeCode
     */
    private int resLevel;

    /**
     * Build an element-info with an empty flag set.
     *
     * @author ClaudeCode
     */
    public ElementInfo() {
        this.flags = new Flag<>(ElementInfoEnum.class);
    }

    /**
     * @return the resistance level against this element
     * @author ClaudeCode
     */
    public int getResLevel() {
        return resLevel;
    }

    /**
     * @return the hates/ignores/random flags for this element
     * @author ClaudeCode
     */
    public Flag<ElementInfoEnum> getFlags() {
        return flags;
    }

    /**
     * Set one of this element's flags.
     *
     * @param info the flag to set
     * @return true if the flag was newly set, false if already set
     * @author ClaudeCode
     */
    public boolean on(ElementInfoEnum info) {
        return flags.on(info);
    }
}
