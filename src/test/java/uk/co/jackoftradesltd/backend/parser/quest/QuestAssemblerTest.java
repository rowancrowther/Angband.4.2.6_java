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
 * {@code parse_quest_number} ({@code player-quest.c}) and from {@code parser_getuint} /
 * {@code parser_parse} ({@code parser.c}), which read the field with {@code strtoul} into an
 * {@code unsigned int}, reject a leading {@code -} with {@code PARSE_ERROR_NOT_NUMBER}, and are then
 * stored into a {@code uint8_t} ({@code level}) or an {@code int} ({@code max_num}). Cases that
 * exercise a deliberate divergence from C say so in their comment.
 *
 * <p>Race resolution needs {@code monster.txt}, so {@link #bootstrap()} runs the same full
 * {@code GameConstants.init} chain that {@code QuestReaderTest} uses.
 *
 * <p>Class QuestAssemblerTest coded on 261014, commented in full on 261014.
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

    // ---- level: strtoul -> unsigned int -> uint8_t -----------------------

    /**
     * {@code quest.level} is a {@code uint8_t}: 255 fits, 256 wraps to 0, 257 to 1. Zero is a legal
     * level (it is also how a completed quest is marked).
     */
    @Test
    void levelIsStoredModulo256() {
        assertEquals(0, single("0", SAURON, "1").getLevel());
        assertEquals(255, single("255", SAURON, "1").getLevel());
        assertEquals(0, single("256", SAURON, "1").getLevel());
        assertEquals(1, single("257", SAURON, "1").getLevel());
        assertEquals(100, single("356", SAURON, "1").getLevel());
    }

    /**
     * {@code parser_getuint} returns an {@code unsigned int}, so the stored value is the parsed value
     * modulo 2^32 before it is cut to a byte. 2147483649 (2^31 + 1) is negative as a Java {@code int}
     * and must still give 1; 4294967295 (2^32 - 1) gives 255; 4294967296 (2^32) gives 0.
     */
    @Test
    void levelWrapsThroughUnsignedIntBeforeTheByte() {
        assertEquals(0, single("2147483648", SAURON, "1").getLevel());
        assertEquals(1, single("2147483649", SAURON, "1").getLevel());
        assertEquals(255, single("4294967295", SAURON, "1").getLevel());
        assertEquals(0, single("4294967296", SAURON, "1").getLevel());
        assertEquals(1, single("4294967297", SAURON, "1").getLevel());
    }

    /**
     * 99999999999 is 0x174876E7FF, so its low byte is 255. It is above {@code int} but within
     * {@code unsigned long}, so C stores 255 without complaint and the port must keep the record.
     */
    @Test
    void levelBeyondIntButWithinLongStillWrapsAndIsKept() {
        assertEquals(255, single("99999999999", SAURON, "1").getLevel());
        // Long.MAX_VALUE is 0x7FFFFFFFFFFFFFFF.
        assertEquals(255, single("9223372036854775807", SAURON, "1").getLevel());
    }

    /**
     * Deliberate divergence (class block, 261013): above {@code Long.MAX_VALUE} the port drops the
     * record with a soft error where C would wrap (2^63 would give a stored level of 0).
     */
    @Test
    void levelAboveLongMaxIsDroppedAsASoftError() {
        List<String> errors = new ArrayList<>();
        List<Quest> quests = assemble(errors, record("9223372036854775808", SAURON, "1"));

        assertTrue(quests.isEmpty());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("invalid level integer"), errors.get(0));
        assertTrue(errors.get(0).contains("line 7"), errors.get(0));
    }

    /**
     * Deliberate divergence (class block, 261013): C reads base 0, so {@code 010} is octal 8; the
     * port always reads decimal and stores 10.
     */
    @Test
    void levelWithLeadingZeroIsReadAsDecimalNotOctal() {
        assertEquals(10, single("010", SAURON, "1").getLevel());
        assertEquals(8, single("08", SAURON, "1").getLevel());
    }

    // ---- level / number: leading '-' is a hard error ---------------------

    /**
     * C: {@code z == tok || *tok == '-'} gives {@code PARSE_ERROR_NOT_NUMBER}, which stops the whole
     * file load. {@code -0} is rejected too, because the test is on the first character, not the value.
     */
    @Test
    void negativeLevelAbortsTheLoad() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> assemble(new ArrayList<>(), record("-1", SAURON, "1")));
        assertTrue(e.getMessage().contains("-1"), e.getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> assemble(new ArrayList<>(), record("-0", SAURON, "1")));
    }

    @Test
    void negativeNumberAbortsTheLoad() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> assemble(new ArrayList<>(), record("99", SAURON, "-5")));
        assertTrue(e.getMessage().contains("-5"), e.getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> assemble(new ArrayList<>(), record("99", SAURON, "-0")));
    }

    // ---- number: strtoul -> unsigned int -> int --------------------------

    /**
     * {@code max_num} is a plain {@code int}, so no byte truncation: 300 stays 300, and
     * {@code Integer.MAX_VALUE} is kept as is.
     */
    @Test
    void numberIsStoredAsParsed() {
        assertEquals(0, single("99", SAURON, "0").getMaxNumber());
        assertEquals(1, single("99", SAURON, "1").getMaxNumber());
        assertEquals(300, single("99", SAURON, "300").getMaxNumber());
        assertEquals(Integer.MAX_VALUE, single("99", SAURON, "2147483647").getMaxNumber());
    }

    /**
     * Deliberate divergence (class block, 261013): above {@code Integer.MAX_VALUE} the port drops the
     * record with a soft error where C wraps into a negative {@code int} (2147483648 gives
     * {@code INT_MIN}).
     */
    @Test
    void numberAboveIntMaxIsDroppedAsASoftError() {
        List<String> errors = new ArrayList<>();
        List<Quest> quests = assemble(errors, record("99", SAURON, "2147483648"));

        assertTrue(quests.isEmpty());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("invalid number integer"), errors.get(0));
        assertTrue(errors.get(0).contains("line: 7"), errors.get(0));
    }

    /**
     * Deliberate divergence (class block, 261013): {@code 010} is octal 8 in C and decimal 10 here.
     */
    @Test
    void numberWithLeadingZeroIsReadAsDecimalNotOctal() {
        assertEquals(10, single("99", SAURON, "010").getMaxNumber());
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
     * The exact class matters: {@link IllegalArgumentException} is the negative-integer error, and a
     * plain {@link RuntimeException} is the race one.
     */
    @Test
    void unknownRaceAbortsTheLoadWithAPlainRuntimeException() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> assemble(new ArrayList<>(), record("5", NO_SUCH_RACE, "1")));

        assertEquals(RuntimeException.class, e.getClass());
        assertTrue(e.getMessage().contains(NO_SUCH_RACE), e.getMessage());
        assertTrue(e.getMessage().contains("line: 7"), e.getMessage());
    }

    // ---- ordering of the checks -------------------------------------------

    /**
     * C meets the directives as level, race, number. A negative level with an unknown race therefore
     * fails on the level.
     */
    @Test
    void levelIsCheckedBeforeRace() {
        assertThrows(IllegalArgumentException.class,
                () -> assemble(new ArrayList<>(), record("-1", NO_SUCH_RACE, "1")));
    }

    /**
     * An unknown race with a negative number fails on the race, so the exception is a plain
     * {@link RuntimeException} and not the {@link IllegalArgumentException} the number would give.
     */
    @Test
    void raceIsCheckedBeforeNumber() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> assemble(new ArrayList<>(), record("5", NO_SUCH_RACE, "-1")));

        assertEquals(RuntimeException.class, e.getClass());
    }

    /**
     * Consequence of the level divergence: the oversized level drops the record before the race is
     * looked up, so an unknown race alongside it is never reported. C would wrap the level and then
     * abort the load on the race.
     */
    @Test
    void droppedLevelRecordIsNeverRaceChecked() {
        List<String> errors = new ArrayList<>();
        List<Quest> quests = assemble(errors, record("9223372036854775808", NO_SUCH_RACE, "1"));

        assertTrue(quests.isEmpty());
        assertEquals(1, errors.size());
    }

    // ---- index contiguity -------------------------------------------------

    /**
     * Indices are assigned only to surviving records, so a dropped first record leaves no gap
     * (C stamps {@code index} from the final array position).
     */
    @Test
    void indicesStayContiguousAcrossDroppedRecords() {
        List<String> errors = new ArrayList<>();
        List<Quest> quests = assemble(errors,
                record("5", SAURON, "2147483648"),
                record("99", SAURON, "1"),
                record("9223372036854775808", SAURON, "1"),
                record("100", MORGOTH, "1"));

        assertEquals(2, errors.size());
        assertEquals(2, quests.size());
        assertEquals(0, quests.get(0).getIndex());
        assertEquals(99, quests.get(0).getLevel());
        assertEquals(1, quests.get(1).getIndex());
        assertEquals(100, quests.get(1).getLevel());
    }
}
