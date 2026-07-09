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

package uk.co.jackoftrades.frontend.screen.enums;

/**
 * The placement options for the player status sidebar, mirroring the
 * {@code SIDEBAR_*} layout modes of the C original.
 *
 * @author Rowan Crowther
 */
public enum Sidebar {
    /**
     * Sidebar down the left edge of the screen. @author Rowan Crowther
     */
    SIDEBAR_LEFT,
    /**
     * Sidebar across the top of the screen. @author Rowan Crowther
     */
    SIDEBAR_TOP,
    /** No sidebar shown. @author Rowan Crowther */
    SIDEBAR_NONE,
    /** Count sentinel (number of sidebar modes). @author Rowan Crowther */
    SIDEBAR_MAX
}
