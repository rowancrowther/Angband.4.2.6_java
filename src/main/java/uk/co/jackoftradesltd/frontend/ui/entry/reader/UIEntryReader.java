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

package uk.co.jackoftradesltd.frontend.ui.entry.reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.frontend.ui.entry.antlr4.uientry.UIEntryGrammar;
import uk.co.jackoftradesltd.frontend.ui.entry.antlr4.uientry.UIEntryLexer;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryAssembler;
import uk.co.jackoftradesltd.frontend.ui.entry.assembler.UIEntryParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/ui_entry.txt} into {@link UIEntry} objects, the thin
 * hand-written bridge between the generated grammar code and the game and the
 * Java port of the equivalent C data-file parser.
 * <p>
 * All the heavy lifting is delegated to {@link GrammarDriver#run}: this class
 * only supplies the three pieces the driver cannot know - the
 * {@link UIEntryLexer}/{@link UIEntryGrammar} constructors, the {@link #extract}
 * step that pulls the parse records off the {@code file} parse tree, and the
 * {@link UIEntryAssembler} that resolves them into domain objects. It implements
 * the shared {@link Reader} contract so callers can treat every data file
 * uniformly.
 * <p>
 * <strong>Relation to the C original.</strong> There is no single C function this class
 * ports. In {@code ui-entry.c} the {@code ui_entry_parser} {@code file_parser} bundles the
 * directive registration ({@code init_parse_ui_entry}, which here is the
 * {@code UIEntryGrammar} itself), the two-file run ({@code run_parse_ui_entry}) and the
 * finishing pass ({@code finish_parse_ui_entry}). This class covers only the second half of
 * {@code run_parse_ui_entry}'s job: reading {@code ui_entry.txt}. The
 * {@code ui_entry_base.txt} read that C performs first is a separate boundary,
 * {@code UIEntryBaseReader}, and the ordering between the two (bases first, then entries)
 * is enforced by {@code UIDataLoader}, not here. The assembler seeds its result list from the
 * entries already in {@code UIRegistry}, so a call to this reader is only meaningful after
 * the base file has been loaded.
 *
 * @author Rowan Crowther
 *
 * <p>Class UIEntryReader coded before 260929, commented in full on 260929.
 */
public class UIEntryReader implements Reader<UIEntry> {
    /**
     * Logger handed to {@link GrammarDriver#run} so an IO failure while reading
     * {@code ui_entry.txt} is logged under this reader's name.
     *
     * <p>Field logger coded before 260929, commented in full on 260929.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Driver callback that runs the {@code file} start rule and pulls the raw
     * {@link UIEntryParseRecord}s off the resulting parse tree.
     * <p>
     * Syntax errors are hard: {@link ParseErrors#throwIfAny()} is called before
     * anything else is returned, so a malformed file aborts the parse rather
     * than yielding a partial list. The declared {@code record-count} header is
     * then validated against the number of records actually parsed - a
     * mismatch is a <em>soft</em> error recorded in {@code errors}, not a
     * failure, so a miscounted-but-otherwise-valid file still loads.
     *
     * @param parser       the parser positioned at the start of the file.
     * @param errorCatcher the collector holding any syntax errors seen so far.
     * @param errors       the soft-error sink for the record-count check.
     * @return a fresh, mutable copy of the parsed records, in file order.
     *
     * <p>Method extract coded before 260929, commented in full on 260929.
     */
    private static List<UIEntryParseRecord> extract(
            @NotNull UIEntryGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        UIEntryGrammar.FileContext output = parser.file();
        List<UIEntryParseRecord> results = output.entries;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount,
                results.size(), errors);

        return new ArrayList<>(results);
    }

    /**
     * Parse the file into assembled {@link UIEntry} objects, discarding the
     * accompanying soft-error list (see {@link #parseWithResults} to keep it).
     *
     * @param filename the path of the {@code ui_entry.txt} data file to read.
     * @return the assembled entries, in file order.
     * @throws IOException if the file cannot be read.
     *
     * <p>Method parse coded before 260929, commented in full on 260929.
     */
    @Override
    public @NotNull List<UIEntry> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the file and return both the assembled {@link UIEntry} objects and
     * the collected soft errors (record-count mismatches and any records the
     * assembler skipped), for callers that need to inspect or report them.
     * <p>
     * A hard syntax error aborts the parse: {@link GrammarDriver#run} then returns an
     * <em>empty</em> item list carrying the collected error messages, mirroring C's
     * {@code parse_file} returning non-zero and {@code run_parse_ui_entry} bailing out
     * without reading further. Callers must therefore treat "empty items plus errors" as a
     * failed load, not as a file with no entries.
     *
     * @param filename the path of the {@code ui_entry.txt} data file to read.
     * @return the assembled entries paired with the soft-error list.
     * @throws IOException if the file cannot be read.
     *
     * <p>Method parseWithResults coded before 260929, commented in full on 260929.
     */
    public @NotNull ParseResult<UIEntry> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                UIEntryLexer::new,
                UIEntryGrammar::new,
                UIEntryReader::extract,
                new UIEntryAssembler(), logger);
    }
}