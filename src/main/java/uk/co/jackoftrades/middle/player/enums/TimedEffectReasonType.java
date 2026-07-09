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

package uk.co.jackoftrades.middle.player.enums;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Discriminator describing <em>what kind of game condition</em> a single
 * {@link uk.co.jackoftrades.middle.player.TimedFailure} entry refers to.
 *
 * <p>Ports the {@code code} field of the C {@code struct timed_failure}
 * ({@code player-timed.h}), whose companion {@code idx} field is interpreted
 * differently depending on this code (it might index an object flag, a resisted or
 * vulnerable element, a player flag, or another timed effect). In C this is a loose
 * {@code int} compared against the {@code TMD_FAIL_FLAG_*} constants; here it is a
 * proper enum so the alternative cannot be confused with an unrelated integer.
 *
 * <p><b>Why each constant carries an explicit numeric value:</b> the original codes
 * are stable integers that appear in data/serialisation, so the port pins each
 * constant to its C value rather than relying on {@link Enum#ordinal()} (which would
 * silently shift if the list were ever reordered). {@link #fromValue(int)} performs
 * the reverse mapping when reading those raw integers back in.
 *
 * @author Rowan Crowther
 */
public enum TimedEffectReasonType {
    /**
     * Unset / placeholder — no condition (C {@code TMD_FAIL_FLAG} value 0).
     */
    TYPE_NONE(0),
    /** The companion index names an object flag the player must have. */
    TYPE_OBJECT_FLAG(1),
    /** The companion index names an element the player resists. */
    TYPE_RESIST(2),
    /** The companion index names an element the player is vulnerable to. */
    TYPE_VULN(3),
    /** The companion index names a player (class/race) flag. */
    TYPE_PLAYER_FLAG(4),
    /** The companion index names another timed effect. */
    TYPE_TIMED_EFFECT(5);

    /** The stable C integer code for this reason type; preserved for data compatibility. */
    private final int value;

    /**
     * Binds the constant to its fixed C code.
     *
     * @param index the {@code TMD_FAIL_FLAG_*} integer this constant represents
     */
    @Contract(mutates = "this")
    private TimedEffectReasonType(int index) {
        this.value = index;
    }

    /**
     * Resolves a raw C reason code back into its enum constant.
     *
     * <p>Used when reading integers that originated from the C {@code timed_failure.code}
     * field. Returns {@code null} for any unrecognised value rather than throwing, so a
     * malformed or future code can be handled gracefully by the caller.
     *
     * @param value a {@code TMD_FAIL_FLAG_*} integer
     * @return the matching constant, or {@code null} if {@code value} is not a known code
     */
    @Contract(pure = true)
    @Nullable
    @CheckReturnValue
    public static TimedEffectReasonType fromValue(int value) {
        return switch (value) {
            case 0 -> TYPE_NONE;
            case 1 -> TYPE_OBJECT_FLAG;
            case 2 -> TYPE_RESIST;
            case 3 -> TYPE_VULN;
            case 4 -> TYPE_PLAYER_FLAG;
            case 5 -> TYPE_TIMED_EFFECT;
            default -> null;
        };
    }
}