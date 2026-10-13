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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.CaveProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.DungeonRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DungeonLoader}, the port of C's {@code generate_module} init,
 * {@code run_template_parser()} in {@code generate.c}: the dungeon-profile, room-template and vault
 * parsers, run in that order.
 *
 * <p><b>Expected values come from C's data, not from the port.</b> The shipped files in
 * {@code lib/gamedata} are the C originals (the port only adds {@code record-count:} lines), and
 * the expectations below were counted and read from them with {@code grep}: 9 {@code name:} records
 * in {@code dungeon_profile.txt}, in the order town, labyrinth, cavern, classic, modified, moria,
 * lair, gauntlet, hard centre; 500 in {@code room_template.txt}, from "Tiny hidden room" to "Rod
 * chamber"; 162 in {@code vault.txt}, from "Round" to "Golden Flower". C's
 * {@code finish_parse_profile} fills its array back to front from a prepended list, so the
 * finished array is in file order; the port must be too.
 *
 * <p><b>The C failure behaviour.</b> {@code parse_file_quit_not_found} quits on a missing file, and
 * {@code run_template_parser} quits when a parser reports an error. The port stops the same way for
 * a missing file and for a parse that leaves errors and no records, and deliberately lets through a
 * parse whose soft errors still left some records (the partial-results contract).
 *
 * <p><b>What is tested.</b> The real files, then fixtures in a temporary directory standing in for
 * the gamedata directory: a missing file for each loader, soft errors that leave records and soft
 * errors that leave none, a reload replacing rather than appending, and the profile count that
 * {@code finish_parse_profile} writes to {@code z_info->profile_max}. The vault {@code max-depth:0}
 * rewrite needs {@code GameConstants}, which the real-data tests load if no earlier test has.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at the temporary directory and puts it, the
 * three registry lists and the profile count back afterwards.
 *
 * <p>Class DungeonLoaderTest coded on 261013, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
@DisplayName("DungeonLoader")
class DungeonLoaderTest {

    private static final List<String> C_PROFILE_NAMES = List.of(
            "town", "labyrinth", "cavern", "classic", "modified", "moria", "lair", "gauntlet",
            "hard centre");

    /** A one-record vault of the smallest legal size: three rows of three columns. */
    private static final String GOOD_VAULT = String.join("\n",
            "name:Tiny", "type:Lesser vault", "rating:5", "rows:3", "columns:3",
            "min-depth:7", "max-depth:40", "D:###", "D:#.#", "D:###");

    @TempDir
    Path tempDir;

    private String savedGamedata;
    private List<CaveProfile> savedProfiles;
    private List<RoomTemplate> savedRooms;
    private List<Vault> savedVaults;
    private int savedProfileMax;

    /**
     * Saves the gamedata directory, the three registry lists and the profile count, then points the
     * gamedata directory at the empty temporary directory.
     */
    @BeforeEach
    void pointAtTempDir() throws Exception {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        savedProfiles = registryList("caveProfiles");
        savedRooms = registryList("roomTemplates");
        savedVaults = registryList("vaultTemplates");
        savedProfileMax = GameConstants.getCaveProfileMax();
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    /**
     * Puts everything saved by {@link #pointAtTempDir()} back.
     */
    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        DungeonRegistry.setCaveProfiles(savedProfiles);
        DungeonRegistry.setRoomTemplates(savedRooms);
        DungeonRegistry.setVaultTemplates(savedVaults);
        GameConstants.setCaveProfileMax(savedProfileMax);
    }

