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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.ObjectPropertyReader;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks that {@link ObjectProperty} holds, field for field, what C's {@code struct obj_property}
 * ({@code obj-properties.h}) holds after {@code finish_parse_object_property} ({@code obj-init.c}).
 *
 * <p>The expected values come from two places that are not the Java: the shipped
 * {@code lib/gamedata/object_property.txt} read line by line with a small parser of this test's
 * own, applying C's defaults (every {@code power}, {@code mult} and index zero-filled; every
 * {@code type_mult} slot 1); and the C struct's own rules for what an absent line leaves behind.
 * The existing {@code ObjectPropertyAccessorsTest} covers the accessors one record at a time; this
 * class walks all 79 records against the file, and adds the element-relation and negative-power
 * records it does not touch.
 *
 * <p>Class ObjectPropertyStructFidelityTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
class ObjectPropertyStructFidelityTest {
    private static final String DATA = "lib/gamedata/object_property.txt";

    /**
     * What one record of the data file says, read straight off its lines.
     *
     * @param name      the {@code name:} text
     * @param power     the {@code power:} figure, 0 where absent (C's zero fill)
     * @param mult      the {@code mult:} figure, 0 where absent (C's zero fill)
     * @param typeMults the {@code type-mult:} figures, keyed by the tval name the file writes
     * @param msg       the {@code msg:} text, or {@code null} where absent (C's {@code NULL})
     */
    private record Expected(String name, int power, int mult, Map<String, Integer> typeMults, String msg) {
    }

    private List<ObjectProperty> loaded;
    private List<Expected> expected;

    /**
     * Loads the file through the real reader and reads the same file again with the test's own
     * line parser.
     *
     * @throws IOException if the data file cannot be read
     */
    @BeforeEach
    void load() throws IOException {
        loaded = new ObjectPropertyReader().parseWithResults(DATA).items();
        expected = readExpected();
    }

    /**
     * A deliberately naive reader of {@code object_property.txt}: one record starts at each
     * {@code name:} line, and only the fields this test compares are kept.
     *
     * @return the records in file order
     * @throws IOException if the file cannot be read
     */
    private static List<Expected> readExpected() throws IOException {
        List<Expected> out = new ArrayList<>();
        String name = null;
        int power = 0;
        int mult = 0;
        Map<String, Integer> typeMults = new HashMap<>();
        String msg = null;
        for (String line : Files.readAllLines(Path.of(DATA))) {
            if (line.startsWith("name:")) {
                if (name != null) out.add(new Expected(name, power, mult, typeMults, msg));
                name = line.substring(5);
                power = 0;
                mult = 0;
                typeMults = new HashMap<>();
                msg = null;
            } else if (line.startsWith("power:")) {
                power = Integer.parseInt(line.substring(6));
            } else if (line.startsWith("mult:")) {
                mult = Integer.parseInt(line.substring(5));
            } else if (line.startsWith("type-mult:")) {
                String[] parts = line.substring(10).split(":");
                typeMults.put(parts[0], Integer.parseInt(parts[1]));
            } else if (line.startsWith("msg:")) {
                msg = line.substring(4);
            }
        }
        out.add(new Expected(name, power, mult, typeMults, msg));
        return out;
    }

    /**
     * Finds a property by the name its record gives.
     *
     * @param name the {@code name:} text
     * @return the matching property
     */
    private ObjectProperty named(String name) {
        return loaded.stream().filter(p -> p.getName().equals(name)).findFirst()
                .orElseThrow(() -> new NoSuchElementException(name));
    }

