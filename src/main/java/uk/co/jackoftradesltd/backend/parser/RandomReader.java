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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.backend.parser.random.RandomLexer;
import uk.co.jackoftradesltd.backend.parser.random.RandomParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link Random} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class RandomReader implements Reader<Random> {
    /**
     * Logger used to report file-loading failures.
     *
     * <p>Field logger commented in full on 260915.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses a dice-notation expression (for example {@code "2d6"}) and returns a single-element
     * list holding the resulting {@link Random}. Unlike every other reader in this package,
     * {@code filename} is not a path - it is read directly as the dice text via
     * {@link CharStreams#fromString}, and there is no {@code parseWithResults}/soft-error channel
     * to discard: any failure is a hard {@code IOException} straight out of the grammar.
     *
     * <p>Function parse coded before 260915, commented in full on 260915.
     *
     * @param filename the dice-notation text to parse, despite the name (e.g. {@code "2d6"})
     * @return a single-element list containing the parsed {@link Random}
     * @throws IOException if the text cannot be parsed as a dice expression
     */
    @Override
    public @NotNull List<Random> parse(@NotNull String filename) throws IOException {
        try {
            CharStream stream = CharStreams.fromString(filename);
            RandomLexer lexer = new RandomLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            RandomParser parser = new RandomParser(tokens);
            RandomParser.DiceContext output = parser.dice();

            List<Random> randoms = new ArrayList<>();
            randoms.add(output.random);
            return randoms;
        } catch (Exception e) {
            logger.error("Error while reading random {}", filename, e);
            throw e;
        }
    }
}
