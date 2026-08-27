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

package uk.co.jackoftrades.middle.effect;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.enums.EffectBaseType;
import uk.co.jackoftrades.middle.enums.EffectEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * One configured effect instance — the runtime pairing of an {@link EffectEnum}
 * with the concrete parameters it needs: dice for its magnitude, a sub-type
 * payload ({@link EffectSubTypeWrapper}), radius, target offset, timing and any
 * {@link Expression}s that scale values at evaluation time. Effects are chained
 * to build spells, traps, activations, etc. This is the Java port of the C
 * original's {@code struct effect} ({@code src/effects.h}).
 *
 * @author Rowan Crowther
 */
public class Effect {
    /**
     * This effect's type
     */
    private EffectEnum index;

    /**
     * The dice expression giving the effect's primary magnitude.
     */
    private Random dice;

    /**
     * The unparsed complex dice string linked with an expression (retained until expression is resolved).
     */
    private String diceString;

    /**
     * Target vertical offset (for positioned effects).
     */
    private int y;

    /**
     * Target horizontal offset (for positioned effects).
     */
    private int x;

    /**
     * The effect's sub-type category.
     */
    private EffectSubTypeEnum subType;

    /**
     * The typed sub-type payload (its meaning depends on {@link #subType}).
     */
    private EffectSubTypeWrapper value;

    /**
     * The effect's radius, as an int
     */
    private int radius;

    /**
     * A free-form extra parameter whose meaning depends on the effect.
     */
    private int otherParameter;

    // Two effect values not presently used
    //    private String visMsg;
    //    private int power;

    /**
     * The message displayed when the effect triggers
     */
    private String msg;

    /**
     * The effect's timing/duration, as a Random expression.
     */
    private Random time;

    /**
     * Expressions that occur on the effect triggering. Each expression consists
     * of a char linked to the diceString, an {@link EffectBaseType} used to determine
     * which value is plugged into the equation to create the dice value, and
     * an operation, which is a string representation of a set of operator/operand
     * values used to calculate the exact value plugged into the dice value
     */
    private final List<Expression> expression;

    /**
     * Build a fully-specified effect from its parsed fields.
     *
     * @param index          the effect kind
     * @param dice           magnitude dice
     * @param diceString     unparsed complex dice string
     * @param y              target row offset
     * @param x              target column offset
     * @param subType        sub-type category
     * @param value          typed subtype payload
     * @param radius         radius int
     * @param otherParameter integer extra parameter
     * @param time           timing dice
     * @param expression     value-scaling expressions
     * @param msg            String message on effect triggering
     */
    public Effect(EffectEnum index, Random dice, String diceString, int y, int x, EffectSubTypeEnum subType,
                  EffectSubTypeWrapper value, int radius, int otherParameter, Random time,
                  List<Expression> expression, String msg) {
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
        this.msg = msg;
    }

//    /**
//     * Set this effect's typed sub-type payload.
//     *
//     * @param value the sub-type payload to store
//     * @author Rowan Crowther
//     */
//    public void setWrapperValue(EffectSubTypeWrapper value) {
//        this.value = value;
//    }

    /**
     * Determines whether this Effect has a valid Effect index
     *
     * @return true if the index is not EF_NONE or EF_MAX, false otherwise
     */
    @Contract(pure = true)
    public boolean isValid() {
        return index != EffectEnum.EF_NONE && index != EffectEnum.EF_MAX;
    }


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

//    /**
//     * Set the effect's target offset.
//     *
//     * @param y target row offset
//     * @param x target column offset
//     * @author Rowan Crowther
//     */
//    public void setYX(int y, int x) {
//        this.y = y;
//        this.x = x;
//    }

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
     * @return the recharge or duration dice attached to this effect, shared with this instance -
     * C's {@code effect->time}
     */
    public Random getTime() {
        return time;
    }

    /**
     * Returns an independent copy of this effect.
     *
     * <p>Deep-copied because their contents are mutable: the expression list (each
     * {@link Expression} copied in turn), the magnitude dice, the sub-type payload and the time
     * dice. Everything else is a primitive, an immutable {@link String} or an enum constant and goes
     * through the constructor unchanged.
     *
     * <p>Note that {@code index} is shared rather than copied. It identifies which effect this is,
     * and two copies of the same effect are meant to point at the same one.
     *
     * <p><b>Not chained.</b> C's {@code effect} is a linked list and its copy walks {@code ->next};
     * the port holds effects in a {@link java.util.List} owned by the object, so a copy here is one
     * effect and the caller copies the list.
     *
     * <p>Function copy commented in full on 260827.
     *
     * @return a new effect that shares no mutable state with this one
     */
    public Effect copy() {
        List<Expression> expression = new ArrayList<>();

        for (Expression e : this.expression) {
            expression.add(e.copy());
        }
        Random newDice = this.dice.copy();

        EffectSubTypeWrapper newWrapper = this.value.copy();

        Random newTime = this.time.copy();
        Effect copy = new Effect(this.index, newDice, this.diceString, this.y, this.x,
                this.subType, newWrapper, this.radius, this.otherParameter, newTime,
                expression, this.msg);

        return copy;
    }

    //    /**
//     * Set the unparsed dice string for this effect's magnitude.
//     *
//     * @param diceString the dice expression string
//     * @author Rowan Crowther
//     */
//    public void setDice(String diceString) {
//        this.diceString = diceString;
//    }
}