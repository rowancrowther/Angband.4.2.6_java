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
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks {@link EgoItem#egoHasIgnoreType} against C's {@code ego_has_ignore_type} in
 * {@code obj-ignore.c}.
 *
 * <p>C walks every kind in the ego's {@code poss_items}, and for each walks all of
 * {@code quality_mapping}. It answers true on the first row where the tval matches, the row's
 * ignore type is the one asked for, and {@code strstr(kind->name, identifier)} finds the identifier
 * inside the <em>kind's name</em>. An empty identifier is found in every name, so it means "any
 * kind of this tval". Otherwise it answers false.
 *
 * <p>Every expected value below is worked by hand from that C and the {@code quality_mapping}
 * table, not read back from the Java.
 */
class EgoHasIgnoreTypeTest {

    private static ObjectKind kind(String name, TValue tval) {
        return new ObjectKind(null, 0, 0, 0, 0, name, tval, "test", null, false);
    }

    private static EgoItem egoOn(ObjectKind... kinds) {
        return new EgoItem("of Testing", "a test ego", 1, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class),
                new Flag<>(ObjectKindFlag.class), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashSet<>(), new HashSet<>(), new HashMap<>(),
                0, 0, 0, 0, new ArrayList<>(List.of(kinds)), null, null, null, 0, 0, 0,
                null, null, false);
    }

    @Test
    @DisplayName("an empty identifier row matches any kind of that tval")
    void emptyIdentifierMatchesAnyKindOfTval() {
        EgoItem ego = egoOn(kind("Dagger", TValue.TV_SWORD));

        assertTrue(ego.egoHasIgnoreType(IgnoreType.ITYPE_SHARP), "TV_SWORD, \"\" -> SHARP");
    }

    @Test
    @DisplayName("a type the kind's tval does not map to is false")
    void unrelatedTypesAreFalse() {
        EgoItem ego = egoOn(kind("Dagger", TValue.TV_SWORD));

        assertFalse(ego.egoHasIgnoreType(IgnoreType.ITYPE_RING));
        assertFalse(ego.egoHasIgnoreType(IgnoreType.ITYPE_BLUNT));
        assertFalse(ego.egoHasIgnoreType(IgnoreType.ITYPE_BODY_ARMOR));
    }

    @Test
    @DisplayName("a named identifier must appear in the kind's name, so Chaos is not in Dagger")
    void namedIdentifierMustAppearInKindName() {
        EgoItem ego = egoOn(kind("Dagger", TValue.TV_SWORD));

        assertFalse(ego.egoHasIgnoreType(IgnoreType.ITYPE_GREAT),
                "strstr(\"Dagger\", \"Chaos\") is NULL");
    }

    @Test
    @DisplayName("a named identifier is found inside a longer kind name")
    void namedIdentifierFoundInsideLongerName() {
        EgoItem ego = egoOn(kind("Light Crossbow", TValue.TV_BOW));

        assertTrue(ego.egoHasIgnoreType(IgnoreType.ITYPE_CROSSBOW),
                "strstr(\"Light Crossbow\", \"Crossbow\") hits");
        assertFalse(ego.egoHasIgnoreType(IgnoreType.ITYPE_BOW),
                "strstr(\"Light Crossbow\", \"Bow\") is case sensitive and misses");
        assertFalse(ego.egoHasIgnoreType(IgnoreType.ITYPE_SLING));
    }

    @Test
    @DisplayName("dragon armour: Black is basic, Chaos is high, neither is the other")
    void dragonArmourGroups() {
        EgoItem black = egoOn(kind("Black Dragon Scale Mail", TValue.TV_DRAG_ARMOR));
        EgoItem chaos = egoOn(kind("Chaos Dragon Scale Mail", TValue.TV_DRAG_ARMOR));

        assertTrue(black.egoHasIgnoreType(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR));
        assertFalse(black.egoHasIgnoreType(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR));
        assertTrue(chaos.egoHasIgnoreType(IgnoreType.ITYPE_HIGH_DRAGON_ARMOR));
        assertFalse(chaos.egoHasIgnoreType(IgnoreType.ITYPE_BASIC_DRAGON_ARMOR));
        assertFalse(chaos.egoHasIgnoreType(IgnoreType.ITYPE_GREAT),
                "the Chaos row for GREAT is TV_SWORD, not TV_DRAG_ARMOR");
    }

    @Test
    @DisplayName("a later possible kind is reached when the first one does not match")
    void laterKindIsReached() {
        EgoItem ego = egoOn(kind("Dagger", TValue.TV_SWORD), kind("Ring of Protection", TValue.TV_RING));

        assertTrue(ego.egoHasIgnoreType(IgnoreType.ITYPE_RING));
        assertTrue(ego.egoHasIgnoreType(IgnoreType.ITYPE_SHARP));
        assertFalse(ego.egoHasIgnoreType(IgnoreType.ITYPE_AMULET));
    }

    @Test
    @DisplayName("an ego with no possible kinds has no ignore type")
    void noPossibleKinds() {
        EgoItem ego = egoOn();

        for (IgnoreType type : IgnoreType.values()) {
            assertFalse(ego.egoHasIgnoreType(type), type.toString());
        }
    }

    @Test
    @DisplayName("a kind with an empty name does not match a named identifier")
    void emptyKindNameDoesNotMatchNamedIdentifier() {
        EgoItem ego = egoOn(kind("", TValue.TV_SWORD));

        assertFalse(ego.egoHasIgnoreType(IgnoreType.ITYPE_GREAT),
                "strstr(\"\", \"Chaos\") is NULL");
        assertTrue(ego.egoHasIgnoreType(IgnoreType.ITYPE_SHARP),
                "strstr(\"\", \"\") is the haystack, so true");
    }
}
