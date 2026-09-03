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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterLore;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.ObjectUtils;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerBirth.playerInit}, the port of C's {@code player_init}
 * ({@code player-birth.c:396-458}).
 *
 * <p>{@code playerInit} is private and instance-scoped, and nothing in the port calls it — the
 * same gap noted on the method's own Javadoc. Every test here reaches it through reflection on a
 * freshly constructed {@link PlayerBirth}.
 *
 * <p>The method leans on five registries ({@link ObjectRegistry}, {@link MonsterRegistry},
 * {@link PlayerRegistry}, {@link WorldRegistry}, {@link GameConstants}) that are otherwise loaded
 * once from the data files. Each test seeds only what {@code playerInit} itself reads — a two
 * or three-element list standing in for the real table — and restores whatever was there before,
 * so this suite runs the same whether it is the only class executed or one of hundreds.
 *
 * <p>The two findings from the target's stage-1 review are the centre of this suite: C's kind and
 * race loops both start at index 1 ({@code player-birth.c:415,421}), skipping a synthetic zeroth
 * entry — the {@code <pile>} kind and the {@code <player>} race — while the artifact loop
 * ({@code player-birth.c:407}) starts at index 0 and skips nothing. Each fixture list here puts a
 * "sentinel" first and a "real" second, so a loop that touched the wrong end would be caught.
 *
 * @author Rowan Crowther
 */
class PlayerBirthPlayerInitTest {

    /**
     * The pairs of (owner, field name) this suite seeds and must hand back afterward.
     * {@link MonsterRegistry#monsterRaces} is public and handled separately, not through this
     * list, since it needs no reflection.
     */
    private static final Object[][] SEEDED_FIELDS = {
            {ObjectRegistry.class, "objectKinds"},
            {ObjectRegistry.class, "artifacts"},
            {ObjectRegistry.class, "curses"},
            {PlayerRegistry.class, "playerBodies"},
            {PlayerRegistry.class, "playerRaces"},
            {PlayerRegistry.class, "playerClasses"},
            {PlayerRegistry.class, "playerShapes"},
            {WorldRegistry.class, "quests"},
            {GameConstants.class, "data"},
    };
    /**
     * Snapshots of every static registry field this suite seeds, keyed by
     * {@code "OwnerClass#fieldName"}, restored verbatim in {@link #restoreRegistries()}.
     */
    private final Map<String, Object> savedStatics = new HashMap<>();
    private List<MonsterRace> savedMonsterRaces;
    private int savedTurn;

    // ------------------------------------------------------------------
    // Reflection plumbing
    // ------------------------------------------------------------------
    private Player player;
    private ObjectKind sentinelKind;
    private ObjectKind realKind;
    private MonsterRace sentinelRace;
    private MonsterRace normalRace;
    private MonsterRace uniqueRace;

    // ------------------------------------------------------------------
    // Fixture builders
    // ------------------------------------------------------------------
    private MonsterLore sentinelLore;
    private MonsterLore normalLore;
    private MonsterLore uniqueLore;
    private Artifact testArtifact;
    private Quest seededQuest;

    private static Field accessibleField(Class<?> owner, String name) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    // ------------------------------------------------------------------
    // Shared fixtures, rebuilt for every test
    // ------------------------------------------------------------------

    private static Object readStatic(Class<?> owner, String name) throws Exception {
        return accessibleField(owner, name).get(null);
    }

    private static void writeStatic(Class<?> owner, String name, Object value) throws Exception {
        accessibleField(owner, name).set(null, value);
    }

    private static Object readInstance(Object target, String name) throws Exception {
        return accessibleField(target.getClass(), name).get(target);
    }

    private static void writeInstance(Object target, String name, Object value) throws Exception {
        accessibleField(target.getClass(), name).set(target, value);
    }

    private static void invokePlayerInit(Player player) throws Exception {
        Method method = PlayerBirth.class.getDeclaredMethod("playerInit", Player.class);
        method.setAccessible(true);
        method.invoke(new PlayerBirth(), player);
    }

