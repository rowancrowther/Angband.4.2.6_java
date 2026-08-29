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

package uk.co.jackoftrades.middle.player.enums;

import uk.co.jackoftrades.channel.utils.Flag;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.channel.utils.FlagView;

/**
 * The catalogue of timed player statuses — the {@code TMD_*} effects such as haste, fear,
 * poison, temporary resistances and the brand/attack buffs — each recording the screen
 * redraws and model recalculations its onset or expiry should trigger.
 *
 * <p>Ports the C {@code TMD_*} timed-effect table ({@code list-player-timed.h} /
 * {@code player-timed.c}). The numeric level of each status is stored on the player; this
 * enum captures the <em>static</em> per-effect metadata — specifically which
 * {@link PlayerRedraw} regions and which {@link PlayerUpdateEnum} recalculations become dirty
 * whenever the effect's value changes.
 *
 * <p><b>Why carry the flag sets here:</b> when a timed status rises or falls the engine must
 * refresh exactly the affected display and derived state and nothing more (the dirty-region
 * discipline described on {@link PlayerRedraw} / {@link PlayerUpdateEnum}). Attaching those
 * flag sets to the effect keeps the mapping declarative and in one place. Most effects only
 * touch the status line and the bonus recalculation ({@code PR_STATUS} + {@code PU_BONUS});
 * the notable exceptions are sense-altering statuses — e.g. {@code TMD_BLIND} and
 * {@code TMD_IMAGE} dirty the map and monster/item views, and see-invisible / infravision
 * additionally re-evaluate monster visibility.
 *
 * <p>Each constant declares its flags as plain arrays which the constructor folds into a
 * {@link uk.co.jackoftrades.channel.utils.Flag} bitset for compact storage and querying.
 *
 * <p>Enum TimedEffect coded on 260817; the per-constant comments written on 260818.
 *
 * @author Rowan Crowther
 */
