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

package uk.co.jackoftradesltd.middle.game.globals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.UIEntryValueRegistry;
import uk.co.jackoftradesltd.middle.objects.Archery;
import uk.co.jackoftradesltd.middle.objects.ElementSet;
import uk.co.jackoftradesltd.middle.objects.FlagSet;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ResType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Pins what a real {@link GameConstants#init(Core)} leaves behind that the other test classes for
 * {@link GameConstants} do not: the three process-wide constants, the scalar tunables as they stand
 * in C's own {@code constants.txt}, and the object-power tables C declares statically in
 * {@code obj-power.c}.
 *
 * <p>Every expected figure is typed from the C source ({@code obj-util.h}, {@code cmd-core.h},
 * {@code init.c}, {@code lib/gamedata/constants.txt}, {@code obj-power.c}), never read back from the
 * port. {@code GameConstantsAccessorsTest} proves each accessor reads its own record component with
 * invented values; this class proves the components hold the numbers C's file gives them, so a
 * parser that filed a label under the wrong component is caught here. The thirteen-row element power
 * table is covered by {@code GameEngineBootstrapTest}, so it is not repeated.
 *
 * <p>One full {@link GameConstants#init(Core)} is run once, the same way
 * {@code GameConstantsInitUIBindingTest} runs it.
 *
 * <p>Class GameConstantsInitTablesTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class GameConstantsInitTablesTest {

    /**
     * Runs the full load, after the front end's UIEntry load has been sent, because
     * {@link GameConstants#init(Core)} blocks waiting for it.
     *
     * @throws IOException if a data file cannot be read
     */
    @BeforeAll
    static void bootstrap() throws IOException {
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);
    }

    /**
     * Resets the registries the heavy load filled, so it does not leak into order-sensitive suites.
     */
    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
        UIEntryValueRegistry.clearEntryBindings();
    }

    /**
     * The three constants that are fixed in C's headers: {@code MAX_PVAL} is 32767 in
     * {@code obj-util.h}, {@code CMD_MAX_ARGS} is 4 in {@code cmd-core.h}, and {@code ANGBAND_SYS}
     * defaults to {@code "xxx"} in {@code init.c}.
     */
    @Test
    @DisplayName("the header constants hold C's figures")
    void headerConstants() {
        assertEquals(32767, GameConstants.MAX_PVAL);
        assertEquals(4, GameConstants.MAX_COMMAND_ARGUMENTS);
        assertEquals("xxx", GameConstants.ANGBAND_SYS);
    }

    /**
     * The {@code level-max}, {@code mon-gen} and {@code mon-play} blocks of C's
     * {@code constants.txt}. Each value is distinct from its neighbours' where the file allows it,
     * so a label filed under the wrong component shows.
     */
    @Test
    @DisplayName("monster constants hold the figures in constants.txt")
    void monsterConstants() {
        assertEquals(1024, GameConstants.getLevelMaxMonsters());

        assertEquals(500, GameConstants.getMonGenChance());
        assertEquals(14, GameConstants.getMonGenLevelMin());
        assertEquals(4, GameConstants.getMonGenTownDay());
        assertEquals(8, GameConstants.getMonGenTownNight());
        assertEquals(100, GameConstants.getMonGenReproMax());
        assertEquals(25, GameConstants.getMonGenOodChance());
        assertEquals(10, GameConstants.getMonGenOodAmount());
        assertEquals(25, GameConstants.getMonGenGroupMax());
        assertEquals(5, GameConstants.getMonGenGroupDist());

        assertEquals(550, GameConstants.getMonPlayBreakGlyph());
        assertEquals(8, GameConstants.getMonPlayMultRate());
        assertEquals(2, GameConstants.getMonPlayLifeDrain());
        assertEquals(5, GameConstants.getMonPlayFleeRange());
        assertEquals(5, GameConstants.getMonPlayTurnRange());
    }

    /**
     * The {@code dun-gen}, {@code world} and {@code carry-cap} blocks of C's {@code constants.txt}.
     */
    @Test
    @DisplayName("dungeon, world and carrying constants hold the figures in constants.txt")
    void worldConstants() {
        assertEquals(100, GameConstants.getDunGenCentMax());
        assertEquals(200, GameConstants.getDunGenDoorMax());
        assertEquals(500, GameConstants.getDunGenWallMax());
        assertEquals(900, GameConstants.getDunGenTunnMax());
        assertEquals(9, GameConstants.getDunGenAmtRoom());
        assertEquals(3, GameConstants.getDunGenAmtItem());
        assertEquals(3, GameConstants.getDunGenAmtGold());
        assertEquals(2, GameConstants.getDunGenPitMax());

        assertEquals(128, GameConstants.getWorldMaxDepth());
        assertEquals(10000, GameConstants.getWorldDayLength());
        assertEquals(66, GameConstants.getWorldDungeonHgt());
        assertEquals(198, GameConstants.getWorldDungeonWid());
        assertEquals(22, GameConstants.getWorldTownHgt());
        assertEquals(66, GameConstants.getWorldTownWid());
        assertEquals(100, GameConstants.getWorldFeelingTotal());
        assertEquals(10, GameConstants.getWorldFeelingNeed());
        assertEquals(1, GameConstants.getWorldStairSkip());
        assertEquals(100, GameConstants.getWorldMoveEnergy());

        assertEquals(23, GameConstants.getCarryCapPackSize());
        assertEquals(10, GameConstants.getCarryCapQuiverSize());
        assertEquals(40, GameConstants.getCarryCapQuiverSlotSize());
        assertEquals(5, GameConstants.getCarryCapThrownQuiverMult());
        assertEquals(23, GameConstants.getCarryCapFloorSize());
    }

    /**
     * The {@code store}, {@code obj-make} and {@code player} blocks of C's {@code constants.txt}.
     */
    @Test
    @DisplayName("store, object and player constants hold the figures in constants.txt")
    void storeObjectAndPlayerConstants() {
        assertEquals(24, GameConstants.getStoreInvenMax());
        assertEquals(1000, GameConstants.getStoreTurns());
        assertEquals(25, GameConstants.getStoreShuffle());
        assertEquals(5, GameConstants.getStoreMagicLevel());

        assertEquals(100, GameConstants.getObjectMakeMaxDepth());
        assertEquals(20, GameConstants.getObjectMakeGreatObj());
        assertEquals(20, GameConstants.getObjectMakeGreatEgo());
        assertEquals(5000, GameConstants.getObjectMakeFuelTorch());
        assertEquals(15000, GameConstants.getObjectMakeFuelLamp());
        assertEquals(7500, GameConstants.getObjectMakeDefaultLamp());

        assertEquals(20, GameConstants.getPlayerMaxSight());
        assertEquals(20, GameConstants.getPlayerMaxRange());
        assertEquals(600, GameConstants.getPlayerStartGold());
        assertEquals(100, GameConstants.getPlayerFoodValue());
    }

    /**
     * The public critical-hit constants of C's {@code constants.txt}: the whole
     * {@code melee-critical} block, including the negative {@code chance-offset} that a sign-dropping
     * parser would lose, and the {@code o-ranged-critical} figures.
     */
    @Test
    @DisplayName("critical constants hold the figures in constants.txt")
    void criticalConstants() {
        assertEquals(10, GameConstants.getMeleeCriticalDebuffToh());
        assertEquals(1, GameConstants.getMeleeCriticalChanceWeightScale());
        assertEquals(5, GameConstants.getMeleeCriticalChanceTohScale());
        assertEquals(0, GameConstants.getMeleeCriticalChanceLevelScale());
        assertEquals(1, GameConstants.getMeleeCriticalChanceTohSkillScale());
        assertEquals(-60, GameConstants.getMeleeCriticalChanceOffset());
        assertEquals(5000, GameConstants.getMeleeCriticalChanceRange());
        assertEquals(1, GameConstants.getMeleeCriticalPowerWeightScale());
        assertEquals(650, GameConstants.getMeleeCriticalPowerRandom());

        assertEquals(1, GameConstants.getORangedCriticalPowerLaunchedTohScaleNumerator());
        assertEquals(1, GameConstants.getORangedCriticalPowerLaunchedTohScaleDenominator());
        assertEquals(3, GameConstants.getORangedCriticalPowerThrownTohScaleNumerator());
        assertEquals(2, GameConstants.getORangedCriticalPowerThrownTohScaleDenominator());
        assertEquals(1, GameConstants.getORangedCriticalChancePowerScaleNumerator());
        assertEquals(1, GameConstants.getORangedCriticalChancePowerScaleDenominator());
    }

    /**
     * C's {@code archery[]} table ({@code obj-power.c}): {ammo_tval, ammo_dam, launch_dam,
     * launch_mult} for shots, arrows and bolts. The port keys the rows by tval instead of indexing
     * by {@code tval - TV_SHOT}, so each row is looked up by its key and then checked against the
     * figures.
     */
    @Test
    @DisplayName("the archery table holds C's three rows")
    void archeryTableMatchesC() {
        Object[][] c = {
                {TValue.TV_SHOT, 10, 9, 4},
                {TValue.TV_ARROW, 12, 9, 5},
                {TValue.TV_BOLT, 14, 9, 7},
        };

        assertEquals(c.length, ObjectRegistry.archery.size(), "one row per ammunition tval");
        for (Object[] row : c) {
            Archery found = ObjectRegistry.archery.get((TValue) row[0]);
            String where = "row " + row[0];
            assertSame(row[0], found.getAmmoType(), where + " ammo_tval");
            assertEquals(row[1], found.getAmmoDamage(), where + " ammo_dam");
            assertEquals(row[2], found.getLaunchDamage(), where + " launch_dam");
            assertEquals(row[3], found.getLaunchMult(), where + " launch_mult");
        }
    }

    /**
     * C's {@code flag_sets[]} table ({@code obj-power.c}): {type, factor, bonus, size, count, desc}
     * for sustains, protections and miscellaneous abilities. {@code count} starts at 0 in C and is
     * recomputed for each object, so it must start at 0 here too.
     */
    @Test
    @DisplayName("the flag set table holds C's three rows")
    void flagSetTableMatchesC() {
        Object[][] c = {
                {ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains"},
                {ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections"},
                {ObjectFlagType.OFT_MISC, 1, 25, 8, 0, "misc abilities"},
        };

        assertEquals(c.length, ObjectRegistry.flagSets.size(), "one row per flag type");
        for (Object[] row : c) {
            FlagSet found = ObjectRegistry.flagSets.get((ObjectFlagType) row[0]);
            String where = "row " + row[5];
            assertSame(row[0], found.getType(), where + " type");
            assertEquals(row[1], found.getFactor(), where + " factor");
            assertEquals(row[2], found.getBonus(), where + " bonus");
            assertEquals(row[3], found.getSize(), where + " size");
            assertEquals(row[4], found.getCount(), where + " count");
            assertEquals(row[5], found.getDescription(), where + " desc");
        }
    }

    /**
     * C's {@code element_sets[]} table ({@code obj-power.c}): {type, res_level, factor, bonus, size,
     * count, desc}. The immunities row's bonus is {@code INHIBIT_POWER}, 20000 in {@code obj-power.h}.
     * Unlike the other two tables the port holds this one as a list, so row order is the key and is
     * checked.
     */
    @Test
    @DisplayName("the element set table holds C's three rows in order")
    void elementSetTableMatchesC() {
        Object[][] c = {
                {ResType.T_LRES, 3, 6, 20000, 4, 0, "immunities"},
                {ResType.T_LRES, 1, 1, 10, 4, 0, "low resists"},
                {ResType.T_HRES, 1, 2, 10, 9, 0, "high resists"},
        };

        assertEquals(c.length, ObjectRegistry.elementSets.size(), "three rows");
        for (int i = 0; i < c.length; i++) {
            ElementSet row = ObjectRegistry.elementSets.get(i);
            String where = "row " + i + " (" + c[i][6] + ")";
            assertSame(c[i][0], row.getType(), where + " type");
            assertEquals(c[i][1], row.getResLevel(), where + " res_level");
            assertEquals(c[i][2], row.getFactor(), where + " factor");
            assertEquals(c[i][3], row.getBonus(), where + " bonus");
            assertEquals(c[i][4], row.getSize(), where + " size");
            assertEquals(c[i][5], row.getCount(), where + " count");
            assertEquals(c[i][6], row.getDescription(), where + " desc");
        }
    }
}
