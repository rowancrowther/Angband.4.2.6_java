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

package uk.co.jackoftradesltd.testsupport;

import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.util.List;
import java.util.Map;

/**
 * The one place a test builds a {@link Curse}.
 *
 * <p>While {@code docs/Curse_object_unflattening.md} is in progress a curse carries its mechanical
 * properties twice: as the loose fields of the {@link Curse} constructor and on the
 * {@link ItemObject} that stands for C's {@code curse->obj}. A test that fills only the first
 * leaves the second empty, and fails as soon as a reader moves over to the object. {@link #curse}
 * takes the same values the constructor does, minus the object, and fills both from them, so a
 * reader can move without any test noticing.
 *
 * <p>The signature deliberately mirrors the constructor's. When the unflattening reaches the step
 * that removes the loose parameters, this is the one file whose call into {@code new Curse(...)}
 * changes; the roughly forty tests that call {@link #curse} do not.
 *
 * <p>Every collection argument may be {@code null}, as the constructor allows; a {@code null} is
 * left off the object. The two maps are shared with the curse, not copied, exactly as
 * {@code CurseAssembler} shares them, so a test that adds an entry through either view sees it through
 * the other. The flag set is the exception: {@link ItemObject#setFlagsTo} copies, so flags added to
 * the curse after construction do not reach the object.
 *
 * <p>The object's {@code time} is not filled in. {@code CurseAssembler} does not fill it either,
 * and {@code Effect_time_migration.md} owns that move.
 *
 * <p>Class CurseFixture coded on 261005.
 *
 * @author Rowan Crowther
 */
public final class CurseFixture {

    private CurseFixture() {
    }

    /**
     * Builds a curse and its object from the same values.
     *
     * <p>The parameters are the {@link Curse} constructor's, in its order, with the
     * {@link ItemObject} dropped.
     *
     * @param name          the curse's name
     * @param objectBases   the bases it may attach to
     * @param weight        the weight adjustment, written to both the curse and its object
     * @param effect        the effect, or {@code null}; the object gets a one-element list, or an
     *                      empty one
     * @param objectFlags   the object flags, or {@code null}
     * @param modifiers     the modifiers, or {@code null}
     * @param elInfo        the element info, or {@code null}
     * @param combatToHit   the to-hit penalty
     * @param combatDam     the to-damage penalty
     * @param combatAC      the armour-class penalty
     * @param conflictNames the names of the curses it conflicts with
     * @param conflictFlags the object flags it conflicts with
     * @param description   the description
     * @param message       the effect message, written to both the curse and its object
     * @param index         the curse's index
     * @return the curse, with {@link Curse#getItemObject()} populated to match
     */
    public static Curse curse(String name,
                              List<ObjectBase> objectBases,
                              int weight,
                              Effect effect,
                              Flag<ObjectFlag> objectFlags,
                              Map<ObjectModifier, Integer> modifiers,
                              Map<ElementEnum, ElementInfo> elInfo,
                              int combatToHit,
                              int combatDam,
                              int combatAC,
                              List<String> conflictNames,
                              Flag<ObjectFlag> conflictFlags,
                              String description,
                              String message,
                              int index) {
        ItemObject object = new ItemObject();
        object.setWeight(weight);
        object.setEffect(effect == null ? null : List.of(effect));
        if (objectFlags != null) object.setFlagsTo(objectFlags);
        if (modifiers != null) object.setModifiers(modifiers);
        if (elInfo != null) object.setElInfo(elInfo);
        object.setToHit(combatToHit);
        object.setToDam(combatDam);
        object.setToAC(combatAC);
        object.setEffectMessage(message);

        return new Curse(name, objectBases, object, weight, effect, objectFlags, modifiers, elInfo,
                combatToHit, combatDam, combatAC, conflictNames, conflictFlags, description, message, index);
    }
}
