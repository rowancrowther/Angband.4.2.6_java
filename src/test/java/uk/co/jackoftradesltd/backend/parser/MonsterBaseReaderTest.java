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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
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
 * the glyph split, and the fatal errors C raises for each of them. The list comes back in C's chain
 * order, the reverse of file order.
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
        // The list is in C's chain order, so the first record in the file is the last in the list.
        assertEquals("ancient dragon", result.items().get(55).getCodeName());
    }

    @Test
    void aWellFormedSyntheticRecordAssemblesWithNoErrors() throws IOException {
        ParseResult<MonsterBase> result =
                load("ok.txt", withHeader(1, entry("orc", 'o', 3, "Orc", "ANIMAL | OPEN_DOOR")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals("orc", result.items().get(0).getCodeName());
    }

    // ---- Assembler fatal branches ----------------------------------------

    @Test
    void unknownRaceFlagIsAFatalLoadErrorAsInCsInvalidFlag() {
        // grab_flag fails, parse_mon_base_flags returns PARSE_ERROR_INVALID_FLAG and C's load stops.
        // The message names the bad flag, not the glyph, plus the record's line and the file.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-flag.txt", withHeader(1, entry("orc", 'o', 3, "Orc", "NOTAFLAG"))));

        assertTrue(e.getMessage().contains("illegal monster race value: NOTAFLAG"), e::getMessage);
        assertTrue(e.getMessage().contains("starting at line: 2"), e::getMessage);
        assertTrue(e.getMessage().contains("bad-flag.txt"), e::getMessage);
    }

    @Test
    void theBadFlagInAListIsTheOneNamedEvenWhenGoodOnesComeFirst() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-second.txt",
                        withHeader(1, entry("orc", 'o', 3, "Orc", "ANIMAL | NOTAFLAG | EVIL"))));

        assertTrue(e.getMessage().contains("illegal monster race value: NOTAFLAG"), e::getMessage);
    }

    @Test
    void theNoneFlagIsAccepted() throws IOException {
        // NONE is the first row of list-mon-race-flags.h, so r_info_flags holds it and grab_flag
        // accepts it.
        ParseResult<MonsterBase> result =
                load("none-flag.txt", withHeader(1, entry("orc", 'o', 3, "Orc", "NONE")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    void theMaxTerminatorIsNotAFlag() {
        // r_info_flags ends in NULL, not a "MAX" name, so grab_flag rejects it.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("max-flag.txt", withHeader(1, entry("orc", 'o', 3, "Orc", "MAX"))));

        assertTrue(e.getMessage().contains("illegal monster race value: MAX"), e::getMessage);
    }

    @Test
    void aLowerCaseFlagIsRejected() throws IOException {
        // lookup_flag compares names exactly, so "animal" is not ANIMAL. Whether the lexer or the
        // assembler refuses it, no base may come back from it.
        boolean rejected;
        try {
            ParseResult<MonsterBase> result =
                    load("lower-flag.txt", withHeader(1, entry("orc", 'o', 3, "Orc", "animal")));
            rejected = result.items().isEmpty() && result.hasErrors();
        } catch (DataFileParseException e) {
            rejected = true;
        }
        assertTrue(rejected);
    }

    @Test
    void aBadFlagIsFatalWithNoSoftErrorWritten() {
        // The assembler never writes to the soft-error list: a bad flag throws instead.
        List<String> errors = new java.util.ArrayList<>();
        var record = new uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseParseRecord(
                "orc", "Orc", List.of("NOTAFLAG"), "o", "3", "Orc", 7);

        assertThrows(DataFileParseException.class,
                () -> new uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseAssembler()
                        .assemble(List.of(record), errors, "direct.txt"));
        assertTrue(errors.isEmpty());
    }

    @Test
    void unknownPainIndexIsAFatalLoadError() {
        // Pain 99 is out of range, so lookupMonsterPain returns null. The assembler must throw, NOT
        // build a base with a null pain set.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-pain.txt", withHeader(1, entry("orc", 'o', 99, "Orc", "ANIMAL"))));

        assertTrue(e.getMessage().contains("illegal pain value: 99"), e::getMessage);
    }

    @Test
    void overflowingPainIndexIsAFatalLoadError() {
        // pain: is an INTEGER token, so the only route into the NumberFormatException branch is a
        // value too large for int.
        String oversized = "name:orc\nglyph:o\npain:99999999999999999999\nflags:ANIMAL\ndesc:Orc\n";
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("overflow-pain.txt", withHeader(1, oversized)));

        assertTrue(e.getMessage().contains("illegal pain value: 99999999999999999999"), e::getMessage);
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
    void theRealFileComesBackInCsReversedChainOrder() throws IOException {
        // C's parse_mon_base_name pushes each record on the head of rb_info, so C's chain starts
        // with Morgoth and ends with "ancient dragon". The expected names are the name: lines of
        // the data file, read from the bottom up.
        List<String> fileOrder = Files.readAllLines(Path.of(REAL_FILE)).stream()
                .filter(l -> l.startsWith("name:"))
                .map(l -> l.substring("name:".length()))
                .toList();

        List<String> actual = new MonsterBaseReader().parse(REAL_FILE).stream()
                .map(MonsterBase::getCodeName).toList();

        assertEquals(56, fileOrder.size());
        assertEquals(fileOrder.reversed(), actual);
        assertEquals("Morgoth", actual.get(0));
        assertEquals("ancient dragon", actual.get(55));
    }

    @Test
    void flagsLinesAccumulateIntoOneSetAsInCsParseMonBaseFlags() throws IOException {
        // "ancient dragon" in monster_base.txt has five flags: lines; parse_mon_base_flags ORs each
        // into rb->flags, so the base inherits all twelve codes.
        MonsterBase dragon = new MonsterBaseReader().parse(REAL_FILE).get(55);

        List<String> flags = dragon.getFlags().toList().stream().map(Enum::name).sorted().toList();
        assertEquals(List.of("RF_CLEAR_WEB", "RF_DRAGON", "RF_DROP_4", "RF_EVIL", "RF_FORCE_SLEEP",
                "RF_MOVE_BODY", "RF_NO_CONF", "RF_NO_HOLD", "RF_NO_SLEEP", "RF_POWERFUL", "RF_SMART",
                "RF_SPIRIT"), flags);
    }

    @Test
    void playerAndMorgothHaveNoFlagsLinesAndLoadWithAnEmptyFlagSet() throws IOException {
        // The only two templates in the file with no flags: line. C leaves rb->flags zeroed.
        List<MonsterBase> bases = new MonsterBaseReader().parse(REAL_FILE);

        // Chain order: Morgoth is first and player second.
        assertEquals("Morgoth", bases.get(0).getCodeName());
        assertEquals("player", bases.get(1).getCodeName());
        assertTrue(bases.get(0).getFlags().isEmpty());
        assertTrue(bases.get(1).getFlags().isEmpty());
    }

    @Test
    void firstAndLastPainSetsAreTheOnesTheFileNames() throws Exception {
        // "ancient dragon" has pain:1 and Morgoth has pain:12, the lowest and highest serials used.
        List<MonsterBase> bases = new MonsterBaseReader().parse(REAL_FILE);

        assertEquals(1, painIndexOf(bases.get(55)));
        assertEquals(12, painIndexOf(bases.get(0)));
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
    void aBadRecordStopsTheWholeLoadAsInC() {
        // C stops at the first bad record and returns no table. The exception names the second
        // record's starting line: line 1 is the header and each entry here takes five lines.
        String body = entry("first", 'a', 1, "A", "ANIMAL")
                + entry("second", 'b', 1, "B", "NOTAFLAG")
                + entry("third", 'c', 1, "C", "EVIL");
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("skip.txt", withHeader(3, body)));

        assertTrue(e.getMessage().contains("starting at line: 7"), e::getMessage);
        assertTrue(e.getMessage().contains("NOTAFLAG"), e::getMessage);
    }

    @Test
    void duplicateNamesAreBothKeptWithTheLaterRecordFirstAsInCsChain() throws Exception {
        // C chains the second "orc" in front of the first, so lookup_monster_base("orc") answers the
        // LATER record. The list is [second, first], so a first-match search finds the later one.
        String body = entry("orc", 'o', 1, "First")
                + entry("orc", 'O', 2, "Second");
        ParseResult<MonsterBase> result = load("dup.txt", withHeader(2, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals("Second", descOf(result.items().get(0)));
        assertEquals("First", descOf(result.items().get(1)));
    }

    @Test
    void thePainSerialZeroIsFatalHereWhereCPointsAtTheBlankSlot() {
        // Accepted blank-slot deviation: pain_messages[0] is a blank record in C and parse_mon_base_pain
        // accepts 0 (0 < mp_max). The Java list holds only real records, and pain.txt has no serial 0.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("pain0.txt", withHeader(1, entry("orc", 'o', 0, "Orc"))));

        assertTrue(e.getMessage().contains("illegal pain value: 0"), e::getMessage);
    }

    @Test
    void aSupplementaryGlyphLeavesTheNarrowHalfAtNulWhileAnAsciiOneLeavesTheWideHalfAtZero()
            throws IOException {
        ParseResult<MonsterBase> result = load("both.txt", withHeader(2,
                entry("narrow", 'n', 1, "N")
                        + "name:wide\nglyph:🐉\npain:1\ndesc:W\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        // Chain order: the wide record, second in the file, comes first.
        MonsterBase wide = result.items().get(0);
        MonsterBase narrow = result.items().get(1);
        assertEquals("wide", wide.getCodeName());
        assertEquals('\0', wide.getDefaultMonsterChar());
        assertEquals(0x1F409, wide.getWideMonsterChar());
        assertEquals('n', narrow.getDefaultMonsterChar());
        assertEquals(0, narrow.getWideMonsterChar());
    }

    @Test
    void aGlyphMessageNamesTheLineFileAndValue() {
        // Reached directly, as the grammar already refuses a two-character glyph.
        var record = new uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseParseRecord(
                "orc", "Orc", List.of(), "oo", "1", "Orc", 9);
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseAssembler()
                        .assemble(List.of(record), new java.util.ArrayList<>(), "direct.txt"));

        assertTrue(e.getMessage().contains("starting at line: 9"), e::getMessage);
        assertTrue(e.getMessage().contains("direct.txt"), e::getMessage);
        assertTrue(e.getMessage().contains("illegal glyph value: oo"), e::getMessage);
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
    void aNegativePainIsAFatalLoadErrorHereWhereCRejectsItAsNotANumber() {
        // strtoul path in parser_parse: a "uint" token starting with '-' is PARSE_ERROR_NOT_NUMBER.
        // INTEGER accepts the minus, so the assembler throws because no pain set has that serial.
        String body = entry("orc", 'o', 1, "Orc").replace("pain:1", "pain:-1");
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("neg-pain.txt", withHeader(1, body)));

        assertTrue(e.getMessage().contains("illegal pain value: -1"), e::getMessage);
    }

    @Test
    void aPainSerialBeyondTheLoadedSetsIsAFatalLoadErrorAsInCsOutOfBounds() {
        // parse_mon_base_pain returns PARSE_ERROR_OUT_OF_BOUNDS when pain_idx >= z_info->mp_max.
        // pain.txt's highest serial is 12, so mp_max is 13 and 13 is the first rejected value.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("pain13.txt", withHeader(1, entry("orc", 'o', 13, "Orc"))));

        assertTrue(e.getMessage().contains("illegal pain value: 13"), e::getMessage);
    }

    @Test
    void theHighestPainSerialInPainTxtIsAccepted() throws Exception {
        ParseResult<MonsterBase> result =
                load("pain12.txt", withHeader(1, entry("orc", 'o', 12, "Orc")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(12, painIndexOf(result.items().get(0)));
    }

    @Test
    void aGlyphOutsideTheBasicMultilingualPlaneIsKeptWholeInTheWideHalf() throws IOException {
        // d_char is a wchar_t, which holds U+1F600, so C loads the record. A Java char does not hold
        // it, so the port keeps it in the wide half and leaves the narrow half at '\0'.
        String body = "name:orc\nglyph:😀\npain:1\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("astral.txt", withHeader(1, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        MonsterBase base = result.items().get(0);
        assertTrue(base.isWideChar());
        assertEquals(0x1F600, base.getWideMonsterChar());
        assertEquals('\0', base.getDefaultMonsterChar());
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
