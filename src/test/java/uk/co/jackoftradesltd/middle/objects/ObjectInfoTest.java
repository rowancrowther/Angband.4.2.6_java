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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.QualityValueEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ObjectIgnoreTables} - the Java home of {@code quality_mapping[]} and {@code ignore_level[]}
 * from {@code obj-ignore.c}.
 *
 * <p>The expected rows below are typed out from the C table, not read back from the Java one, so a
 * dropped, reordered or mistyped row fails here. The lookups run C's first-match loop over the table
 * with kind names taken from {@code object.txt}, which is how {@code ignore_type_of} uses it.
 *
 * <p>Class ObjectInfoTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class ObjectInfoTest {

    /** C's {@code quality_mapping[]}, row by row: ignore type, tval, identifier. */
    private static final Object[][] C_ROWS = {
            {IgnoreType.ITYPE_GREAT, TValue.TV_SWORD, "Chaos"},
            {IgnoreType.ITYPE_GREAT, TValue.TV_POLEARM, "Slicing"},
            {IgnoreType.ITYPE_GREAT, TValue.TV_HAFTED, "Disruption"},
            {IgnoreType.ITYPE_SHARP, TValue.TV_SWORD, ""},
            {IgnoreType.ITYPE_SHARP, TValue.TV_POLEARM, ""},
            {IgnoreType.ITYPE_BLUNT, TValue.TV_HAFTED, ""},
            {IgnoreType.ITYPE_SLING, TValue.TV_BOW, "Sling"},
            {IgnoreType.ITYPE_BOW, TValue.TV_BOW, "Bow"},
            {IgnoreType.ITYPE_CROSSBOW, TValue.TV_BOW, "Crossbow"},
            {IgnoreType.ITYPE_SHOT, TValue.TV_SHOT, ""},
            {IgnoreType.ITYPE_ARROW, TValue.TV_ARROW, ""},
            {IgnoreType.ITYPE_BOLT, TValue.TV_BOLT, ""},
            {IgnoreType.ITYPE_ROBE, TValue.TV_SOFT_ARMOR, "Robe"},
            {IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Black"},
            {IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Blue"},
            {IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "White"},
            {IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Red"},
            {IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Green"},
            {IgnoreType.ITYPE_MULTI_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Multi"},
            {IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Shining"},
            {IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Law"},
            {IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Gold"},
            {IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Chaos"},
            {IgnoreType.ITYPE_BALANCE_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Balance"},
            {IgnoreType.ITYPE_POWER_DRAGON_ARMOR, TValue.TV_DRAG_ARMOR, "Power"},
            {IgnoreType.ITYPE_BODY_ARMOR, TValue.TV_HARD_ARMOR, ""},
            {IgnoreType.ITYPE_BODY_ARMOR, TValue.TV_SOFT_ARMOR, ""},
            {IgnoreType.ITYPE_ELVEN_CLOAK, TValue.TV_CLOAK, "Elven"},
            {IgnoreType.ITYPE_CLOAK, TValue.TV_CLOAK, ""},
            {IgnoreType.ITYPE_SHIELD, TValue.TV_SHIELD, ""},
            {IgnoreType.ITYPE_HEADGEAR, TValue.TV_HELM, ""},
            {IgnoreType.ITYPE_HEADGEAR, TValue.TV_CROWN, ""},
            {IgnoreType.ITYPE_HANDGEAR, TValue.TV_GLOVES, ""},
            {IgnoreType.ITYPE_FEET, TValue.TV_BOOTS, ""},
            {IgnoreType.ITYPE_DIGGER, TValue.TV_DIGGING, ""},
            {IgnoreType.ITYPE_RING, TValue.TV_RING, ""},
            {IgnoreType.ITYPE_AMULET, TValue.TV_AMULET, ""},
            {IgnoreType.ITYPE_LIGHT, TValue.TV_LIGHT, ""},
    };

    /** Runs C's {@code ignore_type_of} loop over {@link ObjectIgnoreTables#qualityMapping}. */
    private static IgnoreType ignoreTypeOf(TValue tval, String kindName) {
        for (ObjectIgnoreTables.QualityMapping row : ObjectIgnoreTables.qualityMapping) {
            if (row.tval() == tval) {
                if (!row.identifier().isEmpty() && !kindName.contains(row.identifier())) continue;
                return row.ignoreType();
            }
        }
        return IgnoreType.ITYPE_MAX;
    }

    @Nested
    @DisplayName("qualityMapping rows")
    class Rows {

        @Test
        @DisplayName("has the 38 rows of C's table")
        void rowCount() {
            assertEquals(38, C_ROWS.length, "the expectation itself must have C's row count");
            assertEquals(38, ObjectIgnoreTables.qualityMapping.length);
        }

        @Test
        @DisplayName("every row matches C by position: ignore type, tval and identifier")
        void rowsMatchC() {
            for (int i = 0; i < C_ROWS.length; i++) {
                ObjectIgnoreTables.QualityMapping row = ObjectIgnoreTables.qualityMapping[i];
                assertEquals(C_ROWS[i][0], row.ignoreType(), "row " + i + " ignore type");
                assertEquals(C_ROWS[i][1], row.tval(), "row " + i + " tval");
                assertEquals(C_ROWS[i][2], row.identifier(), "row " + i + " identifier");
            }
        }

        @Test
        @DisplayName("a row with an identifier always sits above a row of the same tval with none")
        void identifierRowsComeFirst() {
            for (int i = 0; i < ObjectIgnoreTables.qualityMapping.length; i++) {
                ObjectIgnoreTables.QualityMapping blanket = ObjectIgnoreTables.qualityMapping[i];
                if (!blanket.identifier().isEmpty()) continue;
                for (int j = i + 1; j < ObjectIgnoreTables.qualityMapping.length; j++) {
                    ObjectIgnoreTables.QualityMapping later = ObjectIgnoreTables.qualityMapping[j];
                    assertFalse(later.tval() == blanket.tval() && !later.identifier().isEmpty(),
                            "row " + j + " hides behind the catch-all row " + i);
                }
            }
        }

        @Test
        @DisplayName("no tval carries two catch-all rows, so the second could never be reached")
        void oneCatchAllPerTval() {
            for (int i = 0; i < ObjectIgnoreTables.qualityMapping.length; i++) {
                if (!ObjectIgnoreTables.qualityMapping[i].identifier().isEmpty()) continue;
                for (int j = i + 1; j < ObjectIgnoreTables.qualityMapping.length; j++) {
                    assertFalse(ObjectIgnoreTables.qualityMapping[j].tval() == ObjectIgnoreTables.qualityMapping[i].tval()
                                    && ObjectIgnoreTables.qualityMapping[j].identifier().isEmpty(),
                            "rows " + i + " and " + j + " are both catch-alls for one tval");
                }
            }
        }

        @Test
        @DisplayName("the record is a value: equal rows are equal and the accessors read its fields back")
        void recordIsAValue() {
            ObjectIgnoreTables.QualityMapping a = new ObjectIgnoreTables.QualityMapping(IgnoreType.ITYPE_RING, TValue.TV_RING, "");
            ObjectIgnoreTables.QualityMapping b = new ObjectIgnoreTables.QualityMapping(IgnoreType.ITYPE_RING, TValue.TV_RING, "");
            assertEquals(a, b);
            assertEquals(IgnoreType.ITYPE_RING, a.ignoreType());
            assertEquals(TValue.TV_RING, a.tval());
            assertEquals("", a.identifier());
        }
    }

    @Nested
    @DisplayName("first-match lookups with object.txt kind names")
    class Lookups {

        @Test
        @DisplayName("all twelve dragon scale mails land in the group C gives them")
        void dragonScaleMails() {
            assertEquals(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Black Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Blue Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "White Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Red Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Green Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_MULTI_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Multi-Hued Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Shining Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Law Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Gold Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Chaos Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_BALANCE_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Balance Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_POWER_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Power Dragon Scale Mail"));
        }

        @Test
        @DisplayName("Chaos is the high dragon group for dragon armour but the great group for a sword")
        void chaosByTval() {
            assertEquals(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR, ignoreTypeOf(TValue.TV_DRAG_ARMOR, "Chaos Dragon Scale Mail"));
            assertEquals(IgnoreType.ITYPE_GREAT, ignoreTypeOf(TValue.TV_SWORD, "Blade of Chaos"));
            assertEquals(IgnoreType.ITYPE_SHARP, ignoreTypeOf(TValue.TV_SWORD, "Dagger"));
        }

        @Test
        @DisplayName("Elven Cloak is tried before the catch-all cloak row; the other cloaks fall through to it")
        void cloaks() {
            assertEquals(IgnoreType.ITYPE_ELVEN_CLOAK, ignoreTypeOf(TValue.TV_CLOAK, "& Elven Cloak~"));
            assertEquals(IgnoreType.ITYPE_CLOAK, ignoreTypeOf(TValue.TV_CLOAK, "& Cloak~"));
            assertEquals(IgnoreType.ITYPE_CLOAK, ignoreTypeOf(TValue.TV_CLOAK, "& Fur Cloak~"));
            assertEquals(IgnoreType.ITYPE_CLOAK, ignoreTypeOf(TValue.TV_CLOAK, "& Ethereal Cloak~"));
        }

        @Test
        @DisplayName("a Robe is the robe group and any other soft armour is body armour")
        void softArmour() {
            assertEquals(IgnoreType.ITYPE_ROBE, ignoreTypeOf(TValue.TV_SOFT_ARMOR, "& Robe~"));
            assertEquals(IgnoreType.ITYPE_BODY_ARMOR, ignoreTypeOf(TValue.TV_SOFT_ARMOR, "Soft Leather Armour"));
        }

        @Test
        @DisplayName("identifiers are case sensitive substrings, so Crossbow is not a Bow")
        void caseSensitiveSubstring() {
            assertEquals(IgnoreType.ITYPE_CROSSBOW, ignoreTypeOf(TValue.TV_BOW, "Light Crossbow"));
            assertEquals(IgnoreType.ITYPE_BOW, ignoreTypeOf(TValue.TV_BOW, "Long Bow"));
            assertEquals(IgnoreType.ITYPE_SLING, ignoreTypeOf(TValue.TV_BOW, "Sling"));
        }

        @Test
        @DisplayName("a bow whose name holds no identifier, and a tval with no row, give ITYPE_MAX")
        void noMatch() {
            assertEquals(IgnoreType.ITYPE_MAX, ignoreTypeOf(TValue.TV_BOW, "Ballista"));
            assertEquals(IgnoreType.ITYPE_MAX, ignoreTypeOf(TValue.TV_FLASK, "Flask of Oil"));
        }

        @Test
        @DisplayName("helm and crown share headgear, and the one-row tvals map straight across")
        void sharedAndPlainRows() {
            assertEquals(IgnoreType.ITYPE_HEADGEAR, ignoreTypeOf(TValue.TV_HELM, "Hard Leather Cap"));
            assertEquals(IgnoreType.ITYPE_HEADGEAR, ignoreTypeOf(TValue.TV_CROWN, "Iron Crown"));
            assertEquals(IgnoreType.ITYPE_HANDGEAR, ignoreTypeOf(TValue.TV_GLOVES, "Leather Gloves"));
            assertEquals(IgnoreType.ITYPE_FEET, ignoreTypeOf(TValue.TV_BOOTS, "Leather Boots"));
            assertEquals(IgnoreType.ITYPE_DIGGER, ignoreTypeOf(TValue.TV_DIGGING, "Shovel"));
            assertEquals(IgnoreType.ITYPE_LIGHT, ignoreTypeOf(TValue.TV_LIGHT, "Wooden Torch"));
        }
    }

    @Nested
    @DisplayName("ignoreLevel")
    class IgnoreLevel {

        @Test
        @DisplayName("starts every ignore type at IGNORE_NONE, as C's zero-filled array does")
        void startsAtNone() {
            for (IgnoreType type : IgnoreType.values()) {
                assertEquals(QualityValueEnum.IGNORE_NONE, ObjectIgnoreTables.ignoreLevel.get(type), type.name());
            }
        }

        @Test
        @DisplayName("holds a slot for every ignore type, the ITYPE_NONE and ITYPE_MAX sentinels included")
        void oneSlotPerType() {
            assertEquals(IgnoreType.values().length, ObjectIgnoreTables.ignoreLevel.size());
            assertTrue(ObjectIgnoreTables.ignoreLevel.containsKey(IgnoreType.ITYPE_NONE));
            assertTrue(ObjectIgnoreTables.ignoreLevel.containsKey(IgnoreType.ITYPE_MAX));
        }

        @Test
        @DisplayName("IGNORE_NONE is the lowest quality band, so a freshly started level hides nothing")
        void noneIsLowest() {
            assertEquals(0, QualityValueEnum.IGNORE_NONE.ordinal());
        }
    }
}
