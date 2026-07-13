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
import uk.co.jackoftrades.backend.parser.grammars.playerrace.PlayerRaceGrammar;
import uk.co.jackoftrades.backend.parser.grammars.playerrace.PlayerRaceLexer;
import uk.co.jackoftrades.backend.parser.playerrace.PlayerRaceAssembler;
import uk.co.jackoftrades.backend.parser.playerrace.PlayerRaceParseRecord;
import uk.co.jackoftrades.middle.player.PlayerRace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link PlayerRace} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class PlayerRaceReader implements Reader<PlayerRace> {
    /**
     * Logger used to report file-loading failures.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<PlayerRace> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    public @NotNull ParseResult<PlayerRace> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PlayerRaceLexer::new,
                PlayerRaceGrammar::new,
                PlayerRaceReader::extract,
                new PlayerRaceAssembler(), logger);
    }

    private static List<PlayerRaceParseRecord> extract(
            @NotNull PlayerRaceGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        PlayerRaceGrammar.FileContext output = parser.file();
        List<PlayerRaceParseRecord> result = output.races;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return new ArrayList<>(result);
    }
}
