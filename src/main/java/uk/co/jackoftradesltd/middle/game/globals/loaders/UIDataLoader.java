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
import uk.co.jackoftradesltd.backend.parser.ParseResult;
import uk.co.jackoftradesltd.backend.parser.UIEntryBaseReader;
import uk.co.jackoftradesltd.backend.parser.UIEntryReader;
import uk.co.jackoftradesltd.backend.parser.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.game.globals.registry.UIRegistry;

import java.io.IOException;

/**
 * Startup loader for the UI slice: parses the UI-entry gamedata files
 * ({@code ui_entry_renderer.txt}, {@code ui_entry_base.txt}, {@code ui_entry.txt}) and populates
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.UIRegistry} through its setters.
 *
 * <p>This is the write side of the UI slice, paired with {@code UIRegistry} (the read side). Its
 * loaders are invoked by {@code GameConstants.init()} in dependency order — renderers before bases,
 * bases before entries — and must run before the player- and object-property loaders that resolve
 * their {@code bindui} targets against the loaded UI entries. It was split out of
 * {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class UIDataLoader {
    private static final Logger logger = LogManager.getLogger(UIDataLoader.class);

    /**
     * Load the UI entries from {@code ui_entry.txt} into {@link UIRegistry}. Must run after the entry
     * bases and renderers it references.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the entries that did
     * assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} rather than {@code IOException} and <em>rethrows</em>, so an unresolvable
     * base or renderer stops the load here rather than leaving the renderer subsystem half-built.
     *
     * @throws IOException if an IO error occurs while reading the file
     */
    public static void loadUIEntries() throws IOException {
        UIEntryReader parser = new UIEntryReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "ui_entry.txt";
        ParseResult<UIEntry> result;

        try {
            result = parser.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            UIRegistry.setUIEntries(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the UI entry bases from {@code ui_entry_base.txt} into {@link UIRegistry}. Must run
     * before {@link #loadUIEntries()}, which resolves each entry to one of these bases.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the bases that did
     * assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} and <em>rethrows</em>; a missing base here would resurface as an
     * unresolvable reference while loading the entries, so it is stopped at source.
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadUIEntryBases() throws IOException {
        UIEntryBaseReader reader = new UIEntryBaseReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "ui_entry_base.txt";

        try {
            ParseResult<UIEntryBase> result = reader.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            UIRegistry.setUIEntryBases(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the UI entry renderers from {@code ui_entry_renderer.txt} into {@link UIRegistry}. Must
     * run before {@link #loadUIEntries()}, which resolves each entry to one of these renderers.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the renderers that
     * did assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} and <em>rethrows</em>, for the same reason as the bases above.
     *
     * @throws IOException an error occurred during the parsing - log it and rethrow it
     */
    public static void loadUIEntryRenderers() throws IOException {
        UIEntryRendererReader reader = new UIEntryRendererReader();
        String filename = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath() + "ui_entry_renderer.txt";

        try {
            ParseResult<UIEntryRenderer> result = reader.parseWithResults(filename);

            ErrorParsing.reportAndCheck(filename, result, logger);

            UIRegistry.setUIEntryRenderers(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }
}
