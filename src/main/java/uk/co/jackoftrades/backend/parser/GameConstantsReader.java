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

package uk.co.jackoftrades.backend.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsAssembler;
import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.gameconstants.GameConstantsGrammar;
import uk.co.jackoftrades.backend.parser.grammars.gameconstants.GameConstantsLexer;
import uk.co.jackoftrades.middle.game.globals.GameConstantsData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to read in <code>constants.txt</code> file and pass back to the <code>GameConstants</code> object.
 *
 * @author Rowan Crowther
 */
public class GameConstantsReader {
    /**
     * Logger used to report file-loading failures
     */
    private static final Logger logger = LogManager.getLogger();

    public GameConstantsParseResult parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename);
    }

    /**
     * Dispatch a record to the builder passing through the current list of errors
     * @param filename The name of the file to parse
     *
     * @author Rowan Crowther
     */
    @CheckReturnValue
    public GameConstantsParseResult parseWithResults(@NotNull String filename) throws IOException {
        GameConstantsData data = null;
        ParseErrors errorCatcher = null;
        List<String> errors = new ArrayList<>();

        try {
            CharStream input = CharStreams.fromFileName(filename);
            GameConstantsLexer lexer = new GameConstantsLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GameConstantsGrammar parser = new GameConstantsGrammar(tokens);

            errorCatcher = ParseErrors.install(lexer, parser, filename);

            GameConstantsGrammar.FileContext output = parser.file();

            errorCatcher.throwIfAny();

            List<GameConstantsParseRecord> records = output.results;

            data = new GameConstantsAssembler().assemble(records, errors);

            errorCatcher.throwIfAny();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        } catch (ParseCancellationException e) {
            logger.error("Error while parsing file {}", filename, e);
            return new GameConstantsParseResult(null, errorCatcher.getErrors());
        }

        return new GameConstantsParseResult(data, errors);
    }
}