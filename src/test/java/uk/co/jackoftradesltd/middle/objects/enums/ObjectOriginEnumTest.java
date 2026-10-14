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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link ObjectOriginEnum} against the rows of {@code list-origins.h}, which C builds both
 * the {@code ORIGIN_*} enum in {@code object.h} and the {@code origins[]} table in
 * {@code obj-info.c} from.
 *
 * <p>The expected values are written out from the C header, one {@link Row} per
 * {@code ORIGIN(...)} line, in its column order (name, args, desc). The columns are private and
 * have no getter, so they are read by reflection. A local copy of the {@code switch} in
 * {@code describe_origin} then turns a row into the history line C would print, so the templates
 * are checked as text and not only as strings.
 *
 * <p>Class ObjectOriginEnumTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class ObjectOriginEnumTest {

    /**
     * One line of {@code list-origins.h}.
     */
    private record Row(String name, int args, String desc) {
    }

    /**
     * The 26 real rows of {@code list-origins.h}, in the header's order.
     */
    private static final List<Row> C_ROWS = List.of(
            new Row("NONE", -1, ""),
            new Row("FLOOR", 1, "Found lying on the floor %s"),
            new Row("CHEST", 1, "Taken from a chest found %s"),
            new Row("SPECIAL", 1, "Found lying on the floor of a special room %s"),
            new Row("PIT", 1, "Found lying on the floor in a pit %s"),
            new Row("VAULT", 1, "Found lying on the floor in a vault %s"),
            new Row("LABYRINTH", 1, "Found lying on the floor of a labyrinth %s"),
            new Row("CAVERN", 1, "Found lying on the floor of a cavern %s"),
            new Row("RUBBLE", 1, "Found under some rubble %s"),
            new Row("MIXED", -1, ""),
            new Row("DROP", 2, "Dropped by %s %s"),
            new Row("DROP_SPECIAL", 2, "Dropped by %s %s"),
            new Row("DROP_PIT", 2, "Dropped by %s %s"),
            new Row("DROP_VAULT", 2, "Dropped by %s %s"),
            new Row("STATS", -1, ""),
            new Row("ACQUIRE", 1, "Conjured forth by magic %s"),
            new Row("STORE", 0, "Bought from a store"),
            new Row("STOLEN", -1, ""),
            new Row("BIRTH", 0, "An inheritance from your family"),
            new Row("CHEAT", 0, "Created by debug option"),
            new Row("DROP_BREED", 2, "Dropped by %s %s"),
            new Row("DROP_SUMMON", 2, "Dropped by %s %s"),
            new Row("DROP_UNKNOWN", 1, "Dropped by an unknown monster %s"),
            new Row("DROP_POLY", 2, "Dropped by %s %s"),
            new Row("DROP_MIMIC", 2, "Dropped by %s %s"),
            new Row("DROP_WIZARD", 2, "Dropped by %s %s"));

    /**
     * Reads one private column of a constant.
     *
     * @param origin the constant
     * @param column the field name
     * @return the field's value
     * @throws Exception if the field cannot be reached
     */
    private static Object column(ObjectOriginEnum origin, String column) throws Exception {
        Field field = ObjectOriginEnum.class.getDeclaredField(column);
        field.setAccessible(true);
        return field.get(origin);
    }

    /**
     * C's {@code args} column for a constant.
     *
     * @param origin the constant
     * @return its parameter count
     * @throws Exception if the field cannot be reached
     */
    private static int args(ObjectOriginEnum origin) throws Exception {
        return (int) column(origin, "numParameters");
    }

    /**
     * C's {@code desc} column for a constant.
     *
     * @param origin the constant
     * @return its template
     * @throws Exception if the field cannot be reached
     */
    private static String desc(ObjectOriginEnum origin) throws Exception {
        return (String) column(origin, "name");
    }

    /**
     * A local copy of the {@code switch (origins[origin].args)} at the end of {@code describe_origin}
     * in {@code obj-info.c}, with the place and monster-name strings already built by the caller.
     *
     * @param origin   the origin to describe
     * @param monster  the monster name {@code describe_origin} builds ("a Cave bear", "Grip, Farmer
     *                 Maggot's Dog")
     * @param depth    the object's origin depth; 0 reads as "in town"
     * @return the history line, or {@code null} where C returns false and prints nothing
     * @throws Exception if a column cannot be reached
     */
    private static String describe(ObjectOriginEnum origin, String monster, int depth)
            throws Exception {
        String lootSpot = depth != 0
                ? "at " + (depth * 50) + " feet (level " + depth + ")"
                : "in town";
        return switch (args(origin)) {
            case -1 -> null;
            case 0 -> desc(origin);
            case 1 -> desc(origin).formatted(lootSpot);
            case 2 -> desc(origin).formatted(monster, lootSpot);
            default -> fail("unexpected parameter count " + args(origin));
        };
    }

    /**
     * C has 26 origins before {@code ORIGIN_MAX}, and Java has the same 26 in front of its own
     * {@code ORIGIN_MAX}, in the header's order, with no extra lead because {@code ORIGIN_NONE} is
     * the header's own first row.
     *
     * <p>Function constantsFollowTheHeaderOrder coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("The 26 header rows in order, then MAX")
    void constantsFollowTheHeaderOrder() {
        ObjectOriginEnum[] all = ObjectOriginEnum.values();
        assertEquals(C_ROWS.size() + 1, all.length);
        assertSame(ObjectOriginEnum.ORIGIN_MAX, all[all.length - 1]);
        for (int i = 0; i < C_ROWS.size(); i++) {
            assertEquals("ORIGIN_" + C_ROWS.get(i).name(), all[i].name(), "position " + i);
        }
    }

    /**
     * The ordinal is C's enum value, because the header's first row is {@code NONE} and Java adds
     * no lead. {@code origins[origin]} in C is therefore {@code values()[origin]} here.
     *
     * <p>Function ordinalsAreCsEnumValues coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("Ordinals are C's enum values: NONE 0, FLOOR 1, STATS 14, DROP_WIZARD 25, MAX 26")
    void ordinalsAreCsEnumValues() {
        assertEquals(0, ObjectOriginEnum.ORIGIN_NONE.ordinal());
        assertEquals(1, ObjectOriginEnum.ORIGIN_FLOOR.ordinal());
        assertEquals(9, ObjectOriginEnum.ORIGIN_MIXED.ordinal());
        assertEquals(14, ObjectOriginEnum.ORIGIN_STATS.ordinal());
        assertEquals(15, ObjectOriginEnum.ORIGIN_ACQUIRE.ordinal());
        assertEquals(18, ObjectOriginEnum.ORIGIN_BIRTH.ordinal());
        assertEquals(25, ObjectOriginEnum.ORIGIN_DROP_WIZARD.ordinal());
        assertEquals(26, ObjectOriginEnum.ORIGIN_MAX.ordinal());
        assertEquals(C_ROWS.size(), ObjectOriginEnum.ORIGIN_MAX.ordinal());
    }

    /**
     * Every row's parameter count and template, compared with the header.
     *
     * <p>Function everyRowMatchesTheHeader coded on 261014, commented in full on 261014.
     *
     * @throws Exception if a column cannot be reached
     */
    @Test
    @DisplayName("Every constant's parameter count and template match its header row")
    void everyRowMatchesTheHeader() throws Exception {
        ObjectOriginEnum[] all = ObjectOriginEnum.values();
        for (int i = 0; i < C_ROWS.size(); i++) {
            Row row = C_ROWS.get(i);
            assertEquals(row.args(), args(all[i]), row.name() + " args");
            assertEquals(row.desc(), desc(all[i]), row.name() + " desc");
        }
    }

    /**
     * The cavern template read "in a cavern" until 261014; the header says "of a cavern", like the
     * labyrinth and special-room rows beside it.
     *
     * <p>Function cavernReadsOfACavern coded on 261014, commented in full on 261014.
     *
     * @throws Exception if a column cannot be reached
     */
    @Test
    @DisplayName("ORIGIN_CAVERN says \"of a cavern\", not \"in a cavern\"")
    void cavernReadsOfACavern() throws Exception {
        assertEquals("Found lying on the floor of a cavern %s", desc(ObjectOriginEnum.ORIGIN_CAVERN));
        assertEquals("Found lying on the floor of a cavern at 600 feet (level 12)",
                describe(ObjectOriginEnum.ORIGIN_CAVERN, null, 12));
    }

    /**
     * C's four no-history rows ({@code NONE}, {@code MIXED}, {@code STATS}, {@code STOLEN}) have
     * {@code -1} and an empty string, and so does Java's {@code MAX}, which C has no row for. The
     * three rows with {@code 0} are exactly the ones whose template has no placeholder.
     *
     * <p>Function theSentinelAndPlainRowsAreWhereCPutsThem coded on 261014, commented in full on
     * 261014.
     *
     * @throws Exception if a column cannot be reached
     */
    @Test
    @DisplayName("The -1 rows are NONE, MIXED, STATS, STOLEN (and Java's MAX); the 0 rows are STORE, BIRTH, CHEAT")
    void theSentinelAndPlainRowsAreWhereCPutsThem() throws Exception {
        List<ObjectOriginEnum> noHistory = List.of(ObjectOriginEnum.ORIGIN_NONE,
                ObjectOriginEnum.ORIGIN_MIXED, ObjectOriginEnum.ORIGIN_STATS,
                ObjectOriginEnum.ORIGIN_STOLEN, ObjectOriginEnum.ORIGIN_MAX);
        List<ObjectOriginEnum> plain = List.of(ObjectOriginEnum.ORIGIN_STORE,
                ObjectOriginEnum.ORIGIN_BIRTH, ObjectOriginEnum.ORIGIN_CHEAT);
        for (ObjectOriginEnum origin : ObjectOriginEnum.values()) {
            if (noHistory.contains(origin)) {
                assertEquals(-1, args(origin), origin + " args");
                assertEquals("", desc(origin), origin + " desc");
            } else if (plain.contains(origin)) {
                assertEquals(0, args(origin), origin + " args");
            } else {
                assertTrue(args(origin) == 1 || args(origin) == 2, origin + " args");
            }
        }
    }

    /**
     * {@code describe_origin} feeds the template exactly {@code args} strings, so a row whose
     * placeholder count differs from its parameter count would print a stray {@code %s} or drop a
     * value. Every row with a history line must agree.
     *
     * <p>Function placeholderCountEqualsParameterCount coded on 261014, commented in full on
     * 261014.
     *
     * @throws Exception if a column cannot be reached
     */
    @Test
    @DisplayName("Each template holds exactly as many %s as its parameter count")
    void placeholderCountEqualsParameterCount() throws Exception {
        for (ObjectOriginEnum origin : ObjectOriginEnum.values()) {
            if (args(origin) < 0) {
                continue;
            }
            int placeholders = desc(origin).split("%s", -1).length - 1;
            assertEquals(args(origin), placeholders, origin.name());
        }
    }

    /**
     * Walks the templates through the {@code describe_origin} switch: a place at depth, the town
     * at depth 0, a monster drop with a name, the unknown-monster drop that takes only the place,
     * the plain templates, and the rows that print nothing.
     *
     * <p>Function historyLinesComeOutAsCWouldPrintThem coded on 261014, commented in full on
     * 261014.
     *
     * @throws Exception if a column cannot be reached
     */
    @Test
    @DisplayName("History lines: place at depth, town, monster drop, unknown drop, plain, none")
    void historyLinesComeOutAsCWouldPrintThem() throws Exception {
        assertEquals("Found lying on the floor at 250 feet (level 5)",
                describe(ObjectOriginEnum.ORIGIN_FLOOR, null, 5));
        assertEquals("Found lying on the floor in town",
                describe(ObjectOriginEnum.ORIGIN_FLOOR, null, 0));
        assertEquals("Taken from a chest found at 50 feet (level 1)",
                describe(ObjectOriginEnum.ORIGIN_CHEST, null, 1));
        assertEquals("Found under some rubble at 1500 feet (level 30)",
                describe(ObjectOriginEnum.ORIGIN_RUBBLE, null, 30));
        assertEquals("Conjured forth by magic at 100 feet (level 2)",
                describe(ObjectOriginEnum.ORIGIN_ACQUIRE, null, 2));
        assertEquals("Dropped by a Cave bear at 250 feet (level 5)",
                describe(ObjectOriginEnum.ORIGIN_DROP, "a Cave bear", 5));
        assertEquals("Dropped by Grip, Farmer Maggot's Dog in town",
                describe(ObjectOriginEnum.ORIGIN_DROP_VAULT, "Grip, Farmer Maggot's Dog", 0));
        assertEquals("Dropped by an unknown monster at 250 feet (level 5)",
                describe(ObjectOriginEnum.ORIGIN_DROP_UNKNOWN, "a Cave bear", 5));
        assertEquals("Bought from a store", describe(ObjectOriginEnum.ORIGIN_STORE, null, 7));
        assertEquals("An inheritance from your family",
                describe(ObjectOriginEnum.ORIGIN_BIRTH, null, 0));
        assertEquals("Created by debug option", describe(ObjectOriginEnum.ORIGIN_CHEAT, null, 3));
        assertNull(describe(ObjectOriginEnum.ORIGIN_NONE, null, 5));
        assertNull(describe(ObjectOriginEnum.ORIGIN_MIXED, null, 5));
        assertNull(describe(ObjectOriginEnum.ORIGIN_STOLEN, null, 5));
    }

    /**
     * The nine monster-drop rows that take a name and a place are exactly the ones whose header
     * line is {@code "Dropped by %s %s"}; {@code DROP_UNKNOWN} is the only drop with one parameter.
     *
     * <p>Function theMonsterDropsTakeTwoParametersExceptTheUnknownOne coded on 261014, commented in
     * full on 261014.
     *
     * @throws Exception if a column cannot be reached
     */
    @Test
    @DisplayName("Ten drop rows: nine take name and place, DROP_UNKNOWN takes only the place")
    void theMonsterDropsTakeTwoParametersExceptTheUnknownOne() throws Exception {
        int twoParameter = 0;
        int drops = 0;
        for (ObjectOriginEnum origin : ObjectOriginEnum.values()) {
            if (!origin.name().startsWith("ORIGIN_DROP")) {
                continue;
            }
            drops++;
            if (origin == ObjectOriginEnum.ORIGIN_DROP_UNKNOWN) {
                assertEquals(1, args(origin));
            } else {
                assertEquals(2, args(origin), origin.name());
                assertEquals("Dropped by %s %s", desc(origin), origin.name());
                twoParameter++;
            }
        }
        assertEquals(10, drops);
        assertEquals(9, twoParameter);
    }
}
