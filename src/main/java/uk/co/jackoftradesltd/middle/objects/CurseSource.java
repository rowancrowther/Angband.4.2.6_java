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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

/**
 * The {@link BonusSource} view of one {@link Curse} on a worn item — the later passes of
 * {@code calcBonuses}' equipment walk, C's {@code obj = curses[index].obj} in {@code calc_bonuses()}
 * ({@code player-calcs.c}).
 *
 * <p>In C the object bound on those passes is neither the item nor the curse record: it is the
 * curse's own {@code struct object}, one per curse in the game. The curse parser builds it
 * ({@code obj-init.c}), {@code write_curse_kinds()} gives it the {@code <curse object>} kind and a
 * {@code known} counterpart, and {@code update_player_object_knowledge()} runs
 * {@code player_know_object()} over every curse object exactly as it does over items. The port keeps
 * that object on the curse — {@link Curse#getItemObject()} — so every accessor below reads it, or
 * its {@link ItemObject#getKnown() known} counterpart, directly. This class adds no logic of its own.
 *
 * <p>Two quantities are answered with constants, because the curse object never carries them:
 *
 * <ul>
 *   <li>{@link #baseAC} is zero. The {@code combat:} line of {@code curse.txt} sets only to-hit,
 *       to-damage and to-armour, so a curse object's base armour class is never set and stays
 *       zero.</li>
 *   <li>{@link #isDigger} is {@code false}. {@code write_curse_kinds()} sets the curse object's
 *       kind and sval but not its tval, which stays {@code none}.</li>
 * </ul>
 *
 * <p>The {@code known*} accessors are <b>not</b> constants. The curse object's known counterpart is
 * created blank, but it is marked {@code OBJ_NOTICE_ASSESSED} so that it can be fully known, and
 * {@code player_know_object()} then fills its to-armour, to-hit, to-damage, element info and flags
 * from the player's rune knowledge ({@code PlayerKnowledge.knowObject(Player, Curse)} in the port).
 * So under {@code knownOnly} a curse's combat bonuses, resistances and flags count once the player
 * has learned the matching rune, and not before — the same rule an item follows. Only the
 * {@code null} guards are the port's own: C dereferences {@code known} unguarded, which is safe
 * there because {@code write_curse_kinds()} allocates one for every curse.
 *
 * <p>Class CurseSource coded on 260820, commented in full on 261006.
 *
 * @author Rowan Crowther
 * @see ItemSource
 */
public class CurseSource implements BonusSource {
    /**
     * The curse this source reports on, whose {@link Curse#getItemObject() curse object} every
     * accessor reads. Never {@code null}: the constructor is annotated {@link NotNull} and the field
     * is not reassigned, so no accessor needs a guard on the curse itself. The guards that do appear
     * are on the curse object's {@link ItemObject#getKnown() known} counterpart.
     *
     * <p>Field curse coded on 260820, commented in full on 261006.
     */
    private final Curse curse;

