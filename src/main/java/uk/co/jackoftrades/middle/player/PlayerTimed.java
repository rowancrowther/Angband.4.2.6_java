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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.event.projection.Source;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.List;

/**
 * The timed-effect machinery - the port of the effect-handling half of C's {@code player-timed.c}.
 * Everything that changes how long the player is blessed, poisoned, stunned, hasted or afraid comes
 * through here.
 *
 * <p>The turn counts themselves live on {@link Player}, in the map behind
 * {@link Player#getTimedEffect}, and the definitions loaded from {@code player_timed.txt} live in
 * {@link PlayerRegistry} as {@link PlayerTimedEffect} objects. This
 * class is the behaviour between the two: it reads a definition, decides what the new count should
 * be, writes it, and fires whatever the change is supposed to fire. C splits the same work between
 * the parser at the top of {@code player-timed.c} and the functions below it; the port keeps only the
 * functions, because the parsing is already done by the time anything here runs.
 *
 * <p><b>Everything funnels into {@link #setTimed}.</b> That is where the message, the disturb, the
 * redraw flags and the update flags happen, and it is the only method here that touches any of them.
 * {@link #playerIncTimed}, {@link #playerDecTimed} and {@link #playerClearTimed} each work out a new
 * total and hand it over, exactly as C's {@code player_inc_timed}, {@code player_dec_timed} and
 * {@code player_clear_timed} all end in {@code player_set_timed}. The consequence is that a caller who
 * writes the map directly - {@link Player#putTimed} - deliberately gets none of it, which is what the
 * handful of C sites that assign {@code p->timed[idx]} outright are relying on.
 *
 * <p><b>The methods are static and take the player.</b> C reaches these through a {@code struct
 * player *p} argument for the same reason: a timed effect is a property of a character, not of the
 * effect system, and there is no state here to hold. The class is a namespace.
 *
 * <p><b>Three of the methods guard against a table C could not have.</b> {@code p->timed} is a fixed
 * array of {@code TMD_MAX} slots embedded in the player struct, so in C it is always there and every
 * effect always has a slot. The port holds a map, so {@link #incCheck}, {@link #timedGradeEq} and
 * {@link #playerIncTimed} stack {@link Player#playerHasTimed} then
 * {@link Player#playerTimedContains} then a {@code != 0} test where C wrote a single subscript. Only
 * the last of those three is the real question - is the effect running - and a hand-built test
 * character is the only thing the first two ever catch.
 *
 * <p><b>{@link #clearTimed} and {@link #incTimed} are not these methods.</b> They are the older
 * stubs, without a {@link Player} parameter, still called from
 * {@link uk.co.jackoftrades.middle.game.GameWorld}; they report no change and do nothing. The
 * implemented routes are the {@code player}-prefixed ones.
 *
 * <p>Class PlayerTimed commented in full on 260901.
 *
 * @author Rowan Crowther
 */
public class PlayerTimed {
    private final static Logger logger = LogManager.getLogger(PlayerTimed.class);

    /**
     * Clears a timed effect, ending it early - the port of C's {@code player_clear_timed}.
     *
     * <p><b>Stub: superseded, not implemented.</b> It takes no {@link Player} and so cannot reach a
     * timed-effect table at all; it returns {@code false} without doing anything. The working route is
     * {@link #playerClearTimed}, and the two remaining callers - {@code GameWorld:553} and
     * {@code GameWorld:998}, both clearing {@code TMD_COMMAND} - are what keeps this signature alive.
     * Both should move across, after which the method and its tripwire tests in
     * {@code PlayerProgressionTest} can go.
     *
     * @param timedEffect the effect to clear
     * @param notify      whether to announce the effect ending to the player
     * @param canDisturb  whether clearing it may interrupt resting/running
     * @return {@code true} if the effect was active and has now been cleared
     */
    public static boolean clearTimed(TimedEffect timedEffect, boolean notify, boolean canDisturb) {
        // Stub class TODO: implement
        return false;
    }

