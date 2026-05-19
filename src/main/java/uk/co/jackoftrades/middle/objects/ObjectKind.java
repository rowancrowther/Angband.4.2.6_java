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
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.strings.Quark;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.Effect;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.util.ArrayList;
import java.util.HashMap;

public class ObjectKind {
    private String name;
    private String text;

    private ObjectBase base;
    private int kindIndex;

    private Random pVal; // Item extra parameter

    private Random toH;
    private Random toD;
    private Random toA;

    private int ac;
    private int damageDice;
    private int damageSides;
    private int weight;

    private int cost;

    private Flag<ObjectFlag> flags;
    private Flag<ObjectKindFlag> kindFlags;

    private HashMap<ObjectModifier, Random> modifiers;
    private HashMap<ElementEnum, ElementInfo> elInfo;

    private HashMap<Brand, Boolean> brands;
    private HashMap<Slay, Boolean> slays;
    private HashMap<Curse, Integer> curses;

    private AngbandDisplayCharacter character;

    private int alloc_prob;
    private int alloc_min;
    private int alloc_max;
    private int level;

    private ArrayList<Activation> activations;
    private ArrayList<Effect> effect;
    private int power;
    private String effectMessage;
    private String visMessage;
    private Random time;
    private Random charge;

    private int genMultProb;
    private Random stackSize;

    private Flavour flavour;

    private Quark noteAware;
    private Quark noteUnaware;

    private boolean aware;
    private boolean tried;

    private int ignore;
    private boolean everseen;

    public String getName() {
        return name;
    }

    public ObjectBase getBase() {
        return base;
    }
}
