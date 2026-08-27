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

package uk.co.jackoftrades.middle.objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.util.List;
import java.util.Map;

/**
 * The definition of a curse (as loaded from {@code curse.txt}) — a negative
 * property that can attach to objects. This is the Java port of the C original's
 * {@code struct curse} ({@code src/object.h}).
 * <p>
 * In C a curse is little more than a name plus a nested {@code struct object *obj}
 * that carries all the mechanical properties. When the game computes an item's
 * real attributes it walks the item's curses and <em>merges</em> each curse's
 * {@code obj} onto the item ({@code obj-curse.c}). This class flattens that nested
 * object into named carriers, choosing types that match how the merge actually
 * reads them:
 * <ul>
 *   <li><b>{@link #effect}</b> — the effect chain (C: {@code curse->obj->effect}).
 *       All per-effect data (dice, subtype, timing, message, scaling expressions)
 *       lives inside each {@link Effect}, which is what {@code EffectAssembler}
 *       produces, so a curse simply holds a list of them.</li>
 *   <li><b>{@link #modifiers}</b> — the additive numeric bonuses/penalties
 *       (stats, speed, blows, digging, damage reduction, …). C merges these with
 *       simple addition ({@code obj-curse.c}: {@code modifiers[k] += ...}), so a
 *       plain {@code Map<ObjectModifier,Integer>} is the faithful shape.</li>
 *   <li><b>{@link #elInfo}</b> — the <em>element</em> half of the merge. Per
 *       element it holds a resistance level (from the {@code values:} line's
 *       {@code RES_*} tokens) <em>and</em> the hates/ignores flags (from the
 *       {@code flags:} line's {@code HATES_}/{@code IGNORE_} tokens). Resistances
 *       do <b>not</b> merge additively — C applies special immunity/vulnerability
 *       logic ({@code obj-curse.c} ~500-554) that must read a per-element
 *       {@code res_level}, which is why {@link ElementInfo} indexed by
 *       {@link ElementEnum} is required and a flat list would lose information.
 *       Element resistances therefore live here, not among the additive
 *       {@link ObjectModifier} entries in {@link #modifiers}.</li>
 * </ul>
 *
 * @author Rowan Crowther
 */
public class Curse {
    /**
     * Log destination for the conditions C asserts on. C's {@code modify_weight_for_curse} asserts
     * that a curse's weight is not negative ({@code obj-curse.c:400}); the port logs and throws
     * instead, so a malformed data file spoils one calculation rather than the process.
     */
    private static final Logger logger = LogManager.getLogger(Curse.class);
    
    /**
     * The curse's name (C: {@code curse->name}).
     */
    private final String name;

    /**
     * The object bases this curse may attach to (C: {@code curse->poss}, the
     * per-tval possibility array; port {@code types:} line).
     */
    private final List<ObjectBase> objectBases;

    /**
     * Weight the curse adds to its host object (C: {@code curse->obj->weight}).
     */
    private final int weight;

    /**
     * The effect chain the curse triggers (C: {@code curse->obj->effect}). Each
     * {@link Effect} already carries its own dice/subtype/timing/message, so a
     * curse simply holds the list.
     */
    private final Effect effect;

    /**
     * The object flags this curse grants (C: {@code curse->obj->flags}). Only the
     * non-element entries of the {@code flags:} line; {@code HATES_}/{@code IGNORE_}
     * tokens are routed to {@link #elInfo} instead.
     */
    private final Flag<ObjectFlag> objectFlags;

    /**
     * The additive numeric modifiers this curse applies to its host object
     * (C: {@code curse->obj->modifiers}). Populated from the {@code obj_mods}
     * family of the {@code values:} line; resistances are deliberately excluded
     * (they live in {@link #elInfo}).
     */
    private final Map<ObjectModifier, Integer> modifiers;

    /**
     * Per-element resistance level and hates/ignores flags this curse imposes
     * (C: {@code curse->obj->el_info[ELEM_MAX]}). Fed by two data lines: the
     * {@code RES_*} tokens of the {@code values:} line set {@link ElementInfo}
     * resistance levels, and the {@code HATES_}/{@code IGNORE_} tokens of the
     * {@code flags:} line set its flags.
     */
    private final Map<ElementEnum, ElementInfo> elInfo;

