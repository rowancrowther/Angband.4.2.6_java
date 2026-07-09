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
 * The grouping of object flags by purpose — sustains, protections, light, melee,
 * "bad" (negative) flags, digging, throwing, curse-only, etc. Used to organise
 * flags in displays. Mirrors the C original's {@code OFT_*} flag types
 * ({@code src/object.h}); the constants are self-describing. {@code OFT_MAX} is
 * the count sentinel.
 *
 * @author Rowan Crowther
 */
public enum ObjectFlagType {
    OFT_NONE,
    OFT_SUST,
    OFT_PROT,
    OFT_MISC,
    OFT_LIGHT,
    OFT_MELEE,
    OFT_BAD,
    OFT_DIG,
    OFT_THROW,
    OFT_CURSE_ONLY,
    OFT_MAX
}
