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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.util.*;

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
 *   <li><b>{@link #effect}</b> — the effect (C: {@code curse->obj->effect}, a linked chain).
 *       All per-effect data (dice, subtype, timing, message, scaling expressions)
 *       lives inside the {@link Effect}, which is what {@code EffectAssembler}
 *       produces. The port holds a single {@link Effect} rather than a chain, because every
 *       curse in {@code curse.txt} has at most one {@code effect:} block.</li>
 *   <li><b>{@link #modifiers}</b> — the additive numeric bonuses/penalties
 *       (stats, speed, blows, digging, damage reduction, …). C merges these with
 *       simple addition ({@code obj-curse.c}: {@code modifiers[k] += ...}), so a
 *       plain {@code Map<ObjectModifier,Integer>} is the faithful shape.</li>
 *   <li><b>{@link #elInfo}</b> — the <em>element</em> half of the merge. Per
 *       element it holds a resistance level (from the {@code values:} line's
 *       {@code RES_*} tokens) <em>and</em> the hates/ignores flags (from the
 *       {@code flags:} line's {@code HATES_}/{@code IGNORE_} tokens). Resistances
 *       do <b>not</b> merge additively — C applies special immunity/vulnerability
 *       logic ({@code obj-curse.c}, function {@code apply_curse_attributes()}) that must read a
 *       per-element {@code res_level}, which is why {@link ElementInfo} indexed by
 *       {@link ElementEnum} is required and a flat list would lose information.
 *       Element resistances therefore live here, not among the additive
 *       {@link ObjectModifier} entries in {@link #modifiers}.</li>
 * </ul>
 *
 * <p>The {@code known*} fields are the port's flattening of {@code curse->obj->known}: what the
 * player is currently entitled to read off the curse, written by {@code PlayerKnowledge.knowObject}
 * and read back by {@link #isFullyKnown()}. Three fields have no counterpart in C's
 * {@code struct curse}: {@link #index} (C uses the position in {@code curses[]}),
 * {@link #message} (C keeps it in {@code curse->obj->effect_msg}) and {@link #conflictNames}
 * (the raw form of C's {@code conflict} string, kept until the second assembler pass resolves it).
 *
 * <p>Class Curse coded before 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
public class Curse {
    /**
     * Log destination for the conditions C asserts on. C's {@code modify_weight_for_curse} asserts
     * that a curse's weight is not negative (in {@code obj-curse.c}, function
     * {@code modify_weight_for_curse()}); the port logs and throws instead, so a malformed data file
     * spoils one calculation rather than the process.
     *
     * <p>Field logger coded before 261005, commented in full on 261005.
     */
    private static final Logger logger = LogManager.getLogger(Curse.class);
    
    /**
     * The curse's name (C: {@code curse->name}), from the {@code name:} line of {@code curse.txt}.
     * Conflicts between curses are matched on it, and it is what {@code lookup_curse} in
     * {@code obj-curse.c} searches by.
     *
     * <p>Field name coded before 261005, commented in full on 261005.
     */
    private final String name;

    /**
     * This curse's index, passed in at construction - the port's counterpart of C's index into
     * {@code curses[]}. C has no field for it; the position in the array is the index. The numbering
     * differs: C leaves slot 0 as an unused dummy, so its first real curse is 1, while
     * {@code CurseAssembler} gives the first curse it builds index 0. Anything that compares an index
     * against a C-derived value has to allow for that offset.
     *
     * <p>Field index coded before 261005, commented in full on 261005.
     */
    private final int index;

    /**
     * The object bases this curse may attach to (C: {@code curse->poss}, the
     * per-tval possibility array; port {@code types:} line). C holds a {@code bool} per tval, set
     * once for each {@code type:} line; the port holds the {@link ObjectBase} for each of them, and
     * {@link #canAfflict(ObjectBase)} is the lookup. A curse with no {@code type:} line can attach
     * to nothing.
     *
     * <p>Field objectBases coded before 261005, commented in full on 261005.
     */
    private final List<ObjectBase> objectBases;

    /**
     * The curse's own object — the port's counterpart of C's {@code curse->obj}, the
     * {@code struct object} that the {@code curses[]} entry carries. It is a fresh, empty
     * {@link ItemObject} created by the constructor and handed out by {@link #getItemObject()}. The
     * mechanical properties the data file sets are held in this class's own fields rather than on
     * it, so nothing in this class reads or writes it.
     *
     * <p>Field itemObject coded before 261005, commented in full on 261005.
     */
    private final ItemObject itemObject;

    /**
     * Weight the curse adds to its host object (C: {@code curse->obj->weight}), from the
     * {@code weight:} line. Its meaning depends on {@link ObjectFlag#OF_MULTIPLY_WEIGHT}: with the
     * flag it is a percentage the host's weight is scaled by, without it a flat addend in
     * tenth-pounds that may be negative. {@link #modifyWeightForCurse(int)} is the one reader.
     *
     * <p>Field weight coded before 261005, commented in full on 261005.
     */
    private final int weight;
    /**
     * The object flags this curse grants (C: {@code curse->obj->flags}). Only the
     * non-element entries of the {@code flags:} line; {@code HATES_}/{@code IGNORE_}
     * tokens are routed to {@link #elInfo} instead. {@link #modifyWeightForCurse(int)} reads it for
     * {@link ObjectFlag#OF_MULTIPLY_WEIGHT}, and {@link #isFullyKnown()} tests it against
     * {@link #knownObjectFlags}.
     *
     * <p>Field objectFlags coded before 261005, commented in full on 261005.
     */
    private final Flag<ObjectFlag> objectFlags;
    /**
     * The raw names of conflicting curses as read from the data file, retained
     * for the second-pass resolution into {@link #conflict}. C keeps the same information as one
     * string of names, each wrapped in {@code |} delimiters, which it searches by substring.
     *
     * <p>Field conflictNames coded before 261005, commented in full on 261005.
     */
    private final List<String> conflictNames;
    /**
     * Object flags that conflict with this curse (C:
     * {@code curse->conflict_flags}), from the {@code conflict-flags:} line. A host object carrying
     * any of them cannot take this curse.
     *
     * <p>Field conflictFlags coded before 261005, commented in full on 261005.
     */
    private final Flag<ObjectFlag> conflictFlags;
    /**
     * Human-readable description of the curse (C: {@code curse->desc}), the concatenated
     * {@code desc:} lines of the data file.
     *
     * <p>Field description coded before 261005, commented in full on 261005.
     */
    private final String description;
    /**
     * Curse-level flavour message shown when the curse triggers (port
     * {@code msg:} line). C stores this on the curse's object as {@code curse->obj->effect_msg};
     * the port keeps it on the curse itself, so it is separate from the message carried by the
     * {@link Effect}.
     *
     * <p>Field message coded before 261005, commented in full on 261005.
     */
    private final String message;
    /**
     * The effect the curse triggers (C: {@code curse->obj->effect}), or {@code null} for a curse
     * with no {@code effect:} block, such as <em>air swing</em>. C holds a linked chain; the port
     * holds one {@link Effect}, which already carries its own dice, subtype, message and the
     * {@code time:} dice (see {@link #getTime()}). That is enough because no curse in
     * {@code curse.txt} has more than one {@code effect:} block.
     *
     * <p>Field effect coded before 261005, commented in full on 261005.
     */
    private Effect effect;
    /**
     * The additive numeric modifiers this curse applies to its host object
     * (C: {@code curse->obj->modifiers}). Populated from the {@code obj_mods}
     * family of the {@code values:} line; resistances are deliberately excluded
     * (they live in {@link #elInfo}). C holds a dense array with a slot for every modifier; the
     * port holds only the modifiers the curse names, so an absent key means zero. May be
     * {@code null} for a curse built without one; read it through {@link #getModifiers()}.
     *
     * <p>Field modifiers coded before 261005, commented in full on 261005.
     */
    private Map<ObjectModifier, Integer> modifiers;
    /**
     * Per-element resistance level and hates/ignores flags this curse imposes
     * (C: {@code curse->obj->el_info[ELEM_MAX]}). Fed by two data lines: the
     * {@code RES_*} tokens of the {@code values:} line set {@link ElementInfo}
     * resistance levels, and the {@code HATES_}/{@code IGNORE_} tokens of the
     * {@code flags:} line set its flags. C holds a slot for every element; the port holds only the
     * elements the curse names. May be {@code null} for a curse built without one; read it through
     * {@link #getElInfo()}.
     *
     * <p>Field elInfo coded before 261005, commented in full on 261005.
     */
    private Map<ElementEnum, ElementInfo> elInfo;
    /**
     * To-hit penalty imposed by the curse (C: {@code curse->obj->to_h}), the first figure of the
     * {@code combat:} line. {@link #isFullyKnown()} compares it against {@link #knownCombatToHit}.
     *
     * <p>Field combatToHit coded before 261005, commented in full on 261005.
     */
    private int combatToHit;
    /**
     * To-damage penalty imposed by the curse (C: {@code curse->obj->to_d}), the second figure of
     * the {@code combat:} line. {@link #isFullyKnown()} compares it against
     * {@link #knownCombatToDam}.
     *
     * <p>Field combatDam coded before 261005, commented in full on 261005.
     */
    private int combatDam;
    /**
     * Armour-class penalty imposed by the curse (C: {@code curse->obj->to_a}), the third figure of
     * the {@code combat:} line. {@link #isFullyKnown()} compares it against
     * {@link #knownCombatToAC}.
     *
     * <p>Field combatAC coded before 261005, commented in full on 261005.
     */
    private int combatAC;
    /**
     * The curses this one conflicts with (cannot co-occur on the same object).
     * Resolved from {@link #conflictNames} in a second assembler pass, mirroring
     * the Summon fallback model — C stores only the delimited name string
     * ({@code curse->conflict}) and matches by name, holding no pointer. {@code null} until
     * {@link #setConflict(List)} has run.
     *
     * <p>Field conflict coded before 261005, commented in full on 261005.
     */
    private List<Curse> conflict;
    /**
     * The to-hit penalty the player can currently read off this curse
     * (C: {@code curse->obj->known->to_h}). Starts at zero, as C's zeroed known object does, and is
     * written by {@link #setKnownCombatToHit(int)}.
     *
     * <p>Field knownCombatToHit coded before 261005, commented in full on 261005.
     */
    private int knownCombatToHit;

    /**
     * The to-damage penalty the player can currently read off this curse
     * (C: {@code curse->obj->known->to_d}). Starts at zero and is written by
     * {@link #setKnownCombatToDam(int)}.
     *
     * <p>Field knownCombatToDam coded before 261005, commented in full on 261005.
     */
    private int knownCombatToDam;

    /**
     * The armour-class penalty the player can currently read off this curse
     * (C: {@code curse->obj->known->to_a}). Starts at zero and is written by
     * {@link #setKnownCombatToAC(int)}.
     *
     * <p>Field knownCombatToAC coded before 261005, commented in full on 261005.
     */
    private int knownCombatToAC;

    /**
     * The modifiers the player can currently read off this curse
     * (C: {@code curse->obj->known->modifiers}). Never {@code null}; empty until
     * {@link #setKnownModifiers(Map)} has run, which {@link #isFullyKnown()} reads as all zeros.
     *
     * <p>Field knownModifiers coded before 261005, commented in full on 261005.
     */
    private Map<ObjectModifier, Integer> knownModifiers;

    /**
     * The per-element information the player can currently read off this curse
     * (C: {@code curse->obj->known->el_info}). Never {@code null}; empty until
     * {@link #setKnownElInfo(Map)} or {@link #putKnownElementInfo(ElementEnum, ElementInfo)} has
     * run.
     *
     * <p>Field knownElInfo coded before 261005, commented in full on 261005.
     */
    private Map<ElementEnum, ElementInfo> knownElInfo;

    /**
     * The object flags the player can currently read off this curse
     * (C: {@code curse->obj->known->flags}). Never {@code null}; emptied and refilled in place by
     * {@link #setKnownObjectFlags(Flag)}.
     *
     * <p>Field knownObjectFlags coded before 261005, commented in full on 261005.
     */
    private Flag<ObjectFlag> knownObjectFlags;

    /**
     * The effect the player can currently read off this curse
     * (C: {@code curse->obj->known->effect}), the same {@link Effect} instance as {@link #effect}
     * once known and {@code null} before. {@link #isFullyKnown()} compares the two by identity, as
     * C's {@code object_effect_is_known} does.
     *
     * <p>Field knownEffect coded before 261005, commented in full on 261005.
     */
    private Effect knownEffect;


    /**
     * Build a curse from its assembled fields. This takes already-resolved domain
     * objects (an {@link Effect}, an {@link ElementInfo} map, a modifier map)
     * rather than raw dice and expression strings — the parsing/lookup work lives
     * in {@code EffectAssembler} and {@code CurseAssembler}. The {@link #conflict}
     * list is left null here and filled by the assembler's second pass from
     * {@code conflictNames}.
     *
     * <p>Every {@code known*} field starts empty or zero, as C's freshly allocated
     * {@code curse->obj->known} does, and {@link #itemObject} is a new empty {@link ItemObject}.
     * No argument is copied or checked: the maps, lists and flag sets are stored by reference.
     *
     * <p>Constructor Curse coded before 261005, commented in full on 261005.
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
                 String message,
                 int index) {
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
        this.index = index;
        knownElInfo = new HashMap<>();
        knownObjectFlags = new Flag<>(ObjectFlag.class);
        knownModifiers = new HashMap<>();
        this.itemObject = new ItemObject();
    }

    /**
     * The curse's own object, the port's counterpart of C's {@code curse->obj}. It is the empty
     * {@link ItemObject} the constructor made; the curse's mechanical properties are not stored on
     * it but in this class's own fields (see {@link #itemObject}).
     *
     * <p>Function getItemObject coded before 261005, commented in full on 261005.
     *
     * @return this curse's own object; never {@code null}, and the same instance on every call
     */
    public ItemObject getItemObject() {
        return itemObject;
    }

    /**
     * <p>Function getIndex coded before 261003, commented in full on 261003.
     *
     * @return this curse's index, as passed to the constructor
     */
    public int getIndex() {
        return index;
    }

    /**
     * The curse's name (C: {@code curse->name}), as written on the {@code name:} line.
     *
     * <p>Function getName coded before 261005, commented in full on 261005.
     *
     * @return the curse's name
     */
    public String getName() {
        return name;
    }

    /**
     * The object bases this curse may attach to — the {@code types:} lines of the data file, and
     * the port's form of C's {@code curse->poss} array. Use {@link #canAfflict(ObjectBase)} to ask
     * about a single base.
     *
     * <p>Function getObjectBases coded before 261005, commented in full on 261005.
     *
     * @return the object bases this curse may attach to; the curse's own list, not a copy
     */
    public List<ObjectBase> getObjectBases() {
        return objectBases;
    }

    /**
     * The raw weight figure of the curse (C: {@code curse->obj->weight}). This is a percentage or an
     * addend depending on {@link ObjectFlag#OF_MULTIPLY_WEIGHT}, so a caller wanting an item's
     * resulting weight should use {@link #modifyWeightForCurse(int)} rather than add this.
     *
     * <p>Function getWeight coded before 261005, commented in full on 261005.
     *
     * @return the weight figure from the {@code weight:} line, in tenth-pounds or percent
     */
    public int getWeight() {
        return weight;
    }

    /**
     * The effect this curse triggers (C: {@code curse->obj->effect}). The {@code time:} dice sit on
     * this effect; see {@link #getTime()} for the form that is safe to call on a curse with no
     * effect.
     *
     * <p>Function getEffect coded before 261005, commented in full on 261005.
     *
     * @return the curse's effect, or {@code null} if it has no {@code effect:} block
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * The object flags this curse grants (C: {@code curse->obj->flags}). The {@code HATES_} and
     * {@code IGNORE_} tokens are not here; they are in {@link #getElInfo()}.
     *
     * <p>Function getObjectFlags coded before 261005, commented in full on 261005.
     *
     * @return the (non-element) object flags this curse grants; the curse's own set, not a copy
     */
    public Flag<ObjectFlag> getObjectFlags() {
        return objectFlags;
    }

    /**
     * The additive numeric modifiers this curse applies (C: {@code curse->obj->modifiers}). C's
     * array has a slot for every modifier, so a reader never meets a missing one; the port holds
     * only the modifiers the curse names, so a reader should treat an absent key as zero. A curse
     * built with a {@code null} map answers an empty one, so every caller can read it without a
     * guard, as {@link #getElInfo()} does. The map is the curse's own and is not copied.
     *
     * <p>Function getModifiers coded before 261005, commented in full on 261005.
     *
     * @return the additive numeric modifiers this curse applies (obj_mods half of
     * the {@code values:} line), or an empty map if it has none; element resistances are held in
     * {@link #getElInfo()}
     */
    public Map<ObjectModifier, Integer> getModifiers() {
        if (modifiers == null)
            return Map.of();
        
        return modifiers;
    }

    /**
     * The per-element resistance levels and hates/ignores flags this curse imposes (the
     * {@code RES_*} values and {@code HATES_}/{@code IGNORE_} flags), C's
     * {@code curse->obj->el_info}.
     *
     * <p>C's array has a slot for every element, so a reader never meets a missing one. The port
     * holds only the elements the curse names, and answers an empty map for a curse built with a
     * {@code null} map, as {@link #getModifiers()} does, so every caller can read it without a
     * guard. The map is the curse's own and is not copied.
     *
     * <p>Function getElInfo coded before 261003, commented in full on 261003.
     *
     * @return the per-element information, or an empty map if the curse has none
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        if (elInfo == null)
            return Map.of();
        
        return elInfo;
    }

    /**
     * The to-hit figure of the curse (C: {@code curse->obj->to_h}). Three curses in
     * {@code curse.txt} set a non-zero one: <em>enveloping</em>, <em>irritation</em> and
     * <em>air swing</em>.
     *
     * <p>Function getCombatToHit coded before 261005, commented in full on 261005.
     *
     * @return the to-hit penalty imposed by the curse (negative for a penalty)
     */
    public int getCombatToHit() {
        return combatToHit;
    }

    /**
     * The to-damage figure of the curse (C: {@code curse->obj->to_d}).
     *
     * <p>Function getCombatDam coded before 261005, commented in full on 261005.
     *
     * @return the to-damage penalty imposed by the curse (negative for a penalty)
     */
    public int getCombatDam() {
        return combatDam;
    }

    /**
     * The armour-class figure of the curse (C: {@code curse->obj->to_a}).
     *
     * <p>Function getCombatAC coded before 261005, commented in full on 261005.
     *
     * @return the armour-class penalty imposed by the curse (negative for a penalty)
     */
    public int getCombatAC() {
        return combatAC;
    }

    /**
     * The curses this one conflicts with. C has no such list: it searches the delimited
     * {@code curse->conflict} name string each time ({@code curses_conflict} in
     * {@code obj-curse.c}), so the port's list is the resolved form of that string.
     *
     * <p>Function getConflict coded before 261005, commented in full on 261005.
     *
     * @return the curses this one conflicts with, resolved by the second pass
     * (may be {@code null} before {@link #setConflict(List)} has run)
     */
    public List<Curse> getConflict() {
        return conflict;
    }

    /**
     * Set the resolved conflicting curses. Called by the assembler's second pass
     * once every curse has been built and can be looked up by name. The list is stored, not copied.
     *
     * <p>Function setConflict coded before 261005, commented in full on 261005.
     *
     * @param conflict the resolved conflicting curses
     */
    public void setConflict(List<Curse> conflict) {
        this.conflict = conflict;
    }

    /**
     * The object flags that stop this curse attaching to an object (C:
     * {@code curse->conflict_flags}); C's {@code append_object_curse} rejects the curse if the
     * object has any of them.
     *
     * <p>Function getConflictFlags coded before 261005, commented in full on 261005.
     *
     * @return the object flags that conflict with this curse; the curse's own set, not a copy
     */
    public Flag<ObjectFlag> getConflictFlags() {
        return conflictFlags;
    }

    /**
     * The text shown for the curse (C: {@code curse->desc}), the {@code desc:} lines joined.
     *
     * <p>Function getDescription coded before 261005, commented in full on 261005.
     *
     * @return the human-readable description of the curse
     */
    public String getDescription() {
        return description;
    }

    /**
     * The message shown when the curse triggers — the {@code msg:} line, which C keeps as
     * {@code curse->obj->effect_msg}.
     *
     * <p>Function getMessage coded before 261005, commented in full on 261005.
     *
     * @return the flavour message shown when the curse triggers
     */
    public String getMessage() {
        return message;
    }

    /**
     * The names of the curses this one conflicts with, exactly as the {@code conflict:} lines gave
     * them. This is what the assembler's second pass resolves into {@link #getConflict()}.
     *
     * <p>Function getConflictNames coded before 261005, commented in full on 261005.
     *
     * @return the raw names of curses this one conflicts with, for second-pass
     * resolution; the curse's own list, not a copy
     */
    public List<String> getConflictNames() {
        return conflictNames;
    }

    /**
     * Whether this curse may attach to an object of the given base — the port's form of C's
     * {@code curse->poss[obj->tval]} test, made in {@code obj-make.c}, {@code obj-randart.c} and
     * {@code effect-handler-general.c}. The test is by {@link List#contains} on the list built from
     * the {@code types:} lines, so it depends on {@link ObjectBase} equality.
     *
     * <p>Function canAfflict coded before 261005, commented in full on 261005.
     *
     * @param objectBase the base to test
     * @return true if this curse may attach to the given object base
     */
    public boolean canAfflict(ObjectBase objectBase) {
        return objectBases.contains(objectBase);
    }

    /**
     * A debugging aid with no counterpart in C. It lists the fields the data file sets, and leaves
     * out {@link #index}, {@link #itemObject}, {@link #conflict} and every {@code known*} field.
     *
     * <p>Function toString coded before 261005, commented in full on 261005.
     *
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
     * {@code modify_weight_for_curse} ({@code obj-curse.c}). C calls it from two places: once per
     * curse of non-zero power when an item's true weight is worked out ({@code object_weight_one} in
     * {@code obj-util.c}), and from {@code apply_curse_attributes} in {@code obj-curse.c} for every
     * other active curse, so the curses compose by being applied in turn.
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
     * <p>Function modifyWeightForCurse coded before 260820, commented in full on 261005, C line
     * number removed on 261005.
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

    /**
     * Whether this curse's to-hit figure is the unremarkable one for its bearer — the curse arm of
     * C's {@code object_has_standard_to_h} ({@code obj-knowledge.c}). The question behind the
     * name is not "is it zero" but "is it worth telling the player about": an ordinary to-hit
     * teaches nothing, so {@code PlayerKnowledge.knowObject} declines to write it into the known
     * figure.
     *
     * <p><b>This is C's answer for a curse object, not a divergence from it.</b> C's first line in
     * {@code object_has_standard_to_h} is a commented hack that returns true for an object with a
     * null kind. A curse object does not have one at runtime: {@code write_curse_kinds} in
     * {@code obj-init.c} gives it the {@code <curse object>} kind, whose type is {@code none}, so
     * the hack is not taken. The body-armour branch is not taken either, because the type is not
     * body armour, and what remains is C's {@code obj->to_h == 0} test, which is this method.
     * {@link ItemObject#hasStandardToH()} keeps the null-kind line, which only an item without a
     * kind reaches.
     *
     * <p>The test matters where a curse carries a non-zero to-hit, which three in {@code curse.txt}
     * do — <em>enveloping</em> (-5), <em>irritation</em> (-15) and <em>air swing</em> (-20). For
     * those, C copies the to-hit into the known figure once the player can read to-hit, and
     * {@link #isFullyKnown()} then compares {@code combatToHit} against {@code knownCombatToHit}
     * exactly as {@code object_non_curse_runes_known} does. For every other curse the to-hit is zero,
     * the known figure stays zero, and the comparison holds without the player learning anything.
     *
     * <p>Function hasStandardToH coded before 260901, commented in full on 261004, null-kind claim
     * corrected on 261004, C line number removed on 261003.
     *
     * @return true if this curse imposes no to-hit penalty
     */
    public boolean hasStandardToH() {
        return combatToHit == 0;
    }

    /**
     * Records the to-hit penalty the player is currently entitled to read off this curse
     * (C: {@code curse->obj->known->to_h}). Written only by {@code PlayerKnowledge.knowObject}, and
     * only when {@link #hasStandardToH()} says the figure is remarkable; a curse whose to-hit is
     * unremarkable leaves this at zero.
     *
     * <p>Function setKnownCombatToHit coded before 260901, commented in full on 260901.
     *
     * @param toHit the known to-hit penalty — the real one where the player can read to-hit,
     *              otherwise zero
     */
    public void setKnownCombatToHit(int toHit) {
        knownCombatToHit = toHit;
    }

    /**
     * Records the to-damage penalty the player is currently entitled to read off this curse
     * (C: {@code curse->obj->known->to_d}). Written only by {@code PlayerKnowledge.knowObject},
     * where the value arrives already masked by the player's knowledge bit, so zero means "cannot
     * read it" as much as "there is none" — C's idiom, and the zero is what the display wants for
     * both.
     *
     * <p>Function setKnownCombatToDam coded before 260901, commented in full on 260901.
     *
     * @param toDam the known to-damage penalty
     */
    public void setKnownCombatToDam(int toDam) {
        knownCombatToDam = toDam;
    }

    /**
     * Records the armour-class penalty the player is currently entitled to read off this curse
     * (C: {@code curse->obj->known->to_a}). As with {@link #setKnownCombatToDam(int)} the value is
     * pre-masked by the player's knowledge bit.
     *
     * <p>Function setKnownCombatToAC coded before 260901, commented in full on 260901.
     *
     * @param toAC the known armour-class penalty
     */
    public void setKnownCombatToAC(int toAC) {
        knownCombatToAC = toAC;
    }

    /**
     * Replaces the modifiers the player is entitled to read off this curse
     * (C: {@code curse->obj->known->modifiers}). {@code PlayerKnowledge.knowObject} builds the map
     * whole — every {@link ObjectModifier} present with a zero, then the known ones overwritten with
     * their real values — so this stores the reference rather than merging into what was there.
     * That is C's dense array rebuilt each pass, and it is why the map is taken over wholesale: a
     * modifier the player has since stopped being able to read must go back to zero, which a merge
     * would not do.
     *
     * <p>Function setKnownModifiers coded before 260901, commented in full on 260901.
     *
     * @param modifiers the freshly derived known-modifier map; stored, not copied
     */
    public void setKnownModifiers(Map<ObjectModifier, Integer> modifiers) {
        if (modifiers == null) {
            this.knownModifiers = new HashMap<>();
            return;
        }
        
        this.knownModifiers = modifiers;
    }

    /**
     * Whether the player understands everything this curse does — the curse-shaped port of C's
     * {@code object_fully_known} ({@code obj-knowledge.c}) and the two predicates beneath it,
     * {@code object_runes_known} and {@code object_non_curse_runes_known}
     * ({@code obj-knowledge.c}), collapsed into one method because most of what they test
     * cannot arise on a curse.
     *
     * <p><b>What survives the collapse</b> is C's checklist in C's order: the three combat figures
     * compared exactly, every modifier compared exactly, every element with a real resistance
     * required to have a known one, the real flags required to be a subset of the known flags, and
     * finally the effect compared by identity. What falls away is everything about a nested object:
     * C's brand, slay and curse blocks, and the {@code curses_are_equal} call at the head of
     * {@code object_runes_known}. A curse has no brands, no slays and no curses of its own, and it
     * is not something that can carry a known counterpart with a different kind, so the null-known
     * guards go too.
     *
     * <p><b>The flag test reads backwards and is right.</b> C's
     * {@code of_is_subset(obj->known->flags, obj->flags)} asks whether the real flags are contained
     * in the known ones; {@link Flag#isSubset(uk.co.jackoftradesltd.channel.utils.FlagView)} answers
     * whether its <em>argument</em> is a subset of the receiver, so
     * {@code knownObjectFlags.isSubset(objectFlags)} is the same question with the arguments in the
     * same places.
     *
     * <p><b>The modifier loop reads absence as zero, as C's zeroed array does.</b> C compares all
     * {@code OBJ_MOD_MAX} slots of a dense array. This walks every real modifier, skipping
     * {@code OM_NONE} and {@code OM_MAX}, and reads each side with a default of zero, because
     * {@link #modifiers} holds only the modifiers the curse's data lines name and the known map is
     * empty until {@code PlayerKnowledge.knowObject} has run. A curse that confers no modifiers and
     * has never been through {@code knowObject} therefore compares equal on this block, as a curse
     * object with two zeroed arrays does in C.
     *
     * <p><b>C does ask this question of a curse.</b> The "Curse object structures are finished now"
     * return in {@code player_know_object} is never taken for a curse object, because
     * {@code write_curse_kinds} in {@code obj-init.c} gives it a kind, so C runs the effect
     * assignment and the fully-known block on it. This method answers the same question for the
     * flattened curse, including the to-hit comparison discussed at {@link #hasStandardToH()}.
     *
     * <p>Function isFullyKnown coded before 260901, commented in full on 261004, modifier-loop and
     * null-kind paragraphs corrected on 261004, C line numbers removed on 261004.
     *
     * @return true if every property this curse confers is one the player can currently read
     */
    public boolean isFullyKnown() {
        if (combatToHit != knownCombatToHit
                || combatDam != knownCombatToDam
                || combatAC != knownCombatToAC)
            return false;

        for (ObjectModifier om : ObjectModifier.values()) {
            if (om == ObjectModifier.OM_MAX || om == ObjectModifier.OM_NONE) continue;
            if (!Objects.equals(knownModifiers.getOrDefault(om, 0), getModifiers().getOrDefault(om, 0)))
                return false;
        }

        for (ElementEnum em : getElInfo().keySet()) {
            if ((getElInfo().get(em).getResLevel() != 0)
                    && (!knownElInfo.containsKey(em)
                    || knownElInfo.get(em).getResLevel() == 0))
                return false;
        }

        if (!knownObjectFlags.isSubset(objectFlags))
            return false;

        return effect == knownEffect;
    }

    /**
     * Replaces the object flags the player is entitled to read off this curse
     * (C: {@code of_wipe(obj->known->flags)} followed by the flag-by-flag copy in
     * {@code player_know_object}). The wipe before the copy is C's and is load-bearing: the flags
     * are derived afresh from what the player knows now, so a flag that was readable and no longer
     * is has to disappear rather than linger.
     *
     * <p>Unlike its two neighbours this copies into the existing {@link Flag} rather than taking
     * the caller's, so {@link #knownObjectFlags} is never null and never aliases the player's own
     * knowledge set.
     *
     * <p>Function setKnownObjectFlags coded before 260901, commented in full on 260901.
     *
     * @param flags the flags to copy in — the intersection of the player's known flags with this
     *              curse's own
     */
    public void setKnownObjectFlags(Flag<ObjectFlag> flags) {
        knownObjectFlags.wipe();
        knownObjectFlags.copyFrom(flags);
    }

    /**
     * Records the effect chain the player is entitled to read off this curse
     * (C: {@code curse->obj->known->effect}). Held as a reference to the very same {@link Effect},
     * not a copy, because knowledge of an effect is tested by identity —
     * {@code obj->effect == obj->known->effect} in C's {@code object_effect_is_known}, and
     * {@code effect == knownEffect} in {@link #isFullyKnown()}. A copy would compare unequal and
     * the curse could never read as fully known.
     *
     * <p>Function setKnownEffect coded before 260901, commented in full on 260901.
     *
     * @param first the head of this curse's effect chain, or null while it is unknown
     */
    public void setKnownEffect(Effect first) {
        knownEffect = first;
    }

    /**
     * Writes one element's known information, leaving the rest of the map alone — the single-entry
     * counterpart to {@link #setKnownElInfo(Map)}. This is what the fully-known pass of
     * {@code PlayerKnowledge.knowObject} uses: once a curse is completely understood its known
     * element view is promoted entry by entry to the real values, so the player sees what the curse
     * actually does rather than what it would be doing if it had the elements they can read.
     *
     * <p>The {@link ElementInfo} passed in should be a {@link ElementInfo#copy()}, for the aliasing
     * reason given on {@link #setKnownElInfo(Map)}.
     *
     * <p>Function putKnownElementInfo coded before 260901, commented in full on 260901.
     *
     * @param em the element to record
     * @param ei the information to record for it
     */
    public void putKnownElementInfo(ElementEnum em, ElementInfo ei) {
        this.knownElInfo.put(em, ei);
    }

    /**
     * The recharge/duration dice this curse's {@code time:} line sets — the port's read of C's
     * {@code curse->obj->time} (a field of {@code struct object} in {@code object.h}, not of
     * {@code struct effect}): C's effect struct carries no {@code time} member at all, so
     * {@code parse_curse_time} in {@code obj-init.c} writes straight onto the curse's own
     * object.
     *
     * <p>This class has no field of its own to read that from. The port instead folds the
     * {@code time:} data line onto the single {@link Effect} the curse's {@code effect:} line
     * produces — the grammar captures it as that effect block's trailing {@code timeDiceString}
     * ({@code EffectBlock.g4}), and {@code CurseAssembler} carries the built {@link Effect} (with its
     * timing dice already set) straight into {@link #effect}. That is only equivalent to C's
     * per-curse field because every curse in {@code curse.txt} has at most one {@code effect:} block,
     * so there is never a second effect for the timing to be mistaken for.
     *
     * <p>A curse can have no {@code effect:} block at all — <em>air swing</em> is one, combat penalty
     * only, no {@code time:} either. C's {@code curse->obj} is still a {@code mem_zalloc}'d object
     * there, so {@code curse->obj->time} is a zero {@code random_value} rather than a missing one, and
     * every caller ({@code copy_curses} and {@code append_object_curse} in {@code obj-curse.c}, and
     * the curse-timeout loop in {@code game-world.c}) reads it unconditionally. With no
     * {@link #effect} to delegate to, this returns an equivalent
     * zero-valued {@link Random} rather than propagating a {@code null}, so it agrees with C's answer
     * of {@code 0} under every {@link uk.co.jackoftradesltd.middle.enums.DamageAspect} instead of
     * throwing.
     *
     * <p>An effect that has no {@code time:} line yields a {@code null} here, not a zero
     * {@link Random}, because {@code EffectAssembler} leaves its time {@code null} when the line is
     * absent. The data does not exercise that: in {@code curse.txt} every curse that has an
     * {@code effect:} block also has a {@code time:} line.
     *
     * <p>Function getTime coded on 260904, commented in full on 261005, C line numbers removed on
     * 261005.
     *
     * @return this curse's timing dice, read off its one effect, or a zero-valued {@link Random} if
     * it has no effect
     */
    public Random getTime() {
        if (effect != null)
            return effect.getTime();
        return new Random(0, 0, 0, 1, false);
    }

    /**
     * The object flags the player can currently read off this curse
     * (C: {@code curse->obj->known->flags}). This is the field itself rather than a copy.
     *
     * <p>Function getKnownObjectFlags coded before 261005, commented in full on 261005.
     *
     * @return the object flags the player is currently entitled to read off this curse
     * (see {@link #setKnownObjectFlags(Flag)}); never {@code null}
     */
    public Flag<ObjectFlag> getKnownObjectFlags() {
        return knownObjectFlags;
    }

    /**
     * The per-element information the player can currently read off this curse
     * (C: {@code curse->obj->known->el_info}). This is the field itself rather than a copy, and
     * holds only the elements that have been written to it.
     *
     * <p>Function getKnownElInfo coded before 261005, commented in full on 261005.
     *
     * @return the per-element information the player is currently entitled to read off this curse
     * (see {@link #setKnownElInfo(Map)} and {@link #putKnownElementInfo(ElementEnum, ElementInfo)});
     * never {@code null}
     */
    public Map<ElementEnum, ElementInfo> getKnownElInfo() {
        return knownElInfo;
    }

    /**
     * Replaces the per-element information the player is entitled to read off this curse
     * (C: {@code curse->obj->known->el_info}). Rebuilt whole by
     * {@code PlayerKnowledge.knowObject} for the same reason as {@link #setKnownModifiers(Map)},
     * and stored by reference — {@link #putKnownElementInfo(ElementEnum, ElementInfo)} then writes
     * into the map this hands over.
     *
     * <p>The {@link ElementInfo} values are copies, not the curse's own: C assigns
     * {@code res_level} and {@code flags} field by field into a separate struct, so the known view
     * must not alias the real one.
     *
     * <p>A {@code null} argument resets the known view to an empty map. It never touches the
     * curse's real {@link #elInfo}.
     *
     * <p>Function setKnownElInfo coded before 260901, commented in full on 261003.
     *
     * @param knownElInfo the freshly derived known element map; stored, not copied, or
     *                    {@code null} for an empty one
     */
    public void setKnownElInfo(Map<ElementEnum, ElementInfo> knownElInfo) {
        if (knownElInfo == null) {
            this.knownElInfo = new HashMap<>();
            return;
        }
            
        this.knownElInfo = knownElInfo;
    }
}
