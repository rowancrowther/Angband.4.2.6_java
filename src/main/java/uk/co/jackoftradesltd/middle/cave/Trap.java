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

package uk.co.jackoftradesltd.middle.cave;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;

/**
 * A trap instance placed on a specific dungeon grid — distinct from its {@link TrapKind} template. It carries the
 * live state: the kind it was made from, its location, its current power, its disarm/cooldown timeout and the flags
 * that apply to this one trap.
 *
 * <p>This is the Java port of {@code struct trap} in {@code trap.h}. Five of C's fields map across one for one:
 * {@code kind}, {@code grid}, {@code power}, {@code timeout} and {@code flags}. Two do not:
 * <ul>
 *   <li>{@code t_idx}, the kind index, has no field here. It is always the same as the kind's own index, so callers
 *   read it through {@code getKind().getTrapKindIndex()}.</li>
 *   <li>{@code next}, the link to the next trap in the grid, has no field here. A {@link Square} holds its traps as a
 *   list, and {@code Square.getTraps()} replaces the {@code while (trap) trap = trap->next} walk.</li>
 * </ul>
 *
 * <p>C stores {@code power} and {@code timeout} as {@code uint8_t}; here they are plain {@code int}s. No path in the
 * port can push either out of 0..255: the only decrement, {@link #decrementTimeout()}, is guarded by the caller
 * exactly as C guards it.
 *
 * <p>Class coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class Trap {
    /**
     * The kind/template this trap is an instance of, C's {@code struct trap_kind *kind}. It also supplies the kind
     * index that C keeps separately as {@code t_idx}.
     */
    private TrapKind kind;

    /**
     * The grid this trap occupies, C's {@code struct loc grid}.
     */
    private Loc grid;

    /**
     * The trap's power, C's {@code power}. For a player trap it is the visibility: {@code Chunk}'s reveal logic
     * compares it against the player's search skill. For a door lock it is the lock strength.
     */
    private int power;
    /**
     * Turns until a disabled trap works again, C's {@code timeout}. Zero means the trap is armed; anything above
     * zero means it is dormant and is counted down once a turn by {@code Chunk.decreaseTrapTimeout()}.
     */
    private int timeout;

    /**
     * Flags that apply to this one trap, C's {@code bitflag flags[TRF_SIZE]}. The flags shared by every trap of the
     * kind live on {@link TrapKind}, not here.
     */
    private Flag<TrapEnum> flags;

    /**
     * Returns the template this trap was made from.
     *
     * <p>Function getKind coded before 260930, commented in full on 260930.
     *
     * @return the {@link TrapKind} template for this trap
     */
    public TrapKind getKind() {
        return kind;
    }

    /**
     * Checks whether this one trap carries a given flag, the Java form of {@code trf_has(trap->flags, flag)}. Flags
     * held by the kind are not consulted; ask {@link TrapKind} for those.
     *
     * <p>Function hasTrap coded before 260930, commented in full on 260930.
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
     * Returns this trap's power, C's {@code trap->power}.
     *
     * <p>Function getPower coded before 260930, commented in full on 260930.
     *
     * @return the current power of this trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getPower() {
        return power;
    }

    /**
     * Returns the turns left before this trap works again, C's {@code trap->timeout}.
     *
     * <p>Function getTimeout coded before 260930, commented in full on 260930.
     *
     * @return turns until the trap can trigger again (0 = ready)
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getTimeout() {
        return timeout;
    }

    /**
     * Ticks this trap's timeout down by one turn, the {@code trap->timeout--} in {@code game-world.c}. The method
     * does not test for zero: C only decrements inside {@code if (trap->timeout)}, so the caller must do the same
     * ({@code Chunk.decreaseTrapTimeout()} does). Called at zero, it would go to -1 where C's {@code uint8_t} would
     * wrap to 255.
     *
     * <p>Function decrementTimeout coded before 260930, commented in full on 260930.
     */
    public void decrementTimeout() {
        timeout--;
    }
}
