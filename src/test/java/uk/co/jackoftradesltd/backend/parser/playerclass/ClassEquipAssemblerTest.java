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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.StartItem;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the quantity and ordering rules of {@link ClassEquipAssembler} against
 * {@code parse_class_equip()} in {@code init.c} and the {@code uint} field parsing in
 * {@code parser.c}.
 *
 * <p>C reads {@code min} and {@code max} as {@code uint} fields: a leading {@code -} is
 * {@code PARSE_ERROR_NOT_NUMBER}, a missing field is {@code PARSE_ERROR_MISSING_FIELD} (an empty
 * token is never produced, because {@code strtok} skips repeated colons), and either value above
 * 99 is {@code PARSE_ERROR_INVALID_ITEM_NUMBER}. {@code min > max} is not checked. Each entry is
 * pushed on the head of the class's chain, so the list C walks is in reverse file order.
 * An {@code eopts} token that is not a birth option makes {@code parse_class_equip()} return
 * {@code PARSE_ERROR_INVALID_OPTION} before the entry is built.
 *
 * <p>Every one of those errors is fatal in C: the parser reports it and the whole load stops, so a
 * bad line never leaves its neighbours behind. The assembler throws a
 * {@link DataFileParseException} naming the line and the value, and the tests below expect that and
 * not a dropped entry.
 *
 * <p>The object-kind registry is seeded with two kinds by reflection and restored afterwards.
 *
 * <p>From 261028 it also covers the tval forms, the numeric and named subtype forms read by
 * {@code lookup_sval()} in {@code obj-util.c}, and the {@code strtok(" |")} tokenising of
 * {@code eopts}. Two tests are marked ACCEPTED DEVIATION: they pin the numeric-subtype behaviour
 * that differs from C by decision, so a change to it fails loudly.
 *
 * <p>Class ClassEquipAssemblerTest coded on 261009, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
class ClassEquipAssemblerTest {

    private Object savedKinds;

    private static Field registryField(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static ObjectKind kind(TValue tValue, String name) throws Exception {
        ObjectKind k = new ObjectKind();
        Field t = ObjectKind.class.getDeclaredField("tValue");
        t.setAccessible(true);
        t.set(k, tValue);
        Field s = ObjectKind.class.getDeclaredField("sValueName");
        s.setAccessible(true);
        s.set(k, name);
        return k;
    }

    @BeforeEach
    void seedKinds() throws Exception {
        Field f = registryField("objectKinds");
        savedKinds = f.get(null);
        List<ObjectKind> kinds = new ArrayList<>();
        kinds.add(kind(TValue.TV_FOOD, "Ration of Food"));
        kinds.add(kind(TValue.TV_LIGHT, "Wooden Torch"));
        f.set(null, kinds);
    }

    @AfterEach
    void restoreKinds() throws Exception {
        registryField("objectKinds").set(null, savedKinds);
    }

    private static ClassEquipParseRecord food(String min, String max) {
        return new ClassEquipParseRecord("food", "Ration of Food", min, max, "none", 1);
    }

    private static DataFileParseException stops(ClassEquipParseRecord... records) {
        return assertThrows(DataFileParseException.class, () -> run(new ArrayList<>(), records));
    }

    private static void assertNames(DataFileParseException e, String... parts) {
        for (String part : parts)
            assertTrue(e.getMessage().contains(part), () -> "expected \"" + part + "\" in: " + e.getMessage());
    }

    private static List<StartItem> run(List<String> errors, ClassEquipParseRecord... records) {
        return new ClassEquipAssembler().assemble(List.of(records), errors, "test.txt");
    }

    @Test
    @DisplayName("1:3 and the C maximum 99:99 are accepted unchanged")
    void validRanges() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("1", "3"), food("99", "99"));

