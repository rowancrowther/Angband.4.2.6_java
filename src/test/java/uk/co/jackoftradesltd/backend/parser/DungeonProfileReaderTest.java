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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
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
 * {@code '-'? digit+}, so a non-numeric field is a <em>grammar</em> error, not an assembly one.
 * {@code params:}, {@code min-level:} and {@code alloc:} are read by {@code ParseUtils} in base 0, as
 * {@code parser_getint} reads them, so a value too big for {@code int} is clamped (the accepted
 * deviation from C's wrap) and loads. An overflowing {@code room:} field is still soft: the room is
 * dropped and the rest of the file still loads, which is weaker than C, where a bad number is fatal.
 * The sites C quits on and the assemblers already stop at, as {@code print_error} in
 * {@code datafile.c} does, are an unknown profile name, an unknown room name and an overflowing
 * {@code tunnel:} or {@code streamer:} field. Each throws {@link DataFileParseException} out of
 * {@code parseWithResults}.
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
     * Loads {@code path} expecting the assembler to stop the load, and returns what it threw.
     */
    private static DataFileParseException loadAndStop(String path) {
        return assertThrows(DataFileParseException.class,
                () -> new DungeonProfileReader().parseWithResults(path));
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
     * Every {@code room:} line in the file resolves to a {@link RoomType}. The assembler refuses an
     * unresolved name (see {@link #unknownRoomNameStopsTheLoadAsInC()}), so a clean load already
     * implies this; it is kept as the direct assertion that the enum's names agree with the data.
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
     * A profile with nothing but its {@code name:} and {@code alloc:} lines. C's
     * {@code parse_profile_name} makes the record with {@code mem_zalloc}, so the four
     * {@code params:} numbers and {@code min_level} are 0. The tunnel and streamer stay null here
     * where C has zeroed structs, as {@code CaveProfile} documents.
     */
    @Test
    void absentDirectivesReadAsZeroAsInC() throws IOException {
        String path = tempFile("bare.txt", String.join("\n",
                "record-count:1",
                "name:town",
                "alloc:5",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        CaveProfile bare = result.items().getFirst();

        // No params: line - mem_zalloc's 0 in all four.
        assertEquals(0, bare.getBlockSize());
        assertEquals(0, bare.getDunRooms());
        assertEquals(0, bare.getDunUnusual());
        assertEquals(0, bare.getMaxRarity());

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
                "name:classic",
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
                "name:town",
                "alloc:1",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertEquals(1, result.items().size(), "the count check must not cost the file its records");
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    /**
     * C: {@code parser_getint} on {@code params:99999999999} gives 99999999999 in a 64-bit
     * {@code long}, and the {@code int} field keeps the low 32 bits, 1215752191; the profile loads.
     * Here it loads too, with the value clamped to {@link Integer#MAX_VALUE} (the accepted
     * deviation), and no profile is dropped or reported.
     */
    @Test
    void anOverflowingParamsValueClampsWhereCWraps() throws IOException {
        String path = tempFile("overflow-params.txt", String.join("\n",
                "record-count:3",
                "name:town",
                "alloc:1",
                "name:classic",
                "params:99999999999:0:-99999999999:0",
                "alloc:1",
                "name:cavern",
                "alloc:1",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of("town", "classic", "cavern"),
                result.items().stream().map(CaveProfile::getName).toList());
        CaveProfile classic = result.items().get(1);
        assertEquals(Integer.MAX_VALUE, classic.getBlockSize(), "C would hold 1215752191");
        assertEquals(Integer.MIN_VALUE, classic.getDunUnusual(), "C would hold -1215752191");
    }

    /**
     * The same for an overflowing {@code alloc:} or {@code min-level:} value, which the assembler
     * converts itself rather than through a sub-assembler.
     */
    @Test
    void anOverflowingAllocOrMinLevelClampsWhereCWraps() throws IOException {
        String path = tempFile("overflow-alloc.txt", String.join("\n",
                "record-count:2",
                "name:classic",
                "alloc:99999999999",
                "name:modified",
                "min-level:99999999999",
                "alloc:-99999999999",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals(Integer.MAX_VALUE, result.items().get(0).getAlloc(), "C would hold 1215752191");
        assertEquals(Integer.MAX_VALUE, result.items().get(1).getMinLevel(), "C would hold 1215752191");
        assertEquals(Integer.MIN_VALUE, result.items().get(1).getAlloc(), "C would hold -1215752191");
    }

    /**
     * C: {@code parser_getint} is {@code strtol(tok, &z, 0)}, so a leading {@code 0} means octal and
     * reading stops at the first digit that is not octal: {@code params:010:08:017:-010} is 8, 0, 15
     * and -8, {@code min-level:019} is 1 and {@code alloc:-0} is 0. {@code 08} is accepted because
     * the {@code 0} is a digit, so {@code z != tok}.
     */
    @Test
    void paramsMinLevelAndAllocAreReadInBase0AsInC() throws IOException {
        String path = tempFile("base0.txt", String.join("\n",
                "record-count:1",
                "name:classic",
                "params:010:08:017:-010",
                "min-level:019",
                "alloc:-0",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        CaveProfile p = result.items().getFirst();
        assertEquals(8, p.getBlockSize());
        assertEquals(0, p.getDunRooms());
        assertEquals(15, p.getDunUnusual());
        assertEquals(-8, p.getMaxRarity());
        assertEquals(1, p.getMinLevel());
        assertEquals(0, p.getAlloc());
    }

    /**
     * C: an absent {@code alloc:} leaves the field at {@code mem_zalloc}'s 0, which the weighted pick
     * skips ({@code alloc <= 0}), so a modded {@code moria} with no {@code alloc:} line is never
     * chosen. The same here.
     */
    @Test
    void anAbsentAllocIsZeroAsInC() throws IOException {
        String path = tempFile("no-alloc.txt", String.join("\n",
                "record-count:1",
                "name:moria",
                "params:1:50:250:2",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(0, result.items().getFirst().getAlloc());
    }

    /**
     * C: {@code parse_profile_name} compares with {@code streq}, which is exact, so a name with the
     * wrong case, a trailing space or an underscore for the space matches no builder and the load
     * quits on {@code PARSE_ERROR_NO_BUILDER_FOUND}, even after a good profile.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Town", "CLASSIC", "classic ", "hard_centre", "hardcentre", "hard  centre"})
    void aNearMissProfileNameStopsTheLoadAfterAGoodProfile(String bad) throws IOException {
        String path = tempFile("near-miss-profile.txt", String.join("\n",
                "record-count:2",
                "name:town",
                "alloc:1",
                "name:" + bad,
                "alloc:1",
                ""));

        DataFileParseException e = loadAndStop(path);

        assertTrue(e.getMessage().contains("line: 4"), e.getMessage());
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
     * C: {@code parse_profile_room} returns {@code PARSE_ERROR_NO_ROOM_FOUND} when the name matches
     * no entry in {@code room_builders[]}, and {@code print_error} in {@code datafile.c} quits. The
     * assembler throws, naming the room, the line and the file.
     */
    @Test
    void unknownRoomNameStopsTheLoadAsInC() throws IOException {
        String path = tempFile("unknown-room.txt", String.join("\n",
                "record-count:1",
                "name:classic",
                "alloc:1",
                "room:no such room:0:11:33:1:0:0:100",
                ""));

        DataFileParseException e = loadAndStop(path);

        assertTrue(e.getMessage().contains("no such room"), e.getMessage());
        assertTrue(e.getMessage().contains("line: 4"), e.getMessage());
        assertTrue(e.getMessage().contains(path), e.getMessage());
    }

    /**
     * {@code streq} is exact, so a room name with the wrong case or a trailing space misses too, and
     * a good room before it does not save the file.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Simple room", "simple room ", "simple_room", "Greater Vault"})
    void aNearMissRoomNameStopsTheLoadAfterAGoodRoom(String bad) throws IOException {
        String path = tempFile("near-miss-room.txt", String.join("\n",
                "record-count:1",
                "name:classic",
                "alloc:1",
                "room:simple room:0:11:33:1:0:0:100",
                "room:" + bad + ":0:11:33:1:0:0:100",
                ""));

        DataFileParseException e = loadAndStop(path);

        assertTrue(e.getMessage().contains("line: 5"), e.getMessage());
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

    // ---- C-derived: the shipped file, profile by profile -------------------

    /**
     * Every profile of the shipped file against its directives in the C file's own
     * {@code dungeon_profile.txt}: {@code name}, {@code params}, {@code min-level} (0 when the line
     * is absent, as {@code mem_zalloc} leaves it) and {@code alloc}, and the number of
     * {@code room:} lines each carries (0, 0, 0, 14, 20, 12, 20, 0 and 0).
     */
    @Test
    void everyShippedProfileMatchesTheCFileLineForLine() throws IOException {
        List<CaveProfile> profiles = new DungeonProfileReader().parseWithResults(REAL_FILE).items();

        // block, rooms, unusual, rarity, min-level, alloc, room lines
        int[][] expected = {
                {1, 0, 200, 0, 0, -1, 0},      // town
                {1, 0, 200, 0, 0, -1, 0},      // labyrinth
                {1, 0, 200, 0, 15, 10, 0},     // cavern
                {11, 50, 200, 2, 0, 90, 14},   // classic
                {1, 50, 300, 2, 0, 97, 20},    // modified
                {1, 50, 250, 2, 0, -1, 12},    // moria
                {1, 50, 500, 2, 20, 1, 20},    // lair
                {1, 0, 200, 0, 20, 1, 0},      // gauntlet
                {1, 0, 200, 0, 50, 1, 0},      // hard centre
        };
        assertEquals(expected.length, profiles.size());
        for (int i = 0; i < expected.length; i++) {
            CaveProfile p = profiles.get(i);
            int[] e = expected[i];
            int index = i;
            assertEquals(e[0], p.getBlockSize(), () -> p.getName() + " block (index " + index + ")");
            assertEquals(e[1], p.getDunRooms(), () -> p.getName() + " rooms");
            assertEquals(e[2], p.getDunUnusual(), () -> p.getName() + " unusual");
            assertEquals(e[3], p.getMaxRarity(), () -> p.getName() + " rarity");
            assertEquals(e[4], p.getMinLevel(), () -> p.getName() + " min-level");
            assertEquals(e[5], p.getAlloc(), () -> p.getName() + " alloc");
            assertEquals(e[6], p.getRoomProfiles().size(), () -> p.getName() + " room count");
        }
    }

    /**
     * The tunnel and streamer lines of the three profiles that differ from {@code classic}: moria
     * has {@code jct} 30 where the others have 50, and the town has a streamer but no tunnel,
     * while labyrinth, cavern, gauntlet and hard centre have neither.
     */
    @Test
    void tunnelAndStreamerLinesAreAttachedToTheRightProfiles() throws IOException {
        List<CaveProfile> profiles = new DungeonProfileReader().parseWithResults(REAL_FILE).items();

        assertEquals(30, byName(profiles, "moria").getTun().getJct());
        assertEquals(50, byName(profiles, "modified").getTun().getJct());
        assertEquals(50, byName(profiles, "lair").getTun().getJct());
        assertEquals(90, byName(profiles, "lair").getStr().getMc());

        for (String bare : List.of("labyrinth", "cavern", "gauntlet", "hard centre")) {
            assertNull(byName(profiles, bare).getTun(), bare + " has no tunnel: line");
            assertNull(byName(profiles, bare).getStr(), bare + " has no streamer: line");
        }
    }

    /**
     * Rooms whose names hold spaces and parentheses, and the rarity/cutoff fields on the last room
     * of moria, are read from the right columns: {@code room:Greater vault (new):0:44:66:35:0:0:100}
     * and {@code room:monster pit:0:11:33:5:1:2:12}.
     */
    @Test
    void roomNamesWithSpacesAndParenthesesAreReadWhole() throws IOException {
        List<RoomProfile> rooms =
                byName(new DungeonProfileReader().parseWithResults(REAL_FILE).items(), "moria").getRoomProfiles();

        RoomProfile second = rooms.get(1);
        assertEquals("Greater vault (new)", second.getName());
        assertEquals(RoomType.NEW_GREATER_VAULT, second.getRoomType());
        assertEquals(100, second.getCutoff());

        RoomProfile pit = rooms.stream().filter(RoomProfile::isPit).findFirst().orElseThrow();
        assertEquals("monster pit", pit.getName());
        assertEquals(12, pit.getCutoff());
        assertEquals(2, pit.getRarity());

        assertEquals("moria room", rooms.get(rooms.size() - 2).getName());
    }

    // ---- Where the Java and C differ, pinned so a change is noticed --------

    /**
     * C: {@code r->pit = (parser_getint(p, "pit") == 1)}, and {@code parser_getint} is
     * {@code strtol} in base 0, so {@code pit:01} is 1 and the room is a pit. Java compares the
     * text with {@code "1"}, so {@code 01} is not a pit.
     */
    @Test
    void aPitWrittenWithALeadingZeroIsNotAPitHereWhereCReadsOne() throws IOException {
        String path = tempFile("pit-01.txt", String.join("\n",
                "record-count:1",
                "name:classic",
                "alloc:1",
                "room:monster pit:0:11:33:5:01:2:8",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertFalse(result.items().getFirst().getRoomProfiles().getFirst().isPit(), "C would say true");
    }

    /**
     * C: {@code parse_profile_name} fails with {@code PARSE_ERROR_NO_BUILDER_FOUND} when the name
     * is not in {@code cave_builders[]}, and the load quits. The assembler throws, naming the
     * profile, the line and the file.
     */
    @Test
    void anUnknownProfileNameStopsTheLoadAsInC() throws IOException {
        String path = tempFile("unknown-profile.txt", String.join("\n",
                "record-count:1",
                "name:no such profile",
                "alloc:1",
                ""));

        DataFileParseException e = loadAndStop(path);

        assertTrue(e.getMessage().contains("no such profile"), e.getMessage());
        assertTrue(e.getMessage().contains("line 2") || e.getMessage().contains("line: 2"), e.getMessage());
        assertTrue(e.getMessage().contains(path), e.getMessage());
    }

    /**
     * C: {@code parser_getint} on any one of the five {@code tunnel:} fields refuses a value past
     * {@code INT_MAX}, so the load quits. A good profile before the bad one does not save the file,
     * and a profile is never kept without its tunnel.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void anOverflowingTunnelFieldStopsTheLoadAsInC(int field) throws IOException {
        String[] values = {"10", "30", "15", "25", "50"};
        values[field] = "99999999999";
        String path = tempFile("overflow-tunnel.txt", String.join("\n",
                "record-count:2",
                "name:town",
                "alloc:1",
                "name:classic",
                "params:11:50:200:2",
                "tunnel:" + String.join(":", values),
                "alloc:90",
                ""));

        DataFileParseException e = loadAndStop(path);

        assertTrue(e.getMessage().contains("99999999999"), e.getMessage());
        assertTrue(e.getMessage().contains(path), e.getMessage());
    }

    /**
     * The same for each of the six {@code streamer:} fields (C's {@code parse_profile_streamer}).
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    void anOverflowingStreamerFieldStopsTheLoadAsInC(int field) throws IOException {
        String[] values = {"5", "2", "3", "90", "2", "40"};
        values[field] = "99999999999";
        String path = tempFile("overflow-streamer.txt", String.join("\n",
                "record-count:2",
                "name:town",
                "alloc:1",
                "name:classic",
                "params:11:50:200:2",
                "streamer:" + String.join(":", values),
                "alloc:90",
                ""));

        DataFileParseException e = loadAndStop(path);

        assertTrue(e.getMessage().contains("99999999999"), e.getMessage());
        assertTrue(e.getMessage().contains(path), e.getMessage());
    }

    /**
     * C: a file with no profiles is accepted ({@code profile_max} 0). The grammar's
     * {@code (profile)+} makes it a syntax error here, so the header with nothing after it gives
     * no profiles and an error.
     */
    @Test
    void aFileWithNoProfilesIsRefusedHereWhereCLoadsAnEmptyList() throws IOException {
        String path = tempFile("no-profiles.txt", "record-count:0\n");

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * C: every directive but {@code name:} needs a current record
     * ({@code PARSE_ERROR_MISSING_RECORD_HEADER}). The grammar agrees, as the profile rule starts
     * with {@code name}, so a leading {@code params:} loses the file.
     */
    @Test
    void aDirectiveBeforeAnyNameFailsClosedAsInC() throws IOException {
        String path = tempFile("headless.txt", String.join("\n",
                "record-count:1",
                "params:1:0:200:0",
                "name:town",
                "alloc:-1",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * C: {@code parser_getint} reads in base 0 and ignores anything after the value up to the
     * next {@code :}, and the parser skips leading whitespace, so {@code alloc:0x10}, an indented
     * {@code alloc:5} and {@code alloc:5:junk} are all accepted. Each is a lexer or syntax error
     * here and costs the whole file.
     */
    @Test
    void hexIndentedAndTrailingFieldValuesAreRefusedHereWhereCAcceptsThem() throws IOException {
        for (String line : List.of("alloc:0x10", "  alloc:5", "alloc:5:junk")) {
            String path = tempFile("odd.txt", String.join("\n",
                    "record-count:1",
                    "name:odd",
                    line,
                    ""));

            ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

            assertTrue(result.hasErrors(), line);
            assertTrue(result.items().isEmpty(), line);
        }
    }

    /**
     * C: a {@code params:} line with a field missing is {@code PARSE_ERROR_MISSING_FIELD}, which is
     * fatal. The grammar agrees: three fields where four are needed is a syntax error.
     */
    @Test
    void aParamsLineWithAFieldMissingFailsClosedAsInC() throws IOException {
        String path = tempFile("short-params.txt", String.join("\n",
                "record-count:1",
                "name:short",
                "params:1:0:200",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty(), result.items()::toString);
    }

    /**
     * The profile order is the file's, not the order of the builder table: C prepends then fills the array from
     * the far end, so {@code cave_profiles[0]} is the first {@code name:} in the file.
     */
    @Test
    void profilesComeBackInFileOrderNotBuilderTableOrder() throws IOException {
        String path = tempFile("order.txt", String.join("\n",
                "record-count:3",
                "name:cavern",
                "alloc:1",
                "name:town",
                "alloc:1",
                "name:classic",
                "alloc:1",
                ""));

        ParseResult<CaveProfile> result = new DungeonProfileReader().parseWithResults(path);

        assertEquals(List.of("cavern", "town", "classic"),
                result.items().stream().map(CaveProfile::getName).toList());
    }
}
