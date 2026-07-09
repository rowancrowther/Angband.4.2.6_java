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
 * The contexts in which objects stack, which governs the maximum stack size and
 * stacking rules (store, pack/home, object list, monster inventory, floor,
 * quiver). Mirrors the C original's {@code OSTACK_*} modes ({@code src/obj-util.h}).
 *
 * @author Rowan Crowther
 */
public enum ObjectStackEnum {
    /**
     * NO options - this doesn't mean no stacking
     */
    OSTACK_NONE,

    /**
     * Store stacking
     */
    OSTACK_STORE,

    /**
     * Inventory and home stacking
     */
    OSTACK_PACK,

    /**
     * Object list
     */
    OSTACK_LIST,

    /**
     * Monster carrying objects
     */
    OSTACK_MONSTER,

    /**
     * Floor stacking
     */
    OSTACK_FLOOR,

    /**
     * Quiver
     */
    OSTACK_QUIVER
}
