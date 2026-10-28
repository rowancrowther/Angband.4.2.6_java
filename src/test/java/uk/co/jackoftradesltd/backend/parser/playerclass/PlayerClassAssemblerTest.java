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

package uk.co.jackoftradesltd.backend.parser.playerclass;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerClassAssembler} against the {@code parse_class_*} handlers in {@code init.c}
 * that fill a {@code struct player_class}, and against {@code finish_parse_class} for the order the
 * classes come out in.
 *
 * <p>The records are built by hand, so the tests reach what a data file cannot: the grammar
 * hard-codes the stat and skill key names and the lexer only produces digits, so the unknown-name
 * guards are unreachable from {@code class.txt}. {@code PlayerClassReaderTest} drives the same
 * assembler from file text.
 *
 * <p>What C does, which the expected values below come from:
 * <ul>
 *   <li>{@code parse_class_stats} and the nine {@code parse_class_skill_*} handlers store signed
 *       {@code int}s read by {@code parser_getint} ({@code strtol}, base 0), so a negative stat is
 *       valid. {@code skill-melee}, {@code -shoot}, {@code -throw} and {@code -dig} fill
 *       {@code SKILL_TO_HIT_MELEE}, {@code _BOW}, {@code _THROW} and {@code SKILL_DIGGING}; the
 *       first number of a skill line is {@code c_skills[]} and the second {@code x_skills[]}.</li>
 *   <li>The class is zero-filled, so a stat, skill or scalar whose line is absent reads 0; a
 *       repeated line overwrites.</li>
 *   <li>{@code parse_class_title} fills a ten-entry array and returns
 *       {@code PARSE_ERROR_TOO_MANY_ENTRIES} on the eleventh.</li>
 *   <li>{@code parse_class_obj_flags} and {@code parse_class_play_flags} look each name up with
 *       {@code lookup_flag}, which compares with {@code streq} (case-sensitive) and starts at table
 *       index {@code FLAG_START}, 1, so the {@code NONE} entry at index 0 of both tables never
 *       matches. A name not in the table is {@code PARSE_ERROR_INVALID_FLAG}. Repeated lines add
 *       to the set.</li>
 *   <li>Every one of those errors stops the load, so the assembler throws a
 *       {@link DataFileParseException} and the tests below expect that.</li>
 * </ul>
 *
 * <p>One test is marked ACCEPTED DEVIATION: it pins the clamping of a figure past
 * {@code Integer.MAX_VALUE}, where C wraps it, so a change to it fails loudly.
 *
 * <p>Class PlayerClassAssemblerTest coded on 261028, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
class PlayerClassAssemblerTest {

    /** The line every record in these tests claims its {@code name:} is on. */
    private static final int LINE = 7;

    /** The file name every message is expected to carry. */
    private static final String FILE = "class.txt";

    private static Object[] saved;

    @BeforeAll
    static void saveRegistries() throws Exception {
        saved = BookRegistryFixture.save();
    }

    @AfterAll
    static void restoreRegistries() throws Exception {
        BookRegistryFixture.restore(saved);
    }

    @BeforeEach
    void seed() throws Exception {
        BookRegistryFixture.seed();
    }

    // ---- builders ------------------------------------------------------------------------------

    /** A record whose every optional part is absent: the shape of a class with only a name. */
    private static PlayerClassParseRecord bare(String name) {
        return new PlayerClassParseRecord(name, new HashMap<>(), new HashMap<>(), new HashMap<>(),
                "", "", "", "", "", List.of(), List.of(), List.of(), List.of(), null, LINE);
    }

    private static Map<String, String> allStats(String str, String intel, String wis, String dex, String con) {
        Map<String, String> stats = new LinkedHashMap<>();
        stats.put("STAT_STR", str);
        stats.put("STAT_INT", intel);
        stats.put("STAT_WIS", wis);
        stats.put("STAT_DEX", dex);
        stats.put("STAT_CON", con);
        return stats;
    }

