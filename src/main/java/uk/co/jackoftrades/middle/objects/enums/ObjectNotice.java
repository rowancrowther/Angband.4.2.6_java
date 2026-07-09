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
 * Per-object "notice" flags tracking what the player has learned/decided about an
 * item — that it has been worn, assessed, marked to ignore, or is merely imagined
 * (hallucinated). Mirrors the C original's {@code OBJ_NOTICE_*} flags
 * ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public enum ObjectNotice {
    /**
     * The object has been worn/wielded. @author Rowan Crowther
     */
    OBJ_NOTICE_WORN,
    /**
     * The object has been assessed (its quality judged). @author Rowan Crowther
     */
    OBJ_NOTICE_ASSESSED,
    /** The object is marked to be ignored. @author Rowan Crowther */
    OBJ_NOTICE_IGNORE,
    /** The object is imagined (seen only while hallucinating). @author Rowan Crowther */
    OBJ_NOTICE_IMAGINED
}
