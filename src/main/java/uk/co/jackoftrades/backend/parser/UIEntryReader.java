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

package uk.co.jackoftrades.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.uientry.UIEntryGrammar;
import uk.co.jackoftrades.backend.parser.grammars.uientry.UIEntryLexer;
import uk.co.jackoftrades.backend.parser.uientry.UIEntryAssembler;
import uk.co.jackoftrades.backend.parser.uientry.UIEntryParseRecord;
import uk.co.jackoftrades.frontend.entries.UIEntry;

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
 *
 * @author Rowan Crowther
 */
public class UIEntryReader implements Reader<UIEntry> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the file into assembled {@link UIEntry} objects, discarding the
     * accompanying soft-error list (see {@link #parseWithResults} to keep it).
     *
     * @param filename the path of the {@code ui_entry.txt} data file to read.
     * @return the assembled entries, in file order.
     * @throws IOException if the file cannot be read.
     */
    @Override
    public @NotNull List<UIEntry> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the file and return both the assembled {@link UIEntry} objects and
     * the collected soft errors (record-count mismatches and any records the
     * assembler skipped), for callers that need to inspect or report them.
     *
     * @param filename the path of the {@code ui_entry.txt} data file to read.
     * @return the assembled entries paired with the soft-error list.
     * @throws IOException if the file cannot be read.
     */
    public @NotNull ParseResult<UIEntry> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                UIEntryLexer::new,
                UIEntryGrammar::new,
                UIEntryReader::extract,
                new UIEntryAssembler(), logger);
    }

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
}