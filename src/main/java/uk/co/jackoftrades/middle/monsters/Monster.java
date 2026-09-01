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
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerKnowledge;
import uk.co.jackoftrades.middle.player.PlayerState;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerOptionEnum;

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

    /**
     * Report whether the player fails to recognise this monster as a monster. The port of C's
     * {@code monster_is_camouflaged}, a one-line read of the transient {@code MFLAG_CAMOUFLAGE}
     * flag.
     *
     * <p>Camouflage covers both halves of C's disguise mechanic: a monster mimicking an object
     * (C's {@code monster_is_mimicking} is this flag plus a non-null {@code mimicked_obj}) and one
     * mimicking a feature, which carries the flag alone. The flag is transient state on the
     * individual monster, not a property of its race, so it is cleared the moment the monster is
     * revealed.
     *
     * <p>Function monsterIsCamouflaged coded on 260828, commented in full on 260828.
     *
     * @return {@code true} if the monster is camouflaged and so not recognisable as a monster
     */
    public boolean monsterIsCamouflaged() {
        return monsterFlag.has(MonsterFlag.MFLAG_CAMOUFLAGE);
    }

    /**
     * Let this monster learn one "observed" property of the player — a resistance, an object flag,
     * or a player flag — or learn that the player lacks it. The port of C's
     * {@code update_smart_learn} ({@code mon-util.c:790}).
     *
     * <p>The method has two halves that serve different parties. The first half is unconditional
     * and works on the player: whatever a monster might learn from an event, the player is given
     * the same chance to learn it, so {@link PlayerKnowledge#equipLearnFlag} and
     * {@link PlayerKnowledge#equipLearnElement} run before any of the monster's own gates. The second half
     * writes the monster's picture of the player into {@link #knownPState}, and is fenced by four
     * successive returns — the {@code birth_ai_learn} option being off, the monster being stupid,
     * a non-smart monster failing a one-in-two roll, and a flat one-in-a-hundred failure that
     * applies however clever the monster is. C's ordering matters and is kept: the player's
     * learning survives all four returns, and the option is consulted before either predicate, so
     * a game without learning monsters never asks what the race flags say.
     *
     * <p>The two predicates are not opposites. {@link #monsterIsStupid()} reads the current race
     * alone while {@link #monsterIsSmart()} also remembers an original race, so a monster can
     * answer false to both and take the one-in-two path.
     *
     * <p>The leading sanity check is C's {@code if (!flag && !element_ok) return;} — a call that
     * names neither a flag nor a valid element has nothing to teach anyone. C's parameters are
     * plain integers, so its {@code 0} flag and its negative element become the enum sentinels
     * here: {@link ObjectFlag#OF_NONE}, {@link PlayerFlag#PF_NONE} and {@link ElementEnum#ELEM_NONE}.
     * Testing against those constants rather than against null is the point — the sentinels are
     * ordinary enum constants and a null test would let them through, which is what the live
     * caller in {@code Player.playerSetTimed} would hit, passing {@code PF_NONE} on every call.
     *
     * <p>{@code elementOK} is C's {@code (element >= 0) && (element < ELEM_MAX)}, and C's comment
     * records why the bounds are there: the element argument is routinely an arbitrary
     * {@code PROJ_} type handed straight through from a blow or a projection, so a value past the
     * end of the element list is expected rather than exceptional and is simply not learned from.
     * {@link ElementEnum} declares {@code ELEM_MAX} in the position C does, after
     * {@code ELEM_ARROW}, so the two admit the same set.
     *
     * <p>Each of the three learning steps writes an absence as readily as a presence: where the
     * player does not have the flag, the monster's belief is cleared rather than left alone, which
     * is the only place {@link PlayerState#oFlagOff} is used. The element step needs no such pair,
     * copying the player's resistance level across whatever it is, a vulnerability included.
     *
     * <p><b>Outstanding:</b> the {@link ObjectFlag#OF_MAX} end-marker is treated inconsistently —
     * the sanity check at the top counts it as "no flag", while the learning branch counts it as a
     * flag and would write it into {@link #knownPState}. C never passes an end-marker, and no
     * caller in the port does either, so nothing reaches it today.
     *
     * <p>Function updateSmartLearn coded on 260831, commented in full on 260831.
     *
     * @param player  the player whose properties are being observed, and who learns alongside the
     *                monster
     * @param objFlag the object flag observed, or {@link ObjectFlag#OF_NONE} if the event names none
     * @param pFlag   the player flag observed, or {@link PlayerFlag#PF_NONE} if the event names none
     * @param elem    the element observed, or {@link ElementEnum#ELEM_NONE} if the event names none
     */
    public void updateSmartLearn(Player player, ObjectFlag objFlag, PlayerFlag pFlag, ElementEnum elem) {
        boolean elementOK = (elem != ElementEnum.ELEM_NONE && elem != ElementEnum.ELEM_MAX);

        // Sanity check
        if (!elementOK && (objFlag == ObjectFlag.OF_NONE || objFlag == ObjectFlag.OF_MAX)) return;

        // Anything a monster might learn, the player should learn
        if (objFlag != ObjectFlag.OF_NONE)
            PlayerKnowledge.equipLearnFlag(player, objFlag);

        if (elementOK)
            PlayerKnowledge.equipLearnElement(player, elem);

        // Not allowed to learn
        if (!player.opt(PlayerOptionEnum.OP_birth_ai_learn)) return;

        // Too stupid to learn
        if (monsterIsStupid()) return;
        
        // Not intelligent, only learn sometimes
        if (!monsterIsSmart() && RandomValueUtils.oneIn(2)) return;

        // ANalyze the knowledge; fail very rarely
        if (RandomValueUtils.oneIn(100)) return;

        // Learn the flag
        if (objFlag != ObjectFlag.OF_NONE || objFlag == ObjectFlag.OF_MAX) {
            if (player.hasObjectFlag(objFlag)) {
                knownPState.oFlagOn(objFlag);
            } else {
                knownPState.oFlagOff(objFlag);
            }
        }

        // learn the pflag
        if (pFlag != PlayerFlag.PF_NONE) {
            if (player.getPlayerState().hasPFlag(pFlag)) {
                knownPState.playerFlagOn(pFlag);
            } else {
                knownPState.playerFlagOff(pFlag);
            }
        }

        // learn the element
        if (elementOK) {
            knownPState.setElInfo(elem, player.getPlayerState().getResLevel(elem));
        }
    }

    /**
     * Test whether this monster is, or once was, smart — the port of C's {@code monster_is_smart}
     * in {@code mon-predicate.c}.
     *
     * <p>The predicate reads {@code RF_SMART} off both races and takes either: an original race
     * carrying the flag answers {@code true} outright, and only when it does not (or when there is
     * no original race, the monster never having shapechanged) does the current race decide. C's
     * comment names this "is (or was) smart", and the asymmetry is deliberate — cunning learned
     * before a shapechange is not forgotten by wearing a dull shape, but a dull monster that takes
     * a clever shape does gain the wits that go with it. Contrast {@link #monsterIsStupid()}, which
     * consults the current race alone, and {@link #isUnique()}, which prefers the original race and
     * ignores the current one when there is an original to read.
     *
     * <p>The flags are properties of the races rather than transient state on the individual
     * monster, so the answer changes only when one of the monster's races does.
     *
     * <p>In C the predicate gates four behaviours: it halves the chance of learning in
     * {@code update_smart_learn} for a monster that is merely not stupid, it makes a smart monster
     * certain to notice the player in {@code cave-map.c}, it lets a badly wounded smart monster
     * break off and use a spell in {@code mon-attack.c}, and it drives the spell filtering in
     * {@code mon-spell.c}. Only the smart-learning boundary is ported so far, which is why this is
     * private.
     *
     * <p>Function monsterIsSmart coded on 260831, commented in full on 260831.
     *
     * @return {@code true} if either the original race or the current race carries {@code RF_SMART}
     */
    private boolean monsterIsSmart() {
        if (originalRace != null && originalRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_SMART)) return true;

        return monsterRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_SMART);
    }

    /**
     * Test whether this monster is stupid — the port of C's {@code monster_is_stupid} in
     * {@code mon-predicate.c}.
     *
     * <p>The predicate reads {@code RF_STUPID} off the monster's <em>current</em> race and consults
     * nothing else. That distinction matters after a shapechange: unlike {@link #isUnique()}, which
     * prefers the original race, C deliberately looks only at {@code mon->race} here, so a clever
     * monster wearing a stupid shape counts as stupid for as long as it holds that shape, and a
     * stupid monster in a clever shape does not.
     *
     * <p>The flag is a property of the race rather than transient state on the individual monster,
     * so the answer changes only when the monster's race does.
     *
     * <p>In C the predicate gates four behaviours: it ends {@code update_smart_learn} before any
     * knowledge is recorded, it exempts a monster from spell failure and from the spell filtering
     * in {@code mon-attack.c} (jellies and such never fail, and never discriminate), and it drops a
     * sleeping monster's chance of being woken by noise from 25 to 10 in {@code cave-map.c}. Only
     * the smart-learning boundary is ported so far, which is why this is private.
     *
     * <p>Function monsterIsStupid coded on 260831, commented in full on 260831.
     *
     * @return {@code true} if this monster's current race carries {@code RF_STUPID}
     */
    private boolean monsterIsStupid() {
        return monsterRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_STUPID);
    }
}
