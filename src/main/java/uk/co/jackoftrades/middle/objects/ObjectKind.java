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

/**
 * The template for a kind of object (as loaded from {@code object.txt}) — e.g.
 * "Wooden Torch" or "Long Sword": its base type, combat bonuses and dice,
 * weight/cost, flags/modifiers/element info, brands/slays/curses, allocation
 * parameters, activations/effects, flavour and the player's awareness state. Live
 * items ({@link ItemObject}) reference an {@code ObjectKind}. This is the Java
 * port of the C original's {@code struct object_kind} ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public class ObjectKind {
    /**
     * A curse paired with its per-object {@link CurseData} for an object kind.
     *
     * @param curse     the curse
     * @param curseData its instance data (power/timeout)
     * @author ClaudeCode
     */
    public record CurseEntry(Curse curse, CurseData curseData) {
    }

    /**
     * The kind's name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * Flavour/description text.
     *
     * @author ClaudeCode
     */
    private String text;

    /**
     * The base type this kind belongs to.
     *
     * @author ClaudeCode
     */
    private ObjectBase base;
    /**
     * Index of this kind in the global kind table.
     *
     * @author ClaudeCode
     */
    private int kindIndex;

    /**
     * The item type value (tval).
     *
     * @author ClaudeCode
     */
    private TValue tValue;
    /**
     * The sub-type value (sval) as a string.
     *
     * @author ClaudeCode
     */
    private String sValue;

    /**
     * Extra parameter value (the item's "pval"), as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random pVal; // Item extra parameter

    /**
     * Base to-hit bonus, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random toH;
    /**
     * Base to-damage bonus, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random toD;
    /**
     * Base to-armour-class bonus, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random toA;

    /**
     * Base armour class.
     *
     * @author ClaudeCode
     */
    private int ac;
    /**
     * Base damage, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random baseDamage;
    /**
     * Number of damage dice.
     *
     * @author ClaudeCode
     */
    private int damageDice;
    /**
     * Sides per damage die.
     *
     * @author ClaudeCode
     */
    private int damageSides;
    /**
     * Base weight.
     *
     * @author ClaudeCode
     */
    private int weight;

    /**
     * Base cost/value.
     *
     * @author ClaudeCode
     */
    private int cost;

    /**
     * Object flags this kind grants.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectFlag> flags;
    /**
     * Kind flags controlling generation/display.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectKindFlag> kindFlags;

    /**
     * Numeric modifiers granted, keyed by modifier, as dice expressions.
     *
     * @author ClaudeCode
     */
    private Map<ObjectModifier, Random> modifiers;
    /**
     * Per-element relation info (resist/ignore/etc.).
     *
     * @author ClaudeCode
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * Brands this kind carries (mapped to whether they are intrinsic).
     *
     * @author ClaudeCode
     */
    private Map<Brand, Boolean> brands;
    /**
     * Slays this kind carries (mapped to whether they are intrinsic).
     *
     * @author ClaudeCode
     */
    private Map<Slay, Boolean> slays;
    /**
     * Curses this kind carries (mapped to whether they are intrinsic).
     *
     * @author ClaudeCode
     */
    private Map<CurseEntry, Boolean> curses;

    /**
     * The display glyph and colour.
     *
     * @author ClaudeCode
     */
    private AngbandDisplayCharacter character;

    /**
     * Allocation probability weight.
     *
     * @author ClaudeCode
     */
    private int alloc_prob;
    /**
     * Minimum depth at which the kind is allocated.
     *
     * @author ClaudeCode
     */
    private int alloc_min;
    /**
     * Maximum depth at which the kind is allocated.
     *
     * @author ClaudeCode
     */
    private int alloc_max;
    /**
     * The kind's native level.
     *
     * @author ClaudeCode
     */
    private int level;

    /**
     * Activations this kind provides.
     *
     * @author ClaudeCode
     */
    private List<Activation> activations;
    /**
     * Effects this kind produces when used.
     *
     * @author ClaudeCode
     */
    private List<Effect> effect;
    /**
     * The kind's power rating.
     *
     * @author ClaudeCode
     */
    private int power;
    /**
     * Message shown when the kind's effect is used.
     *
     * @author ClaudeCode
     */
    private String effectMessage;
    /**
     * Message shown when the effect is seen.
     *
     * @author ClaudeCode
     */
    private String visMessage;
    /**
     * Recharge/effect timing, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random time;
    /**
     * Charge count (for wands/staves), as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random charge;

    /**
     * Probability used when generating multiple of this kind.
     *
     * @author ClaudeCode
     */
    private int genMultProb;
    /**
     * Stack size when generated, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random stackSize;

    /**
     * The randomised flavour for unidentified instances.
     *
     * @author ClaudeCode
     */
    private Flavour flavour;

    /**
     * Inscription note used once the kind is identified.
     *
     * @author ClaudeCode
     */
    private Quark noteAware;
    /**
     * Inscription note used while the kind is unidentified.
     *
     * @author ClaudeCode
     */
    private Quark noteUnaware;

    /**
     * Whether the player is aware of (has identified) this kind.
     *
     * @author ClaudeCode
     */
    private boolean aware;
    /**
     * Whether the player has tried this kind.
     *
     * @author ClaudeCode
     */
    private boolean tried;

    /**
     * The player's ignore setting for this kind.
     *
     * @author ClaudeCode
     */
    private int ignore;
    /**
     * Whether the player has ever seen this kind.
     *
     * @author ClaudeCode
     */
    private boolean everseen;

    /**
     * Build an empty object kind with fresh collections.
     *
     * @author ClaudeCode
     */
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

    /**
     * Set the kind's display glyph/colour.
     *
     * @param character the display character
     * @author ClaudeCode
     */
    public void setCharacter(AngbandDisplayCharacter character) {
        this.character = character;
    }

    /**
     * Build a partly-specified object kind (used for store/dungeon kinds), seeding
     * default damage/weight and, for dungeon kinds, marking all elements ignored
     * and the kind as "good".
     *
     * @param adc       display character
     * @param cost      base cost
     * @param level     native level
     * @param min       minimum allocation depth
     * @param max       maximum allocation depth
     * @param name      kind name
     * @param tvalue    item type value
     * @param sValue    sub-type value
     * @param base      base type
     * @param isDungeon whether this is a dungeon-generated kind
     * @author ClaudeCode
     */
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

    /**
     * Build a fully-specified object kind from parsed data-file fields, resolving
     * the various dice strings into {@link Random}s and copying the brand/slay/
     * curse maps.
     *
     * @param name          kind name
     * @param text          flavour text
     * @param base          base type
     * @param kindIndex     index in the kind table
     * @param pVal          extra-parameter dice string
     * @param toH           to-hit dice string
     * @param toD           to-damage dice string
     * @param toA           to-AC dice string
     * @param ac            base armour class
     * @param baseDamage    base damage dice string
     * @param damageDice    number of damage dice
     * @param damageSides   sides per damage die
     * @param weight        base weight
     * @param cost          base cost
     * @param flags         object flags
     * @param kindFlags     kind flags
     * @param modifiers     modifier dice strings by modifier
     * @param elInfo        per-element info
     * @param brands        brands (intrinsic flag)
     * @param slays         slays (intrinsic flag)
     * @param curses        curses (intrinsic flag)
     * @param character     display character
     * @param alloc_prob    allocation probability
     * @param alloc_min     minimum allocation depth
     * @param alloc_max     maximum allocation depth
     * @param level         native level
     * @param activations   activations
     * @param effect        effects
     * @param effectMessage effect message
     * @param visMessage    seen-effect message
     * @param time          timing dice string
     * @param charge        charge dice string
     * @param genMultProb   multi-generation probability
     * @param stackSize     stack-size dice string
     * @param flavour       unidentified flavour
     * @param noteAware     identified inscription
     * @param noteUnaware   unidentified inscription
     * @param aware         whether the player is aware of the kind
     * @param tried         whether the player has tried the kind
     * @param ignore        ignore setting
     * @param everseen      whether ever seen
     * @param tValue        item type value
     * @author ClaudeCode
     */
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

    /**
     * @return the kind's name
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }

    /**
     * @return the kind's base type
     * @author ClaudeCode
     */
    public ObjectBase getBase() {
        return base;
    }

    /**
     * @param alloc_prob the allocation probability weight
     * @author ClaudeCode
     */
    public void setAlloc_prob(int alloc_prob) {
        this.alloc_prob = alloc_prob;
    }

    /**
     * @param alloc_min the minimum allocation depth
     * @author ClaudeCode
     */
    public void setAlloc_min(int alloc_min) {
        this.alloc_min = alloc_min;
    }

    /**
     * @param alloc_max the maximum allocation depth
     * @author ClaudeCode
     */
    public void setAlloc_max(int alloc_max) {
        this.alloc_max = alloc_max;
    }

    /**
     * @param cost the base cost/value
     * @author ClaudeCode
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * @return the item type value (tval)
     * @author ClaudeCode
     */
    public TValue gettValue() {
        return tValue;
    }
}
