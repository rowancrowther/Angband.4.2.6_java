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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.world.WorldGrammar;
import uk.co.jackoftrades.backend.parser.grammars.world.WorldLexer;
import uk.co.jackoftrades.backend.parser.world.WorldAssembler;
import uk.co.jackoftrades.backend.parser.world.WorldParseRecord;
import uk.co.jackoftrades.middle.cave.World;

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
public class WorldReader implements Reader<World> {
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
    public List<World> parse(@NotNull String filename) throws IOException {
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
    public ParseResult<World> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                WorldLexer::new,
                WorldGrammar::new,
                WorldReader::extract,
                new WorldAssembler(), logger);
    }

    private static List<WorldParseRecord> extract(
            @NotNull WorldGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        List<WorldParseRecord> records = new ArrayList<>();
        WorldGrammar.FileContext output = parser.file();
        List<List<String>> results = output.levels;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        for (List<String> result : results) {
            records.add(new WorldParseRecord(result.get(0),
                    result.get(1), result.get(2), result.get(3),
                    result.getLast()));
        }
        return records;
    }
}