    private static PlayerClassParseRecord withStats(Map<String, String> stats) {
        return new PlayerClassParseRecord("Test", stats, new HashMap<>(), new HashMap<>(),
                "", "", "", "", "", List.of(), List.of(), List.of(), List.of(), null, LINE);
    }

    private static PlayerClassParseRecord withSkills(Map<String, String> base, Map<String, String> incr) {
        return new PlayerClassParseRecord("Test", new HashMap<>(), base, incr,
                "", "", "", "", "", List.of(), List.of(), List.of(), List.of(), null, LINE);
    }

    /** The five scalars in the order the record declares them. */
    private static PlayerClassParseRecord withScalars(String hitdie, String exp, String maxAttacks,
                                                      String minWeight, String strMult) {
        return new PlayerClassParseRecord("Test", new HashMap<>(), new HashMap<>(), new HashMap<>(),
                hitdie, exp, maxAttacks, minWeight, strMult, List.of(), List.of(), List.of(), List.of(),
                null, LINE);
    }

    private static PlayerClassParseRecord withOFlags(String... flags) {
        return new PlayerClassParseRecord("Test", new HashMap<>(), new HashMap<>(), new HashMap<>(),
                "", "", "", "", "", List.of(), List.of(flags), List.of(), List.of(), null, LINE);
    }

    private static PlayerClassParseRecord withPFlags(String... flags) {
        return new PlayerClassParseRecord("Test", new HashMap<>(), new HashMap<>(), new HashMap<>(),
                "", "", "", "", "", List.of(), List.of(), List.of(flags), List.of(), null, LINE);
    }

    private static PlayerClassParseRecord withTitles(int count) {
        List<String> titles = new ArrayList<>();
        for (int i = 1; i <= count; i++)
            titles.add("Title" + i);
        return new PlayerClassParseRecord("Test", new HashMap<>(), new HashMap<>(), new HashMap<>(),
                "", "", "", "", "", List.of(), List.of(), List.of(), titles, null, LINE);
    }

    private static List<PlayerClass> run(List<String> errors, PlayerClassParseRecord... records) {
        return new PlayerClassAssembler().assemble(List.of(records), errors, FILE);
    }

    private static PlayerClass one(PlayerClassParseRecord record) {
        List<String> errors = new ArrayList<>();
        List<PlayerClass> result = run(errors, record);
        assertEquals(1, result.size());
        assertTrue(errors.isEmpty(), errors::toString);
        return result.getFirst();
    }

    private static DataFileParseException stops(PlayerClassParseRecord record) {
        return assertThrows(DataFileParseException.class, () -> run(new ArrayList<>(), record));
    }

    private static void assertNames(DataFileParseException e, String... parts) {
        for (String part : parts)
            assertTrue(e.getMessage().contains(part), () -> "expected \"" + part + "\" in: " + e.getMessage());
    }

    // ---- the ordinary path ---------------------------------------------------------------------

