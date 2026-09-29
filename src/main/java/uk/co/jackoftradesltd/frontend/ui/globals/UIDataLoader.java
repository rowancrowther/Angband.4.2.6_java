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
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.parser.ErrorParsing;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.globals.UIGlobals;
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
 * Unlike the rest of the {@code GameConstants}-driven loaders this was split out of, its three
 * public loaders are invoked from the UI thread — by {@code UILoop}'s {@code EVENT_ENTER_INIT}
 * handler, not by {@code GameConstants.init()} — in dependency order: renderers, then bases (which
 * {@code UIEntryBaseAssembler} folds into {@code TEMPLATE_ONLY} placeholder entries in
 * {@link UIRegistry} as it parses), then the real, file-parsed entries, which
 * {@code UIEntryAssembler} merges into that same list before the finishing pass below runs. In C
 * the same order is set by the {@code pl[]} table in {@code init.c}, where "ui renderers" and
 * "ui entries" precede the player-property and object-property parsers that resolve their
 * {@code bindui} targets against the loaded UI entries.
 *
 * <p>The two private methods, {@link #finalPass(List)} and {@link #fillOutShortened(UIEntry)}, are
 * the Java form of C's {@code finish_parse_ui_entry} and {@code fill_out_shortened}
 * ({@code ui-entry.c}).
 *
 * <p>Class UIDataLoader coded before 260916, commented in full on 260929.
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
     * bases and renderers it references. Corresponds to the second half of C's
     * {@code run_parse_ui_entry} ({@code ui-entry.c}), which parses {@code ui_entry.txt} once the
     * bases are in, followed by {@code finish_parse_ui_entry}, here {@link #finalPass(List)}.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the entries that did
     * assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} rather than {@code IOException} and <em>rethrows</em>, so an unresolvable
     * base or renderer stops the load here rather than leaving the renderer subsystem half-built.
     *
     * <p>Function loadUIEntries coded before 260916, commented in full on 260929.
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

            UIRegistry.setUIEntries(finalPass(result.items()));
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load the UI entry bases from {@code ui_entry_base.txt} into {@link UIRegistry}. Must run
     * before {@link #loadUIEntries()}, which resolves each entry to one of these bases. Corresponds
     * to the first half of C's {@code run_parse_ui_entry} ({@code ui-entry.c}), which parses
     * {@code ui_entry_base.txt} and then ORs {@code ENTRY_FLAG_TEMPLATE_ONLY} into every entry so far;
     * in Java that flagging happens in {@code UIEntryBaseAssembler} as each record is parsed.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the bases that did
     * assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} and <em>rethrows</em>; a missing base here would resurface as an
     * unresolvable reference while loading the entries, so it is stopped at source.
     *
     * <p>Function loadUIEntryBases coded before 260916, commented in full on 260929.
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
     * Corresponds to {@code run_parse_ui_entry_renderer} in {@code ui-entry-renderers.c}, which is
     * just {@code parse_file} on {@code ui_entry_renderer.txt}.
     * <p>
     * Soft errors are reported through {@link ErrorParsing#reportAndCheck} and the renderers that
     * did assemble are registered regardless, per the partial-results contract. The catch is on
     * {@code Exception} and <em>rethrows</em>, for the same reason as the bases above.
     *
     * <p>Function loadUIEntryRenderers coded before 260916, commented in full on 260929.
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
     * Finishing pass applied once, after both {@code ui_entry_base.txt} and {@code ui_entry.txt} have
     * been parsed and folded into one list: fills any entry's still-empty label from its name, fills
     * out its shortened labels (see {@link #fillOutShortened(UIEntry)}), and resolves any still-unset
     * category priority to the entry's own default priority. The Java form of the per-entry loop in
     * {@code finish_parse_ui_entry} ({@code ui-entry.c}), run over the same combined set
     * C's single {@code n_entry} covers there - both the {@code ui_entry_base.txt} placeholders and
     * the real {@code ui_entry.txt} entries - since {@code entries} here is seeded from
     * {@link UIRegistry#getUIEntries()} by {@code UIEntryAssembler} before this is called.
     *
     * <p>Two things C does there are not reproduced. C's {@code hatch_last_embryo} is the assemblers'
     * job here, and C's {@code -1} results (a label that will not convert to wide characters) have no
     * Java counterpart, as a Java string cannot fail that way. An entry whose label is {@code null}
     * rather than empty would throw here, where C's {@code nlabel == 0} covers both; the assemblers
     * always supply {@code ""}.
     *
     * <p>Function finalPass coded before 260919, commented in full on 260929.
     *
     * @param entries the merged entry list to finish, in file order
     * @return a new list holding the same entry objects, each finished in place
     */
    private static List<UIEntry> finalPass(List<UIEntry> entries) {
        List<UIEntry> results = new ArrayList<>();

        for (UIEntry entry : entries) {
            if (entry.getLabel().isEmpty())
                entry.setLabel(entry.getName());

            fillOutShortened(entry);

            for (UIEntryCategory category : entry.getCategories()) {
                if (!category.isPrioritySet()) {
                    category.setPriority(entry.getDefaultPriority());
                    category.setPrioritySet(true);
                }
            }

            results.add(entry);
        }

        return results;
    }

    /**
     * Fills in an entry's shortened labels at any index left unset, working from the nearest longer
     * shortened label already set (or the full label, if none is) - the Java form of
     * {@code fill_out_shortened} ({@code ui-entry.c}). An index counts as "set" when its
     * label string is non-null and non-empty, corresponding to C's {@code nshortened[i] != 0}.
     *
     * <p>The label at index {@code i} is truncated to at most {@code i + 1} characters, C's
     * {@code (n < i + 1) ? n : i + 1}. Indices are visited in ascending order, so a label filled at
     * a lower index is never the source for a higher one; only labels set by the data file, or the
     * full label, are.
     *
     * <p>Function fillOutShortened coded before 260919, commented in full on 260929.
     *
     * @param entry the entry whose shortened labels are filled in place
     */
    private static void fillOutShortened(UIEntry entry) {
        for (int index = 0; index < UIRegistry.MAX_SHORTENED; index++) {
            String source;

            String current = entry.getShortenedLabel(index);
            if (current != null && !current.isEmpty())
                continue;

            int j = index + 1;
            while (true) {
                if (j >= UIRegistry.MAX_SHORTENED) {
                    source = entry.getLabel();
                    break;
                }
                if (entry.getShortenedLabel(j) != null && !entry.getShortenedLabel(j).isEmpty()) {
                    source = entry.getShortenedLabel(j);
                    break;
                }
                j++;
            }
            int strLength = Math.min(source.length(), index + 1);
            entry.setShortenedLabel(index, source.substring(0, strLength));
        }
    }
}
