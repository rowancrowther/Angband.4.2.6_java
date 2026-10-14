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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link MonTimed} against the rows of {@code list-mon-timed.h}, which C's
 * {@code effects[]} table in {@code mon-timed.c} is built from.
 *
 * <p>The expected values are written out from the C header, one {@link Row} per
 * {@code MON_TMD(...)} line, in its column order (name, save, stack, resist flag, time, begin,
 * end, increase), with C's {@code 0} for "no flag" and "no message" spelt {@code RF_NONE} and
 * {@code MON_MSG_NONE}. The columns are private and have no getter, so they are read by
 * reflection. The Java-only {@code MON_TMD_NONE} lead is checked on its own.
 *
 * <p>Class MonTimedTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class MonTimedTest {

    /**
     * One line of {@code list-mon-timed.h}.
     */
    private record Row(String name, boolean save, MonTimedStack stack, MonsterRaceFlag resist,
                       int time, MonsterMessage begin, MonsterMessage end,
                       MonsterMessage increase) {
    }

    /**
     * The ten real effects of {@code list-mon-timed.h}, in the header's order.
     */
    private static final List<Row> C_ROWS = List.of(
            new Row("SLEEP", true, MonTimedStack.MTS_NO, MonsterRaceFlag.RF_NO_SLEEP, 10000,
                    MonsterMessage.MON_MSG_FALL_ASLEEP, MonsterMessage.MON_MSG_WAKES_UP,
                    MonsterMessage.MON_MSG_NONE),
            new Row("STUN", false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_STUN, 50,
                    MonsterMessage.MON_MSG_DAZED, MonsterMessage.MON_MSG_NOT_DAZED,
                    MonsterMessage.MON_MSG_MORE_DAZED),
            new Row("CONF", false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_CONF, 50,
                    MonsterMessage.MON_MSG_CONFUSED, MonsterMessage.MON_MSG_NOT_CONFUSED,
                    MonsterMessage.MON_MSG_MORE_CONFUSED),
            new Row("FEAR", true, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NO_FEAR, 10000,
                    MonsterMessage.MON_MSG_FLEE_IN_TERROR, MonsterMessage.MON_MSG_NOT_AFRAID,
                    MonsterMessage.MON_MSG_MORE_AFRAID),
            new Row("SLOW", false, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NO_SLOW, 50,
                    MonsterMessage.MON_MSG_SLOWED, MonsterMessage.MON_MSG_NOT_SLOWED,
                    MonsterMessage.MON_MSG_MORE_SLOWED),
            new Row("FAST", false, MonTimedStack.MTS_INCR, MonsterRaceFlag.RF_NONE, 50,
                    MonsterMessage.MON_MSG_HASTED, MonsterMessage.MON_MSG_NOT_HASTED,
                    MonsterMessage.MON_MSG_MORE_HASTED),
            new Row("HOLD", false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NO_HOLD, 50,
                    MonsterMessage.MON_MSG_HELD, MonsterMessage.MON_MSG_NOT_HELD,
                    MonsterMessage.MON_MSG_NONE),
            new Row("DISEN", false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_IM_DISEN, 50,
                    MonsterMessage.MON_MSG_DISEN, MonsterMessage.MON_MSG_NOT_DISEN,
                    MonsterMessage.MON_MSG_NONE),
            new Row("COMMAND", false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NONE, 50,
                    MonsterMessage.MON_MSG_COMMAND, MonsterMessage.MON_MSG_NOT_COMMAND,
                    MonsterMessage.MON_MSG_NONE),
            new Row("CHANGED", false, MonTimedStack.MTS_MAX, MonsterRaceFlag.RF_NONE, 50,
                    MonsterMessage.MON_MSG_NONE, MonsterMessage.MON_MSG_NONE,
                    MonsterMessage.MON_MSG_NONE));

    /**
     * Reads one private column of a constant.
     *
     * @param effect the constant
     * @param column the field name
     * @return the field's value
     * @throws Exception if the field cannot be reached
     */
    private static Object column(MonTimed effect, String column) throws Exception {
        Field field = MonTimed.class.getDeclaredField(column);
        field.setAccessible(true);
        return field.get(effect);
    }

    /**
     * C has ten effects before {@code MAX}; Java adds {@code MON_TMD_NONE} in front and keeps the
     * {@code MAX} row at the end, in the header's order.
     *
     * <p>Function constantsFollowTheHeaderOrder coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("NONE, then the ten effects in the header's order, then MAX")
    void constantsFollowTheHeaderOrder() {
        MonTimed[] all = MonTimed.values();
        assertEquals(C_ROWS.size() + 2, all.length);
        assertSame(MonTimed.MON_TMD_NONE, all[0]);
        assertSame(MonTimed.MON_TMD_MAX, all[all.length - 1]);
        for (int i = 0; i < C_ROWS.size(); i++) {
            assertEquals("MON_TMD_" + C_ROWS.get(i).name(), all[i + 1].name(), "position " + i);
        }
    }

    /**
     * C's {@code MON_TMD_MAX} is the count of real effects, 10. Java's ordinal for the same
     * constant is one higher because of the leading {@code MON_TMD_NONE}.
     *
     * <p>Function maxCountsTheRealEffects coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("MAX counts ten real effects, as C's MON_TMD_MAX does")
    void maxCountsTheRealEffects() {
        assertEquals(10, MonTimed.MON_TMD_MAX.ordinal() - MonTimed.MON_TMD_NONE.ordinal() - 1);
    }

    /**
     * Every column of every real effect against its header line.
     *
     * <p>Function everyColumnMatchesTheHeader coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("Every column of every effect equals the header's value")
    void everyColumnMatchesTheHeader() throws Exception {
        for (Row row : C_ROWS) {
            MonTimed effect = MonTimed.valueOf("MON_TMD_" + row.name());
            String who = row.name();
            assertEquals(row.save(), column(effect, "saveThrow"), who + " save");
            assertSame(row.stack(), column(effect, "stackStyle"), who + " stack");
            assertSame(row.resist(), column(effect, "resistFlag"), who + " resist flag");
            assertEquals(row.time(), column(effect, "time"), who + " time");
            assertSame(row.begin(), column(effect, "messageBegin"), who + " begin");
            assertSame(row.end(), column(effect, "messageEnd"), who + " end");
            assertSame(row.increase(), column(effect, "messageIncrease"), who + " increase");
        }
    }

    /**
     * Only sleep and fear get a saving throw, and only they run to 10000 turns.
     *
     * <p>Function onlySleepAndFearSaveAndRunLong coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("Only sleep and fear have a save and the 10000 cap")
    void onlySleepAndFearSaveAndRunLong() throws Exception {
        EnumSet<MonTimed> saves = EnumSet.noneOf(MonTimed.class);
        EnumSet<MonTimed> long10000 = EnumSet.noneOf(MonTimed.class);
        for (MonTimed effect : MonTimed.values()) {
            if (effect == MonTimed.MON_TMD_NONE || effect == MonTimed.MON_TMD_MAX) {
                continue;
            }
            if ((boolean) column(effect, "saveThrow")) {
                saves.add(effect);
            }
            if ((int) column(effect, "time") == 10000) {
                long10000.add(effect);
            }
        }
        assertEquals(EnumSet.of(MonTimed.MON_TMD_SLEEP, MonTimed.MON_TMD_FEAR), saves);
        assertEquals(saves, long10000);
    }

    /**
     * The three stacking rules partition the ten effects: one does not stack (sleep), three add
     * (fear, slow, fast) and six take the larger (stun, confusion, hold, disenchantment, command,
     * shapechange).
     *
     * <p>Function stackingRulesPartitionTheEffects coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("Stacking: sleep NO; fear, slow, fast INCR; the other six MAX")
    void stackingRulesPartitionTheEffects() throws Exception {
        EnumSet<MonTimed> no = EnumSet.noneOf(MonTimed.class);
        EnumSet<MonTimed> incr = EnumSet.noneOf(MonTimed.class);
        EnumSet<MonTimed> max = EnumSet.noneOf(MonTimed.class);
        for (Row row : C_ROWS) {
            MonTimed effect = MonTimed.valueOf("MON_TMD_" + row.name());
            switch ((MonTimedStack) column(effect, "stackStyle")) {
                case MTS_NO -> no.add(effect);
                case MTS_INCR -> incr.add(effect);
                case MTS_MAX -> max.add(effect);
                default -> fail(row.name() + " has a stacking rule C does not");
            }
        }
        assertEquals(EnumSet.of(MonTimed.MON_TMD_SLEEP), no);
        assertEquals(EnumSet.of(MonTimed.MON_TMD_FEAR, MonTimed.MON_TMD_SLOW,
                MonTimed.MON_TMD_FAST), incr);
        assertEquals(EnumSet.of(MonTimed.MON_TMD_STUN, MonTimed.MON_TMD_CONF,
                MonTimed.MON_TMD_HOLD, MonTimed.MON_TMD_DISEN, MonTimed.MON_TMD_COMMAND,
                MonTimed.MON_TMD_CHANGED), max);
    }

    /**
     * The immunity flags that are not {@code NO_} flags or {@code 0}: disenchantment uses the
     * resistance flag {@code RF_IM_DISEN}. Fast, command and changed have no immunity.
     *
     * <p>Function immunityFlagsIncludeTheOddOnes coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("Disenchantment resists via IM_DISEN; fast, command, changed have no flag")
    void immunityFlagsIncludeTheOddOnes() throws Exception {
        assertSame(MonsterRaceFlag.RF_IM_DISEN, column(MonTimed.MON_TMD_DISEN, "resistFlag"));
        for (MonTimed effect : List.of(MonTimed.MON_TMD_FAST, MonTimed.MON_TMD_COMMAND,
                MonTimed.MON_TMD_CHANGED)) {
            assertSame(MonsterRaceFlag.RF_NONE, column(effect, "resistFlag"), effect.name());
        }
    }

    /**
     * Shapechange is the one effect with no message at all; sleep, hold, disenchantment and
     * command have no increase message, and every other effect has all three.
     *
     * <p>Function messageColumnsHaveTheCGaps coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("Missing messages: all three for CHANGED, increase for SLEEP/HOLD/DISEN/COMMAND")
    void messageColumnsHaveTheCGaps() throws Exception {
        EnumSet<MonTimed> noIncrease = EnumSet.noneOf(MonTimed.class);
        for (Row row : C_ROWS) {
            MonTimed effect = MonTimed.valueOf("MON_TMD_" + row.name());
            if (column(effect, "messageIncrease") == MonsterMessage.MON_MSG_NONE) {
                noIncrease.add(effect);
            }
            boolean noBegin = column(effect, "messageBegin") == MonsterMessage.MON_MSG_NONE;
            boolean noEnd = column(effect, "messageEnd") == MonsterMessage.MON_MSG_NONE;
            assertEquals(effect == MonTimed.MON_TMD_CHANGED, noBegin, row.name() + " begin");
            assertEquals(effect == MonTimed.MON_TMD_CHANGED, noEnd, row.name() + " end");
        }
        assertEquals(EnumSet.of(MonTimed.MON_TMD_SLEEP, MonTimed.MON_TMD_HOLD,
                MonTimed.MON_TMD_DISEN, MonTimed.MON_TMD_COMMAND, MonTimed.MON_TMD_CHANGED),
                noIncrease);
    }

    /**
     * Each cap fits the {@code int16_t} that C keeps a timer in, and is positive, as the header
     * demands ("must be below 32767").
     *
     * <p>Function capsFitTheTimer coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("Every cap is positive and below 32767")
    void capsFitTheTimer() throws Exception {
        for (Row row : C_ROWS) {
            int cap = (int) column(MonTimed.valueOf("MON_TMD_" + row.name()), "time");
            assertTrue(cap > 0 && cap < 32767, row.name());
        }
    }

    /**
     * The divisor C's {@code monster_effect_level} derives from the cap, {@code MAX(cap / 5, 1)},
     * walked at the boundaries between levels: for a cap of 50 the divisor is 10, so timers 1 and
     * 10 are level 1, 11 is level 2 and 50 is level 5; for 10000 it is 2000, so 2000 is level 1,
     * 2001 level 2 and 10000 level 5. Computed here with C's formula on the stored caps.
     *
     * <p>Function effectLevelDivisorsFollowTheCaps coded on 261014, commented in full on
     * 261014.
     */
    @Test
    @DisplayName("The caps give C's monster_effect_level divisors of 10 and 2000")
    void effectLevelDivisorsFollowTheCaps() throws Exception {
        int[][] stun = {{0, 0}, {1, 1}, {10, 1}, {11, 2}, {20, 2}, {21, 3}, {50, 5}};
        int[][] sleep = {{0, 0}, {1, 1}, {2000, 1}, {2001, 2}, {10000, 5}};
        assertLevels(MonTimed.MON_TMD_STUN, stun);
        assertLevels(MonTimed.MON_TMD_SLEEP, sleep);
    }

    /**
     * Applies C's {@code monster_effect_level} to {timer, level} pairs.
     *
     * @param effect the effect whose cap is used
     * @param cases  {timer, expected level} pairs
     * @throws Exception if the cap cannot be read
     */
    private static void assertLevels(MonTimed effect, int[][] cases) throws Exception {
        int cap = (int) column(effect, "time");
        int divisor = Math.max(cap / 5, 1);
        for (int[] c : cases) {
            int level = Math.min((c[0] + divisor - 1) / divisor, 5);
            assertEquals(c[1], level, effect.name() + " timer " + c[0]);
        }
    }

    /**
     * {@code EffectAssembler} resolves the data file's name by prefixing {@code MON_TMD_}, so
     * every C name from the header must round-trip through {@code valueOf}.
     *
     * <p>Function headerNamesResolveThroughThePrefix coded on 261014, commented in full on
     * 261014.
     */
    @Test
    @DisplayName("Every C name resolves via the MON_TMD_ prefix, as EffectAssembler does")
    void headerNamesResolveThroughThePrefix() {
        for (Row row : C_ROWS) {
            MonTimed effect = MonTimed.valueOf("MON_TMD_" + row.name());
            assertEquals(row.name(), effect.name().substring("MON_TMD_".length()));
        }
        assertEquals(10, Arrays.stream(MonTimed.values())
                .filter(e -> e != MonTimed.MON_TMD_NONE && e != MonTimed.MON_TMD_MAX).count());
    }

    /**
     * The Java-only {@code MON_TMD_NONE} lead takes neutral values and the {@code MAX} row
     * carries C's own values for it (save, add, no flag, no cap, no messages).
     *
     * <p>Function sentinelRows coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("NONE is neutral; MAX carries C's MAX row (true, INCR, none, 0, none)")
    void sentinelRows() throws Exception {
        for (String col : List.of("messageBegin", "messageEnd", "messageIncrease")) {
            assertSame(MonsterMessage.MON_MSG_NONE, column(MonTimed.MON_TMD_NONE, col), col);
            assertSame(MonsterMessage.MON_MSG_NONE, column(MonTimed.MON_TMD_MAX, col), col);
        }
        assertEquals(false, column(MonTimed.MON_TMD_NONE, "saveThrow"));
        assertSame(MonTimedStack.MTS_NO, column(MonTimed.MON_TMD_NONE, "stackStyle"));
        assertSame(MonsterRaceFlag.RF_NONE, column(MonTimed.MON_TMD_NONE, "resistFlag"));
        assertEquals(0, column(MonTimed.MON_TMD_NONE, "time"));

        assertEquals(true, column(MonTimed.MON_TMD_MAX, "saveThrow"));
        assertSame(MonTimedStack.MTS_INCR, column(MonTimed.MON_TMD_MAX, "stackStyle"));
        assertSame(MonsterRaceFlag.RF_NONE, column(MonTimed.MON_TMD_MAX, "resistFlag"));
        assertEquals(0, column(MonTimed.MON_TMD_MAX, "time"));
    }
}
