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
import uk.co.jackoftrades.backend.parser.grammars.quest.QuestGrammar;
import uk.co.jackoftrades.backend.parser.grammars.quest.QuestLexer;
import uk.co.jackoftrades.backend.parser.quest.QuestAssembler;
import uk.co.jackoftrades.backend.parser.quest.QuestParseRecord;
import uk.co.jackoftrades.middle.player.Quest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code quest.txt} into a list of {@link Quest}s.
 *
 * <p>Like the other list-shaped readers it is a thin front for {@link GrammarDriver}: it supplies
 * the four grammar-specific knobs ({@link QuestLexer}, {@link QuestGrammar}, the {@link #extract}
 * step, and a {@link QuestAssembler}) and lets the shared driver own the ANTLR plumbing, the
 * error-listener install, and the {@link ParseResult} wrapping. Ports the load side of C's
 * {@code quests_parser} ({@code player-quest.c}).
 *
 * @author Rowan Crowther
 */
public class QuestReader implements Reader<Quest> {
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse the file and return just the assembled quests, discarding the error list.
     *
     * @param filename the quest data file to read
     * @return the quests that assembled successfully
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Quest> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the file and return both the assembled quests and any soft errors gathered along the way
     * (unresolved races, bad integers, a record-count mismatch). The preferred entry point when the
     * caller wants to gate on {@link ParseResult#hasErrors()} - as {@code GameConstants.loadQuests()}
     * does before storing the result.
     *
     * @param filename the quest data file to read
     * @return the assembled quests plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<Quest> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                QuestLexer::new,
                QuestGrammar::new,
                QuestReader::extract,
                new QuestAssembler(), logger);
    }

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot perform itself: run the parser's
     * {@code file} rule and pull the raw records out of its result.
     *
     * <p>Ordering is deliberate - {@link ParseErrors#throwIfAny()} fires straight after the parse so
     * that a hard grammar error (e.g. a missing {@code record-count} header) fails closed before the
     * soft {@link GrammarDriver#checkRecordCount} check runs against whatever did parse.
     *
     * @param parser       the parser positioned at the start of the file
     * @param errorCatcher the shared collector for hard grammar/lexer errors
     * @param errors       the soft-error sink, passed on to the record-count check
     * @return the raw parse records, ready for the assembler
     */
    private static List<QuestParseRecord> extract(
            @NotNull QuestGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        QuestGrammar.FileContext output = parser.file();
        List<QuestParseRecord> records = output.quests;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }
}
