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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.backend.parser.FlavourReader;
import uk.co.jackoftradesltd.backend.parser.HintReader;
import uk.co.jackoftradesltd.backend.parser.NamesReader;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.parser.ErrorParsing;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.Hint;
import uk.co.jackoftradesltd.middle.game.Name;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;

import java.io.IOException;

/**
 * Startup loader for the misc slice: parses the three otherwise-homeless gamedata files
 * ({@code flavor.txt}, {@code hints.txt}, {@code names.txt}) and populates
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry} through its setters.
 *
 * <p>This is the write side of the misc slice, paired with {@code MiscRegistry} (the read side). Of
 * the three, only {@code loadFlavours} has a cross-slice dependency: the flavour assembler resolves
 * each flavour to an object kind, so {@code GameConstants.init()} runs it after the object kinds are
 * loaded; hints and names are self-contained. It was split out of {@code GameConstants} as one
 * domain slice of the loader/registry refactor.
 *
 * <p>Each loader stands in for the {@code run_parse_*} and {@code finish_parse_*} pair of the
 * matching C parser in {@code init.c} ({@code flavor_parser}, {@code hints_parser},
 * {@code names_parser}), run by {@code run_parser} ({@code datafile.c}). C stops the game with
 * {@code quit} in two cases: the file cannot be opened ({@code parse_file_quit_not_found}), and the
 * parse reports an error ({@code init_arrays} then quits with "Cannot initialize"). The loaders
 * stop start-up in the same two cases, by throwing a {@link RuntimeException} that
 * {@code GameConstants.init} wraps with the failing file's name:
 * <ul>
 *   <li>the file cannot be read (an {@link java.io.IOException} from the reader);</li>
 *   <li>the parse yields no records at all and has errors — a hard grammar error cancels the parse
 *       and gives exactly this, so {@link ErrorParsing#reportAndCheck} returns {@code false}.</li>
 * </ul>
 * Soft errors that leave at least one assembled record are the one place the port is more forgiving
 * than C, whose parser stops at the first error: they are logged through
 * {@link ErrorParsing#reportAndCheck} and the records that did assemble are still registered, per
 * the partial-results contract. The user-directory override that C's {@code parse_file} tries first
 * is not ported: only the gamedata directory is read.
 *
 * <p>Class MiscDataLoader coded before 261009, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
public class MiscDataLoader {
    /**
     * Reports file-loading failures and the soft parse errors of the three misc files, so each is
     * attributed to this loader rather than to the reader that found it.
     *
     * <p>Field logger coded before 261009, commented in full on 261013.
     */
    private static final Logger logger = LogManager.getLogger(MiscDataLoader.class);

    /**
     * Load the object flavours from {@code flavor.txt} into {@link MiscRegistry}. Must run after the
     * object kinds are loaded, since the flavour assembler resolves each flavour to an object kind.
     *
     * <p>The port of C's {@code run_parse_flavor} and {@code finish_parse_flavor} ({@code init.c}),
     * the latter of which does no more than assign the parsed list to the global {@code flavors}.
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck}, and the flavours that did
     * assemble are registered regardless, per the partial-results contract. A file that cannot be
     * read, or a parse that yields no flavours while reporting errors, stops start-up as C's
     * {@code quit} does, and nothing is registered.
     *
     * <p>Method loadFlavours coded before 261009, commented in full on 261013.
     *
     * @throws RuntimeException if {@code flavor.txt} cannot be read, or yields no flavours with
     *         errors reported
     */
    public static void loadFlavours() {
        FlavourReader parser = new FlavourReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "flavor.txt";

        try {
            ParseResult<FlavourKind> result = parser.parseWithResults(filename);

            if (!ErrorParsing.reportAndCheck(filename, result, logger)) {
                String message = "Error while parsing file " + filename;
                logger.fatal(message);
                throw new RuntimeException(message);
            }

            MiscRegistry.setFlavours(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the loading hints from {@code hints.txt} into {@link MiscRegistry}. Self-contained (no
     * cross-slice dependency).
     *
     * <p>The port of C's {@code run_parse_hints} and {@code finish_parse_hints} ({@code init.c}),
     * the latter of which does no more than assign the parsed list to the global {@code hints}.
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck}, and the hints that did
     * assemble are registered regardless, per the partial-results contract. A file that cannot be
     * read, or a parse that yields no hints while reporting errors, stops start-up as C's
     * {@code quit} does, and nothing is registered. A hint file with no {@code H:} line at all is
     * accepted, as in C, provided its {@code record-count} header says {@code 0}: {@code HintGrammar}
     * takes zero or more hints, and the registry is given an empty list where C leaves {@code hints}
     * {@code NULL}. A header that promises hints the file does not have is the record-count soft
     * error with no hints behind it, so that file does stop start-up.
     *
     * <p>Method loadHints coded before 261009, commented in full on 261013.
     *
     * @throws RuntimeException if {@code hints.txt} cannot be read, or yields no hints with errors
     *         reported
     */
    public static void loadHints() {
        HintReader parser = new HintReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "hints.txt";

        try {
            ParseResult<Hint> result = parser.parseWithResults(filename);

            if (!ErrorParsing.reportAndCheck(filename, result, logger)) {
                String message = "Error while parsing file " + filename;
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            
            MiscRegistry.setHints(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Load the random name lists from {@code names.txt} into {@link MiscRegistry}. Self-contained
     * (no cross-slice dependency).
     *
     * <p>The port of C's {@code run_parse_names} and {@code finish_parse_names} ({@code init.c});
     * the flattening into per-section word lists that {@code finish_parse_names} does is
     * {@link MiscRegistry#setNames}. Soft errors are reported through
     * {@link ErrorParsing#reportAndCheck}, and the name records that did assemble are registered
     * regardless, per the partial-results contract. A file that cannot be read, or a parse that
     * yields no records while reporting errors, stops start-up as C's {@code quit} does, and
     * nothing is registered.
     *
     * <p>A second hard failure is a record whose {@code section:} is outside the usable range —
     * C's {@code PARSE_ERROR_OUT_OF_BOUNDS} from {@code parse_names_section} ({@code init.c}), which
     * stops C from starting. {@link MiscRegistry#setNames} throws an
     * {@link IllegalArgumentException} for it and this method deliberately does not catch it, so it
     * reaches {@code GameConstants.init}, which stops start-up with an error naming
     * {@code names.txt}. The port is stricter than C only for section zero, which C accepts.
     *
     * <p>Method loadNames coded before 261009, commented in full on 261013.
     *
     * @throws RuntimeException if {@code names.txt} cannot be read, or yields no records with
     *         errors reported
     * @throws IllegalArgumentException if a name record's section names no usable
     *         {@link uk.co.jackoftradesltd.middle.player.enums.RandnameType}
     */
    public static void loadNames() {
        NamesReader parser = new NamesReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "names.txt";

        try {
            ParseResult<Name> result = parser.parseWithResults(filename);

            if (!ErrorParsing.reportAndCheck(filename, result, logger)) {
                String message = "Error while parsing file " + filename;
                logger.fatal(message);
                throw new RuntimeException(message);
            }
            
            MiscRegistry.setNames(result.items());
        } catch (IOException e) {
            String message = "Error while loading file " + filename;
            logger.fatal(message, e);
            throw new RuntimeException(message, e);
        }
    }
}
