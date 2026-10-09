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
 * {@code PARSE_ERROR_INVALID_OPTION} before the entry is built, so the whole line is dropped.
 *
 * <p>The object-kind registry is seeded with two kinds by reflection and restored afterwards.
 *
 * <p>Class ClassEquipAssemblerTest coded on 261009, commented in full on 261009.
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

    private static List<StartItem> run(List<String> errors, ClassEquipParseRecord... records) {
        return new ClassEquipAssembler().assemble(List.of(records), errors);
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
    @DisplayName("min 100 is rejected: C tests min > 99 as well as max > 99")
    void minAboveNinetyNineRejected() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("100", "3"));

        assertEquals(0, items.size());
        assertEquals(1, errors.size());
    }

    @Test
    @DisplayName("max 100 is rejected")
    void maxAboveNinetyNineRejected() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("1", "100"));

        assertEquals(0, items.size());
        assertEquals(1, errors.size());
    }

    @Test
    @DisplayName("min -1 is rejected: a uint field cannot start with '-'")
    void negativeMinRejected() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("-1", "3"));

        assertEquals(0, items.size());
        assertEquals(1, errors.size());
    }

    @Test
    @DisplayName("max -1 is rejected: a uint field cannot start with '-'")
    void negativeMaxRejected() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("1", "-1"));

        assertEquals(0, items.size());
        assertEquals(1, errors.size());
    }

    @Test
    @DisplayName("an empty min is an error and drops the entry, never a 0")
    void emptyMinRejected() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("", "3"));

        assertEquals(0, items.size());
        assertEquals(1, errors.size());
    }

    @Test
    @DisplayName("an empty max is an error and drops the entry, never a 0")
    void emptyMaxRejected() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("1", ""));

        assertEquals(0, items.size());
        assertEquals(1, errors.size());
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
    @DisplayName("an unknown sval is a soft error and drops only that entry")
    void unknownSvalDropped() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors,
                food("1", "3"),
                new ClassEquipParseRecord("food", "No Such Thing", "1", "1", "none", 2));

        assertEquals(1, items.size());
        assertEquals(1, errors.size());
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
    @DisplayName("an unknown option drops the whole entry, as C returns INVALID_OPTION")
    void unknownEoptDropsEntry() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, withEopts("notanoption"));

        assertEquals(0, items.size());
        assertFalse(errors.isEmpty());
    }

    @Test
    @DisplayName("a non-birth option (use_sound) drops the whole entry")
    void nonBirthEoptDropsEntry() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, withEopts("use_sound"));

        assertEquals(0, items.size());
        assertFalse(errors.isEmpty());
    }

    @Test
    @DisplayName("one bad option among valid ones still drops the entry")
    void oneBadEoptAmongValidDropsEntry() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, withEopts("birth_no_recall notanoption"));

        assertEquals(0, items.size());
    }

    @Test
    @DisplayName("a bad NOT- option drops the entry; neighbouring entries survive")
    void badNegatedEoptDropsOnlyThatEntry() {
        List<String> errors = new ArrayList<>();
        List<StartItem> items = run(errors, food("1", "3"), withEopts("NOT-notanoption"));

        assertEquals(1, items.size());
        assertTrue(items.get(0).geteOpts().isEmpty());
    }
}
