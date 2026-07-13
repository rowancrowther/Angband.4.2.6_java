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

package uk.co.jackoftrades.backend.parser.realm;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link RealmParseRecord}s from {@code realm.txt} into typed {@link MagicRealm}
 * domain objects — the port's equivalent of C's {@code parse_realm_*} handlers in {@code init.c}.
 *
 * <p>The two textual fields that name enum constants are resolved here: the {@code stat:} value via a
 * {@code STAT_} prefix into {@link Stats}, and the {@code book-noun:} value via a {@code TV_} prefix
 * over its underscored, upper-cased text into {@link TValue} (so {@code "magic book"} becomes
 * {@link TValue#TV_MAGIC_BOOK}). The verb and spell-noun are carried through as free text. There are
 * no cross-file references, so — unlike most assemblers — this one needs nothing loaded into
 * {@code GameConstants} first.
 *
 * <p><b>Error policy — skip the record, keep the file.</b> A realm whose stat or book-noun fails to
 * resolve is reported to {@code errors} with its source line and dropped via {@code continue}; the
 * other realms still load.
 *
 * @author Rowan Crowther
 */
public class RealmAssembler implements Assembler<RealmParseRecord, List<MagicRealm>> {
    /**
     * Assembles every parsed realm record into a {@link MagicRealm}, dropping (with an error appended
     * to {@code errors}) any record whose stat or book-noun fails to resolve.
     *
     * @param records the raw per-realm parse records, in file order
     * @param errors  sink for human-readable messages describing each dropped record
     * @return the successfully assembled realms, in file order (dropped records omitted)
     * @author Rowan Crowther
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
            TValue tValue;
            try {
                tValue = TValue.valueOf("TV_" + record.bookNoun().replace(" ", "_").toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add("Record at line: " + line + " has " +
                        "an unknown book noun: " + record.bookNoun());
                continue;
            }

            realms.add(new MagicRealm(name, stat, verb, spellNoun, tValue));
        }

        return realms;
    }
}
