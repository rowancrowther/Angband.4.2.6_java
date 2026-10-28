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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftradesltd.middle.magic.MagicSpell;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ClassSpellAssembler} against {@code parse_class_spell()} and
 * {@code parse_class_desc()} in {@code init.c}, and the {@code int} field reading in
 * {@code parser.c}.
 *
 * <p>C registers the line as {@code spell sym name int level int mana int fail int exp}, so the
 * four figures are plain {@code int} fields read with {@code strtol} in base 0, in the order
 * level, mana, fail, exp. There is no upper bound on any of them, and none of them is refused for
 * being negative. The expected figures for the ordinary path are the first spells of the mage
 * book in the shipped {@code class.txt} ({@code spell:Magic Missile:1:1:22:4},
 * {@code spell:Light Room:1:2:26:4}). The description is the {@code desc:} lines appended with no
 * separator, which {@code PlayerClassGrammar.g4} does before this class sees them.
 *
 * <p>Every refusal stops the load in C, so the assembler throws a {@link DataFileParseException}
 * naming the line and the value, and the tests below expect that.
 *
 * <p>Three tests are marked ACCEPTED DEVIATION. They pin behaviour that differs from C by
 * decision, so a change to it fails loudly: the refusal of a negative figure (decided 261028),
 * the order in which the four figures are checked (decided 261028), and the clamping of a figure
 * past {@code Integer.MAX_VALUE}, where C wraps it.
 *
 * <p>The spell members are private and the class has no getters for them yet, so they are read
 * by reflection, as {@code MagicSpellTest} does.
 *
 * <p>Class ClassSpellAssemblerTest coded on 261028, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
class ClassSpellAssemblerTest {

    /** The line every record in these tests claims to start on. */
    private static final int LINE = 211;

    /** The file name every message is expected to carry. */
    private static final String FILE = "class.txt";

    private static Object read(MagicSpell spell, String name) throws ReflectiveOperationException {
        Field f = MagicSpell.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(spell);
    }

    /** One {@code spell:} record in the file's own order (level, mana, fail, exp), no effects. */
    private static ClassSpellParseRecord spell(String level, String mana, String fail, String exp) {
        return new ClassSpellParseRecord("Magic Missile", level, mana, fail, "", List.of(), exp, LINE);
    }

    private static ClassSpellParseRecord withEffects(String name, String desc, EffectParseRecord... effects) {
        return new ClassSpellParseRecord(name, "1", "1", "22", desc, List.of(effects), "4", LINE);
    }

    private static EffectParseRecord effect(String type, int line) {
        return new EffectParseRecord(type, "", "", "", "", "", "", "", "", "", "", line);
    }

    private static List<MagicSpell> run(List<String> errors, ClassSpellParseRecord... records) {
        return new ClassSpellAssembler().assemble(List.of(records), errors, FILE);
    }

    private static MagicSpell one(String level, String mana, String fail, String exp) {
        List<MagicSpell> result = run(new ArrayList<>(), spell(level, mana, fail, exp));
        assertEquals(1, result.size());
        return result.getFirst();
    }

    private static DataFileParseException stops(ClassSpellParseRecord... records) {
        return assertThrows(DataFileParseException.class, () -> run(new ArrayList<>(), records));
    }

    private static void assertNames(DataFileParseException e, String... parts) {
        for (String part : parts)
            assertTrue(e.getMessage().contains(part), () -> "expected \"" + part + "\" in: " + e.getMessage());
    }

    private static void assertFigures(MagicSpell s, int level, int mana, int fail, int exp)
            throws ReflectiveOperationException {
        assertEquals(level, read(s, "level"), "level");
        assertEquals(mana, read(s, "mana"), "mana");
        assertEquals(fail, read(s, "fail"), "fail");
        assertEquals(exp, read(s, "exp"), "exp");
    }

    // ---- the ordinary path --------------------------------------------------------------------

    @Test
    @DisplayName("spell:Magic Missile:1:1:22:4 gives level 1, mana 1, fail 22, exp 4")
    void ordinaryFigures() throws ReflectiveOperationException {
        MagicSpell s = one("1", "1", "22", "4");

        assertEquals("Magic Missile", s.getSpellName());
        assertFigures(s, 1, 1, 22, 4);
    }

    @Test
    @DisplayName("the file's level:mana:fail:exp order reaches the matching members (Light Room 1:2:26:4)")
    void fileOrderReachesTheRightMembers() throws ReflectiveOperationException {
        assertFigures(one("1", "2", "26", "4"), 1, 2, 26, 4);
    }

    @Test
    @DisplayName("zero is a valid figure in every field")
    void zeroIsAccepted() throws ReflectiveOperationException {
        assertFigures(one("0", "0", "0", "0"), 0, 0, 0, 0);
    }

