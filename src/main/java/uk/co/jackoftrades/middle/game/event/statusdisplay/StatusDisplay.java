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

/**
 * The boundary the middle end reports start-up progress through: "show the title screen", and "here is
 * what I am loading now". The front end supplies the implementation; the middle end only ever sees
 * this interface, which is what keeps {@code lib/gamedata} loading free of any Swing type.
 *
 * <p>This is the port of the two callbacks C registers for the initialisation events -
 * {@code ui_enter_init} and {@code splashscreen_note}, both in {@code [C] src/ui-display.c}. C
 * reaches them through the event bus alone; the port keeps the bus for the <em>trigger</em>
 * ({@code EVENT_ENTER_INIT} still arrives as an event) but routes the <em>call</em> through this
 * named interface, so the display contract is a type rather than a convention about which
 * function pointer was registered where.
 *
 * <p>Implementations are reached through {@link StatusDisplayHolder}, never constructed by the
 * caller. The holder is pre-filled with {@link DefaultStatusDisplay} so middle-end code may call
 * without a null check when no front end exists - which is every headless test.
 *
 * <p><b>Threading.</b> Both methods are called from the game thread, because that is where
 * {@code GameConstants.init()} runs. A Swing implementation therefore cannot touch its components
 * directly; it has to hop to the event dispatch thread itself.
 *
 * @author Rowan Crowther
 */
public interface StatusDisplay {
    /**
     * Put the title screen up. Called once, when the middle end enters initialisation and before
     * any game data has been read - so this is the first thing the player sees.
     *
     * @author Rowan Crowther
     */
    void showSplashScreen();

    /**
     * Report what is being loaded, for display beneath the title screen. Called repeatedly during
     * initialisation, once per data file or stage.
     *
     * @param message the progress note to show
     * @author Rowan Crowther
     */
    void splashScreenNote(String message);
}
