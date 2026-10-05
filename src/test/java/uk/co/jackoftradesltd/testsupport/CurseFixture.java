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
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;

import java.util.List;
import java.util.Map;

/**
 * The one place a test builds a {@link Curse}.
 *
 * <p>A curse now holds only C's {@code struct curse} members and its own {@link ItemObject}, C's
 * {@code curse->obj}; the mechanical properties live on that object. {@link #curse} takes the values the
 * old loose-field constructor took and writes them onto the object, so the roughly forty tests that call
 * it did not have to change when the loose parameters went.
 *
 * <p>Every collection argument may be {@code null}, as the constructor allows; a {@code null} is
 * left off the object. The two maps are shared with the object, not copied, exactly as
 * {@code CurseAssembler} shares them. The flag set is the exception: {@link ItemObject#setFlagsTo}
 * copies, so flags added to the argument afterwards do not reach the object.
 *
 * <p>The object also gets a {@code known} twin with {@link ObjectNotice#OBJ_NOTICE_ASSESSED} on,
 * as {@code ObjectDataLoader.writeCurseKinds} gives every real curse, because
 * {@code PlayerKnowledge.knowObject(Player, Curse)} writes through {@code getKnown()}. The kind and
 * kind is a bare {@link ObjectKind} standing in for {@code <curse object>}, on the object and its twin,
 * because {@link ItemObject#hasStandardToH()} answers true for a null kind whatever the to-hit; the
 * sval {@code writeCurseKinds} also sets is left off, as no test here loads {@code object.txt}.
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
     * <p>The parameters are the old loose-field constructor's, in its order; they are written onto a
     * new {@link ItemObject} that is then handed to the slimmed {@link Curse} constructor.
     *
     * @param name          the curse's name
     * @param objectBases   the bases it may attach to
     * @param weight        the weight adjustment, written to the object
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
     * @param message       the effect message, written to the object
     * @param index         the curse's index
     * @return the curse, with {@link Curse#getItemObject()} populated
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

        // A bare kind stands in for <curse object>, so hasStandardToH reads the figure, not the null-kind hack
        ObjectKind curseKind = new ObjectKind();
        object.setKind(curseKind);

        ItemObject known = new ItemObject();
        known.setKind(curseKind);
        known.setNoticeOn(ObjectNotice.OBJ_NOTICE_ASSESSED);
        object.setKnown(known);

        return new Curse(name, objectBases, object, conflictNames, conflictFlags, description, index);
    }
}
