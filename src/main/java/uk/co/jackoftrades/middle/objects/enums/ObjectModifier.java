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

package uk.co.jackoftrades.middle.objects.enums;

/**
 * The numeric modifiers an object can grant — stat bonuses, speed/stealth/search/
 * infravision, the full set of elemental resistances, light radius, digging,
 * extra blows/shots/might, damage reduction and movement. Mirrors the C
 * original's {@code OBJ_MOD_*} list ({@code src/list-object-modifiers.h}); the
 * commented-out block above shows an earlier naming. The active {@code OM_*}
 * constants are self-describing and documented collectively here. {@code OM_MAX}
 * is the count sentinel.
 *
 * @author Rowan Crowther
 */
public enum ObjectModifier {
    OM_NONE,
    OM_STR,
    OM_INT,
    OM_WIS,
    OM_DEX,
    OM_CON,
    OM_SPEED,
    OM_STEALTH,
    OM_SEARCH,
    OM_INFRA,
    OM_LIGHT,
    OM_TUNNEL,
    OM_BLOWS,
    OM_SHOTS,
    OM_MIGHT,
    OM_MOVES,
    OM_DAM_RED,
    OM_MAX
}
