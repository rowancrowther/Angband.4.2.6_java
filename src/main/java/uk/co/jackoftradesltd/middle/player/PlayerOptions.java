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
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The player's option settings — the port of C's {@code struct player_options}
 * ({@code option.h}), reached in C as {@code player->opts}, together with the
 * {@code option.c} functions that reset and restore them.
 *
 * <p>C stores the on/off options as a plain {@code bool opt[OPT_MAX]} array indexed by the
 * {@code OPT_*} enum, so C's {@code OPT(player, name)} macro is an array lookup. The port holds
 * them as a {@link Flag} set over {@link PlayerOptionEnum} instead, which is why the accessor is
 * {@link #has(PlayerOptionEnum)} rather than an index. The four numeric settings below are not
 * booleans and so sit outside that set, exactly as they do in C.
 *
 * <p>All five members are saved and restored with the character ({@code save.c} and
 * {@code load.c}), so they are per-player preferences rather than global configuration.
 *
 * <p><b>Ported here:</b> {@code options_init_defaults}, {@code options_restore_custom},
 * {@code options_restore_maintainer}, {@code options_init_cheat} and the {@code OPT()} macro.
 * <b>Not yet ported:</b> {@code option_set} and {@code options_save_custom}, and there are no
 * accessors for {@code lazymoveDelay}, {@code delayFactor} or {@code nameSuffix} because nothing
 * reads them yet.
 *
 * <p>Class PlayerOptions coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class PlayerOptions {
    /**
     * Logger for the options-file diagnostics {@link #restoreCustom(Flag, PlayerOptionTypes)} writes
     * alongside the player-visible messages it sends.
     *
     * <p>Field logger coded before 261010, commented in full on 261010.
     */
    private static final Logger logger = LogManager.getLogger(PlayerOptions.class);

    /**
     * The set of boolean options currently switched on, standing in for C's
     * {@code bool opt[OPT_MAX]}. Options absent from the set are off, so a freshly constructed
     * {@link PlayerOptions} has every option off until {@link #initDefaults()} is called.
     *
     * <p>Field options coded before 261010, commented in full on 261010.
     */
    private Flag<PlayerOptionEnum> options;

    /**
     * The low-hitpoint warning threshold, in tenths of maximum HP (0 to 9; the options menu resets
     * anything above 9 to 0, and {@link #initDefaults()} sets it to 3). C's {@code take_hit} computes
     * {@code maxHP * hitpoint_warn / 10} with integer division and raises the warning when current
     * HP drops below it. {@code player_hp_attr} and {@code player_sp_attr} in {@code player.c} use
     * the same fraction to choose the yellow-to-red colour change for hit points and spell points.
     *
     * <p>Field hitpointWarn coded before 261010, commented in full on 261010.
     */
    private int hitpointWarn;
    /**
     * The movement delay, in units of ten milliseconds (0 to 255; the options menu shows
     * {@code lazymove_delay * 10} msec). When a direction key is read and this is non-zero, C waits
     * that long via {@code inkey_scan} for a second keypress, so that two keys can combine into a
     * diagonal. Zero disables the wait. Not touched by {@link #initDefaults()}, as in C.
     *
     * <p>Field lazymoveDelay coded before 261010, commented in full on 261010.
     */
    private int lazymoveDelay;
    /**
     * The base delay factor, in milliseconds (0 to 255; the options menu clamps to 255 and
     * {@link #initDefaults()} sets it to 40). C passes it to {@code Term_xtra(TERM_XTRA_DELAY, msec)}
     * between the frames of animated effects such as bolts and explosions, so the player can see
     * them travel. The "0 to 9" in the C struct comment is stale: the menu prompt says 0-255.
     *
     * <p>Field delayFactor coded before 261010, commented in full on 261010.
     */
    private int delayFactor;

    /**
     * A numeric suffix disambiguating save files for characters that share a name — purely a
     * bookkeeping value, written and read back by the savefile code ({@code save.c} and
     * {@code load.c}). Nothing else in the C source reads it. Not touched by
     * {@link #initDefaults()}.
     *
     * <p>Field nameSuffix coded before 261010, commented in full on 261010.
     */
    private int nameSuffix;

    /**
     * Creates an options object with every boolean option off and every numeric setting zero.
     * C has no equivalent, since {@code player->opts} is a plain member of {@code struct player};
     * the defaults only arrive when {@link #initDefaults()} runs (C's {@code init_player} in {@code player.c}
     * calls {@code options_init_defaults}).
     *
     * <p>Constructor PlayerOptions coded before 261010, commented in full on 261010.
     */
    public PlayerOptions() {
        options = new Flag<>(PlayerOptionEnum.class);
    }

    /**
     * Reports whether a boolean option is switched on — the port of C's {@code OPT(player, name)}
     * macro, which indexes {@code player->opts.opt[]}.
     *
     * <p>Method has coded before 261010, commented in full on 261010.
     *
     * @param option the option to test
     * @return {@code true} if the option is set
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean has(@NotNull PlayerOptionEnum option) {
        return options.has(option);
    }

    /**
     * Sets every option to its starting value — the port of C's {@code options_init_defaults}
     * ({@code option.c}).
     *
     * <p>The flag set is rebuilt from scratch with each option on if
     * {@link PlayerOptionEnum#isNormal()} says so. The player's customized defaults are then laid
     * over the BIRTH page and the INTERFACE page, in that order, through
     * {@link #restoreCustom(Flag, PlayerOptionTypes)}; the CHEAT and SCORE pages keep the
     * maintainer values, because C only restores customized defaults for those two pages. Finally the delay factor
     * becomes 40 (msec) and the hitpoint warning 3 (30% of HP). {@code lazymoveDelay} and
     * {@code nameSuffix} are left as they were, because C does not set them here either.
     *
     * <p>C takes the {@code struct player_options} to fill as a parameter; the port fills
     * {@code this}.
     *
     * <p>Method initDefaults coded before 261010, commented in full on 261010.
     */
    public void initDefaults() {
        options = new Flag<>(PlayerOptionEnum.class);

        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            if (option.isNormal())
                options.on(option);
        }

        restoreCustom(options, PlayerOptionTypes.BIRTH);
        restoreCustom(options, PlayerOptionTypes.INTERFACE);

        delayFactor = 40;
        hitpointWarn = 3;
    }

    /**
     * Applies the player's saved customized defaults for one option page — the port of C's
     * {@code options_restore_custom} ({@code option.c}). The file read is
     * {@code customized_<page>_options.txt} in the user directory. If it does not exist, the page
     * falls back to {@link #restoreMaintainer(Flag, PlayerOptionTypes)} and nothing is said to the
     * player, only logged — C is silent there too.
     *
     * <p>Each line of the file is trimmed first, so leading and trailing spaces and tabs, and
     * indented {@code #} comments, are tolerated as in C. Blank lines and {@code #} comment lines
     * are skipped. Every other line must be {@code option:<name>:yes} or {@code option:<name>:no},
     * where {@code <name>} is the bare option name without the {@code OP_} prefix and belongs to
     * {@code page}; the named option is then switched on or off, overriding whatever the flag set
     * held. A line that fails any of these tests is reported to the player with
     * {@link Message#message(String, Object...)} and skipped, and parsing carries on with the next
     * line — a bad line never abandons the rest of the file. Names are matched whole and
     * case-sensitively, and an option from a different page counts as unrecognized.
     *
     * <p>The return value follows C: {@code true} means "successful", which includes the
     * missing-file case and a file containing bad lines, and {@code false} only when the file
     * exists but could not be read (an {@link IOException}, which here also covers a file that is
     * not valid UTF-8).
     *
     * <p><b>Port divergences:</b> the player-visible messages name the file and quote the line
     * rather than giving C's line number and page name, and each bad line is also written to the
     * logger. A line with no value at all ({@code option:foo}) is reported as having the wrong
     * number of colons, where C calls it an unrecognized option, and a line with both an unknown
     * name and a bad value gets the bad-value message first. In every case the options are left
     * unchanged and the return value is the same as C's. {@link String#trim()} strips every
     * character up to U+0020, where C strips only spaces and tabs.
     *
     * <p>Function restoreCustom coded before 260907, commented in full on 261010.
     *
     * @param opts the flag set to update, standing in for C's {@code struct player_options *opts}
     * @param page the option page to restore
     * @return {@code true} if the page was restored (including when no customized file exists, or
     * the file had unparseable lines), {@code false} if the file exists but could not be read
     */
    private boolean restoreCustom(Flag<PlayerOptionEnum> opts, PlayerOptionTypes page) {
        String optionTag = "option:";

        String pageName = page.getName();
        String filename = AngbandDirs.ANGBAND_DIRS.USER.getPath() + "customized_" + pageName + "_options.txt";
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            String msg = "Customized options file " + filename + " does not exist.";
            logger.info(msg);
            restoreMaintainer(opts, page);
            return true;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                if (!line.startsWith(optionTag)) {
                    String msg = "Line read in from file " + filename + " has an illegal option line: " + line;
                    logger.warn(msg);
                    Message.message("%s", msg);
                    continue;
                }

                String optionName = line.substring(optionTag.length());

                String[] optionStringSplit = optionName.split(":");

                if ((optionStringSplit.length != 2) || optionName.endsWith(":")) {
                    String msg = "Line read in from file " + filename + " has the wrong number of ':' in it: " + line;
                    logger.warn(msg);
                    Message.message("%s", msg);
                    continue;
                }

                if (!optionStringSplit[1].equals("yes") && !optionStringSplit[1].equals("no")) {
                    String msg = "Line read in from file " + filename + " does not have 'yes' or 'no' as a option value: " + line;
                    logger.warn(msg);
                    Message.message("%s", msg);
                    continue;
                }
                
                boolean optionFound = false;
                for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
                    if (option.getPlayerOptionType() != page) continue;
                    
                    String optName = "OP_" + optionStringSplit[0];
                    
                    if (option.name().equals(optName)) {
                        if (optionStringSplit[1].trim().equals("yes")) {
                            opts.on(option);
                            optionFound = true;
                        }
                        else {
                            opts.off(option);
                            optionFound = true;
                        }                        
                        break;
                    }
                }
                
                if (!optionFound) {
                    String msg = "Unrecognized option at line " + line + " of the customized " + pageName + " options.";
                    logger.warn(msg);
                    Message.message("%s", msg);
                }
            }
        } catch (IOException e) {
            logger.warn("IOException while reading from file " + filename + ": " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Resets every option of the given page to the maintainer's built-in defaults — the port of C's
     * {@code options_restore_maintainer} ({@code option.c}). Each option on the page is set
     * on if {@link PlayerOptionEnum#isNormal()} reports it as normally-on, off otherwise; options on
     * other pages are untouched.
     *
     * <p>This is the fallback {@link #restoreCustom(Flag, PlayerOptionTypes)} reaches for when
     * there is no customized-options file to read. In C the options menu also calls it directly for
     * its "restore to maintainer's defaults" key; that caller is not ported yet, which is why the
     * method is still private. Unlike {@link #optionsInitCheat()}, it is not how the cheat options
     * are cleared at birth.
     *
     * <p>Function restoreMaintainer coded before 260907, commented in full on 261010.
     *
     * @param opts the flag set to update, standing in for C's {@code struct player_options *opts}
     * @param page the option page to reset
     */
    private void restoreMaintainer(Flag<PlayerOptionEnum> opts, PlayerOptionTypes page) {
        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            if (option.getPlayerOptionType().equals(page)) {
                if (option.isNormal())
                    opts.on(option);
                else
                    opts.off(option);
            }
        }
    }

    /**
     * Makes a field-by-field copy of this player's options — the port of the plain struct
     * assignment C uses to save and restore {@code player->opts} around birth
     * ({@code opts_save = p->opts} in {@code player-birth.c}). C gets a real copy for free because
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
     * {@code options_init_cheat} ({@code option.c}), which {@code do_cmd_accept_character} in
     * {@code player-birth.c} calls as the first thing it does when the new character is accepted. C acts on the global {@code player->opts}; the port acts on {@code this}.
     *
     * <p>{@code list-options.h} documents that "cheat options need to be followed by
     * corresponding score options", and C leans on that ordering
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

    /**
     * Returns the low-hitpoint warning threshold — the port of reading C's
     * {@code player->opts.hitpoint_warn}, as {@code take_hit}, {@code player_hp_attr} and
     * {@code player_sp_attr} do.
     *
     * <p>Method hitPointWarn coded before 261010, commented in full on 261010.
     *
     * @return the threshold in tenths of maximum HP (0 to 9)
     */
    public int hitPointWarn() {
        return hitpointWarn;
    }
}
