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

import uk.co.jackoftrades.backend.colour.ColourEnum;

import java.awt.*;
import java.util.EnumMap;

/**
 * Runtime colour table for the display. Holds both the colours as originally
 * defined ({@link #originalColours}) and the possibly user-modified colours in
 * effect now ({@link #currentColours}), keyed by colour index. This is the Java
 * port of the C original's {@code angband_color_table} / {@code color_table}
 * pairing, which lets the player re-map palette entries while keeping the
 * defaults available to restore.
 *
 * @author Rowan Crowther
 */
public class Colour {
    private static Color colourTable[] = {
            new Color(0, 0, 0),
            new Color(255, 255, 255),
            new Color(128, 128, 128),
            new Color(255, 128, 0),
            new Color(192, 0, 0),
            new Color(0, 128, 64),
            new Color(0, 64, 255),
            new Color(128, 64, 0),
            new Color(96, 96, 96),
            new Color(192, 192, 192),
            new Color(255, 0, 255),
            new Color(255, 255, 0),
            new Color(255, 64, 64),
            new Color(0, 255, 0),
            new Color(0, 255, 255),
            new Color(192, 128, 64),
            new Color(144, 0, 144),
            new Color(144, 32, 255),
            new Color(0, 160, 160),
            new Color(108, 108, 48),
            new Color(255, 255, 144),
            new Color(255, 0, 160),
            new Color(32, 255, 220),
            new Color(184, 168, 255),
            new Color(255, 128, 128),
            new Color(180, 180, 0),
            new Color(160, 192, 208),
            new Color(0, 176, 255),
            new Color(40, 40, 40)};

    /**
     * Total number of palette slots (basic colours plus shade/extra entries).
     *
     * @author Rowan Crowther
     */
    public final static int maxColours = 32;
    /**
     * Number of "basic" named colours before the extra/shade entries.
     *
     * @author Rowan Crowther
     */
    public final static int basicColours = 29;

    /**
     * The colours as originally defined, kept so customised colours can be reset.
     *
     * @author Rowan Crowther
     */
    private static EnumMap<ColourEnum, Color> originalColours = new EnumMap<>(ColourEnum.class);
    /**
     * The colours currently in effect (may differ from the originals after customisation).
     *
     * @author Rowan Crowther
     */
    private static EnumMap<ColourEnum, Color> currentColours = new EnumMap<>(ColourEnum.class);

    /**
     * Private constructor preventing instantiation of this static colour holder.
     *
     * @author Rowan Crowther
     */
    private Colour() {
    }

    /**
     * Populate both the original and current colour tables from the
     * {@link ColourEnum} definitions, indexed in declaration order.
     *
     * @author Rowan Crowther
     */
    public static void init() {
        int index = 0;

        for (ColourEnum colour : ColourEnum.values()) {
            originalColours.put(colour, colourTable[index]);
            currentColours.put(colour, colourTable[index]);
            index++;
        }
    }
}