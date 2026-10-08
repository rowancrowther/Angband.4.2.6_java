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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
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
 * <p><b>Two sources feed one record.</b> In C the {@code timed_effects[]} array is initialized
 * at compile time from {@code list-player-timed.h} (name, redraw flags, update flags) with every
 * other field left at a default, and the parser for {@code player_timed.txt} then overwrites the
 * defaults. Here the {@link TimedEffect} constant supplies the identity and both flag sets, and
 * the constructor receives everything the data file supplies. The defaults the parser leaves
 * behind for a line the file omits are: no messages, no failures, no grades, no effects, not
 * non-stacking, a lower bound of 0, no duplicated object flag and no exact synonym. C marks "no
 * temporary resist, brand or slay" with an index of {@code -1}; the Java assembler passes
 * {@link ElementEnum#ELEM_NONE} for the resist and {@code null} for the brand and slay, so a
 * consumer tests those rather than comparing with {@code -1}.
 *
 * <p>C's {@code flags} bitfield has the single bit {@code TMD_FLAG_NONSTACKING}; it is held here
 * as the boolean {@link #nonStacking}, and any further bit added to that field in C would need a
 * field of its own.
 *
 * <p>Class PlayerTimedEffect coded before 260815, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public class PlayerTimedEffect {
    /**
     * Which {@code TMD_*} status this record defines.
     *
     * <p>C holds a {@code const char *name} here and looks it up by case-insensitive comparison
     * ({@code timed_name_to_idx()}); the port holds the {@link TimedEffect} constant itself, so
     * the identity is checked by the compiler and doubles as the source of both flag sets.
     *
     * <p>Field name coded before 260815, commented in full on 261008.
     */
    private TimedEffect name;
    /**
     * Redraw ({@code PR_*}) flags to raise when this effect changes (C: {@code flag_redraw}).
     *
     * <p>Not parsed from the data file: taken from {@link TimedEffect#getRedrawFlags} by the
     * constructor, as C takes it from the {@code list-player-timed.h} table. C raises
     * {@code PR_STATUS} in addition to these on every notified change
     * ({@code player_set_timed()}); the table already carries it for every effect except
     * {@code TMD_BLIND} and {@code TMD_IMAGE}.
     *
     * <p>Field flagRedraw coded before 260815, commented in full on 261008.
     */
    private FlagView<PlayerRedraw> flagRedraw;
    /**
     * Update ({@code PU_*}) flags to raise when this effect changes (C: {@code flag_update}).
     *
     * <p>Not parsed from the data file: taken from {@link TimedEffect#getUpdateFlags} by the
     * constructor, as C takes it from the {@code list-player-timed.h} table.
     *
     * <p>Field flagUpdate coded before 260815, commented in full on 261008.
     */
    private FlagView<PlayerUpdateEnum> flagUpdate;

    /**
     * Human-readable description of the status (C: {@code desc}, from the {@code desc:} line).
     *
     * <p>Field description coded before 260815, commented in full on 261008.
     */
    private String description;
    /**
     * Message shown when the status ends (C: {@code on_end}, from the {@code on-end:} line).
     *
     * <p>Printed with the {@code MSG_RECOVER} message type rather than {@link #msgT} when a notified
     * change takes the counter to zero.
     *
     * <p>Field onEnd coded before 260815, commented in full on 261008.
     */
    private String onEnd;
    /**
     * Message shown when the status's counter rises without crossing into a stronger grade
     * (C: {@code on_increase}, from the {@code on-increase:} line).
     *
     * <p>Crossing a grade boundary uses that grade's own {@link TimedGrade#upMsg()} instead.
     *
     * <p>Field onIncrease coded before 260815, commented in full on 261008.
     */
    private String onIncrease;
    /**
     * Message shown when the status's counter falls without crossing into a weaker grade
     * (C: {@code on_decrease}, from the {@code on-decrease:} line).
     *
     * <p>Crossing a grade boundary uses that grade's own {@link TimedGrade#downMsg()} instead.
     *
     * <p>Field onDecrease coded before 260815, commented in full on 261008.
     */
    private String onDecrease;
    /**
     * Message type used for the grade-change, increase and decrease messages (C: {@code msgt},
     * from the {@code msgt:} line). The end message does not use it.
     *
     * <p>Field msgT coded before 260815, commented in full on 261008.
     */
    private MessageType msgT;
    /**
     * Conditions under which the status fails to take hold — a blocking object flag, resist,
     * vulnerability, player flag or other timed effect (C: the {@code fail} list, from the
     * {@code fail:} lines). Checked in order by {@code player_inc_check()}.
     *
     * <p>Field fail coded before 260815, commented in full on 261008.
     */
    private List<TimedFailure> fail;
    /**
     * Ordered severity bands of the status (C: the {@code grade} list, from the {@code grade:}
     * lines — see {@link TimedGrade}). The last band's {@code max} is the highest value the
     * counter can reach.
     *
     * <p>Field grade coded before 260815, commented in full on 261008.
     */
    private List<TimedGrade> grade;
    /**
     * Effect fired when the status begins (C: {@code on_begin_effect}); null when the data file
     * gives none.
     *
     * <p>Field onBeginEffect coded before 260815, commented in full on 261008.
     */
    private Effect onBeginEffect;
    /**
     * Effect fired when the status lapses (C: {@code on_end_effect}); null when the data file
     * gives none.
     *
     * <p>Field onEndEffect coded before 260815, commented in full on 261008.
     */
    private Effect onEndEffect;
    /**
     * Whether an increase is blocked while the status is already active — the C
     * {@code TMD_FLAG_NONSTACKING} bit of {@code flags}, from the {@code flags:} line.
     *
     * <p>Field nonStacking coded before 260815, commented in full on 261008.
     */
    private boolean nonStacking;
    /**
     * Minimum value the counter is clamped up to when it is set (C: {@code lower_bound}, from the
     * {@code lower-bound:} line); 0 when the data file gives none.
     *
     * <p>C's parser rejects a bound below 0 or above 32767, because the counter is stored in 16
     * bits and a negative bound would break the "is this effect active" test.
     *
     * <p>Field lowerBound coded before 260815, commented in full on 261008.
     */
    private int lowerBound;
    /**
     * Object flag this status confers while it is active (C: {@code oflag_dup}, from the
     * {@code flag-synonym:} line); {@code OF_NONE} when there is none.
     *
     * <p>While the counter is non-zero, the player's timed-effect flag set is given this flag as
     * though an item carried it ({@code player_flags_timed()}, which skips {@code TMD_TRAPSAFE}).
     * See also {@link #oFlagExactlySyn}.
     *
     * <p>Field oFlagDup coded before 260815, commented in full on 261008.
     */
    private ObjectFlag oFlagDup;
    /**
     * Whether the status is an <em>exact</em> synonym of {@link #oFlagDup} (C: {@code oflag_syn},
     * the second argument of the {@code flag-synonym:} line, non-zero meaning exact).
     *
     * <p>When true, and the player already knows the flag and has it from something other than a
     * timed effect, a change to the status is not announced — the player has nothing new to learn
     * from it ({@code player_set_timed()}).
     *
     * <p>Field oFlagExactlySyn coded before 260815, commented in full on 261008.
     */
    private boolean oFlagExactlySyn;
    /**
     * Element temporarily resisted while the status is active (C: {@code temp_resist}, from the
     * {@code resist:} line); {@link ElementEnum#ELEM_NONE} where C holds {@code -1}.
     *
     * <p>Field tempResist coded before 260815, commented in full on 261008.
     */
    private ElementEnum tempResist;
    /**
     * Brand temporarily added to the player's attacks while the status is active (C:
     * {@code temp_brand}, from the {@code brand:} line, consulted by
     * {@code player_has_temporary_brand()}); null where C holds {@code -1}.
     *
     * <p>Field tempBrand coded before 260815, commented in full on 261008.
     */
    private Brand tempBrand;
    /**
     * Slay temporarily added to the player's attacks while the status is active (C:
     * {@code temp_slay}, from the {@code slay:} line, consulted by
     * {@code player_has_temporary_slay()}); null where C holds {@code -1}.
     *
     * <p>Field tempSlay coded before 260815, commented in full on 261008.
     */
    private Slay tempSlay;

    /**
     * Builds the full static definition of a timed status from its parsed attributes.
     *
     * <p>Each parameter populates the like-named field; see those fields for the detailed meaning
     * of each. Note the redraw/update flags are derived from the {@link TimedEffect} identity and
     * so are not passed here. The list and effect parameters are stored as given, not copied.
     *
     * <p>Constructor PlayerTimedEffect coded before 260815, commented in full on 261008.
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
     * Returns the {@link TimedEffect} identity this record defines.
     *
     * <p>Function getName coded before 260815, commented in full on 261008.
     *
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
     * backing field was narrowed with it. Commented in full on 261008.
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
     * <p>Function getFlagUpdate return type narrowed to {@link FlagView} on 260818. Commented in
     * full on 261008.
     *
     * @return a read-only view of the {@code PU_*} update flags to raise on a change
     */
    public FlagView<PlayerUpdateEnum> getFlagUpdate() {
        return flagUpdate;
    }

    /**
     * Returns the human-readable description of the status.
     *
     * <p>Function getDescription coded before 260815, commented in full on 261008.
     *
     * @return the human-readable description of the status
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the message shown when the status ends.
     *
     * <p>Function getOnEnd coded before 260815, commented in full on 261008.
     *
     * @return the message shown when the status ends
     */
    public String getOnEnd() {
        return onEnd;
    }

    /**
     * Returns the message shown when the status's level rises within a grade.
     *
     * <p>Function getOnIncrease coded before 260815, commented in full on 261008.
     *
     * @return the message shown when the status's level rises
     */
    public String getOnIncrease() {
        return onIncrease;
    }

    /**
     * Returns the message shown when the status's level falls within a grade.
     *
     * <p>Function getOnDecrease coded before 260815, commented in full on 261008.
     *
     * @return the message shown when the status's level falls
     */
    public String getOnDecrease() {
        return onDecrease;
    }

    /**
     * Returns the message channel used for the change messages.
     *
     * <p>Function getMsgT coded before 260815, commented in full on 261008.
     *
     * @return the message channel used for the change messages
     */
    public MessageType getMsgT() {
        return msgT;
    }

    /**
     * Returns the conditions under which the status fails to apply.
     *
     * <p>Function getFail coded before 260815, commented in full on 261008.
     *
     * @return the conditions under which the status fails to apply
     */
    public List<TimedFailure> getFail() {
        return fail;
    }

    /**
     * Returns the ordered severity grades of the status.
     *
     * <p>Function getGrade coded before 260815, commented in full on 261008.
     *
     * @return the ordered severity grades of the status
     */
    public List<TimedGrade> getGrade() {
        return grade;
    }

    /**
     * Returns the effect fired when the status begins.
     *
     * <p>Function getOnBeginEffect coded before 260815, commented in full on 261008.
     *
     * @return the effect fired when the status begins, or null if none
     */
    public Effect getOnBeginEffect() {
        return onBeginEffect;
    }

    /**
     * Returns the effect fired when the status ends.
     *
     * <p>Function getOnEndEffect coded before 260815, commented in full on 261008.
     *
     * @return the effect fired when the status ends, or null if none
     */
    public Effect getOnEndEffect() {
        return onEndEffect;
    }

    /**
     * Returns whether re-applying the status is blocked while it is already active.
     *
     * <p>Function isNonStacking coded before 260815, commented in full on 261008.
     *
     * @return whether re-applying the status refuses to stack
     */
    public boolean isNonStacking() {
        return nonStacking;
    }

    /**
     * Returns the minimum value the counter is clamped up to when set.
     *
     * <p>Function getLowerBound coded before 260815, commented in full on 261008.
     *
     * @return the minimum value the status can be reduced to while active
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * Returns the object flag this status confers while active.
     *
     * <p>Function getoFlagDup coded before 260815, commented in full on 261008.
     *
     * @return the object flag this status duplicates (C {@code oflag_dup}), or {@code OF_NONE}
     */
    public ObjectFlag getoFlagDup() {
        return oFlagDup;
    }

    /**
     * Returns whether the status is an exact synonym of {@link #getoFlagDup()}.
     *
     * <p>Function isoFlagExactlySyn coded before 260815, commented in full on 261008.
     *
     * @return whether the status is an exact synonym of {@link #getoFlagDup()} (C {@code oflag_syn})
     */
    public boolean isoFlagExactlySyn() {
        return oFlagExactlySyn;
    }

    /**
     * Returns the element temporarily resisted while the status is active.
     *
     * <p>Function getTempResist coded before 260815, commented in full on 261008.
     *
     * @return the element temporarily resisted while active, or {@code ELEM_NONE}
     */
    public ElementEnum getTempResist() {
        return tempResist;
    }

    /**
     * Returns the brand temporarily granted while the status is active.
     *
     * <p>Function getTempBrand coded before 260815, commented in full on 261008.
     *
     * @return the brand temporarily granted while active, or null
     */
    public Brand getTempBrand() {
        return tempBrand;
    }

    /**
     * Returns the slay temporarily granted while the status is active.
     *
     * <p>Function getTempSlay coded before 260815, commented in full on 261008.
     *
     * @return the slay temporarily granted while active, or null
     */
    public Slay getTempSlay() {
        return tempSlay;
    }
}