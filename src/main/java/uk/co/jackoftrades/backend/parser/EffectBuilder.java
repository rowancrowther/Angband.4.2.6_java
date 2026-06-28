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

package uk.co.jackoftrades.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.effect.*;
import uk.co.jackoftrades.middle.enums.*;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Holder for the buildEffects method to allow multiple grammars to
 * obtain a List of Effects from a List of a List of Strings.
 *
 * @author Rowan Crowther
 */
public class EffectBuilder {
    private static final Logger logger = LogManager.getLogger();

    @Contract(pure = true)
    private EffectBuilder() {
    }

    /**
     * Workhorse of the system, converts a List of List of Strings to a List of
     * Effect.
     *
     * @param input The input double nested list. Each inner list consists of 12
     *              strings.
     *              <ul>
     *              <li>type - the EffectEnum tag for the type of this effect</li>
     *              <li>subTypeWrapperInit - the raw string that is used to determine
     *                                       the actual EffectSubTypeWrapper value</li>
     *              <li>radius - a string containing an integer</li>
     *              <li>other - a string containing an integer</li>
     *              <li>diceString - a simple dice string</li>
     *              <li>yVal - a string containing the y (vertical) range of this effect
     *                         from its centre</li>
     *              <li>xVal - a string containing the x (horizontal) range of this effect
     *                         from its centre</li>
     *              <li>expressionChars - a caret (^) delimited list of chars used
     *                                    in conjunction with expressionOperation to
     *                                    work out the random value based on a value
     *                                    given in expressionBase</li>
     *              <li>expressionBase - a caret (^) delimited list of strings which
     *                                   represent an EffectBaseType. See previous bullet
     *                                   point for further details</li>
     *              <li>expressionOperation - a caret (^) delimited list of operations.
     *                                        see bullet two back for further details</li>
     *              <li>timeDiceString - a simple dice string used to determine how
     *                                   many turns an effect will last</li>
     *              <li>effectMessage - a string giving the message displayed when the
     *                                  effect triggers</li>
     *              </ul>
     * @return A List of Effects
     * @author Rowan Crowther
     */
    @Contract(value = "_ -> new")
    @NotNull
    @CheckReturnValue
    public static List<Effect> buildEffects(@NotNull List<List<String>> input) {
        List<Effect> effects = new ArrayList<>();

        for (List<String> effectBase : input) {
            effects.add(buildEffect(effectBase));
        }

        return effects;
    }

    /**
     * Create an effect from the incoming string values
     *
     * @param tuple A List of string values
     * @return An effect
     * @throws IllegalArgumentException If any of the arguments are incorrect
     * @author Rowan Crowther
     */
    @Contract("_ -> new")
    @CheckReturnValue
    @NotNull
    private static Effect buildEffect(@NotNull List<String> tuple) throws IllegalArgumentException {
        EffectEnum effectType = EffectEnum.valueOf("EF_" + tuple.get(0));
        EffectSubTypeEnum effectSubTypeEnum = effectType.getSubType();
        EffectSubTypeWrapper effectWrapper = buildWrapper(effectSubTypeEnum, tuple.get(1));
        int radius = 0;
        if (tuple.get(2) != null && !tuple.get(2).isEmpty())
            radius = Integer.parseInt(tuple.get(2));
        int otherParameter = 0;
        if (tuple.get(3) != null && !tuple.get(3).isEmpty())
            otherParameter = Integer.parseInt(tuple.get(3));
        String diceString = tuple.get(4);
        int yVal = 0;
        if (tuple.get(5) != null && !tuple.get(5).isEmpty())
            yVal = Integer.parseInt(tuple.get(5));
        int xVal = 0;
        if (tuple.get(6) != null && !tuple.get(6).isEmpty())
            xVal = Integer.parseInt(tuple.get(6));
        List<Expression> expressions = buildExpressions(tuple.get(7), tuple.get(8), tuple.get(9));
        String randomString = tuple.get(10);
        Random timeDice = null;
        if (randomString != null && !randomString.isEmpty())
            timeDice = Random.parseStr(tuple.get(10));
        String effectMessage = tuple.get(11);

        return new Effect(effectType, null, diceString, yVal, xVal, effectSubTypeEnum,
                effectWrapper, radius, otherParameter, timeDice,
                expressions, effectMessage);
    }

