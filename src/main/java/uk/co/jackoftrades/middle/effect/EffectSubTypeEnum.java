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

package uk.co.jackoftrades.middle.effect;

/**
 * The sub-type families an {@link uk.co.jackoftrades.middle.enums.EffectEnum}
 * effect can belong to — projection, timed effect, nourishment, summon, stat,
 * enchant, shapechange, earthquake, glyph, teleport, etc. The sub-type selects
 * which payload an {@link EffectSubTypeWrapper} carries. The constants are
 * self-describing and documented collectively here.
 *
 * @author Rowan Crowther
 */
public enum EffectSubTypeEnum {
    EST_NONE,
    EST_PROJ,
    EST_TMD,
    EST_NOURISH,
    EST_MON_TMD,
    EST_SUMMON,
    EST_SUMMON_SPEC,
    EST_STAT,
    EST_ENCHANT,
    EST_SHAPECHANGE,
    EST_EARTHQUAKE,
    EST_GLYPH,
    EST_TELEPORT,
    EST_TELEPORT_TO,
    EST_MAX
}