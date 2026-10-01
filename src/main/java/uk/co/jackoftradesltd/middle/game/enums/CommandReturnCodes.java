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

package uk.co.jackoftradesltd.middle.game.enums;

/**
 * The outcome of trying to read a named argument off a command - the port of C's
 * {@code cmd_return_codes} (cmd-core.h). These drive the "get the argument, or prompt the player
 * for it" flow: a handler asks for an argument by name, and the return code tells it whether the
 * value was there, absent, present-but-the-wrong-type, or supplied then cancelled.
 *
 * <p>The integer values are preserved from C - {@link #CMD_OK} is {@code 0} and the three failure
 * modes are negative - because C code tests these codes by sign and by value. In Java the enum
 * itself is the natural currency; the {@code int} is kept only for round-tripping to and from the
 * C-style numeric form.
 *
 * <ul>
 *   <li>{@link #CMD_OK} ({@code 0}) - the argument was present and of the requested type.</li>
 *   <li>{@link #CMD_ARG_NOT_PRESENT} ({@code -1}) - no argument of that name was set.</li>
 *   <li>{@link #CMD_ARG_WRONG_TYPE} ({@code -2}) - an argument of that name exists but holds a
 *       different type than asked for.</li>
 *   <li>{@link #CMD_ARG_ABORTED} ({@code -3}) - the player was prompted for the value and
 *       cancelled.</li>
 * </ul>
 *
 * <p>coded on 2026-10-01 / commented in full on 2026-10-01
 *
 * @author Rowan Crowther
 */
public enum CommandReturnCodes {
    /**
     * The argument was present and of the requested type; C {@code CMD_OK}, value {@code 0}.
     */
    CMD_OK(0),
    /** No argument of that name was set; C {@code CMD_ARG_NOT_PRESENT}, value {@code -1}. */
    CMD_ARG_NOT_PRESENT(-1),
    /** An argument of that name exists but holds a different type; C {@code CMD_ARG_WRONG_TYPE}, value {@code -2}. */
    CMD_ARG_WRONG_TYPE(-2),
    /** The player was prompted for the value and cancelled; C {@code CMD_ARG_ABORTED}, value {@code -3}. */
    CMD_ARG_ABORTED(-3);

    /**
     * The C numeric value of this code: {@code 0} for success, negative for the failure modes.
     */
    private final int value;

    /**
     * Binds a constant to its C numeric value.
     *
     * <p>coded on 2026-10-01 / commented in full on 2026-10-01
     *
     * @param value the value C assigns in {@code cmd_return_codes}
     */
    CommandReturnCodes(int value) {
        this.value = value;
    }

    /**
     * Maps a C-style numeric value back to its enum constant - the inverse of {@link #getValue()},
     * for use at the boundary where a raw {@code int} return code crosses into the port.
     *
     * <p>coded on 2026-10-01 / commented in full on 2026-10-01
     *
     * @param value a numeric code, expected to be one of {@code 0}, {@code -1}, {@code -2},
     *              {@code -3}
     * @return the matching constant, or {@code null} if no constant carries that value
     */
    public static CommandReturnCodes fromValue(int value) {
        for (CommandReturnCodes c : CommandReturnCodes.values()) {
            if (c.value == value) {
                return c;
            }
        }

        return null;
    }

    /**
     * Returns the C-style numeric value of this code.
     *
     * <p>coded on 2026-10-01 / commented in full on 2026-10-01
     *
     * @return {@code 0} for {@link #CMD_OK}, or the corresponding negative value for a failure
     */
    public int getValue() {
        return value;
    }
}
