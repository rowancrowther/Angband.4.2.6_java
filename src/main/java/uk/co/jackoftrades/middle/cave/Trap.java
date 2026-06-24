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

/**
 * A trap instance placed on a specific dungeon grid — distinct from its
 * {@link TrapKind} template. It carries the live state (location, current power,
 * disarm/cooldown timeout and per-instance flags). This is the Java port of the
 * C original's {@code struct trap} ({@code src/trap.h}).
 *
 * @author ClaudeCode
 */
public class Trap {
    /**
     * This trap's index in the level's trap list.
     *
     * @author ClaudeCode
     */
    private int trapIndex;
    /**
     * The kind/template this trap is an instance of.
     *
     * @author ClaudeCode
     */
    private TrapKind kind;

    /**
     * The grid this trap occupies.
     *
     * @author ClaudeCode
     */
    private Loc grid;

    /**
     * The trap's current power (affects difficulty/severity).
     *
     * @author ClaudeCode
     */
    private int power;
    /**
     * Turns until the trap can trigger again (0 means ready/active).
     *
     * @author ClaudeCode
     */
    private int timeout;

    /**
     * Per-instance trap flags.
     *
     * @author ClaudeCode
     */
    private Flag<TrapEnum> flags;

    /**
     * @return the {@link TrapKind} template for this trap
     * @author ClaudeCode
     */
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

    /**
     * @return this trap's index in the level's trap list
     * @author ClaudeCode
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getTrapIndex() {
        return trapIndex;
    }

    /**
     * @return turns until the trap can trigger again (0 = ready)
     * @author ClaudeCode
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getTimeout() {
        return timeout;
    }
}