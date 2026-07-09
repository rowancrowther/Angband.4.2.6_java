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

package uk.co.jackoftrades.frontend.screen.hooks;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.frontend.screen.Screen;
import uk.co.jackoftrades.frontend.screen.enums.TermXtraEventEnum;
import uk.co.jackoftrades.frontend.sounds.MessageBoxFlags;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.io.File;

/**
 * Windows-style implementation of the {@link TermEventHook} "extra event"
 * handler — the Java/JavaFX port of the C original's {@code Term_xtra_win}
 * ({@code src/main-win.c}). It dispatches each {@link TermXtraEventEnum} to the
 * appropriate action (play a sound, clear the screen, delay, …). Several actions
 * that the C code needed (flush, event pumping) are no-ops here because JavaFX's
 * event loop handles them.
 *
 * @author Rowan Crowther
 */
public class TermXtraWin implements TermEventHook {
    /**
     * Logger used to report sound/playback failures.
     *
     * @author Rowan Crowther
     */
    private static Logger logger = LogManager.getLogger();

    /**
     * Player retained for the most recent sound effect so it is not garbage
     * collected before playback completes.
     *
     * @author Rowan Crowther
     */
    private MediaPlayer mediaPlayer;

    /**
     * Dispatch a terminal extra-event to its concrete handler.
     *
     * @param event the requested action
     * @param value an event-specific parameter (e.g. delay length)
     * @author Rowan Crowther
     */
    public void doSomething(@NotNull TermXtraEventEnum event, int value) {
        switch (event) {
            case TERM_XTRA_NOISE:
                termXtraWinNoise(MessageBoxFlags.MB_ICONASTERISK);
                break;

            case TERM_XTRA_BORED:
                termXtraWinEvent(0);
                break;

            case TERM_XTRA_EVENT:
                termXtraWinEvent(value);

            case TERM_XTRA_FLUSH:
                termXtraWinFlush();
                break;

            case TERM_XTRA_CLEAR:
                termXtraWinClear();
                break;

            case TERM_XTRA_REACT:
                termXtraWinReact();
                break;

            case TERM_XTRA_DELAY:
                termXtraDelay(value);
                break;
        }
    }

    /**
     * Pause the current thread for the given number of milliseconds (a no-op for
     * non-positive delays).
     *
     * @param delay delay in milliseconds
     * @author Rowan Crowther
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
     *
     * @author Rowan Crowther
     */
    private void termXtraWinReact() {
        Screen mainScreen = GameConstants.AngbandScreens.get(0);

        long code;
        boolean change = false;


    }

    /**
     * Clear the main screen.
     *
     * @author Rowan Crowther
     */
    private void termXtraWinClear() {
        Screen screen = GameConstants.AngbandScreens.get(0);
        screen.clear();
    }

    /**
     * Flush pending output. A no-op under JavaFX, whose event handlers manage
     * flushing; retained to satisfy the C event model.
     *
     * @author Rowan Crowther
     */
    private void termXtraWinFlush() {
        // Not needed as we are in Java and all events are handled by event handlers
    }

    /**
     * Pump/await a windowing event. A no-op under JavaFX, whose event handlers
     * deliver events directly; retained to satisfy the C event model.
     *
     * @param value the event parameter (unused here)
     * @author Rowan Crowther
     */
    private void termXtraWinEvent(int value) {
        // Not needed as we are in Java and all events are handled by event handlers
    }

    /**
     * Play the sound associated with the given message-box style via a JavaFX
     * {@link MediaPlayer}. Failures are logged and swallowed so a missing/invalid
     * sound file never interrupts gameplay.
     *
     * @param flag the message-box style whose sound to play
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    private void termXtraWinNoise(MessageBoxFlags flag) {
        try {
            File file = flag.getFileName();
            Media sound = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            logger.error("Error caught", e);
        }
    }
}