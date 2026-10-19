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
import uk.co.jackoftradesltd.backend.parser.grammars.shape.ShapeGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.shape.ShapeLexer;
import uk.co.jackoftradesltd.backend.parser.shape.ShapeAssembler;
import uk.co.jackoftradesltd.backend.parser.shape.ShapeParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.player.PlayerShape;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code shape.txt} into {@link PlayerShape}s by driving {@code ShapeLexer} and
 * {@code ShapeGrammar}, the hand-written bridge between the generated parser and the game. It
 * stands in for {@code shape_parser} in the C original's {@code init.c}: {@code init_parse_shape}
 * (the directives {@code name}, {@code combat}, the eight {@code skill-*} lines, {@code obj-flags},
 * {@code player-flags}, {@code values}, the effect lines and {@code blow}, which the grammar's
 * {@code shape} rule takes), {@code run_parse_shape} (a single call to
 * {@code parse_file_quit_not_found}) and the list-building half of {@code finish_parse_shape}.
 *
 * <p>The rest of C's work is done elsewhere. The {@code parse_shape_*} handlers live in
 * {@link ShapeAssembler}, which hands the effect lines to {@code EffectAssembler}. C's global
 * {@code shapes} list and its counter {@code z_info->shape_max} have no counterpart in this
 * class: the list this reader returns is installed, and counted, by
 * {@code PlayerDataLoader.loadPlayerShapes} through {@code PlayerRegistry.setPlayerShape}. C's
 * {@code cleanup_shape} is not ported, as the garbage collector frees the shapes.
 *
 * <p>The list comes back in file order. C pushes each new shape onto the head of its list, so its
 * {@code shapes} runs in reverse file order while each shape's {@code sidx} counts up in file
 * order; {@code PlayerRegistry} records that difference and looks shapes up first-match, so two
 * shapes with the same name would resolve differently here. The shipped file has nine shapes with
 * unique names.
 *
 * <p>C quits on the first bad line. This reader follows the shared two-channel contract instead: a
 * lexer or grammar error returns no shapes and the messages, and a shape whose flags, values or
 * effects do not resolve, or a {@code record-count:} mismatch, is a soft error that leaves the
 * other shapes loaded. The {@code record-count:} header is Java-only: the upstream file has none,
 * and the copy in {@code lib/gamedata} adds it. A file that cannot be opened is an
 * {@link IOException}, where C quits.
 *
 * <p>Class ShapeReader coded before 260915, commented in full on 261018.
 *
 * @author Rowan Crowther
 */
public class ShapeReader implements Reader<PlayerShape> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled shapes in file order, discarding the error
     * messages. The convenience form of {@link #parseWithResults}, which is the one to call when
     * the errors matter: with this one a hard grammar error and a file whose every shape was
     * dropped look the same.
     *
     * <p>Function parse coded before 260915, commented in full on 261018.
     *
     * @param filename the data file to read
     * @return the player shapes read from the file, empty if the parse failed
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<PlayerShape> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule, fails closed on any hard lexer or grammar error through {@link ParseErrors#throwIfAny()},
     * and soft-checks the {@code record-count:} header against the number of shape records parsed.
     * No value is interpreted here: the numbers, flag names and {@code values:} tokens are
     * resolved by the assembler, as they are in C's {@code parse_shape_*} handlers.
     *
     * <p>The grammar builds each {@link ShapeParseRecord} itself, so the records are only copied
     * into a new list. A directive that takes one value ({@code combat}, a {@code skill-*} line)
     * keeps its last value when repeated, as C's handlers overwrite, and {@code obj-flags},
     * {@code player-flags}, {@code values}, {@code blow} and each effect block accumulate. The
     * rule requires at least one shape, where C accepts an empty file, and it takes an
     * {@code effect-msg:}, {@code dice:} or {@code expr:} line only after an {@code effect:} line,
     * where C ignores one that comes first.
     *
     * <p>Function extract coded before 260915, commented in full on 261018.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the records are used
     * @param errors       the soft-error sink: a record-count mismatch is appended here
     * @return the raw shape parse records, one per {@code name:} line, in file order
     * @throws IOException if reading fails downstream
     */
    private static List<ShapeParseRecord> extract(
            @NotNull ShapeGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) throws IOException {
        ShapeGrammar.FileContext output = parser.file();
        List<ShapeParseRecord> records = output.shapes;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Reads {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link PlayerShape}s in file order, and every error message collected on the way. This is the
     * Java form of C's {@code run_parse_shape} followed by {@code finish_parse_shape}.
     *
     * <p>There are three outcomes. A clean file gives nine shapes and no errors for the shipped
     * data. A lexer or grammar error gives no shapes and the parser's messages. A shape whose
     * flags, values or effects do not resolve is dropped with a message beside the shapes that
     * survived, where C raises a parse error and quits. A file that cannot be opened is an
     * {@link IOException}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018.
     *
     * @param filename the shape data file to read
     * @return the parse result: the shapes plus any error messages
     * @throws IOException if the file cannot be opened or read
     */
    public @NotNull ParseResult<PlayerShape> parseWithResults(String filename) throws IOException {
        return GrammarDriver.run(filename,
                ShapeLexer::new,
                ShapeGrammar::new,
                ShapeReader::extract,
                new ShapeAssembler(), logger);
    }
}
