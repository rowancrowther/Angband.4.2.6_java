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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.ProjectionReader;
import uk.co.jackoftradesltd.backend.parser.WorldReader;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.cave.World;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.player.Quest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link WorldRegistry}: the size accessors and the level and projection lookups.
 *
 * <p>The expected values are read off the shipped C data rather than off the Java code. The world
 * list is the 128 {@code level:} records of {@code world.txt} (depth 0, "Town", to depth 127,
 * "Angband 127"), which matches C's {@code world:max-depth:128} in {@code constants.txt}. The
 * projection list is the 56 {@code code:} records of {@code projection.txt}: 25 {@code element},
 * 7 {@code environs} and 24 {@code monster}, which is C's {@code PROJ_MAX} (25 entries in
 * {@code list-elements.h} plus 31 in {@code list-projections.h}). The {@code lash-desc:} strings
 * are typed in from the same file. The two lookups that port C functions follow
 * {@code level_by_name} / {@code level_by_depth} ({@code streq} is case-sensitive, a miss is
 * {@code NULL}) and {@code proj_name_to_idx} ({@code project.c}).
 *
 * <p>The registry's lists are global static state shared with the other suites, so each test saves
 * and restores them around itself.
 *
 * <p>Class WorldRegistryTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class WorldRegistryTest {

    private static final String WORLD_FILE = "lib/gamedata/world.txt";
    private static final String PROJECTION_FILE = "lib/gamedata/projection.txt";

    private List<World> savedWorlds;
    private List<Projection> savedProjections;
    private List<Quest> savedQuests;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws Exception {
        savedWorlds = (List<World>) field("worlds").get(null);
        savedProjections = (List<Projection>) field("projections").get(null);
        savedQuests = (List<Quest>) field("quests").get(null);
    }

    @AfterEach
    void tearDown() throws Exception {
        field("worlds").set(null, savedWorlds);
        field("projections").set(null, savedProjections);
        field("quests").set(null, savedQuests);
    }

    private static Field field(String name) throws Exception {
        Field f = WorldRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static void loadWorlds() throws Exception {
        WorldRegistry.setWorlds(new WorldReader().parseWithResults(WORLD_FILE).items());
    }

    private static void loadProjections() throws Exception {
        WorldRegistry.setProjections(new ProjectionReader().parseWithResults(PROJECTION_FILE).items());
    }

    private static Quest quest(int index, String name) {
        return new Quest(index, name, 100, new MonsterRace(), 0, 1);
    }

    // ---- Worlds ------------------------------------------------------------

    @Test
    @DisplayName("getMaxRandDepth is the 128 level records of world.txt, matching world:max-depth:128")
    void maxRandDepthIsRecordCount() throws Exception {
        loadWorlds();

        assertEquals(128, WorldRegistry.getMaxRandDepth());
        assertEquals(128, WorldRegistry.getWorlds().size());
    }

    @Test
    @DisplayName("getLevelByName finds the Town at depth 0 with no level above it")
    void levelByNameTown() throws Exception {
        loadWorlds();

        World town = WorldRegistry.getLevelByName("Town").orElseThrow();

        assertEquals(0, town.levelNumber());
        assertNull(town.prevLevel());
        assertEquals("Angband 1", town.nextLevel());
    }

    @Test
    @DisplayName("getLevelByName finds the deepest level, which has no level below it")
    void levelByNameDeepest() throws Exception {
        loadWorlds();

        World bottom = WorldRegistry.getLevelByName("Angband 127").orElseThrow();

        assertEquals(127, bottom.levelNumber());
        assertEquals("Angband 126", bottom.prevLevel());
        assertNull(bottom.nextLevel());
    }

    @Test
    @DisplayName("getLevelByName is case-sensitive and exact, as streq is, and a miss is empty")
    void levelByNameMisses() throws Exception {
        loadWorlds();

        assertTrue(WorldRegistry.getLevelByName("town").isEmpty());
        assertTrue(WorldRegistry.getLevelByName("Angband 128").isEmpty());
        assertTrue(WorldRegistry.getLevelByName("Angband").isEmpty());
        assertTrue(WorldRegistry.getLevelByName("").isEmpty());
    }

    @Test
    @DisplayName("getLevelByDepth maps depth to the level named in world.txt at both ends and the middle")
    void levelByDepthHits() throws Exception {
        loadWorlds();

        assertEquals("Town", WorldRegistry.getLevelByDepth(0).orElseThrow().levelName());
        assertEquals("Angband 1", WorldRegistry.getLevelByDepth(1).orElseThrow().levelName());
        assertEquals("Angband 64", WorldRegistry.getLevelByDepth(64).orElseThrow().levelName());
        assertEquals("Angband 127", WorldRegistry.getLevelByDepth(127).orElseThrow().levelName());
    }

    @Test
    @DisplayName("getLevelByDepth is empty just outside 0..127, where C would return NULL")
    void levelByDepthMisses() throws Exception {
        loadWorlds();

        assertEquals(Optional.empty(), WorldRegistry.getLevelByDepth(-1));
        assertEquals(Optional.empty(), WorldRegistry.getLevelByDepth(128));
        assertEquals(Optional.empty(), WorldRegistry.getLevelByDepth(Integer.MAX_VALUE));
    }

    @Test
    @DisplayName("getLevelByName and getLevelByDepth agree on every level")
    void nameAndDepthAgree() throws Exception {
        loadWorlds();

        for (int depth = 0; depth < 128; depth++) {
            World byDepth = WorldRegistry.getLevelByDepth(depth).orElseThrow();
            String expected = depth == 0 ? "Town" : "Angband " + depth;
            assertEquals(expected, byDepth.levelName());
            assertSame(byDepth, WorldRegistry.getLevelByName(expected).orElseThrow());
        }
    }

    @Test
    @DisplayName("getWorlds is a read-only view")
    void worldsViewIsReadOnly() throws Exception {
        loadWorlds();

        assertThrows(UnsupportedOperationException.class, () -> WorldRegistry.getWorlds().clear());
    }

    @Test
    @DisplayName("the world accessors throw IllegalStateException before the list is loaded")
    void worldGuards() throws Exception {
        field("worlds").set(null, null);

        assertThrows(IllegalStateException.class, WorldRegistry::getMaxRandDepth);
        assertThrows(IllegalStateException.class, () -> WorldRegistry.getLevelByName("Town"));
        assertThrows(IllegalStateException.class, () -> WorldRegistry.getLevelByDepth(0));
    }

    // ---- Projections -------------------------------------------------------

    @Test
    @DisplayName("getProjMax is C's PROJ_MAX: 25 elements plus 31 projections is 56")
    void projMaxIsProjMax() throws Exception {
        loadProjections();

        assertEquals(56, WorldRegistry.getProjMax());
        assertEquals(56, WorldRegistry.getProjections().size());
    }

    @Test
    @DisplayName("projection.txt splits 25 element, 7 environs and 24 monster")
    void projectionTypeCounts() throws Exception {
        loadProjections();

        List<Projection> all = WorldRegistry.getProjections();

        assertEquals(25, all.stream().filter(p -> p.getType() == ProjectionType.PT_ELEMENT).count());
        assertEquals(7, all.stream().filter(p -> p.getType() == ProjectionType.PT_ENVIRONS).count());
        assertEquals(24, all.stream().filter(p -> p.getType() == ProjectionType.PT_MONSTER).count());
    }

    @Test
    @DisplayName("lookupProjectionByCode returns the record whose code: matches")
    void byCodeHits() throws Exception {
        loadProjections();

        Projection acid = WorldRegistry.lookupProjectionByCode(ProjectionEnum.PROJ_ACID);
        Projection killWall = WorldRegistry.lookupProjectionByCode(ProjectionEnum.PROJ_KILL_WALL);

        assertNotNull(acid);
        assertEquals(ProjectionEnum.PROJ_ACID, acid.getProjection());
        assertEquals("acid", acid.getName());
        assertNotNull(killWall);
        assertEquals(ProjectionEnum.PROJ_KILL_WALL, killWall.getProjection());
        assertEquals(ProjectionType.PT_ENVIRONS, killWall.getType());
    }

    @Test
    @DisplayName("every loaded projection is found again by its own code, and by lookupProjectionByLash")
    void everyProjectionRoundTrips() throws Exception {
        loadProjections();

        for (Projection p : WorldRegistry.getProjections()) {
            assertSame(p, WorldRegistry.lookupProjectionByCode(p.getProjection()), p.getName());
            assertSame(p, WorldRegistry.lookupProjectionByLash(p.getProjection()), p.getName());
        }
    }

    @Test
    @DisplayName("a code with no record in projection.txt gives null, where proj_name_to_idx gives -1")
    void byCodeMiss() throws Exception {
        loadProjections();

        assertNull(WorldRegistry.lookupProjectionByCode(ProjectionEnum.PROJ_NONE));
        assertNull(WorldRegistry.lookupProjectionByLash(ProjectionEnum.PROJ_NONE));
    }

    @Test
    @DisplayName("lookupProjectionByName matches lash-desc: for the strings typed in from projection.txt")
    void byNameHits() throws Exception {
        loadProjections();

        assertEquals(ProjectionEnum.PROJ_ACID, WorldRegistry.lookupProjectionByName("acid").getProjection());
        assertEquals(ProjectionEnum.PROJ_ELEC, WorldRegistry.lookupProjectionByName("lightning").getProjection());
        assertEquals(ProjectionEnum.PROJ_COLD, WorldRegistry.lookupProjectionByName("frost").getProjection());
        assertEquals(ProjectionEnum.PROJ_POIS, WorldRegistry.lookupProjectionByName("venom").getProjection());
        assertEquals(ProjectionEnum.PROJ_SHARD, WorldRegistry.lookupProjectionByName("razors").getProjection());
    }

    @Test
    @DisplayName("lookupProjectionByName is case-sensitive and does not match code: or name:")
    void byNameMisses() throws Exception {
        loadProjections();

        assertNull(WorldRegistry.lookupProjectionByName("Venom"));
        assertNull(WorldRegistry.lookupProjectionByName("POIS"));
        assertNull(WorldRegistry.lookupProjectionByName("arrows"));
    }

    @Test
    @DisplayName("lookupProjectionByType returns the first record of each category in file order")
    void byTypeFirstInFile() throws Exception {
        loadProjections();

        assertEquals(ProjectionEnum.PROJ_ACID,
                WorldRegistry.lookupProjectionByType(ProjectionType.PT_ELEMENT).getProjection());
        assertEquals(ProjectionEnum.PROJ_LIGHT_WEAK,
                WorldRegistry.lookupProjectionByType(ProjectionType.PT_ENVIRONS).getProjection());
        assertEquals(ProjectionEnum.PROJ_AWAY_UNDEAD,
                WorldRegistry.lookupProjectionByType(ProjectionType.PT_MONSTER).getProjection());
    }

    @Test
    @DisplayName("lookupProjectionByType finds nothing for PT_NONE, which no record carries")
    void byTypeNone() throws Exception {
        loadProjections();

        assertNull(WorldRegistry.lookupProjectionByType(ProjectionType.PT_NONE));
    }

    @Test
    @DisplayName("getProjections is a read-only view")
    void projectionsViewIsReadOnly() throws Exception {
        loadProjections();

        assertThrows(UnsupportedOperationException.class, () -> WorldRegistry.getProjections().clear());
    }

    @Test
    @DisplayName("the projection lookups throw IllegalStateException before the list is loaded")
    void projectionGuards() throws Exception {
        field("projections").set(null, null);

        assertThrows(IllegalStateException.class,
                () -> WorldRegistry.lookupProjectionByLash(ProjectionEnum.PROJ_ACID));
        assertThrows(IllegalStateException.class, () -> WorldRegistry.lookupProjectionByName("acid"));
        assertThrows(IllegalStateException.class,
                () -> WorldRegistry.lookupProjectionByType(ProjectionType.PT_ELEMENT));
        assertThrows(IllegalStateException.class,
                () -> WorldRegistry.lookupProjectionByCode(ProjectionEnum.PROJ_ACID));
    }

    // ---- Quests ------------------------------------------------------------

    @Test
    @DisplayName("getQuestMax counts the records with no blank slot, as finish_parse_quest does")
    void questMaxIsRecordCount() {
        WorldRegistry.setQuests(List.of(quest(0, "Sauron"), quest(1, "Morgoth")));

        assertEquals(2, WorldRegistry.getQuestMax());
        assertEquals(2, WorldRegistry.getQuests().size());
    }

    @Test
    @DisplayName("getQuestMax is zero for an empty quest list")
    void questMaxEmpty() {
        WorldRegistry.setQuests(List.of());

        assertEquals(0, WorldRegistry.getQuestMax());
    }

    @Test
    @DisplayName("getQuests is a read-only view")
    void questsViewIsReadOnly() {
        WorldRegistry.setQuests(List.of(quest(0, "Sauron")));

        assertThrows(UnsupportedOperationException.class, () -> WorldRegistry.getQuests().clear());
    }
}
