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

package uk.co.jackoftradesltd.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType;

import java.security.InvalidParameterException;

/**
 * One condition that stops a timed effect from being increased — an object flag the player
 * has, an element they resist or are vulnerable to, a player flag they have, or another timed
 * effect that is running. If any one of an effect's conditions holds, {@code player_inc_check}
 * refuses the increase.
 *
 * <p>Ports the C {@code struct timed_failure} ({@code player-timed.h}), whose
 * {@code {code, idx}} pair is a hand-rolled tagged union: {@code code} says which
 * category the condition belongs to and {@code idx} is interpreted accordingly (an
 * object-flag index, an element, a player flag, or a timed-effect index).
 *
 * <p><b>Why typed fields instead of one {@code idx}:</b> Java has no unions, so rather
 * than reinterpret a single integer the port keeps a separate, correctly-typed field
 * per category ({@link #oFlag}, {@link #element}, {@link #pFlag}, {@link #effect}) and
 * an {@link #index} discriminator ({@link TimedEffectReasonType}) that says which one
 * is live. Exactly one payload field is meaningful at a time; the discriminator is the
 * contract for which.
 *
 * <p><b>Why every constructor and accessor validates the discriminator:</b> because the
 * type system cannot enforce "this field is only valid for that code", each entry point
 * checks {@code index} against the expected category and fails loudly (logging, then
 * throwing) on a mismatch. That turns what would be a silent union-misuse bug in C into
 * an immediate, traceable error in the port.
 *
 * <p><b>Where the entries come from and who reads them:</b> {@code PlayerTimedAssembler} builds one
 * entry per {@code fail:} line of {@code player_timed.txt}, standing in for C's
 * {@code parse_player_timed_fail}, and checks the flag, element or effect name there; this class
 * never sees a name. {@code PlayerTimed.incCheck} reads them, standing in for C's
 * {@code player_inc_check}. C links each new entry in at the head of the chain, so it walks the
 * entries last line first; the Java list is in file order and {@code incCheck} reverses it, which
 * gives the same walk.
 *
 * <p>C's {@code next} pointer has no field here, because the owning
 * {@code PlayerTimedEffect} holds the entries in a {@code List}. C's range
 * {@code assert}s on {@code idx} have no counterpart, because each payload is already an enum
 * constant.
 *
 * <p>Class TimedFailure coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class TimedFailure {
    /**
     * Shared logger; used to record the construction/accessor contract violations below before they are thrown.
     *
     * <p>Field logger coded before 261014, commented in full on 261014.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Discriminator selecting which payload field is live (C: {@code timed_failure.code}, one of
     * the {@code TMD_FAIL_FLAG_*} constants).
     *
     * <p>Set by whichever constructor ran and never changed after, so it is never {@code null} and
     * never {@code TYPE_NONE}: every constructor rejects any code but its own.
     *
     * <p>Field index coded before 261014, commented in full on 261014.
     */
    private TimedEffectReasonType index;
    /**
     * Payload when {@link #index} is {@code TYPE_OBJECT_FLAG}: the object flag whose presence blocks
     * the effect (C: {@code idx} holding an {@code OF_*} value). {@code null} for every other category.
     *
     * <p>Field oFlag coded before 261014, commented in full on 261014.
     */
    private ObjectFlag oFlag;
    /**
     * Payload when {@link #index} is {@code TYPE_RESIST} or {@code TYPE_VULN}: the element whose
     * resistance (above 0) or vulnerability (below 0) blocks the effect (C: {@code idx} holding an
     * {@code ELEM_*} value). {@code null} for every other category.
     *
     * <p>Field element coded before 261014, commented in full on 261014.
     */
    private ElementEnum element;
    /**
     * Payload when {@link #index} is {@code TYPE_PLAYER_FLAG}: the player flag whose presence blocks
     * the effect (C: {@code idx} holding a {@code PF_*} value). {@code null} for every other category.
     *
     * <p>Field pFlag coded before 261014, commented in full on 261014.
     */
    private PlayerFlag pFlag;
    /**
     * Payload when {@link #index} is {@code TYPE_TIMED_EFFECT}: the other timed effect whose running
     * blocks this one (C: {@code idx} holding a {@code TMD_*} value). {@code null} for every other
     * category.
     *
     * <p>Field effect coded before 261014, commented in full on 261014.
     */
    private TimedEffect effect;

    /**
     * Builds a condition that blocks the effect while another timed effect is running (C:
     * {@code TMD_FAIL_FLAG_TIMED_EFFECT}, the {@code fail:5:} line, for example {@code OPP_POIS}
     * blocking {@code POISONED}).
     *
     * <p>Stores the effect and the code; the other three payloads stay {@code null}.
     *
     * <p>Constructor TimedFailure(TimedEffect, TimedEffectReasonType) coded before 261014, commented
     * in full on 261014.
     *
     * @param code  the referenced timed effect
     * @param index must be {@code TYPE_TIMED_EFFECT}; any other value is a programming
     *              error and is rejected
     * @throws java.security.InvalidParameterException if {@code index} is not {@code TYPE_TIMED_EFFECT}
     */
    public TimedFailure(@NotNull TimedEffect code, TimedEffectReasonType index) {
        if (index != TimedEffectReasonType.TYPE_TIMED_EFFECT) {
            String errMsg = "Invalid TimedFailure code. Expected TYPE_TIMED_EFFECT \n" +
                    "Received " + index;
            InvalidParameterException ex = new InvalidParameterException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        this.index = index;
        this.effect = code;
    }

    /**
     * Builds a condition that blocks the effect while the player has an object flag (C:
     * {@code TMD_FAIL_FLAG_OBJECT}, the {@code fail:1:} line, for example {@code FREE_ACT} blocking
     * {@code SLOW}).
     *
     * <p>Stores the flag and the code; the other three payloads stay {@code null}.
     *
     * <p>Constructor TimedFailure(ObjectFlag, TimedEffectReasonType) coded before 261014, commented
     * in full on 261014.
     *
     * @param code  the required object flag
     * @param index must be {@code TYPE_OBJECT_FLAG}
     * @throws java.security.InvalidParameterException if {@code index} is not {@code TYPE_OBJECT_FLAG}
     */
    @Contract(mutates = "this")
    public TimedFailure(@NotNull ObjectFlag code, TimedEffectReasonType index) {
        if (index != TimedEffectReasonType.TYPE_OBJECT_FLAG) {
            String errMsg = "Invalid TimedFailure code. Expected TYPE_OBJECT_FLAG \n" +
                    "Received " + index;
            InvalidParameterException ex = new InvalidParameterException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        this.index = index;
        this.oFlag = code;
    }

    /**
     * Builds a condition that blocks the effect while the player resists an element (C:
     * {@code TMD_FAIL_FLAG_RESIST}, the {@code fail:2:} line) or is vulnerable to one (C:
     * {@code TMD_FAIL_FLAG_VULN}, the {@code fail:3:} line).
     *
     * <p>Both categories share this constructor and the one {@link #element} payload, because in C
     * both store an {@code ELEM_*} value in {@code idx}. {@code index} alone tells
     * {@code player_inc_check} whether to test for a resist level above 0 or below 0.
     *
     * <p>Constructor TimedFailure(ElementEnum, TimedEffectReasonType) coded before 261014, commented
     * in full on 261014.
     *
     * @param code  the element in question
     * @param index must be {@code TYPE_RESIST} or {@code TYPE_VULN} (the only two element-based
     *              categories), which is why this constructor accepts a pair of valid codes
     * @throws java.security.InvalidParameterException if {@code index} is neither {@code TYPE_RESIST} nor {@code TYPE_VULN}
     */
    public TimedFailure(@NotNull ElementEnum code, TimedEffectReasonType index) {
        if (index != TimedEffectReasonType.TYPE_RESIST && index != TimedEffectReasonType.TYPE_VULN) {
            String errMsg = "Invalid TimedFailure code. Expected TYPE_VULN or TYPE_RESIST\n" +
                    "Received " + index;
            InvalidParameterException ex = new InvalidParameterException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        this.index = index;
        element = code;
    }

    /**
     * Builds a condition that blocks the effect while the player has a player flag (C:
     * {@code TMD_FAIL_FLAG_PLAYER}, the {@code fail:4:} line, for example {@code ROCK} blocking
     * {@code CUT}).
     *
     * <p>Stores the flag and the code; the other three payloads stay {@code null}.
     *
     * <p>Constructor TimedFailure(PlayerFlag, TimedEffectReasonType) coded before 261014, commented
     * in full on 261014.
     *
     * @param code  the required player flag
     * @param index must be {@code TYPE_PLAYER_FLAG}
     * @throws java.security.InvalidParameterException if {@code index} is not {@code TYPE_PLAYER_FLAG}
     */
    public TimedFailure(@NotNull PlayerFlag code, TimedEffectReasonType index) {
        if (index != TimedEffectReasonType.TYPE_PLAYER_FLAG) {
            String errMsg = "Invalid TimedFailure code. Expected TYPE_PLAYER_FLAG\n" +
                    "Received " + index;
            InvalidParameterException ex = new InvalidParameterException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        this.index = index;
        this.pFlag = code;
    }

    /**
     * Returns the object-flag payload.
     *
     * <p>Guarded by the discriminator: it is a misuse to read the object-flag payload
     * from an entry that does not hold one, so this throws rather than returning a stale
     * or {@code null} field.
     *
     * <p>Method getObjFlagCode coded before 261014, commented in full on 261014.
     *
     * @return the object flag this condition requires
     * @throws IllegalArgumentException if this entry's category is not {@code TYPE_OBJECT_FLAG}
     */
    @Contract(pure = true)
    @CheckReturnValue
    @NotNull
    public ObjectFlag getObjFlagCode() {
        if (index != TimedEffectReasonType.TYPE_OBJECT_FLAG) {
            String errMsg = "Invalid ObjectFlag code. Expected TYPE_OBJECT_FLAG but got "
                    + index + ".";
            IllegalArgumentException ex = new IllegalArgumentException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        return oFlag;
    }

    /**
     * Returns the element payload, for either of the two element categories.
     *
     * <p>Guarded by the discriminator, as {@link #getObjFlagCode()} is.
     *
     * <p>Method getElementCode coded before 261014, commented in full on 261014.
     *
     * @return the resisted-or-feared element this condition concerns
     * @throws IllegalArgumentException if this entry's category is neither {@code TYPE_RESIST} nor {@code TYPE_VULN}
     */
    public ElementEnum getElementCode() {
        if (index != TimedEffectReasonType.TYPE_RESIST && index != TimedEffectReasonType.TYPE_VULN) {
            String errMsg = "Invalid TimedEffectReasonType code. Expected TYPE_RESIST or TYPE_VULN but got "
                    + index + ".";
            IllegalArgumentException ex = new IllegalArgumentException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        return element;
    }

    /**
     * Returns the player-flag payload.
     *
     * <p>Guarded by the discriminator, as {@link #getObjFlagCode()} is.
     *
     * <p>Method getPlayerFlagCode coded before 261014, commented in full on 261014.
     *
     * @return the player flag this condition requires
     * @throws IllegalArgumentException if this entry's category is not {@code TYPE_PLAYER_FLAG}
     */
    public PlayerFlag getPlayerFlagCode() {
        if (index != TimedEffectReasonType.TYPE_PLAYER_FLAG) {
            String errMsg = "Invalid TimedEffectReasonType code. Expected TYPE_PLAYER_FLAG but got "
                    + index + ".";
            IllegalArgumentException ex = new IllegalArgumentException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        return pFlag;
    }

    /**
     * Returns the timed-effect payload.
     *
     * <p>Guarded by the discriminator, as {@link #getObjFlagCode()} is.
     *
     * <p>Method getEffectCode coded before 261014, commented in full on 261014.
     *
     * @return the timed effect this condition references
     * @throws IllegalArgumentException if this entry's category is not {@code TYPE_TIMED_EFFECT}
     */
    public TimedEffect getEffectCode() {
        if (index != TimedEffectReasonType.TYPE_TIMED_EFFECT) {
            String errMsg = "Invalid TimedEffectReasonType code. Expected TYPE_TIMED_EFFECT but got "
                    + index + ".";
            IllegalArgumentException ex = new IllegalArgumentException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        return effect;
    }

    /**
     * Returns the category of this condition (C: {@code timed_failure.code}).
     *
     * <p>{@code PlayerTimed.incCheck} switches on this value to decide which payload accessor is
     * valid to call. It is never {@code null} and never {@code TYPE_NONE}, because every
     * constructor rejects any code but its own.
     *
     * <p>Method getCode coded before 261014, commented in full on 261014.
     *
     * @return the discriminator that says which payload is live
     */
    public TimedEffectReasonType getCode() {
        return index;
    }
}