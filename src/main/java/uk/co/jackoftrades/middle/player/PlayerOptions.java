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
import uk.co.jackoftrades.middle.player.enums.PlayerOptionEnum;

/**
 * The player's option settings — the port of C's {@code struct player_options}
 * ({@code option.h:61-69}), reached in C as {@code player->opts}.
 *
 * <p>C stores the on/off options as a plain {@code bool opt[OPT_MAX]} array indexed by the
 * {@code OPT_*} enum, so C's {@code OPT(player, name)} macro is an array lookup. The port holds
 * them as a {@link Flag} set over {@link PlayerOptionEnum} instead, which is why the accessor is
 * {@link #has(PlayerOptionEnum)} rather than an index. The four numeric settings below are not
 * booleans and so sit outside that set, exactly as they do in C.
 *
 * <p>All five members are saved and restored with the character ({@code save.c:320-432},
 * {@code load.c:434-711}), so they are per-player preferences rather than global configuration.
 *
 * @author Rowan Crowther
 */
public class PlayerOptions {
    /**
     * The set of boolean options currently switched on, standing in for C's
     * {@code bool opt[OPT_MAX]}. Options absent from the set are off.
     *
     * @author Rowan Crowther
     */
    private Flag<PlayerOptionEnum> options;

    /**
     * The low-hitpoint warning threshold, in tenths of maximum HP (0 to 9; C defaults it to 3 in
     * {@code option.c:163}). The warning fires once current HP falls below
     * {@code maxHP * hitpointWarn / 10} — see C's {@code player-util.c:201} and
     * {@code player.c:329}, which applies the same fraction to spell points.
     *
     * @author Rowan Crowther
     */
    private int hitpointWarn;
    /**
     * How long to pause, in centiseconds, before acting on a movement key, so that a second
     * keypress can arrive and be treated as a diagonal or a run. Zero disables the wait. C feeds
     * this straight into {@code inkey_scan} ({@code ui-input.c:1573-1576}).
     *
     * @author Rowan Crowther
     */
    private int lazymoveDelay;
    /**
     * The visual delay factor (0 to 9), used as a millisecond pause between the frames of animated
     * effects such as bolts and explosions, so the player can see them travel. C reads it as
     * {@code int msec = player->opts.delay_factor} ({@code ui-display.c:1565}).
     *
     * @author Rowan Crowther
     */
    private int delayFactor;

    /**
     * A numeric suffix disambiguating save files for characters that share a name — purely a
     * bookkeeping value, written and read back by the savefile code ({@code save.c:432},
     * {@code load.c:711}) and never consulted during play.
     *
     * @author Rowan Crowther
     */
    private int nameSuffix;

    /**
     * Reports whether a boolean option is switched on — the port of C's {@code OPT(player, name)}
     * macro, which indexes {@code player->opts.opt[]}.
     *
     * @param option the option to test
     * @return {@code true} if the option is set
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean has(@NotNull PlayerOptionEnum option) {
        return options.has(option);
    }
}
