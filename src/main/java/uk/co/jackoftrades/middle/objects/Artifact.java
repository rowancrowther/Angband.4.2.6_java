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

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.List;
import java.util.Map;

/**
 * A unique artifact definition (as loaded from {@code artifact.txt}) — a one-of-a-kind
 * item with fixed bonuses, flags, modifiers, brands/slays/curses, an optional
 * activation, and allocation parameters. This is the Java port of the C
 * original's {@code struct artifact} ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class Artifact {
    /**
     * The artifact's name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * Flavour/description text.
     *
     * @author Rowan Crowther
     */
    private String text;

    /**
     * Index in the global artifact table.
     *
     * @author Rowan Crowther
     */
    private int index;

    /**
     * The base item type value (tval) the artifact is built on.
     *
     * @author Rowan Crowther
     */
    private TValue tValue;
    /**
     * The base sub-type value (sval).
     *
     * @author Rowan Crowther
     */
    private String sValue;

    /**
     * To-hit bonus.
     *
     * @author Rowan Crowther
     */
    private int toHit;
    /**
     * To-damage bonus.
     *
     * @author Rowan Crowther
     */
    private int toDam;
    /**
     * To-armour-class bonus.
     *
     * @author Rowan Crowther
     */
    private int toAC;
    /**
     * Base armour class.
     *
     * @author Rowan Crowther
     */
    private int ac;

    // private int damageDice;
    // private int damageSides;
    /**
     * Damage dice expression as a string.
     *
     * @author Rowan Crowther
     */
    private String diceString;

    /**
     * Weight.
     *
     * @author Rowan Crowther
     */
    private int weight;

    /**
     * Cost/value.
     *
     * @author Rowan Crowther
     */
    private int cost;

    /**
     * Object flags this artifact grants.
     *
     * @author Rowan Crowther
     */
    private Flag<ObjectFlag> flags;

    /**
     * Numeric modifiers granted, keyed by modifier.
     *
     * @author Rowan Crowther
     */
    private Map<ObjectModifier, Integer> modifiers;
    /**
     * Per-element relation info.
     *
     * @author Rowan Crowther
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * Brands the artifact carries.
     *
     * @author Rowan Crowther
     */
    private List<Brand> brands;
    /**
     * Slays the artifact carries.
     *
     * @author Rowan Crowther
     */
    private List<Slay> slays;
    /**
     * Curses the artifact carries, each with its instance data.
     *
     * @author Rowan Crowther
     */
    private Map<Curse, CurseData> curses;

    /**
     * The artifact's native level.
     *
     * @author Rowan Crowther
     */
    private int level;

    /**
     * Allocation probability weight.
     *
     * @author Rowan Crowther
     */
    private int allocProb;
    /**
     * Minimum allocation depth.
     *
     * @author Rowan Crowther
     */
    private int allocMin;
    /**
     * Maximum allocation depth.
     *
     * @author Rowan Crowther
     */
    private int allocMax;

    /**
     * The artifact's activation, if any.
     *
     * @author Rowan Crowther
     */
    private Activation activation;
    /**
     * Message shown when the activation is used.
     *
     * @author Rowan Crowther
     */
    private String activationMessage;

    /**
     * Recharge time for the activation, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random time;

    public Artifact(String name, String text, int index, TValue tValue, String sValue,
                    int toHit, int toDam, int toAC, int ac, String diceString,
                    int weight, int cost, Flag<ObjectFlag> flags,
                    Map<ObjectModifier, Integer> modifiers,
                    Map<ElementEnum, ElementInfo> elInfo, List<Brand> brands,
                    List<Slay> slays, Map<Curse, CurseData> curses, int level,
                    int allocProb, int allocMin, int allocMax, Activation activation,
                    String activationMessage, Random time) {
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
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = curses;
        this.level = level;
        this.allocProb = allocProb;
        this.allocMin = allocMin;
        this.allocMax = allocMax;
        this.activation = activation;
        this.activationMessage = activationMessage;
        this.time = time;
    }
//
//    /**
//     * Build an artifact from its parsed data-file fields.
//     *
//     * @param name              artifact name
//     * @param text              flavour text
//     * @param index             artifact-table index
//     * @param tValue            base item type value
//     * @param sValue            base sub-type value
//     * @param toHit             to-hit bonus
//     * @param toDam             to-damage bonus
//     * @param toAC              to-AC bonus
//     * @param ac                base armour class
//     * @param diceString        damage dice string
//     * @param weight            weight
//     * @param cost              cost
//     * @param flags             object flags
//     * @param modifiers         numeric modifiers
//     * @param brands            brands
//     * @param slays             slays
//     * @param curses            curses with instance data
//     * @param level             native level
//     * @param allocProb         allocation probability
//     * @param allocMin          minimum allocation depth
//     * @param allocMax          maximum allocation depth
//     * @param activation        activation, if any
//     * @param activationMessage activation message
//     * @param time              recharge-time dice string
//     * @author Rowan Crowther
//     */
//    public Artifact(String name, String text, int index, TValue tValue, String sValue, int toHit, int toDam, int toAC,
//                    int ac, String diceString, int weight, int cost, Flag<ObjectFlag> flags, Map<ObjectModifier,
//                    Integer> modifiers, List<Brand> brands, List<Slay> slays, Map<Curse, CurseData> curses,
//                    int level, int allocProb, int allocMin, int allocMax, Activation activation,
//                    String activationMessage, String time) {
//        this.name = name;
//        this.text = text;
//        this.index = index;
//        this.tValue = tValue;
//        this.sValue = sValue;
//        this.toHit = toHit;
//        this.toDam = toDam;
//        this.toAC = toAC;
//        this.ac = ac;
//        this.diceString = diceString;
//        this.weight = weight;
//        this.cost = cost;
//        this.flags = flags;
//        this.modifiers = modifiers;
//        this.brands = brands;
//        this.slays = slays;
//        this.curses = curses;
//        this.level = level;
//        this.allocProb = allocProb;
//        this.allocMin = allocMin;
//        this.allocMax = allocMax;
//        this.activation = activation;
//        this.activationMessage = activationMessage;
//        this.time = Random.parseStr(time);
//    }
}
