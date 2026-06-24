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

package uk.co.jackoftrades.middle.objects.enums;

/**
 * The categories an object property can belong to — a stat boost, a modifier, a
 * flag, an ignore, a resistance, a vulnerability or an immunity. Mirrors the C
 * original's {@code OBJ_PROPERTY_*} types ({@code src/object.h}); the constants
 * are self-describing. {@code OBJ_PROPERTY_MAX} is the count sentinel.
 *
 * @author ClaudeCode
 */
public enum ObjPropertyType {
    OBJ_PROPERTY_NONE,
    OBJ_PROPERTY_STAT,
    OBJ_PROPERTY_MOD,
    OBJ_PROPERTY_FLAG,
    OBJ_PROPERTY_IGNORE,
    OBJ_PROPERTY_RESIST,
    OBJ_PROPERTY_VULN,
    OBJ_PROPERTY_IMM,
    OBJ_PROPERTY_MAX
}
