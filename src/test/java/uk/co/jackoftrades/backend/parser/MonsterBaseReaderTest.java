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
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;

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
 * <p>The assembler resolves {@code pain:} via {@link GameConstants#lookupMonsterPain}, which reads
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
     * The value {@code GameConstants.monsterPains} held before this suite overwrote it.
     */
    private static Object savedMonsterPains;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void loadPainSets() throws Exception {
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();

        Field field = GameConstants.class.getDeclaredField("monsterPains");
        field.setAccessible(true);
        savedMonsterPains = field.get(null);
        field.set(null, pains);
    }

    @AfterAll
    static void restorePainSets() throws Exception {
        Field field = GameConstants.class.getDeclaredField("monsterPains");
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

        // The shipped file has a placeholder record-count:0, so the only expected error is the count
        // mismatch; every one of the 56 bases resolves (all pain indices are 1..12).
        assertEquals(56, result.items().size());
        assertEquals("ancient dragon", result.items().get(0).getCodeName());
        assertTrue(result.errors().stream().allMatch(e -> e.contains("record-count")),
                () -> "only the count-mismatch error was expected, got: " + result.errors());
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
}
