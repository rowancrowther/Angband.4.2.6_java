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

/**
 * The timed status effects a monster can carry (sleep, stun, confusion, fear, slow, haste, hold,
 * disenchantment, command and shapechange), each bundled with the rules that govern it: the
 * Java form of the static {@code effects[]} array of {@code struct mon_timed_effect} in
 * {@code mon-timed.c}, which C builds from the rows of {@code list-mon-timed.h}.
 *
 * <p>Each constant is one row, and the constructor arguments are that row's columns in the
 * header's order: whether the monster gets a saving throw, how a second application stacks, the
 * race flag that makes the monster immune, the longest the timer may run, and the begin, end and
 * increase messages. All eleven rows of the header (the ten effects and the {@code MAX} row)
 * were compared with their constants this pass and agree.
 *
 * <p>Where this differs from C:
 * <ul>
 *   <li>{@link #MON_TMD_NONE} leads the enum and has no C row, so an ordinal here is one more than
 *   C's index for the same effect. {@code Monster} keys its timers by constant, in a
 *   {@code Map<MonTimed, Integer>}, rather than by position in an array as C's {@code m_timed}
 *   does, so nothing depends on the ordinal.</li>
 *   <li>{@link #MON_TMD_MAX} is C's last row, kept with C's values. C never treats it as an
 *   effect: it asserts {@code effect_type < MON_TMD_MAX} and {@code mon_timed_name_to_idx} loops
 *   below it. It is only the count of real effects.</li>
 *   <li>C's {@code name} column ({@code "SLEEP"}, {@code "STUN"}, …) is not stored. It is the
 *   constant's own {@code name()} without the {@code MON_TMD_} prefix, which is why
 *   {@code EffectAssembler} adds that prefix before calling {@code valueOf}.</li>
 *   <li>C writes {@code 0} for "no race flag" and "no message", and {@code MON_MSG_NONE} and
 *   {@code RF_NONE} are the zero entries of their own enums. {@link MonsterMessage#MON_MSG_NONE}
 *   still carries the text "is hurt.", so a reader of a message column must test for the constant
 *   as C's {@code mon_set_timed} tests {@code m_note} for zero, not print it.</li>
 *   <li>The stacking column uses {@link MonTimedStack}, whose {@code MTS_MAX} stands for C's
 *   {@code STACK_MAX}, a real stacking rule, not a count.</li>
 * </ul>
 *
 * <p>The columns are private and have no getter, so nothing outside this enum can read them yet.
 * The readers are all in {@code mon-timed.c}: {@code mon_set_timed} (the timer cap and the
 * message), {@code mon_inc_timed} (stacking), {@code does_resist} (the race flag and the save)
 * and {@code monster_effect_level} (the cap divided by five).
 *
 * <p>Enum MonTimed coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public enum MonTimed {
    /**
     * "No effect": a Java-only leading constant with no row in {@code list-mon-timed.h}. Its
     * columns are the neutral values (no save, no stacking, no race flag, no cap, no messages), and
     * C has nothing to compare them with.
     *
     * <p>Constant MON_TMD_NONE coded before 261014, commented in full on 261014.
     */
    MON_TMD_NONE(false, MonTimedStack.MTS_NO, MonsterRaceFlag.RF_NONE, 0, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE),
    /**
     * C's {@code SLEEP} row: a saving throw, no stacking, immunity from {@code RF_NO_SLEEP}, a cap
     * of 10000 turns, "falls asleep" on starting and "wakes up" on ending. The increase column is
     * {@code MON_MSG_NONE}, which is never reached because a non-stacking effect never increases.
     *
     * <p>Constant MON_TMD_SLEEP coded before 261014, commented in full on 261014.
     */
    MON_TMD_SLEEP(true, MonTimedStack.MTS_NO, MonsterRaceFlag.RF_NO_SLEEP, 10000, MonsterMessage.MON_MSG_FALL_ASLEEP, MonsterMessage.MON_MSG_WAKES_UP, MonsterMessage.MON_MSG_NONE),
    /**
     * C's {@code STUN} row: no saving throw, stacks to the larger timer, immunity from
     * {@code RF_NO_STUN}, a cap of 50 turns, with begin, end and "more stunned" messages.
     *
     * <p>Constant MON_TMD_STUN coded before 261014, commented in full on 261014.
     */
    MON_TMD_STUN(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_STUN, 50, MonsterMessage.MON_MSG_DAZED, MonsterMessage.MON_MSG_NOT_DAZED, MonsterMessage.MON_MSG_MORE_DAZED),
    /**
     * C's {@code CONF} row: no saving throw, stacks to the larger timer, immunity from
     * {@code RF_NO_CONF}, a cap of 50 turns, with begin, end and "more confused" messages.
     *
     * <p>Constant MON_TMD_CONF coded before 261014, commented in full on 261014.
     */
    MON_TMD_CONF(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_CONF, 50, MonsterMessage.MON_MSG_CONFUSED, MonsterMessage.MON_MSG_NOT_CONFUSED, MonsterMessage.MON_MSG_MORE_CONFUSED),
    /**
     * C's {@code FEAR} row: a saving throw, timers add, immunity from {@code RF_NO_FEAR}, a cap of
     * 10000 turns (the same as sleep, unlike the 50 of the other effects), with "flees in terror",
     * "no longer afraid" and "more terrified" messages.
     *
     * <p>Constant MON_TMD_FEAR coded before 261014, commented in full on 261014.
     */
    MON_TMD_FEAR(true, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NO_FEAR, 10000, MonsterMessage.MON_MSG_FLEE_IN_TERROR, MonsterMessage.MON_MSG_NOT_AFRAID, MonsterMessage.MON_MSG_MORE_AFRAID),
    /**
     * C's {@code SLOW} row: no saving throw, timers add, immunity from {@code RF_NO_SLOW}, a cap of
     * 50 turns, with begin, end and "more slowed" messages.
     *
     * <p>Constant MON_TMD_SLOW coded before 261014, commented in full on 261014.
     */
    MON_TMD_SLOW(false, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NO_SLOW, 50, MonsterMessage.MON_MSG_SLOWED, MonsterMessage.MON_MSG_NOT_SLOWED, MonsterMessage.MON_MSG_MORE_SLOWED),
    /**
     * C's {@code FAST} row: no saving throw, timers add, a cap of 50 turns, with begin, end and
     * "even faster" messages. The race flag is {@code RF_NONE} (C's {@code 0}), so no monster is
     * immune to being hasted.
     *
     * <p>Constant MON_TMD_FAST coded before 261014, commented in full on 261014.
     */
    MON_TMD_FAST(false, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_HASTED, MonsterMessage.MON_MSG_NOT_HASTED, MonsterMessage.MON_MSG_MORE_HASTED),
    /**
     * C's {@code HOLD} row: no saving throw, stacks to the larger timer, immunity from
     * {@code RF_NO_HOLD}, a cap of 50 turns, with begin and end messages and no increase message.
     *
     * <p>Constant MON_TMD_HOLD coded before 261014, commented in full on 261014.
     */
    MON_TMD_HOLD(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_HOLD, 50, MonsterMessage.MON_MSG_HELD, MonsterMessage.MON_MSG_NOT_HELD, MonsterMessage.MON_MSG_NONE),
    /**
     * C's {@code DISEN} row (disenchantment): no saving throw, stacks to the larger timer,
     * immunity from {@code RF_IM_DISEN} (a resistance flag, not a {@code NO_} one), a cap of 50
     * turns, with begin and end messages and no increase message.
     *
     * <p>Constant MON_TMD_DISEN coded before 261014, commented in full on 261014.
     */
    MON_TMD_DISEN(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_IM_DISEN, 50, MonsterMessage.MON_MSG_DISEN, MonsterMessage.MON_MSG_NOT_DISEN, MonsterMessage.MON_MSG_NONE),
    /**
     * C's {@code COMMAND} row (a monster under the player's control, the Necromancer power): no
     * saving throw, stacks to the larger timer, no race flag, a cap of 50 turns, with begin and
     * end messages and no increase message. {@code mon_set_timed} also clears the monster's
     * target when this one reaches zero.
     *
     * <p>Constant MON_TMD_COMMAND coded before 261014, commented in full on 261014.
     */
    MON_TMD_COMMAND(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_COMMAND, MonsterMessage.MON_MSG_NOT_COMMAND, MonsterMessage.MON_MSG_NONE),
    /**
     * C's {@code CHANGED} row (a monster in another shape): no saving throw, stacks to the larger
     * timer, no race flag, a cap of 50 turns, and no messages at all. {@code mon_set_timed} gives
     * it a special case, changing the shape when the timer rises and reverting it when the timer
     * reaches zero.
     *
     * <p>Constant MON_TMD_CHANGED coded before 261014, commented in full on 261014.
     */
    MON_TMD_CHANGED(false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NONE, 50, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE),
    /**
     * C's {@code MAX} row, the count of real effects. C gives it a save and an additive stack and
     * zeroes the rest, and never uses any of it, so the values are copied for parity and mean
     * nothing. Do not apply it as an effect.
     *
     * <p>Constant MON_TMD_MAX coded before 261014, commented in full on 261014.
     */
    MON_TMD_MAX(true, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NONE, 0, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE);

    /**
     * Whether the monster gets a saving throw against this effect: C's {@code gets_save}. Only
     * sleep and fear have one. {@code does_resist} reaches it only after the race flag has failed
     * to make the monster immune, and the {@code MON_TMD_FLG_NOFAIL} flag skips both.
     *
     * <p>Field saveThrow coded before 261014, commented in full on 261014.
     */
    private boolean saveThrow;
    /**
     * How re-applying this effect combines with a timer already running: C's {@code stacking}.
     * {@code mon_inc_timed} keeps the old timer for {@code MTS_NO}, the larger of old and new for
     * {@code MTS_MAX}, and their sum for {@code MTS_INCR}.
     *
     * <p>Field stackStyle coded before 261014, commented in full on 261014.
     */
    private MonTimedStack stackStyle;
    /**
     * The race flag that makes a monster immune to this effect: C's {@code flag_resist}.
     * {@code RF_NONE} (C's {@code 0}) means no monster is immune.
     *
     * <p>Field resistFlag coded before 261014, commented in full on 261014.
     */
    private MonsterRaceFlag resistFlag;
    /**
     * The most turns the timer can reach: C's {@code max_timer}, 50 for most effects and 10000 for
     * sleep and fear. {@code mon_set_timed} clamps every new timer to it, and
     * {@code monster_effect_level} divides it by five (at least 1) to rate how strongly the
     * effect holds. C keeps timers in an {@code int16_t}, which is why the header says the cap
     * must stay below 32767.
     *
     * <p>Field time coded before 261014, commented in full on 261014.
     */
    private int time;
    /**
     * The message shown when the effect starts: C's {@code message_begin}.
     * {@code MON_MSG_NONE} (C's {@code 0}) means none.
     *
     * <p>Field messageBegin coded before 261014, commented in full on 261014.
     */
    private MonsterMessage messageBegin;
    /**
     * The message shown when the effect ends: C's {@code message_end}.
     * {@code MON_MSG_NONE} (C's {@code 0}) means none.
     *
     * <p>Field messageEnd coded before 261014, commented in full on 261014.
     */
    private MonsterMessage messageEnd;
    /**
     * The message shown when a running timer rises: C's {@code message_increase}.
     * {@code MON_MSG_NONE} (C's {@code 0}) means none; sleep, hold, disenchantment and command
     * have none.
     *
     * <p>Field messageIncrease coded before 261014, commented in full on 261014.
     */
    private MonsterMessage messageIncrease;

    /**
     * Builds one row of the table, taking the columns in the order of {@code list-mon-timed.h}
     * after its name column.
     *
     * <p>Constructor MonTimed coded before 261014, commented in full on 261014.
     *
     * @param save     whether the monster gets a saving throw (C's {@code gets_save})
     * @param stc      how a second application stacks (C's {@code stacking})
     * @param flag     the race flag that grants immunity (C's {@code flag_resist})
     * @param time     the most turns the timer can reach (C's {@code max_timer})
     * @param begin    the message when the effect starts (C's {@code message_begin})
     * @param end      the message when the effect ends (C's {@code message_end})
     * @param increase the message when the timer rises (C's {@code message_increase})
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