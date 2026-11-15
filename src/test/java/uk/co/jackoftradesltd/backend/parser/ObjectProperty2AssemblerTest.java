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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectProperty2Assembler;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectProperty2ParseRecord;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks {@link ObjectProperty2Assembler} against C's {@code parse_object_property_*} handlers
 * in {@code obj-init.c}: the cases where C refuses a record or keeps the last of a repeated
 * line. The real-file comparison is not here because Gradle does not yet compile
 * {@code ObjectProperty2Grammar}, which lives in {@code src/main/gen}.
 *
 * <p>The cases build {@link Directive}s directly, so they test the assembler alone
 * and not the grammar.
 */
class ObjectProperty2AssemblerTest {
    private static Directive d(String name, int line, String... values) {
        return new Directive(name, Arrays.asList(values), line);
    }

    private static List<ObjectProperty> assemble(Directive... directives) {
        return new ObjectProperty2Assembler().assemble(
                List.of(new ObjectProperty2ParseRecord(Arrays.asList(directives))), "test.txt");
    }

    @Test
    @DisplayName("a record with no type-mult or bindui line has an empty map and list, and mult 1 for any tval")
    void defaultsAreNotNull() {
        List<ObjectProperty> result = assemble(d("name", 1, "plain"));
        ObjectProperty p = result.getFirst();
        assertNotNull(p.getBoundEntries());
        assertTrue(p.getBoundEntries().isEmpty());
        assertEquals(1, p.getTypeMult(TValue.TV_SWORD));
    }

