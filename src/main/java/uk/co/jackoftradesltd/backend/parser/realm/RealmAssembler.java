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

import org.jetbrains.annotations.NotNull;
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
 * <p><b>Error policy — skip the record, keep the file.</b> A realm whose stat fails to resolve is
 * reported to {@code errors} with its source line and dropped via {@code continue}; the other
 * realms still load. C rejects an unknown stat too (it raises {@code PARSE_ERROR_INVALID_SPELL_STAT}
 * in {@code parse_realm_stat}). The assembler drops a record for no other reason.
 *
 * <p>Class RealmAssembler coded before 260915, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class RealmAssembler implements Assembler<RealmParseRecord, List<MagicRealm>> {
    /**
     * Assembles every parsed realm record into a {@link MagicRealm}, dropping (with an error appended
     * to {@code errors}) any record whose stat fails to resolve. The verb, spell-noun and book-noun
     * are passed to the {@link MagicRealm} unchanged, whatever they say.
     *
     * <p><b>Order.</b> The result is in <em>reverse</em> file order. C's {@code parse_realm_name}
     * pushes each realm onto the head of the {@code realms} list and {@code finish_parse_realm}
     * keeps the list as built, so the last realm in {@code realm.txt} is first. The loop does the
     * same with {@code addFirst}, so the shipped file gives shadow, nature, divine, arcane. Realms
     * are looked up by name, so the order changes nothing there; it is kept so that anything which
     * walks the whole list meets the realms in C's order.
     *
     * <p>Function assemble coded before 260915, updated on 261014 to stop resolving the book-noun
     * to a {@link TValue}, commented in full on 261014.
     *
     * @param records the raw per-realm parse records, in file order
     * @param errors  sink for human-readable messages describing each dropped record
     * @return the successfully assembled realms in reverse file order (records with an unknown
     * stat omitted)
     */
    @Override
    public List<MagicRealm> assemble(@NotNull List<RealmParseRecord> records, @NotNull List<String> errors) {
        List<MagicRealm> realms = new ArrayList<>();

        for (RealmParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            Stats stat;
            try {
                stat = Stats.valueOf("STAT_" + record.stat());
            } catch (IllegalArgumentException e) {
                errors.add("Record at line: " + line + " has " +
                        "an unknown stat: " + record.stat());
                continue;
            }
            String verb = record.verb();
            String spellNoun = record.spellNoun();
            String bookNoun = record.bookNoun();

            realms.addFirst(new MagicRealm(name, stat, verb, spellNoun, bookNoun));
        }

        return realms;
    }
}
