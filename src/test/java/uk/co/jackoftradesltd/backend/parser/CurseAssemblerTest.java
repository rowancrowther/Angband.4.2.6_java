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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.curse.CurseAssembler;
import uk.co.jackoftradesltd.backend.parser.curse.CurseParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
 *   <li>{@code parse_curse_type} accepts any name {@code tval_find_idx} resolves, a bare number
 *       included, and records it in {@code curse->poss[tval]} without looking for an object base;</li>
 *   <li>any parse error ends the load at that record ({@code run_parser} in {@code datafile.c}),
 *       so every fault here expects a {@code DataFileParseException}, and nothing is skipped.</li>
 *   <li>{@code parse_curse_desc} concatenates {@code desc:} lines with no separator;</li>
 *   <li>{@code parse_curse_flags} and {@code parse_curse_values} both write the same
 *       {@code el_info[]} slot for an element, so {@code HATES_FIRE} and {@code RES_FIRE[-1]} land
 *       on one entry.</li>
 * </ul>
 *
 * <p>No registry is needed: {@code type:} resolves through {@code TValue.fromName} alone, and the
 * one effect test uses an unknown effect name, which fails before any registry is read.
 *
 * <p>Class CurseAssemblerTest coded on 261005, commented in full on 261005, rewritten on 261023
 * for stop-the-load and {@code type:} held as tvals.
 *
 * @author Rowan Crowther
 */
class CurseAssemblerTest {

    // ---- fixture helpers -------------------------------------------------

    private static List<Curse> assemble(List<String> errors, Rec... recs) {
        List<CurseParseRecord> records = new ArrayList<>();
        for (Rec r : recs) records.add(r.build());
        return new CurseAssembler().assemble(records, errors, "test.txt");
    }

