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

package uk.co.jackoftradesltd.backend.parser.grammars;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.effect.*;
import uk.co.jackoftradesltd.middle.enums.*;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.monsters.Summon;
import uk.co.jackoftradesltd.middle.monsters.enums.MonTimed;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static uk.co.jackoftradesltd.middle.effect.EffectSubTypeWrapper.teleport;
import static uk.co.jackoftradesltd.middle.effect.EffectSubTypeWrapper.teleportTo;

/**
 * Resolves the raw {@link EffectParseRecord}s captured from an {@code effect:} block into
 * domain {@link Effect}s. Effects are shared across many data files (objects, curses, shapes,
 * activations, …), so this assembler is a static helper the owning assemblers delegate to
 * rather than a stand-alone {@code Assembler} keyed to one grammar.
 *
 * <p>It ports the reading done by {@code grab_effect_data} in {@code init.c}, which every C
 * {@code parse_*_effect} handler calls. C matches the effect name against its effect list
 * with {@code grab_name} ({@code datafile.c}), case-exactly, with {@code NONE} and {@code MAX}
 * in the list; reads the optional second token through {@code effect_subtype}
 * ({@code effects.c}); and reads the optional radius and other parameter as plain integers. The
 * port maps the name to an {@link EffectEnum}, resolves the second token through
 * {@link #getWrapperSubType} into whichever payload that effect expects (a projection, timed
 * effect, summon, shape, stat, …) wrapped in an {@link EffectSubTypeWrapper}, and parses the
 * numbers with {@code ParseUtils}. The {@code effect-yx:} pair and the {@code dice:} and
 * {@code expr:} lines are read by the sibling C handlers and are carried across here too.
 *
 * <p>Every failure stops the load with a {@link DataFileParseException}, as a C parse error does.
 * The old soft-error channel survives only as an unused {@code errors} parameter.
 *
 * <p>Two things C does at load are deliberately not done here. Dice and expressions are kept
 * as raw text and not evaluated - see the note in {@link #assembleOne} - and the load-time
 * refusal of a bad {@code dice:} or {@code expr:} string (C's {@code dice_parse_string},
 * {@code expression_add_operations_string} and {@code dice_bind_expression}) waits for the roll
 * engine, which will be called from this class when it exists (accepted 261025). A sub-type
 * written as a number, which C's {@code effect_subtype} reads with {@code strtol} first, is also
 * not accepted (accepted deviation 261025, "for the time being"): the lexer's sub-type token has
 * no digits, so the {@code onlyDigits} branches in {@link #getWrapperSubType} cannot be reached
 * from a data file.
 *
 * <p>The radius, other parameter, {@code y} and {@code x} are read by {@code ParseUtils} as
 * {@code strtol} base 0 reads them, so {@code 010} is 8 and {@code 0x10} is 16. A number outside
 * the {@code int} range is clamped by {@code ParseUtils} where C wraps it into its 32-bit field
 * (accepted deviation 261027, "An integer too big for its C type is clamped, where C wraps it" in
 * {@code docs/Accepted_Deviations.md}).
 *
 * <p>Shapes and summons are resolved against {@link PlayerRegistry} and {@link MonsterRegistry}
 * as the effect is assembled, so both must already be loaded; an unloaded registry, or an empty
 * summon list when a summon effect gives no sub-type, throws {@link IllegalStateException} and
 * not a {@link DataFileParseException}. The timed-effect, projection, stat and monster
 * timed-effect names come from Java enums, not from a loaded file, so C's need to load
 * {@code player_timed.txt} first does not arise.
 *
 * <p>Class EffectAssembler coded before 260915, commented in full on 261029 (first on 261026).
 *
 * @author Rowan Crowther
 */
