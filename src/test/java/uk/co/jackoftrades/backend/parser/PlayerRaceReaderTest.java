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
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerBody;
import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
import uk.co.jackoftrades.middle.player.PlayerRace;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link PlayerRaceReader}: file text -> {@code PlayerRaceLexer}/
 * {@code PlayerRaceGrammar} -> {@link uk.co.jackoftrades.backend.parser.playerrace.PlayerRaceParseRecord}
 * -> {@link uk.co.jackoftrades.backend.parser.playerrace.PlayerRaceAssembler} -> {@link PlayerRace}.
 *
 * <p>Unlike a self-contained reader, the race assembler resolves two cross-file references at load
 * time — the equipment {@link PlayerBody} (always index 0) and the background
 * {@link PlayerHistoryChart} — through {@link GameConstants}. {@link #seed()} therefore loads the
 * real {@code body.txt} and {@code history.txt} through their own readers and injects them into the
 * private static registries by reflection, restoring them afterwards so no global state leaks to
 * other suites.
 *
 * <p>{@link PlayerRace} exposes only {@code getName()}, so the remaining assertions read its private
 * fields reflectively via {@link #field}. Several tests deliberately pin the fixes this round put in:
 * the {@code PF_}/{@code ELEM_} family prefixes, and {@code lookupPlayerBody(0)} returning the real
 * humanoid body rather than {@code null}.
 *
 * @author Rowan Crowther
 */
class PlayerRaceReaderTest {

    private static final String REAL_FILE = "lib/gamedata/p_race.txt";
    private static final String BODY_FILE = "lib/gamedata/body.txt";
    private static final String HISTORY_FILE = "lib/gamedata/history.txt";

    private static Object savedBodies, savedHistories;

    /**
     * Seeds the two registries the race assembler resolves against — bodies and history charts — so
     * that every record's {@code lookupPlayerBody(0)} and {@code history:} line can resolve.
     *
     * @author Rowan Crowther
     */
    @BeforeAll
    static void seed() throws Exception {
        savedBodies = setStatic("playerBodies", new BodyReader().parse(BODY_FILE));
        savedHistories = setStatic("playerHistoryCharts", new HistoryReader().parse(HISTORY_FILE));
    }

    /**
     * Restores the registries mutated by {@link #seed()} so the shared static state on
     * {@link GameConstants} does not leak into other test suites.
     *
     * @author Rowan Crowther
     */
    @AfterAll
    static void restore() throws Exception {
        setStatic("playerBodies", savedBodies);
        setStatic("playerHistoryCharts", savedHistories);
    }

    // ---- fixture + reflection helpers ------------------------------------

    @TempDir
    Path tempDir;

    private ParseResult<PlayerRace> load(String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.writeString(file, content);
        return new PlayerRaceReader().parseWithResults(file.toString());
    }

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * A single race record: a name line followed by whatever body lines the test supplies.
     */
    private static String race(String name, String body) {
        return "name:" + name + "\n" + body;
    }

    private static PlayerRace byName(List<PlayerRace> races, String name) {
        return races.stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = GameConstants.class.getDeclaredField(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    /**
     * Reads a private field off a {@link PlayerRace} (which exposes only {@code getName()}).
     */
    @SuppressWarnings("unchecked")
    private static <T> T field(PlayerRace r, String name) throws Exception {
        Field f = PlayerRace.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(r);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllElevenRacesWithNoErrors() throws IOException {
        ParseResult<PlayerRace> result = new PlayerRaceReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(11, result.items().size());
    }

    @Test
    void scalarFieldsAreParsedIncludingNegatives() throws Exception {
        PlayerRace troll = byName(new PlayerRaceReader().parse(REAL_FILE), "Half-Troll");
        assertNotNull(troll);

        assertEquals(12, (int) field(troll, "raceMhp"));   // hitdie
        assertEquals(120, (int) field(troll, "raceExp"));  // exp
        assertEquals(3, (int) field(troll, "infravision"));
        assertEquals(20, (int) field(troll, "baseAge"));
        assertEquals(10, (int) field(troll, "modAge"));
        assertEquals(90, (int) field(troll, "baseHeight"));
        assertEquals(16, (int) field(troll, "modHeight"));
        assertEquals(240, (int) field(troll, "baseWeight"));
        assertEquals(60, (int) field(troll, "modWeight"));
    }

    @Test
    void statsMapKeepsTheStrIntWisDexConOrderingAndNegatives() throws Exception {
        // stats:4:-4:-2:-4:3  ->  STR INT WIS DEX CON  (order matters, negatives allowed)
        PlayerRace troll = byName(new PlayerRaceReader().parse(REAL_FILE), "Half-Troll");
        Map<Stats, Integer> stats = field(troll, "statsAdj");

        assertEquals(4, (int) stats.get(Stats.STAT_STR));
        assertEquals(-4, (int) stats.get(Stats.STAT_INT));
        assertEquals(-2, (int) stats.get(Stats.STAT_WIS));
        assertEquals(-4, (int) stats.get(Stats.STAT_DEX));
        assertEquals(3, (int) stats.get(Stats.STAT_CON));
    }

    @Test
    void skillsResolveIncludingTheRenamedMeleeBowThrowDigKeys() throws Exception {
        PlayerRace troll = byName(new PlayerRaceReader().parse(REAL_FILE), "Half-Troll");
        Map<PlayerSkill, Integer> skills = field(troll, "skillsAdj");

        assertEquals(20, (int) skills.get(PlayerSkill.SKILL_TO_HIT_MELEE));   // skill-melee
        assertEquals(-10, (int) skills.get(PlayerSkill.SKILL_TO_HIT_BOW));    // skill-shoot
        assertEquals(-10, (int) skills.get(PlayerSkill.SKILL_TO_HIT_THROW));  // skill-throw
        assertEquals(-5, (int) skills.get(PlayerSkill.SKILL_DISARM_PHYS));

        PlayerRace dwarf = byName(new PlayerRaceReader().parse(REAL_FILE), "Dwarf");
        Map<PlayerSkill, Integer> dwarfSkills = field(dwarf, "skillsAdj");
        assertEquals(40, (int) dwarfSkills.get(PlayerSkill.SKILL_DIGGING));   // skill-dig
    }

    @Test
    void objectFlagsGetTheOfPrefixAndSplitOnThePipe() throws Exception {
        // obj-flags:SUST_STR | REGEN  -> both flags set, OF_ prefix applied by the assembler
        PlayerRace troll = byName(new PlayerRaceReader().parse(REAL_FILE), "Half-Troll");
        Flag<ObjectFlag> of = field(troll, "oFlags");

        assertTrue(of.has(ObjectFlag.OF_SUST_STR));
        assertTrue(of.has(ObjectFlag.OF_REGEN));
    }

    @Test
    void playerFlagsGetThePfPrefix() throws Exception {
        // Guards this round's fix: player-flags names carry no PF_ in the file.
        List<PlayerRace> races = new PlayerRaceReader().parse(REAL_FILE);

        assertTrue(this.<Flag<PlayerFlag>>reflect(byName(races, "Hobbit"), "pFlags")
                .has(PlayerFlag.PF_KNOW_MUSHROOM));
        assertTrue(this.<Flag<PlayerFlag>>reflect(byName(races, "Gnome"), "pFlags")
                .has(PlayerFlag.PF_KNOW_ZAPPER));
        assertTrue(this.<Flag<PlayerFlag>>reflect(byName(races, "Dwarf"), "pFlags")
                .has(PlayerFlag.PF_SEE_ORE));
    }

    @Test
    void resistValuesGetTheElemPrefixAndStoreTheirLevel() throws Exception {
        // Guards this round's fix: values:RES_LIGHT[1] -> ELEM_LIGHT resist level 1
        List<PlayerRace> races = new PlayerRaceReader().parse(REAL_FILE);

        Map<ElementEnum, ElementInfo> elf = field(byName(races, "Elf"), "resists");
        assertEquals(1, elf.get(ElementEnum.ELEM_LIGHT).getResLevel());

        Map<ElementEnum, ElementInfo> orc = field(byName(races, "Half-Orc"), "resists");
        assertEquals(1, orc.get(ElementEnum.ELEM_DARK).getResLevel());

        Map<ElementEnum, ElementInfo> kobold = field(byName(races, "Kobold"), "resists");
        assertEquals(1, kobold.get(ElementEnum.ELEM_POIS).getResLevel());
    }

    @Test
    void everyRaceGetsTheHumanoidBodyNotNull() throws Exception {
        // Guards the lookupPlayerBody(0) fix: it must return the real body, not null.
        PlayerBody humanoid = GameConstants.lookupPlayerBody(0);
        for (PlayerRace r : new PlayerRaceReader().parse(REAL_FILE)) {
            PlayerBody body = field(r, "body");
            assertNotNull(body, () -> r.getName() + " has a null body");
            assertSame(humanoid, body);
            assertEquals(12, body.getCount());
        }
    }

    @Test
    void historyChartResolvesToTheReferencedChart() throws Exception {
        PlayerRace troll = byName(new PlayerRaceReader().parse(REAL_FILE), "Half-Troll");
        PlayerHistoryChart history = field(troll, "history");

        assertNotNull(history);
        assertEquals(21, history.getChartNumber());   // history:21
    }

    // ---- Soft errors: drop the record, keep the rest ---------------------

    @Test
    void unknownObjectFlagIsReportedAndTheRaceDropped() throws IOException {
        ParseResult<PlayerRace> result = load("bad-oflag.txt",
                withHeader(1, race("Weird", "obj-flags:NOTAFLAG\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("invalid object flag value")),
                result.errors()::toString);
    }

    @Test
    void unknownPlayerFlagIsReportedAndTheRaceDropped() throws IOException {
        ParseResult<PlayerRace> result = load("bad-pflag.txt",
                withHeader(1, race("Weird", "player-flags:NOTAFLAG\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("invalid player flag value")),
                result.errors()::toString);
    }

    @Test
    void unknownResistNameIsReportedAndTheRaceDropped() throws IOException {
        // Exercises the IllegalArgumentException catch added this round (was an uncaught crash).
        ParseResult<PlayerRace> result = load("bad-resist.txt",
                withHeader(1, race("Weird", "history:1\nvalues:RES_NOSUCH[1]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("invalid resist name")),
                result.errors()::toString);
    }

    @Test
    void badHistoryChartIsReportedAndTheRaceDropped() throws IOException {
        ParseResult<PlayerRace> result = load("bad-history.txt",
                withHeader(1, race("Weird", "history:99999\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("invalid history chart value")),
                result.errors()::toString);
    }

    @Test
    void oneBadRaceIsDroppedButTheOthersStillLoad() throws IOException {
        ParseResult<PlayerRace> result = load("mixed.txt", withHeader(2,
                race("Good", "history:1\n")
                        + race("Bad", "obj-flags:NOTAFLAG\n")));

        assertEquals(1, result.items().size());
        assertNotNull(byName(result.items(), "Good"));
        assertNull(byName(result.items(), "Bad"));
        assertTrue(result.hasErrors());
    }

    // ---- FLAG lexing: digit-bearing flags tokenise as one FLAG -----------

    @Test
    void digitBearingFlagLexesAsASingleTokenThenFailsEnumLookup() throws IOException {
        // FLAG_MODE allows digits in a flag body, so "NOPE_42" must reach the assembler intact
        // (one token) and be rejected by name — not split or cause a lexer error.
        ParseResult<PlayerRace> result = load("digit-flag.txt",
                withHeader(1, race("Weird", "obj-flags:NOPE_42\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(s -> s.contains("NOPE_42")),
                result.errors()::toString);
    }

    // ---- Record count + hard errors --------------------------------------

    @Test
    void recordCountMismatchIsReportedButTheValidRaceStillLoads() throws IOException {
        ParseResult<PlayerRace> result = load("bad-count.txt",
                withHeader(5, race("Solo", "history:1\n")));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<PlayerRace> result = load("no-header.txt", race("Solo", "history:1\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aRecordWithNoNameLineIsAHardError() throws IOException {
        // A race record must open with name:; a leading directive has nothing to attach to.
        ParseResult<PlayerRace> result = load("no-name.txt", withHeader(1, "history:1\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * Convenience wrapper so a reflected flag field reads inline in the flag tests.
     */
    private <T> T reflect(PlayerRace r, String name) throws Exception {
        return field(r, name);
    }
}
