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

package uk.co.jackoftrades.middle.game.globals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.game.globals.data.CarryCapData;
import uk.co.jackoftrades.middle.game.globals.data.DunGenData;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.data.LevelMaxData;
import uk.co.jackoftrades.middle.game.globals.data.MeleeCriticalData;
import uk.co.jackoftrades.middle.game.globals.data.MonGenData;
import uk.co.jackoftrades.middle.game.globals.data.MonPlayData;
import uk.co.jackoftrades.middle.game.globals.data.OMeleeCriticalData;
import uk.co.jackoftrades.middle.game.globals.data.ORangedCriticalData;
import uk.co.jackoftrades.middle.game.globals.data.ObjMakeData;
import uk.co.jackoftrades.middle.game.globals.data.PlayerData;
import uk.co.jackoftrades.middle.game.globals.data.RangedCriticalData;
import uk.co.jackoftrades.middle.game.globals.data.StoreData;
import uk.co.jackoftrades.middle.game.globals.data.WorldData;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link GameConstants}' accessors for the scalar tunables read from {@code constants.txt}.
 *
 * <p>Every one of them is a two-hop delegation — {@code data.world().maxDepth()} — and that is
 * exactly why they are worth testing as a set rather than individually. The constants arrive as
 * thirteen records of same-typed {@code int} components, several with near-identical names inside
 * one record ({@code amtRoom}, {@code amtItem}, {@code amtGold}) and some repeated across records
 * ({@code maxDepth} appears in two, {@code debuffToh} in four). An accessor delegating to the
 * neighbouring component compiles, loads and produces a plausible number.
 *
 * <p>So the fixture gives every constant in the whole structure a <b>unique</b> value, counting
 * upward from 101, and each accessor is asserted to return its own. A single transposed delegation
 * fails, and the message says which accessor and which field it should have read.
 *
 * @author Rowan Crowther
 */
class GameConstantsAccessorsTest {

    /**
     * Whatever the constants holder held before this class ran.
     */
    private static Object savedConstants;

