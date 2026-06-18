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

import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

public class MonsterSpellLevel {
    private int power;
    private String loreDesc;
    private ColourType loreAttr;
    private ColourType loreAttrResist;
    private ColourType loreAttImmune;
    String message;
    String blindMessage;
    String missMessage;
    String saveMessage;

    @Contract(mutates = "this")
    public MonsterSpellLevel(int power, String loreDesc, ColourType loreAttr, ColourType loreAttrResist,
                             ColourType loreAttImmune, String message, String blindMessage, String missMessage,
                             String saveMessage) {
        this.power = power;
        this.loreDesc = loreDesc;
        this.loreAttr = loreAttr;
        this.loreAttrResist = loreAttrResist;
        this.loreAttImmune = loreAttImmune;
        this.message = message;
        this.blindMessage = blindMessage;
        this.missMessage = missMessage;
        this.saveMessage = saveMessage;
    }
}
