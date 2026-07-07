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

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

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
    public ObjectBase(TValue tVal, String name, ColourType colour, Flag<ObjectKindFlag> kFlag,
                      Flag<ElementEnum> hatesFlag, int breakChance, int maxStack) {
        this.tVal = tVal;
        this.name = name;
        this.attr = colour;
        this.kindFlags = kFlag;
        this.hatesEl = hatesFlag;
        this.breakPerc = breakChance;
        this.maxStack = maxStack;
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
                ", hatesEl=" + hatesEl +
                ", kindFlags=" + kindFlags +
                ", breakPerc=" + breakPerc +
                ", maxStack=" + maxStack +
                ", numSvals=" + numSvals +
                '}';
    }
}