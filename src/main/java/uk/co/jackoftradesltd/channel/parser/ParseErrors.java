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

package uk.co.jackoftradesltd.channel.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shared ANTLR error listener that collects every error raised while one game data file is
 * lexed, parsed and checked, and turns them into a single fatal failure at the end.
 *
 * <p>This is the Java side of the error path in {@code datafile.c}: function
 * {@code parse_file()} feeds the file to {@code parser_parse()} a line at a time and stops at the
 * first line that returns an error; {@code run_parser()} then calls {@code print_error()}, which
 * shows {@code "Parse error in <file> line <n> column <c>: ..."} and quits. Here the grammar has
 * already parsed the whole file by the time anyone looks, so every syntax error the lexer or
 * parser reports, and every semantic error a reader adds through {@link #add(int, String)} or
 * {@link #add(String)}, is kept in order. The reader then calls {@link #throwIfAny()}, which logs
 * each one and throws, so a bad file is still fatal to the load, as it is in C.
 *
 * <p>Deliberate differences from C:
 * <ul>
 *   <li>C reports only the first error in a file; this class reports all of them;</li>
 *   <li>C's "column" is the colon-separated field number the parser had reached
 *       ({@code parser.c}, {@code colno}, counting from 1); a syntax error here carries ANTLR's
 *       character offset within the line, counting from 0, and an {@code add} error carries no
 *       column at all;</li>
 *   <li>C quits the program from inside {@code print_error()}; this class throws a
 *       {@link ParseCancellationException}, which nothing on the load path catches until
 *       {@code GameConstants.init()}, so the load still stops. Neither {@link GrammarDriver}
 *       nor the loaders turn it into an empty result.</li>
 * </ul>
 *
 * <p>Class ParseErrors coded before 261104, commented in full on 261104, last bullet updated on
 * 261104 for the removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public final class ParseErrors extends BaseErrorListener {
    /**
     * Log4j logger for this class. {@link #throwIfAny()} writes each collected error to it at
     * {@code ERROR} level just before throwing, so the details reach the error log rather than
     * the exception message. It stands in for the {@code msg()} call in C's
     * {@code print_error()} ({@code datafile.c}).
     *
     * <p>Field logger coded before 261104, commented in full on 261104.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The formatted error lines collected so far, in the order they were reported. Each one
     * starts with {@code "file: "} and {@link #sourceName}. {@link #syntaxError},
     * {@link #add(int, String)} and {@link #add(String)} write to it;
     * {@link #throwIfAny()}, {@link #hasErrors()} and {@link #getErrors()} read it. It is never
     * cleared, so one instance serves one file.
     *
     * <p>Field errors coded on 261104, commented in full on 261104.
     */
    private final List<String> errors = new ArrayList<>();

    /**
     * The name of the data file being parsed, as given to {@link #install}. Every collected error
     * starts with it, as {@code fp->name} starts every message in C's {@code print_error()}
     * ({@code datafile.c}).
     *
     * <p>Field sourceName coded before 261104, commented in full on 261104.
     */
    private final String sourceName;

    /**
     * Creates an empty collector for one file. It is private so that every instance comes
     * through {@link #install}, which also wires it onto the lexer and parser; a collector that
     * was never installed would never hear a syntax error.
     *
     * <p>Constructor ParseErrors coded before 261104, commented in full on 261104.
     *
     * @param sourceName the name of the data file being parsed, used as the prefix of every
     *                   error
     */
    private ParseErrors(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * Creates a collector for {@code filename} and makes it the only error listener on both
     * {@code lexer} and {@code parser}. Removing the existing listeners first takes off ANTLR's
     * default {@code ConsoleErrorListener}, so errors are no longer printed to stderr and go only
     * here. Lexer and parser share the one collector, so all errors for the file end up in one
     * list, in the order they were raised. The parser's error strategy is left alone, so the
     * parser still recovers and goes on to find further errors.
     *
     * <p>Function install coded before 261104, commented in full on 261104.
     *
     * @param lexer    the ANTLR lexer for the file
     * @param parser   the ANTLR parser reading that lexer's tokens
     * @param filename the name of the data file, used as the prefix of every error
     * @return the installed collector, for the reader to call {@link #add(int, String)} and
     *         {@link #throwIfAny()} on
     */
    public static ParseErrors install(Lexer lexer, Parser parser, String filename) {
        ParseErrors collector = new ParseErrors(filename);

        parser.removeErrorListeners();
        lexer.removeErrorListeners();

        parser.addErrorListener(collector);
        lexer.addErrorListener(collector);

        return collector;
    }

    /**
     * Records a lexer or parser syntax error as
     * {@code "file: <sourceName> line <line>:<charPositionInLine> <msg>"} and returns without
     * throwing, so ANTLR's recovery carries on and later errors in the same file are also
     * collected. C instead stops at the first line {@code parser_parse()} rejects
     * ({@code datafile.c}, function {@code parse_file()}); see the class comment.
     *
     * <p>{@code line} counts from 1, as C's line number does. {@code charPositionInLine} is a
     * character offset counting from 0, not C's field number.
     *
     * <p>Function syntaxError coded on 261104, commented in full on 261104.
     *
     * @param recognizer         the lexer or parser that found the error; not used
     * @param offendingSymbol    the token at fault, or {@code null} when the lexer reports;
     *                           not used
     * @param line               the line of the file where the error occurred, counting from 1
     * @param charPositionInLine the character offset within that line, counting from 0
     * @param msg                ANTLR's description of the error
     * @param e                  the exception behind the error, or {@code null} if the parser
     *                           recovered within the rule; not used
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        errors.add("file: " + sourceName + " line " + line + ":" + charPositionInLine + " " + msg);
    }

    /**
     * Makes the collected errors fatal. If none have been collected it returns and does nothing.
     * Otherwise it logs each error at {@code ERROR} level, in the order collected, and then
     * throws. The exception message is a fixed pointer to the error log; it doesn't include the
     * errors. This is the Java version of the {@code print_error()} call in {@code datafile.c},
     * function {@code run_parser()}, except that C reports one error and quits rather than
     * throwing.
     *
     * <p>It doesn't clear the list, so a second call after a throw throws again. Readers call it
     * straight after the parse, so that grammar errors stop the load before any records are
     * assembled.
     *
     * <p>Function throwIfAny coded on 261104, commented in full on 261104.
     *
     * @throws ParseCancellationException if at least one error has been collected
     */
    public void throwIfAny() {
        if (errors.isEmpty())
            return;

        for (String error : errors) {
            logger.error(error);
        }

        throw new ParseCancellationException("Errors occurred during parsing, check Error logfile for details.");
    }

    /**
     * Records an error found after the parse, such as a reader's semantic check, against a line
     * of the file, as {@code "file: <sourceName> line <line> <msg>"}. Unlike
     * {@link #syntaxError}, the entry has no column. Nothing is thrown until
     * {@link #throwIfAny()}.
     *
     * <p>Function add coded on 261104, commented in full on 261104.
     *
     * @param line the line of the file where the error was found, counting from 1
     * @param msg  the error message
     */
    public void add(int line, String msg) {
        errors.add("file: " + sourceName + " line " + line + " " + msg);
    }

    /**
     * Records an error that belongs to the file as a whole rather than to one line, as
     * {@code "file: <sourceName> <msg>"}. Nothing is thrown until {@link #throwIfAny()}. Its one
     * planned caller, the {@code record-count:} check, was removed on 261104, so nothing calls it
     * at present.
     *
     * <p>Function add coded on 261104, commented in full on 261104, example replaced on 261104 once
     * the {@code record-count:} header was removed.
     *
     * @param msg the error message
     */
    public void add(String msg) {
        errors.add("file: " + sourceName + " " + msg);
    }

    /**
     * Reports whether any error has been collected, either from {@link #syntaxError} or from
     * {@code add}. It lets a caller check without the throw that {@link #throwIfAny()} would
     * cause.
     *
     * <p>Function hasErrors coded on 261104, commented in full on 261104.
     *
     * @return {@code true} if at least one error has been collected, {@code false} otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Returns the collected errors, in order, as a read-only view. It is a live view, not a copy:
     * errors added later show up in a list already returned. Any attempt to change it throws
     * {@link UnsupportedOperationException}.
     *
     * <p>Function getErrors coded on 261104, commented in full on 261104.
     *
     * @return an unmodifiable, live view of the collected error lines
     */
    @CheckReturnValue
    @Contract(pure = true)
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}