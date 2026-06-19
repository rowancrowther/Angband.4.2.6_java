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

package uk.co.jackoftrades.frontend.colour;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterRace;

import java.util.HashMap;
import java.util.Map;

public class VisualsColourCyclesByRace {
    private static final Map<MonsterRace, VisualsColourCycle> race = new HashMap<>();

    @Contract(pure = true)
    @CheckReturnValue
    @NotNull
    public static ColourType getAttrForFrame(@NotNull MonsterRace monsterRace, int frame) {
        VisualsColourCycle cycle = race.get(monsterRace);
        if (cycle == null)
            return ColourType.COLOUR_TYPE_DARK;

        return cycle.attrForFrame(frame);
    }

    /**
     * Add a new MonsterRace/VisualsColourCycle entry to the map based on the group
     * name and the cycle name
     *
     * @param monsterRace The Monster Race which we are adding
     * @param groupName   The name of the colour cycle group
     * @param cycleName   The name of the colour cycle within that group
     */
    public static void setCycleForRace(MonsterRace monsterRace, String groupName, String cycleName) {
        VisualsCycler cyclerTable = GameConstants.getVisualsCyclerTable();

        VisualsColourCycle cycle = cyclerTable.cycleByName(groupName, cycleName);
        race.put(monsterRace, cycle);
    }
}