    /**
     * Reads one of {@code DungeonRegistry}'s lists, which has no accessor yet.
     *
     * @param field the registry field name
     * @return the list, or {@code null} if nothing has been installed
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> registryList(String field) throws Exception {
        Field f = DungeonRegistry.class.getDeclaredField(field);
        f.setAccessible(true);
        return (List<T>) f.get(null);
    }

    /** Points the gamedata directory back at the real, shipped files. */
    private void useRealGamedata() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
    }

    private void write(String file, String text) throws IOException {
        Files.writeString(tempDir.resolve(file), text);
    }

    /** One loader, with the data file it reads. */
    private record Case(String file, Runnable loader) {
        @Override
        public String toString() {
            return file;
        }
    }

    static Stream<Case> loaders() {
        return Stream.of(
                new Case("dungeon_profile.txt", DungeonLoader::loadDungeonProfiles),
                new Case("room_template.txt", DungeonLoader::loadRoomTemplates),
                new Case("vault.txt", DungeonLoader::loadVaultTemplates));
    }

    /**
     * The shipped files, checked against the counts and names read from C's data.
     */
    @Nested
    @DisplayName("against the shipped files")
    class RealData {

        private static boolean bootstrapped;

        /**
         * Loads {@code GameConstants} if no earlier test has, as {@code MonsterDataLoaderLoreTest}
         * does, because the vault loader needs the world's maximum depth to resolve
         * {@code max-depth:0}.
         */
        @BeforeAll
        static void bootstrap() throws IOException {
            if (gameConstantsLoaded()) return;
            bootstrapped = true;
            UIDataLoader.loadUIEntryRenderers();
            UIDataLoader.loadUIEntryBases();
            UIDataLoader.loadUIEntries();

            List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
            for (UIEntry entry : UIRegistry.getUIEntries()) {
                uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
            }
            Channels channels = Channels.create();
            channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
            GameConstants.init(new Core(channels.coreChannel(),
                    new StartupOptions(false, false, false, false, "", "", List.of())));
        }

        /**
         * The nine profiles arrive in C's file order, with the block size, minimum level and
         * allocation that {@code dungeon_profile.txt} gives each, and the count lands in
         * {@code GameConstants} as {@code z_info->profile_max} does.
         */
        /** Clears the object tables {@code GameConstants.init} filled, if this class loaded it. */
        @AfterAll
        static void cleanup() {
            if (bootstrapped) ObjectRegistry.reset();
        }

        @Test
        @DisplayName("the nine profiles load in file order and the count is recorded")
        void profiles() throws Exception {
            useRealGamedata();

            DungeonLoader.loadDungeonProfiles();

            List<CaveProfile> profiles = registryList("caveProfiles");
            assertEquals(9, profiles.size(), "profile count");
            assertEquals(C_PROFILE_NAMES, profiles.stream().map(CaveProfile::getName).toList());
            assertEquals(9, GameConstants.getCaveProfileMax(), "z_info->profile_max");

            // params:<block>:... , min-level and alloc, straight from dungeon_profile.txt
            int[][] expected = {
                    {1, 0, -1}, {1, 0, -1}, {1, 15, 10}, {11, 0, 90}, {1, 0, 97},
                    {1, 0, -1}, {1, 20, 1}, {1, 20, 1}, {1, 50, 1}};
            for (int i = 0; i < expected.length; i++) {
                CaveProfile p = profiles.get(i);
                assertEquals(expected[i][0], p.getBlockSize(), p.getName() + " block size");
                assertEquals(expected[i][1], p.getMinLevel(), p.getName() + " min-level");
                assertEquals(expected[i][2], p.getAlloc(), p.getName() + " alloc");
            }
        }

        /**
         * All 500 templates, first, second and last as the file lists them.
         */
        @Test
        @DisplayName("the 500 room templates load in file order")
        void rooms() throws Exception {
            useRealGamedata();

            DungeonLoader.loadRoomTemplates();

            List<RoomTemplate> rooms = registryList("roomTemplates");
            assertEquals(500, rooms.size(), "template count");
            assertEquals("Tiny hidden room", rooms.get(0).getName());
            assertEquals("Ladder", rooms.get(1).getName());
            assertEquals("Rod chamber", rooms.get(rooms.size() - 1).getName());
        }

        /**
         * All 162 vaults, with the first record's fields as {@code vault.txt} gives them. Its
         * {@code max-depth:0} becomes {@code z_info->max_depth}, 128 in {@code constants.txt}.
         */
        @Test
        @DisplayName("the 162 vaults load in file order, max-depth:0 becoming the world maximum")
        void vaults() throws Exception {
            useRealGamedata();

            DungeonLoader.loadVaultTemplates();

            List<Vault> vaults = registryList("vaultTemplates");
            assertEquals(162, vaults.size(), "vault count");
            Vault round = vaults.get(0);
            assertEquals("Round", round.getName());
            assertEquals(RoomType.LESSER_VAULT, round.getType());
            assertEquals(5, round.getRating());
            assertEquals(12, round.getHeight());
            assertEquals(20, round.getWidth());
            assertEquals(0, round.getMinLevel());
            assertEquals(128, round.getMaxLevel(), "max-depth:0 rewritten to z_info->max_depth");
            assertEquals("Golden Flower", vaults.get(vaults.size() - 1).getName().trim());
        }
    }

    private static boolean gameConstantsLoaded() {
        try {
            GameConstants.getWorldMaxDepth();
            return true;
        } catch (NullPointerException notLoaded) {
            return false;
        }
    }

    /**
     * C's {@code parse_file_quit_not_found} quits on a missing file. Each loader must throw, name
     * the file, keep the underlying {@code IOException} as the cause, and leave the registry alone.
     */
    @ParameterizedTest(name = "missing {0} stops the load")
    @MethodSource("loaders")
    @DisplayName("a missing data file stops the load, naming the file")
    void missingFileStopsTheLoad(Case loaderCase) throws Exception {
        RuntimeException thrown = assertThrows(RuntimeException.class, loaderCase.loader()::run);

        assertTrue(thrown.getMessage().contains(loaderCase.file()), thrown.getMessage());
        assertInstanceOf(IOException.class, thrown.getCause());
        assertSame(savedProfiles, registryList("caveProfiles"), "profiles untouched");
        assertSame(savedRooms, registryList("roomTemplates"), "rooms untouched");
        assertSame(savedVaults, registryList("vaultTemplates"), "vaults untouched");
        assertEquals(savedProfileMax, GameConstants.getCaveProfileMax(), "profile count untouched");
    }

    /**
     * A reload replaces: loading a two-profile file over the nine real ones leaves two, and the
     * recorded count follows.
     */
    @Test
    @DisplayName("a reload replaces the profiles and the count rather than appending")
    void reloadReplaces() throws Exception {
        useRealGamedata();
        DungeonLoader.loadDungeonProfiles();
        assertEquals(9, GameConstants.getCaveProfileMax());

        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
        write("dungeon_profile.txt", String.join("\n",
                "record-count:2",
                "name:first", "params:1:0:200:0", "alloc:5",
                "name:second", "params:3:0:200:0", "min-level:4", "alloc:6", ""));

        DungeonLoader.loadDungeonProfiles();

        List<CaveProfile> profiles = registryList("caveProfiles");
        assertEquals(List.of("first", "second"), profiles.stream().map(CaveProfile::getName).toList());
        assertEquals(2, GameConstants.getCaveProfileMax(), "z_info->profile_max follows the reload");
        assertEquals(4, profiles.get(1).getMinLevel());
    }

    /**
     * An overflowing {@code params:} block is a soft error that drops its profile. With a good
     * profile beside it, the loader installs the survivor, which is the deliberate softening of C
     * (where {@code parser_getint} would make it fatal), and counts only the survivor.
     */
    @Test
    @DisplayName("soft errors that leave profiles install them and count only the survivors")
    void profileSoftErrorWithSurvivors() throws Exception {
        write("dungeon_profile.txt", String.join("\n",
                "record-count:2",
                "name:bad", "params:99999999999:0:200:0", "alloc:5",
                "name:good", "params:1:0:200:0", "alloc:6", ""));

        DungeonLoader.loadDungeonProfiles();

        List<CaveProfile> profiles = registryList("caveProfiles");
        assertEquals(List.of("good"), profiles.stream().map(CaveProfile::getName).toList());
        assertEquals(1, GameConstants.getCaveProfileMax());
    }

    /**
     * The same overflow with nothing else in the file leaves no profile, which
     * {@code ErrorParsing.reportAndCheck} reports as {@code false}: the load stops, as C's
     * {@code run_template_parser} does on any parser error, before registry or count are written.
     */
    @Test
    @DisplayName("soft errors that leave no profiles stop the load before anything is written")
    void profileSoftErrorWithNoSurvivors() throws Exception {
        write("dungeon_profile.txt", String.join("\n",
                "record-count:1",
                "name:bad", "params:99999999999:0:200:0", "alloc:5", ""));

        RuntimeException thrown = assertThrows(RuntimeException.class, DungeonLoader::loadDungeonProfiles);

        assertEquals("Error parsing Dungeon profiles file", thrown.getMessage());
        assertSame(savedProfiles, registryList("caveProfiles"), "profiles untouched");
        assertEquals(savedProfileMax, GameConstants.getCaveProfileMax(), "profile count untouched");
    }

    /**
     * A vault with an explicit {@code max-depth} keeps it: the rewrite is only for zero, and an
     * explicit value must not need {@code GameConstants}.
     */
    @Test
    @DisplayName("a vault with an explicit max-depth keeps it")
    void vaultExplicitDepths() throws Exception {
        write("vault.txt", "record-count:1\n" + GOOD_VAULT + "\n");

        DungeonLoader.loadVaultTemplates();

        List<Vault> vaults = registryList("vaultTemplates");
        assertEquals(1, vaults.size());
        assertEquals("Tiny", vaults.get(0).getName());
        assertEquals(3, vaults.get(0).getHeight());
        assertEquals(3, vaults.get(0).getWidth());
        assertEquals(7, vaults.get(0).getMinLevel());
        assertEquals(40, vaults.get(0).getMaxLevel());
    }

    /**
     * One overflowing rating drops one vault; the other survives and is installed. Mirrors the
     * profile case for the third loader.
     */
    @Test
    @DisplayName("a vault soft error beside a good vault installs the good one")
    void vaultSoftErrorWithSurvivors() throws Exception {
        write("vault.txt", "record-count:2\n"
                + GOOD_VAULT.replace("name:Tiny", "name:Bad").replace("rating:5", "rating:99999999999")
                + "\n" + GOOD_VAULT + "\n");

        DungeonLoader.loadVaultTemplates();

        List<Vault> vaults = registryList("vaultTemplates");
        assertEquals(List.of("Tiny"), vaults.stream().map(Vault::getName).toList());
    }

    /**
     * The vault loader's no-survivor case: stop, naming the file's kind, and write nothing.
     */
    @Test
    @DisplayName("a vault soft error that leaves no vaults stops the load")
    void vaultSoftErrorWithNoSurvivors() throws Exception {
        write("vault.txt", "record-count:1\n"
                + GOOD_VAULT.replace("rating:5", "rating:99999999999") + "\n");

        RuntimeException thrown = assertThrows(RuntimeException.class, DungeonLoader::loadVaultTemplates);

        assertEquals("Error parsing Vault profiles file", thrown.getMessage());
        assertSame(savedVaults, registryList("vaultTemplates"), "vaults untouched");
    }

    /**
     * The room-template loader's no-survivor case.
     */
    @Test
    @DisplayName("a room-template soft error that leaves no templates stops the load")
    void roomSoftErrorWithNoSurvivors() throws Exception {
        write("room_template.txt", String.join("\n",
                "record-count:1",
                "name:Bad", "type:1", "rating:99999999999", "rows:3", "columns:3", "doors:1",
                "tval:0", "D:###", "D:#.#", "D:###", ""));

        RuntimeException thrown = assertThrows(RuntimeException.class, DungeonLoader::loadRoomTemplates);

        assertEquals("Error parsing Room profiles file", thrown.getMessage());
        assertSame(savedRooms, registryList("roomTemplates"), "templates untouched");
    }
}
