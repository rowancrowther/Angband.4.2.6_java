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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.pit.PitAssembler;
import uk.co.jackoftradesltd.backend.parser.pit.PitParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.PitProfile;
import uk.co.jackoftradesltd.middle.cave.enums.PitRoomType;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PitAssembler}.
 *
 * <p>These build {@link PitParseRecord}s directly, with no grammar, so text the lexer would never
 * produce (a lower-case flag, a colour of several words, an empty number) still reaches the
 * assembler, and the monster registries are seeded by hand instead of by the whole init chain.
 * End-to-end cases against the shipped file live in {@code PitReaderTest}.
 *
 * <p>The expected values are worked out from {@code mon-init.c}, {@code parser.c},
 * {@code datafile.c}, {@code mon-util.c} and {@code ui-color.c}, not from the Java:
 * <ul>
 *   <li>{@code parse_pit_room}, {@code parse_pit_alloc} and {@code parse_pit_obj_rarity} read
 *       {@code uint} fields, and {@code parser_parse} reads a {@code uint} with {@code strtoul} in
 *       base 0 and refuses a token whose first character is {@code -} (even {@code -0}); the
 *       {@code alloc} fields are rarity first, level second, and the level is {@code ave};</li>
 *   <li>{@code parse_pit_innate_freq} reads an {@code int}, refuses a percentage below 1 or above
 *       100 ({@code PARSE_ERROR_INVALID_SPELL_FREQ}) and stores {@code 100 / pct} as integer
 *       division;</li>
 *   <li>{@code grab_flag} goes through {@code lookup_flag}, which starts at index 1 and compares
 *       with {@code streq}: {@code NONE} (index 0) and any lower-case code are refused. The
 *       race-flag table has no {@code MAX} row; the spell table ends in one, so {@code MAX} is a
 *       valid spell;</li>
 *   <li>{@code parse_pit_color} sends one character to {@code color_char_to_attr} (case-sensitive,
 *       white if unknown, black for a space) and more to {@code color_text_to_attr}
 *       ({@code my_stricmp}, white if unknown), so a bad colour is never an error;</li>
 *   <li>{@code parse_pit_mon_base} refuses a name {@code lookup_monster_base} cannot find with
 *       {@code streq}; {@code parse_pit_mon_ban} stores whatever {@code lookup_monster} returns,
 *       a {@code NULL} race included, and {@code lookup_monster} returns the first exact match
 *       ignoring case, else the first race whose name contains the text;</li>
 *   <li>a directive a record leaves out keeps the zero {@code mem_zalloc} gave it;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 * One accepted deviation is pinned as well: a number outside the {@code int} range is refused where
 * C keeps the low 32 bits (decided 261027).
 *
 * <p>Test class PitAssemblerTest coded on 261028, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
class PitAssemblerTest {

    private static final String FILE = "pit.txt";

    /** The line the records below sit on, so a message can be checked for it. */
    private static final int LINE = 7;

    private List<MonsterBase> savedBases;
    private List<MonsterRace> savedRaces;

    /**
     * Puts two bases and four races into the registries the assembler looks names up in, and keeps
     * what was there to put back. The races are listed with a prefix match ahead of an exact one
     * ({@code Grip, Farmer Maggot's Dog} before {@code Grip}) so the two lookup rules can be told
     * apart.
     */
    @BeforeEach
    void seedRegistries() throws Exception {
        Field basesField = RegistrySeeding.resolve("monsterBases");
        basesField.setAccessible(true);
        savedBases = castList(basesField.get(null));
        savedRaces = MonsterRegistry.monsterRaces;

        MonsterRegistry.setMonsterBases(List.of(new MonsterBase("orc"), new MonsterBase("troll")));
        MonsterRegistry.monsterRaces = List.of(race("<player>"), race("Grip, Farmer Maggot's Dog"),
                race("Grip"), race("Horned Reaper"));
    }

    @AfterEach
    void restoreRegistries() {
        MonsterRegistry.setMonsterBases(savedBases);
        MonsterRegistry.monsterRaces = savedRaces;
    }

