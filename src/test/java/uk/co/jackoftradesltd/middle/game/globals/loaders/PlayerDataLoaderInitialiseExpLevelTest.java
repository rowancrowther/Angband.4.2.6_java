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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.messages.data.PlayerCharSheetView;
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link PlayerDataLoader#initialiseExpLevel()} — the port of C's
 * {@code player_exp[PY_MAX_LEVEL]} constant array ({@code player.c:48-100}).
 *
 * <p><b>Where the expected values come from.</b> Every assertion below is transcribed directly
 * from the fifty entries in {@code player.c}, not from {@link PlayerRegistry#playerExperience}
 * itself, so a transcription slip in the port would show up here rather than being echoed back.
 *
 * <p><b>Global state.</b> {@link PlayerRegistry#playerExperience} and the character-sheet cache in
 * {@link PlayerEventStatusUpdate} are both static and shared across the JVM, so each test saves
 * what it finds beforehand and restores it afterwards, the same as
 * {@code uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistryFoodThresholdsTest} does
 * for the {@code PY_FOOD_*} thresholds.
 *
 * <p>Class PlayerDataLoaderInitialiseExpLevelTest coded on 260925, commented in full on 260925.
 *
 * @author Rowan Crowther
 */
class PlayerDataLoaderInitialiseExpLevelTest {

    /**
     * The table's fifty values in level order, transcribed from {@code player.c:50-99}.
     */
    private static final long[] EXPECTED = {
            10L, 25L, 45L, 70L, 100L, 140L, 200L, 280L, 380L, 500L,
            650L, 850L, 1100L, 1400L, 1800L, 2300L, 2900L, 3600L, 4400L, 5400L,
            6800L, 8400L, 10200L, 12500L, 17500L, 25000L, 35000L, 50000L, 75000L, 100000L,
            150000L, 200000L, 275000L, 350000L, 450000L, 550000L, 700000L, 850000L, 1000000L, 1250000L,
            1500000L, 1800000L, 2100000L, 2400000L, 2700000L, 3000000L, 3500000L, 4000000L, 4500000L, 5000000L};

    /**
     * {@link PlayerRegistry#playerExperience} as it was before this test, restored afterwards.
     */
    private Map<Integer, Long> savedPlayerExperience;

    /**
     * The character-sheet snapshot in place before the test, restored afterwards.
     */
    private PlayerCharSheetView savedPlayerCharSheetView;

    /**
     * Takes a copy of the two static caches this test is about to overwrite.
     */
    @BeforeEach
    void saveGlobals() {
        savedPlayerExperience = new HashMap<>(PlayerRegistry.playerExperience);
        savedPlayerCharSheetView = PlayerEventStatusUpdate.getPlayerCharSheetView();
    }

    /**
     * Puts both caches back once the test is done.
     */
    @AfterEach
    void restoreGlobals() {
        PlayerRegistry.playerExperience.clear();
        PlayerRegistry.playerExperience.putAll(savedPlayerExperience);
        PlayerEventStatusUpdate.updatePlayerCharSheetView(savedPlayerCharSheetView);
    }

    /**
     * The ordinary path: all fifty entries land in {@link PlayerRegistry#playerExperience} at the
     * key matching their C array index, with no entry short or extra.
     */
    @Test
    @DisplayName("fills all fifty levels with the C table's values")
    void fillsAllFiftyLevels() {
        PlayerRegistry.playerExperience.clear();

        PlayerDataLoader.initialiseExpLevel();

        assertEquals(50, PlayerRegistry.playerExperience.size(), "entry count");
        for (int level = 0; level < EXPECTED.length; level++) {
            assertEquals(EXPECTED[level], PlayerRegistry.playerExperience.get(level),
                    "level index " + level);
        }
    }

    /**
     * The two boundary levels by name, rather than by looping — level 1 ({@code 10}) and level 50
     * ({@code 5000000}), C's first and last entries.
     */
    @Test
    @DisplayName("matches the C table at both ends")
    void matchesBothEnds() {
        PlayerRegistry.playerExperience.clear();

        PlayerDataLoader.initialiseExpLevel();

        assertEquals(10L, PlayerRegistry.playerExperience.get(0), "level 1");
        assertEquals(5000000L, PlayerRegistry.playerExperience.get(49), "level 50");
    }

    /**
     * The map is cleared before being refilled, so an unrelated key left over from a previous run
     * does not survive a call — C's array has no such possibility, since it is a fixed-size
     * compile-time constant, but the port's {@code Map} does not enforce that on its own.
     */
    @Test
    @DisplayName("clears any pre-existing entries before refilling")
    void clearsPreExistingEntries() {
        PlayerRegistry.playerExperience.clear();
        PlayerRegistry.playerExperience.put(99, 123456789L);

        PlayerDataLoader.initialiseExpLevel();

        assertEquals(50, PlayerRegistry.playerExperience.size(), "entry count");
        assertEquals(false, PlayerRegistry.playerExperience.containsKey(99), "stale key 99");
    }

    /**
     * A stale value under an in-range key is overwritten with the C value rather than left as it
     * was, since the method's job is to (re)establish the table, not merge into it.
     */
    @Test
    @DisplayName("overwrites a stale value already at an in-range key")
    void overwritesStaleInRangeValue() {
        PlayerRegistry.playerExperience.clear();
        PlayerRegistry.playerExperience.put(10, -1L);

        PlayerDataLoader.initialiseExpLevel();

        assertEquals(650L, PlayerRegistry.playerExperience.get(10), "level 11");
    }

    /**
     * The same fifty values, copied into a primitive array in key order, are pushed to
     * {@link PlayerEventStatusUpdate#getPlayerCharSheetView()}{@code .expToLevel()} — the point at
     * which the freshly loaded table reaches the UI cache. C has no equivalent step: the array is
     * read directly wherever it is needed, rather than copied into a display cache.
     */
    @Test
    @DisplayName("pushes the same fifty values to the char-sheet cache")
    void pushesTableToCharSheetCache() {
        PlayerRegistry.playerExperience.clear();

        PlayerDataLoader.initialiseExpLevel();

        assertArrayEquals(EXPECTED,
                PlayerEventStatusUpdate.getPlayerCharSheetView().expToLevel(),
                "expToLevel");
    }

    /**
     * Pushing the exp-to-level array rebuilds the char-sheet snapshot in place, so every other
     * field the snapshot already carried survives untouched — the same wrong-constructor-slot risk
     * {@code PlayerEventStatusUpdateTest} guards against for the rest of that record's setters —
     * except for the nine skills-panel fields ({@code saveSkill} through
     * {@code optionEffectiveSpeed}), which {@code updatePlayerCharSheetExpToLevel} resets to their
     * zero value rather than carrying forward, the same placeholder behaviour
     * {@code PlayerEventStatusUpdateTest.CHAR_SHEET_PLACEHOLDER_RESET_FIELDS} documents for every
     * other char-sheet setter.
     */
    @Test
    @DisplayName("leaves every other char-sheet field untouched, apart from the placeholder fields")
    void leavesOtherCharSheetFieldsUntouched() {
        PlayerRegistry.playerExperience.clear();
        PlayerCharSheetView distinct = new PlayerCharSheetView(7, false,
                new int[]{1, 2, 3, 4, 5}, new int[]{6, 7, 8, 9, 10}, new int[]{11, 12, 13, 14, 15},
                new int[]{16, 17, 18, 19, 20}, new int[]{21, 22, 23, 24, 25}, 26, 27, new long[]{}, 28,
                29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41,
                42, 43, 44, 45, 46, 47, 48, 49, true);
        PlayerEventStatusUpdate.updatePlayerCharSheetView(distinct);

        PlayerDataLoader.initialiseExpLevel();

        PlayerCharSheetView result = PlayerEventStatusUpdate.getPlayerCharSheetView();
        assertEquals(7, result.bodyCount(), "bodyCount");
        assertEquals(false, result.playerIsPlaying(), "playerIsPlaying");
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.playerRaceStatBonuses(), "playerRaceStatBonuses");
        assertArrayEquals(new int[]{6, 7, 8, 9, 10}, result.playerClassStatBonuses(), "playerClassStatBonuses");
        assertArrayEquals(new int[]{11, 12, 13, 14, 15}, result.playerEquipStatBonuses(), "playerEquipStatBonuses");
        assertArrayEquals(new int[]{16, 17, 18, 19, 20}, result.playerTotalStatBonuses(), "playerTotalStatBonuses");
        assertArrayEquals(new int[]{21, 22, 23, 24, 25}, result.playerCurrModStat(), "playerCurrModStat");
        assertEquals(26, result.totalWeight(), "totalWeight");
        assertEquals(27, result.weightLimit(), "weightLimit");
        assertEquals(28, result.expFactor(), "expFactor");
        assertEquals(29, result.height(), "height");
        assertEquals(30, result.weight(), "weight");
        assertEquals(31, result.age(), "age");
        assertEquals(32, result.toA(), "toA");
        assertEquals(33, result.toD(), "toD");
        assertEquals(34, result.toH(), "toH");
        assertEquals(35, result.meleeSkill(), "meleeSkill");
        assertEquals(36, result.shootSkill(), "shootSkill");
        assertEquals(37, result.bthPlusAdj(), "bthPlusAdj");
        assertEquals(38, result.meleeDice(), "meleeDice");
        assertEquals(39, result.meleeSides(), "meleeSides");
        assertEquals(40, result.numBlows(), "numBlows");
        assertEquals(41, result.numShots(), "numShots");
        // The nine skills-panel fields are placeholders every char-sheet setter currently resets
        // to its type's zero value rather than carrying forward — see this method's Javadoc.
        assertEquals(0, result.saveSkill(), "saveSkill");
        assertEquals(0, result.stealthSkill(), "stealthSkill");
        assertEquals(0, result.disarmPhysSkill(), "disarmPhysSkill");
        assertEquals(0, result.disarmMagicSkill(), "disarmMagicSkill");
        assertEquals(0, result.deviceSkill(), "deviceSkill");
        assertEquals(0, result.searchSkill(), "searchSkill");
        assertEquals(0, result.infra(), "infra");
        assertEquals(0, result.calcSpeed(), "calcSpeed");
        assertEquals(false, result.optionEffectiveSpeed(), "optionEffectiveSpeed");
    }
}