    @Test
    @DisplayName("a full set of lines lands in the matching fields, as the parse_class_* handlers store them")
    void ordinaryClass() {
        Map<String, String> base = new HashMap<>();
        base.put("SKILL_DISARM_PHYS", "30");
        base.put("SKILL_SAVE", "18");
        Map<String, String> incr = new HashMap<>();
        incr.put("SKILL_DISARM_PHYS", "10");
        incr.put("SKILL_SAVE", "9");

        PlayerClassParseRecord record = new PlayerClassParseRecord("Warrior",
                allStats("3", "-2", "-2", "2", "2"), base, incr,
                "9", "0", "6", "30", "5", List.of(), List.of("SUST_STR", "PROT_FEAR"),
                List.of("BRAVERY_30", "SHIELD_BASH"), List.of("Rookie", "Soldier"), null, LINE);

        PlayerClass c = one(record);

        assertEquals("Warrior", c.getName());
        assertEquals(3, c.getStatsAdj(Stats.STAT_STR));
        assertEquals(-2, c.getStatsAdj(Stats.STAT_INT));
        assertEquals(-2, c.getStatsAdj(Stats.STAT_WIS));
        assertEquals(2, c.getStatsAdj(Stats.STAT_DEX));
        assertEquals(2, c.getStatsAdj(Stats.STAT_CON));
        assertEquals(30, c.getSkill(PlayerSkill.SKILL_DISARM_PHYS));
        assertEquals(10, c.getXSkill(PlayerSkill.SKILL_DISARM_PHYS));
        assertEquals(18, c.getSkill(PlayerSkill.SKILL_SAVE));
        assertEquals(9, c.getXSkill(PlayerSkill.SKILL_SAVE));
        assertEquals(9, c.getMaxHitDie());
        assertEquals(0, c.getExpFactor());
        assertEquals(6, c.getMaxAttacks());
        assertEquals(30, c.getMinWeight());
        assertEquals(5, c.getAttMultiply());
        assertTrue(c.getoFlags().has(ObjectFlag.OF_SUST_STR));
        assertTrue(c.getoFlags().has(ObjectFlag.OF_PROT_FEAR));
        assertEquals(2, c.getoFlags().count());
        assertTrue(c.getpFlags().has(PlayerFlag.PF_BRAVERY_30));
        assertTrue(c.getpFlags().has(PlayerFlag.PF_SHIELD_BASH));
        assertEquals(2, c.getpFlags().count());
        assertEquals(2, c.getNoTitles());
        assertEquals("Rookie", c.getTitle(1));
        assertEquals("Soldier", c.getTitle(6));
    }

    @Test
    @DisplayName("negative stats and skills are valid: C reads them with parser_getint, a signed int")
    void negativeNumbersAreAccepted() {
        PlayerClass c = one(withStats(allStats("-5", "-1", "0", "1", "-3")));

        assertEquals(-5, c.getStatsAdj(Stats.STAT_STR));
        assertEquals(-3, c.getStatsAdj(Stats.STAT_CON));

        Map<String, String> base = Map.of("SKILL_STEALTH", "-4");
        Map<String, String> incr = Map.of("SKILL_STEALTH", "-1");
        PlayerClass s = one(withSkills(base, incr));
        assertEquals(-4, s.getSkill(PlayerSkill.SKILL_STEALTH));
        assertEquals(-1, s.getXSkill(PlayerSkill.SKILL_STEALTH));

        PlayerClass k = one(withScalars("-1", "-2", "-3", "-4", "-5"));
        assertEquals(-1, k.getMaxHitDie());
        assertEquals(-2, k.getExpFactor());
        assertEquals(-3, k.getMaxAttacks());
        assertEquals(-4, k.getMinWeight());
        assertEquals(-5, k.getAttMultiply());
    }

    @Test
    @DisplayName("every skill line fills its own skill, base into c_skills and increment into x_skills")
    void everySkillKeyResolves() {
        // The record's keys are the SKILL_* names the grammar gives each skill-* line:
        // skill-melee -> TO_HIT_MELEE, skill-shoot -> TO_HIT_BOW, skill-throw -> TO_HIT_THROW,
        // skill-dig -> DIGGING.
        PlayerSkill[] skills = {PlayerSkill.SKILL_DISARM_PHYS, PlayerSkill.SKILL_DISARM_MAGIC,
                PlayerSkill.SKILL_DEVICE, PlayerSkill.SKILL_SAVE, PlayerSkill.SKILL_STEALTH,
                PlayerSkill.SKILL_SEARCH, PlayerSkill.SKILL_TO_HIT_MELEE, PlayerSkill.SKILL_TO_HIT_BOW,
                PlayerSkill.SKILL_TO_HIT_THROW, PlayerSkill.SKILL_DIGGING};
        Map<String, String> base = new HashMap<>();
        Map<String, String> incr = new HashMap<>();
        for (int i = 0; i < skills.length; i++) {
            base.put(skills[i].name(), String.valueOf(10 + i));
            incr.put(skills[i].name(), String.valueOf(100 + i));
        }

        PlayerClass c = one(withSkills(base, incr));

        for (int i = 0; i < skills.length; i++) {
            assertEquals(10 + i, c.getSkill(skills[i]), skills[i]::name);
            assertEquals(100 + i, c.getXSkill(skills[i]), skills[i]::name);
        }
    }

