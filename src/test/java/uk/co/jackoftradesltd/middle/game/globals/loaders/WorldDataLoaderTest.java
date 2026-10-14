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
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.cave.World;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.player.Quest;

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
 * Tests {@link WorldDataLoader}, the port of C's {@code world_parser} ({@code init.c}),
 * {@code projection_parser} ({@code obj-init.c}) and {@code quests_parser} ({@code player-quest.c}).
 *
 * <p><b>Expected values come from C's data, not from the port.</b> The shipped files in
 * {@code lib/gamedata} are the C originals (the port only adds {@code record-count:} lines), and
 * the expectations below were counted and read from them: 128 {@code level:} records in
 * {@code world.txt}, "Town" at depth 0 with no level above it to "Angband 127" with none below;
 * 56 {@code code:} records in {@code projection.txt}, the first 25 of {@code type:element}, running
 * from {@code ACID} to {@code ARROW} and ending the file at {@code MON_CRUSH}; and the two quests in
 * {@code quest.txt}, Sauron at level 99 and Morgoth at level 100, one kill each. C numbers the quests
 * from 0 in file order in {@code finish_parse_quest}, so the port must too.
 *
 * <p><b>The C failure behaviour.</b> {@code parse_file_quit_not_found} quits on a missing file,
 * {@code run_parser} quits when a parser reports an error, and {@code finish_parse_world} quits with
 * "Invalid level reference" when a level's {@code up} or {@code down} names no loaded level, unless the
 * text is {@code "None"}. {@code finish_parse_projection} quits when the file does not hold exactly
 * the 25 elements. The port stops the same way in each case, and deliberately lets through a parse
 * whose soft errors still left some records (the partial-results contract).
 *
 * <p><b>What is tested.</b> The real files, then fixtures in a temporary directory standing in for
 * the gamedata directory: a missing file for each loader, every branch of the level reference check,
 * the element count, a reload replacing rather than appending, and the empty quest file, which C
 * accepts and the port rejects. The quest tests need the monster races, so the class runs {@code GameConstants.init} once
 * if no earlier test has.
 *
 * <p>Each test saves the three {@code WorldRegistry} lists and the gamedata directory and puts them
 * back afterwards.
 *
 * <p>Class WorldDataLoaderTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
@DisplayName("WorldDataLoader")
class WorldDataLoaderTest {

    private static boolean bootstrapped;

    @TempDir
    Path tempDir;

    private String savedGamedata;
    private List<World> savedWorlds;
    private List<Projection> savedProjections;
    private List<Quest> savedQuests;

