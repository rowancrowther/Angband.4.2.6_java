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
import uk.co.jackoftradesltd.middle.magic.ClassMagic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ClassMagicAssembler} against {@code parse_class_magic()} in {@code init.c}, the
 * {@code uint} field reading in {@code parser.c}, and the {@code class_max_books} limit that
 * {@code parse_class_book()} applies to each {@code book:} line.
 *
 * <p>C reads {@code first}, {@code weight} and {@code books} as {@code uint} fields with
 * {@code strtoul} in base 0 and refuses a field that starts with {@code -} or has no digit
 * ({@code PARSE_ERROR_NOT_NUMBER}), so {@code -0} is refused as well. Base 0 means {@code 010} is 8,
 * {@code 08} is 0 and {@code 0x10} is 16. The declared book count is the most {@code book:} lines
 * the class may then give ({@code PARSE_ERROR_TOO_MANY_ENTRIES}); fewer is accepted, because the
 * count only sizes the array and {@code num_books} ends at the number read. A declared count of 0
 * leaves the array {@code NULL} ({@code mem_zalloc(0)} returns {@code NULL} in {@code z-virt.c}),
 * so the first {@code book:} line fails the same test.
 *
 * <p>Every one of those errors stops the load in C, so the assembler throws a
 * {@link DataFileParseException} naming the line and the value, and the tests below expect that.
 *
 * <p>The books are built by {@link ClassSpellBookAssembler}, which stops the load on any book it
 * cannot build, so the count check needs good books and the object and realm registries behind
 * them. {@link BookRegistryFixture} seeds those before each test and puts them back afterwards. The
 * books are assembled before the count is checked, so a bad book is reported for its own fault and
 * never reaches the count check, as C quits on the first bad {@code book:} line.
 *
 * <p>One test is marked ACCEPTED DEVIATION: it pins the clamping of a figure past
 * {@code Integer.MAX_VALUE}, where C wraps it, so a change to it fails loudly.
 *
 * <p>Class ClassMagicAssemblerTest coded on 261028, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
class ClassMagicAssemblerTest {

    /** The line every record in these tests claims to start on. */
    private static final int LINE = 12;

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

    private static ClassSpellBookParseRecord goodBook(int index, int line) {
        return new ClassSpellBookParseRecord("magic book", "town", "[B" + index + "]", "1", "arcane",
                "", "", "", "", "", "", List.of(), line);
    }

    private static ClassSpellBookParseRecord badBook(int line) {
        return new ClassSpellBookParseRecord("notabase", "town", "[Bad]", "1", "arcane",
                "", "", "", "", "", "", List.of(), line);
    }

    private static List<ClassSpellBookParseRecord> books(int count) {
        List<ClassSpellBookParseRecord> books = new ArrayList<>();
        for (int i = 0; i < count; i++)
            books.add(goodBook(i, LINE + 1 + i));
        return books;
    }

    private static ClassMagicParseRecord magic(String first, String weight, String numBooks, int bookCount) {
        return new ClassMagicParseRecord(first, weight, numBooks, books(bookCount), LINE);
    }

    private static List<ClassMagic> run(List<String> errors, ClassMagicParseRecord... records) {
        return new ClassMagicAssembler().assemble(List.of(records), errors, FILE);
    }

    private static ClassMagic one(String first, String weight, String numBooks) {
        List<ClassMagic> result = run(new ArrayList<>(), magic(first, weight, numBooks, 0));
        assertEquals(1, result.size());
        return result.getFirst();
    }

    private static DataFileParseException stops(ClassMagicParseRecord record) {
        return assertThrows(DataFileParseException.class, () -> run(new ArrayList<>(), record));
    }

    private static void assertNames(DataFileParseException e, String... parts) {
        for (String part : parts)
            assertTrue(e.getMessage().contains(part), () -> "expected \"" + part + "\" in: " + e.getMessage());
    }

    // ---- the ordinary path --------------------------------------------------------------------