    @Test
    @DisplayName("a class with only a name is zero-filled: every stat, skill and scalar reads 0, no title, no flags")
    void aNameOnlyRecordIsZeroFilled() {
        PlayerClass c = one(bare("Empty"));

        for (Stats stat : new Stats[]{Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON})
            assertEquals(0, c.getStatsAdj(stat), stat::name);
        for (PlayerSkill skill : PlayerSkill.values()) {
            assertEquals(0, c.getSkill(skill), skill::name);
            assertEquals(0, c.getXSkill(skill), skill::name);
        }
        assertEquals(0, c.getMaxHitDie());
        assertEquals(0, c.getExpFactor());
        assertEquals(0, c.getMaxAttacks());
        assertEquals(0, c.getMinWeight());
        assertEquals(0, c.getAttMultiply());
        assertTrue(c.getoFlags().isEmpty());
        assertTrue(c.getpFlags().isEmpty());
        assertEquals(0, c.getNoTitles());
        assertTrue(c.getStartItems().isEmpty());
    }

    @Test
    @DisplayName("numbers are read as strtol with base 0 reads them: 010 is octal 8, 0x10 is 16")
    void baseZeroReading() {
        PlayerClass c = one(withScalars("010", "0x10", "08", "+7", "0"));

        assertEquals(8, c.getMaxHitDie());
        assertEquals(16, c.getExpFactor());
        assertEquals(0, c.getMaxAttacks());
        assertEquals(7, c.getMinWeight());
        assertEquals(0, c.getAttMultiply());
    }

    @Test
    @DisplayName("records come back in file order, which is C's cidx order")
    void classesKeepFileOrder() {
        List<PlayerClass> result = run(new ArrayList<>(), bare("First"), bare("Second"), bare("Third"));

        assertEquals(List.of("First", "Second", "Third"),
                result.stream().map(PlayerClass::getName).toList());
    }

    @Test
    @DisplayName("an empty record list gives an empty result")
    void noRecords() {
        assertTrue(run(new ArrayList<>()).isEmpty());
    }

    @Test
    @DisplayName("a non-caster stores ClassMagic.NONE, never null")
    void aNonCasterHasNoMagic() {
        PlayerClass c = one(bare("Warrior"));

        assertSame(ClassMagic.NONE, c.getMagic());
        assertFalse(c.getMagic().isCaster());
    }

    @Test
    @DisplayName("a magic record is assembled by ClassMagicAssembler into the class's ClassMagic")
    void aCasterKeepsItsMagic() {
        ClassSpellBookParseRecord book = new ClassSpellBookParseRecord("magic book", "town", "[Magic for Beginners]",
                "1", "arcane", "", "", "", "", "", "", List.of(), LINE + 3);
        ClassMagicParseRecord magic = new ClassMagicParseRecord("1", "300", "1", List.of(book), LINE + 2);
        PlayerClassParseRecord record = new PlayerClassParseRecord("Mage", new HashMap<>(), new HashMap<>(),
                new HashMap<>(), "0", "", "", "", "", List.of(), List.of(), List.of(), List.of(), magic, LINE);

        PlayerClass c = one(record);

        assertTrue(c.getMagic().isCaster());
        assertEquals(1, c.getMagic().getSpellFirst());
        assertEquals(300, c.getMagic().getSpellWeight());
        assertEquals(1, c.getMagic().getNumBooks());
    }

