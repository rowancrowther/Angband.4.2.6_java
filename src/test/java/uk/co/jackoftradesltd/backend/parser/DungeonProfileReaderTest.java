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
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.CaveProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.RoomProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.StreamerProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.TunnelProfile;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link DungeonProfileReader}: {@code dungeon_profile.txt} text ->
 * {@code DungeonProfileLexer}/{@code DungeonProfileGrammar} -> {@code DungeonProfileParseRecord} ->
 * {@code DungeonProfileAssembler} (plus its three sub-assemblers) -> {@link CaveProfile}.
 *
 * <p>Assertions are on the assembled {@link CaveProfile}s rather than the intermediate DTOs, since
 * that is where the file stops being text: the sub-records become {@link TunnelProfile} /
 * {@link StreamerProfile} / {@link RoomProfile}, and a room name becomes a {@link RoomType} (the
 * port's stand-in for C's {@code room_builder} function pointer).
 *
 * <p>The happy path runs against the real shipped file. The error paths build one-defect fixtures in
 * a temp dir. Note which channel each defect lands on: the lexer's {@code INTEGER} token is
 * {@code '-'? digit+}, so a non-numeric field is a <em>grammar</em> error, not an assembly one — the
 * only way to reach the assembler's {@code NumberFormatException} handling is a value that overflows
 * {@code int}. Those are soft: the offending profile is dropped and the rest of the file still loads
 * (the partial-results contract), which is deliberately weaker than C, where {@code parser_getint}
 * makes a bad number fatal.
 *
 * @author Rowan Crowther
 */
class DungeonProfileReaderTest {

    /**
     * The real shipped data file, relative to the Gradle working directory (project root).
     */
    private static final String REAL_FILE = "lib/gamedata/dungeon_profile.txt";

    /**
     * The file's {@code record-count:} header, and the number of {@code name:} records in it.
     */
    private static final int EXPECTED_PROFILES = 9;

    @TempDir
    Path tempDir;

    private static CaveProfile byName(List<CaveProfile> profiles, String name) {
        return profiles.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no profile named " + name));
    }

    /**
     * Writes {@code content} to a file in the temp dir and returns its absolute path.
     */
    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAllProfiles() throws IOException {
        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());

        List<CaveProfile> profiles = result.items();
        assertEquals(EXPECTED_PROFILES, profiles.size());

