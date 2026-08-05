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

package uk.co.jackoftrades.middle.game.event.statusdisplay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The {@link StatusDisplay} that does nothing: what {@link StatusDisplayHolder} holds until a front
 * end registers, and what runs whenever there is no front end at all.
 *
 * <p>Its whole job is to let the holder's slot start non-null, so middle-end code may report
 * progress unconditionally. Without it every call site would need a null check, and a missed one
 * would be a {@link NullPointerException} in the middle of the data load - on the path that only
 * runs headlessly, so only tests and batch runs would ever hit it.
 *
 * <p>Silently discarding is the right behaviour, not a placeholder: a run with no display has
 * nowhere to put a title screen. C makes the same choice by simply having no handler registered for
 * the initialisation events, in which case the signal is dispatched to an empty list.
 *
 * @author Rowan Crowther
 */
public class DefaultStatusDisplay implements StatusDisplay {
    /**
     * Logger, currently unused - the methods deliberately say nothing, since a headless run would
     * otherwise log a line per data file loaded.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger(DefaultStatusDisplay.class);

    /**
     * Discard the request; there is no screen to show a title on.
     *
     * @author Rowan Crowther
     */
    @Override
    public void showSplashScreen() {

    }

    /**
     * Discard the note; there is no screen to show it on.
     *
     * @param message the progress note, ignored
     * @author Rowan Crowther
     */
    @Override
    public void splashScreenNote(String message) {

    }
}
