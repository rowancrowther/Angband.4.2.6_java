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

package uk.co.jackoftrades.backend.enums;

/**
 * Describes how a {@code RandomValue}/dice expression should be resolved into a
 * concrete number. Rather than always rolling, callers can ask for a fixed
 * summary of the distribution (minimum, average, maximum) which is needed when
 * the game wants deterministic values — for example when describing an item's
 * damage to the player or comparing two pieces of equipment without side
 * effects.
 * <p>
 * Mirrors the {@code aspect} enumeration used by {@code randcalc()} in the
 * original C source ({@code src/z-rand.h}).
 *
 * @author ClaudeCode
 */
public enum DamageAspect {
    /**
     * Minimize the roll rather than rolling
     */
    MINIMIZE,

    /**
     * Take the average roll rather than rolling
     */
    AVERAGE,

    /**
     * Maximize the roll rather than rolling
     */
    MAXIMIZE,

    /**
     * Similar to MAXIMIZE
     */
    EXTREMIFY,

    /**
     * Roll a random number
     */
    RANDOMIZE
}
