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

import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.List;

/**
 * The static definition of a single timed player status — all the data describing how one
 * {@code TMD_*} effect behaves, as distinct from the live counter that tracks how long an
 * instance has left to run.
 *
 * <p>Ports the C {@code struct timed_effect_data} ({@code player-timed.h}), parsed from
 * {@code player_timed.txt}. Each instance bundles the effect's identity, its begin/end/change
 * messages, the screen-redraw and model-update flags to raise on a change, the conditions under
 * which it fails to apply ({@link TimedFailure}), its severity {@link TimedGrade}s, the effects
 * fired on begin/end, and the temporary combat properties (resist/brand/slay) some statuses
 * confer.
 *
 * <p><b>Why one rich record:</b> the timed-effect system is entirely data-driven — adding or
 * tuning a status is an edit to {@code player_timed.txt}, not to code — so every behavioural
 * knob a status can need is gathered here, keyed by its {@link TimedEffect} identity.
 *
 * @author ClaudeCode
 */
public class PlayerTimedEffect {
    /**
     * Which {@code TMD_*} status this record defines.
     */
    private TimedEffect name;
    /** Packed redraw ({@code PR_*}) flags to raise when this effect changes (C: {@code flag_redraw}). */
    private int flagRedraw;
    /** Packed update ({@code PU_*}) flags to raise when this effect changes (C: {@code flag_update}). */
    private int flagUpdate;

    /** Human-readable description of the status. */
    private String description;
    /** Message shown when the status ends. */
    private String onEnd;
    /** Message shown when the status's level increases. */
    private String onIncrease;
    /** Message shown when the status's level decreases. */
    private String onDecrease;
    /** Message type/channel used for the messages above. */
    private MessageType msgT;
    /**
     * Conditions under which the status fails to take hold (resisted element, required flag, …).
     */
    private List<TimedFailure> fail;
    /** Ordered severity bands of the status (see {@link TimedGrade}). */
    private List<TimedGrade> grade;
    /** Effect fired when the status begins. */
    private Effect onBeginEffect;
    /** Effect fired when the status ends. */
    private Effect onEndEffect;
    /** Whether re-applying the status refuses to stack with an existing instance. */
    private boolean NonStacking;
    /** Minimum value the status can be reduced to while still active. */
    private int lowerBound;
    /** Object flag whose presence duplicates/implies this status (C: object-flag duplicate). */
    private int ofFlagDup;
    /** Object flag treated as synonymous with this status. */
    private ObjectFlag oFlagSyn;
    /** Element temporarily resisted while the status is active. */
    private ElementEnum tempResist;
    /** Brand temporarily added to the player's attacks while the status is active. */
    private Brand tempBrand;
    /** Slay temporarily added to the player's attacks while the status is active. */
    private Slay tempSlay;

    /**
     * Builds the full static definition of a timed status from its parsed attributes.
     *
     * <p>Each parameter populates the like-named field; see those fields for the detailed meaning
     * of each. Note the redraw/update flags are derived from the {@link TimedEffect} identity and
     * so are not passed here.
     *
     * @param name          the {@link TimedEffect} this defines
     * @param description   human-readable description
     * @param onEnd         message shown when the status ends
     * @param onIncrease    message shown when the status's level rises
     * @param onDecrease    message shown when the status's level falls
     * @param msgT          message channel for the above
     * @param fail          conditions under which the status fails to apply
     * @param grade         ordered severity bands
     * @param onBeginEffect effect fired on begin
     * @param onEndEffect   effect fired on end
     * @param nonStacking   whether re-application refuses to stack
     * @param lowerBound    minimum value while active
     * @param ofFlagDup     duplicating object flag
     * @param oFlagSyn      synonymous object flag
     * @param tempResist    element temporarily resisted
     * @param tempBrand     brand temporarily granted
     * @param tempSlay      slay temporarily granted
     */
    public PlayerTimedEffect(TimedEffect name, String description,
                             String onEnd, String onIncrease,
                             String onDecrease, MessageType msgT,
                             List<TimedFailure> fail, List<TimedGrade> grade,
                             Effect onBeginEffect, Effect onEndEffect,
                             boolean nonStacking, int lowerBound,
                             int ofFlagDup, ObjectFlag oFlagSyn,
                             ElementEnum tempResist, Brand tempBrand,
                             Slay tempSlay) {
        this.name = name;
        this.description = description;
        this.onEnd = onEnd;
        this.onIncrease = onIncrease;
        this.onDecrease = onDecrease;
        this.msgT = msgT;
        this.fail = fail;
        this.grade = grade;
        this.onBeginEffect = onBeginEffect;
        this.onEndEffect = onEndEffect;
        NonStacking = nonStacking;
        this.lowerBound = lowerBound;
        this.ofFlagDup = ofFlagDup;
        this.oFlagSyn = oFlagSyn;
        this.tempResist = tempResist;
        this.tempBrand = tempBrand;
        this.tempSlay = tempSlay;
    }
}