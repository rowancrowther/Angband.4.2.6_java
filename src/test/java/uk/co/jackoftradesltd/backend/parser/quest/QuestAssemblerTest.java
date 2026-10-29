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

package uk.co.jackoftradesltd.backend.parser.quest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.player.Quest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuestAssembler}, feeding {@link QuestParseRecord}s straight to
 * {@code assemble} so the integer and race handling is exercised without the lexer in the way.
 *
 * <p>Expected values come from C's {@code parse_quest_level}, {@code parse_quest_race} and
 * {@code parse_quest_number} ({@code player-quest.c}) and from {@code parser_parse} ({@code parser.c}),
 * which reads a {@code uint} field with {@code strtoul} in base 0 into an {@code unsigned int}, refuses
 * a token that has no digits or starts with {@code -} with {@code PARSE_ERROR_NOT_NUMBER}, and is then
 * stored into a {@code uint8_t} ({@code level}) or an {@code int} ({@code max_num}). Every fault stops
 * the load, so each failure case asserts {@link DataFileParseException} and the tail of its message.
 * Cases that pin the accepted clamp above {@code Integer.MAX_VALUE} (261027) say so in their comment and
 * give the C value alongside.
 *
 * <p>Race resolution needs {@code monster.txt}, so {@link #bootstrap()} runs the same full
 * {@code GameConstants.init} chain that {@code QuestReaderTest} uses.
 *
 * <p>Class QuestAssemblerTest coded on 261014, rewritten for the fatal paths and commented in full on
 * 261029.
 *
 * @author Rowan Crowther
 */
class QuestAssemblerTest {

    private static final String SAURON = "Sauron, the Sorcerer";
    private static final String MORGOTH = "Morgoth, Lord of Darkness";
    private static final String NO_SUCH_RACE = "Xyzzy the Nonexistent";

    @BeforeAll
    static void bootstrap() throws IOException {
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

    @AfterAll
    static void cleanup() throws Exception {
        ObjectRegistry.reset();
    }

    // ---- helpers ---------------------------------------------------------

    private static QuestParseRecord record(String level, String race, String number) {
        return new QuestParseRecord("Test", level, race, number, 7);
    }

    private static List<Quest> assemble(List<String> errors, QuestParseRecord... records) {
        return new QuestAssembler().assemble(List.of(records), errors, "test.txt");
    }

    private static Quest single(String level, String race, String number) {
        List<String> errors = new ArrayList<>();
        List<Quest> quests = assemble(errors, record(level, race, number));
        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, quests.size());
        return quests.get(0);
    }

    /**
     * Run the records, expect the load to stop, and return the message. The list passed in is checked
     * to be still empty, because this assembler never writes to it.
     */
    private static String stopMessage(QuestParseRecord... records) {
        List<String> errors = new ArrayList<>();
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> assemble(errors, records));
        assertTrue(errors.isEmpty(), errors::toString);
        return e.getMessage();
    }

    // ---- ordinary path ---------------------------------------------------

    /**
     * The shipped Sauron record: every field copied through, the kill tally seeded to 0 (C's
     * {@code mem_zalloc}'d {@code cur_num}), index 0.
     */
    @Test
    void ordinaryRecordAssemblesEveryField() {
        Quest quest = single("99", SAURON, "1");

        assertEquals(0, quest.getIndex());
        assertEquals("Test", quest.getName());
        assertEquals(99, quest.getLevel());
        assertEquals(SAURON, quest.getRace().getName());
        assertEquals(0, quest.getCurrentNumber());
        assertEquals(1, quest.getMaxNumber());
    }

    @Test
    void emptyRecordListGivesEmptyQuestList() {
        List<String> errors = new ArrayList<>();

        assertTrue(new QuestAssembler().assemble(List.of(), errors, "test.txt").isEmpty());
        assertTrue(errors.isEmpty());
    }

    /**
     * C's {@code finish_parse_quest} numbers quests by final array position, which is file order
     * (the linked list is built newest-first and filled from the back).
     */
    @Test
    void indicesFollowFileOrder() {
        List<String> errors = new ArrayList<>();
        List<Quest> quests = assemble(errors,
                record("99", SAURON, "1"),
                record("100", MORGOTH, "1"));

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(2, quests.size());
        assertEquals(0, quests.get(0).getIndex());
        assertEquals(SAURON, quests.get(0).getRace().getName());
        assertEquals(1, quests.get(1).getIndex());
        assertEquals(MORGOTH, quests.get(1).getRace().getName());
    }

    /**
     * Each record keeps its own name; the name is the string {@code parse_quest_name} copies with
     * {@code string_make}, untouched.
     */
    @Test
    void eachRecordKeepsItsOwnName() {
        List<Quest> quests = new QuestAssembler().assemble(List.of(
                new QuestParseRecord("Sauron", "99", SAURON, "1", 3),
                new QuestParseRecord("Morgoth", "100", MORGOTH, "1", 8)), new ArrayList<>(), "test.txt");

        assertEquals("Sauron", quests.get(0).getName());
        assertEquals("Morgoth", quests.get(1).getName());
    }

    // ---- level: strtoul base 0 -> uint8_t --------------------------------

    /**
     * {@code quest.level} is a {@code uint8_t}: 255 fits, 256 wraps to 0, 257 to 1, 356 to 100. Zero is
     * a legal level (it is also how a completed quest is marked).
     */
    @Test
    void levelIsStoredModulo256() {
        assertEquals(0, single("0", SAURON, "1").getLevel());
        assertEquals(255, single("255", SAURON, "1").getLevel());
        assertEquals(0, single("256", SAURON, "1").getLevel());
        assertEquals(1, single("257", SAURON, "1").getLevel());
        assertEquals(100, single("356", SAURON, "1").getLevel());
        assertEquals(255, single("2147483647", SAURON, "1").getLevel());
    }

    /**
     * {@code strtoul} in base 0 reads a leading {@code 0} as octal: {@code 010} is 8, {@code 00} is 0,
     * and {@code 08} reads the {@code 0} and stops at the {@code 8}, so it is 0.
     */
    @Test
    void levelIsReadInBaseZero() {
        assertEquals(8, single("010", SAURON, "1").getLevel());
        assertEquals(0, single("00", SAURON, "1").getLevel());
        assertEquals(0, single("08", SAURON, "1").getLevel());
        assertEquals(63, single("077", SAURON, "1").getLevel());
        assertEquals(8, single("0010", SAURON, "1").getLevel());
    }

    /**
     * {@code strtoul} accepts a leading {@code +}, so {@code +5} is 5 in C. The lexer never produces
     * one, so this record is built by hand.
     */
    @Test
    void levelWithPlusSignIsRead() {
        assertEquals(5, single("+5", SAURON, "1").getLevel());
    }

    /**
     * Accepted deviation (261027): C keeps the low 32 bits of a figure above {@code INT_MAX} and then
     * the low byte, so 2147483648 gives 0, 2147483649 gives 1, 4294967295 gives 255 and 4294967296 gives
     * 0. The port clamps the figure to {@code Integer.MAX_VALUE} first, so all four store 255.
     */
    @Test
    void levelAboveIntMaxIsClampedWhereCWraps() {
        assertEquals(255, single("2147483648", SAURON, "1").getLevel());
        assertEquals(255, single("2147483649", SAURON, "1").getLevel());
        assertEquals(255, single("4294967295", SAURON, "1").getLevel());
        assertEquals(255, single("4294967296", SAURON, "1").getLevel());
    }

    /**
     * 99999999999 is 0x174876E7FF, whose low byte is 255, so C and the port agree by coincidence. A
     * value past {@code Long.MAX_VALUE} is clamped the same way and the record is kept, not dropped.
     */
    @Test
    void levelBeyondLongIsKeptNotDropped() {
        assertEquals(255, single("99999999999", SAURON, "1").getLevel());
        assertEquals(255, single("9223372036854775808", SAURON, "1").getLevel());
        assertEquals(255, single("99999999999999999999999999", SAURON, "1").getLevel());
    }

    // ---- level / number: no digits, or a leading '-', ends the load ------

    /**
     * C: {@code z == tok || *tok == '-'} gives {@code PARSE_ERROR_NOT_NUMBER}, which stops the whole
     * file load. {@code -0} is refused too, because the test is on the first character, not the value.
     */
    @Test
    void negativeLevelStopsTheLoad() {
        String message = stopMessage(record("-1", SAURON, "1"));
        assertTrue(message.contains("has an illegal negative level value: -1"), message);
        assertTrue(message.contains("line: 7"), message);
        assertTrue(message.contains("test.txt"), message);

        assertTrue(stopMessage(record("-0", SAURON, "1")).contains("illegal negative level value: -0"));
        assertTrue(stopMessage(record("-2147483648", SAURON, "1")).contains("negative level"));
    }

    @Test
    void negativeNumberStopsTheLoad() {
        String message = stopMessage(record("99", SAURON, "-5"));
        assertTrue(message.contains("has an illegal negative number value: -5"), message);
        assertTrue(message.contains("line: 7"), message);
        assertTrue(message.contains("test.txt"), message);

        assertTrue(stopMessage(record("99", SAURON, "-0")).contains("illegal negative number value: -0"));
    }

    /**
     * A lone {@code -} is refused in C both ways over (no digits and a leading minus); the port reports
     * it as the negative value.
     */
    @Test
    void loneMinusIsNegative() {
        assertTrue(stopMessage(record("-", SAURON, "1")).contains("negative level"));
        assertTrue(stopMessage(record("99", SAURON, "-")).contains("negative number"));
    }

    /**
     * Text with no leading digit gives {@code z == tok} in C. The lexer never produces it; the message
     * comes from {@code ParseUtils} and names the directive.
     */
    @Test
    void textWithNoDigitsStopsTheLoad() {
        String level = stopMessage(record("abc", SAURON, "1"));
        assertTrue(level.contains("invalid level value: abc"), level);

        String number = stopMessage(record("99", SAURON, "x1"));
        assertTrue(number.contains("invalid number value: x1"), number);
    }

    // ---- number: strtoul base 0 -> int -----------------------------------

    /**
     * {@code max_num} is a plain {@code int}, so no byte truncation: 300 stays 300, and
     * {@code Integer.MAX_VALUE} is kept as is.
     */
    @Test
    void numberIsStoredAsRead() {
        assertEquals(0, single("99", SAURON, "0").getMaxNumber());
        assertEquals(1, single("99", SAURON, "1").getMaxNumber());
        assertEquals(300, single("99", SAURON, "300").getMaxNumber());
        assertEquals(Integer.MAX_VALUE, single("99", SAURON, "2147483647").getMaxNumber());
    }

    /**
     * Base 0, as for the level: {@code 010} is 8 and {@code 08} is 0.
     */
    @Test
    void numberIsReadInBaseZero() {
        assertEquals(8, single("99", SAURON, "010").getMaxNumber());
        assertEquals(0, single("99", SAURON, "08").getMaxNumber());
        assertEquals(0, single("99", SAURON, "00").getMaxNumber());
        assertEquals(255, single("99", SAURON, "0377").getMaxNumber());
    }

    /**
     * Accepted deviation (261027): C stores 2147483648 as {@code INT_MIN} (the unsigned value wraps in
     * the {@code int}) and 4294967295 as -1, and 4294967296 as 0. The port clamps to
     * {@code Integer.MAX_VALUE}, keeps the record and reports nothing.
     */
    @Test
    void numberAboveIntMaxIsClampedWhereCWraps() {
        assertEquals(Integer.MAX_VALUE, single("99", SAURON, "2147483648").getMaxNumber());
        assertEquals(Integer.MAX_VALUE, single("99", SAURON, "4294967295").getMaxNumber());
        assertEquals(Integer.MAX_VALUE, single("99", SAURON, "4294967296").getMaxNumber());
        assertEquals(Integer.MAX_VALUE, single("99", SAURON, "99999999999999999999").getMaxNumber());
    }

    // ---- race: lookup_monster ---------------------------------------------

    /**
     * {@code lookup_monster} matches an exact name case-insensitively first; failing that, the first
     * race whose name contains the query.
     */
    @Test
    void raceResolvesCaseInsensitivelyAndBySubstring() {
        assertEquals(SAURON, single("99", "SAURON, THE SORCERER", "1").getRace().getName());
        assertEquals(MORGOTH, single("100", "morgoth, lord", "1").getRace().getName());
    }

    /**
     * C: {@code parse_quest_race} returns {@code PARSE_ERROR_INVALID_MONSTER}, which stops the load.
     */
    @Test
    void unknownRaceStopsTheLoad() {
        String message = stopMessage(record("5", NO_SUCH_RACE, "1"));

        assertTrue(message.contains("has an illegal monster race value: " + NO_SUCH_RACE), message);
        assertTrue(message.contains("line: 7"), message);
        assertTrue(message.contains("test.txt"), message);
    }

    /**
     * An empty race text matches nothing in C ({@code my_stristr} never reports a match for an empty
     * pattern, and no race name is empty), so the load stops. The grammar cannot produce it.
     */
    @Test
    void emptyRaceStopsTheLoad() {
        assertTrue(stopMessage(record("5", "", "1")).contains("illegal monster race value: "));
    }

    // ---- ordering of the checks -------------------------------------------

    /**
     * C meets the directives as level, race, number. A negative level with an unknown race therefore
     * fails on the level.
     */
    @Test
    void levelIsCheckedBeforeRace() {
        String message = stopMessage(record("-1", NO_SUCH_RACE, "1"));
        assertTrue(message.contains("negative level"), message);
    }

    /**
     * An unknown race with a negative number fails on the race.
     */
    @Test
    void raceIsCheckedBeforeNumber() {
        String message = stopMessage(record("5", NO_SUCH_RACE, "-1"));
        assertTrue(message.contains("monster race"), message);
    }

    /**
     * A negative level and a negative number in one record fail on the level, the first directive C
     * reads.
     */
    @Test
    void levelIsCheckedBeforeNumber() {
        String message = stopMessage(record("-1", SAURON, "-1"));
        assertTrue(message.contains("negative level"), message);
    }

    /**
     * Records are met in file order and the first fault ends the load: the second record's bad race is
     * reported and the third record's negative level never is, and nothing is returned.
     */
    @Test
    void theFirstFaultInFileOrderWins() {
        String message = stopMessage(
                record("99", SAURON, "1"),
                new QuestParseRecord("Bad race", "5", NO_SUCH_RACE, "1", 20),
                new QuestParseRecord("Bad level", "-1", SAURON, "1", 30));

        assertTrue(message.contains("monster race"), message);
        assertTrue(message.contains("line: 20"), message);
    }

    // ---- no record is dropped ----------------------------------------------

    /**
     * Nothing is dropped any more: oversized figures are clamped and kept, so the indices stay the
     * records' positions and the error list stays empty.
     */
    @Test
    void oversizedFiguresKeepTheirRecordsAndIndices() {
        List<String> errors = new ArrayList<>();
        List<Quest> quests = assemble(errors,
                record("5", SAURON, "2147483648"),
                record("99", SAURON, "1"),
                record("9223372036854775808", SAURON, "1"),
                record("100", MORGOTH, "1"));

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(4, quests.size());
        for (int i = 0; i < quests.size(); i++) {
            assertEquals(i, quests.get(i).getIndex());
        }
        assertEquals(5, quests.get(0).getLevel());
        assertEquals(Integer.MAX_VALUE, quests.get(0).getMaxNumber());
        assertEquals(99, quests.get(1).getLevel());
        assertEquals(255, quests.get(2).getLevel());
        assertEquals(100, quests.get(3).getLevel());
    }
}
