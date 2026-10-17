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
import uk.co.jackoftradesltd.backend.parser.grammars.monster.MonsterGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.monster.MonsterLexer;
import uk.co.jackoftradesltd.backend.parser.monster.MonsterAssembler;
import uk.co.jackoftradesltd.backend.parser.monster.MonsterParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/monster.txt} into {@link MonsterRace} objects - every monster in the
 * game, from the {@code <player>} placeholder that opens the file to the last unique - each with its
 * base template, stats, flags, spells, blows, drops, companions, mimic kinds, shapes and
 * description. It is the thin hand-written bridge between the generated {@code MonsterLexer} /
 * {@code MonsterGrammar} pair and the game, plugged into the shared {@link GrammarDriver} pipeline
 * and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code monster_parser} in {@code mon-init.c}:
 * <ul>
 *   <li>{@code init_parse_monster} and its 33 {@code parse_monster_*} handlers become the grammar
 *       plus {@link MonsterAssembler}: the grammar collects each directive's raw text, and the
 *       assembler resolves the {@code base:} name to a monster base, the blow method and effect
 *       names, the flag and spell names, the {@code drop:} and {@code mimic:} tval/sval pairs to
 *       object kinds, the {@code friends-base:} names to monster bases, the {@code color:} code and
 *       the {@code color-cycle:} name. The names in {@code friends:} and {@code shape:} are left as
 *       text, because they may name a race further down the file;</li>
 *   <li>{@code run_parse_monster} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_monster} is shared out. The list handed back, one {@link MonsterRace}
 *       per {@code name:} block, is the copy into {@code r_info}.
 *       {@code MonsterDataLoader.loadMonsters} gives it to {@code MonsterRegistry.setMonsterRaces},
 *       which also does the scan for {@code r_max} and {@code mon_blows_max}, and then runs the
 *       second pass: resolving the {@code friends:} and {@code shape:} names to races and giving
 *       every race a blank {@code MonsterLore}, which stands in for the {@code l_list} allocation.
 *       Copying each race's blows into an array of {@code mon_blows_max} entries, and numbering
 *       each race with {@code ridx}, are not done: a race's blow list is not padded, and its index
 *       is its position in the list;</li>
 *   <li>{@code cleanup_monster} has no counterpart, since the garbage collector frees the list.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> The list that comes back is in file order, and so is C's
 * {@code r_info}. C's chain is built head-first, so it holds the records in reverse, but
 * {@code finish_parse_monster} starts at the chain head with the highest index and counts down,
 * which puts the last record in the file in the last slot. (Contrast {@code finish_parse_meth},
 * which counts up and so reverses the blow methods.) That keeps the {@code <player>} record at
 * element 0 and lets {@code MonsterRegistry.lookupMonsterRace} find the first of two races that
 * share a name, as {@code lookup_monster} does.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code monster.txt} has no such line, and this repository's copy adds it.</li>
 *   <li>C stops at the first parse error, and an unresolved base, blow method, blow effect, flag,
 *       spell, tval or sval is one. Here a syntax error is fatal and returns no items, but an
 *       assembler error skips only the offending record and the rest still load.</li>
 *   <li>C takes the directives in any order and leaves any but {@code name:} out. The grammar takes
 *       them in any order too, but wants at least one after each {@code name:} and at least one
 *       record in the file. The shapes it accepts for numbers, flag lists, colours and messages
 *       are narrower than C's; see {@code docs/grammar_issues.md}. The assembler's reading of
 *       several directives differs as well; see {@code docs/Assembler_Issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot at the end. This is the
 *       accepted deviation recorded in the registry's class block, so C's {@code r_max}, one higher
 *       than the record count, has no counterpart: {@code MonsterRegistry.monsterRaceMax} is the
 *       plain size.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customized copy in the user directory first.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class MonsterReader coded before 260915, commented in full on 261017.
 */
public class MonsterReader implements Reader<MonsterRace> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code monster.txt} is logged under this reader's name before it is rethrown. Syntax and soft
     * errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled monster races, in file order (which is
     * C's {@code r_info} order too, see the class block), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list, and a record skipped for an unresolved base, blow, flag,
     * spell, drop or mimic just goes missing. Callers that must tell a partial or failed load from
     * a clean one have to use {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the assembled {@link MonsterRace} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<MonsterRace> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link MonsterParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C fails too: a directive before the first {@code name:}
     * ({@code PARSE_ERROR_MISSING_RECORD_HEADER} in every {@code parse_monster_*} handler but
     * {@code parse_monster_name}) and a {@code speed:} or other integer line with no number
     * ({@code PARSE_ERROR_MISSING_FIELD}). The header check is soft -
     * {@link GrammarDriver#checkRecordCount} only appends to {@code errors}, so a miscount never
     * discards the records that did parse.
     * <p>
     * The records stay in file order, and so does the result: nothing later reverses them. The copy
     * at the end detaches the result from the parse tree, so the returned list is the caller's to
     * keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated parser for this file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link MonsterParseRecord}s in file order
     */
    private static List<MonsterParseRecord> extract(
            @NotNull MonsterGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        MonsterGrammar.FileContext output = parser.file();
        List<MonsterParseRecord> records = output.monsters;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Parse the given file, returning both the assembled {@link MonsterRace} list (in file order)
     * and any soft errors gathered along the way, for callers that need to tell a clean load from
     * a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_monster} and the {@code parse_monster_*}
     * handlers in {@code mon-init.c}: it wires {@code MonsterLexer}, {@code MonsterGrammar},
     * {@link #extract} and {@link MonsterAssembler} into {@link GrammarDriver#run}. A syntax error
     * returns no items and the collected messages; an unresolved base, blow, flag, spell, drop or
     * mimic, or a {@code record-count:} mismatch, leaves the good records in the result alongside
     * the messages, so the list can be short without anything having been thrown. C would have
     * stopped at the first of the unresolved names and ignored the mismatch. An unreadable file is
     * not a result at all but an {@link IOException}, the analogue of
     * {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code MonsterDataLoader.loadMonsters} gates on
     * the soft errors for exactly that reason. The result is only the first half of
     * {@code finish_parse_monster}; the friend and shape names are still text until that loader's
     * second pass.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the monster data file to read
     * @return a {@link ParseResult} of the assembled races plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<MonsterRace> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                MonsterLexer::new,
                MonsterGrammar::new,
                MonsterReader::extract,
                new MonsterAssembler(), logger);
    }
}
