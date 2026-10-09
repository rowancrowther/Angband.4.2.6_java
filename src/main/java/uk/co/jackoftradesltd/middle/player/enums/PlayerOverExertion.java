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

package uk.co.jackoftradesltd.middle.player.enums;

/**
 * The penalties {@code player_over_exert()} can inflict on the player — the port of the anonymous
 * {@code PY_EXERT_*} enum in {@code player-util.h}.
 *
 * <p>In C these are single-bit values ({@code 0x01} to {@code 0x80}) OR-ed into an {@code int}, so
 * one call can request several penalties at once ({@code game-world.c} asks for
 * {@code PY_EXERT_HP | PY_EXERT_CUT | PY_EXERT_SLOW} while bloodlust fades). The port drops the
 * numeric values: the constants are held in a {@link uk.co.jackoftradesltd.channel.utils.Flag},
 * an {@link java.util.EnumSet} underneath, so a combination is a set of constants and only
 * membership matters. The constants keep C's declaration order, but nothing depends on it.
 *
 * <p>C rolls each requested penalty independently, in the order {@code CON}, {@code FAINT},
 * {@code SCRAMBLE}, {@code CUT}, {@code CONF}, {@code HALLU}, {@code SLOW}, {@code HP}; a call
 * with a {@code chance} of zero or less does nothing at all.
 *
 * <p>Enum PlayerOverExertion coded on 261009 / commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public enum PlayerOverExertion {
    /**
     * No penalty — C's {@code PY_EXERT_NONE}, value {@code 0x00}. In C it is the empty mask and is
     * never tested. In Java an empty {@link uk.co.jackoftradesltd.channel.utils.Flag} already
     * means "no penalty", so no caller needs to set this constant.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_NONE,
    /**
     * Constitution damage — C's {@code PY_EXERT_CON}, value {@code 0x01}. With probability
     * {@code chance}% the player is told "You have damaged your health!" and loses a point of
     * CON. The loss is permanent only when {@code chance} is at least 50 and a second roll of
     * {@code chance / 2}% succeeds, which in practice means the no-mana casting case. Ignores
     * {@code amount}.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_CON,
    /**
     * Fainting — C's {@code PY_EXERT_FAINT}, value {@code 0x02}. With probability {@code chance}%
     * the player is told "You faint from the effort!" and paralysed for 1 to {@code amount} turns,
     * bypassing free action.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_FAINT,
    /**
     * Scrambled stats — C's {@code PY_EXERT_SCRAMBLE}, value {@code 0x04}. With probability
     * {@code chance}% the player's stats are scrambled for 1 to {@code amount} turns. No message
     * is printed here; the timed effect supplies its own.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_SCRAMBLE,
    /**
     * Wounds — C's {@code PY_EXERT_CUT}, value {@code 0x08}. With probability {@code chance}% the
     * player is told "Wounds appear on your body!" and cut for 1 to {@code amount} turns.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_CUT,
    /**
     * Confusion — C's {@code PY_EXERT_CONF}, value {@code 0x10}. With probability {@code chance}%
     * the player is confused for 1 to {@code amount} turns. No message is printed here.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_CONF,
    /**
     * Hallucination — C's {@code PY_EXERT_HALLU}, value {@code 0x20}. With probability
     * {@code chance}% the player hallucinates for 1 to {@code amount} turns. No message is
     * printed here.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_HALLU,
    /**
     * Slowing — C's {@code PY_EXERT_SLOW}, value {@code 0x40}. With probability {@code chance}%
     * the player is told "You feel suddenly lethargic." and slowed for 1 to {@code amount} turns.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_SLOW,
    /**
     * Hit point damage — C's {@code PY_EXERT_HP}, value {@code 0x80}. With probability
     * {@code chance}% the player cries out in sudden pain and takes 1 to {@code amount} damage,
     * after damage reduction, attributed to "over-exertion". The damage can kill.
     *
     * <p>Coded on 261009 / commented in full on 261009.
     */
    PY_EXERT_HP
}
