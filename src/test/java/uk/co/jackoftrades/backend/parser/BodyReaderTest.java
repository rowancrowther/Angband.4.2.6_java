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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.player.PlayerBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link BodyReader}: file text -> {@code BodyLexer}/
 * {@code BodyGrammar} -> {@link uk.co.jackoftrades.backend.parser.body.BodyParseRecord}
 * -> {@link uk.co.jackoftrades.backend.parser.body.BodyAssembler} -> {@link PlayerBody}.
 *
 * <p>These pin the assembler's job: a {@code body:} line plus its {@code slot:} lines become one
 * {@link PlayerBody} holding an <em>ordered</em> list of {@link uk.co.jackoftrades.middle.player.EquipSlot}s,
 * each slot's {@code TYPE} resolved to an {@link EquipmentSlotsEnum}. Order is significant (slots are
 * index-addressed) and duplicate slot types are legal (the two {@code RING} fingers), so both are
 * asserted directly. The suite also covers the whole-body rejection on a bad slot type (a corrupt
 * slot would shift every later index, so the body is dropped rather than half-built) and both error
 * channels {@code GrammarDriver} threads through {@link ParseResult}.
 *
 * <p>No registry seeding is required — a body resolves entirely against a compile-time enum, with no
 * cross-file references, so nothing needs {@code GameConstants} loaded first.
 *
 * @author Rowan Crowther
 */
class BodyReaderTest {

    private static final String REAL_FILE = "lib/gamedata/body.txt";

    @TempDir
    Path tempDir;

    // ---- fixture helpers -------------------------------------------------

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    private ParseResult<PlayerBody> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new BodyReader().parseWithResults(file.toString());
    }

    private static PlayerBody byName(List<PlayerBody> bodies, String name) {
        return bodies.stream().filter(b -> b.getName().equals(name)).findFirst().orElse(null);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsOneHumanoidBodyWithNoErrors() throws IOException {
        ParseResult<PlayerBody> result = new BodyReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());

        PlayerBody humanoid = result.items().get(0);
        assertEquals("Humanoid", humanoid.getName());
        assertEquals(12, humanoid.getCount());
    }

    @Test
    void slotsKeepDeclaredOrderWithResolvedTypesAndNames() throws IOException {
        // Position is the slot's identity, so the exact index -> (type, name) mapping matters.
        PlayerBody body = new BodyReader().parseWithResults(REAL_FILE).items().get(0);

        assertEquals(EquipmentSlotsEnum.EQUIP_WEAPON, body.getSlot(0).type());
        assertEquals("weapon", body.getSlot(0).name());
        assertEquals(EquipmentSlotsEnum.EQUIP_BOW, body.getSlot(1).type());
        // BODY_ARMOR proves the underscore-bearing slot type lexes and resolves.
        assertEquals(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, body.getSlot(6).type());
        assertEquals("body", body.getSlot(6).name());
        assertEquals(EquipmentSlotsEnum.EQUIP_BOOTS, body.getSlot(11).type());
        assertEquals("feet", body.getSlot(11).name());
    }

    @Test
    void duplicateRingSlotsAreBothKeptDistinctByName() throws IOException {
        // Two RING slots are legal; they are distinguished only by index and name.
        PlayerBody body = new BodyReader().parseWithResults(REAL_FILE).items().get(0);

        assertEquals(EquipmentSlotsEnum.EQUIP_RING, body.getSlot(2).type());
        assertEquals("right hand", body.getSlot(2).name());
        assertEquals(EquipmentSlotsEnum.EQUIP_RING, body.getSlot(3).type());
        assertEquals("left hand", body.getSlot(3).name());

        long rings = body.getSlots().stream()
                .filter(s -> s.type() == EquipmentSlotsEnum.EQUIP_RING).count();
        assertEquals(2, rings);
    }

    @Test
    void multipleBodiesEachLoadWithTheirOwnSlots() throws IOException {
        ParseResult<PlayerBody> result = load("two-bodies.txt", withHeader(2,
                "body:First\nslot:WEAPON:weapon\n"
                        + "body:Second\nslot:AMULET:neck\nslot:LIGHT:light\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals(1, byName(result.items(), "First").getCount());
        assertEquals(2, byName(result.items(), "Second").getCount());
    }

    // ---- Soft errors: skip-and-continue, partial results survive ---------

    @Test
    void invalidSlotTypeRejectsTheWholeBody() throws IOException {
        // A bad slot type would shift every later slot's index, so the entire body is dropped.
        ParseResult<PlayerBody> result = load("bad-slot.txt", withHeader(1,
                "body:Weird\nslot:WEAPON:weapon\nslot:NOTASLOT:somewhere\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("Invalid equipment slot type")),
                result.errors()::toString);
    }

    @Test
    void oneBadBodyIsDroppedButOtherBodiesStillLoad() throws IOException {
        ParseResult<PlayerBody> result = load("mixed.txt", withHeader(2,
                "body:Good\nslot:WEAPON:weapon\n"
                        + "body:Bad\nslot:NOTASLOT:somewhere\n"));

        assertEquals(1, result.items().size());
        assertNotNull(byName(result.items(), "Good"));
        assertNull(byName(result.items(), "Bad"));
        assertTrue(result.hasErrors());
    }

    @Test
    void recordCountMismatchIsReportedButValidBodyStillLoads() throws IOException {
        ParseResult<PlayerBody> result = load("bad-count.txt", withHeader(5,
                "body:Solo\nslot:WEAPON:weapon\n"));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    // ---- Hard errors: fail closed, nothing loads -------------------------

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<PlayerBody> result = load("no-header.txt", "body:Solo\nslot:WEAPON:weapon\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void bodyWithNoSlotsIsAHardError() throws IOException {
        // The grammar requires at least one slot per body.
        ParseResult<PlayerBody> result = load("no-slots.txt", withHeader(1, "body:Empty\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void slotWithoutAPrecedingBodyIsAHardError() throws IOException {
        // A record must open with a body: line; a leading slot has nothing to attach to.
        ParseResult<PlayerBody> result = load("orphan-slot.txt", withHeader(1, "slot:WEAPON:weapon\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
