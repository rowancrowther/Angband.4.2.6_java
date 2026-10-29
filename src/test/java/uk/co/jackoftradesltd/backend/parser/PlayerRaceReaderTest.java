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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerHistoryChart;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link PlayerRaceReader}: file text -> {@code PlayerRaceLexer}/
 * {@code PlayerRaceGrammar} -> {@link uk.co.jackoftradesltd.backend.parser.playerrace.PlayerRaceParseRecord}
 * -> {@link uk.co.jackoftradesltd.backend.parser.playerrace.PlayerRaceAssembler} -> {@link PlayerRace}.
 *
 * <p>Unlike a self-contained reader, the race assembler resolves two cross-file references at load
 * time — the equipment {@link PlayerBody} (always index 0) and the background
 * {@link PlayerHistoryChart} — through {@link PlayerRegistry}. {@link #seed()} therefore loads the
 * real {@code body.txt} and {@code history.txt} through their own readers and injects them into the
 * private static registries by reflection, restoring them afterwards so no global state leaks to
 * other suites.
 *
 * <p>{@link PlayerRace} exposes only {@code getName()}, so the remaining assertions read its private
 * fields reflectively via {@link #field}. Several tests deliberately pin the fixes this round put in:
 * the {@code PF_}/{@code ELEM_} family prefixes, and {@code lookupPlayerBody(0)} returning the real
 * humanoid body rather than {@code null}.
 *
 * <p>Every error the assembler raises is a {@link DataFileParseException}, which ends the load as a
 * parser error does in C; the grammar's own errors still come back in the {@link ParseResult}. The
 * expected values for the assembler tests are worked from {@code init.c}, the {@code parse_p_race_*}
 * handlers and {@code finish_parse_p_race}, and {@code datafile.c}, not from the Java.
 *
 * <p>Class PlayerRaceReaderTest coded before 260915, updated on 261029 for the fatal assembler, commented in
 * full on 261029.
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
     */
    @BeforeAll
    static void seed() throws Exception {
        savedBodies = setStatic("playerBodies", new BodyReader().parse(BODY_FILE));
        savedHistories = setStatic("playerHistoryCharts", new HistoryReader().parse(HISTORY_FILE));
    }

    /**
     * Restores the registries mutated by {@link #seed()} so the shared static state on
     * the registries do not leak into other test suites.
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

    /**
     * Loads {@code content} and expects the assembler to end the load, returning the exception so that
     * a test can check the message names the offending value.
     */
    private DataFileParseException rejects(String fileName, String content) {
        return assertThrows(DataFileParseException.class, () -> load(fileName, content));
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
        Field f = RegistrySeeding.resolve(field);
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
        // Guards the lookupPlayerBody(0) fix: it must return the real body, not null. Identity is
        // deliberately not asserted - the registry hands out a copy per call, so that a race (and
        // in turn a player) cannot write into the loaded template.
        PlayerBody humanoid = PlayerRegistry.lookupPlayerBody(0);
        for (PlayerRace r : new PlayerRaceReader().parse(REAL_FILE)) {
            PlayerBody body = field(r, "body");
            assertNotNull(body, () -> r.getName() + " has a null body");
            assertNotSame(humanoid, body, () -> r.getName() + " shares the registry's body");
            assertEquals(humanoid.getName(), body.getName());
            assertEquals(12, body.getCount());
            for (int index = 0; index < body.getCount(); index++) {
                assertEquals(humanoid.getSlot(index).getName(), body.getSlot(index).getName());
                assertEquals(humanoid.getSlot(index).getType(), body.getSlot(index).getType());
            }
        }
    }

    @Test
    void historyChartResolvesToTheReferencedChart() throws Exception {
        PlayerRace troll = byName(new PlayerRaceReader().parse(REAL_FILE), "Half-Troll");
        PlayerHistoryChart history = field(troll, "history");

        assertNotNull(history);
        assertEquals(21, history.getChartNumber());   // history:21
    }

    // ---- Fatal errors: a C parser error ends the load --------------------

    @Test
    void unknownObjectFlagEndsTheLoad() {
        DataFileParseException e = rejects("bad-oflag.txt", withHeader(1, race("Weird", "obj-flags:NOTAFLAG\n")));

        assertTrue(e.getMessage().contains("illegal object flag value: NOTAFLAG"), e::getMessage);
    }

    @Test
    void unknownPlayerFlagEndsTheLoad() {
        DataFileParseException e = rejects("bad-pflag.txt", withHeader(1, race("Weird", "player-flags:NOTAFLAG\n")));

        assertTrue(e.getMessage().contains("illegal player flag value: NOTAFLAG"), e::getMessage);
    }

    @Test
    void unknownResistNameEndsTheLoad() {
        DataFileParseException e = rejects("bad-resist.txt",
                withHeader(1, race("Weird", "history:1\nvalues:RES_NOSUCH[1]\n")));

        assertTrue(e.getMessage().contains("illegal resist name value: RES_NOSUCH"), e::getMessage);
    }

    @Test
    void aResistNameWithoutThePrefixResIsRejectedAsCRejectsIt() {
        // grab_index_and_int builds "RES_" + each element name and compares the whole token, so
        // ELEM_FIRE and a bare FIRE match nothing. The assembler must not just drop four characters.
        DataFileParseException prefixed = rejects("elem-prefix.txt",
                withHeader(1, race("P", "history:1\nvalues:ELEM_FIRE[1]\n")));
        DataFileParseException bare = rejects("bare-name.txt",
                withHeader(1, race("B", "history:1\nvalues:FIRE[1]\n")));
        DataFileParseException short3 = rejects("short-name.txt",
                withHeader(1, race("S", "history:1\nvalues:AB[1]\n")));

        assertTrue(prefixed.getMessage().contains("illegal resist value: ELEM_FIRE"), prefixed::getMessage);
        assertTrue(bare.getMessage().contains("illegal resist value: FIRE"), bare::getMessage);
        assertTrue(short3.getMessage().contains("illegal resist value: AB"), short3::getMessage);
    }

    @Test
    void anUnknownHistoryChartLoadsTheRaceWithANullHistory() throws Exception {
        // parse_p_race_history stores whatever findchart returns, and findchart returns NULL for a
        // chart that does not exist, with no error.
        ParseResult<PlayerRace> result = load("bad-history.txt", withHeader(1, race("Weird", "history:99999\n")));

        assertEquals(1, result.items().size(), result.errors()::toString);
        assertNull(field(result.items().get(0), "history"));
    }

    @Test
    void oneBadRaceEndsTheWholeLoadAsCQuitsOnTheFirstParserError() {
        DataFileParseException e = rejects("mixed.txt", withHeader(2,
                race("Good", "history:1\n")
                        + race("Bad", "obj-flags:NOTAFLAG\n")));

        assertTrue(e.getMessage().contains("line: 4"), e::getMessage);
    }

    // ---- FLAG lexing: digit-bearing flags tokenise as one FLAG -----------

    @Test
    void digitBearingFlagLexesAsASingleTokenThenFailsEnumLookup() {
        // FLAG_MODE allows digits in a flag body, so "NOPE_42" must reach the assembler intact
        // (one token) and be rejected by name — not split or cause a lexer error.
        DataFileParseException e = rejects("digit-flag.txt",
                withHeader(1, race("Weird", "obj-flags:NOPE_42\n")));

        assertTrue(e.getMessage().contains("NOPE_42"), e::getMessage);
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

    // ---- The upstream table -----------------------------------------------

    /** The eleven races of the upstream {@code p_race.txt}, in file order. */
    private static final String[] UPSTREAM_NAMES = {"Human", "Half-Elf", "Elf", "Hobbit", "Gnome", "Dwarf",
            "Half-Orc", "Half-Troll", "Dunadan", "High-Elf", "Kobold"};

    /**
     * Each race's numbers as the upstream file gives them: the five stats in the file's
     * {@code str:int:wis:dex:con} order, the ten skills in the order disarm-phys, disarm-magic,
     * device, save, stealth, search, melee, shoot, throw, dig, then hitdie, exp, infravision,
     * history, and the base and modifier of age, height and weight.
     */
    private static final int[][] UPSTREAM_NUMBERS = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 100, 0, 1, 14, 6, 69, 10, 165, 35},
            {0, 1, -1, 1, -1, 2, 2, 3, 3, 1, 3, -1, 5, 5, 0, 10, 120, 2, 4, 24, 16, 71, 8, 115, 25},
            {-1, 2, -1, 1, -1, 5, 5, 6, 6, 2, 6, -5, 15, 15, 0, 9, 120, 3, 5, 75, 75, 73, 7, 90, 10},
            {-2, 2, 1, 3, 2, 15, 15, 18, 18, 4, 6, -10, 20, 20, 0, 7, 120, 4, 10, 21, 12, 34, 4, 55, 5},
            {-1, 2, 0, 2, 1, 10, 10, 22, 12, 3, 4, -8, 12, 12, 0, 8, 120, 4, 13, 50, 40, 40, 5, 80, 5},
            {2, -3, 2, -2, 2, 2, 2, 9, 9, -1, 2, 15, 0, 0, 40, 11, 120, 5, 16, 35, 15, 47, 4, 135, 20},
            {2, -1, 0, 0, 1, -3, -3, -3, -3, -1, -3, 12, -5, -5, 0, 10, 120, 3, 19, 11, 4, 64, 2, 135, 15},
            {4, -4, -2, -4, 3, -5, -5, -8, -8, -2, -9, 20, -10, -10, 0, 12, 120, 3, 21, 20, 10, 90, 16, 240, 60},
            {1, 2, 2, 2, 3, 4, 4, 5, 5, 1, 3, 15, 10, 10, 0, 10, 120, 0, 1, 50, 20, 80, 6, 190, 20},
            {1, 3, -1, 3, 1, 4, 4, 20, 20, 2, 10, 10, 25, 25, 0, 10, 145, 4, 7, 100, 30, 77, 6, 190, 20},
            {-1, -1, 0, 2, 2, 10, 10, 5, 0, 3, 10, -5, 10, 10, 0, 8, 120, 5, 23, 15, 10, 37, 4, 65, 5},
    };

    /** The {@code obj-flags:} names per race, without the {@code OF_} prefix. */
    private static final String[][] UPSTREAM_OBJ_FLAGS = {{}, {"SUST_DEX"}, {"SUST_DEX"}, {"HOLD_LIFE"},
            {"FREE_ACT"}, {"PROT_BLIND"}, {}, {"SUST_STR", "REGEN"}, {"SUST_CON"}, {"SEE_INVIS"}, {}};

    /** The {@code player-flags:} names per race, without the {@code PF_} prefix. */
    private static final String[][] UPSTREAM_PLAYER_FLAGS = {{}, {}, {}, {"KNOW_MUSHROOM"}, {"KNOW_ZAPPER"},
            {"SEE_ORE"}, {}, {}, {}, {}, {}};

    /** The element of each race's one {@code values:RES_x[1]} entry, or empty for none. */
    private static final String[][] UPSTREAM_RESISTS = {{}, {}, {"LIGHT"}, {}, {}, {}, {"DARK"}, {}, {},
            {"LIGHT"}, {"POIS"}};

    @Test
    void everyRaceAgreesWithATableWorkedOutFromTheUpstreamFile() throws Exception {
        List<PlayerRace> races = new PlayerRaceReader().parse(REAL_FILE);
        Stats[] statOrder = {Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON};
        PlayerSkill[] skillOrder = {PlayerSkill.SKILL_DISARM_PHYS, PlayerSkill.SKILL_DISARM_MAGIC,
                PlayerSkill.SKILL_DEVICE, PlayerSkill.SKILL_SAVE, PlayerSkill.SKILL_STEALTH,
                PlayerSkill.SKILL_SEARCH, PlayerSkill.SKILL_TO_HIT_MELEE, PlayerSkill.SKILL_TO_HIT_BOW,
                PlayerSkill.SKILL_TO_HIT_THROW, PlayerSkill.SKILL_DIGGING};
        String[] scalarOrder = {"raceMhp", "raceExp", "infravision"};
        String[] spreadOrder = {"baseAge", "modAge", "baseHeight", "modHeight", "baseWeight", "modWeight"};

        assertEquals(UPSTREAM_NAMES.length, races.size());
        for (int i = 0; i < UPSTREAM_NAMES.length; i++) {
            PlayerRace r = races.get(i);
            String who = UPSTREAM_NAMES[i];
            int[] n = UPSTREAM_NUMBERS[i];
            assertEquals(who, r.getName());

            Map<Stats, Integer> stats = field(r, "statsAdj");
            assertEquals(5, stats.size(), who);
            for (int k = 0; k < 5; k++) assertEquals(n[k], (int) stats.get(statOrder[k]), who + " " + statOrder[k]);

            Map<PlayerSkill, Integer> skills = field(r, "skillsAdj");
            assertEquals(10, skills.size(), who);
            for (int k = 0; k < 10; k++)
                assertEquals(n[5 + k], (int) skills.get(skillOrder[k]), who + " " + skillOrder[k]);

            for (int k = 0; k < 3; k++)
                assertEquals(n[15 + k], (int) field(r, scalarOrder[k]), who + " " + scalarOrder[k]);
            PlayerHistoryChart history = field(r, "history");
            assertEquals(n[18], history.getChartNumber(), who + " history");
            for (int k = 0; k < 6; k++)
                assertEquals(n[19 + k], (int) field(r, spreadOrder[k]), who + " " + spreadOrder[k]);

            Set<ObjectFlag> wantObj = new HashSet<>();
            for (String f : UPSTREAM_OBJ_FLAGS[i]) wantObj.add(ObjectFlag.valueOf("OF_" + f));
            Set<ObjectFlag> haveObj = new HashSet<>();
            for (ObjectFlag f : this.<Flag<ObjectFlag>>reflect(r, "oFlags")) haveObj.add(f);
            assertEquals(wantObj, haveObj, who + " obj-flags");

            Set<PlayerFlag> wantPlayer = new HashSet<>();
            for (String f : UPSTREAM_PLAYER_FLAGS[i]) wantPlayer.add(PlayerFlag.valueOf("PF_" + f));
            Set<PlayerFlag> havePlayer = new HashSet<>();
            for (PlayerFlag f : this.<Flag<PlayerFlag>>reflect(r, "pFlags")) havePlayer.add(f);
            assertEquals(wantPlayer, havePlayer, who + " player-flags");

            Map<ElementEnum, ElementInfo> resists = field(r, "resists");
            Set<ElementEnum> wantResists = new HashSet<>();
            for (String e : UPSTREAM_RESISTS[i]) wantResists.add(ElementEnum.valueOf("ELEM_" + e));
            assertEquals(wantResists, resists.keySet(), who + " resists");
            for (ElementInfo info : resists.values()) assertEquals(1, info.getResLevel(), who);
        }
    }

    @Test
    void theRacesComeBackInFileOrderWhereCsListIsNewestFirst() throws IOException {
        // parse_p_race_name pushes each race on the head of C's list, so C's list starts at Kobold;
        // finish_parse_p_race numbers ridx down the list so Human is ridx 0. The port keeps file order.
        List<PlayerRace> races = new PlayerRaceReader().parse(REAL_FILE);

        for (int i = 0; i < UPSTREAM_NAMES.length; i++) assertEquals(UPSTREAM_NAMES[i], races.get(i).getName());
    }

    @Test
    void aMissingFileIsAnIoExceptionWhereCQuits() {
        assertThrows(IOException.class,
                () -> new PlayerRaceReader().parseWithResults(tempDir.resolve("nope.txt").toString()));
    }

    // ---- C behaviour that the port keeps ---------------------------------

    @Test
    void aDirectiveMayComeInAnyOrderAfterTheName() throws Exception {
        // The C handlers are independent, so only name: has to come first.
        PlayerRace r = load("order.txt", withHeader(1, race("Odd",
                "weight:165:35\nhistory:1\nexp:100\nstats:1:2:3:4:5\nhitdie:10\n"))).items().get(0);

        assertEquals(10, (int) field(r, "raceMhp"));
        assertEquals(100, (int) field(r, "raceExp"));
        assertEquals(165, (int) field(r, "baseWeight"));
        Map<Stats, Integer> stats = field(r, "statsAdj");
        assertEquals(3, (int) stats.get(Stats.STAT_WIS));
    }

    @Test
    void aDirectiveNeverGivenLeavesItsFieldAtZeroAsCsZeroedRecordDoes() throws Exception {
        // mem_zalloc in parse_p_race_name zeroes r_adj, r_skills and el_info, so a race with no
        // stats: or skill-*: line reads 0 for every stat and skill, not "no entry".
        PlayerRace r = load("bare.txt", withHeader(1, race("Bare", "history:1\n"))).items().get(0);

        for (String f : new String[]{"raceMhp", "raceExp", "infravision", "baseAge", "modAge", "baseHeight",
                "modHeight", "baseWeight", "modWeight"}) {
            assertEquals(0, (int) field(r, f), f);
        }
        Map<Stats, Integer> stats = field(r, "statsAdj");
        assertEquals(5, stats.size());
        for (Stats stat : stats.keySet()) assertEquals(0, (int) stats.get(stat), stat.name());
        Map<PlayerSkill, Integer> skills = field(r, "skillsAdj");
        assertEquals(10, skills.size());
        for (PlayerSkill skill : skills.keySet()) assertEquals(0, (int) skills.get(skill), skill.name());
        assertEquals(0, r.getStatAdjust(Stats.STAT_CON));
        assertEquals(0, r.getSkill(PlayerSkill.SKILL_DIGGING));
        Flag<ObjectFlag> of = field(r, "oFlags");
        assertTrue(of.isEmpty());
    }

    @Test
    void aSkillLineLeavesTheNineOthersAtZero() throws Exception {
        // Each skill-* directive sets one r_skills slot and touches no other.
        PlayerRace r = load("one-skill.txt", withHeader(1, race("One", "history:1\nskill-dig:7\n"))).items().get(0);

        assertEquals(7, r.getSkill(PlayerSkill.SKILL_DIGGING));
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX
                    || skill == PlayerSkill.SKILL_DIGGING) continue;
            assertEquals(0, r.getSkill(skill), skill.name());
        }
    }

    @Test
    void anElementTheRaceNeverNamesReadsAsNoResistance() throws Exception {
        // el_info is zeroed, so an element with no values: entry has res_level 0.
        PlayerRace r = load("one-res.txt", withHeader(1, race("One", "history:1\nvalues:RES_FIRE[1]\n"))).items().get(0);

        assertEquals(1, r.getResistanceLevel(ElementEnum.ELEM_FIRE));
        assertEquals(0, r.getResistanceLevel(ElementEnum.ELEM_COLD));
        assertFalse(r.getResistKnowledge(ElementEnum.ELEM_COLD));
    }

    @Test
    void aRepeatedScalarDirectiveTakesTheLaterValue() throws Exception {
        PlayerRace r = load("repeat.txt", withHeader(1, race("Twice",
                "history:1\nexp:100\nexp:150\nstats:1:1:1:1:1\nstats:2:3:4:5:6\nage:1:2\nage:7:8\n"))).items().get(0);

        assertEquals(150, (int) field(r, "raceExp"));
        Map<Stats, Integer> stats = field(r, "statsAdj");
        assertEquals(2, (int) stats.get(Stats.STAT_STR));
        assertEquals(6, (int) stats.get(Stats.STAT_CON));
        assertEquals(7, (int) field(r, "baseAge"));
        assertEquals(8, (int) field(r, "modAge"));
    }

    @Test
    void repeatedFlagAndValuesLinesAccumulateAsCsDo() throws Exception {
        PlayerRace r = load("accumulate.txt", withHeader(1, race("Many", "history:1\n"
                + "obj-flags:SUST_STR\nobj-flags:REGEN\n"
                + "player-flags:SEE_ORE\nplayer-flags:KNOW_ZAPPER\n"
                + "values:RES_ACID[1]\nvalues:RES_FIRE[1]\n"))).items().get(0);

        Flag<ObjectFlag> of = field(r, "oFlags");
        assertTrue(of.has(ObjectFlag.OF_SUST_STR) && of.has(ObjectFlag.OF_REGEN));
        Flag<PlayerFlag> pf = field(r, "pFlags");
        assertTrue(pf.has(PlayerFlag.PF_SEE_ORE) && pf.has(PlayerFlag.PF_KNOW_ZAPPER));
        Map<ElementEnum, ElementInfo> resists = field(r, "resists");
        assertEquals(Set.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_FIRE), resists.keySet());
    }

    @Test
    void anEmptyFlagLineLoadsTheRaceWithNoFlagsAsCDoes() throws Exception {
        // parse_p_race_obj_flags and parse_p_race_play_flags return no error when "flags" is absent.
        PlayerRace r = load("empty-flags.txt",
                withHeader(1, race("Plain", "history:1\nobj-flags:\nplayer-flags:\n"))).items().get(0);

        Flag<ObjectFlag> of = field(r, "oFlags");
        Flag<PlayerFlag> pf = field(r, "pFlags");
        assertTrue(of.isEmpty());
        assertTrue(pf.isEmpty());
    }

    @Test
    void aRepeatedResistTakesTheLaterLevelAndANegativeLevelIsKept() throws Exception {
        // grab_index_and_int reads the level with strtol, so [-1] is a level of -1.
        PlayerRace r = load("levels.txt", withHeader(1, race("Levels", "history:1\n"
                + "values:RES_ACID[1] | RES_ACID[2] | RES_FIRE[-1]\n"))).items().get(0);

        Map<ElementEnum, ElementInfo> resists = field(r, "resists");
        assertEquals(2, resists.get(ElementEnum.ELEM_ACID).getResLevel());
        assertEquals(-1, resists.get(ElementEnum.ELEM_FIRE).getResLevel());
    }

    @Test
    void aNameKeepsItsColonsAndSpacesAsCsStrFieldDoes() throws IOException {
        ParseResult<PlayerRace> result = load("colon-name.txt", withHeader(1, race("Half Elf: the second", "history:1\n")));

        assertEquals("Half Elf: the second", result.items().get(0).getName());
    }

    @Test
    void aLowerCaseFlagIsRejectedAsCRejectsIt() throws IOException {
        ParseResult<PlayerRace> result = load("lower-flag.txt",
                withHeader(1, race("Low", "history:1\nobj-flags:sust_str\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aStatsLineWithFewerThanFiveFieldsIsRejectedAsCRejectsIt() throws IOException {
        ParseResult<PlayerRace> result = load("short-stats.txt",
                withHeader(1, race("Short", "history:1\nstats:1:2:3:4\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aNegativeHistoryChartIsRejectedAsCRejectsAUint() {
        // "history uint hist": parser_parse ends the load when the token's first character is '-'.
        DataFileParseException e = rejects("neg-history.txt", withHeader(1, race("Neg", "history:-1\n")));

        assertTrue(e.getMessage().contains("negative player history value: -1"), e::getMessage);
    }

    @Test
    void aNegativeZeroHistoryChartIsRejectedToo() {
        // The test is on the first character, not the value, so -0 is refused although it is 0.
        DataFileParseException e = rejects("neg-zero-history.txt", withHeader(1, race("Neg", "history:-0\n")));

        assertTrue(e.getMessage().contains("negative player history value: -0"), e::getMessage);
    }

    @Test
    void aPlayerFlagMaxIsRejectedAsCRejectsIt() {
        // PlayerFlag has no PF_MAX, so MAX fails as an unknown name.
        DataFileParseException e = rejects("pf-max.txt", withHeader(1, race("Max", "history:1\nplayer-flags:MAX\n")));

        assertTrue(e.getMessage().contains("illegal player flag value: MAX"), e::getMessage);
    }

    // ---- Differences from C, pinned so that a change to either side shows up ----

    @Test
    void aNameOnlyRaceIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        ParseResult<PlayerRace> result = load("name-only.txt", withHeader(1, "name:Empty\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aHeaderWithNoRacesIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        ParseResult<PlayerRace> result = load("no-races.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void objectFlagsSeparatedByASpaceAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        // C's strtok(flags, " |") reads "SUST_STR REGEN" as two flags.
        ParseResult<PlayerRace> result = load("of-space.txt",
                withHeader(1, race("Sp", "history:1\nobj-flags:SUST_STR REGEN\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void playerFlagsSeparatedByASpaceAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        ParseResult<PlayerRace> result = load("pf-space.txt",
                withHeader(1, race("Sp", "history:1\nplayer-flags:SEE_ORE KNOW_ZAPPER\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void valuesSeparatedByASpaceAreAHardErrorHereWhereCSplitsOnSpaceToo() throws IOException {
        ParseResult<PlayerRace> result = load("val-space.txt",
                withHeader(1, race("Sp", "history:1\nvalues:RES_ACID[1] RES_FIRE[1]\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void textAfterANumberIsAHardErrorHereWhereCIgnoresIt() throws IOException {
        ParseResult<PlayerRace> result = load("trailing.txt",
                withHeader(1, race("Tr", "history:1\nhitdie:10abc\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aHexNumberIsAHardErrorHereWhereCReadsItInBaseSixteen() throws IOException {
        ParseResult<PlayerRace> result = load("hex.txt",
                withHeader(1, race("Hx", "history:1\nhitdie:0x10\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void anIndentedLineIsAHardErrorHereWhereCSkipsLeadingWhitespace() throws IOException {
        ParseResult<PlayerRace> result = load("indent.txt",
                withHeader(1, race("In", "history:1\n  hitdie:10\n")));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Behaviour now matching C ------------------------------------------

    @Test
    void aRaceWithNoHistoryLineLoadsWithANullHistoryAsCDoes() throws Exception {
        // parse_p_race_history is the only thing that sets history, so C leaves it NULL.
        ParseResult<PlayerRace> result = load("no-history.txt", withHeader(1, race("NoHist", "hitdie:10\n")));

        assertEquals(1, result.items().size(), result.errors()::toString);
        assertNull(field(result.items().get(0), "history"));
        assertEquals(10, (int) field(result.items().get(0), "raceMhp"));
    }

    @Test
    void historyChartZeroLoadsWithANullHistoryBecauseNoChartIsNumberedZero() throws Exception {
        // "history:0" is a valid uint and findchart finds nothing numbered 0 in the shipped charts.
        ParseResult<PlayerRace> result = load("history-zero.txt", withHeader(1, race("Zero", "history:0\n")));

        assertEquals(1, result.items().size(), result.errors()::toString);
        assertNull(field(result.items().get(0), "history"));
    }

    @Test
    void objectFlagNoneIsRejectedAsCRejectsIt() {
        // lookup_flag starts at FLAG_START, so the "NONE" entry at index 0 can never be found.
        DataFileParseException e = rejects("of-none.txt", withHeader(1, race("N", "history:1\nobj-flags:NONE\n")));

        assertTrue(e.getMessage().contains("illegal sentinel object flag value: NONE"), e::getMessage);
    }

    @Test
    void objectFlagMaxIsRejectedAsCRejectsIt() {
        // The object-flag table has no MAX entry, so the name matches nothing.
        DataFileParseException e = rejects("of-max.txt", withHeader(1, race("M", "history:1\nobj-flags:MAX\n")));

        assertTrue(e.getMessage().contains("illegal sentinel object flag value: MAX"), e::getMessage);
    }

    @Test
    void playerFlagNoneIsRejectedAsCRejectsIt() {
        DataFileParseException e = rejects("pf-none.txt", withHeader(1, race("N", "history:1\nplayer-flags:NONE\n")));

        assertTrue(e.getMessage().contains("illegal sentinel player flag value: NONE"), e::getMessage);
    }

    @Test
    void aSentinelAfterARealFlagIsStillRejected() {
        // strtok walks every token and stops at the first grab_flag failure, wherever it falls.
        DataFileParseException e = rejects("of-late-none.txt",
                withHeader(1, race("L", "history:1\nobj-flags:SUST_STR | NONE\n")));

        assertTrue(e.getMessage().contains("NONE"), e::getMessage);
    }

    @Test
    void resistNoneAndMaxAreRejectedAsCRejectsThem() {
        // list_element_names has no NONE or MAX, so grab_index_and_int fails for both.
        DataFileParseException none = rejects("res-none.txt",
                withHeader(1, race("N", "history:1\nvalues:RES_NONE[1]\n")));
        DataFileParseException max = rejects("res-max.txt",
                withHeader(1, race("M", "history:1\nvalues:RES_MAX[1]\n")));

        assertTrue(none.getMessage().contains("illegal sentinel element value: NONE"), none::getMessage);
        assertTrue(max.getMessage().contains("illegal sentinel element value: MAX"), max::getMessage);
    }

    @Test
    void everyRaceIsNumberedFromZeroInFileOrderAsFinishParsePRaceDoes() throws Exception {
        // finish_parse_p_race gives the first race in the file ridx 0 and the last count - 1.
        List<PlayerRace> races = new PlayerRaceReader().parse(REAL_FILE);

        for (int i = 0; i < races.size(); i++) {
            assertEquals(i, (int) field(races.get(i), "rIndex"), races.get(i).getName());
        }
    }

    @Test
    void theIndexFollowsFilePositionNotTheNameOrAnythingElse() throws Exception {
        ParseResult<PlayerRace> result = load("index.txt", withHeader(3,
                race("Zed", "history:1\n") + race("Alpha", "history:1\n") + race("Mid", "history:1\n")));

        assertEquals(0, (int) field(byName(result.items(), "Zed"), "rIndex"));
        assertEquals(1, (int) field(byName(result.items(), "Alpha"), "rIndex"));
        assertEquals(2, (int) field(byName(result.items(), "Mid"), "rIndex"));
    }

    /**
     * Convenience wrapper so a reflected flag field reads inline in the flag tests.
     */
    private <T> T reflect(PlayerRace r, String name) throws Exception {
        return field(r, name);
    }
}
