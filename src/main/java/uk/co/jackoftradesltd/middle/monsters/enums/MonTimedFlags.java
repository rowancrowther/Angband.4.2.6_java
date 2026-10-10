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

package uk.co.jackoftradesltd.middle.monsters.enums;

/**
 * The behaviour flags passed to the monster timed-effect functions: {@code Monster.setTimed},
 * {@code Monster.decrementTimed} and {@code Monster.clearTimed}, the ports of C's
 * {@code mon_set_timed}, {@code mon_dec_timed} and {@code mon_clear_timed} in
 * {@code mon-timed.c}. Each constant is one of the {@code MON_TMD_FLG_*} defines in
 * {@code mon-timed.h}.
 *
 * <p>C passes these as an {@code int} and combines them with {@code |} and {@code &}, using
 * the values {@code 0x01} (notify), {@code 0x04} (no message) and {@code 0x08} (no fail), with
 * {@code 0x02} left unused. Java passes a {@code Flag<MonTimedFlags>} instead, which is an
 * {@link java.util.EnumSet} underneath, so the constants have no numeric value and the gap at
 * {@code 0x02} does not exist. Nothing in the port can OR one of these into a plain
 * {@code int}; a caller that does that in C builds a {@code Flag} here.
 *
 * <p>Enum MonTimedFlags coded before 260815, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public enum MonTimedFlags {
    /**
     * C's {@code MON_TMD_FLG_NOTIFY} ({@code 0x01}): give a notification. The message at the end
     * of {@code mon_set_timed} in {@code mon-timed.c} is only printed when this flag is set and
     * {@link #MON_TMD_FLG_NOMESSAGE} is not. {@code mon_set_timed} switches the flag on itself when
     * the effect starts (old timer zero) or ends (new timer zero), so a caller only has to pass it
     * to get a message for a change in between, such as an increase.
     *
     * <p>Constant MON_TMD_FLG_NOTIFY coded before 260815, commented in full on 261010.
     */
    MON_TMD_FLG_NOTIFY,
    /**
     * C's {@code MON_TMD_FLG_NOMESSAGE} ({@code 0x04}): never show a message. It wins over
     * {@link #MON_TMD_FLG_NOTIFY} when both are set.
     *
     * <p>Constant MON_TMD_FLG_NOMESSAGE coded before 260815, commented in full on 261010.
     */
    MON_TMD_FLG_NOMESSAGE,
    /**
     * C's {@code MON_TMD_FLG_NOFAIL} ({@code 0x08}): never fail. Makes {@code does_resist} in
     * {@code mon-timed.c} return false before it looks at the monster's race flags or its saving
     * throw, so the game can override a monster's innate resistance.
     *
     * <p>Constant MON_TMD_FLG_NOFAIL coded before 260815, commented in full on 261010.
     */
    MON_TMD_FLG_NOFAIL
}
