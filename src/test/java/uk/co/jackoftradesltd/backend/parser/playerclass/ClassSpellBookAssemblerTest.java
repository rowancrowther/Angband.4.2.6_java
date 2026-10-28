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
import uk.co.jackoftradesltd.backend.parser.ObjectBaseReader;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ClassSpellBookAssembler} against {@code parse_class_book()},
 * {@code parse_class_book_graphics()} and {@code parse_class_book_properties()} in {@code init.c},
 * and the {@code write_book_kind()} that {@code parse_class_book()} calls.
 *
 * <p>The expected values come from the C. {@code write_book_kind} gives a new kind a glyph of
 * {@code *} in red, a die of 1d1 and a weight of 30, adds the sval after the base's last one (the
 * count starts at 0, so the first book under a base is sval 1), copies the base's kind flags, and,
 * for a dungeon book only, sets {@code KF_GOOD} and {@code EL_INFO_IGNORE} on the four base
 * elements ({@code ELEM_BASE_MIN} up to but not including {@code ELEM_BASE_MAX}: acid, electricity,
 * fire, cold). The graphics line overwrites the glyph and colour, a one-character colour going
 * through {@code color_char_to_attr} and a longer one through {@code color_text_to_attr}, both of
 * which answer white for an unknown colour. The properties line sets the cost, the allocation
 * probability, and {@code level}, {@code alloc_min} and {@code alloc_max} from the first and second
 * numbers of the range as written, with no comparison between them. A book whose name is already
 * registered is not built again.
 *
 * <p>The {@code spells} figure on the {@code book:} line sizes the book's array, so
 * {@code parse_class_spell} refuses the first {@code spell:} line past it
 * ({@code PARSE_ERROR_TOO_MANY_ENTRIES}); fewer is accepted, and a declared 0 allows none.
 *
 * <p>Every error stops the load in C, so the assembler throws a {@link DataFileParseException}
 * naming the line and the file, and the tests expect that.
 *
 * <p>The registries are seeded from the real {@code realm.txt} and {@code object_base.txt} through
 * their own readers, with a single placeholder kind so that the kind table counts as loaded, and
 * are put back afterwards so nothing leaks to other suites. {@link ObjectKind} and
 * {@link MagicBook} expose no getters for some of the fields tested, which are read reflectively.
 *
 * <p>Two tests are marked DOCUMENTED DIFFERENCE: they pin the points in the class Javadoc where the
 * port departs from C and the shipped {@code class.txt} cannot show it, so a change fails loudly.
 *
 * <p>Class ClassSpellBookAssemblerTest coded on 261028, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
class ClassSpellBookAssemblerTest {

    /** The line every record in these tests claims to start on. */
    private static final int LINE = 30;

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

    /**
     * A fresh set of registries for every test, because registering a book moves the base's sval
     * count and adds to the kind table.
     */
    @BeforeEach
    void seed() throws Exception {
        BookRegistryFixture.seed();
    }

    // ---- fixture + reflection helpers -------------------------------------------------------

    private static Object get(String name) throws Exception {
        return BookRegistryFixture.get(name);
    }

    private static void set(String name, Object value) throws Exception {
        BookRegistryFixture.set(name, value);
    }

    @SuppressWarnings("unchecked")
    private static void addToTable(ObjectKind kind) throws Exception {
        ((List<ObjectKind>) get("objectKinds")).add(kind);
    }