    @SuppressWarnings("unchecked")
    private static List<MonsterBase> castList(Object o) {
        return (List<MonsterBase>) o;
    }

    private static MonsterRace race(String name) throws Exception {
        MonsterRace race = new MonsterRace();
        Field f = MonsterRace.class.getDeclaredField("name");
        f.setAccessible(true);
        f.set(race, name);
        return race;
    }

    /**
     * Builds a {@link PitParseRecord} with every field blank, as {@code pitRecord}'s {@code @init}
     * leaves it, and lets one test set just the fields it is about.
     */
    private static final class Rec {
        String name = "Test pit";
        String room = "";
        String rarity = "";
        String depth = "";
        String objRarity = "";
        List<String> flags = List.of();
        List<String> bannedFlags = List.of();
        String innate = "";
        List<String> spells = List.of();
        List<String> bannedSpells = List.of();
        List<String> colours = List.of();
        List<String> bases = List.of();
        List<String> bannedMonsters = List.of();
        int line = LINE;

        Rec name(String v) { name = v; return this; }
        Rec room(String v) { room = v; return this; }
        Rec rarity(String v) { rarity = v; return this; }
        Rec depth(String v) { depth = v; return this; }
        Rec objRarity(String v) { objRarity = v; return this; }
        Rec flags(String... v) { flags = List.of(v); return this; }
        Rec bannedFlags(String... v) { bannedFlags = List.of(v); return this; }
        Rec innate(String v) { innate = v; return this; }
        Rec spells(String... v) { spells = List.of(v); return this; }
        Rec bannedSpells(String... v) { bannedSpells = List.of(v); return this; }
        Rec colours(String... v) { colours = List.of(v); return this; }
        Rec bases(String... v) { bases = List.of(v); return this; }
        Rec bannedMonsters(String... v) { bannedMonsters = List.of(v); return this; }
        Rec line(int v) { line = v; return this; }

        PitParseRecord build() {
            return new PitParseRecord(name, room, rarity, depth, objRarity, flags, bannedFlags, innate,
                    spells, bannedSpells, colours, bases, bannedMonsters, line);
        }
    }

    private static List<PitProfile> assemble(Rec... records) {
        List<PitParseRecord> built = new ArrayList<>();
        for (Rec r : records) {
            built.add(r.build());
        }
        return new PitAssembler().assemble(built, new ArrayList<>(), FILE);
    }