    private static PlayerClass plainClass(String name) {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        return new PlayerClass(name, new ArrayList<>(), stats, skills, new HashMap<>(skills),
                0, 100, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                1, 1, 1, new ArrayList<>(), null);
    }

    private static PlayerShape normalShape() {
        return new PlayerShape("normal", 0, 0, 0, new HashMap<>(), new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class), new HashMap<>(), new HashMap<>(), new ArrayList<>(),
                0, new ArrayList<>());
    }

    private static Artifact artifact() {
        return new Artifact("Test Blade", "It gleams.", TValue.TV_SWORD, "long sword",
                5, 6, 7, 8, "3d5", 120, 4500,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(),
                40, 11, 12, 13,
                new Activation("test activation", 1, false, 5, 30, new ArrayList<>(), "It fires.", "fires"),
                "The blade glows.", new Random(0, 1, 1, 20, false));
    }

    private static MonsterLore freshLore() {
        return new MonsterLore(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                null, null, null, null, null, null, null, null);
    }

    private static void makeUnique(MonsterRace race) throws Exception {
        writeInstance(race, "flags", new Flag<>(MonsterRaceFlag.class, MonsterRaceFlag.RF_UNIQUE));
    }

    private static GameConstantsData carryCapOnly() {
        return new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, null, null, null, null, null, null, null, null, null);
    }

    @BeforeEach
    void seedRegistriesAndRunPlayerInit() throws Exception {
        for (Object[] pair : SEEDED_FIELDS) {
            Class<?> owner = (Class<?>) pair[0];
            String name = (String) pair[1];
            savedStatics.put(owner.getName() + "#" + name, readStatic(owner, name));
        }
        savedMonsterRaces = MonsterRegistry.monsterRaces;
        savedTurn = GameState.getTurn();

        // PlayerRegistry: a body, a race, a class, and the "normal" shape playerInit looks up.
        PlayerBody body = SeededPlayerRegistry.humanoidBody();
        writeStatic(PlayerRegistry.class, "playerBodies", List.of(body));
        writeStatic(PlayerRegistry.class, "playerRaces", List.of(SeededPlayerRegistry.plainRace(body)));
        writeStatic(PlayerRegistry.class, "playerClasses", List.of(plainClass("Test Class")));
        writeStatic(PlayerRegistry.class, "playerShapes", List.of(normalShape()));

        // ObjectRegistry: no curses (KnownObject#initCurses needs a non-null list), one artifact,
        // and a sentinel-then-real pair of kinds mirroring k_info[0]'s <pile> placeholder.
        ObjectRegistry.setCurses(new ArrayList<Curse>());
        testArtifact = artifact();
        ObjectUtils.markArtifactCreated(testArtifact, true);
        ObjectUtils.markArtifactSeen(testArtifact, true);
        ObjectRegistry.setArtifacts(List.of(testArtifact));

        sentinelKind = new ObjectKind();
        sentinelKind.setTried(true);
        sentinelKind.setAware(true);
        realKind = new ObjectKind();
        realKind.setTried(true);
        realKind.setAware(true);
        ObjectRegistry.setObjectKinds(List.of(sentinelKind, realKind));

        // MonsterRegistry: a sentinel-then-real pair mirroring r_info[0]'s <player> placeholder,
        // plus a third, unique race to exercise the RF_UNIQUE branch.
        sentinelRace = new MonsterRace();
        sentinelLore = freshLore();
        sentinelLore.setPSkills(7);
        sentinelLore.setThefts(3);
        sentinelRace.setLore(sentinelLore);
        sentinelRace.setCurNum(5);
        sentinelRace.setMaxNum(42);

        normalRace = new MonsterRace();
        normalLore = freshLore();
        normalRace.setLore(normalLore);

        uniqueRace = new MonsterRace();
        makeUnique(uniqueRace);
        uniqueLore = freshLore();
        uniqueRace.setLore(uniqueLore);

        MonsterRegistry.monsterRaces = List.of(sentinelRace, normalRace, uniqueRace);

        // WorldRegistry: one quest for player_quests_reset to copy in.
        seededQuest = new Quest(0, "Test Quest", 3, null, 0, 1);
        WorldRegistry.setQuests(List.of(seededQuest));

        // GameConstants: enough for PlayerUpkeep's quiver/inventory sizing.
        writeStatic(GameConstants.class, "data", carryCapOnly());

        GameState.setTurn(999);

        // A "played" character: stale identity, vitals and quest history that player_init's
        // memset (Player#wipe) must clear, and a non-default option it must carry through.
        player = new Player();
        writeInstance(player, "fullName", "Stale Hero");
        writeInstance(player, "au", 500);
        player.setLevel(50);
        player.setCurrentHP(30);
        player.setQuests(new ArrayList<>(List.of(new Quest(0, "Stale Quest", 1, null, 0, 1))));
        Flag<PlayerOptionEnum> optionFlags =
                (Flag<PlayerOptionEnum>) readInstance(player.getPlayerOptions(), "options");
        optionFlags.off(PlayerOptionEnum.OP_pickup_inven);

        invokePlayerInit(player);
    }

    @AfterEach
    void restoreRegistries() throws Exception {
        for (Object[] pair : SEEDED_FIELDS) {
            Class<?> owner = (Class<?>) pair[0];
            String name = (String) pair[1];
            writeStatic(owner, name, savedStatics.get(owner.getName() + "#" + name));
        }
        MonsterRegistry.monsterRaces = savedMonsterRaces;
        GameState.setTurn(savedTurn);
    }

    // ------------------------------------------------------------------
    // The wipe - C's memset(p, 0, sizeof(struct player))
    // ------------------------------------------------------------------

    @Test
    @DisplayName("wipes stale identity and vitals, matching C's memset")
    void wipeClearsStaleFields() throws Exception {
        assertEquals(0, player.getLevel(), "level not zeroed");
        assertEquals(0, player.getCurrentHP(), "currentHP not zeroed");
        assertEquals(0, (int) readInstance(player, "au"), "au not zeroed");
        assertEquals(null, readInstance(player, "fullName"), "fullName not cleared");
    }

    @Test
    @DisplayName("player_quests_reset rebuilds quests from WorldRegistry, discarding the old list")
    void questsRebuiltFromRegistry() {
        assertEquals(1, player.getQuests().size(), "quest list not rebuilt to the registry's one quest");
        Quest carried = player.getQuests().get(0);
        assertEquals("Test Quest", carried.getName(), "wrong quest copied in");
        assertNotSame(seededQuest, carried, "player_quests_reset must copy, not alias, the registry entry");
    }

    // ------------------------------------------------------------------
    // Options persist across the wipe - C's opts_save / p->opts = opts_save
    // ------------------------------------------------------------------

    @Test
    @DisplayName("a customised option survives the wipe instead of reverting to default")
    void optionsPersistAcrossWipe() {
        assertFalse(player.getPlayerOptions().has(PlayerOptionEnum.OP_pickup_inven),
                "OP_pickup_inven (default on) should still be off after playerInit - "
                        + "C restores opts_save, it does not re-default");
    }

    // ------------------------------------------------------------------
    // Object-kind loop - C's for (i = 1; ... i < z_info->k_max; i++), player-birth.c:415
    // ------------------------------------------------------------------

    @Test
    @DisplayName("kind loop skips index 0, the <pile>-equivalent sentinel entry")
    void kindLoopSkipsIndexZero() throws Exception {
        assertTrue((boolean) readInstance(sentinelKind, "tried"), "sentinel kind's tried was touched");
        assertTrue(sentinelKind.isAware(), "sentinel kind's aware was touched");
    }

    @Test
    @DisplayName("kind loop resets tried and aware from index 1 onward")
    void kindLoopResetsRealKind() throws Exception {
        assertFalse((boolean) readInstance(realKind, "tried"), "real kind's tried not reset");
        assertFalse(realKind.isAware(), "real kind's aware not reset");
    }

    // ------------------------------------------------------------------
    // Monster-race loop - C's for (i = 1; ... i < z_info->r_max; i++), player-birth.c:421
    // ------------------------------------------------------------------

    @Test
    @DisplayName("race loop skips index 0, the <player>-equivalent sentinel entry")
    void raceLoopSkipsIndexZero() throws Exception {
        assertEquals(5, readInstance(sentinelRace, "curNum"), "sentinel race's curNum was touched");
        assertEquals(42, readInstance(sentinelRace, "maxNum"), "sentinel race's maxNum was touched");
        assertEquals(7, readInstance(sentinelLore, "pkills"), "sentinel race's lore pkills was touched");
        assertEquals(3, readInstance(sentinelLore, "thefts"), "sentinel race's lore thefts was touched");
    }

    @Test
    @DisplayName("race loop resets a non-unique race to cur_num 0, max_num 100")
    void raceLoopResetsNonUnique() throws Exception {
        assertEquals(0, readInstance(normalRace, "curNum"));
        assertEquals(100, readInstance(normalRace, "maxNum"));
        assertEquals(0, readInstance(normalLore, "pkills"));
        assertEquals(0, readInstance(normalLore, "thefts"));
    }

    @Test
    @DisplayName("race loop caps an RF_UNIQUE race's max_num at 1, not 100")
    void raceLoopCapsUniqueAtOne() throws Exception {
        assertEquals(0, readInstance(uniqueRace, "curNum"));
        assertEquals(1, readInstance(uniqueRace, "maxNum"), "RF_UNIQUE branch (player-birth.c:426-427) not applied");
    }

    // ------------------------------------------------------------------
    // Artifact loop - C's for (i = 0; ... i < z_info->a_max; i++), player-birth.c:407, no skip
    // ------------------------------------------------------------------

    @Test
    @DisplayName("artifact loop touches index 0 - unlike the kind and race loops, C skips nothing here")
    void artifactLoopTouchesEveryEntry() {
        assertFalse(testArtifact.getAup().isCreated(), "artifact's created flag not cleared");
        assertFalse(testArtifact.getAup().isSeen(), "artifact's seen flag not cleared");
    }

    // ------------------------------------------------------------------
    // Upkeep, timed table, item knowledge - player-birth.c:432-442
    // ------------------------------------------------------------------

    @Test
    @DisplayName("upkeep is rebuilt with inventory/quiver sized from GameConstants")
    void upkeepSizedFromConstants() {
        assertEquals(GameConstants.getCarryCapPackSize() + 1, player.getPlayerUpkeep().getInventory().length);
        assertEquals(GameConstants.getCarryCapQuiverSize(), player.getPlayerUpkeep().getQuiver().length);
    }

    @Test
    @DisplayName("every timed effect is reset to 0")
    void timedEffectsAllZero() {
        for (TimedEffect effect : TimedEffect.values()) {
            assertEquals(0, player.getTimedEffect(effect), effect + " not reset to 0");
        }
    }

    @Test
    @DisplayName("item knowledge is rebuilt fresh, sized from the registry's curse list")
    void itemKnowledgeRebuilt() throws Exception {
        Object knowledge = readInstance(player, "itemKnowledge");
        assertNotNull(knowledge, "itemKnowledge not rebuilt");
    }

    // ------------------------------------------------------------------
    // Turn, energy and defaults - player-birth.c:447-457
    // ------------------------------------------------------------------

    @Test
    @DisplayName("the global turn counter is reset to 1")
    void turnResetToOne() {
        assertEquals(1, GameState.getTurn());
    }

    @Test
    @DisplayName("total energy and resting turn are reset to 0")
    void energyAndRestingTurnReset() throws Exception {
        assertEquals(0, player.getTotalEnergy());
        assertEquals(0, readInstance(player, "restingTurn"));
    }

    @Test
    @DisplayName("race, class and shape default to the first entries and the normal shape")
    void defaultsAssigned() {
        assertSame(PlayerRegistry.getPlayerRaces().getFirst(), player.getRace(),
                "race not set to the first entry in the edit file");
        assertSame(PlayerRegistry.getPlayerClasses().getFirst(), player.getPlayerClass(),
                "class not set to the first entry in the edit file");
        assertSame(PlayerRegistry.lookupPlayerShape("normal"), player.getShape(),
                "shape not set to \"normal\"");
    }
}
