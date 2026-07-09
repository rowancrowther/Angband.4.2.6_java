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

package uk.co.jackoftrades.middle.cave;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.jackoftrades.backend.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.cave.enums.GridLightLevel;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerOption;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * The fully-resolved "what should be drawn here" view of a single dungeon grid:
 * the visible monster (or none), terrain feature, object(s), traps and lighting,
 * taking into account the player's knowledge, line of sight and hallucination.
 * This is the Java port of the C original's {@code grid_data} struct and its
 * {@code map_info()} routine ({@code src/cave-map.c}), which the display layer
 * consults to decide each cell's glyph and colour.
 *
 * @author Rowan Crowther
 */
public class GridData {
    /**
     * Logger used to report out-of-bounds/invalid grid queries.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The visible monster on this grid, or {@code null} if none is shown.
     *
     * @author Rowan Crowther
     */
    private Monster monster;
    /**
     * Index of a (possibly hallucinated) monster to display.
     *
     * @author Rowan Crowther
     */
    private int monsterIndex;
    /**
     * The terrain feature to display (after any mimic resolution).
     *
     * @author Rowan Crowther
     */
    private Feature feature;
    /**
     * The first/topmost object kind on this grid, or {@code null} if none.
     *
     * @author Rowan Crowther
     */
    private ObjectKind firstKind;
    /**
     * The object kinds present on this grid.
     *
     * @author Rowan Crowther
     */
    private ArrayList<ObjectKind> objectKindList;
    /**
     * The traps shown on this grid.
     *
     * @author Rowan Crowther
     */
    private ArrayList<Trap> trapList = new ArrayList<>();

    /**
     * True when more than one object is present (so a "pile" should be shown).
     *
     * @author Rowan Crowther
     */
    private boolean multipleObjects;
    /**
     * True when there are objects the player knows about but cannot currently see.
     *
     * @author Rowan Crowther
     */
    private boolean unseenObjects;
    /**
     * True when there is money the player knows about but cannot currently see.
     *
     * @author Rowan Crowther
     */
    private boolean unseenMoney;

    /**
     * The lighting state used to colour this grid.
     *
     * @author Rowan Crowther
     */
    private GridLightLevel lighting;
    /**
     * Whether this grid is currently in the player's view.
     *
     * @author Rowan Crowther
     */
    private boolean inView;
    /**
     * Whether the player occupies this grid.
     *
     * @author Rowan Crowther
     */
    private boolean isPlayer;
    /**
     * Whether the player is hallucinating (which can replace the real contents).
     *
     * @author Rowan Crowther
     */
    private boolean hallucinate;

    /**
     * Populate this grid-data from the live caves for the given location: resolve
     * the feature (handling mimics), determine visibility and lighting, memorise
     * the grid, then gather the displayed trap, object(s) and monster — applying
     * the player's ignore settings and, if hallucinating, occasionally
     * substituting random contents. Mirrors the C original's {@code map_info()}.
     *
     * @param grid the dungeon grid to describe
     * @throws Exception if the grid is out of bounds, or a monster index exceeds
     *                   the cave's maximum
     * @author Rowan Crowther
     */
    private void mapInfo(Loc grid) throws Exception {
        if (!GameConstants.cave.inBounds(grid)) {
            String message = "Grid location out of bounds! " + grid;
            IndexOutOfBoundsException ex = new IndexOutOfBoundsException(message);
            logger.error(message, ex);
            throw ex;
        }

        Chunk cave = GameConstants.cave;
        Player player = GameConstants.mainPlayer;
        Chunk playerCave = player.getCave();

        objectKindList = null;
        firstKind = null;
        trapList = null;
        multipleObjects = false;
        lighting = GridLightLevel.LIGHTING_LIT;
        unseenObjects = false;
        unseenMoney = false;

        feature = cave.getSquare(grid).getFeature();
        if (feature.isMimicing())
            feature = feature.getMimic();

        inView = cave.squareIsSeen(grid);
        isPlayer = cave.squareIsPlayer(grid);
        monster = isPlayer ? null : cave.getMonster(grid);
        hallucinate = player.getTimedEffect(TimedEffect.TMD_IMAGE) > 0;

        // Lighting
        if (inView) {
            boolean lit = cave.squareIsLit(grid);

            if (cave.squareHasInfoFlag(grid, SquareEnum.SQUARE_CLOSE_PLAYER)) {
                if (player.hasPlayerFlag(PlayerFlag.PF_UNLIGHT) && player.getStateLight() <= 1) {
                    lighting = lit
                            ? GridLightLevel.LIGHTING_LOS
                            : GridLightLevel.LIGHTING_DARK;
                } else if (lit) {
                    lighting = player.opt(PlayerOption.OP_view_yellow_light)
                            ? GridLightLevel.LIGHTING_TORCH
                            : GridLightLevel.LIGHTING_LOS;
                }
            } else if (lit) {
                lighting = GridLightLevel.LIGHTING_LOS;
            }

            cave.squareMemorize(grid);
        } else if (!cave.isKnown(grid)) {
            feature = GameConstants.lookupFeature(TerrainFlags.FEAT_NONE);
        } else if (cave.squareIsGlow(grid)) {
            lighting = GridLightLevel.LIGHTING_LIT;
        }

        // Mimic
        feature = playerCave.getSquare(grid).getFeature();
        if (feature.isMimicing()) {
            feature = feature.getMimic();
        }

        // Traps
        if (playerCave.squareIsTrap(grid) && cave.isKnown(grid)) {
            ArrayList<Trap> traps = playerCave.getSquare(grid).getTraps();
            for (Trap trap : traps) {
                if (trap.hasTrap(TrapEnum.TRF_TRAP) || trap.hasTrap(TrapEnum.TRF_GLYPH) || trap.hasTrap(TrapEnum.TRF_WEB)) {
                    if (trap.getTimeout() == 0) {
                        trapList.add(trap);
                        break;
                    }
                }
            }
        }

        // Objects
        Iterator<ItemObject> pileIterator = playerCave.getPileIterator(grid);
        while (pileIterator.hasNext()) {
            ItemObject item = pileIterator.next();
            if (item.getKind().equals(GameConstants.unknownGoldKind)) {
                unseenMoney = true;
            } else if (item.getKind().equals(GameConstants.unknownItemKind)) {
                unseenObjects = true;
            } else if (player.ignoreKnownItemOk(item)) {
                // Item stays hidden
            } else if (firstKind == null) {
                firstKind = item.getKind();
            } else {
                multipleObjects = true;
                break;
            }
        }

        // Monsters
        if (monster != null) {
            if (!monster.hasMonsterFlag(MonsterFlag.MFLAG_VISIBLE))
                monster = null;
        }

        // Hallucination
        if (hallucinate && monster == null && firstKind == null) {
            if (RandomValueUtils.oneIn(128) && !feature.isPermanent())
                monsterIndex = 1;
            else if (RandomValueUtils.oneIn(128) && !feature.isPermanent())
                firstKind = GameConstants.objectKinds.getFirst();
            else
                hallucinate = false;
        }

        if (!hallucinate && monsterIndex >= cave.getMonMax()) {
            String message = "Monster index higher than the maximum number of monsters in this cave.";
            Exception ex = new Exception(message);
            logger.fatal(message, ex);
            throw ex;
        }
    }
}