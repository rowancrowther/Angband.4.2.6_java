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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.cave.Heatmap;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.combat.Target;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;
import uk.co.jackoftrades.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.PlayerState;

import java.util.List;
import java.util.Map;

public class Monster {
    private MonsterRace monsterRace;
    private MonsterRace originalRace;
    private Loc grid;

    private int hp;
    private int maxHp;

    private Map<MonTimed, Integer> mTimed;

    private int mSpeed;
    private int energy;

    private int cDistance;

    private Flag<MonsterFlag> monsterFlag;

    private ItemObject mimickedObject;
    private List<ItemObject> heldObject;

    private ColourType colourAttr;

    private PlayerState knownPState;

    private Target target;

    private List<MonsterGroupInfo> groupInfo;
    private Heatmap heatmap;

    private int minRange;
    private int bestRange;

    public Monster(MonsterRace monsterRace, MonsterRace originalRace, Loc grid, int hp, int maxHp,
                   Map<MonTimed, Integer> mTimed, int mSpeed, int energy, int cDistance, Flag<MonsterFlag> monsterFlag,
                   ItemObject mimickedObject, List<ItemObject> heldObject, ColourType colourAttr,
                   PlayerState knownPState, Target target, List<MonsterGroupInfo> groupInfo, Heatmap heatmap,
                   int minRange, int bestRange) {
        this.monsterRace = monsterRace;
        this.originalRace = originalRace;
        this.grid = grid;
        this.hp = hp;
        this.maxHp = maxHp;
        this.mTimed = mTimed;
        this.mSpeed = mSpeed;
        this.energy = energy;
        this.cDistance = cDistance;
        this.monsterFlag = monsterFlag;
        this.mimickedObject = mimickedObject;
        this.heldObject = heldObject;
        this.colourAttr = colourAttr;
        this.knownPState = knownPState;
        this.target = target;
        this.groupInfo = groupInfo;
        this.heatmap = heatmap;
        this.minRange = minRange;
        this.bestRange = bestRange;
    }

    public MonsterRace getMonsterRace() {
        return monsterRace;
    }

    public boolean hasMonsterFlag(MonsterFlag flag) {
        return monsterFlag.has(flag);
    }
}
