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

package uk.co.jackoftrades.middle.effect;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.middle.enums.EffectEnum;

import java.util.List;

/**
 * One configured effect instance — the runtime pairing of an {@link EffectEnum}
 * with the concrete parameters it needs: dice for its magnitude, a sub-type
 * payload ({@link EffectSubTypeWrapper}), radius, target offset, timing and any
 * {@link Expression}s that scale values at evaluation time. Effects are chained
 * to build spells, traps, activations, etc. This is the Java port of the C
 * original's {@code struct effect} ({@code src/effects.h}).
 *
 * @author ClaudeCode
 */
public class Effect {
    /**
     * Which effect this is.
     *
     * @author ClaudeCode
     */
    private EffectEnum index;
    /**
     * The dice expression giving the effect's primary magnitude.
     *
     * @author ClaudeCode
     */
    private Random dice;
    /**
     * The unparsed dice string (retained until resolved into {@link #dice}).
     *
     * @author ClaudeCode
     */
    private String diceString;
    /**
     * Target row offset (for positioned effects).
     *
     * @author ClaudeCode
     */
    private int y;
    /**
     * Target column offset (for positioned effects).
     *
     * @author ClaudeCode
     */
    private int x;
    /**
     * The effect's sub-type category.
     *
     * @author ClaudeCode
     */
    private EffectSubTypeEnum subType;
    /**
     * The typed sub-type payload (its meaning depends on {@link #subType}).
     *
     * @author ClaudeCode
     */
    private EffectSubTypeWrapper value;
    /**
     * The effect's radius, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random radius;
    /**
     * A free-form extra parameter whose meaning depends on the effect.
     *
     * @author ClaudeCode
     */
    private String otherParameter;
//    private int power;
//    private String msg;
//    private String visMsg;
    /**
     * The effect's timing/duration, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random time;
    /**
     * Expressions that scale this effect's values from game state at evaluation time.
     *
     * @author ClaudeCode
     */
    private List<Expression> expression;

    /**
     * Build a fully-specified effect from its parsed fields.
     *
     * @param index          the effect kind
     * @param dice           magnitude dice
     * @param diceString     unparsed dice string
     * @param y              target row offset
     * @param x              target column offset
     * @param subType        sub-type category
     * @param value          typed sub-type payload
     * @param radius         radius dice
     * @param otherParameter free-form extra parameter
     * @param time           timing dice
     * @param expression     value-scaling expressions
     * @author ClaudeCode
     */
    public Effect(EffectEnum index, Random dice, String diceString, int y, int x, EffectSubTypeEnum subType,
                  EffectSubTypeWrapper value, Random radius, String otherParameter, Random time,
                  List<Expression> expression) {
        this.index = index;
        this.dice = dice;
        this.diceString = diceString;
        this.y = y;
        this.x = x;
        this.subType = subType;
        this.value = value;
        this.radius = radius;
        this.otherParameter = otherParameter;
        this.time = time;
        this.expression = expression;
    }

//    @Contract(mutates = "this")
//    public Effect(EffectEnum type, EffectSubTypeEnum subType, EffectSubTypeWrapper wrappedValue,
//                  String radius, String otherParm) {
//        this.index = type;
//        this.subType = subType;
//        this.value = wrappedValue;
//        this.radius = Random.parseStr(radius);
//        this.otherParameter = otherParm;
//    }

    /**
     * Set this effect's typed sub-type payload.
     *
     * @param value the sub-type payload to store
     * @author ClaudeCode
     */
    @Contract(pure = true)
    public void setWrapperValue(EffectSubTypeWrapper value) {
        this.value = value;
    }

    /**
     * Determines whether this Effect has a valid Effect index
     *
     * @return true if the index is not EF_NONE or EF_MAX, false otherwise
     */
    @Contract(pure = true)
    public boolean isValid() {
        return index != EffectEnum.EF_NONE && index != EffectEnum.EF_MAX;
    }

//    public void setExpression(Expression expression) {
//        this.expression = expression;
//    }

    /**
     * Determines whether this effect is aimed
     *
     * @return True if this effect is aimed, false otherwise
     */
    @Contract(pure = true)
    public boolean isAim() {
        if (!isValid())
            return false;

        return index.getAim();
    }

    /**
     * Get the information label for this effect
     *
     * @return the information label for this, or null if no label exists
     */
    @Contract(pure = true)
    public String getInfo() {
        if (!isValid())
            return null;

        return index.getInfoLabel();
    }

    /**
     * Set the effect's target offset.
     *
     * @param y target row offset
     * @param x target column offset
     * @author ClaudeCode
     */
    public void setYX(int y, int x) {
        this.y = y;
        this.x = x;
    }

    /**
     * Get the description label for this effect
     *
     * @return the description label for this effect, or null if none exists
     */
    @Contract(pure = true)
    public String getDescription() {
        if (!isValid()) return null;

        return index.getDescription();
    }

    /**
     * Set the unparsed dice string for this effect's magnitude.
     *
     * @param diceString the dice expression string
     * @author ClaudeCode
     */
    public void setDice(String diceString) {
        this.diceString = diceString;
    }

//    public Effect(EffectEnum type, EffectSubTypeWrapper subType) {
//        this.index = type;
//        this.dice = null;
//        this.y = 0;
//        this.x = 0;
//        this.value = subType;
//        this.radius = null;
//        this.otherParameter = null;
//        this.msg = null;
//        this.visMsg = null;
//        this.expression = null;
//    }

//    public Effect(EffectEnum index, int y, int x, String diceString,
//                  EffectSubTypeEnum subType, EffectSubTypeWrapper value,
//                  Random radius, String otherParameter, Expression expression) {
//        this.index = index;
//        this.y = y;
//        this.x = x;
//        this.diceString = diceString;
//        this.subType = subType;
//        this.value = value;
//        this.radius = radius;
//        this.otherParameter = otherParameter;
//        this.expression = expression;
//    }
//
//    public Effect(EffectEnum type) {
//        this.index = type;
//        this.dice = null;
//        this.y = 0;
//        this.x = 0;
//        this.value = null;
//        this.radius = null;
//        this.otherParameter = null;
//        this.msg = null;
//        this.visMsg = null;
//        this.expression = null;
//    }
//
//    @Contract(mutates = "this")
//    public Effect(EffectEnum index,
//                  String dice,
//                  int y,
//                  int x,
//                  EffectSubTypeWrapper wrappedValue,
//                  String radius,
//                  String otherParameter,
//                  String msg,
//                  String visMsg,
//                  Expression expression) {
//        this.index = index;
//        this.dice = Random.parseStr(dice);
//        this.y = y;
//        this.x = x;
//        this.value = wrappedValue;
//        this.radius = Random.parseStr(radius);
//        this.otherParameter = otherParameter;
//        this.msg = msg;
//        this.visMsg = visMsg;
//        this.expression = expression;
//    }
//
//    @Contract(mutates = "this")
//    public Effect(EffectEnum index,
//                  String dice,
//                  int y,
//                  int x,
//                  EffectSubTypeWrapper wrappedValue,
//                  String radius,
//                  String otherParameter,
//                  int power,
//                  String msg,
//                  String visMsg,
//                  String time,
//                  Expression expression) {
//        this.index = index;
//        this.dice = Random.parseStr(dice);
//        this.y = y;
//        this.x = x;
//        this.value = wrappedValue;
//        this.radius = Random.parseStr(radius);
//        this.otherParameter = otherParameter;
//        this.power = power;
//        this.msg = msg;
//        this.visMsg = visMsg;
//        this.time = Random.parseStr(time);
//        this.expression = expression;
//    }
}