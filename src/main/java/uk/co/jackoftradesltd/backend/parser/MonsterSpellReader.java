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
import uk.co.jackoftradesltd.backend.parser.grammars.monsterspell.MonsterSpellGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.monsterspell.MonsterSpellLexer;
import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellAssembler;
import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.monsters.MonsterSpellType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/monster_spell.txt} into {@link MonsterSpellType} objects - what each
 * monster spell does: its message type, hit chance, effects, and the lore text and cast messages
 * for each tier of spell power. It is the thin hand-written bridge between the generated
 * {@code MonsterSpellLexer} / {@code MonsterSpellGrammar} pair and the game, plugged into the
 * shared {@link GrammarDriver} pipeline and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code mon_spell_parser} in {@code mon-init.c}:
 * <ul>
 *   <li>{@code init_parse_mon_spell} and the {@code parse_mon_spell_*} handlers become the grammar
 *       plus {@link MonsterSpellAssembler}: the grammar collects each record's raw text, and the
 *       assembler turns it into a {@link MonsterSpellType} - {@code name:} to a {@code MonsterSpell}
 *       by way of its {@code RSF_} prefix, {@code msgt:} to a {@code MessageType} (left at
 *       {@code MSG_GENERIC}, C's zeroed value, when the line is absent), {@code hit:} to an int,
 *       the effect lines to {@code Effect}s through {@code EffectAssembler}, and the
 *       {@code power-cutoff:} tiers to {@code MonsterSpellLevel}s;</li>
 *   <li>{@code run_parse_mon_spell} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_mon_spell} becomes the list handed back, one {@link MonsterSpellType}
 *       per {@code name:} block. C does nothing more than point the global {@code monster_spells} at
 *       the chain; here {@code MonsterDataLoader.loadMonsterSpellTypes} hands the list to
 *       {@code MonsterRegistry}, which keeps it and has no lookup over it yet.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> The spells come back in file order. C's order is the reverse:
 * {@code parse_mon_spell_name} pushes each new record on the head of a chain, and
 * {@code finish_parse_mon_spell} keeps it as it is, so {@code monster_spells} starts with the last
 * record in the file. Nothing can tell the two apart with the shipped data, because
 * {@code monster_spell_by_index} in {@code mon-spell.c} walks the chain for a matching spell index
 * and every spell appears once. They differ only for a spell named twice: C finds the later record,
 * and a Java lookup over this list has to take the last match to agree; see
 * {@code docs/Assembler_Issues.md}.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition; the upstream
 *       {@code monster_spell.txt} has no such line, and this repository's copy adds
 *       {@code record-count:91}. A wrong count is a soft error and the records still load, but a
 *       missing header is a syntax error and loads nothing.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (an unknown spell name, message type, colour or effect) skips only the
 *       offending record, or level, and the rest still load.</li>
 *   <li>C takes the directives in any order and attaches each to the last record, effect or level.
 *       The grammar is stricter and in places looser: it wants a directive after every
 *       {@code name:}, closes a level when another kind of directive interrupts the lore and
 *       message lines, and takes {@code effect-msg:}, which C does not register. See
 *       {@code docs/grammar_issues.md}.</li>
 *   <li>The assembler differs from C over an absent {@code hit:}, an unknown lore colour, a
 *       {@code msgt:} written in lower case or as a number, and a record with no lore or message
 *       lines at all. See {@code docs/Assembler_Issues.md}.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class MonsterSpellReader coded before 260915, commented in full on 261017.
 */
public class MonsterSpellReader implements Reader<MonsterSpellType> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code monster_spell.txt} is logged under this reader's name before it is rethrown. Syntax and
     * soft errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled monster spells, in file order (see the
     * class block for how that differs from C's chain), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean load are told apart only by the
     * list being empty, and a record skipped for an unknown spell name, message type, colour or
     * effect just goes missing. Callers that must tell a partial or failed load from a clean one
     * have to use {@link #parseWithResults}, as {@code MonsterDataLoader.loadMonsterSpellTypes}
     * does.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the assembled {@link MonsterSpellType} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<MonsterSpellType> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link MonsterSpellParseRecord}s, still holding unconverted text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too, such as a malformed
     * {@code hit:} or {@code power-cutoff:} number or a directive before any {@code name:} (C's
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER}). The header check is soft -
     * {@link GrammarDriver#checkRecordCount} only appends to {@code errors}, so a miscount never
     * discards the records that did parse.
     * <p>
     * The records stay in file order. The copy at the end detaches the result from the parse tree,
     * so the returned list is the caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated parser for this file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink (record-count mismatches are appended here)
     * @return the parsed {@link MonsterSpellParseRecord}s in file order
     */
    private static List<MonsterSpellParseRecord> extract(
            @NotNull MonsterSpellGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        MonsterSpellGrammar.FileContext output = parser.file();
        List<MonsterSpellParseRecord> records = output.monsterSpells;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Parse the given file, returning both the assembled {@link MonsterSpellType} list (in file
     * order, see the class block) and any soft errors gathered along the way, for callers that need
     * to tell a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_mon_spell} and the
     * {@code parse_mon_spell_*} handlers in {@code mon-init.c}: it wires {@code MonsterSpellLexer},
     * {@code MonsterSpellGrammar}, {@link #extract} and {@link MonsterSpellAssembler} into
     * {@link GrammarDriver#run}, so the reader stays a thin wiring layer. A syntax error returns no
     * items and the collected messages; an unknown name, an unresolved effect or colour or a
     * {@code record-count:} mismatch leaves the good records in the result alongside the messages,
     * so the list can be short without anything having been thrown. C would have stopped at the
     * first of any of these. An unreadable file is not a result at all but an {@link IOException},
     * the analogue of {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code MonsterDataLoader.loadMonsterSpellTypes}
     * treats a result that carries errors and no spells as fatal, and reports the errors either way.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the monster-spell data file to read
     * @return a {@link ParseResult} of the assembled spells plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<MonsterSpellType> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                MonsterSpellLexer::new,
                MonsterSpellGrammar::new,
                MonsterSpellReader::extract,
                new MonsterSpellAssembler(), logger);
    }
}
