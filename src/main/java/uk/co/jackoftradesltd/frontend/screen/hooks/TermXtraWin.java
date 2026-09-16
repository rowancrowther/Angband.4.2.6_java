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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.frontend.screen.enums.TermXtraEventEnum;
import uk.co.jackoftradesltd.frontend.sounds.MessageBoxFlags;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Windows-style implementation of the {@link TermEventHook} "extra event"
 * handler — the Java/JavaFX port of the C original's {@code Term_xtra_win}
 * ({@code src/main-win.c}). It dispatches each {@link TermXtraEventEnum} to the
 * appropriate action (play a sound, clear the screen, delay, …). Several actions
 * that the C code needed (flush, event pumping) are no-ops here because JavaFX's
 * event loop handles them.
 *
 * <p>Class TermXtraWin commented in full before 260916, provenance stamp added
 * on 260916.
 *
 * @author Rowan Crowther
 */
public class TermXtraWin implements TermEventHook {
    /**
     * Logger used to report sound/playback failures, most notably a missing or
     * unreadable file behind {@link #termXtraWinNoise(MessageBoxFlags)}.
     *
     * <p>Field logger commented in full before 260916, provenance stamp added on
     * 260916.
     */
    private static Logger logger = LogManager.getLogger();

    /**
     * Dispatch a terminal extra-event to its concrete handler.
     *
     * @param event the requested action
     * @param value an event-specific parameter (e.g. delay length)
     */
    public void doSomething(@NotNull TermXtraEventEnum event, int value) {
        switch (event) {
            case TERM_XTRA_NOISE -> termXtraWinNoise(MessageBoxFlags.MB_ICONASTERISK);

            case TERM_XTRA_BORED -> termXtraWinEvent(0);

            case TERM_XTRA_EVENT -> termXtraWinEvent(value);

            case TERM_XTRA_FLUSH -> termXtraWinFlush();

            case TERM_XTRA_CLEAR -> termXtraWinClear();

            case TERM_XTRA_REACT -> termXtraWinReact();

            case TERM_XTRA_DELAY -> termXtraDelay(value);
        }
    }

    /**
     * Pause the current thread for the given number of milliseconds (a no-op for
     * non-positive delays).
     *
     * @param delay delay in milliseconds
     */
    private void termXtraDelay(int delay) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * React to a change in colour/preferences on the main screen. Currently a
     * stub awaiting the colour-reaction logic from the C original.
     */
    private void termXtraWinReact() {
        //Screen mainScreen = GameConstants.AngbandScreens.get(0);

        long code;
        boolean change = false;


    }

    /**
     * Clear the main screen.
     */
    private void termXtraWinClear() {
        //Screen screen = GameConstants.AngbandScreens.get(0);
        //screen.clear();
    }

    /**
     * Flush pending output. A no-op under JavaFX, whose event handlers manage
     * flushing; retained to satisfy the C event model.
     */
    private void termXtraWinFlush() {
        // Not needed as we are in Java and all events are handled by event handlers
    }

    /**
     * Pump/await a windowing event. A no-op under JavaFX, whose event handlers
     * deliver events directly; retained to satisfy the C event model.
     *
     * @param value the event parameter (unused here)
     */
    private void termXtraWinEvent(int value) {
        // Not needed as we are in Java and all events are handled by event handlers
    }

    /**
     * Plays the sound bound to a {@link MessageBoxFlags} style — the boundary's
     * stand-in for the C original's {@code Term_xtra_win_noise}
     * ({@code [C] src/main-win.c}), which unconditionally calls
     * {@code MessageBeep(MB_ICONASTERISK)}. Opens {@link MessageBoxFlags#getFileName()}
     * as a {@link javax.sound.sampled.Clip} and starts it asynchronously, matching
     * {@code MessageBeep}'s fire-and-forget behaviour of returning immediately
     * without waiting for playback to finish. Failures (a missing or unreadable
     * file, no available audio line, …) are logged and swallowed so they never
     * interrupt gameplay, mirroring the C original's disregard for
     * {@code MessageBeep}'s own return value.
     *
     * <p>Function termXtraWinNoise coded on 260916, commented in full on 260916.
     *
     * @param flag the message-box style whose sound to play
     */
    @Contract(pure = true)
    private void termXtraWinNoise(MessageBoxFlags flag) {
        try {
            File file = flag.getFileName();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(Clip.class, audioFormat);
            Clip clip = (Clip) AudioSystem.getLine(dataLineInfo);
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            logger.error("Error caught", e);
        }


    }
}