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
 *   <li><b>{@link #effects}</b> — the effect chain (C: {@code curse->obj->effect}).
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
     * The curse's name (C: {@code curse->name}).
     *
     * @author Rowan Crowther
     */
    private final String name;

    /**
     * The object bases this curse may attach to (C: {@code curse->poss}, the
     * per-tval possibility array; port {@code types:} line).
     *
     * @author Rowan Crowther
     */
    private final List<ObjectBase> objectBases;

    /**
     * Weight the curse adds to its host object (C: {@code curse->obj->weight}).
     *
     * @author Rowan Crowther
     */
    private final int weight;

    /**
     * The effect chain the curse triggers (C: {@code curse->obj->effect}). Each
     * {@link Effect} already carries its own dice/subtype/timing/message, so a
     * curse simply holds the list.
     *
     * @author Rowan Crowther
     */
    private final List<Effect> effects;

    /**
     * The object flags this curse grants (C: {@code curse->obj->flags}). Only the
     * non-element entries of the {@code flags:} line; {@code HATES_}/{@code IGNORE_}
     * tokens are routed to {@link #elInfo} instead.
     *
     * @author Rowan Crowther
     */
    private final List<ObjectFlag> objectFlags;

    /**
     * The additive numeric modifiers this curse applies to its host object
     * (C: {@code curse->obj->modifiers}). Populated from the {@code obj_mods}
     * family of the {@code values:} line; resistances are deliberately excluded
     * (they live in {@link #elInfo}).
     *
     * @author Rowan Crowther
     */
    private final Map<ObjectModifier, Integer> modifiers;

    /**
     * Per-element resistance level and hates/ignores flags this curse imposes
     * (C: {@code curse->obj->el_info[ELEM_MAX]}). Fed by two data lines: the
     * {@code RES_*} tokens of the {@code values:} line set {@link ElementInfo}
     * resistance levels, and the {@code HATES_}/{@code IGNORE_} tokens of the
     * {@code flags:} line set its flags.
     *
     * @author Rowan Crowther
     */
    private final Map<ElementEnum, ElementInfo> elInfo;

    /**
     * To-hit penalty imposed by the curse (C: {@code curse->obj->to_h}).
     *
     * @author Rowan Crowther
     */
    private final int combatToHit;

    /**
     * To-damage penalty imposed by the curse (C: {@code curse->obj->to_d}).
     *
     * @author Rowan Crowther
     */
    private final int combatDam;

    /**
     * Armour-class penalty imposed by the curse (C: {@code curse->obj->to_a}).
     *
     * @author Rowan Crowther
     */
    private final int combatAC;

    /**
     * The curses this one conflicts with (cannot co-occur on the same object).
     * Resolved from {@link #conflictNames} in a second assembler pass, mirroring
     * the Summon fallback model — C stores only the delimited name string
     * ({@code curse->conflict}) and matches by name, holding no pointer.
     *
     * @author Rowan Crowther
     */
    private List<Curse> conflict;

    /**
     * The raw names of conflicting curses as read from the data file, retained
     * for the second-pass resolution into {@link #conflict}.
     *
     * @author Rowan Crowther
     */
    private final List<String> conflictNames;

    /**
     * Object flags that conflict with this curse (C:
     * {@code curse->conflict_flags}).
     *
     * @author Rowan Crowther
     */
    private final List<ObjectFlag> conflictFlags;

    /**
     * Human-readable description of the curse (C: {@code curse->desc}).
     *
     * @author Rowan Crowther
     */
    private final String description;

    /**
     * Curse-level flavour message shown when the curse triggers (port
     * {@code msg:} line).
     *
     * @author Rowan Crowther
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
     * @param effects       triggered effect chain
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
     * @author Rowan Crowther
     */
    public Curse(String name,
                 List<ObjectBase> objectBases,
                 int weight,
                 List<Effect> effects,
                 List<ObjectFlag> objectFlags,
                 Map<ObjectModifier, Integer> modifiers,
                 Map<ElementEnum, ElementInfo> elInfo,
                 int combatToHit,
                 int combatDam,
                 int combatAC,
                 List<String> conflictNames,
                 List<ObjectFlag> conflictFlags,
                 String description,
                 String message) {
        this.name = name;
        this.objectBases = objectBases;
        this.weight = weight;
        this.effects = effects;
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
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * @return the object bases this curse may attach to
     * @author Rowan Crowther
     */
    public List<ObjectBase> getObjectBases() {
        return objectBases;
    }

    /**
     * @return the weight this curse adds to its host object
     * @author Rowan Crowther
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return the effect chain this curse triggers
     * @author Rowan Crowther
     */
    public List<Effect> getEffects() {
        return effects;
    }

    /**
     * @return the (non-element) object flags this curse grants
     * @author Rowan Crowther
     */
    public List<ObjectFlag> getObjectFlags() {
        return objectFlags;
    }

    /**
     * @return the additive numeric modifiers this curse applies (obj_mods half of
     * the {@code values:} line); element resistances are held in {@link #getElInfo()}
     * @author Rowan Crowther
     */
    public Map<ObjectModifier, Integer> getModifiers() {
        return modifiers;
    }

    /**
     * @return the per-element resistance levels and hates/ignores flags this curse
     * imposes (the {@code RES_*} values and {@code HATES_}/{@code IGNORE_} flags)
     * @author Rowan Crowther
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        return elInfo;
    }

    /**
     * @return the to-hit penalty imposed by the curse
     * @author Rowan Crowther
     */
    public int getCombatToHit() {
        return combatToHit;
    }

    /**
     * @return the to-damage penalty imposed by the curse
     * @author Rowan Crowther
     */
    public int getCombatDam() {
        return combatDam;
    }

    /**
     * @return the armour-class penalty imposed by the curse
     * @author Rowan Crowther
     */
    public int getCombatAC() {
        return combatAC;
    }

    /**
     * @return the curses this one conflicts with, resolved by the second pass
     * (may be {@code null} before {@link #setConflict(List)} has run)
     * @author Rowan Crowther
     */
    public List<Curse> getConflict() {
        return conflict;
    }

    /**
     * @return the object flags that conflict with this curse
     * @author Rowan Crowther
     */
    public List<ObjectFlag> getConflictFlags() {
        return conflictFlags;
    }

    /**
     * @return the human-readable description of the curse
     * @author Rowan Crowther
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the flavour message shown when the curse triggers
     * @author Rowan Crowther
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the raw names of curses this one conflicts with, for second-pass
     * resolution
     * @author Rowan Crowther
     */
    public List<String> getConflictNames() {
        return conflictNames;
    }

    /**
     * Set the resolved conflicting curses. Called by the assembler's second pass
     * once every curse has been built and can be looked up by name.
     *
     * @param conflict the resolved conflicting curses
     * @author Rowan Crowther
     */
    public void setConflict(List<Curse> conflict) {
        this.conflict = conflict;
    }

    /**
     * @param objectBase the base to test
     * @return true if this curse may attach to the given object base
     * @author Rowan Crowther
     */
    public boolean canAfflict(ObjectBase objectBase) {
        return objectBases.contains(objectBase);
    }

    /**
     * @return a debug string listing this curse's fields
     * @author Rowan Crowther
     */
    @Override
    public String toString() {
        return "Curse{" +
                "name='" + name + '\'' +
                ", objectBases=" + objectBases +
                ", weight=" + weight +
                ", effects=" + effects +
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
}
