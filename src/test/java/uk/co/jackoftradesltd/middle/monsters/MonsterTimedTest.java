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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonTimed;
import uk.co.jackoftradesltd.middle.monsters.enums.MonTimedFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterFlag;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the timed-effect entry points in {@link Monster}: {@code getMonTimed},
 * {@code clearTimed} ({@code mon_clear_timed}) and {@code decrementTimed} ({@code mon_dec_timed}),
 * both in {@code mon-timed.c}.
 *
 * <p>{@code setTimed} ({@code mon_set_timed}) is still a stub, so the two entry points are tested
 * through a recording subclass that captures the {@code (effect, timer)} pair each one hands to it.
 * The expected values come from the C: {@code mon_clear_timed} returns false without calling
 * {@code mon_set_timed} when {@code m_timed[effect] == 0}, and otherwise passes 0;
 * {@code mon_dec_timed} passes {@code m_timed[effect] - timer} floored at 0.
 *
 * <p>Class MonsterTimedTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
@DisplayName("Monster timed effects")
class MonsterTimedTest {

    private static final Flag<MonTimedFlags> NO_FLAGS = new Flag<>(MonTimedFlags.class);

    private static Recording monsterWith(MonTimed effect, int turns) {
        Map<MonTimed, Integer> timed = new EnumMap<>(MonTimed.class);
        if (effect != null) timed.put(effect, turns);
        return new Recording(timed);
    }

    @Test
    @DisplayName("getMonTimed reads the stored turns, and 0 for an effect never set")
    void getMonTimed() {
        Recording mon = monsterWith(MonTimed.MON_TMD_FEAR, 7);
        assertEquals(7, mon.getMonTimed(MonTimed.MON_TMD_FEAR));
        assertEquals(0, mon.getMonTimed(MonTimed.MON_TMD_STUN));
    }

    @Test
    @DisplayName("clearTimed on an inactive effect returns false and never calls setTimed")
    void clearInactive() {
        Recording mon = monsterWith(null, 0);
        mon.result = true;
        assertFalse(mon.clearTimed(MonTimed.MON_TMD_FEAR, NO_FLAGS));
        assertTrue(mon.calls.isEmpty());

        Recording zeroed = monsterWith(MonTimed.MON_TMD_FEAR, 0);
        zeroed.result = true;
        assertFalse(zeroed.clearTimed(MonTimed.MON_TMD_FEAR, NO_FLAGS));
        assertTrue(zeroed.calls.isEmpty());
    }

    @Test
    @DisplayName("clearTimed on an active effect passes 0 to setTimed and returns its answer")
    void clearActive() {
        Recording mon = monsterWith(MonTimed.MON_TMD_FEAR, 5);
        mon.result = true;
        assertTrue(mon.clearTimed(MonTimed.MON_TMD_FEAR, NO_FLAGS));
        assertEquals(1, mon.calls.size());
        assertEquals(MonTimed.MON_TMD_FEAR, mon.calls.get(0)[0]);
        assertEquals(0, mon.calls.get(0)[1]);

        mon.result = false;
        assertFalse(mon.clearTimed(MonTimed.MON_TMD_FEAR, NO_FLAGS));
    }

    @Test
    @DisplayName("decrementTimed passes current minus timer")
    void decrementOrdinary() {
        Recording mon = monsterWith(MonTimed.MON_TMD_STUN, 10);
        mon.decrementTimed(MonTimed.MON_TMD_STUN, 3, NO_FLAGS);
        assertEquals(7, mon.calls.get(0)[1]);
    }

    @Test
    @DisplayName("decrementTimed floors at zero: exactly to zero, and past it")
    void decrementFloor() {
        Recording mon = monsterWith(MonTimed.MON_TMD_STUN, 4);
        mon.decrementTimed(MonTimed.MON_TMD_STUN, 4, NO_FLAGS);
        mon.decrementTimed(MonTimed.MON_TMD_STUN, 9, NO_FLAGS);
        assertEquals(0, mon.calls.get(0)[1]);
        assertEquals(0, mon.calls.get(1)[1]);
    }

    @Test
    @DisplayName("decrementTimed on an effect never set passes 0, and always calls setTimed")
    void decrementInactive() {
        Recording mon = monsterWith(null, 0);
        mon.result = true;
        assertTrue(mon.decrementTimed(MonTimed.MON_TMD_SLOW, 1, NO_FLAGS));
        assertEquals(1, mon.calls.size());
        assertEquals(0, mon.calls.get(0)[1]);
    }

    /** A monster that records what its entry points pass to the stubbed {@code setTimed}. */
    private static final class Recording extends Monster {
        final List<Object[]> calls = new ArrayList<>();
        boolean result;

        Recording(Map<MonTimed, Integer> timed) {
            super(null, null, null, 0, 0, timed, 0, 0, 0, new Flag<>(MonsterFlag.class),
                    null, null, null, null, null, null, null, 0, 0);
        }

        @Override
        public boolean setTimed(MonTimed timed, int timer, Flag<MonTimedFlags> flag) {
            calls.add(new Object[]{timed, timer});
            return result;
        }
    }
}