    /**
     * Wraps one curse as a bonus source.
     *
     * <p>The curse's power is not held here. Whether a curse contributes at all is the caller's
     * question — {@code calcBonuses} ({@code PlayerCalcs}) builds a source only for a curse whose
     * {@link CurseData} power is non-zero, which is C's {@code if (curse[index].power)} in
     * {@code calc_bonuses()}.
     *
     * <p>Constructor CurseSource coded on 260820, commented in full on 261006.
     *
     * @param curse the curse, from the item's curse map
     */
    public CurseSource(@NotNull Curse curse) {
        this.curse = curse;
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code object_flags()} applied to the curse object: a copy of its full {@code flags},
     * whatever the player has learned. Built fresh from {@link ItemObject#getFlags()}, which already
     * copies, and copied once more into a new set by this method, so the caller can never reach the
     * curse object's own storage.
     *
     * <p>Function flags coded on 260820, commented in full on 261006.
     */
    @Override
    public Flag<ObjectFlag> flags() {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);

        for (ObjectFlag o : curse.getItemObject().getFlags()) {
            result.on(o);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->modifiers[mod]} on the curse object. Returned raw, on every pass: the
     * player's rune knowledge gates a modifier in the caller ({@code p->obj_k->modifiers[]}), not
     * here, and that gate is the same for a curse as for an item. A modifier the curse does not
     * carry reads as zero.
     *
     * <p>Function modifier coded on 260820, commented in full on 261006.
     */
    @Override
    public int modifier(ObjectModifier modifier) {
        return curse.getItemObject().getModifierValue(modifier);
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->el_info[j].res_level} on the curse object. When {@code knownOnly} is set
     * the caller reads it only for an element where {@link #knownResLevel} is non-zero. An element
     * the curse has no entry for reads as zero, as C's fixed array would, and the same goes for a
     * curse object whose element map is {@code null}, which {@link ItemObject#getElInfo()} answers
     * with an empty map.
     *
     * <p>Function resLevel coded on 260820, commented in full on 261006.
     */
    @Override
    public int resLevel(ElementEnum element) {
        ElementInfo elementInfo = curse.getItemObject().getElInfo().get(element);
        if (elementInfo == null) return 0;
        return elementInfo.getResLevel();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->known->el_info[j].res_level} on the curse object. {@code player_know_object()}
     * copies an element's level across only where the player knows that element's rune, and writes
     * zero otherwise, so a non-zero answer means the player can see this curse's resistance.
     * Returns zero when the curse object has no known counterpart (the port's guard; C always has
     * one) or no entry for the element.
     *
     * <p>Function knownResLevel coded on 260820, commented in full on 261006.
     *
     * @return the level the player has learned, or zero if none
     */
    @Override
    public int knownResLevel(ElementEnum element) {
        ItemObject known = curse.getItemObject().getKnown();
        if (known == null) return 0;
        ElementInfo elementInfo = known.getElInfo().getOrDefault(element, null);
        if (elementInfo == null) return 0;
        return elementInfo.getResLevel();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->ac} on the curse object, which is always zero: the {@code combat:} line of
     * {@code curse.txt} has no armour-class field, and nothing else sets one. It is the object's own
     * {@code ac}, not the kind's, that is zero here, so the constant stays correct whatever the
     * {@code <curse object>} kind declares.
     *
     * <p>Function baseAC coded on 260820, commented in full on 261006.
     *
     * @return always zero — a curse can never add base armour class
     */
    @Override
    public int baseAC() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->to_a} on the curse object: the third field of the curse's {@code combat:}
     * line, after to-hit and to-damage.
     *
     * <p>Function toAC coded on 260820, commented in full on 261006.
     */
    @Override
    public int toAC() {
        return curse.getItemObject().getToAC();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->known->to_a} on the curse object, which {@code player_know_object()} sets to
     * the player's to-armour rune knowledge multiplied by the curse's {@link #toAC}. It is therefore
     * non-zero only once the player has learned that rune, and the caller's
     * {@code !known_only || obj->known->to_a} test drops a curse's armour bonus until then.
     * Returns zero when the curse object has no known counterpart (the port's guard).
     *
     * <p>Function knownToAC coded on 260820, commented in full on 261006.
     *
     * @return the armour bonus the player has learned, or zero
     */
    @Override
    public int knownToAC() {
        ItemObject known = curse.getItemObject().getKnown();
        if (known == null) return 0;
        return known.getToAC();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->to_h} on the curse object: the first field of the curse's {@code combat:}
     * line.
     *
     * <p>Function toHit coded on 260820, commented in full on 261006.
     */
    @Override
    public int toHit() {
        return curse.getItemObject().getToHit();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->known->to_h} on the curse object. {@code player_know_object()} sets it from
     * the player's to-hit rune knowledge multiplied by the curse's {@link #toHit}, unless
     * {@code object_has_standard_to_h()} says the value is the standard one; a curse object's
     * standard value is zero, so the exception never hides a real bonus. Returns zero when the curse
     * object has no known counterpart (the port's guard).
     *
     * <p>Function knownToHit coded on 260820, commented in full on 261006.
     *
     * @return the to-hit bonus the player has learned, or zero
     */
    @Override
    public int knownToHit() {
        ItemObject known = curse.getItemObject().getKnown();
        if (known == null) return 0;
        return known.getToHit();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->to_d} on the curse object: the second field of the curse's {@code combat:}
     * line.
     *
     * <p>Function toDam coded on 260820, commented in full on 261006.
     */
    @Override
    public int toDam() {
        return curse.getItemObject().getToDam();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code obj->known->to_d} on the curse object, set by {@code player_know_object()} from
     * the player's to-damage rune knowledge multiplied by the curse's {@link #toDam}. Returns zero
     * when the curse object has no known counterpart (the port's guard).
     *
     * <p>Function knownToDam coded on 260820, commented in full on 261006.
     *
     * @return the damage bonus the player has learned, or zero
     */
    @Override
    public int knownToDam() {
        ItemObject known = curse.getItemObject().getKnown();
        if (known == null) return 0;
        return known.getToDam();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code tval_is_digger(obj)} on the curse object. {@code write_curse_kinds()} never sets
     * the object's tval, so it stays {@code none} and the {@code OF_DIG_*} flags are never read from
     * a curse, even one whose flags name them.
     *
     * <p>Function isDigger coded on 260820, commented in full on 261006.
     *
     * @return always {@code false}
     */
    @Override
    public boolean isDigger() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The curse object's known flags. C's {@code object_flags_known()} intersects the object's
     * flags with {@code obj->known->flags}, then adds back the kind's flags if the player is aware of
     * the kind and an ego's if there is one. For a curse object neither addition applies — the
     * {@code <curse object>} kind carries no flags and there is no ego — and
     * {@code player_know_object()} builds the known flags as a subset of the object's own, so the
     * intersection changes nothing and the known flags are the answer. This is deliberately not
     * {@link ItemObject#flagsKnown()}, which would redo that work to the same result.
     *
     * <p>The set is a fresh copy ({@link ItemObject#getFlags()} copies), so the caller's flag
     * variable, kept across passes, never aliases the curse object's own. A curse object with no
     * known counterpart (the port's guard) answers with a new empty set rather than leaving the
     * previous source's flags standing.
     *
     * <p>Function flagsKnown coded on 260820, commented in full on 261006.
     *
     * @return the flags the player has learned this curse carries; never the curse object's own
     * storage
     */
    @Override
    public Flag<ObjectFlag> flagsKnown() {
        ItemObject known = curse.getItemObject().getKnown();
        if (known == null) return new Flag<>(ObjectFlag.class);
        return known.getFlags();
    }

    /**
     * {@inheritDoc}
     *
     * <p>C's {@code of_has(obj->flags, flag)} on the curse object. Reachable only through
     * {@link #isDigger}, which a curse always answers {@code false}, so in practice this is never
     * consulted — it is implemented for completeness rather than use.
     *
     * <p>Function flagSet coded on 260820, commented in full on 261006.
     */
    @Override
    public boolean flagSet(ObjectFlag flag) {
        return curse.getItemObject().hasFlag(flag);
    }
}