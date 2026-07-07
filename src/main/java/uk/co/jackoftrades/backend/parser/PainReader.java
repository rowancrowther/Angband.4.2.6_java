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
import uk.co.jackoftrades.backend.parser.grammars.pain.PainGrammar;
import uk.co.jackoftrades.backend.parser.grammars.pain.PainLexer;
import uk.co.jackoftrades.backend.parser.pain.PainAssembler;
import uk.co.jackoftrades.backend.parser.pain.PainParseRecord;
import uk.co.jackoftrades.middle.monsters.MonsterPain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/pain.txt} into {@link MonsterPain} objects - the monster "pain
 * message" sets keyed by the index a monster base references. It is the thin, grammar-specific
 * bridge that plugs the generated {@code PainLexer}/{@code PainGrammar} pair into the shared
 * {@link GrammarDriver} pipeline and implements the common {@link Reader} contract, mirroring
 * {@code ObjectBaseReader}. This is the Java port of the C {@code pain.txt} file parser.
 *
 * <p>All the invariant plumbing (ANTLR wiring, error-listener install, assemble call, result
 * wrapping) lives in {@link GrammarDriver}; this class supplies only the four grammar-specific
 * knobs - the lexer/parser constructors, the {@link #extract} step, and a {@link PainAssembler}.
 *
 * @author Rowan Crowther
 */
public class PainReader implements Reader<MonsterPain> {
    /**
     * Logger passed to {@link GrammarDriver#run} so an IO failure is reported under this reader's
     * name.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled items, discarding the soft-error channel.
     * Convenience wrapper over {@link #parseWithResults} for callers that do not need the errors;
     * satisfies the {@link Reader} contract.
     *
     * @param filename the data file to parse
     * @return the assembled {@link MonsterPain} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    @NotNull
    @Override
    public List<MonsterPain> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the given file, returning both the assembled {@link MonsterPain} list and any soft
     * errors gathered along the way. Delegates the whole ritual to {@link GrammarDriver#run},
     * handing it the two generated constructors, this class's {@link #extract} step and a fresh
     * {@link PainAssembler}.
     *
     * @param filename the data file to parse
     * @return a {@link ParseResult} of the assembled sets plus any soft errors
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public ParseResult<MonsterPain> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PainLexer::new,
                PainGrammar::new,
                PainReader::extract,
                new PainAssembler(), logger);
    }

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot perform itself: run the parser's
     * {@code file} entry rule, fail closed on any hard lexer/parser error, validate the
     * {@code record-count} header, and hand back the raw parse records.
     *
     * <p>Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts here rather than reaching the
     * assembler. The header check is soft - {@link GrammarDriver#checkRecordCount} only appends to
     * {@code errors}, so a miscount never discards the records that did parse. The returned list is
     * a defensive copy so downstream mutation cannot reach into the parse tree.
     *
     * @param parser       the generated parser for this file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link PainParseRecord}s
     * @author Rowan Crowther
     */
    private static List<PainParseRecord> extract(
            @NotNull PainGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        PainGrammar.FileContext output = parser.file();
        List<PainParseRecord> results = output.monsterPain;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecords;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }
}
