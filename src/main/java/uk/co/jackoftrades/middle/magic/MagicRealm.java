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

package uk.co.jackoftrades.middle.magic;

import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.enums.TValue;

public class MagicRealm {
    private String name;
    private String code;
    private Stats stat;
    private String verb;
    private String spellNoun;
    private TValue book;

    public MagicRealm(String name, Stats stat, String verb, String spellNoun, TValue book) {
        this.name = name;
        this.stat = stat;
        this.verb = verb;
        this.spellNoun = spellNoun;
        this.book = book;
    }

    public String getName() {
        return name;
    }
}