    @SuppressWarnings("unchecked")
    private static <T> T read(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    private static void write(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static ClassSpellParseRecord spell(String name) {
        return new ClassSpellParseRecord(name, "1", "1", "22", "", List.of(), "4", LINE + 1);
    }

    private static List<ClassSpellParseRecord> spells(int count) {
        List<ClassSpellParseRecord> spells = new ArrayList<>();
        for (int i = 0; i < count; i++)
            spells.add(spell("Spell " + i));
        return spells;
    }

    /** A complete book record: {@code book:}, {@code book-graphics:} and {@code book-properties:}. */
    private static ClassSpellBookParseRecord record(String oBase, String locFrom, String name, String declared,
                                                    String realm, String glyph, String colour, String cost,
                                                    String commonness, String min, String max, int spellCount) {
        return new ClassSpellBookParseRecord(oBase, locFrom, name, declared, realm, glyph, colour, cost,
                commonness, min, max, spells(spellCount), LINE);
    }

    /** The town book from the ordinary path: 7 declared, 2 listed, with both optional lines. */
    private static ClassSpellBookParseRecord town() {
        return record("magic book", "town", "[First Spells]", "7", "arcane", "?", "R", "25", "40", "1", "100", 2);
    }

    /** A book with neither optional line, so every optional value arrives as an empty string. */
    private static ClassSpellBookParseRecord bare(String locFrom, String name) {
        return record("magic book", locFrom, name, "2", "arcane", "", "", "", "", "", "", 2);
    }

    private static List<MagicBook> run(ClassSpellBookParseRecord... records) {
        return new ClassSpellBookAssembler().assemble(List.of(records), new ArrayList<>(), FILE);
    }

    private static MagicBook one(ClassSpellBookParseRecord record) {
        List<MagicBook> books = run(record);
        assertEquals(1, books.size());
        return books.getFirst();
    }

    private static DataFileParseException stops(ClassSpellBookParseRecord record) {
        return assertThrows(DataFileParseException.class, () -> run(record));
    }

    private static void assertNames(DataFileParseException e, String... parts) {
        for (String part : parts)
            assertTrue(e.getMessage().contains(part), () -> "expected \"" + part + "\" in: " + e.getMessage());
    }

    /** The kind the assembler registered under {@code name}, which the registry hands back by name. */
    private static ObjectKind kindNamed(String name) {
        ObjectKind kind = ObjectRegistry.lookupObjectKind(name);
        assertNotNull(kind, () -> "no kind registered under " + name);
        return kind;
    }

    private static int count(String name) {
        return (int) ObjectRegistry.getObjectKinds().stream().filter(k -> name.equals(k.getName())).count();
    }

    // ---- the ordinary path ------------------------------------------------------------------

    @Test
    @DisplayName("an ordinary town book becomes a MagicBook with the book: line's tval, name and spells")
    void ordinaryBook() throws Exception {
        MagicBook book = one(town());

        assertEquals("[First Spells]", book.getBookName());
        assertEquals(TValue.TV_MAGIC_BOOK, book.getBookTValue());
        assertFalse(book.isDungeon());
        assertEquals(2, book.getNumOfSpells());
        assertEquals(2, book.getSpells().size());
        assertEquals("Spell 0", (String) read(book.getSpells().get(0), "spellName"));
        assertEquals("Spell 1", (String) read(book.getSpells().get(1), "spellName"));
    }

    @Test
    @DisplayName("every spell takes the book's realm, as parse_class_spell copies book->realm")
    void spellsTakeTheBooksRealm() throws Exception {
        MagicBook book = one(town());

        assertNotNull(book.getRealm());
        assertEquals("arcane", book.getRealm().getName());
        for (var spell : book.getSpells())
            assertSame(book.getRealm(), read(spell, "realm"));
    }

    @Test
    @DisplayName("write_book_kind: the new kind has 1d1, weight 30, the next sval, and the properties line's figures")
    void newKindFromTheBookLines() throws Exception {
        MagicBook book = one(town());
        ObjectKind kind = kindNamed("[First Spells]");

        assertEquals(1, kind.getDamageDice());
        assertEquals(1, kind.getDamageSides());
        assertEquals(30, kind.getWeight());
        assertEquals(25, kind.getCost());
        assertEquals(40, (int) read(kind, "alloc_prob"));
        assertEquals(1, (int) read(kind, "alloc_min"));
        assertEquals(100, (int) read(kind, "alloc_max"));
        assertEquals(1, (int) read(kind, "level"));
        assertEquals(TValue.TV_MAGIC_BOOK, kind.gettValue());
        assertSame(ObjectRegistry.getBaseFromTVal(TValue.TV_MAGIC_BOOK), kind.getBase());

        // sval starts at 1 under a base whose count starts at 0, and the book learns it
        assertEquals(1, kind.getsVal());
        assertEquals(1, book.getSval());
        assertEquals(1, kind.getBase().getNumSvals());
    }

    @Test
    @DisplayName("the graphics line sets the glyph and the colour: ? and R (Light Red)")
    void graphicsLine() {
        one(town());
        ObjectKind kind = kindNamed("[First Spells]");

        assertEquals('?', kind.getCharacter().getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, kind.getCharacter().getAttributeColour());
    }

    @Test
    @DisplayName("the kind inherits the base's kind flags: EASY_KNOW for a magic book")
    void inheritsBaseKindFlags() {
        one(town());

        assertTrue(kindNamed("[First Spells]").getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
    }

    @Test
    @DisplayName("a town book is not KF_GOOD and ignores no element")
    void townBookHasNoDungeonExtras() {
        one(town());
        ObjectKind kind = kindNamed("[First Spells]");

        assertFalse(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD));
        for (ElementEnum e : ElementEnum.values())
            assertFalse(kind.getElInfo(e).has(ElementInfoEnum.EL_INFO_IGNORE), e.name());
    }

    @Test
    @DisplayName("a dungeon book is KF_GOOD and ignores exactly acid, electricity, fire and cold")
    void dungeonBookExtras() {
        MagicBook book = one(record("magic book", "dungeon", "[Arcane Control]", "6", "arcane",
                "?", "r", "30000", "15", "40", "100", 1));
        ObjectKind kind = kindNamed("[Arcane Control]");

        assertTrue(book.isDungeon());
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_GOOD));
        assertTrue(kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW), "the base's flags are kept as well");
        Set<ElementEnum> base = Set.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC,
                ElementEnum.ELEM_FIRE, ElementEnum.ELEM_COLD);
        for (ElementEnum e : ElementEnum.values())
            assertEquals(base.contains(e), kind.getElInfo(e).has(ElementInfoEnum.EL_INFO_IGNORE), e.name());
    }

    @Test
    @DisplayName("any quality other than dungeon makes a town book, as C tests only for dungeon")
    void anyOtherQualityIsTown() {
        MagicBook book = one(record("magic book", "elsewhere", "[Odd]", "2", "arcane",
                "?", "R", "1", "1", "1", "5", 1));

        assertFalse(book.isDungeon());
        assertFalse(kindNamed("[Odd]").getKindFlags().has(ObjectKindFlag.KF_GOOD));
    }

    @Test
    @DisplayName("the errors list is untouched on a good load")
    void errorsListUntouched() {
        List<String> errors = new ArrayList<>();
        new ClassSpellBookAssembler().assemble(List.of(town()), errors, FILE);

        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("an empty record list gives an empty result and registers nothing")
    void noRecords() {
        assertTrue(new ClassSpellBookAssembler().assemble(List.of(), new ArrayList<>(), FILE).isEmpty());
        assertEquals(1, ObjectRegistry.getObjectKinds().size(), "only the placeholder is there");
    }

    @Test
    @DisplayName("each realm name is found whatever its case, as lookup_realm uses my_stricmp")
    void realmIsCaseInsensitive() {
        assertEquals("arcane", one(record("magic book", "town", "[A]", "1", "ARCANE",
                "?", "R", "1", "1", "1", "5", 1)).getRealm().getName());
        assertEquals("nature", one(record("magic book", "town", "[B]", "1", "NaTuRe",
                "?", "R", "1", "1", "1", "5", 1)).getRealm().getName());
    }

    // ---- the optional lines: write_book_kind's defaults ---------------------------------------

    @Test
    @DisplayName("no book-graphics: line leaves write_book_kind's defaults: * in red")
    void missingGraphicsGivesStarInRed() {
        one(bare("town", "[Plain]"));
        ObjectKind kind = kindNamed("[Plain]");

        assertEquals('*', kind.getCharacter().getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_RED, kind.getCharacter().getAttributeColour());
    }

    @Test
    @DisplayName("no book-properties: line leaves cost, probability, level and both depths at 0")
    void missingPropertiesGivesZeros() throws Exception {
        one(bare("town", "[Plain]"));
        ObjectKind kind = kindNamed("[Plain]");

        assertEquals(0, kind.getCost());
        assertEquals(0, (int) read(kind, "alloc_prob"));
        assertEquals(0, (int) read(kind, "alloc_min"));
        assertEquals(0, (int) read(kind, "alloc_max"));
        assertEquals(0, (int) read(kind, "level"));
    }

    @Test
    @DisplayName("a glyph with a colour but no properties, and the reverse, each keep the other's default")
    void halfTheOptionalLines() throws Exception {
        one(record("magic book", "town", "[G]", "1", "arcane", "#", "", "", "", "", "", 1));
        one(record("magic book", "town", "[P]", "1", "arcane", "", "", "9", "8", "7", "6", 1));

        ObjectKind g = kindNamed("[G]");
        assertEquals('#', g.getCharacter().getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_RED, g.getCharacter().getAttributeColour(), "no colour given: red");
        assertEquals(0, g.getCost());

        ObjectKind p = kindNamed("[P]");
        assertEquals('*', p.getCharacter().getNarrowCharacter());
        assertEquals(9, p.getCost());
        assertEquals(8, (int) read(p, "alloc_prob"));
    }

    // ---- the colour: color_char_to_attr and color_text_to_attr --------------------------------

    @ParameterizedTest(name = "colour code {0} is single-character, an unknown one is white")
    @ValueSource(strings = {"!", "%", "?"})
    void unknownSingleCharacterColourIsWhite(String code) {
        one(record("magic book", "town", "[C]", "1", "arcane", "?", code, "1", "1", "1", "5", 1));

        assertEquals(ColourEnum.COLOUR_WHITE, kindNamed("[C]").getCharacter().getAttributeColour());
    }

    @Test
    @DisplayName("a colour longer than one character is matched as a name, without regard to case")
    void colourByName() {
        one(record("magic book", "town", "[N1]", "1", "arcane", "?", "light red", "1", "1", "1", "5", 1));
        one(record("magic book", "town", "[N2]", "1", "arcane", "?", "LIGHT RED", "1", "1", "1", "5", 1));

        assertEquals(ColourEnum.COLOUR_LIGHT_RED, kindNamed("[N1]").getCharacter().getAttributeColour());
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, kindNamed("[N2]").getCharacter().getAttributeColour());
    }

    @Test
    @DisplayName("an unknown colour name is white, not an error")
    void unknownColourNameIsWhite() {
        one(record("magic book", "town", "[N]", "1", "arcane", "?", "nonsense", "1", "1", "1", "5", 1));

        assertEquals(ColourEnum.COLOUR_WHITE, kindNamed("[N]").getCharacter().getAttributeColour());
    }

    @Test
    @DisplayName("the colour codes are case-sensitive: r is Red and R is Light Red")
    void colourCodesAreCaseSensitive() {
        one(record("magic book", "town", "[Lo]", "1", "arcane", "?", "r", "1", "1", "1", "5", 1));
        one(record("magic book", "town", "[Hi]", "1", "arcane", "?", "R", "1", "1", "1", "5", 1));

        assertEquals(ColourEnum.COLOUR_RED, kindNamed("[Lo]").getCharacter().getAttributeColour());
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, kindNamed("[Hi]").getCharacter().getAttributeColour());
    }

    // ---- the glyph: parse_type char -----------------------------------------------------------

    @Test
    @DisplayName("a glyph outside the basic plane is one character, and is kept whole")
    void supplementaryGlyph() {
        one(record("magic book", "town", "[W]", "1", "arcane", "📖", "R", "1", "1", "1", "5", 1));
        ObjectKind kind = kindNamed("[W]");

        assertEquals(0x1F4D6, kind.getCharacter().getWideCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, kind.getCharacter().getAttributeColour());
    }

    @Test
    @DisplayName("a glyph in the basic plane but not ASCII is one character")
    void nonAsciiGlyph() {
        one(record("magic book", "town", "[E]", "1", "arcane", "é", "R", "1", "1", "1", "5", 1));

        assertEquals('é', kindNamed("[E]").getCharacter().getNarrowCharacter());
    }

    @ParameterizedTest(name = "glyph \"{0}\" is more than one character")
    @ValueSource(strings = {"ab", "??", "📖📖"})
    void glyphOfMoreThanOneCharacterStopsTheLoad(String glyph) {
        assertNames(stops(record("magic book", "town", "[X]", "1", "arcane", glyph, "R", "1", "1", "1", "5", 1)),
                "line: " + LINE, "file: " + FILE, "illegal glyph value: " + glyph);
    }

    // ---- the minmax range: parse_class_book_properties ---------------------------------------

    @Test
    @DisplayName("a range written high-to-low (50 to 10): level and alloc_min are 50, alloc_max is 10")
    void reversedRangeKeepsTheFirstNumberAsLevel() throws Exception {
        one(record("magic book", "town", "[R]", "1", "arcane", "?", "R", "1", "1", "50", "10", 1));
        ObjectKind kind = kindNamed("[R]");

        assertEquals(50, (int) read(kind, "level"));
        assertEquals(50, (int) read(kind, "alloc_min"));
        assertEquals(10, (int) read(kind, "alloc_max"));
    }

    @Test
    @DisplayName("equal depths, and zero depths, are accepted")
    void equalAndZeroDepths() throws Exception {
        one(record("magic book", "town", "[E]", "1", "arcane", "?", "R", "1", "1", "20", "20", 1));
        one(record("magic book", "town", "[Z]", "1", "arcane", "?", "R", "1", "1", "0", "0", 1));

        assertEquals(20, (int) read(kindNamed("[E]"), "level"));
        assertEquals(0, (int) read(kindNamed("[Z]"), "level"));
    }

    @Test
    @DisplayName("a negative cost, probability and depth are accepted, as parse_class_book_properties reads plain ints")
    void negativeFigures() throws Exception {
        one(record("magic book", "town", "[N]", "1", "arcane", "?", "R", "-5", "-6", "-7", "-8", 1));
        ObjectKind kind = kindNamed("[N]");

        assertEquals(-5, kind.getCost());
        assertEquals(-6, (int) read(kind, "alloc_prob"));
        assertEquals(-7, (int) read(kind, "level"));
        assertEquals(-8, (int) read(kind, "alloc_max"));
    }

    // ---- the declared spell count: book_max_spells --------------------------------------------

    @Test
    @DisplayName("as many spells as declared is accepted")
    void exactCount() {
        assertEquals(3, one(record("magic book", "town", "[Three]", "3", "arcane", "?", "R",
                "1", "1", "1", "5", 3)).getNumOfSpells());
    }

    @Test
    @DisplayName("fewer spells than declared is accepted, and the book holds the number listed")
    void fewerThanDeclared() {
        MagicBook book = one(record("magic book", "town", "[Short]", "7", "arcane", "?", "R",
                "1", "1", "1", "5", 2));

        assertEquals(2, book.getNumOfSpells());
        assertEquals(2, book.getSpells().size());
    }

    @Test
    @DisplayName("one spell past the declared count stops the load, citing the book: line")
    void oneTooMany() {
        assertNames(stops(record("magic book", "town", "[Over]", "2", "arcane", "?", "R", "1", "1", "1", "5", 3)),
                "line: " + LINE, "file: " + FILE, "too many spells", "3 spells found", "2 declared");
    }

    @Test
    @DisplayName("a declared 0 with no spells is accepted, and with one spell stops the load")
    void declaredZero() {
        assertEquals(0, one(record("magic book", "town", "[None]", "0", "arcane", "?", "R",
                "1", "1", "1", "5", 0)).getNumOfSpells());
        assertNames(stops(record("magic book", "town", "[Some]", "0", "arcane", "?", "R", "1", "1", "1", "5", 1)),
                "too many spells");
    }

    @Test
    @DisplayName("the cap is per book: a full book does not use up the next book's count")
    void capIsPerBook() {
        List<MagicBook> books = run(
                record("magic book", "town", "[A]", "2", "arcane", "?", "R", "1", "1", "1", "5", 2),
                record("magic book", "town", "[B]", "2", "arcane", "?", "R", "1", "1", "1", "5", 2));

        assertEquals(2, books.size());
        assertEquals(2, books.get(0).getNumOfSpells());
        assertEquals(2, books.get(1).getNumOfSpells());
    }

    // ---- several books, and a book that two classes share --------------------------------------

    @Test
    @DisplayName("books come back in file order and take the next sval each, 1 then 2 then 3")
    void booksInFileOrderWithConsecutiveSvals() {
        List<MagicBook> books = run(
                record("magic book", "town", "[A]", "1", "arcane", "?", "R", "1", "1", "1", "5", 1),
                record("magic book", "town", "[B]", "1", "arcane", "?", "R", "1", "1", "1", "5", 1),
                record("magic book", "dungeon", "[C]", "1", "arcane", "?", "R", "1", "1", "1", "5", 1));

        assertEquals(List.of("[A]", "[B]", "[C]"), books.stream().map(MagicBook::getBookName).toList());
        assertEquals(List.of(1, 2, 3), books.stream().map(MagicBook::getSval).toList());
    }

    @Test
    @DisplayName("each base counts its own svals: a prayer book is sval 1 although a magic book is already 1")
    void svalsAreCountedPerBase() {
        List<MagicBook> books = run(
                record("magic book", "town", "[A]", "1", "arcane", "?", "R", "1", "1", "1", "5", 1),
                record("prayer book", "town", "[P]", "1", "divine", "?", "G", "1", "1", "1", "5", 1));

        assertEquals(1, books.get(0).getSval());
        assertEquals(1, books.get(1).getSval());
        assertEquals(TValue.TV_PRAYER_BOOK, books.get(1).getBookTValue());
    }

    @Test
    @DisplayName("a book listed by a second class reuses the first class's kind and shares its sval")
    void sharedBookReusesTheKind() {
        MagicBook first = one(town());
        ObjectKind kind = kindNamed("[First Spells]");

        // the second class lists the same book with neither optional line, as class.txt does
        MagicBook second = one(bare("town", "[First Spells]"));

        assertEquals(1, count("[First Spells]"), "no second kind is built");
        assertEquals(first.getSval(), second.getSval());
        assertEquals(1, kind.getBase().getNumSvals(), "the base's count moves once");
        assertSame(kind, kindNamed("[First Spells]"));
        assertEquals(25, kind.getCost(), "the first listing's figures stand");
        assertEquals('?', kind.getCharacter().getNarrowCharacter());
    }

    @Test
    @DisplayName("a shared book does not take another book's sval, whatever the order the classes list them in")
    void sharedBookKeepsItsOwnSval() {
        run(record("magic book", "town", "[A]", "1", "arcane", "?", "R", "1", "1", "1", "5", 1),
                record("magic book", "town", "[B]", "1", "arcane", "?", "R", "1", "1", "1", "5", 1));

        // a later class lists [B] then [A]
        List<MagicBook> second = run(bare("town", "[B]"), bare("town", "[A]"));

        assertEquals(2, second.get(0).getSval());
        assertEquals(1, second.get(1).getSval());
        assertEquals(2, kindNamed("[A]").getBase().getNumSvals());
    }

    @Test
    @DisplayName("DOCUMENTED DIFFERENCE: a second listing's different graphics and properties are ignored, where C rewrites the kind")
    void secondListingIsIgnored() {
        one(town());
        one(record("magic book", "town", "[First Spells]", "7", "arcane", "#", "b", "99", "98", "97", "96", 2));
        ObjectKind kind = kindNamed("[First Spells]");

        assertEquals(25, kind.getCost());
        assertEquals('?', kind.getCharacter().getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, kind.getCharacter().getAttributeColour());
    }

    @Test
    @DisplayName("a kind already in the table is reused as it stands: not rebuilt, and no dungeon extras added")
    void existingKindIsReused() throws Exception {
        ObjectKind old = new ObjectKind();
        write(old, "name", "[Old Book]");
        write(old, "sValueName", "[Old Book]");
        write(old, "tValue", TValue.TV_MAGIC_BOOK);
        old.setsVal(5);
        addToTable(old);
        int before = ObjectRegistry.getObjectKinds().size();

        MagicBook book = one(record("magic book", "dungeon", "[Old Book]", "1", "arcane",
                "?", "R", "77", "1", "1", "5", 1));

        assertEquals(before, ObjectRegistry.getObjectKinds().size(), "nothing is added");
        assertEquals(5, book.getSval(), "the book takes the kind's own sval");
        assertEquals(0, ObjectRegistry.getBaseFromTVal(TValue.TV_MAGIC_BOOK).getNumSvals(), "the base's count does not move");
        assertEquals(0, old.getCost(), "the properties are not applied to an existing kind");
        assertFalse(old.getKindFlags().has(ObjectKindFlag.KF_GOOD), "write_book_kind returns before the dungeon extras");
    }

    // ---- errors: each one stops the load -------------------------------------------------------

    @Test
    @DisplayName("an unknown tval stops the load, and the message is '<Type> starting at line: N of file: F has ... value: X'")
    void unknownTval() {
        assertEquals("Class spell book starting at line: 30 of file: class.txt has an illegal tValue value: notatval",
                stops(record("notatval", "town", "[X]", "1", "arcane", "?", "R", "1", "1", "1", "5", 1)).getMessage());
    }

    @ParameterizedTest(name = "declared count {0} is negative")
    @ValueSource(strings = {"-1", "-0", "-99999999999"})
    void negativeDeclaredCount(String value) {
        assertNames(stops(record("magic book", "town", "[X]", value, "arcane", "?", "R", "1", "1", "1", "5", 1)),
                "line: " + LINE, "file: " + FILE, "illegal negative number of spells value: " + value);
    }

    @ParameterizedTest(name = "declared count {0} is not a number")
    @ValueSource(strings = {"abc", "+", "x1"})
    void nonNumericDeclaredCount(String value) {
        assertNames(stops(record("magic book", "town", "[X]", value, "arcane", "?", "R", "1", "1", "1", "5", 1)),
                "line: " + LINE, "file: " + FILE, "number of spells");
    }

    @Test
    @DisplayName("the declared count is read as strtoul in base 0: 1x is 1, 0x2 is 2, 010 is 8")
    void declaredCountIsReadInBaseZero() {
        assertEquals(1, one(record("magic book", "town", "[A]", "1x", "arcane", "?", "R", "1", "1", "1", "5", 1)).getNumOfSpells());
        assertEquals(2, one(record("magic book", "town", "[B]", "0x2", "arcane", "?", "R", "1", "1", "1", "5", 2)).getNumOfSpells());
        assertEquals(8, one(record("magic book", "town", "[C]", "010", "arcane", "?", "R", "1", "1", "1", "5", 8)).getNumOfSpells());
        assertNames(stops(record("magic book", "town", "[D]", "1x", "arcane", "?", "R", "1", "1", "1", "5", 2)),
                "too many spells", "2 spells found", "1 declared");
    }

    @Test
    @DisplayName("an unknown realm stops the load, as lookup_realm quits")
    void unknownRealm() {
        assertNames(stops(record("magic book", "town", "[X]", "1", "psionic", "?", "R", "1", "1", "1", "5", 1)),
                "line: " + LINE, "file: " + FILE, "illegal realm value: psionic");
    }

    @ParameterizedTest(name = "{0} that is not a number stops the load")
    @ValueSource(strings = {"cost", "commonness", "minimum", "maximum"})
    void nonNumericFigure(String which) {
        String cost = which.equals("cost") ? "x" : "1";
        String common = which.equals("commonness") ? "x" : "1";
        String min = which.equals("minimum") ? "x" : "1";
        String max = which.equals("maximum") ? "x" : "5";

        assertNames(stops(record("magic book", "town", "[X]", "1", "arcane", "?", "R", cost, common, min, max, 1)),
                "line: " + LINE, "file: " + FILE, which);
    }

    @Test
    @DisplayName("a spell that ClassSpellAssembler refuses stops the load")
    void badSpellStopsTheLoad() {
        ClassSpellBookParseRecord bad = new ClassSpellBookParseRecord("magic book", "town", "[X]", "1", "arcane",
                "?", "R", "1", "1", "1", "5",
                List.of(new ClassSpellParseRecord("Bad", "-1", "1", "22", "", List.of(), "4", LINE + 1)), LINE);

        assertNames(stops(bad), "file: " + FILE, "negative level");
    }

    @Test
    @DisplayName("a tval with no object base stops the load")
    void noObjectBase() throws Exception {
        List<ObjectBase> bases = new ArrayList<>(new ObjectBaseReader().parse(BookRegistryFixture.OBJECT_BASE_FILE));
        bases.removeIf(b -> b.gettVal() == TValue.TV_MAGIC_BOOK);
        set("objectBases", bases);

        assertNames(stops(town()), "line: " + LINE, "file: " + FILE, "illegal object base value: magic book");
    }

    @Test
    @DisplayName("a failed book registers no kind")
    void failedBookRegistersNothing() {
        int before = ObjectRegistry.getObjectKinds().size();

        stops(record("magic book", "town", "[X]", "1", "psionic", "?", "R", "1", "1", "1", "5", 1));
        stops(record("magic book", "town", "[Y]", "1", "arcane", "?", "R", "x", "1", "1", "5", 1));

        assertEquals(before, ObjectRegistry.getObjectKinds().size());
    }

    @Test
    @DisplayName("no object kinds loaded throws IllegalStateException, as object.txt must be read first")
    void noObjectKindsLoaded() throws Exception {
        set("objectKinds", new ArrayList<ObjectKind>());

        assertThrows(IllegalStateException.class, () -> run(town()));
    }

    @Test
    @DisplayName("DOCUMENTED DIFFERENCE: a name registered under another tval gives the book sval -1, where C reuses the kind's sval")
    void nameSharedAcrossTvalsGivesMinusOne() throws Exception {
        ObjectKind old = new ObjectKind();
        write(old, "name", "[Shared Name]");
        write(old, "sValueName", "[Shared Name]");
        write(old, "tValue", TValue.TV_PRAYER_BOOK);
        old.setsVal(4);
        addToTable(old);

        MagicBook book = one(record("magic book", "town", "[Shared Name]", "1", "arcane",
                "?", "R", "1", "1", "1", "5", 1));

        assertEquals(-1, book.getSval());
    }
}