    @Test
    @DisplayName("magic:1:300:3 gives first spell 1 and weight 300, with no books read")
    void ordinaryScalars() {
        ClassMagic cm = one("1", "300", "3");

        assertEquals(1, cm.getSpellFirst());
        assertEquals(300, cm.getSpellWeight());
        assertEquals(0, cm.getNumBooks());
        assertEquals(0, cm.getTotalSpells());
    }

    @Test
    @DisplayName("zero is a valid uint in every field")
    void zeroIsAccepted() {
        ClassMagic cm = one("0", "0", "0");

        assertEquals(0, cm.getSpellFirst());
        assertEquals(0, cm.getSpellWeight());
    }

    @Test
    @DisplayName("an empty record list gives an empty result, which PlayerClassAssembler reads as a non-caster")
    void noRecordsGivesNoResult() {
        assertTrue(new ClassMagicAssembler().assemble(List.of(), new ArrayList<>(), FILE).isEmpty());
    }

    @Test
    @DisplayName("two records give two results in order, each read on its own")
    void recordsAreReadInOrder() {
        List<ClassMagic> result = run(new ArrayList<>(), magic("1", "100", "0", 0), magic("5", "200", "0", 0));

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getSpellFirst());
        assertEquals(200, result.get(1).getSpellWeight());
    }

    // ---- strtoul in base 0 --------------------------------------------------------------------

    @Test
    @DisplayName("base 0: 010 is octal 8, 08 stops at the 8 and reads 0, 0x10 is 16")
    void baseZeroReading() {
        assertEquals(8, one("010", "1", "0").getSpellFirst());
        assertEquals(0, one("08", "1", "0").getSpellFirst());
        assertEquals(16, one("0x10", "1", "0").getSpellFirst());
        assertEquals(8, one("1", "010", "0").getSpellWeight());
        assertEquals(16, one("1", "0X10", "0").getSpellWeight());
    }

    @Test
    @DisplayName("ACCEPTED DEVIATION: a figure past Integer.MAX_VALUE is clamped, where C wraps it into 32 bits")
    void aFigureOutsideIntIsClamped() {
        assertEquals(Integer.MAX_VALUE, one("2147483648", "1", "0").getSpellFirst());
        assertEquals(Integer.MAX_VALUE, one("1", "4294967296", "0").getSpellWeight());
    }

    // ---- a leading minus: PARSE_ERROR_NOT_NUMBER ----------------------------------------------

    @ParameterizedTest(name = "first spell {0} stops the load")
    @ValueSource(strings = {"-1", "-0", "-2147483648", "-99999999999"})
    void negativeFirstSpell(String value) {
        assertNames(stops(magic(value, "1", "1", 0)), "line: " + LINE, "file: " + FILE,
                "illegal negative first spell value: " + value);
    }

    @ParameterizedTest(name = "spell weight {0} stops the load")
    @ValueSource(strings = {"-1", "-0", "-2147483648", "-99999999999"})
    void negativeSpellWeight(String value) {
        assertNames(stops(magic("1", value, "1", 0)), "line: " + LINE, "file: " + FILE,
                "illegal negative spell weight value: " + value);
    }

    @ParameterizedTest(name = "book count {0} stops the load")
    @ValueSource(strings = {"-1", "-0", "-2147483648", "-99999999999"})
    void negativeNumberOfBooks(String value) {
        assertNames(stops(magic("1", "1", value, 0)), "line: " + LINE, "file: " + FILE,
                "illegal negative number of books value: " + value);
    }

    @Test
    @DisplayName("the whole message is '<Type> starting at line: N of file: F has ... value: X'")
    void messageShape() {
        assertEquals("Class magic starting at line: 12 of file: class.txt has an illegal negative first spell value: -1",
                stops(magic("-1", "1", "1", 0)).getMessage());
    }

    // ---- no digit at all: z == tok ------------------------------------------------------------

    @ParameterizedTest(name = "first spell {0} has no number")
    @ValueSource(strings = {"abc", "+", "+-5"})
    void nonNumberFirstSpell(String value) {
        assertNames(stops(magic(value, "1", "1", 0)), "line: " + LINE, "file: " + FILE, "first spell");
    }

    @ParameterizedTest(name = "spell weight {0} has no number")
    @ValueSource(strings = {"abc", "+", "+-5"})
    void nonNumberSpellWeight(String value) {
        assertNames(stops(magic("1", value, "1", 0)), "line: " + LINE, "file: " + FILE, "spell weight");
    }

    @ParameterizedTest(name = "book count {0} has no number")
    @ValueSource(strings = {"abc", "+", "+-5"})
    void nonNumberOfBooks(String value) {
        assertNames(stops(magic("1", "1", value, 0)), "line: " + LINE, "file: " + FILE, "number of books");
    }

    // ---- the fields are read in the order C reads them ----------------------------------------

    @Test
    @DisplayName("with all three fields bad, the first spell is the one reported")
    void firstSpellReportedFirst() {
        assertNames(stops(magic("-1", "-2", "-3", 0)), "first spell value: -1");
    }

    @Test
    @DisplayName("a bad first spell wins over a bad spell weight, whichever kind of bad each is")
    void firstSpellNotNumberBeatsNegativeWeight() {
        assertNames(stops(magic("abc", "-2", "1", 0)), "first spell");
    }

    @Test
    @DisplayName("a bad spell weight wins over a bad book count")
    void spellWeightReportedBeforeBookCount() {
        assertNames(stops(magic("1", "-2", "-3", 0)), "spell weight value: -2");
    }

    // ---- the declared book count: class_max_books ---------------------------------------------

    @Test
    @DisplayName("as many books as declared is the ordinary case, and the book is kept")
    void declaredCountMatching() {
        List<String> errors = new ArrayList<>();

        List<ClassMagic> result = run(errors, magic("1", "100", "1", 1));

        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getNumBooks());
        assertTrue(errors.isEmpty(), errors::toString);
    }

    @Test
    @DisplayName("fewer books than declared is accepted, as in C where the count only sizes the array")
    void fewerBooksThanDeclared() {
        assertEquals(1, run(new ArrayList<>(), magic("1", "100", "5", 1)).getFirst().getNumBooks());
        assertEquals(0, run(new ArrayList<>(), magic("1", "100", "5", 0)).getFirst().getNumBooks());
    }

    @Test
    @DisplayName("one book more than declared stops the load, as parse_class_book does")
    void oneBookTooMany() {
        assertNames(stops(magic("1", "100", "1", 2)), "line: " + LINE, "file: " + FILE,
                "one or more illegal book entries");
    }

    @Test
    @DisplayName("a declared count of 0 refuses the first book, since C's zero-length array is NULL")
    void zeroDeclaredRefusesEveryBook() {
        assertNames(stops(magic("1", "100", "0", 1)), "one or more illegal book entries");
    }

    @Test
    @DisplayName("three books against a declared two stop the load, and against a declared three do not")
    void everyBookCountsTowardTheLimit() {
        assertNames(stops(magic("1", "100", "2", 3)), "one or more illegal book entries");
        assertDoesNotThrow(() -> run(new ArrayList<>(), magic("1", "100", "3", 3)));
    }

    @Test
    @DisplayName("a bad book is reported for its own fault and wins over the count, as C quits on its book: line first")
    void aBadBookIsReportedBeforeTheCount() {
        List<ClassSpellBookParseRecord> books = new ArrayList<>(books(2));
        books.add(badBook(LINE + 3));

        DataFileParseException e = stops(new ClassMagicParseRecord("1", "100", "2", books, LINE));

        assertNames(e, "line: " + (LINE + 3), "file: " + FILE, "illegal tValue value: notabase");
        assertFalse(e.getMessage().contains("one or more illegal book entries"), e::getMessage);
    }

    @Test
    @DisplayName("a negative book count is reported as that, not as a surplus of books")
    void negativeCountBeatsSurplus() {
        assertNames(stops(magic("1", "100", "-1", 2)), "illegal negative number of books value: -1");
    }

    @Test
    @DisplayName("the scalar errors never touch the errors list")
    void scalarFaultsAreThrownNotAppended() {
        List<String> errors = new ArrayList<>();

        assertThrows(DataFileParseException.class, () -> run(errors, magic("-1", "1", "1", 0)));

        assertTrue(errors.isEmpty(), errors::toString);
    }
}
