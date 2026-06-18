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

/**
 * STUB CLASS: TODO Code, comment and test this
 */

public class Effect {
    private EffectEnum index;
    private Random dice;
    private String diceString;
    private int y;
    private int x;
    private EffectSubTypeEnum subType;
    private EffectSubTypeWrapper value;
    private Random radius;
    private String otherParameter;
    private int power;
    private String msg;
    private String visMsg;
    private Random time;
    private Expression expression;

    @Contract(mutates = "this")
    public Effect(EffectEnum type, EffectSubTypeEnum subType, EffectSubTypeWrapper wrappedValue,
                  String radius, String otherParm) {
        this.index = type;
        this.subType = subType;
        this.value = wrappedValue;
        this.radius = Random.parseStr(radius);
        this.otherParameter = otherParm;
    }

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

    public void setExpression(Expression expression) {
        this.expression = expression;
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

    public void setYX(int y, int x) {
        this.y = y;
        this.x = x;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
     * Getter for the message for this effect
     *
     * @return The message for this effect
     */
    @Contract(pure = true)
    public String getMessage() {
        if (!isValid()) return null;

        return msg;
    }

    public void setDice(String diceString) {
        this.diceString = diceString;
    }

    public Effect(EffectEnum type, EffectSubTypeWrapper subType) {
        this.index = type;
        this.dice = null;
        this.y = 0;
        this.x = 0;
        this.value = subType;
        this.radius = null;
        this.otherParameter = null;
        this.msg = null;
        this.visMsg = null;
        this.expression = null;
    }

    public Effect(EffectEnum index, int y, int x, String diceString,
                  EffectSubTypeEnum subType, EffectSubTypeWrapper value,
                  Random radius, String otherParameter, Expression expression) {
        this.index = index;
        this.y = y;
        this.x = x;
        this.diceString = diceString;
        this.subType = subType;
        this.value = value;
        this.radius = radius;
        this.otherParameter = otherParameter;
        this.expression = expression;
    }

    public Effect(EffectEnum type) {
        this.index = type;
        this.dice = null;
        this.y = 0;
        this.x = 0;
        this.value = null;
        this.radius = null;
        this.otherParameter = null;
        this.msg = null;
        this.visMsg = null;
        this.expression = null;
    }

    @Contract(mutates = "this")
    public Effect(EffectEnum index,
                  String dice,
                  int y,
                  int x,
                  EffectSubTypeWrapper wrappedValue,
                  String radius,
                  String otherParameter,
                  String msg,
                  String visMsg,
                  Expression expression) {
        this.index = index;
        this.dice = Random.parseStr(dice);
        this.y = y;
        this.x = x;
        this.value = wrappedValue;
        this.radius = Random.parseStr(radius);
        this.otherParameter = otherParameter;
        this.msg = msg;
        this.visMsg = visMsg;
        this.expression = expression;
    }

    @Contract(mutates = "this")
    public Effect(EffectEnum index,
                  String dice,
                  int y,
                  int x,
                  EffectSubTypeWrapper wrappedValue,
                  String radius,
                  String otherParameter,
                  int power,
                  String msg,
                  String visMsg,
                  String time,
                  Expression expression) {
        this.index = index;
        this.dice = Random.parseStr(dice);
        this.y = y;
        this.x = x;
        this.value = wrappedValue;
        this.radius = Random.parseStr(radius);
        this.otherParameter = otherParameter;
        this.power = power;
        this.msg = msg;
        this.visMsg = visMsg;
        this.time = Random.parseStr(time);
        this.expression = expression;
    }
}