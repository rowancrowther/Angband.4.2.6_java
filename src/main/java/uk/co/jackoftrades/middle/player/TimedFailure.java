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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.middle.player.enums.TimedEffectReasonType;

import java.security.InvalidParameterException;

/**
 * One condition under which a timed effect behaves specially — for example an element
 * the player resists, an object or player flag they carry, or another timed effect —
 * used when deciding whether a timed status can take hold or how strongly.
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
 * @author Rowan Crowther
 */
public class TimedFailure {
    /**
     * Shared logger; used to record the construction/accessor contract violations below before they are thrown.
     */
    private static final Logger logger = LogManager.getLogger();

    /** Discriminator selecting which payload field is live (C: {@code timed_failure.code}). */
    private TimedEffectReasonType index;
    /** Payload when {@link #index} is {@code TYPE_OBJECT_FLAG}: the required object flag. */
    private ObjectFlag oFlag;
    /** Payload when {@link #index} is {@code TYPE_RESIST}/{@code TYPE_VULN}: the element resisted or feared. */
    private ElementEnum element;
    /** Payload when {@link #index} is {@code TYPE_PLAYER_FLAG}: the required player flag. */
    private PlayerFlag pFlag;
    /** Payload when {@link #index} is {@code TYPE_TIMED_EFFECT}: the referenced timed effect. */
    private TimedEffect effect;

    /**
     * Builds a condition referring to another timed effect.
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
     * Builds a condition requiring a particular object flag.
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
     * Builds a condition keyed on an element the player either resists or is vulnerable to.
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
     * Builds a condition requiring a particular player (class/race) flag.
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
     * Placeholder overload (currently a no-op).
     *
     * <p>Declared but not yet implemented — it neither validates nor stores its arguments.
     * Present to reserve the signature; left empty until the corresponding port path is wired up.
     *
     * @param code   intended player flag (ignored for now)
     * @param effect intended timed effect (ignored for now)
     */
    public TimedFailure(@NotNull PlayerFlag code, TimedEffect effect) {
    }

    /**
     * Returns the object-flag payload.
     *
     * <p>Guarded by the discriminator: it is a misuse to read the object-flag payload
     * from an entry that does not hold one, so this throws rather than returning a stale
     * or {@code null} field.
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
     * Returns the element payload.
     *
     * @return the resisted-or-feared element this condition concerns
     * @throws IllegalArgumentException if this entry's category is neither {@code TYPE_RESIST} nor {@code TYPE_VULN}
     */
    public ElementEnum getElementCode() {
        if (index != TimedEffectReasonType.TYPE_RESIST && index != TimedEffectReasonType.TYPE_VULN) {
            String errMsg = "Invalid ObjectFlag code. Expected TYPE_RESIST or TYPE_VULN but got "
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
     * @return the player flag this condition requires
     * @throws IllegalArgumentException if this entry's category is not {@code TYPE_PLAYER_FLAG}
     */
    public PlayerFlag getPlayerFlagCode() {
        if (index != TimedEffectReasonType.TYPE_PLAYER_FLAG) {
            String errMsg = "Invalid ObjectFlag code. Expected TYPE_PLAYER_FLAG but got "
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
     * @return the timed effect this condition references
     * @throws IllegalArgumentException if this entry's category is not {@code TYPE_TIMED_EFFECT}
     */
    public TimedEffect getEffectCode() {
        if (index != TimedEffectReasonType.TYPE_TIMED_EFFECT) {
            String errMsg = "Invalid ObjectFlag code. Expected TYPE_TIMED_EFFECT but got "
                    + index + ".";
            IllegalArgumentException ex = new IllegalArgumentException(errMsg);
            logger.error(errMsg, ex);
            throw ex;
        }

        return effect;
    }
}