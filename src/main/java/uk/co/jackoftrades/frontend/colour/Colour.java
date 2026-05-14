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

public class Colour {
    public final static int maxColours = 32;
    public final static int basicColours = 29;

    private static HashMap<Integer, Color> originalColours = new HashMap<>();
    private static HashMap<Integer, Color> currentColours = new HashMap<>();

    private Colour() {
    }

    public static void init() {
        int index = 0;

        for (ColourType colourType : ColourType.values()) {
            originalColours.put(index, colourType.getColour());
            currentColours.put(index, colourType.getColour());
            index++;
        }
    }
}