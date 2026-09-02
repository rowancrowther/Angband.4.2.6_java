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

package uk.co.jackoftradesltd.middle.game.gameengine;

import uk.co.jackoftradesltd.middle.game.enums.CommandContext;

/**
 * The always-fail {@link CommandGetter} installed when no front-end has set a real one - the port of
 * C's unset {@code cmd_get_hook} and of {@code textui_get_cmd}'s "haven't got a command" path, which
 * likewise returns {@code errr} 1. It queues nothing and reports failure, so a game loop running
 * against it simply finds an empty queue each turn.
 *
 * @author Rowan Crowther
 */
public class DefaultCommandGetter implements CommandGetter {

    /**
     * Fetches nothing and reports failure.
     *
     * @param context ignored - no command is fetched
     * @param queue   ignored - nothing is pushed
     * @return C's {@code errr} failure value, 1
     */
    @Override
    public int getCommand(CommandContext context, CommandQueue queue) {
        return 1;
    }
}