        // File order is preserved - the level generator walks these in order.
        assertEquals(
                List.of("town", "labyrinth", "cavern", "classic", "modified", "moria", "lair",
                        "gauntlet", "hard centre"),
                profiles.stream().map(CaveProfile::getName).toList());
    }

    /**
     * The town: the one profile whose directives are out of the usual order ({@code streamer:}
     * before {@code params:}), with no tunnel and no rooms at all.
     */
    @Test
    void townTakesItsDirectivesInAnyOrderAndHasNoTunnelOrRooms() throws IOException {
        CaveProfile town = byName(new DungeonProfileReader().parseWithResults(REAL_FILE).items(), "town");

        assertEquals(1, town.getBlockSize());
        assertEquals(0, town.getDunRooms());
        assertEquals(200, town.getDunUnusual());
        assertEquals(0, town.getMaxRarity());

        assertNull(town.getTun(), "town has no tunnel: line");
        assertNotNull(town.getStr(), "town's streamer: line precedes its params: line");
        assertEquals(1, town.getStr().getDen());
        assertEquals(1, town.getStr().getRng());
        assertEquals(0, town.getStr().getMam());

        assertTrue(town.getRoomProfiles().isEmpty());
        // alloc:-1 - reachable only by generate.c's hard-coded profile tests.
        assertEquals(-1, town.getAlloc());
        // No min-level: line, so the assembler's default stands.
        assertEquals(0, town.getMinLevel());
    }

    /**
     * The classic profile carries all four numeric directives, so it pins every field of
     * {@link TunnelProfile} and {@link StreamerProfile} against the file at once.
     */
    @Test
    void classicResolvesEveryDirective() throws IOException {
        CaveProfile classic = byName(new DungeonProfileReader().parseWithResults(REAL_FILE).items(), "classic");

        // params:11:50:200:2
        assertEquals(11, classic.getBlockSize());
        assertEquals(50, classic.getDunRooms());
        assertEquals(200, classic.getDunUnusual());
        assertEquals(2, classic.getMaxRarity());

        // tunnel:10:30:15:25:50
        TunnelProfile tunnel = classic.getTun();
        assertNotNull(tunnel);
        assertEquals(10, tunnel.getRnd());
        assertEquals(30, tunnel.getChg());
        assertEquals(15, tunnel.getCon());
        assertEquals(25, tunnel.getPen());
        assertEquals(50, tunnel.getJct());

        // streamer:5:2:3:90:2:40
        StreamerProfile streamer = classic.getStr();
        assertNotNull(streamer);
        assertEquals(5, streamer.getDen());
        assertEquals(2, streamer.getRng());
        assertEquals(3, streamer.getMam());
        assertEquals(90, streamer.getMc());
        assertEquals(2, streamer.getQua());
        assertEquals(40, streamer.getQc());

        assertEquals(90, classic.getAlloc());
        assertEquals(14, classic.getRoomProfiles().size());
    }

    /**
     * Every {@code room:} line in the file resolves to a {@link RoomType}. The assembler does not
     * check for a failed lookup (see {@link #unknownRoomNameLeavesANullRoomTypeAndNoError()}), so
     * this is the assertion that the enum's names still agree with the data.
     */
    @Test
    void everyRoomNameInTheRealFileResolvesToARoomType() throws IOException {
        List<CaveProfile> profiles = new DungeonProfileReader().parseWithResults(REAL_FILE).items();

        for (CaveProfile profile : profiles) {
            for (RoomProfile room : profile.getRoomProfiles()) {
                assertNotNull(room.getRoomType(),
                        () -> "unresolved room name '" + room.getName() + "' in profile " + profile.getName());
            }
        }
    }

    /**
     * Rooms keep file order, and the fields of the two special cases are read across correctly: the
     * pit flag (a 0/1 int in the file, a bool in C, compared rather than parsed here) and the
     * staircase room's rarity 99, which exists to keep it out of the random draw.
     */
    @Test
    void classicRoomsKeepFileOrderAndNarrowTheirFieldsCorrectly() throws IOException {
        List<RoomProfile> rooms =
                byName(new DungeonProfileReader().parseWithResults(REAL_FILE).items(), "classic").getRoomProfiles();

        // room:Greater vault:0:44:66:35:0:0:100 - first in the file, so first here.
        RoomProfile greater = rooms.getFirst();
        assertEquals("Greater vault", greater.getName());
        assertEquals(RoomType.GREATER_VAULT, greater.getRoomType());
        assertEquals(0, greater.getRating());
        assertEquals(44, greater.getHeight());
        assertEquals(66, greater.getWidth());
        assertEquals(35, greater.getLevel());
        assertFalse(greater.isPit());
        assertEquals(0, greater.getRarity());
        assertEquals(100, greater.getCutoff());

        // room:monster pit:0:11:33:5:1:2:8 - the pit field is the only true in the profile.
        RoomProfile pit = rooms.get(1);
        assertEquals(RoomType.PIT, pit.getRoomType());
        assertTrue(pit.isPit(), "monster pit is the one room with pit set");

        // The three "room template" lines differ only by rating, and are ordered 1, 2, 3.
        List<RoomProfile> templates = rooms.stream()
                .filter(r -> r.getRoomType() == RoomType.TEMPLATE)
                .toList();
        assertEquals(List.of(1, 2, 3), templates.stream().map(RoomProfile::getRating).toList());

        // room:staircase room:0:3:3:1:0:99:0 - last, rarity 99 keeps it out of the random draw.
        RoomProfile staircase = rooms.getLast();
        assertEquals(RoomType.STAIRCASE, staircase.getRoomType());
        assertEquals(99, staircase.getRarity());
        assertEquals(0, staircase.getCutoff());
    }

    /**
     * {@code min-level:} is present on only four profiles; the rest fall back to 0.
     */
    @Test
    void minLevelIsReadWhenPresentAndDefaultsToZeroOtherwise() throws IOException {
        List<CaveProfile> profiles = new DungeonProfileReader().parseWithResults(REAL_FILE).items();

        assertEquals(15, byName(profiles, "cavern").getMinLevel());
        assertEquals(20, byName(profiles, "lair").getMinLevel());
        assertEquals(20, byName(profiles, "gauntlet").getMinLevel());
        assertEquals(50, byName(profiles, "hard centre").getMinLevel());

        assertEquals(0, byName(profiles, "classic").getMinLevel());
        assertEquals(0, byName(profiles, "modified").getMinLevel());
    }

    // ---- Fixtures: defaults and error paths -------------------------------

    /**
     * A profile with nothing but its {@code name:} line. The three optional sub-records stay null
     * rather than becoming empty objects, and the numeric fields take the assembler's sentinels —
     * the distinction C cannot make, since {@code mem_zalloc} leaves an absent field at 0 exactly as
     * an explicit 0 would.
     */
    @Test
    void absentDirectivesLeaveSentinelsRatherThanZeroes() throws IOException {
        String path = tempFile("bare.txt", String.join("\n",
                "record-count:1",
                "name:bare",
                "alloc:5",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        CaveProfile bare = result.items().getFirst();

        // No params: line - the four numbers keep the assembler's -1, not 0.
        assertEquals(-1, bare.getBlockSize());
        assertEquals(-1, bare.getDunRooms());
        assertEquals(-1, bare.getDunUnusual());
        assertEquals(-1, bare.getMaxRarity());

        assertNull(bare.getTun());
        assertNull(bare.getStr());
        assertTrue(bare.getRoomProfiles().isEmpty());
        assertEquals(0, bare.getMinLevel());
        assertEquals(5, bare.getAlloc());
    }

    /**
     * A repeated single-valued directive: the last one wins, mirroring C, where each callback simply
     * overwrites the field.
     */
    @Test
    void aRepeatedDirectiveIsOverwrittenByItsLastOccurrence() throws IOException {
        String path = tempFile("repeat.txt", String.join("\n",
                "record-count:1",
                "name:repeat",
                "alloc:1",
                "alloc:2",
                "params:1:0:200:0",
                "params:2:0:100:1",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        CaveProfile profile = result.items().getFirst();
        assertEquals(2, profile.getAlloc());
        assertEquals(2, profile.getBlockSize());
        assertEquals(100, profile.getDunUnusual());
    }

    /**
     * A wrong {@code record-count:} is a soft error here: it is reported, but every profile that
     * parsed still loads. C has no such header at all, so this check is a port-ism.
     */
    @Test
    void recordCountMismatchIsReportedButProfilesStillLoad() throws IOException {
        String path = tempFile("bad-count.txt", String.join("\n",
                "record-count:5",
                "name:only one",
                "alloc:1",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertEquals(1, result.items().size(), "the count check must not cost the file its records");
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    /**
     * A number too large for {@code int} is the only way past the lexer's {@code INTEGER} token, so
     * it is what reaches the assembler's {@code NumberFormatException} handling. The offending
     * profile is dropped and reported; the profiles either side of it still load.
     */
    @Test
    void anOverflowingParamsValueDropsOnlyItsOwnProfile() throws IOException {
        String path = tempFile("overflow-params.txt", String.join("\n",
                "record-count:3",
                "name:before",
                "alloc:1",
                "name:broken",
                "params:99999999999:0:200:0",
                "alloc:1",
                "name:after",
                "alloc:1",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertEquals(List.of("before", "after"), result.items().stream().map(CaveProfile::getName).toList());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("invalid param line") && e.contains("99999999999")),
                result.errors()::toString);
        // The count check still runs, and still counts the record that was parsed but not assembled.
        assertFalse(result.errors().stream().anyMatch(e -> e.contains("record-count header")),
                result.errors()::toString);
    }

    /**
     * The same fate for an overflowing {@code alloc:} or {@code min-level:} value, which the
     * assembler converts on its own rather than through a sub-assembler.
     */
    @Test
    void anOverflowingAllocOrMinLevelDropsItsProfile() throws IOException {
        String path = tempFile("overflow-alloc.txt", String.join("\n",
                "record-count:2",
                "name:bad alloc",
                "alloc:99999999999",
                "name:bad min level",
                "min-level:99999999999",
                "alloc:1",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertTrue(result.items().isEmpty(), result.items()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid alloc line")),
                result.errors()::toString);
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid min level line")),
                result.errors()::toString);
    }

    /**
     * An overflowing field on a {@code room:} line costs that room, not the whole profile: the room
     * sub-assembler reports and skips, and the profile is still built from the rooms that converted.
     */
    @Test
    void anOverflowingRoomFieldDropsOnlyThatRoom() throws IOException {
        String path = tempFile("overflow-room.txt", String.join("\n",
                "record-count:1",
                "name:classic",
                "params:11:50:200:2",
                "alloc:90",
                "room:simple room:0:11:33:1:0:0:100",
                "room:large room:0:11:33:99999999999:0:1:15",
                "room:staircase room:0:3:3:1:0:99:0",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        List<RoomProfile> rooms = result.items().getFirst().getRoomProfiles();
        assertEquals(List.of("simple room", "staircase room"),
                rooms.stream().map(RoomProfile::getName).toList());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("invalid level integer") && e.contains("99999999999")),
                result.errors()::toString);
    }

    /**
     * A gap in the port, pinned here so it is noticed if it is ever closed: an unknown room name
     * silently yields a {@link RoomProfile} with a null {@link RoomType}, with nothing appended to
     * the errors. C's {@code parse_profile_room} (generate.c:150) instead fails the parse outright
     * when the name matches no entry in {@code room_builders[]}.
     */
    @Test
    void unknownRoomNameLeavesANullRoomTypeAndNoError() throws IOException {
        String path = tempFile("unknown-room.txt", String.join("\n",
                "record-count:1",
                "name:classic",
                "alloc:1",
                "room:no such room:0:11:33:1:0:0:100",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        RoomProfile room = result.items().getFirst().getRoomProfiles().getFirst();
        assertEquals("no such room", room.getName());
        assertNull(room.getRoomType(), "an unresolved room name is currently not reported at all");
    }

    /**
     * A missing {@code record-count:} header is a grammar error, so it goes down the hard channel:
     * {@link ParseErrors#throwIfAny()} fires and nothing loads.
     */
    @Test
    void missingRecordCountHeaderFailsClosed() throws IOException {
        String path = tempFile("no-header.txt", "name:town\nalloc:-1\n");

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * A {@code name:} line with no directives after it is a grammar error too — the {@code profile}
     * rule requires at least one — so the whole file fails closed rather than yielding an empty
     * profile.
     */
    @Test
    void aProfileWithNoDirectivesFailsClosed() throws IOException {
        String path = tempFile("empty-profile.txt", "record-count:1\nname:town\n");

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * Comments and blank lines are dropped by the imported {@code CommentsAndEol} rules, so a file
     * padded with them assembles exactly as the bare one does.
     */
    @Test
    void commentsAndBlankLinesAreIgnored() throws IOException {
        String path = tempFile("commented.txt", String.join("\n",
                "# a leading comment",
                "",
                "record-count:1",
                "",
                "## Town",
                "name:town",
                "# the streamer line comes first here",
                "streamer:1:1:0:0:0:0",
                "params:1:0:200:0",
                "alloc:-1",
                "",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals("town", result.items().getFirst().getName());
        assertEquals(-1, result.items().getFirst().getAlloc());
    }

    /**
     * {@link Reader#parse} is {@link DungeonProfileReader#parseWithResults} with the messages
     * dropped: same items, no way to see what went wrong.
     */
    @Test
    void parseReturnsTheSameItemsAsParseWithResults() throws IOException {
        DungeonProfileReader reader = new DungeonProfileReader();

        List<CaveProfile> viaParse = reader.parse(REAL_FILE);
        List<CaveProfile> viaResults = reader.parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.size(), viaParse.size());
        assertEquals(viaResults.stream().map(CaveProfile::getName).toList(),
                viaParse.stream().map(CaveProfile::getName).toList());
    }

    @Test
    void aMissingFileThrows() {
        assertThrows(IOException.class,
                () -> new DungeonProfileReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }
}
