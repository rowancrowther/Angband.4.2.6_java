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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.jackoftradesltd.backend.parser.MonsterBaseReader;
import uk.co.jackoftradesltd.backend.parser.PainReader;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link MonsterBase} against {@code struct monster_base} in {@code monster.h} and the
 * {@code monster_base.txt} records that C's {@code parse_mon_base_*} functions in
 * {@code mon-init.c} turn into it.
 *
 * <p>The expected rows in {@link #C_RECORDS} were read off the {@code name:}, {@code glyph:},
 * {@code pain:} and {@code desc:} lines of 4.2.6's own {@code lib/gamedata/monster_base.txt}, not
 * taken from the Java loader's output. The two flag expectations follow C's {@code parse_mon_base_flags},
 * which ORs every {@code flags:} line into one set. The private {@code pain}, {@code inGameName} and
 * {@code description} fields have no accessors yet, so they are read by reflection.
 *
 * @author Rowan Crowther
 */
class MonsterBaseTest {

    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";

    /**
     * One expected record: name, glyph, pain serial and desc text, in C's file order.
     */
    private record Row(String name, char glyph, int pain, String desc) {
    }

    /**
     * Every record of 4.2.6's {@code monster_base.txt}, in file order.
     */
    private static final List<Row> C_RECORDS = List.of(
            new Row("ancient dragon", 'D', 1, "Ancient Dragon/Wyrm"),
            new Row("ainu", 'A', 1, "Ainu/maia"),
            new Row("ant", 'a', 7, "Ant"),
            new Row("bat", 'b', 4, "Bat"),
            new Row("bird", 'B', 8, "Bird"),
            new Row("canine", 'C', 3, "Canine (Dog)"),
            new Row("centipede", 'c', 5, "Centipede"),
            new Row("creeping coins", '$', 1, "Creeping Coins"),
            new Row("dragon", 'd', 1, "Dragon"),
            new Row("dragon fly", 'F', 7, "Dragon Fly"),
            new Row("eye", 'e', 1, "Floating Eye"),
            new Row("elemental", 'E', 1, "Elemental"),
            new Row("feline", 'f', 6, "Feline (Cat)"),
            new Row("ghost", 'G', 1, "Ghost"),
            new Row("giant", 'P', 1, "Giant Humanoid"),
            new Row("golem", 'g', 1, "Golem"),
            new Row("humanoid", 'h', 1, "Hobbit/Elf/Dwarf"),
            new Row("hybrid", 'H', 1, "Hybrid"),
            new Row("hydra", 'M', 4, "Hydra"),
            new Row("icky thing", 'i', 2, "Icky Thing"),
            new Row("insect", 'I', 7, "Insect"),
            new Row("jelly", 'j', 2, "Jelly"),
            new Row("killer beetle", 'K', 7, "Killer Beetle"),
            new Row("kobold", 'k', 1, "Kobold"),
            new Row("lich", 'L', 1, "Lich"),
            new Row("lurker", 'x', 1, "Lurker"),
            new Row("major demon", 'U', 1, "Major Demon"),
            new Row("mimic", '?', 1, "Mimic"),
            new Row("minor demon", 'u', 1, "Minor Demon"),
            new Row("mold", 'm', 2, "Mold"),
            new Row("mushroom", ',', 2, "Mushroom"),
            new Row("naga", 'n', 1, "Naga"),
            new Row("ogre", 'O', 1, "Ogre"),
            new Row("orc", 'o', 1, "Orc"),
            new Row("person", 'p', 1, "Person/Human"),
            new Row("quadruped", 'q', 4, "Quadruped"),
            new Row("quylthulg", 'Q', 2, "Quylthulg"),
            new Row("reptile", 'R', 5, "Reptile/Amphibian"),
            new Row("rodent", 'r', 4, "Rodent"),
            new Row("skeleton", 's', 9, "Skeleton"),
            new Row("snake", 'J', 5, "Snake"),
            new Row("spider", 'S', 7, "Spider/Scorpion/Tick"),
            new Row("townsfolk", 't', 4, "Townsfolk"),
            new Row("tree", 'l', 9, "Tree/Ent"),
            new Row("troll", 'T', 1, "Troll"),
            new Row("vampire", 'V', 1, "Vampire"),
            new Row("vortex", 'v', 11, "Vortex"),
            new Row("worm", 'w', 1, "Worm/Worm Mass"),
            new Row("wraith", 'W', 1, "Wight/Wraith"),
            new Row("xorn", 'X', 4, "Xorn/Xaren"),
            new Row("yeek", 'y', 1, "Yeek"),
            new Row("yeti", 'Y', 1, "Yeti"),
            new Row("zephyr hound", 'Z', 3, "Zephyr Hound"),
            new Row("zombie", 'z', 10, "Zombie/Mummy"),
            new Row("player", '@', 1, "You"),
            new Row("Morgoth", 'P', 12, "Morgoth"));

    /**
     * The value {@code MonsterRegistry.monsterPains} held before this suite overwrote it.
     */
    private static Object savedMonsterPains;
    private static List<MonsterBase> bases;

    @BeforeAll
    static void loadBases() throws Exception {
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();
        Field field = MonsterRegistry.class.getDeclaredField("monsterPains");
        field.setAccessible(true);
        savedMonsterPains = field.get(null);
        field.set(null, pains);

        bases = new MonsterBaseReader().parse(BASE_FILE);
    }

    @AfterAll
    static void restorePainSets() throws Exception {
        Field field = MonsterRegistry.class.getDeclaredField("monsterPains");
        field.setAccessible(true);
        field.set(null, savedMonsterPains);
    }

    private static Object readField(MonsterBase base, String name) throws Exception {
        Field field = MonsterBase.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(base);
    }

    private static MonsterBase named(String name) {
        return bases.stream().filter(b -> b.getCodeName().equals(name)).findFirst().orElseThrow();
    }

    // ---- The code-name-only constructor: C's mem_zalloc'd struct ---------

    @Test
    void codeNameOnlyConstructorGivesAZeroedStruct() throws Exception {
        MonsterBase base = new MonsterBase("dragon");

        assertEquals("dragon", base.getCodeName());
        assertTrue(base.getFlags().isEmpty());
        assertEquals('\0', base.getDefaultMonsterChar());
        assertNull(readField(base, "pain"));
        assertEquals("", readField(base, "inGameName"));
        assertEquals("", readField(base, "description"));
    }

    // ---- The full constructor ----------------------------------------------

    @Test
    void fullConstructorStoresEveryFieldAndKeepsTheFlagSetByReference() throws Exception {
        Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class, MonsterRaceFlag.RF_EVIL);
        MonsterBase base = new MonsterBase("orc", "Orc", flags, 'o', 0, null, "Orc text");

        assertEquals("orc", base.getCodeName());
        assertSame(flags, base.getFlags());
        assertEquals('o', base.getDefaultMonsterChar());
        assertEquals(0, base.getWideMonsterChar());
        assertNull(readField(base, "pain"));
        assertEquals("Orc", readField(base, "inGameName"));
        assertEquals("Orc text", readField(base, "description"));
    }

    // ---- The glyph split: C's one wchar_t d_char held as a char and an int -----

    /**
     * Builds a base around one glyph, passing the halves the way the split's convention asks.
     */
    private static MonsterBase baseWithGlyph(int codePoint) {
        char narrow = codePoint <= 0xFFFF ? (char) codePoint : '\0';
        int wide = codePoint <= 0xFFFF ? 0 : codePoint;
        return new MonsterBase("glyph", "Glyph", new Flag<>(MonsterRaceFlag.class), narrow, wide, null, "Glyph");
    }

    @Test
    void codeNameOnlyConstructorHasNoWideGlyph() {
        // mem_zalloc zeroes d_char, so there is nothing wide to report.
        MonsterBase base = new MonsterBase("dragon");

        assertEquals(0, base.getWideMonsterChar());
        assertFalse(base.isWideChar());
    }

    @Test
    void anAsciiGlyphIsNarrowAndNotWide() {
        // 'D' is d_char 0x44 in C; it fits a char, so the wide half stays 0.
        MonsterBase base = baseWithGlyph('D');

        assertEquals('D', base.getDefaultMonsterChar());
        assertEquals(0, base.getWideMonsterChar());
        assertFalse(base.isWideChar());
    }

    @Test
    void aGlyphAtTheTopOfTheBasicPlaneIsStillNarrow() {
        // 0xFFFF is the last code point a char holds; C stores it in d_char like any other.
        MonsterBase base = baseWithGlyph(0xFFFF);

        assertEquals('￿', base.getDefaultMonsterChar());
        assertEquals(0, base.getWideMonsterChar());
        assertFalse(base.isWideChar());
    }

    @Test
    void aGlyphOneAboveTheBasicPlaneIsWide() {
        // 0x10000 is the first code point a char cannot hold, so it goes to the wide half.
        MonsterBase base = baseWithGlyph(0x10000);

        assertEquals('\0', base.getDefaultMonsterChar());
        assertEquals(0x10000, base.getWideMonsterChar());
        assertTrue(base.isWideChar());
    }

    @Test
    void aSupplementaryGlyphIsWideAndKeepsItsWholeCodePoint() {
        // U+1F409 (a dragon emoji) is 0x1F409 in a wchar_t; the int half keeps all of it.
        MonsterBase base = baseWithGlyph(0x1F409);

        assertEquals('\0', base.getDefaultMonsterChar());
        assertEquals(0x1F409, base.getWideMonsterChar());
        assertTrue(base.isWideChar());
    }

    @Test
    void theTopCodePointIsWide() {
        MonsterBase base = baseWithGlyph(0x10FFFF);

        assertEquals(0x10FFFF, base.getWideMonsterChar());
        assertTrue(base.isWideChar());
    }

    @Test
    void aNulGlyphIsNotWide() {
        // d_char 0 in C: both halves zero, and isWideChar is what tells it from a wide glyph.
        MonsterBase base = baseWithGlyph(0);

        assertEquals('\0', base.getDefaultMonsterChar());
        assertEquals(0, base.getWideMonsterChar());
        assertFalse(base.isWideChar());
    }

    @Test
    void isWideCharTestsOnlyTheWideHalf() {
        // The constructor does not police the pairing; isWideChar follows the wide half alone.
        MonsterBase base = new MonsterBase("odd", "Odd", new Flag<>(MonsterRaceFlag.class), 'a', 0x1F409,
                null, "Odd");

        assertEquals('a', base.getDefaultMonsterChar());
        assertEquals(0x1F409, base.getWideMonsterChar());
        assertTrue(base.isWideChar());
    }

    @Test
    void noGlyphInTheRealFileIsWide() {
        // Every glyph: line in 4.2.6's monster_base.txt is a single ASCII character.
        for (MonsterBase base : bases) {
            assertFalse(base.isWideChar(), base.getCodeName());
            assertEquals(0, base.getWideMonsterChar(), base.getCodeName());
        }
    }

    // ---- Every record of the real file --------------------------------------

    @Test
    void theRealFileLoadsOneBasePerRecordInCsChainOrder() {
        // parse_mon_base_name pushes each record on the head of rb_info, so C's chain runs from
        // Morgoth (last in the file) back to "ancient dragon" (first in the file).
        assertEquals(C_RECORDS.size(), bases.size());
        List<String> names = new ArrayList<>();
        bases.forEach(b -> names.add(b.getCodeName()));
        assertEquals(C_RECORDS.reversed().stream().map(Row::name).toList(), names);
    }

    @Test
    void everyRecordCarriesItsGlyphPainSetAndDescText() throws Exception {
        assertEquals(C_RECORDS.size(), bases.size());
        List<Executable> checks = new ArrayList<>();
        for (int i = 0; i < C_RECORDS.size(); i++) {
            Row row = C_RECORDS.get(i);
            // The list is in C's chain order, the reverse of the file order the rows are in.
            MonsterBase base = bases.get(C_RECORDS.size() - 1 - i);
            String label = row.name();

            checks.add(() -> assertEquals(row.glyph(), base.getDefaultMonsterChar(), label + " glyph"));
            checks.add(() -> assertSame(MonsterRegistry.lookupMonsterPain(row.pain()),
                    readField(base, "pain"), label + " pain set"));
            checks.add(() -> assertEquals(row.desc(), readField(base, "inGameName"),
                    label + " in-game name"));
            checks.add(() -> assertEquals(row.desc(), readField(base, "description"),
                    label + " description"));
        }
        assertAll(checks);
    }

    // ---- Flag accumulation: C ORs every flags: line into one set -------------

    @Test
    void flagsFromEveryFlagsLineAreOrredTogether() {
        // ancient dragon spreads twelve flags over five flags: lines in the C file.
        Flag<MonsterRaceFlag> expected = new Flag<>(MonsterRaceFlag.class,
                MonsterRaceFlag.RF_DRAGON, MonsterRaceFlag.RF_EVIL, MonsterRaceFlag.RF_POWERFUL,
                MonsterRaceFlag.RF_SMART, MonsterRaceFlag.RF_SPIRIT, MonsterRaceFlag.RF_DROP_4,
                MonsterRaceFlag.RF_MOVE_BODY, MonsterRaceFlag.RF_CLEAR_WEB, MonsterRaceFlag.RF_NO_CONF,
                MonsterRaceFlag.RF_NO_SLEEP, MonsterRaceFlag.RF_NO_HOLD, MonsterRaceFlag.RF_FORCE_SLEEP);

        Flag<MonsterRaceFlag> actual = named("ancient dragon").getFlags();

        assertEquals(12, actual.count());
        assertTrue(actual.isEqual(expected));
    }

    @Test
    void aSharedFlagOnTwoLinesIsSetOnce() {
        // orc: "MALE | ORC | EVIL | SPIRIT" then "OPEN_DOOR | BASH_DOOR | CLEAR_WEB".
        Flag<MonsterRaceFlag> expected = new Flag<>(MonsterRaceFlag.class,
                MonsterRaceFlag.RF_MALE, MonsterRaceFlag.RF_ORC, MonsterRaceFlag.RF_EVIL,
                MonsterRaceFlag.RF_SPIRIT, MonsterRaceFlag.RF_OPEN_DOOR, MonsterRaceFlag.RF_BASH_DOOR,
                MonsterRaceFlag.RF_CLEAR_WEB);

        assertTrue(named("orc").getFlags().isEqual(expected));
        assertEquals(7, named("orc").getFlags().count());
    }

    @Test
    void aRecordWithNoFlagsLineHasAnEmptyFlagSet() {
        // C's flags: line is optional; "player" and "Morgoth" have none.
        assertTrue(named("player").getFlags().isEmpty());
        assertTrue(named("Morgoth").getFlags().isEmpty());
    }

    // ---- Name matching is exact, as C's streq ----------------------------------

    @Test
    void namesKeepTheirCaseAndEmbeddedSpaces() {
        assertEquals("Morgoth", named("Morgoth").getCodeName());
        assertEquals("dragon fly", named("dragon fly").getCodeName());
        assertNotSame(named("dragon"), named("dragon fly"));
    }
}
