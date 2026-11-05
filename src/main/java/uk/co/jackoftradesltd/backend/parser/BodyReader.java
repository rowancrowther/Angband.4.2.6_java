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
import uk.co.jackoftradesltd.backend.parser.body.BodyAssembler;
import uk.co.jackoftradesltd.backend.parser.body.BodyParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.body.BodyGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.body.BodyLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.player.PlayerBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code body.txt} into {@link PlayerBody}s by driving {@code BodyLexer} and
 * {@code BodyGrammar}, the hand-written bridge between the generated parser and the game. It
 * stands in for {@code body_parser} in the C original's {@code init.c}: {@code init_parse_body}
 * (the two directives {@code body:} and {@code slot:}, which the grammar's {@code body} and
 * {@code slot} rules take), {@code run_parse_body} (a single call to
 * {@code parse_file_quit_not_found}) and the reading half of {@code finish_parse_body}.
 *
 * <p>The rest of C's work is done elsewhere. {@code parse_body_body} and {@code parse_body_slot}
 * are the grammar and {@link BodyAssembler}: the grammar pairs each {@code body:} line with the
 * {@code slot:} lines after it into a {@link BodyParseRecord}, and the assembler resolves each slot
 * type to an {@link uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum}, where C uses
 * {@code lookup_flag} over its {@code slots} table. The first half of {@code finish_parse_body},
 * the scan for {@code z_info->equip_slots_max}, is {@code PlayerRegistry.setPlayerBodies}, and the
 * second half, which copies each body's linked list of slots into an array, has no counterpart as
 * the port's bodies already hold their slots in a list. {@code cleanup_body} has no counterpart, as
 * the garbage collector frees the bodies. The caller is {@code PlayerDataLoader.loadBodies()},
 * which runs before the races because each race names its body by index.
 *
 * <p>The bodies come back in file order where C's list is newest first, because
 * {@code parse_body_body} pushes each new body on the front. Nothing can tell them apart while
 * {@code body.txt} holds the one {@code Humanoid} body. A second body would be first in C's list,
 * so C and the port would disagree on which one a race's index {@code 0} names.
 *
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a slot type the assembler
 * cannot resolve is thrown as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here, and
 * no bodies are returned. A file that cannot be opened is an {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in one place: a {@code body:} line with no {@code slot:} line
 * after it is a grammar error, where C accepts a body with no slots. It is written up in
 * {@code docs/grammar_issues.md}. The assembler's own differences, including its acceptance of the
 * slot types {@code NONE} and {@code MAX} where C rejects them, are in
 * {@code docs/Assembler_Issues.md}.
 *
 * <p>Class BodyReader coded before 260915, commented in full on 261018, updated on 261105 for the
 * removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class BodyReader implements Reader<PlayerBody> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled bodies in file order, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. Any error throws exactly as it
     * does there.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the body data file to read
     * @return every body in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<PlayerBody> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: the slot type text is
     * resolved to an enum by the assembler, and the body and slot names are copied through.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a {@code slot:} line before any {@code body:}, a {@code body:} with no
     * {@code slot:}, a line the lexer does not know) fails closed before any row is used. The
     * grammar builds each {@link BodyParseRecord} itself, so the rows are only copied into a new
     * list. The rule needs at least one body, where C accepts a file with none.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parse records, one per {@code body:} block, in file order
     */
    private static List<BodyParseRecord> extract(
            @NotNull BodyGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        BodyGrammar.FileContext output = parser.file();
        List<BodyParseRecord> records = output.bodies;
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link PlayerBody}s wrapped in a
     * {@link ParseResult}. This is the Java form of C's {@code run_parse_body} followed by
     * {@code finish_parse_body}, and the entry point {@code PlayerDataLoader.loadBodies()} uses.
     * {@link GrammarDriver#run} does the lex, parse, extract and assemble.
     *
     * <p>As C fails the load on the first error, so does this: a grammar error throws a
     * {@code ParseCancellationException}, a slot type the assembler cannot resolve throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, and no result is
     * returned. A file that cannot be opened is an {@link IOException}, where C's
     * {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the body data file to load
     * @return every body in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<PlayerBody> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                BodyLexer::new,
                BodyGrammar::new,
                BodyReader::extract,
                new BodyAssembler(), logger);
    }
}