    /**
     * Returns how many turns remain on a timed effect, or a caller-chosen figure if the effect has no
     * entry at all - the port of reading C's {@code p->timed[idx]} where the caller wants to name the
     * value an absent slot stands for.
     *
     * <p>C's {@code timed} is an array with a slot for every effect, and the port populates a map with
     * a zero for every {@link TimedEffect} at construction, so in a live character no key is ever
     * missing and the default is never reached. It is what a partially-built character - a test
     * fixture that writes the map itself - reads instead of throwing. Passing {@code 0}, as every
     * caller in {@link PlayerCalcs} does, makes this exactly {@link Player#getTimedEffect}.
     *
     * <p>The value is a turn count, and callers test it against zero rather than for presence: an
     * effect that is not running is a zero in its slot, not a missing one.
     *
     * <p>The presence check is {@link Player#playerTimedContains} alone, without the
     * {@link Player#playerHasTimed} guard that {@link #incCheck} and {@link #timedGradeEq} put in
     * front of it, so a character with no table at all reaches this and throws rather than answering
     * {@code defaultValue}. That is the same character C cannot construct, and no caller passes one.
     *
     * <p>Function getTimedEffectOrDefault coded on 260901, commented in full on 260901.
     *
     * @param player       the character whose timed-effect table is read; C's {@code struct player *p}
     * @param timedEffect  the timed effect to query
     * @param defaultValue the figure to answer with if the effect has no entry
     * @return the turns remaining on the effect, or {@code defaultValue} if it has no entry
     */
    public static int getTimedEffectOrDefault(Player player, @NotNull TimedEffect timedEffect, int defaultValue) {
        if (player.playerTimedContains(timedEffect)) {
            return player.getTimedEffect(timedEffect);
        }
        return defaultValue;
    }

    /**
     * Extend (or begin) a timed effect by a given amount, delegating to {@link #setTimed} with the
     * new total. The port of C's {@code player_inc_timed} ({@code player-timed.c}).
     *
     * <p><b>Stub: superseded, not implemented.</b> Like {@link #clearTimed} it takes no
     * {@link Player}, so it cannot reach a timed-effect table; it returns {@code false} without doing
     * anything. The working route is {@link #playerIncTimed}, and the one remaining caller -
     * {@code GameWorld:841}, paralysing the player - is what keeps this signature alive.
     *
     * @param timedEffect the effect to lengthen
     * @param amount      the number of turns to add
     * @param notify      whether to announce the change to the player
     * @param canDisturb  whether the change may interrupt resting/running
     * @param check       whether to honour the effect's failure conditions before applying it
     * @return {@code true} if the effect's value actually changed
     */
    public static boolean incTimed(TimedEffect timedEffect, int amount, boolean notify, boolean canDisturb, boolean check) {
        // Stub function TODO: implement
        return false;
    }

