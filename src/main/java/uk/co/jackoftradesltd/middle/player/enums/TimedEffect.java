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

package uk.co.jackoftradesltd.middle.player.enums;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;

/**
 * The catalogue of timed player statuses — the {@code TMD_*} effects such as haste, fear,
 * poison, temporary resistances and the brand/attack buffs — each recording the screen
 * redraws and model recalculations its onset or expiry should trigger.
 *
 * <p>Ports the C {@code TMD_*} timed-effect table ({@code list-player-timed.h}, the {@code TMD()}
 * rows, with the enum built from them in {@code player-timed.h}). The numeric level of each status
 * is stored on the player; this enum captures the <em>static</em> per-effect metadata —
 * specifically which {@link PlayerRedraw} regions and which {@link PlayerUpdateEnum}
 * recalculations become dirty whenever the effect's value changes. The constants follow the rows
 * of {@code list-player-timed.h} in the same order, with the same flag sets.
 *
 * <p><b>Why carry the flag sets here:</b> when a timed status rises or falls the engine must
 * refresh exactly the affected display and derived state and nothing more (the dirty-region
 * discipline described on {@link PlayerRedraw} / {@link PlayerUpdateEnum}). Attaching those
 * flag sets to the effect keeps the mapping declarative and in one place. Most effects only
 * touch the status line and the bonus recalculation ({@code PR_STATUS} + {@code PU_BONUS});
 * the notable exceptions are sense-altering statuses — {@code TMD_BLIND} repaints the map and
 * recomputes the field of view and monster visibility, {@code TMD_IMAGE} repaints the map, the
 * monster list and the item list, and see-invisible / infravision additionally re-evaluate
 * monster visibility.
 *
 * <p><b>{@code PR_STATUS} is not always in the set.</b> The {@code TMD_BLIND} and
 * {@code TMD_IMAGE} rows, like their C counterparts, list no {@code PR_STATUS}. They still repaint
 * the status line, because C's {@code player_set_timed} ORs {@code PR_STATUS} into the redraw flags
 * of every notified change on top of the effect's own set, and {@code PlayerTimed.setTimed} does
 * the same. Anything reading these sets directly must add {@code PR_STATUS} itself.
 *
 * <p><b>Divergences from C's enum:</b> {@link #TMD_NONE} leads the enum and has no C row, so
 * every ordinal here is one more than C's {@code TMD_*} value; every lookup in the port is by
 * name, so nothing depends on the numbering. {@link #TMD_MAX} ends the enum as in C, where it is
 * the array length rather than an effect, so {@code TMD_MAX.ordinal()} is one more than the count
 * of real effects.
 *
 * <p>Each constant declares its flags as plain arrays which the constructor folds into a
 * {@link uk.co.jackoftradesltd.channel.utils.Flag} bitset for compact storage and querying.
 *
 * <p>Enum TimedEffect coded on 260817; the per-constant comments written on 260818, every
 * constant checked against {@code list-player-timed.h}, {@code player_timed.txt} and the code
 * that acts on it, and the comments commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public enum TimedEffect {
    /**
     * No effect — the absent-value sentinel. Has no counterpart in C, whose {@code TMD_*} indices
     * start at {@code FAST}; it exists because a Java reference needs something to hold before a
     * name has been resolved.
     *
     * <p>{@code PlayerTimedAssembler.assemble} starts its running "previous" and "current" effect
     * as this, and refuses a record that names it (or {@link #TMD_MAX}) as an illegal sentinel;
     * {@code EffectAssembler.getWrapperSubType} refuses it as a timed sub-type the same way. A name
     * that resolves to no constant at all is likewise a fatal parse error, so a loaded definition
     * is never keyed on this.
     *
     * <p>Nothing ever gives it a duration and no definition is loaded for it, so it has no grades
     * and carries no redraw or update flags. Loops over every constant that read the player's
     * levels ({@code PlayerCalcs}, {@code UIEntryValueRegistry}) skip it explicitly, where C relies
     * on there being no such slot.
     *
     * <p>Constant TMD_NONE coded on 260817, commented in full on 261029.
     */
    TMD_NONE(new PlayerRedraw[]{}, new PlayerUpdateEnum[]{}),
    /**
     * Haste: the player acts faster (+10 speed, shared with {@link #TMD_SPRINT}).
     *
     * <p>Constant TMD_FAST coded on 260817, commented in full on 261029.
     */
    TMD_FAST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Slowness: the player acts slower (-10 speed).
     *
     * <p>Constant TMD_SLOW coded on 260817, commented in full on 261029.
     */
    TMD_SLOW(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Blindness. One of only two statuses whose own redraw set is something other than the status
     * line — the other being {@link #TMD_IMAGE} — because sight itself is gone: the map is
     * repainted, and both the field of view and monster visibility are recomputed.
     *
     * <p>The set holds no {@code PR_STATUS}, as in C; the status line is still repainted, because
     * {@code PlayerTimed.setTimed} adds it to every notified change (see the class comment).
     *
     * <p>Constant TMD_BLIND coded on 260817, commented in full on 261029.
     */
    TMD_BLIND(new PlayerRedraw[]{PlayerRedraw.PR_MAP}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_UPDATE_VIEW, PlayerUpdateEnum.PU_MONSTERS}),
    /**
     * Paralysis: the player cannot act until it lapses.
     *
     * <p>Constant TMD_PARALYZED coded on 260817, commented in full on 261029.
     */
    TMD_PARALYZED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Confusion: movement goes astray, and scrolls and device use suffer.
     *
     * <p>Constant TMD_CONFUSED coded on 260817, commented in full on 261029.
     */
    TMD_CONFUSED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Fear: the player cannot attack in melee.
     *
     * <p>Constant TMD_AFRAID coded on 260817, commented in full on 261029.
     */
    TMD_AFRAID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Hallucination. Monsters and objects are shown as something other than what they are, so the
     * map, the monster list and the item list are all repainted — the widest own redraw set of any
     * status, and with {@link #TMD_BLIND} one of the two whose own set holds no {@code PR_STATUS}
     * (it is added on every notified change, as the class comment explains).
     *
     * <p>Constant TMD_IMAGE coded on 260817, commented in full on 261029.
     */
    TMD_IMAGE(new PlayerRedraw[]{PlayerRedraw.PR_MAP, PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Poisoning: damage over time. Recovers at the CON-derived rate rather than one per turn
     * ({@code GameWorld.decreaseTimeouts}).
     *
     * <p>Constant TMD_POISONED coded on 260817, commented in full on 261029.
     */
    TMD_POISONED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Bleeding wounds: damage over time. Like poison it recovers at the CON-derived rate, except
     * at the "Mortal Wound" grade or for a {@code PF_ROCK} race, neither of which bleeds down.
     *
     * <p>Constant TMD_CUT coded on 260817, commented in full on 261029.
     */
    TMD_CUT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Stunning: penalties to hit and to spell failure. Recovers at the CON-derived rate.
     *
     * <p>Constant TMD_STUN coded on 260817, commented in full on 261029.
     */
    TMD_STUN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Nourishment — the food counter rather than an affliction, which is why higher is better here
     * and worse everywhere else. Aged by the digestion code in {@code GameWorld.processWorld}, so
     * the per-turn effect countdown deliberately decrements it by zero.
     *
     * <p>Constant TMD_FOOD coded on 260817, commented in full on 261029.
     */
    TMD_FOOD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Protection from evil.
     *
     * <p>Constant TMD_PROTEVIL coded on 260817, commented in full on 261029.
     */
    TMD_PROTEVIL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Invulnerability.
     *
     * <p>Constant TMD_INVULN coded on 260817, commented in full on 261029.
     */
    TMD_INVULN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Heroism: a bonus to hit.
     *
     * <p>Constant TMD_HERO coded on 260817, commented in full on 261029.
     */
    TMD_HERO(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Berserk rage: a large melee to-hit bonus, paid for with a penalty to armour class.
     *
     * <p>Constant TMD_SHERO coded on 260817, commented in full on 261029.
     */
    TMD_SHERO(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Mystic shield: a bonus to armour class.
     *
     * <p>Constant TMD_SHIELD coded on 260817, commented in full on 261029.
     */
    TMD_SHIELD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Blessing: a bonus to armour class and to hit.
     *
     * <p>Constant TMD_BLESSED coded on 260817, commented in full on 261029.
     */
    TMD_BLESSED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * See invisible. Also recomputes monster visibility, since what the player can see has changed
     * without the map itself changing.
     *
     * <p>Constant TMD_SINVIS coded on 260817, commented in full on 261029.
     */
    TMD_SINVIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_MONSTERS}),
    /**
     * Enhanced infravision. Recomputes monster visibility for the same reason as
     * {@link #TMD_SINVIS}.
     *
     * <p>Constant TMD_SINFRA coded on 260817, commented in full on 261029.
     */
    TMD_SINFRA(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_MONSTERS}),
    /**
     * Temporary acid resistance.
     *
     * <p>Constant TMD_OPP_ACID coded on 260817, commented in full on 261029.
     */
    TMD_OPP_ACID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary electricity resistance.
     *
     * <p>Constant TMD_OPP_ELEC coded on 260817, commented in full on 261029.
     */
    TMD_OPP_ELEC(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary fire resistance.
     *
     * <p>Constant TMD_OPP_FIRE coded on 260817, commented in full on 261029.
     */
    TMD_OPP_FIRE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary cold resistance.
     *
     * <p>Constant TMD_OPP_COLD coded on 260817, commented in full on 261029.
     */
    TMD_OPP_COLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary poison resistance.
     *
     * <p>Constant TMD_OPP_POIS coded on 260817, commented in full on 261029.
     */
    TMD_OPP_POIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary confusion resistance.
     *
     * <p>Constant TMD_OPP_CONF coded on 260817, commented in full on 261029.
     */
    TMD_OPP_CONF(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Amnesia: the player cannot read scrolls, spell failure rates are sharply worse, and device
     * skill is reduced.
     *
     * <p>Constant TMD_AMNESIA coded on 260817, commented in full on 261029.
     */
    TMD_AMNESIA(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary telepathy.
     *
     * <p>Constant TMD_TELEPATHY coded on 260817, commented in full on 261029.
     */
    TMD_TELEPATHY(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Stone skin: +40 armour class, but -5 speed.
     *
     * <p>Constant TMD_STONESKIN coded on 260817, commented in full on 261029.
     */
    TMD_STONESKIN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Terror: fear that also grants +10 speed, so the player runs away fast.
     *
     * <p>Constant TMD_TERROR coded on 260817, commented in full on 261029.
     */
    TMD_TERROR(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Sprinting: +10 speed, paid for afterwards — when it ends the player is slowed.
     *
     * <p>Constant TMD_SPRINT coded on 260817, commented in full on 261029.
     */
    TMD_SPRINT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Fearlessness: immunity to fear while it lasts.
     *
     * <p>Constant TMD_BOLD coded on 260817, commented in full on 261029.
     */
    TMD_BOLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Scrambled stats: the player's statistics are shuffled.
     *
     * <p>Constant TMD_SCRAMBLE coded on 260817, commented in full on 261029.
     */
    TMD_SCRAMBLE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Safety from traps.
     *
     * <p>Constant TMD_TRAPSAFE coded on 260817, commented in full on 261029.
     */
    TMD_TRAPSAFE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Mana channelling: a successful spell cast uses three quarters of a normal turn's energy.
     *
     * <p>Constant TMD_FASTCAST coded on 260817, commented in full on 261029.
     */
    TMD_FASTCAST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary acid brand on the player's attacks.
     *
     * <p>Constant TMD_ATT_ACID coded on 260817, commented in full on 261029.
     */
    TMD_ATT_ACID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary lightning brand on the player's attacks.
     *
     * <p>Constant TMD_ATT_ELEC coded on 260817, commented in full on 261029.
     */
    TMD_ATT_ELEC(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary fire brand on the player's attacks.
     *
     * <p>Constant TMD_ATT_FIRE coded on 260817, commented in full on 261029.
     */
    TMD_ATT_FIRE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary cold brand on the player's attacks.
     *
     * <p>Constant TMD_ATT_COLD coded on 260817, commented in full on 261029.
     */
    TMD_ATT_COLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary poison brand on the player's attacks.
     *
     * <p>Constant TMD_ATT_POIS coded on 260817, commented in full on 261029.
     */
    TMD_ATT_POIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * The player's next melee hit confuses the monster it lands on, and the status is cleared by
     * that hit.
     *
     * <p>Constant TMD_ATT_CONF coded on 260817, commented in full on 261029.
     */
    TMD_ATT_CONF(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary slay evil on the player's attacks.
     *
     * <p>Constant TMD_ATT_EVIL coded on 260817, commented in full on 261029.
     */
    TMD_ATT_EVIL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary slay demon on the player's attacks.
     *
     * <p>Constant TMD_ATT_DEMON coded on 260817, commented in full on 261029.
     */
    TMD_ATT_DEMON(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary hit-point siphoning: melee hits on living monsters heal the player.
     *
     * <p>Constant TMD_ATT_VAMP coded on 260817, commented in full on 261029.
     */
    TMD_ATT_VAMP(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Turn-by-turn healing through raised metabolism: each turn heals the player, and the cost
     * comes out of the food counter. The status is cancelled outright once {@link #TMD_FOOD} falls
     * below hungry.
     *
     * <p>Constant TMD_HEAL coded on 260817, commented in full on 261029.
     */
    TMD_HEAL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * A monster is under the player's command. The only status whose countdown reaches outside the
     * player: the commanded monster's own timer is stepped in time with this one, and losing line
     * of sight breaks the command rather than shortening it.
     *
     * <p>Constant TMD_COMMAND coded on 260817, commented in full on 261029.
     */
    TMD_COMMAND(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Hit and run: after the player's attempt to steal from a monster ({@code steal_monster_item} in
     * {@code mon-util.c}), whether it succeeded or not, the player is teleported a short way off
     * ("You vanish into the shadows!") and the status is cleared.
     *
     * <p>Constant TMD_ATT_RUN coded on 260817, commented in full on 261029.
     */
    TMD_ATT_RUN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Covered tracks: no scent trail is left and the player is harder to see. Coarsens the noise
     * increment in {@code GameWorld.makeNoise} from 1 to 4, shrinking the range at which monsters
     * can hear the player.
     *
     * <p>Constant TMD_COVERTRACKS coded on 260817, commented in full on 261029.
     */
    TMD_COVERTRACKS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Piercing shot: the next sharp missile passes through its target, and the status is cleared
     * by that shot.
     *
     * <p>Constant TMD_POWERSHOT coded on 260817, commented in full on 261029.
     */
    TMD_POWERSHOT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Taunting: monsters are provoked, so they are likelier to attack in melee than to cast.
     *
     * <p>Constant TMD_TAUNT coded on 260817, commented in full on 261029.
     */
    TMD_TAUNT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Bloodlust: bonus damage and extra blows that grow with the level of the status, bought with
     * the risk of being forced into attacking, a cost in hit points as it fades, and a chance of
     * surviving what would otherwise be a killing blow.
     *
     * <p>Constant TMD_BLOODLUST coded on 260817, commented in full on 261029.
     */
    TMD_BLOODLUST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * The Black Breath: each turn there is an even chance of each of a Constitution drain, a
     * Strength drain and a loss of experience.
     *
     * <p>Constant TMD_BLACKBREATH coded on 260817, commented in full on 261029.
     */
    TMD_BLACKBREATH(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary stealth: +10 to the stealth skill.
     *
     * <p>Constant TMD_STEALTH coded on 260817, commented in full on 261029.
     */
    TMD_STEALTH(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary free action: immunity to paralysis.
     *
     * <p>Constant TMD_FREE_ACT coded on 260817, commented in full on 261029.
     */
    TMD_FREE_ACT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * The count sentinel: C's {@code TMD_MAX}, the length of its {@code timed_effects} and
     * {@code p->timed} arrays, which is not itself an effect. Kept as a constant so the assemblers
     * can refuse it by name as they refuse {@link #TMD_NONE}; it has no grades and carries no
     * redraw or update flags.
     *
     * <p>Constant TMD_MAX coded on 260817, commented in full on 261029.
     */
    TMD_MAX(new PlayerRedraw[]{}, new PlayerUpdateEnum[]{});

    /**
     * Screen regions to mark for redraw whenever this effect's level changes.
     *
     * <p>Mirrors the {@code flag_redraw} column of {@code list-player-timed.h}. {@code PR_STATUS}
     * is not added here for every effect, as C's {@code player_set_timed} adds it on each notified
     * change.
     *
     * <p>Field redrawFlags coded on 260817, commented in full on 261029.
     */
    private final Flag<PlayerRedraw> redrawFlags;
    /**
     * Derived quantities to mark for recalculation whenever this effect's level changes.
     *
     * <p>Mirrors the {@code flag_update} column of {@code list-player-timed.h}.
     *
     * <p>Field updateFlags coded on 260817, commented in full on 261029.
     */
    private final Flag<PlayerUpdateEnum> updateFlags;

    /**
     * Folds the per-effect redraw/upkeep arrays into queryable bitsets.
     *
     * <p>The constants declare their flags as readable arrays; this constructor turns each
     * into a {@link Flag} so the runtime can test and combine them cheaply.
     *
     * <p>Constructor coded on 260817, commented in full on 261029.
     *
     * @param redrawFlags the {@link PlayerRedraw} regions this effect dirties
     * @param upkeepFlags the {@link PlayerUpdateEnum} recalculations this effect triggers
     */
    TimedEffect(PlayerRedraw @NotNull [] redrawFlags, PlayerUpdateEnum @NotNull [] upkeepFlags) {
        this.redrawFlags = new Flag<>(PlayerRedraw.class);
        this.updateFlags = new Flag<>(PlayerUpdateEnum.class);

        for (PlayerRedraw redrawFlag : redrawFlags) {
            this.redrawFlags.on(redrawFlag);
        }

        for (PlayerUpdateEnum upkeepFlag : upkeepFlags) {
            this.updateFlags.on(upkeepFlag);
        }
    }

    /**
     * Returns the screen regions this effect dirties when its value changes — the port of reading
     * C's {@code timed_effects[idx].flag_redraw}.
     *
     * <p>The set is the table's own, so it holds no {@code PR_STATUS} for {@link #TMD_BLIND} or
     * {@link #TMD_IMAGE}; the caller that applies a change ({@code PlayerTimed.setTimed}) raises
     * {@code PR_STATUS} itself, as C does.
     *
     * <p><b>The constant's own set, handed out rather than copied.</b> An enum constant is a
     * singleton for the life of the JVM, so a mutable reference escaping here would let one
     * caller's change reach every later reader of this effect. The {@link FlagView} return type is
     * what prevents that, and it does so without the allocation a defensive copy would cost on
     * every call. The field is {@code final} and written only by the constructor, so there is no
     * second writer for a caller to observe either.
     *
     * <p>The guarantee is the interface's, not the object's: a caller who downcasts the result to
     * {@link Flag} can still change it, and would corrupt this constant for the rest of the run. A
     * view withholds mutation from whoever holds the view; it does not make the underlying set
     * immutable. Nothing in the port does that, and the type is the statement that nothing should.
     *
     * <p>Function getRedrawFlags coded on 260817, commented in full on 261029, the defensive copy
     * dropped on 260818 once the return type made it redundant.
     *
     * @return a read-only view of the {@code PR_*} flags to raise when this effect changes
     */
    public FlagView<PlayerRedraw> getRedrawFlags() {
        return redrawFlags;
    }

    /**
     * Returns the derived quantities this effect invalidates when its value changes — the port of
     * reading C's {@code timed_effects[idx].flag_update}.
     *
     * <p>Handed out as a view rather than copied, for the reasons set out on
     * {@link #getRedrawFlags}.
     *
     * <p>Function getUpdateFlags coded on 260817, commented in full on 261029, the defensive copy
     * dropped on 260818 once the return type made it redundant.
     *
     * @return a read-only view of the {@code PU_*} flags to raise when this effect changes
     */
    public FlagView<PlayerUpdateEnum> getUpdateFlags() {
        return updateFlags;
    }
}
