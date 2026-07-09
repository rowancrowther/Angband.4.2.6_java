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
 * Specifies what type a file is when writing. Used in fileOpen()
 */
public enum FileTypeEnum {
    /**
     * A text file
     */
    FTYPE_TEXT,

    /**
     * A save file
     */
    FTYPE_SAVE,

    /**
     * A raw binary file
     */
    FTYPE_RAW,

    /**
     * An HTML file
     */
    FTYPE_HTML,

    /**
     * A null type returned if the file doesn't exist
     */
    FTYPE_NULL
}