    @Test
    @DisplayName("bindui with no type: line is refused (C: PARSE_ERROR_MISSING_OBJ_PROP_TYPE)")
    void binduiWithoutTypeRefused() {
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("bindui", 2, "ui_entry", "", "0", null)));
    }

    @Test
    @DisplayName("bindui with a type but no code: line loads (C checks only the type)")
    void binduiWithTypeButNoCodeLoads() {
        List<ObjectProperty> result = assemble(d("name", 1, "x"), d("type", 2, "flag"),
                d("bindui", 3, "ui_entry", "", "0", null));
        assertEquals(1, result.getFirst().getBoundEntries().size());
    }

    @Test
    @DisplayName("code: with no type: line is refused")
    void codeWithoutTypeRefused() {
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("code", 2, "SLOW_DIGEST")));
    }

    @Test
    @DisplayName("unknown subtype, id-type and code are refused")
    void unknownTokensRefused() {
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("subtype", 2, "nonsense")));
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("id-type", 2, "nonsense")));
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "flag"), d("code", 3, "NOT_A_FLAG")));
    }

    @Test
    @DisplayName("a repeated msg: or desc: replaces the earlier text; every bindui: binds")
    void repeatedLines() {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"),
                d("msg", 3, "first"), d("msg", 4, "second"),
                d("desc", 5, "one"), d("desc", 6, "two"),
                d("bindui", 7, "ui_a", "", "0", null),
                d("bindui", 8, "ui_b", "", "1", "3")).getFirst();
        assertEquals("second", p.getMessage());
        assertEquals("two", p.getDescription());
        assertEquals(2, p.getBoundEntries().size());
        assertNull(p.getBoundEntries().get(0).value());
        assertEquals(3, p.getBoundEntries().get(1).value());
        assertTrue(p.getBoundEntries().get(1).aux());
    }

    @Test
    @DisplayName("a record with no type: line keeps no type (C: OBJ_PROPERTY_NONE)")
    void noTypeLoads() {
        ObjectProperty p = assemble(d("name", 1, "x")).getFirst();
        assertTrue(p.getType() == null || p.getType() == ObjPropertyType.OBJ_PROPERTY_NONE);
    }

    @Test
    @DisplayName("a repeated type: leaves getType and getPayload agreeing (C keeps the index, new type)")
    void repeatedTypeKeepsTypeAndPayloadTogether() {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"),
                d("code", 3, "SLOW_DIGEST"), d("type", 4, "stat")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_STAT, p.getType());
        assertEquals(p.getType(), p.getPayload().getType(),
                "payload still says " + p.getPayload().getType());
    }

    @Test
    @DisplayName("a record with a type: and no code: holds that type's index 0 (C: zeroed index, new type)")
    void typeOnlyRecordHoldsIndexZeroUnderItsType() {
        ObjectProperty flag = assemble(d("name", 1, "x"), d("type", 2, "flag")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_FLAG, flag.getType());
        assertEquals(ObjPropertyType.OBJ_PROPERTY_FLAG, flag.getPayload().getType());
        assertEquals(ObjectFlag.values()[0], flag.getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));

        // C's obj_mods and element_names have no NONE, so index 0 is STR and ACID. The Java enums
        // carry a NONE placeholder at ordinal 0, which is why this is not values()[0].
        ObjectProperty mod = assemble(d("name", 1, "x"), d("type", 2, "mod")).getFirst();
        assertEquals(ObjectModifier.OM_STR, mod.getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));

        ObjectProperty resist = assemble(d("name", 1, "x"), d("type", 2, "resistance")).getFirst();
        assertEquals(ElementEnum.ELEM_ACID, resist.getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
    }

    @Test
    @DisplayName("type:stat, code, type:mod keeps the same modifier under the new type (same table in C)")
    void statRetypedToModKeepsTheModifier() {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "stat"),
                d("code", 3, "STR"), d("type", 4, "mod")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_MOD, p.getType());
        assertEquals(ObjPropertyType.OBJ_PROPERTY_MOD, p.getPayload().getType());
        assertEquals(ObjectModifier.OM_STR, p.getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
    }

    @Test
    @DisplayName("a retype between two element types keeps the element and takes the new type")
    void elementRetypedToAnotherElementTypeKeepsTheElement() {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "resistance"),
                d("code", 3, "FIRE"), d("type", 4, "immunity")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_IMM, p.getType());
        assertEquals(ObjPropertyType.OBJ_PROPERTY_IMM, p.getPayload().getType());
        assertEquals(ElementEnum.ELEM_FIRE, p.getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_IMM));
    }

    @Test
    @DisplayName("a cross-table retype keeps the index and reads it in the new table when it fits (C does the same)")
    void crossTableRetypeKeepsTheIndexWhenItFits() {
        // SUST_STR is index 1 in obj_flags (NONE is 0); index 1 in obj_mods is INT (STR is 0).
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"),
                d("code", 3, "SUST_STR"), d("type", 4, "mod")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_MOD, p.getType());
        assertEquals(ObjPropertyType.OBJ_PROPERTY_MOD, p.getPayload().getType());
        assertEquals(ObjectModifier.OM_INT, p.getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));

        // An element index also fits in the larger flag table. ACID is index 0 in element_names,
        // and index 0 in obj_flags is NONE.
        ObjectProperty q = assemble(d("name", 1, "x"), d("type", 2, "ignore"),
                d("code", 3, "ACID"), d("type", 4, "flag")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_FLAG, q.getPayload().getType());
        assertEquals(ObjectFlag.OF_NONE, q.getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    @Test
    @DisplayName("retyping away and back leaves the original code in place (C: the index never changed)")
    void retypingAwayAndBackKeepsTheOriginalCode() {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"),
                d("code", 3, "SUST_STR"), d("type", 4, "mod"), d("type", 5, "flag")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_FLAG, p.getType());
        assertEquals(ObjectFlag.OF_SUST_STR, p.getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    @Test
    @DisplayName("a later code: after a retype resolves in the new table and replaces the earlier one")
    void aCodeAfterARetypeResolvesInTheNewTable() {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"),
                d("code", 3, "SUST_STR"), d("type", 4, "mod"), d("code", 5, "DEX")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_MOD, p.getPayload().getType());
        assertEquals(ObjectModifier.OM_DEX, p.getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
    }

    @Test
    @DisplayName("a retype whose carried index is past the new table is refused (C: stores the index and loads)")
    void retypeWithAnIndexPastTheNewTableIsRefused() {
        // OF_BLESSED and OF_DRAIN_EXP both have a flag index past the modifier and element tables.
        DataFileParseException toMod = assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "flag"), d("code", 3, "BLESSED"), d("type", 4, "mod")));
        assertTrue(toMod.getMessage().contains("out of range"), toMod.getMessage());
        assertTrue(toMod.getMessage().contains("line: 4"), toMod.getMessage());

        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "flag"), d("code", 3, "BLESSED"), d("type", 4, "stat")));
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "flag"), d("code", 3, "DRAIN_EXP"), d("type", 4, "ignore")));
    }

    @Test
    @DisplayName("a flag at index 16 retyped to mod loads, holding OM_MAX; index 17 is refused (accepted)")
    void aRetypeLandingOnOmMaxLoads() {
        // obj_mods has 16 entries (index 0 to 15), so index 16 is the NULL slot. The Java enum has
        // OM_MAX there (ordinal 17, one more than the C index), so it loads. Index 17 is past it.
        ObjectFlag flag = ObjectFlag.values()[ObjectModifier.OM_MAX.ordinal() - 1];
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"),
                d("code", 3, flag.name()), d("type", 4, "mod")).getFirst();
        assertEquals(ObjPropertyType.OBJ_PROPERTY_MOD, p.getPayload().getType());
        assertEquals(ObjectModifier.OM_MAX, p.getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));

        ObjectFlag next = ObjectFlag.values()[ObjectModifier.OM_MAX.ordinal()];
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "flag"), d("code", 3, next.name()), d("type", 4, "mod")));
    }

    @Test
    @DisplayName("the last slot of each table a retype can land in loads, and the next index is refused")
    void theEdgeOfEachRetypeTargetTable() {
        // Flag index 25 is ELEM_MAX's slot (element_names has 25 entries, 0 to 24); 26 is past it.
        ObjectFlag landsOnElemMax = ObjectFlag.values()[ElementEnum.ELEM_MAX.ordinal() - 1];
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"),
                d("code", 3, landsOnElemMax.name()), d("type", 4, "resistance")).getFirst();
        assertEquals(ElementEnum.ELEM_MAX, p.getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
        ObjectFlag pastElements = ObjectFlag.values()[ElementEnum.ELEM_MAX.ordinal()];
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "flag"), d("code", 3, pastElements.name()),
                        d("type", 4, "resistance")));

        // Element index 16 is OM_MAX's slot in obj_mods; element index 17 is past it.
        ElementEnum landsOnOmMax = ElementEnum.values()[ObjectModifier.OM_MAX.ordinal()];
        ObjectProperty q = assemble(d("name", 1, "x"), d("type", 2, "resistance"),
                d("code", 3, landsOnOmMax.name()), d("type", 4, "mod")).getFirst();
        assertEquals(ObjectModifier.OM_MAX, q.getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
        ElementEnum pastMods = ElementEnum.values()[ObjectModifier.OM_MAX.ordinal() + 1];
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "resistance"), d("code", 3, pastMods.name()),
                        d("type", 4, "mod")));
    }

    @Test
    @DisplayName("an element index past the modifier table is refused on a retype to mod or stat")
    void elementRetypedToModPastTheTableIsRefused() {
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "resistance"), d("code", 3, "FORCE"), d("type", 4, "mod")));
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, "immunity"), d("code", 3, "FORCE"), d("type", 4, "stat")));
    }

    @Test
    @DisplayName("the carried code does not leak into the next record")
    void theCarriedCodeDoesNotLeakIntoTheNextRecord() {
        List<ObjectProperty> result = new ObjectProperty2Assembler().assemble(List.of(
                new ObjectProperty2ParseRecord(Arrays.asList(d("name", 1, "a"), d("type", 2, "flag"),
                        d("code", 3, "BLESSED"))),
                new ObjectProperty2ParseRecord(Arrays.asList(d("name", 4, "b"), d("type", 5, "mod")))), "test.txt");
        // The second record holds the zeroed index, which is STR in obj_mods, not BLESSED's index.
        assertEquals(ObjectModifier.OM_STR,
                result.get(1).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
    }

    // ---- code: values that name a sentinel or another table's constant ----
    // C resolves a code: line with code_index_in_array over the table the type: selects:
    // obj_mods (stats then modifiers, no NONE, no MAX), obj_flags ("NONE" first, "MAX" last)
    // and element_names (no NONE, no MAX). A name the table lacks is refused.

    @ParameterizedTest(name = "[{index}] type:{0} code:{1} is refused")
    @CsvSource({"stat, NONE", "stat, OM_NONE", "stat, MAX", "stat, OM_MAX",
            "mod, NONE", "mod, OM_NONE", "mod, MAX", "mod, OM_MAX"})
    @DisplayName("a modifier code that names a sentinel is refused (obj_mods has neither)")
    void modifierSentinelCodesAreRefused(String type, String code) {
        DataFileParseException e = assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, type), d("code", 3, code)));
        assertTrue(e.getMessage().contains("sentinel"), e.getMessage());
    }

    @ParameterizedTest(name = "[{index}] type:{0} code:{1} is refused")
    @CsvSource({"ignore, NONE", "ignore, ELEM_NONE", "ignore, MAX", "ignore, ELEM_MAX",
            "resistance, NONE", "resistance, ELEM_NONE", "resistance, MAX", "resistance, ELEM_MAX",
            "vulnerability, NONE", "vulnerability, ELEM_MAX", "immunity, NONE", "immunity, ELEM_MAX"})
    @DisplayName("an element code that names a sentinel is refused (element_names has neither)")
    void elementSentinelCodesAreRefused(String type, String code) {
        DataFileParseException e = assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, type), d("code", 3, code)));
        assertTrue(e.getMessage().contains("sentinel"), e.getMessage());
    }

    @ParameterizedTest(name = "[{index}] type:flag code:{0} loads as OF_NONE")
    @CsvSource({"NONE", "OF_NONE"})
    @DisplayName("type:flag with code:NONE loads as index 0 (obj_flags starts with NONE)")
    void flagNoneLoadsAsIndexZero(String code) {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"), d("code", 3, code)).getFirst();
        assertEquals(ObjectFlag.OF_NONE, p.getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    @ParameterizedTest(name = "[{index}] type:flag code:{0} loads as OF_MAX")
    @CsvSource({"MAX", "OF_MAX"})
    @DisplayName("type:flag with code:MAX loads as the last index (obj_flags ends with MAX)")
    void flagMaxLoadsAsTheLastIndex(String code) {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "flag"), d("code", 3, code)).getFirst();
        assertEquals(ObjectFlag.OF_MAX, p.getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    @ParameterizedTest(name = "[{index}] adjective:\"{0}\" is stored as written")
    @ValueSource(strings = {"fast", "fast moving", "Slow", "half-hearted", " foo", "foo ", " "})
    @DisplayName("B6: adjective: and neg-adjective: keep the whole value as written (C: a str takes the rest of the line)")
    void anAdjectiveIsStoredAsWritten(String value) {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "stat"),
                d("adjective", 3, value), d("neg-adjective", 4, value)).getFirst();
        assertEquals(value, p.getAdjective());
        assertEquals(value, p.getNegAdjective());
    }

    @Test
    @DisplayName("B6: a repeated adjective: or neg-adjective: replaces the earlier one")
    void aRepeatedAdjectiveKeepsTheLast() {
        ObjectProperty p = assemble(d("name", 1, "x"), d("type", 2, "stat"),
                d("adjective", 3, "first one"), d("adjective", 4, "Second"),
                d("neg-adjective", 5, "weak one"), d("neg-adjective", 6, "Weaker")).getFirst();
        assertEquals("Second", p.getAdjective());
        assertEquals("Weaker", p.getNegAdjective());
    }

    @ParameterizedTest(name = "[{index}] type:{0} code:{1} is refused")
    @CsvSource({"flag, NOT_A_FLAG", "flag, OF_NOT_A_FLAG", "flag, OM_NONE", "flag, OM_MAX", "flag, ELEM_NONE",
            "flag, ELEM_MAX", "mod, NOT_A_FLAG", "mod, OF_NONE", "mod, OF_MAX", "mod, ELEM_NONE", "mod, ELEM_MAX",
            "stat, OF_MAX", "resistance, NOT_A_FLAG", "resistance, OM_NONE", "resistance, OM_MAX",
            "resistance, OF_NONE", "resistance, OF_MAX", "ignore, OF_NONE", "immunity, OM_MAX"})
    @DisplayName("a name the selected table lacks is refused, whichever table it belongs to")
    void aNameTheTableLacksIsRefused(String type, String code) {
        assertThrows(DataFileParseException.class, () ->
                assemble(d("name", 1, "x"), d("type", 2, type), d("code", 3, code)));
    }
}
