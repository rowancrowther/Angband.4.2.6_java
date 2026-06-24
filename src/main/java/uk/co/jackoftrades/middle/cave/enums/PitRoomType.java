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

package uk.co.jackoftrades.middle.cave.enums;

/**
 * The kinds of "pit"/"nest" themed monster rooms the level generator can build,
 * mirroring the pit-type categories of the C original. Pits hold an ordered line
 * of related monsters; nests hold a themed jumble.
 *
 * @author ClaudeCode
 */
public enum PitRoomType {
    /**
     * Not a pit or nest. @author ClaudeCode
     */
    PIT_TYPE_NONE,
    /** A monster pit (ordered line of themed monsters). @author ClaudeCode */
    PIT_TYPE_PIT,
    /** A monster nest (themed cluster of monsters). @author ClaudeCode */
    PIT_TYPE_NEST,
    /** Some other special monster-room type. @author ClaudeCode */
    PIT_TYPE_OTHER
}
