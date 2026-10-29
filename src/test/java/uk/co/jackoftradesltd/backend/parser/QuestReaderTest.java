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
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.player.Quest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link QuestReader}: file text -&gt; {@code QuestLexer}/
 * {@code QuestGrammar} -&gt; {@link uk.co.jackoftradesltd.backend.parser.quest.QuestParseRecord} -&gt;
 * {@code QuestAssembler} -&gt; {@link Quest}.
 *
 * <p>The quest assembler resolves each {@code race:} name against the loaded monster races via
 * {@link MonsterRegistry#lookupMonsterRace}, so the registry must be populated first. Rather than
 * hand-seed it, the suite runs the full {@code GameConstants.init(CoreChannel)} chain in {@link #bootstrap()}
 * (which loads {@code monster.txt} and then, late in the order, {@code quest.txt} itself) - the same
 * bootstrap {@code PitReaderTest} uses for the other monster-dependent reader.
 *
 * <p>Error-injection cases write small fixture files into a {@link TempDir} and parse those, so the
 * canonical {@code lib/gamedata/quest.txt} is never mutated.
 *
 * @author Rowan Crowther
 */
class QuestReaderTest {

    private static final String REAL_FILE = "lib/gamedata/quest.txt";

    /**
     * quest.txt's {@code record-count:} header, and the number of {@code name:} records in it.
     */
    private static final int EXPECTED_QUESTS = 2;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void bootstrap() throws IOException {
        // Mirrors UILoop's EVENT_ENTER_INIT handler: the real init chain now blocks partway
        // through GameConstants.init() for this ack, so the front end's UIEntry load has to
        // actually happen before it is sent, or the assemblers below find an empty UIRegistry.
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);
    }

    /**
     * {@code GameConstants.init(CoreChannel)} populates the shared registries in place; reset the object-kind
     * ones to the empty baseline so this heavy load does not leak into order-sensitive suites
     * (matching {@code PitReaderTest}'s cleanup).
     */
    @AfterAll
    static void cleanup() throws Exception {
        ObjectRegistry.reset();
    }

    // ---- fixture helpers -------------------------------------------------

    /**
     * Assemble a single quest record's four lines.
     */
    private static String quest(String name, String level, String race, String number) {
        return "name:" + name + "\n"
                + "level:" + level + "\n"
                + "race:" + race + "\n"
                + "number:" + number + "\n";
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n\n" + body;
    }

    private ParseResult<Quest> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new QuestReader().parseWithResults(file.toString());
    }

    private static Quest byName(List<Quest> quests, String name) {
        return quests.stream().filter(q -> q.getName().equals(name)).findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T staticField(String name) throws Exception {
        // The registries were split out of GameConstants a slice at a time, so resolve the field
        // across every holder rather than assuming it still lives on GameConstants (quests moved to
        // WorldRegistry).
        Field f = RegistrySeeding.resolve(name);
        f.setAccessible(true);
        return (T) f.get(null);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsBothQuestsWithNoErrors() throws IOException {
        ParseResult<Quest> result = new QuestReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(EXPECTED_QUESTS, result.items().size());
    }

    @Test
    void realFileQuestFieldsResolveCorrectly() throws IOException {
        List<Quest> quests = new QuestReader().parseWithResults(REAL_FILE).items();

        Quest sauron = byName(quests, "Sauron");
        assertNotNull(sauron);
        assertEquals(0, sauron.getIndex());
        assertEquals(99, sauron.getLevel());
        assertEquals(1, sauron.getMaxNumber());
        assertEquals(0, sauron.getCurrentNumber());
        assertNotNull(sauron.getRace(), "race must resolve against the loaded monster races");
        assertEquals("Sauron, the Sorcerer", sauron.getRace().getName());

        Quest morgoth = byName(quests, "Morgoth");
        assertNotNull(morgoth);
        assertEquals(1, morgoth.getIndex());
        assertEquals(100, morgoth.getLevel());
        assertEquals(1, morgoth.getMaxNumber());
        assertEquals("Morgoth, Lord of Darkness", morgoth.getRace().getName());
    }

    /**
     * The genuine startup path: {@code GameConstants.init(CoreChannel)} (run in {@link #bootstrap()}) calls
     * {@code loadQuests()}, which only stores into {@code quests} when the parse is error-free.
     * A non-null registry of the expected size proves quest.txt loaded cleanly as part of real init,
     * i.e. that {@code loadQuests()} is actually wired into the chain.
     */
    @Test
    void initChainPopulatesTheQuestRegistry() throws Exception {
        List<Quest> registry = staticField("quests");

        assertNotNull(registry, "init() left quests null - loadQuests reported errors or is unwired");
        assertEquals(EXPECTED_QUESTS, registry.size());
    }

    // ---- Race resolution -------------------------------------------------

    /**
     * {@code lookupMonsterRace} (faithful to C {@code lookup_monster}) falls back from an exact,
     * case-insensitive match to the first race whose name <em>contains</em> the query. A quest whose
     * {@code race:} is a lowercase partial ("spider") must still resolve - here to "cave spider",
     * the earliest containing race (mirrors {@code MonsterReaderTest}).
     */
    @Test
    void raceResolvesViaCaseInsensitiveSubstringFallback() throws IOException {
        ParseResult<Quest> result = load("partial.txt",
                withHeader(1, quest("Spider Hunt", "5", "spider", "3")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals("cave spider", result.items().get(0).getRace().getName());
    }

    // ---- Hard errors from the assembler (the load aborts, as in C) --------

    /**
     * C's {@code parse_quest_race} returns {@code PARSE_ERROR_INVALID_MONSTER}, which stops the whole
     * {@code quest.txt} load. The assembler throws, and {@code GrammarDriver.run} does not catch it.
     */
    @Test
    void unknownRaceAbortsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("badrace.txt",
                withHeader(1, quest("Impossible", "5", "Xyzzy the Nonexistent", "1"))));

        assertTrue(e.getMessage().contains("has an illegal monster race value: Xyzzy the Nonexistent"),
                e.getMessage());
    }

    /**
     * C rejects a leading {@code -} on a {@code uint} field with {@code PARSE_ERROR_NOT_NUMBER}, and
     * the lexer's INTEGER token does allow one, so a negative level reaches the assembler and aborts
     * the load.
     */
    @Test
    void negativeLevelAbortsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("neglevel.txt",
                withHeader(1, quest("Below", "-3", "Sauron, the Sorcerer", "1"))));

        assertTrue(e.getMessage().contains("has an illegal negative level value: -3"), e.getMessage());
    }

    // ---- No record is dropped; the record count is the only soft error ---

    /**
     * 99999999999 is above {@code int} but within {@code unsigned long}, so C accepts it and stores
     * the low byte: it is 0x174876E7FF, giving level 255. The port clamps the figure, which gives 255
     * by coincidence, and keeps the record.
     */
    @Test
    void levelBeyondIntWrapsToAByteAndIsKept() throws IOException {
        ParseResult<Quest> result = load("biglevel.txt",
                withHeader(1, quest("Deep", "99999999999", "Sauron, the Sorcerer", "1")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(255, result.items().get(0).getLevel());
    }

    /**
     * Accepted deviation (261027): C wraps 99999999999 into the {@code int} {@code max_num} (low 32
     * bits, 0x4876E7FF = 1215752191); the port clamps to {@code Integer.MAX_VALUE}, keeps the record
     * and reports nothing.
     */
    @Test
    void numberBeyondIntIsClampedAndTheRecordKept() throws IOException {
        ParseResult<Quest> result = load("bignumber.txt",
                withHeader(1, quest("Many", "5", "Sauron, the Sorcerer", "99999999999")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(Integer.MAX_VALUE, result.items().get(0).getMaxNumber());
    }

    @Test
    void recordCountMismatchIsASoftError() throws IOException {
        // Header claims three, body holds one; the one still loads (partial-results contract).
        ParseResult<Quest> result = load("miscount.txt",
                withHeader(3, quest("Solo", "5", "Sauron, the Sorcerer", "1")));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("record-count header declares")),
                () -> result.errors().toString());
    }

    /**
     * An oversized figure no longer drops its record, so the indices are the positions in the file
     * with no gap: 0 for the first, 1 for the second.
     */
    @Test
    void indicesAreContiguousWhenAFigureIsOversized() throws IOException {
        String body = quest("Big", "5", "Sauron, the Sorcerer", "99999999999")
                + "\n"
                + quest("Next", "10", "Morgoth, Lord of Darkness", "1");
        ParseResult<Quest> result = load("gap.txt", withHeader(2, body));

        assertEquals(2, result.items().size());
        assertEquals(0, result.items().get(0).getIndex());
        assertEquals("Next", result.items().get(1).getName());
        assertEquals(1, result.items().get(1).getIndex());
    }

    // ---- Hard error (fail-closed) ----------------------------------------

    /**
     * The {@code file} rule requires the {@code record-count} header, so a file without it is a hard
     * grammar error: {@code throwIfAny()} fires and the driver returns an empty result carrying the
     * collected errors, rather than a partial load.
     */
    @Test
    void missingRecordCountHeaderIsAHardErrorYieldingEmptyResult() throws IOException {
        ParseResult<Quest> result = load("noheader.txt",
                quest("Headerless", "5", "Sauron, the Sorcerer", "1"));

        assertTrue(result.items().isEmpty(), "a hard grammar error must fail closed");
        assertTrue(result.hasErrors());
    }

    // ---- Reader surface: parse(), missing file, record order -------------

    /**
     * {@code parse} is {@code parseWithResults} with the error list dropped; on the shipped file the
     * two agree on the quests.
     */
    @Test
    void parseReturnsTheSameQuestsAsParseWithResults() throws IOException {
        List<Quest> quests = new QuestReader().parse(REAL_FILE);

        assertEquals(EXPECTED_QUESTS, quests.size());
        assertEquals("Sauron", quests.get(0).getName());
        assertEquals("Morgoth", quests.get(1).getName());
    }

    /**
     * C's {@code parse_file_quit_not_found} quits on a missing file; the port surfaces it as an
     * {@link IOException} for {@code WorldDataLoader.loadQuests} to turn into a fatal error.
     */
    @Test
    void missingFileIsAnIOException() {
        assertThrows(IOException.class,
                () -> new QuestReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    /**
     * C's {@code finish_parse_quest} numbers the quests from 0 in file order (it walks the reversed
     * linked list from the back). Three quests keep file order and take indices 0, 1, 2.
     */
    @Test
    void questsKeepFileOrderAndAreNumberedFromZero() throws IOException {
        String body = quest("First", "1", "Sauron, the Sorcerer", "1") + "\n"
                + quest("Second", "2", "Morgoth, Lord of Darkness", "2") + "\n"
                + quest("Third", "3", "Sauron, the Sorcerer", "3");
        ParseResult<Quest> result = load("three.txt", withHeader(3, body));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(3, result.items().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i, result.items().get(i).getIndex());
        }
        assertEquals("First", result.items().get(0).getName());
        assertEquals("Second", result.items().get(1).getName());
        assertEquals("Third", result.items().get(2).getName());
        assertEquals(3, result.items().get(2).getMaxNumber());
    }

    /**
     * {@code name} is a {@code str}, so C takes the rest of the line without tokenizing on {@code :}.
     * The lexer's {@code REST_OF_LINE} mode does the same.
     */
    @Test
    void nameKeepsColonsInTheRestOfTheLine() throws IOException {
        ParseResult<Quest> result = load("colon.txt",
                withHeader(1, quest("The Hunt: Part 2", "5", "Sauron, the Sorcerer", "1")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("The Hunt: Part 2", result.items().get(0).getName());
    }

    /**
     * Comments and blank lines between and inside records are skipped, as {@code parser_parse} skips
     * them.
     */
    @Test
    void commentsAndBlankLinesBetweenDirectivesAreIgnored() throws IOException {
        String text = "# header comment\nrecord-count:1\n\n# before\nname:Sauron\n\n# inside\nlevel:99\n"
                + "race:Sauron, the Sorcerer\n# more\nnumber:1\n# after\n";
        ParseResult<Quest> result = load("comments.txt", text);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(99, result.items().get(0).getLevel());
    }

    // ---- Integer fields as the reader hands them on ----------------------

    /**
     * C stores the level in a {@code uint8_t}: 0 is legal (it is also the "completed" marker), 255 is
     * the top, and 256 wraps to 0, 300 to 44.
     */
    @Test
    void levelIsStoredModuloTwoFiftySix() throws IOException {
        String body = quest("Zero", "0", "Sauron, the Sorcerer", "1") + "\n"
                + quest("Top", "255", "Sauron, the Sorcerer", "1") + "\n"
                + quest("Wrap", "256", "Sauron, the Sorcerer", "1") + "\n"
                + quest("Wrap44", "300", "Sauron, the Sorcerer", "1");
        List<Quest> quests = load("levels.txt", withHeader(4, body)).items();

        assertEquals(0, quests.get(0).getLevel());
        assertEquals(255, quests.get(1).getLevel());
        assertEquals(0, quests.get(2).getLevel());
        assertEquals(44, quests.get(3).getLevel());
    }

    /**
     * {@code max_num} is a plain {@code int} in C, so 300 is kept as 300; {@code cur_num} is 0.
     */
    @Test
    void numberIsKeptWithoutTruncation() throws IOException {
        List<Quest> quests = load("number.txt",
                withHeader(1, quest("Horde", "5", "Sauron, the Sorcerer", "300"))).items();

        assertEquals(300, quests.get(0).getMaxNumber());
        assertEquals(0, quests.get(0).getCurrentNumber());
    }

    /**
     * A fault names the line of the {@code name:} directive that opened the record. Header on
     * line 1, first record on lines 3-6, second record's {@code name:} on line 8.
     */
    @Test
    void aFaultNamesTheLineOfTheRecordsNameDirective() {
        String body = quest("Good", "5", "Sauron, the Sorcerer", "1") + "\n"
                + quest("Bad", "5", "Sauron, the Sorcerer", "-1");
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("lines.txt", withHeader(2, body)));

        assertTrue(e.getMessage().contains("line: 8"), e.getMessage());
        assertTrue(e.getMessage().contains("illegal negative number value: -1"), e.getMessage());
    }

    /**
     * C's {@code strtoul} in base 0 reads {@code 010} as octal 8, and the port reads base 0 too.
     */
    @Test
    void aLeadingZeroLevelIsReadAsOctalAsInC() throws IOException {
        List<Quest> quests = load("octal.txt",
                withHeader(1, quest("Octal", "010", "Sauron, the Sorcerer", "010"))).items();

        assertEquals(8, quests.get(0).getLevel());
        assertEquals(8, quests.get(0).getMaxNumber());
    }

    /**
     * C reads {@code 0x10} as 16 in base 0. {@code INTEGER} stops at the {@code 0}, leaving
     * {@code x10} that no rule accepts, so the whole file is refused.
     */
    @Test
    void aHexadecimalLevelIsRefusedHereWhereCReadsBaseZero() throws IOException {
        ParseResult<Quest> result = load("hex.txt",
                withHeader(1, quest("Hex", "0x10", "Sauron, the Sorcerer", "1")));

        assertTrue(result.items().isEmpty(), "C would store level 16");
        assertTrue(result.hasErrors());
    }

    // ---- Where the grammar is stricter than C ----------------------------
    // C registers four independent one-line directives; the grammar demands exactly
    // name, level, race, number in that order, at least one record.

    @Test
    void directivesInAnotherOrderAreRefusedHereWhereCAcceptsThem() throws IOException {
        String text = "record-count:1\n\nname:Sauron\nrace:Sauron, the Sorcerer\nlevel:99\nnumber:1\n";
        ParseResult<Quest> result = load("order.txt", text);

        assertTrue(result.items().isEmpty(), "C loads the quest with every field set");
        assertTrue(result.hasErrors());
    }

    @Test
    void aMissingDirectiveIsRefusedHereWhereCLeavesTheFieldZero() throws IOException {
        String text = "record-count:1\n\nname:Sauron\nlevel:99\nrace:Sauron, the Sorcerer\n";
        ParseResult<Quest> result = load("nonumber.txt", text);

        assertTrue(result.items().isEmpty(), "C loads the quest with max_num 0");
        assertTrue(result.hasErrors());
    }

    @Test
    void aRepeatedDirectiveIsRefusedHereWhereCTakesTheLastOne() throws IOException {
        String text = "record-count:1\n\nname:Sauron\nlevel:5\nlevel:99\nrace:Sauron, the Sorcerer\nnumber:1\n";
        ParseResult<Quest> result = load("dup.txt", text);

        assertTrue(result.items().isEmpty(), "C loads the quest with level 99");
        assertTrue(result.hasErrors());
    }

    @Test
    void aFileWithNoQuestsIsRefusedHereWhereCLoadsAnEmptyList() throws IOException {
        ParseResult<Quest> result = load("empty.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors(), "C finishes with z_info->quest_max 0 and no error");
    }

    @Test
    void aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt() throws IOException {
        ParseResult<Quest> result = load("extra.txt",
                withHeader(1, quest("Sauron", "99:7", "Sauron, the Sorcerer", "1")));

        assertTrue(result.items().isEmpty(), "C reads level 99 and ignores the rest");
        assertTrue(result.hasErrors());
    }

    @Test
    void anIndentedLineIsRefusedHereWhereCSkipsTheIndent() throws IOException {
        String text = "record-count:1\n\nname:Sauron\n  level:99\nrace:Sauron, the Sorcerer\nnumber:1\n";
        ParseResult<Quest> result = load("indent.txt", text);

        assertTrue(result.items().isEmpty(), "C skips leading whitespace");
        assertTrue(result.hasErrors());
    }

    @Test
    void aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt() throws IOException {
        String text = withHeader(1, quest("Sauron", "99", "Sauron, the Sorcerer", "1")) + "# end";
        ParseResult<Quest> result = load("lastcomment.txt", text);

        assertTrue(result.items().isEmpty(), "C ignores a comment on the last line");
        assertTrue(result.hasErrors());
    }

    @Test
    void anUnknownDirectiveRefusesTheFileAsInC() throws IOException {
        String text = withHeader(1, quest("Sauron", "99", "Sauron, the Sorcerer", "1")) + "colour:red\n";
        ParseResult<Quest> result = load("unknown.txt", text);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
