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

package uk.co.jackoftradesltd.frontend.ui.globals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.parser.ErrorParsing;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.ui.entrybase.reader.UIEntryBaseReader;
import uk.co.jackoftradesltd.frontend.ui.entry.reader.UIEntryReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Startup loader for the UI slice: parses the UI-entry gamedata files
 * ({@code ui_entry_renderer.txt}, {@code ui_entry_base.txt}, {@code ui_entry.txt}) and populates
 * {@link UIRegistry} through its setters.
 *
 * <p>This is the write side of the UI slice, paired with {@code UIRegistry} (the read side).
 * Unlike the rest of the {@code GameConstants}-driven loaders this was split out of, its four
 * methods are invoked from the UI thread — by {@code UILoop}'s {@code EVENT_ENTER_INIT} handler,
 * not by {@code GameConstants.init()} — in dependency order: renderers, then bases (immediately
 * folded into placeholder entries by {@link #portUIEntryBasesToUIEntries()}), then the real,
 * file-parsed entries, which overwrite those placeholders in {@link UIRegistry}. This must still
 * run before the player- and object-property loaders that resolve their {@code bindui} targets
 * against the loaded UI entries.
 *
 * <p>Class UIDataLoader coded before 260916, commented in full on 260919.
 *
 * @author Rowan Crowther
 */
public class UIDataLoader {
    /**
     * Logger for the soft/hard failures each loader below reports: a soft parse error goes through
     * {@link ErrorParsing#reportAndCheck}, while a hard one (an unresolvable reference or IO
     * failure) is logged here immediately before the catch block rethrows it.
     *
     * <p>Field logger coded before 260916, commented in full on 260919.
     */
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
     * <p>Function loadUIEntries coded before 260916, commented in full on 260919.
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
     * <p>Function loadUIEntryBases coded before 260916, commented in full on 260919.
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
     * <p>Function loadUIEntryRenderers coded before 260916, commented in full on 260919.
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

    /**
     * Converts the loaded {@link UIEntryBase} templates into placeholder {@link UIEntry} records
     * and registers them in {@link UIRegistry}, ahead of {@link #loadUIEntries()} overwriting the
     * list with the real, file-parsed entries.
     * <p>
     * This is the Java form of C's {@code run_parse_ui_entry} ({@code [C] ui-entry.c:2263-2276}),
     * which parses {@code ui_entry_base} directly into the same {@code entries[]} array used for
     * {@code ui_entry.txt}, then OR's {@code ENTRY_FLAG_TEMPLATE_ONLY} onto every entry parsed so
     * far - marking them as templates that {@code initialize_ui_entry_iterator}'s flag check
     * ({@code [C] ui-entry.c:471}) will never surface directly - before going on to parse
     * {@code ui_entry.txt} into the rest of the array. This port keeps the two files behind
     * separate readers instead of one shared array, so each {@link UIEntryBase} is rebuilt here as
     * a standalone {@link UIEntry}: its own resolved flags are unioned with
     * {@link ChannelEntryFlag#ENTRY_FLAG_TEMPLATE_ONLY} (matching C's {@code |=} rather than
     * replacing the set outright), and its categories are rebuilt with an explicitly-unset
     * priority ({@code prioritySet = false}), matching {@code ui_entry_base.txt} never giving a
     * template category one either.
     * <p>
     * The parameter, label and shortened-label fields C's templates never populate are passed
     * through as {@code null}/{@code ""} placeholders. C's {@code parse_entry_template} only ever
     * reads a template's renderer, combiner, default priority, flags and categories when a
     * concrete {@code ui_entry.txt} record pulls it in with {@code template:}
     * ({@code [C] ui-entry.c:1946-1978}), so these placeholder fields are never observed once the
     * template-only entries built here are replaced by {@link #loadUIEntries()}.
     * <p>
     * Must run after {@link #loadUIEntryBases()} and before {@link #loadUIEntries()}.
     *
     * <p>Function portUIEntryBasesToUIEntries coded on 260919, commented in full on 260919.
     */
    public static void portUIEntryBasesToUIEntries() {
        List<UIEntryBase> baseEntries = UIRegistry.getUIEntryBases();
        List<UIEntry> entries = new ArrayList<UIEntry>();

        for (UIEntryBase base : baseEntries) {
            List<UIEntryCategory> categories = new ArrayList<>();
            for (String category : base.getCategories()) {
                categories.add(new UIEntryCategory(category, 1, false));
            }

            Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
            flags.copyFrom(base.getFlags());
            flags.on(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);

            UIEntry entry = new UIEntry(base.getName(), null, null, base.getRenderer(),
                    base.getCombine(), categories, 0, flags,
                    "", "", "", "");
            entries.add(entry);
        }

        UIRegistry.setUIEntries(entries);
    }
}
