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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.playerproperty.PlayerPropertyGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.playerproperty.PlayerPropertyLexer;
import uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyAssembler;
import uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code player_property.txt} into {@link PlayerProperty}s (the catalogue of every player flag,
 * object flag and elemental resistance, immunity and vulnerability, each with a display name, a
 * description and any bindings to the user-interface entries of {@code ui_entry.txt}) by driving
 * {@code PlayerPropertyLexer} and {@code PlayerPropertyGrammar}, the hand-written bridge between the
 * generated parser and the game. It stands in for {@code player_property_parser} in the C original's
 * {@code init.c}: {@code init_parse_player_prop} (the six directives {@code type}, {@code code},
 * {@code desc}, {@code name}, {@code value} and {@code bindui}, which the grammar's {@code property}
 * rule takes), {@code run_parse_player_prop} (a single call to {@code parse_file_quit_not_found}) and
 * the list-building half of {@code finish_parse_player_prop}.
 *
 * <p>The rest of C's work is done elsewhere. The {@code parse_player_prop_*} handlers and the
 * expansion in {@code finish_parse_player_prop} are {@link PlayerPropertyAssembler}: it resolves the
 * {@code code} against {@code PlayerFlag} or {@code ObjectFlag}, reads the numbers, and turns each
 * {@code type:element} record into one property per element. C binds each {@code bindui} to its UI
 * entry at that point with {@code bind_player_ability_to_ui_entry_by_name}; the Java property keeps
 * the raw entry name for the UI layer to resolve. {@code cleanup_player_prop} has no counterpart, as
 * the garbage collector frees the properties. The caller is
 * {@code PlayerDataLoader.loadPlayerProperties()}, which stops the load on an unreadable or empty
 * file.
 *
 * <p>The properties come back in file order, which is C's {@code player_abilities} order: each
 * {@code parse_player_prop_type} links its record after the last, and {@code finish_parse_player_prop}
 * copies the records over in that order. An {@code element} record is expanded in place, one property
 * per element in {@code list_element_names} order, so the shipped file's 44 records give 116
 * properties. C adds a blank ability to the list when the file has no records; the Java list holds
 * only real properties.
 *
 * <p>C quits on the first bad line. This reader follows the shared two-channel contract instead: a
 * lexer or grammar error returns no properties and the messages, a {@code code:} the assembler cannot
 * resolve drops that record with a soft error, and a {@code record-count:} mismatch is a soft error
 * that leaves the properties that did parse loaded. The {@code record-count:} header is Java-only, the
 * upstream file has none, and it counts {@code type:} lines. A file that cannot be opened is an
 * {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in several places: the lines of a record must come in the order
 * {@code type}, {@code code}, {@code bindui}, {@code name}, {@code desc}, {@code value}, a record needs
 * a {@code name:}, and a repeated {@code code:}, {@code name:} or {@code value:}, or a {@code desc:}
 * that is not next to the other {@code desc:} lines, is a hard error where C takes the last (or
 * appends). The assembler is looser in others: a {@code code:} on an {@code element} record is
 * accepted, a {@code value:} on a {@code player} or {@code object} record is kept where C drops it,
 * an {@code aux} of {@code 00} or {@code -0} counts as set, and {@code code:MAX} loads for an
 * {@code object} record. They are written up in {@code docs/grammar_issues.md} and
 * {@code docs/Assembler_Issues.md}.
 *
 * <p>Class PlayerPropertyReader coded before 260915, commented in full on 261018.
 *
 * @author Rowan Crowther
 */
public class PlayerPropertyReader implements Reader<PlayerProperty> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled properties in file order, discarding the
     * error messages. The convenience form of {@link #parseWithResults}, which is the one to call
     * when the errors matter: with this one a hard grammar error and a file in which every record
     * was dropped look the same.
     *
     * <p>Function parse coded before 260915, commented in full on 261018.
     *
     * @param filename the {@code player_property.txt} file to read
     * @return the player properties read from the file, empty if the parse failed
     * @throws IOException if the file cannot be opened or read
     */
    @NotNull
    @Override
    @Contract("_ -> _")
    public List<PlayerProperty> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule, fails closed on any hard lexer or grammar error through {@link ParseErrors#throwIfAny()},
     * and soft-checks the {@code record-count:} header against the number of {@code type:} blocks
     * parsed. No value is interpreted here: the type, code, numbers and {@code bindui} fields stay
     * as text for {@link PlayerPropertyAssembler}, and the grammar has already joined the
     * {@code desc:} lines of a record as C's {@code string_append} joins them, with no separator.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a missing {@code record-count:} header, a record with no {@code name:}, a
     * directive out of order, a line the lexer does not know) fails closed before the count check
     * runs against whatever did parse. The rule needs at least one record, where C accepts a file
     * with none. The rows are copied into a new list so the assembler cannot alter the parser's own.
     *
     * <p>Function extract coded before 260915, commented in full on 261018.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @param errors       the soft-error sink: a record-count mismatch is appended here
     * @return the parse records, one per {@code type:} block, in file order
     */
    private static List<PlayerPropertyParseRecord> extract(
            @NotNull PlayerPropertyGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        PlayerPropertyGrammar.FileContext output = parser.file();
        List<PlayerPropertyParseRecord> result = output.properties;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecords;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);
        return new ArrayList<>(result);
    }

    /**
     * Reads {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link PlayerProperty}s and every error message collected on the way. This is the Java form of
     * C's {@code run_parse_player_prop} followed by {@code finish_parse_player_prop}, and the entry
     * point {@code PlayerDataLoader.loadPlayerProperties()} uses so that it can inspect
     * {@link ParseResult#hasErrors()} before registering the result. {@link GrammarDriver#run} does
     * the lex, parse, extract and assemble.
     *
     * <p>Where C quits on a {@code code:} it cannot find, or on a {@code bindui} value that is not
     * a number, the assembler records a soft error and drops the whole record (for the code) or just
     * that one binding (for the value). A {@code type:} other than {@code player}, {@code object} or
     * {@code element} never gets that far: the lexer rejects it as a hard error. A file that cannot
     * be opened is an {@link IOException}, where C's {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018.
     *
     * @param filename the {@code player_property.txt} file to read
     * @return the parse result: the properties in file order plus any error messages
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<PlayerProperty> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PlayerPropertyLexer::new,
                PlayerPropertyGrammar::new,
                PlayerPropertyReader::extract,
                new PlayerPropertyAssembler(),
                logger);
    }
}