    @Test
    @DisplayName("there is no upper bound, as in C: a failure rate of 150 and a level of 100 load")
    void noUpperBound() throws ReflectiveOperationException {
        assertFigures(one("100", "255", "150", "9999"), 100, 255, 150, 9999);
    }

    @Test
    @DisplayName("an empty record list gives an empty result")
    void noRecordsGivesNoResult() {
        assertTrue(new ClassSpellAssembler().assemble(List.of(), new ArrayList<>(), FILE).isEmpty());
    }

    @Test
    @DisplayName("records give spells in file order, each read on its own")
    void recordsAreReadInOrder() throws ReflectiveOperationException {
        List<MagicSpell> result = run(new ArrayList<>(),
                new ClassSpellParseRecord("Magic Missile", "1", "1", "22", "", List.of(), "4", 211),
                new ClassSpellParseRecord("Light Room", "1", "2", "26", "", List.of(), "4", 221),
                new ClassSpellParseRecord("Phase Door", "2", "2", "22", "", List.of(), "4", 238));

        assertEquals(3, result.size());
        assertEquals("Magic Missile", result.get(0).getSpellName());
        assertEquals("Light Room", result.get(1).getSpellName());
        assertEquals("Phase Door", result.get(2).getSpellName());
        assertFigures(result.get(1), 1, 2, 26, 4);
        assertFigures(result.get(2), 2, 2, 22, 4);
    }

    @Test
    @DisplayName("the realm is left null for the book assembler to set, as the indices are never stored")
    void realmIsNotSet() throws ReflectiveOperationException {
        assertNull(read(one("1", "1", "22", "4"), "realm"));
    }

    // ---- strtol in base 0 ---------------------------------------------------------------------

    @Test
    @DisplayName("base 0: 010 is octal 8, 08 stops at the 8 and reads 0, 0x10 is 16, +5 is 5")
    void baseZeroReading() throws ReflectiveOperationException {
        assertFigures(one("010", "08", "0x10", "+5"), 8, 0, 16, 5);
    }

