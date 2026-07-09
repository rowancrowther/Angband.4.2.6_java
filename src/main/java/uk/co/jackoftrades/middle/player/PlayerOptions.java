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

package uk.co.jackoftrades.middle.player;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.player.enums.PlayerOption;

public class PlayerOptions {
    private Flag<PlayerOption> options;

    private int hitpointWarn;
    private int lazymoveDelay;
    private int delayFactor;

    private int nameSuffix;

    /**
     * Check to see whether a particular flag is set on the options
     *
     * @param option the PlayerOptionType flag we are checking
     * @return true if the flag is set
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean has(@NotNull PlayerOption option) {
        return options.has(option);
    }
}