    /**
     * Loads {@code GameConstants} if no earlier test has, because {@code QuestAssembler} resolves
     * each quest's race against the loaded monsters. The same bootstrap {@code QuestReaderTest} uses.
     */
    @BeforeAll
    static void bootstrap() throws IOException {
        if (monstersLoaded()) return;
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

    /** Clears the object tables {@code GameConstants.init} filled, if this class loaded it. */
    @AfterAll
    static void cleanup() {
        if (bootstrapped) ObjectRegistry.reset();
    }

    private static boolean monstersLoaded() {
        try {
            WorldRegistry.getQuestMax();
            return true;
        } catch (NullPointerException notLoaded) {
            return false;
        }
    }

    /**
     * Saves the gamedata directory and the three registry lists, then points the gamedata directory
     * at the empty temporary directory.
     */
    @BeforeEach
    void pointAtTempDir() throws Exception {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        savedWorlds = registryList("worlds");
        savedProjections = registryList("projections");
        savedQuests = registryList("quests");
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    /** Puts everything saved by {@link #pointAtTempDir()} back. */
    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        WorldRegistry.setWorlds(savedWorlds);
        WorldRegistry.setProjections(savedProjections);
        WorldRegistry.setQuests(savedQuests);
    }

    /**
     * Reads one of {@code WorldRegistry}'s lists directly, because its getters throw while a list is
     * unset and wrap the list in a view.
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> registryList(String field) throws Exception {
        Field f = WorldRegistry.class.getDeclaredField(field);
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

    private static String level(int depth, String name, String up, String down) {
        return "level:" + depth + ":" + name + ":" + up + ":" + down + "\n";
    }

    private static String worldFile(int count, String... levels) {
        return "record-count:" + count + "\n\n" + String.join("", levels);
    }

    /** One loader, with the data file it reads. */
    private record Case(String file, Executable loader) {
        @Override
        public String toString() {
            return file;
        }
    }

    static Stream<Case> loaders() {
        return Stream.of(
                new Case("world.txt", WorldDataLoader::loadWorld),
                new Case("projection.txt", WorldDataLoader::loadProjections),
                new Case("quest.txt", WorldDataLoader::loadQuests));
    }

    /**
     * The shipped files, checked against the counts and names read from C's data.
     */
    @Nested
    @DisplayName("against the shipped files")
    class RealData {

        /**
         * 128 levels in file order. The town has no level above it, so C holds {@code NULL}; the port
         * keeps the file's {@code "None"}. The deepest level has none below.
         */
        @Test
        @DisplayName("the 128 levels load in file order, with None at both ends")
        void world() throws Exception {
            useRealGamedata();

            WorldDataLoader.loadWorld();

            List<World> worlds = WorldRegistry.getWorlds();
            assertEquals(128, worlds.size(), "level count");
            assertEquals(new World(0, "Town", "None", "Angband 1"), worlds.getFirst());
            assertEquals(new World(1, "Angband 1", "Town", "Angband 2"), worlds.get(1));
            assertEquals(new World(126, "Angband 126", "Angband 125", "Angband 127"), worlds.get(126));
            assertEquals(new World(127, "Angband 127", "Angband 126", "None"), worlds.getLast());
            for (int i = 0; i < worlds.size(); i++) {
                assertEquals(i, worlds.get(i).levelNumber(), "depth of record " + i);
            }
        }

        /**
         * 56 projections in file order, the 25 elements first. C's {@code projections[]} is indexed
         * by the {@code PROJ_*} value, so the elements have to open the list in order.
         */
        @Test
        @DisplayName("the 56 projections load in file order, the 25 elements first")
        void projections() throws Exception {
            useRealGamedata();

            WorldDataLoader.loadProjections();

            List<Projection> projections = WorldRegistry.getProjections();
            assertEquals(56, projections.size(), "projection count");
            assertEquals(ProjectionEnum.PROJ_ACID, projections.getFirst().getProjection());
            assertEquals(ProjectionEnum.PROJ_ARROW, projections.get(24).getProjection());
            assertEquals(ProjectionEnum.PROJ_MON_CRUSH, projections.getLast().getProjection());
            for (int i = 0; i < projections.size(); i++) {
                assertEquals(i < 25, projections.get(i).getType() == ProjectionType.PT_ELEMENT,
                        "type of record " + i);
            }
            assertEquals(56, WorldRegistry.getProjMax());
        }

        /**
         * Sauron and Morgoth, numbered 0 and 1 as {@code finish_parse_quest} numbers them, and the
         * count that C records as {@code z_info->quest_max}.
         */
        @Test
        @DisplayName("the two quests load in file order, numbered from 0")
        void quests() {
            useRealGamedata();

            WorldDataLoader.loadQuests();

            List<Quest> quests = WorldRegistry.getQuests();
            assertEquals(2, quests.size(), "quest count");
            assertEquals(2, WorldRegistry.getQuestMax(), "z_info->quest_max");
            Quest sauron = quests.get(0);
            assertEquals(0, sauron.getIndex());
            assertEquals("Sauron", sauron.getName());
            assertEquals(99, sauron.getLevel());
            assertEquals(1, sauron.getMaxNumber());
            assertEquals("Sauron, the Sorcerer", sauron.getRace().getName());
            Quest morgoth = quests.get(1);
            assertEquals(1, morgoth.getIndex());
            assertEquals("Morgoth", morgoth.getName());
            assertEquals(100, morgoth.getLevel());
            assertEquals("Morgoth, Lord of Darkness", morgoth.getRace().getName());
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
        RuntimeException thrown = assertThrows(RuntimeException.class, loaderCase.loader()::execute);

        assertTrue(thrown.getMessage().contains(loaderCase.file()), thrown.getMessage());
        assertInstanceOf(IOException.class, thrown.getCause());
        assertSame(savedWorlds, registryList("worlds"), "worlds untouched");
        assertSame(savedProjections, registryList("projections"), "projections untouched");
        assertSame(savedQuests, registryList("quests"), "quests untouched");
    }

    /**
     * The level reference check of {@code finish_parse_world}.
     */
    @Nested
    @DisplayName("the level reference check")
    class LevelReferences {

        /**
         * C checks after the whole file is read, so a level may name one defined later; and a level
         * whose neighbour is {@code "None"} needs no match, wherever it is in the list.
         */
        @Test
        @DisplayName("a forward reference and a None in the middle of the list are accepted")
        void forwardReferenceAndNone() throws Exception {
            write("world.txt", worldFile(3,
                    level(0, "A", "None", "C"),
                    level(1, "B", "None", "None"),
                    level(2, "C", "A", "None")));

            WorldDataLoader.loadWorld();

            assertEquals(List.of(
                    new World(0, "A", "None", "C"),
                    new World(1, "B", "None", "None"),
                    new World(2, "C", "A", "None")), WorldRegistry.getWorlds());
        }

        /** A level may be its own neighbour, since C only asks whether the name exists. */
        @Test
        @DisplayName("a level that names itself is accepted")
        void selfReference() throws Exception {
            write("world.txt", worldFile(1, level(0, "Loop", "Loop", "Loop")));

            WorldDataLoader.loadWorld();

            assertEquals(List.of(new World(0, "Loop", "Loop", "Loop")), WorldRegistry.getWorlds());
        }

        /**
         * Depths are not checked: they may repeat or run backwards. C has no check either.
         */
        @Test
        @DisplayName("repeated and out-of-order depths are accepted")
        void depthsAreNotChecked() throws Exception {
            write("world.txt", worldFile(3,
                    level(5, "Low", "None", "None"),
                    level(5, "Same", "None", "None"),
                    level(1, "Back", "None", "None")));

            WorldDataLoader.loadWorld();

            assertEquals(List.of(5, 5, 1),
                    WorldRegistry.getWorlds().stream().map(World::levelNumber).toList());
        }

        @Test
        @DisplayName("a previous level that is not loaded stops the load")
        void missingPrevious() throws Exception {
            write("world.txt", worldFile(2,
                    level(0, "A", "None", "B"),
                    level(1, "B", "Nowhere", "None")));

            RuntimeException thrown = assertThrows(RuntimeException.class, WorldDataLoader::loadWorld);

            assertTrue(thrown.getMessage().contains("Nowhere"), thrown.getMessage());
            assertSame(savedWorlds, registryList("worlds"), "worlds untouched");
        }

        @Test
        @DisplayName("a next level that is not loaded stops the load")
        void missingNext() throws Exception {
            write("world.txt", worldFile(2,
                    level(0, "A", "None", "Nowhere"),
                    level(1, "B", "A", "None")));

            RuntimeException thrown = assertThrows(RuntimeException.class, WorldDataLoader::loadWorld);

            assertTrue(thrown.getMessage().contains("Nowhere"), thrown.getMessage());
            assertSame(savedWorlds, registryList("worlds"), "worlds untouched");
        }

        /**
         * {@code streq(up, "None")} is case-sensitive, so {@code none} is a level name that nothing
         * has, and C quits. The same holds for the next level.
         */
        @ParameterizedTest(name = "{0} is not None")
        @MethodSource("uk.co.jackoftradesltd.middle.game.globals.loaders.WorldDataLoaderTest#notNone")
        @DisplayName("only the exact text None means no neighbour")
        void noneIsCaseSensitive(String spelling) throws Exception {
            write("world.txt", worldFile(1, level(0, "A", spelling, "None")));
            assertThrows(RuntimeException.class, WorldDataLoader::loadWorld, "previous");

            write("world.txt", worldFile(1, level(0, "A", "None", spelling)));
            assertThrows(RuntimeException.class, WorldDataLoader::loadWorld, "next");

            assertSame(savedWorlds, registryList("worlds"), "worlds untouched");
        }

        /**
         * {@code streq} compares the whole name, so a prefix of a real name is not a match.
         */
        @Test
        @DisplayName("a name must match a level exactly, not as a prefix or in another case")
        void exactNameMatch() throws Exception {
            write("world.txt", worldFile(2,
                    level(0, "Angband 1", "None", "None"),
                    level(1, "B", "Angband", "None")));
            assertThrows(RuntimeException.class, WorldDataLoader::loadWorld, "prefix");

            write("world.txt", worldFile(2,
                    level(0, "Angband 1", "None", "None"),
                    level(1, "B", "angband 1", "None")));
            assertThrows(RuntimeException.class, WorldDataLoader::loadWorld, "case");
        }
    }

    static Stream<String> notNone() {
        return Stream.of("none", "NONE", "Non");
    }

    /**
     * A reload replaces: loading a two-level file over the 128 real ones leaves two.
     */
    @Test
    @DisplayName("a reload replaces the levels rather than appending")
    void reloadReplacesWorld() throws Exception {
        useRealGamedata();
        WorldDataLoader.loadWorld();
        assertEquals(128, WorldRegistry.getWorlds().size());

        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
        write("world.txt", worldFile(2,
                level(0, "Top", "None", "Bottom"),
                level(1, "Bottom", "Top", "None")));

        WorldDataLoader.loadWorld();

        assertEquals(2, WorldRegistry.getWorlds().size());
        assertEquals(2, WorldRegistry.getMaxRandDepth());
    }

    /**
     * C's {@code finish_parse_projection} quits unless there are exactly 25 elements. The assembler
     * returns an empty list with an error, which the loader must turn into a stop that leaves the
     * registry as it was. The fixture is the real file with the last element, {@code ARROW}, changed
     * to {@code type:environs}, so the file has 24.
     */
    @Test
    @DisplayName("a projection file with 24 elements stops the load")
    void tooFewElements() throws Exception {
        String real = Files.readString(Path.of(savedGamedata, "projection.txt"));
        int record = real.indexOf("code:ARROW");
        int type = real.indexOf("type:element", record);
        assertTrue(record > 0 && type > record, "fixture markers found in projection.txt");
        write("projection.txt", real.substring(0, type) + "type:environs"
                + real.substring(type + "type:element".length()));

        RuntimeException thrown = assertThrows(RuntimeException.class, WorldDataLoader::loadProjections);

        assertTrue(thrown.getMessage().contains("projection.txt"), thrown.getMessage());
        assertSame(savedProjections, registryList("projections"), "projections untouched");
    }

    /**
     * <b>A known divergence from C.</b> C's {@code finish_parse_quest} accepts a {@code quest.txt}
     * with no quests, setting {@code z_info->quest_max} to 0 and allocating nothing. {@code QuestGrammar}
     * requires at least one quest, so the port rejects the file as a syntax error and the load stops.
     * This pins the port's present behaviour; if the grammar is changed to accept zero quests, as
     * {@code HintGrammar} and {@code NamesGrammar} were, this test should instead expect an empty
     * registry list and a {@code getQuestMax()} of 0.
     */
    @Test
    @DisplayName("an empty quest file stops the load, where C loads no quests")
    void emptyQuestFile() throws Exception {
        write("quest.txt", "record-count:0\n");

        assertThrows(RuntimeException.class, WorldDataLoader::loadQuests);

        assertSame(savedQuests, registryList("quests"), "quests untouched");
    }

    /**
     * A quest whose race names no monster is {@code PARSE_ERROR_INVALID_MONSTER} in C, and the game
     * quits. The load must stop and leave the registry as it was.
     */
    @Test
    @DisplayName("a quest naming an unknown monster stops the load")
    void unknownRace() throws Exception {
        write("quest.txt", "record-count:1\n\nname:Ghost\nlevel:50\nrace:Zzzzzz Nonexistent Beast\nnumber:1\n");

        assertThrows(RuntimeException.class, WorldDataLoader::loadQuests);

        assertSame(savedQuests, registryList("quests"), "quests untouched");
    }

    /**
     * A reload replaces, and the quests are numbered from 0 in file order whatever the file holds:
     * Morgoth first in the fixture gets index 0.
     */
    @Test
    @DisplayName("a reload replaces the quests, numbered from 0 in file order")
    void reloadReplacesQuests() throws Exception {
        useRealGamedata();
        WorldDataLoader.loadQuests();
        assertEquals(2, WorldRegistry.getQuestMax());

        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
        write("quest.txt", "record-count:1\n\nname:Morgoth\nlevel:100\nrace:Morgoth, Lord of Darkness\nnumber:1\n");

        WorldDataLoader.loadQuests();

        assertEquals(1, WorldRegistry.getQuestMax());
        assertEquals(0, WorldRegistry.getQuests().getFirst().getIndex());
        assertEquals("Morgoth", WorldRegistry.getQuests().getFirst().getName());
    }
}
