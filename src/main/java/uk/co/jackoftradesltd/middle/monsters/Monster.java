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

package uk.co.jackoftradesltd.middle.monsters;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Heatmap;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.combat.Target;
import uk.co.jackoftradesltd.middle.monsters.enums.MonTimed;
import uk.co.jackoftradesltd.middle.monsters.enums.MonTimedFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerKnowledge;
import uk.co.jackoftradesltd.middle.player.PlayerState;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;

import java.util.List;
import java.util.Map;

/**
 * A live monster on the current level — an instance of a {@link MonsterRace} with
 * its own position, hit points, timed effects, speed/energy, status flags, held
 * and mimicked objects, target, group membership and flow heatmap. This is the
 * Java port of the C original's {@code struct monster} ({@code src/monster.h});
 * contrast with {@link MonsterRace}, which is the shared template.
 *
 * <p>The class also carries the C predicates and flag macros that take a {@code struct monster}
 * ({@code mon-predicate.c}, {@code mflag_on} and {@code mflag_off} in {@code monster.h}), the
 * timed-effect entry points of {@code mon-timed.c}, and {@code update_smart_learn} from
 * {@code mon-util.c}. Several fields are mutable, and a few can be {@code null} on a shell
 * monster built for a test; each accessor below says which link it dereferences.
 *
 * <p>Class Monster coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class Monster {
    /**
     * This monster's slot in the level's monster array (C: {@code mon->midx}). Set by
     * {@link #setMonIndex(int)} when the chunk places or moves the monster; {@code 0} until then,
     * which is also the value C reserves for "no monster".
     */
    private int monIndex;

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
     * Build a live monster from its full set of state fields. Has no C counterpart: C fills a
     * {@code struct monster} field by field in {@code place_new_monster_one} and friends.
     *
     * <p>The constructor is not side-effect free. {@link #setHp(int)} and {@link #setMaxHp(int)}
     * write the legacy {@code PlayerEventStatusUpdate} cache, and if {@code monsterFlag} is
     * non-null with {@code MFLAG_VISIBLE} set, {@link #updateCached(Boolean)} refreshes the rest of
     * it. Nothing else is validated: every argument may be {@code null}, and the accessors that
     * dereference a field do not guard it.
     *
     * <p>Constructor Monster coded before 261009, commented in full on 261009.
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
        setHp(hp);
        setMaxHp(maxHp);
        this.mTimed = mTimed;
        this.mSpeed = mSpeed;
        this.energy = energy;
        this.cDistance = cDistance;
        this.monsterFlag = monsterFlag;
        if (this.monsterFlag != null && this.monsterFlag.has(MonsterFlag.MFLAG_VISIBLE))
            updateCached(null);
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
     * Read the race this monster currently is (C: {@code mon->race}). After a shapechange this is
     * the shape being worn; the form it started as is held in {@link #originalRace}.
     *
     * <p>Method getMonsterRace coded before 261009, commented in full on 261009.
     *
     * @return this monster's current race
     */
    public MonsterRace getMonsterRace() {
        return monsterRace;
    }

    /**
     * Test whether one of this monster's transient status flags is set - the port of C's
     * {@code mflag_has} macro in {@code monster.h}. Dereferences {@link #monsterFlag}, which is
     * never null in C (the flags are an inline array) but can be here if the constructor was given
     * {@code null}.
     *
     * <p>Method hasMonsterFlag coded before 261009, commented in full on 261009.
     *
     * @param flag the flag to test
     * @return true if the flag is set
     */
    public boolean hasMonsterFlag(MonsterFlag flag) {
        return monsterFlag.has(flag);
    }

    /**
     * Clear one of this monster's transient status flags — the port of C's {@code mflag_off} macro
     * in {@code monster.h}. Leaves the flag clear whether or not it was previously set. Clearing
     * {@code MFLAG_VISIBLE} also refreshes the legacy cache through {@link #updateCached(Boolean)},
     * which C has no equivalent of; see {@link #monsterFlagOn} for the mirror.
     *
     * <p>Method monsterFlagOff coded before 261009, commented in full on 261009.
     *
     * @param flag the flag to clear
     */
    public void monsterFlagOff(MonsterFlag flag) {
        monsterFlag.off(flag);

        if (flag == MonsterFlag.MFLAG_VISIBLE)
            updateCached(null);
    }

    /**
     * Read the monster's current grid (C: {@code mon->grid}). The {@link Loc} itself is returned,
     * not a copy.
     *
     * <p>Method getGrid coded before 261009, commented in full on 261009.
     *
     * @return this monster's current grid location
     */
    public Loc getGrid() {
        return grid;
    }

    /**
     * Read the monster's cached distance from the player (C: {@code mon->cdis}, a {@code uint8_t}).
     * It is a stored value refreshed by the monster-update code, so it is only as current as the
     * last update.
     *
     * <p>Method getcDistance coded before 261009, commented in full on 261009.
     *
     * @return this monster's distance from the player, in grids
     */
    public int getcDistance() {
        return cDistance;
    }

    /**
     * Test whether this monster is a unique - the port of C's {@code monster_is_unique} in
     * {@code mon-predicate.c}, whose comment reads "unshifted form is unique".
     *
     * <p>The test reads {@code RF_UNIQUE} off the original race when there is one and off the
     * current race otherwise; it never combines the two. A unique that has shapechanged into a
     * common race is therefore still unique, and a common monster that takes a unique's shape is
     * not. C keeps the other reading separately as {@code monster_is_shape_unique}, which looks at
     * {@code mon->race} alone and is not ported. Contrast {@link #monsterIsSmart()}, which takes
     * either race, and {@link #monsterIsStupid()}, which takes the current race only.
     *
     * <p>Method isUnique coded before 261009, commented in full on 261009.
     *
     * @return {@code true} if this monster is a unique — tested against its original race if it has
     * shapechanged, otherwise its current race
     */
    public boolean isUnique() {
        return (originalRace != null) ? originalRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE)
                : monsterRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE);
    }

    /**
     * Read the turns remaining on a monster timed effect (C: {@code mon->m_timed[effect_type]}).
     * The map is sparse, so an effect that was never set reads as {@code 0}, as C's zeroed array
     * does. Dereferences {@link #mTimed}, the container, which is {@code null} only if the
     * constructor was given none; a missing key is not an error.
     *
     * <p>Method getMonTimed coded before 261009, commented in full on 261009.
     *
     * @param timed the monster timed effect to query
     * @return the turns remaining on that effect, or {@code 0} if the monster is not under it
     */
    public int getMonTimed(MonTimed timed) {
        return mTimed.getOrDefault(timed, 0);
    }

    /**
     * Clear a monster timed effect outright by setting its duration to zero,
     * delegating to {@link #setTimed}. The port of C's {@code mon_clear_timed}.
     * A no-op (returns {@code false}) if the effect is not currently active, which is C's early
     * return on {@code mon->m_timed[effect_type] == 0}. C's {@code assert}s on the effect index
     * have no Java counterpart, since an enum cannot be out of range.
     *
     * <p>Method clearTimed coded on 260830, commented in full on 261009.
     *
     * @param timed the monster timed effect to clear
     * @param flag  behavioural flags controlling messaging/notification
     * @return {@code true} if the effect was active and has now been cleared
     */
    public boolean clearTimed(MonTimed timed, Flag<MonTimedFlags> flag) {
        if (mTimed.getOrDefault(timed, 0) == 0) {
            return false;
        }
        return setTimed(timed, 0, flag);
    }

    /**
     * Set a monster timed effect to an absolute duration, applying any messaging
     * dictated by {@code flag}. The port of C's {@code mon_set_timed}; the common
     * sink that {@link #clearTimed} and {@link #decrementTimed} both funnel through.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the monster timed-effect runtime. It does not
     * store {@code timer} in {@link #mTimed}, so a later {@link #getMonTimed} still reads the old
     * value, and it always reports {@code false} (no change). Its only effect is to push
     * {@code timer != 0} into the legacy {@code PlayerEventStatusUpdate} cache for the seven
     * health-bar effects (fear, disenchant, command, confusion, stun, sleep, hold). C's resist
     * check, shapechange handling, message and redraw requests are all still to come.
     *
     * <p>Method setTimed stub coded before 261009, commented in full on 261009.
     *
     * @param timed the monster timed effect to set
     * @param timer the new duration in turns
     * @param flag  behavioural flags controlling messaging/notification
     * @return {@code true} if the effect's value actually changed
     */
    public boolean setTimed(MonTimed timed, int timer, Flag<MonTimedFlags> flag) {
        // Stub class: TODO: implement

        // Update cached values
        if (timed == MonTimed.MON_TMD_FEAR)
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdFear(timer != 0);
        if (timed == MonTimed.MON_TMD_DISEN)
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdDisen(timer != 0);
        if (timed == MonTimed.MON_TMD_COMMAND)
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdCommand(timer != 0);
        if (timed == MonTimed.MON_TMD_CONF)
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdConf(timer != 0);
        if (timed == MonTimed.MON_TMD_STUN)
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdStun(timer != 0);
        if (timed == MonTimed.MON_TMD_SLEEP)
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdSleep(timer != 0);
        if (timed == MonTimed.MON_TMD_HOLD)
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdHold(timer != 0);
        
        return false;
    }

    /**
     * Reduce a monster timed effect's duration by a given amount, flooring at zero,
     * and delegate to {@link #setTimed}. The port of C's {@code mon_dec_timed}. Used
     * to keep a commanded monster's timer aligned with the player's fading command.
     *
     * <p>C asserts {@code timer > 0} ("for negative amounts, we use mon_inc_timed instead"). Java
     * has no such check, so a negative {@code timer} would raise the level rather than fail. Both
     * versions floor the result at zero, and C's comment that decreasing "should never fail"
     * holds because decreases skip the resist check in {@code mon_set_timed}.
     *
     * <p>Method decrementTimed coded on 260830, commented in full on 261009.
     *
     * @param timed the monster timed effect to shorten
     * @param timer the number of turns to remove
     * @param flag  behavioural flags controlling messaging/notification
     * @return {@code true} if the effect's value actually changed
     */
    public boolean decrementTimed(MonTimed timed, int timer, Flag<MonTimedFlags> flag) {
        int newLevel = mTimed.getOrDefault(timed, 0) - timer;
        newLevel = Math.max(0, newLevel);

        return setTimed(timed, newLevel, flag);
    }

    /**
     * Report whether the player fails to recognize this monster as a monster. The port of C's
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
     * @return {@code true} if the monster is camouflaged and so not recognizable as a monster
     */
    public boolean monsterIsCamouflaged() {
        return monsterFlag.has(MonsterFlag.MFLAG_CAMOUFLAGE);
    }

    /**
     * Let this monster learn one "observed" property of the player — a resistance, an object flag,
     * or a player flag — or learn that the player lacks it. The port of C's
     * {@code update_smart_learn} in {@code mon-util.c}.
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
     * caller in {@code PlayerTimed} (the object-flag failure check) would hit, passing
     * {@code PF_NONE} and {@code ELEM_NONE} on every call.
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
     * caller in the port does either, so nothing reaches it today. The learning condition itself,
     * {@code objFlag != OF_NONE || objFlag == OF_MAX}, reduces to {@code objFlag != OF_NONE}.
     *
     * <p>The only caller in the port is {@code PlayerTimed}, which passes a real object flag with
     * {@code PF_NONE} and {@code ELEM_NONE}. So the pflag and element halves are exercised only
     * by tests until another caller arrives. {@link #knownPState} is dereferenced after the gates
     * pass and is {@code null} on a shell monster built without one.
     *
     * <p>Function updateSmartLearn coded on 260831, commented in full on 261009.
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

    /**
     * Read the monster's current hit points.
     *
     * <p>Method getHp coded before 260929, commented in full on 260929.
     *
     * @return the current hit points, C's {@code mon->hp}; negative means dead
     */
    public int getHp() {
        return hp;
    }

    /**
     * Write the monster's current hit points and keep the legacy cache in step. The cache write,
     * {@code PlayerEventStatusUpdate.updatePlayerStatusMonsterHealth}, is not part of C's
     * {@code mon->hp = ...}; it is a leftover of the shared-cache design that
     * {@code docs/implementation/260926_change_in_architecture_from_cache_to_messages.md} is
     * replacing. The health bar no longer reads it - {@code PlayerCalcs.redrawStuff}'s
     * {@code PR_HEALTH} arm reads {@link #getHp()} directly - so the call goes when the cache does.
     * The constructor calls this method, so building a monster also writes the cache.
     *
     * <p>Method setHp coded before 260929, commented in full on 260929.
     *
     * @param hp the new current hit points
     */
    public void setHp(int hp) {
        this.hp = hp;

        // Update cached copy
        PlayerEventStatusUpdate.updatePlayerStatusMonsterHealth(this.hp);
    }

    /**
     * Read the monster's maximum hit points.
     *
     * <p>Method getMaxHp coded before 260929, commented in full on 260929.
     *
     * @return the maximum hit points, C's {@code mon->maxhp}
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Write the monster's maximum hit points and keep the legacy cache in step - the same
     * cache-write caveat as {@link #setHp(int)}, via
     * {@code PlayerEventStatusUpdate.updatePlayerStatusMaxMonsterHealth}. The constructor calls
     * this method too.
     *
     * <p>Method setMaxHp coded before 260929, commented in full on 260929.
     *
     * @param maxHp the new maximum hit points
     */
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;

        // Update cached copy
        PlayerEventStatusUpdate.updatePlayerStatusMaxMonsterHealth(this.maxHp);
    }

    /**
     * Set one of this monster's transient status flags - the port of C's {@code mflag_on}, the
     * counterpart of {@link #monsterFlagOff}. Setting a flag that is already set changes nothing.
     * Setting {@code MFLAG_VISIBLE} also refreshes the legacy cache, as {@link #monsterFlagOff}
     * does when it clears it.
     *
     * <p>Method monsterFlagOn coded before 260929, commented in full on 260929.
     *
     * @param flag the flag to set
     */
    public void monsterFlagOn(MonsterFlag flag) {
        monsterFlag.on(flag);

        if (flag == MonsterFlag.MFLAG_VISIBLE)
            updateCached(null);
    }

    /**
     * Push whether this monster is the one the health bar tracks into the legacy cache. Has no C
     * counterpart: C tracks by {@code player->upkeep->health_who} pointing at the monster, which
     * {@code PlayerUpkeep.setHealthWho} already models and also writes to the cache. Nothing calls
     * this method today.
     *
     * <p>Method setMonsterTracked coded before 260929, commented in full on 260929.
     *
     * @param monsterTracked whether this monster is the tracked one
     */
    public void setMonsterTracked(boolean monsterTracked) {
        updateCached(monsterTracked);
    }

    /**
     * Copy this monster's visibility, optional tracked state and seven health-bar timed effects
     * into the legacy {@code PlayerEventStatusUpdate} cache. Has no C counterpart - C's health bar
     * reads the live monster - and, like the cache writes in {@link #setHp(int)}, exists only until
     * the cache-to-messages migration removes the cache.
     *
     * <p>Each part is skipped when its source is {@code null}: the visibility write needs
     * {@link #monsterFlag}, and the seven timed-effect writes need {@link #mTimed}. The constructor
     * assigns both fields before it calls this method, so the guards only matter when a caller
     * passed {@code null} for one of them, as shell monsters in tests do. The tracked
     * write is skipped when {@code monTracked} is {@code null}, which is how the flag setters ask
     * for "leave tracking alone".
     *
     * <p>Method updateCached coded before 260929, commented in full on 261009.
     *
     * @param monTracked whether this monster is the tracked one, or {@code null} to leave that
     *                   part of the cache untouched
     */
    private void updateCached(Boolean monTracked) {
        if (monsterFlag != null)
            PlayerEventStatusUpdate.updatePlayerStatusMonsterVisible(monsterFlag.has(MonsterFlag.MFLAG_VISIBLE));
        if (monTracked != null)
            PlayerEventStatusUpdate.updatePlayerStatusMonsterTracked(monTracked);
        if (mTimed != null) {
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdFear(getMonTimed(MonTimed.MON_TMD_FEAR) != 0);
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdDisen(getMonTimed(MonTimed.MON_TMD_DISEN) != 0);
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdCommand(getMonTimed(MonTimed.MON_TMD_COMMAND) != 0);
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdConf(getMonTimed(MonTimed.MON_TMD_CONF) != 0);
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdStun(getMonTimed(MonTimed.MON_TMD_STUN) != 0);
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdSleep(getMonTimed(MonTimed.MON_TMD_SLEEP) != 0);
            PlayerEventStatusUpdate.updatePlayerStatusMonTmdHold(getMonTimed(MonTimed.MON_TMD_HOLD) != 0);
        }
    }

    /**
     * Read the objects this monster is carrying (C: {@code mon->held_obj}, a linked list headed by
     * that pointer). The list itself is returned, not a copy, and is {@code null} if the
     * constructor was given none - C's empty list is a {@code NULL} head, so a Java caller that
     * iterates must guard the list itself.
     *
     * <p>Method getHeldObjects coded before 261009, commented in full on 261009.
     *
     * @return the carried objects, dropped when the monster dies
     */
    public List<ItemObject> getHeldObjects() {
        return heldObject;
    }

    /**
     * Read the object this monster is currently mimicking (C: {@code mon->mimicked_obj}). A
     * {@code null} answer means none, as a {@code NULL} pointer does in C; a mimicking monster is
     * one with this non-null <em>and</em> {@code MFLAG_CAMOUFLAGE} set, per C's
     * {@code monster_is_mimicking}, which is not ported.
     *
     * <p>Method getMimickedObject coded before 261009, commented in full on 261009.
     *
     * @return the mimicked object, or {@code null} if the monster is not mimicking one
     */
    public ItemObject getMimickedObject() {
        return mimickedObject;
    }

    /**
     * Test whether the player recognizes this monster as a monster - the port of C's
     * {@code monster_is_obvious} in {@code mon-predicate.c}, which is
     * {@code monster_is_visible(mon) && !monster_is_camouflaged(mon)}. A visible mimic is
     * therefore not obvious, and an unseen monster never is, camouflaged or not. Both halves read
     * {@link #monsterFlag}.
     *
     * <p>Method isObvious coded before 261009, commented in full on 261009.
     *
     * @return {@code true} if {@code MFLAG_VISIBLE} is set and {@code MFLAG_CAMOUFLAGE} is not
     */
    public boolean isObvious() {
        return monsterIsVisible() && !monsterIsCamouflaged();
    }

    /**
     * Test whether the player can currently see this monster - the port of C's
     * {@code monster_is_visible} in {@code mon-predicate.c}, a read of the transient
     * {@code MFLAG_VISIBLE} flag. {@link #isObvious()} calls this method, and
     * {@code PlayerCalcs.redrawStuff}'s {@code PR_HEALTH} arm uses it to fill the health bar's
     * visibility component.
     *
     * <p>Method monsterIsVisible coded before 261009, commented in full on 261009.
     *
     * @return {@code true} if {@code MFLAG_VISIBLE} is set
     */
    public boolean monsterIsVisible() {
        return monsterFlag.has(MonsterFlag.MFLAG_VISIBLE);
    }

    /**
     * Read this monster's slot in the level's monster array (C: {@code mon->midx}). {@code 0}
     * until {@link #setMonIndex(int)} has been called.
     *
     * <p>Method getMonIndex coded before 261009, commented in full on 261009.
     *
     * @return the monster's index
     */
    public int getMonIndex() {
        return monIndex;
    }

    /**
     * Record this monster's slot in the level's monster array. {@code Chunk} calls it when the
     * monster is moved to a new index. C assigns {@code mon->midx} directly in
     * {@code monster_index_move()} and {@code place_monster()} in {@code mon-make.c}, so there is
     * no C setter.
     *
     * <p>Method setMonIndex coded before 261009, commented in full on 261009.
     *
     * @param monIndex the new index
     */
    public void setMonIndex(int monIndex) {
        this.monIndex = monIndex;
    }
}