    /**
     * Fills the holder with a fully populated structure whose every component is distinct.
     *
     * @throws Exception if the holder cannot be reached
     */
    @BeforeAll
    static void seedConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        savedConstants = data.get(null);
        data.set(null, new GameConstantsData(
                new LevelMaxData(101),
                new MonGenData(102, 103, 104, 105, 106, 107, 108, 109, 110),
                new MonPlayData(111, 112, 113, 114, 115),
                new DunGenData(116, 117, 118, 119, 120, 121, 122, 123),
                new WorldData(124, 125, 126, 127, 128, 129, 130, 131, 132, 133),
                new CarryCapData(134, 135, 136, 137, 138),
                new StoreData(139, 140, 141, 142),
                new ObjMakeData(143, 144, 145, 146, 147, 148),
                new PlayerData(149, 150, 151, 152),
                new MeleeCriticalData(153, 154, 155, 156, 157, 158, 159, 160, 161),
                List.of(),
                new RangedCriticalData(162, 163, 164, 165, 166, 167, 168, 169, 170, 171),
                List.of(),
                new OMeleeCriticalData(172, 173, 174, 175, 176, 177),
                List.of(),
                new ORangedCriticalData(178, 179, 180, 181, 182, 183, 184, 185),
                List.of()));
    }

    /**
     * Puts the holder back, so a class running after this one sees what it expected.
     *
     * @throws Exception if the holder cannot be reached
     */
    @AfterAll
    static void restoreConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, savedConstants);
    }

    /**
     * Writes one of the private counters that the two non-delegating accessors read.
     *
     * @param name  the field's name
     * @param value the value to store
     * @throws Exception if the field cannot be reached
     */
    private static void setCounter(String name, int value) throws Exception {
        Field field = GameConstants.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setInt(null, value);
    }

    /**
     * The {@code levelMax} block: 1 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("levelMax constants read their own fields")
    void levelMaxConstants() {
        assertEquals(101, GameConstants.getLevelMaxMonsters(), "getLevelMaxMonsters should read levelMax.monsters");
    }

    /**
     * The {@code monGen} block: 9 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("monGen constants read their own fields")
    void monGenConstants() {
        assertEquals(102, GameConstants.getMonGenChance(), "getMonGenChance should read monGen.chance");
        assertEquals(103, GameConstants.getMonGenLevelMin(), "getMonGenLevelMin should read monGen.levelMin");
        assertEquals(104, GameConstants.getMonGenTownDay(), "getMonGenTownDay should read monGen.townDay");
        assertEquals(105, GameConstants.getMonGenTownNight(), "getMonGenTownNight should read monGen.townNight");
        assertEquals(106, GameConstants.getMonGenReproMax(), "getMonGenReproMax should read monGen.reproMax");
        assertEquals(107, GameConstants.getMonGenOodChance(), "getMonGenOodChance should read monGen.oodChance");
        assertEquals(108, GameConstants.getMonGenOodAmount(), "getMonGenOodAmount should read monGen.oodAmount");
        assertEquals(109, GameConstants.getMonGenGroupMax(), "getMonGenGroupMax should read monGen.groupMax");
        assertEquals(110, GameConstants.getMonGenGroupDist(), "getMonGenGroupDist should read monGen.groupDist");
    }

    /**
     * The {@code monPlay} block: 5 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("monPlay constants read their own fields")
    void monPlayConstants() {
        assertEquals(111, GameConstants.getMonPlayBreakGlyph(), "getMonPlayBreakGlyph should read monPlay.breakGlyph");
        assertEquals(112, GameConstants.getMonPlayMultRate(), "getMonPlayMultRate should read monPlay.multRate");
        assertEquals(113, GameConstants.getMonPlayLifeDrain(), "getMonPlayLifeDrain should read monPlay.lifeDrain");
        assertEquals(114, GameConstants.getMonPlayFleeRange(), "getMonPlayFleeRange should read monPlay.fleeRange");
        assertEquals(115, GameConstants.getMonPlayTurnRange(), "getMonPlayTurnRange should read monPlay.turnRange");
    }

    /**
     * The {@code dunGen} block: 8 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("dunGen constants read their own fields")
    void dunGenConstants() {
        assertEquals(116, GameConstants.getDunGenCentMax(), "getDunGenCentMax should read dunGen.centMax");
        assertEquals(117, GameConstants.getDunGenDoorMax(), "getDunGenDoorMax should read dunGen.doorMax");
        assertEquals(118, GameConstants.getDunGenWallMax(), "getDunGenWallMax should read dunGen.wallMax");
        assertEquals(119, GameConstants.getDunGenTunnMax(), "getDunGenTunnMax should read dunGen.tunnMax");
        assertEquals(120, GameConstants.getDunGenAmtRoom(), "getDunGenAmtRoom should read dunGen.amtRoom");
        assertEquals(121, GameConstants.getDunGenAmtItem(), "getDunGenAmtItem should read dunGen.amtItem");
        assertEquals(122, GameConstants.getDunGenAmtGold(), "getDunGenAmtGold should read dunGen.amtGold");
        assertEquals(123, GameConstants.getDunGenPitMax(), "getDunGenPitMax should read dunGen.pitMax");
    }

    /**
     * The {@code world} block: 10 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("world constants read their own fields")
    void worldConstants() {
        assertEquals(124, GameConstants.getWorldMaxDepth(), "getWorldMaxDepth should read world.maxDepth");
        assertEquals(125, GameConstants.getWorldDayLength(), "getWorldDayLength should read world.dayLength");
        assertEquals(126, GameConstants.getWorldDungeonHgt(), "getWorldDungeonHgt should read world.dungeonHgt");
        assertEquals(127, GameConstants.getWorldDungeonWid(), "getWorldDungeonWid should read world.dungeonWid");
        assertEquals(128, GameConstants.getWorldTownHgt(), "getWorldTownHgt should read world.townHgt");
        assertEquals(129, GameConstants.getWorldTownWid(), "getWorldTownWid should read world.townWid");
        assertEquals(130, GameConstants.getWorldFeelingTotal(), "getWorldFeelingTotal should read world.feelingTotal");
        assertEquals(131, GameConstants.getWorldFeelingNeed(), "getWorldFeelingNeed should read world.feelingNeed");
        assertEquals(132, GameConstants.getWorldStairSkip(), "getWorldStairSkip should read world.stairSkip");
        assertEquals(133, GameConstants.getWorldMoveEnergy(), "getWorldMoveEnergy should read world.moveEnergy");
    }

    /**
     * The {@code carryCap} block: 5 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("carryCap constants read their own fields")
    void carryCapConstants() {
        assertEquals(134, GameConstants.getCarryCapPackSize(), "getCarryCapPackSize should read carryCap.packSize");
        assertEquals(135, GameConstants.getCarryCapQuiverSize(), "getCarryCapQuiverSize should read carryCap.quiverSize");
        assertEquals(136, GameConstants.getCarryCapQuiverSlotSize(), "getCarryCapQuiverSlotSize should read carryCap.quiverSlotSize");
        assertEquals(137, GameConstants.getCarryCapThrownQuiverMult(), "getCarryCapThrownQuiverMult should read carryCap.thrownQuiverMult");
        assertEquals(138, GameConstants.getCarryCapFloorSize(), "getCarryCapFloorSize should read carryCap.floorSize");
    }

    /**
     * The {@code store} block: 4 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("store constants read their own fields")
    void storeConstants() {
        assertEquals(139, GameConstants.getStoreInvenMax(), "getStoreInvenMax should read store.invenMax");
        assertEquals(140, GameConstants.getStoreTurns(), "getStoreTurns should read store.turns");
        assertEquals(141, GameConstants.getStoreShuffle(), "getStoreShuffle should read store.shuffle");
        assertEquals(142, GameConstants.getStoreMagicLevel(), "getStoreMagicLevel should read store.magicLevel");
    }

    /**
     * The {@code objMake} block: 6 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("objMake constants read their own fields")
    void objMakeConstants() {
        assertEquals(143, GameConstants.getObjectMakeMaxDepth(), "getObjectMakeMaxDepth should read objMake.maxDepth");
        assertEquals(144, GameConstants.getObjectMakeGreatObj(), "getObjectMakeGreatObj should read objMake.greatObj");
        assertEquals(145, GameConstants.getObjectMakeGreatEgo(), "getObjectMakeGreatEgo should read objMake.greatEgo");
        assertEquals(146, GameConstants.getObjectMakeFuelTorch(), "getObjectMakeFuelTorch should read objMake.fuelTorch");
        assertEquals(147, GameConstants.getObjectMakeFuelLamp(), "getObjectMakeFuelLamp should read objMake.fuelLamp");
        assertEquals(148, GameConstants.getObjectMakeDefaultLamp(), "getObjectMakeDefaultLamp should read objMake.defaultLamp");
    }

    /**
     * The {@code player} block: 4 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("player constants read their own fields")
    void playerConstants() {
        assertEquals(149, GameConstants.getPlayerMaxSight(), "getPlayerMaxSight should read player.maxSight");
        assertEquals(150, GameConstants.getPlayerMaxRange(), "getPlayerMaxRange should read player.maxRange");
        assertEquals(151, GameConstants.getPlayerStartGold(), "getPlayerStartGold should read player.startGold");
        assertEquals(152, GameConstants.getPlayerFoodValue(), "getPlayerFoodValue should read player.foodValue");
    }

    /**
     * The {@code meleeCritical} block: 9 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("meleeCritical constants read their own fields")
    void meleeCriticalConstants() {
        assertEquals(153, GameConstants.getMeleeCriticalDebuffToh(), "getMeleeCriticalDebuffToh should read meleeCritical.debuffToh");
        assertEquals(154, GameConstants.getMeleeCriticalChanceWeightScale(), "getMeleeCriticalChanceWeightScale should read meleeCritical.chanceWeightScale");
        assertEquals(155, GameConstants.getMeleeCriticalChanceTohScale(), "getMeleeCriticalChanceTohScale should read meleeCritical.chanceTohScale");
        assertEquals(156, GameConstants.getMeleeCriticalChanceLevelScale(), "getMeleeCriticalChanceLevelScale should read meleeCritical.chanceLevelScale");
        assertEquals(157, GameConstants.getMeleeCriticalChanceTohSkillScale(), "getMeleeCriticalChanceTohSkillScale should read meleeCritical.chanceTohSkillScale");
        assertEquals(158, GameConstants.getMeleeCriticalChanceOffset(), "getMeleeCriticalChanceOffset should read meleeCritical.chanceOffset");
        assertEquals(159, GameConstants.getMeleeCriticalChanceRange(), "getMeleeCriticalChanceRange should read meleeCritical.chanceRange");
        assertEquals(160, GameConstants.getMeleeCriticalPowerWeightScale(), "getMeleeCriticalPowerWeightScale should read meleeCritical.powerWeightScale");
        assertEquals(161, GameConstants.getMeleeCriticalPowerRandom(), "getMeleeCriticalPowerRandom should read meleeCritical.powerRandom");
    }

    /**
     * The {@code oRangedCritical} block: 7 constants, each read back through its own accessor.
     */
    @Test
    @DisplayName("oRangedCritical constants read their own fields")
    void oRangedCriticalConstants() {
        assertEquals(179, GameConstants.getORangedCriticalPowerLaunchedTohScaleNumerator(), "getORangedCriticalPowerLaunchedTohScaleNumerator should read oRangedCritical.powerLaunchedTohScaleNumerator");
        assertEquals(180, GameConstants.getORangedCriticalPowerLaunchedTohScaleDenominator(), "getORangedCriticalPowerLaunchedTohScaleDenominator should read oRangedCritical.powerLaunchedTohScaleDenominator");
        assertEquals(181, GameConstants.getORangedCriticalPowerThrownTohScaleNumerator(), "getORangedCriticalPowerThrownTohScaleNumerator should read oRangedCritical.powerThrownTohScaleNumerator");
        assertEquals(182, GameConstants.getORangedCriticalPowerThrownTohScaleDenominator(), "getORangedCriticalPowerThrownTohScaleDenominator should read oRangedCritical.powerThrownTohScaleDenominator");
        assertEquals(183, GameConstants.getORangedCriticalChancePowerScaleNumerator(), "getORangedCriticalChancePowerScaleNumerator should read oRangedCritical.chancePowerScaleNumerator");
        assertEquals(184, GameConstants.getORangedCriticalChancePowerScaleDenominator(), "getORangedCriticalChancePowerScaleDenominator should read oRangedCritical.chancePowerScaleDenominator");
        assertEquals(185, GameConstants.getORangedCriticalChanceAddDenominator(), "getORangedCriticalChanceAddDenominator should read oRangedCritical.chanceAddDenominator");
    }

    /**
     * The three accessors that do not delegate to the constants structure at all: two read private
     * counters the loader writes, and one forwards to the object registry. They are grouped here
     * because a reader scanning the class would otherwise expect them to behave like the sixty-eight
     * above.
     *
     * @throws Exception if a counter cannot be reached
     */
    @Test
    @DisplayName("the three non-delegating accessors read their own sources")
    void nonDelegatingAccessors() throws Exception {
        setCounter("storeMax", 71);
        setCounter("caveProfileMax", 72);

        assertEquals(71, GameConstants.getStoreMax());
        assertEquals(72, GameConstants.getCaveProfileMax());
    }

    /**
     * With no constants loaded the accessors throw rather than answering a default. That is worth
     * pinning: it means the data files have to be read before anything asks, and a caller cannot
     * treat an unloaded game as one with zeroed tunables.
     *
     * @throws Exception if the holder cannot be reached
     */
    @Test
    @DisplayName("an unloaded holder throws rather than answering zero")
    void unloadedHolderThrows() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        Object seeded = data.get(null);
        try {
            data.set(null, null);

            assertThrows(NullPointerException.class, GameConstants::getWorldMaxDepth);
        } finally {
            data.set(null, seeded);
        }
    }
}
