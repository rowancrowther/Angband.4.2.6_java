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
 * {@code struct curse} ({@code object.h}), one entry of C's {@code curses[]} array.
 * <p>
 * In C a curse is little more than a name plus a nested {@code struct object *obj} that carries
 * all the mechanical properties; when the game works out an item's real attributes it walks the
 * item's curses and merges each curse's {@code obj} onto the item ({@code obj-curse.c}, function
 * {@code apply_curse_attributes()}). The port keeps that shape: the mechanical properties live on
 * the curse's own {@link ItemObject}, reached through {@link #getItemObject()}, and this class
 * holds only what {@code struct curse} holds around it.
 * <ul>
 *   <li><b>{@link #itemObject}</b> — C's {@code curse->obj}. {@code CurseAssembler} sets its
 *       weight, flags, modifiers, element info, to-hit, to-damage and to-armour figures, effect
 *       and effect message from the data file, and the merge code reads them back from it.</li>
 *   <li><b>{@link #objectBases}</b> — C's {@code curse->poss}, the object bases the curse may
 *       attach to.</li>
 *   <li><b>{@link #conflictFlags}</b>, <b>{@link #description}</b>, <b>{@link #name}</b> — C's
 *       {@code conflict_flags}, {@code desc} and {@code name}.</li>
 *   <li><b>{@link #conflictNames}</b> and <b>{@link #conflict}</b> — C's {@code conflict} string,
 *       in its raw form and in the form the assembler's second pass resolves it to.</li>
 * </ul>
 *
 * <p>{@link #index} has no counterpart in {@code struct curse}: C uses the position in
 * {@code curses[]}. The one behaviour on the class is {@link #modifyWeightForCurse(int)}, the
 * port of {@code modify_weight_for_curse()}; the other methods are accessors and
 * {@link #canAfflict(ObjectBase)}.
 *
 * <p>Class Curse coded before 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
public class Curse {
    /**
     * Log destination for the one condition C asserts on that the port keeps. C's
     * {@code modify_weight_for_curse()} (in {@code obj-curse.c}) asserts that a weight-multiplying
     * curse's own weight is not negative; the port logs and throws instead, so a malformed data
     * file spoils one calculation rather than the process. C's other assertion there, that the
     * curse index is in range, has no counterpart because the method is called on the curse itself.
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
     * {@code struct object} that the {@code curses[]} entry carries. The caller builds it and passes
     * it to the constructor ({@code CurseAssembler} does so, after setting the weight, flags,
     * modifiers, element info, combat figures, effect and effect message from the data file), and
     * {@link #getItemObject()} hands it out. The only thing this class reads from it is the weight
     * and flags, in {@link #modifyWeightForCurse(int)}.
     *
     * <p>Field itemObject coded before 261005, commented in full on 261005.
     */
    private final ItemObject itemObject;

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
     * Build a curse from its assembled fields. This takes already-resolved domain objects (an
     * {@link ItemObject} carrying the curse's mechanical properties, a flag set, a list of bases)
     * rather than raw data-file text — the parsing and lookup work lives in {@code CurseAssembler}.
     * The {@link #conflict} list is left null here and filled by the assembler's second pass from
     * {@code conflictNames}.
     *
     * <p>No argument is copied or checked: the list, the flag set and the object are stored by
     * reference, so the caller must not hand in {@code null} for any of them if the curse is later
     * asked for its weight effect or its bases.
     *
     * <p>Constructor Curse coded before 261005, commented in full on 261005.
     *
     * @param name          curse name
     * @param objectBases   affectable object bases ({@code types:} line)
     * @param itemObject    the curse's own object, C's {@code curse->obj}
     * @param conflictNames names of conflicting curses (resolved later)
     * @param conflictFlags conflicting object flags
     * @param description   description
     * @param index         the curse's index in the assembled list, 0-based
     */
    public Curse(String name,
                 List<ObjectBase> objectBases,
                 ItemObject itemObject,
                 List<String> conflictNames,
                 Flag<ObjectFlag> conflictFlags,
                 String description,
                 int index) {
        this.name = name;
        this.objectBases = objectBases;
        this.conflictNames = conflictNames;
        this.conflictFlags = conflictFlags;
        this.description = description;
        this.index = index;
        this.itemObject = itemObject;
    }

    /**
     * The curse's own object, the port's counterpart of C's {@code curse->obj}. It carries the
     * curse's mechanical properties (see {@link #itemObject}); callers read the weight, flags,
     * modifiers and so on straight from it.
     *
     * <p>Function getItemObject coded before 261005, commented in full on 261005.
     *
     * @return the object the constructor was given, and the same instance on every call
     */
    public ItemObject getItemObject() {
        return itemObject;
    }

    /**
     * The curse's index, the port's stand-in for C's position in {@code curses[]}. It is 0-based,
     * where C's first real curse is 1 (see {@link #index}), so a comparison with a C-derived
     * number has to allow for that offset.
     *
     * <p>Function getIndex coded before 261003, commented in full on 261005.
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
     * Applies this curse's weight change to an item's weight — the port of C's
     * {@code modify_weight_for_curse()} ({@code obj-curse.c}). C calls it from two places: once per
     * curse of non-zero power when an item's true weight is worked out ({@code object_weight_one()}
     * in {@code obj-util.c}), and from {@code apply_curse_attributes()} in {@code obj-curse.c} for
     * every other active curse, so the curses compose by being applied in turn. C takes the curse
     * as an index into {@code curses[]}; the port is called on the curse itself.
     *
     * <p>The weight of the curse's own object ({@link #getItemObject()}) means one of two different
     * things depending on its {@link ObjectFlag#OF_MULTIPLY_WEIGHT} flag, which is why this cannot
     * be a plain addition:
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
     * number removed on 261005, rewritten for the unflattened curse object on 261005.
     *
     * @param weight the item's weight before this curse is applied, in tenth-pounds
     * @return the weight after it, never negative and never above {@link Short#MAX_VALUE}
     * @throws IllegalArgumentException if this curse multiplies weight but its own weight is
     *                                  negative, which C asserts against
     */
    public int modifyWeightForCurse(int weight) {
        int result = weight;
        int thisWeight = getItemObject().getWeight();

        if (getItemObject().getFlags().has(ObjectFlag.OF_MULTIPLY_WEIGHT)) {
            if (thisWeight < 0) {
                logger.error("Weight cannot be negative.");
                throw new IllegalArgumentException("Weight cannot be negative.");
            }

            int scaled;
            if (thisWeight > 100)
                scaled = Math.max(weight, 1);
            else
                scaled = Math.max(weight, 0);

            scaled *= thisWeight;

            int quotient = scaled / 100;
            if (quotient < Short.MAX_VALUE) {
                result = quotient;
                if (scaled % 100 >= 50)
                    result++;
            } else
                result = Short.MAX_VALUE;
        } else {
            weight = Math.max(0, weight);
            if (thisWeight < 0) {
                result = weight + thisWeight;
                if (result < 0) result = 0;
            } else {
                result = (weight < Short.MAX_VALUE - thisWeight) ?
                        weight + thisWeight : Short.MAX_VALUE;
            }
        }

        return result;
    }
}
