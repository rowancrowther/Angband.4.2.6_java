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
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
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
     * The number of {@code name:} records in quest.txt.
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
                quest("Spider Hunt", "5", "spider", "3"));

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
                quest("Impossible", "5", "Xyzzy the Nonexistent", "1")));

        assertTrue(e.getMessage().contains("monster race value"), e.getMessage());
        assertTrue(e.getMessage().contains("Xyzzy the Nonexistent"), e.getMessage());
    }

    /**
     * C rejects a leading {@code -} on a {@code uint} field with {@code PARSE_ERROR_NOT_NUMBER}, and
     * the lexer's INTEGER token does allow one, so a negative level reaches the assembler and aborts
     * the load.
     */
    @Test
    void negativeLevelAbortsTheLoad() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("neglevel.txt",
                quest("Below", "-3", "Sauron, the Sorcerer", "1")));

        assertTrue(e.getMessage().contains("negative level value"), e.getMessage());
        assertTrue(e.getMessage().contains("-3"), e.getMessage());
    }

    // ---- No record is dropped ------------------------------------------

    /**
     * 99999999999 is above {@code int} but within {@code unsigned long}, so C accepts it and stores
     * the low byte: it is 0x174876E7FF, giving level 255. The port clamps the figure, which gives 255
     * by coincidence, and keeps the record.
     */
    @Test
    void levelBeyondIntWrapsToAByteAndIsKept() throws IOException {
        ParseResult<Quest> result = load("biglevel.txt",
                quest("Deep", "99999999999", "Sauron, the Sorcerer", "1"));

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
                quest("Many", "5", "Sauron, the Sorcerer", "99999999999"));

        assertEquals(1, result.items().size());
        assertEquals(Integer.MAX_VALUE, result.items().get(0).getMaxNumber());
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
        ParseResult<Quest> result = load("gap.txt", body);

        assertEquals(2, result.items().size());
        assertEquals(0, result.items().get(0).getIndex());
        assertEquals("Next", result.items().get(1).getName());
        assertEquals(1, result.items().get(1).getIndex());
    }

    // ---- Hard error (fail-closed) ----------------------------------------

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
        ParseResult<Quest> result = load("three.txt", body);

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
                quest("The Hunt: Part 2", "5", "Sauron, the Sorcerer", "1"));

        assertEquals("The Hunt: Part 2", result.items().get(0).getName());
    }

    /**
     * Comments and blank lines between and inside records are skipped, as {@code parser_parse} skips
     * them.
     */
    @Test
    void commentsAndBlankLinesBetweenDirectivesAreIgnored() throws IOException {
        String text = "# header comment\n\n# before\nname:Sauron\n\n# inside\nlevel:99\n"
                + "race:Sauron, the Sorcerer\n# more\nnumber:1\n# after\n";
        ParseResult<Quest> result = load("comments.txt", text);

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
        List<Quest> quests = load("levels.txt", body).items();

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
                quest("Horde", "5", "Sauron, the Sorcerer", "300")).items();

        assertEquals(300, quests.get(0).getMaxNumber());
        assertEquals(0, quests.get(0).getCurrentNumber());
    }

    /**
     * A fault names the line of the directive that caused it, as C does ({@code parser_parse} counts
     * {@code lineno} per line and {@code datafile.c} prints it). First record on lines 1-4, a blank
     * line, second record's {@code name:} on line 6 and its bad {@code number:} on line 9.
     */
    @Test
    void aFaultNamesTheLineOfTheBadDirectiveAsInC() {
        String body = quest("Good", "5", "Sauron, the Sorcerer", "1") + "\n"
                + quest("Bad", "5", "Sauron, the Sorcerer", "-1");
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> load("lines.txt", body));

        assertTrue(e.getMessage().contains("line: 9"), e.getMessage());
        assertFalse(e.getMessage().contains("line: 6"), e.getMessage());
        assertTrue(e.getMessage().contains("negative number value"), e.getMessage());
        assertTrue(e.getMessage().contains("-1"), e.getMessage());
    }

    /**
     * C's {@code strtoul} in base 0 reads {@code 010} as octal 8, and the port reads base 0 too.
     */
    @Test
    void aLeadingZeroLevelIsReadAsOctalAsInC() throws IOException {
        List<Quest> quests = load("octal.txt",
                quest("Octal", "010", "Sauron, the Sorcerer", "010")).items();

        assertEquals(8, quests.get(0).getLevel());
        assertEquals(8, quests.get(0).getMaxNumber());
    }

    /**
     * C reads {@code 0x10} as 16 in base 0, and so does the port since {@code QuestLexer}'s
     * {@code INTEGER} took the {@code 0x} and {@code 0X} prefixes and the digits {@code a}-{@code f}
     * (261124). {@code level:} and {@code number:} both go through the same reading: {@code 0x10} is 16,
     * {@code 0X0a} is 10 in either case of letter and prefix, and {@code 0xFF} is 255 for a level (the
     * {@code uint8_t} holds it) and 255 for a number.
     */
    @Test
    void aHexadecimalLevelOrNumberLoadsAsInC() throws IOException {
        List<Quest> quests = load("hex.txt",
                quest("Hex", "0x10", "Sauron, the Sorcerer", "0x1F")
                        + quest("Upper", "0X0a", "Morgoth, Lord of Darkness", "0XfF")).items();

        assertEquals(16, quests.get(0).getLevel());
        assertEquals(31, quests.get(0).getMaxNumber());
        assertEquals(10, quests.get(1).getLevel());
        assertEquals(255, quests.get(1).getMaxNumber());
    }

    /**
     * {@code strtoul} in base 0 reads the leading digits of a figure and ignores the rest, so
     * {@code 12ab} is 12 and {@code 1f} is 1 (no {@code 0x}, so the {@code f} is not a digit). The lexer
     * lets the letters {@code a}-{@code f} follow any digit, and {@code parseIntString} cuts the run at the
     * first character that is not a digit of the base.
     */
    @Test
    void lettersAfterDecimalDigitsAreIgnoredAsInC() throws IOException {
        List<Quest> quests = load("letters.txt",
                quest("Letters", "12ab", "Sauron, the Sorcerer", "1f")).items();

        assertEquals(12, quests.get(0).getLevel());
        assertEquals(1, quests.get(0).getMaxNumber());
    }

    /**
     * Letters with no digit in front of them are not a number in C ({@code strtoul} reads nothing, so
     * {@code z == tok} gives {@code PARSE_ERROR_NOT_NUMBER}). The lexer takes {@code ab} as an
     * {@code INTEGER}, so it is the assembler that refuses it, for {@code level:} and {@code number:} alike.
     * A minus ahead of a hex figure is refused as {@code -5} is.
     */
    @Test
    void lettersWithNoDigitAndAMinusOnAHexFigureAreRefusedAsInC() {
        assertThrows(DataFileParseException.class, () -> load("ab-level.txt",
                quest("Ab", "ab", "Sauron, the Sorcerer", "1")));
        assertThrows(DataFileParseException.class, () -> load("ab-number.txt",
                quest("Ab", "5", "Sauron, the Sorcerer", "ab")));
        assertThrows(DataFileParseException.class, () -> load("minus-hex.txt",
                quest("MinusHex", "-0x10", "Sauron, the Sorcerer", "1")));
    }

    /**
     * C's {@code strtoul} takes a leading {@code +}, and the {@code *tok == '-'} test does not refuse it,
     * so {@code level:+5} is 5 and {@code number:+3} is 3. {@code QuestLexer}'s {@code INTEGER} takes the
     * {@code +} (261118) and the assembler's {@code parseIntString} strips it. {@code +0} is 0.
     */
    @Test
    void aLeadingPlusOnALevelOrNumberLoadsAsInC() throws IOException {
        List<Quest> quests = load("plus.txt",
                quest("Plus", "+5", "Sauron, the Sorcerer", "+3")
                        + quest("Zero", "+0", "Morgoth, Lord of Darkness", "+0")).items();

        assertEquals(5, quests.get(0).getLevel());
        assertEquals(3, quests.get(0).getMaxNumber());
        assertEquals(0, quests.get(1).getLevel());
        assertEquals(0, quests.get(1).getMaxNumber());
    }

    /**
     * A sign with no digit after it is not a number in C ({@code strtoul} reads nothing, so
     * {@code PARSE_ERROR_NOT_NUMBER}), and the lexer has no rule for a lone {@code +}. A minus still
     * refuses {@code -0} as C does, and {@code -} then {@code +} is no number at all.
     */
    @Test
    void aLoneSignOrAMinusStillRefusesTheLoadAsInC() {
        assertThrows(ParseCancellationException.class, () -> load("lone.txt",
                quest("Lone", "+", "Sauron, the Sorcerer", "1")));
        assertThrows(DataFileParseException.class, () -> load("minus.txt",
                quest("Minus", "-0", "Sauron, the Sorcerer", "1")));
        assertThrows(ParseCancellationException.class, () -> load("both.txt",
                quest("Both", "-+5", "Sauron, the Sorcerer", "1")));
    }

    // ---- Directive order, absence and repetition ---------------------------
    // C registers four independent one-line directives: only name: opens a record, the other three
    // fill the current one in any order, any may be missing, and the last of a kind wins.

    @Test
    void directivesInAnotherOrderLoadAsInC() throws IOException {
        String text = "\nname:Sauron\nrace:Sauron, the Sorcerer\nlevel:99\nnumber:1\n";
        Quest quest = load("order.txt", text).items().getFirst();

        assertEquals("Sauron", quest.getName());
        assertEquals(99, quest.getLevel());
        assertEquals("Sauron, the Sorcerer", quest.getRace().getName());
        assertEquals(1, quest.getMaxNumber());
    }

    /**
     * C zero-fills the quest, so a missing {@code level:} or {@code number:} is 0 and a missing
     * {@code race:} is a null race.
     */
    @Test
    void aMissingDirectiveLoadsWithTheFieldZeroAsInC() throws IOException {
        Quest noNumber = load("nonumber.txt",
                "\nname:Sauron\nlevel:99\nrace:Sauron, the Sorcerer\n").items().getFirst();
        assertEquals(0, noNumber.getMaxNumber());
        assertEquals(99, noNumber.getLevel());

        Quest noLevel = load("nolevel.txt",
                "\nname:Sauron\nrace:Sauron, the Sorcerer\nnumber:1\n").items().getFirst();
        assertEquals(0, noLevel.getLevel());

        Quest noRace = load("norace.txt", "\nname:Sauron\nlevel:99\nnumber:1\n").items().getFirst();
        assertNull(noRace.getRace());
        assertEquals(99, noRace.getLevel());

        Quest nameOnly = load("nameonly.txt", "name:Sauron\n").items().getFirst();
        assertEquals("Sauron", nameOnly.getName());
        assertEquals(0, nameOnly.getLevel());
        assertNull(nameOnly.getRace());
        assertEquals(0, nameOnly.getMaxNumber());
        assertEquals(0, nameOnly.getCurrentNumber());
    }

    @Test
    void aRepeatedDirectiveTakesTheLastOneAsInC() throws IOException {
        String text = "\nname:Sauron\nlevel:5\nlevel:99\nrace:Sauron, the Sorcerer\nnumber:1\n";

        assertEquals(99, load("dup.txt", text).items().getFirst().getLevel());
    }

    /**
     * C acts on each line as it reads it, so a bad value that a later line would have overwritten
     * still ends the load. The directives are handed on in file order and checked one by one.
     */
    @Test
    void aBadValueOverwrittenByALaterLineIsStillRefusedAsInC() {
        String level = "\nname:Sauron\nlevel:-5\nlevel:99\nrace:Sauron, the Sorcerer\nnumber:1\n";
        String number = "\nname:Sauron\nnumber:-1\nnumber:1\nrace:Sauron, the Sorcerer\nlevel:9\n";
        String race = "\nname:Sauron\nrace:Xyzzy the Nonexistent\nrace:Sauron, the Sorcerer\nlevel:9\nnumber:1\n";

        assertThrows(DataFileParseException.class, () -> load("badlevel.txt", level));
        assertThrows(DataFileParseException.class, () -> load("badnumber.txt", number));
        assertThrows(DataFileParseException.class, () -> load("badrace.txt", race));
    }

    /**
     * With two bad lines the first in the file is the one reported, as C stops at it: here the race on
     * line 2, ahead of the negative level on line 3.
     */
    @Test
    void theFirstBadLineInTheFileIsTheOneReportedAsInC() {
        String text = "name:Sauron\nrace:Xyzzy the Nonexistent\nlevel:-5\n";
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> load("two.txt", text));

        assertTrue(e.getMessage().contains("monster race value"), e.getMessage());
        assertTrue(e.getMessage().contains("line: 2"), e.getMessage());
    }

    @Test
    void aFileWithNoQuestsLoadsAnEmptyListAsInC() throws IOException {
        assertTrue(load("empty.txt", "").items().isEmpty());
        assertTrue(load("comment.txt", "# nothing here\n").items().isEmpty());
    }

    @Test
    void aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("extra.txt",
                quest("Sauron", "99:7", "Sauron, the Sorcerer", "1")));

    }

    @Test
    void anIndentedLineLoadsAsInC() throws IOException {
        String text = "\nname:Sauron\n  level:99\nrace:Sauron, the Sorcerer\nnumber:1\n";

        assertEquals(1, load("indent.txt", text).items().size());
    }

    @Test
    void aFinalCommentWithoutANewlineLoadsAsInC() throws IOException {
        String text = quest("Sauron", "99", "Sauron, the Sorcerer", "1") + "# end";

        assertEquals(1, load("lastcomment.txt", text).items().size());
    }

    @Test
    void anUnknownDirectiveRefusesTheFileAsInC() throws IOException {
        String text = quest("Sauron", "99", "Sauron, the Sorcerer", "1") + "colour:red\n";
        assertThrows(ParseCancellationException.class, () -> load("unknown.txt", text));

    }

    /**
     * C's {@code init_parse_quest} starts with a null private pointer, and {@code parse_quest_level},
     * {@code parse_quest_race} and {@code parse_quest_number} each {@code assert(q)} on it, so a
     * directive ahead of the first {@code name:} crashes C. Java refuses the file.
     * Accepted deviation, "Quest" in {@code docs/Accepted_Deviations.md}.
     */
    @Test
    void aDirectiveBeforeTheFirstNameIsRefusedHereWhereCCrashes() {
        for (String directive : List.of("level:99", "race:Sauron, the Sorcerer", "number:1")) {
            String text = directive + "\n" + quest("Sauron", "99", "Sauron, the Sorcerer", "1");
            assertThrows(ParseCancellationException.class, () -> load("early.txt", text), directive);
        }
    }

    /**
     * The control for the pin above: comments and blank lines ahead of the first {@code name:} are fine.
     */
    @Test
    void commentsAndBlankLinesBeforeTheFirstNameLoadAsInC() throws IOException {
        String text = "# a comment\n\n" + quest("Sauron", "99", "Sauron, the Sorcerer", "1");
        assertEquals(1, load("lead.txt", text).items().size());
    }
}
