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

import static uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper.teleport;
import static uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper.teleportTo;

/**
 * Resolves the raw {@link EffectParseRecord}s captured from an {@code effect:} block into
 * domain {@link Effect}s. Effects are shared across many data files (objects, curses, shapes,
 * activations, …), so this assembler is a static helper the owning assemblers delegate to
 * rather than a stand-alone {@code Assembler} keyed to one grammar. It ports the interpretation
 * done by {@code effect_subtype} and the {@code parse_*_effect} handlers in {@code obj-init.c}:
 * the effect name maps to an {@link EffectEnum}, and the effect's second token is resolved
 * through {@link #getWrapperSubType} into whichever payload that effect expects (a projection,
 * timed effect, summon, shape, stat, …), wrapped in a type-safe {@link EffectSubTypeWrapper}.
 *
 * <p>Dice and expressions are captured but <em>not</em> evaluated here — see the note in
 * {@link #assembleOne} — because a die roll must be computed against live game state when the
 * effect fires, not at load time.
 *
 * @author Rowan Crowther
 */
public class EffectAssembler {

    /**
     * Assemble every effect in one block into resolved {@link Effect}s.
     *
     * <p><b>All-or-nothing contract:</b> a block is only meaningful as a whole, so if any single
     * effect fails to resolve this returns {@code null} rather than a partial list. Every calling
     * assembler treats that {@code null} as a reason to drop the <em>entire owning record</em>
     * (the activation, curse, object kind or shape) — the alternative, silently loading a record
     * with one of its effects quietly missing, would produce a subtly wrong game object that is
     * far harder to diagnose than a reported, dropped record. Per-effect error messages are still
     * appended to {@code errors} so every failure in the block is surfaced, not just the first.
     *
     * @param records the parsed effect blocks for one owning record
     * @param errors  the soft-error channel; per-effect failures are appended here
     * @return the resolved effects in order, or {@code null} if any effect failed to resolve
     * @author Rowan Crowther
     */
    @Nullable
    @CheckReturnValue
    public static List<Effect> assemble(@NotNull List<EffectParseRecord> records,
                                        @NotNull List<String> errors) {
        List<Effect> effects = new ArrayList<>();
        boolean failed = false;
        for (EffectParseRecord record : records) {
            Effect e = assembleOne(record, errors, record.line());
            // Keep going after a failure so every bad effect in the block is reported,
            // but remember that the block as a whole must now fail (return null below).
            if (e == null) failed = true;
            else effects.add(e);
        }
        return failed ? null : effects;
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
        // An effect with no subtype token normally resolves to EST_NONE, and the EST_NONE seed
        // below is what it keeps - it also avoids a may-not-be-initialised error on the branch.
        //
        // TELEPORT and TELEPORT_TO are the exception, and the || below exists solely for them.
        // Their subtype is a flag rather than a kind, and its absent value carries meaning: [C]
        // effect_subtype (effects.c) never runs for a missing line, leaving effect->subtype at
        // zero, which the handlers read as "a monster may not cast this at the player". Letting
        // those two kinds through the gate with an empty value lets the switch return a
        // false-valued teleport wrapper instead of a bare EST_NONE, so every TELEPORT effect
        // comes out the same shape whether or not the data file gave it a subtype.
        EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(EffectSubTypeEnum.EST_NONE);
        EffectSubTypeEnum kind = effectEnum.getSubType();
        if (!record.subTypeWrapper().isEmpty() || kind == EffectSubTypeEnum.EST_TELEPORT
                || kind == EffectSubTypeEnum.EST_TELEPORT_TO) {
            wrapper = getWrapperSubType(kind,
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

        // The evaluated dice value (the Random field) is intentionally left null here.
        // Dice are not rolled at parse time: an expression such as "$B+1d8" bound to
        // "B:PLAYER_HP:* 4" must be computed against live game state at the moment the
        // effect fires (4 * player HP + 1d8), which is a runtime action outside the scope
        // of file parsing. This mirrors Angband's C, where dice_parse_string only builds a
        // dice_t skeleton at load and dice_roll/dice_evaluate produce the value at fire time.
        // We therefore retain only the lossless raw diceString plus the parsed expressions,
        // to be parsed and evaluated together by the future runtime roll engine.
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
                // Unlike the enum-backed sub-types above, a shapechange target is a
                // shape *name* resolved against the loaded shape registry (C's
                // shape_name_to_idx). The grammar upper-cases every sub-type token,
                // whereas shape names are stored mixed-case (e.g. "Pukel-man"), so the
                // registry lookup is deliberately case-insensitive. This therefore
                // requires the shapes to already be loaded when effects are assembled.
                PlayerShape sp = GameConstants.lookupPlayerShape(value);
                if (sp != null)
                    return new EffectSubTypeWrapper(sp);
                else
                    errors.add("Effect starting at line: " + line + " has " +
                            "an unknown player shape: " + value);
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
            // [C] effect_subtype accepts exactly one string here - "AWAY" - and falls through to
            // its -1 error return for anything else, including "SELF" and "NONE". An absent
            // subtype is not an error: it is the flag left switched off.
            case EST_TELEPORT -> {
                if (value.isEmpty()) return teleport(false);
                if ("AWAY".equals(value)) return teleport(true);
                errors.add("Effect starting at line: " + line + " has " +
                        "an invalid teleport type: " + value);
                return null;
            }
            // The mirror of the case above, and deliberately not interchangeable with it: [C]
            // accepts only "SELF" for EF_TELEPORT_TO. monster_spell.txt is the sole data file in
            // the game that supplies either of these two subtypes.
            case EST_TELEPORT_TO -> {
                if (value.isEmpty()) return teleportTo(false);
                if ("SELF".equals(value)) return teleportTo(true);
                errors.add("Effect starting at line: " + line + " has " +
                        "an invalid teleport to type: " + value);
                return null;
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