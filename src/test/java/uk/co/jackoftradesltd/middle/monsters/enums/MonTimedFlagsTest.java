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
import uk.co.jackoftradesltd.channel.utils.Flag;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link MonTimedFlags} against the {@code MON_TMD_FLG_*} defines in {@code mon-timed.h}.
 *
 * <p>Expected values come from the C header: three flags, {@code NOTIFY} ({@code 0x01}),
 * {@code NOMESSAGE} ({@code 0x04}) and {@code NOFAIL} ({@code 0x08}), declared in that order
 * with {@code 0x02} unused. Java carries no numeric value, so the C values are checked through
 * what they imply: there are exactly three constants, they are distinct bits in C's
 * order, and a {@link Flag} over them keeps each one independent, as the C {@code |} and
 * {@code &} on the three bits do. The combinations are the ones the C callers build:
 * {@code NOMESSAGE | NOFAIL} ({@code mon-util.c}) and {@code context->flag | NOTIFY}
 * ({@code project-mon.c}).
 *
 * <p>Class MonTimedFlagsTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class MonTimedFlagsTest {

    /**
     * C defines exactly three flags; a fourth would need a value in the {@code 0x02} gap or
     * above {@code 0x08}.
     *
     * <p>Function exactlyThreeFlags coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("There are exactly the three C flags, in C's declaration order")
    void exactlyThreeFlags() {
        assertEquals(3, MonTimedFlags.values().length);
        assertEquals(MonTimedFlags.MON_TMD_FLG_NOTIFY, MonTimedFlags.values()[0]);
        assertEquals(MonTimedFlags.MON_TMD_FLG_NOMESSAGE, MonTimedFlags.values()[1]);
        assertEquals(MonTimedFlags.MON_TMD_FLG_NOFAIL, MonTimedFlags.values()[2]);
    }

    /**
     * A fresh set is C's {@code flag = 0}, the value of the {@code flag} argument the
     * game-world code passes when it wants no notification, no suppression and no override.
     *
     * <p>Function emptySetHasNoFlag coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("An empty set has none of the flags, as C's flag = 0")
    void emptySetHasNoFlag() {
        Flag<MonTimedFlags> flag = new Flag<>(MonTimedFlags.class);
        assertTrue(flag.isEmpty());
        for (MonTimedFlags f : MonTimedFlags.values()) {
            assertFalse(flag.has(f), f.name());
        }
    }

    /**
     * Each flag on its own is set and tested without disturbing the other two, as
     * {@code flag & MON_TMD_FLG_NOFAIL} tests only the 0x08 bit.
     *
     * <p>Function eachFlagIsIndependent coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("Each flag is a separate bit: setting one leaves the other two clear")
    void eachFlagIsIndependent() {
        for (MonTimedFlags only : MonTimedFlags.values()) {
            Flag<MonTimedFlags> flag = new Flag<>(MonTimedFlags.class);
            flag.on(only);
            assertEquals(1, flag.count(), only.name());
            for (MonTimedFlags other : MonTimedFlags.values()) {
                assertEquals(other == only, flag.has(other), only + " then " + other);
            }
        }
    }

    /**
     * {@code MON_TMD_FLG_NOMESSAGE | MON_TMD_FLG_NOFAIL}, which {@code mon-util.c} passes when it
     * must clear a timer silently and without a resistance check.
     *
     * <p>Function noMessageWithNoFail coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("NOMESSAGE | NOFAIL keeps both bits and leaves NOTIFY clear")
    void noMessageWithNoFail() {
        Flag<MonTimedFlags> flag = new Flag<>(MonTimedFlags.class);
        flag.on(MonTimedFlags.MON_TMD_FLG_NOMESSAGE);
        flag.on(MonTimedFlags.MON_TMD_FLG_NOFAIL);
        assertEquals(2, flag.count());
        assertTrue(flag.has(MonTimedFlags.MON_TMD_FLG_NOMESSAGE));
        assertTrue(flag.has(MonTimedFlags.MON_TMD_FLG_NOFAIL));
        assertFalse(flag.has(MonTimedFlags.MON_TMD_FLG_NOTIFY));
    }

    /**
     * {@code context->flag | MON_TMD_FLG_NOTIFY} in {@code project-mon.c}: OR-ing a bit that is
     * already set changes nothing, so {@code on} twice leaves one flag.
     *
     * <p>Function settingTwiceIsIdempotent coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("Setting NOTIFY twice is idempotent, as OR-ing 0x01 twice is")
    void settingTwiceIsIdempotent() {
        Flag<MonTimedFlags> flag = new Flag<>(MonTimedFlags.class);
        flag.on(MonTimedFlags.MON_TMD_FLG_NOTIFY);
        flag.on(MonTimedFlags.MON_TMD_FLG_NOTIFY);
        assertEquals(1, flag.count());
        assertTrue(flag.has(MonTimedFlags.MON_TMD_FLG_NOTIFY));
    }

    /**
     * Clearing one bit leaves the others, as {@code flag &= ~MON_TMD_FLG_NOTIFY} would.
     *
     * <p>Function clearingOneLeavesTheRest coded on 261010, commented in full on 261010.
     */
    @Test
    @DisplayName("Clearing one flag leaves the others set")
    void clearingOneLeavesTheRest() {
        Flag<MonTimedFlags> flag = new Flag<>(MonTimedFlags.class,
                MonTimedFlags.MON_TMD_FLG_NOTIFY, MonTimedFlags.MON_TMD_FLG_NOFAIL);
        flag.off(MonTimedFlags.MON_TMD_FLG_NOTIFY);
        assertFalse(flag.has(MonTimedFlags.MON_TMD_FLG_NOTIFY));
        assertTrue(flag.has(MonTimedFlags.MON_TMD_FLG_NOFAIL));
        assertEquals(1, flag.count());
    }
}
