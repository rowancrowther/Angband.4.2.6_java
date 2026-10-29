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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.Assembler;
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
 * <p><b>Every fault stops the load.</b> The one fault the assembler can find is a {@code stat:} that
 * names no stat. It logs at {@code fatal} and throws {@link DataFileParseException}, as C's
 * {@code PARSE_ERROR_INVALID_SPELL_STAT} from {@code parse_realm_stat} ends the whole
 * {@code realm.txt} load. No realm is ever dropped, and the {@code errors} list passed to
 * {@link #assemble} is never written to.
 *
 * <p><b>The stat.</b> The text goes through {@link Stats#statNameToIdx(String)}, the port of C's
 * {@code stat_name_to_idx}, so it is matched ignoring case, {@code NONE} is refused (C's
 * {@code stat_name_list} has no such name), and {@code MAX} is accepted and gives
 * {@link Stats#STAT_MAX}, as C's list holds {@code "MAX"} after the five real stats. A realm with no
 * {@code stat:} line at all reads as {@link Stats#STAT_STR}: C zero-fills each new realm in
 * {@code parse_realm_name}, {@code parse_realm_stat} never runs for it, and 0 is {@code STAT_STR}.
 * The grammar hands over {@code ""} for the absent line, and that is the only way the field can be
 * empty, since a directive with no value is a hard error in the grammar.
 *
 * <p><b>Free text.</b> The verb, spell-noun and book-noun are never inspected. The grammar seeds
 * {@code ""} for a directive the record lacks, where C leaves the pointer {@code NULL}
 * (see {@code docs/grammar_issues.md}); nothing in the port reads those fields yet.
 *
 * <p>Class RealmAssembler coded before 260915, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public class RealmAssembler implements Assembler<RealmParseRecord, List<MagicRealm>> {
    /**
     * Logger the assembler uses for its one failure: the {@code fatal} line written just before the
     * {@link DataFileParseException} for an unresolvable {@code stat:}.
     *
     * <p>Field logger coded before 260915, commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(RealmAssembler.class);

    /**
     * Assembles every parsed realm record into a {@link MagicRealm}, with the {@code stat:} text
     * resolved to a {@link Stats} (see the class block for the rules) and the verb, spell-noun and
     * book-noun passed through unchanged, whatever they say. A stat that resolves to nothing
     * stops the load: the method logs at {@code fatal} and throws {@link DataFileParseException}
     * with "Realm starting at line: N of file: F has an illegal stat value: X", where N is the line
     * of the record's {@code name:} directive. C's parser would name the {@code stat:} line itself.
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
     * absent stat as strength, commented in full on 261029.
     *
     * @param records  the raw per-realm parse records, in file order
     * @param errors   not written to; every fault is thrown
     * @param filename the file the records came from, named in the failure message
     * @return the assembled realms in reverse file order, one per record
     * @throws DataFileParseException if a record's {@code stat:} names no stat
     */
    @Override
    public List<MagicRealm> assemble(@NotNull List<RealmParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MagicRealm> realms = new ArrayList<>();

        for (RealmParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            Stats stat;
            if (record.stat().isEmpty())
                stat = Stats.STAT_STR;
            else
                stat = Stats.statNameToIdx(record.stat());
            if (stat == null) {
                String message = "Realm starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal stat value: " + record.stat();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            String verb = record.verb();
            String spellNoun = record.spellNoun();
            String bookNoun = record.bookNoun();

            realms.addFirst(new MagicRealm(name, stat, verb, spellNoun, bookNoun));
        }

        return realms;
    }
}