    /**
     * Build a list of expressions from three caret (^) delimited strings
     *
     * @param characters      A caret (^) delimited list of characters, normally B, D, M or S
     *                        used to determine which part of the complex dice string is
     *                        replaced by this expression
     * @param effectBaseTypes A caret (^) delimited list of EffectBaseType values
     * @param operations      A caret (^) delimited list of operations, stored as a string
     *                        at the moment - may change to a operator/operand list later
     *                        on
     * @return an ArrayList of Expressions
     * @author Rowan Crowther
     */
    @Contract("_, _, _ -> new")
    @NotNull
    @CheckReturnValue
    private static List<Expression> buildExpressions(@NotNull String characters, @NotNull String effectBaseTypes, @NotNull String operations) {
        if (characters.isEmpty())
            return new ArrayList<>();

        List<Expression> expressions = new ArrayList<>();
        String[] chars = characters.split("\\^");
        String[] bases = effectBaseTypes.split("\\^");
        String[] ops = operations.split("\\^");

        if (chars.length != bases.length || chars.length != ops.length) {
            String message = "Expression fields are of incorrect size once split.";
            IllegalArgumentException e = new IllegalArgumentException(message);
            logger.error(message, e);
            return new ArrayList<>();
        }

        for (int index = 0; index < chars.length; index++) {
            char expChar = chars[index].charAt(0);
            EffectBaseType expBaseType = EffectBaseType.valueOf("EFB_" + bases[index]);
            String operation = ops[index];

            Expression expression = new Expression(expChar, expBaseType, operation);
            expressions.add(expression);
        }

        return expressions;
    }

    /**
     * Builds an EffectSubTypeWrapper from the subType a string representation of the value
     * being wrapped
     *
     * @param subType the subtype of the effect
     * @param raw     the string representation, either a enum value less the prefix, or the
     *                name of a 'summon'
     * @return A wrapped effect value
     * @throws IllegalArgumentException if an unknown subType is found.
     * @author Rowan Crowther
     */
    @Nullable
    private static EffectSubTypeWrapper buildWrapper(@NotNull EffectSubTypeEnum subType, @NotNull String raw) throws IllegalArgumentException {
        if (subType == EffectSubTypeEnum.EST_NONE)
            return null;

        if (null == raw || raw.equals("null")) {
            String message = "Invalid value for subType " + subType;
            IllegalArgumentException e = new IllegalArgumentException(message);
            logger.error(message, e);
            return null;
        }

        switch (subType) {
            case EST_TMD:
                TimedEffect timedEffect = TimedEffect.valueOf("TMD_" + raw);
                return new EffectSubTypeWrapper(timedEffect);

            case EST_EARTHQUAKE:
                Earthquake earthquake = Earthquake.valueOf("QUAKE_" + raw);
                return new EffectSubTypeWrapper(earthquake);

            case EST_PROJ:
                ProjectionEnum project = ProjectionEnum.valueOf("PROJ_" + raw);
                return new EffectSubTypeWrapper(project);

            case EST_GLYPH:
                GlyphType glyph = GlyphType.valueOf("GLYPH_" + raw);
                return new EffectSubTypeWrapper(glyph);

            case EST_STAT:
                Stats stat = Stats.valueOf("STAT_" + raw);
                return new EffectSubTypeWrapper(stat);

            case EST_SUMMON:
                Summon summon = GameConstants.lookupSummon(raw);
                if (summon == null) {
                    String message = "Summon could not be found on the given string: " + raw;
                    IllegalArgumentException e = new IllegalArgumentException(message);
                    logger.error(message, e);
                    throw e;
                }
                return new EffectSubTypeWrapper(summon);

            case EST_ENCHANT:
                EffectEnchant effectEnchant = EffectEnchant.valueOf("EE_" + raw);
                return new EffectSubTypeWrapper(effectEnchant);

            case EST_MON_TMD:
                MonTimed monTimed = MonTimed.valueOf("MON_TMD_" + raw);
                return new EffectSubTypeWrapper(monTimed);

            case EST_NOURISH:
                EffectNourish effectNourish = EffectNourish.valueOf("EN_" + raw);
                return new EffectSubTypeWrapper(effectNourish);

            case EST_TELEPORT:
                TeleportEnum teleportType = TeleportEnum.valueOf("TELE_" + raw);
                return new EffectSubTypeWrapper(teleportType, false);

            case EST_TELEPORT_TO:
                TeleportEnum teleportToType = TeleportEnum.valueOf("TELE_" + raw);
                return new EffectSubTypeWrapper(teleportToType, true);

            case EST_SHAPECHANGE:
                PlayerShape shape = GameConstants.lookupPlayerShape(raw);
                if (shape == null) {
                    String message = "Shape could not be found on the given string: " + raw;
                    IllegalArgumentException e = new IllegalArgumentException(message);
                    logger.error(message, e);
                    throw e;
                }
                return new EffectSubTypeWrapper(shape);

            case EST_SUMMON_SPEC:
                SummonType type = SummonType.valueOf("SUM_" + raw);
                return new EffectSubTypeWrapper(type);

            default:
                IllegalArgumentException exception = new IllegalArgumentException("Unknown EffectSubType: " + raw);
                logger.error(exception.getMessage(), exception);
                return null;
        }
    }
}