public class EffectAssembler {
    /**
     * Logger for this assembler. Every failure is logged at {@code fatal} just before the
     * {@link DataFileParseException} that stops the load is thrown.
     *
     * <p>Field logger commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(EffectAssembler.class);

    /**
     * The name of the data file being assembled, for the error messages. {@link #assemble} sets it
     * on every call, and the private helpers read it, so it is shared static state: two threads
     * assembling different files at once would overwrite each other's name. Loading is
     * single-threaded.
     *
     * <p>Field filename commented in full on 261029.
     */
    private static String filename;

    /**
     * Assemble every effect in one block into resolved {@link Effect}s, in the order given.
     *
     * <p>A block is only meaningful as a whole, so the first effect that fails to resolve stops
     * the load: {@link #assembleOne} throws a {@link DataFileParseException} naming the file and
     * the line the effect starts on, and this method lets it through. It never returns a partial
     * list and never returns {@code null}, so a caller does not need to test the result.
     *
     * <p>{@code filename} is stored in a static field for the helpers to put in their error
     * messages. {@code errors} is passed down but nothing appends to it any more; it is kept so
     * the callers' signatures did not have to change.
     *
     * <p>Function assemble coded before 260915, commented in full on 261029.
     *
     * @param records  the parsed effect blocks for one owning record
     * @param errors   unused; formerly the soft-error channel
     * @param filename the data file the records came from, for error messages
     * @return the resolved effects in order; empty if {@code records} is empty
     * @throws DataFileParseException if any effect fails to resolve
     * @author Rowan Crowther
     */
    @NotNull
    @CheckReturnValue
    public static List<Effect> assemble(@NotNull List<EffectParseRecord> records,
                                        @NotNull List<String> errors,
                                        @NotNull String filename) {
        EffectAssembler.filename = filename;
        List<Effect> effects = new ArrayList<>();
        for (EffectParseRecord record : records) {
            Effect e = assembleOne(record, errors, record.line());
            effects.add(e);
        }
        return effects;
    }

