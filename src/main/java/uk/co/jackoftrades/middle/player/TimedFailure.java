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

public class TimedFailure {
    private static final Logger logger = LogManager.getLogger();

    private TimedEffectReasonType index;
    private ObjectFlag oFlag;
    private ElementEnum element;
    private PlayerFlag pFlag;
    private TimedEffect effect;

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