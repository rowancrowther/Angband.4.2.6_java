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

package uk.co.jackoftradesltd.frontend.screen.hooks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.frontend.screen.enums.TermXtraEventEnum;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * {@link TermXtraWin#termXtraWinNoise(uk.co.jackoftradesltd.frontend.sounds.MessageBoxFlags)},
 * reached only through {@link TermXtraWin#doSomething}. C's {@code Term_xtra_win_noise}
 * ({@code [C] src/main-win.c}) calls {@code MessageBeep(MB_ICONASTERISK)} and always
 * returns {@code 0} without inspecting {@code MessageBeep}'s result, so a beep failure
 * (missing sound device, unreadable file, …) must never be visible to the caller - the
 * port's equivalent is that {@code TERM_XTRA_NOISE} must never throw, regardless of
 * whether the host actually has a usable audio line.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermXtraWinTest {

    private TermXtraWin hook;

    @BeforeEach
    void buildAHook() {
        hook = new TermXtraWin();
    }

    /**
     * Dispatching {@code TERM_XTRA_NOISE} must complete without throwing, matching C
     * discarding whatever {@code MessageBeep} returns.
     */
    @Test
    void noiseEventNeverThrowsRegardlessOfAudioAvailability() {
        assertDoesNotThrow(() -> hook.doSomething(TermXtraEventEnum.TERM_XTRA_NOISE, 0));
    }

    /**
     * The {@code value} parameter carries no meaning for {@code TERM_XTRA_NOISE} in either
     * the C original or the port - it is unused here, so different values must not change
     * the no-throw outcome.
     */
    @Test
    void noiseEventIgnoresItsValueParameter() {
        assertDoesNotThrow(() -> hook.doSomething(TermXtraEventEnum.TERM_XTRA_NOISE, -1));
        assertDoesNotThrow(() -> hook.doSomething(TermXtraEventEnum.TERM_XTRA_NOISE, 42));
    }
}