    /**
     * Resolves one {@link EffectParseRecord} into an {@link Effect}: the type name to an
     * {@link EffectEnum}, the sub-type token to a payload-specific {@link EffectSubTypeWrapper}
     * via {@link #getWrapperSubType}, the radius, other parameter and {@code effect-yx:} pair
     * from text to {@code int}, and the dice expression's bound variables to
     * {@link Expression}s. The dice themselves are left as raw text (see the note before the
     * {@code return}).
     *
     * <p>The effect name is matched case-exactly against {@link EffectEnum} with an {@code EF_}
     * prefix, as {@code grab_name} ({@code datafile.c}) matches with {@code streq}. The sentinels
     * {@code NONE} and {@code MAX} are accepted because C's effect list holds them, and C only
     * refuses them later, when an effect is used ({@code effect_valid}). An absent radius, other
     * parameter, {@code y} or {@code x} is 0, which is what C's zero-filled {@code struct effect}
     * holds when the field is not given. The effect's message is passed through unchanged, and is
     * {@code null} when the block has none, as in C.
     *
     * <p>The checks run in C's order: the name, then the sub-type, then the numbers. C's
     * {@code grab_effect_data} reads the radius and the other parameter, and the sibling
     * {@code effect-yx} handler the {@code y} and {@code x}; a number that is not a number ends the
     * load in both ({@code PARSE_ERROR_NOT_NUMBER}).
     *
     * <p>Function assembleOne coded before 260915, commented in full on 261029.
     *
     * @param record the raw effect record to resolve
     * @param errors unused; formerly the soft-error sink
     * @param line   the record's source line, for error messages
     * @return the resolved effect; never {@code null}
     * @throws DataFileParseException if the effect name is empty or not one of {@link EffectEnum},
     *                                the sub-type does not resolve, or a number or expression is bad
     */
    private static Effect assembleOne(@NotNull EffectParseRecord record,
                                      @NotNull List<String> errors,
                                      int line) {
        if (record.typeInit().isEmpty()) {
            String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                    "an empty element value";
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        EffectEnum effectEnum;
        try {
            effectEnum = EffectEnum.valueOf("EF_" + record.typeInit());
        } catch (IllegalArgumentException e) {
            String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal element value: " + record.typeInit();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        // The sub-type token is resolved for every effect, an empty one included, because [C]
        // effect_subtype (effects.c) is skipped for a missing token and leaves effect->subtype at
        // zero. Each kind in getWrapperSubType turns the empty value into its own index 0: the
        // first projection, the first timed effect, a teleport flag that is off (which the
        // handlers read as "a monster may not cast this at the player"), and so on. So every
        // effect of a kind comes out the same shape whether or not the data file gave it a
        // sub-type.
        EffectSubTypeWrapper wrapper;
        EffectSubTypeEnum kind = effectEnum.getSubType();
        wrapper = getWrapperSubType(kind, record.subTypeWrapper(), errors, line);
        int radius = ParseUtils.parseIntString("Effect", record.radius(), line, filename, "radius");
        int otherParameter = ParseUtils.parseIntString("Effect", record.other(), line, filename, "other parameter");
        int yVal = ParseUtils.parseIntString("Effect", record.yVal(), line, filename, "y-val");
        int xVal = ParseUtils.parseIntString("Effect", record.xVal(), line, filename, "x-val");
        String diceString = record.diceString();
        String expressionChars = record.expressionChars();
        String expressionBases = record.expressionBases();
        String expressionOperations = record.expressionOperations();
        List<Expression> expressions = getExpressions(expressionChars,
                expressionBases, expressionOperations, line, errors);
        String msg = record.effectMessage();

        // The evaluated dice value (the Random field) is intentionally left null here.
        // Dice are not rolled at parse time: an expression such as "$B+1d8" bound to
        // "B:PLAYER_HP:* 4" must be computed against live game state at the moment the
        // effect fires (4 * player HP + 1d8), which is a runtime action outside the scope
        // of file parsing. This mirrors Angband's C, where dice_parse_string only builds a
        // dice_t skeleton at load and dice_roll/dice_evaluate produce the value at fire time.
        // We therefore retain only the lossless raw diceString plus the parsed expressions,
        // to be parsed and evaluated together by the future runtime roll engine.
        return new Effect(effectEnum, null, diceString, yVal, xVal, effectEnum.getSubType(),
                wrapper, radius, otherParameter, expressions, msg);
    }

    /**
     * Splits the {@code ^}-delimited {@code chars}/{@code bases}/{@code operations} strings from
     * an {@link EffectParseRecord} back into their positionally-paired {@link Expression}s,
     * resolving each base-type token against {@code EffectBaseType} with an {@code EFB_} prefix.
     * An empty {@code chars} string is not an error — it means the dice string bound no
     * expressions — and returns an empty list.
     *
     * <p>The base name stands in for C's {@code effect_value_base_by_name} ({@code effects.c}),
     * which matches the name against its seven bases (case-insensitively) and returns a
     * {@code NULL} function for any other name, which C accepts without an error. The port
     * does the same: a name that is not an {@link EffectBaseType}, and the name {@code MAX}
     * itself, give {@code EFB_NONE}. The operation string is kept raw; C's refusal of a bad one
     * waits for the roll engine, as does the check that the character is bound by a {@code $}
     * in the dice string.
     *
     * <p>C matches the base name without regard to case ({@code my_stricmp}), where this method
     * matches it exactly. The two cannot differ from a data file, because the lexer's
     * {@code EXPR_UCASE} passes only upper-case letters and {@code _}.
     *
     * <p>The character is one letter {@code A} to {@code Z}, because the lexer's
     * {@code EXPR_CHAR} is, so the "not one character" refusal cannot be reached from a data
     * file. An operation string containing a {@code ^} would be split in two, since {@code ^} is
     * the delimiter, but no shipped operation holds one.
     *
     * <p>Function getExpressions coded before 260915, commented in full on 261029.
     *
     * @param chars      the {@code ^}-delimited single-character expression codes
     * @param bases      the {@code ^}-delimited base-type names, paired positionally with
     *                   {@code chars}
     * @param operations the {@code ^}-delimited raw operation strings, paired positionally with
     *                   {@code chars}
     * @param line       the owning record's source line, for error messages
     * @param errors     unused; formerly the soft-error sink
     * @return the resolved expressions in order; empty if {@code chars} is empty
     * @throws DataFileParseException if the three strings split into different numbers of parts,
     *                                or a code is not exactly one character
     */
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
            String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                    "differing numbers of chars/bases & operation values.";
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i].length() != 1) {
                String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal expression value: '" + charArray[i] + "'";
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            char ch = charArray[i].charAt(0);
            String baseType = basesArray[i];
            EffectBaseType efb;
            try {
                efb = EffectBaseType.valueOf("EFB_" + baseType);
                if (efb == EffectBaseType.EFB_MAX)
                    efb = EffectBaseType.EFB_NONE;
            } catch (IllegalArgumentException e) {
                efb = EffectBaseType.EFB_NONE;
            }
            String operation = operationsArray[i];

            results.add(new Expression(ch, efb, operation));
        }

