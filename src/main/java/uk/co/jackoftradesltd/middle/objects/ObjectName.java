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

package uk.co.jackoftradesltd.middle.objects;

import uk.co.jackoftradesltd.middle.game.NameCreator;
import uk.co.jackoftradesltd.middle.player.PlayerName;
import uk.co.jackoftradesltd.middle.player.enums.RandnameType;

import java.util.Map;

public class ObjectName {

    public static Map<RandnameType, Integer> nameSections;

    public static String randnameMake(RandnameType randnameType, int min, int max,
                                      Map<RandnameType, Integer> sections) {
        PlayerName pn = new PlayerName();
        return NameCreator.randnameMake(randnameType, min, max);
    }
}
