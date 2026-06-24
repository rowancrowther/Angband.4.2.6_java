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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.objects;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.List;

/**
 * The base type of a family of object kinds (as loaded from {@code object_base.txt})
 * — e.g. "sword", "potion" — carrying defaults shared by all kinds of that type:
 * display colour, common flags, elemental vulnerabilities, break chance and
 * stack size. This is the Java port of the C original's {@code struct object_base}
 * ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public class ObjectBase {
    /**
     * The base type's name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * The item type value (tval) of this base.
     *
     * @author ClaudeCode
     */
    private TValue tVal;
    /**
     * Default display colour for kinds of this base.
     *
     * @author ClaudeCode
     */
    private ColourType attr;

    /**
     * Object flags shared by kinds of this base.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectFlag> flags;
    // Put in to handle HATES_EL flags in object_base.txt
    /**
     * Elements this base's items are destroyed by (the {@code HATES_*} flags).
     *
     * @author ClaudeCode
     */
    private Flag<ElementEnum> hatesEl;
    /**
     * Object-kind flags shared by kinds of this base.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectKindFlag> kindFlags;

    /**
     * Percentage chance an item of this base breaks when thrown ({@code -1} if unset).
     *
     * @author ClaudeCode
     */
    private int breakPerc;
    /**
     * Maximum stack size for items of this base ({@code -1} if unset).
     *
     * @author ClaudeCode
     */
    private int maxStack;
    /**
     * Number of distinct sub-values (svals) under this base.
     *
     * @author ClaudeCode
     */
    private int numSvals;

    /**
     * Build an empty object base with fresh flag sets and unset break/stack values.
     *
     * @author ClaudeCode
     */
    public ObjectBase() {
        flags = new Flag<>(ObjectFlag.class);
        hatesEl = new Flag<>(ElementEnum.class);
        kindFlags = new Flag<>(ObjectKindFlag.class);
        breakPerc = -1;
        maxStack = -1;
    }

    /**
     * Parse and apply a list of flag strings, routing {@code HATES_*} entries to
     * the elemental-vulnerability set and the rest to the kind-flag set.
     *
     * @param flagStrings the raw flag names from the data file
     * @author ClaudeCode
     */
    public void setFlags(@NotNull List<String> flagStrings) {
        for (String flagString : flagStrings) {
            if (flagString.startsWith("HATES_")) {
                setHatesFlag(ElementEnum.valueOf("ELEM_" + flagString.substring(6)));
            } else {
                setKindFlag(ObjectKindFlag.valueOf("KF_" + flagString));
            }
        }
    }


    /**
     * Set an elemental-vulnerability flag.
     *
     * @param flag the element this base hates
     * @author ClaudeCode
     */
    private void setHatesFlag(ElementEnum flag) {
        hatesEl.on(flag);
    }

    /**
     * Set an object-kind flag on this base.
     *
     * @param flag the kind flag to set
     * @author ClaudeCode
     */
    private void setKindFlag(ObjectKindFlag flag) {
        kindFlags.on(flag);
    }

    /**
     * @param name the base type's name
     * @author ClaudeCode
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param tVal the item type value
     * @author ClaudeCode
     */
    public void settVal(TValue tVal) {
        this.tVal = tVal;
    }

    /**
     * @param attr the default display colour
     * @author ClaudeCode
     */
    public void setAttr(ColourType attr) {
        this.attr = attr;
    }

    /**
     * @param breakPerc the break-on-throw percentage
     * @author ClaudeCode
     */
    public void setBreakPerc(int breakPerc) {
        this.breakPerc = breakPerc;
    }

    /**
     * @param maxStack the maximum stack size
     * @author ClaudeCode
     */
    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    /**
     * @param numSvals the number of sub-values
     * @author ClaudeCode
     */
    public void setNumSvals(int numSvals) {
        this.numSvals = numSvals;
    }

    /**
     * @return the base type's name
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }

    /**
     * @return the item type value (tval)
     * @author ClaudeCode
     */
    public TValue gettVal() {
        return tVal;
    }

    /**
     * @return the default display colour
     * @author ClaudeCode
     */
    public ColourType getAttr() {
        return attr;
    }

    /**
     * @return the break-on-throw percentage
     * @author ClaudeCode
     */
    public int getBreakPerc() {
        return breakPerc;
    }

    /**
     * @return the maximum stack size
     * @author ClaudeCode
     */
    public int getMaxStack() {
        return maxStack;
    }

    /**
     * @return a debug string listing this base's fields
     * @author ClaudeCode
     */
    @Override
    public String toString() {
        return "ObjectBase{" +
                "name='" + name + '\'' +
                ", tVal=" + tVal +
                ", attr=" + attr +
                ", flags=" + flags +
                ", hatesEl=" + hatesEl +
                ", kindFlags=" + kindFlags +
                ", breakPerc=" + breakPerc +
                ", maxStack=" + maxStack +
                ", numSvals=" + numSvals +
                '}';
    }
}