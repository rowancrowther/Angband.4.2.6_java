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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.curse.CurseAssembler;
import uk.co.jackoftradesltd.backend.parser.curse.CurseParseRecord;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives {@link CurseAssembler#assemble} directly with hand-built {@link CurseParseRecord}s, so each
 * case isolates one clause of the assembler without the lexer and grammar in the way
 * ({@code CurseReaderTest} covers the file-to-curse path end to end).
 *
 * <p>Expected values come from the C loader in {@code obj-init.c}, not from the Java:
 * <ul>
 *   <li>{@code parse_curse_weight} accepts a weight adjustment from -32768 to 32767 inclusive and
 *       returns {@code PARSE_ERROR_INVALID_VALUE} outside that range; it does not clamp, so a
 *       negative adjustment is kept;</li>
 *   <li>{@code finish_parse_curse} rejects a curse that has {@code OF_MULTIPLY_WEIGHT} and a
 *       negative weight, allows 254 curses and fails on the 255th, and numbers them from the head of
 *       a list built by prepending, so the last curse in the file comes first;</li>
 *   <li>{@code parse_curse_desc} concatenates {@code desc:} lines with no separator;</li>
 *   <li>{@code parse_curse_flags} and {@code parse_curse_values} both write the same
 *       {@code el_info[]} slot for an element, so {@code HATES_FIRE} and {@code RES_FIRE[-1]} land
 *       on one entry.</li>
 * </ul>
 *
 * <p>Only {@code object_base.txt} is seeded (by reflection, restored afterwards), because every
 * record here has an empty effect list and so needs no summon or monster registries.
 *
 * <p>Class CurseAssemblerTest coded on 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
class CurseAssemblerTest {

    private static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";

    private static Object savedObjectBases;

    /**
     * Loads the real object bases so {@code type:cloak} and friends resolve.
     */
    @BeforeAll
    static void seed() throws Exception {
        List<ObjectBase> objectBases = new ObjectBaseReader().parseWithResults(OBJECT_BASE_FILE).items();
        Field f = RegistrySeeding.resolve("objectBases");
        f.setAccessible(true);
        savedObjectBases = f.get(null);
        f.set(null, objectBases);
    }

    /**
     * Puts the original object bases back so no state leaks to other suites.
     */
    @AfterAll
    static void restore() throws Exception {
        Field f = RegistrySeeding.resolve("objectBases");
        f.setAccessible(true);
        f.set(null, savedObjectBases);
    }

    // ---- fixture helpers -------------------------------------------------

    private static List<Curse> assemble(List<String> errors, Rec... recs) {
        List<CurseParseRecord> records = new ArrayList<>();
        for (Rec r : recs) records.add(r.build());
        return new CurseAssembler().assemble(records, errors);
    }

    private static Curse only(List<Curse> curses) {
        assertEquals(1, curses.size());
        return curses.get(0);
    }

    private static Rec[] numbered(int count) {
        Rec[] recs = new Rec[count];
        for (int i = 0; i < count; i++) recs[i] = new Rec("curse" + i);
        return recs;
    }

    // ---- weight: parse_curse_weight --------------------------------------

    @Test
    @DisplayName("an absent weight line means 0, not an error")
    void absentWeightIsZero() {
        List<String> errors = new ArrayList<>();
        Curse c = only(assemble(errors, new Rec("a")));

        assertAll(() -> assertEquals(0, c.getItemObject().getWeight()),
                () -> assertTrue(errors.isEmpty(), errors::toString));
    }

    @Test
    @DisplayName("a negative weight adjustment is kept, not clamped to 0")
    void negativeWeightIsKept() {
        List<String> errors = new ArrayList<>();
        Curse c = only(assemble(errors, new Rec("a").weight("-5")));

        assertAll(() -> assertEquals(-5, c.getItemObject().getWeight()),
                () -> assertTrue(errors.isEmpty(), errors::toString));
    }

    @Test
    @DisplayName("weight bounds -32768 and 32767 are both accepted (inclusive)")
    void weightBoundsAreInclusive() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors, new Rec("lo").weight("-32768"), new Rec("hi").weight("32767"));

        // Result order is reversed against the file, so hi is first
        assertAll(() -> assertEquals(2, curses.size()),
                () -> assertEquals(32767, curses.get(0).getItemObject().getWeight()),
                () -> assertEquals(-32768, curses.get(1).getItemObject().getWeight()),
                () -> assertTrue(errors.isEmpty(), errors::toString));
    }

    @Test
    @DisplayName("weight 32768 and -32769 are out of range: reported, curse skipped")
    void weightOutsideRangeIsRejected() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors, new Rec("over").weight("32768").line(3),
                new Rec("under").weight("-32769").line(9));

        assertAll(() -> assertTrue(curses.isEmpty()),
                () -> assertTrue(errors.stream().anyMatch(
                        e -> e.contains("line: 3") && e.contains("out of range") && e.contains("32768")), errors::toString),
                () -> assertTrue(errors.stream().anyMatch(
                        e -> e.contains("line: 9") && e.contains("out of range") && e.contains("-32769")), errors::toString));
    }

    @Test
    @DisplayName("a non-integer weight, or one beyond int range, is reported and skipped")
    void malformedWeightIsRejected() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors, new Rec("word").weight("heavy"),
                new Rec("huge").weight("99999999999"));

        assertAll(() -> assertTrue(curses.isEmpty()),
                () -> assertEquals(2, errors.stream().filter(e -> e.contains("invalid weight adjustment")).count(),
                        errors::toString));
    }

    // ---- finish_parse_curse: MULTIPLY_WEIGHT -----------------------------

    @Test
    @DisplayName("MULTIPLY_WEIGHT with a negative weight is reported and the curse skipped")
    void multiplyWeightWithNegativeWeightIsRejected() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors, new Rec("a").weight("-1").flags("MULTIPLY_WEIGHT").line(4));

        assertAll(() -> assertTrue(curses.isEmpty()),
                () -> assertTrue(errors.stream().anyMatch(
                        e -> e.contains("line: 4") && e.contains("multiply weight")), errors::toString));
    }

    @Test
    @DisplayName("MULTIPLY_WEIGHT with weight 0 or positive loads; negative weight without the flag loads")
    void multiplyWeightBoundaryCases() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors,
                new Rec("zero").weight("0").flags("MULTIPLY_WEIGHT"),
                new Rec("pos").weight("150").flags("MULTIPLY_WEIGHT"),
                new Rec("neg-no-flag").weight("-1"));

        assertAll(() -> assertEquals(3, curses.size()),
                () -> assertTrue(errors.isEmpty(), errors::toString),
                () -> assertTrue(curses.get(1).getItemObject().getFlags().has(ObjectFlag.OF_MULTIPLY_WEIGHT)));
    }

    // ---- combat: parse_curse_combat --------------------------------------

    @Test
    @DisplayName("combat values map to to-hit, to-dam and to-AC in that order, negatives kept")
    void combatMapsInOrder() {
        List<String> errors = new ArrayList<>();
        Curse c = only(assemble(errors, new Rec("a").combat("-5", "-6", "20")));

        assertAll(() -> assertEquals(-5, c.getItemObject().getToHit()),
                () -> assertEquals(-6, c.getItemObject().getToDam()),
                () -> assertEquals(20, c.getItemObject().getToAC()),
                () -> assertTrue(errors.isEmpty(), errors::toString));
    }

    @Test
    @DisplayName("a bad to-hit, to-dam or to-AC value is reported against its own field and skipped")
    void badCombatFieldIsRejected() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors,
                new Rec("h").combat("x", "0", "0"),
                new Rec("d").combat("0", "x", "0"),
                new Rec("a").combat("0", "0", "x"));

        assertAll(() -> assertTrue(curses.isEmpty()),
                () -> assertTrue(errors.stream().anyMatch(e -> e.contains("combat toh")), errors::toString),
                () -> assertTrue(errors.stream().anyMatch(e -> e.contains("combat tod")), errors::toString),
                () -> assertTrue(errors.stream().anyMatch(e -> e.contains("combat toa")), errors::toString));
    }

    // ---- flags / values sharing one element entry ------------------------

    @Test
    @DisplayName("HATES_FIRE on flags: and RES_FIRE on values: share one element entry")
    void flagsAndValuesShareOneElementInfo() {
        List<String> errors = new ArrayList<>();
        Curse c = only(assemble(errors, new Rec("a").flags("HATES_FIRE").value("RES_FIRE", "-1")));

        var info = c.getItemObject().getElInfo().get(ElementEnum.ELEM_FIRE);
        assertAll(() -> assertEquals(1, c.getItemObject().getElInfo().size()),
                () -> assertNotNull(info),
                () -> assertTrue(info.getFlags().has(ElementInfoEnum.EL_INFO_HATES)),
                () -> assertEquals(-1, info.getResLevel()),
                () -> assertTrue(errors.isEmpty(), errors::toString));
    }

    @Test
    @DisplayName("a malformed integer in values: is reported for both the RES_ and modifier branches")
    void badValueIntegerIsRejected() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors,
                new Rec("res").value("RES_FIRE", "x"),
                new Rec("mod").value("STR", "x"));

        assertAll(() -> assertTrue(curses.isEmpty()),
                () -> assertEquals(2, errors.stream().filter(e -> e.contains("badly formed integer")).count(),
                        errors::toString));
    }

    // ---- desc: parse_curse_desc ------------------------------------------

    @Test
    @DisplayName("desc lines are concatenated with no separator")
    void descLinesConcatenate() {
        Curse c = only(assemble(new ArrayList<>(), new Rec("a").desc("makes you ", "mentally slow")));

        assertEquals("makes you mentally slow", c.getDescription());
    }

    // ---- conflict-flags --------------------------------------------------

    @Test
    @DisplayName("conflict-flags set the conflict flag set, not the curse object's flags")
    void conflictFlagsLandOnTheCurse() {
        List<String> errors = new ArrayList<>();
        Curse c = only(assemble(errors, new Rec("a").cFlag("NO_TELEPORT", "AFRAID")));

        assertAll(() -> assertTrue(c.getConflictFlags().has(ObjectFlag.OF_NO_TELEPORT)),
                () -> assertTrue(c.getConflictFlags().has(ObjectFlag.OF_AFRAID)),
                () -> assertTrue(c.getItemObject().getFlags().isEmpty()),
                () -> assertTrue(errors.isEmpty(), errors::toString));
    }

    @Test
    @DisplayName("an unknown conflict flag is reported and the curse skipped")
    void unknownConflictFlagIsRejected() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors, new Rec("a").cFlag("NOT_A_FLAG"));

        assertAll(() -> assertTrue(curses.isEmpty()),
                () -> assertTrue(errors.stream().anyMatch(e -> e.contains("invalid conflict flag")), errors::toString));
    }

    // ---- ordering and index: finish_parse_curse --------------------------

    @Test
    @DisplayName("the last curse in the file comes first, and indexes run 0.. in that order")
    void resultIsReversedAndIndexed() {
        List<Curse> curses = assemble(new ArrayList<>(), new Rec("first"), new Rec("second"), new Rec("third"));

        assertAll(() -> assertEquals(List.of("third", "second", "first"),
                        curses.stream().map(Curse::getName).toList()),
                () -> assertEquals(List.of(0, 1, 2), curses.stream().map(Curse::getIndex).toList()));
    }

    @Test
    @DisplayName("a skipped record leaves no gap in the indexes")
    void skippedRecordLeavesNoIndexGap() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors, new Rec("a"), new Rec("bad").weight("x"), new Rec("c"));

        assertAll(() -> assertEquals(List.of("c", "a"), curses.stream().map(Curse::getName).toList()),
                () -> assertEquals(List.of(0, 1), curses.stream().map(Curse::getIndex).toList()));
    }

    // ---- curse-count cap: finish_parse_curse -----------------------------

    @Test
    @DisplayName("254 curses load")
    void exactlyTheCapLoads() {
        assertEquals(254, assemble(new ArrayList<>(), numbered(254)).size());
    }

    @Test
    @DisplayName("255 curses throw, naming the count")
    void oneOverTheCapThrows() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> assemble(new ArrayList<>(), numbered(255)));

        assertTrue(e.getMessage().contains("255"), e::getMessage);
    }

    @Test
    @DisplayName("records skipped for errors do not count towards the cap")
    void skippedRecordsDoNotCountTowardsTheCap() {
        Rec[] recs = numbered(255);
        recs[100] = new Rec("bad").weight("x");

        assertEquals(254, assemble(new ArrayList<>(), recs).size());
    }

    @Test
    @DisplayName("a conflict naming a later curse links to that same instance")
    void forwardConflictReferenceLinks() {
        List<String> errors = new ArrayList<>();
        List<Curse> curses = assemble(errors, new Rec("a").conflict("b"), new Rec("b"));

        Curse a = curses.stream().filter(c -> c.getName().equals("a")).findFirst().orElseThrow();
        Curse b = curses.stream().filter(c -> c.getName().equals("b")).findFirst().orElseThrow();
        assertAll(() -> assertEquals(1, a.getConflict().size()),
                () -> assertSame(b, a.getConflict().get(0)),
                () -> assertTrue(errors.isEmpty(), errors::toString));
    }

    // ---- second pass: conflict linking -----------------------------------

    @Test
    @DisplayName("an unknown conflict name is reported but the curse stays loaded")
    void unknownConflictNameKeepsTheCurse() {
        List<String> errors = new ArrayList<>();
        Curse a = only(assemble(errors, new Rec("a").conflict("nobody")));

        assertAll(() -> assertTrue(a.getConflict().isEmpty()),
                () -> assertTrue(errors.stream().anyMatch(
                        e -> e.contains("Invalid curse conflict name") && e.contains("nobody")), errors::toString));
    }

    /**
     * A mutable stand-in for the record's fields, defaulted to what the grammar hands over for a
     * bare {@code name:}/{@code type:cloak} curse (blank optional numbers, empty lists).
     */
    private static final class Rec {
        String name;
        List<String> type = List.of("cloak");
        String weight = "";
        String toh = "";
        String tod = "";
        String toa = "";
        List<String> flags = List.of();
        Map<String, String> values = new LinkedHashMap<>();
        String message = "";
        List<String> desc = List.of();
        List<String> conflict = List.of();
        List<String> cFlag = List.of();
        int line = 1;

        Rec(String name) {
            this.name = name;
        }

        Rec weight(String w) {
            weight = w;
            return this;
        }

        Rec flags(String... f) {
            flags = List.of(f);
            return this;
        }

        Rec value(String key, String value) {
            values.put(key, value);
            return this;
        }

        Rec combat(String h, String d, String a) {
            toh = h;
            tod = d;
            toa = a;
            return this;
        }

        Rec desc(String... d) {
            desc = List.of(d);
            return this;
        }

        Rec conflict(String... c) {
            conflict = List.of(c);
            return this;
        }

        Rec cFlag(String... c) {
            cFlag = List.of(c);
            return this;
        }

        Rec line(int l) {
            line = l;
            return this;
        }

        CurseParseRecord build() {
            return new CurseParseRecord(name, type, weight, toh, tod, toa, List.of(), flags, values,
                    message, desc, conflict, cFlag, line);
        }
    }
}
