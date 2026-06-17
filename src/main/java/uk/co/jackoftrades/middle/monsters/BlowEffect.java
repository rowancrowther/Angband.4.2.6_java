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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;

public class BlowEffect {
    private String name;
    private int power;
    private int eval;
    private String desc;
    private ColourType loreAttr;
    private ColourType loreAttrResist;
    private ColourType loreAttrImmune;
    private String effectType;
    private ElementEnum resist;
    private ElementEnum lashType;

    public BlowEffect(String name, int power, int eval, String desc, ColourType loreAttr, ColourType loreAttrResist,
                      ColourType loreAttrImmune, String effectType, ElementEnum resist, ElementEnum lashType) {
        this.name = name;
        this.power = power;
        this.eval = eval;
        this.desc = desc;
        this.loreAttr = loreAttr;
        this.loreAttrResist = loreAttrResist;
        this.loreAttrImmune = loreAttrImmune;
        this.effectType = effectType;
        this.resist = resist;
        this.lashType = lashType;
    }
}
