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
import uk.co.jackoftrades.middle.cave.enums.GridLightLevel;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.Pile;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;


public class GridData {
    private static final Logger logger = LogManager.getLogger();

    private Monster monster;
    private Feature feature;
    private ArrayList<ObjectKind> objectKindList;
    private ArrayList<Trap> trapList;

    private boolean multipleObjects;
    private boolean unseenObjects;
    private boolean unseenMoney;

    private GridLightLevel lighting;
    private boolean inView;
    private boolean isPlayer;
    private boolean hallucinate;

    private void mapInfo(Loc grid) {
        if (!GameConstants.cave.inBounds(grid)) {
            String message = "Grid location out of bounds! " + grid;
            IndexOutOfBoundsException ex = new IndexOutOfBoundsException(message);
            logger.error(message, ex);
        }

        Chunk cave = GameConstants.cave;
        Player player = GameConstants.mainPlayer;

        Pile objects;

        objectKindList = null;
        trapList = null;
        multipleObjects = false;
        lighting = GridLightLevel.LIGHTING_LIT;
        unseenObjects = false;
        unseenMoney = false;

        feature = cave.getSquare(grid).getFeature();
        if (feature.isMimicing())
            feature = GameConstants.lookupFeature(feature.getMimic());

        inView = cave.squareIsSeen(grid);
        isPlayer = cave.squareIsPlayer(grid);
        monster = cave.getMonster(grid);
        hallucinate = player.getTimedEffect(TimedEffect.TMD_IMAGE) > 0;

        if (inView) {
            boolean lit = cave.squareIsLit(grid);


        }
    }
}