    /**
     * Decides whether a timed effect is allowed to take hold, by walking the failure conditions
     * declared for it in {@code player_timed.txt} - the port of C's {@code player_inc_check}
     * ({@code player-timed.c}).
     *
     * <p>Each condition is a veto: the first one that holds answers {@code false} and the walk stops
     * there. Only an effect that survives every condition answers {@code true}, so an effect with no
     * declared conditions always passes.
     *
     * <p><b>What {@code lore} selects.</b> A lore check asks what the player <em>believes</em> would
     * stop the effect, and so reads {@code Player#knownState} and nothing else; it is a query, and leaves
     * the character untouched. The live check reads the calculated state from
     * {@link Player#getPlayerState()} instead, and learning is part of its job: being subjected to an
     * effect that one's equipment turns aside is how that equipment's property gets identified, so
     * the non-lore branches call {@link PlayerKnowledge#equipLearnFlag} and {@link PlayerKnowledge#equipLearnElement} before
     * testing. The two branches are alternatives, never a sequence - a lore check that fell through
     * to the live test would both answer the wrong question and identify equipment the player never
     * used.
     *
     * <p><b>The monster boundary in the object-flag case.</b> When the effect arrives from a
     * monster's action the cave names the actor in {@code monCurrent}, and two further things
     * happen: the monster observes the player's property through {@link Monster#updateSmartLearn},
     * and a successful resist is announced. Both are conditional on there being an actor - an effect
     * from a trap or a potion is learned from silently. C passes {@code 0} and {@code -1} for the
     * player flag and element it is not reporting; the port spells those {@link PlayerFlag#PF_NONE}
     * and {@link ElementEnum#ELEM_NONE}.
     *
     * <p><b>Resistance and vulnerability differ only in sign.</b> A resist vetoes at
     * {@code resLevel > 0} and a vulnerability at {@code resLevel < 0}; both learn from equipment on
     * the live path. C carries a note that the pair reading asymmetrically is accepted for now.
     *
     * <p><b>Why the timed-effect case ignores {@code lore}.</b> A timed effect that is running shows
     * on the player's status line, so there is nothing for them to be ignorant of and no second
     * branch to write. The test is on the counter being non-zero, not on the entry existing:
     * {@code Player.timed} is populated with a zero for every effect at construction, so a presence test
     * would hold always and veto the effect unconditionally.
     *
     * <p>C asserts that each condition's index lies in range for its category. The port needs no
     * equivalent: {@link TimedFailure} keeps a separately-typed payload per category and its
     * accessors refuse to hand back the wrong one, so a malformed condition fails at the accessor
     * rather than indexing past an array. The unreachable {@code TYPE_NONE} answers C's
     * {@code assert(0)} with a logged throw.
     *
     * <p>Function incCheck coded on 260831, commented in full on 260831.
     *
     * @param player the character the conditions are tested against - their state, equipment,
     *               knowledge and running effects; C's {@code struct player *p}
     * @param index  the timed effect whose failure conditions are to be tested
     * @param lore   {@code true} to test only against what the player already knows, learning
     *               nothing from equipment; {@code false} for the live check
     * @return {@code true} if nothing prevents the effect from being increased
     */
    public static boolean incCheck(Player player, TimedEffect index, boolean lore) {
        PlayerTimedEffect effect = PlayerRegistry.lookupPlayerTimedEffect(index);
        List<TimedFailure> failures = effect.getFail();

        for (TimedFailure failure : failures) {
            switch (failure.getCode()) {
                case TYPE_OBJECT_FLAG -> {
                    if (lore) {
                        if (player.getKnownState().hasOFlag(failure.getObjFlagCode()))
                            return false;
                    } else {
                        // If the effect is from a monster action, extra stuff happens
                        Monster mon = player.getCave().getMonCurrent() > 0 ? player.getCave().caveMonster(player.getCave().getMonCurrent())
                                : null;

                        PlayerKnowledge.equipLearnFlag(player, failure.getObjFlagCode());
                        if (mon != null) {
                            mon.updateSmartLearn(player, failure.getObjFlagCode(), PlayerFlag.PF_NONE,
                                    ElementEnum.ELEM_NONE);
                        }
                        if (player.hasObjectFlag(failure.getObjFlagCode())) {
                            if (mon != null) {
                                Message.message("You resist the effect!");
                            }
                            return false;
                        }
                    }
                }
                case TYPE_RESIST -> {
                    if (lore) {
                        // Effect is inhibited by a resist
                        if (player.getKnownState().getElInfo().get(failure.getElementCode()).getResLevel() > 0) {
                            return false;
                        }
                    } else {
                        PlayerKnowledge.equipLearnElement(player, failure.getElementCode());
                        if (player.getPlayerState().getElInfo().get(failure.getElementCode()).getResLevel() > 0) {
                            return false;
                        }
                    }
                }
                case TYPE_VULN -> {
                    // Effect is inhibited by a vulnerability
                    if (lore) {
                        if (player.getKnownState().getElInfo().get(failure.getElementCode()).getResLevel() < 0) {
                            return false;
                        }
                    } else {
                        PlayerKnowledge.equipLearnElement(player, failure.getElementCode());
                        if (player.getPlayerState().getElInfo().get(failure.getElementCode()).getResLevel() < 0) {
                            return false;
                        }
                    }
                }
                case TYPE_PLAYER_FLAG -> {
                    // Effect is inhibited by a player flag
                    if (lore) {
                        if (player.getKnownState().hasPFlag(failure.getPlayerFlagCode())) {
                            return false;
                        }
                    } else {
                        if (player.hasPlayerFlag(failure.getPlayerFlagCode())) {
                            return false;
                        }
                    }
                }
                case TYPE_TIMED_EFFECT -> {
                    /*
                     * Effect is inhibited by a timed effect.  If timed
                     * effect is active, it is known to the player, so
                     * there's no difference between whether this is
                     * solely a lore check or not.
                     */
                    TimedEffect e = failure.getEffectCode();
                    if (player.playerHasTimed() && player.playerTimedContains(e) && player.getTimedEffect(e) != 0) {
                        return false;
                    }
                }
                case TYPE_NONE -> {
                    // should never happen
                    String message = "Error: Failure.code.TYPE_NONE reached in Player.incCheck. " +
                            "Should not have occured";
                    logger.error(message);
                    throw new RuntimeException(message);
                }
            }
        }

        return true;
    }

