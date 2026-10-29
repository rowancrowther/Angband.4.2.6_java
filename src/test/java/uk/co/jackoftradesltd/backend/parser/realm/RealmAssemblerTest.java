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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RealmAssembler}, feeding {@link RealmParseRecord}s straight to
 * {@code assemble} so the stat handling is exercised without the lexer and grammar in the way.
 *
 * <p>Expected values come from C's {@code parse_realm_name}, {@code parse_realm_stat},
 * {@code parse_realm_verb}, {@code parse_realm_spell_noun}, {@code parse_realm_book_noun} and
 * {@code finish_parse_realm} ({@code init.c}) and from {@code stat_name_to_idx} and
 * {@code stat_name_list} ({@code player.c}). C pushes each new realm onto the head of the list,
 * zero-fills it (so an absent {@code stat:} is {@code STAT_STR}), compares the stat text with
 * {@code my_stricmp} against {@code STR, INT, WIS, DEX, CON, MAX}, and ends the load with
 * {@code PARSE_ERROR_INVALID_SPELL_STAT} on anything else. The assembler has no soft errors, so each
 * failure case asserts {@link DataFileParseException} and the wording of its message.
 *
 * <p>{@link MagicRealm} exposes only {@code getName()} and {@code getStat()}, so the verb, spell-noun
 * and book-noun are read reflectively via {@link #field}.
 *
 * <p>Class RealmAssemblerTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
class RealmAssemblerTest {

    private static final String FILE = "realm.txt";

    // ---- helpers ---------------------------------------------------------

    private static RealmParseRecord record(String name, String stat, int line) {
        return new RealmParseRecord(name, stat, "cast", "spell", "magic book", line);
    }

    private static List<MagicRealm> assemble(List<String> errors, RealmParseRecord... records) {
        return new RealmAssembler().assemble(List.of(records), errors, FILE);
    }

    private static Stats statOf(String stat) {
        List<String> errors = new ArrayList<>();
        List<MagicRealm> realms = assemble(errors, record("r", stat, 1));
        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, realms.size());
        return realms.get(0).getStat();
    }

    private static DataFileParseException refused(RealmParseRecord... records) {
        return assertThrows(DataFileParseException.class,
                () -> assemble(new ArrayList<>(), records));
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(MagicRealm r, String name) throws Exception {
        Field f = MagicRealm.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(r);
    }

    // ---- the stat, as stat_name_to_idx resolves it -----------------------

    @Test
    void everyRealStatNameResolvesToItsConstant() {
        // C: stat_name_list is STR, INT, WIS, DEX, CON, then MAX.
        assertEquals(Stats.STAT_STR, statOf("STR"));
        assertEquals(Stats.STAT_INT, statOf("INT"));
        assertEquals(Stats.STAT_WIS, statOf("WIS"));
        assertEquals(Stats.STAT_DEX, statOf("DEX"));
        assertEquals(Stats.STAT_CON, statOf("CON"));
    }

    @Test
    void theStatIsMatchedIgnoringCaseAsMyStricmpDoes() {
        assertEquals(Stats.STAT_INT, statOf("int"));
        assertEquals(Stats.STAT_WIS, statOf("Wis"));
        assertEquals(Stats.STAT_DEX, statOf("dEX"));
        assertEquals(Stats.STAT_CON, statOf("con"));
        assertEquals(Stats.STAT_STR, statOf("str"));
    }

    @Test
    void theCaseFoldingDoesNotDependOnTheDefaultLocale() {
        // Under Turkish rules "int".toUpperCase() is "İNT"; C's toupper runs in the C locale.
        Locale saved = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals(Stats.STAT_INT, statOf("int"));
            assertEquals(Stats.STAT_WIS, statOf("wis"));
        } finally {
            Locale.setDefault(saved);
        }
    }

    @Test
    void maxIsAcceptedBecauseCsStatNameListEndsWithIt() {
        // C: the list ends "MAX", so stat_name_to_idx("MAX") is STAT_MAX (5) and the parse goes on.
        assertEquals(Stats.STAT_MAX, statOf("MAX"));
        assertEquals(Stats.STAT_MAX, statOf("max"));
    }

    @Test
    void anAbsentStatReadsAsStrengthBecauseCZeroFillsTheRealm() {
        // C: parse_realm_name uses mem_zalloc and parse_realm_stat never runs, so stat is 0, STAT_STR.
        // The grammar hands over "" for the line that is not there.
        assertEquals(Stats.STAT_STR, statOf(""));
    }

    @Test
    void noneIsRefusedBecauseCsStatNameListHasNoSuchName() {
        DataFileParseException e = refused(record("r", "NONE", 5));

        assertTrue(e.getMessage().endsWith("has an illegal stat value: NONE"), e::getMessage);
        refused(record("r", "none", 5));
    }

    @Test
    void anUnknownStatIsRefused() {
        assertTrue(refused(record("r", "ZZZ", 1)).getMessage().endsWith("illegal stat value: ZZZ"));
        // the constant's own name is not a stat name: C compares against the bare "STR"
        refused(record("r", "STAT_STR", 1));
        // a stat name with a character added or taken away does not match
        refused(record("r", "STRR", 1));
        refused(record("r", "ST", 1));
        // C compares the whole string, so a name with surrounding blanks is not a stat
        refused(record("r", " INT", 1));
        refused(record("r", "INT ", 1));
    }

    @Test
    void theFailureMessageNamesTheRecordsLineTheFileAndTheStat() {
        DataFileParseException e = refused(record("weird", "ZZZ", 12));

        assertEquals("Realm starting at line: 12 of file: realm.txt has an illegal stat value: ZZZ",
                e.getMessage());
    }

    @Test
    void aBadStatStopsTheLoadAtTheFirstBadRecord() {
        // C quits at the first bad stat, so the realms before it are lost and the ones after never read.
        DataFileParseException e = refused(record("a", "INT", 3), record("b", "ZZZ", 8),
                record("c", "YYY", 13));

        assertTrue(e.getMessage().contains("line: 8 "), e::getMessage);
        assertTrue(e.getMessage().endsWith("ZZZ"), e::getMessage);
    }

    @Test
    void theErrorsListIsNeverWrittenTo() {
        List<String> errors = new ArrayList<>();
        assemble(errors, record("a", "INT", 1), record("b", "WIS", 6));
        assertTrue(errors.isEmpty());

        assertThrows(DataFileParseException.class, () -> new RealmAssembler()
                .assemble(List.of(record("c", "ZZZ", 1)), errors, FILE));
        assertTrue(errors.isEmpty(), errors::toString);
    }

    // ---- order, count and the free text ----------------------------------

    @Test
    void theRealmsComeBackInReverseFileOrderAsCsHeadPushesLeaveThem() {
        // C: parse_realm_name sets realm->next to the list built so far, so the last record is first.
        List<MagicRealm> realms = assemble(new ArrayList<>(),
                record("arcane", "INT", 1), record("divine", "WIS", 7), record("nature", "WIS", 13));

        assertEquals(List.of("nature", "divine", "arcane"),
                realms.stream().map(MagicRealm::getName).toList());
    }

    @Test
    void noRecordsGiveNoRealms() {
        assertTrue(assemble(new ArrayList<>()).isEmpty());
    }

    @Test
    void aRepeatedNameIsNotMergedBecauseCStartsANewRealmEachTime() {
        // C: parse_realm_name allocates a fresh realm for every name: line, and nothing de-duplicates.
        List<MagicRealm> realms = assemble(new ArrayList<>(),
                record("arcane", "INT", 1), record("arcane", "WIS", 7));

        assertEquals(2, realms.size());
        assertEquals(Stats.STAT_WIS, realms.get(0).getStat());
        assertEquals(Stats.STAT_INT, realms.get(1).getStat());
    }

    @Test
    void theNameAndTheThreeTextsPassThroughAsGiven() throws Exception {
        // C stores the text as written; tests/parse/realm.c loads "exercise manual" as a book-noun.
        MagicRealm realm = assemble(new ArrayList<>(), new RealmParseRecord(
                "Odd Realm", "CON", "Cast  Twice", "Spell-Thing", "exercise manual", 4)).get(0);

        assertEquals("Odd Realm", realm.getName());
        assertEquals("Cast  Twice", field(realm, "verb"));
        assertEquals("Spell-Thing", field(realm, "spellNoun"));
        assertEquals("exercise manual", field(realm, "bookNoun"));
    }

    @Test
    void anEmptyTextIsPassedThroughAsEmptyWhereCHoldsNull() throws Exception {
        // The grammar seeds "" for an absent verb:, spell-noun: or book-noun:, where C leaves NULL
        // (docs/grammar_issues.md); the assembler does not turn it into null.
        MagicRealm realm = assemble(new ArrayList<>(),
                new RealmParseRecord("r", "INT", "", "", "", 2)).get(0);

        assertEquals("", field(realm, "verb"));
        assertEquals("", field(realm, "spellNoun"));
        assertEquals("", field(realm, "bookNoun"));
    }

    @Test
    void theRecordsOwnStatDoesNotLeakIntoTheNextRealm() {
        // C zero-fills every realm separately, so a realm with no stat: is STR even after an INT realm.
        List<MagicRealm> realms = assemble(new ArrayList<>(),
                record("first", "INT", 1), record("second", "", 7));

        assertEquals(Stats.STAT_STR, realms.get(0).getStat());
        assertEquals(Stats.STAT_INT, realms.get(1).getStat());
    }
}
