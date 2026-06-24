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
 * Per-object "notice" flags tracking what the player has learned/decided about an
 * item — that it has been worn, assessed, marked to ignore, or is merely imagined
 * (hallucinated). Mirrors the C original's {@code OBJ_NOTICE_*} flags
 * ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public enum ObjectNotice {
    /**
     * The object has been worn/wielded. @author ClaudeCode
     */
    OBJ_NOTICE_WORN,
    /** The object has been assessed (its quality judged). @author ClaudeCode */
    OBJ_NOTICE_ASSESSED,
    /** The object is marked to be ignored. @author ClaudeCode */
    OBJ_NOTICE_IGNORE,
    /** The object is imagined (seen only while hallucinating). @author ClaudeCode */
    OBJ_NOTICE_IMAGINED
}
