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

/**
 * The timed status effects applicable to a monster (sleep, stun, confusion, fear,
 * slow, haste, hold, …), each bundling its saving-throw flag, stacking rule,
 * immunity race flag, duration cap and the begin/end/increase messages. This is a
 * close parallel of the C original's {@code mon_timed_effect} table ({@code src/mon-timed.c}); the
 * constants are self-describing and documented collectively here.
 *
 * @author Rowan Crowther
 */
public enum MonTimed {
    MON_TMD_NONE(false, MonTimedStack.MTS_NO, MonsterRaceFlag.RF_NONE, 0, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE),
    MON_TMD_SLEEP(true, MonTimedStack.MTS_NO, MonsterRaceFlag.RF_NO_SLEEP, 10000, MonsterMessage.MON_MSG_FALL_ASLEEP, MonsterMessage.MON_MSG_WAKES_UP, MonsterMessage.MON_MSG_NONE),
    MON_TMD_STUN(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_STUN, 50, MonsterMessage.MON_MSG_DAZED, MonsterMessage.MON_MSG_NOT_DAZED, MonsterMessage.MON_MSG_MORE_DAZED),
    MON_TMD_CONF(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_CONF, 50, MonsterMessage.MON_MSG_CONFUSED, MonsterMessage.MON_MSG_NOT_CONFUSED, MonsterMessage.MON_MSG_MORE_CONFUSED),
    MON_TMD_FEAR(true, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NO_FEAR, 10000, MonsterMessage.MON_MSG_FLEE_IN_TERROR, MonsterMessage.MON_MSG_NOT_AFRAID, MonsterMessage.MON_MSG_MORE_AFRAID),
    MON_TMD_SLOW(false, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NO_SLOW, 50, MonsterMessage.MON_MSG_SLOWED, MonsterMessage.MON_MSG_NOT_SLOWED, MonsterMessage.MON_MSG_MORE_SLOWED),
    MON_TMD_FAST(false, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_HASTED, MonsterMessage.MON_MSG_NOT_HASTED, MonsterMessage.MON_MSG_MORE_HASTED),
    MON_TMD_HOLD(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_HOLD, 50, MonsterMessage.MON_MSG_HELD, MonsterMessage.MON_MSG_NOT_HELD, MonsterMessage.MON_MSG_NONE),
    MON_TMD_DISEN(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_IM_DISEN, 50, MonsterMessage.MON_MSG_DISEN, MonsterMessage.MON_MSG_NOT_DISEN, MonsterMessage.MON_MSG_NONE),
    MON_TMD_COMMAND(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_COMMAND, MonsterMessage.MON_MSG_NOT_COMMAND, MonsterMessage.MON_MSG_NONE),
    MON_TMD_CHANGED(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE),
    MON_TMD_MAX(true, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NONE, 0, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE);

    /**
     * Whether the monster gets a saving throw against this effect.
     *
     * @author Rowan Crowther
     */
    private boolean saveThrow;
    /**
     * How re-applying this effect combines with an existing instance.
     *
     * @author Rowan Crowther
     */
    private MonTimedStack stackStyle;
    /**
     * Race flag that makes a monster immune to this effect.
     *
     * @author Rowan Crowther
     */
    private MonsterRaceFlag resistFlag;
    /**
     * Duration cap/scaling value for this effect.
     *
     * @author Rowan Crowther
     */
    private int time;
    /**
     * Message shown when the effect begins.
     *
     * @author Rowan Crowther
     */
    private MonsterMessage messageBegin;
    /**
     * Message shown when the effect ends.
     *
     * @author Rowan Crowther
     */
    private MonsterMessage messageEnd;
    /**
     * Message shown when the effect's duration increases.
     *
     * @author Rowan Crowther
     */
    private MonsterMessage messageIncrease;

    /**
     * Build a monster timed-effect descriptor.
     *
     * @param save     whether a save is allowed
     * @param stc      stacking behaviour
     * @param flag     immunity race flag
     * @param time     duration cap
     * @param begin    begin message
     * @param end      end message
     * @param increase increase message
     * @author Rowan Crowther
     */
    MonTimed(boolean save, MonTimedStack stc, MonsterRaceFlag flag,
             int time, MonsterMessage begin, MonsterMessage end,
             MonsterMessage increase) {
        this.saveThrow = save;
        this.stackStyle = stc;
        this.resistFlag = flag;
        this.time = time;
        this.messageBegin = begin;
        this.messageEnd = end;
        this.messageIncrease = increase;
    }
}