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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.body.BodyAssembler;
import uk.co.jackoftradesltd.backend.parser.body.BodyParseRecord;
import uk.co.jackoftradesltd.backend.parser.body.BodyParseRecord.BodySlotRecord;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.player.PlayerBody;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BodyAssembler}.
 *
 * <p>These construct {@link BodyParseRecord}s directly - no grammar, no reader - so slot types the
 * lexer would never produce (lower case, an {@code EQUIP_} prefix) can still reach the assembler.
 * End-to-end cases live in {@code BodyReaderTest}.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>slot resolution from {@code parse_body_slot} in {@code init.c}, which calls
 *       {@code lookup_flag} in {@code datafile.c} against the {@code slots[]} table built from
 *       {@code list-equip-slots.h}. The search starts at {@code FLAG_START} (1), so {@code NONE} at
 *       index 0 is never matched; {@code MAX} is not in the table; the compare is {@code streq},
 *       so case-sensitive; and any miss returns 0, which is {@code PARSE_ERROR_INVALID_FLAG};</li>
 *   <li>slot order from {@code parse_body_slot} appending each slot at the tail, and the count
 *       from its {@code b->count++} per slot;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>Class BodyAssemblerTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class BodyAssemblerTest {

    private static final String FILE = "body.txt";

    private static BodySlotRecord slot(String type, String name) {
        return new BodySlotRecord(type, name);
    }

    private static List<PlayerBody> assemble(List<BodyParseRecord> records, List<String> errors) {
        return new BodyAssembler().assemble(records, errors, FILE);
    }

    private static PlayerBody one(String type) {
        List<PlayerBody> bodies = assemble(
                List.of(new BodyParseRecord("Solo", List.of(slot(type, "somewhere")), 2)), new ArrayList<>());
        assertEquals(1, bodies.size());
        return bodies.getFirst();
    }

    // ---- Ordinary path -----------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {"WEAPON", "BOW", "RING", "AMULET", "LIGHT", "BODY_ARMOR", "CLOAK", "SHIELD",
            "HAT", "GLOVES", "BOOTS"})
    void everySlotNameInCsTableAfterNoneResolves(String type) {
        // Indices 1-11 of C's slots[]: lookup_flag finds each one and returns a non-zero index.
        assertEquals(EquipmentSlotsEnum.valueOf("EQUIP_" + type), one(type).getSlot(0).getType());
    }

    @Test
    void theHumanoidBodyKeepsItsSlotsInFileOrderWithTheCountC_Gives() {
        // The twelve slot: lines of lib/gamedata/body.txt; C appends each and counts twelve.
        String[][] upstream = {
                {"WEAPON", "weapon"}, {"BOW", "shooting"}, {"RING", "right hand"}, {"RING", "left hand"},
                {"AMULET", "neck"}, {"LIGHT", "light"}, {"BODY_ARMOR", "body"}, {"CLOAK", "back"},
                {"SHIELD", "arm"}, {"HAT", "head"}, {"GLOVES", "hands"}, {"BOOTS", "feet"}};
        List<BodySlotRecord> slots = new ArrayList<>();
        for (String[] pair : upstream) {
            slots.add(slot(pair[0], pair[1]));
        }
        List<String> errors = new ArrayList<>();

        PlayerBody body = assemble(List.of(new BodyParseRecord("Humanoid", slots, 2)), errors).getFirst();

        assertEquals("Humanoid", body.getName());
        assertEquals(12, body.getCount());
        for (int i = 0; i < upstream.length; i++) {
            assertEquals(EquipmentSlotsEnum.valueOf("EQUIP_" + upstream[i][0]), body.getSlot(i).getType(), "type " + i);
            assertEquals(upstream[i][1], body.getSlot(i).getName(), "name " + i);
        }
        assertTrue(errors.isEmpty());
    }

    @Test
    void aRepeatedSlotTypeIsKeptAsTwoSlots() {
        PlayerBody body = assemble(List.of(new BodyParseRecord("Hands",
                List.of(slot("RING", "right hand"), slot("RING", "left hand")), 2)), new ArrayList<>()).getFirst();

        assertEquals(2, body.getCount());
        assertEquals(EquipmentSlotsEnum.EQUIP_RING, body.getSlot(0).getType());
        assertEquals(EquipmentSlotsEnum.EQUIP_RING, body.getSlot(1).getType());
        assertEquals("right hand", body.getSlot(0).getName());
        assertEquals("left hand", body.getSlot(1).getName());
    }

    @Test
    void bodiesComeBackInTheOrderTheyArriveWithoutReordering() {
        // The grammar already supplies C's newest-first order; the assembler must not undo it.
        List<PlayerBody> bodies = assemble(List.of(
                new BodyParseRecord("Third", List.of(slot("LIGHT", "c")), 6),
                new BodyParseRecord("Second", List.of(slot("BOW", "b")), 4),
                new BodyParseRecord("First", List.of(slot("WEAPON", "a")), 2)), new ArrayList<>());

        assertEquals(List.of("Third", "Second", "First"), bodies.stream().map(PlayerBody::getName).toList());
    }

    @Test
    void namesAreCopiedVerbatim() {
        PlayerBody body = assemble(List.of(new BodyParseRecord("Two Words: more",
                List.of(slot("RING", "on the left hand")), 2)), new ArrayList<>()).getFirst();

        assertEquals("Two Words: more", body.getName());
        assertEquals("on the left hand", body.getSlot(0).getName());
    }

    @Test
    void noRecordsGiveNoBodies() {
        assertTrue(assemble(List.of(), new ArrayList<>()).isEmpty());
    }

    // ---- Load-ending errors ------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {
            "NONE",          // index 0 of slots[]: lookup_flag starts at FLAG_START (1) and never sees it
            "MAX",           // not in slots[] at all
            "NOTASLOT",      // not in slots[]
            "weapon",        // streq is case-sensitive
            "Weapon",
            "EQUIP_WEAPON",  // slots[] holds the bare names, so the prefix is not a match
            ""})
    void aSlotTypeCsLookupFlagCannotFindEndsTheLoad(String type) {
        List<String> errors = new ArrayList<>();
        List<BodyParseRecord> records = List.of(new BodyParseRecord("Odd", List.of(slot(type, "x")), 2));

        assertThrows(DataFileParseException.class, () -> assemble(records, errors));
        assertTrue(errors.isEmpty(), "errors are thrown, not collected");
    }

    @Test
    void aBadSlotInALaterBodyEndsTheWholeLoadNotJustThatBody() {
        // C's first parser error ends the file; there is no partial result.
        List<BodyParseRecord> records = List.of(
                new BodyParseRecord("Good", List.of(slot("WEAPON", "weapon")), 2),
                new BodyParseRecord("Bad", List.of(slot("WEAPON", "weapon"), slot("NOTASLOT", "x")), 4));

        assertThrows(DataFileParseException.class, () -> assemble(records, new ArrayList<>()));
    }

    @Test
    void theErrorMessageNamesTheLineFileAndBadSlot() {
        List<BodyParseRecord> records = List.of(new BodyParseRecord("Odd", List.of(slot("NOTASLOT", "x")), 17));

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> assemble(records, new ArrayList<>()));

        assertTrue(e.getMessage().contains("line: 17"), e.getMessage());
        assertTrue(e.getMessage().contains(FILE), e.getMessage());
        assertTrue(e.getMessage().contains("NOTASLOT"), e.getMessage());
    }
}
