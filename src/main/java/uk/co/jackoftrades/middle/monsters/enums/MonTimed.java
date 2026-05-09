package uk.co.jackoftrades.middle.monsters.enums;

public enum MonTimed {
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

    private boolean saveThrow;
    private MonTimedStack stackStyle;
    private MonsterRaceFlag resistFlag;
    private int time;
    private MonsterMessage messageBegin;
    private MonsterMessage messageEnd;
    private MonsterMessage messageIncrease;

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