    /**
     * Sets a timed effect to a given value, announcing and applying the consequences of the change -
     * the port of C's {@code player_set_timed} ({@code player-timed.c}).
     *
     * <p>The requested value is coerced into the effect's legal range: first raised to the effect's
     * lower bound, then, if it still exceeds the top grade's maximum, capped there. Both bounds are
     * applied around an early exit, so the "no change" test runs against the raw (lower-bounded)
     * value while the second exit catches the case of asking to exceed the top of the scale when the
     * player is already pinned to it. Either exit returns {@code false} without touching the effect.
     *
     * <p>Grades are the effect's named bands, held in ascending order with an implicit "off" grade of
     * maximum {@code 0} at the head. C walks a linked list and compares the {@code grade} numbers;
     * because the assembler numbers them sequentially from that head, the list index carries the same
     * ordering and is compared directly here. Both walks stop at the last grade rather than running
     * off the end, which is what makes the upper-bound test above meaningful.
     *
     * <p>Messages follow C's precedence exactly. Moving up a grade always speaks, and forces
     * {@code notify}; moving down speaks only if the grade being entered carries a down message, and
     * then also forces {@code notify}. Failing both, a caller-requested {@code notify} produces the
     * effect's end, decrease or increase message according to the direction of travel, with a missing
     * message simply printing nothing. Before any of that, {@code notify} is suppressed for a change
     * the player could not perceive: one that duplicates an element they already know themselves
     * immune to, or an object flag they already know they carry, since the status would tell them
     * nothing new.
     *
     * <p>C passes a possibly-null object to {@code print_custom_message} and prints "hands" in place
     * of its name. Java has no null receiver, so the equipped weapon prints the message when there is
     * one and a placeholder object prints it with the bare-hands switch set when there is not.
     *
     * <p>Begin and end effect chains fire on the transitions into and out of zero, before the new
     * value is stored. C's choice of origin looks inverted and is not: passing {@code sourceNone} when
     * the change may disturb lets any nested timed-effect handler make its own disturbance decision,
     * while {@code sourcePlayer} marks the change as self-inflicted and suppresses it.
     *
     * <p>Only a notifying change disturbs the player, raises the effect's update and redraw flags
     * (always including {@code PR_STATUS}) and calls {@link PlayerCalcs#handleStuff(Player)}; a silent change stores
     * the value and stops.
     *
     * <p><b>Outstanding:</b> {@code Effect.effectDo} and {@link PlayerUtils#disturb()} are still
     * stubs, so the transition chains and the disturbance are stored-up work rather than observable
     * behaviour.
     *
     * <p>Function setTimed coded on 260829, commented in full on 260829.
     *
     * @param player      the character whose effect is written, and who receives any message,
     *                    disturb, redraw and update the change calls for; C's {@code struct player *p}
     * @param timedEffect the effect to set; must be one that the registry knows
     * @param amount      the requested new value, before the lower and upper bounds are applied
     * @param notify      whether the caller wants an ordinary change announced; a grade change
     *                    overrides this upwards, a duplicated known effect overrides it downwards
     * @param canDisturb  whether a notifying change may interrupt resting or running
     * @return {@code true} if the player was notified, which is C's return value - not whether the
     * stored value changed
     * @throws IllegalArgumentException if the registry holds no effect of that name
     */
    public static boolean setTimed(Player player, TimedEffect timedEffect, int amount, boolean notify, boolean canDisturb) {
        List<PlayerTimedEffect> timedEffects = PlayerRegistry.getPlayerTimedEffects();

        // Get timed_effects[idx] into effect
        PlayerTimedEffect effect = null;
        for (PlayerTimedEffect playerTimedEffect : timedEffects) {
            if (playerTimedEffect.getName() == timedEffect) {
                effect = playerTimedEffect;
                break;
            }
        }
        if (effect == null) {
            logger.error("Passed in timed effect that doesn't exist in PlayerTimedEffects: " + timedEffect.name());
            throw new IllegalArgumentException("Passed in timed effect that doesn't exist in PlayerTimedEffects: " + timedEffect.name());
        }

        List<TimedGrade> grade = effect.getGrade();
        int newGradeIndex = 0;
        int currentGradeIndex = 0;
        ItemObject weapon = player.getPlayerBody().equippedItemBySlotName("weapon");

        // lowerBound
        amount = Math.max(amount, effect.getLowerBound());

        // no change
        if (player.getTimedEffect(timedEffect) == amount) return false;

        // Find the new grade we will be going to, and the current one
        while (amount > grade.get(newGradeIndex).max()) {
            newGradeIndex++;
            if (newGradeIndex >= grade.size() - 1) break;
        }
        while (player.getTimedEffect(timedEffect) > grade.get(currentGradeIndex).max()) {
            currentGradeIndex++;
            if (currentGradeIndex >= grade.size() - 1) break;
        }


        // Upper bound
        if (amount > grade.get(newGradeIndex).max()) {
            if (player.getTimedEffect(timedEffect) == grade.get(newGradeIndex).max()) {
                // No change - tried to exceed maximum position and already there
                return false;
            }
            amount = grade.get(newGradeIndex).max();
        }

        // Don't mention effects which already match the player known state.
        if (effect.getTempResist() != ElementEnum.ELEM_NONE
                && player.itemKnowledge.getElementResistInfo().get(effect.getTempResist())
                && PlayerUtils.playerIsImmune(player, effect.getTempResist())) {
            notify = false;
        }
        if (effect.isoFlagExactlySyn() && effect.getoFlagDup() != ObjectFlag.OF_NONE
                && player.itemKnowledge.flagIsKnown(effect.getoFlagDup())
                && PlayerUtils.playerOfHasNotTimed(player, effect.getoFlagDup())) {
            notify = false;
        }

        ItemObject newObj = new ItemObject();

        // Always mention going up a grade
        if (newGradeIndex > currentGradeIndex) {
            if (weapon == null)
                newObj.printCustomMessage(grade.get(newGradeIndex).upMsg(), effect.getMsgT(), player, true);
            else
                weapon.printCustomMessage(grade.get(newGradeIndex).upMsg(), effect.getMsgT(), player, false);
            notify = true;
        } else if (newGradeIndex < currentGradeIndex
                && grade.get(newGradeIndex).downMsg() != null) {
            if (weapon == null)
                newObj.printCustomMessage(grade.get(newGradeIndex).downMsg(), effect.getMsgT(), player, true);
            else weapon.printCustomMessage(grade.get(newGradeIndex).downMsg(), effect.getMsgT(), player, false);
            notify = true;
        } else if (notify) {
            if (amount == 0) {
                if (weapon == null)
                    newObj.printCustomMessage(effect.getOnEnd(), MessageType.MSG_RECOVER, player, true);
                else
                    weapon.printCustomMessage(effect.getOnEnd(), MessageType.MSG_RECOVER, player, false);
            } else if (player.getTimedEffect(timedEffect) > amount && effect.getOnDecrease() != null) {
                if (weapon == null)
                    newObj.printCustomMessage(effect.getOnDecrease(), effect.getMsgT(), player, true);
                else
                    weapon.printCustomMessage(effect.getOnDecrease(), effect.getMsgT(), player, false);
            } else if (player.getTimedEffect(timedEffect) < amount && effect.getOnIncrease() != null) {
                if (weapon == null)
                    newObj.printCustomMessage(effect.getOnIncrease(), effect.getMsgT(), player, true);
                else
                    weapon.printCustomMessage(effect.getOnIncrease(), effect.getMsgT(), player, false);
            }
        }

        // Dispatch effects for transitions
        if (amount > 0 && player.getTimedEffect(timedEffect) == 0) {
            // effect starts
            if (effect.getOnBeginEffect() != null) {
                boolean identity = false;

                Source source = canDisturb ? Source.sourceNone() : Source.sourcePlayer();
                effect.getOnBeginEffect().effectDo(source, null, identity, true,
                        DirectionEnum.DIR_UNKNOWN, 0, 0, null);
            }
        } else if (amount == 0) {
            if (effect.getOnEndEffect() != null) {
                boolean identity = false;
                Source source = canDisturb ? Source.sourceNone() : Source.sourcePlayer();
                effect.getOnEndEffect().effectDo(source, null, identity, true,
                        DirectionEnum.DIR_UNKNOWN, 0, 0, null);
            }
        }

        player.putTimed(timedEffect, amount);

        if (notify) {
            if (canDisturb) {
                PlayerUtils.disturb();
            }

            for (PlayerUpdateEnum flag : effect.getFlagUpdate())
                player.getPlayerUpkeep().setUpdateFlagOn(flag);

            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_STATUS);
            for (PlayerRedraw flag : effect.getFlagRedraw())
                player.getPlayerUpkeep().setRedrawFlagsOn(flag);

            PlayerCalcs.handleStuff(player);
        }

