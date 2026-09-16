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

package uk.co.jackoftradesltd.frontend.colour;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import java.awt.*;
import java.util.EnumMap;

/**
 * Runtime colour table for the display. Holds both the colours as originally
 * defined ({@link #originalColours}) and the possibly user-modified colours in
 * effect now ({@link #currentColours}), keyed by {@link ColourEnum}. This is
 * the Java port of the C original's {@code angband_color_table} /
 * {@code color_table} pairing ({@code [C] src/z-color.c}), which the
 * interactive palette editor in {@code ui-options.c} and the {@code V:}
 * preference-file directive in {@code ui-prefs.c} both write into directly at
 * runtime.
 *
 * <p>The C original keeps no separate copy of the compiled-in defaults once
 * {@code angband_color_table} has been overwritten - {@link #originalColours}
 * is this port's addition, letting a caller restore the defaults without
 * recompiling.
 *
 * <p>Class Colour coded on 260902, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class Colour {
    /**
     * Total number of palette slots - the Java form of C's {@code MAX_COLORS}
     * ({@code [C] src/z-color.h}), which sizes {@code angband_color_table} and
     * {@code color_table} to 32 even though only {@link #basicColours} of
     * those rows are ever given real values.
     *
     * <p>Field maxColours coded on 260902, commented in full on 260916.
     */
    public final static int maxColours = 32;
    /**
     * Number of "basic" named colours before the extra/shade entries - the
     * Java form of C's {@code BASIC_COLORS} ({@code [C] src/z-color.h}), and
     * the length of both {@link #colourTable} and {@link ColourEnum#values()}.
     *
     * <p>Field basicColours coded on 260902, commented in full on 260916.
     */
    public final static int basicColours = 29;
    /**
     * The 29 default RGB triples, in {@link ColourEnum} declaration order -
     * the Java form of the C original's {@code angband_color_table[MAX_COLORS][4]}
     * ({@code [C] src/z-color.c}), read there as {@code {full, r, g, b}} per
     * row. Only the {@link #basicColours} named rows are given here; C's array
     * is sized to {@link #maxColours} but leaves the remaining rows zeroed, and
     * this port never indexes past {@link ColourEnum#COLOUR_SHADE}.
     *
     * <p>Field colourTable coded on 260902, commented in full on 260916.
     */
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
     * The colours as originally defined, indexed by {@link ColourEnum} and
     * kept so customised colours can be reset. Populated once by
     * {@link #init()} from {@link #colourTable} and never written to again;
     * the C original has no equivalent, since it overwrites
     * {@code angband_color_table} in place and keeps no separate copy of the
     * compiled-in defaults.
     *
     * <p>Field originalColours coded on 260902, commented in full on 260916.
     */
    private static EnumMap<ColourEnum, Color> originalColours = new EnumMap<>(ColourEnum.class);
    /**
     * The colours currently in effect, indexed by {@link ColourEnum} - the
     * Java form of the C original's {@code angband_color_table} /
     * {@code color_table} ({@code [C] src/z-color.c}), which the palette
     * editor and preference-file loader write into directly.
     * {@link #getColour(ColourEnum)} always reads from here, never from
     * {@link #originalColours}.
     *
     * <p>Field currentColours coded on 260902, commented in full on 260916.
     */
    private static EnumMap<ColourEnum, Color> currentColours = new EnumMap<>(ColourEnum.class);

    /**
     * Private constructor preventing instantiation of this static colour
     * holder; every member is static, mirroring the C original's file-scope
     * arrays.
     *
     * <p>Function Colour() coded on 260902, commented in full on 260916.
     */
    private Colour() {
    }

    /**
     * Populates both {@link #originalColours} and {@link #currentColours}
     * from {@link #colourTable}, keyed by {@link ColourEnum} in
     * {@code values()} declaration order - which matches the row order of the
     * C original's {@code angband_color_table} ({@code [C] src/z-color.c})
     * index for index. Must be called once before
     * {@link #getColour(ColourEnum)} is used; C has no equivalent step, since
     * its array is populated by its initializer at compile time rather than
     * at runtime.
     *
     * <p>Function init() coded on 260902, commented in full on 260916.
     */
    public static void init() {
        int index = 0;

        for (ColourEnum colour : ColourEnum.values()) {
            originalColours.put(colour, colourTable[index]);
            currentColours.put(colour, colourTable[index]);
            index++;
        }
    }

    /**
     * Looks up the colour currently in effect for {@code colour} - the Java
     * form of indexing the C original's {@code angband_color_table}
     * ({@code [C] src/z-color.c}) by attribute number. Always reads
     * {@link #currentColours}, never {@link #originalColours}, so a
     * customised palette is reflected immediately.
     *
     * <p>Function getColour(ColourEnum) coded on 260902, commented in full on
     * 260916.
     *
     * @param colour the colour to look up
     * @return the {@link Color} currently mapped to {@code colour}, or
     * {@code null} if {@link #init()} has not been called yet
     */
    public static Color getColour(ColourEnum colour) {
        return currentColours.get(colour);
    }
}