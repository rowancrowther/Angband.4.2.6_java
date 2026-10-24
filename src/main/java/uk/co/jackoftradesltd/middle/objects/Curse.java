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
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

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
 *   <li><b>{@link #objectBases}</b> — C's {@code curse->poss}, the tvals the curse may
 *       attach to, held as a list of {@link TValue}s rather than C's {@code bool} per tval.</li>
 *   <li><b>{@link #conflictFlags}</b>, <b>{@link #description}</b>, <b>{@link #name}</b> — C's
 *       {@code conflict_flags}, {@code desc} and {@code name}.</li>
 *   <li><b>{@link #conflictNames}</b> and <b>{@link #conflict}</b> — C's {@code conflict} string,
 *       in its raw form and in the form the assembler's second pass resolves it to.</li>
 * </ul>
 *
 * <p>{@link #index} has no counterpart in {@code struct curse}: C uses the position in
 * {@code curses[]}. The one behaviour on the class is {@link #modifyWeightForCurse(int)}, the
 * port of {@code modify_weight_for_curse()}; the other methods are accessors,
 * {@link #canAfflict(ObjectBase)} and {@link #setIndex(int)}, which {@code CurseAssembler} uses to
 * renumber the curses once it has put them in C's order.
 *
 * <p>Unlike {@code struct curse}, the constructor refuses a {@code null} or empty name: C's
 * {@code parse_curse_name} cannot produce one ({@code parser_parse} in {@code parser.c} returns
 * {@code PARSE_ERROR_MISSING_FIELD} for an empty {@code str} field), so the port makes the
 * impossible case loud.
 *
 * <p>Class Curse coded before 261005, commented in full on 261023, {@link #objectBases} described
 * as {@link TValue}s on 261023.
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
     * The order in which curses are held in a curse map: by {@link #index}, then by {@link #name}.
     * Passed to the {@link TreeMap} constructor wherever a curse map is built, so that iterating a
     * map visits the curses in the order of C's {@code curses[]}, where an object's curses sit in an
     * array indexed by curse.
     *
     * <p>The comparison reads the live {@link #index}, which {@link #setIndex(int)} can change. A
     * curse must therefore not be re-indexed while it is a key in a map ordered by this comparator.
     * The name tie-break only matters for two curses given the same index, and it treats a
     * {@code null} name as first, though the constructor never lets one through.
     *
     * <p>Field CURSE_ORDER coded on 261017, commented in full on 261017.
     */
    public static final Comparator<Curse> CURSE_ORDER =Comparator.comparing(Curse::getIndex)
            .thenComparing(Curse::getName, Comparator.nullsFirst(Comparator.naturalOrder()));
    
    /**
     * The curse's name (C: {@code curse->name}), from the {@code name:} line of {@code curse.txt}.
     * Conflicts between curses are matched on it, and it is what {@code lookup_curse} in
     * {@code obj-curse.c} searches by.
     *
     * <p>Field name coded before 261005, commented in full on 261005.
     */
    private final String name;

    /**
     * This curse's index - the port's counterpart of C's index into {@code curses[]}. C has no field
     * for it; the position in the array is the index. The numbering differs in two ways. C leaves
     * slot 0 as an unused dummy, so its first real curse is 1, while the port numbers from 0.
     * And C builds its list by prepending each record as {@code curse.txt} is parsed, so slot 1 holds
     * the <em>last</em> curse in the file; {@code CurseAssembler} reverses its list to match and then
     * overwrites the index the constructor was given, through {@link #setIndex(int)}. Anything that
     * compares an index against a C-derived value has to allow for the offset of one.
     *
     * <p>Field index coded before 261005, commented in full on 261008.
     */
    private int index;

    /**
     * The tvals this curse may attach to — the port's form of C's {@code curse->poss}, the
     * per-tval possibility array that {@code parse_curse_type} in {@code obj-init.c} sets once for
     * each {@code type:} line. C allocates a zero-filled {@code bool} for every tval in
     * {@code parse_curse_name} and sets {@code poss[tval]} to true; the port holds the
     * {@link TValue} of each {@code type:} line instead, and {@link #canAfflict(ObjectBase)} is the
     * lookup. Membership answers exactly as C's array does.
     *
     * <p>Two things the list keeps that C's array cannot: the order of the {@code type:} lines, and
     * a repeat ({@code type:cloak} twice gives two entries where C sets one flag twice). Neither
     * changes {@link #canAfflict(ObjectBase)}. A tval with no object base ({@code none},
     * {@code other book}) is held like any other and simply never matches an object, as in C. A
     * curse with no {@code type:} line has an empty list and can attach to nothing.
     *
     * <p>Unlike C's array, which {@code parse_curse_name} always allocates, the list may be
     * {@code null} if a caller passes one; {@code CurseAssembler} never does, and
     * {@link #canAfflict(ObjectBase)} would throw on it.
     *
     * <p>Field objectBases coded before 261005, commented in full on 261023, changed from object
     * bases to {@link TValue}s on 261023.
     */
    private final List<TValue> objectBases;

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
     * Build a curse from its assembled fields — the port's counterpart of the record
     * {@code parse_curse_name} in {@code obj-init.c} allocates and the later {@code parse_curse_*}
     * handlers fill. This takes already-resolved domain objects (an {@link ItemObject} carrying the
     * curse's mechanical properties, a flag set, a list of {@link TValue}s) rather than raw
     * data-file text — the parsing and lookup work lives in {@code CurseAssembler}. The
     * {@link #conflict} list is left null here and filled by the assembler's second pass from
     * {@code conflictNames}.
     *
     * <p>Only the name is checked, and a {@code null} or empty one throws where C's parser stops
     * with {@code PARSE_ERROR_MISSING_FIELD} before {@code parse_curse_name} runs. The lists, the
     * flag set and the object are stored by reference and never copied, so the caller must not hand
     * in {@code null} for any of them if the curse is later asked for its weight effect or whether
     * it can afflict a base; C has no such case, as every record gets a zero-filled object and
     * {@code poss} array.
     *
     * <p>The index is provisional when {@code CurseAssembler} is the caller: it builds the curses in
     * file order, then reverses them to C's order and renumbers them with {@link #setIndex(int)}.
     *
     * <p>Constructor Curse coded before 261005, commented in full on 261023, {@code objectBases}
     * changed to {@link TValue}s on 261023.
     *
     * @param name          curse name
     * @param objectBases   the tvals of the {@code type:} lines, C's {@code curse->poss}; stored,
     *                      not copied
     * @param itemObject    the curse's own object, C's {@code curse->obj}
     * @param conflictNames names of conflicting curses (resolved later)
     * @param conflictFlags conflicting object flags
     * @param description   description
     * @param index         the curse's index in the assembled list, 0-based
     * @throws IllegalArgumentException if {@code name} is {@code null} or empty
     */
    public Curse(String name,
                 List<TValue> objectBases,
                 ItemObject itemObject,
                 List<String> conflictNames,
                 Flag<ObjectFlag> conflictFlags,
                 String description,
                 int index) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("name cannot be null or empty");
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
     * <p>Function getIndex coded before 261003, commented in full on 261008.
     *
     * @return this curse's index: the constructor's argument until {@link #setIndex(int)} replaces it
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
     * The tvals this curse may attach to — the {@code type:} lines of the data file, in file order
     * and with any repeat kept, and the port's form of C's {@code curse->poss} array (see
     * {@link #objectBases}). C has no getter; its callers index {@code poss} directly. Use
     * {@link #canAfflict(ObjectBase)} to ask about a single base.
     *
     * <p>Function getTValues coded before 261005, commented in full on 261023, return type changed
     * to {@link TValue}s on 261023.
     *
     * @return the tvals this curse may attach to; the curse's own list, not a copy, so a change to it
     *         changes what {@link #canAfflict(ObjectBase)} answers
     */
    public List<TValue> getTValues() {
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
     * Replaces this curse's index. {@code CurseAssembler} calls it once, after reversing its list
     * into C's order, so that the index each curse ends up with is its position in that list — the
     * port's counterpart of the slot a C curse occupies in {@code curses[]}, less C's unused slot 0.
     * Nothing validates the value, and nothing re-sorts anything that already holds this curse as a
     * key, so it is not safe to call once the curse sits in an {@code ItemObject} curse map ordered
     * by index.
     *
     * <p>Method setIndex coded before 261005, commented in full on 261008.
     *
     * @param index the new 0-based index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Whether this curse may attach to an object of the given base — the port's form of C's
     * {@code curse->poss[obj->tval]} test, made in {@code obj-make.c} (function
     * {@code apply_curse}), {@code obj-randart.c} (on the artifact's tval) and
     * {@code effect-handler-general.c} (the two curse-adding effects). The base's tval is looked up
     * in {@link #objectBases}, so any base with a listed tval matches, whichever instance it is, as
     * C's index by tval does. A repeated {@code type:} line makes no difference, and an empty list
     * answers {@code false} for every base, as a C curse with no {@code type:} line does.
     *
     * <p>C always has an object to index by; here a {@code null} base, or a curse built with a
     * {@code null} list, throws {@link NullPointerException}.
     *
     * <p>Function canAfflict coded before 261005, commented in full on 261023, made to test by tval
     * on 261023.
     *
     * @param objectBase the base of the object to test; its tval is what is checked
     * @return true if this curse's {@code type:} lines include the base's tval
     */
    public boolean canAfflict(ObjectBase objectBase) {
        return objectBases.contains(objectBase.gettVal());
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
     * <p>Function modifyWeightForCurse coded before 260820, commented in full on 261008, C line
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
    
    public record FullCurseData(Curse curse,
                                int power,
                                int timeout) {}
}