    // ---- repeated lines ------------------------------------------------------------------------

    @Test
    @DisplayName("object flags are a set: a name given twice sets one flag")
    void aRepeatedFlagIsOneFlag() {
        PlayerClass c = one(withOFlags("SUST_STR", "SUST_STR", "PROT_FEAR"));

        assertEquals(2, c.getoFlags().count());
    }

    // ---- flags ---------------------------------------------------------------------------------

    @ParameterizedTest(name = "obj-flags:{0} is refused")
    @ValueSource(strings = {"NOTAFLAG", "NONE", "MAX", "OF_SUST_STR", "sust_str", "Sust_Str", " SUST_STR", ""})
    void anUnknownObjectFlagStopsTheLoad(String flag) {
        // NONE: C's table holds "NONE" at index 0 and lookup_flag starts at index 1, so it never
        // matches. MAX is not in the table at all. OF_SUST_STR would be looked up as written, and
        // streq is case-sensitive.
        DataFileParseException e = stops(withOFlags(flag));

        assertNames(e, "Player class starting at line: " + LINE + " of file: " + FILE, "object flag value: OF_" + flag);
    }

    @ParameterizedTest(name = "player-flags:{0} is refused")
    @ValueSource(strings = {"NOTAFLAG", "NONE", "MAX", "PF_ZERO_FAIL", "zero_fail", "Zero_Fail", " ZERO_FAIL", ""})
    void anUnknownPlayerFlagStopsTheLoad(String flag) {
        DataFileParseException e = stops(withPFlags(flag));

        // NONE is refused as a sentinel (PF_NONE) and the rest as unknown, so the tails differ; both
        // end in the text as written after the PF_ prefix, or the bare text for an unknown name.
        assertNames(e, "Player class starting at line: " + LINE + " of file: " + FILE, "player flag value: ");
        assertTrue(e.getMessage().endsWith(flag) || e.getMessage().endsWith("PF_" + flag), e::getMessage);
    }

    @Test
    @DisplayName("a bad flag after good ones still stops the load: nothing is returned for the class")
    void aBadFlagAfterGoodOnesStillStops() {
        assertNames(stops(withOFlags("SUST_STR", "NOTAFLAG")), "OF_NOTAFLAG");
        assertNames(stops(withPFlags("ZERO_FAIL", "NOTAFLAG")), "NOTAFLAG");
    }

    @Test
    @DisplayName("every player flag in list-player-flags.h bar NONE resolves")
    void everyRealPlayerFlagResolves() {
        for (PlayerFlag flag : PlayerFlag.values()) {
            if (flag == PlayerFlag.PF_NONE)
                continue;
            String bare = flag.name().substring("PF_".length());
            assertTrue(one(withPFlags(bare)).getpFlags().has(flag), flag::name);
        }
    }

    @Test
    @DisplayName("every object flag in list-object-flags.h resolves")
    void everyRealObjectFlagResolves() {
        for (ObjectFlag flag : ObjectFlag.values()) {
            if (flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX)
                continue;
            String bare = flag.name().substring("OF_".length());
            assertTrue(one(withOFlags(bare)).getoFlags().has(flag), flag::name);
        }
    }

    // ---- titles --------------------------------------------------------------------------------

    @ParameterizedTest(name = "{0} titles load")
    @ValueSource(ints = {0, 1, 5, 9, 10})
    void upToTenTitlesLoad(int count) {
        PlayerClass c = one(withTitles(count));

        assertEquals(count, c.getNoTitles());
    }

    @Test
    @DisplayName("an eleventh title is PARSE_ERROR_TOO_MANY_ENTRIES in C, so the load stops")
    void anEleventhTitleStopsTheLoad() {
        assertNames(stops(withTitles(11)), "Player class starting at line: " + LINE + " of file: " + FILE,
                "illegal number of titles: 11");
        assertNames(stops(withTitles(12)), "illegal number of titles: 12");
    }

