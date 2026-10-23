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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord.Room;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.RoomAssembler;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.RoomProfile;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RoomAssembler}.
 *
 * <p>These construct {@link Room} records directly - no grammar, no reader - so names and number
 * texts the shipped file never holds can still reach the assembler.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>name resolution from {@code parse_profile_room} in {@code generate.c}, which walks
 *       {@code room_builders[]} (built from {@code list-rooms.h}, nineteen entries) with
 *       {@code streq}, so the match is exact and case-sensitive, and a miss is
 *       {@code PARSE_ERROR_NO_ROOM_FOUND}, checked before any number is read;</li>
 *   <li>number reading from {@code parser_getint}, whose field was filled by {@code strtol(tok,
 *       &z, 0)} in {@code parser.c}: base 0, so a leading {@code 0} is octal, digits after the
 *       last valid one are ignored ({@code 08} is 0), and only text with no digit at all is
 *       {@code PARSE_ERROR_NOT_NUMBER};</li>
 *   <li>{@code pit} from {@code r->pit = (parser_getint(p, "pit") == 1)}, a compare of the parsed
 *       value;</li>
 *   <li>order from {@code parse_profile_room} appending each room at the tail of the list;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>Class RoomAssemblerTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class RoomAssemblerTest {

    private static final String FILE = "dungeon_profile.txt";

    private static Room room(String name, String rating, String height, String width, String level,
                             String pit, String rarity, String cutoff, int line) {
        return new Room(name, rating, height, width, level, pit, rarity, cutoff, line);
    }

    private static List<RoomProfile> assemble(List<Room> records) {
        return new RoomAssembler().assemble(records, new ArrayList<>(), FILE);
    }

    private static RoomProfile one(Room record) {
        List<RoomProfile> rooms = assemble(List.of(record));
        assertEquals(1, rooms.size());
        return rooms.getFirst();
    }

    private static RoomProfile withPit(String pit) {
        return one(room("monster pit", "0", "11", "33", "5", pit, "2", "8", 97));
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void shippedPitLineAssemblesEveryField() {
        // dungeon_profile.txt: room:monster pit:0:11:33:5:1:2:8
        RoomProfile r = one(room("monster pit", "0", "11", "33", "5", "1", "2", "8", 97));
        assertEquals("monster pit", r.getName());
        assertEquals(RoomType.PIT, r.getRoomType());
        assertEquals(0, r.getRating());
        assertEquals(11, r.getHeight());
        assertEquals(33, r.getWidth());
        assertEquals(5, r.getLevel());
        assertTrue(r.isPit());
        assertEquals(2, r.getRarity());
        assertEquals(8, r.getCutoff());
    }

    @Test
    void eachNumberLandsInItsOwnField() {
        // Seven distinct values, so a transposed pair cannot pass.
        RoomProfile r = one(room("simple room", "11", "12", "13", "14", "0", "16", "17", 1));
        assertEquals(11, r.getRating());
        assertEquals(12, r.getHeight());
        assertEquals(13, r.getWidth());
        assertEquals(14, r.getLevel());
        assertFalse(r.isPit());
        assertEquals(16, r.getRarity());
        assertEquals(17, r.getCutoff());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "staircase room|STAIRCASE",
            "simple room|SIMPLE",
            "moria room|MORIA",
            "large room|LARGE",
            "crossed room|CROSSED",
            "circular room|CIRCULAR",
            "overlap room|OVERLAP",
            "room template|TEMPLATE",
            "Interesting room|INTERESTING",
            "monster pit|PIT",
            "monster nest|NEST",
            "huge room|HUGE",
            "room of chambers|CHAMBERS",
            "Lesser vault|LESSER_VAULT",
            "Medium vault|MEDIUM_VAULT",
            "Greater vault|GREATER_VAULT",
            "Lesser vault (new)|NEW_LESSER_VAULT",
            "Medium vault (new)|NEW_MEDIUM_VAULT",
            "Greater vault (new)|NEW_GREATER_VAULT"
    })
    void everyListRoomsNameResolves(String name, RoomType expected) {
        RoomProfile r = one(room(name, "0", "11", "33", "1", "0", "0", "100", 1));
        assertEquals(expected, r.getRoomType());
        assertEquals(name, r.getName());
    }

    @Test
    void emptyInputGivesEmptyList() {
        assertTrue(assemble(List.of()).isEmpty());
    }

    @Test
    void fileOrderIsKeptAndRepeatsAreAllowed() {
        List<RoomProfile> rooms = assemble(List.of(
                room("Greater vault", "0", "44", "66", "35", "0", "0", "100", 94),
                room("simple room", "0", "11", "33", "1", "0", "0", "10", 95),
                room("Greater vault", "0", "44", "66", "40", "0", "0", "20", 96)));
        assertEquals(3, rooms.size());
        assertEquals(RoomType.GREATER_VAULT, rooms.get(0).getRoomType());
        assertEquals(100, rooms.get(0).getCutoff());
        assertEquals(RoomType.SIMPLE, rooms.get(1).getRoomType());
        assertEquals(RoomType.GREATER_VAULT, rooms.get(2).getRoomType());
        assertEquals(20, rooms.get(2).getCutoff());
    }

    // ---- pit: a compare of the parsed value with 1 -------------------------

    @ParameterizedTest
    @ValueSource(strings = {"1", "01", "001"})
    void pitIsTrueWhenTheValueIsOne(String pit) {
        assertTrue(withPit(pit).isPit());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "2", "-1", "11", "10", "08"})
    void pitIsFalseForAnyOtherValue(String pit) {
        // 08 in base 0 is 0; 10 and 11 are ten and eleven, not 1.
        assertFalse(withPit(pit).isPit());
    }

    // ---- Number reading: strtol base 0 -------------------------------------

    @Test
    void leadingZeroIsOctal() {
        RoomProfile r = one(room("simple room", "010", "011", "033", "07", "0", "00", "0144", 1));
        assertEquals(8, r.getRating());
        assertEquals(9, r.getHeight());
        assertEquals(27, r.getWidth());
        assertEquals(7, r.getLevel());
        assertEquals(0, r.getRarity());
        assertEquals(100, r.getCutoff());
    }

    @Test
    void octalReadingStopsAtTheFirstNonOctalDigit() {
        // strtol("08", &z, 0) and strtol("019", &z, 0) read "0" and "01" and stop there.
        RoomProfile r = one(room("simple room", "08", "019", "33", "1", "0", "0", "100", 1));
        assertEquals(0, r.getRating());
        assertEquals(1, r.getHeight());
    }

    @Test
    void negativeValuesAreKept() {
        RoomProfile r = one(room("simple room", "-1", "-11", "-33", "-5", "0", "-2", "-010", 1));
        assertEquals(-1, r.getRating());
        assertEquals(-11, r.getHeight());
        assertEquals(-33, r.getWidth());
        assertEquals(-5, r.getLevel());
        assertEquals(-2, r.getRarity());
        assertEquals(-8, r.getCutoff());
    }

    // ---- Faults end the load -----------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {"interesting room", "SIMPLE ROOM", "simple room ", " simple room",
            "simple", "vault", "Lesser Vault", "Lesser vault(new)", "pit", "NONE", ""})
    void unknownRoomNameEndsTheLoad(String name) {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(room(name, "0", "11", "33", "1", "0", "0", "100", 42))));
        assertTrue(e.getMessage().contains("42"), e.getMessage());
        assertTrue(e.getMessage().contains(FILE), e.getMessage());
    }

    @Test
    void nameIsCheckedBeforeTheNumbers() {
        // C returns PARSE_ERROR_NO_ROOM_FOUND before any parser_getint, so a bad name wins over a
        // bad number on the same line.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> assemble(List.of(room("no such room", "x", "x", "x", "x", "x", "x", "x", 7))));
        assertTrue(e.getMessage().contains("room type"), e.getMessage());
    }

    @Test
    void numberWithNoDigitsEndsTheLoad() {
        // strtol leaves z == tok, so C returns PARSE_ERROR_NOT_NUMBER. The grammar's INTEGER token
        // keeps this out of a real load; the assembler must still refuse it.
        assertThrows(DataFileParseException.class,
                () -> assemble(List.of(room("simple room", "0", "11", "x", "1", "0", "0", "100", 1))));
    }

    @Test
    void aFaultAfterGoodLinesStillEndsTheLoad() {
        assertThrows(DataFileParseException.class, () -> assemble(List.of(
                room("simple room", "0", "11", "33", "1", "0", "0", "100", 1),
                room("bogus room", "0", "11", "33", "1", "0", "0", "100", 2))));
    }

    @Test
    void errorsListIsLeftUntouched() {
        List<String> errors = new ArrayList<>();
        new RoomAssembler().assemble(List.of(room("simple room", "0", "11", "33", "1", "0", "0", "100", 1)),
                errors, FILE);
        assertTrue(errors.isEmpty());
    }
}
