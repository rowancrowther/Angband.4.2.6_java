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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Grouper}, the port of C's {@code struct grouper} ({@code z-type.h}). The struct has
 * no C function to cross-reference, so the expected values are rows transcribed by hand from the C
 * original's static tables: {@code group_item} and {@code group_artifact} in {@code wiz-spoil.c}
 * and {@code object_text_order} in {@code ui-knowledge.c}. The one piece of C logic that reads
 * the rows, the group-id walk in {@code textui_knowledge_init} that carries a heading down over
 * {@code NULL}-named rows, is repeated here over the Java rows and checked against the group ids
 * worked out from the C table.
 *
 * <p>Class GrouperTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class GrouperTest {

    /**
     * The first rows of C's {@code group_item}: a heading row followed by two continuation rows.
     */
    private static List<Grouper> ammoAndBows() {
        return List.of(
                new Grouper(TValue.TV_SHOT, "Ammo"),
                new Grouper(TValue.TV_ARROW, null),
                new Grouper(TValue.TV_BOLT, null),
                new Grouper(TValue.TV_BOW, "Bows"));
    }

    /**
     * A row with a heading hands back exactly the type and name it was built with.
     */
    @Test
    void headingRowKeepsTypeAndName() {
        Grouper g = new Grouper(TValue.TV_SHOT, "Ammo");
        assertSame(TValue.TV_SHOT, g.gettValue());
        assertEquals("Ammo", g.getName());
    }

    /**
     * C's {@code { TV_ARROW, NULL }}: a continuation row keeps the type and a {@code null} name,
     * and the constructor does not substitute an empty string for it.
     */
    @Test
    void continuationRowKeepsNullName() {
        Grouper g = new Grouper(TValue.TV_ARROW, null);
        assertSame(TValue.TV_ARROW, g.gettValue());
        assertNull(g.getName());
    }

    /**
     * An empty name is a different thing from {@code NULL}: the sentinel row of {@code group_item}
     * is {@code { 0, "" }} and must not read as a continuation row.
     */
    @Test
    void emptyNameIsNotNull() {
        Grouper g = new Grouper(TValue.TV_NONE, "");
        assertEquals("", g.getName());
        assertNotNull(g.getName());
    }

    /**
     * C's end-of-table row has tval 0, and {@link TValue#TV_NONE} is tval 0, so the loop test
     * {@code 0 != object_text_order[i].tval} ports as a comparison with {@code TV_NONE}.
     */
    @Test
    void sentinelTvalIsZero() {
        Grouper end = new Grouper(TValue.TV_NONE, null);
        assertEquals(0, end.gettValue().ordinal());
    }

    /**
     * The ordinal of the stored type is the C value of the tval, so a row built for a type reports
     * the same number C's {@code TV_*} has (from {@code list-tvals.h}: SHOT 2, ARROW 3, BOLT 4,
     * BOW 5).
     */
    @Test
    void storedTypeKeepsItsCValue() {
        List<Grouper> rows = ammoAndBows();
        assertEquals(2, rows.get(0).gettValue().ordinal());
        assertEquals(3, rows.get(1).gettValue().ordinal());
        assertEquals(4, rows.get(2).gettValue().ordinal());
        assertEquals(5, rows.get(3).gettValue().ordinal());
    }

    /**
     * The group-id walk of {@code textui_knowledge_init}: stop at tval 0, remember the index of the
     * latest row with a name, and give every row that index. Applied to the first four rows of
     * {@code group_item}, SHOT, ARROW and BOLT all land in group 0 ("Ammo") and BOW in group 3.
     */
    @Test
    void continuationRowsJoinTheGroupAbove() {
        List<Grouper> rows = new ArrayList<>(ammoAndBows());
        rows.add(new Grouper(TValue.TV_NONE, ""));

        int[] groupOf = new int[TValue.values().length];
        java.util.Arrays.fill(groupOf, -1);
        int gid = -1;
        for (int i = 0; rows.get(i).gettValue() != TValue.TV_NONE; i++) {
            if (rows.get(i).getName() != null) gid = i;
            groupOf[rows.get(i).gettValue().ordinal()] = gid;
        }

        assertEquals(0, groupOf[TValue.TV_SHOT.ordinal()]);
        assertEquals(0, groupOf[TValue.TV_ARROW.ordinal()]);
        assertEquals(0, groupOf[TValue.TV_BOLT.ordinal()]);
        assertEquals(3, groupOf[TValue.TV_BOW.ordinal()]);
        assertEquals(-1, groupOf[TValue.TV_SWORD.ordinal()], "a type with no row stays at -1");
    }

    /**
     * C's {@code group_artifact} rows for armour: "Body Armor" heads SOFT, HARD and DRAGON armour,
     * and "Helms/Crowns" heads HELM and CROWN. Counting the rows that carry a name gives the number
     * of headings C would write, which is two out of these five rows.
     */
    @Test
    void artifactArmourRowsHaveTwoHeadings() {
        List<Grouper> rows = List.of(
                new Grouper(TValue.TV_SOFT_ARMOR, "Body Armour"),
                new Grouper(TValue.TV_HARD_ARMOR, null),
                new Grouper(TValue.TV_DRAG_ARMOR, null),
                new Grouper(TValue.TV_HELM, "Helms/Crowns"),
                new Grouper(TValue.TV_CROWN, null));
        assertEquals(2, rows.stream().filter(r -> r.getName() != null).count());
        assertEquals(3, rows.stream().filter(r -> r.getName() == null).count() );
    }

    /**
     * The same type can head a group in one table and be absent from another, and two rows may
     * share a type: a row is only a pair, with no uniqueness rule. C's {@code object_text_order}
     * and {@code group_item} both list TV_AMULET, under different headings.
     */
    @Test
    void rowsAreIndependentPairs() {
        Grouper knowledge = new Grouper(TValue.TV_AMULET, "Amulet");
        Grouper spoiler = new Grouper(TValue.TV_AMULET, "Amulets");
        assertSame(knowledge.gettValue(), spoiler.gettValue());
        assertNotEquals(knowledge.getName(), spoiler.getName());
    }
}
