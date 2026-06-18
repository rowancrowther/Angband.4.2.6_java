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

package uk.co.jackoftrades.middle.enums;

import uk.co.jackoftrades.middle.monsters.enums.MonsterMessage;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

public enum EffectMonTimed {
    MON_TMD_NONE(false, StackType.STACK_NONE, MonsterRaceFlag.RF_NONE, 0, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE),
    MON_TMD_SLEEP(true, StackType.STACK_NO, MonsterRaceFlag.RF_NO_SLEEP, 10000, MonsterMessage.MON_MSG_FALL_ASLEEP, MonsterMessage.MON_MSG_WAKES_UP, MonsterMessage.MON_MSG_NONE),
    MON_TMD_STUN(false, StackType.STACK_MAX, MonsterRaceFlag.RF_NO_STUN, 50, MonsterMessage.MON_MSG_DAZED, MonsterMessage.MON_MSG_NOT_DAZED, MonsterMessage.MON_MSG_MORE_DAZED),
    MON_TMD_CONF(false, StackType.STACK_MAX, MonsterRaceFlag.RF_NO_CONF, 50, MonsterMessage.MON_MSG_CONFUSED, MonsterMessage.MON_MSG_NOT_CONFUSED, MonsterMessage.MON_MSG_MORE_CONFUSED),
    MON_TMD_FEAR(true, StackType.STACK_INCR, MonsterRaceFlag.RF_NO_FEAR, 10000, MonsterMessage.MON_MSG_FLEE_IN_TERROR, MonsterMessage.MON_MSG_NOT_AFRAID, MonsterMessage.MON_MSG_MORE_AFRAID),
    MON_TMD_SLOW(false, StackType.STACK_INCR, MonsterRaceFlag.RF_NO_SLOW, 50, MonsterMessage.MON_MSG_SLOWED, MonsterMessage.MON_MSG_NOT_SLOWED, MonsterMessage.MON_MSG_MORE_SLOWED),
    MON_TMD_FAST(false, StackType.STACK_INCR, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_HASTED, MonsterMessage.MON_MSG_NOT_HASTED, MonsterMessage.MON_MSG_MORE_HASTED),
    MON_TMD_HOLD(false, StackType.STACK_MAX, MonsterRaceFlag.RF_NO_HOLD, 50, MonsterMessage.MON_MSG_HELD, MonsterMessage.MON_MSG_HELD, MonsterMessage.MON_MSG_NONE),
    MON_TMD_DISEN(false, StackType.STACK_MAX, MonsterRaceFlag.RF_IM_DISEN, 50, MonsterMessage.MON_MSG_DISEN, MonsterMessage.MON_MSG_NOT_DISEN, MonsterMessage.MON_MSG_NONE),
    MON_TMD_COMMAND(false, StackType.STACK_MAX, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_COMMAND, MonsterMessage.MON_MSG_NOT_COMMAND, MonsterMessage.MON_MSG_NONE),
    MON_TMD_CHANGED(false, StackType.STACK_MAX, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE),
    MON_TMD_MAX(true, StackType.STACK_INCR, MonsterRaceFlag.RF_NONE, 0, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE);

    private final boolean savingThrow;
    private final StackType stackType;
    private final MonsterRaceFlag resist;
    private final int time;
    private MonsterMessage messageBegin;
    private MonsterMessage messageEnd;
    private MonsterMessage messageIncrease;

    private EffectMonTimed(boolean savingThrow, StackType stackType, MonsterRaceFlag resist, int time, MonsterMessage messageBegin, MonsterMessage messageEnd, MonsterMessage messageIncrease) {
        this.savingThrow = savingThrow;
        this.stackType = stackType;
        this.resist = resist;
        this.time = time;
        this.messageBegin = messageBegin;
        this.messageEnd = messageEnd;
        this.messageIncrease = messageIncrease;
    }

    ;
}