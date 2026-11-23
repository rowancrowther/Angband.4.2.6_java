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

package uk.co.jackoftradesltd.backend.parser.realm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link RealmParseRecord}s from {@code realm.txt} into typed {@link MagicRealm}
 * domain objects — the port's equivalent of C's {@code parse_realm_*} handlers in {@code init.c}.
 *
 * <p>One textual field names an enum constant and is resolved here: the {@code stat:} value, via a
 * {@code STAT_} prefix, into {@link Stats}. The verb, spell-noun and book-noun are carried through as
 * free text, as C stores them; the book-noun is deliberately not resolved to a tval, because C
 * accepts any text there. There are no cross-file references, so — unlike most assemblers — this one
 * needs nothing loaded into {@code GameConstants} first.
 *
 * <p><b>Every fault stops the load.</b> Two faults can be found here, both in the stat: a
 * {@code stat:} line with no name in it, and a name that matches no stat. Each logs at {@code fatal}
 * and throws {@link DataFileParseException}, as C ends the whole {@code realm.txt} load for both:
 * {@code PARSE_ERROR_MISSING_FIELD} from {@code parser_parse} when the {@code sym} field has no token,
 * and {@code PARSE_ERROR_INVALID_SPELL_STAT} from {@code parse_realm_stat} when
 * {@code stat_name_to_idx} answers {@code -1}. No realm is ever dropped.
 *
 * <p><b>The stat.</b> {@code stat} is a {@code sym} in C, which {@code parser_parse} reads with
 * {@code strtok(sp, ":")}: leading colons are skipped and the value stops at the next colon. The
 * assembler does the same to the raw text, so {@code stat:INT:extra}, {@code stat:INT:} and
 * {@code stat::INT} all read {@code INT}, and only the text before the first colon is looked up.
 * Blanks are kept, as {@code strtok} keeps them, so {@code stat: INT} and {@code stat:INT :x} name no
 * stat. The text then goes through {@link Stats#statNameToIdx(String)}, the port of C's
 * {@code stat_name_to_idx}, so it is matched ignoring case, {@code NONE} is refused (C's
 * {@code stat_name_list} has no such name), and {@code MAX} is accepted and gives
 * {@link Stats#STAT_MAX}, as C's list holds {@code "MAX"} after the five real stats.
 *
 * <p><b>Three kinds of empty.</b> The record's {@code stat} field is one of three things, and they
 * must not be mixed up:
 * <ul>
 *   <li>{@code ""} means the record has no {@code stat:} line at all. It reads as
 *       {@link Stats#STAT_STR}: C zero-fills each new realm in {@code parse_realm_name},
 *       {@code parse_realm_stat} never runs for it, and 0 is {@code STAT_STR}. This test comes
 *       before any cutting.</li>
 *   <li>{@code null} means the line is there but has no name, {@code stat:} or {@code stat::} with a
 *       single colon, where the grammar's {@code stat} rule matched no {@code STRING}. C finds no
 *       token and refuses, so the assembler throws "has an empty stat value".</li>
 *   <li>Colons only, two or more, arrives as text, because the lexer reads a run of colons longer than
 *       one as a single {@code STRING}. The cut leaves nothing, nothing matches, and the assembler
 *       throws "has an illegal stat value: ::". C refuses it as well; only the wording differs from
 *       the {@code null} case.</li>
 * </ul>
 *
 * <p><b>Free text.</b> The verb, spell-noun and book-noun are never inspected. The grammar seeds
 * {@code ""} for a directive the record lacks, where C leaves the pointer {@code NULL}
 * (see {@code docs/grammar_issues.md}); nothing in the port reads those fields yet.
 *
 * <p>Class RealmAssembler coded before 260915, commented in full on 261029, updated on 261105 for
 * the removal of the soft-error path and on 261122 to cut the stat at its first colon as C's
 * {@code sym} field does.
 *
 * @author Rowan Crowther
 */
public class RealmAssembler implements Assembler<RealmParseRecord, List<MagicRealm>> {
    /**
     * Logger the assembler uses for its two failures: the {@code fatal} line written just before the
     * {@link DataFileParseException} for a {@code stat:} with no name and for one that names no stat.
     *
     * <p>Field logger coded before 260915, commented in full on 261029, updated on 261122 for the
     * second failure.
     */
    private static final Logger logger = LogManager.getLogger(RealmAssembler.class);

    /**
     * Assembles every parsed realm record into a {@link MagicRealm}, with the {@code stat:} text
     * cut at its first colon and resolved to a {@link Stats} (see the class block for the rules, and
     * for the three kinds of empty stat) and the verb, spell-noun and book-noun passed through
     * unchanged, whatever they say. A stat that resolves to nothing stops the load: the method logs at
     * {@code fatal} and throws {@link DataFileParseException} with "Realm starting at line: N of file:
     * F has an illegal stat value: X", where X is the raw {@code stat:} text, before the cut. A
     * {@code stat:} line with no name stops it the same way with "has an empty stat value". N is the
     * line of the record's {@code name:} directive in both; C's parser would name the {@code stat:}
     * line itself.
     *
     * <p><b>Order.</b> The result is in <em>reverse</em> file order. C's {@code parse_realm_name}
     * pushes each realm onto the head of the {@code realms} list and {@code finish_parse_realm}
     * keeps the list as built, so the last realm in {@code realm.txt} is first. The loop does the
     * same with {@code addFirst}, so the shipped file gives shadow, nature, divine, arcane. Realms
     * are looked up by name, so the order changes nothing there; it is kept so that anything which
     * walks the whole list meets the realms in C's order.
     *
     * <p>Function assemble coded before 260915, updated on 261014 to stop resolving the book-noun
     * to a {@link TValue} and on 261029 to stop the load on an unresolvable stat and to read an
     * absent stat as strength, commented in full on 261029, updated on 261122 to cut the stat at its
     * first colon and to refuse a {@code stat:} line with no name.
     *
     * @param records  the raw per-realm parse records, in file order
     * @param filename the file the records came from, named in the failure message
     * @return the assembled realms in reverse file order, one per record
     * @throws DataFileParseException if a record's {@code stat:} line has no name, or names no stat
     */
    @Override
    public List<MagicRealm> assemble(@NotNull List<RealmParseRecord> records,
                                    String filename) {
        List<MagicRealm> realms = new ArrayList<>();

        for (RealmParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            String statName = record.stat();
            Stats stat;
            // null: a stat: line with no name (C: MISSING_FIELD). "" is a record with no stat: line.
            if (record.stat() == null) {
                String message = "Realm starting at line: " + line + " of file: " + filename + " has " +
                        "an empty stat value";
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            if (statName.isEmpty()) {
                stat = Stats.STAT_STR;
            } else {
                // Cut initial colons
                while (!statName.isEmpty() && statName.charAt(0) == ':')
                    statName = statName.substring(1);
                // Cut to first colon
                String singleStat = statName.split(":", 2)[0];

                stat = Stats.statNameToIdx(singleStat);
                if (stat == null) {
                    String message = "Realm starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal stat value: " + record.stat();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            
            String verb = record.verb();
            String spellNoun = record.spellNoun();
            String bookNoun = record.bookNoun();

            realms.addFirst(new MagicRealm(name, stat, verb, spellNoun, bookNoun));
        }

        return realms;
    }
}
