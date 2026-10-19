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
import uk.co.jackoftradesltd.backend.parser.grammars.trap.TrapGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.trap.TrapLexer;
import uk.co.jackoftradesltd.backend.parser.trap.TrapAssembler;
import uk.co.jackoftradesltd.backend.parser.trap.TrapParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.cave.TrapKind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code trap.txt} into {@link TrapKind}s (traps, runes, glyphs, decoys, door locks and webs)
 * by driving {@code TrapLexer} and {@code TrapGrammar}, the hand-written bridge between the
 * generated parser and the game. It stands in for {@code trap_parser} in the C original's
 * {@code init.c}: {@code init_parse_trap} (every directive of the file, from {@code name} to
 * {@code msg-xtra}, which the grammar's {@code trapRecord} rule takes), {@code run_parse_trap} (a
 * single call to {@code parse_file_quit_not_found}) and the list-building half of
 * {@code finish_parse_trap}.
 *
 * <p>The rest of C's work is done elsewhere. The {@code parse_trap_*} handlers are
 * {@link TrapAssembler} and {@code EffectAssembler}: they resolve the colour, the {@code TRF_} flags,
 * the {@code OF_} save flags, the {@code visibility:} dice and both effect lists.
 * {@code cleanup_trap} has no counterpart, as the garbage collector frees the kinds. The caller is
 * {@code TerrainDataLoader.loadTraps()}, which also refuses a list whose first kind is not
 * {@code no trap}.
 *
 * <p>The kinds come back in file order, which is C's {@code trap_info} order: {@code parse_trap_name}
 * pushes each record on the front of a list, and {@code finish_parse_trap} copies the list into the
 * array from the back, so the first record in the file is {@code tidx} 0. {@link TrapAssembler} numbers
 * {@code trapKindIndex} by position among the records that assemble, so a record it drops moves every
 * later kind down one place, where C would have quit. C also allocates one blank element after the last
 * kind and counts {@code z_info->trap_max} without it; the Java list holds only real kinds and its size
 * is that count, the accepted deviation recorded on {@code TerrainDataLoader.loadTraps()}.
 *
 * <p>C quits on the first bad line. This reader follows the shared two-channel contract instead: a
 * lexer or grammar error returns no kinds and the messages, and a {@code record-count:} mismatch is a
 * soft error that leaves the kinds that did parse loaded. The {@code record-count:} header is
 * Java-only, the upstream file has none, and it counts {@code name:} lines. A file that cannot be
 * opened is an {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in several places: a record with only a {@code name:} line, a
 * colon in the second field of {@code name:}, flags separated by a space alone, an empty
 * {@code flags:} line, {@code effect-yx-xtra:}, and a {@code dice:}, {@code dice-xtra:} or
 * {@code expr:} with no effect before it are hard errors where C accepts them. The grammar and
 * assembler are looser in others: {@code effect-msg:} is taken where C has no such directive, a
 * negative {@code appear:} value loads, and {@code flags:MAX} and {@code save:MAX} resolve to the
 * enum's sentinel. A kind with no {@code graphics:} or {@code visibility:} line has a {@code null}
 * display character or power where C has zeroes, and a kind with a bad effect line is kept with a
 * {@code null} effect list where C quits. They are written up in {@code docs/grammar_issues.md} and
 * {@code docs/Assembler_Issues.md}.
 *
 * <p>Class TrapReader coded before 260915, commented in full on 261018.
 *
 * @author Rowan Crowther
 */
public class TrapReader implements Reader<TrapKind> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled trap kinds in file order, discarding the error
     * messages. The convenience form of {@link #parseWithResults}, which is the one to call when the
     * errors matter: with this one a hard grammar error and a file in which every record was dropped
     * look the same.
     *
     * <p>Function parse coded before 260915, commented in full on 261018.
     *
     * @param filename the trap data file to read
     * @return the trap kinds read from the file, empty if the parse failed
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<TrapKind> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule, fails closed on any hard lexer or grammar error through {@link ParseErrors#throwIfAny()},
     * and soft-checks the {@code record-count:} header against the number of {@code name:} blocks
     * parsed. No value is interpreted here: the colour, flags, numbers and dice stay as text for
     * {@link TrapAssembler}, and the grammar has already gathered each record's lines, repeated
     * message lines joined as C's {@code string_append} joins them, into its {@link TrapParseRecord}.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a missing {@code record-count:} header, a directive before any {@code name:},
     * a {@code name:} with nothing after it, a line the lexer does not know) fails closed before the
     * count check runs against whatever did parse. The rule needs at least one record, where C
     * accepts a file with none. The rows are copied into a new list so the assembler cannot alter
     * the parser's own.
     *
     * <p>Function extract coded before 260915, commented in full on 261018.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @param errors       the soft-error sink: a record-count mismatch is appended here
     * @return the parse records, one per {@code name:} block, in file order
     */
    private static List<TrapParseRecord> extract(
            @NotNull TrapGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        TrapGrammar.FileContext output = parser.file();
        List<TrapParseRecord> results = output.traps;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }

    /**
     * Reads {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link TrapKind}s and every error message collected on the way. This is the Java form of C's
     * {@code run_parse_trap} followed by {@code finish_parse_trap}, and the entry point
     * {@code TerrainDataLoader.loadTraps()} uses so that it can inspect {@link ParseResult#hasErrors()}
     * before registering the result. {@link GrammarDriver#run} does the lex, parse, extract and
     * assemble.
     *
     * <p>Where C quits on a flag or save flag it cannot resolve, or on an {@code appear:} number too
     * large for an {@code int}, the assembler records a soft error and drops the whole trap. An unknown
     * colour, a non-numeric number and a malformed {@code visibility:} never get that far: the lexer
     * rejects them as hard errors. A bad effect line is the exception to the dropping: the error is
     * recorded but the kind is still built, with a {@code null} effect list. A file that cannot be
     * opened is an {@link IOException}, where C's {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018.
     *
     * @param filename the trap data file to read
     * @return the parse result: the trap kinds in file order plus any error messages
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<TrapKind> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                TrapLexer::new,
                TrapGrammar::new,
                TrapReader::extract,
                new TrapAssembler(), logger);
    }
}