    /**
     * To-hit penalty imposed by the curse (C: {@code curse->obj->to_h}).
     */
    private final int combatToHit;

    /**
     * To-damage penalty imposed by the curse (C: {@code curse->obj->to_d}).
     */
    private final int combatDam;

    /**
     * Armour-class penalty imposed by the curse (C: {@code curse->obj->to_a}).
     */
    private final int combatAC;

    /**
     * The curses this one conflicts with (cannot co-occur on the same object).
     * Resolved from {@link #conflictNames} in a second assembler pass, mirroring
     * the Summon fallback model — C stores only the delimited name string
     * ({@code curse->conflict}) and matches by name, holding no pointer.
     */
    private List<Curse> conflict;

    /**
     * The raw names of conflicting curses as read from the data file, retained
     * for the second-pass resolution into {@link #conflict}.
     */
    private final List<String> conflictNames;

    /**
     * Object flags that conflict with this curse (C:
     * {@code curse->conflict_flags}).
     */
    private final Flag<ObjectFlag> conflictFlags;

    /**
     * Human-readable description of the curse (C: {@code curse->desc}).
     */
    private final String description;

    /**
     * Curse-level flavour message shown when the curse triggers (port
     * {@code msg:} line).
     */
    private final String message;

    /**
     * Build a curse from its assembled fields. This takes already-resolved domain
     * objects (an {@link Effect} list, an {@link ElementInfo} map, a modifier map)
     * rather than raw dice and expression strings — the parsing/lookup work lives
     * in {@code EffectAssembler} and {@code CurseAssembler}. The {@link #conflict}
     * list is left null here and filled by the assembler's second pass from
     * {@code conflictNames}.
     *
     * @param name          curse name
     * @param objectBases   affectable object bases ({@code types:} line)
     * @param weight        added weight
     * @param effect        triggered effect chain
     * @param objectFlags   granted object flags (non-element)
     * @param modifiers     additive numeric modifiers (obj_mods)
     * @param elInfo        per-element resistances and hates/ignores flags
     * @param combatToHit   to-hit penalty
     * @param combatDam     to-damage penalty
     * @param combatAC      armour-class penalty
     * @param conflictNames names of conflicting curses (resolved later)
     * @param conflictFlags conflicting object flags
     * @param description   description
     * @param message       trigger message
     */
    public Curse(String name,
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
                 String message) {
        this.name = name;
        this.objectBases = objectBases;
        this.weight = weight;
        this.effect = effect;
        this.objectFlags = objectFlags;
        this.modifiers = modifiers;
        this.elInfo = elInfo;
        this.combatToHit = combatToHit;
        this.combatDam = combatDam;
        this.combatAC = combatAC;
        this.conflictNames = conflictNames;
        this.conflictFlags = conflictFlags;
        this.description = description;
        this.message = message;
    }

    /**
     * @return the curse's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the object bases this curse may attach to
     */
    public List<ObjectBase> getObjectBases() {
        return objectBases;
    }

    /**
     * @return the weight this curse adds to its host object
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return the effect chain this curse triggers
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * @return the (non-element) object flags this curse grants
     */
    public Flag<ObjectFlag> getObjectFlags() {
        return objectFlags;
    }

    /**
     * @return the additive numeric modifiers this curse applies (obj_mods half of
     * the {@code values:} line); element resistances are held in {@link #getElInfo()}
     */
    public Map<ObjectModifier, Integer> getModifiers() {
        return modifiers;
    }

    /**
     * @return the per-element resistance levels and hates/ignores flags this curse
     * imposes (the {@code RES_*} values and {@code HATES_}/{@code IGNORE_} flags)
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        return elInfo;
    }

    /**
     * @return the to-hit penalty imposed by the curse
     */
    public int getCombatToHit() {
        return combatToHit;
    }

    /**
     * @return the to-damage penalty imposed by the curse
     */
    public int getCombatDam() {
        return combatDam;
    }

    /**
     * @return the armour-class penalty imposed by the curse
     */
    public int getCombatAC() {
        return combatAC;
    }

    /**
     * @return the curses this one conflicts with, resolved by the second pass
     * (may be {@code null} before {@link #setConflict(List)} has run)
     */
    public List<Curse> getConflict() {
        return conflict;
    }

