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
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.world.Worlds;
import uk.co.jackoftrades.backend.parser.grammars.world.WorldsLexer;
import uk.co.jackoftrades.backend.parser.world.WorldParseRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the data-file lib/gamedata/world.txt entries into the corresponding
 * data objects by driving the matching ANTLR-generated lexer/parser. The
 * thin hand-written bridge between the generated grammar code and the game,
 * implementing the shared {@link Reader} contract (Java port of the
 * equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class WorldReader implements Reader<WorldParseRecord> {
    /**
     * Logger used to report file-loading failures
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     *
     * @author Rowan Crowther
     */
    @NotNull
    @Override
    public List<WorldParseRecord> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Run the parser and generate the ArrayList from the file
     * logging all errors that occur during the run. Once the parse
     * has been complete, change the incoming values to values
     * acceptable to the data format of the stored values
     *
     * @param filename The name of the file to parse
     * @return A {@link ParseResult} of type {@link WorldParseRecord}
     * @throws IOException when there is a problem finding or reading
     *                     the file
     * @author Rowan Crowther
     */
    public ParseResult<WorldParseRecord> parseWithResults(@NotNull String filename) throws IOException {
        ParseResult<WorldParseRecord> worldResult = new ParseResult<>(new ArrayList<>(), new ArrayList<>());
        List<List<String>> records;
        int recCount = 0;

        ParseErrors errorCatcher = null;

        try {
            CharStream stream = CharStreams.fromFileName(filename);
            WorldsLexer lexer = new WorldsLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Worlds parser = new Worlds(tokens);

            // Install the error catcher
            errorCatcher = ParseErrors.install(lexer, parser, filename);

            Worlds.FileContext output = parser.file();

            // throw any caught errors
            errorCatcher.throwIfAny();

            records = output.levels;

            try {
                recCount = Integer.parseInt(output.declaredCount);
            } catch (NumberFormatException e) {
                errorCatcher.add("Invalid number format for declared count");
                recCount = -1;
            }

            if (recCount != -1 && recCount != records.size()) {
                errorCatcher.add("record-count header declares " + recCount +
                        " but file contains " + records.size());
            }

            int line = -1;

            for (List<String> record : records) {
                try {
                    int levelNumber = Integer.parseInt(record.get(0));
                    String levelName = record.get(1);
                    String upLevelName = record.get(2).equals("None")
                            ? null : record.get(2);
                    String downLevelName = record.get(3).equals("None")
                            ? null : record.get(3);
                    line = Integer.parseInt(record.get(4));

                    WorldParseRecord worldParseRecord = new WorldParseRecord(levelNumber,
                            levelName, upLevelName, downLevelName, line);

                    worldResult.items().add(worldParseRecord);

                } catch (NumberFormatException e) {
                    errorCatcher.add("Line: " + record.get(4) + ": Invalid number format found: " +
                            record.get(0));
                }
            }

            errorCatcher.throwIfAny();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        } catch (ParseCancellationException e) {
            return new ParseResult<>(List.of(), errorCatcher != null ? errorCatcher.getErrors() : null);
        }

        return worldResult;
    }
}