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
import uk.co.jackoftradesltd.backend.parser.grammars.playerclass.PlayerClassGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.playerclass.PlayerClassLexer;
import uk.co.jackoftradesltd.backend.parser.playerclass.PlayerClassAssembler;
import uk.co.jackoftradesltd.backend.parser.playerclass.PlayerClassParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.player.PlayerClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code class.txt} into {@link PlayerClass}es by driving {@code PlayerClassLexer} and
 * {@code PlayerClassGrammar}, the hand-written bridge between the generated parser and the game. It
 * stands in for {@code class_parser} in the C original's {@code init.c}: {@code init_parse_class}
 * (every directive of the file, from {@code name} to {@code desc}, which the grammar's
 * {@code playerClass} rule takes), {@code run_parse_class} (a single call to
 * {@code parse_file_quit_not_found}) and the list-building half of {@code finish_parse_class}.
 *
 * <p>The rest of C's work is done elsewhere. The {@code parse_class_*} handlers are
 * {@link PlayerClassAssembler} and the four assemblers under it: it reads each number, resolves the
 * stat, skill and flag names, builds the start items, and for a caster descends magic, books, spells
 * and effects. The book handler's {@code write_book_kind} becomes a new {@code ObjectKind} registered
 * through {@code ObjectRegistry}, so the assembler needs the realms, the object bases, the object
 * kinds and the shapes loaded first. {@code cleanup_class} has no counterpart, as the garbage
 * collector frees the classes. The caller is {@code PlayerDataLoader.loadPlayerClasses()}.
 *
 * <p>The classes come back in file order. C's list is newest first, because
 * {@code parse_class_name} pushes each new class on the front, and {@code finish_parse_class} then
 * numbers {@code cidx} down the list so that the first class in the file is {@code cidx} 0. The
 * port does not store {@code cidx}: a class's position in the list is that index, which is what
 * {@code PlayerClass.getClassFromIndex} reads. A class the assembler drops closes the gap, so every
 * class after it moves up one place, where C would have quit.
 *
 * <p>C quits on the first bad line. This reader follows the shared two-channel contract instead: a
 * lexer or grammar error returns no classes and the messages, and a {@code record-count:} mismatch
 * is a soft error that leaves the classes that did parse loaded. The {@code record-count:} header is
 * Java-only, the upstream file has none, and it counts {@code name:} lines. A file that cannot be
 * opened is an {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in several places: a class with only a {@code name:} line, a
 * {@code magic:} line with no {@code book:} after it, a book with no {@code spell:}, flags separated
 * by a space alone, an empty flag list, and a {@code dice:} with no {@code effect:} before it are
 * hard errors where C accepts them. The assemblers are looser in two: a repeated {@code magic:}
 * replaces the first where C rejects it, and a book keeps more spells than it declared. They are
 * written up in {@code docs/grammar_issues.md} and {@code docs/Assembler_Issues.md}.
 *
 * <p>Class PlayerClassReader coded before 260915, commented in full on 261018.
 *
 * @author Rowan Crowther
 */
public class PlayerClassReader implements Reader<PlayerClass> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled classes in file order, discarding the error
     * messages. The convenience form of {@link #parseWithResults}, which is the one to call when the
     * errors matter: with this one a hard grammar error and a file in which every class was dropped
     * look the same.
     *
     * <p>Function parse coded before 260915, commented in full on 261018.
     *
     * @param filename the class data file to read
     * @return the classes read from the file, empty if the parse failed
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<PlayerClass> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule, fails closed on any hard lexer or grammar error through {@link ParseErrors#throwIfAny()},
     * and soft-checks the {@code record-count:} header against the number of {@code name:} blocks
     * parsed. No value is interpreted here: numbers, flag names, tvals and realm names stay as text
     * for the assemblers, and the grammar has already gathered each class's titles, start items,
     * flags and magic into its {@link PlayerClassParseRecord}.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a missing {@code record-count:} header, a directive before any {@code name:},
     * a {@code name:} with nothing after it, a line the lexer does not know) fails closed before the
     * count check runs against whatever did parse. The rule needs at least one class, where C
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
    private static List<PlayerClassParseRecord> extract(
            @NotNull PlayerClassGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        PlayerClassGrammar.FileContext output = parser.file();
        List<PlayerClassParseRecord> results = output.playerClasses;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }

    /**
     * Reads {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link PlayerClass}es and every error message collected on the way. This is the Java form of
     * C's {@code run_parse_class} followed by {@code finish_parse_class}, and the entry point
     * {@code PlayerDataLoader.loadPlayerClasses()} uses so that it can inspect
     * {@link ParseResult#hasErrors()} before registering the result. {@link GrammarDriver#run} does
     * the lex, parse, extract and assemble.
     *
     * <p>Where C quits on a stat, flag, tval, option or realm it cannot resolve, the assemblers
     * record a soft error and drop the smallest piece they can: the whole class for a bad flag, a
     * bad scalar or more than ten titles, one start item for an unknown tval or sval, and one book
     * for an unknown base or realm. A class whose books are all dropped still loads, with
     * {@code ClassMagic.NONE}. A file that cannot be opened is an {@link IOException}, where C's
     * {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018.
     *
     * @param filename the class data file to read
     * @return the parse result: the classes in file order plus any error messages
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<PlayerClass> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PlayerClassLexer::new,
                PlayerClassGrammar::new,
                PlayerClassReader::extract,
                new PlayerClassAssembler(), logger);
    }
}
