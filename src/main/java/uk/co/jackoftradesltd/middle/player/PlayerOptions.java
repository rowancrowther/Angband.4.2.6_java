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

package uk.co.jackoftradesltd.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

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
     */
    private Flag<PlayerOptionEnum> options;

    /**
     * The low-hitpoint warning threshold, in tenths of maximum HP (0 to 9; C defaults it to 3 in
     * {@code option.c:163}). The warning fires once current HP falls below
     * {@code maxHP * hitpointWarn / 10} — see C's {@code player-util.c:201} and
     * {@code player.c:329}, which applies the same fraction to spell points.
     */
    private int hitpointWarn;
    /**
     * How long to pause, in centiseconds, before acting on a movement key, so that a second
     * keypress can arrive and be treated as a diagonal or a run. Zero disables the wait. C feeds
     * this straight into {@code inkey_scan} ({@code ui-input.c:1573-1576}).
     */
    private int lazymoveDelay;
    /**
     * The visual delay factor (0 to 9), used as a millisecond pause between the frames of animated
     * effects such as bolts and explosions, so the player can see them travel. C reads it as
     * {@code int msec = player->opts.delay_factor} ({@code ui-display.c:1565}).
     */
    private int delayFactor;

    /**
     * A numeric suffix disambiguating save files for characters that share a name — purely a
     * bookkeeping value, written and read back by the savefile code ({@code save.c:432},
     * {@code load.c:711}) and never consulted during play.
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

    /**
     * Resets every option of the given page to the player's saved customized defaults — the port of
     * C's {@code options_restore_custom} ({@code option.c:225-333}). If no customized-options file
     * exists yet, this falls back to {@link #restoreMaintainer(PlayerOptionTypes)} instead, matching
     * C's {@code options_restore_maintainer} call at {@code option.c:236}.
     *
     * <p>Each line of the file is {@code option:<name>:<yes|no>}, written by the (not yet ported)
     * save side of this pair. "yes" turns the option on, "no" turns it off; either way, an option
     * named in the file overrides whatever the flag set already held for it, exactly as C's
     * {@code (*opts).opt[opt] = true} / {@code = false} do.
     *
     * <p>The return value follows C's own contract for it: {@code true} means "successful", which
     * includes the missing-file case, and {@code false} only for a customized-options file that
     * exists but could not be parsed cleanly (an unreadable line, an unknown option name, or an
     * I/O error).
     *
     * <p>Function restoreCustom coded before 260907, commented in full on 260907.
     *
     * @param type the option page to restore
     * @return {@code true} if the page was restored without error (including when no customized
     * file exists), {@code false} if the file exists but could not be fully parsed
     */
    private boolean restoreCustom(PlayerOptionTypes type) {
        String optionTag = "option:";
        boolean loadedNoErrors = true;

        String pageName = type.getName();
        String filename = AngbandDirs.ANGBAND_DIRS.USER.getPath() + "customized_" + pageName + "_options.txt";
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            logger.info("Customized options file " + filename + " does not exist.");
            restoreMaintainer(type);
            return true;
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
                        options.off(poEnum);
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

    /**
     * Resets every option of the given page to the maintainer's built-in defaults — the port of C's
     * {@code options_restore_maintainer} ({@code option.c:338-345}). Each option on the page is set
     * on if {@link PlayerOptionEnum#isNormal()} reports it as normally-on, off otherwise; options on
     * other pages are untouched.
     *
     * <p>This is the fallback {@link #restoreCustom(PlayerOptionTypes)} reaches for when there is no
     * customized-options file to read, and is also how C's {@code options_init_cheat} clears the
     * cheat page directly.
     *
     * <p>Function restoreMaintainer coded before 260907, commented in full on 260907.
     *
     * @param type the option page to reset
     */
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

    /**
     * Makes a field-by-field copy of this player's options — the port of the plain struct
     * assignment C uses to save and restore {@code player->opts} around birth
     * ({@code opts_save = p->opts}, {@code player-birth.c:403}). C gets a real copy for free because
     * {@code struct player_options} holds nothing but value fields; here that has to be done by
     * hand, one field at a time, including a deep copy of the option flag set via
     * {@link Flag#copyFrom(uk.co.jackoftradesltd.channel.utils.FlagView)} so the copy shares nothing
     * with the original.
     *
     * <p>Function copy coded before 260907, commented in full on 260907.
     *
     * @return a new {@link PlayerOptions} with the same flags and numeric settings as this one
     */
    public PlayerOptions copy() {
        Flag<PlayerOptionEnum> copyOptions = new Flag<>(PlayerOptionEnum.class);
        copyOptions.copyFrom(options);

        PlayerOptions copy = new PlayerOptions();
        copy.options = copyOptions;
        copy.hitpointWarn = hitpointWarn;
        copy.lazymoveDelay = lazymoveDelay;
        copy.delayFactor = delayFactor;
        copy.nameSuffix = nameSuffix;
        return copy;
    }

    /**
     * Clears every cheat option and its paired score option — the port of C's
     * {@code options_init_cheat} ({@code option.c:133-143}).
     *
     * <p>{@code list-options.h} documents that "cheat options need to be followed by
     * corresponding score options" ({@code list-options.h:7}), and C leans on that ordering
     * directly: for each cheat-type index {@code i} it clears {@code opt[i]} and
     * {@code opt[i + 1]} by raw array arithmetic. The port has no index to add one to, so
     * it walks {@link PlayerOptionEnum#values()} in the same declared order — which mirrors
     * {@code list-options.h} exactly — and carries a {@code prevIsCheat} flag forward instead:
     * an option is switched off if it is itself a cheat option, or if the option immediately
     * before it was one. That reproduces C's {@code opt[i + 1]} clear without needing an index.
     *
     * <p>Function optionsInitCheat coded before 260907, commented in full on 260907.
     */
    public void optionsInitCheat() {
        boolean prevIsCheat = false;
        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            if (option.isCheat()) {
                options.off(option);
                prevIsCheat = true;
            } else if (prevIsCheat) {
                options.off(option);
                prevIsCheat = false;
            }
        }
    }
}