    private static PitProfile one(Rec record) {
        return assemble(record).getFirst();
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(PitProfile target, String name) throws Exception {
        Field f = PitProfile.class.getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    private static int intField(PitProfile target, String name) throws Exception {
        return field(target, name);
    }

    private static int raceFlagCount(PitProfile target, String name) throws Exception {
        Flag<MonsterRaceFlag> flags = field(target, name);
        return flags.count();
    }

    private static int spellCount(PitProfile target, String name) throws Exception {
        Flag<MonsterSpell> flags = field(target, name);
        return flags.count();
    }

    private static boolean hasRaceFlag(PitProfile target, String name, MonsterRaceFlag flag) throws Exception {
        Flag<MonsterRaceFlag> flags = field(target, name);
        return flags.has(flag);
    }

    private static boolean hasSpell(PitProfile target, String name, MonsterSpell spell) throws Exception {
        Flag<MonsterSpell> flags = field(target, name);
        return flags.has(spell);
    }

    private static int listSize(PitProfile target, String name) throws Exception {
        List<?> list = field(target, name);
        return list.size();
    }

    /**
     * Runs the assembler on the records and expects the load to stop with the
     * {@link DataFileParseException} C's first parser error would be. Also checks the message's
     * prefix: the record type, its line and the file.
     */
    private static DataFileParseException stopsTheLoad(Rec... records) {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> assemble(records));
        assertTrue(e.getMessage().startsWith("Pit record starting at line: "), e.getMessage());
        assertTrue(e.getMessage().contains(" of file: " + FILE + " has "), e.getMessage());
        return e;
    }

    // ---- The ordinary path ------------------------------------------------

    /**
     * Every directive at once. {@code alloc:1:25} is rarity 1, level 25, and the level is {@code ave};
     * {@code innate-freq:5} is stored as 100 / 5; the codes get their prefixes back.
     */
    @Test
    void everyDirectiveIsResolvedIntoItsField() throws Exception {
        PitProfile pit = one(new Rec().name("Orc pit").room("1").rarity("1").depth("25").objRarity("3")
                .flags("ANIMAL", "EVIL").bannedFlags("UNIQUE").innate("5")
                .spells("BR_ACID").bannedSpells("BR_ELEC", "SHRIEK")
                .colours("r", "g").bases("orc", "troll").bannedMonsters("Horned Reaper"));

        assertEquals("Orc pit", field(pit, "name"));
        assertEquals(PitRoomType.PIT_TYPE_PIT, field(pit, "roomType"));
        assertEquals(1, intField(pit, "rarity"));
        assertEquals(25, intField(pit, "ave"));
        assertEquals(3, intField(pit, "objectRarity"));
        assertEquals(20, intField(pit, "freqInnate"));

        Flag<MonsterRaceFlag> flags = field(pit, "flags");
        assertEquals(2, flags.count());
        assertTrue(flags.has(MonsterRaceFlag.RF_ANIMAL));
        assertTrue(flags.has(MonsterRaceFlag.RF_EVIL));
        Flag<MonsterRaceFlag> banned = field(pit, "forbiddenFlags");
        assertEquals(1, banned.count());
        assertTrue(banned.has(MonsterRaceFlag.RF_UNIQUE));

        Flag<MonsterSpell> spells = field(pit, "spellsFlags");
        assertEquals(1, spells.count());
        assertTrue(spells.has(MonsterSpell.RSF_BR_ACID));
        Flag<MonsterSpell> bannedSpells = field(pit, "forbiddenSpellFlags");
        assertEquals(2, bannedSpells.count());
        assertTrue(bannedSpells.has(MonsterSpell.RSF_BR_ELEC));
        assertTrue(bannedSpells.has(MonsterSpell.RSF_SHRIEK));

        assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_GREEN), field(pit, "colours"));
        List<MonsterBase> bases = field(pit, "bases");
        assertEquals(List.of("orc", "troll"), bases.stream().map(MonsterBase::getCodeName).toList());
        List<MonsterRace> bans = field(pit, "forbiddenMonsters");
        assertEquals(List.of("Horned Reaper"), bans.stream().map(MonsterRace::getName).toList());
    }

    /**
     * {@code mem_zalloc} leaves a record that has only a name with zero in every number, no room
     * type, empty flag sets and empty lists, and the assembler does the same.
     */
    @Test
    void aDirectiveARecordLeavesOutKeepsTheZeroCGivesIt() throws Exception {
        PitProfile pit = one(new Rec());

        assertEquals(PitRoomType.PIT_TYPE_NONE, field(pit, "roomType"));
        assertEquals(0, intField(pit, "rarity"));
        assertEquals(0, intField(pit, "ave"));
        assertEquals(0, intField(pit, "objectRarity"));
        assertEquals(0, intField(pit, "freqInnate"));
        assertEquals(0, raceFlagCount(pit, "flags"));
        assertEquals(0, raceFlagCount(pit, "forbiddenFlags"));
        assertEquals(0, spellCount(pit, "spellsFlags"));
        assertEquals(0, spellCount(pit, "forbiddenSpellFlags"));
        assertTrue(listSize(pit, "colours") == 0);
        assertTrue(listSize(pit, "bases") == 0);
        assertTrue(listSize(pit, "forbiddenMonsters") == 0);
    }

    /** The profiles come back in the order the records were given, each with its own name. */
    @Test
    void severalRecordsComeBackInTheOrderGiven() throws Exception {
        List<PitProfile> pits = assemble(new Rec().name("First"), new Rec().name("Second"),
                new Rec().name("Third"));

        assertEquals(3, pits.size());
        assertEquals("First", field(pits.get(0), "name"));
        assertEquals("Second", field(pits.get(1), "name"));
        assertEquals("Third", field(pits.get(2), "name"));
    }

    /** No records is an empty list and no error. */
    @Test
    void noRecordsGivesAnEmptyList() {
        assertTrue(assemble().isEmpty());
    }

    /** The assembler throws rather than collects, so the soft-error sink is left as it was found. */
    @Test
    void theErrorSinkIsLeftEmpty() {
        List<String> errors = new ArrayList<>();

        new PitAssembler().assemble(List.of(new Rec().build()), errors, FILE);

        assertTrue(errors.isEmpty(), errors::toString);
    }

    /** {@code string_make(parser_getstr(...))} copies the name exactly, edge spaces and colons included. */
    @Test
    void theNameIsPassedOnAsWritten() throws Exception {
        assertEquals(" Nest: of \"orcs\" ", field(one(new Rec().name(" Nest: of \"orcs\" ")), "name"));
    }

    // ---- room: ------------------------------------------------------------

    /**
     * 1, 2 and 3 are the pit, nest and other room types. Any other number, and an absent line, is
     * {@code PIT_TYPE_NONE}: C keeps the number, but {@code set_pit_type} only asks for 0, 1 or 2.
     * {@code 01} and {@code 02} are octal under base 0, so they are still 1 and 2; {@code 010} is 8.
     */
    @ParameterizedTest
    @CsvSource({
            "1,PIT_TYPE_PIT", "2,PIT_TYPE_NEST", "3,PIT_TYPE_OTHER",
            "0,PIT_TYPE_NONE", "4,PIT_TYPE_NONE", "9,PIT_TYPE_NONE", "99,PIT_TYPE_NONE",
            "01,PIT_TYPE_PIT", "02,PIT_TYPE_NEST", "03,PIT_TYPE_OTHER", "010,PIT_TYPE_NONE",
            "08,PIT_TYPE_NONE"})
    void theRoomNumberPicksTheRoomType(String written, PitRoomType expected) throws Exception {
        assertEquals(expected, field(one(new Rec().room(written)), "roomType"));
    }

    /** {@code parser_parse} refuses a {@code uint} token that starts with '-', {@code -0} included. */
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0", "-99"})
    void aRoomNumberStartingWithAMinusStopsTheLoad(String written) {
        DataFileParseException e = stopsTheLoad(new Rec().room(written));

        assertTrue(e.getMessage().endsWith("room type value: " + written), e.getMessage());
    }

    // ---- alloc: and obj-rarity: ---------------------------------------------

    /**
     * {@code parse_pit_alloc} reads rarity first and level second, and stores the level in
     * {@code ave}; the two are easy to transpose.
     */
    @Test
    void allocIsRarityThenLevel() throws Exception {
        PitProfile pit = one(new Rec().rarity("2").depth("30"));

        assertEquals(2, intField(pit, "rarity"));
        assertEquals(30, intField(pit, "ave"));
    }

    /** The numbers are read as {@code strtoul} reads them in base 0: {@code 010} is 8 and {@code 08} is 0. */
    @ParameterizedTest
    @CsvSource({"0,0", "7,7", "100,100", "010,8", "017,15", "08,0", "0089,0"})
    void numbersAreReadInBaseZero(String written, int expected) throws Exception {
        PitProfile pit = one(new Rec().rarity(written).depth(written).objRarity(written));

        assertEquals(expected, intField(pit, "rarity"));
        assertEquals(expected, intField(pit, "ave"));
        assertEquals(expected, intField(pit, "objectRarity"));
    }

    /** Each of the three {@code uint} numbers refuses a leading '-', and the message names which one. */
    @Test
    void aNegativeAllocOrObjRarityNumberStopsTheLoad() {
        assertTrue(stopsTheLoad(new Rec().rarity("-2")).getMessage().endsWith("pit rarity value: -2"));
        assertTrue(stopsTheLoad(new Rec().depth("-3")).getMessage().endsWith("pit average depth value: -3"));
        assertTrue(stopsTheLoad(new Rec().objRarity("-4")).getMessage().endsWith("object rarity value: -4"));
        assertTrue(stopsTheLoad(new Rec().rarity("-0")).getMessage().endsWith("pit rarity value: -0"));
    }

    /**
     * Accepted deviation (decided 261027): C keeps the low 32 bits of a {@code uint}, so
     * {@code 4294967296} is 0 there. The port clamps a number outside the {@code int} range to
     * {@link Integer#MAX_VALUE} ({@link ParseUtils#parseIntString}), in each of the three
     * {@code uint} fields, and does not stop the load.
     */
    @Test
    void aNumberOutsideTheIntRangeIsClampedInTheUintFields() throws Exception {
        PitProfile pit = one(new Rec().rarity("99999999999").depth("4294967296").objRarity("2147483648"));

        assertEquals(Integer.MAX_VALUE, intField(pit, "rarity"));
        assertEquals(Integer.MAX_VALUE, intField(pit, "ave"));
        assertEquals(Integer.MAX_VALUE, intField(pit, "objectRarity"));
    }

    /**
     * The same clamp in {@code innate-freq:} leaves a value far above 100, so the range check
     * refuses it, naming the number as written.
     */
    @Test
    void aHugeInnateFreqIsClampedAndThenRefusedAsOutOfRange() {
        DataFileParseException e = stopsTheLoad(new Rec().innate("99999999999"));

        assertTrue(e.getMessage().endsWith("an out of range innate frequency value: 99999999999"),
                e.getMessage());
    }

    // ---- innate-freq: -------------------------------------------------------

    /**
     * {@code freq_innate = 100 / pct} with C's integer division: 3 gives 33, 34 gives 2, 51 gives 1.
     * The ends of the range, 1 and 100, are accepted.
     */
    @ParameterizedTest
    @CsvSource({"1,100", "2,50", "3,33", "4,25", "5,20", "7,14", "33,3", "34,2", "50,2", "51,1", "99,1",
            "100,1"})
    void innateFreqIsStoredAsHundredOverThePercentage(String written, int expected) throws Exception {
        assertEquals(expected, intField(one(new Rec().innate(written)), "freqInnate"));
    }

    /**
     * {@code pct < 1 || pct > 100} is {@code PARSE_ERROR_INVALID_SPELL_FREQ}. The field is an
     * {@code int} in C, so a negative gets this message and not the leading-'-' one.
     */
    @ParameterizedTest
    @ValueSource(strings = {"0", "101", "1000", "-1", "-100"})
    void anInnateFreqOutsideOneToAHundredStopsTheLoad(String written) {
        DataFileParseException e = stopsTheLoad(new Rec().innate(written));

        assertTrue(e.getMessage().endsWith("an out of range innate frequency value: " + written),
                e.getMessage());
    }

    /** {@code 08} is 0 in base 0, which is below the range, so it is refused as 0 is. */
    @Test
    void anInnateFreqThatReadsAsZeroStopsTheLoad() {
        stopsTheLoad(new Rec().innate("08"));
    }

    // ---- flags-req: and flags-ban: ------------------------------------------

    /** A repeated code is one bit, as {@code flag_on} sets a bit it already holds. */
    @Test
    void aRepeatedCodeIsOneBit() throws Exception {
        PitProfile pit = one(new Rec().flags("ANIMAL", "ANIMAL").spells("BR_ACID", "BR_ACID"));

        assertEquals(1, raceFlagCount(pit, "flags"));
        assertEquals(1, spellCount(pit, "spellsFlags"));
    }

    /**
     * The required and the banned lists are separate loops over separate record fields. A record
     * that gives only one of them must not leak into the other.
     */
    @Test
    void theBannedListsAreNotTheRequiredLists() throws Exception {
        PitProfile onlyRequired = one(new Rec().flags("ANIMAL").spells("BR_ACID"));
        PitProfile onlyBanned = one(new Rec().bannedFlags("UNIQUE").bannedSpells("BR_ELEC"));

        assertEquals(0, raceFlagCount(onlyRequired, "forbiddenFlags"));
        assertEquals(0, spellCount(onlyRequired, "forbiddenSpellFlags"));
        assertEquals(0, raceFlagCount(onlyBanned, "flags"));
        assertEquals(0, spellCount(onlyBanned, "spellsFlags"));
        assertTrue(hasRaceFlag(onlyBanned, "forbiddenFlags", MonsterRaceFlag.RF_UNIQUE));
        assertTrue(hasSpell(onlyBanned, "forbiddenSpellFlags", MonsterSpell.RSF_BR_ELEC));
    }

    /**
     * {@code grab_flag} fails on a code in no table with {@code PARSE_ERROR_INVALID_FLAG}. The code
     * is compared with {@code streq}, so a lower-case code is unknown too, and the race-flag table
     * has no {@code MAX}. Each of the four lists has its own message.
     */
    @ParameterizedTest
    @ValueSource(strings = {"NOT_A_FLAG", "animal", "Animal", "MAX", "RF_ANIMAL", "ANIMAL "})
    void anUnknownRaceFlagStopsTheLoad(String code) {
        DataFileParseException required = stopsTheLoad(new Rec().flags("ANIMAL", code));
        DataFileParseException banned = stopsTheLoad(new Rec().bannedFlags("UNIQUE", code));

        assertTrue(required.getMessage().endsWith("an illegal monster race flag value: " + code),
                required.getMessage());
        assertTrue(banned.getMessage().endsWith("an illegal banned monster race flag value: " + code),
                banned.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"NOT_A_SPELL", "br_acid", "Br_Acid", "RSF_BR_ACID", "BR_NOTHING"})
    void anUnknownSpellStopsTheLoad(String code) {
        DataFileParseException required = stopsTheLoad(new Rec().spells("BR_ACID", code));
        DataFileParseException banned = stopsTheLoad(new Rec().bannedSpells("BR_ELEC", code));

        assertTrue(required.getMessage().endsWith("an illegal spell value: " + code), required.getMessage());
        assertTrue(banned.getMessage().endsWith("an illegal banned spell value: " + code),
                banned.getMessage());
    }

    /**
     * {@code lookup_flag} starts at {@code FLAG_START} (1), so index 0, {@code NONE}, never matches in
     * either table. The sentinel has its own message in each of the four lists.
     */
    @Test
    void theNoneSentinelIsRefusedInAllFourLists() {
        assertTrue(stopsTheLoad(new Rec().flags("NONE")).getMessage()
                .endsWith("an illegal sentinel monster race flag value: NONE"));
        assertTrue(stopsTheLoad(new Rec().bannedFlags("NONE")).getMessage()
                .endsWith("an illegal banned monster race sentinel value: NONE"));
        assertTrue(stopsTheLoad(new Rec().spells("NONE")).getMessage()
                .endsWith("an illegal spell sentinel value: NONE"));
        assertTrue(stopsTheLoad(new Rec().bannedSpells("NONE")).getMessage()
                .endsWith("an illegal banned spell sentinel value: NONE"));
    }

    /**
     * The spell table is built from {@code list-mon-spells.h}, whose last row is {@code MAX}, so
     * {@code grab_flag} accepts it as a spell (and sets the bit one past the last real spell).
     */
    @Test
    void maxIsAValidSpellCodeAsInC() throws Exception {
        PitProfile pit = one(new Rec().spells("MAX").bannedSpells("MAX"));

        assertTrue(hasSpell(pit, "spellsFlags", MonsterSpell.RSF_MAX));
        assertTrue(hasSpell(pit, "forbiddenSpellFlags", MonsterSpell.RSF_MAX));
    }

    /** The first bad code ends the load, so a second bad code in the same list is never named. */
    @Test
    void theFirstBadCodeEndsTheLoad() {
        DataFileParseException e = stopsTheLoad(new Rec().flags("FIRST_BAD", "SECOND_BAD"));

        assertTrue(e.getMessage().contains("FIRST_BAD"), e.getMessage());
        assertFalse(e.getMessage().contains("SECOND_BAD"), e.getMessage());
    }

    // ---- color: -------------------------------------------------------------

    /**
     * One character goes to {@code color_char_to_attr}: a letter in {@code color_table} is that
     * colour, matched case-sensitively ({@code d} is dark and {@code D} is light dark), and a space
     * is dark. The order of the entries is kept.
     */
    @Test
    void aColourCodeIsLookedUpCaseSensitively() throws Exception {
        PitProfile pit = one(new Rec().colours("d", "D", "r", "R", " "));

        assertEquals(List.of(ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_LIGHT_DARK, ColourEnum.COLOUR_RED,
                ColourEnum.COLOUR_LIGHT_RED, ColourEnum.COLOUR_DARK), field(pit, "colours"));
    }

    /**
     * A character in no row of {@code color_table} is white, not an error, because
     * {@code color_char_to_attr} falls back to {@code COLOUR_WHITE}. The lexer admits only real
     * codes, so these reach the assembler only by hand.
     */
    @ParameterizedTest
    @ValueSource(strings = {"q", "?", "1", "x", "-"})
    void anUnknownColourCharacterIsWhiteRatherThanAnError(String code) throws Exception {
        assertEquals(List.of(ColourEnum.COLOUR_WHITE), field(one(new Rec().colours(code)), "colours"));
    }

    /**
     * More than one character goes to {@code color_text_to_attr}, which matches the names
     * case-insensitively and falls back to white. The lexer only admits one letter, so these reach
     * the assembler only by hand.
     */
    @Test
    void aColourNameIsMatchedIgnoringCaseAndUnknownNamesAreWhite() throws Exception {
        PitProfile pit = one(new Rec().colours("Red", "LIGHT DARK", "nonsense"));

        assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_LIGHT_DARK, ColourEnum.COLOUR_WHITE),
                field(pit, "colours"));
    }

    // ---- mon-base: ----------------------------------------------------------

    /**
     * {@code lookup_monster_base} compares with {@code streq}, so the name must match exactly,
     * case included, and there is no substring fallback. C ends the load with
     * {@code PARSE_ERROR_UNRECOGNISED_TVAL}.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Orc", "ORC", "or", "orcs", "hippogriff", " orc"})
    void aMonBaseThatIsNotExactlyALoadedBaseStopsTheLoad(String name) {
        DataFileParseException e = stopsTheLoad(new Rec().bases("orc", name));

        assertTrue(e.getMessage().endsWith("an illegal monster base value: " + name), e.getMessage());
    }

    /** The bases come back as the registry's own objects, in the order written. */
    @Test
    void theBasesAreTheRegistrysObjectsInFileOrder() throws Exception {
        PitProfile pit = one(new Rec().bases("troll", "orc"));

        List<MonsterBase> bases = field(pit, "bases");
        assertEquals(2, bases.size());
        assertEquals("troll", bases.get(0).getCodeName());
        assertEquals("orc", bases.get(1).getCodeName());
    }

    // ---- mon-ban: -----------------------------------------------------------

    /**
     * {@code lookup_monster} matches the whole name ignoring case, so an upper-case ban finds the
     * race the file spells in mixed case.
     */
    @Test
    void aMonBanNameMatchesIgnoringCase() throws Exception {
        PitProfile pit = one(new Rec().bannedMonsters("HORNED REAPER"));

        List<MonsterRace> bans = field(pit, "forbiddenMonsters");
        assertEquals("Horned Reaper", bans.getFirst().getName());
    }

    /**
     * With no exact match, {@code lookup_monster} returns the first race whose name contains the
     * text, so {@code reaper} finds {@code Horned Reaper}.
     */
    @Test
    void aMonBanNameFallsBackToTheFirstNameContainingIt() throws Exception {
        PitProfile pit = one(new Rec().bannedMonsters("reaper"));

        List<MonsterRace> bans = field(pit, "forbiddenMonsters");
        assertEquals("Horned Reaper", bans.getFirst().getName());
    }

    /**
     * An exact match anywhere in the list beats a containing match that comes earlier: the loop
     * returns on equality at once and only remembers the first containing match. {@code Grip,
     * Farmer Maggot's Dog} contains {@code Grip} and sits ahead of {@code Grip}, which is the exact
     * match.
     */
    @Test
    void anExactMonBanNameBeatsAnEarlierContainingOne() throws Exception {
        PitProfile pit = one(new Rec().bannedMonsters("grip"));

        List<MonsterRace> bans = field(pit, "forbiddenMonsters");
        assertEquals("Grip", bans.getFirst().getName());
    }

    /**
     * {@code parse_pit_mon_ban} never checks what {@code lookup_monster} returned, so a name that
     * matches nothing becomes a {@code NULL} race in the list and the load goes on. Here it is a
     * {@code null} entry, in place, between the ones that resolved.
     */
    @Test
    void aMonBanNameThatMatchesNothingIsStoredAsNull() throws Exception {
        PitProfile pit = one(new Rec().bannedMonsters("Horned Reaper", "Norman the Unlikely", "Grip"));

        List<MonsterRace> bans = field(pit, "forbiddenMonsters");
        assertEquals(3, bans.size());
        assertEquals("Horned Reaper", bans.get(0).getName());
        assertNull(bans.get(1));
        assertEquals("Grip", bans.get(2).getName());
    }

    // ---- Registries not loaded -----------------------------------------------

    /**
     * Both lookups throw when their registry has not been loaded, which is why pits load after
     * {@code monster_base.txt} and {@code monster.txt}. A record that names neither never asks.
     */
    @Test
    void theLookupsNeedTheirRegistriesButOnlyWhenAskedFor() {
        MonsterRegistry.setMonsterBases(null);
        MonsterRegistry.monsterRaces = null;

        assertThrows(IllegalStateException.class, () -> assemble(new Rec().bases("orc")));
        assertThrows(IllegalStateException.class, () -> assemble(new Rec().bannedMonsters("Grip")));
        assertEquals(1, assemble(new Rec().flags("ANIMAL").colours("r")).size());
    }

    // ---- Stopping the load ------------------------------------------------------

    /** The message names the line the record's {@code name:} sits on and the file. */
    @Test
    void theMessageNamesTheRecordsLineAndTheFile() {
        DataFileParseException e = stopsTheLoad(new Rec().line(41).innate("0"));

        assertEquals("Pit record starting at line: 41 of file: pit.txt has an out of range innate frequency"
                + " value: 0", e.getMessage());
    }

    /**
     * C ends the whole load at the first bad line, so a bad record anywhere gives no list at all,
     * whether good records come before it or after it, and it is the bad record's own line that is named.
     */
    @Test
    void aBadRecordEndsTheLoadWhereverItSits() {
        DataFileParseException after = stopsTheLoad(new Rec().name("Good").line(3),
                new Rec().name("Bad").line(12).flags("NOT_A_FLAG"));
        DataFileParseException before = stopsTheLoad(new Rec().name("Bad").line(5).flags("NOT_A_FLAG"),
                new Rec().name("Good").line(15));

        assertTrue(after.getMessage().contains("line: 12 "), after.getMessage());
        assertTrue(before.getMessage().contains("line: 5 "), before.getMessage());
    }

    /** Nothing is written to the soft-error sink on the way to a refusal either. */
    @Test
    void theErrorSinkIsStillEmptyAfterARefusal() {
        List<String> errors = new ArrayList<>();

        assertThrows(DataFileParseException.class,
                () -> new PitAssembler().assemble(List.of(new Rec().flags("NOT_A_FLAG").build()), errors, FILE));

        assertTrue(errors.isEmpty(), errors::toString);
    }
}
