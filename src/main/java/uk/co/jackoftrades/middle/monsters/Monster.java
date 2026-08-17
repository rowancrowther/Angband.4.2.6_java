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

import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Heatmap;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.combat.Target;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;
import uk.co.jackoftrades.middle.monsters.enums.MonTimedFlags;
import uk.co.jackoftrades.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
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
     */
    private MonsterRace monsterRace;
    /**
     * The race this monster originally was (before any shapechange).
     */
    private MonsterRace originalRace;
    /**
     * The monster's current grid location.
     */
    private Loc grid;

    /**
     * Current hit points.
     */
    private int hp;
    /**
     * Maximum hit points.
     */
    private int maxHp;

    /**
     * Remaining duration of each active timed effect.
     */
    private Map<MonTimed, Integer> mTimed;

    /**
     * The monster's current speed.
     */
    private int mSpeed;
    /**
     * Accumulated energy (the monster acts when it has enough).
     */
    private int energy;

    /**
     * Current distance from the player.
     */
    private int cDistance;

    /**
     * The monster's transient status flags.
     */
    private Flag<MonsterFlag> monsterFlag;

    /**
     * The object this monster is mimicking, if any.
     */
    private ItemObject mimickedObject;
    /**
     * Objects this monster is carrying (dropped on death).
     */
    private List<ItemObject> heldObject;

    /**
     * The colour this monster is currently drawn in.
     */
    private ColourEnum colourAttr;

    /**
     * A snapshot of the player state as known/used by this monster.
     */
    private PlayerState knownPState;

    /**
     * The monster's current target.
     */
    private Target target;

    /**
     * This monster's membership in one or more groups.
     */
    private List<MonsterGroupInfo> groupInfo;
    /**
     * The monster's personal flow/heatmap used for pathfinding.
     */
    private Heatmap heatmap;

    /**
     * The minimum range at which the monster prefers to engage.
     */
    private int minRange;
    /**
     * The range at which the monster fights most effectively.
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
     */
    public Monster(MonsterRace monsterRace, MonsterRace originalRace, Loc grid, int hp, int maxHp,
                   Map<MonTimed, Integer> mTimed, int mSpeed, int energy, int cDistance, Flag<MonsterFlag> monsterFlag,
                   ItemObject mimickedObject, List<ItemObject> heldObject, ColourEnum colourAttr,
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
     */
    public MonsterRace getMonsterRace() {
        return monsterRace;
    }

    /**
     * Test whether one of this monster's transient status flags is set.
     *
     * @param flag the flag to test
     * @return true if the flag is set
     */
    public boolean hasMonsterFlag(MonsterFlag flag) {
        return monsterFlag.has(flag);
    }

    /**
     * Clear one of this monster's transient status flags — the port of C's {@code mflag_off}. Leaves
     * the flag clear whether or not it was previously set.
     *
     * @param flag the flag to clear
     */
    public void monsterFlagOff(MonsterFlag flag) {
        monsterFlag.off(flag);
    }

    /**
     * @return this monster's current grid location
     */
    public Loc getGrid() {
        return grid;
    }

    /**
     * @return this monster's distance from the player, in grids (C: {@code cdis})
     */
    public int getcDistance() {
        return cDistance;
    }

    /**
     * @return {@code true} if this monster is a unique — tested against its original race if it has
     * shapechanged, otherwise its current race
     */
    public boolean isUnique() {
        return (originalRace != null) ? originalRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE)
                : monsterRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE);
    }

    /**
     * @param timed the monster timed effect to query
     * @return the turns remaining on that effect, or {@code 0} if the monster is not under it
     */
    public int getMonTimed(MonTimed timed) {
        return mTimed.get(timed);
    }

    /**
     * Clear a monster timed effect outright by setting its duration to zero,
     * delegating to {@link #setTimed}. The port of C's {@code mon_clear_timed}.
     * A no-op (returns {@code false}) if the effect is not currently active.
     *
     * @param timed the monster timed effect to clear
     * @param flag  behavioural flags controlling messaging/notification
     * @return {@code true} if the effect was active and has now been cleared
     */
    public boolean clearTimed(MonTimed timed, Flag<MonTimedFlags> flag) {
        if (mTimed.get(timed) == 0) {
            return false;
        }
        return setTimed(timed, 0, flag);
    }

    /**
     * Set a monster timed effect to an absolute duration, applying any messaging
     * dictated by {@code flag}. The port of C's {@code mon_set_timed}; the common
     * sink that {@link #clearTimed} and {@link #decrementTimed} both funnel through.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the monster timed-effect runtime;
     * reports {@code false} (no change).</p>
     *
     * @param timed the monster timed effect to set
     * @param timer the new duration in turns
     * @param flag  behavioural flags controlling messaging/notification
     * @return {@code true} if the effect's value actually changed
     */
    public boolean setTimed(MonTimed timed, int timer, Flag<MonTimedFlags> flag) {
        // Stub class: TODO: implement
        return false;
    }

    /**
     * Reduce a monster timed effect's duration by a given amount, flooring at zero,
     * and delegate to {@link #setTimed}. The port of C's {@code mon_dec_timed}. Used
     * to keep a commanded monster's timer aligned with the player's fading command.
     *
     * @param timed the monster timed effect to shorten
     * @param timer the number of turns to remove
     * @param flag  behavioural flags controlling messaging/notification
     * @return {@code true} if the effect's value actually changed
     */
    public boolean decrementTimed(MonTimed timed, int timer, Flag<MonTimedFlags> flag) {
        int newLevel = mTimed.get(timed) - timer;
        newLevel = Math.max(0, newLevel);

        return setTimed(timed, newLevel, flag);
    }
}
