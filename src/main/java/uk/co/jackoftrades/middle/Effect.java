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

package uk.co.jackoftrades.middle;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.EffectSubTypeEnum;
import uk.co.jackoftrades.middle.enums.Projection;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

/**
 * STUB CLASS: TODO Code, comment and test this
 */

public class Effect {
    private EffectEnum index;
    private Random dice;
    private int y;
    private int x;
    private EffectSubTypeEnum subType;
    private TimedEffect timedEffect;
    private Projection projection;
    private int radius;
    private Object otherParameter;
    private String msg;
    private Expression expression;

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

    @Contract(mutates = "this")
    public Effect(EffectEnum index,
                  String dice,
                  int y,
                  int x,
                  TimedEffect timedEffect,
                  Projection projection,
                  int radius,
                  Object otherParameter,
                  String msg,
                  Expression expression) {
        this.index = index;
        this.dice = null;// Random.parseRandom(dice);
        this.y = y;
        this.x = x;
        this.projection = projection;
        this.timedEffect = timedEffect;
        this.radius = radius;
        this.otherParameter = otherParameter;
        this.msg = msg;
        this.expression = expression;
    }
}