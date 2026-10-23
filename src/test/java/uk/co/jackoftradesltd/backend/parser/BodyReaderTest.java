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
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.player.PlayerBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link BodyReader}: file text -> {@code BodyLexer}/
 * {@code BodyGrammar} -> {@link uk.co.jackoftradesltd.backend.parser.body.BodyParseRecord}
 * -> {@link uk.co.jackoftradesltd.backend.parser.body.BodyAssembler} -> {@link PlayerBody}.
 *
 * <p>These pin the assembler's job: a {@code body:} line plus its {@code slot:} lines become one
 * {@link PlayerBody} holding an <em>ordered</em> list of {@link uk.co.jackoftradesltd.middle.player.EquipSlot}s,
 * each slot's {@code TYPE} resolved to an {@link EquipmentSlotsEnum}. Order is significant (slots are
 * index-addressed) and duplicate slot types are legal (the two {@code RING} fingers), so both are
 * asserted directly. The suite also covers the load-ending {@code DataFileParseException} on a bad
 * slot type (C's {@code parse_body_slot} returns {@code PARSE_ERROR_INVALID_FLAG}, which ends the
 * whole file), C's newest-first body order, and both error channels {@code GrammarDriver} threads
 * through {@link ParseResult}.
 *
 * <p>Class BodyReaderTest coded before 261023, updated on 261023 for the load-ending slot errors and
 * C's body order.
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

        assertEquals(EquipmentSlotsEnum.EQUIP_WEAPON, body.getSlot(0).getType());
        assertEquals("weapon", body.getSlot(0).getName());
        assertEquals(EquipmentSlotsEnum.EQUIP_BOW, body.getSlot(1).getType());
        // BODY_ARMOR proves the underscore-bearing slot type lexes and resolves.
        assertEquals(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, body.getSlot(6).getType());
        assertEquals("body", body.getSlot(6).getName());
        assertEquals(EquipmentSlotsEnum.EQUIP_BOOTS, body.getSlot(11).getType());
        assertEquals("feet", body.getSlot(11).getName());
    }

    @Test
    void duplicateRingSlotsAreBothKeptDistinctByName() throws IOException {
        // Two RING slots are legal; they are distinguished only by index and name.
        PlayerBody body = new BodyReader().parseWithResults(REAL_FILE).items().get(0);

        assertEquals(EquipmentSlotsEnum.EQUIP_RING, body.getSlot(2).getType());
        assertEquals("right hand", body.getSlot(2).getName());
        assertEquals(EquipmentSlotsEnum.EQUIP_RING, body.getSlot(3).getType());
        assertEquals("left hand", body.getSlot(3).getName());

        long rings = body.getSlots().stream()
                .filter(s -> s.getType() == EquipmentSlotsEnum.EQUIP_RING).count();
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

    // ---- Load-ending slot errors: C's parse_body_slot returns PARSE_ERROR_INVALID_FLAG ----

    @Test
    void invalidSlotTypeEndsTheLoad() {
        assertThrows(DataFileParseException.class, () -> load("bad-slot.txt", withHeader(1,
                "body:Weird\nslot:WEAPON:weapon\nslot:NOTASLOT:somewhere\n")));
    }

    @Test
    void oneBadBodyEndsTheLoadForTheGoodOneToo() {
        // C's first parser error ends the whole file, so there is no partial result.
        assertThrows(DataFileParseException.class, () -> load("mixed.txt", withHeader(2,
                "body:Good\nslot:WEAPON:weapon\n"
                        + "body:Bad\nslot:NOTASLOT:somewhere\n")));
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

    // ---- Reader contract and the C-derived table -------------------------

    @Test
    void theRealFileSlotsAgreeWithTheUpstreamFileLineForLine() throws IOException {
        // Expected pairs are the twelve slot: lines of Angband-4.2.6/lib/gamedata/body.txt, in order.
        String[][] upstream = {
                {"WEAPON", "weapon"}, {"BOW", "shooting"}, {"RING", "right hand"}, {"RING", "left hand"},
                {"AMULET", "neck"}, {"LIGHT", "light"}, {"BODY_ARMOR", "body"}, {"CLOAK", "back"},
                {"SHIELD", "arm"}, {"HAT", "head"}, {"GLOVES", "hands"}, {"BOOTS", "feet"}};

        PlayerBody body = new BodyReader().parseWithResults(REAL_FILE).items().get(0);

        assertEquals(upstream.length, body.getCount());
        for (int i = 0; i < upstream.length; i++) {
            assertEquals(EquipmentSlotsEnum.valueOf("EQUIP_" + upstream[i][0]), body.getSlot(i).getType(), "type " + i);
            assertEquals(upstream[i][1], body.getSlot(i).getName(), "name " + i);
        }
    }

    @Test
    void parseReturnsTheSameBodiesAsParseWithResults() throws IOException {
        List<PlayerBody> viaParse = new BodyReader().parse(REAL_FILE);
        List<PlayerBody> viaResults = new BodyReader().parseWithResults(REAL_FILE).items();

        assertEquals(1, viaParse.size());
        assertEquals(viaResults.get(0).getName(), viaParse.get(0).getName());
        assertEquals(viaResults.get(0).getCount(), viaParse.get(0).getCount());
    }

    @Test
    void parseReturnsNothingForAHardGrammarError() throws IOException {
        Path file = tempDir.resolve("hard.txt");
        Files.writeString(file, "body:Solo\nslot:WEAPON:weapon\n");

        assertTrue(new BodyReader().parse(file.toString()).isEmpty());
    }

    @Test
    void aMissingFileIsAnIOExceptionWhereCQuits() {
        assertThrows(IOException.class,
                () -> new BodyReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void bodiesComeBackNewestFirstAsCListsThem() throws IOException {
        // C's parse_body_body pushes each body on the front, so its list reads Third, Second, First.
        ParseResult<PlayerBody> result = load("order.txt", withHeader(3,
                "body:First\nslot:WEAPON:a\nbody:Second\nslot:BOW:b\nbody:Third\nslot:LIGHT:c\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("Third", "Second", "First"),
                result.items().stream().map(PlayerBody::getName).toList());
    }

    @Test
    void aBodyAndSlotNameKeepTheirSpacesAndColonsWhole() throws IOException {
        ParseResult<PlayerBody> result = load("names.txt", withHeader(1,
                "body:Two Words: more\nslot:RING:on the left hand\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("Two Words: more", result.items().get(0).getName());
        assertEquals("on the left hand", result.items().get(0).getSlot(0).getName());
    }

    @Test
    void theRecordCountMismatchMessageNamesBothCounts() throws IOException {
        ParseResult<PlayerBody> result = load("count-text.txt", withHeader(5,
                "body:Solo\nslot:WEAPON:weapon\n"));

        assertTrue(result.errors().stream().anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void slotTypeNoneEndsTheLoadAsInC() {
        // C: lookup_flag(slots, "NONE") returns 0, so parse_body_slot returns PARSE_ERROR_INVALID_FLAG.
        assertThrows(DataFileParseException.class,
                () -> load("none.txt", withHeader(1, "body:Odd\nslot:NONE:nothing\n")));
    }

    @Test
    void slotTypeMaxEndsTheLoadAsInC() {
        // C: "MAX" is not in the slots table, so lookup_flag returns 0 and the line is an invalid flag.
        assertThrows(DataFileParseException.class,
                () -> load("max.txt", withHeader(1, "body:Odd\nslot:MAX:nothing\n")));
    }

    // ---- Differences from C ------------------------------------------------

    @Test
    void aLowerCaseSlotTypeIsAHardLexerErrorWhereCReportsAnInvalidFlag() throws IOException {
        // Both fail the load; only the channel differs (C: parser error, Java: the lexer's SLOT_NAME is A-Z and _).
        ParseResult<PlayerBody> result = load("lower.txt", withHeader(1, "body:Odd\nslot:weapon:weapon\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aHeaderWithNoBodiesIsAHardErrorHereWhereCAcceptsAnEmptyFile() throws IOException {
        ParseResult<PlayerBody> result = load("header-only.txt", withHeader(0, ""));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
