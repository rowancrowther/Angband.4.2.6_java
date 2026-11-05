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
 * {@code trapKindIndex} by position. C also allocates one blank element after the last kind and counts
 * {@code z_info->trap_max} without it; the Java list holds only real kinds and its size is that count,
 * the accepted deviation recorded on {@code TerrainDataLoader.loadTraps()}.
 *
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a value the assembler refuses
 * is thrown as a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is
 * caught here, and no kinds are returned. A file that cannot be opened is an {@link IOException},
 * where C quits.
 *
 * <p>The grammar is stricter than C in several places: a record with only a {@code name:} line, a
 * colon in the second field of {@code name:}, flags separated by a space alone, an empty
 * {@code flags:} line, {@code effect-yx-xtra:}, and a {@code dice:}, {@code dice-xtra:} or
 * {@code expr:} with no effect before it are hard errors where C accepts them. The grammar is looser
 * in one: {@code effect-msg:} is taken where C has no such directive. They are written up in
 * {@code docs/grammar_issues.md} and {@code docs/Assembler_Issues.md}.
 *
 * <p>Class TrapReader coded before 260915, commented in full on 261018, updated on 261105 for the
 * removal of the soft-error path and the {@code record-count:} header, and for the assembler now
 * refusing negative {@code appear:} values and sentinel flags.
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
     * Reads {@code filename} and returns the assembled trap kinds in file order, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. Any error throws exactly as it does
     * there.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the trap data file to read
     * @return every trap kind in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<TrapKind> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: the colour, flags, numbers and
     * dice stay as text for {@link TrapAssembler}, and the grammar has already gathered each
     * record's lines, repeated message lines joined as C's {@code string_append} joins them, into
     * its {@link TrapParseRecord}.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a directive before any {@code name:}, a {@code name:} with nothing after it, a
     * line the lexer does not know) fails closed before any row is used. The rule needs at least
     * one record, where C accepts a file with none. The rows are copied into a new list so the
     * assembler cannot alter the parser's own.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parse records, one per {@code name:} block, in file order
     */
    private static List<TrapParseRecord> extract(
            @NotNull TrapGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        TrapGrammar.FileContext output = parser.file();
        List<TrapParseRecord> results = output.traps;
        errorCatcher.throwIfAny();

        return new ArrayList<>(results);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link TrapKind}s wrapped in a
     * {@link ParseResult}. This is the Java form of C's {@code run_parse_trap} followed by
     * {@code finish_parse_trap}, and the entry point {@code TerrainDataLoader.loadTraps()} uses.
     * {@link GrammarDriver#run} does the lex, parse, extract and assemble.
     *
     * <p>As C quits on a flag or save flag it cannot resolve, or on a bad {@code appear:} number,
     * the assembler throws a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}
     * there; a non-numeric number and a malformed {@code visibility:} are rejected by the lexer and
     * thrown as a {@code ParseCancellationException}. No result is returned for a file that fails.
     * A file that cannot be opened is an {@link IOException}, where C's
     * {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the trap data file to read
     * @return every trap kind in the file, in file order
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