        return results;
    }

    /**
     * Resolves an effect's raw sub-type token into the payload-specific {@link EffectSubTypeWrapper}
     * its {@link EffectSubTypeEnum} kind expects. The port of {@code effect_subtype}
     * ({@code effects.c}), with {@code grab_effect_data} ({@code init.c}) supplying the absent
     * case. The kinds, and the C lookup each one stands in for, are:
     * <ul>
     *   <li>{@code EST_PROJ} - {@code proj_name_to_idx}, case-insensitive; accepts {@code MAX},
     *       refuses {@code NONE}.</li>
     *   <li>{@code EST_TMD} - {@code timed_name_to_idx}, case-insensitive; refuses both
     *       sentinels, {@code NONE} and {@code MAX}.</li>
     *   <li>{@code EST_STAT} - {@code stat_name_to_idx}, case-insensitive; accepts {@code MAX},
     *       refuses {@code NONE}.</li>
     *   <li>{@code EST_MON_TMD} - {@code mon_timed_name_to_idx}, case-exact; refuses both
     *       sentinels.</li>
     *   <li>{@code EST_NOURISH}, {@code EST_ENCHANT}, {@code EST_EARTHQUAKE},
     *       {@code EST_GLYPH} - the fixed names C compares with {@code streq}:
     *       {@code INC_BY DEC_BY SET_TO INC_TO}; {@code TOBOTH TOHIT TODAM TOAC};
     *       {@code NONE TARGETED}; {@code WARDING DECOY}. Case-exact.</li>
     *   <li>{@code EST_SUMMON} - {@code summon_name_to_idx}, case-exact, against
     *       {@link MonsterRegistry#lookupSummon}.</li>
     *   <li>{@code EST_SHAPECHANGE} - {@code shape_name_to_idx}, case-exact, against
     *       {@link PlayerRegistry#lookupPlayerShape(String)}. A miss shows the player
     *       {@code "Could not find %s shape!"}, as C does, and then stops the load.</li>
     *   <li>{@code EST_TELEPORT} and {@code EST_TELEPORT_TO} - a flag, set by the one token
     *       {@code AWAY} or {@code SELF} respectively and off when the token is absent.</li>
     *   <li>{@code EST_NONE} - every other effect, which accepts only {@code NONE}.</li>
     * </ul>
     *
     * <p>A token that is not in a kind's list stops the load, as C's {@code -1} return becomes
     * {@code PARSE_ERROR_INVALID_VALUE}. An <em>absent</em> token does not: C skips
     * {@code effect_subtype} and leaves the sub-type at 0, so an empty {@code value} gives the
     * kind's index 0 - {@code ACID}, {@code FAST}, {@code STR}, {@code SLEEP}, {@code INC_BY},
     * the first summon in {@code summon.txt}, {@code NONE} for enchant, earthquake and glyph, and
     * the first shape in {@code shape.txt}. The shape is the one place the port differs in
     * what it holds: C stores the index and looks the shape up when the effect fires, and the port
     * stores the {@link PlayerShape} itself, so an empty shape list stops the load here where C
     * would fail later. The shipped {@code shape.txt} is never empty.
     *
     * <p>C also reads a sub-type written as a number; the port does not (accepted deviation
     * 261025). The {@code onlyDigits} branches below are left over from that and cannot be
     * reached, because the lexer's sub-type token has no digits.
     *
     * <p>The case-insensitive kinds fold with {@code toUpperCase(Locale.ROOT)}, which also folds a
     * few non-ASCII letters that C's ASCII-only {@code my_stricmp} does not (accepted deviation
     * 261023); the lexer's sub-type token holds ASCII letters only, so a data file cannot reach it.
     *
     * <p>Function getWrapperSubType coded before 260915, commented in full on 261029.
     *
     * @param type   the effect's sub-type kind, which selects how {@code value} is resolved
     * @param value  the raw sub-type token from the data file, or empty if the line gave none
     * @param errors unused; formerly the soft-error sink
     * @param line   the owning record's source line, for error messages
     * @return the resolved wrapper; never {@code null}
     * @throws DataFileParseException if {@code value} is not in the list for {@code type}, or it
     *                                is empty and the shape list is empty
     * @throws IllegalStateException  if a summon or shape is resolved before its registry is
     *                                loaded, or {@code value} is empty and the summon list is empty
     */
    static EffectSubTypeWrapper getWrapperSubType(EffectSubTypeEnum type, String value,
                                                          List<String> errors, int line) {
        switch (type) {
            case EST_PROJ -> {
                ProjectionEnum pe;
                if (value.isEmpty()) {
                    pe = ProjectionEnum.PROJ_ACID;
                    return new EffectSubTypeWrapper(pe);
                }
                if (ParseUtils.onlyDigits(value)) {
                    int val = ParseUtils.parseIntString("Effect", value, line, filename, "projection subtype");
                    // NONE sentinel exists - don't allow it to be accessed
                    val++;
                    if (val < 1 || val > ProjectionEnum.values().length) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid projection sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    pe = ProjectionEnum.values()[val];
                    return new EffectSubTypeWrapper(pe);
                }
                try {
                    pe = ProjectionEnum.valueOf("PROJ_" + value.toUpperCase(Locale.ROOT));
                    if (pe == ProjectionEnum.PROJ_NONE) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel projection sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    return new EffectSubTypeWrapper(pe);
                } catch (IllegalArgumentException e) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal projection value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);

                }
            }
            case EST_TMD -> {
                TimedEffect te;
                if (value.isEmpty()) {
                    te = TimedEffect.TMD_FAST;
                    return new EffectSubTypeWrapper(te);
                }
                if (ParseUtils.onlyDigits(value)) {
                    int val = ParseUtils.parseIntString("Effect", value, line, filename, "timed subtype");
                    // NONE sentinel exists - don't allow it to be accessed
                    val++;
                    if (val < 1 || val > TimedEffect.values().length) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid timed sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    te = TimedEffect.values()[val];
                    return new EffectSubTypeWrapper(te);
                }
                try {
                    te = TimedEffect.valueOf("TMD_" + value.toUpperCase(Locale.ROOT));
                    if (te == TimedEffect.TMD_NONE || te == TimedEffect.TMD_MAX) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel timed sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    return new EffectSubTypeWrapper(te);
                } catch (IllegalArgumentException e) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal timed value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            case EST_NOURISH -> {
                EffectNourish ne;
                if (value.isEmpty()) {
                    ne = EffectNourish.EN_INC_BY;
                    return new EffectSubTypeWrapper(ne);
                }
                if (ParseUtils.onlyDigits(value)) {
                    int val = ParseUtils.parseIntString("Effect", value, line, filename, "nourish subtype");
                    // NONE sentinel exists - don't allow it to be accessed
                    val++;
                    if (val < 1 || val > EffectNourish.values().length) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid nourish sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    ne = EffectNourish.values()[val];
                    return new EffectSubTypeWrapper(ne);
                }
                try {
                    ne = EffectNourish.valueOf("EN_" + value);
                    if (ne == EffectNourish.EN_NONE) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel nourish sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    return new EffectSubTypeWrapper(ne);
                } catch (IllegalArgumentException e) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal nourish value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            case EST_MON_TMD -> {
                MonTimed mt;
                if (value.isEmpty()) {
                    mt = MonTimed.MON_TMD_SLEEP;
                    return new EffectSubTypeWrapper(mt);
                }
                if (ParseUtils.onlyDigits(value)) {
                    int val = ParseUtils.parseIntString("Effect", value, line, filename, "mon timed subtype");
                    // NONE & MAX sentinels exists - don't allow either to be accessed
                    val++;
                    if (val < 1 || val > MonTimed.values().length - 1) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid mon timed sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    mt = MonTimed.values()[val];
                    return new EffectSubTypeWrapper(mt);
                }
                try {
                    mt = MonTimed.valueOf("MON_TMD_" + value);
                    if (mt == MonTimed.MON_TMD_NONE || mt == MonTimed.MON_TMD_MAX) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel mon timed sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    return new EffectSubTypeWrapper(mt);
                } catch (IllegalArgumentException e) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal mon timed value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            case EST_SHAPECHANGE -> {
                // Unlike the enum-backed sub-types above, a shapechange target is a
                // shape *name* resolved against the loaded shape registry (C's
                // shape_name_to_idx). The grammar passes the sub-type token through as
                // written, and the lookup matches it case-exactly as C's streq does, so
                // the data must spell the shape as shape.txt does (e.g. "Pukel-man").
                // This requires the shapes to already be loaded when effects are assembled.
                PlayerShape sp;
                if (value.isEmpty()) {
                    sp = PlayerRegistry.lookupPlayerShape(0);
                    if (sp == null) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an empty shape name value and the global player shapes array is empty";
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    } else 
                        return new EffectSubTypeWrapper(sp);
                }
                sp = PlayerRegistry.lookupPlayerShape(value);
                if (sp != null)
                    return new EffectSubTypeWrapper(sp);
                else {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " either has " +
                            "an illegal shape name value: " + value + " or the global player shapes array is empty";
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            case EST_SUMMON -> {
                Summon sum = MonsterRegistry.lookupSummon(value);
                if (sum != null)
                    return new EffectSubTypeWrapper(sum);
                else if (value.isEmpty()) {
                    sum = MonsterRegistry.lookupSummon(0);
                    return new EffectSubTypeWrapper(sum);
                } else {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal summon value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            case EST_STAT -> {
                Stats stat;
                if (value.isEmpty()) {
                    stat = Stats.STAT_STR;
                    return new EffectSubTypeWrapper(stat);
                }
                if (ParseUtils.onlyDigits(value)) {
                    int val = ParseUtils.parseIntString("Effect", value, line, filename, "mon timed subtype");
                    // NONE & MAX sentinels exists - don't allow either to be accessed
                    val++;
                    if (val < 1 || val > Stats.values().length - 1) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid mon timed sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    stat = Stats.values()[val];
                    return new EffectSubTypeWrapper(stat);
                }
                try {
                    stat = Stats.valueOf("STAT_" + value.toUpperCase(Locale.ROOT));
                    if (stat == Stats.STAT_NONE) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel stat sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    return new EffectSubTypeWrapper(stat);
                } catch (IllegalArgumentException e) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal stat value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            case EST_ENCHANT -> {
                EffectEnchant ee;
                if (value.isEmpty()) {
                    ee = EffectEnchant.EE_NONE;
                    return new EffectSubTypeWrapper(ee);
                }
                if (ParseUtils.onlyDigits(value)) {
                    int val = ParseUtils.parseIntString("Effect", value, line, filename, "enchant subtype");
                    // NONE sentinels exists - don't allow it to be accessed
                    val++;
                    if (val < 1 || val > EffectEnchant.values().length) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid enchant sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    ee = EffectEnchant.values()[val];
                    return new EffectSubTypeWrapper(ee);
                }
                try {
                    ee = EffectEnchant.valueOf("EE_" + value);
                    if (ee == EffectEnchant.EE_NONE) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel enchant sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    return new EffectSubTypeWrapper(ee);
                } catch (IllegalArgumentException e) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal enchant value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            case EST_EARTHQUAKE -> {
                Earthquake eq;
                if (value.isEmpty()) {
                    eq = Earthquake.QUAKE_NONE;
                    return new EffectSubTypeWrapper(eq);
                }
                if (ParseUtils.onlyDigits(value)) {
                    int val = ParseUtils.parseIntString("Effect", value, line, filename, "earthquake subtype");
                    // No sentinels exists
                    if (val < 0 || val > Earthquake.values().length) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid earthquake sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    eq = Earthquake.values()[val];
                    return new EffectSubTypeWrapper(eq);
                }
                try {
                    eq = Earthquake.valueOf("QUAKE_" + value);
                    return new EffectSubTypeWrapper(eq);
                } catch (IllegalArgumentException e) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal earthquake value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            case EST_GLYPH -> {
                GlyphType glyph;
                if (value.isEmpty()) {
                    return new EffectSubTypeWrapper(GlyphType.GLYPH_NONE);
                }
                if (ParseUtils.onlyDigits(value)) {
                    int val = ParseUtils.parseIntString("Effect", value, line, filename, "glyph subtype");
                    // NONE sentinel exists
                    val++;
                    if (val < 1 || val > Earthquake.values().length) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "an invalid glyph sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    glyph = GlyphType.values()[val];
                    return new EffectSubTypeWrapper(glyph);
                }
                try {
                    glyph = GlyphType.valueOf("GLYPH_" + value);
                    if (glyph == GlyphType.GLYPH_NONE) {
                        String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                                "a sentinel glyph sub-type value: " + value;
                        logger.fatal(msg);
                        throw new DataFileParseException(msg);
                    }
                    return new EffectSubTypeWrapper(glyph);
                } catch (IllegalArgumentException e) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal glyph value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
            }
            // [C] effect_subtype accepts exactly one string here - "AWAY" - and falls through to
            // its -1 error return for anything else, including "SELF" and "NONE". An absent
            // subtype is not an error: it is the flag left switched off.
            case EST_TELEPORT -> {
                if (value.isEmpty()) return teleport(false);
                if ("AWAY".equals(value)) return teleport(true);
                String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal teleport value: " + value;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            // The mirror of the case above, and deliberately not interchangeable with it: [C]
            // accepts only "SELF" for EF_TELEPORT_TO. monster_spell.txt is the sole data file in
            // the game that supplies either of these two subtypes.
            case EST_TELEPORT_TO -> {
                if (value.isEmpty()) return teleportTo(false);
                if ("SELF".equals(value)) return teleportTo(true);
                String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal teleport to value: " + value;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            case EST_NONE -> {
                if (!value.equals("NONE") && !value.isEmpty()) {
                    String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal EST_NONE sub-type value: " + value;
                    logger.fatal(msg);
                    throw new DataFileParseException(msg);
                }
                return new EffectSubTypeWrapper(EffectSubTypeEnum.EST_NONE);
            }
            default -> {
                String msg = "Effect starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal type value: " + type.name();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }
}