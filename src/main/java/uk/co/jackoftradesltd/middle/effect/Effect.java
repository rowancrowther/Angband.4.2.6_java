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

package uk.co.jackoftradesltd.middle.effect;

import org.jetbrains.annotations.Contract;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.game.event.projection.Source;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.enums.EffectBaseType;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import java.util.ArrayList;
import java.util.List;

/**
 * One configured effect instance — the runtime pairing of an {@link EffectEnum}
 * with the concrete parameters it needs: dice for its magnitude, a sub-type
 * payload ({@link EffectSubTypeWrapper}), radius, target offset, timing and any
 * {@link Expression}s that scale values at evaluation time. Effects are chained
 * to build spells, traps, activations, etc. This is the Java port of the C
 * original's {@code struct effect} ({@code object.h}), together with the utility functions that
 * read it in {@code effects.c}: {@code effect_valid}, {@code effect_aim}, {@code effect_info} and
 * {@code effect_desc}.
 *
 * <p>C chains effects through {@code ->next}; the port has no {@code next} field and holds effects
 * in a {@link List} owned by whatever uses them. {@code time}, {@code diceString} and
 * {@code expression} are port additions with no field in C's {@code struct effect}. C's
 * {@code free_effect} is not ported, as the garbage collector does that work.
 *
 * <p>Class Effect coded before 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public class Effect {
    /**
     * Expressions that occur on the effect triggering. Each expression consists
     * of a char linked to the diceString, an {@link EffectBaseType} used to determine
     * which value is plugged into the equation to create the dice value, and
     * an operation, which is a string representation of a set of operator/operand
     * values used to calculate the exact value plugged into the dice value. A port addition; C's
     * {@code struct effect} has no such field. Mutable, so {@link #copy()} copies each one.
     *
     * <p>Field expression coded before 261001, commented in full on 261001.
     */
    private final List<Expression> expression;
    /**
     * This effect's type — which row of the effect table it uses. Anything other than
     * {@code EF_NONE} and {@code EF_MAX} counts as valid; see {@link #isValid()}. C's {@code index}.
     *
     * <p>Field index coded before 261001, commented in full on 261001.
     */
    private EffectEnum index;
    /**
     * The dice expression giving the effect's primary magnitude. C's {@code dice}, held there as a
     * {@code dice_t}.
     *
     * <p>Field dice coded before 261001, commented in full on 261001.
     */
    private Random dice;
    /**
     * The unparsed complex dice string linked with an expression (retained until expression is resolved).
     * A port addition; C's {@code struct effect} has no such field.
     *
     * <p>Field diceString coded before 261001, commented in full on 261001.
     */
    private String diceString;
    /**
     * Target vertical offset (for positioned effects). C's {@code y}, documented there as "Y
     * coordinate or distance".
     *
     * <p>Field y coded before 261001, commented in full on 261001.
     */
    private int y;
    /**
     * Target horizontal offset (for positioned effects). C's {@code x}, documented there as "X
     * coordinate or distance".
     *
     * <p>Field x coded before 261001, commented in full on 261001.
     */
    private int x;
    /**
     * The effect's sub-type category — whether the sub-type is a projection type, a timed effect
     * type, and so on. Paired with {@link #value}, which carries the payload. C stores both in the
     * single int {@code subtype}.
     *
     * <p>Field subType coded before 261001, commented in full on 261001.
     */
    private EffectSubTypeEnum subType;
    /**
     * The typed sub-type payload (its meaning depends on {@link #subType}). Mutable, so
     * {@link #copy()} copies it.
     *
     * <p>Field value coded before 261001, commented in full on 261001.
     */
    private EffectSubTypeWrapper value;
    /**
     * The effect's radius, as an int, for effects that have one. C's {@code radius}.
     *
     * <p>Field radius coded before 261001, commented in full on 261001.
     */
    private int radius;
    /**
     * A free-form extra parameter whose meaning depends on the effect, passed through to its
     * handler. C's {@code other}.
     *
     * <p>Field otherParameter coded before 261001, commented in full on 261001.
     */
    private int otherParameter;
    /**
     * The message displayed when the effect triggers; C's comment calls it the "message for death
     * or whatever". C's {@code msg}.
     *
     * <p>Field msg coded before 261001, commented in full on 261001.
     */
    private String msg;
    /**
     * The effect's timing/duration, as a Random expression. A port addition; C's
     * {@code struct effect} has no such field. Read through {@link #getTime()}.
     *
     * <p>Field time coded before 261001, commented in full on 261001.
     */
    private Random time;

    /**
     * Build a fully-specified effect from its parsed fields. Every argument is stored as given, with
     * no copying; use {@link #copy()} for an independent instance.
     *
     * <p>Constructor Effect coded before 261001, commented in full on 261001.
     *
     * @param index         the effect kind
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

    /**
     * Determines whether this Effect has a valid Effect index. Port of {@code effect_valid} in
     * {@code effects.c}, which tests {@code index > EF_NONE && index < EF_MAX}; the enum comparison
     * here is equivalent. C's leading NULL check has no counterpart because a Java instance is never
     * null.
     *
     * <p>Function isValid coded before 261001, commented in full on 261001.
     *
     * @return true if the index is not EF_NONE or EF_MAX, false otherwise
     */
    @Contract(pure = true)
    public boolean isValid() {
        return index != EffectEnum.EF_NONE && index != EffectEnum.EF_MAX;
    }


    /**
     * Determines whether this effect, or any effect after it in the list, must be aimed. Port of
     * {@code effect_aim} in {@code effects.c}, which starts at the effect it is handed and walks
     * {@code ->next} to the end of the chain.
     *
     * <p>Here the chain is the {@code effects} list and the start is this effect's position in it,
     * found by identity: this effect is tested first, then each later one. An effect that is not
     * valid returns false at once, without looking further along the list, as in C. An effect that
     * is not in the list also returns false, a case C cannot express. Invalid effects later in the
     * list are skipped; C reads their table row instead, but the {@code EF_NONE} and {@code EF_MAX}
     * rows are both unaimed, so the answer is the same.
     *
     * <p>Function isAim coded on 261001, commented in full on 261001.
     *
     * @param effects the list this effect belongs to
     * @return True if this effect or a later one in {@code effects} is aimed, false otherwise
     */
    @Contract(pure = true)
    public boolean isAim(List<Effect> effects) {
        int effectIndex = effects.indexOf(this);
        if (effectIndex == -1 || !isValid()) return false;

        for (int idx = effectIndex; idx < effects.size(); idx++) {
            if (effects.get(idx).isValid() && (effects.get(idx).index.getAim())) return true;
        }

        return false;
    }

    /**
     * Get the information label for this effect. Port of {@code effect_info} in {@code effects.c}:
     * null for an invalid effect, otherwise the label from the effect table row.
     *
     * <p>Function getInfo coded before 261001, commented in full on 261001.
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
     * Get the description label for this effect. Port of {@code effect_desc} in {@code effects.c}:
     * null for an invalid effect, otherwise the description from the effect table row.
     *
     * <p>Function getDescription coded before 261001, commented in full on 261001.
     *
     * @return the description label for this effect, or null if none exists
     */
    @Contract(pure = true)
    public String getDescription() {
        if (!isValid()) return null;

        return index.getDescription();
    }

    /**
     * Returns the recharge or duration dice attached to this effect. The returned object is the
     * field itself, not a copy, so changes to it are changes to this effect.
     *
     * <p>Function getTime coded before 261001, commented in full on 261001.
     *
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
     * <p>Function copy coded before 261001, commented in full on 261001 (first commented 260827).
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

    /**
     * Stub for {@code effect_do} in {@code effects.c}, which runs an effect chain. Does nothing and
     * always returns false until it is ported, which the TODO below places in Chapter 6. Nothing
     * has been compared against C here.
     *
     * <p>Function effectDo coded on 261001 as a stub, commented in full on 261001.
     *
     * @param origin       who or what the effect comes from
     * @param object       the object that carries the effect, if any
     * @param identifiable whether using the effect can identify the object
     * @param aware        whether the player is already aware of the object's flavour
     * @param direction    the direction chosen for an aimed effect
     * @param beamChance   the chance that a bolt becomes a beam
     * @param boost        the percentage boost applied to the effect
     * @param cmd          the command that triggered the effect
     * @return always false while this is a stub
     */
    public boolean effectDo(Source origin, ItemObject object, boolean identifiable, boolean aware,
                            DirectionEnum direction, int beamChance, int boost, Command cmd) {
        // STUB function: TODO: Implement as part of Chapter 6
        return false;
    }
}