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

package uk.co.jackoftrades.backend.parser.grammars;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.effect.*;
import uk.co.jackoftrades.middle.enums.*;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.List;

public class EffectAssembler {

    @NotNull
    @CheckReturnValue
    public static List<Effect> assemble(@NotNull List<EffectParseRecord> records,
                                        @NotNull List<String> errors) {
        List<Effect> effects = new ArrayList<>();
        for (EffectParseRecord record : records) {
            Effect e = assembleOne(record, errors, record.line());
            if (e != null) effects.add(e);
        }
        return effects;
    }

    private static Effect assembleOne(@NotNull EffectParseRecord record,
                                      @NotNull List<String> errors,
                                      int line) {
        if (record.typeInit().isEmpty()) {
            errors.add("Effect starting at line: " + line + " has " +
                    "no valid effect type line");
            return null;
        }
        EffectEnum effectEnum = EffectEnum.EF_NONE;
        try {
            effectEnum = EffectEnum.valueOf("EF_" + record.typeInit());
        } catch (IllegalArgumentException e) {
            errors.add("Effect starting at line: " + line + " has " +
                    "invalid effect type: " + record.typeInit());
            return null;
        }
        // Initialise to avoid may not be initialised bug
        EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(EffectSubTypeEnum.EST_NONE);
        if (!record.subTypeWrapper().isEmpty()) {
            wrapper = getWrapperSubType(effectEnum.getSubType(),
                    record.subTypeWrapper(), errors, line);
            if (wrapper == null) return null;
        }
        int radius = 0;
        if (!record.radius().isEmpty()) {
            try {
                radius = Integer.parseInt(record.radius());
            } catch (NumberFormatException e) {
                errors.add("Effect starting at line : " + line + " has " +
                        "invalid radius format: " + record.radius());
                return null;
            }
        }
        int otherParameter = 0;
        if (!record.other().isEmpty()) {
            try {
                otherParameter = Integer.parseInt(record.other());
            } catch (NumberFormatException e) {
                errors.add("Effect starting at line: " + line + " has " +
                        "invalid other parameter format: " + record.other());
                return null;
            }
        }
        int yVal = 0;
        if (!record.yVal().isEmpty()) {
            try {
                yVal = Integer.parseInt(record.yVal());
            } catch (NumberFormatException e) {
                errors.add("Effect starting at line: " + line + " has " +
                        "invalid y-val format: " + record.yVal());
                return null;
            }
        }
        int xVal = 0;
        if (!record.xVal().isEmpty()) {
            try {
                xVal = Integer.parseInt(record.xVal());
            } catch (NumberFormatException e) {
                errors.add("Effect starting at line: " + line + " has " +
                        "invalid x-val format: " + record.xVal());
                return null;
            }
        }
        String diceString = record.diceString();
        String expressionChars = record.expressionChars();
        String expressionBases = record.expressionBases();
        String expressionOperations = record.expressionOperations();
        List<Expression> expressions = getExpressions(expressionChars,
                expressionBases, expressionOperations, line, errors);
        if (expressions == null) return null;
        String msg = record.effectMessage();
        Random time = Random.parseStr(record.timeDiceString());

        return new Effect(effectEnum, null, diceString, yVal, xVal, effectEnum.getSubType(),
                wrapper, radius, otherParameter, time, expressions, msg);
    }