        assertTrue(errors.isEmpty(), errors.toString());
        assertEquals(2, items.size());
        assertEquals(99, items.get(0).getMin());
        assertEquals(99, items.get(0).getMax());
        assertEquals(1, items.get(1).getMin());
        assertEquals(3, items.get(1).getMax());
    }

    @Test
    @DisplayName("min 100 stops the load: C tests min > 99 as well as max > 99")
    void minAboveNinetyNineRejected() {
        assertNames(stops(food("100", "3")), "line: 1", "out of range minimum value: 100");
    }

    @Test
    @DisplayName("max 100 stops the load")
    void maxAboveNinetyNineRejected() {
        assertNames(stops(food("1", "100")), "line: 1", "out of range maximum value: 100");
    }

    @Test
    @DisplayName("min -1 stops the load: a uint field cannot start with '-'")
    void negativeMinRejected() {
        assertNames(stops(food("-1", "3")), "line: 1", "negative minimum value: -1");
    }

    @Test
    @DisplayName("max -1 stops the load: a uint field cannot start with '-'")
    void negativeMaxRejected() {
        assertNames(stops(food("1", "-1")), "line: 1", "negative maximum value: -1");
    }

    @Test
    @DisplayName("an empty min stops the load, never a 0")
    void emptyMinRejected() {
        assertNames(stops(food("", "3")), "line: 1", "blank minimum value");
    }

    @Test
    @DisplayName("an empty max stops the load, never a 0")
    void emptyMaxRejected() {
        assertNames(stops(food("1", "")), "line: 1", "blank maximum value");
    }

    @Test
    @DisplayName("min 0 and min above max are not C errors, so both are kept")
    void zeroAndInvertedRangesKept() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("0", "0"), food("3", "1"));

        assertTrue(errors.isEmpty(), errors.toString());
        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("an unknown sval stops the load at its own line, even after a good entry")
    void unknownSvalDropped() {
        assertNames(stops(food("1", "3"),
                        new ClassEquipParseRecord("food", "No Such Thing", "1", "1", "none", 2)),
                "line: 2", "illegal sValue", "No Such Thing");
    }

    @Test
    @DisplayName("items come back in reverse file order, as C's head-insertion chain is walked")
    void reverseFileOrder() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors,
                food("1", "3"),
                new ClassEquipParseRecord("light", "Wooden Torch", "1", "3", "none", 2));

        assertEquals(2, items.size());
        assertEquals(TValue.TV_LIGHT, items.get(0).gettValue());
        assertEquals(TValue.TV_FOOD, items.get(1).gettValue());
    }

    private static ClassEquipParseRecord withEopts(String eopts) {
        return new ClassEquipParseRecord("food", "Ration of Food", "1", "3", eopts, 1);
    }

    @Test
    @DisplayName("a birth option and a NOT- birth option are both kept, in order")
    void validEoptsKept() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, withEopts("birth_no_recall | NOT-birth_start_kit"));

        assertTrue(errors.isEmpty(), errors.toString());
        assertEquals(1, items.size());
        assertEquals(2, items.get(0).geteOpts().size());
        assertEquals(PlayerOptionEnum.OP_birth_no_recall, items.get(0).geteOpts().get(0).option());
        assertFalse(items.get(0).geteOpts().get(0).negated());
        assertEquals(PlayerOptionEnum.OP_birth_start_kit, items.get(0).geteOpts().get(1).option());
        assertTrue(items.get(0).geteOpts().get(1).negated());
    }

    @Test
    @DisplayName("an unknown option stops the load, as C returns INVALID_OPTION")
    void unknownEoptDropsEntry() {
        assertNames(stops(withEopts("notanoption")), "line: 1", "illegal birth option value: notanoption");
    }

    @Test
    @DisplayName("a non-birth option (use_sound) stops the load")
    void nonBirthEoptDropsEntry() {
        assertNames(stops(withEopts("use_sound")), "line: 1", "illegal non-birth option value: OP_use_sound");
    }

    @Test
    @DisplayName("one bad option among valid ones still stops the load")
    void oneBadEoptAmongValidDropsEntry() {
        assertNames(stops(withEopts("birth_no_recall notanoption")),
                "illegal birth option value: notanoption");
    }

    @Test
    @DisplayName("a bad NOT- option stops the load, and the message keeps the NOT- prefix")
    void badNegatedEoptDropsOnlyThatEntry() {
        assertNames(stops(food("1", "3"), withEopts("NOT-notanoption")),
                "illegal birth option value: NOT-notanoption");
    }

    private static ClassEquipParseRecord withTval(String tVal, String sVal) {
        return new ClassEquipParseRecord(tVal, sVal, "1", "1", "none", 1);
    }

    private static StartItem only(ClassEquipParseRecord record) {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, record);
        assertTrue(errors.isEmpty(), errors.toString());
        assertEquals(1, items.size());
        return items.get(0);
    }

    @Test
    @DisplayName("a tval name is matched case-insensitively, as tval_find_idx() uses my_stricmp")
    void tvalMatchedIgnoringCase() {
        assertEquals(TValue.TV_FOOD, only(withTval("FOOD", "Ration of Food")).gettValue());
    }

    @Test
    @DisplayName("a numeric tval is its list-tvals.h index: 9 is sword, 16 is soft armor")
    void numericTval() {
        assertEquals(TValue.TV_SWORD, only(withTval("9", "1")).gettValue());
        assertEquals(TValue.TV_SOFT_ARMOR, only(withTval("16", "1")).gettValue());
    }

    @Test
    @DisplayName("de_armour() rewrites 'soft armour' to 'soft armor' before the name match")
    void britishArmourSpellingResolves() {
        assertEquals(TValue.TV_SOFT_ARMOR, only(withTval("soft armour", "1")).gettValue());
    }

    @Test
    @DisplayName("an unknown tval stops the load: PARSE_ERROR_UNRECOGNISED_TVAL")
    void unknownTvalRejected() {
        assertNames(stops(withTval("nosuchtval", "1")), "line: 1", "illegal tValue value: nosuchtval");
    }

    @Test
    @DisplayName("an out-of-range numeric tval stops the load: C needs r < TV_MAX")
    void outOfRangeNumericTvalRejected() {
        assertNames(stops(withTval("999", "1")), "illegal tValue value: 999");
    }

    @Test
    @DisplayName("a numeric sval is not checked against the kinds: lookup_sval() returns the number")
    void numericSvalNeedsNoKind() {
        assertEquals("99", only(withTval("food", "99")).getsValue());
    }

    @Test
    @DisplayName("a leading '+', leading whitespace and trailing spaces or tabs are all read as the number")
    void numericSvalToleratesSignAndSpace() {
        assertEquals("5", only(withTval("food", "+5")).getsValue());
        assertEquals("5", only(withTval("food", "  5 \t")).getsValue());
    }

    @Test
    @DisplayName("digits followed by anything but spaces or tabs give -1, so the line is refused")
    void numericSvalWithTrailingJunkRejected() {
        assertNames(stops(withTval("food", "5x")), "illegal sValue", "5x");
        assertNames(stops(withTval("food", "5\n")), "illegal sValue");
    }

    @Test
    @DisplayName("lookup_sval() reads base 10, so 0x10 is the digit 0 followed by junk and is refused")
    void hexSvalRejected() {
        assertNames(stops(withTval("food", "0x10")), "illegal sValue", "0x10");
    }

    @Test
    @DisplayName("a negative numeric sval is refused: strtoul wraps it above INT_MAX")
    void negativeSvalRejected() {
        assertNames(stops(withTval("food", "-5")), "negative sValue number: -5");
    }

    @Test
    @DisplayName("a lone sign reads no digits, so it is looked up as a name and refused")
    void loneSignSvalRejected() {
        assertNames(stops(withTval("food", "+")), "illegal sValue");
        assertNames(stops(withTval("food", "-")), "negative sValue number");
    }

    @Test
    @DisplayName("a signed name is refused: strtoul reads nothing from '+Ration', and no kind has that name")
    void signedNameRejected() {
        assertNames(stops(withTval("food", "+Ration of Food")), "illegal sValue");
    }

    @Test
    @DisplayName("an sval name is matched case-insensitively, as lookup_sval() uses my_stricmp")
    void svalNameMatchedIgnoringCase() {
        only(withTval("food", "ration of food"));
    }

    @Test
    @DisplayName("an sval name belongs to its tval: a torch is not food (kind->tval != tval)")
    void svalNameOfAnotherTvalRejected() {
        assertNames(stops(withTval("food", "Wooden Torch")), "illegal sValue", "Wooden Torch");
    }

    @Test
    @DisplayName("ACCEPTED DEVIATION 261028: a numeric sval of INT_MAX or more is kept, where C refuses it")
    void hugeNumericSvalAcceptedDeviation() {
        assertEquals("2147483647", only(withTval("food", "2147483647")).getsValue());
    }

    @Test
    @DisplayName("ACCEPTED DEVIATION 261028: '-0' is refused, where C reads it as sval 0")
    void negativeZeroSvalRefusedDeviation() {
        assertNames(stops(withTval("food", "-0")), "negative sValue number: -0");
    }

    @Test
    @DisplayName("'none' and 'NOT-none' name no option and give an empty exclusion list")
    void noneEoptsGiveEmptyList() {
        assertTrue(only(withEopts("none")).geteOpts().isEmpty());
        assertTrue(only(withEopts("NOT-none")).geteOpts().isEmpty());
    }

    @Test
    @DisplayName("'none' beside a real option is skipped and the option kept")
    void noneBesideRealOption() {
        List<StartItem> items = run(new ArrayList<>(), withEopts("none birth_no_recall"));

        assertEquals(1, items.get(0).geteOpts().size());
        assertEquals(PlayerOptionEnum.OP_birth_no_recall, items.get(0).geteOpts().get(0).option());
    }

    @Test
    @DisplayName("'|' separates options as well as a space, so two options need no spaces round it")
    void pipeSeparatesOptions() {
        assertEquals(2, only(withEopts("birth_no_recall|NOT-birth_start_kit")).geteOpts().size());
    }

    @Test
    @DisplayName("strtok(\" |\") does not split on a tab, so two options joined by one are a bad token")
    void tabIsNotASeparator() {
        assertNames(stops(withEopts("birth_no_recall\tbirth_start_kit")), "illegal birth option value");
    }

    @Test
    @DisplayName("a bare 'NOT-' leaves an empty name, which is no option, and stops the load")
    void bareNotPrefixRejected() {
        assertNames(stops(withEopts("NOT-")), "illegal birth option value: NOT-");
    }

    @Test
    @DisplayName("'NOT-NOT-none' strips one prefix only, leaving 'NOT-none', which is no option")
    void doubledNotPrefixRejected() {
        assertNames(stops(withEopts("NOT-NOT-none")), "illegal birth option value");
    }

    @Test
    @DisplayName("a negated non-birth option stops the load as a non-birth option does")
    void negatedNonBirthEoptRejected() {
        assertNames(stops(withEopts("NOT-use_sound")), "illegal non-birth option value: OP_use_sound");
    }

    @Test
    @DisplayName("an option name is case-sensitive: streq() in lookup_option(), not stricmp")
    void optionNameIsCaseSensitive() {
        assertNames(stops(withEopts("BIRTH_NO_RECALL")), "illegal birth option value: BIRTH_NO_RECALL");
    }
}
