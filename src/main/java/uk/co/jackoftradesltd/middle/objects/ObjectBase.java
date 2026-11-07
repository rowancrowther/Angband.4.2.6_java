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

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.Map;

/**
 * The base type of a family of object kinds (as loaded from {@code object_base.txt})
 * — e.g. "sword", "potion" — carrying defaults shared by all kinds of that type:
 * display colour, common flags, elemental vulnerabilities, break chance and
 * stack size. This is the Java port of the C original's {@code struct object_base}
 * ({@code object.h}).
 *
 * <p>The C struct also carries a {@code next} pointer, used only while {@code obj-init.c}
 * is parsing to chain the records together; it is not ported, because the assembler hands back a
 * {@code List<ObjectBase>} instead. C then copies each record into {@code kb_info}, an array
 * with one slot per tval; here the assembler's list takes that role, ending in a synthetic
 * {@code TV_NONE} base (zero break chance and stack size, dark colour, no flags) that stands in
 * for a zeroed C slot.
 *
 * <p>Plain data holder: nothing here reads the game state or has behaviour of its own, so the
 * class is a field-for-field port of the struct rather than of any C function.
 *
 * <p>Class ObjectBase coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class ObjectBase {
    /**
     * The base type's name, C's {@code object_base.name} (the optional second argument of the
     * {@code name:} line in {@code object_base.txt}, e.g. {@code "Bladed weapon~"}). The assembler
     * passes {@code null} when the line has no name part, as C's {@code name} stays {@code NULL},
     * and for the synthetic {@code TV_NONE} base.
     *
     * <p>Field name coded before 261009, commented in full on 261009, null-when-absent described on
     * 261105.
     */
    private String name;

    /**
     * The item type value (tval) of this base, C's {@code object_base.tval}. Each base owns exactly
     * one tval; the kinds beneath it are told apart by sval.
     *
     * <p>Field tVal coded before 261009, commented in full on 261009.
     */
    private TValue tVal;

    /**
     * Default display colour for kinds of this base, C's {@code object_base.attr} (an {@code int}
     * colour index there, a {@link ColourEnum} here).
     *
     * <p>Field attr coded before 261009, commented in full on 261009.
     */
    private ColourEnum attr;

    /**
     * Per-element info shared by kinds of this base, keyed by element: C's
     * {@code object_base.el_info[ELEM_MAX]}. The map is built by the assembler and stored by
     * reference: {@code ObjectBaseAssembler} puts an entry carrying
     * {@link ElementInfoEnum#EL_INFO_HATES} or {@link ElementInfoEnum#EL_INFO_IGNORE} against each
     * element named by a {@code HATES_} or {@code IGNORE_} flag word, through
     * {@code ParseUtils.grabElementFlag}. Derived kinds may add further per-element flags (e.g.
     * dungeon spellbooks setting {@code EL_INFO_IGNORE}).
     *
     * <p>C's array has an entry for every element, all zeroed unless the data file touches them;
     * this map is sparse, holding an entry only for an element that has one, so an absent key
     * means "zeroed entry" (no flags, resistance level 0). Readers must therefore test
     * {@link Map#containsKey} before dereferencing, as {@code ObjectUtils} does.
     *
     * <p>Field elementInfo coded before 261009, commented in full on 261009, rewritten for the
     * assembler-built map on 261105.
     */
    private Map<ElementEnum, ElementInfo> elementInfo;

    /**
     * Object flags (the {@code OF_*} set) for this base, C's {@code object_base.flags}. The set is
     * built by {@code ObjectBaseAssembler} and stored by reference: a {@code flags:} token that is
     * not an element flag and names no kind flag is tried as an {@link ObjectFlag}, as C's
     * {@code parse_object_base_flags} offers each token to the object-flag table too, and
     * {@code MAX} switches on {@code OF_MAX} as well as {@code KF_MAX}. The shipped file uses no
     * object flag (its flags are {@code HATES_*}, {@code EASY_KNOW}, {@code SHOW_DICE} and
     * {@code SHOW_MULT}), so the set is empty on the shipped data. {@code ObjectKind} copies this
     * set into each kind it builds.
     *
     * <p>Field flags coded before 261009, commented in full on 261009, rewritten for the
     * assembler-built set on 261105.
     */
    private Flag<ObjectFlag> flags;

    /**
     * Object-kind flags shared by kinds of this base, C's {@code object_base.kind_flags}
     * ({@code EASY_KNOW}, {@code SHOW_DICE} and the like).
     *
     * <p>Field kindFlags coded before 261009, commented in full on 261009.
     */
    private Flag<ObjectKindFlag> kindFlags;

    /**
     * Percentage chance an item of this base breaks when thrown, C's {@code object_base.break_perc}.
     * Never "unset" in practice: a record that omits its own {@code break:} line receives the
     * file-wide {@code default:break-chance:} value (10 in the shipped file), and only the synthetic
     * {@code TV_NONE} base carries 0.
     *
     * <p>Field breakPerc coded before 261009, commented in full on 261009.
     */
    private int breakPerc;
    /**
     * Maximum stack size for items of this base, C's {@code object_base.max_stack}. As with
     * {@link #breakPerc}, a record without its own {@code max-stack:} line receives the file-wide
     * {@code default:max-stack:} value (40 in the shipped file); only the synthetic {@code TV_NONE}
     * base carries 0.
     *
     * <p>Field maxStack coded before 261009, commented in full on 261009.
     */
    private int maxStack;
    /**
     * Number of distinct sub-values (svals) allocated under this base so far, C's
     * {@code object_base.num_svals}. Starts at 0 (C sets it to 0 in {@code parse_object_base_name})
     * and is advanced as kinds are registered.
     *
     * <p>Field numSvals coded before 261009, commented in full on 261009.
     */
    private int numSvals;

    /**
     * Build an object base from the values the assembler has already resolved. This one constructor
     * stands in for the sequence of {@code obj-init.c} parser callbacks that build a C record
     * ({@code parse_object_base_name}, {@code _graphics}, {@code _break}, {@code _max_stack} and
     * {@code _flags}); {@link #numSvals} starts at 0, as {@code parse_object_base_name} sets it.
     *
     * <p>{@code kFlag}, {@code elInfo} and {@code oFlags} are stored by reference, not copied (C's
     * record owns its own arrays), so the caller must not go on mutating what it passed in. The
     * element map is the sparse one described at {@link #elementInfo}: only elements the data file
     * named have an entry.
     *
     * <p>Constructor ObjectBase coded before 261009, commented in full on 261009, rewritten for the
     * element map and object flags being passed in on 261105.
     *
     * @param tVal        the item type value
     * @param name        the base type's name, or {@code null} if the {@code name:} line gave none
     * @param colour      the default display colour
     * @param kFlag       the object-kind flags shared by this base's kinds
     * @param elInfo      the per-element flags and resistance levels, keyed by element
     * @param breakChance the break-on-throw percentage
     * @param maxStack    the maximum stack size
     * @param oFlags      the object flags shared by this base's kinds
     */
    public ObjectBase(TValue tVal, String name, ColourEnum colour, Flag<ObjectKindFlag> kFlag,
                      Map<ElementEnum, ElementInfo> elInfo, int breakChance, int maxStack, 
                      Flag<ObjectFlag> oFlags) {
        this.tVal = tVal;
        this.name = name;
        this.attr = colour;
        this.kindFlags = kFlag;
        this.flags = oFlags;
        this.elementInfo = elInfo;
        this.breakPerc = breakChance;
        this.maxStack = maxStack;
    }

    /**
     * Replace this base's per-element info table wholesale, with no merge into the entries the
     * constructor created: the old map is dropped and the new one is stored by reference, not
     * copied. Java-side only: C writes straight into the {@code el_info[]} array of
     * {@code struct object_base} ({@code object.h}). Nothing in {@code src/main} calls this at
     * present, so it is an unused boundary there; only {@code ObjectBaseTest} exercises it.
     *
     * <p>Function setElementInfo coded before 261009, commented in full on 261107.
     *
     * @param elementInfo the new element-info map
     */
    public void setElementInfo(Map<ElementEnum, ElementInfo> elementInfo) {
        this.elementInfo = elementInfo;
    }

    /**
     * Returns the number of svals allocated under this base so far, C's {@code object_base.num_svals}.
     * Starts at 0 and only moves when {@link #setNumSvals} is called.
     *
     * <p>Function getNumSvals coded before 261009, commented in full on 261009.
     *
     * @return the number of distinct svals allocated under this base so far
     */
    public int getNumSvals() {
        return numSvals;
    }

    /**
     * Set the running count of svals allocated under this base. Used as kinds are registered so each
     * new kind can be handed the next sval (see {@link ObjectRegistry#addObjectKind}).
     *
     * <p>Function setNumSvals coded before 261009, commented in full on 261009.
     *
     * @param numSvals the new sval count
     */
    public void setNumSvals(int numSvals) {
        this.numSvals = numSvals;
    }

    /**
     * Returns the base type's name, C's {@code object_base.name}. May be {@code null}, since the
     * name part of the {@code name:} line in {@code object_base.txt} is optional.
     *
     * <p>Function getName coded before 261009, commented in full on 261009, null-when-absent
     * described on 261105.
     *
     * @return the base type's name, or {@code null} if the line gave none
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this base's per-element info table, C's {@code object_base.el_info}. This is the live
     * map, not a copy, so a caller can add or change entries through it. It is sparse: an element
     * with nothing to record has no key (see {@link #elementInfo}), so test {@link Map#containsKey}
     * before {@code get}.
     *
     * <p>Function getElementMap coded before 261009, commented in full on 261009.
     *
     * @return this base's per-element info table (keyed by element)
     */
    public Map<ElementEnum, ElementInfo> getElementMap() {
        return elementInfo;
    }

    /**
     * Returns the item type value this base describes, C's {@code object_base.tval}. The accessor
     * is spelt {@code gettVal} to match the field, not the usual bean form.
     *
     * <p>Function gettVal coded before 261009, commented in full on 261009.
     *
     * @return the item type value (tval)
     */
    public TValue gettVal() {
        return tVal;
    }

    /**
     * Returns the default display colour for kinds of this base, C's {@code object_base.attr}.
     *
     * <p>Function getAttr coded before 261009, commented in full on 261009.
     *
     * @return the default display colour
     */
    public ColourEnum getAttr() {
        return attr;
    }

    /**
     * Returns the percentage chance that an item of this base breaks when thrown, C's
     * {@code object_base.break_perc}.
     *
     * <p>Function getBreakPerc coded before 261009, commented in full on 261009.
     *
     * @return the break-on-throw percentage
     */
    public int getBreakPerc() {
        return breakPerc;
    }

    /**
     * Returns the maximum stack size for items of this base, C's {@code object_base.max_stack}.
     *
     * <p>Function getMaxStack coded before 261009, commented in full on 261009.
     *
     * @return the maximum stack size
     */
    public int getMaxStack() {
        return maxStack;
    }

    /**
     * Returns the object-kind flags shared by kinds of this base, C's {@code object_base.kind_flags}.
     * This is the live set that the constructor stored, not a copy.
     *
     * <p>Function getKindFlags coded before 261009, commented in full on 261009.
     *
     * @return the object-kind flags shared by kinds of this base
     */
    public Flag<ObjectKindFlag> getKindFlags() {
        return kindFlags;
    }

    /**
     * Returns the object flags (the {@code OF_*} set) for this base, C's {@code object_base.flags}.
     * This is the live set, not a copy, and it is empty on the shipped data (see {@link #flags}).
     *
     * <p>Function getFlags coded before 261009, commented in full on 261009, "always empty" removed
     * on 261105.
     *
     * @return the object flags for this base
     */
    public Flag<ObjectFlag> getFlags() {
        return flags;
    }

    /**
     * Returns a debug string listing the name, tval, colour, kind flags, break chance, maximum
     * stack and sval count. It leaves out {@link #flags} and {@link #elementInfo}. Java-side only;
     * C has no equivalent.
     *
     * <p>Function toString coded before 261009, commented in full on 261009.
     *
     * @return a debug string listing this base's fields
     */
    @Override
    public String toString() {
        return "ObjectBase{" +
                "name='" + name + '\'' +
                ", tVal=" + tVal +
                ", attr=" + attr +
                ", kindFlags=" + kindFlags +
                ", breakPerc=" + breakPerc +
                ", maxStack=" + maxStack +
                ", numSvals=" + numSvals +
                '}';
    }
}