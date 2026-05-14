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
 *  Java code copyright (c) 2026 Rowan Crowther, Jack of Trades Ltd.
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

public class TermXtraWin implements TermEventHook {
    private static Logger logger = LogManager.getLogger();

    private MediaPlayer mediaPlayer;

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

    private void termXtraDelay(int delay) {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    private void termXtraWinReact() {
        Screen mainScreen = GameConstants.AngbandScreens.get(0);

        long code;
        boolean change = false;


    }

    private void termXtraWinClear() {
        Screen screen = GameConstants.AngbandScreens.get(0);
        screen.clear();
    }

    private void termXtraWinFlush() {
        // Not needed as we are in Java and all events are handled by event handlers
    }

    private void termXtraWinEvent(int value) {
        // Not needed as we are in Java and all events are handled by event handlers
    }

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