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
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
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
 * 261029, directive-before-name, wrong-value-count and whole-name cases added on 261118.
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

    /**
     * One directive on its own line, as {@code QuestGrammar} builds it.
     */
    private static Directive directive(String name, String value, int line) {
        return new Directive(name, List.of(value), line);
    }

    /**
     * A record of {@code name:} on {@code nameLine} followed by {@code level:}, {@code race:} and
     * {@code number:} on the next three lines, in that order.
     */
    private static QuestParseRecord full(String name, int nameLine, String level, String race, String number) {
        return new QuestParseRecord(List.of(
                directive("name", name, nameLine),
                directive("level", level, nameLine + 1),
                directive("race", race, nameLine + 2),
                directive("number", number, nameLine + 3)));
    }

    /**
     * The usual test record: {@code name:Test} on line 6, so level is on line 7, race on 8 and number on 9.
     */
    private static QuestParseRecord record(String level, String race, String number) {
        return full("Test", 6, level, race, number);
    }

    /**
     * A record of the given directives in the given order, the name first as the grammar always has it.
     */
    private static QuestParseRecord ordered(Directive... directives) {
        List<Directive> all = new ArrayList<>();
        all.add(directive("name", "Test", 1));
        all.addAll(List.of(directives));
        return new QuestParseRecord(all);
    }

    private static List<Quest> assemble(QuestParseRecord... records) {
        return new QuestAssembler().assemble(List.of(records), "test.txt");
    }

    private static Quest single(String level, String race, String number) {
        List<Quest> quests = assemble(record(level, race, number));
        assertEquals(1, quests.size());
        return quests.get(0);
    }

    /**
     * Run the records, expect the load to stop, and return the message. The list passed in is checked
     * to be still empty, because this assembler never writes to it.
     */
    private static String stopMessage(QuestParseRecord... records) {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> assemble(records));
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
        assertTrue(new QuestAssembler().assemble(List.of(), "test.txt").isEmpty());
    }

    /**
     * C's {@code finish_parse_quest} numbers quests by final array position, which is file order
     * (the linked list is built newest-first and filled from the back).
     */
    @Test
    void indicesFollowFileOrder() {
        List<Quest> quests = assemble(record("99", SAURON, "1"),
                record("100", MORGOTH, "1"));

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
                full("Sauron", 3, "99", SAURON, "1"),
                full("Morgoth", 8, "100", MORGOTH, "1")), "test.txt");

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
        assertTrue(message.contains("negative level value"), message);
        assertTrue(message.contains(": -1"), message);
        assertTrue(message.contains("line: 7"), message);
        assertTrue(message.contains("test.txt"), message);

        assertTrue(stopMessage(record("-0", SAURON, "1")).contains("negative level value"));
        assertTrue(stopMessage(record("-2147483648", SAURON, "1")).contains("negative level"));
    }

    @Test
    void negativeNumberStopsTheLoad() {
        String message = stopMessage(record("99", SAURON, "-5"));
        assertTrue(message.contains("negative number value: -5"), message);
        assertTrue(message.contains("line: 9"), message);
        assertTrue(message.contains("test.txt"), message);

        assertTrue(stopMessage(record("99", SAURON, "-0")).contains("negative number value: -0"));
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
        assertTrue(number.contains("number value: x1"), number);
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

        assertTrue(message.contains("monster race value"), message);
        assertTrue(message.contains(": " + NO_SUCH_RACE), message);
        assertTrue(message.contains("line: 8"), message);
        assertTrue(message.contains("test.txt"), message);
    }

    /**
     * An empty race text matches nothing in C ({@code my_stristr} never reports a match for an empty
     * pattern, and no race name is empty), so the load stops. The grammar cannot produce it.
     */
    @Test
    void emptyRaceStopsTheLoad() {
        assertTrue(stopMessage(record("5", "", "1")).contains("monster race value"));
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
                full("Bad race", 20, "5", NO_SUCH_RACE, "1"),
                full("Bad level", 30, "-1", SAURON, "1"));

        assertTrue(message.contains("monster race"), message);
        assertTrue(message.contains("line: 22"), message);
    }

    // ---- no record is dropped ----------------------------------------------

    /**
     * Nothing is dropped any more: oversized figures are clamped and kept, so the indices stay the
     * records' positions.
     */
    @Test
    void oversizedFiguresKeepTheirRecordsAndIndices() {
        List<Quest> quests = assemble(record("5", SAURON, "2147483648"),
                record("99", SAURON, "1"),
                record("9223372036854775808", SAURON, "1"),
                record("100", MORGOTH, "1"));

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

    // ---- directives in the order the file gives them -----------------------

    // C acts on each line as it reads it, so the order of the lines is the order of the checks and the
    // order of the writes. These cases hand the assembler directives in orders the old fixed grammar
    // could not produce.

    @Test
    void directivesInAnyOrderAssembleAsInC() {
        List<Quest> quests = assemble(ordered(
                directive("number", "3", 2),
                directive("race", SAURON, 3),
                directive("level", "99", 4)));

        assertEquals(99, quests.getFirst().getLevel());
        assertEquals(SAURON, quests.getFirst().getRace().getName());
        assertEquals(3, quests.getFirst().getMaxNumber());
    }

    /**
     * C zero-fills the quest: no {@code level:} is level 0, no {@code number:} is 0 and no {@code race:}
     * is a null race, not an error.
     */
    @Test
    void aRecordWithNoLevelRaceOrNumberLoadsZeroedAsInC() {
        Quest quest = assemble(ordered()).getFirst();

        assertEquals("Test", quest.getName());
        assertEquals(0, quest.getLevel());
        assertNull(quest.getRace());
        assertEquals(0, quest.getCurrentNumber());
        assertEquals(0, quest.getMaxNumber());
    }

    @Test
    void aRepeatedDirectiveTakesTheLastValueAsInC() {
        Quest quest = assemble(ordered(
                directive("level", "5", 2),
                directive("level", "99", 3),
                directive("number", "1", 4),
                directive("number", "7", 5))).getFirst();

        assertEquals(99, quest.getLevel());
        assertEquals(7, quest.getMaxNumber());
    }

    /**
     * A bad value is refused where it stands, even when a later line of the same kind would have
     * replaced it, because C returns the error from that line's handler.
     */
    @Test
    void aBadValueTheNextLineWouldReplaceIsStillRefusedAsInC() {
        String level = stopMessage(ordered(directive("level", "-5", 2), directive("level", "99", 3)));
        assertTrue(level.contains("negative level value"), level);
        assertTrue(level.contains(": -5"), level);
        assertTrue(level.contains("line: 2"), level);

        String number = stopMessage(ordered(directive("number", "-1", 2), directive("number", "1", 3)));
        assertTrue(number.contains("negative number value: -1"), number);

        String race = stopMessage(ordered(directive("race", NO_SUCH_RACE, 2), directive("race", SAURON, 3)));
        assertTrue(race.contains("monster race value"), race);
        assertTrue(race.contains("line: 2"), race);
    }

    /**
     * The line that comes first in the file is the one reported: here the negative number, ahead of the
     * negative level and the unknown race, which is the reverse of the old fixed level-race-number order.
     */
    @Test
    void theFirstBadDirectiveInFileOrderIsTheOneReported() {
        String message = stopMessage(ordered(
                directive("number", "-1", 2),
                directive("level", "-5", 3),
                directive("race", NO_SUCH_RACE, 4)));

        assertTrue(message.contains("negative number value"), message);
        assertTrue(message.contains("line: 2"), message);
    }

    @Test
    void aDirectiveNameTheAssemblerDoesNotKnowStopsTheLoad() {
        String message = stopMessage(ordered(directive("colour", "red", 2)));

        assertTrue(message.contains("illegal directive value: colour"), message);
        assertTrue(message.contains("line: 2"), message);
    }

    /**
     * The grammar never gives a directive more than one value, so this only guards a hand-built record.
     */
    @Test
    void aDirectiveWithTwoValuesStopsTheLoad() {
        String message = stopMessage(ordered(new Directive("level", List.of("1", "2"), 2)));

        assertTrue(message.contains("incorrect number of level values"), message);
    }

    /**
     * Same guard for {@code race:} and {@code number:}, and for a {@code name:} with two values.
     */
    @Test
    void aRaceNumberOrNameWithTwoValuesStopsTheLoad() {
        String race = stopMessage(ordered(new Directive("race", List.of(SAURON, MORGOTH), 2)));
        assertTrue(race.contains("incorrect number of race values"), race);

        String number = stopMessage(ordered(new Directive("number", List.of("1", "2"), 2)));
        assertTrue(number.contains("incorrect number of number values"), number);

        String name = stopMessage(new QuestParseRecord(List.of(new Directive("name", List.of("A", "B"), 1))));
        assertTrue(name.contains("incorrect number of name values"), name);
    }

    // ---- a directive before the name: C asserts, the port throws -------------

    // C's parse_quest_level, parse_quest_race and parse_quest_number each assert(q) on parser_priv(p),
    // which is NULL until parse_quest_name has run, so C crashes on a file whose first quest line is not
    // name:. The grammar refuses such a file first; these cases hand the assembler a record it would
    // never build, to pin the second line of defence. Accepted 261118 ("Where C crashes, Java throws").

    /**
     * Each of the three directives, first in its record, stops the load with a message that names the
     * directive and its own line.
     */
    @Test
    void aLevelRaceOrNumberBeforeTheNameStopsTheLoadHereWhereCCrashes() {
        List<Directive> firsts = List.of(
                directive("level", "99", 4),
                directive("race", SAURON, 5),
                directive("number", "1", 6));

        for (Directive first : firsts) {
            String message = stopMessage(new QuestParseRecord(List.of(first)));

            assertTrue(message.contains("a " + first.name() + " directive before a name directive"), message);
            assertTrue(message.contains("line: " + first.line()), message);
            assertTrue(message.contains("file: test.txt"), message);
        }
    }

    /**
     * The refusal is at the early directive, not deferred to the {@code name:} behind it: C asserts on the
     * first line it meets.
     */
    @Test
    void aNameAfterTheDirectiveDoesNotRescueIt() {
        String message = stopMessage(new QuestParseRecord(List.of(
                directive("level", "99", 3),
                directive("name", "Late", 4))));

        assertTrue(message.contains("a level directive before a name directive"), message);
        assertTrue(message.contains("line: 3"), message);
    }

    /**
     * A name given to an earlier record is not carried over: a second record that opens with
     * {@code level:} is refused, and it is the second record's line that is reported.
     */
    @Test
    void theEarlierRecordsNameDoesNotSatisfyALaterRecord() {
        String message = stopMessage(
                record("99", SAURON, "1"),
                new QuestParseRecord(List.of(directive("level", "100", 12))));

        assertTrue(message.contains("a level directive before a name directive"), message);
        assertTrue(message.contains("line: 12"), message);
    }

    /**
     * A name followed by any directive is the ordinary shape, so the guard must not fire: this is the
     * boundary against the cases above.
     */
    @Test
    void aDirectiveAfterTheNameIsNotRefused() {
        Quest quest = assemble(ordered(directive("level", "99", 2))).getFirst();

        assertEquals("Test", quest.getName());
        assertEquals(99, quest.getLevel());
    }

    // ---- the name is the rest of the line ----------------------------------

    /**
     * C's {@code name} is a {@code str} field, which {@code parser_parse} takes to the end of the line
     * without tokenizing on {@code :}, and {@code string_make} copies it whole: colons, commas and blanks
     * at either end all survive.
     */
    @Test
    void theNameIsKeptWhole() {
        Quest quest = assemble(new QuestParseRecord(List.of(
                directive("name", "  Sauron: the, Sorcerer  ", 1)))).getFirst();

        assertEquals("  Sauron: the, Sorcerer  ", quest.getName());
    }
}
