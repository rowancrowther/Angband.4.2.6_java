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
import uk.co.jackoftradesltd.backend.parser.grammars.quest.QuestGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.quest.QuestLexer;
import uk.co.jackoftradesltd.backend.parser.quest.QuestAssembler;
import uk.co.jackoftradesltd.backend.parser.quest.QuestParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.player.Quest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code quest.txt} into a list of {@link Quest}s.
 *
 * <p>Like the other list-shaped readers it is a thin front for {@link GrammarDriver}: it supplies
 * the four grammar-specific knobs ({@link QuestLexer}, {@link QuestGrammar}, the {@link #extract}
 * step, and a {@link QuestAssembler}) and lets the shared driver own the ANTLR plumbing, the
 * error-listener install, and the {@link ParseResult} wrapping.
 *
 * <p><b>C counterpart:</b> {@code quests_parser} in {@code player-quest.c}. Its four
 * {@code parse_quest_*} handlers and the numbering done by {@code finish_parse_quest} are performed by
 * {@link QuestAssembler}; {@code run_parse_quest} ({@code parse_file_quit_not_found}) is the file open
 * inside {@link GrammarDriver}, where a missing file is an {@link IOException} for
 * {@code WorldDataLoader.loadQuests} to turn into a fatal error. This class itself only wires the
 * grammar to the driver, so it has no C logic of its own to mismatch.
 *
 * <p><b>Where the grammar is stricter than C</b> (recorded in {@code docs/grammar_issues.md}): C
 * registers four independent one-line directives and so accepts them in any order, missing, repeated,
 * indented or followed by a further {@code :} field, and accepts a file with no quests. {@code QuestGrammar}
 * demands a {@code record-count:} header and then one or more records of exactly {@code name:},
 * {@code level:}, {@code race:}, {@code number:} in that order, and refuses the whole file otherwise.
 * The shipped {@code quest.txt} satisfies all of that. The {@code record-count:} header is a
 * Java-only addition checked by {@link GrammarDriver#checkRecordCount}.
 *
 * <p>Class QuestReader coded before 260915, commented in full on 261019.
 *
 * @author Rowan Crowther
 */
public class QuestReader implements Reader<Quest> {
    /**
     * Logger handed to {@link GrammarDriver#run} so an {@link IOException} on the quest file is logged
     * under this reader's name before it is rethrown. C has no counterpart: it reports through
     * {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse the file and return just the assembled quests, discarding the error list.
     *
     * <p>This is {@link #parseWithResults} with its soft errors thrown away, so a caller that needs to
     * know a quest was dropped (a bad {@code level} or {@code number}, a record-count mismatch) must
     * use that method instead. A hard grammar error gives an empty list here, indistinguishable from an
     * empty file. The C counterpart is the {@code quests_parser} run as a whole
     * ({@code player-quest.c}); C has no partial result.
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
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
     * The one grammar-specific step {@link GrammarDriver} cannot perform itself: run the parser's
     * {@code file} rule and pull the raw records out of its result.
     *
     * <p>Ordering is deliberate - {@link ParseErrors#throwIfAny()} fires straight after the parse so
     * that a hard grammar error (e.g. a missing {@code record-count} header) fails closed before the
     * soft {@link GrammarDriver#checkRecordCount} check runs against whatever did parse. The records
     * come back in file order, which is the order {@code finish_parse_quest} in {@code player-quest.c}
     * gives the quests (index 0 is the first {@code name:} in the file). The grammar's own
     * {@code ArrayList} is copied so the caller owns the result.
     *
     * <p>Function extract coded before 260915, commented in full on 261019.
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

    /**
     * Parse the file and return both the assembled quests and any soft errors gathered along the way
     * (unresolved races, bad integers, a record-count mismatch). The preferred entry point when the
     * caller wants to gate on {@link ParseResult#hasErrors()} - as {@code GameConstants.loadQuests()}
     * does before storing the result. A missing or unreadable file is an {@link IOException}
     * (C: {@code parse_file_quit_not_found} quits); an unresolved {@code race:} or a negative integer
     * is not a soft error but an exception from {@link QuestAssembler}, as C's parse errors abort the
     * load.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
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
}
