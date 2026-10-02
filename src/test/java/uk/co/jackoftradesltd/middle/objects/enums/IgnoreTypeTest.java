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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link IgnoreType}, the port of C's {@code ignore_type_t}
 * ({@code src/list-ignore-types.h}, expanded in {@code src/obj-ignore.h}).
 *
 * <p>The expected rows below are copied from {@code list-ignore-types.h}, not read back from
 * the enum. The order matters because each constant's ordinal is its C value, and C sizes
 * {@code ignore_level}, {@code quality_choices} and {@code ego_ignore_types} by
 * {@code ITYPE_MAX}. The save file writes those arrays in that order too, so a reordering
 * here would load ignore settings into the wrong kinds.
 *
 * @author Rowan Crowther
 */
class IgnoreTypeTest {

    /**
     * Every row of {@code list-ignore-types.h}, in file order.
     */
    private static final List<CRow> C_ROWS = List.of(
            new CRow("NONE", ""),
            new CRow("SHARP", "Sharp Melee Weapons"),
            new CRow("BLUNT", "Blunt Melee Weapons"),
            new CRow("GREAT", "Great Weapons"),
            new CRow("SLING", "Slings"),
            new CRow("BOW", "Bows"),
            new CRow("CROSSBOW", "Crossbows"),
            new CRow("SHOT", "Shots and Pebbles"),
            new CRow("ARROW", "Arrows"),
            new CRow("BOLT", "Bolts"),
            new CRow("ROBE", "Robes"),
            new CRow("BODY_ARMOR", "Body Armor"),
            new CRow("BASIC_DRAGON_ARMOR", "Basic Dragon Scale Mail"),
            new CRow("MULTI_DRAGON_ARMOR", "Multi-Hued Dragon Scale Mail"),
            new CRow("HIGH_DRAGON_ARMOR", "High Dragon Scale Mail"),
            new CRow("BALANCE_DRAGON_ARMOR", "Balance Dragon Scale Mail"),
            new CRow("POWER_DRAGON_ARMOR", "Power Dragon Scale Mail"),
            new CRow("CLOAK", "Cloaks"),
            new CRow("ELVEN_CLOAK", "Elven Cloaks"),
            new CRow("SHIELD", "Shields"),
            new CRow("HEADGEAR", "Headgear"),
            new CRow("HANDGEAR", "Handgear"),
            new CRow("FEET", "Footgear"),
            new CRow("DIGGER", "Diggers"),
            new CRow("RING", "Rings"),
            new CRow("AMULET", "Amulets"),
            new CRow("LIGHT", "Lights"));

    /**
     * C has 27 rows, NONE included; {@code obj-ignore.h} adds ITYPE_MAX after them.
     */
    @Test
    void theEnumIsCsTwentySevenRowsPlusTheMaxSentinel() {
        assertEquals(28, IgnoreType.values().length,
                "27 rows from list-ignore-types.h, plus ITYPE_MAX");
    }

    /**
     * Every row lands on its C index, under the {@code ITYPE_} name C's macro pastes.
     */
    @Test
    void everyRowSitsAtItsCIndexUnderItsCName() {
        IgnoreType[] values = IgnoreType.values();
        for (int i = 0; i < C_ROWS.size(); i++) {
            assertEquals("ITYPE_" + C_ROWS.get(i).index(), values[i].name(),
                    "constant at C index " + i);
        }
    }

    /**
     * Every row carries its {@code description} column as its name, character for character.
     */
    @Test
    void everyRowCarriesItsCDescription() {
        for (CRow row : C_ROWS) {
            IgnoreType type = IgnoreType.valueOf("ITYPE_" + row.index());
            assertEquals(row.description(), type.getName(), type.name());
        }
    }

    /**
     * ITYPE_MAX is the count of real rows, which is what C sizes its arrays by.
     */
    @Test
    void maxSitsAfterTheLastRowAndEqualsTheRowCount() {
        assertEquals(C_ROWS.size(), IgnoreType.ITYPE_MAX.ordinal());
        assertSame(IgnoreType.ITYPE_MAX, IgnoreType.values()[IgnoreType.values().length - 1]);
    }

    /**
     * NONE is index 0 with the empty name C gives it, and the only row with an empty name.
     */
    @Test
    void noneIsSlotZeroAndTheOnlyRowWithAnEmptyName() {
        assertEquals(0, IgnoreType.ITYPE_NONE.ordinal());
        assertEquals("", IgnoreType.ITYPE_NONE.getName());
        long emptyRows = Arrays.stream(IgnoreType.values())
                .filter(t -> t != IgnoreType.ITYPE_MAX)
                .filter(t -> t.getName().isEmpty())
                .count();
        assertEquals(1, emptyRows, "only ITYPE_NONE has an empty description in C");
    }

    /**
     * C gives ITYPE_MAX no name at all; the empty string is the Java filler, pinned here so a
     * change to it is a deliberate one.
     */
    @Test
    void maxHasTheEmptyJavaFillerName() {
        assertEquals("", IgnoreType.ITYPE_MAX.getName());
    }

    /**
     * The neighbours C's {@code quality_mapping} table relies on: the identifier rows
     * (great weapons, elven cloaks) are separate types from the plain rows they shadow.
     */
    @Test
    void theShadowingTypesAreDistinctFromThePlainOnes() {
        assertEquals(3, IgnoreType.ITYPE_GREAT.ordinal());
        assertEquals(1, IgnoreType.ITYPE_SHARP.ordinal());
        assertEquals(2, IgnoreType.ITYPE_BLUNT.ordinal());
        assertEquals(17, IgnoreType.ITYPE_CLOAK.ordinal());
        assertEquals(18, IgnoreType.ITYPE_ELVEN_CLOAK.ordinal());
        assertEquals(26, IgnoreType.ITYPE_LIGHT.ordinal());
    }

    /**
     * One {@code ITYPE(index, description)} row from {@code list-ignore-types.h}.
     */
    private record CRow(String index, String description) {
    }
}
