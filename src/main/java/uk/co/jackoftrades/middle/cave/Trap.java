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

package uk.co.jackoftrades.middle.cave;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.TrapEnum;

public class Trap {
    private int trapIndex;
    private TrapKind kind;

    private Loc grid;

    private int power;
    private int timeout;

    private Flag<TrapEnum> flags;

    public TrapKind getKind() {
        return kind;
    }

    /**
     * Checks for the existence of a given trap flag
     *
     * @param trapFlag the flag to check for
     * @return true if this trap has the flag set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean hasTrap(@NotNull TrapEnum trapFlag) {
        return flags.has(trapFlag);
    }

    /**
     * Get the current power of this trap
     *
     * @return the current power of this trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getPower() {
        return power;
    }

    @CheckReturnValue
    @Contract(pure = true)
    public int getTrapIndex() {
        return trapIndex;
    }

    @CheckReturnValue
    @Contract(pure = true)
    public int getTimeout() {
        return timeout;
    }
}