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

package uk.co.jackoftrades.frontend.screen;

import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;

import java.util.ArrayList;

public class TermWin {
    private boolean cu;
    private boolean cv;
    private int cx;
    private int cy;
    private int cnx;
    private int cny;

    private ArrayList<AngbandDisplayCharacter> a;
    private ArrayList<AngbandDisplayCharacter> va;
    private ArrayList<AngbandDisplayCharacter> ta;
    private ArrayList<AngbandDisplayCharacter> vta;

    public void init(int width, int height) {
        a = new ArrayList<>();
        va = new ArrayList<>();
        ta = new ArrayList<>();
        vta = new ArrayList<>();

        for (int index = 0; index < height; index++) {
            a.add(new AngbandDisplayCharacter(' ', AttributeColour.COLOUR_WHITE));
            va.add(new AngbandDisplayCharacter(' ', AttributeColour.COLOUR_WHITE));
            ta.add(new AngbandDisplayCharacter(' ', AttributeColour.COLOUR_WHITE));
            vta.add(new AngbandDisplayCharacter(' ', AttributeColour.COLOUR_WHITE));
        }
    }
}