    /**
     * @return the object flags that conflict with this curse
     */
    public Flag<ObjectFlag> getConflictFlags() {
        return conflictFlags;
    }

    /**
     * @return the human-readable description of the curse
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the flavour message shown when the curse triggers
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the raw names of curses this one conflicts with, for second-pass
     * resolution
     */
    public List<String> getConflictNames() {
        return conflictNames;
    }

    /**
     * Set the resolved conflicting curses. Called by the assembler's second pass
     * once every curse has been built and can be looked up by name.
     *
     * @param conflict the resolved conflicting curses
     */
    public void setConflict(List<Curse> conflict) {
        this.conflict = conflict;
    }

    /**
     * @param objectBase the base to test
     * @return true if this curse may attach to the given object base
     */
    public boolean canAfflict(ObjectBase objectBase) {
        return objectBases.contains(objectBase);
    }

    /**
     * @return a debug string listing this curse's fields
     */
    @Override
    public String toString() {
        return "Curse{" +
                "name='" + name + '\'' +
                ", objectBases=" + objectBases +
                ", weight=" + weight +
                ", effects=" + effect +
                ", objectFlags=" + objectFlags +
                ", modifiers=" + modifiers +
                ", elInfo=" + elInfo +
                ", combatToHit=" + combatToHit +
                ", combatDam=" + combatDam +
                ", combatAC=" + combatAC +
                ", conflictNames=" + conflictNames +
                ", conflictFlags=" + conflictFlags +
                ", description='" + description + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * Applies this curse's weight change to an item's weight — the port of C's
     * {@code modify_weight_for_curse} ({@code obj-curse.c:382-430}). Called once per curse of
     * non-zero power when an item's true weight is worked out, so the curses compose by being
     * applied in turn.
     *
     * <p>The curse's own {@code weight} field means one of two different things depending on a
     * flag, which is why this cannot be a plain addition:
     *
     * <ul>
     *   <li>With {@link ObjectFlag#OF_MULTIPLY_WEIGHT} it is a percentage. The incoming weight is
     *       multiplied by it and divided by 100, rounding to nearest — the {@code >= 50} test on the
     *       remainder. A factor above 100 first coerces a weightless item up to 1, so that
     *       multiplying can have an effect on something that would otherwise stay at zero however
     *       heavy the curse.</li>
     *   <li>Without it the field is a flat addend, and may be negative — a curse that makes an item
     *       lighter. A negative result is clamped to zero rather than wrapping.</li>
     * </ul>
     *
     * <p>Both branches saturate at {@link Short#MAX_VALUE} rather than overflowing, because C stores
     * an object's weight in an {@code int16_t} and the arithmetic there is done in a wider type
     * precisely so it can be clamped. The port has no such narrowing, but keeps the ceiling so that
     * a cursed item weighs the same in both.
     *
     * <p>Function modifyWeightForCurse commented in full on 260820.
     *
     * @param weight the item's weight before this curse is applied, in tenth-pounds
     * @return the weight after it, never negative and never above {@link Short#MAX_VALUE}
     * @throws IllegalArgumentException if this curse multiplies weight but its own weight is
     *                                  negative, which C asserts against
     */
    public int modifyWeightForCurse(int weight) {
        int result = weight;

        if (objectFlags.has(ObjectFlag.OF_MULTIPLY_WEIGHT)) {
            if (this.weight < 0) {
                logger.error("Weight cannot be negative.");
                throw new IllegalArgumentException("Weight cannot be negative.");
            }

            int scaled;
            if (this.weight > 100)
                scaled = Math.max(weight, 1);
            else
                scaled = Math.max(weight, 0);

            scaled *= this.weight;

            int quotient = scaled / 100;
            if (quotient < Short.MAX_VALUE) {
                result = quotient;
                if (scaled % 100 >= 50)
                    result++;
            } else
                result = Short.MAX_VALUE;
        } else {
            weight = Math.max(0, weight);
            if (this.weight < 0) {
                result = weight + this.weight;
                if (result < 0) result = 0;
            } else {
                result = (weight < Short.MAX_VALUE - this.weight) ?
                        weight + this.weight : Short.MAX_VALUE;
            }
        }

        return result;
    }
}
