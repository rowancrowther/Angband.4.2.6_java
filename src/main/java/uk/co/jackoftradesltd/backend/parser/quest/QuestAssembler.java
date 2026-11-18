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

package uk.co.jackoftradesltd.backend.parser.quest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.player.Quest;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link QuestParseRecord}s from {@code quest.txt} into domain {@link Quest}s.
 *
 * <p>This is the "assemble" half of the reader pipeline: it does the interpretation the grammar
 * deliberately skipped - parsing the integer fields and resolving each {@code race:} name to a real
 * {@link MonsterRace} against the loaded registry. It mirrors the behaviour of C's
 * {@code parse_quest_*} functions ({@code player-quest.c}), which likewise fill an in-memory quest
 * from the same four directives.
 *
 * <p><b>C counterpart:</b> {@code player-quest.c} reads {@code quest.txt} with one hook per directive
 * ({@code parse_quest_name}, {@code parse_quest_level}, {@code parse_quest_race} and
 * {@code parse_quest_number}) and then {@code finish_parse_quest} numbers the quests by their final
 * array position. The port folds all of that into the single {@link #assemble} method, because the
 * grammar has already grouped the four directives into one {@link QuestParseRecord}.
 *
 * <p><b>Dependency:</b> race resolution needs {@code monster.txt} already loaded, which is why
 * {@code loadQuests()} runs late in {@link GameConstants}' init order (after {@code loadMonsters()}).
 *
 * <p><b>Every fault stops the load.</b> A negative {@code level} or
 * {@code number}, a figure with no digits, a {@code race} that matches no monster, a directive ahead of
 * its record's {@code name}, a directive with the wrong number of values and a directive name the file
 * format does not have each log at {@code fatal} and throw {@link DataFileParseException}. The first
 * four follow C's {@code PARSE_ERROR_NOT_NUMBER} and {@code PARSE_ERROR_INVALID_MONSTER}, which end the
 * whole {@code quest.txt} load. No record is ever dropped.
 *
 * <p><b>Directive order.</b> C's four hooks are independent: only {@code parse_quest_name} starts a
 * quest, and {@code level:}, {@code race:} and {@code number:} each fill the quest started by the last
 * {@code name:}, in any order, any number of times (the last of a kind wins), or not at all (the field
 * stays at the zero {@code mem_zalloc} gave it, so a missing {@code race:} leaves a null race).
 * {@code QuestGrammar} groups the lines the same way, and {@link #assemble} applies a record's
 * directives in file order. A {@code level:}, {@code race:} or {@code number:} ahead of the file's first
 * {@code name:} is different: C's hooks {@code assert(q)} on the null quest, so C crashes, and the port
 * refuses the file with a message that names the line. The grammar already refuses it as a syntax error,
 * so {@link #checkNameFound} is the second line of defence for a record built by hand. See "A
 * {@code level:}, {@code race:} or {@code number:} line before the file's first {@code name:} is refused,
 * where C crashes" under Quest in {@code docs/Accepted_Deviations.md} (decided 261118).
 *
 * <p><b>Integers.</b> {@code level} and {@code number} are {@code uint} fields in C, read by
 * {@code parser_parse} ({@code parser.c}) with {@code strtoul} in base 0 into an {@code unsigned int}.
 * They go through {@link ParseUtils#parseIntString(String, String, int, String, String)}, which reads base 0
 * as well, so {@code 010} is 8 and {@code 08} is 0 here as there; {@link ParseUtils#minusFound(String)}
 * is asked first about the raw text because C refuses any token starting with {@code -}, {@code -0}
 * included. The lexer's {@code INTEGER} token is an optional {@code +} or {@code -} and then digits
 * (the {@code +} added 261118, as {@code strtoul} accepts it), so a {@code +5} arrives and
 * {@link ParseUtils#parseIntString(String, String, int, String, String)} strips the sign, while a
 * {@code 0x} prefix cannot arrive.
 *
 * <p><b>Accepted deviation</b> (decided 261027; "An integer too big for its C type is clamped, where C
 * wraps it" under ParseUtils in {@code docs/Accepted_Deviations.md}): C keeps the low 32 bits of a figure
 * above {@code UINT_MAX} and wraps one above {@code INT_MAX} into a negative {@code int}. The port clamps
 * to {@link Integer#MAX_VALUE}, so {@code level:2147483649} stores 255 where C stores 1,
 * {@code level:4294967296} stores 255 where C stores 0, and {@code number:2147483648} stores
 * 2147483647 where C stores {@code INT_MIN}. The shipped {@code quest.txt} holds levels 99 and 100 and
 * a number of 1.
 *
 * <p>Class QuestAssembler coded before 261013, commented in full on 261118 (directive-order and
 * directive-before-name paragraphs added; earlier updates 261029 and 261105 for the removal of the
 * soft-error path).
 *
 * @author Rowan Crowther
 */
public class QuestAssembler implements Assembler<QuestParseRecord, List<Quest>> {
    /**
     * Log4j logger for this class. {@link #assemble} and the five private methods it calls write to it at
     * {@code fatal} level just before each of their throws (illegal directive name, directive before
     * {@code name}, wrong number of values, negative {@code level} or {@code number}, unknown
     * {@code race}), so the reason for an aborted {@code quest.txt} load reaches the log as well as the
     * stack trace. The figure-reading faults inside {@link ParseUtils} are logged by {@code ParseUtils}'
     * own logger.
     *
     * <p>Field logger coded before 261013, commented in full on 261118.
     */
    private static final Logger logger = LogManager.getLogger(QuestAssembler.class);

    /**
     * Assemble every parsed record into a {@link Quest}, reading the {@code level} and {@code number}
     * text the way C's {@code parse_quest_level} and {@code parse_quest_number} do, and resolving the
     * {@code race} name the way {@code parse_quest_race} does.
     *
     * <p>Each record's directives are applied in file order, which is the order C's hooks meet them,
     * records in file order too, and the first fault stops the whole load. Every fault logs at {@code fatal}
     * and throws {@link DataFileParseException}, which nothing between here and the caller of
     * {@code GameConstants} init catches, as C's parse errors abort the {@code quest.txt} load:
     * <ul>
     *   <li><b>Negative {@code level} or {@code number}.</b> C's {@code uint} test is
     *       {@code z == tok || *tok == '-'}, so any text starting with {@code -} is refused, {@code -0}
     *       included. {@link ParseUtils#minusFound(String)} makes the same first-character test on the
     *       raw text, before the figure is read.</li>
     *   <li><b>A figure with no digits.</b> {@link ParseUtils#parseIntString(String, String, int, String,
     *       String)} throws for it, as {@code z == tok} gives {@code PARSE_ERROR_NOT_NUMBER}. The lexer
     *       only ever passes digits, so this cannot be reached from a data file.</li>
     *   <li><b>Unknown {@code race}.</b> {@link MonsterRegistry#lookupMonsterRace} returns {@code null}
     *       where C's {@code lookup_monster} does, and {@code parse_quest_race} returns
     *       {@code PARSE_ERROR_INVALID_MONSTER}. The lookup is an exact case-insensitive match first and
     *       then the first race whose name contains the text.</li>
     *   <li><b>A directive before its record's {@code name}.</b> C asserts on the null quest; the port
     *       throws from {@link #checkNameFound}, naming the directive and its line (see the class
     *       block).</li>
     * </ul>
     * A directive that fails stops the load before any later directive of its record is read, so a record
     * that fails on {@code level} never has its {@code race} looked up if the {@code level:} line comes
     * first, and the reverse if {@code race:} does, as in C. A repeated {@code race:} whose first value
     * matches no monster is refused even when a later one would match, because C reports the error at
     * the first line. Because the load stops, no record is dropped.
     *
     * <p><b>Level.</b> C's {@code quest.level} is a {@code uint8_t}, so the stored level is the
     * base 0 value modulo 256: {@code 255} stays 255, {@code 256} gives 0, {@code 257} gives 1,
     * {@code 356} gives 100, {@code 010} gives 8 and {@code 08} gives 0. The figure is already clamped to
     * {@code 0..Integer.MAX_VALUE} by then (a negative one was refused above), so the
     * {@code if (level < 0) level += 256} line is unreachable. Above {@code Integer.MAX_VALUE} the
     * clamp makes the stored level 255, where C stores the low byte of the wrapped value (see the
     * accepted deviation in the class block).
     *
     * <p><b>Number.</b> C's {@code quest.max_num} is a plain {@code int}, so the value is stored as
     * read, with no truncation: {@code 300} stays 300 and {@code 2147483647} stays 2147483647. Above
     * that the clamp stores 2147483647, where C wraps into a negative {@code int}.
     *
     * <p>The {@code index} is the record's position in the returned list, 0 for the first, as
     * {@code finish_parse_quest} stamps {@code quest.index} from the final array position (C builds
     * the list newest-first and fills the array from the back, so the order is the file's). Each quest's
     * kill tally starts empty, so {@code currentNumber} is seeded to 0 (C {@code cur_num}, from
     * {@code mem_zalloc}); only {@code maxNumber} comes from the file. {@code QuestGrammar} requires only the
     * {@code name} line, so a record can arrive without a {@code level}, {@code race} or {@code number}
     * directive, as in C: the quest is built with level 0, a {@code null} race and a maximum of 0, which are
     * the zeroed fields C leaves. An empty list of records gives an empty list of quests, as C's
     * {@code finish_parse_quest} gives {@code quest_max} 0.
     *
     * <p>Each quest starts as {@code new Quest(index, null, 0, null, 0, 0)}, so a {@code null} name is how
     * {@link #checkNameFound} knows no {@code name} directive has been applied yet.
     *
     * <p>Function assemble coded before 260915, level and number handling reworked on 261013,
     * soft errors made fatal before 261029, split into per-directive methods before 261118,
     * commented in full on 261118.
     *
     * @param records  the raw quest records in file order
     * @param filename the file being read, for the messages
     * @return the assembled quests, in file order, each with the index of its position
     * @throws DataFileParseException if a record has a directive before its {@code name}, a directive name
     *                                or value count the format does not allow, a {@code level} or
     *                                {@code number} that is negative or has no digits, or a {@code race}
     *                                that matches no monster
     * @throws IllegalStateException  if the monster races have not been loaded yet
     *                                ({@link MonsterRegistry#lookupMonsterRace})
     */
    @Override
    public List<Quest> assemble(@NotNull List<QuestParseRecord> records,
                                    String filename) {
        List<Quest> quests = new ArrayList<>();
        int index = -1;

        for (QuestParseRecord record : records) {
            index++;
            Quest quest = new Quest(index, null, 0, null, 0, 0);
            
            for (Directive directive : record.directives()) {
                switch (directive.name()) {
                    case "name" -> {
                        parseNameDirective(quest, directive, filename);
                    }
                    case "level" -> {
                        checkNameFound(quest, directive, filename);
                        parseLevelDirective(quest, directive, filename);
                    }
                    case "race" -> {
                        checkNameFound(quest, directive, filename);
                        parseRaceDirective(quest, directive, filename);}
                    case "number" -> {
                        checkNameFound(quest, directive, filename);
                        parseNumberDirective(quest, directive, filename);
                    }
                    default -> {
                        String message = "Quest directive at line: " + directive.line() + " of file: " + filename + " has " +
                                "an illegal directive value: " + directive.name();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            }
            quests.add(quest);
        }

        return quests;
    }
    
    /**
     * Refuse a {@code level:}, {@code race:} or {@code number:} directive that arrives before this
     * record's {@code name:}. C's {@code parse_quest_level}, {@code parse_quest_race} and
     * {@code parse_quest_number} read the current quest with {@code parser_priv(p)} and then
     * {@code assert(q)}; {@code parser_priv} is {@code NULL} until {@code parse_quest_name} has run, so C
     * crashes on such a file. By the rule decided on 261118 (wherever C crashes on a data file, Java throws)
     * this logs at {@code fatal} and throws {@link DataFileParseException} naming the directive, its line
     * and the file.
     *
     * <p>{@link #assemble} builds every quest with a {@code null} name and only {@link #parseNameDirective}
     * sets one, so {@code quest.getName() == null} means no {@code name} directive has been applied to
     * this record yet. The reader never reaches this: {@code QuestGrammar}'s {@code quest} rule starts with
     * {@code name}, so the same file is a syntax error first. The check matters for a
     * {@link QuestParseRecord} built by hand, which is how it is tested.
     *
     * <p>Function checkNameFound coded before 261118, commented in full on 261118.
     *
     * @param quest     the quest being filled for the current record
     * @param directive the {@code level}, {@code race} or {@code number} directive about to be applied
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the quest has no name yet
     */
    private void checkNameFound(Quest quest, Directive directive, String filename) {
        if (quest.getName() == null) {
            String message = "Quest directive at line: " + directive.line() + " of file: " + filename + " has " +
                    "a " + directive.name() + " directive before a name directive";
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
    }
    
    /**
     * Store a {@code name:} directive's value as the quest's name, as {@code parse_quest_name} does with
     * {@code string_make(name)}. C's field is a {@code str}, so it is the rest of the line: colons, leading
     * blanks and trailing blanks are kept, and nothing is trimmed here.
     *
     * <p>C's hook also allocates the new quest and makes it the parser's current one. The port does that
     * in {@link #assemble}, once per record, so this method only fills the name. A directive with other
     * than exactly one value is refused; {@code QuestGrammar} always supplies one, so only a record built by
     * hand reaches that.
     *
     * <p>Function parseNameDirective coded before 261118, commented in full on 261118.
     *
     * @param quest     the quest being filled for the current record
     * @param directive the {@code name} directive
     * @param filename  the file being read, for the message
     * @throws DataFileParseException if the directive does not have exactly one value
     */
    private void parseNameDirective(Quest quest, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder names = new StringBuilder();
            for (String name : directive.values()) {
                names.append(name);
                names.append(", ");
            }

            if (!names.isEmpty()) names = new StringBuilder(names.substring(0, names.length() - 2));

            String msg = "Quest starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of name values (should be 1): " + names;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        String name = directive.values().getFirst();
        quest.setName(name);
    }
    
    /**
     * Store a {@code level:} directive's value, as {@code parse_quest_level} does with
     * {@code q->level = parser_getuint(p, "level")}. The field is a {@code uint} read with
     * {@code strtoul} in base 0, and {@code quest.level} is a {@code uint8_t}, so the stored level is the
     * value modulo 256 ({@code 356} gives 100, {@code 010} gives 8, {@code 08} gives 0).
     *
     * <p>A text starting with {@code -}, {@code -0} included, is refused first through
     * {@link ParseUtils#minusFound(String)}, as C's {@code *tok == '-'} test does. A repeated
     * {@code level:} overwrites the earlier one, as in C. Above {@link Integer#MAX_VALUE} the figure is
     * clamped before the modulo, which is the accepted deviation described in the class block. A
     * directive with other than exactly one value is refused; only a record built by hand reaches that.
     *
     * <p>Function parseLevelDirective coded before 261118, commented in full on 261118.
     *
     * @param quest     the quest being filled for the current record
     * @param directive the {@code level} directive
     * @param filename  the file being read, for the messages
     * @throws DataFileParseException if the directive does not have exactly one value, the text starts with
     *                                {@code -}, or it has no digits
     */
    private void parseLevelDirective(Quest quest, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder names = new StringBuilder();
            for (String name : directive.values()) {
                names.append(name);
                names.append(", ");
            }

            if (!names.isEmpty()) names = new StringBuilder(names.substring(0, names.length() - 2));

            String msg = "Quest starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of level values (should be 1): " + names;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        String levelString = directive.values().getFirst();
        if (ParseUtils.minusFound(levelString)) {
            String msg = "Quest starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "a negative level values: " + levelString;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        int level = ParseUtils.parseIntString("Quest", levelString, directive.line(), filename, "level");
        level = level % 256;
        quest.setLevel(level);
    }
    
    /**
     * Resolve a {@code race:} directive's monster name and store the race, as {@code parse_quest_race} does
     * with {@code lookup_monster}. C's field is a {@code str}, so the whole rest of the line is the name,
     * commas included ({@code Sauron, the Sorcerer}). An exact case-insensitive match wins, otherwise the
     * first race whose name contains the text; see {@link MonsterRegistry#lookupMonsterRace}.
     *
     * <p>When nothing matches, C returns {@code PARSE_ERROR_INVALID_MONSTER} and the load ends; the port logs
     * at {@code fatal} and throws. The error is raised at this directive even if a later {@code race:} in
     * the same record would match, and a repeated {@code race:} that does match replaces the earlier race.
     * Needs {@code monster.txt} loaded first. A directive with other than exactly one value is refused; only
     * a record built by hand reaches that.
     *
     * <p>Function parseRaceDirective coded before 261118, commented in full on 261118.
     *
     * @param quest     the quest being filled for the current record
     * @param directive the {@code race} directive
     * @param filename  the file being read, for the messages
     * @throws DataFileParseException if the directive does not have exactly one value or the name matches
     *                                no monster
     */
    private void parseRaceDirective(Quest quest, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder names = new StringBuilder();
            for (String name : directive.values()) {
                names.append(name);
                names.append(", ");
            }

            if (!names.isEmpty()) names = new StringBuilder(names.substring(0, names.length() - 2));

            String msg = "Quest starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of race values (should be 1): " + names;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        String raceString = directive.values().getFirst();
        MonsterRace race = MonsterRegistry.lookupMonsterRace(raceString);
        if (race == null) {
            String msg = "Quest starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect monster race values: " + raceString;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        quest.setRace(race);
    }
    
    /**
     * Store a {@code number:} directive's value as the quest's maximum, as {@code parse_quest_number} does
     * with {@code q->max_num = parser_getuint(p, "number")}. {@code max_num} is a plain {@code int}, so the
     * value is kept as read with no modulo; above {@link Integer#MAX_VALUE} it is clamped, where C wraps
     * (the accepted deviation in the class block). A text starting with {@code -}, {@code -0} included, is
     * refused first through {@link ParseUtils#minusFound(String)}, and a repeated {@code number:}
     * overwrites the earlier one, as in C. A directive with other than exactly one value is refused; only
     * a record built by hand reaches that.
     *
     * <p>The closing {@code setCurrentNumber(0)} repeats what the constructor call in {@link #assemble}
     * already did. C's parser never sets {@code cur_num}; it stays at the zero {@code mem_zalloc} gave it.
     *
     * <p>Function parseNumberDirective coded before 261118, commented in full on 261118.
     *
     * @param quest     the quest being filled for the current record
     * @param directive the {@code number} directive
     * @param filename  the file being read, for the messages
     * @throws DataFileParseException if the directive does not have exactly one value, the text starts with
     *                                {@code -}, or it has no digits
     */
    private void parseNumberDirective(Quest quest, Directive directive, String filename) {
        if (directive.values().size() != 1) {
            StringBuilder names = new StringBuilder();
            for (String name : directive.values()) {
                names.append(name);
                names.append(", ");
            }

            if (!names.isEmpty()) names = new StringBuilder(names.substring(0, names.length() - 2));

            String msg = "Quest starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "an incorrect number of number values (should be 1): " + names;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        String maxNumberString = directive.values().getFirst();
        if (ParseUtils.minusFound(maxNumberString)) {
            String msg = "Quest starting at line: " + directive.line() + " of file: " + filename + " has " +
                    "a negative number value: " + maxNumberString;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        
        int maxNumber = ParseUtils.parseIntString("Quest", maxNumberString, directive.line(), filename, "max number");
        quest.setMaxNumber(maxNumber);
        quest.setCurrentNumber(0);
    }
}