    private static List<Expression> getExpressions(
            @NotNull String chars,
            @NotNull String bases,
            @NotNull String operations,
            int line,
            @NotNull List<String> errors) {
        List<Expression> results = new ArrayList<>();

        if (chars.isEmpty()) return results;

        String delimiter = "^";
        String[] charArray = chars.split(java.util.regex.Pattern.quote(delimiter));
        String[] basesArray = bases.split(java.util.regex.Pattern.quote(delimiter));
        String[] operationsArray = operations.split(java.util.regex.Pattern.quote(delimiter));

        if (charArray.length != basesArray.length || charArray.length != operationsArray.length) {
            errors.add("Expressions in effect starting at line: " + line + " have " +
                    "differing numbers of chars/bases & Operations.");
            return null;
        }

        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i].length() != 1) {
                errors.add("Expression in effect starting at line: " + line + " has " +
                        "an invalid expression code: '" + charArray[i] + "'");
                return null;
            }
            char ch = charArray[i].charAt(0);
            String baseType = basesArray[i];
            EffectBaseType efb = EffectBaseType.EFB_NONE;
            try {
                efb = EffectBaseType.valueOf("EFB_" + baseType);
            } catch (IllegalArgumentException e) {
                errors.add("Expression in effect starting at line: " + line + " has " +
                        "an invalid expression base type: " + baseType);
                return null;
            }
            String operation = operationsArray[i];

            results.add(new Expression(ch, efb, operation));
        }

        return results;
    }

    private static EffectSubTypeWrapper getWrapperSubType(EffectSubTypeEnum type, String value,
                                                          List<String> errors, int line) {
        switch (type) {
            case EST_PROJ -> {
                ProjectionEnum pe = ProjectionEnum.PROJ_NONE;
                try {
                    pe = ProjectionEnum.valueOf("PROJ_" + value);
                    return new EffectSubTypeWrapper(pe);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid projection type: " + value);
                }
            }
            case EST_TMD -> {
                TimedEffect te = TimedEffect.TMD_NONE;
                try {
                    te = TimedEffect.valueOf("TMD_" + value);
                    return new EffectSubTypeWrapper(te);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid timed effect: " + value);
                }
            }
            case EST_NOURISH -> {
                EffectNourish ne = EffectNourish.EN_NONE;
                try {
                    ne = EffectNourish.valueOf("EN_" + value);
                    return new EffectSubTypeWrapper(ne);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid nourish effect: " + value);
                }
            }
            case EST_MON_TMD -> {
                MonTimed mt = MonTimed.MON_TMD_NONE;
                try {
                    mt = MonTimed.valueOf("MON_TMD_" + value);
                    return new EffectSubTypeWrapper(mt);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid mon timed effect: " + value);
                }
            }
            case EST_SHAPECHANGE -> {
                ;

            }
            case EST_SUMMON -> {
                Summon sum = GameConstants.lookupSummon(value);
                if (sum != null)
                    return new EffectSubTypeWrapper(sum);
                else
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid summon type: " + value);
            }
            case EST_STAT -> {
                Stats stat = Stats.STAT_NONE;
                try {
                    stat = Stats.valueOf("STAT_" + value);
                    return new EffectSubTypeWrapper(stat);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid stat type: " + value);
                }
            }
            case EST_ENCHANT -> {
                EffectEnchant ee = EffectEnchant.EE_NONE;
                try {
                    ee = EffectEnchant.valueOf("EE_" + value);
                    return new EffectSubTypeWrapper(ee);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid enchant type: " + value);
                }
            }
            case EST_EARTHQUAKE -> {
                Earthquake eq = Earthquake.QUAKE_NONE;
                try {
                    eq = Earthquake.valueOf("QUAKE_" + value);
                    return new EffectSubTypeWrapper(eq);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid earthquake type: " + value);
                }
            }
            case EST_GLYPH -> {
                GlyphType glyph = GlyphType.GLYPH_NONE;
                try {
                    glyph = GlyphType.valueOf("GLYPH_" + value);
                    return new EffectSubTypeWrapper(glyph);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid glyph type: " + value);
                }
            }
            case EST_TELEPORT -> {
                TeleportEnum teleportEnum = TeleportEnum.TELE_NONE;
                try {
                    teleportEnum = TeleportEnum.valueOf("TELE_" + value);
                    return new EffectSubTypeWrapper(teleportEnum, false);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid teleport type: " + value);
                }
            }
            case EST_TELEPORT_TO -> {
                TeleportEnum teleportEnum = TeleportEnum.TELE_NONE;
                try {
                    teleportEnum = TeleportEnum.valueOf("TELE_" + value);
                    return new EffectSubTypeWrapper(teleportEnum, true);
                } catch (IllegalArgumentException e) {
                    errors.add("Effect starting at line: " + line + " has " +
                            "an invalid teleport type: " + value);
                }
            }
            case EST_NONE -> {
                return new EffectSubTypeWrapper(EffectSubTypeEnum.EST_NONE);
            }
            default -> {
                errors.add("Unsupported effect sub-type: " + type.name());
            }
        }

        return null;
    }
}