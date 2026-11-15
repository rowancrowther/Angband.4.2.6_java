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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
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
 * <p>Test class MonsterBaseReaderTest coded before 261105, updated on 261105 for the removal of the
 * soft-error path, and on 261113 for the unordered directives (Stage 7a) and the blank-and-bar flag
 * separators (Stage 7d).
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
        assertEquals(56, result.items().size());
        // The list is in C's chain order, so the first record in the file is the last in the list.
        assertEquals("ancient dragon", result.items().get(55).getCodeName());
    }

    @Test
    void aWellFormedSyntheticRecordAssemblesWithNoErrors() throws IOException {
        ParseResult<MonsterBase> result =
                load("ok.txt", entry("orc", 'o', 3, "Orc", "ANIMAL | OPEN_DOOR"));

        assertEquals(1, result.items().size());
        assertEquals("orc", result.items().get(0).getCodeName());
    }

    // ---- Assembler fatal branches ----------------------------------------

    @Test
    void unknownRaceFlagIsAFatalLoadErrorAsInCsInvalidFlag() {
        // grab_flag fails, parse_mon_base_flags returns PARSE_ERROR_INVALID_FLAG and C's load stops.
        // The message names the bad flag, not the glyph, plus the record's line and the file.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-flag.txt", entry("orc", 'o', 3, "Orc", "NOTAFLAG")));

        assertTrue(e.getMessage().contains("illegal monster race value: NOTAFLAG"), e::getMessage);
        assertTrue(e.getMessage().contains("starting at line: 1"), e::getMessage);
        assertTrue(e.getMessage().contains("bad-flag.txt"), e::getMessage);
    }

    @Test
    void theBadFlagInAListIsTheOneNamedEvenWhenGoodOnesComeFirst() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-second.txt",
                        entry("orc", 'o', 3, "Orc", "ANIMAL | NOTAFLAG | EVIL")));

        assertTrue(e.getMessage().contains("illegal monster race value: NOTAFLAG"), e::getMessage);
    }

    @Test
    void theNoneFlagIsAccepted() throws IOException {
        // NONE is the first row of list-mon-race-flags.h, so r_info_flags holds it and grab_flag
        // accepts it.
        ParseResult<MonsterBase> result =
                load("none-flag.txt", entry("orc", 'o', 3, "Orc", "NONE"));

        assertEquals(1, result.items().size());
    }

    @Test
    void theMaxTerminatorIsNotAFlag() {
        // r_info_flags ends in NULL, not a "MAX" name, so grab_flag rejects it.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("max-flag.txt", entry("orc", 'o', 3, "Orc", "MAX")));

        assertTrue(e.getMessage().contains("illegal monster race value: MAX"), e::getMessage);
    }

    @Test
    void aLowerCaseFlagIsRejected() throws IOException {
        // lookup_flag compares names exactly, so "animal" is not ANIMAL. Whether the lexer or the
        // assembler refuses it, the load must stop.
        boolean rejected;
        try {
            load("lower-flag.txt", entry("orc", 'o', 3, "Orc", "animal"));
            rejected = false;
        } catch (DataFileParseException | ParseCancellationException e) {
            rejected = true;
        }
        assertTrue(rejected);
    }

    @Test
    void aBadFlagIsFatal() {
        // A bad flag throws DataFileParseException and ends the load.
        var record = new uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseParseRecord(
                "orc", "Orc", List.of("NOTAFLAG"), "o", "3", "Orc", 7);

        assertThrows(DataFileParseException.class,
                () -> new uk.co.jackoftradesltd.backend.parser.monsterbase.MonsterBaseAssembler()
                        .assemble(List.of(record), "direct.txt"));
    }

    @Test
    void unknownPainIndexIsAFatalLoadError() {
        // Pain 99 is out of range, so lookupMonsterPain returns null. The assembler must throw, NOT
        // build a base with a null pain set.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("bad-pain.txt", entry("orc", 'o', 99, "Orc", "ANIMAL")));

        assertTrue(e.getMessage().contains("illegal pain value: 99"), e::getMessage);
    }

    @Test
    void overflowingPainIndexIsAFatalLoadError() {
        // pain: is an INTEGER token, so the only route into the NumberFormatException branch is a
        // value too large for int.
        String oversized = "name:orc\nglyph:o\npain:99999999999999999999\nflags:ANIMAL\ndesc:Orc\n";
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("overflow-pain.txt", oversized));

        assertTrue(e.getMessage().contains("illegal pain value: 99999999999999999999"), e::getMessage);
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

    /**
     * The in-game name a base holds, read by reflection because {@code MonsterBase} has no accessor
     * for it yet. It is the same text as the description, so a test that reads both is checking that
     * the two stay equal.
     *
     * <p>Function inGameNameOf coded on 261113, commented in full on 261113.
     */
    private static String inGameNameOf(MonsterBase base) throws Exception {
        Field field = MonsterBase.class.getDeclaredField("inGameName");
        field.setAccessible(true);
        return (String) field.get(base);
    }

    /**
     * The race flags a base holds, as sorted enum names, so a test can compare them without
     * depending on the order the flags were written in.
     *
     * <p>Function flagNamesOf coded on 261113, commented in full on 261113.
     */
    private static List<String> flagNamesOf(MonsterBase base) {
        return base.getFlags().toList().stream().map(Enum::name).sorted().toList();
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
        ParseResult<MonsterBase> result = load("two-desc.txt", twoDescs);

        assertEquals("abcd", descOf(result.items().get(0)));
    }

    @Test
    void aTemplateWithNoFlagsLinesLoadsWithAnEmptyFlagSet() throws IOException {
        String noFlags = "name:orc\nglyph:o\npain:1\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("no-flags.txt", noFlags);

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
                () -> load("skip.txt", body));

        assertTrue(e.getMessage().contains("starting at line: 6"), e::getMessage);
        assertTrue(e.getMessage().contains("NOTAFLAG"), e::getMessage);
    }

    @Test
    void duplicateNamesAreBothKeptWithTheLaterRecordFirstAsInCsChain() throws Exception {
        // C chains the second "orc" in front of the first, so lookup_monster_base("orc") answers the
        // LATER record. The list is [second, first], so a first-match search finds the later one.
        String body = entry("orc", 'o', 1, "First")
                + entry("orc", 'O', 2, "Second");
        ParseResult<MonsterBase> result = load("dup.txt", body);

        assertEquals(2, result.items().size());
        assertEquals("Second", descOf(result.items().get(0)));
        assertEquals("First", descOf(result.items().get(1)));
    }

    @Test
    void thePainSerialZeroIsFatalHereWhereCPointsAtTheBlankSlot() {
        // Accepted blank-slot deviation: pain_messages[0] is a blank record in C and parse_mon_base_pain
        // accepts 0 (0 < mp_max). The Java list holds only real records, and pain.txt has no serial 0.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("pain0.txt", entry("orc", 'o', 0, "Orc")));

        assertTrue(e.getMessage().contains("illegal pain value: 0"), e::getMessage);
    }

    @Test
    void aSupplementaryGlyphLeavesTheNarrowHalfAtNulWhileAnAsciiOneLeavesTheWideHalfAtZero()
            throws IOException {
        ParseResult<MonsterBase> result = load("both.txt", entry("narrow", 'n', 1, "N")
                        + "name:wide\nglyph:🐉\npain:1\ndesc:W\n");

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
                        .assemble(List.of(record), "direct.txt"));

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
        assertThrows(ParseCancellationException.class, () -> load("glyph2.txt", body));

    }

    /**
     * C declares {@code flags} as {@code ?str}, so {@code parse_mon_base_flags} returns
     * {@code PARSE_ERROR_NONE} for an empty value and the base keeps no flags. A lone blank after the
     * colon is a value, and {@code strtok(flags, " |")} splits it into no names, with the same result.
     *
     * <p>Function anEmptyFlagsValueLoadsWithNoFlagsAsInC coded on 261113, commented in full on
     * 261113 (renamed and flipped from anEmptyFlagsValueIsAHardErrorHereWhereCAcceptsIt on 261113).
     */
    @Test
    void anEmptyFlagsValueLoadsWithNoFlagsAsInC() throws IOException {
        for (String value : List.of("", " ")) {
            String body = "name:orc\nglyph:o\npain:1\nflags:" + value + "\ndesc:Orc\n";
            ParseResult<MonsterBase> result = load("empty-flags.txt", body);

            assertEquals(1, result.items().size(), "flags:[" + value + "]");
            assertTrue(result.items().get(0).getFlags().isEmpty(), "flags:[" + value + "]");
        }
    }

    /**
     * {@code parse_mon_base_flags} splits the value with {@code strtok(flags, " |")}, so the
     * delimiters are a blank or a bar and any run of them counts as one. A blank on its own, several
     * blanks, a bar with any number of blanks either side, and runs of bars all separate the same two
     * names, and a bar or blank after the last name adds nothing.
     *
     * <p>Function aFlagListSplitOnAnyRunOfBlanksAndBarsLoadsAsInC coded on 261113, commented in full
     * on 261113 (renamed and flipped from spaceSeparatedFlagsAreAHardErrorHereWhereCSplitsOnSpaceToo
     * on 261113).
     */
    @Test
    void aFlagListSplitOnAnyRunOfBlanksAndBarsLoadsAsInC() throws IOException {
        for (String separator : List.of(" ", "    ", "|", " | ", "||", " |  ", "  |  ", " | | ",
                "| |", " |  | ")) {
            String body = "name:orc\nglyph:o\npain:1\nflags:ANIMAL" + separator + "EVIL\ndesc:Orc\n";
            ParseResult<MonsterBase> result = load("separators.txt", body);

            assertEquals(List.of("RF_ANIMAL", "RF_EVIL"), flagNamesOf(result.items().get(0)),
                    "separator [" + separator + "]");
        }
        for (String tail : List.of("|", " |", " | ", " ")) {
            String body = "name:orc\nglyph:o\npain:1\nflags:ANIMAL" + tail + "\ndesc:Orc\n";
            ParseResult<MonsterBase> result = load("tails.txt", body);

            assertEquals(List.of("RF_ANIMAL"), flagNamesOf(result.items().get(0)),
                    "tail [" + tail + "]");
        }
    }

    /**
     * Each {@code parse_mon_base_*} handler needs only the record header, so C takes {@code pain:}
     * before {@code glyph:} and any other order.
     *
     * <p>Function directivesOutOfOrderLoadAsInC coded on 261113, commented in full on 261113
     * (renamed and flipped from directivesOutOfOrderAreAHardErrorHereWhereCTakesAnyOrder on 261113).
     */
    @Test
    void directivesOutOfOrderLoadAsInC() throws Exception {
        ParseResult<MonsterBase> result =
                load("order.txt", "name:orc\npain:1\nglyph:o\ndesc:Orc\n");

        MonsterBase base = result.items().get(0);
        assertEquals('o', base.getDefaultMonsterChar());
        assertEquals(1, painIndexOf(base));
        assertEquals("Orc", descOf(base));
    }

    /**
     * C's {@code mem_zalloc} leaves {@code d_char} at 0 when {@code glyph:} is absent, and the load
     * goes on. Both halves of the port's glyph stay at zero.
     *
     * <p>Function aTemplateWithNoGlyphLoadsWithGlyphZeroAsInC coded on 261113, commented in full on
     * 261113 (renamed and flipped from aTemplateWithNoGlyphIsAHardErrorHereWhereCLeavesTheGlyphZero
     * on 261113).
     */
    @Test
    void aTemplateWithNoGlyphLoadsWithGlyphZeroAsInC() throws IOException {
        ParseResult<MonsterBase> result = load("no-glyph.txt", "name:orc\npain:1\ndesc:Orc\n");

        MonsterBase base = result.items().get(0);
        assertEquals('\0', base.getDefaultMonsterChar());
        assertEquals(0, base.getWideMonsterChar());
    }

    /**
     * C requires no directive after {@code name:}: {@code parse_mon_base_desc} runs only when a
     * {@code desc:} line is present, so a base without one keeps the NULL {@code text} that
     * {@code mem_zalloc} gave it. The port holds {@code null} for the description and for the
     * in-game name, which C's one {@code text} member fills.
     *
     * <p>Function aTemplateWithNoDescLoadsWithANullDescriptionAsInC coded on 261113, commented in
     * full on 261113 (renamed and flipped from aTemplateMissingItsDescIsAHardError on 261113, then
     * from aTemplateWithNoDescLoadsWithAnEmptyDescriptionHereWhereCLeavesTheTextNull on 261113 once
     * the grammar and the assembler passed an absent desc through as null).
     */
    @Test
    void aTemplateWithNoDescLoadsWithANullDescriptionAsInC() throws Exception {
        ParseResult<MonsterBase> result =
                load("no-desc.txt", "name:orc\nglyph:o\npain:3\nflags:ANIMAL\n");

        assertEquals(1, result.items().size());
        assertNull(descOf(result.items().get(0)));
        assertNull(inGameNameOf(result.items().get(0)));
    }

    /**
     * C's {@code parse_mon_base_name} allocates a zeroed record and none of the other handlers is
     * required, so a base of a name alone is legal: no flags, glyph 0, no pain set and no text.
     *
     * <p>Function aNameOnlyRecordLoadsWithEveryOtherFieldAbsentAsInC coded on 261113, commented in
     * full on 261113, updated on 261113 once an absent desc became null.
     */
    @Test
    void aNameOnlyRecordLoadsWithEveryOtherFieldAbsentAsInC() throws Exception {
        ParseResult<MonsterBase> result = load("name-only.txt", "name:orc\n");

        assertEquals(1, result.items().size());
        MonsterBase base = result.items().get(0);
        assertEquals("orc", base.getCodeName());
        assertTrue(base.getFlags().isEmpty());
        assertEquals('\0', base.getDefaultMonsterChar());
        assertEquals(0, base.getWideMonsterChar());
        assertEquals(-1, painIndexOf(base), "no pain set");
        assertNull(descOf(base));
        assertNull(inGameNameOf(base));
    }

    /**
     * {@code parse_mon_base_pain} is the only handler that sets the record's pain set, so a base
     * with no {@code pain:} line keeps the NULL that {@code mem_zalloc} gave it. The port holds a
     * {@code null} pain set, and the rest of the record loads as written.
     *
     * <p>Function anAbsentPainLeavesThePainSetNullAsInC coded on 261113, commented in full on
     * 261113.
     */
    @Test
    void anAbsentPainLeavesThePainSetNullAsInC() throws Exception {
        ParseResult<MonsterBase> result =
                load("no-pain.txt", "name:orc\nglyph:o\nflags:ANIMAL\ndesc:Orc\n");

        MonsterBase base = result.items().get(0);
        assertEquals(-1, painIndexOf(base), "no pain set");
        assertEquals('o', base.getDefaultMonsterChar());
        assertEquals(List.of("RF_ANIMAL"), flagNamesOf(base));
        assertEquals("Orc", descOf(base));
    }

    /**
     * A file with no record at all loads in C and leaves {@code rb_info} empty: an empty file, or
     * one of comments and blank lines, gives no bases.
     *
     * <p>Function anEmptyOrCommentOnlyFileLoadsNoBasesAsInC coded on 261113, commented in full on
     * 261113.
     */
    @Test
    void anEmptyOrCommentOnlyFileLoadsNoBasesAsInC() throws IOException {
        for (String body : List.of("", "\n", "# nothing here\n", "# one\n\n# two\n")) {
            assertTrue(load("no-records.txt", body).items().isEmpty(), "file [" + body + "]");
        }
    }

    /**
     * {@code parse_mon_base_desc} appends each {@code desc:} line to the record's text with
     * {@code string_append}, whatever other directive sits between them, so a {@code desc:} split
     * by a {@code flags:} line still reads {@code abcd}. The {@code flags:} line is kept as well.
     *
     * <p>Function aDescSplitByAnotherDirectiveIsJoinedEndToEndAsInC coded on 261113, commented in
     * full on 261113.
     */
    @Test
    void aDescSplitByAnotherDirectiveIsJoinedEndToEndAsInC() throws Exception {
        ParseResult<MonsterBase> result = load("split-desc.txt",
                "name:orc\nglyph:o\npain:1\ndesc:ab\nflags:ANIMAL\ndesc:cd\n");

        MonsterBase base = result.items().get(0);
        assertEquals("abcd", descOf(base));
        assertEquals(List.of("RF_ANIMAL"), flagNamesOf(base));
    }

    /**
     * {@code parse_mon_base_glyph} and {@code parse_mon_base_pain} each overwrite the field they
     * set, so the last {@code glyph:} and the last {@code pain:} in a record win.
     *
     * <p>Function aRepeatedGlyphOrPainKeepsTheLastAsInC coded on 261113, commented in full on
     * 261113.
     */
    @Test
    void aRepeatedGlyphOrPainKeepsTheLastAsInC() throws Exception {
        ParseResult<MonsterBase> result = load("repeats.txt",
                "name:orc\nglyph:o\nglyph:p\npain:1\npain:2\ndesc:Orc\n");

        MonsterBase base = result.items().get(0);
        assertEquals('p', base.getDefaultMonsterChar());
        assertEquals(2, painIndexOf(base));
    }

    /**
     * {@code strtok(flags, " |")} skips delimiters at the start of the value as well as between
     * names, so C reads {@code flags: ANIMAL}, {@code flags:|ANIMAL} and {@code flags: | ANIMAL} as the
     * one flag. The {@code flags} rule takes a flag, or a run of delimiters with no flag, straight
     * after {@code flags:}, but not a delimiter followed by a flag, so each of these is a syntax
     * error. This is an accepted deviation, recorded in {@code docs/Accepted_Deviations.md}.
     *
     * <p>Function aBlankOrBarBeforeTheFirstFlagIsAHardErrorHereWhereCSkipsIt coded on 261113,
     * commented in full on 261113, updated on 261113 once a list of delimiters alone was accepted.
     */
    @Test
    void aBlankOrBarBeforeTheFirstFlagIsAHardErrorHereWhereCSkipsIt() {
        for (String lead : List.of(" ", "  ", "|", "||", " | ", " | | ")) {
            String body = "name:orc\nglyph:o\npain:1\nflags:" + lead + "ANIMAL\ndesc:Orc\n";
            assertThrows(ParseCancellationException.class, () -> load("lead.txt", body),
                    "lead [" + lead + "]");
        }
    }

    /**
     * A value of nothing but bars and blanks splits into no names in C, as an empty value does, and
     * the base keeps no flags. The {@code flags} rule takes a run of delimiters with no flag, so each
     * of these loads with an empty flag set.
     *
     * <p>Function aFlagListOfOnlyBarsLoadsWithNoFlagsAsInC coded on 261113, commented in full on
     * 261113 (renamed and flipped from aFlagListOfOnlyBarsIsAHardErrorHereWhereCReadsNoFlags on
     * 261113).
     */
    @Test
    void aFlagListOfOnlyBarsLoadsWithNoFlagsAsInC() throws IOException {
        for (String value : List.of("|", "||", " |", "| ", " | ", " | |", "|  |", "  |  | ")) {
            String body = "name:orc\nglyph:o\npain:1\nflags:" + value + "\ndesc:Orc\n";
            ParseResult<MonsterBase> result = load("only-bars.txt", body);

            assertEquals(1, result.items().size(), "value [" + value + "]");
            assertTrue(result.items().get(0).getFlags().isEmpty(), "value [" + value + "]");
        }
    }

    /**
     * C reads a last line that has no newline after it: {@code file_getl} returns an unterminated
     * final line of at least one character, and {@code parse_file} hands it to {@code parser_parse}.
     * The flag list is closed by {@code FLAG_EOL} or by end of file, so a {@code flags:} line at the
     * very end of the file loads whatever its value, and a value of delimiters alone loads with no
     * flags.
     *
     * <p>Function aFlagsLineWithNoFinalNewlineLoadsAsInC coded on 261113, commented in full on
     * 261113 (renamed and flipped from aFlagsLineWithNoFinalNewlineIsAHardErrorHereWhereCReadsIt on
     * 261113).
     */
    @Test
    void aFlagsLineWithNoFinalNewlineLoadsAsInC() throws IOException {
        record Case(String last, List<String> flags) {}
        for (Case c : List.of(
                new Case("flags:ANIMAL", List.of("RF_ANIMAL")),
                new Case("flags:ANIMAL | EVIL", List.of("RF_ANIMAL", "RF_EVIL")),
                new Case("flags:ANIMAL EVIL", List.of("RF_ANIMAL", "RF_EVIL")),
                new Case("flags:ANIMAL ", List.of("RF_ANIMAL")),
                new Case("flags:ANIMAL | |", List.of("RF_ANIMAL")),
                new Case("flags:", List.of()),
                new Case("flags: ", List.of()),
                new Case("flags:|", List.of()),
                new Case("flags: | |", List.of()))) {
            String body = "name:orc\nglyph:o\npain:1\n" + c.last();
            ParseResult<MonsterBase> result = load("flags-eof.txt", body);

            assertEquals(1, result.items().size(), "last line [" + c.last() + "]");
            assertEquals(c.flags(), flagNamesOf(result.items().get(0)), "last line [" + c.last() + "]");
        }
    }

    /**
     * The other half of {@link #aFlagsLineWithNoFinalNewlineLoadsAsInC}: every other directive loads
     * as the last line with no newline, as in C. Before the flag list accepted end of file in place
     * of its newline token, it was the only one refused.
     *
     * <p>Function aLastLineOfAnyOtherDirectiveNeedsNoFinalNewlineAsInC coded on 261113, commented
     * in full on 261113, updated on 261113 once the flag list accepted end of file.
     */
    @Test
    void aLastLineOfAnyOtherDirectiveNeedsNoFinalNewlineAsInC() throws Exception {
        assertEquals(1, load("eof-name.txt", "name:orc").items().size());

        MonsterBase glyphLast = load("eof-glyph.txt", "name:orc\nglyph:o").items().get(0);
        assertEquals('o', glyphLast.getDefaultMonsterChar());

        MonsterBase painLast = load("eof-pain.txt", "name:orc\nglyph:o\npain:1").items().get(0);
        assertEquals(1, painIndexOf(painLast));

        MonsterBase descLast = load("eof-desc.txt", "name:orc\nglyph:o\npain:1\ndesc:Orc").items().get(0);
        assertEquals("Orc", descOf(descLast));
    }

    /**
     * C reads {@code pain:} with {@code strtoul(tok, &z, 0)}, base 0, so {@code 010} is octal 8 and picks
     * the set with serial 8, not the one with serial 10.
     *
     * <p>Function aLeadingZeroPainIsReadAsOctalAsInC coded on 261112, commented in full on 261112
     * (renamed and flipped from aLeadingZeroPainIsReadAsDecimalHereWhereCReadsOctal on 261112).
     */
    @Test
    void aLeadingZeroPainIsReadAsOctalAsInC() throws Exception {
        String body = entry("orc", 'o', 1, "Orc").replace("pain:1", "pain:010");
        ParseResult<MonsterBase> result = load("octal.txt", body);

        assertEquals(8, painIndexOf(result.items().get(0)));
    }

    /**
     * C refuses a {@code uint} token that starts with {@code -} as {@code PARSE_ERROR_NOT_NUMBER}. The
     * assembler refuses it first, by name, before any lookup of the set.
     *
     * <p>Function aNegativePainIsAFatalLoadErrorAsInC coded on 261112, commented in full on 261112
     * (renamed and flipped from aNegativePainIsAFatalLoadErrorHereWhereCRejectsItAsNotANumber on 261112).
     */
    @Test
    void aNegativePainIsAFatalLoadErrorAsInC() {
        String body = entry("orc", 'o', 1, "Orc").replace("pain:1", "pain:-1");
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("neg-pain.txt", body));

        assertTrue(e.getMessage().contains("illegal negative pain value: -1"), e::getMessage);
    }

    @Test
    void aPainSerialBeyondTheLoadedSetsIsAFatalLoadErrorAsInCsOutOfBounds() {
        // parse_mon_base_pain returns PARSE_ERROR_OUT_OF_BOUNDS when pain_idx >= z_info->mp_max.
        // pain.txt's highest serial is 12, so mp_max is 13 and 13 is the first rejected value.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("pain13.txt", entry("orc", 'o', 13, "Orc")));

        assertTrue(e.getMessage().contains("illegal pain value: 13"), e::getMessage);
    }

    @Test
    void theHighestPainSerialInPainTxtIsAccepted() throws Exception {
        ParseResult<MonsterBase> result =
                load("pain12.txt", entry("orc", 'o', 12, "Orc"));

        assertEquals(12, painIndexOf(result.items().get(0)));
    }

    @Test
    void aGlyphOutsideTheBasicMultilingualPlaneIsKeptWholeInTheWideHalf() throws IOException {
        // d_char is a wchar_t, which holds U+1F600, so C loads the record. A Java char does not hold
        // it, so the port keeps it in the wide half and leaves the narrow half at '\0'.
        String body = "name:orc\nglyph:😀\npain:1\ndesc:Orc\n";
        ParseResult<MonsterBase> result = load("astral.txt", body);

        assertEquals(1, result.items().size());
        MonsterBase base = result.items().get(0);
        assertTrue(base.isWideChar());
        assertEquals(0x1F600, base.getWideMonsterChar());
        assertEquals('\0', base.getDefaultMonsterChar());
    }

    @Test
    void anIndentedDirectiveLoadsAsInC() throws IOException {
        // parser_parse skips leading isspace before reading the directive, and so does the INDENT rule.
        ParseResult<MonsterBase> result = load("indent.txt", "name:orc\n  glyph:o\npain:1\ndesc:Orc\n");

        assertEquals('o', result.items().get(0).getDefaultMonsterChar());
    }
}
