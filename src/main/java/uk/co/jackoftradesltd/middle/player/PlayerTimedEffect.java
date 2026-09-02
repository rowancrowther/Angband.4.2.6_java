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

import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.Slay;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

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
 * @author Rowan Crowther
 */
public class PlayerTimedEffect {
    /**
     * Which {@code TMD_*} status this record defines.
     */
    private TimedEffect name;
    /** Packed redraw ({@code PR_*}) flags to raise when this effect changes (C: {@code flag_redraw}). */
    private FlagView<PlayerRedraw> flagRedraw;
    /** Packed update ({@code PU_*}) flags to raise when this effect changes (C: {@code flag_update}). */
    private FlagView<PlayerUpdateEnum> flagUpdate;

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
    private boolean nonStacking;
    /** Minimum value the status can be reduced to while still active. */
    private int lowerBound;
    /** Object flag whose presence duplicates/implies this status (C: object-flag duplicate). */
    private ObjectFlag oFlagDup;
    /**
     * Object flag treated as synonymous with this status.
     */
    private boolean oFlagExactlySyn;
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
     * @param nonStacking     whether re-application refuses to stack
     * @param lowerBound      minimum value while active
     * @param oFlagDup        the object flag this status duplicates (C {@code oflag_dup}); despite the
     *                        parameter name it populates {@link #oFlagDup}
     * @param oFlagExactlySyn whether the status is an <em>exact</em> synonym of that flag (C
     *                        {@code oflag_syn}); populates {@link #oFlagExactlySyn}
     * @param tempResist      element temporarily resisted
     * @param tempBrand       brand temporarily granted
     * @param tempSlay        slay temporarily granted
     */
    public PlayerTimedEffect(TimedEffect name, String description,
                             String onEnd, String onIncrease,
                             String onDecrease, MessageType msgT,
                             List<TimedFailure> fail, List<TimedGrade> grade,
                             Effect onBeginEffect, Effect onEndEffect,
                             boolean nonStacking, int lowerBound,
                             ObjectFlag oFlagDup,
                             boolean oFlagExactlySyn,
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
        this.nonStacking = nonStacking;
        this.lowerBound = lowerBound;
        this.oFlagDup = oFlagDup;
        this.oFlagExactlySyn = oFlagExactlySyn;
        this.tempResist = tempResist;
        this.tempBrand = tempBrand;
        this.tempSlay = tempSlay;

        // Add in the Update and redraw data
        flagUpdate = name.getUpdateFlags();
        flagRedraw = name.getRedrawFlags();
    }

    /**
     * @return the {@link TimedEffect} identity this record defines
     */
    public TimedEffect getName() {
        return name;
    }

    /**
     * Returns the screen regions this effect dirties, as a read-only view.
     *
     * <p>Forwarded from the {@link TimedEffect} constant, not stored independently: the
     * constructor takes the set from {@link TimedEffect#getRedrawFlags} and holds it as a
     * {@link FlagView} throughout. No copy is made at any point in that chain, so this is
     * ultimately the enum constant's own set — which is safe only because a view withholds
     * mutation from everyone who receives it. A single {@link uk.co.jackoftradesltd.channel.utils.Flag}
     * anywhere along the chain would put a JVM-lifetime singleton within a caller's reach.
     *
     * <p>Function getFlagRedraw return type narrowed to {@link FlagView} on 260818, when the
     * backing field was narrowed with it.
     *
     * @return a read-only view of the {@code PR_*} redraw flags to raise on a change
     */
    public FlagView<PlayerRedraw> getFlagRedraw() {
        return flagRedraw;
    }

    /**
     * Returns the derived quantities this effect invalidates, as a read-only view.
     *
     * <p>Read-only for the same reason as {@link #getFlagRedraw}.
     *
     * <p>Function getFlagUpdate return type narrowed to {@link FlagView} on 260818.
     *
     * @return a read-only view of the {@code PU_*} update flags to raise on a change
     */
    public FlagView<PlayerUpdateEnum> getFlagUpdate() {
        return flagUpdate;
    }

    /**
     * @return the human-readable description of the status
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the message shown when the status ends
     */
    public String getOnEnd() {
        return onEnd;
    }

    /**
     * @return the message shown when the status's level rises
     */
    public String getOnIncrease() {
        return onIncrease;
    }

    /**
     * @return the message shown when the status's level falls
     */
    public String getOnDecrease() {
        return onDecrease;
    }

    /**
     * @return the message channel used for the change messages
     */
    public MessageType getMsgT() {
        return msgT;
    }

    /**
     * @return the conditions under which the status fails to apply
     */
    public List<TimedFailure> getFail() {
        return fail;
    }

    /**
     * @return the ordered severity grades of the status
     */
    public List<TimedGrade> getGrade() {
        return grade;
    }

    /**
     * @return the effect fired when the status begins, or null if none
     */
    public Effect getOnBeginEffect() {
        return onBeginEffect;
    }

    /**
     * @return the effect fired when the status ends, or null if none
     */
    public Effect getOnEndEffect() {
        return onEndEffect;
    }

    /**
     * @return whether re-applying the status refuses to stack
     */
    public boolean isNonStacking() {
        return nonStacking;
    }

    /**
     * @return the minimum value the status can be reduced to while active
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * @return the object flag this status duplicates (C {@code oflag_dup})
     */
    public ObjectFlag getoFlagDup() {
        return oFlagDup;
    }

    /**
     * @return whether the status is an exact synonym of {@link #getoFlagDup()} (C {@code oflag_syn})
     */
    public boolean isoFlagExactlySyn() {
        return oFlagExactlySyn;
    }

    /**
     * @return the element temporarily resisted while active, or {@code ELEM_NONE}
     */
    public ElementEnum getTempResist() {
        return tempResist;
    }

    /**
     * @return the brand temporarily granted while active, or null
     */
    public Brand getTempBrand() {
        return tempBrand;
    }

    /**
     * @return the slay temporarily granted while active, or null
     */
    public Slay getTempSlay() {
        return tempSlay;
    }
}