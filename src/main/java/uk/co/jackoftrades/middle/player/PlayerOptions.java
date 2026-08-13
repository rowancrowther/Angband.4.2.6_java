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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.channel.directories.AngbandDirs;
import uk.co.jackoftrades.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftrades.middle.player.enums.PlayerOptionTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private static final Logger logger = LogManager.getLogger(PlayerOptions.class);

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

    public PlayerOptions() {
        options = new Flag<>(PlayerOptionEnum.class);
    }

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

    public void initDefaults() {
        options = new Flag<>(PlayerOptionEnum.class);

        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            if (option.isNormal())
                options.on(option);
        }

        restoreCustom(PlayerOptionTypes.BIRTH);
        restoreCustom(PlayerOptionTypes.INTERFACE);

        delayFactor = 40;
        hitpointWarn = 3;
    }

    private boolean restoreCustom(PlayerOptionTypes type) {
        String optionTag = "option:";
        boolean loadedNoErrors = true;

        String pageName = type.getName();
        String filename = AngbandDirs.ANGBAND_DIRS.USER.getPath() + "customized_" + pageName + "_options.txt";
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            logger.info("Customized options file " + filename + " does not exist.");
            restoreMaintainer(type);
            return false;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                if (!line.startsWith(optionTag)) {
                    logger.warn("Line read in from file " + filename + " has an illegal option line: " + line);
                    loadedNoErrors = false;
                    continue;
                }

                String optionName = line.substring(optionTag.length());

                String[] optionStringSplit = optionName.split(":");

                if (optionStringSplit.length != 2) {
                    logger.warn("Line read in from file " + filename + " has too many ':' in it: " + line);
                    loadedNoErrors = false;
                    continue;
                }

                if (!optionStringSplit[1].equalsIgnoreCase("yes") && !optionStringSplit[1].equalsIgnoreCase("no")) {
                    logger.warn("Line read in from file " + filename + " does not have 'yes' or 'no' as a option value: " + line);
                    loadedNoErrors = false;
                    continue;
                }

                try {
                    PlayerOptionEnum poEnum = PlayerOptionEnum.valueOf("OP_" + optionStringSplit[0].toLowerCase());

                    if (optionStringSplit[1].equalsIgnoreCase("yes")) {
                        options.on(poEnum);
                    } else {
                        options.on(poEnum);
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("Line read in from file " + filename + " has an unknown option: " + line);
                    loadedNoErrors = false;
                }
            }
        } catch (IOException e) {
            logger.warn("IOException while reading from file " + filename + ": " + e.getMessage());
            loadedNoErrors = false;
        }

        return loadedNoErrors;
    }

    private void restoreMaintainer(PlayerOptionTypes type) {
        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            if (option.getPlayerOptionType().equals(type)) {
                if (option.isNormal())
                    options.on(option);
                else
                    options.off(option);
            }
        }
    }
}