    @Test
    @DisplayName("titles keep file order")
    void titlesKeepFileOrder() {
        PlayerClass c = one(withTitles(10));

        for (int i = 0; i < 10; i++)
            assertEquals("Title" + (i + 1), c.getTitle(i * 5 + 1), "level " + (i * 5 + 1));
    }

    // ---- bad names and bad numbers -------------------------------------------------------------

    @Test
    @DisplayName("an unknown stat name stops the load (unreachable from a file: the grammar hard-codes the keys)")
    void anUnknownStatNameStopsTheLoad() {
        Map<String, String> stats = allStats("1", "1", "1", "1", "1");
        stats.put("STAT_LUCK", "1");

        assertNames(stops(withStats(stats)), "Player class starting at line: " + LINE + " of file: " + FILE,
                "illegal stat name value: STAT_LUCK");
    }

    @Test
    @DisplayName("an unknown base skill name stops the load")
    void anUnknownBaseSkillNameStopsTheLoad() {
        assertNames(stops(withSkills(Map.of("SKILL_LUCK", "1"), Map.of())),
                "illegal base skill name value: SKILL_LUCK");
    }

    @Test
    @DisplayName("an unknown skill increment name stops the load")
    void anUnknownSkillIncrementNameStopsTheLoad() {
        assertNames(stops(withSkills(Map.of(), Map.of("SKILL_LUCK", "1"))),
                "illegal skill name value: SKILL_LUCK");
    }

    @Test
    @DisplayName("text with no digit in a stat, skill or scalar is PARSE_ERROR_NOT_NUMBER in C, so the load stops")
    void aNumberWithNoDigitStopsTheLoad() {
        assertNames(stops(withStats(allStats("abc", "1", "1", "1", "1"))), "stat: STAT_STR", "abc");
        assertNames(stops(withSkills(Map.of("SKILL_SAVE", "x"), Map.of())), "skill: SKILL_SAVE", "x");
        assertNames(stops(withSkills(Map.of(), Map.of("SKILL_SAVE", "x"))), "skill: SKILL_SAVE", "x");
        assertNames(stops(withScalars("-", "", "", "", "")), "hit die");
        assertNames(stops(withScalars("", "q", "", "", "")), "experience adjustment");
        assertNames(stops(withScalars("", "", "q", "", "")), "maximum attacks");
        assertNames(stops(withScalars("", "", "", "q", "")), "minimum weight");
        assertNames(stops(withScalars("", "", "", "", "q")), "strength multiplier");
    }

    // ---- stopping the load ---------------------------------------------------------------------

    @Test
    @DisplayName("a bad class stops the load: nothing after it is read and no earlier class is returned")
    void aBadClassStopsTheWholeLoad() {
        PlayerClassParseRecord bad = withOFlags("NOTAFLAG");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> run(new ArrayList<>(), bare("Good"), bad, bare("NeverReached")));

        assertNames(e, "OF_NOTAFLAG");
    }

    @Test
    @DisplayName("the errors list is not written to when every record is good")
    void theErrorsListStaysEmpty() {
        List<String> errors = new ArrayList<>();

        run(errors, bare("A"), withTitles(3));

        assertTrue(errors.isEmpty(), errors::toString);
    }

    // ---- ACCEPTED DEVIATION --------------------------------------------------------------------

    @Test
    @DisplayName("ACCEPTED DEVIATION: a figure past Integer.MAX_VALUE is clamped, where C wraps it into 32 bits")
    void aFigurePastIntMaxIsClamped() {
        // C: strtol gives the 64-bit 2147483648 and the int field keeps the low 32 bits, -2147483648.
        // docs/Accepted_Deviations.md: Java clamps instead.
        PlayerClass c = one(withScalars("2147483648", "", "", "", ""));

        assertEquals(Integer.MAX_VALUE, c.getMaxHitDie());
    }
}
