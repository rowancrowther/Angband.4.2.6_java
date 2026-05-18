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
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.HashMap;

public class Artifact {
    private String name;
    private String text;

    private int index;

    private TValue tValue;
    private int sValue;

    private int toHit;
    private int toDam;
    private int toAC;
    private int ac;

    private int damageDice;
    private int damageSides;

    private int weight;

    private int cost;

    private Flag<ObjectFlag> flags;

    private HashMap<ObjectModifier, Integer> modifiers;
    private HashMap<ElementEnum, ElementInfo> elInfo;

    private boolean brands;
    private boolean slays;
    private int curses;

    private int level;

    private int allocProb;
    private int allocMin;
    private int allocMax;

    private Activation activation;
    private String activationMessage;

    private Random time;
}