    @Test
    @DisplayName("ACCEPTED DEVIATION: a figure past Integer.MAX_VALUE is clamped, where C wraps it into 32 bits")
    void aFigureOutsideIntIsClamped() throws ReflectiveOperationException {
        assertFigures(one("2147483648", "4294967296", "2147483647", "99999999999"),
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    // ---- name, description, effects -----------------------------------------------------------

    @Test
    @DisplayName("the name is kept as written, commas, ampersand and spaces included")
    void nameIsKeptVerbatim() {
        List<MagicSpell> result = run(new ArrayList<>(), withEffects("Find Traps, Doors & Stairs", ""));

        assertEquals("Find Traps, Doors & Stairs", result.getFirst().getSpellName());
    }

    @Test
    @DisplayName("the description is passed on unchanged, and a spell with no desc: line has an empty one")
    void descriptionIsPassedOn() throws ReflectiveOperationException {
        String text = "Fires a magic missile that always hits its target and does" +
                " unresistable damage.";
        List<MagicSpell> result = run(new ArrayList<>(),
                withEffects("Magic Missile", text), withEffects("Light Room", ""));

        assertEquals(text, read(result.get(0), "spellDescription"));
        assertEquals("", read(result.get(1), "spellDescription"));
    }

    @Test
    @DisplayName("a spell with no effect blocks has an empty effect list")
    void noEffects() throws ReflectiveOperationException {
        List<MagicSpell> result = run(new ArrayList<>(), withEffects("Magic Missile", ""));

        assertEquals(List.of(), read(result.getFirst(), "effects"));
    }

    @Test
    @DisplayName("each effect block becomes one effect, in file order, and the errors list stays empty")
    void effectsAreResolved() throws ReflectiveOperationException {
        List<String> errors = new ArrayList<>();
        List<MagicSpell> result = run(errors,
                withEffects("Light Room", "", effect("LIGHT_AREA", 222), effect("NOURISH", 223)));

        List<?> effects = (List<?>) read(result.getFirst(), "effects");
        assertEquals(2, effects.size());
        assertTrue(errors.isEmpty(), errors.toString());
    }

    @Test
    @DisplayName("an effect name that is not an effect stops the load, naming the effect's own line")
    void unknownEffectStopsTheLoad() {
        assertNames(stops(withEffects("Magic Missile", "", effect("NO_SUCH_EFFECT", 212))),
                "line: 212", "file: " + FILE, "illegal element value: NO_SUCH_EFFECT");
    }

    // ---- a leading minus: ACCEPTED DEVIATION --------------------------------------------------

    @ParameterizedTest(name = "level {0} stops the load")
    @ValueSource(strings = {"-1", "-0", "-2147483648", "-99999999999"})
    @DisplayName("ACCEPTED DEVIATION: a negative level is refused, where C stores it")
    void negativeLevel(String value) {
        assertNames(stops(spell(value, "1", "22", "4")), "line: " + LINE, "file: " + FILE,
                "illegal negative level value: " + value);
    }

    @ParameterizedTest(name = "mana {0} stops the load")
    @ValueSource(strings = {"-1", "-0", "-2147483648", "-99999999999"})
    @DisplayName("ACCEPTED DEVIATION: a negative mana cost is refused, where C stores it")
    void negativeMana(String value) {
        assertNames(stops(spell("1", value, "22", "4")), "line: " + LINE, "file: " + FILE,
                "illegal negative mana value: " + value);
    }

    @ParameterizedTest(name = "fail {0} stops the load")
    @ValueSource(strings = {"-1", "-0", "-2147483648", "-99999999999"})
    @DisplayName("ACCEPTED DEVIATION: a negative failure rate is refused, where C stores it")
    void negativeFail(String value) {
        assertNames(stops(spell("1", "1", value, "4")), "line: " + LINE, "file: " + FILE,
                "illegal negative fail value: " + value);
    }

    @ParameterizedTest(name = "exp {0} stops the load")
    @ValueSource(strings = {"-1", "-0", "-2147483648", "-99999999999"})
    @DisplayName("ACCEPTED DEVIATION: a negative experience figure is refused, where C stores it")
    void negativeExp(String value) {
        assertNames(stops(spell("1", "1", "22", value)), "line: " + LINE, "file: " + FILE,
                "illegal negative experience value: " + value);
    }

    @Test
    @DisplayName("the whole message is '<Type> starting at line: N of file: F has ... value: X'")
    void messageShape() {
        assertEquals("Class spell starting at line: 211 of file: class.txt has an illegal negative level value: -1",
                stops(spell("-1", "1", "22", "4")).getMessage());
    }

    // ---- the order the figures are checked: ACCEPTED DEVIATION --------------------------------

    @Test
    @DisplayName("ACCEPTED DEVIATION: with mana and fail both bad, fail is reported, where C reads mana first")
    void failIsCheckedBeforeMana() {
        assertNames(stops(spell("1", "-1", "-1", "4")), "negative fail value");
    }

    @Test
    @DisplayName("level is checked before the others, and exp last")
    void levelFirstExpLast() {
        assertNames(stops(spell("-1", "-1", "-1", "-1")), "negative level value");
        assertNames(stops(spell("1", "1", "1", "-1")), "negative experience value");
    }

    // ---- no digit at all: z == tok ------------------------------------------------------------

    @ParameterizedTest(name = "level {0} has no number")
    @ValueSource(strings = {"abc", "+", "x1"})
    void nonNumberLevel(String value) {
        assertNames(stops(spell(value, "1", "22", "4")), "line: " + LINE, "file: " + FILE, "level");
    }

    @ParameterizedTest(name = "mana {0} has no number")
    @ValueSource(strings = {"abc", "+", "x1"})
    void nonNumberMana(String value) {
        assertNames(stops(spell("1", value, "22", "4")), "line: " + LINE, "file: " + FILE, "mana");
    }

    @ParameterizedTest(name = "fail {0} has no number")
    @ValueSource(strings = {"abc", "+", "x1"})
    void nonNumberFail(String value) {
        assertNames(stops(spell("1", "1", value, "4")), "line: " + LINE, "file: " + FILE, "fail");
    }

    @ParameterizedTest(name = "exp {0} has no number")
    @ValueSource(strings = {"abc", "+", "x1"})
    void nonNumberExp(String value) {
        assertNames(stops(spell("1", "1", "22", value)), "line: " + LINE, "file: " + FILE, "experience");
    }

    // ---- the load stops, it does not skip ------------------------------------------------------

    @Test
    @DisplayName("a bad figure in a later record stops the whole load: no spell is skipped and kept past")
    void aBadRecordStopsTheLoad() {
        ClassSpellParseRecord good = spell("1", "1", "22", "4");
        ClassSpellParseRecord bad = new ClassSpellParseRecord("Broken", "1", "x", "22", "", List.of(), "4", 221);

        assertNames(stops(good, bad, good), "line: 221", "mana");
    }

    @Test
    @DisplayName("the figures are read before the effects: a bad figure wins over a bad effect")
    void figuresBeforeEffects() {
        ClassSpellParseRecord both = new ClassSpellParseRecord("Both", "-1", "1", "22", "",
                List.of(effect("NO_SUCH_EFFECT", 212)), "4", LINE);

        assertNames(stops(both), "negative level value");
    }
}
