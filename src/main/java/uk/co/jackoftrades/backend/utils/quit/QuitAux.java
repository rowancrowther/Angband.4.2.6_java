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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.utils.quit;

/**
 * Strategy interface for terminating the game, mirroring the swappable
 * {@code quit_aux} function pointer in the original C source. Abstracting the
 * quit behaviour behind an interface lets the front end decide what "quit"
 * actually does (clean exit, save-and-exit, error abort, …) without the core
 * game code needing to know.
 *
 * @author ClaudeCode
 */
public interface QuitAux {

    /**
     * Quits the system with a specific message
     *
     * @param quitMessage The message to pass from the program as it quits
     */
    void quit(String quitMessage);
}
