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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link MonsterBaseReader}: file text -> {@code MonsterBaseLexer}/
 * {@code MonsterBaseGrammar} -> {@code MonsterBaseParseRecord} -> {@code MonsterBaseAssembler} ->
 * {@link MonsterBase}. Where {@code MonsterBaseGrammarTest} stops at the raw record, these pin the
 * assembler half - flag resolution, the {@code pain:} index being looked up in the loaded pain sets,
 * and the skip-and-continue soft-error channel (including the null-return fix, where an unknown pain
 * index now skips the record instead of building a base with a {@code null} pain set).
 *
 * <p>The assembler resolves {@code pain:} via {@link MonsterRegistry#lookupMonsterPain}, which reads
 * the static {@code monsterPains} list normally populated during {@code GameConstants.init()}. That
 * full init is far too heavy for a unit test (and monster-base loading is not even wired into it
 * yet), so {@link #loadPainSets()} loads {@code pain.txt} directly through {@link PainReader} and
 * injects the result into the private static field by reflection, restoring it afterwards so no
 * global state leaks to other suites.
 *
 * @author Rowan Crowther
 */
class MonsterBaseReaderTest {

    private static final String REAL_FILE = "lib/gamedata/monster_base.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";

    /**
     * The value {@code MonsterRegistry.monsterPains} held before this suite overwrote it.
     */
    private static Object savedMonsterPains;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void loadPainSets() throws Exception {
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();

        Field field = MonsterRegistry.class.getDeclaredField("monsterPains");
        field.setAccessible(true);
        savedMonsterPains = field.get(null);
        field.set(null, pains);
    }

    @AfterAll
    static void restorePainSets() throws Exception {
        Field field = MonsterRegistry.class.getDeclaredField("monsterPains");
        field.setAccessible(true);
        field.set(null, savedMonsterPains);
    }

    // ---- fixture helpers -------------------------------------------------

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    private static String entry(String name, char glyph, int pain, String desc, String... flagLines) {
        StringBuilder sb = new StringBuilder("name:").append(name).append("\n")
                .append("glyph:").append(glyph).append("\n")
                .append("pain:").append(pain).append("\n");
        for (String f : flagLines) {
            sb.append("flags:").append(f).append("\n");
        }
        sb.append("desc:").append(desc).append("\n");
        return sb.toString();
    }

    private ParseResult<MonsterBase> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new MonsterBaseReader().parseWithResults(file.toString());
    }

    // ---- Happy path ------------------------------------------------------

    @Test
    void cleanLoadOfTheRealFileYieldsAll56Bases() throws IOException {
        ParseResult<MonsterBase> result = new MonsterBaseReader().parseWithResults(REAL_FILE);

        // Every one of the 56 bases resolves (all pain indices are 1..12) and the header declares
        // the real count, so the load is completely error-free.
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(56, result.items().size());
        assertEquals("ancient dragon", result.items().get(0).getCodeName());
    }

    @Test
    void aWellFormedSyntheticRecordAssemblesWithNoErrors() throws IOException {
        ParseResult<MonsterBase> result =
                load("ok.txt", withHeader(1, entry("orc", 'o', 3, "Orc", "ANIMAL | OPEN_DOOR")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals("orc", result.items().get(0).getCodeName());
    }

    // ---- Assembler soft-error branches -----------------------------------

    @Test
    void unknownRaceFlagIsReportedAndRecordSkipped() throws IOException {
        ParseResult<MonsterBase> result =
                load("bad-flag.txt", withHeader(1, entry("orc", 'o', 3, "Orc", "NOTAFLAG")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("invalid Monster Race flag") && e.contains("NOTAFLAG")),
                result.errors()::toString);
    }

    @Test
    void unknownPainIndexIsReportedAndRecordSkipped() throws IOException {
        // Regression for the null-return fix: pain 99 is out of range, so lookupMonsterPain returns
        // null. The assembler must skip-and-report, NOT build a base with a null pain set.
        ParseResult<MonsterBase> result =
                load("bad-pain.txt", withHeader(1, entry("orc", 'o', 99, "Orc", "ANIMAL")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown pain reference")),
                result.errors()::toString);
    }

    @Test
    void overflowingPainIndexIsReportedAndRecordSkipped() throws IOException {
        // pain: is an INTEGER token, so the only route into the NumberFormatException branch is a
        // value too large for int.
        String oversized = "name:orc\nglyph:o\npain:99999999999999999999\nflags:ANIMAL\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("overflow-pain.txt", withHeader(1, oversized));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid pain reference")),
                result.errors()::toString);
    }

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        ParseResult<MonsterBase> result =
                load("bad-count.txt", withHeader(5, entry("orc", 'o', 3, "Orc", "ANIMAL")));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    // ---- Hard (fail-closed) parse errors ---------------------------------

    @Test
    void aTemplateMissingItsDescIsAHardError() throws IOException {
        // monsterBase requires a desc: after the flags; omitting it is a syntax error, which the hard
        // channel turns into an empty result carrying the error.
        String noDesc = "name:orc\nglyph:o\npain:3\nflags:ANIMAL\n";
        ParseResult<MonsterBase> result = load("no-desc.txt", withHeader(1, noDesc));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<MonsterBase> result =
                load("no-header.txt", entry("orc", 'o', 3, "Orc", "ANIMAL"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
    // ---- The reader against the upstream monster_base.txt ----------------

    /**
     * The pain set a base holds, read by reflection because {@code MonsterBase} has no accessor
     * for it yet.
     */
    private static int painIndexOf(MonsterBase base) throws Exception {
        Field field = MonsterBase.class.getDeclaredField("pain");
        field.setAccessible(true);
        MonsterPain pain = (MonsterPain) field.get(base);
        return pain == null ? -1 : pain.getPainIndex();
    }

    private static String descOf(MonsterBase base) throws Exception {
        Field field = MonsterBase.class.getDeclaredField("description");
        field.setAccessible(true);
        return (String) field.get(base);
    }

    @Test
    void theRealFileComesBackInFileOrderNotInCsReversedChainOrder() throws IOException {
        // C's parse_mon_base_name pushes each record on the head of rb_info, so C's chain starts
        // with Morgoth and ends with "ancient dragon". The reader keeps file order; see
        // docs/Assembler_Issues.md. The expected names are the name: lines of the data file.
        List<String> expected = Files.readAllLines(Path.of(REAL_FILE)).stream()
                .filter(l -> l.startsWith("name:"))
                .map(l -> l.substring("name:".length()))
                .toList();

        List<String> actual = new MonsterBaseReader().parse(REAL_FILE).stream()
                .map(MonsterBase::getCodeName).toList();

        assertEquals(56, expected.size());
        assertEquals(expected, actual);
        assertEquals("Morgoth", actual.get(55));
    }

    @Test
    void flagsLinesAccumulateIntoOneSetAsInCsParseMonBaseFlags() throws IOException {
        // "ancient dragon" in monster_base.txt has five flags: lines; parse_mon_base_flags ORs each
        // into rb->flags, so the base inherits all twelve codes.
        MonsterBase dragon = new MonsterBaseReader().parse(REAL_FILE).get(0);

        List<String> flags = dragon.getFlags().toList().stream().map(Enum::name).sorted().toList();
        assertEquals(List.of("RF_CLEAR_WEB", "RF_DRAGON", "RF_DROP_4", "RF_EVIL", "RF_FORCE_SLEEP",
                "RF_MOVE_BODY", "RF_NO_CONF", "RF_NO_HOLD", "RF_NO_SLEEP", "RF_POWERFUL", "RF_SMART",
                "RF_SPIRIT"), flags);
    }

    @Test
    void playerAndMorgothHaveNoFlagsLinesAndLoadWithAnEmptyFlagSet() throws IOException {
        // The only two templates in the file with no flags: line. C leaves rb->flags zeroed.
        List<MonsterBase> bases = new MonsterBaseReader().parse(REAL_FILE);

        assertTrue(bases.get(54).getFlags().isEmpty());
        assertTrue(bases.get(55).getFlags().isEmpty());
    }

    @Test
    void firstAndLastPainSetsAreTheOnesTheFileNames() throws Exception {
        // "ancient dragon" has pain:1 and Morgoth has pain:12, the lowest and highest serials used.
        List<MonsterBase> bases = new MonsterBaseReader().parse(REAL_FILE);

        assertEquals(1, painIndexOf(bases.get(0)));
        assertEquals(12, painIndexOf(bases.get(55)));
    }

    @Test
    void repeatedDescLinesAreJoinedEndToEndLikeCsStringAppend() throws Exception {
        // parse_mon_base_desc does rb->text = string_append(rb->text, desc); string_append adds no
        // separator, so "ab" + "cd" is "abcd".
        String twoDescs = "name:orc\nglyph:o\npain:1\ndesc:ab\ndesc:cd\n";
        ParseResult<MonsterBase> result = load("two-desc.txt", withHeader(1, twoDescs));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("abcd", descOf(result.items().get(0)));
    }

    @Test
    void aTemplateWithNoFlagsLinesLoadsWithAnEmptyFlagSet() throws IOException {
        String noFlags = "name:orc\nglyph:o\npain:1\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("no-flags.txt", withHeader(1, noFlags));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).getFlags().isEmpty());
    }

    @Test
    void aSkippedRecordDoesNotDisturbTheOthers() throws IOException {
        // C stops at the first bad record; the port skips only it. The two good bases stay, in order.
        String body = entry("first", 'a', 1, "A", "ANIMAL")
                + entry("second", 'b', 1, "B", "NOTAFLAG")
                + entry("third", 'c', 1, "C", "EVIL");
        ParseResult<MonsterBase> result = load("skip.txt", withHeader(3, body));

        assertEquals(List.of("first", "third"),
                result.items().stream().map(MonsterBase::getCodeName).toList());
        assertEquals(1, result.errors().size(), result.errors()::toString);
    }

    @Test
    void duplicateNamesAreBothKeptInFileOrderWhereCHoldsThemReversed() throws Exception {
        // C chains the second "orc" in front of the first, so lookup_monster_base("orc") answers the
        // LATER record. The reader returns [first, second]; a lookup that takes the first match
        // finds the earlier one. See docs/Assembler_Issues.md.
        String body = entry("orc", 'o', 1, "First")
                + entry("orc", 'O', 2, "Second");
        ParseResult<MonsterBase> result = load("dup.txt", withHeader(2, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals("First", descOf(result.items().get(0)));
        assertEquals("Second", descOf(result.items().get(1)));
    }

    @Test
    void aMissingFileThrows() {
        assertThrows(IOException.class,
                () -> new MonsterBaseReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void parseReturnsTheSameBasesAsParseWithResults() throws IOException {
        List<MonsterBase> viaParse = new MonsterBaseReader().parse(REAL_FILE);
        List<MonsterBase> viaResults = new MonsterBaseReader().parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.stream().map(MonsterBase::getCodeName).toList(),
                viaParse.stream().map(MonsterBase::getCodeName).toList());
    }

    // ---- Places the grammar or assembler differs from C ------------------

    @Test
    void aGlyphOfTwoCharactersIsAHardErrorAsInCsFieldTooLong() throws IOException {
        // parser_parse for a "char" field refuses text after the first character unless it is the
        // field separator (PARSE_ERROR_FIELD_TOO_LONG). Both sides reject; C reports it as a
        // parser error, the grammar as a syntax error.
        String body = "name:orc\nglyph:oo\npain:1\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("glyph2.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void anEmptyFlagsValueIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        // "flags ?str flags" is optional and parse_mon_base_flags returns PARSE_ERROR_NONE when
        // parser_hasval is false, so C loads this record with no flags.
        String body = "name:orc\nglyph:o\npain:1\nflags:\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("empty-flags.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void spaceSeparatedFlagsAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        // strtok(flags, " |") in parse_mon_base_flags splits on a space as well as a bar, so C
        // reads ANIMAL and EVIL here.
        String body = "name:orc\nglyph:o\npain:1\nflags:ANIMAL EVIL\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("space-flags.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void directivesOutOfOrderAreAHardErrorHereWhereCTakesAnyOrder() throws IOException {
        // Each parse_mon_base_* handler only needs the record header; C accepts pain: before glyph:.
        String body = "name:orc\npain:1\nglyph:o\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("order.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aTemplateWithNoGlyphIsAHardErrorHereWhereCLeavesTheGlyphZero() throws IOException {
        // C's mem_zalloc leaves d_char as 0 when glyph: is absent; the grammar needs the line.
        String body = "name:orc\npain:1\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("no-glyph.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aLeadingZeroPainIsReadAsDecimalHereWhereCReadsOctal() throws Exception {
        // "pain uint pain" is read with strtoul(tok, &z, 0): base 0, so "010" is octal 8 in C. The
        // lexer's INTEGER is decimal, so the assembler reads 10 and picks the zombie set.
        String body = entry("orc", 'o', 1, "Orc").replace("pain:1", "pain:010");
        ParseResult<MonsterBase> result = load("octal.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(10, painIndexOf(result.items().get(0)));
    }

    @Test
    void aNegativePainIsASoftErrorHereWhereCRejectsItAsNotANumber() throws IOException {
        // strtoul path in parser_parse: a "uint" token starting with '-' is PARSE_ERROR_NOT_NUMBER.
        // INTEGER accepts the minus, so the assembler skips the record as an unknown pain set.
        String body = entry("orc", 'o', 1, "Orc").replace("pain:1", "pain:-1");
        ParseResult<MonsterBase> result = load("neg-pain.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown pain reference")),
                result.errors()::toString);
    }

    @Test
    void aPainSerialBeyondTheLoadedSetsIsASkippedRecordAsInCsOutOfBounds() throws IOException {
        // parse_mon_base_pain returns PARSE_ERROR_OUT_OF_BOUNDS when pain_idx >= z_info->mp_max.
        // pain.txt's highest serial is 12, so mp_max is 13 and 13 is the first rejected value.
        ParseResult<MonsterBase> result =
                load("pain13.txt", withHeader(1, entry("orc", 'o', 13, "Orc")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown pain reference")),
                result.errors()::toString);
    }

    @Test
    void theHighestPainSerialInPainTxtIsAccepted() throws Exception {
        ParseResult<MonsterBase> result =
                load("pain12.txt", withHeader(1, entry("orc", 'o', 12, "Orc")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(12, painIndexOf(result.items().get(0)));
    }

    @Test
    void aGlyphOutsideTheBasicMultilingualPlaneIsASoftErrorHereWhereCHoldsAWideChar() throws IOException {
        // d_char is a wchar_t, which holds U+1F600. A Java char does not, and the glyph reaches the
        // assembler as two chars, so the record is skipped.
        String body = "name:orc\nglyph:😀\npain:1\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("astral.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        // parser_parse skips leading isspace before reading the directive.
        String body = "name:orc\n  glyph:o\npain:1\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("indent.txt", withHeader(1, body));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
