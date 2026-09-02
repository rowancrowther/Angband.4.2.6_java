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

package uk.co.jackoftradesltd.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.Food;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.StatTables;
import uk.co.jackoftradesltd.middle.gameinput.GameInputHolder;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.monsters.MonsterUtils;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.*;
import uk.co.jackoftradesltd.middle.player.enums.*;

import java.util.*;

/**
 * The derivation engine and the pipeline that decides when it runs - the port of C's
 * {@code player-calcs.c}.
 *
 * <p>Two families of method live here and they meet in the middle. The {@code calc*} family works
 * out everything about a character that is derived rather than stored:
 * {@link #calcBonuses} rebuilds a whole {@link PlayerState} out of race, class, level, equipment,
 * curses, shape and running statuses, and {@link #calcMana}, {@link #calcHitpoints},
 * {@link #calcLight}, {@link #calcBlows}, {@link #calcInventory} and {@link #calcSpells} derive the
 * quantities that hang off it. The {@code *Stuff} family decides when any of that happens.
 *
 * <p><b>Nothing in the game recalculates anything at the point it changes something.</b> Code that
 * dirties the model raises a flag on {@link PlayerUpkeep} - a {@link PlayerUpdateEnum} {@code PU_*}
 * for a stale derived quantity, a {@link PlayerRedraw} {@code PR_*} for a stale piece of screen, a
 * {@link PlayerNotice} {@code PN_*} for a pending pack action - and moves on.
 * {@link #updateStuff} pays off the first, {@link #redrawStuff} the second,
 * {@link #handleStuff} the two together in that order, and {@link #noticeStuff} the third. That
 * indirection is the point: a turn may dirty the same figure a dozen times and still pay for one
 * recalculation, and the order in which the debts are settled is decided in one place rather than
 * at each of the hundreds of sites that raise a flag.
 *
 * <p><b>Nothing is remembered between calls.</b> A derived state is wiped and rebuilt from scratch,
 * so there is no incremental update to get wrong and no way for a stale contribution to survive a
 * change of gear. The consequence is that finding out <em>what</em> moved needs a copy taken
 * beforehand, which is what {@link #updateBonuses} does: derive, compare against the old state,
 * raise a flag per difference, install.
 *
 * <p>The methods are static and take the player, as C's take {@code struct player *p}: a derived
 * state belongs to a character, and there is no state here to hold. The class is a namespace. The
 * lookup tables the derivations read are not here either - the {@code adj_*} tables live in
 * {@link StatTables}, and the per-item questions are methods on the objects themselves.
 *
 * <p>Class PlayerCalcs commented in full on 260901.
 *
 * @author Rowan Crowther
 */
public class PlayerCalcs {
    private static final Logger logger = LogManager.getLogger(PlayerCalcs.class);

