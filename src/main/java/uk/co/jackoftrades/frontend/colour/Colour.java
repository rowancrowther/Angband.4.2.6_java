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

import javafx.scene.paint.Color;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.HashMap;

/**
 * Runtime colour table for the display. Holds both the colours as originally
 * defined ({@link #originalColours}) and the possibly user-modified colours in
 * effect now ({@link #currentColours}), keyed by colour index. This is the Java
 * port of the C original's {@code angband_color_table} / {@code color_table}
 * pairing, which lets the player re-map palette entries while keeping the
 * defaults available to restore.
 *
 * @author ClaudeCode
 */
public class Colour {
    /**
     * Total number of palette slots (basic colours plus shade/extra entries).
     *
     * @author ClaudeCode
     */
    public final static int maxColours = 32;
    /**
     * Number of "basic" named colours before the extra/shade entries.
     *
     * @author ClaudeCode
     */
    public final static int basicColours = 29;

    /**
     * The colours as originally defined, kept so customised colours can be reset.
     *
     * @author ClaudeCode
     */
    private static HashMap<Integer, Color> originalColours = new HashMap<>();
    /**
     * The colours currently in effect (may differ from the originals after customisation).
     *
     * @author ClaudeCode
     */
    private static HashMap<Integer, Color> currentColours = new HashMap<>();

    /**
     * Private constructor preventing instantiation of this static colour holder.
     *
     * @author ClaudeCode
     */
    private Colour() {
    }

    /**
     * Populate both the original and current colour tables from the
     * {@link ColourType} definitions, indexed in declaration order.
     *
     * @author ClaudeCode
     */
    public static void init() {
        int index = 0;

        for (ColourType colourType : ColourType.values()) {
            originalColours.put(index, colourType.getColour());
            currentColours.put(index, colourType.getColour());
            index++;
        }
    }
}