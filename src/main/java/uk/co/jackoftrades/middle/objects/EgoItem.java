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

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.Map;

public class EgoItem {
    private String name;
    private String text;
    private TValue type;

    private int egoIndex;

    private Random cost;

    private Flag<ObjectFlag> flags;
    private Flag<ObjectFlag> flagsOff;
    private Flag<ObjectKindFlag> kindFLags;
    private Flag<ObjectKindFlag> kindFLagsOff;

    private Map<ObjectModifier, Random> modifiers;
    private Map<ObjectModifier, Random> minModifiers;
    private Map<ElementEnum, ElementInfo> elInfo;

    private Map<Brand, Boolean> brands;
    private Map<Slay, Boolean> slays;
    private Map<CurseData, Integer> curses;

    private Random rating;
    private Random allocProb;
    private int allocMin;
    private int allocMax;

    private Map<TValue, ObjectKind> possItems;

    private Random toHit;
    private Random toDam;
    private Random toAC;

    private Random minToHit;
    private Random minToDam;
    private Random minToAC;

    private Activation activation;
    private Random time;

    private boolean everSeen;

    public EgoItem(String name, TValue type, int egoIndex,
                   Random cost, Flag<ObjectFlag> flags,
                   Flag<ObjectFlag> flagsOff,
                   Flag<ObjectKindFlag> kindFLags,
                   Flag<ObjectKindFlag> kindFLagsOff,
                   Map<ObjectModifier, Random> modifiers,
                   Map<ObjectModifier, Random> minModifiers,
                   Map<ElementEnum, ElementInfo> elInfo,
                   Map<Brand, Boolean> brands,
                   Map<Slay, Boolean> slays,
                   Map<CurseData, Integer> curses, Random rating,
                   Random allocProb, int allocMin, int allocMax,
                   Map<TValue, ObjectKind> possItems, Random toHit,
                   Random toDam, Random toAC, Random minToHit,
                   Random minToDam, Random minToAC,
                   Activation activation, Random time,
                   boolean everSeen) {
        this.name = name;
        this.type = type;
        this.egoIndex = egoIndex;
        this.cost = cost;
        this.flags = flags;
        this.flagsOff = flagsOff;
        this.kindFLags = kindFLags;
        this.kindFLagsOff = kindFLagsOff;
        this.modifiers = modifiers;
        this.minModifiers = minModifiers;
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = curses;
        this.rating = rating;
        this.allocProb = allocProb;
        this.allocMin = allocMin;
        this.allocMax = allocMax;
        this.possItems = possItems;
        this.toHit = toHit;
        this.toDam = toDam;
        this.toAC = toAC;
        this.minToHit = minToHit;
        this.minToDam = minToDam;
        this.minToAC = minToAC;
        this.activation = activation;
        this.time = time;
        this.everSeen = everSeen;
    }
}