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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.middle.effect.Expression;
import uk.co.jackoftrades.middle.enums.EffectBaseType;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.ValueEnum;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.List;
import java.util.Map;

/**
 * The definition of a curse (as loaded from {@code curse.txt}) — a negative
 * property that can attach to objects, with the object bases it can affect, its
 * weight/conflicts, the flags it grants, its effect and timed effect, combat
 * penalties, value modifiers and flavour message. This is the Java port of the C
 * original's {@code struct curse} ({@code src/object.h}); live instances pair a
 * {@code Curse} with {@link CurseData}.
 *
 * @author ClaudeCode
 */
public class Curse {
    /**
     * Logger used to report malformed curse definitions.
     *
     * @author ClaudeCode
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * The curse's name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * Whether this curse can occur on a randomly-generated (non-artifact) item.
     *
     * @author ClaudeCode
     */
    private boolean poss;
    /**
     * The object bases this curse may attach to.
     *
     * @author ClaudeCode
     */
    private List<ObjectBase> objectBases;
    /**
     * The name of a curse this one conflicts with (cannot co-occur).
     *
     * @author ClaudeCode
     */
    private String conflict;
    /**
     * Weight the curse adds (affects encumbrance/selection).
     *
     * @author ClaudeCode
     */
    private int weight;
    /**
     * The object flags this curse grants.
     *
     * @author ClaudeCode
     */
    private List<ObjectFlag> flags;
    /**
     * Object flags that conflict with this curse.
     *
     * @author ClaudeCode
     */
    private List<ObjectFlag> conflictFlags;
    /**
     * The curse's magnitude dice, if a literal dice expression.
     *
     * @author ClaudeCode
     */
    private Random dice;
    /**
     * The curse's magnitude as a scaling expression, if not literal dice.
     *
     * @author ClaudeCode
     */
    private Expression diceExpression;
    /**
     * A monster race flag associated with the curse's effect.
     *
     * @author ClaudeCode
     */
    private MonsterRaceFlag monsterRaceFlag;
    /**
     * (Unused/legacy) timed effects field.
     *
     * @author ClaudeCode
     */
    private TimedEffect timedEffects;
    /**
     * Timing dice for the curse's recurring effect.
     *
     * @author ClaudeCode
     */
    private Random time;
    /**
     * Human-readable description of the curse.
     *
     * @author ClaudeCode
     */
    private String description;
    /**
     * The effect the curse triggers.
     *
     * @author ClaudeCode
     */
    private EffectEnum effect;
    /**
     * The timed effect the curse inflicts.
     *
     * @author ClaudeCode
     */
    private TimedEffect timedEffect;
    /**
     * To-hit penalty imposed by the curse.
     *
     * @author ClaudeCode
     */
    private int combatToHit;
    /**
     * To-damage penalty imposed by the curse.
     *
     * @author ClaudeCode
     */
    private int combatDam;
    /**
     * Armour-class penalty imposed by the curse.
     *
     * @author ClaudeCode
     */
    private int combatAC;
    /**
     * Scaling expression used to compute curse values.
     *
     * @author ClaudeCode
     */
    private Expression expression;
    /**
     * Modifiers the curse applies to object combat values.
     *
     * @author ClaudeCode
     */
    private Map<ValueEnum, Integer> valueCollection;
    /**
     * Flavour message shown when the curse triggers.
     *
     * @author ClaudeCode
     */
    private String message;

    /**
     * Build a curse from its parsed data-file fields. The {@code dice} string is
     * interpreted as a literal dice expression, an expression placeholder
     * (prefixed with {@code $}), or empty (no dice).
     *
     * @param name                curse name
     * @param poss                whether it can occur on random items
     * @param objectBases         affectable object bases
     * @param weight              added weight
     * @param flags               granted object flags
     * @param conflict            conflicting curse name
     * @param conflictFlags       conflicting object flags
     * @param dice                magnitude dice string (or {@code $}-expression)
     * @param time                timing dice string
     * @param description         description
     * @param effect              triggered effect
     * @param monsterRaceFlag     associated race flag
     * @param timedEffect         inflicted timed effect
     * @param combatToHit         to-hit penalty
     * @param combatDam           to-damage penalty
     * @param combatAC            armour-class penalty
     * @param expressionChar      expression placeholder letter
     * @param expressionEffect    expression base type
     * @param expressionOperation expression operation chain
     * @param valueCollection     combat-value modifiers
     * @param message             trigger message
     * @author ClaudeCode
     */
    public Curse(String name,
                 boolean poss,
                 List<ObjectBase> objectBases,
                 int weight,
                 List<ObjectFlag> flags,
                 String conflict,
                 List<ObjectFlag> conflictFlags,
                 String dice,
                 String time,
                 String description,
                 EffectEnum effect,
                 MonsterRaceFlag monsterRaceFlag,
                 TimedEffect timedEffect,
                 int combatToHit,
                 int combatDam,
                 int combatAC,
                 char expressionChar,
                 EffectBaseType expressionEffect,
                 String expressionOperation,
                 Map<ValueEnum, Integer> valueCollection,
                 String message) {
        this.name = name;
        this.poss = poss;
        this.objectBases = objectBases;
        this.weight = weight;
        this.flags = flags;
        this.conflict = conflict;
        this.conflictFlags = conflictFlags;
        if (dice.isBlank()) {
            this.dice = null;
            this.diceExpression = null;
        } else if (!dice.startsWith("$")) {
            //this.dice = Reader.parseDiceString(dice);
            this.diceExpression = null;
        } else {
            this.dice = null;
            this.diceExpression = new Expression(dice.substring(1).charAt(0),
                    expressionEffect, expressionOperation);
        }
        if (time.isBlank())
            this.time = null;
//        else
//            this.time = Reader.parseDiceString(time);
        this.description = description;
        this.effect = effect;
        this.monsterRaceFlag = monsterRaceFlag;
        this.timedEffect = timedEffect;
        this.combatToHit = combatToHit;
        this.combatDam = combatDam;
        this.combatAC = combatAC;
        this.expression = new Expression(expressionChar, expressionEffect, expressionOperation);
        this.valueCollection = valueCollection;
        this.message = message;
    }

    /**
     * @return the curse's name
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }

    /**
     * @return a debug string listing this curse's fields
     * @author ClaudeCode
     */
    @Override
    public String toString() {
        return "Curse{" +
                "name='" + name + '\'' +
                ", poss=" + poss +
                ", objectBases=" + objectBases +
                ", conflict='" + conflict + '\'' +
                ", flags=" + flags +
                ", conflictFlags=" + conflictFlags +
                ", dice=" + dice +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", effect=" + effect +
                ", timedEffect=" + timedEffect +
                ", combatToHit=" + combatToHit +
                ", combatDam=" + combatDam +
                ", combatAC=" + combatAC +
                ", expression=" + expression +
                ", valueCollection=" + valueCollection +
                ", message='" + message + '\'' +
                '}';
    }
}