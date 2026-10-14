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

import uk.co.jackoftradesltd.middle.enums.MessageType;

/**
 * The set of templated messages describing what happens to a monster (dies, resists, wakes up,
 * flees, is held, …), each with its {@link MessageType} category, whether the monster's name is
 * omitted, and a template using {@code [singular|plural]} markers. This is the Java form of the
 * C {@code enum mon_messages} and of the static {@code msg_repository[]} array behind it in
 * {@code mon-msg.c}; both are built from the rows of {@code list-mon-message.h}.
 *
 * <p>Each constant is one row, and the constructor arguments are that row's columns in the
 * header's order, minus the name: the {@code MSG_} category, the {@code omit_subject} flag and the
 * text. All 63 rows of the header were compared with their constants this pass, field by field,
 * and agree in name, category, flag, text and order.
 *
 * <p>Where this differs from C:
 * <ul>
 *   <li>C needs two parallel structures, the enum for the codes and {@code msg_repository[]}
 *   indexed by them. Here a constant carries its own row, so {@link #ordinal()} is C's message
 *   code and nothing has to be kept in step.</li>
 *   <li>{@link #MON_MSG_MAX} is C's last row, kept with C's values. C never treats it as a message:
 *   it asserts {@code msg_code < MON_MSG_MAX} before every lookup. It only marks the end of the
 *   real codes.</li>
 *   <li>The seven {@code MON_MSG_95} to {@code MON_MSG_0} rows have an empty text in C as well as
 *   here. {@code get_message_text()} swaps in the monster base's pain message at show time, which
 *   is not ported with this enum.</li>
 *   <li>{@link #getType()} returns the category written in the row. C's
 *   {@code get_message_type()} then turns {@code MSG_KILL} into {@code MSG_KILL_UNIQUE}, or
 *   {@code MSG_KILL_KING} for Morgoth, when the dying monster is unique; that step belongs to the
 *   caller, not to this enum.</li>
 * </ul>
 *
 * <p>Template syntax, from the comment above {@code msg_repository[]}: {@code [s]} is text kept
 * only for one monster, {@code [is|are]} chooses between singular and plural, and either side may
 * be empty, as in {@code scream[|s]}. The brackets are never printed. The parser is
 * {@code get_message_text()} in {@code mon-msg.c} and it is not part of this enum.
 *
 * <p>Enum MonsterMessage coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public enum MonsterMessage {
    /**
     * Template {@code "[is|are] hurt."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>C's code 0, and also the "no message" value: {@code mon-timed.c} stores it in a message
     * column to mean there is nothing to print, so a reader must test for the constant, not print
     * its text. Its text is still a real message, used for plain damage.
     *
     * <p>Constant MON_MSG_NONE coded before 261014, commented in full on 261014.
     */
    MON_MSG_NONE(MessageType.MSG_GENERIC, false, "[is|are] hurt."),
    /**
     * Template {@code "die[s]."}, category {@link MessageType#MSG_KILL}.
     *
     * <p>Queued with the longest delay (2) by {@code add_monster_message()} in {@code mon-msg.c},
     * so it shows after the other messages from the same turn.
     *
     * <p>Constant MON_MSG_DIE coded before 261014, commented in full on 261014.
     */
    MON_MSG_DIE(MessageType.MSG_KILL, false, "die[s]."),
    /**
     * Template {@code "[is|are] destroyed."}, category {@link MessageType#MSG_KILL}.
     *
     * <p>Queued with the longest delay (2) by {@code add_monster_message()} in {@code mon-msg.c},
     * like {@link #MON_MSG_DIE}.
     *
     * <p>Constant MON_MSG_DESTROYED coded before 261014, commented in full on 261014.
     */
    MON_MSG_DESTROYED(MessageType.MSG_KILL, false, "[is|are] destroyed."),
    /**
     * Template {@code "resist[s] a lot."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_RESIST_A_LOT coded before 261014, commented in full on 261014.
     */
    MON_MSG_RESIST_A_LOT(MessageType.MSG_GENERIC, false, "resist[s] a lot."),
    /**
     * Template {@code "[is|are] hit hard."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_HIT_HARD coded before 261014, commented in full on 261014.
     */
    MON_MSG_HIT_HARD(MessageType.MSG_GENERIC, false, "[is|are] hit hard."),
    /**
     * Template {@code "resist[s]."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_RESIST coded before 261014, commented in full on 261014.
     */
    MON_MSG_RESIST(MessageType.MSG_GENERIC, false, "resist[s]."),
    /**
     * Template {@code "[is|are] immune."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_IMMUNE coded before 261014, commented in full on 261014.
     */
    MON_MSG_IMMUNE(MessageType.MSG_GENERIC, false, "[is|are] immune."),
    /**
     * Template {@code "resist[s] somewhat."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_RESIST_SOMEWHAT coded before 261014, commented in full on 261014.
     */
    MON_MSG_RESIST_SOMEWHAT(MessageType.MSG_GENERIC, false, "resist[s] somewhat."),
    /**
     * Template {@code "[is|are] unaffected!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_UNAFFECTED coded before 261014, commented in full on 261014.
     */
    MON_MSG_UNAFFECTED(MessageType.MSG_GENERIC, false, "[is|are] unaffected!"),
    /**
     * Template {@code "spawn[s]!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_SPAWN coded before 261014, commented in full on 261014.
     */
    MON_MSG_SPAWN(MessageType.MSG_GENERIC, false, "spawn[s]!"),
    /**
     * Template {@code "look[s] healthier."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_HEALTHIER coded before 261014, commented in full on 261014.
     */
    MON_MSG_HEALTHIER(MessageType.MSG_GENERIC, false, "look[s] healthier."),
    /**
     * Template {@code "fall[s] asleep!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_FALL_ASLEEP coded before 261014, commented in full on 261014.
     */
    MON_MSG_FALL_ASLEEP(MessageType.MSG_GENERIC, false, "fall[s] asleep!"),
    /**
     * Template {@code "wake[s] up."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_WAKES_UP coded before 261014, commented in full on 261014.
     */
    MON_MSG_WAKES_UP(MessageType.MSG_GENERIC, false, "wake[s] up."),
    /**
     * Template {@code "cringe[s] from the light!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_CRINGE_LIGHT coded before 261014, commented in full on 261014.
     */
    MON_MSG_CRINGE_LIGHT(MessageType.MSG_GENERIC, false, "cringe[s] from the light!"),
    /**
     * Template {@code "shrivel[s] away in the light!"}, category {@link MessageType#MSG_KILL}.
     *
     * <p>Constant MON_MSG_SHRIVEL_LIGHT coded before 261014, commented in full on 261014.
     */
    MON_MSG_SHRIVEL_LIGHT(MessageType.MSG_KILL, false, "shrivel[s] away in the light!"),
    /**
     * Template {@code "lose[s] some skin!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_LOSE_SKIN coded before 261014, commented in full on 261014.
     */
    MON_MSG_LOSE_SKIN(MessageType.MSG_GENERIC, false, "lose[s] some skin!"),
    /**
     * Template {@code "dissolve[s]!"}, category {@link MessageType#MSG_KILL}.
     *
     * <p>Constant MON_MSG_DISSOLVE coded before 261014, commented in full on 261014.
     */
    MON_MSG_DISSOLVE(MessageType.MSG_KILL, false, "dissolve[s]!"),
    /**
     * Template {@code "catch[es] fire!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_CATCH_FIRE coded before 261014, commented in full on 261014.
     */
    MON_MSG_CATCH_FIRE(MessageType.MSG_GENERIC, false, "catch[es] fire!"),
    /**
     * Template {@code "[is|are] badly frozen."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_BADLY_FROZEN coded before 261014, commented in full on 261014.
     */
    MON_MSG_BADLY_FROZEN(MessageType.MSG_GENERIC, false, "[is|are] badly frozen."),
    /**
     * Template {@code "shudder[s]."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_SHUDDER coded before 261014, commented in full on 261014.
     */
    MON_MSG_SHUDDER(MessageType.MSG_GENERIC, false, "shudder[s]."),
    /**
     * Template {@code "change[s]!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_CHANGE coded before 261014, commented in full on 261014.
     */
    MON_MSG_CHANGE(MessageType.MSG_GENERIC, false, "change[s]!"),
    /**
     * Template {@code "disappear[s]!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_DISAPPEAR coded before 261014, commented in full on 261014.
     */
    MON_MSG_DISAPPEAR(MessageType.MSG_GENERIC, false, "disappear[s]!"),
    /**
     * Template {@code "[is|are] even more stunned."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_MORE_DAZED coded before 261014, commented in full on 261014.
     */
    MON_MSG_MORE_DAZED(MessageType.MSG_GENERIC, false, "[is|are] even more stunned."),
    /**
     * Template {@code "[is|are] stunned."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_DAZED coded before 261014, commented in full on 261014.
     */
    MON_MSG_DAZED(MessageType.MSG_GENERIC, false, "[is|are] stunned."),
    /**
     * Template {@code "[is|are] no longer stunned."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_NOT_DAZED coded before 261014, commented in full on 261014.
     */
    MON_MSG_NOT_DAZED(MessageType.MSG_GENERIC, false, "[is|are] no longer stunned."),
    /**
     * Template {@code "look[s] more confused."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_MORE_CONFUSED coded before 261014, commented in full on 261014.
     */
    MON_MSG_MORE_CONFUSED(MessageType.MSG_GENERIC, false, "look[s] more confused."),
    /**
     * Template {@code "look[s] confused."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_CONFUSED coded before 261014, commented in full on 261014.
     */
    MON_MSG_CONFUSED(MessageType.MSG_GENERIC, false, "look[s] confused."),
    /**
     * Template {@code "[is|are] no longer confused."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_NOT_CONFUSED coded before 261014, commented in full on 261014.
     */
    MON_MSG_NOT_CONFUSED(MessageType.MSG_GENERIC, false, "[is|are] no longer confused."),
    /**
     * Template {@code "look[s] more slowed."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_MORE_SLOWED coded before 261014, commented in full on 261014.
     */
    MON_MSG_MORE_SLOWED(MessageType.MSG_GENERIC, false, "look[s] more slowed."),
    /**
     * Template {@code "look[s] slowed."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_SLOWED coded before 261014, commented in full on 261014.
     */
    MON_MSG_SLOWED(MessageType.MSG_GENERIC, false, "look[s] slowed."),
    /**
     * Template {@code "speed[s] up."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_NOT_SLOWED coded before 261014, commented in full on 261014.
     */
    MON_MSG_NOT_SLOWED(MessageType.MSG_GENERIC, false, "speed[s] up."),
    /**
     * Template {@code "look[s] even faster!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_MORE_HASTED coded before 261014, commented in full on 261014.
     */
    MON_MSG_MORE_HASTED(MessageType.MSG_GENERIC, false, "look[s] even faster!"),
    /**
     * Template {@code "start[s] moving faster."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_HASTED coded before 261014, commented in full on 261014.
     */
    MON_MSG_HASTED(MessageType.MSG_GENERIC, false, "start[s] moving faster."),
    /**
     * Template {@code "slow[s] down."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_NOT_HASTED coded before 261014, commented in full on 261014.
     */
    MON_MSG_NOT_HASTED(MessageType.MSG_GENERIC, false, "slow[s] down."),
    /**
     * Template {@code "look[s] more terrified!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_MORE_AFRAID coded before 261014, commented in full on 261014.
     */
    MON_MSG_MORE_AFRAID(MessageType.MSG_GENERIC, false, "look[s] more terrified!"),
    /**
     * Template {@code "flee[s] in terror!"}, category {@link MessageType#MSG_FLEE}.
     *
     * <p>Constant MON_MSG_FLEE_IN_TERROR coded before 261014, commented in full on 261014.
     */
    MON_MSG_FLEE_IN_TERROR(MessageType.MSG_FLEE, false, "flee[s] in terror!"),
    /**
     * Template {@code "[is|are] no longer afraid."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_NOT_AFRAID coded before 261014, commented in full on 261014.
     */
    MON_MSG_NOT_AFRAID(MessageType.MSG_GENERIC, false, "[is|are] no longer afraid."),
    /**
     * Template {@code "[is|are] frozen to the spot!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_HELD coded before 261014, commented in full on 261014.
     */
    MON_MSG_HELD(MessageType.MSG_GENERIC, false, "[is|are] frozen to the spot!"),
    /**
     * Template {@code "can move again."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_NOT_HELD coded before 261014, commented in full on 261014.
     */
    MON_MSG_NOT_HELD(MessageType.MSG_GENERIC, false, "can move again."),
    /**
     * Template {@code "seem[s] less magical!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_DISEN coded before 261014, commented in full on 261014.
     */
    MON_MSG_DISEN(MessageType.MSG_GENERIC, false, "seem[s] less magical!"),
    /**
     * Template {@code "seem[s] magical again."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_NOT_DISEN coded before 261014, commented in full on 261014.
     */
    MON_MSG_NOT_DISEN(MessageType.MSG_GENERIC, false, "seem[s] magical again."),
    /**
     * Template {@code "falls under your spell!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Unlike most rows this text has no plural markers, so it reads the same for one monster or
     * several.
     *
     * <p>Constant MON_MSG_COMMAND coded before 261014, commented in full on 261014.
     */
    MON_MSG_COMMAND(MessageType.MSG_GENERIC, false, "falls under your spell!"),
    /**
     * Template {@code "is no longer under your control."}, category
     * {@link MessageType#MSG_GENERIC}.
     *
     * <p>Like {@link #MON_MSG_COMMAND} this text has no plural markers.
     *
     * <p>Constant MON_MSG_NOT_COMMAND coded before 261014, commented in full on 261014.
     */
    MON_MSG_NOT_COMMAND(MessageType.MSG_GENERIC, false, "is no longer under your control."),
    /**
     * Template {@code "shimmers for a moment."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Like {@link #MON_MSG_COMMAND} this text has no plural markers.
     *
     * <p>Constant MON_MSG_SHAPE_FAIL coded before 261014, commented in full on 261014.
     */
    MON_MSG_SHAPE_FAIL(MessageType.MSG_GENERIC, false, "shimmers for a moment."),
    /**
     * Template {@code "You hear [a|several] scream[|s] of agony!"}, category
     * {@link MessageType#MSG_KILL}, monster name omitted.
     *
     * <p>One of two rows that omit the monster's name, and the only template with an empty
     * alternative: {@code scream[|s]} yields "scream" for one monster and "screams" for several.
     *
     * <p>Constant MON_MSG_MORIA_DEATH coded before 261014, commented in full on 261014.
     */
    MON_MSG_MORIA_DEATH(MessageType.MSG_KILL, true, "You hear [a|several] scream[|s] of agony!"),
    /**
     * Template {@code "disintegrate[s]!"}, category {@link MessageType#MSG_KILL}.
     *
     * <p>Constant MON_MSG_DISINTEGRATES coded before 261014, commented in full on 261014.
     */
    MON_MSG_DISINTEGRATES(MessageType.MSG_KILL, false, "disintegrate[s]!"),
    /**
     * Template {@code "freeze[s] and shatter[s]!"}, category {@link MessageType#MSG_KILL}.
     *
     * <p>Constant MON_MSG_FREEZE_SHATTER coded before 261014, commented in full on 261014.
     */
    MON_MSG_FREEZE_SHATTER(MessageType.MSG_KILL, false, "freeze[s] and shatter[s]!"),
    /**
     * Template {@code "lose[s] some mana!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_MANA_DRAIN coded before 261014, commented in full on 261014.
     */
    MON_MSG_MANA_DRAIN(MessageType.MSG_GENERIC, false, "lose[s] some mana!"),
    /**
     * Template {@code "look[s] briefly puzzled."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_BRIEF_PUZZLE coded before 261014, commented in full on 261014.
     */
    MON_MSG_BRIEF_PUZZLE(MessageType.MSG_GENERIC, false, "look[s] briefly puzzled."),
    /**
     * Template {@code "maintain[s] the same shape."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_MAINTAIN_SHAPE coded before 261014, commented in full on 261014.
     */
    MON_MSG_MAINTAIN_SHAPE(MessageType.MSG_GENERIC, false, "maintain[s] the same shape."),
    /**
     * Template {@code "[is|are] unharmed."}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>The result of {@code get_pain_msg_code()} in {@code mon-msg.c} when the damage is not
     * above zero.
     *
     * <p>Constant MON_MSG_UNHARMED coded before 261014, commented in full on 261014.
     */
    MON_MSG_UNHARMED(MessageType.MSG_GENERIC, false, "[is|are] unharmed."),
    /**
     * Template {@code "appear[s]!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_APPEAR coded before 261014, commented in full on 261014.
     */
    MON_MSG_APPEAR(MessageType.MSG_GENERIC, false, "appear[s]!"),
    /**
     * Template {@code "There is a puff of smoke!"}, category {@link MessageType#MSG_GENERIC},
     * monster name omitted.
     *
     * <p>The other row that omits the monster's name, as the whole sentence is the message.
     *
     * <p>Constant MON_MSG_HIT_AND_RUN coded before 261014, commented in full on 261014.
     */
    MON_MSG_HIT_AND_RUN(MessageType.MSG_GENERIC, true, "There is a puff of smoke!"),
    /**
     * Template {@code "[is|are] embedded in rock!"}, category {@link MessageType#MSG_KILL}.
     *
     * <p>Constant MON_MSG_QUAKE_DEATH coded before 261014, commented in full on 261014.
     */
    MON_MSG_QUAKE_DEATH(MessageType.MSG_KILL, false, "[is|are] embedded in rock!"),
    /**
     * Template {@code "wail[s] out in pain!"}, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>Constant MON_MSG_QUAKE_HURT coded before 261014, commented in full on 261014.
     */
    MON_MSG_QUAKE_HURT(MessageType.MSG_GENERIC, false, "wail[s] out in pain!"),
    /* Dummy messages for monster pain - we use edit file info instead. */
    /**
     * Pain placeholder with an empty template, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>{@code get_message_text()} in {@code mon-msg.c} replaces the template with the first of
     * the monster base's seven pain messages. {@code get_pain_msg_code()} selects it when the
     * monster is left with more than 95% of the hit points it had before the hit (an integer
     * percentage).
     *
     * <p>Constant MON_MSG_95 coded before 261014, commented in full on 261014.
     */
    MON_MSG_95(MessageType.MSG_GENERIC, false, ""),
    /**
     * Pain placeholder for the second pain message, selected when the monster is left with more
     * than 75%, and at most 95%, of its pre-hit hit points.
     *
     * <p>Constant MON_MSG_75 coded before 261014, commented in full on 261014.
     */
    MON_MSG_75(MessageType.MSG_GENERIC, false, ""),
    /**
     * Pain placeholder for the third pain message, selected when the monster is left with more
     * than 50%, and at most 75%, of its pre-hit hit points.
     *
     * <p>Constant MON_MSG_50 coded before 261014, commented in full on 261014.
     */
    MON_MSG_50(MessageType.MSG_GENERIC, false, ""),
    /**
     * Pain placeholder for the fourth pain message, selected when the monster is left with more
     * than 35%, and at most 50%, of its pre-hit hit points.
     *
     * <p>Constant MON_MSG_35 coded before 261014, commented in full on 261014.
     */
    MON_MSG_35(MessageType.MSG_GENERIC, false, ""),
    /**
     * Pain placeholder for the fifth pain message, selected when the monster is left with more
     * than 20%, and at most 35%, of its pre-hit hit points.
     *
     * <p>Constant MON_MSG_20 coded before 261014, commented in full on 261014.
     */
    MON_MSG_20(MessageType.MSG_GENERIC, false, ""),
    /**
     * Pain placeholder for the sixth pain message, selected when the monster is left with more
     * than 10%, and at most 20%, of its pre-hit hit points.
     *
     * <p>Constant MON_MSG_10 coded before 261014, commented in full on 261014.
     */
    MON_MSG_10(MessageType.MSG_GENERIC, false, ""),
    /**
     * Pain placeholder for the seventh pain message, selected when the monster is left with 10% or
     * less of its pre-hit hit points.
     *
     * <p>Constant MON_MSG_0 coded before 261014, commented in full on 261014.
     */
    MON_MSG_0(MessageType.MSG_GENERIC, false, ""),
    /**
     * Sentinel with an empty template, category {@link MessageType#MSG_GENERIC}.
     *
     * <p>C's final row, kept with C's values. It is not a message: {@code mon-msg.c} asserts
     * {@code msg_code < MON_MSG_MAX} before every lookup, so it only marks the end of the real
     * codes.
     *
     * <p>Constant MON_MSG_MAX coded before 261014, commented in full on 261014.
     */
    MON_MSG_MAX(MessageType.MSG_GENERIC, false, "");

    /**
     * The message/sound category for this message: the {@code type} member of C's
     * {@code msg_repository[]} row, given by the row's {@code MSG_} column.
     *
     * <p>Field type coded before 261014, commented in full on 261014.
     */
    private MessageType type;
    /**
     * Whether the monster's name is omitted when displaying the message: the {@code omit_subject}
     * member of C's {@code msg_repository[]} row. Only {@link #MON_MSG_MORIA_DEATH} and
     * {@link #MON_MSG_HIT_AND_RUN} set it.
     *
     * <p>Field omitMonsterName coded before 261014, commented in full on 261014.
     */
    private boolean omitMonsterName;
    /**
     * The message template with its {@code [singular|plural]} markers still in place: the
     * {@code msg} member of C's {@code msg_repository[]} row.
     *
     * <p>Field message coded before 261014, commented in full on 261014.
     */
    private String message;

    /**
     * Bind a monster message to its category, name-omission flag and template. The three arguments
     * are the columns of one row of {@code list-mon-message.h}, in that order, without the name.
     *
     * <p>Constructor MonsterMessage coded before 261014, commented in full on 261014.
     *
     * @param type            the message category
     * @param omitMonsterName whether to omit the monster's name
     * @param message         the message template
     */
    MonsterMessage(MessageType type, boolean omitMonsterName, String message) {
        this.type = type;
        this.omitMonsterName = omitMonsterName;
        this.message = message;
    }

    /**
     * The raw template for this message, with its {@code [singular|plural]} markers unparsed. It
     * is the {@code msg} member that C's {@code get_message_text()} reads from
     * {@code msg_repository[]} before choosing the singular or plural side. For the seven pain
     * placeholders, {@link #MON_MSG_95} to {@link #MON_MSG_0}, it is empty, because C takes the
     * text from the monster base instead.
     *
     * <p>Function getMessage coded before 261014, commented in full on 261014.
     *
     * @return the unparsed message template
     */
    public String getMessage() {
        return message;
    }

    /**
     * Whether the monster's name is left off the front of this message: C's {@code skip_subject()}
     * in {@code mon-msg.c}, which returns the {@code omit_subject} member. When it is {@code true}
     * the template is the whole sentence.
     *
     * <p>Function isOmitMonsterName coded before 261014, commented in full on 261014.
     *
     * @return {@code true} if no monster name should precede the message
     */
    public boolean isOmitMonsterName() {
        return omitMonsterName;
    }

    /**
     * The message category written in this constant's row. It is not C's
     * {@code get_message_type()}: that function starts from the same {@code type} member but
     * turns {@link MessageType#MSG_KILL} into {@code MSG_KILL_UNIQUE}, or {@code MSG_KILL_KING}
     * for Morgoth, when the dying monster is unique. That step needs the monster race and is left
     * to the caller.
     *
     * <p>Function getType coded before 261014, commented in full on 261014.
     *
     * @return the category from the row, before any unique-monster adjustment
     */
    public MessageType getType() {
        return type;
    }
}