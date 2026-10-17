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
import uk.co.jackoftradesltd.backend.parser.grammars.monsterbase.MonsterBaseGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.monsterbase.MonsterBaseLexer;
import uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseAssembler;
import uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/monster_base.txt} into {@link MonsterBase} objects - the templates that
 * groups of monsters share: a default display glyph, the race flags every member inherits, a
 * pain-message set and a lore description. {@code monster.txt}, {@code slay.txt}, {@code pit.txt}
 * and {@code summon.txt} refer to a template by its {@code name:}. It is the thin hand-written
 * bridge between the generated {@code MonsterBaseLexer} / {@code MonsterBaseGrammar} pair and the
 * game, plugged into the shared {@link GrammarDriver} pipeline and implementing the common
 * {@link Reader} contract.
 * <p>
 * It stands in for {@code mon_base_parser} in {@code mon-init.c}:
 * <ul>
 *   <li>{@code init_parse_mon_base} and the {@code parse_mon_base_*} handlers become the grammar plus
 *       {@link MonsterBaseAssembler}: the grammar collects each record's raw text, and the assembler
 *       turns it into a {@link MonsterBase} - each flag code to a {@code MonsterRaceFlag} (via the
 *       {@code RF_} prefix), the glyph to a {@code char}, and the {@code pain:} serial to the
 *       {@code MonsterPain} that {@code pain.txt} loaded under that serial;</li>
 *   <li>{@code run_parse_mon_base} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_mon_base}, which sets {@code rb_info} to the head of the chain, becomes
 *       the list handed back, one {@link MonsterBase} per {@code name:} block;
 *       {@code MonsterDataLoader.loadMonsterBases} hands it to {@code MonsterRegistry}, where
 *       {@code lookupMonsterBase} does the name search that {@code lookup_monster_base} in
 *       {@code mon-util.c} does down the chain;</li>
 *   <li>{@code cleanup_mon_base} has no counterpart; the garbage collector frees the bases.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>Load order.</strong> The pain sets must already be in {@code MonsterRegistry}, because the
 * assembler resolves {@code pain:} through {@code lookupMonsterPain}; C has the same dependency, as
 * {@code parse_mon_base_pain} reads {@code pain_messages} and {@code z_info->mp_max}.
 * <p>
 * <strong>List order.</strong> The bases come back in file order. C's {@code parse_mon_base_name}
 * pushes each record on the head of a chain ({@code rb->next = h}) and {@code finish_parse_mon_base}
 * keeps the chain as it is, so {@code rb_info} starts with the <em>last</em> record in the file
 * ({@code Morgoth}) and ends with the first. The shipped file names every base once, so the order
 * cannot show through {@code lookup_monster_base}; with a name used twice, C finds the later record
 * and a first-match search over this list finds the earlier. The order is set by
 * {@link MonsterBaseAssembler}; see {@code docs/Assembler_Issues.md}.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition; the upstream
 *       {@code monster_base.txt} has no such line, and this repository's copy adds
 *       {@code record-count:56}. A wrong count is a soft error and the records still load, but a
 *       missing header is a syntax error and loads nothing.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (an unknown race flag, a glyph that is not one {@code char}, a {@code pain:}
 *       no loaded set has) skips only the offending record and the rest still load.</li>
 *   <li>C takes the directives in any order, with {@code flags:} optional and written as a list
 *       split on spaces and bars. The grammar needs {@code name:}, {@code glyph:}, {@code pain:},
 *       any number of {@code flags:} lines, then {@code desc:}, in that order, and each flag list
 *       must be {@code CODE | CODE}; see {@code docs/grammar_issues.md}.</li>
 *   <li>Several {@code desc:} lines are joined end to end with no separator, as
 *       {@code parse_mon_base_desc} does with {@code string_append}, and several {@code flags:}
 *       lines accumulate into one set.</li>
 *   <li>The pain sets are found by serial, not by position, and a serial no loaded set has is an
 *       error. C accepts any serial below {@code mp_max}, including the blank slot 0; that blank
 *       slot is the accepted deviation recorded in the registry's class block.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class MonsterBaseReader coded before 260915, commented in full on 261017.
 */
public class MonsterBaseReader implements Reader<MonsterBase> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code monster_base.txt} is logged under this reader's name before it is rethrown. Syntax and
     * soft errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled monster bases, in file order (see the
     * class block for how that differs from C's chain), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean load are told apart only by the
     * list being empty, and a record skipped for an unknown race flag, a bad glyph or an unknown
     * pain set just goes missing. Callers that must tell a partial or failed load from a clean one
     * have to use {@link #parseWithResults}, as {@code MonsterDataLoader.loadMonsterBases} does.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the assembled {@link MonsterBase} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @NotNull
    @Override
    public List<MonsterBase> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link MonsterBaseParseRecord}s, still holding unconverted text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a directive before any
     * {@code name:} (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}) and a {@code glyph:} longer
     * than one character (C's {@code PARSE_ERROR_FIELD_TOO_LONG}). The header check is soft -
     * {@link GrammarDriver#checkRecordCount} only appends to {@code errors}, so a miscount never
     * discards the records that did parse.
     * <p>
     * The records stay in file order. The copy at the end detaches the result from the parse tree,
     * so the returned list is the caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the records are used
     * @param errors       the soft-error sink, appended to on a record-count mismatch
     * @return the parsed {@link MonsterBaseParseRecord}s in file order
     */
    private static List<MonsterBaseParseRecord> extract(
            @NotNull MonsterBaseGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        MonsterBaseGrammar.FileContext output = parser.file();
        List<MonsterBaseParseRecord> records = output.bases;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Parse the given file, returning both the assembled {@link MonsterBase} list (in file order)
     * and any soft errors gathered along the way, for callers that need to tell a clean load from a
     * partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_mon_base} and the {@code parse_mon_base_*}
     * handlers in {@code mon-init.c}: it wires {@code MonsterBaseLexer}, {@code MonsterBaseGrammar},
     * {@link #extract} and {@link MonsterBaseAssembler} into {@link GrammarDriver#run}, so the
     * reader stays a thin wiring layer. A syntax error returns no items and the collected messages;
     * an unknown race flag, a bad glyph, an unknown pain set or a {@code record-count:} mismatch
     * leaves the good records in the result alongside the messages, so the list can be short
     * without anything having been thrown. C would have stopped at the first of any of these. An
     * unreadable file is not a result at all but an {@link IOException}, the analogue of
     * {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code MonsterDataLoader.loadMonsterBases} gates on
     * the result's errors for exactly that reason. {@code pain.txt} must have been loaded first.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the monster-base data file to read
     * @return a {@link ParseResult} of the assembled bases plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<MonsterBase> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                MonsterBaseLexer::new,
                MonsterBaseGrammar::new,
                MonsterBaseReader::extract,
                new MonsterBaseAssembler(), logger);
    }
}
