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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.Map;

public class PlayerRace {
    private static final Logger logger = LogManager.getLogger();

    private String name;
    private int rIndex;

    private int raceMhp;
    private int raceExp;

    private int baseAge;
    private int modAge;

    private int baseHeight;
    private int modHeight;
    private int baseWeight;
    private int modWeight;

    private int infravision;

    private PlayerBody body;

    private Map<Stats, Integer> statsAdj;
    private Map<PlayerSkill, Integer> skillsAdj;

    private Flag<ObjectFlag> oFlags;
    private Flag<PlayerFlag> pFlags;

    private PlayerHistoryChart history;

    private Map<ObjectModifier, Integer> resists;

    public PlayerRace(String name, int rIndex, int raceMhp, int raceExp, int baseAge, int modAge, int baseHeight,
                      int modHeight, int baseWeight, int modWeight, int infravision, PlayerBody body,
                      Map<Stats, Integer> statsAdj, Map<PlayerSkill, Integer> skillsAdj, Flag<ObjectFlag> oFlags,
                      Flag<PlayerFlag> pFlags, PlayerHistoryChart history, Map<ObjectModifier, Integer> resists) {
        this.name = name;
        this.rIndex = rIndex;
        this.raceMhp = raceMhp;
        this.raceExp = raceExp;
        this.baseAge = baseAge;
        this.modAge = modAge;
        this.baseHeight = baseHeight;
        this.modHeight = modHeight;
        this.baseWeight = baseWeight;
        this.modWeight = modWeight;
        this.infravision = infravision;
        this.body = body;
        this.statsAdj = statsAdj;
        this.skillsAdj = skillsAdj;
        this.oFlags = oFlags;
        this.pFlags = pFlags;
        this.history = history;
        this.resists = resists;
    }
}