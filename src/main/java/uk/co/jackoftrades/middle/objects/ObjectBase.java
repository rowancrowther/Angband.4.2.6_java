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
import uk.co.jackoftrades.middle.combat.enums.Element;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.List;

public class ObjectBase {
    private String name;
    private TValue tVal;
    private ColourType attr;

    private Flag<ObjectFlag> flags;
    // Put in to handle HATES_EL flags in object_base.txt
    private Flag<Element> hatesEl;
    private Flag<ObjectKindFlag> kindFlags;

    private int breakPerc;
    private int maxStack;
    private int numSvals;

    public ObjectBase() {
        flags = new Flag<>(ObjectFlag.class);
        hatesEl = new Flag<>(Element.class);
        kindFlags = new Flag<>(ObjectKindFlag.class);
        breakPerc = -1;
        maxStack = -1;
    }

    public void setFlags(@NotNull List<String> flagStrings) {
        for (String flagString : flagStrings) {
            if (flagString.startsWith("HATES_")) {
                setHatesFlag(Element.valueOf("ELEM_" + flagString.substring(6)));
            } else {
                setKindFlag(ObjectKindFlag.valueOf("KF_" + flagString));
            }
        }
    }

    private void setHatesFlag(Element flag) {
        hatesEl.on(flag);
    }

    private void setKindFlag(ObjectKindFlag flag) {
        kindFlags.on(flag);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void settVal(TValue tVal) {
        this.tVal = tVal;
    }

    public void setAttr(ColourType attr) {
        this.attr = attr;
    }

    public void setBreakPerc(int breakPerc) {
        this.breakPerc = breakPerc;
    }

    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    public void setNumSvals(int numSvals) {
        this.numSvals = numSvals;
    }

    public String getName() {
        return name;
    }

    public TValue gettVal() {
        return tVal;
    }

    public ColourType getAttr() {
        return attr;
    }

    public int getBreakPerc() {
        return breakPerc;
    }

    public int getMaxStack() {
        return maxStack;
    }

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