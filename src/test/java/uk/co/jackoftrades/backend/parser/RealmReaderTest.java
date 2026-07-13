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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link RealmReader}: file text -> {@code RealmLexer}/
 * {@code RealmGrammar} -> {@link uk.co.jackoftrades.backend.parser.realm.RealmParseRecord} ->
 * {@link uk.co.jackoftrades.backend.parser.realm.RealmAssembler} -> {@link MagicRealm}.
 *
 * <p>The realm assembler resolves entirely against compile-time enums — the primary casting
 * {@link Stats} (via a {@code STAT_} prefix) and the spellbook {@link TValue} (via a {@code TV_}
 * prefix over the underscored, upper-cased {@code book-noun} text) — with no cross-file references,
 * so no {@code GameConstants} seeding is required.
 *
 * <p>{@link MagicRealm} exposes only {@code getName()}, so the remaining assertions read its private
 * fields reflectively via {@link #field}.
 *
 * @author Rowan Crowther
 */
class RealmReaderTest {

    private static final String REAL_FILE = "lib/gamedata/realm.txt";

    @TempDir
    Path tempDir;

    // ---- fixture + reflection helpers ------------------------------------

    private ParseResult<MagicRealm> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new RealmReader().parseWithResults(file.toString());
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * A full realm record with every mandatory field, only the named parts substituted.
     */
    private static String realm(String name, String stat, String book) {
        return "name:" + name + "\nstat:" + stat + "\nverb:cast\n"
                + "spell-noun:spell\nbook-noun:" + book + "\n";
    }

    private static MagicRealm byName(List<MagicRealm> realms, String name) {
        return realms.stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(MagicRealm r, String name) throws Exception {
        Field f = MagicRealm.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(r);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllFourRealmsWithNoErrors() throws IOException {
        ParseResult<MagicRealm> result = new RealmReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(4, result.items().size());
    }

    @Test
    void everyFieldResolvesForTheArcaneRealm() throws Exception {
        MagicRealm arcane = byName(new RealmReader().parse(REAL_FILE), "arcane");
        assertNotNull(arcane);

        assertEquals(Stats.STAT_INT, field(arcane, "stat"));
        assertEquals("cast", field(arcane, "verb"));
        assertEquals("spell", field(arcane, "spellNoun"));
        // book-noun "magic book" -> TV_MAGIC_BOOK: proves the spaced free-text resolves to a tval.
        assertEquals(TValue.TV_MAGIC_BOOK, field(arcane, "book"));
    }

    @Test
    void eachRealmMapsToItsOwnStatAndBookType() throws Exception {
        List<MagicRealm> realms = new RealmReader().parse(REAL_FILE);

        assertEquals(Stats.STAT_WIS, field(byName(realms, "divine"), "stat"));
        assertEquals(TValue.TV_PRAYER_BOOK, field(byName(realms, "divine"), "book"));
        assertEquals(Stats.STAT_WIS, field(byName(realms, "nature"), "stat"));
        assertEquals(TValue.TV_NATURE_BOOK, field(byName(realms, "nature"), "book"));
        assertEquals(Stats.STAT_INT, field(byName(realms, "shadow"), "stat"));
        assertEquals(TValue.TV_SHADOW_BOOK, field(byName(realms, "shadow"), "book"));
    }

    // ---- Soft errors: drop the record, keep the rest ---------------------

    @Test
    void unknownStatIsReportedAndTheRealmDropped() throws IOException {
        ParseResult<MagicRealm> result = load("bad-stat.txt",
                withHeader(1, realm("weird", "ZZZ", "magic book")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown stat")),
                result.errors()::toString);
    }

    @Test
    void unknownBookNounIsReportedAndTheRealmDropped() throws IOException {
        ParseResult<MagicRealm> result = load("bad-book.txt",
                withHeader(1, realm("weird", "INT", "not a book")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("unknown book noun")),
                result.errors()::toString);
    }

    @Test
    void oneBadRealmIsDroppedButTheOthersStillLoad() throws IOException {
        ParseResult<MagicRealm> result = load("mixed.txt", withHeader(2,
                realm("good", "INT", "magic book") + realm("bad", "ZZZ", "magic book")));

        assertEquals(1, result.items().size());
        assertNotNull(byName(result.items(), "good"));
        assertNull(byName(result.items(), "bad"));
        assertTrue(result.hasErrors());
    }

    // ---- Record count + hard errors --------------------------------------

    @Test
    void recordCountMismatchIsReportedButTheValidRealmStillLoads() throws IOException {
        ParseResult<MagicRealm> result = load("bad-count.txt",
                withHeader(9, realm("solo", "INT", "magic book")));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void aMissingOptionalFieldDefaultsToEmpty() throws Exception {
        // Only name: is required to open a record; the four flavour directives are optional. An
        // omitted verb is not a parse error - it defaults to the empty string seeded by the
        // grammar's @init, and the assembler does not validate verb, so the realm still loads.
        ParseResult<MagicRealm> result = load("missing-field.txt", withHeader(1,
                "name:weird\nstat:INT\nspell-noun:spell\nbook-noun:magic book\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals("", field(result.items().get(0), "verb"));
    }

    @Test
    void aRecordWithOnlyANameIsAHardError() throws IOException {
        // The realm rule is name followed by one-or-more flavour directives, so a bare name: with
        // no stat/verb/spell-noun/book-noun cannot complete the record.
        ParseResult<MagicRealm> result = load("name-only.txt", withHeader(1, "name:weird\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Grammar/lexer architecture --------------------------------------

    @Test
    void commentsAndBlankLinesAreSkippedEvenMidRecord() throws IOException {
        // COMMENT/EOL (from the CommentsAndEol import) are on the skip channel, so they may fall
        // anywhere between tokens — including between two directives of the same record.
        ParseResult<MagicRealm> result = load("comments.txt",
                "record-count:1\n"
                        + "# a leading comment\n\n"
                        + "name:mystic\nstat:INT\n"
                        + "# a comment between directives\n"
                        + "verb:cast\nspell-noun:spell\nbook-noun:magic book\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals("mystic", result.items().get(0).getName());
    }

    @Test
    void crlfLineEndingsParseCleanly() throws IOException {
        // FREE_TEXT_EOL ('\r'* '\n') and EOL (' '* '\r'? '\n') both swallow the carriage return,
        // so a Windows-authored file must parse identically to a unix one.
        ParseResult<MagicRealm> result = load("crlf.txt",
                "record-count:1\r\n"
                        + "name:arcane\r\nstat:INT\r\nverb:cast\r\n"
                        + "spell-noun:spell\r\nbook-noun:magic book\r\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals("arcane", result.items().get(0).getName());
    }

    @Test
    void freeTextValuesPreserveInternalSpacesAndCase() throws Exception {
        // The FREE_TEXT-mode STRING captures the whole value verbatim — multi-word, mixed-case,
        // untrimmed of interior spacing — which is why name/verb/spell-noun are free text not FLAGs.
        ParseResult<MagicRealm> result = load("free-text.txt", withHeader(1,
                "name:High Mystic\nstat:INT\nverb:solemnly intone\n"
                        + "spell-noun:arcane whisper\nbook-noun:magic book\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        MagicRealm r = result.items().get(0);
        assertEquals("High Mystic", r.getName());
        assertEquals("solemnly intone", field(r, "verb"));
        assertEquals("arcane whisper", field(r, "spellNoun"));
    }

    @Test
    void fieldsAreAcceptedInAnyOrderAfterName() throws Exception {
        // After name:, the four flavour directives may appear in any order; here verb precedes
        // stat, and every field must still resolve correctly.
        ParseResult<MagicRealm> result = load("reordered.txt", withHeader(1,
                "name:weird\nverb:cast\nstat:INT\nspell-noun:spell\nbook-noun:magic book\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        MagicRealm r = result.items().get(0);
        assertEquals("weird", r.getName());
        assertEquals(Stats.STAT_INT, field(r, "stat"));
        assertEquals("cast", field(r, "verb"));
        assertEquals(TValue.TV_MAGIC_BOOK, field(r, "book"));
    }

    @Test
    void anEmptyValueAfterADirectiveIsAHardError() throws IOException {
        // STRING is one-or-more chars, so "verb:" with nothing after it produces no STRING token
        // and the verb rule (VERB STRING) cannot complete.
        ParseResult<MagicRealm> result = load("empty-value.txt", withHeader(1,
                "name:weird\nstat:INT\nverb:\nspell-noun:spell\nbook-noun:magic book\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
