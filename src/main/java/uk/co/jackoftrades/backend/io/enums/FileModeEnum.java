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

package uk.co.jackoftrades.backend.io.enums;

/**
 * The type of access required for a file as used in fileOpen()
 */
public enum FileModeEnum {
    /**
     * Write access required
     */
    MODE_WRITE,

    /**
     * Read access required
     */
    MODE_READ,

    /**
     * Append access required
     */
    MODE_APPEND
}
