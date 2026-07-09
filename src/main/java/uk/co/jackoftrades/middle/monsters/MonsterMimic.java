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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.util.List;

/**
 * The set of object kinds a "mimic" monster can disguise itself as on the floor
 * (so it looks like an item until disturbed). This is the Java port of the C
 * original's {@code struct monster_mimic} ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterMimic {
    /**
     * The object kinds this monster can mimic.
     *
     * @author Rowan Crowther
     */
    private List<ObjectKind> kinds;

    /**
     * Build a mimic entry from its allowed object kinds.
     *
     * @param kinds the object kinds the monster can mimic
     * @author Rowan Crowther
     */
    public MonsterMimic(List<ObjectKind> kinds) {
        this.kinds = kinds;
    }
}