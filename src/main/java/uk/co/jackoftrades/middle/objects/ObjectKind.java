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
import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectParser;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.strings.Quark;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectKind {
    public record CurseEntry(Curse curse, CurseData curseData) {
    }

    private String name;
    private String text;

    private ObjectBase base;
    private int kindIndex;

    private TValue tValue;
    private String sValue;

    private Random pVal; // Item extra parameter

    private Random toH;
    private Random toD;
    private Random toA;

    private int ac;
    private Random baseDamage;
    private int damageDice;
    private int damageSides;
    private int weight;

    private int cost;

    private Flag<ObjectFlag> flags;
    private Flag<ObjectKindFlag> kindFlags;

    private Map<ObjectModifier, Random> modifiers;
    private Map<ElementEnum, ElementInfo> elInfo;

    private Map<Brand, Boolean> brands;
    private Map<Slay, Boolean> slays;
    private Map<CurseEntry, Boolean> curses;

    private AngbandDisplayCharacter character;

    private int alloc_prob;
    private int alloc_min;
    private int alloc_max;
    private int level;

    private List<Activation> activations;
    private List<Effect> effect;
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

    public ObjectKind() {
        elInfo = new HashMap<>();
        kindFlags = new Flag<>(ObjectKindFlag.class);
        flags = new Flag<>(ObjectFlag.class);
        activations = new ArrayList<>();
        effect = new ArrayList<>();
        brands = new HashMap<>();
        slays = new HashMap<>();
        curses = new HashMap<>();
    }

    public void setCharacter(AngbandDisplayCharacter character) {
        this.character = character;
    }

    public ObjectKind(AngbandDisplayCharacter adc, int cost,
                      int level, int min, int max,
                      String name, TValue tvalue, String sValue,
                      ObjectBase base, boolean isDungeon
    ) {
        this.name = name;
        this.character = adc;
        this.damageDice = 1;
        this.damageSides = 1;
        this.weight = 30;
        this.cost = cost;
        this.level = level;
        this.alloc_min = min;
        this.alloc_max = max;
        this.tValue = tvalue;
        this.sValue = sValue;
        this.base = base;

        elInfo = new HashMap<>();
        kindFlags = new Flag<>(ObjectKindFlag.class);
        if (isDungeon) {
            for (ElementEnum ee : ElementEnum.values()) {
                ElementInfo ei = new ElementInfo();
                ei.on(ElementInfoEnum.EL_INFO_IGNORE);
                elInfo.put(ee, ei);

                kindFlags.on(ObjectKindFlag.KF_GOOD);
            }
        }

        modifiers = new HashMap<>();
        flags = new Flag<>(ObjectFlag.class);
        brands = new HashMap<>();
        slays = new HashMap<>();
        curses = new HashMap<>();
        activations = new ArrayList<>();
        effect = new ArrayList<>();
    }

    public ObjectKind(String name, String text, ObjectBase base,
                      int kindIndex, String pVal, String toH,
                      String toD, String toA, int ac, String baseDamage,
                      int damageDice, int damageSides,
                      int weight, int cost,
                      Flag<ObjectFlag> flags,
                      Flag<ObjectKindFlag> kindFlags,
                      Map<ObjectModifier, String> modifiers,
                      Map<ElementEnum, ElementInfo> elInfo,
                      Map<Brand, Boolean> brands,
                      Map<Slay, Boolean> slays,
                      Map<ItemObjectParser.CurseEntry, Boolean> curses,
                      AngbandDisplayCharacter character,
                      int alloc_prob, int alloc_min,
                      int alloc_max, int level,
                      List<Activation> activations,
                      List<Effect> effect, String effectMessage,
                      String visMessage, String time,
                      String charge, int genMultProb,
                      String stackSize, Flavour flavour,
                      Quark noteAware, Quark noteUnaware,
                      boolean aware, boolean tried,
                      int ignore, boolean everseen, TValue tValue) {
        this.name = name;
        this.text = text;
        this.base = base;
        this.kindIndex = kindIndex;
        this.pVal = Random.parseStr(pVal);
        this.toH = Random.parseStr(toH);
        this.toD = Random.parseStr(toD);
        this.toA = Random.parseStr(toA);
        this.ac = ac;
        this.baseDamage = Random.parseStr(baseDamage);
        this.damageDice = damageDice;
        this.damageSides = damageSides;
        this.weight = weight;
        this.cost = cost;
        this.flags = flags;
        this.kindFlags = kindFlags;
        this.modifiers = new HashMap<>();
        for (ObjectModifier mod : modifiers.keySet()) {
            Random r = Random.parseStr(modifiers.get(mod));
            this.modifiers.put(mod, r);
        }
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = new HashMap<>();
        for (ItemObjectParser.CurseEntry ce : curses.keySet()) {
            CurseEntry thisCE = new CurseEntry(ce.curse(), ce.curseData());
            this.curses.put(thisCE, curses.get(ce));
        }
        this.character = character;
        this.alloc_prob = alloc_prob;
        this.alloc_min = alloc_min;
        this.alloc_max = alloc_max;
        this.level = level;
        this.activations = activations;
        this.effect = effect;
        this.power = power;
        this.effectMessage = effectMessage;
        this.visMessage = visMessage;
        this.time = Random.parseStr(time);
        this.charge = Random.parseStr(charge);
        this.genMultProb = genMultProb;
        this.stackSize = Random.parseStr(stackSize);
        this.flavour = flavour;
        this.noteAware = noteAware;
        this.noteUnaware = noteUnaware;
        this.aware = aware;
        this.tried = tried;
        this.ignore = ignore;
        this.everseen = everseen;
        this.tValue = tValue;
    }

    public String getName() {
        return name;
    }

    public ObjectBase getBase() {
        return base;
    }

    public void setAlloc_prob(int alloc_prob) {
        this.alloc_prob = alloc_prob;
    }

    public void setAlloc_min(int alloc_min) {
        this.alloc_min = alloc_min;
    }

    public void setAlloc_max(int alloc_max) {
        this.alloc_max = alloc_max;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public TValue gettValue() {
        return tValue;
    }
}
