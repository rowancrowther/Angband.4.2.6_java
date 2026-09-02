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
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private Effect effect;

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
    private Map<ObjectModifier, Integer> modifiers;

    /**
     * Per-element resistance level and hates/ignores flags this curse imposes
     * (C: {@code curse->obj->el_info[ELEM_MAX]}). Fed by two data lines: the
     * {@code RES_*} tokens of the {@code values:} line set {@link ElementInfo}
     * resistance levels, and the {@code HATES_}/{@code IGNORE_} tokens of the
     * {@code flags:} line set its flags.
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * To-hit penalty imposed by the curse (C: {@code curse->obj->to_h}).
     */
    private int combatToHit;

    /**
     * To-damage penalty imposed by the curse (C: {@code curse->obj->to_d}).
     */
    private int combatDam;

    /**
     * Armour-class penalty imposed by the curse (C: {@code curse->obj->to_a}).
     */
    private int combatAC;

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

    private int knownCombatToHit;
    private int knownCombatToDam;
    private int knownCombatToAC;

    private Map<ObjectModifier, Integer> knownModifiers;

    private Map<ElementEnum, ElementInfo> knownElInfo;

    private Flag<ObjectFlag> knownObjectFlags;

    private Effect knownEffect;
    

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
        knownElInfo = new HashMap<>();
        knownObjectFlags = new Flag<>(ObjectFlag.class);
        knownModifiers = new HashMap<>();
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

    /**
     * Whether this curse's to-hit figure is the unremarkable one for its bearer — the curse arm of
     * C's {@code object_has_standard_to_h} ({@code obj-knowledge.c:590}). The question behind the
     * name is not "is it zero" but "is it worth telling the player about": an ordinary to-hit
     * teaches nothing, so {@code PlayerKnowledge.knowObject} declines to write it into the known
     * figure.
     *
     * <p><b>C answers true here unconditionally, and this does not.</b> C's first line is a
     * commented hack for exactly this case — a curse carries its properties on a bearer-less object
     * whose {@code kind} is null, and the function returns true for a null kind before looking at
     * anything else. It has no choice: the branch below reads {@code obj->kind->to_h}, which a curse
     * has no kind to supply. {@link ItemObject#hasStandardToH()} keeps that null-kind line; this
     * one instead applies C's other branch, the {@code obj->to_h == 0} test written for ordinary
     * objects.
     *
     * <p>The two disagree exactly where a curse carries a non-zero to-hit, which three in
     * {@code curse.txt} do — <em>enveloping</em> (-5), <em>irritation</em> (-15) and
     * <em>air swing</em> (-20). C never copies their to-hit into the known figure; this lets it
     * through once the player can read to-hit. That is the deliberate half of the divergence rather
     * than an oversight: {@link #isFullyKnown()} compares {@code combatToHit} against
     * {@code knownCombatToHit}, so under C's answer those three curses could never become fully
     * known. C is not troubled by that because it returns from {@code player_know_object} before its
     * own fully-known block ever sees a curse — see {@link #isFullyKnown()}.
     *
     * <p>Function hasStandardToH coded before 260901, commented in full on 260901.
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
        this.knownModifiers = modifiers;
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
     * <p>Function setKnownElInfo coded before 260901, commented in full on 260901.
     *
     * @param knownElInfo the freshly derived known element map; stored, not copied
     */
    public void setKnownElInfo(Map<ElementEnum, ElementInfo> knownElInfo) {
        this.knownElInfo = knownElInfo;
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
     * Whether the player understands everything this curse does — the curse-shaped port of C's
     * {@code object_fully_known} ({@code obj-knowledge.c:763}) and the two predicates beneath it,
     * {@code object_runes_known} and {@code object_non_curse_runes_known}
     * ({@code obj-knowledge.c:741, 500}), collapsed into one method because most of what they test
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
     * <p><b>The modifier loop walks fewer entries than C's.</b> C compares all
     * {@code OBJ_MOD_MAX} slots of a dense array; this walks the keys the curse's data lines
     * actually name, since {@link #modifiers} holds only those. The two agree as long as the known
     * map is built solely from the real one, which is what {@code PlayerKnowledge.knowObject} does —
     * a known non-zero for a modifier the curse does not have would be invisible here and is not
     * reachable from that caller.
     *
     * <p><b>Outstanding: C never asks this question of a curse.</b> Its
     * {@code player_know_object} returns at "Curse object structures are finished now" — before the
     * effect assignment and before the fully-known block — so no curse object ever reaches
     * {@code object_fully_known} in the original, and none of its other callers is handed one. This
     * method and the block that calls it are therefore the port's own, following from the decision
     * recorded on this class to flatten C's nested {@code curse->obj} onto {@link Curse} itself.
     * The one place it changes an answer is the to-hit comparison, discussed at
     * {@link #hasStandardToH()}.
     *
     * <p>Function isFullyKnown coded before 260901, commented in full on 260901.
     *
     * @return true if every property this curse confers is one the player can currently read
     */
    public boolean isFullyKnown() {
        if (combatToHit != knownCombatToHit
                || combatDam != knownCombatToDam
                || combatAC != knownCombatToAC)
            return false;

        for (ObjectModifier om : modifiers.keySet()) {
            if (!knownModifiers.containsKey(om))
                return false;
            if (!Objects.equals(knownModifiers.get(om), modifiers.get(om)))
                return false;
        }

        for (ElementEnum em : elInfo.keySet()) {
            if ((elInfo.get(em).getResLevel() != 0)
                    && (!knownElInfo.containsKey(em)
                    || knownElInfo.get(em).getResLevel() == 0))
                return false;
        }

        if (!knownObjectFlags.isSubset(objectFlags))
            return false;

        return effect == knownEffect;
    }
}