    /**
     * Recomputes everything about the character that is derived rather than stored, filling
     * {@code state} from the player's race, class, level, equipment, shape and running statuses —
     * the port of C's {@code calc_bonuses} ({@code player-calcs.c:1877-2325}).
     *
     * <p>This is the game's central derivation. Nothing here is remembered between calls: the state
     * is wiped and rebuilt from scratch every time, so there is no incremental update to get wrong
     * and no way for a stale contribution to survive a change of gear.
     *
     * <p><b>Order is the method's substance.</b> The sequence below is not arbitrary and several
     * steps read what earlier ones wrote:
     *
     * <ol>
     *   <li>Defaults — speed 110, one blow — then race and class: infravision, the skill bases, the
     *       innate resistances and the player flags.</li>
     *   <li>Every worn item, and every curse on it, contributing flags, modifiers, resistances and
     *       combat bonuses.</li>
     *   <li>The shape, which adds to all of the above.</li>
     *   <li>Vulnerabilities, held back until now so that a resistance from any source is compared
     *       against the unpenalised level rather than a lowered one.</li>
     *   <li>Light, the environment-dependent resistances, and the stats — converted here from raw
     *       values into the compressed table indices everything downstream subscripts with.</li>
     *   <li>Hunger, then the timed statuses, then fear.</li>
     *   <li>Carried weight against the strength limit, giving the speed penalty.</li>
     *   <li>The stat-derived bonuses to armour, to-hit, to-damage and the skills — <em>after</em>
     *       step 5, because they are table lookups on the indices it computed.</li>
     *   <li>Launcher and weapon, which need the finished strength index to decide whether either is
     *       too heavy to use properly; then mana, which needs the finished stat indices.</li>
     * </ol>
     *
     * <p><b>The two boolean parameters are independent and neither is a debug switch.</b>
     *
     * <p>{@code knownOnly} builds the state the player <em>believes</em> they have rather than the
     * one they have, by admitting a contribution only where the corresponding rune has been learned.
     * It is what the character sheet displays, so that unidentified gear does not give away its
     * properties. Note what it does to a curse: a curse's template object has a blank known
     * counterpart ({@code obj-init.c:188-194}), so under {@code knownOnly} a curse contributes its
     * modifiers and nothing else — no flags, no resistances, no combat bonuses.
     *
     * <p>{@code update} distinguishes a real recalculation from a hypothetical one. When it is
     * clear, the method must not write anything back to the player: the mana calculation stops short
     * of storing a new maximum, the stun handler does not cancel fast-casting, and the stat indices
     * are nudged by the values the incoming state already carried — C's "hack to allow calculating
     * hypothetical blows", which is how the game answers "what would this weapon give me?" without
     * disturbing the character.
     *
     * <p><b>The equipment walk is the part that looks least like its original.</b> C runs a
     * {@code while (obj)} loop that binds one pointer first to the slot's item and then, in turn, to
     * the template object of each curse on it ({@code player-calcs.c:1929-2020}) — one body,
     * {@code n + 1} passes. The port cannot do that directly because a {@link Curse} has no object
     * of its own, so the passes are built as a list of {@link BonusSource} and the body reads
     * whichever is current. The behaviour is the same, including that curse objects' own curses are
     * never walked and that a curse recorded at zero power contributes nothing.
     *
     * <p>Function calcBonuses commented in full on 260820.
     *
     * @param player    the character being measured: race, class, level, equipment, curses, shape,
     *                  hunger and timed effects are all read from them
     * @param state     the state to fill; wiped on entry and wholly rewritten
     * @param knownOnly {@code true} to count only what the player has learned
     * @param update    {@code true} for a real recalculation that may write back to the player;
     *                  {@code false} for a hypothetical one that must not
     */
    @Contract(mutates = "param1")
    public static void calcBonuses(Player player, @NotNull PlayerState state, boolean knownOnly, boolean update) {
        int extraBlows = 0;
        int extraShots = 0;
        int extraMight = 0;
        int extraMoves = 0;

        ItemObject launcher = player.getPlayerBody().equippedItemBySlotName("shooting");
        ItemObject weapon = player.getPlayerBody().equippedItemBySlotName("weapon");

        Flag<ObjectFlag> f = new Flag<>(ObjectFlag.class);
        Flag<ObjectFlag> collectF = new Flag<>(ObjectFlag.class);
        Map<ElementEnum, Boolean> vulnerabilities = new HashMap<>();

        // Hack to allow calculating hypothetical blows for extra Str, Dex
        int strInd = state.getStatInd(Stats.STAT_STR);
        int dexInd = state.getStatInd(Stats.STAT_DEX);

        // reset the player state
        state.wipe();

        // Various defaults
        state.setSpeed(110);
        state.setNumBlows(100);

        // Race class info
        state.setSeeInfra(player.getRace().getInfravision());
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_MAX || skill == PlayerSkill.SKILL_NONE) continue;
            state.setStateSkill(skill, player.getRace().getSkill(skill) + player.getPlayerClass().getSkill(skill));
        }
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            if (player.getRace().getResistanceLevel(element) == -1)
                vulnerabilities.put(element, true);
            else
                state.setElInfo(element, player.getRace().getResistanceLevel(element));
        }

        // Base pFlags
        state.copyPlayerFlag(player.getRace().getpFlags());
        state.unionPlayerFlags(player.getPlayerClass().getpFlags());

        // Extract the player flags
        player.playerFlags(state, collectF);

        // Analyse equipment
        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            ItemObject item = slot.getItem();
            if (item == null) continue;

            List<BonusSource> sources = new ArrayList<>();
            sources.add(new ItemSource(item));
            for (Map.Entry<Curse, CurseData> e : item.getCurses().entrySet()) {
                if (e.getValue().getPower() != 0) sources.add(new CurseSource(e.getKey()));
            }

            for (BonusSource source : sources) {
                int dig = 0;

                // Extract the item flags
                if (knownOnly) {
                    f = source.flagsKnown();
                } else {
                    f.copyFrom(source.flags());
                }
                collectF.union(f);

                // Apply modifiers
                state.statAdd(Stats.STAT_STR, source.modifier(ObjectModifier.OM_STR)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_STR) ? 1 : 0));
                state.statAdd(Stats.STAT_INT, source.modifier(ObjectModifier.OM_INT)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_INT) ? 1 : 0));
                state.statAdd(Stats.STAT_WIS, source.modifier(ObjectModifier.OM_WIS)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_WIS) ? 1 : 0));
                state.statAdd(Stats.STAT_DEX, source.modifier(ObjectModifier.OM_DEX)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_DEX) ? 1 : 0));
                state.statAdd(Stats.STAT_CON, source.modifier(ObjectModifier.OM_CON)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_CON) ? 1 : 0));
                state.skillAdd(PlayerSkill.SKILL_STEALTH, source.modifier(ObjectModifier.OM_STEALTH)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_STEALTH) ? 1 : 0));
                state.skillAdd(PlayerSkill.SKILL_SEARCH, source.modifier(ObjectModifier.OM_SEARCH) * 5
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_SEARCH) ? 1 : 0));
                state.infraAdd(source.modifier(ObjectModifier.OM_INFRA)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_INFRA) ? 1 : 0));

                if (source.isDigger()) {
                    if (source.flagSet(ObjectFlag.OF_DIG_1))
                        dig = 1;
                    else if (source.flagSet(ObjectFlag.OF_DIG_2))
                        dig = 2;
                    else if (source.flagSet(ObjectFlag.OF_DIG_3))
                        dig = 3;
                }

                dig += source.modifier(ObjectModifier.OM_TUNNEL)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_TUNNEL) ? 1 : 0);
                state.skillAdd(PlayerSkill.SKILL_DIGGING, dig * 20);
                state.setSpeed(state.getSpeed() + source.modifier(ObjectModifier.OM_SPEED)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_SPEED) ? 1 : 0));
                state.setDamRed(state.getDamRed() + source.modifier(ObjectModifier.OM_DAM_RED)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_DAM_RED) ? 1 : 0));
                extraBlows += source.modifier(ObjectModifier.OM_BLOWS)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_BLOWS) ? 1 : 0);
                extraShots += source.modifier(ObjectModifier.OM_SHOTS)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_SHOTS) ? 1 : 0);
                extraMight += source.modifier(ObjectModifier.OM_MIGHT)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_MIGHT) ? 1 : 0);
                extraMoves += source.modifier(ObjectModifier.OM_MOVES)
                        * (player.itemKnowledge.modifierIsKnown(ObjectModifier.OM_MOVES) ? 1 : 0);

                // Apply element info, noting vulnerabilities for later processing
                for (ElementEnum element : ElementEnum.values()) {
                    if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
                    if (!knownOnly || source.knownResLevel(element) != 0) {
                        if (source.resLevel(element) == -1)
                            vulnerabilities.put(element, true);

                        // Res level hasn't included vulnerability yet
                        if (source.resLevel(element) > state.getResLevel(element))
                            state.setResLevel(element, source.resLevel(element));
                    }
                }

                // Apply combat bonuses
                state.setBaseAc(state.getBaseAc() + source.baseAC());
                if (!knownOnly || source.knownToAC() != 0)
                    state.toAcAdd(source.toAC());
                if (slot.getType() != EquipmentSlotsEnum.EQUIP_WEAPON &&
                        slot.getType() != EquipmentSlotsEnum.EQUIP_BOW) {

                    if (!knownOnly || source.knownToHit() != 0) {
                        state.toHitAdd(source.toHit());
                    }
                    if (!knownOnly || source.knownToDam() != 0) {
                        state.toDamAdd(source.toDam());
                    }
                }
            }
        }

        // apply collected flags
        state.unionObjectFlags(collectF);

        // Add shapechange info
        Extras ingoing = new Extras(extraBlows, extraShots, extraMight, extraMoves);
        Extras outgoing = calcShapechange(state, vulnerabilities, player.getShape(), ingoing);
        extraBlows = outgoing.blows();
        extraShots = outgoing.shots();
        extraMight = outgoing.might();
        extraMoves = outgoing.moves();

        // Vulnerabilities
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            if (vulnerabilities.getOrDefault(element, false) && (state.getResLevel(element) < 3))
                state.setResLevel(element, state.getResLevel(element) - 1);
        }

        // Light
        calcLight(player, state, update);

        // Unlight - needs change if anything but resist is introduced for dark
        if (state.hasPFlag(PlayerFlag.PF_UNLIGHT) && GameWorld.hasCharacterDungeon()) {
            state.setElInfo(ElementEnum.ELEM_DARK, 1);
        }

        // Evil
        if (state.hasPFlag(PlayerFlag.PF_EVIL) && GameWorld.hasCharacterDungeon()) {
            state.setElInfo(ElementEnum.ELEM_NETHER, 1);
            state.setElInfo(ElementEnum.ELEM_HOLY_ORB, -1);
        }

        // Various stat values
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_MAX || stat == Stats.STAT_NONE) continue;

            int add = state.getStatAdd(stat);
            add += player.getRace().getStatAdjust(stat);
            add += player.getPlayerClass().getStatsAdj(stat);
            state.setStatTop(stat, PlayerUtils.modifyStatValue(player.getMaxStatValue(stat), add));
            int use = PlayerUtils.modifyStatValue(player.getCurStatValue(stat), add);

            state.setStatUse(stat, use);

            int ind;
            if (use <= 3)
                ind = 0;
            else if (use <= 18)
                ind = use - 3;
            else if (use <= 18 + 219)
                ind = (15 + (use - 18) / 10);
            else
                ind = 37;

            // Hack for hypothetical blows
            if (!update) {
                if (stat == Stats.STAT_STR) {
                    ind += strInd;
                    ind = Math.min(ind, 37);
                    ind = Math.max(ind, 3);
                } else if (stat == Stats.STAT_DEX) {
                    ind += dexInd;
                    ind = Math.min(ind, 37);
                    ind = Math.max(ind, 3);
                }
            }

            // save the new index
            state.setStatInd(stat, ind);
        }

        // Effects of food outside the "fed" range
        if (!PlayerTimed.timedGradeEq(player, TimedEffect.TMD_FOOD, "Fed")) {
            int excess = player.getTimedEffect(TimedEffect.TMD_FOOD) - Food.PY_FOOD_FULL.getFoodValue();
            int lack = Food.PY_FOOD_HUNGRY.getFoodValue() - player.getTimedEffect(TimedEffect.TMD_FOOD);
            if (excess > 0 && player.getTimedEffect(TimedEffect.TMD_ATT_VAMP) == 0) {
                excess = (excess * 10) / (Food.PY_FOOD_MAX.getFoodValue() - Food.PY_FOOD_FULL.getFoodValue());
                state.setSpeed(state.getSpeed() - excess);
            } else if (lack > 0) {
                // Scale to 1/20 of range
                lack = (lack * 20) / Food.PY_FOOD_HUNGRY.getFoodValue();

                // Apply effects progressively
                state.toHitAdd(-lack);
                state.toDamAdd(-lack);
                if (lack > 10 && lack <= 15) {
                    int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
                    value = adjustSkillScale(value, -1, 10, 0);
                    state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
                } else if (lack > 15 && lack <= 18) {
                    int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
                    value = adjustSkillScale(value, -1, 5, 0);
                    state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS) * 9);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS) / 10);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC) * 9);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC) / 10);
                } else if (lack > 18) {
                    int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
                    value = adjustSkillScale(value, -3, 10, 0);
                    state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS) * 8);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS) / 10);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC) * 8);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC) / 10);
                    state.setStateSkill(PlayerSkill.SKILL_SAVE, state.getPlayerSkill(PlayerSkill.SKILL_SAVE) * 9);
                    state.setStateSkill(PlayerSkill.SKILL_SAVE, state.getPlayerSkill(PlayerSkill.SKILL_SAVE) / 10);
                    state.setStateSkill(PlayerSkill.SKILL_SEARCH, state.getPlayerSkill(PlayerSkill.SKILL_SEARCH) * 9);
                    state.setStateSkill(PlayerSkill.SKILL_SEARCH, state.getPlayerSkill(PlayerSkill.SKILL_SEARCH) / 10);
                }
            }
        }

        // Other timed effects
        player.flagsTimed(state.getObjectFlag());

        if (PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Heavy Stun")) {
            state.toHitAdd(-20);
            state.toDamAdd(-20);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 5, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
            if (update)
                player.putTimed(TimedEffect.TMD_FASTCAST, 0);
        } else if (PlayerTimed.timedGradeEq(player, TimedEffect.TMD_STUN, "Stun")) {
            state.toHitAdd(-5);
            state.toDamAdd(-5);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 10, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
            if (update)
                player.putTimed(TimedEffect.TMD_FASTCAST, 0);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_INVULN, 0) != 0)
            state.toAcAdd(100);
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLESSED, 0) != 0) {
            state.toAcAdd(5);
            state.toHitAdd(10);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, 1, 20, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_SHIELD, 0) != 0)
            state.toAcAdd(50);
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_STONESKIN, 0) != 0) {
            state.toAcAdd(40);
            state.setSpeed(state.getSpeed() - 5);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_HERO, 0) != 0) {
            state.toHitAdd(12);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, 1, 20, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_SHERO, 0) != 0) {
            state.skillAdd(PlayerSkill.SKILL_TO_HIT_MELEE, 75);
            state.toAcAdd(-10);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 10, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_FAST, 0) != 0
                || PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_SPRINT, 0) != 0)
            state.setSpeed(state.getSpeed() + 10);
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_SLOW, 0) != 0)
            state.setSpeed(state.getSpeed() - 10);
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_SINFRA, 0) != 0)
            state.infraAdd(5);
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_TERROR, 0) != 0)
            state.setSpeed(state.getSpeed() + 10);
        for (TimedEffect tmd : TimedEffect.values()) {
            if (tmd == TimedEffect.TMD_NONE) continue;
            int resLevel;
            PlayerTimedEffect effect = PlayerRegistry.lookupPlayerTimedEffect(tmd);
            if (effect != null) {
                ElementEnum elementEnum = effect.getTempResist();
                if (elementEnum == null)
                    resLevel = 0;
                else {
                    ElementInfo elementInfo = state.getElInfo().getOrDefault(elementEnum, null);
                    if (elementInfo == null)
                        resLevel = 0;
                    else
                        resLevel = elementInfo.getResLevel();
                }
            } else {
                resLevel = 0;
            }
            if (PlayerTimed.getTimedEffectOrDefault(player, tmd, 0) != 0 && effect != null
                    && effect.getTempResist() != ElementEnum.ELEM_NONE
                    && state.getElInfo().get(effect.getTempResist()) != null
                    && resLevel < 2)
                state.setElInfo(effect.getTempResist(), resLevel + 1);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_CONFUSED, 0) != 0) {
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 4, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_AMNESIA, 0) != 0) {
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 5, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_POISONED, 0) != 0) {
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 20, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_IMAGE, 0) != 0) {
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 5, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLOODLUST, 0) != 0) {
            state.toDamAdd(PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLOODLUST, 0) / 2);
            extraBlows += PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_BLOODLUST, 0) / 20;
        }
        if (PlayerTimed.getTimedEffectOrDefault(player, TimedEffect.TMD_STEALTH, 0) != 0)
            state.setStateSkill(PlayerSkill.SKILL_STEALTH, state.getPlayerSkill(PlayerSkill.SKILL_STEALTH) + 10);

        // Analyze flags, check for fear
        if (state.hasOFlag(ObjectFlag.OF_AFRAID)) {
            state.toHitAdd(-20);
            state.toAcAdd(8);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 20, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }

        // Analyze weight
        int totalWeight = player.getPlayerUpkeep().getTotalWeight();
        int limit = state.weightLimit();
        if (totalWeight > limit / 2)
            state.setSpeed(state.getSpeed() - (totalWeight - (limit / 2)) / (limit / 10));
        if (state.getSpeed() < 0)
            state.setSpeed(0);
        if (state.getSpeed() > 199)
            state.setSpeed(199);

        // Apply modifier bonuses (un-inflate stat bonuses)
        state.toAcAdd(StatTables.adjDexTa[state.getStatInd(Stats.STAT_DEX)]);
        state.toDamAdd(StatTables.adjStrTd[state.getStatInd(Stats.STAT_STR)]);
        state.toHitAdd(StatTables.adjDexTh[state.getStatInd(Stats.STAT_DEX)]);
        state.toHitAdd(StatTables.adjStrTh[state.getStatInd(Stats.STAT_STR)]);

        // Modify skills
        state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS)
                + StatTables.adjDexDis[state.getStatInd(Stats.STAT_DEX)]);
        state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC)
                + StatTables.adjIntDis[state.getStatInd(Stats.STAT_INT)]);
        state.setStateSkill(PlayerSkill.SKILL_DEVICE, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)
                + StatTables.adjIntDev[state.getStatInd(Stats.STAT_INT)]);
        state.setStateSkill(PlayerSkill.SKILL_SAVE, state.getPlayerSkill(PlayerSkill.SKILL_SAVE)
                + StatTables.adjWisSav[state.getStatInd(Stats.STAT_WIS)]);
        state.setStateSkill(PlayerSkill.SKILL_DIGGING, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING)
                + StatTables.adjStrDig[state.getStatInd(Stats.STAT_STR)]);

        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_MAX || skill == PlayerSkill.SKILL_NONE) continue;

            state.setStateSkill(skill, state.getPlayerSkill(skill) + player.getPlayerClass().getXSkill(skill) * player.getLevel() / 10);
        }

        if (state.getPlayerSkill(PlayerSkill.SKILL_DIGGING) < 1) state.setStateSkill(PlayerSkill.SKILL_DIGGING, 1);
        if (state.getPlayerSkill(PlayerSkill.SKILL_STEALTH) > 30) state.setStateSkill(PlayerSkill.SKILL_STEALTH, 30);
        if (state.getPlayerSkill(PlayerSkill.SKILL_STEALTH) < 0) state.setStateSkill(PlayerSkill.SKILL_STEALTH, 0);
        int hold = StatTables.adjStrHold[state.getStatInd(Stats.STAT_STR)];

        // Analyze launcher
        state.setHeavyShoot(false);
        if (launcher != null) {
            int launcherWeight = launcher.weightOne();

            if (hold < launcherWeight / 10) {
                state.toHitAdd(2 * (hold - launcherWeight / 10));
                state.setHeavyShoot(true);
            }

            state.setNumShots(10);

            // Type of ammo
            if (launcher.getKind() == null)
                state.setAmmoTValue(TValue.TV_NONE);
            else {
                if (launcher.getKind().getKindFlags().has(ObjectKindFlag.KF_SHOOTS_SHOTS))
                    state.setAmmoTValue(TValue.TV_SHOT);
                else if (launcher.getKind().getKindFlags().has(ObjectKindFlag.KF_SHOOTS_ARROWS))
                    state.setAmmoTValue(TValue.TV_ARROW);
                else if (launcher.getKind().getKindFlags().has(ObjectKindFlag.KF_SHOOTS_BOLTS))
                    state.setAmmoTValue(TValue.TV_BOLT);
            }

            // Multiplier
            state.setAmmoMult(launcher.getpValue());

            // Special flags
            if (!state.isHeavyShoot()) {
                state.setNumShots(state.getNumShots() + extraShots);
                state.setAmmoMult(state.getAmmoMult() + extraMight);
                if (state.hasPFlag(PlayerFlag.PF_FAST_SHOT))
                    state.setNumShots(state.getNumShots() + player.getLevel() / 3);
            }

            // Need at least 1 shot
            if (state.getNumShots() < 10) state.setNumShots(10);
        }

        // Analyse weapon
        state.setHeavyWield(false);
        state.setBlessWield(false);
        if (weapon != null) {
            int weaponWeight = weapon.weightOne();

            // Can you take the weight (of the weapon)
            if (hold < weaponWeight / 10) {
                state.toHitAdd(2 * (hold - weaponWeight / 10));
                state.setHeavyWield(true);
            }

            if (!state.isHeavyWield()) {
                state.setNumBlows(calcBlows(player, weapon, state, extraBlows));
                state.setStateSkill(PlayerSkill.SKILL_DIGGING, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING) + weaponWeight / 10);
            }

            // Divine weapon bonus
            if (state.hasPFlag(PlayerFlag.PF_BLESS_WEAPON)
                    && (weapon.gettValue() == TValue.TV_HAFTED || state.hasOFlag(ObjectFlag.OF_BLESSED))) {
                state.toDamAdd(2);
                state.setBlessWield(true);
            }
        } else {
            // unarmed
            state.setNumBlows(calcBlows(player, null, state, extraBlows));
        }

        // Mana
        calcMana(player, state, update);
        if (player.getMaxSP() == 0)
            state.playerFlagOn(PlayerFlag.PF_NO_MANA);

        state.setNumMoves(extraMoves);
    }

    /**
     * Carries out the pending one-off notice actions - the port of C's {@code notice_stuff}
     * ({@code player-calcs.c:2536}). Returns at once when no {@code PN_*} flag is raised.
     *
     * <p>Three actions, in C's order: {@code PN_IGNORE} drops items that have become ignorable,
     * {@code PN_COMBINE} merges stacks in the pack, and {@code PN_MON_MESSAGE} flushes the queued
     * monster messages. The flush is deliberately last, so that anything the first two actions say
     * has already been said.
     *
     * <p>Each flag is cleared before the work it asks for runs, not after, so an action may raise
     * its own flag again and have the request survive rather than be wiped by the pass that is
     * carrying it out.
     *
     * <p>The block order does the rest. {@link ObjectIgnore#ignoreDrop} raises {@code PN_COMBINE} as its last
     * act, and the combine block sits after the ignore block - so the combine it asks for is carried
     * out in the same pass, and the method returns with nothing pending. Reversed, the combine would
     * be consumed before the ignore pass had asked for it, and the pack would stay uncombined until
     * something else raised the flag.
     *
     * <p><b>Outstanding:</b> {@link MonsterUtils#showMonsterMessages} is a chapter-6 stub, so
     * {@code PN_MON_MESSAGE} currently clears the flag and discards the messages rather than
     * showing them.
     *
     * <p>Function noticeStuff coded on 260822, commented in full on 260824.
     *
     * @param player the character whose pending notices are serviced; each flag is cleared as it
     *               is dealt with
     */
    public static void noticeStuff(Player player) {
        // Is there anything to notice
        if (!player.getPlayerUpkeep().isNotice()) return;

        // deal with ignore stuff
        if (player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_IGNORE)) {
            player.getPlayerUpkeep().setNoticeFlagOff(PlayerNotice.PN_IGNORE);
            ObjectIgnore.ignoreDrop(player);
        }

        // Combine the pack
        if (player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_COMBINE)) {
            player.getPlayerUpkeep().setNoticeFlagOff(PlayerNotice.PN_COMBINE);
            ObjectUtils.combinePack(player);
        }

        // Dump the monster messages
        if (player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_MON_MESSAGE)) {
            player.getPlayerUpkeep().setNoticeFlagOff(PlayerNotice.PN_MON_MESSAGE);

            // Make sure that this comes after all the monster messages
            MonsterUtils.showMonsterMessages();
        }
    }

    /**
     * Pays off both halves of the player's pending debt - the port of C's {@code handle_stuff}
     * ({@code player-calcs.c:2728}).
     *
     * <p>Code that changes the model never recomputes or repaints anything itself; it raises a
     * {@code PU_*} flag on {@link PlayerUpkeep} for a stale derived quantity, or a {@code PR_*} flag
     * for a stale piece of screen, and moves on. This method is the single call that settles both,
     * and it is what the game loop and every command reach for when the model has to be made
     * consistent again before anything else looks at it.
     *
     * <p>It is only a guarded pair of calls, and both guards are the plain "is anything pending"
     * test C makes on its two bitmasks: {@link PlayerUpkeep#getUpdate()} for the update set, an
     * empty check on the snapshot from {@link PlayerUpkeep#getRedrawFlags()} for the redraw set.
     * Neither call clears anything - each of {@link #updateStuff(Player)} ()} and
     * {@link #redrawStuff(Player)} owns the clearing of the flags it services.
     *
     * <p>The order matters and follows C: recalculation runs first, repaint second. A recalculation
     * routinely raises redraw flags for the figures it has just changed, and because the redraw
     * guard is evaluated after {@link #updateStuff(Player)} has returned, those flags are seen and
     * serviced on this same pass rather than waiting for the next one. The reverse order would
     * repaint the old values and leave the new ones a turn behind.
     *
     * <p>Function handleStuff coded before 260828, commented in full on 260828.
     *
     * @param player the character whose pending updates and redraws are settled
     * @see #updateStuff(Player)
     * @see #redrawStuff(Player)
     */
    public static void handleStuff(Player player) {
        if (player.getPlayerUpkeep().getUpdate()) updateStuff(player);
        if (!player.getPlayerUpkeep().getRedrawFlags().isEmpty()) redrawStuff(player);
    }

    /**
     * Recomputes whichever derived player quantities have been flagged stale, clearing each flag as
     * its recalculation runs - the port of C's {@code update_stuff} ({@code player-calcs.c}).
     *
     * <p>Code that changes the model does not recompute anything itself; it raises the relevant
     * {@link PlayerUpdateEnum} ({@code PU_*}) flag on {@link PlayerUpkeep} and moves on. This method
     * is the single point where that debt is paid off, so a turn that dirties the same quantity a
     * dozen times still only recalculates it once.
     *
     * <p>The order of the clauses is load-bearing and follows C exactly: the inventory is rebuilt
     * before bonuses, because {@link #updateBonuses} reads the equipment; bonuses come before the
     * light radius, hit points and mana, all of which depend on the bonus figures; and spells come
     * last of the model-side clauses. Each clause clears its own flag <em>before</em> calling the
     * calculation, so a recalculation that raises the same flag again - legitimately asking for
     * another pass - is not swallowed.
     *
     * <p>Spells are recalculated only for a class with spells to learn
     * ({@code total_spells > 0}); for a warrior the flag is still cleared, matching C.
     *
     * <p>Two early returns then split the model half from the map half. Nothing below them runs
     * until the character exists ({@link GameWorld#characterGenerated}) and the map is actually on
     * screen ({@link uk.co.jackoftradesltd.middle.gameinput.GameInput#mapIsVisible()}); the flags for
     * those clauses are deliberately left raised, so the work happens on the first pass after the
     * map appears rather than being lost.
     *
     * <p>In the map half, {@code PU_DISTANCE} subsumes {@code PU_MONSTERS}: it clears both flags and
     * calls {@link MonsterUtils#updateMonsters(boolean)} with {@code full} set, so the cheaper monster-only pass
     * is skipped rather than run twice. The final clause signals
     * {@link uk.co.jackoftradesltd.channel.enums.GameEventType#EVENT_PLAYERMOVED}, which is how the
     * viewport is re-centred across the boundary; C raises the same event for the same reason.
     *
     * <p>The leading {@code getUpdate()} guard is a fast exit for the common case of nothing being
     * stale, not a correctness requirement - with no flags raised every clause would fall through
     * anyway.
     *
     * <p><b>Outstanding:</b> {@link MonsterUtils#updateMonsters(boolean)} is still a stub, so the
     * {@code PU_DISTANCE} and {@code PU_MONSTERS} clauses clear their flags but do no work yet.
     *
     * <p>Function updateStuff coded before 260828, commented in full on 260828.
     *
     * @param player the character whose flagged derived quantities are recomputed, and on whom the
     *               results are stored
     * @see #redrawStuff(Player)
     */
    public static void updateStuff(Player player) {
        if (!player.getPlayerUpkeep().getUpdate()) return;

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_INVEN)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_INVEN);
            calcInventory(player);
        }

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_BONUS)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_BONUS);
            updateBonuses(player);
        }

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_TORCH)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_TORCH);
            calcLight(player, player.getPlayerState(), true);
        }

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_HP)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_HP);
            calcHitpoints(player);
        }

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_MANA)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_MANA);
            calcMana(player, player.getPlayerState(), true);
        }

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_SPELLS)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_SPELLS);
            if (player.getPlayerClass().getMagic().getTotalSpells() > 0)
                calcSpells();
        }

        // Character is not ready yet - no map updates
        if (!GameWorld.characterGenerated) return;

        // Map is not shown, no map updates
        if (!GameInputHolder.getInstance().mapIsVisible()) return;

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_UPDATE_VIEW)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_UPDATE_VIEW);
            // Run on actual cave, not player's view
            GameState.getCave().updateView(player);
        }

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_DISTANCE)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_DISTANCE);
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_MONSTERS);
            MonsterUtils.updateMonsters(true);
        }

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_MONSTERS)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_MONSTERS);
            MonsterUtils.updateMonsters(false);
        }

        if (player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_PANEL)) {
            player.getPlayerUpkeep().updateOff(PlayerUpdateEnum.PU_PANEL);
            GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_PLAYERMOVED);
        }
    }

    /**
     * Repaints whichever screen regions have been flagged stale, by signalling one UI event per
     * raised flag and then clearing the flags it dealt with — the port of C's {@code redraw_stuff}
     * ({@code player-calcs.c:2678}).
     *
     * <p>This is the redraw half of the pair {@link #updateStuff(Player)} begins: code that changes the
     * model raises a {@link PlayerRedraw} ({@code PR_*}) flag on {@link PlayerUpkeep} and moves on,
     * and this method is the single point where the screen catches up. Nothing is painted here —
     * every flag becomes an event on the bus, and the display side across the boundary decides what
     * that means.
     *
     * <p>The pass works on a <em>snapshot</em> of the flags ({@link PlayerUpkeep#getRedrawFlags()},
     * C's {@code uint32_t redraw = p->upkeep->redraw;}), and clears only that snapshot at the end
     * ({@link PlayerUpkeep#clearRedrawFlags}, C's {@code p->upkeep->redraw &= ~redraw}). That
     * matters twice over: a handler that dirties something while responding to one of these events
     * raises its flag on the live set and keeps it, and the narrowing described below drops flags
     * from the snapshot without ever clearing them from the upkeep.
     *
     * <p>Three guards sit in front of the work, in C's order:
     * <ul>
     *   <li>an empty snapshot returns at once — the common case;</li>
     *   <li>no character yet ({@link GameWorld#characterGenerated}) returns, leaving every flag
     *       raised for the first pass after birth;</li>
     *   <li>the map not being on screen
     *       ({@link uk.co.jackoftradesltd.middle.gameinput.GameInput#mapIsVisible()}) does not return;
     *       it narrows the snapshot to the subwindow flags ({@code PR_MONSTER}, {@code PR_OBJECT},
     *       {@code PR_MONLIST}, {@code PR_ITEMLIST} — C's {@code PR_SUBWINDOW} mask), so the
     *       detachable panes still refresh while the main-term flags stay pending.</li>
     * </ul>
     *
     * <p>Then the speed hack C keeps: while resting or running, the screen is only refreshed on
     * every hundredth turn of either counter, because a rest that repaints each turn takes visibly
     * longer to sit through. A pending message or map redraw overrides the hack. Note that the
     * narrowing above happens first, so with the map hidden neither override can be present and the
     * hack always returns.
     *
     * <p>Every remaining flag is signalled through {@link PlayerRedraw#getEventType()}, then the map
     * separately, because it is the one event carrying data: {@code EVENT_MAP} with the point
     * {@code (-1, -1)}, C's sentinel for "the whole map, not one grid". A last
     * {@code EVENT_END} tells the display the batch is complete and it may now do any plotting it
     * deferred — and, like the narrowing, it is skipped when only subwindows were refreshed.
     *
     * <p><b>Deliberate divergence:</b> C drives the signalling from a fixed table
     * ({@code redraw_events}, {@code player-calcs.c:2634}) and so emits the events in that table's
     * order; this iterates the flag set, which is {@link PlayerRedraw} declaration order. The
     * ordering is not honoured, and does not need to be — the handlers are independent. What is
     * honoured is the map coming after the rest of the events, and {@code EVENT_END} coming last of
     * all.
     *
     * <p>Function redrawStuff coded on 260828, commented in full on 260828.
     *
     * @param player the character whose flagged display elements are re-sent to the front end
     * @see #updateStuff(Player)
     * @see PlayerUpkeep#getRedrawFlags()
     * @see PlayerUpkeep#clearRedrawFlags(uk.co.jackoftradesltd.channel.utils.FlagView)
     */
    public static void redrawStuff(Player player) {
        Flag<PlayerRedraw> redraw = player.getPlayerUpkeep().getRedrawFlags();

        // Is there stuff to redraw
        if (redraw.isEmpty()) return;

        // Do we have a character?
        if (!GameWorld.characterGenerated) return;

        // Map is not shown - subwindow updates only
        if (!GameInputHolder.getInstance().mapIsVisible()) {
            redraw.mask(PlayerRedraw.PR_MONSTER, PlayerRedraw.PR_OBJECT,
                    PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST);
        }

        // Hack - rarely update while resting or running, makes it over quicker
        if (((PlayerUtils.playerRestingCount(player) % 100 != 0) || (player.getPlayerUpkeep().getRunning() % 100 != 0))
                && ((!redraw.has(PlayerRedraw.PR_MESSAGE)) && !redraw.has(PlayerRedraw.PR_MAP)))
            return;

        // For each listed flag (apart from PR_MAP) - send the appropriate signal to the UI
        for (PlayerRedraw playerRedraw : redraw) {
            if (playerRedraw == PlayerRedraw.PR_MAP) continue;
            GameEngine.getEventsBusHandler().eventSignal(playerRedraw.getEventType());
        }

        // Now for the ones that require parameters to be supplied
        if (redraw.has(PlayerRedraw.PR_MAP)) {
            GameEngine.getEventsBusHandler().eventSignalPoint(GameEventType.EVENT_MAP, -1, -1);
        }

        // clear the flags
        player.getPlayerUpkeep().clearRedrawFlags(redraw);

        // If map isn't shown do the subwindow updates only.
        if (!GameInputHolder.getInstance().mapIsVisible()) return;

        // Do any plotting etc, delayed from earlier - this set of updates is over
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_END);
    }

    /**
     * Works out the player's maximum mana, and whether their armour is heavy enough to cost them
     * some — the port of C's {@code calc_mana} ({@code player-calcs.c:1480-1554}).
     *
     * <p>Three questions in order. Can the class cast at all: a class with no spells has its mana
     * zeroed outright and the method returns. What does level give: mana grows with the levels
     * gained <em>since</em> the class's first spell level, scaled by a table indexed on the average
     * of the governing stats, so a caster below that level gets nothing. What does armour take
     * away: everything worn except weapon, launcher, rings, amulet and light is weighed, and each
     * ten tenth-pounds above the class's allowance costs a point and raises the encumbrance flag.
     *
     * <p>Writes to two places, and the split follows {@code update}. The encumbrance flag always
     * goes to the state, because it describes the calculation. The maximum itself goes to the
     * <em>player</em>, not the state, and only when {@code update} is set — so a hypothetical
     * recalculation leaves the character's mana alone. When the maximum does change, current mana is
     * capped to it and a redraw is asked for.
     *
     * <p>Function calcMana commented in full on 260820.
     *
     * @param player the character whose class, level and spell stat set the maximum, and on whom
     *               that maximum is stored when {@code update} is set
     * @param state  the state being filled; receives the armour-encumbrance flag
     * @param update {@code true} to store the new maximum on the player, {@code false} to compute
     *               and discard it
     */
    public static void calcMana(Player player, PlayerState state, boolean update) {
        // Must know spells
        if (player.getPlayerClass().getMagic().getTotalSpells() == 0) {
            player.setMaxSP(0);
            player.setCurSp(0);
            player.setCspFrac(0);
            return;
        }

        int tempMaxSP;

        // Extract effective player level
        int levels = (player.getLevel() - player.getPlayerClass().getMagic().getSpellFirst()) + 1;
        if (levels > 0) {
            tempMaxSP = 1;
            tempMaxSP += StatTables.adjMagMana[averageSpellStat(player, state)] * levels / 100;
        } else {
            tempMaxSP = 0;
        }

        // Assume not encumbered by armour
        state.setCumberArmour(false);

        // weigh the armour
        int currentWeight = 0;
        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_WEAPON) continue;
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_BOW) continue;
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_RING) continue;
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_AMULET) continue;
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_LIGHT) continue;

            ItemObject item = slot.getItem();

            if (item != null)
                currentWeight += item.weightOne();
        }

        // determine max weight allowance
        int maxWeight = player.getPlayerClass().getMagic().getSpellWeight();

        // Heavy armour penalises mana
        if (((currentWeight - maxWeight) / 10) > 0) {
            // Encumbered
            state.setCumberArmour(true);

            // reduce mana
            tempMaxSP -= ((currentWeight - maxWeight) / 10);
        }

        // Non-negative
        tempMaxSP = Math.max(tempMaxSP, 0);

        // if no updates, return
        if (!update) return;

        if (player.getMaxSP() != tempMaxSP) {
            player.setMaxSP(tempMaxSP);

            // enforce new limits
            if (player.getCurSp() >= player.getMaxSP()) {
                player.setCurSp(player.getMaxSP());
                player.setCspFrac(0);
            }

            // Display mana at next draw
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_MANA);
        }
    }

    /**
     * The stat table index a caster's mana is scaled by, averaged over every realm the class draws
     * on — the port of C's {@code average_spell_stat}
     * ({@code player-calcs.c:1247-1259}).
     *
     * <p>Averages the compressed <em>indices</em>, not the stat values, because that is what indexes
     * the mana table. A class casting from one realm gets that realm's stat unchanged; a class
     * spanning two is held to the mean of both, so neglecting either costs mana. The division rounds
     * up.
     *
     * <p>Function averageSpellStat commented in full on 260820.
     *
     * @param player the character whose class supplies the realms to average over
     * @param state  the state whose stat indices to read
     * @return the averaged stat index
     * @throws ArithmeticException if the class has no realms — the caller must establish that it
     *                             casts before asking, as {@code calcMana}'s literacy test does
     */
    private static int averageSpellStat(Player player, PlayerState state) {
        Set<MagicRealm> realms = player.getPlayerClass().magicRealm();
        int numRealms = realms.size();
        int total = 0;
        for (MagicRealm realm : realms) {
            total += state.getStatInd(realm.getStat());
        }

        return (total + numRealms - 1) / numRealms;
    }

    /**
     * Blows per turn with a given weapon, scaled by 100 — the port of C's {@code calc_blows}
     * ({@code player-calcs.c:1703-1735}).
     *
     * <p>Strength is weighed against the weapon: {@code adjStrBlow[STR]} times the class's attack
     * multiplier, divided by the weapon's weight. A heavy weapon in a weak arm lands on a low rung,
     * and the class's minimum weight is a floor on the divisor — a weapon lighter than that is
     * treated as if it weighed that much, which both stops the division by zero for a weightless
     * weapon and stops a class with a high minimum profiting endlessly from daggers. Dexterity gives
     * the second subscript; both saturate at 11.
     *
     * <p>The table holds energy per blow, so the count is 10000 divided by it, capped at the class's
     * maximum attacks. Extra blows from equipment are added <em>after</em> that cap, so a modifier
     * can carry a character past the class ceiling where strength and dexterity alone cannot.
     *
     * <p>The floor at the end is one blow, or two under the percentage-damage birth option, where
     * blows are worth proportionally less.
     *
     * <p>Function calcBlows commented in full on 260820.
     *
     * @param player     the character whose class supplies the minimum weapon weight, attack
     *                   multiplier, maximum attacks and birth options
     * @param item       the weapon, or {@code null} for unarmed — which weighs nothing and so gets
     *                   the class minimum as its divisor
     * @param state      the state whose strength and dexterity indices to read
     * @param extraBlows extra blows gathered from equipment, shape and statuses
     * @return blows per turn, multiplied by 100
     */
    public static int calcBlows(Player player, ItemObject item, PlayerState state, int extraBlows) {
        int weight = (item == null) ? 0 : item.weightOne();
        int minWeight = player.getPlayerClass().getMinWeight();

        // Enforce a 1/10 pound minimum weight
        int divisor = Math.max(weight, minWeight);

        // Get the strength v weight
        int strIndex = StatTables.adjStrBlow[state.getStatInd(Stats.STAT_STR)]
                * player.getPlayerClass().getAttMultiply() / divisor;

        // Maximal value
        if (strIndex > 11) strIndex = 11;

        // Dexterity
        int dexIndex = Math.min(StatTables.adjDexBlow[state.getStatInd(Stats.STAT_DEX)], 11);

        // Energy per blow
        int blowEnergy = StatTables.blowsTable[strIndex][dexIndex];

        int blows = Math.min((10000 / blowEnergy), (100 * player.getPlayerClass().getMaxAttacks()));

        return Math.max(blows + (100 * extraBlows),
                player.getPlayerOptions().has(PlayerOptionEnum.OP_birth_percent_damage) ? 200 : 100);
    }

    /**
     * Adds the player's current shape's contribution to the state — the port of C's
     * {@code calc_shapechange} ({@code player-calcs.c:1798-1853}).
     *
     * <p>A shape contributes on the same terms as a piece of equipment: combat bonuses, skills,
     * object and player flags, stats, the seven other modifiers, and resistances. It is applied
     * after the equipment walk and before vulnerabilities are settled, so a shape's resistance is
     * weighed against the gear's on equal footing and its vulnerability is remembered for later
     * alongside everything else's.
     *
     * <p>Two departures from C, both forced by the port's shapes:
     *
     * <ul>
     *   <li><b>A null shape returns the totals untouched.</b> C cannot reach this state — a player
     *       always has a shape, "normal", assigned at birth and restored on changing back
     *       ({@code player-birth.c:456}, {@code player-util.c:1050}) — but the port allows the field
     *       to be absent, and an absent shape must contribute nothing rather than throw.</li>
     *   <li><b>The extra blows and shots travel by value.</b> C passes four pointers and adds into
     *       the caller's storage; see {@link Extras}.</li>
     * </ul>
     *
     * <p>Function calcShapechange commented in full on 260820.
     *
     * @param state           the state being filled, mutated in place
     * @param vulnerabilities the running set of elements something has made the player vulnerable
     *                        to, added to if the shape carries a vulnerability
     * @param shape           the player's current shape, or {@code null} if none is set
     * @param incoming        the blow, shot, might and move totals gathered so far
     * @return the same four totals with the shape's contribution added
     */
    public static Extras calcShapechange(PlayerState state,
                                         Map<ElementEnum, Boolean> vulnerabilities,
                                         PlayerShape shape,
                                         Extras incoming) {
        // If shape == null, not shape changed
        if (shape == null) return incoming;

        // Combat stats
        state.toAcAdd(shape.getToAc());
        state.toHitAdd(shape.getToHit());
        state.toDamAdd(shape.getToDam());

        // Skills
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            state.skillAdd(skill, shape.getSkills().getOrDefault(skill, 0));
        }

        // Object flags
        state.unionObjectFlags(shape.getFlags());

        // Player flags
        state.unionPlayerFlags(shape.getPflags());

        // Stats
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            state.statAdd(stat, shape.getModifier(stat));
        }

        // Other modifiers
        state.skillAdd(PlayerSkill.SKILL_STEALTH, shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_STEALTH, 0));
        state.skillAdd(PlayerSkill.SKILL_SEARCH, shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_SEARCH, 0) * 5);
        state.infraAdd(shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_INFRA, 0));
        state.skillAdd(PlayerSkill.SKILL_DIGGING,
                shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_TUNNEL, 0) * 20);
        state.setSpeed(state.getSpeed() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_SPEED, 0));
        state.setDamRed(state.getDamRed() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_DAM_RED, 0));

        int extraBlows = incoming.blows() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0);
        int extraShots = incoming.shots() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_SHOTS, 0);
        int extraMight = incoming.might() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_MIGHT, 0);
        int extraMoves = incoming.moves() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_MOVES, 0);

        // Resists and vulnerabilities
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            ElementInfo elInfo = shape.getElementValueModifiers().getOrDefault(element, null);
            if (elInfo != null && elInfo.getResLevel() == -1) {
                vulnerabilities.put(element, true);
            } else if (elInfo != null && elInfo.getResLevel() > state.getResLevel(element)) {
                state.setResLevel(element, elInfo.getResLevel());
            }
        }

        return new Extras(extraBlows, extraShots, extraMight, extraMoves);
    }

    /**
     * Works out the radius of light the player sheds — the port of C's {@code calc_light}
     * ({@code player-calcs.c:1598-1646}).
     *
     * <p>Sums rather than picks: every worn item's contribution is added, so a lantern and a glowing
     * ring both count. An item's contribution is its innate radius from {@code OF_LIGHT_2} or
     * {@code OF_LIGHT_3} plus its light modifier, with two adjustments. A player with
     * {@code PF_UNLIGHT} loses a point from any positive light modifier, which lets them carry
     * lightly-glowing gear without spoiling the dark they depend on. And a fuelled light source that
     * has burnt out contributes nothing at all — its whole contribution is zeroed, not just its
     * innate part.
     *
     * <p>In the town by day the answer is simply zero and the method returns early, but not before
     * checking whether that differs from the player's current light and asking for a redraw if so —
     * which is why the early return is inside the {@code update} branch rather than around it.
     *
     * <p>Function calcLight commented in full on 260820.
     *
     * @param player the character whose equipment, timed effects and surroundings supply the light
     * @param state  the state to write the light radius to
     * @param update {@code true} if this is a real recalculation, which may ask the display to
     *               refresh
     */
    public static void calcLight(Player player, PlayerState state, boolean update) {
        state.setCurLight(0);

        // Is it day in the town
        if (player.getDepth() == 0 && GameWorld.isDaytime() && update) {
            if (player.getPlayerState() != null && player.getPlayerState().getCurLight() != state.getCurLight()) {
                player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_MONSTERS);
                player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_UPDATE_VIEW);
            }
            return;
        }

        // Get brightest of wielded objects
        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            ItemObject item = slot.getItem();
            if (item == null) continue;
            int amount = 0;

            // does item have light radius?
            if (item.hasFlag(ObjectFlag.OF_LIGHT_2)) {
                amount = 2;
            } else if (item.hasFlag(ObjectFlag.OF_LIGHT_3)) {
                amount = 3;
            }
            amount += item.getModifierValue(ObjectModifier.OM_LIGHT);

            // Adjustment to allow UNLIGHT players to use +1 LIGHT gear
            if (item.getModifierValue(ObjectModifier.OM_LIGHT) > 0 && state.hasPFlag(PlayerFlag.PF_UNLIGHT)) {
                amount--;
            }

            if (item.gettValue() != null && item.gettValue().isLight()
                    && !item.hasFlag(ObjectFlag.OF_NO_FUEL)
                    && item.getTimeout() == 0) {
                // Items without fuel yield no light
                amount = 0;
            }

            state.setCurLight(state.getCurLight() + amount);
        }
    }

    /**
     * Scales a skill by a fraction of itself — the port of C's {@code adjust_skill_scale}
     * ({@code player-calcs.c:1781-1792}), the way every temporary skill penalty and bonus in
     * {@code calcBonuses} is applied.
     *
     * <p>Proportional rather than flat: blessing improves device skill by a twentieth of what it
     * already is, so it is worth more to a character who is already good. The adjustment is computed
     * from the magnitude of the value, so a negative skill is scaled by the same amount a positive
     * one would be rather than moving the other way, and {@code minValue} sets a floor on that
     * magnitude so a skill of zero can still be adjusted.
     *
     * <p><b>A negative numerator is not simply the positive case with the sign flipped.</b> The
     * subtraction rounds <em>up</em> — the {@code + denominator - 1} — so that the result matches
     * what {@code value * (denominator + numerator) / denominator} would give for a positive value.
     * Truncating instead would make a penalty slightly gentler than the equivalent multiplication,
     * and the two idioms are used interchangeably in the original.
     *
     * <p>Function adjustSkillScale commented in full on 260820.
     *
     * @param value       the skill value to adjust
     * @param numerator   the fraction's numerator; negative for a penalty
     * @param denominator the fraction's denominator
     * @param minValue    a floor on the magnitude the fraction is taken of, so that a small or zero
     *                    skill still moves
     * @return the adjusted skill value
     */
    public static int adjustSkillScale(int value, int numerator, int denominator, int minValue) {
        if (numerator >= 0) {
            int add = Math.max(minValue, Math.abs(value)) * numerator / denominator;
            return value + add;
        }
        int sub = ((Math.max(minValue, Math.abs(value)) * -numerator) + denominator - 1) / denominator;
        return value - sub;
    }

    /**
     * Recalculates the character's maximum hit points and clamps the current total to it — the port
     * of C's {@code calc_hitpoints} ({@code player-calcs.c:1562-1588}).
     *
     * <p>Two numbers make the maximum. {@code playerHP[level - 1]} is the running total of the hit
     * dice rolled at each level, fixed at birth and never re-rolled, and {@code adjConMhp} adds a
     * bonus for constitution expressed in hundredths of a hit point per level — so the table's 250
     * at 18/40 is two and a half hit points for every level the character has. The bonus is negative
     * for poor constitution, and the division truncates toward zero in both languages, so a penalty
     * is rounded the same way a bonus is rather than one point harsher.
     *
     * <p>C declares {@code bonus} as {@code long}; an {@code int} carries it here because the widest
     * product the table can reach is 1250 × 50, nowhere near overflow.
     *
     * <p><b>Everything is guarded on the maximum having actually changed.</b> Most calls do not move
     * it, and the body must not run for those: it would clamp and repaint on every recalculation.
     * Inside the guard the clamp is {@code >=}, not {@code >}, so a character already sitting exactly
     * on the new maximum has {@code Player.chpFrac} cleared as well. That matters because the fraction is
     * sub-hitpoint regeneration credit — leaving a stale one behind hands out a free hit point at
     * the next tick.
     *
     * <p>{@link PlayerRedraw#PR_HP} is raised rather than the display being touched, in keeping with
     * the rest of the calculation: this runs in the game half and the UI repaints when it is told.
     *
     * <p>The constitution read is {@code state}'s <em>derived</em> stat index, so this must run after
     * {@code calcBonuses} has filled it. C guarantees that by ordering the flags in
     * {@code update_stuff}, where {@code PU_BONUS} is handled before {@code PU_HP}
     * ({@code player-calcs.c:2575-2588}).
     *
     * <p>Function calcHitpoints commented in full on 260820.
     *
     * @param player the character whose maximum hit points are recomputed and stored
     */
    public static void calcHitpoints(Player player) {
        // 1/100th hitpoint bonus per level
        int bonus = StatTables.adjConMhp[player.getPlayerState().getStatInd(Stats.STAT_CON)];

        // Calculate max hp
        int mhp = player.getPlayerHP(player.getLevel() - 1) + (bonus * player.getLevel() / 100);

        // Always have 1 hp per level
        if (mhp < player.getLevel() + 1) mhp = player.getLevel() + 1;

        // New maximum hitpoints
        if (player.getMaxHP() != mhp) {
            // save the new limit
            player.setPlayerMaxHP(mhp);

            // enforce new limit
            if (player.getCurrentHP() >= mhp) {
                player.setCurrentHP(mhp);
                player.setChpFrac(0);
            }

            // Prepare to display the hitpoints
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_HP);
        }
    }

    /**
     * Recalculates the character's whole derived state and then reports what moved — the port of
     * C's {@code update_bonuses} ({@code player-calcs.c:2336-2456}), and the only caller of
     * {@link PlayerCalcs#calcBonuses}.
     *
     * <p>The method is in two halves. The first derives a fresh state from scratch; the second sets
     * it beside the old one, raises an update or redraw flag for each difference that something
     * downstream cares about, and finally installs the new state over the old. Nothing here draws
     * anything or recalculates hit points itself — it only records that those things are now due,
     * and {@code updateStuff} runs them in its own order afterwards. C guarantees that ordering by
     * handling {@code PU_BONUS} before {@code PU_HP}, {@code PU_MANA} and {@code PU_SPELLS}
     * ({@code player-calcs.c:2570-2600}), so a flag raised here is always serviced after this
     * returns and never during.
     *
     * <p><b>The two {@code copy} calls are the load-bearing line of the method.</b> C opens with
     * {@code struct player_state state = p->state;} — a by-value copy of the entire struct, taken
     * before anything is recalculated, so that {@code p->state} still holds the old values while
     * the local holds the new. A Java assignment would bind a second name to the same object
     * instead: {@code calcBonuses} would write straight through into the field, every comparison
     * below would compare an object with itself, and the whole second half would silently do
     * nothing. {@link PlayerState#copy()} exists for this one call site.
     *
     * <p>The null guard has no counterpart in C, where {@code p->state} is an inline struct member
     * and so exists, zeroed, from the moment the player does. The port's fields start as
     * {@code null}, and this is the first thing to want them, so it is the natural place to give
     * them a zeroed state. That makes the first call after birth report every stat as changed, which
     * is correct rather than merely harmless: C's zeroed struct behaves identically, and the flags
     * it raises are exactly the ones a newly created character needs serviced.
     *
     * <p><b>Two of the comparisons read a different pair of states from the rest, and both are
     * deliberate.</b> Armour compares the new known state against the old <em>known</em> state,
     * because the armour class on the display is what the character believes it to be, not what it
     * truly is. The light radius compares the two <em>plain</em> states, because how far the
     * character can actually see is a fact about the world and not a matter of belief. Everything
     * else compares plain against plain.
     *
     * <p>The stat loop leans on {@link PlayerState#wipe()} having filled every stat map, so
     * {@link PlayerState#getStatTop} and {@link PlayerState#getStatUse} can read straight out of the
     * map: a missing stat would be a real defect and is better heard about loudly. Only a change in
     * the compressed index raises anything beyond a redraw, since that index is what the
     * {@code adj_*} tables are read with — a stat that moves without changing its rung changes no
     * derived number, and constitution is singled out because it is the one that feeds hit points.
     *
     * <p>The message block is skipped in partial-update mode. C sets {@code only_partial} around the
     * full-screen rebuild on arriving at a new level ({@code ui-display.c:2522-2557}), where the
     * state is being recomputed wholesale rather than responding to anything the character did;
     * announcing "you have trouble wielding such a heavy bow" there would be reporting a change that
     * never happened.
     *
     * <p>Function updateBonuses commented in full on 260820.
     *
     * @param player the character whose real and known states are rebuilt
     */
    public static void updateBonuses(Player player) {
        if (player.getPlayerState() == null) player.setState(new PlayerState());
        if (player.getKnownState() == null) player.setKnownState(new PlayerState());

        PlayerState state = player.getPlayerState().copy();
        PlayerState knownState = player.getKnownState().copy();

        // calculate bonuses
        calcBonuses(player, state, false, true);
        calcBonuses(player, knownState, true, true);

        // Notice changes
        // Analyze stats
        for (Stats stat : Stats.values()) {
            // Only check non-guard stats
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;

            // Check for changes
            if (state.getStatTop(stat) != player.getPlayerState().getStatTop(stat))
                // Set to redraw stats
                player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_STATS);

            // Check for changes
            if (state.getStatUse(stat) != player.getPlayerState().getStatUse(stat))
                // Set to redraw stats
                player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_STATS);

            // Check for changes
            if (state.getStatInd(stat) != player.getPlayerState().getStatInd(stat)) {
                // change in Con can affect Hitpoints
                if (stat == Stats.STAT_CON)
                    player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_HP);

                // Change in stats may affect mana and spells
                player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_MANA);
                player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_SPELLS);
            }
        }

        // Telepathy change
        if (state.hasOFlag(ObjectFlag.OF_TELEPATHY) != player.getPlayerState().hasOFlag(ObjectFlag.OF_TELEPATHY))
            // Update monster visibility
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_MONSTERS);

        // See invis change
        if (state.hasOFlag(ObjectFlag.OF_SEE_INVIS) != player.getPlayerState().hasOFlag(ObjectFlag.OF_SEE_INVIS))
            // Update monster visibility
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_MONSTERS);

        // Redraw speed if required
        if (state.getSpeed() != player.getPlayerState().getSpeed())
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_SPEED);

        // Redraw armour if required
        if (knownState.getBaseAc() != player.getKnownState().getBaseAc()
                || knownState.getToAc() != player.getKnownState().getToAc())
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_ARMOR);

        // Notice changes in the 'light radius'
        if (state.getCurLight() != player.getPlayerState().getCurLight()) {
            // Update visuals
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_UPDATE_VIEW);
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_MONSTERS);
        }

        // Notice changes to the weight limit
        if (player.getPlayerState().weightLimit() != state.weightLimit())
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_INVEN);

        // Partial modes
        if (!player.getPlayerUpkeep().isOnlyPartial()) {
            // Has Heavy Bow changed
            if (state.isHeavyShoot() != player.getPlayerState().isHeavyShoot()) {
                if (state.isHeavyShoot())
                    Message.message("You have trouble wielding such a heavy bow.");
                else if (player.getPlayerBody().equippedItemBySlotName("shooting") != null)
                    Message.message("You have no trouble wielding your bow.");
                else
                    Message.message("You feel relieved to put down your heavy bow.");
            }

            // Has heavy weapon changed
            if (state.isHeavyWield() != player.getPlayerState().isHeavyWield()) {
                if (state.isHeavyWield())
                    Message.message("You have trouble wielding such a heavy weapon.");
                else if (player.getPlayerBody().equippedItemBySlotName("weapon") != null)
                    Message.message("You have no trouble wielding your weapon.");
                else
                    Message.message("You feel relieved to put down your heavy weapon.");
            }

            // Has illegal weapon changed
            if (state.isBlessWield() != player.getPlayerState().isBlessWield()) {
                if (state.isBlessWield())
                    Message.message("You feel attuned to your weapon.");
                else if (player.getPlayerBody().equippedItemBySlotName("weapon") != null)
                    Message.message("You feel less attuned to your weapon.");
            }

            // Has armour state changed
            if (state.isCumberArmour() != player.getPlayerState().isCumberArmour()) {
                if (state.isCumberArmour())
                    Message.message("The weight of your armor reduces your maximum SP.");
                else
                    Message.message("Your maximum SP is no longer reduced by armor weight.");
            }
        }

        player.setState(state);
        player.setKnownState(knownState);
    }

    /**
     * Rebuilds the pack and quiver views over the gear - the port of C's {@code calc_inventory}
     * ({@code player-calcs.c:1023}).
     *
     * <p>The gear is the one true list of what the player carries; {@code upkeep.inventory} and
     * {@code upkeep.quiver} are only views onto it, arrays that give the display and the commands
     * something to index. Nothing edits those views in place - every change to the gear raises
     * {@code PU_INVEN} and they are thrown away and rebuilt here.
     *
     * <p>The work is a single pass over the gear per destination slot, and the {@code assigned}
     * list is what keeps the passes from claiming the same object twice. It is seeded from
     * {@link PlayerBody#itemIsEquipped}, so equipped items start out already spoken for and are
     * never offered to either view; the remaining entries are filled false out to {@code numMax},
     * which is C's {@code n_max}, the largest gear list that can exist - a pack that may be
     * overfull by one, plus a full quiver, plus every body slot.
     *
     * <p>The quiver is filled in two stages, because an inscription outranks the ordering. First
     * every object carrying an {@code @vN} inscription is offered its named slot, taken from
     * {@link ObjectUtils#preferredQuiverSlot(Player, ItemObject)}, and gets it if it is empty.
     * Then the slots still empty are filled in index order, each taking the earliest remaining ammunition by
     * {@link ItemObject#earlierObject}. The pack is filled the same way afterwards from whatever is
     * left, and its loop deliberately runs to {@code pack_size} inclusive: that extra slot is where
     * an overfull pack shows, and is why {@code upkeep.inventory} is one longer than the pack size.
     *
     * <p>A stack too large for one quiver slot is split, with the remainder appended to the gear by
     * {@link ObjectUtils#gearInsertEnd} to be picked up by the pack pass. Thrown weapons count
     * {@code thrown_quiver_mult} against the slot, so their limit is reached sooner than
     * ammunition's. Splitting is refused once {@code numStackSplit} would pass
     * {@code numPackRemaining}, the free pack slots measured before any of this ran: overfilling
     * the pack by one slot is tolerated, by more is not, and the object simply stays unassigned
     * rather than forcing the pack past that. {@code Player#invenCanStackPartial} declines to make
     * quiver-bound partial stacks for the same reason - a stack combined there would only be split
     * again here.
     *
     * <p>C's asserts on those invariants are thrown as {@link RuntimeException}s: an oversized gear
     * list, a split that would not shrink the stack, and a split attempted with no room for it.
     * They are all "cannot happen" guards on the caller's arithmetic, not conditions to recover
     * from.
     *
     * <p>Both views are compared against the copies taken before the rebuild, and a message is
     * given if anything moved. The pack comparison is skipped unless the count is unchanged, since
     * a pack that gained or lost an object has re-arranged itself for a reason the player can
     * already see, and an object that moved out to the equipment does not count as a re-arrangement
     * either. Neither message is given before the dungeon exists, which is character creation
     * stocking the pack.
     *
     * <p>Function calcInventory stubbed on 260822, coded on 260826, commented in full on 260827.
     *
     * @param player the character whose pack and quiver are re-sorted
     */
    public static void calcInventory(Player player) {
        int oldInventoryCount = player.getPlayerUpkeep().getInventoryCount();
        int numStackSplit = 0;
        int numPackRemaining = GameConstants.getCarryCapPackSize() - ObjectUtils.packSlotsUsed(player);
        int numMax = 1 + GameConstants.getCarryCapPackSize() + GameConstants.getCarryCapQuiverSize() + player.getPlayerBody().getCount();
        ItemObject[] oldQuiver = new ItemObject[GameConstants.getCarryCapQuiverSize()];
        ItemObject[] oldPack = new ItemObject[GameConstants.getCarryCapPackSize()];
        List<Boolean> assigned = new ArrayList<>();

        // Start with the equipped items - this step is vital
        int count = 0;
        for (ItemObject current : player.getGear()) {
            count++;
            if (count > numMax) {
                String message = "Number of equipped items greater than total number of items allowed.";
                logger.error(message);
                throw new RuntimeException(message);
            }
            assigned.add(player.getPlayerBody().itemIsEquipped(current));
        }
        // Now the rest of the gear slots
        for (int index = count; index < numMax; index++) {
            assigned.add(false);
        }

        // Preparation for filling of the quiver
        player.getPlayerUpkeep().setQuiverCount(0);

        // Save the state of the quiver and clear it down
        int index = 0;
        for (ItemObject quiverItem : player.getPlayerUpkeep().getQuiver()) {
            oldQuiver[index] = quiverItem;
            player.getPlayerUpkeep().getQuiver()[index] = null;
            index++;
        }

        // Fill the quiver - allocate inscribed items first
        for (index = 0; index < player.getGear().size(); index++) {
            ItemObject quiverCurrent = player.getGear().get(index);
            if (assigned.get(index)) continue; // skip already assigned (equipped) items

            int preferredSlot = ObjectUtils.preferredQuiverSlot(player, quiverCurrent);
            if (preferredSlot >= 0 && preferredSlot < GameConstants.getCarryCapQuiverSize()
                    && player.getPlayerUpkeep().getQuiver()[preferredSlot] == null) {
                // Split the stack if required - don't allow splitting if it
                // will result in overfilling the pack by more than one
                // slot.
                int mult = quiverCurrent.gettValue().isAmmo() ? 1 : GameConstants.getCarryCapThrownQuiverMult();
                ItemObject toQuiver;

                if (quiverCurrent.getNumber() * mult <= GameConstants.getCarryCapQuiverSlotSize()) {
                    toQuiver = quiverCurrent;
                } else {
                    int numSplit = GameConstants.getCarryCapQuiverSlotSize() / mult;

                    if (numSplit >= quiverCurrent.getNumber()) {
                        String message = "Number of slots in the quiver required for the number of items in the stack " +
                                "is too many.";
                        logger.error(message);
                        throw new RuntimeException(message);
                    }
                    if (numSplit > 0 && numStackSplit <= numPackRemaining) {
                        // Split off the portion that goes into the pack.
                        toQuiver = quiverCurrent;
                        ObjectUtils.gearInsertEnd(player, quiverCurrent.objectSplit(quiverCurrent.getNumber() - numSplit));
                        numStackSplit++;
                    } else {
                        toQuiver = null;
                    }
                }

                if (toQuiver != null) {
                    player.getPlayerUpkeep().getQuiver()[preferredSlot] = toQuiver;
                    player.getPlayerUpkeep().setQuiverCount(player.getPlayerUpkeep().getQuiverCount() + toQuiver.getNumber() * mult);

                    // Mark that item done
                    assigned.set(index, true);
                }
            }
        }

        // Now the rest of the slots in order
        for (int quiverIndex = 0; quiverIndex < GameConstants.getCarryCapQuiverSize(); quiverIndex++) {
            ItemObject first = null;
            int firstIndex = -1;

            // skip over full slots
            if (player.getPlayerUpkeep().getQuiver()[quiverIndex] != null) continue;

            // Find the quiver object that should go there.
            // At this point we are at the first empty quiver slot
            int gearIndex = -1;
            ItemObject current;

            while (true) {
                gearIndex++;
                if (gearIndex >= player.getGear().size()) break;

                current = player.getGear().get(gearIndex);

                // Only try to assign if not already assigned, ammo and, if necessary to split
                // have room for the split stacks.
                if (!assigned.get(gearIndex) && current.gettValue().isAmmo()
                        && (current.getNumber() <= GameConstants.getCarryCapQuiverSlotSize()
                        || (GameConstants.getCarryCapQuiverSlotSize() > 0
                        && numStackSplit <= numPackRemaining))) {
                    // Get the first in order
                    if (ItemObject.earlierObject(first, current, false)) {
                        first = current;
                        firstIndex = gearIndex;
                    }
                }
            }

            // Stop looking if there is nothing left in the gear
            if (first == null) break;

            // Put the item in the slot, splitting if needed
            if (first.getNumber() > GameConstants.getCarryCapQuiverSlotSize()) {
                if (GameConstants.getCarryCapQuiverSlotSize() <= 0 || numStackSplit > numPackRemaining) {
                    String message = "Invalid numStackSplit: " + numStackSplit
                            + " & numPackRemaining: " + numPackRemaining + " values.";
                    logger.error(message);
                    throw new RuntimeException(message);
                }
                ObjectUtils.gearInsertEnd(player, first.objectSplit(first.getNumber() - GameConstants.getCarryCapQuiverSlotSize()));
            }
            player.getPlayerUpkeep().getQuiver()[quiverIndex] = first;
            player.getPlayerUpkeep().setQuiverCount(player.getPlayerUpkeep().getQuiverCount() + first.getNumber());

            // Mark that item as assigned
            assigned.set(firstIndex, true);
        }

        // Note reordering
        if (GameWorld.hasCharacterDungeon()) {
            for (int quiverIndex = 0; quiverIndex < player.getPlayerUpkeep().getQuiver().length; quiverIndex++) {
                ItemObject oldQuiverItem = oldQuiver[quiverIndex];
                ItemObject newQuiverItem = player.getPlayerUpkeep().getQuiver()[quiverIndex];
                if (oldQuiverItem != null && oldQuiverItem != newQuiverItem) {
                    Message.message("You re-arrange your quiver.");
                    break;
                }
            }
        }

        // (Shallow?) copy the current pack
        ItemObject[] inventory = player.getPlayerUpkeep().getInventory();
        System.arraycopy(inventory, 0, oldPack, 0, GameConstants.getCarryCapPackSize());

        // Prepare to fill the inventory
        player.getPlayerUpkeep().setInventoryCount(0);

        for (int equipIndex = 0; equipIndex <= GameConstants.getCarryCapPackSize(); equipIndex++) {
            ItemObject first = null;
            int firstIndex = -1;

            // Find the object that should go there
            for (int gearIndex = 0; gearIndex < player.getGear().size(); gearIndex++) { // Changes numMax to getGear().size()
                ItemObject current = player.getGear().get(gearIndex);

                // Consider if it if hasn't already been handled
                if (!assigned.get(gearIndex)) {
                    if (ItemObject.earlierObject(first, current, false)) {
                        first = current;
                        firstIndex = gearIndex;
                    }
                }
            }

            // Allocate
            player.getPlayerUpkeep().getInventory()[equipIndex] = first;
            if (first != null) {
                player.getPlayerUpkeep().setInventoryCount(player.getPlayerUpkeep().getInventoryCount() + 1);
                assigned.set(firstIndex, true);
            }
        }

        // Note reordering
        if (GameWorld.hasCharacterDungeon() && player.getPlayerUpkeep().getInventoryCount() == oldInventoryCount) {
            for (int checkIndex = 0; checkIndex < oldInventoryCount; checkIndex++) {
                if (oldPack[checkIndex] != null
                        && oldPack[checkIndex] != player.getPlayerUpkeep().getInventory()[checkIndex]
                        && !player.getPlayerBody().itemIsEquipped(oldPack[checkIndex])) {
                    Message.message("You re-arrange your pack.");
                    break;
                }
            }
        }
    }

    /**
     * Recalculates which spells the player may cast and how many they may learn - the port of C's
     * {@code calc_spells} ({@code player-calcs.c}).
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the magic subsystem. C's version announces newly
     * learnable spells, forgets spells the player no longer has the levels for, and raises the
     * spell-related redraws; none of that happens yet.
     *
     * <p>Function calcSpells commented in full on 260827.
     */
    public static void calcSpells() {
        // Stub class. TODO: Implement
    }

    /**
     * The four running totals {@code calcBonuses} accumulates across the equipment walk and then
     * hands to {@code calcShapechange} to add to — extra blows, shots, shooting might and movement
     * actions.
     *
     * <p>Exists only because of a difference in how the two languages pass things. C declares four
     * {@code int}s and passes their addresses ({@code player-calcs.c:2030-2031}), so
     * {@code calc_shapechange} adds into the caller's own storage. The port cannot take an address,
     * so the four travel together as a value in and a value out, and the caller assigns the result
     * back over its locals.
     *
     * @param blows extra blows per turn, unscaled
     * @param shots extra shots per turn, unscaled
     * @param might extra shooting multiplier
     * @param moves extra movement actions per turn
     */
    public record Extras(int blows, int shots, int might, int moves) {
    }
}