public enum TimedEffect {
    /**
     * No effect — the absent-value sentinel. Has no counterpart in C, whose {@code TMD_*} indices
     * start at {@code FAST}; it exists because a Java reference needs something to hold before a
     * name has been resolved, which is what the parsers use it for
     * ({@code PlayerTimedAssembler.java:75}, {@code EffectAssembler.java:245}). A name that fails
     * to resolve is recorded as an error rather than left as this.
     *
     * <p>Nothing ever gives it a duration and no definition is loaded for it, so it has no grades
     * and carries no redraw or update flags. Its presence shifts every other constant one place
     * along relative to C's numbering, which costs nothing because every lookup in the port is by
     * name.
     */
    TMD_NONE(new PlayerRedraw[]{}, new PlayerUpdateEnum[]{}),
    /**
     * Haste: the player acts faster.
     */
    TMD_FAST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Slowness: the player acts slower.
     */
    TMD_SLOW(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Blindness. One of only two statuses that repaint something other than the status line — the
     * other being {@link #TMD_IMAGE} — because sight itself is gone: the map is repainted, and
     * both the field of view and monster visibility are recomputed.
     */
    TMD_BLIND(new PlayerRedraw[]{PlayerRedraw.PR_MAP}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_UPDATE_VIEW, PlayerUpdateEnum.PU_MONSTERS}),
    /**
     * Paralysis: the player cannot act until it lapses.
     */
    TMD_PARALYZED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Confusion: movement and targeting go astray.
     */
    TMD_CONFUSED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Fear: the player cannot attack in melee.
     */
    TMD_AFRAID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Hallucination. Monsters and objects are shown as something other than what they are, so the
     * map, the monster list and the item list are all repainted — the widest redraw set of any
     * status, and with {@link #TMD_BLIND} one of the two that does not touch the status line.
     */
    TMD_IMAGE(new PlayerRedraw[]{PlayerRedraw.PR_MAP, PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Poisoning: damage over time. Recovers at the CON-derived rate rather than one per turn
     * ({@code GameWorld.decreaseTimeouts}).
     */
    TMD_POISONED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Bleeding wounds: damage over time. Like poison it recovers at the CON-derived rate, except
     * at the "Mortal Wound" grade or for a {@code PF_ROCK} race, neither of which bleeds down.
     */
    TMD_CUT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Stunning: penalties to hit and to spell failure. Recovers at the CON-derived rate.
     */
    TMD_STUN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Nourishment — the food counter rather than an affliction, which is why higher is better here
     * and worse everywhere else. Aged by the digestion code in {@code GameWorld.processWorld}, so
     * the per-turn effect countdown deliberately decrements it by zero.
     */
    TMD_FOOD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Protection from evil.
     */
    TMD_PROTEVIL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Invulnerability.
     */
    TMD_INVULN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Heroism.
     */
    TMD_HERO(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Berserk rage.
     */
    TMD_SHERO(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Mystic shield: a bonus to armour class.
     */
    TMD_SHIELD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Blessing: a bonus to armour class and to hit.
     */
    TMD_BLESSED(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * See invisible. Also recomputes monster visibility, since what the player can see has changed
     * without the map itself changing.
     */
    TMD_SINVIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_MONSTERS}),
    /**
     * Enhanced infravision. Recomputes monster visibility for the same reason as
     * {@link #TMD_SINVIS}.
     */
    TMD_SINFRA(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_MONSTERS}),
    /**
     * Temporary acid resistance.
     */
    TMD_OPP_ACID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary electricity resistance.
     */
    TMD_OPP_ELEC(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary fire resistance.
     */
    TMD_OPP_FIRE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary cold resistance.
     */
    TMD_OPP_COLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary poison resistance.
     */
    TMD_OPP_POIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary confusion resistance.
     */
    TMD_OPP_CONF(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Amnesia: the player's map and object knowledge is forgotten.
     */
    TMD_AMNESIA(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary telepathy.
     */
    TMD_TELEPATHY(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Stone skin: armour class up, but slower.
     */
    TMD_STONESKIN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Terror: fear that also makes the player flee faster.
     */
    TMD_TERROR(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Sprinting: a burst of speed, paid for afterwards.
     */
    TMD_SPRINT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Fearlessness: immunity to fear while it lasts.
     */
    TMD_BOLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Scrambled stats: the player's statistics are shuffled.
     */
    TMD_SCRAMBLE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Safety from traps.
     */
    TMD_TRAPSAFE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Mana channelling: spells cost less time to cast.
     */
    TMD_FASTCAST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary acid brand on the player's attacks.
     */
    TMD_ATT_ACID(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary lightning brand on the player's attacks.
     */
    TMD_ATT_ELEC(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary fire brand on the player's attacks.
     */
    TMD_ATT_FIRE(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary cold brand on the player's attacks.
     */
    TMD_ATT_COLD(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary poison brand on the player's attacks.
     */
    TMD_ATT_POIS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Attacks temporarily confuse the monsters they hit.
     */
    TMD_ATT_CONF(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary slay evil on the player's attacks.
     */
    TMD_ATT_EVIL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary slay demon on the player's attacks.
     */
    TMD_ATT_DEMON(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary hit-point siphoning: attacks heal the player.
     */
    TMD_ATT_VAMP(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Turn-by-turn healing through raised metabolism. The speed comes out of the food counter,
     * and the status is cancelled outright once {@link #TMD_FOOD} falls below hungry.
     */
    TMD_HEAL(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * A monster is under the player's command. The only status whose countdown reaches outside the
     * player: the commanded monster's own timer is stepped in time with this one, and losing line
     * of sight breaks the command rather than shortening it.
     */
    TMD_COMMAND(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Attacks teleport the player away after landing.
     */
    TMD_ATT_RUN(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Covered tracks: no scent trail is left and the player is harder to see. Coarsens the noise
     * increment in {@code GameWorld.makeNoise} from 1 to 4, shrinking the range at which monsters
     * can hear the player.
     */
    TMD_COVERTRACKS(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Piercing shot: missiles pass through their target.
     */
    TMD_POWERSHOT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Taunting: monsters are provoked into attacking.
     */
    TMD_TAUNT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Bloodlust: more damage dealt and more taken.
     */
    TMD_BLOODLUST(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * The Black Breath: drains stats and experience each turn.
     */
    TMD_BLACKBREATH(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary stealth.
     */
    TMD_STEALTH(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS}),
    /**
     * Temporary free action: immunity to paralysis.
     */
    TMD_FREE_ACT(new PlayerRedraw[]{PlayerRedraw.PR_STATUS}, new PlayerUpdateEnum[]{PlayerUpdateEnum.PU_BONUS});

    /**
     * Screen regions to mark for redraw whenever this effect's level changes.
     */
    private final Flag<PlayerRedraw> redrawFlags;
    /** Derived quantities to mark for recalculation whenever this effect's level changes. */
    private final Flag<PlayerUpdateEnum> updateFlags;

    /**
     * Folds the per-effect redraw/upkeep arrays into queryable bitsets.
     *
     * <p>The constants declare their flags as readable arrays; this constructor turns each
     * into a {@link Flag} so the runtime can test and combine them cheaply.
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
     * <p>Function getRedrawFlags coded on 260817, commented in full on 260818, the defensive copy
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
     * <p>Function getUpdateFlags coded on 260817, commented in full on 260818, the defensive copy
     * dropped on 260818 once the return type made it redundant.
     *
     * @return a read-only view of the {@code PU_*} flags to raise when this effect changes
     */
    public FlagView<PlayerUpdateEnum> getUpdateFlags() {
        return updateFlags;
    }
}