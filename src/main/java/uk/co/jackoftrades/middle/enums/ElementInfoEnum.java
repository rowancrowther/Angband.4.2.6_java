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

package uk.co.jackoftrades.middle.enums;

/**
 * Flags describing how an object kind relates to a damage element, mirroring the
 * C original's {@code EL_INFO_*} flags ({@code src/object.h}) — whether the
 * material is destroyed by, ignores, or is randomly affected by that element.
 *
 * @author Rowan Crowther
 */
public enum ElementInfoEnum {
    /**
     * The object is damaged/destroyed by this element. @author Rowan Crowther
     */
    EL_INFO_HATES,
    /**
     * The object ignores (is unaffected by) this element. @author Rowan Crowther
     */
    EL_INFO_IGNORE,
    /** The object is randomly affected by this element. @author Rowan Crowther */
    EL_INFO_RANDOM
}
