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
 * How an object flag becomes known to the player — never, by normal use, after a
 * timed delay, or immediately on wielding. Mirrors the C original's
 * {@code OFID_*} identification categories ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public enum ObjectFlagID {
    /**
     * The flag is never identified this way. @author ClaudeCode
     */
    OFID_NONE,
    /** Identified through normal use. @author ClaudeCode */
    OFID_NORMAL,
    /** Identified after a timed delay of use. @author ClaudeCode */
    OFID_TIMED,
    /** Identified immediately on wielding. @author ClaudeCode */
    OFID_WIELD
}
