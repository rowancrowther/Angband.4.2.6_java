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

import uk.co.jackoftrades.middle.objects.enums.TValue;

/**
 * A simple {@link TValue}-to-name pairing used to group/label related object
 * types in lists and menus. This is the Java port of the C original's
 * {@code grouper} struct ({@code src/obj-util.c}).
 *
 * @author ClaudeCode
 */
public class Grouper {
    /**
     * The item type this grouper covers.
     *
     * @author ClaudeCode
     */
    private final TValue tValue;
    /**
     * The group's display name.
     *
     * @author ClaudeCode
     */
    private final String name;

    /**
     * Constructor
     *
     * @param tValue the TValue of this grouper
     * @param name   the string associated with this grouper
     */
    public Grouper(TValue tValue, String name) {
        this.name = name;
        this.tValue = tValue;
    }

    /**
     * Returns the string name associated with this grouper
     *
     * @return the string name associated with this grouper
     */
    public String getName() {
        return name;
    }

    /**
     * Get the TValue of this Grouper
     *
     * @return the TValue of this grouper
     */
    public TValue gettValue() {
        return tValue;
    }
}
