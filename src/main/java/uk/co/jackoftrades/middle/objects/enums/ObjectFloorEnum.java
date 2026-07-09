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
 * Filtering options for listing objects on the floor (test against a tester,
 * sensed/known only, top item only, visible only). Mirrors the C original's
 * {@code OFLOOR_*} flags ({@code src/obj-util.h}).
 *
 * @author Rowan Crowther
 */
public enum ObjectFloorEnum {
    /**
     * No options
     */
    OFLOOR_NONE,

    /**
     * Verify item tester
     */
    OFLOOR_TEST,

    /**
     * Sensed or known items only
     */
    OFLOOR_SENSE,

    /**
     * Only the top item
     */
    OFLOOR_TOP,

    /**
     * Visible items only
     */
    OFLOOR_VISIBLE
}
