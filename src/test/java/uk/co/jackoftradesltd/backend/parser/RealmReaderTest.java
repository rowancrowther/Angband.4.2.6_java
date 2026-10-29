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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link RealmReader}: file text -> {@code RealmLexer}/
 * {@code RealmGrammar} -> {@link uk.co.jackoftradesltd.backend.parser.realm.RealmParseRecord} ->
 * {@link uk.co.jackoftradesltd.backend.parser.realm.RealmAssembler} -> {@link MagicRealm}.
 *
 * <p>The realm assembler resolves only the primary casting {@link Stats} (via a {@code STAT_}
 * prefix); the verb, spell-noun and book-noun are kept as the free text C stores, so any book-noun
 * loads. There are no cross-file references, so no {@code GameConstants} seeding is required.
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
        // book-noun is kept as the raw text, spaces and all, exactly as lib/gamedata/realm.txt has it.
        assertEquals("magic book", field(arcane, "bookNoun"));
    }

    @Test
    void eachRealmKeepsItsOwnStatVerbNounsFromTheRealFile() throws Exception {
        List<MagicRealm> realms = new RealmReader().parse(REAL_FILE);

        // Expected values are lib/gamedata/realm.txt in the C tree.
        assertEquals(Stats.STAT_WIS, field(byName(realms, "divine"), "stat"));
        assertEquals("recite", field(byName(realms, "divine"), "verb"));
        assertEquals("prayer", field(byName(realms, "divine"), "spellNoun"));
        assertEquals("prayer book", field(byName(realms, "divine"), "bookNoun"));
        assertEquals(Stats.STAT_WIS, field(byName(realms, "nature"), "stat"));
        assertEquals("chant", field(byName(realms, "nature"), "verb"));
        assertEquals("verse", field(byName(realms, "nature"), "spellNoun"));
        assertEquals("nature book", field(byName(realms, "nature"), "bookNoun"));
        assertEquals(Stats.STAT_INT, field(byName(realms, "shadow"), "stat"));
        assertEquals("perform", field(byName(realms, "shadow"), "verb"));
        assertEquals("ritual", field(byName(realms, "shadow"), "spellNoun"));
        assertEquals("shadow book", field(byName(realms, "shadow"), "bookNoun"));
    }

    // ---- A stat that names no stat stops the load ------------------------

    @Test
    void unknownStatStopsTheLoadAsCsParseErrorDoes() {
        // C: parse_realm_stat returns PARSE_ERROR_INVALID_SPELL_STAT, which ends the realm.txt load.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-stat.txt",
                withHeader(1, realm("weird", "ZZZ", "magic book"))));

        assertTrue(e.getMessage().endsWith("has an illegal stat value: ZZZ"), e::getMessage);
    }

    @Test
    void anyBookNounLoadsAsRawText() throws Exception {
        // C's tests/parse/realm.c (test_book_noun0) loads "exercise manual" and "hymnal"; the
        // parser stores the text and nothing in C resolves it, so neither may drop the realm.
        ParseResult<MagicRealm> result = load("free-book.txt", withHeader(2,
                realm("gym", "INT", "exercise manual") + realm("choir", "WIS", "hymnal")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals("exercise manual", field(byName(result.items(), "gym"), "bookNoun"));
        assertEquals("hymnal", field(byName(result.items(), "choir"), "bookNoun"));
    }

    @Test
    void oneBadRealmStopsTheWholeLoadSoNoRealmIsKept() {
        // C quits on the bad stat, so the realm before it is lost with the rest.
        assertThrows(DataFileParseException.class, () -> load("mixed.txt", withHeader(2,
                realm("good", "INT", "magic book") + realm("bad", "ZZZ", "magic book"))));
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
        assertEquals("magic book", field(r, "bookNoun"));
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

    // ---- Added 261018: C-derived expectations (init.c parse_realm_*, finish_parse_realm) ----

    @Test
    void realFileComesBackInReverseFileOrderAsCsRealmsListDoes() throws IOException {
        // C: parse_realm_name pushes each new realm onto the head (realm->next = h) and
        // finish_parse_realm keeps that list, so realm.txt's arcane, divine, nature, shadow
        // is walked as shadow, nature, divine, arcane.
        List<MagicRealm> realms = new RealmReader().parse(REAL_FILE);

        assertEquals(List.of("shadow", "nature", "divine", "arcane"),
                realms.stream().map(MagicRealm::getName).toList());
    }

    @Test
    void realFileGivesTheFullTableOfFlavourFieldsInCsOrder() throws Exception {
        // Expected rows are lib/gamedata/realm.txt in the C tree, in C's list order.
        String[][] expected = {
                {"shadow", "STAT_INT", "perform", "ritual", "shadow book"},
                {"nature", "STAT_WIS", "chant", "verse", "nature book"},
                {"divine", "STAT_WIS", "recite", "prayer", "prayer book"},
                {"arcane", "STAT_INT", "cast", "spell", "magic book"}};
        List<MagicRealm> realms = new RealmReader().parse(REAL_FILE);

        assertEquals(expected.length, realms.size());
        for (int i = 0; i < expected.length; i++) {
            MagicRealm r = realms.get(i);
            assertEquals(expected[i][0], r.getName());
            assertEquals(expected[i][1], field(r, "stat").toString());
            assertEquals(expected[i][2], field(r, "verb"));
            assertEquals(expected[i][3], field(r, "spellNoun"));
            assertEquals(expected[i][4], field(r, "bookNoun"));
        }
    }

    @Test
    void aMissingFileIsAnIOExceptionWhereCQuits() {
        // C: run_parse_realm calls parse_file_quit_not_found, which quits the game.
        assertThrows(IOException.class,
                () -> new RealmReader().parseWithResults(tempDir.resolve("no-such-file.txt").toString()));
    }

    @Test
    void parseDiscardsTheErrorsThatParseWithResultsKeeps() throws IOException {
        // A record-count mismatch is the one soft error left: the realm loads and the message is kept.
        String text = withHeader(2, realm("good", "INT", "magic book"));
        Path file = tempDir.resolve("both-forms.txt");
        Files.writeString(file, text);

        assertEquals(1, new RealmReader().parse(file.toString()).size());
        assertTrue(new RealmReader().parseWithResults(file.toString()).hasErrors());
    }

    @Test
    void theUnknownStatMessageNamesTheRecordsLineTheFileAndTheStat() {
        // Line 1 is the header, line 2 the blank line below it, line 3 the name: of the bad record.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("bad-stat-line.txt",
                "record-count:1\n\n" + realm("weird", "ZZZ", "magic book")));

        assertEquals("Realm starting at line: 3 of file: " + tempDir.resolve("bad-stat-line.txt")
                + " has an illegal stat value: ZZZ", e.getMessage());
    }

    @Test
    void aRepeatedDirectiveKeepsTheLastValueAsCsHandlersOverwrite() throws Exception {
        // C: parse_realm_verb frees the old verb and stores the new one; the other handlers do the same.
        ParseResult<MagicRealm> result = load("repeat.txt", withHeader(1,
                "name:weird\nstat:INT\nverb:cast\nverb:invoke\nspell-noun:spell\nbook-noun:magic book\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("invoke", field(result.items().get(0), "verb"));
    }

    @Test
    void aDirectiveBeforeAnyNameIsAHardErrorAsCsMissingRecordHeaderIs() throws IOException {
        // C: parse_realm_stat returns PARSE_ERROR_MISSING_RECORD_HEADER when no name: has been read.
        ParseResult<MagicRealm> result = load("no-header.txt", withHeader(1,
                "stat:INT\nname:weird\nverb:cast\nspell-noun:spell\nbook-noun:magic book\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aHeaderWithNoRealmsIsAHardErrorWhereCAcceptsAnEmptyFile() throws IOException {
        // C would end with an empty realms list; the grammar's file rule needs at least one realm.
        ParseResult<MagicRealm> result = load("empty.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aRecordCountMismatchMessageGivesBothCounts() throws IOException {
        ParseResult<MagicRealm> result = load("count-text.txt",
                withHeader(3, realm("solo", "INT", "magic book")));

        assertEquals(List.of("record-count header declares 3 records, but file contains 1"), result.errors());
    }

    @Test
    void everyRealStatNameResolvesToItsIndex() throws Exception {
        // C: stat_name_list is STR, INT, WIS, DEX, CON and stat_name_to_idx returns the index.
        String[][] cases = {{"STR", "STAT_STR"}, {"INT", "STAT_INT"}, {"WIS", "STAT_WIS"},
                {"DEX", "STAT_DEX"}, {"CON", "STAT_CON"}};
        for (String[] c : cases) {
            ParseResult<MagicRealm> result = load("stat-" + c[0] + ".txt",
                    withHeader(1, realm("r", c[0], "magic book")));
            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertEquals(c[1], field(result.items().get(0), "stat").toString());
        }
    }

    // ---- Stat resolution as C does it (fixed since docs/Assembler_Issues.md) ----


    @Test
    void aLowerCaseStatLoadsAsCDoesIgnoringCase() throws Exception {
        // C: stat_name_to_idx compares with my_stricmp, so "stat:int" is STAT_INT.
        ParseResult<MagicRealm> result = load("lower-stat.txt",
                withHeader(1, realm("weird", "int", "magic book")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(Stats.STAT_INT, field(result.items().get(0), "stat"));
    }

    @Test
    void statNoneStopsTheLoadAsCDoes() {
        // C: "NONE" is not in stat_name_list, so stat_name_to_idx returns -1 and the parse fails.
        assertThrows(DataFileParseException.class, () -> load("none-stat.txt",
                withHeader(1, realm("weird", "NONE", "magic book"))));
    }

    @Test
    void aRealmWithNoStatIsKeptAsStrengthAsCDoes() throws Exception {
        // C: the realm is zero-filled and parse_realm_stat is never called, so stat stays 0 (STAT_STR).
        ParseResult<MagicRealm> result = load("no-stat.txt", withHeader(1,
                "name:weird\nverb:cast\nspell-noun:spell\nbook-noun:magic book\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(Stats.STAT_STR, field(result.items().get(0), "stat"));
    }

    // ---- Known differences from C: pinned so a fix is noticed (docs/grammar_issues.md) ----


    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsLeadingWhitespace() throws IOException {
        // C: parser_parse skips leading isspace characters before it reads the directive name.
        ParseResult<MagicRealm> result = load("indented.txt", withHeader(1,
                "name:weird\n  stat:INT\nverb:cast\nspell-noun:spell\nbook-noun:magic book\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void textAfterASecondColonOnTheStatLineIsPartOfTheStatHereWhereCIgnoresIt() throws IOException {
        // C: stat is a sym, tokenized on ':', so "stat:INT:extra" reads INT and drops the rest.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("stat-colon.txt",
                withHeader(1, realm("weird", "INT:extra", "magic book"))));

        assertTrue(e.getMessage().endsWith("has an illegal stat value: INT:extra"), e::getMessage);
    }
}
