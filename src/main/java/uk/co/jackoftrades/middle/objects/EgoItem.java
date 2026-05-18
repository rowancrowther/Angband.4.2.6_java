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
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.util.ArrayList;
import java.util.HashMap;

public class EgoItem {
    private String name;
    private String text;

    private int egoIndex;

    private int cost;

    private Flag<ObjectFlag> flags;
    private Flag<ObjectFlag> flagsOff;
    private Flag<ObjectKindFlag> kindFLags;

    private HashMap<ObjectModifier, Random> modifiers;
    private HashMap<ObjectModifier, Integer> minModifiers;
    private HashMap<ElementEnum, ElementInfo> elInfo;

    private ArrayList<Boolean> brands;
    private ArrayList<Boolean> slays;
    private ArrayList<Integer> curses;

    private int rating;
    private int allocProb;
    private int allocMin;
    private int allocMax;

    private ArrayList<ObjectKind> possItems;

    private Random toHit;
    private Random toDam;
    private Random toAC;

    private int minToHit;
    private int minToDam;
    private int minToAC;

    private Activation activation;
    private Random time;

    private boolean everSeen;
}