        return notify;
    }

    /**
     * Reports whether an active timed effect is currently at the grade of the given name — the
     * port of C's {@code player_timed_grade_eq} ({@code player-timed.c:734}).
     *
     * <p>A timed effect is a single counter, but the player-facing status is a band of that
     * counter: stunning runs "Stun" → "Heavy Stun" → "Knocked Out" as the number climbs. This
     * answers which band the counter is in, by name, so that callers can branch on severity
     * without knowing the thresholds — {@code GameWorld.decreaseTimeouts} asks whether a wound is
     * a "Mortal Wound" before deciding it does not bleed down, and the digestion code asks whether
     * nourishment is "Full" or "Faint".
     *
     * <p><b>Exactly one grade is tested.</b> The grades are ordered by ascending {@code max}, and
     * the effect's band is the first one whose {@code max} the value does not exceed; that grade's
     * name is compared and the answer returned whether it matched or not. Continuing past it into
     * the higher grades would be the easy mistake, because their maxima also cover the value — a
     * lightly stunned player would answer {@code true} to "Knocked Out". C expresses this as a
     * {@code while} that walks to the band and then a single {@code streq} outside the loop.
     *
     * <p>An effect at zero answers {@code false} without consulting its grades, matching C's
     * opening {@code if (p->timed[idx])}. The check is needed rather than incidental: the map is
     * populated with a zero for every effect at construction, and the port's grade list has no
     * entry for the dormant state, so a zero reaching the loop would be tested against the first
     * real grade.
     *
     * <p>The null definition guard has no counterpart in C, which indexes a static table that is
     * always populated. Here the effects are loaded from {@code player_timed.txt} into
     * {@link PlayerRegistry}, and {@link PlayerRegistry#lookupPlayerTimedEffect} answers null for
     * an effect with no loaded definition — {@link TimedEffect#TMD_NONE} being the standing
     * example, though its zero value means it never reaches this far.
     *
     * <p>Function timedGradeEq coded on 260818, commented in full on 260818.
     *
     * @param player the character whose current count for the effect decides the grade; C's
     *               {@code struct player *p}
     * @param index  the timed effect to inspect
     * @param match  the grade name to compare against, as written in {@code player_timed.txt}
     * @return {@code true} if the effect is running and its current grade has that name
     */
    public static boolean timedGradeEq(Player player, TimedEffect index, String match) {
        if (player.playerHasTimed() && player.playerTimedContains(index) && player.getTimedEffect(index) != 0) {
            int value = player.getTimedEffect(index);
            PlayerTimedEffect effect = PlayerRegistry.lookupPlayerTimedEffect(index);
            if (effect == null) return false;

            List<TimedGrade> grades = effect.getGrade();

            for (TimedGrade grade : grades) {
                if (grade.max() < value)
                    continue;

                return (grade.status() != null && grade.status().equals(match));
            }
        }

        return false;
    }

    /**
     * Adds {@code amount} to the current duration of a timed effect - the port of C's
     * {@code player_inc_timed} ({@code player-timed.c:1053}).
     *
     * <p>Three gates stand between the request and the change. The first is the caller's
     * {@code check} flag: when it is set, {@link PlayerTimed#incCheck} is asked whether anything the player
     * carries or is already under prevents the effect, and a veto ends the call at once. When it is
     * clear the question is never asked, so none of the learning {@code incCheck} does on the way
     * past happens either. The second gate is the effect's own non-stacking property: an effect
     * marked {@code NONSTACKING} that is already running refuses a further increase outright rather
     * than extending itself. The third is {@link PlayerTimed#setTimed}, which does the real work and decides
     * everything about announcement, grades and upkeep.
     *
     * <p>The new duration is the current value plus {@code amount}, computed here and handed to
     * {@code setTimed} as an absolute value. Nothing clamps it at this level: a negative
     * {@code amount} is a legitimate way to shorten an effect, and the lower and upper bounds are
     * {@code setTimed}'s business. C computes {@code p->timed[idx] + v} in the same place and for
     * the same reason.
     *
     * <p>The return value is {@code setTimed}'s, and so means what that method's means: whether the
     * player was notified, not whether the stored duration moved. Both refusals - a failed check and
     * a blocked non-stacking increase - answer {@code false}, which is indistinguishable from a
     * silent change that did happen. Callers in C that care about resistance test the return anyway
     * ({@code mon-blows.c:548}), which is C's own looseness rather than something the port tightens.
     *
     * <p>C asserts the index is in range, since it is about to subscript {@code timed_effects}; a
     * {@link TimedEffect} makes an out-of-range index unrepresentable, so the assertions have no
     * analogue here. C then reads {@code timed_effects[idx].flags} directly, where the port walks
     * the registry's list for the definition of that name. An effect the registry has no definition
     * for leaves the local null, and the non-stacking question is simply not asked - the call falls
     * through to {@code setTimed}, which raises {@code IllegalArgumentException} on the same missing
     * definition. That is the same class of programming error C's assert catches, reported one call
     * deeper.
     *
     * <p>The {@code timed.containsKey} test costs nothing and finds nothing: the constructor seeds
     * the map with every {@link TimedEffect} at zero, so the lookup below it is never null.
     *
     * <p>Function playerIncTimed coded on 260831, commented in full on 260831.
     *
     * @param player     the character whose effect is lengthened, passed on to {@link #incCheck} and
     *                   {@link #setTimed}; C's {@code struct player *p}
     * @param index      the effect to lengthen; must be one the registry knows
     * @param amount     how much to add to the current duration; may be negative
     * @param notify     whether the caller wants an ordinary change announced, passed on to
     *                   {@link PlayerTimed#setTimed} unaltered
     * @param canDisturb whether a notifying change may interrupt resting or running
     * @param check      whether the player is allowed to resist the effect, by way of
     *                   {@link PlayerTimed#incCheck}
     * @return {@code true} if the player was notified, which is C's return value - not whether the
     * effect actually grew
     */
    public static boolean playerIncTimed(Player player, TimedEffect index, int amount, boolean notify,
                                         boolean canDisturb, boolean check) {
        if (!check || incCheck(player, index, false)) {
            List<PlayerTimedEffect> timedEffects = PlayerRegistry.getPlayerTimedEffects();
            PlayerTimedEffect actualEffect = null;

            for (PlayerTimedEffect timedEffect : timedEffects) {
                if (timedEffect.getName() != index) continue;
                else {
                    actualEffect = timedEffect;
                    break;
                }
            }

            if (actualEffect != null && actualEffect.isNonStacking()
                    && player.playerTimedContains(index) && player.getTimedEffect(index) > 0) {
                // Block the increase if the effect is nonstacking and already active
                return false;
            } else {
                return setTimed(player, index, player.getTimedEffect(index) + amount, notify, canDisturb);
            }
        }

        return false;
    }

    /**
     * Subtracts {@code amount} from the current duration of a timed effect - the port of C's
     * {@code player_dec_timed} ({@code player-timed.c:1097}).
     *
     * <p>Almost all of the work belongs to {@link PlayerTimed#setTimed}: the new duration is worked out here
     * as an absolute value and handed over, and every decision about messages, grades, transition
     * effects and upkeep is made there. What this method contributes is one rule, and it is worth
     * stating plainly - an effect that is finishing always announces itself. If the subtraction
     * leaves anything behind, the caller's {@code notify} is passed on unaltered; if it leaves
     * nothing, {@code notify} is overridden to {@code true} so the "you feel yourself again"
     * message and the accompanying redraw cannot be suppressed. The turn-by-turn decay in
     * {@code game-world.c:348} relies on exactly this: it decrements every running effect by one
     * with {@code notify} false, silently, and gets told only about the tick on which an effect
     * actually lapses.
     *
     * <p>Nothing is clamped at this level. A subtraction that overshoots produces a negative value
     * and that negative value is what {@code setTimed} receives, where {@code Math.max} against the
     * effect's lower bound turns it into the floor. Passing the result through rather than a
     * pre-clamped zero matters because {@code setTimed} compares the incoming value against the
     * stored one to decide whether anything changed at all. C computes {@code p->timed[idx] - v}
     * and forwards it untouched for the same reason.
     *
     * <p>By symmetry with {@link #playerIncTimed} a negative {@code amount} is a legitimate way to
     * lengthen an effect, and takes the first branch as an ordinary change.
     *
     * <p>The return value is {@code setTimed}'s, and so means what that method's means: whether the
     * player was notified, not whether the stored duration moved. A decrement of an effect that was
     * already at zero answers {@code false}, since nothing changed.
     *
     * <p>C asserts the index is in range, since it is about to subscript {@code p->timed}; a
     * {@link TimedEffect} makes an out-of-range index unrepresentable, so the assertions have no
     * analogue here. The {@code getOrDefault} default costs nothing and is never used: the
     * constructor seeds the map with every {@link TimedEffect} at zero.
     *
     * <p>Function playerDecTimed coded on 260831, commented in full on 260831.
     *
     * @param player     the character whose effect is shortened, passed on to {@link #setTimed}; C's
     *                   {@code struct player *p}
     * @param index      the effect to shorten; must be one the registry knows
     * @param amount     how much to take off the current duration; may be negative, which lengthens
     *                   it
     * @param notify     whether the caller wants an ordinary change announced; ignored, and treated
     *                   as {@code true}, when the effect finishes
     * @param canDisturb whether a notifying change may interrupt resting or running
     * @return {@code true} if the player was notified, which is C's return value - not whether the
     * effect actually shrank
     */
    public static boolean playerDecTimed(Player player, TimedEffect index, int amount, boolean notify, boolean canDisturb) {
        int newValue = player.getTimedEffectOrDefault(index, 0) - amount;

        if (newValue > 0) {
            return setTimed(player, index, newValue, notify, canDisturb);
        }
        return setTimed(player, index, newValue, true, canDisturb);
    }

    /**
     * Cancels a timed effect outright, setting its duration to zero. Ports
     * {@code player_clear_timed} ({@code player-timed.c:1127}).
     *
     * <p>A one-line delegation to {@link PlayerTimed#setTimed}, which does everything: the lapse messages, the
     * grade transitions, the recalculation and redraw flags, and the decision about whether the
     * player is disturbed. All this method fixes is the destination, zero.
     *
     * <p>Note what it does <em>not</em> do. Unlike {@link #playerDecTimed}, which forces
     * {@code notify} to {@code true} whenever a subtraction takes an effect to its end, this method
     * passes the caller's {@code notify} through untouched. Clearing an effect silently is
     * therefore possible and is deliberately used: {@code game-world.c:1078} clears
     * {@code TMD_COMMAND} with {@code notify} false when a level ends, because the player is not to
     * be told about bookkeeping. The asymmetry is real and is in the C.
     *
     * <p>Zero is not a special value to {@code setTimed}; it is an ordinary target that happens to
     * be the floor for most effects. So the usual rules apply - an effect already at zero is
     * unchanged, and the method answers {@code false}. The return value is {@code setTimed}'s and
     * means what that method's means: whether the player was notified, not whether the duration
     * moved.
     *
     * <p>C asserts the index is in range, since it is about to subscript {@code p->timed}; a
     * {@link TimedEffect} makes an out-of-range index unrepresentable, so the assertions have no
     * analogue here.
     *
     * <p>Function playerClearTimed coded on 260831, commented in full on 260831.
     *
     * @param player     the character whose effect is cancelled, passed on to {@link #setTimed}; C's
     *                   {@code struct player *p}
     * @param index      the effect to cancel; must be one the registry knows
     * @param notify     whether the caller wants the change announced - passed straight through,
     *                   not forced true as it is in {@link #playerDecTimed}
     * @param canDisturb whether a notifying change may interrupt resting or running
     * @return {@code true} if the player was notified, which is C's return value - not whether the
     * effect was actually running
     */
    public static boolean playerClearTimed(Player player, TimedEffect index, boolean notify, boolean canDisturb) {
        return setTimed(player, index, 0, notify, canDisturb);
    }
}
