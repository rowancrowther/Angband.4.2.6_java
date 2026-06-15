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

import java.util.List;
import java.util.Map;

public class Artifact {
    private String name;
    private String text;

    private int index;

    private TValue tValue;
    private String sValue;

    private int toHit;
    private int toDam;
    private int toAC;
    private int ac;

    // private int damageDice;
    // private int damageSides;
    private String diceString;

    private int weight;

    private int cost;

    private Flag<ObjectFlag> flags;

    private Map<ObjectModifier, Integer> modifiers;
    private Map<ElementEnum, ElementInfo> elInfo;

    private List<Brand> brands;
    private List<Slay> slays;
    private List<Map<Curse, CurseData>> curses;

    private int level;

    private int allocProb;
    private int allocMin;
    private int allocMax;

    private Activation activation;
    private String activationMessage;

    private Random time;

    public Artifact(String name, String text, int index, TValue tValue, String sValue, int toHit, int toDam, int toAC,
                    int ac, String diceString, int weight, int cost, Flag<ObjectFlag> flags, Map<ObjectModifier,
                    Integer> modifiers, List<Brand> brands, List<Slay> slays, List<Map<Curse, CurseData>> curses,
                    int level, int allocProb, int allocMin, int allocMax, Activation activation,
                    String activationMessage, String time) {
        this.name = name;
        this.text = text;
        this.index = index;
        this.tValue = tValue;
        this.sValue = sValue;
        this.toHit = toHit;
        this.toDam = toDam;
        this.toAC = toAC;
        this.ac = ac;
        this.diceString = diceString;
        this.weight = weight;
        this.cost = cost;
        this.flags = flags;
        this.modifiers = modifiers;
        this.brands = brands;
        this.slays = slays;
        this.curses = curses;
        this.level = level;
        this.allocProb = allocProb;
        this.allocMin = allocMin;
        this.allocMax = allocMax;
        this.activation = activation;
        this.activationMessage = activationMessage;
        this.time = Random.parseStr(time);
    }
}
