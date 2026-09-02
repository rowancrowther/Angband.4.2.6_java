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

package uk.co.jackoftradesltd.middle.objects;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

/**
 * One contributor of bonuses to {@code calcBonuses}' equipment walk — either a worn item or one of
 * the curses on it. A port-only abstraction with no counterpart in the C original, and the reason
 * it exists is worth stating, because the C it replaces looks much simpler than it is.
 *
 * <p>C's equipment loop ({@code player-calcs.c:1929-2020}) is a {@code while (obj)} over a single
 * pointer. The first pass binds {@code obj} to the slot's item; each later pass rebinds it to
 * {@code curses[index].obj}, the template object of a curse the item carries with non-zero power.
 * One variable, one body, {@code n + 1} passes. The port cannot follow that shape directly because
 * {@link Curse} has no object of its own — the parser flattens the C {@code curse->obj} into
 * {@link Curse#getObjectFlags}, {@link Curse#getModifiers}, {@link Curse#getElInfo} and the three
 * {@code getCombat*} accessors. This interface restores the single variable: the loop body asks the
 * source, and the two implementations answer from an item or from a curse.
 *
 * <p><b>The curse side answers with constants more often than it answers with data</b>, and those
 * constants are C's behaviour rather than a simplification. A curse's template object is built at
 * {@code obj-init.c:175-195} with the {@code <curse object>} kind and nothing else: its
 * {@code ac} is zero, its tval is {@code none}, and its {@code known} counterpart is a freshly
 * zeroed object carrying only a kind and an sval. Nothing in {@code obj-knowledge.c} ever fills that
 * known object in. So every {@code known*} method below, {@link #baseAC} and {@link #isDigger}
 * answer flatly for a curse — which means that when {@code calcBonuses} runs with
 * {@code knownOnly} set, a curse contributes its modifiers and nothing else: no flags, no
 * resistances, no combat bonuses.
 *
 * <p><b>Modifier knowledge is not here.</b> C gates every modifier on
 * {@code p->obj_k->modifiers[...]} ({@code player-calcs.c:1943-1970}) — the <em>player's</em> rune
 * knowledge, which is the same value on the item pass and the curse pass. It is a property of the
 * player, not of the object being examined, so it stays in {@code calcBonuses} where the player is
 * in scope. {@link #modifier} returns the raw value and the caller applies the gate.
 *
 * <p>Interface BonusSource coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 * @see ItemSource
 * @see CurseSource
 */
public interface BonusSource {
    /**
     * The source's full object flags, ignoring what the player knows — the port of C's
     * {@code object_flags} ({@code obj-util.c:351-356}).
     *
     * @return a flag set the caller may keep or mutate; never the source's own storage
     */
    Flag<ObjectFlag> flags();

    /**
     * The raw value of one modifier, C's {@code obj->modifiers[mod]}. <b>Ungated:</b> the caller
     * must still multiply by the player's rune knowledge, as {@code player-calcs.c:1943-1970} does.
     *
     * @param modifier the modifier to read
     * @return the modifier's value, or zero if the source does not carry it
     */
    int modifier(ObjectModifier modifier);

    /**
     * The source's resistance level for one element — C's {@code obj->el_info[j].res_level}, where
     * {@code -1} is a vulnerability, {@code 0} no opinion, and higher values successive grades of
     * resistance.
     *
     * @param element the element to read
     * @return the resistance level, or zero if the source has no entry for that element
     */
    int resLevel(ElementEnum element);

    /**
     * The resistance level the player has learned for one element — C's
     * {@code obj->known->el_info[j].res_level}, which {@code calc_bonuses} tests to decide whether
     * a resistance may be counted at all under {@code known_only}.
     *
     * @param element the element to read
     * @return the known resistance level; always zero for a curse, whose known object is blank
     */
    int knownResLevel(ElementEnum element);

    /**
     * The source's base armour class — C's {@code obj->ac}, the armour the item is worth before any
     * enchantment. Added unconditionally by {@code calc_bonuses}, knowledge notwithstanding.
     *
     * @return the base armour class; always zero for a curse, whose template object has no kind
     */
    int baseAC();

    /**
     * The source's armour enchantment — C's {@code obj->to_a}, the plus that {@code calc_bonuses}
     * adds to {@code state->to_a} only when {@link #knownToAC} says the player can see it.
     *
     * @return the bonus to armour class
     */
    int toAC();

    /**
     * The armour enchantment the player has learned — C's {@code obj->known->to_a}.
     *
     * @return the known bonus to armour class; always zero for a curse
     */
    int knownToAC();

    /**
     * The source's to-hit enchantment — C's {@code obj->to_h}. Never applied from a weapon or
     * launcher slot: those two are excluded by {@code calc_bonuses} so that wielding a weapon does
     * not improve unrelated actions.
     *
     * @return the bonus to hit
     */
    int toHit();

    /**
     * The to-hit enchantment the player has learned — C's {@code obj->known->to_h}.
     *
     * @return the known bonus to hit; always zero for a curse
     */
    int knownToHit();

    /**
     * The source's to-damage enchantment — C's {@code obj->to_d}, excluded from the weapon and
     * launcher slots for the same reason as {@link #toHit}.
     *
     * @return the bonus to damage
     */
    int toDam();

    /**
     * The to-damage enchantment the player has learned — C's {@code obj->known->to_d}.
     *
     * @return the known bonus to damage; always zero for a curse
     */
    int knownToDam();

    /**
     * Whether this source is a digging tool, C's {@code tval_is_digger(obj)} — the test that
     * decides whether the {@code OF_DIG_*} flags are read at all.
     *
     * @return {@code true} for a digger; always {@code false} for a curse, whose tval is
     * {@code none}
     */
    boolean isDigger();

    /**
     * The source's object flags reduced to what the player knows — the port of C's
     * {@code object_flags_known} ({@code obj-util.c:362-379}), which intersects the real flags with
     * the known object's and then folds back in what awareness of the kind or ego reveals.
     *
     * <p>Returns a fresh set every call rather than filling a caller's, so an empty answer really is
     * empty. That matters: C's version wipes the destination before it starts, so a source that
     * knows nothing must not leave the previous source's flags standing.
     *
     * @return a flag set the caller may keep or mutate; empty for a curse, whose known object is
     * blank
     */
    Flag<ObjectFlag> flagsKnown();

    /**
     * Tests one flag on the source's real flags, C's {@code of_has(obj->flags, flag)}. Despite
     * sitting beside the {@code known*} family this asks nothing about knowledge — {@code calc_bonuses}
     * reads the {@code OF_DIG_*} flags raw ({@code player-calcs.c:1959-1966}).
     *
     * @param flag the flag to test
     * @return {@code true} if the source carries that flag
     */
    boolean flagSet(ObjectFlag flag);
}