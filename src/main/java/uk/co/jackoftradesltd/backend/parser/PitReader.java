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

package uk.co.jackoftradesltd.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.pit.PitGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.pit.PitLexer;
import uk.co.jackoftradesltd.backend.parser.pit.PitAssembler;
import uk.co.jackoftradesltd.backend.parser.pit.PitParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.cave.PitProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/pit.txt} into {@link PitProfile} objects - the recipes for themed monster
 * pits and nests: which monster bases, colours, race flags and spells the occupants must or must not
 * have, how deep and rare the room is, and how thickly it is strewn with objects. It is the thin
 * hand-written bridge between the generated {@code PitLexer} / {@code PitGrammar} pair and the game,
 * plugged into the shared {@link GrammarDriver} pipeline and implementing the common {@link Reader}
 * contract.
 * <p>
 * It stands in for {@code pit_parser} in {@code mon-init.c}:
 * <ul>
 *   <li>{@code init_parse_pit} and the {@code parse_pit_*} handlers become the grammar plus
 *       {@link PitAssembler}: the grammar collects each record's raw text, and the assembler turns it
 *       into a {@link PitProfile} - room digit to {@code PitRoomType}, {@code innate-freq:} to
 *       {@code 100 / pct}, flag, spell and colour codes to their enums, {@code mon-base:} to a
 *       {@code MonsterBase} and {@code mon-ban:} to a {@code MonsterRace};</li>
 *   <li>{@code run_parse_pit} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_pit} becomes the list handed back, one {@link PitProfile} per
 *       {@code name:} block. Copying the chain into the {@code pit_info} array, numbering
 *       {@code pit_idx}, linking the {@code next} pointers and setting {@code z_info->pit_max} is not
 *       done here; {@code MonsterDataLoader.loadPitProfiles} hands the list to {@code MonsterRegistry}.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> The profiles come back in file order, which is C's {@code pit_info}
 * order: {@code parse_pit_name} pushes each record on the head of a chain, but
 * {@code finish_parse_pit} fills {@code pit_info} downward from the end, so slot 0 is the first
 * record in the file. The lists inside a profile (bases, colours, forbidden monsters) are in file
 * order here, where C pushes each entry on the head of its list and so holds them reversed. Nothing
 * can tell the two apart: {@code mon_pit_hook} in {@code gen-room.c} only asks whether a race matches
 * some entry.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition; the upstream {@code pit.txt} has no
 *       such line, and this repository's copy adds {@code record-count:40}. A wrong count is a soft
 *       error and the records still load, but a missing header is a syntax error and loads nothing.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (an unknown flag, spell, monster base or monster name, or an
 *       {@code innate-freq:} outside 1 to 100) skips only the offending record and the rest still
 *       load. C silently keeps an unresolved {@code mon-ban:} name as a null race; see
 *       {@code docs/Assembler_Issues.md}.</li>
 *   <li>C takes the directives in any order, and so does the grammar, but the grammar is stricter in
 *       the value shapes it lexes and in demanding at least one record with at least one directive
 *       after each {@code name:}; see {@code docs/grammar_issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot after the last. This is the
 *       accepted deviation recorded in the registry's class block, so {@code pit_max} is the plain
 *       record count, one lower than C's.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class PitReader coded before 260915, commented in full on 261017.
 */
public class PitReader implements Reader<PitProfile> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code pit.txt} is logged under this reader's name before it is rethrown. Syntax and soft
     * errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled pit profiles, in file order (which is
     * C's {@code pit_info} order, see the class block), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean load are told apart only by the
     * list being empty, and a record skipped for an unknown flag, spell, monster base or monster
     * name just goes missing. Callers that must tell a partial or failed load from a clean one have
     * to use {@link #parseWithResults}, as {@code MonsterDataLoader.loadPitProfiles} does.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the assembled {@link PitProfile} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<PitProfile> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link PitParseRecord}s, still holding unconverted text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a directive before any
     * {@code name:} (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}), an {@code alloc:} line with
     * one number and a lower-case flag code. The header check is soft -
     * {@link GrammarDriver#checkRecordCount} only appends to {@code errors}, so a miscount never
     * discards the records that did parse.
     * <p>
     * The records stay in file order. The copy at the end detaches the result from the parse tree,
     * so the returned list is the caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated parser for this file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink (record-count mismatches are appended here)
     * @return the parsed {@link PitParseRecord}s in file order
     */
    private static List<PitParseRecord> extract(@NotNull PitGrammar parser,
                                                @NotNull ParseErrors errorCatcher,
                                                @NotNull List<String> errors) {
        PitGrammar.FileContext output = parser.file();
        List<PitParseRecord> pits = output.pits;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, pits.size(), errors);

        return new ArrayList<>(pits);
    }

    /**
     * Parse the given file, returning both the assembled {@link PitProfile} list (in file order,
     * which is C's {@code pit_info} order) and any soft errors gathered along the way, for callers
     * that need to tell a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_pit} and the {@code parse_pit_*} handlers in
     * {@code mon-init.c}: it wires {@code PitLexer}, {@code PitGrammar}, {@link #extract} and
     * {@link PitAssembler} into {@link GrammarDriver#run}, so the reader stays a thin wiring layer.
     * A syntax error returns no items and the collected messages; an unresolved name, an unknown
     * code or a {@code record-count:} mismatch leaves the good records in the result alongside the
     * messages, so the list can be short without anything having been thrown. C would have stopped
     * at the first of any of these. An unreadable file is not a result at all but an
     * {@link IOException}, the analogue of {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code MonsterDataLoader.loadPitProfiles} gates on
     * the result's errors for exactly that reason.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to parse
     * @return a {@link ParseResult} of the assembled profiles plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<PitProfile> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PitLexer::new,
                PitGrammar::new,
                PitReader::extract,
                new PitAssembler(), logger);
    }
}
