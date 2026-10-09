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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyAssembler;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyParseRecord;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins how {@link ObjectPropertyAssembler} treats the {@code type:} token against
 * {@code parse_object_property_type}, {@code parse_object_property_code} and
 * {@code parse_object_property_subtype} in {@code obj-init.c}.
 *
 * <p>Records are built by hand so each case reaches the assembler with exactly the token
 * under test. C rejects any {@code type:} word outside its seven, and rejects a
 * {@code code:} that arrives before any type ({@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE}). In
 * both cases the record is dropped and the load carries on with the next one.
 *
 * <p>Class ObjectPropertyAssemblerTypeTest coded on 261009, commented in full on 261009.
 */
class ObjectPropertyAssemblerTypeTest {

    private static ObjectPropertyParseRecord record(String type, String subtype, String code, int line) {
        return new ObjectPropertyParseRecord("test", type, subtype, "", code, "", "",
                new HashMap<>(), "", "", "", "", "", "", "", "", line);
    }

    private static List<ObjectProperty> assemble(List<String> errors, ObjectPropertyParseRecord... records) {
        return new ObjectPropertyAssembler().assemble(List.of(records), errors);
    }

    /** The code resolves against the table the type selects, so each pairing is a distinct path. */
    @Test
    void everyRealType_assemblesAgainstItsOwnTable() {
        List<String> errors = new ArrayList<>();
        List<ObjectProperty> result = assemble(errors,
                record("stat", "", "STR", 1),
                record("mod", "", "STEALTH", 2),
                record("flag", "", "SLOW_DIGEST", 3),
                record("ignore", "", "ACID", 4),
                record("resistance", "", "ACID", 5),
                record("vulnerability", "", "FIRE", 6),
                record("immunity", "", "COLD", 7));

        assertEquals(List.of(), errors);
        assertEquals(List.of(
                        ObjPropertyType.OBJ_PROPERTY_STAT, ObjPropertyType.OBJ_PROPERTY_MOD,
                        ObjPropertyType.OBJ_PROPERTY_FLAG, ObjPropertyType.OBJ_PROPERTY_IGNORE,
                        ObjPropertyType.OBJ_PROPERTY_RESIST, ObjPropertyType.OBJ_PROPERTY_VULN,
                        ObjPropertyType.OBJ_PROPERTY_IMM),
                result.stream().map(ObjectProperty::getType).toList());
    }

    /** C: "max" falls through the whole streq chain to PARSE_ERROR_INVALID_PROPERTY. */
    @Test
    void maxType_isDroppedWithIllegalTypeError() {
        List<String> errors = new ArrayList<>();
        List<ObjectProperty> result = assemble(errors, record("max", "", "STR", 8));

        assertEquals(0, result.size());
        assertEquals(List.of("Object Property at line: 8 has an illegal type: max"), errors);
    }

    /** C: a record whose type is never set has prop->type == 0, so code: is MISSING_OBJ_PROP_TYPE. */
    @Test
    void emptyType_isDroppedWithNoTypeError() {
        List<String> errors = new ArrayList<>();
        List<ObjectProperty> result = assemble(errors, record("", "", "STR", 9));

        assertEquals(0, result.size());
        assertEquals(List.of("Object Property at line: 9 has no type"), errors);
    }

    @Test
    void unrecognizedTypes_areDroppedWithIllegalTypeError() {
        List<String> errors = new ArrayList<>();
        List<ObjectProperty> result = assemble(errors,
                record("resist", "", "ACID", 10),
                record("Stat", "", "STR", 11),
                record("none", "", "STR", 12));

        assertEquals(0, result.size());
        assertEquals(List.of(
                "Object Property at line: 10 has an illegal type: resist",
                "Object Property at line: 11 has an illegal type: Stat",
                "Object Property at line: 12 has an illegal type: none"), errors);
    }

    /** One bad record must not abort the load: the good records either side still assemble. */
    @Test
    void badTypeRecord_doesNotStopTheNextRecord() {
        List<String> errors = new ArrayList<>();
        List<ObjectProperty> result = assemble(errors,
                record("stat", "", "STR", 1),
                record("max", "", "STR", 2),
                record("", "", "STR", 3),
                record("flag", "", "SLOW_DIGEST", 4));

        assertEquals(2, result.size());
        assertSame(ObjPropertyType.OBJ_PROPERTY_STAT, result.get(0).getType());
        assertSame(ObjPropertyType.OBJ_PROPERTY_FLAG, result.get(1).getType());
        assertEquals(2, errors.size());
    }

    /** Only a flag may carry a subtype; the same token on a non-flag is dropped. */
    @Test
    void subtype_isAcceptedOnFlagOnly() {
        List<String> errors = new ArrayList<>();
        List<ObjectProperty> result = assemble(errors,
                record("flag", "sustain", "SUST_STR", 1),
                record("stat", "sustain", "STR", 2));

        assertEquals(1, result.size());
        assertSame(ObjectFlagType.OFT_SUST, result.get(0).getSubtype());
        assertEquals(List.of("Object Property at line: 2 has a subtype on a non flag type: sustain"), errors);
    }

    /** A code that is not in the table the type selects is dropped, not mis-resolved. */
    @Test
    void codeFromTheWrongTable_isDropped() {
        List<String> errors = new ArrayList<>();
        List<ObjectProperty> result = assemble(errors, record("flag", "", "STR", 5));

        assertEquals(0, result.size());
        assertTrue(errors.get(0).contains("an invalid code: STR"), errors.toString());
    }
}