    /**
     * Assembles the records and expects the load to end, as C's {@code run_parser} does on the
     * first parse error.
     *
     * @param recs the records
     * @return the exception, for its message
     */
    private static DataFileParseException stops(Rec... recs) {
        return assertThrows(DataFileParseException.class, () -> assemble(new ArrayList<>(), recs));
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

    // ---- name: parse_curse_name ------------------------------------------

    @Test
    @DisplayName("an empty name ends the load")
    void emptyNameStops() {
        DataFileParseException e = stops(new Rec("").line(6));

        assertAll(() -> assertTrue(e.getMessage().contains("line: 6"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("test.txt"), e::getMessage));
    }

    // ---- type: parse_curse_type ------------------------------------------

    @Test
    @DisplayName("type: lines are kept as tvals, in file order")
    void typesAreTvals() {
        Curse c = only(assemble(new ArrayList<>(), new Rec("a").type("cloak", "soft armor")));

        assertEquals(List.of(TValue.TV_CLOAK, TValue.TV_SOFT_ARMOR), c.getTValues());
    }

    @Test
    @DisplayName("a tval with no object base (none, other book) loads, as tval_find_idx accepts it")
    void tvalWithNoObjectBaseLoads() {
        List<String> errors = new ArrayList<>();
        Curse c = only(assemble(errors, new Rec("a").type("none", "other book")));

        assertAll(() -> assertEquals(List.of(TValue.TV_NONE, TValue.TV_OTHER_BOOK), c.getTValues()),
                () -> assertTrue(errors.isEmpty(), errors::toString));
    }

    @Test
    @DisplayName("a numeric type: is a tval index, as tval_find_idx reads it with strtoul (15 is cloak)")
    void numericTypeIsTvalIndex() {
        Curse c = only(assemble(new ArrayList<>(), new Rec("a").type("15")));

        assertEquals(List.of(TValue.TV_CLOAK), c.getTValues());
    }

    @Test
    @DisplayName("British spelling of an armour tval resolves, as de_armour rewrites it first")
    void britishArmourSpellingResolves() {
        Curse c = only(assemble(new ArrayList<>(), new Rec("a").type("dragon armour")));

        assertEquals(List.of(TValue.TV_DRAG_ARMOR), c.getTValues());
    }

    @Test
    @DisplayName("an unknown type: name ends the load (PARSE_ERROR_UNRECOGNISED_TVAL)")
    void unknownTypeStops() {
        DataFileParseException e = stops(new Rec("a").type("cloak", "spaceship").line(12));

        assertAll(() -> assertTrue(e.getMessage().contains("line: 12"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("spaceship"), e::getMessage));
    }

    @Test
    @DisplayName("a type: index of TV_MAX or more ends the load")
    void outOfRangeTvalIndexStops() {
        stops(new Rec("a").type(String.valueOf(TValue.values().length)));
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
    @DisplayName("weight 32768 ends the load (PARSE_ERROR_INVALID_VALUE)")
    void weightAboveRangeStops() {
        DataFileParseException e = stops(new Rec("over").weight("32768").line(3));

        assertAll(() -> assertTrue(e.getMessage().contains("line: 3"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("out of range"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("32768"), e::getMessage));
    }

    @Test
    @DisplayName("weight -32769 ends the load (PARSE_ERROR_INVALID_VALUE)")
    void weightBelowRangeStops() {
        DataFileParseException e = stops(new Rec("under").weight("-32769").line(9));

        assertAll(() -> assertTrue(e.getMessage().contains("line: 9"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("-32769"), e::getMessage));
    }

    @Test
    @DisplayName("a non-integer weight ends the load")
    void nonIntegerWeightStops() {
        stops(new Rec("word").weight("heavy"));
    }

    @Test
    @DisplayName("a weight beyond int range ends the load")
    void hugeWeightStops() {
        stops(new Rec("huge").weight("99999999999"));
    }

    @Test
    @DisplayName("the first bad record ends the load: later records are never reached")
    void firstFaultEndsTheLoad() {
        DataFileParseException e = stops(new Rec("ok"), new Rec("bad").weight("x").line(5),
                new Rec("worse").weight("40000").line(20));

        assertTrue(e.getMessage().contains("line: 5"), e::getMessage);
    }

    // ---- finish_parse_curse: MULTIPLY_WEIGHT -----------------------------

    @Test
    @DisplayName("MULTIPLY_WEIGHT with a negative weight ends the load")
    void multiplyWeightWithNegativeWeightStops() {
        DataFileParseException e = stops(new Rec("a").weight("-1").flags("MULTIPLY_WEIGHT").line(4));

        assertAll(() -> assertTrue(e.getMessage().contains("line: 4"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("multiply weight"), e::getMessage));
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
    @DisplayName("an absent combat line means 0 for all three, as C's zero-filled object")
    void absentCombatIsZero() {
        Curse c = only(assemble(new ArrayList<>(), new Rec("a")));

        assertAll(() -> assertEquals(0, c.getItemObject().getToHit()),
                () -> assertEquals(0, c.getItemObject().getToDam()),
                () -> assertEquals(0, c.getItemObject().getToAC()));
    }

    @Test
    @DisplayName("a bad to-hit value ends the load, named against its own field")
    void badToHitStops() {
        DataFileParseException e = stops(new Rec("h").combat("x", "0", "0"));
        assertTrue(e.getMessage().contains("combat to Hit"), e::getMessage);
    }

    @Test
    @DisplayName("a bad to-dam value ends the load, named against its own field")
    void badToDamStops() {
        DataFileParseException e = stops(new Rec("d").combat("0", "x", "0"));
        assertTrue(e.getMessage().contains("combat to Damage"), e::getMessage);
    }

    @Test
    @DisplayName("a bad to-AC value ends the load, named against its own field")
    void badToAcStops() {
        DataFileParseException e = stops(new Rec("a").combat("0", "0", "x"));
        assertTrue(e.getMessage().contains("combat to AC"), e::getMessage);
    }

    // ---- flags: parse_curse_flags ----------------------------------------

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
    @DisplayName("IGNORE_ sets the ignore element flag and an ordinary token sets an object flag")
    void ignoreAndObjectFlags() {
        Curse c = only(assemble(new ArrayList<>(), new Rec("a").flags("IGNORE_ACID", "NO_TELEPORT")));

        assertAll(() -> assertTrue(c.getItemObject().getElInfo().get(ElementEnum.ELEM_ACID)
                        .getFlags().has(ElementInfoEnum.EL_INFO_IGNORE)),
                () -> assertTrue(c.getItemObject().getFlags().has(ObjectFlag.OF_NO_TELEPORT)));
    }

    @Test
    @DisplayName("an unknown object flag ends the load (PARSE_ERROR_INVALID_FLAG)")
    void unknownObjectFlagStops() {
        DataFileParseException e = stops(new Rec("a").flags("NOT_A_FLAG"));
        assertTrue(e.getMessage().contains("NOT_A_FLAG"), e::getMessage);
    }

    @Test
    @DisplayName("an unknown element after HATES_ ends the load (PARSE_ERROR_INVALID_FLAG)")
    void unknownElementFlagStops() {
        DataFileParseException e = stops(new Rec("a").flags("HATES_CUSTARD"));
        assertTrue(e.getMessage().contains("HATES_CUSTARD"), e::getMessage);
    }

    // ---- values: parse_curse_values --------------------------------------

    @Test
    @DisplayName("a modifier value lands on the object")
    void modifierLands() {
        Curse c = only(assemble(new ArrayList<>(), new Rec("a").value("STR", "-3")));

        assertEquals(-3, c.getItemObject().getModifiers().get(ObjectModifier.OM_STR));
    }

    @Test
    @DisplayName("a malformed integer on a RES_ entry ends the load")
    void badResIntegerStops() {
        stops(new Rec("res").value("RES_FIRE", "x"));
    }

    @Test
    @DisplayName("a malformed integer on a modifier entry ends the load")
    void badModifierIntegerStops() {
        stops(new Rec("mod").value("STR", "x"));
    }

    @Test
    @DisplayName("an unknown modifier ends the load (PARSE_ERROR_INVALID_VALUE)")
    void unknownModifierStops() {
        DataFileParseException e = stops(new Rec("a").value("LUCK", "1"));
        assertTrue(e.getMessage().contains("LUCK"), e::getMessage);
    }

    @Test
    @DisplayName("an unknown RES_ element ends the load (PARSE_ERROR_INVALID_VALUE)")
    void unknownResElementStops() {
        DataFileParseException e = stops(new Rec("a").value("RES_CUSTARD", "1"));
        assertTrue(e.getMessage().contains("RES_CUSTARD"), e::getMessage);
    }

    // ---- effect: parse_curse_effect --------------------------------------

    @Test
    @DisplayName("an unknown effect ends the load, as grab_effect_data's error does")
    void unknownEffectStops() {
        DataFileParseException e = stops(new Rec("a").effect("NOT_AN_EFFECT", 7).line(5));

        assertAll(() -> assertTrue(e.getMessage().contains("line: 5"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("line: 7"), e::getMessage));
    }

    @Test
    @DisplayName("no effect lines give an empty effect list, not an error")
    void noEffectsIsEmpty() {
        List<String> errors = new ArrayList<>();
        assemble(errors, new Rec("a"));

        assertTrue(errors.isEmpty(), errors::toString);
    }

    // ---- time: parse_curse_time ------------------------------------------

    @Test
    @DisplayName("an unparseable time: ends the load")
    void badTimeStops() {
        DataFileParseException e = stops(new Rec("a").time("not dice"));
        assertTrue(e.getMessage().contains("time"), e::getMessage);
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
    @DisplayName("an unknown conflict flag ends the load (PARSE_ERROR_INVALID_FLAG)")
    void unknownConflictFlagStops() {
        DataFileParseException e = stops(new Rec("a").cFlag("NOT_A_FLAG"));
        assertTrue(e.getMessage().contains("invalid conflict flag"), e::getMessage);
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

    // ---- curse-count cap: finish_parse_curse -----------------------------

    @Test
    @DisplayName("254 curses load")
    void exactlyTheCapLoads() {
        assertEquals(254, assemble(new ArrayList<>(), numbered(254)).size());
    }

    @Test
    @DisplayName("255 curses end the load, naming the line of the 255th")
    void oneOverTheCapStops() {
        Rec[] recs = numbered(255);
        recs[254].line(999);

        DataFileParseException e = stops(recs);

        assertAll(() -> assertTrue(e.getMessage().contains("Too many"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("line: 999"), e::getMessage));
    }

    @Test
    @DisplayName("a bad record before the 255th ends the load on its own fault, not the cap")
    void badRecordBeforeTheCapReportsItself() {
        Rec[] recs = numbered(255);
        recs[100] = new Rec("bad").weight("x").line(101);

        DataFileParseException e = stops(recs);

        assertAll(() -> assertFalse(e.getMessage().contains("Too many"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("line: 101"), e::getMessage));
    }

    // ---- second pass: conflict linking -----------------------------------

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

    @Test
    @DisplayName("a conflict name matches case-sensitively, as C's strstr does")
    void conflictNameIsCaseSensitive() {
        stops(new Rec("a").conflict("B"), new Rec("b"));
    }

    /**
     * C never checks a {@code conflict:} name; this ending the load is the accepted deviation
     * "A {@code conflict:} name that matches no curse ends the load" in
     * {@code docs/Accepted_Deviations.md}.
     */
    @Test
    @DisplayName("an unknown conflict name ends the load (accepted deviation: C never checks it)")
    void unknownConflictNameStopsHereWhereCNeverChecksIt() {
        DataFileParseException e = stops(new Rec("a").conflict("nobody"));

        assertAll(() -> assertTrue(e.getMessage().contains("nobody"), e::getMessage),
                () -> assertTrue(e.getMessage().contains("Curse: a"), e::getMessage));
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
        String time = "";
        List<EffectParseRecord> effects = List.of();
        int line = 1;

        Rec(String name) {
            this.name = name;
        }

        Rec type(String... t) {
            type = List.of(t);
            return this;
        }

        Rec effect(String type, int effectLine) {
            effects = List.of(new EffectParseRecord(type, "", "", "", "", "", "", "", "", "", "",
                    effectLine));
            return this;
        }

        Rec time(String t) {
            time = t;
            return this;
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
            return new CurseParseRecord(name, type, weight, toh, tod, toa, effects, flags, values,
                    message, desc, conflict, cFlag, time, line);
        }
    }
}
