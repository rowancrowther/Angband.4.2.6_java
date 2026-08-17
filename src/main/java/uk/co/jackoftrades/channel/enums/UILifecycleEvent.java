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

package uk.co.jackoftrades.channel.enums;

/**
 * What the front end can ask of the core about its life, as opposed to about the game. Travels in
 * {@link uk.co.jackoftrades.channel.messages.UIMessage.LifecycleUIMessage}, and the counterpart of
 * {@link CoreLifecycleEvent}.
 *
 * <p>These bracket a run: the core is told when to begin and when to stop, and says nothing in
 * between about either. In C the same decisions are made by {@code main()} calling
 * {@code play_game()} and returning from it; with the game on its own thread they have to become
 * messages, because the front end can no longer simply call the game.
 *
 * @author Rowan Crowther
 */
public enum UILifecycleEvent {
    /**
     * Begin. Sent once, when the front end is ready to display what the core produces — the port
     * of the moment C's {@code main()} calls into the game.
     */
    START,

    /**
     * Save the game and shut down. The two are one request because the front end is never entitled
     * to ask for a shutdown that skips the save; separating them would make "quit without saving"
     * expressible, and C only offers that by way of a deliberate suicide command rather than as an
     * exit route.
     *
     * <p>Asynchronous: this asks, and the core replies with {@link CoreLifecycleEvent#STOPPED}
     * when it is done. The front end must wait for that reply rather than exiting on having sent
     * this.
     */
    SAVE_AND_STOP
}
