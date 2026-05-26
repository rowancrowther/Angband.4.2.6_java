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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.cave;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.co.jackoftrades.backend.utils.RandomValueUtils;
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


public class GridData {
    private static final Logger logger = LogManager.getLogger();

    private Monster monster;
    private int monsterIndex;
    private Feature feature;
    private ObjectKind firstKind;
    private ArrayList<ObjectKind> objectKindList;
    private ArrayList<Trap> trapList = new ArrayList<>();

    private boolean multipleObjects;
    private boolean unseenObjects;
    private boolean unseenMoney;

    private GridLightLevel lighting;
    private boolean inView;
    private boolean isPlayer;
    private boolean hallucinate;

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