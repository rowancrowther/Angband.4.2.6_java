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
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.HashMap;
import java.util.Map;

/**
 * The base type of a family of object kinds (as loaded from {@code object_base.txt})
 * — e.g. "sword", "potion" — carrying defaults shared by all kinds of that type:
 * display colour, common flags, elemental vulnerabilities, break chance and
 * stack size. This is the Java port of the C original's {@code struct object_base}
 * ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class ObjectBase {
    /**
     * The base type's name.
     *
     * @author Rowan Crowther
     */
    private String name;

    /**
     * The item type value (tval) of this base.
     *
     * @author Rowan Crowther
     */
    private TValue tVal;

    /**
     * Default display colour for kinds of this base.
     *
     * @author Rowan Crowther
     */
    private ColourType attr;

    /**
     * Per-element info shared by kinds of this base, keyed by element. Replaces the earlier plain
     * {@code HATES_*} flag set: the constructor folds each hated element in as an entry carrying the
     * {@link ElementInfoEnum#EL_INFO_HATES} flag, and derived kinds may add further per-element flags
     * (e.g. dungeon spellbooks setting {@code EL_INFO_IGNORE}). This mirrors C's move from a bitflag
     * to the richer {@code el_info[]} table.
     *
     * @author Rowan Crowther
     */
    private Map<ElementEnum, ElementInfo> elementInfo;

    /**
     * Object-kind flags shared by kinds of this base.
     *
     * @author Rowan Crowther
     */
    private Flag<ObjectKindFlag> kindFlags;

    /**
     * Percentage chance an item of this base breaks when thrown ({@code -1} if unset).
     *
     * @author Rowan Crowther
     */
    private int breakPerc;
    /**
     * Maximum stack size for items of this base ({@code -1} if unset).
     *
     * @author Rowan Crowther
     */
    private int maxStack;
    /**
     * Number of distinct sub-values (svals) under this base.
     *
     * @author Rowan Crowther
     */
    private int numSvals;

    /**
     * Build an object base, folding the incoming {@code HATES_*} element flags into the per-element
     * {@link #elementInfo} table (each hated element gets an entry flagged
     * {@link ElementInfoEnum#EL_INFO_HATES}).
     *
     * @param tVal        the item type value
     * @param name        the base type's name
     * @param colour      the default display colour
     * @param kFlag       the object-kind flags shared by this base's kinds
     * @param hatesFlag   the elements items of this base are destroyed by
     * @param breakChance the break-on-throw percentage ({@code -1} if unset)
     * @param maxStack    the maximum stack size ({@code -1} if unset)
     * @author Rowan Crowther
     */
    public ObjectBase(TValue tVal, String name, ColourType colour, Flag<ObjectKindFlag> kFlag,
                      Flag<ElementEnum> hatesFlag, int breakChance, int maxStack) {
        this.tVal = tVal;
        this.name = name;
        this.attr = colour;
        this.kindFlags = kFlag;
        this.elementInfo = new HashMap<>();
        for (ElementEnum element : hatesFlag) {
            ElementInfo info = elementInfo.computeIfAbsent(element, k -> new ElementInfo());
            info.getFlags().on(ElementInfoEnum.EL_INFO_HATES);
        }
        this.breakPerc = breakChance;
        this.maxStack = maxStack;
    }

    /**
     * Replace this base's per-element info table wholesale.
     *
     * @param elementInfo the new element-info map
     * @author Rowan Crowther
     */
    public void setElementInfo(Map<ElementEnum, ElementInfo> elementInfo) {
        this.elementInfo = elementInfo;
    }

    /**
     * @return the number of distinct svals allocated under this base so far
     * @author Rowan Crowther
     */
    public int getNumSvals() {
        return numSvals;
    }

    /**
     * Set the running count of svals allocated under this base. Used as kinds are registered so each
     * new kind can be handed the next sval (see {@link uk.co.jackoftrades.middle.game.globals.GameConstants#addObjectKind}).
     *
     * @param numSvals the new sval count
     * @author Rowan Crowther
     */
    public void setNumSvals(int numSvals) {
        this.numSvals = numSvals;
    }

    /**
     * @return the base type's name
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * @return this base's per-element info table (keyed by element)
     * @author Rowan Crowther
     */
    public Map<ElementEnum, ElementInfo> getElementMap() {
        return elementInfo;
    }

    /**
     * @return the item type value (tval)
     * @author Rowan Crowther
     */
    public TValue gettVal() {
        return tVal;
    }

    /**
     * @return the default display colour
     * @author Rowan Crowther
     */
    public ColourType getAttr() {
        return attr;
    }

    /**
     * @return the break-on-throw percentage
     * @author Rowan Crowther
     */
    public int getBreakPerc() {
        return breakPerc;
    }

    /**
     * @return the maximum stack size
     * @author Rowan Crowther
     */
    public int getMaxStack() {
        return maxStack;
    }

    /**
     * @return the object-kind flags shared by kinds of this base
     * @author Rowan Crowther
     */
    public Flag<ObjectKindFlag> getKindFlags() {
        return kindFlags;
    }

    /**
     * @return a debug string listing this base's fields
     * @author Rowan Crowther
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