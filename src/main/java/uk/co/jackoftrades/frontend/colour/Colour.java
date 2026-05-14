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
 *  Java code copyright (c) 2026 Rowan Crowther, Jack of Trades Ltd.
 */

package uk.co.jackoftrades.frontend.colour;

import javafx.scene.paint.Color;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.HashMap;

public class Colour {
    public final static int maxColours = 32;
    public final static int basicColours = 29;

    private static HashMap<ColourType, Color> originalColours;
    private static HashMap<ColourType, Color> currentColours;

    private Colour() {
        originalColours = new HashMap<>();
        currentColours = new HashMap<>();

        for (ColourType colourType : ColourType.values()) {
            originalColours.put(colourType, colourType.getColour());
            currentColours.put(colourType, colourType.getColour());
        }
    }
}