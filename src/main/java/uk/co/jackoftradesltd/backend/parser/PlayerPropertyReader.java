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
 * {@code PlayerDataLoader.loadPlayerProperties()}, which stops the load on an unreadable file.
 *
 * <p>The properties come back in file order, which is C's {@code player_abilities} order: each
 * {@code parse_player_prop_type} links its record after the last, and {@code finish_parse_player_prop}
 * copies the records over in that order. An {@code element} record is expanded in place, one property
 * per element in {@code list_element_names} order, so the shipped file's 44 records give 116
 * properties. C adds a blank ability to the list when the file has no records; the Java list holds
 * only real properties.
 *
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a {@code code:} or
 * {@code bindui:} value the assembler cannot resolve is thrown as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here, and
 * no properties are returned. A file that cannot be opened is an {@link IOException}, where C
 * quits.
 *
 * <p>The grammar is stricter than C in several places: the lines of a record must come in the order
 * {@code type}, {@code code}, {@code bindui}, {@code name}, {@code desc}, {@code value}, a record needs
 * a {@code name:}, and a repeated {@code code:}, {@code name:} or {@code value:}, or a {@code desc:}
 * that is not next to the other {@code desc:} lines, is a hard error where C takes the last (or
 * appends). The assembler is looser in one: a {@code value:} on a {@code player} or {@code object}
 * record is kept where C drops it. They are written up in {@code docs/grammar_issues.md} and
 * {@code docs/Assembler_Issues.md}.
 *
 * <p>Class PlayerPropertyReader coded before 260915, commented in full on 261018, updated on 261105
 * for the removal of the soft-error path and the {@code record-count:} header, and for the
 * assembler now refusing a sentinel {@code code:} and reading {@code aux} as C does.
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
     * Reads {@code filename} and returns the assembled properties in file order, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. Any error throws exactly as it does
     * there.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the {@code player_property.txt} file to read
     * @return every player property in the file, in file order
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
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: the type, code, numbers and
     * {@code bindui} fields stay as text for {@link PlayerPropertyAssembler}, and the grammar has
     * already joined the {@code desc:} lines of a record as C's {@code string_append} joins them,
     * with no separator.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a record with no {@code name:}, a directive out of order, a line the lexer does
     * not know) fails closed before any row is used. The rule needs at least one record, where C
     * accepts a file with none. The rows are copied into a new list so the assembler cannot alter
     * the parser's own.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parse records, one per {@code type:} block, in file order
     */
    private static List<PlayerPropertyParseRecord> extract(
            @NotNull PlayerPropertyGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        PlayerPropertyGrammar.FileContext output = parser.file();
        List<PlayerPropertyParseRecord> result = output.properties;
        errorCatcher.throwIfAny();
        
        return new ArrayList<>(result);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link PlayerProperty}s wrapped in a
     * {@link ParseResult}. This is the Java form of C's {@code run_parse_player_prop} followed by
     * {@code finish_parse_player_prop}, and the entry point
     * {@code PlayerDataLoader.loadPlayerProperties()} uses. {@link GrammarDriver#run} does the lex,
     * parse, extract and assemble.
     *
     * <p>As C quits on a {@code code:} it cannot find, or on a {@code bindui} value that is not a
     * number, the assembler throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException} on either, and no result
     * is returned. A {@code type:} other than {@code player}, {@code object} or {@code element}
     * never gets that far: the lexer rejects it, and it is thrown as a
     * {@code ParseCancellationException}. A file that cannot be opened is an {@link IOException},
     * where C's {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the {@code player_property.txt} file to read
     * @return every player property in the file, in file order
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