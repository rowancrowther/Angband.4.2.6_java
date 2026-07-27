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

package uk.co.jackoftrades.middle.game.globals.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.parser.FlavourReader;
import uk.co.jackoftrades.backend.parser.HintReader;
import uk.co.jackoftrades.backend.parser.NamesReader;
import uk.co.jackoftrades.backend.parser.ParseResult;
import uk.co.jackoftrades.middle.game.Hint;
import uk.co.jackoftrades.middle.game.Name;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftrades.middle.objects.FlavourKind;

import java.io.IOException;

/**
 * Startup loader for the misc slice: parses the three otherwise-homeless gamedata files
 * ({@code flavor.txt}, {@code hints.txt}, {@code names.txt}) and populates
 * {@link uk.co.jackoftrades.middle.game.globals.registry.MiscRegistry} through its setters.
 *
 * <p>This is the write side of the misc slice, paired with {@code MiscRegistry} (the read side). Of
 * the three, only {@code loadFlavours} has a cross-slice dependency: the flavour assembler resolves
 * each flavour to an object kind, so {@code GameConstants.init()} runs it after the object kinds are
 * loaded; hints and names are self-contained. Every loader here soft-fails — a file with parse
 * errors is logged and skipped, leaving that registry list unpopulated rather than partially
 * filled. It was split out of {@code GameConstants} as one domain slice of the loader/registry
 * refactor.
 *
 * @author Rowan Crowther
 */
public class MiscDataLoader {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Load the object flavours from {@code flavor.txt} into {@link MiscRegistry}. Must run after the
     * object kinds are loaded, since the flavour assembler resolves each flavour to an object kind.
     * A file with soft errors is logged and skipped, leaving the flavour list unpopulated.
     *
     * @author Rowan Crowther
     */
    public static void loadFlavours() {
        FlavourReader parser = new FlavourReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "flavor.txt";

        try {
            ParseResult<FlavourKind> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MiscRegistry.setFlavours(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the loading hints from {@code hints.txt} into {@link MiscRegistry}. Self-contained (no
     * cross-slice dependency). A file with soft errors is logged and skipped, leaving the hint list
     * unpopulated.
     *
     * @author Rowan Crowther
     */
    public static void loadHints() {
        HintReader parser = new HintReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "hints.txt";

        try {
            ParseResult<Hint> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MiscRegistry.setHints(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the random name lists from {@code names.txt} into {@link MiscRegistry}. Self-contained
     * (no cross-slice dependency). A file with soft errors is logged and skipped, leaving the name
     * list unpopulated.
     *
     * @author Rowan Crowther
     */
    public static void loadNames() {
        NamesReader parser = new NamesReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "names.txt";

        try {
            ParseResult<Name> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            MiscRegistry.setNames(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }
}
