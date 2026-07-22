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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.player.Quest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link QuestReader}: file text -&gt; {@code QuestLexer}/
 * {@code QuestGrammar} -&gt; {@link uk.co.jackoftrades.backend.parser.quest.QuestParseRecord} -&gt;
 * {@code QuestAssembler} -&gt; {@link Quest}.
 *
 * <p>The quest assembler resolves each {@code race:} name against the loaded monster races via
 * {@link GameConstants#lookupMonsterRace}, so the registry must be populated first. Rather than
 * hand-seed it, the suite runs the full {@link GameConstants#init()} chain in {@link #bootstrap()}
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
    static void bootstrap() {
        GameConstants.init();
    }

    /**
     * {@link GameConstants#init()} populates the shared registries in place; reset the object-kind
     * ones to the empty baseline so this heavy load does not leak into order-sensitive suites
     * (matching {@code PitReaderTest}'s cleanup).
     */
    @AfterAll
    static void cleanup() throws Exception {
        GameConstants.objectKinds.clear();
        Field f = GameConstants.class.getDeclaredField("kindsByTvalSval");
        f.setAccessible(true);
        ((java.util.Map<?, ?>) f.get(null)).clear();
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
        Field f = GameConstants.class.getDeclaredField(name);
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
     * The genuine startup path: {@link GameConstants#init()} (run in {@link #bootstrap()}) calls
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

    // ---- Soft errors (record dropped, parse continues) -------------------

    @Test
    void unknownRaceIsASoftErrorAndDropsTheRecord() throws IOException {
        ParseResult<Quest> result = load("badrace.txt",
                withHeader(1, quest("Impossible", "5", "Xyzzy the Nonexistent", "1")));

        assertTrue(result.items().isEmpty(), "the record with the unknown race must be dropped");
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown monster race")),
                () -> result.errors().toString());
    }

    /**
     * The grammar constrains {@code level:} to an INTEGER token, so a non-numeric value is a hard
     * lexer error, never the assembler's {@code NumberFormatException}. The one value that is a valid
     * INTEGER token yet still fails {@code Integer.parseInt} is one that overflows {@code int} - that
     * is the only way to reach (and hence the only way to test) the assembler's level guard.
     */
    @Test
    void overflowingLevelIsASoftErrorAndDropsTheRecord() throws IOException {
        ParseResult<Quest> result = load("biglevel.txt",
                withHeader(1, quest("Deep", "99999999999", "Sauron, the Sorcerer", "1")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid level integer")),
                () -> result.errors().toString());
    }

    @Test
    void overflowingNumberIsASoftErrorAndDropsTheRecord() throws IOException {
        ParseResult<Quest> result = load("bignumber.txt",
                withHeader(1, quest("Many", "5", "Sauron, the Sorcerer", "99999999999")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid number integer")),
                () -> result.errors().toString());
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
     * Indices are assigned only to records that survive assembly, so a dropped first record must not
     * leave a gap: the surviving quest takes index 0. Guards the {@code index++}-before-add placement.
     */
    @Test
    void indicesAreContiguousAfterADroppedRecord() throws IOException {
        String body = quest("Dropped", "5", "Xyzzy the Nonexistent", "1")
                + "\n"
                + quest("Kept", "10", "Morgoth, Lord of Darkness", "1");
        ParseResult<Quest> result = load("gap.txt", withHeader(2, body));

        assertEquals(1, result.items().size());
        Quest kept = result.items().get(0);
        assertEquals("Kept", kept.getName());
        assertEquals(0, kept.getIndex(), "surviving quest must take index 0, not inherit the drop's slot");
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
}