    /**
     * The list holds one property per {@code name:} line, in file order — C's chain is built in
     * reverse and then copied into {@code obj_properties} back to front, so the array ends up in
     * file order too.
     */
    @Test
    @DisplayName("one property per name: line, in file order")
    void namesFollowTheFile() {
        assertEquals(79, expected.size());
        assertEquals(expected.size(), loaded.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).name(), loaded.get(i).getName(), "record " + i);
        }
    }

    /**
     * Every record's power, mult and notice message match the file, with C's defaults: an absent
     * {@code power:} or {@code mult:} is 0. An absent {@code msg:} is {@code NULL} in C and the
     * empty string in the port.
     */
    @Test
    @DisplayName("power, mult and message match the file for all 79 records")
    void scalarsFollowTheFile() {
        for (int i = 0; i < expected.size(); i++) {
            Expected e = expected.get(i);
            ObjectProperty p = loaded.get(i);
            assertEquals(e.power(), p.getPower(), e.name() + " power");
            assertEquals(e.mult(), p.getMultiplier(), e.name() + " mult");
            assertEquals(e.msg() == null ? "" : e.msg(), p.getNoticeMessage(), e.name() + " msg");
        }
    }

    /**
     * Every tval, for every record: the figure the file names if there is one, otherwise 1. This
     * is C's {@code type_mult[tval]} read across the whole array, including the two-word tval
     * names such as {@code dragon armor}.
     */
    @Test
    @DisplayName("type multiplier is the named figure or 1, for every tval of every record")
    void typeMultsFollowTheFile() {
        for (int i = 0; i < expected.size(); i++) {
            Expected e = expected.get(i);
            ObjectProperty p = loaded.get(i);
            Map<TValue, Integer> named = new HashMap<>();
            e.typeMults().forEach((k, v) -> named.put(TValue.fromName(k), v));
            for (TValue tval : TValue.values()) {
                assertEquals(named.getOrDefault(tval, 1), p.getTypeMult(tval),
                        e.name() + " " + tval);
            }
        }
    }

    /**
     * Negative power survives loading: a vulnerability prices below zero, and the power code adds
     * the figure as it stands.
     */
    @Test
    @DisplayName("acid vulnerability: power -6 keeps its sign")
    void negativePower() {
        ObjectProperty vuln = named("acid vulnerability");

        assertSame(ObjPropertyType.OBJ_PROPERTY_VULN, vuln.getType());
        assertEquals(-6, vuln.getPower());
        assertEquals(0, vuln.getMultiplier());
    }

    /**
     * An element relation carries an element payload, no subtype and no id-type (those are
     * flag-only), and a record with no {@code msg:} line holds the empty string. Its binding has
     * an auxiliary flag of 0 and no {@code uival}, so the value is absent (C's
     * {@code have_value} false) and the flag is false.
     */
    @Test
    @DisplayName("acid resistance: element payload, no subtype, empty message, unvalued binding")
    void elementRelation() {
        ObjectProperty res = named("acid resistance");

        assertSame(ObjPropertyType.OBJ_PROPERTY_RESIST, res.getType());
        assertEquals(ElementEnum.ELEM_ACID, res.getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
        assertEquals(5, res.getPower());
        assertEquals(ObjectFlagType.OFT_NONE, res.getSubtype());
        assertEquals(ObjectFlagID.OFID_NONE, res.getIdType());
        assertEquals("", res.getNoticeMessage());
        assertEquals(1, res.getBoundEntries().size());
        assertEquals("resist_ui_compact_0<ACID>", res.getBoundEntries().get(0).entry());
        assertNull(res.getBoundEntries().get(0).value());
        assertFalse(res.getBoundEntries().get(0).aux());
    }

    /**
     * The same code can be an element under one type and a modifier under another: the type
     * decides which table the payload came from, so a stat and a resistance of the same name do
     * not share a payload kind.
     */
    @Test
    @DisplayName("stat and resistance payloads come from different tables")
    void payloadKindFollowsType() {
        assertSame(ObjPropertyType.OBJ_PROPERTY_STAT, named("strength").getPayload().getType());
        assertSame(ObjPropertyType.OBJ_PROPERTY_RESIST, named("acid resistance").getPayload().getType());
    }

    /**
     * A record with a {@code bindui:} line carries exactly one binding and a record without one
     * carries none, because C only pushes a bound property when the directive is present. The
     * count of bound properties is the count of {@code bindui:} lines in the file.
     *
     * @throws IOException if the file cannot be read
     */
    @Test
    @DisplayName("one binding per bindui: line")
    void bindingsFollowTheFile() throws IOException {
        long binduiLines = Files.readAllLines(Path.of(DATA)).stream()
                .filter(l -> l.startsWith("bindui:")).count();
        long bindings = loaded.stream().mapToLong(p -> p.getBoundEntries().size()).sum();

        assertEquals(binduiLines, bindings);
        assertTrue(bindings > 0);
    }
}
