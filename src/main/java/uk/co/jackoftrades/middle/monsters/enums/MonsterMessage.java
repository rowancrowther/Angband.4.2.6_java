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

package uk.co.jackoftrades.middle.monsters.enums;

import uk.co.jackoftrades.middle.enums.MessageType;

/**
 * The set of templated messages describing what happens to a monster (dies,
 * resists, wakes up, flees, is held, …), each with its {@link MessageType}
 * category, whether the monster's name is omitted, and a template using
 * {@code [is|are]}/{@code [s]} pluralisation markers. Mirrors the C original's
 * {@code MON_MSG_*} list ({@code src/list-mon-message.h}); the constants are
 * self-describing and documented collectively here.
 *
 * @author Rowan Crowther
 */
public enum MonsterMessage {
    MON_MSG_NONE(MessageType.MSG_GENERIC, false, "[is|are] hurt."),
    MON_MSG_DIE(MessageType.MSG_KILL, false, "die[s]."),
    MON_MSG_DESTROYED(MessageType.MSG_KILL, false, "[is|are] destroyed."),
    MON_MSG_RESIST_A_LOT(MessageType.MSG_GENERIC, false, "resist[s] a lot."),
    MON_MSG_HIT_HARD(MessageType.MSG_GENERIC, false, "[is|are] hit hard."),
    MON_MSG_RESIST(MessageType.MSG_GENERIC, false, "resist[s]."),
    MON_MSG_IMMUNE(MessageType.MSG_GENERIC, false, "[is|are] immune."),
    MON_MSG_RESIST_SOMEWHAT(MessageType.MSG_GENERIC, false, "resist[s] somewhat."),
    MON_MSG_UNAFFECTED(MessageType.MSG_GENERIC, false, "[is|are] unaffected!"),
    MON_MSG_SPAWN(MessageType.MSG_GENERIC, false, "spawn[s]!"),
    MON_MSG_HEALTHIER(MessageType.MSG_GENERIC, false, "look[s] healthier."),
    MON_MSG_FALL_ASLEEP(MessageType.MSG_GENERIC, false, "fall[s] asleep!"),
    MON_MSG_WAKES_UP(MessageType.MSG_GENERIC, false, "wake[s] up."),
    MON_MSG_CRINGE_LIGHT(MessageType.MSG_GENERIC, false, "cringe[s] from the light!"),
    MON_MSG_SHRIVEL_LIGHT(MessageType.MSG_KILL, false, "shrivel[s] away in the light!"),
    MON_MSG_LOSE_SKIN(MessageType.MSG_GENERIC, false, "lose[s] some skin!"),
    MON_MSG_DISSOLVE(MessageType.MSG_KILL, false, "dissolve[s]!"),
    MON_MSG_CATCH_FIRE(MessageType.MSG_GENERIC, false, "catch[es] fire!"),
    MON_MSG_BADLY_FROZEN(MessageType.MSG_GENERIC, false, "[is|are] badly frozen."),
    MON_MSG_SHUDDER(MessageType.MSG_GENERIC, false, "shudder[s]."),
    MON_MSG_CHANGE(MessageType.MSG_GENERIC, false, "change[s]!"),
    MON_MSG_DISAPPEAR(MessageType.MSG_GENERIC, false, "disappear[s]!"),
    MON_MSG_MORE_DAZED(MessageType.MSG_GENERIC, false, "[is|are] even more stunned."),
    MON_MSG_DAZED(MessageType.MSG_GENERIC, false, "[is|are] stunned."),
    MON_MSG_NOT_DAZED(MessageType.MSG_GENERIC, false, "[is|are] no longer stunned."),
    MON_MSG_MORE_CONFUSED(MessageType.MSG_GENERIC, false, "look[s] more confused."),
    MON_MSG_CONFUSED(MessageType.MSG_GENERIC, false, "look[s] confused."),
    MON_MSG_NOT_CONFUSED(MessageType.MSG_GENERIC, false, "[is|are] no longer confused."),
    MON_MSG_MORE_SLOWED(MessageType.MSG_GENERIC, false, "look[s] more slowed."),
    MON_MSG_SLOWED(MessageType.MSG_GENERIC, false, "look[s] slowed."),
    MON_MSG_NOT_SLOWED(MessageType.MSG_GENERIC, false, "speed[s] up."),
    MON_MSG_MORE_HASTED(MessageType.MSG_GENERIC, false, "look[s] even faster!"),
    MON_MSG_HASTED(MessageType.MSG_GENERIC, false, "start[s] moving faster."),
    MON_MSG_NOT_HASTED(MessageType.MSG_GENERIC, false, "slow[s] down."),
    MON_MSG_MORE_AFRAID(MessageType.MSG_GENERIC, false, "look[s] more terrified!"),
    MON_MSG_FLEE_IN_TERROR(MessageType.MSG_FLEE, false, "flee[s] in terror!"),
    MON_MSG_NOT_AFRAID(MessageType.MSG_GENERIC, false, "[is|are] no longer afraid."),
    MON_MSG_HELD(MessageType.MSG_GENERIC, false, "[is|are] frozen to the spot!"),
    MON_MSG_NOT_HELD(MessageType.MSG_GENERIC, false, "can move again."),
    MON_MSG_DISEN(MessageType.MSG_GENERIC, false, "seem[s] less magical!"),
    MON_MSG_NOT_DISEN(MessageType.MSG_GENERIC, false, "seem[s] magical again."),
    MON_MSG_COMMAND(MessageType.MSG_GENERIC, false, "falls under your spell!"),
    MON_MSG_NOT_COMMAND(MessageType.MSG_GENERIC, false, "is no longer under your control."),
    MON_MSG_SHAPE_FAIL(MessageType.MSG_GENERIC, false, "shimmers for a moment."),
    MON_MSG_MORIA_DEATH(MessageType.MSG_KILL, true, "You hear [a|several] scream[|s] of agony!"),
    MON_MSG_DISINTEGRATES(MessageType.MSG_KILL, false, "disintegrate[s]!"),
    MON_MSG_FREEZE_SHATTER(MessageType.MSG_KILL, false, "freeze[s] and shatter[s]!"),
    MON_MSG_MANA_DRAIN(MessageType.MSG_GENERIC, false, "lose[s] some mana!"),
    MON_MSG_BRIEF_PUZZLE(MessageType.MSG_GENERIC, false, "look[s] briefly puzzled."),
    MON_MSG_MAINTAIN_SHAPE(MessageType.MSG_GENERIC, false, "maintain[s] the same shape."),
    MON_MSG_UNHARMED(MessageType.MSG_GENERIC, false, "[is|are] unharmed."),
    MON_MSG_APPEAR(MessageType.MSG_GENERIC, false, "appear[s]!"),
    MON_MSG_HIT_AND_RUN(MessageType.MSG_GENERIC, true, "There is a puff of smoke!"),
    MON_MSG_QUAKE_DEATH(MessageType.MSG_KILL, false, "[is|are] embedded in rock!"),
    MON_MSG_QUAKE_HURT(MessageType.MSG_GENERIC, false, "wail[s] out in pain!"),
    /* Dummy messages for monster pain - we use edit file info instead. */
    MON_MSG_95(MessageType.MSG_GENERIC, false, ""),
    ON_MSG_75(MessageType.MSG_GENERIC, false, ""),
    MON_MSG_50(MessageType.MSG_GENERIC, false, ""),
    MON_MSG_35(MessageType.MSG_GENERIC, false, ""),
    MON_MSG_20(MessageType.MSG_GENERIC, false, ""),
    MON_MSG_10(MessageType.MSG_GENERIC, false, ""),
    MON_MSG_0(MessageType.MSG_GENERIC, false, ""),
    MON_MSG_MAX(MessageType.MSG_GENERIC, false, "");

    /**
     * The message/sound category for this message.
     *
     * @author Rowan Crowther
     */
    private MessageType type;
    /**
     * Whether the monster's name is omitted when displaying the message.
     *
     * @author Rowan Crowther
     */
    private boolean omitMonsterName;
    /**
     * The message template, with pluralisation markers.
     *
     * @author Rowan Crowther
     */
    private String message;

    /**
     * Bind a monster message to its category, name-omission flag and template.
     *
     * @param type            the message category
     * @param omitMonsterName whether to omit the monster's name
     * @param message         the message template
     * @author Rowan Crowther
     */
    MonsterMessage(MessageType type, boolean omitMonsterName, String message) {
        this.type = type;
        this.omitMonsterName = omitMonsterName;
        this.message = message;
    }
}