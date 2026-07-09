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

/**
 * A live monster on the current level — an instance of a {@link MonsterRace} with
 * its own position, hit points, timed effects, speed/energy, status flags, held
 * and mimicked objects, target, group membership and flow heatmap. This is the
 * Java port of the C original's {@code struct monster} ({@code src/monster.h});
 * contrast with {@link MonsterRace}, which is the shared template.
 *
 * @author Rowan Crowther
 */
public class Monster {
    /**
     * The race this monster currently is.
     *
     * @author Rowan Crowther
     */
    private MonsterRace monsterRace;
    /**
     * The race this monster originally was (before any shapechange).
     *
     * @author Rowan Crowther
     */
    private MonsterRace originalRace;
    /**
     * The monster's current grid location.
     *
     * @author Rowan Crowther
     */
    private Loc grid;

    /**
     * Current hit points.
     *
     * @author Rowan Crowther
     */
    private int hp;
    /**
     * Maximum hit points.
     *
     * @author Rowan Crowther
     */
    private int maxHp;

    /**
     * Remaining duration of each active timed effect.
     *
     * @author Rowan Crowther
     */
    private Map<MonTimed, Integer> mTimed;

    /**
     * The monster's current speed.
     *
     * @author Rowan Crowther
     */
    private int mSpeed;
    /**
     * Accumulated energy (the monster acts when it has enough).
     *
     * @author Rowan Crowther
     */
    private int energy;

    /**
     * Current distance from the player.
     *
     * @author Rowan Crowther
     */
    private int cDistance;

    /**
     * The monster's transient status flags.
     *
     * @author Rowan Crowther
     */
    private Flag<MonsterFlag> monsterFlag;

    /**
     * The object this monster is mimicking, if any.
     *
     * @author Rowan Crowther
     */
    private ItemObject mimickedObject;
    /**
     * Objects this monster is carrying (dropped on death).
     *
     * @author Rowan Crowther
     */
    private List<ItemObject> heldObject;

    /**
     * The colour this monster is currently drawn in.
     *
     * @author Rowan Crowther
     */
    private ColourType colourAttr;

    /**
     * A snapshot of the player state as known/used by this monster.
     *
     * @author Rowan Crowther
     */
    private PlayerState knownPState;

    /**
     * The monster's current target.
     *
     * @author Rowan Crowther
     */
    private Target target;

    /**
     * This monster's membership in one or more groups.
     *
     * @author Rowan Crowther
     */
    private List<MonsterGroupInfo> groupInfo;
    /**
     * The monster's personal flow/heatmap used for pathfinding.
     *
     * @author Rowan Crowther
     */
    private Heatmap heatmap;

    /**
     * The minimum range at which the monster prefers to engage.
     *
     * @author Rowan Crowther
     */
    private int minRange;
    /**
     * The range at which the monster fights most effectively.
     *
     * @author Rowan Crowther
     */
    private int bestRange;

    /**
     * Build a live monster from its full set of state fields.
     *
     * @param monsterRace    current race
     * @param originalRace   original race (pre-shapechange)
     * @param grid           current location
     * @param hp             current hit points
     * @param maxHp          maximum hit points
     * @param mTimed         active timed effects
     * @param mSpeed         current speed
     * @param energy         accumulated energy
     * @param cDistance      distance from the player
     * @param monsterFlag    transient status flags
     * @param mimickedObject mimicked object, if any
     * @param heldObject     carried objects
     * @param colourAttr     current draw colour
     * @param knownPState    known player-state snapshot
     * @param target         current target
     * @param groupInfo      group membership
     * @param heatmap        personal flow map
     * @param minRange       preferred minimum engagement range
     * @param bestRange      most-effective fighting range
     * @author Rowan Crowther
     */
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

    /**
     * @return this monster's current race
     * @author Rowan Crowther
     */
    public MonsterRace getMonsterRace() {
        return monsterRace;
    }

    /**
     * Test whether one of this monster's transient status flags is set.
     *
     * @param flag the flag to test
     * @return true if the flag is set
     * @author Rowan Crowther
     */
    public boolean hasMonsterFlag(MonsterFlag flag) {
        return monsterFlag.has(flag);
    }
}
