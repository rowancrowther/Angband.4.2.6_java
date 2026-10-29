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

package uk.co.jackoftradesltd.backend.parser.playertimed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectAssembler;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.Slay;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.TimedFailure;
import uk.co.jackoftradesltd.middle.player.TimedGrade;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Resolves the raw {@link PlayerTimedParseRecord}s captured from {@code player_timed.txt} into
 * domain {@link PlayerTimedEffect}s — the assembler half of the reader pipeline, porting the
 * interpretation the C {@code parse_player_timed_*} handlers do in {@code player-timed.c}. Each
 * still-textual field is resolved to its domain type: the name and {@code fail:}/{@code resist:}
 * tokens to their enums, {@code grade:} colours/maxima to {@link TimedGrade}s, brand/slay codes to
 * registry entries, and the begin/end effect blocks (via {@link EffectAssembler}) to {@link Effect}s.
 *
 * <p><b>Fatal errors:</b> a record that fails any resolution ends the load. The assembler logs the
 * problem at fatal level and throws a {@link DataFileParseException} naming the record's line and
 * the file, as C's parser stops on the first error. The {@code errors} list is not used for this; it
 * is only handed on to {@link EffectAssembler}.
 *
 * <p><b>A repeated {@code name:}:</b> C's {@code parse_player_timed_name} only points at the existing
 * {@code timed_effects[]} slot, so a second record with the same name carries on the first. Here the
 * later record is merged into the effect already built. Text lines ({@code desc:}, {@code on-end:},
 * {@code on-increase:}, {@code on-decrease:}) are appended with no separator, grades carry on the
 * numbering and are checked against every earlier grade, begin and end effects are appended, and
 * {@code flags:} are ORed in. {@code msgt:}, {@code resist:}, {@code brand:}, {@code slay:},
 * {@code flag-synonym:} and {@code lower-bound:} replace the earlier value only when the later record
 * gives one. The merged effect is moved to the end of the result list, so the list is ordered by each
 * name's last record. C indexes its array by timed effect, so the order there never depends on the
 * file.
 *
 * <p><b>Fail order:</b> C links each {@code fail:} line in at the head of the list, so
 * {@code player_inc_check} meets the last line first, and a later record's lines go in front of an
 * earlier record's. The list built here is in that same order, and {@code PlayerTimed.incCheck} walks
 * it as stored. The grammar hands each record's lines over already reversed, so the assembler adds
 * them as they arrive.
 *
 * <p><b>Defaults:</b> every effect's grade list opens with the implicit "off" grade that C gets from
 * the zeroed head node in {@code parse_player_timed_grade}, even when the record has no
 * {@code grade:} line. A text line the file never gives is {@code null}, as in C, not an empty string.
 * The message type defaults to {@link MessageType#MSG_GENERIC}.
 *
 * <p><b>Deviations from C:</b> the names {@code NONE} and {@code MAX} are refused, where C's name
 * list ends in {@code MAX} and so accepts it and then indexes past the array. The {@code PY_FOOD_*}
 * thresholds that C sets while parsing the {@code FOOD} grades are set from the finished effect in
 * {@code PlayerRegistry.setPlayerTimedEffects}, not here. The {@code effect-yx}, {@code effect-dice},
 * {@code effect-expr} and {@code effect-msg} directives are not part of the record this assembler
 * receives.
 *
 * <p><b>Load-order dependency:</b> the {@code ATT_*} statuses resolve a brand/slay code through
 * {@link ObjectRegistry#lookupBrandCode}/{@link ObjectRegistry#lookupSlay}, so {@code brand.txt} and
 * {@code slay.txt} must already be loaded when this runs.
 *
 * <p>Class PlayerTimedAssembler coded before 260915, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
public class PlayerTimedAssembler implements Assembler<PlayerTimedParseRecord, List<PlayerTimedEffect>> {
    /**
     * Receives the fatal log line written just before each {@link DataFileParseException} is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261029.
     */
    private static final Logger logger = LogManager.getLogger(PlayerTimedAssembler.class);

    /**
     * Resolve every parsed record into a {@link PlayerTimedEffect}, merging records that repeat a
     * name and stopping the load at the first value that does not resolve.
     *
     * <p>Stands in for the {@code parse_player_timed_*} handlers of {@code player-timed.c}. For each
     * record in file order it resolves the name, then the optional parts. A grade's colour goes
     * through {@link ColourEnum#fromCode(String)} and an unknown colour becomes white as in C; its
     * maximum must be from 1 to 32767 (divided by the food value for {@code FOOD}) and strictly above every earlier
     * grade, and the stored maximum is scaled up by the food value. A status or up message of one
     * character is a dummy and becomes {@code null}; the down message is kept as written. A
     * {@code fail:} code of 1 to 5 selects an object flag, a resist, a vulnerability, a player flag or
     * a timed effect. {@code lower-bound:} must be from 0 to 32767, and {@code flags:} may only give
     * {@code NONSTACKING}.
     *
     * <p>The {@code resist:} element and the {@code msgt:} name are matched without regard to case, as
     * C's {@code proj_name_to_idx} and {@code message_lookup_by_name} do; the other names are
     * case-sensitive. A name is refused if it is unknown, if it is a {@code NONE} or {@code MAX}
     * sentinel that C's list does not hold (a {@code fail:5} naming {@code MAX} gets through, as it does
     * in C's list), or if a number does not parse. The messages read "Player timed starting at line: N
     * of file: F has ... value: X".
     *
     * <p>See the class comment for how a repeated name, the fail order and the defaults behave.
     *
     * <p>Function assemble coded before 260915, commented in full on 261029.
     *
     * @param records  the parsed timed-effect records, in file order
     * @param errors   passed on to {@link EffectAssembler} for the begin and end effects; nothing here
     *                 appends to it
     * @param filename the data file's name, for the error messages
     * @return the resolved effects, one per distinct name, ordered by each name's last record
     * @throws DataFileParseException on the first value that does not resolve
     */
    @Override
    public List<PlayerTimedEffect> assemble(@NotNull List<PlayerTimedParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<PlayerTimedEffect> effects = new ArrayList<>();
        TimedEffect previousEffect = TimedEffect.TMD_NONE;

        for (PlayerTimedParseRecord record : records) {
            int line = record.line();
            String name = record.name();
            TimedEffect timedEffect = TimedEffect.TMD_NONE;
            if (!name.isEmpty()) {
                try {
                    timedEffect = TimedEffect.valueOf("TMD_" + name);
                    if (timedEffect == TimedEffect.TMD_NONE || timedEffect == TimedEffect.TMD_MAX) {
                        String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel name value: " + record.name();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal name value: " + record.name();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            
            TimedEffect prevName = timedEffect;
            PlayerTimedEffect previous = effects.stream().filter(p -> p.getName() == prevName)
                    .findFirst().orElse(null);
            
            boolean updatePrevious = previous != null;
            
            String description;
            if (updatePrevious)
                description = updatePreviousStringAppend(previous.getDescription(), record.description());
            else
                description = record.description().isEmpty() ? null : record.description();
            List<TimedGrade> grades;
            if (updatePrevious)
                grades = previous.getGrade();
            else 
                grades = new ArrayList<>();
            if (!updatePrevious) {
                // Put in the C header TimedGrade
                TimedGrade head = new TimedGrade(0, ColourEnum.COLOUR_DARK, 0, null, null, null);
                grades.add(head);
            }
            int count = 0;
            int previousValue = 0;
            
            if (updatePrevious) {
                count = previous.getGrade().size() - 1;
                int scale = (timedEffect == TimedEffect.TMD_FOOD) ? GameConstants.getPlayerFoodValue() : 1;
                previousValue = previous.getGrade().getLast().max() / scale;
            }
            for (PlayerTimedParseRecord.PlayerTimedGradeParseRecord g : record.grades()) {
                count++;
                String colour = g.colour();
                int max;
                String status = g.status().length() > 1 ? g.status() : null;
                String msgDown = g.msgDown();
                String msgUp = g.msgUp().length() > 1 ? g.msgUp() : null;
                ColourEnum colourType = ColourEnum.fromCode(colour);
                if (colourType == null) {
                    colourType = ColourEnum.COLOUR_WHITE;
                }
                max = ParseUtils.parseIntString("Player timed", g.max(), line, filename, "maximum");
                int scale = (timedEffect == TimedEffect.TMD_FOOD) ? GameConstants.getPlayerFoodValue() : 1;
                int upperBound = Short.MAX_VALUE / scale;
                if (max <= 0 || max > Short.MAX_VALUE / scale) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal out of range maximum value: " + g.max() + " " +
                            "must be between 1 and " + upperBound;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                TimedGrade previousGrade = grades.isEmpty() ? null : grades.getLast();
                if (previousGrade != null && max <= previousValue) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal value: " + max + " for " + record.name() + ". Values must be strictly increasing" +
                            " for effects of the same type.";
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                previousValue = max;
                max *= scale;
                grades.add(new TimedGrade(count, colourType, max, status, msgUp, msgDown));
            }
            String onEnd;
            if (updatePrevious) 
                onEnd = updatePreviousStringAppend(previous.getOnEnd(), record.onEnd());
            else
                onEnd = record.onEnd().isEmpty() ? null : record.onEnd();
            String onIncrease;
            if (updatePrevious)
                onIncrease = updatePreviousStringAppend(previous.getOnIncrease(), record.onIncrease());
            else
                onIncrease = record.onIncrease().isEmpty() ? null : record.onIncrease();
            String onDecrease;
            if (updatePrevious)
                onDecrease = updatePreviousStringAppend(previous.getOnDecrease(), record.onDecrease());
            else
                onDecrease = record.onDecrease().isEmpty() ? null : record.onDecrease();
            String msg = record.msgT();
            MessageType messageType = updatePrevious ? previous.getMsgT() : MessageType.MSG_GENERIC;
            if (!msg.isEmpty()) {
                messageType = MessageType.lookupByName(msg);
                if (messageType == null) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal message type value: " + record.msgT();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            List<TimedFailure> fails = new ArrayList<>();
            for (PlayerTimedParseRecord.FailureParseRecord fail : record.fail()) {
                String failType = fail.type();
                String failValue = fail.value();
                try {
                    int failInt = ParseUtils.parseIntString("Player timed", failType, line, filename, "fail type");
                    switch (failInt) {
                        case 1:
                            // object flag
                            ObjectFlag objectFlag = ObjectFlag.valueOf("OF_" + failValue);
                            if (objectFlag == ObjectFlag.OF_NONE || objectFlag == ObjectFlag.OF_MAX) {
                                String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                        "an illegal object flag sentinel value: " + failValue;
                                logger.fatal(message);
                                throw new DataFileParseException(message);
                            }
                            fails.add(new TimedFailure(objectFlag, TimedEffectReasonType.TYPE_OBJECT_FLAG));
                            break;
                        case 2:
                            // resist element
                            ElementEnum elementEnum_2 = ElementEnum.valueOf("ELEM_" + failValue);
                            if (elementEnum_2 == ElementEnum.ELEM_NONE || elementEnum_2 == ElementEnum.ELEM_MAX) {
                                String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                        "an illegal resist element sentinel value: " + failValue;
                                logger.fatal(message);
                                throw new DataFileParseException(message);
                            }
                            fails.add(new TimedFailure(elementEnum_2, TimedEffectReasonType.TYPE_RESIST));
                            break;
                        case 3:
                            // vulnerable element
                            ElementEnum elementEnum_3 = ElementEnum.valueOf("ELEM_" + failValue);
                            if (elementEnum_3 == ElementEnum.ELEM_NONE || elementEnum_3 == ElementEnum.ELEM_MAX) {
                                String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                        "an illegal vulnerable element sentinel value: " + failValue;
                                logger.fatal(message);
                                throw new DataFileParseException(message);
                            }
                            fails.add(new TimedFailure(elementEnum_3, TimedEffectReasonType.TYPE_VULN));
                            break;
                        case 4:
                            // Player flag
                            PlayerFlag playerFlag = PlayerFlag.valueOf("PF_" + failValue);
                            if (playerFlag == PlayerFlag.PF_NONE) {
                                String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                        "an illegal player flag sentinel value: " + failValue;
                                logger.fatal(message);
                                throw new DataFileParseException(message);
                            }
                            fails.add(new TimedFailure(playerFlag, TimedEffectReasonType.TYPE_PLAYER_FLAG));
                            break;
                        case 5:
                            // Timed flag
                            TimedEffect effect = TimedEffect.valueOf("TMD_" + failValue);
                            if (effect == TimedEffect.TMD_NONE) {
                                String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                        "an illegal timed effect sentinel value: " + failValue;
                                logger.fatal(message);
                                throw new DataFileParseException(message);
                            }
                            fails.add(new TimedFailure(effect, TimedEffectReasonType.TYPE_TIMED_EFFECT));
                            break;
                        default:
                            String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                    "an illegal fail type value: " + failType;
                            logger.fatal(message);
                            throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal fail value: " + failValue;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            if (updatePrevious)
                fails.addAll(previous.getFail());
            List<Effect> begin;
            List<Effect> onBeginEffects;
            if (updatePrevious)
                onBeginEffects = previous.getOnBeginEffect();
            else
                onBeginEffects = new ArrayList<>();
            if (record.onBeginEffect() != null && !record.onBeginEffect().typeInit().isEmpty()) {
                begin = EffectAssembler.assemble(List.of(record.onBeginEffect()), errors, filename);
                onBeginEffects.addAll(begin);
            }
            List<Effect> end;
            List<Effect> onEndEffects;
            if (updatePrevious)
                onEndEffects = previous.getOnEndEffect();
            else
                onEndEffects = new ArrayList<>();
            if (record.onEndEffect() != null && !record.onEndEffect().typeInit().isEmpty()) {
                end = EffectAssembler.assemble(List.of(record.onEndEffect()), errors, filename);
                onEndEffects.addAll(end);
            }
            ElementEnum resist = updatePrevious ? previous.getTempResist() : ElementEnum.ELEM_NONE;
            if (!record.resist().isEmpty()) {
                try {
                    resist = ElementEnum.valueOf("ELEM_" + record.resist().toUpperCase(Locale.ROOT));
                    if (resist == ElementEnum.ELEM_NONE || resist == ElementEnum.ELEM_MAX) {
                        String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal resist sentinel value: " + record.resist();
                        logger.fatal(message);
                        throw new DataFileParseException(message);    
                    }
                } catch (IllegalArgumentException e) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal resist value: " + record.resist();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            String brand = record.brand();
            Brand b = updatePrevious ? previous.getTempBrand() : null;
            if (!brand.isEmpty()) {
                b = ObjectRegistry.lookupBrandCode(brand);
                if (b == null) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal brand value: " + record.brand();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            Slay s = updatePrevious ? previous.getTempSlay() : null;
            String slay = record.slay();
            if (!slay.isEmpty()) {
                s = ObjectRegistry.lookupSlay(slay);
                if (s == null) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal slay value: " + record.slay();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            int exactVal = ParseUtils.parseIntString("Player timed", record.flagSynonymValue(), line, filename, "exact synonym value");
            boolean exactlySyn = updatePrevious ? previous.isoFlagExactlySyn() : exactVal != 0;
            ObjectFlag synFlag = updatePrevious ? previous.getoFlagDup() : ObjectFlag.OF_NONE;
            if (!record.flagSynonymFlag().isEmpty()) {
                exactlySyn = exactVal != 0;
                try {
                    synFlag = ObjectFlag.valueOf("OF_" + record.flagSynonymFlag());
                    if (synFlag == ObjectFlag.OF_NONE || synFlag == ObjectFlag.OF_MAX) {
                        String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal object flag synonym sentinel value: " + record.flagSynonymFlag();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal object flag synonym value: " + record.flagSynonymFlag();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            int prevLowerBound = updatePrevious ? previous.getLowerBound() : 0;
            int lowerBound = ParseUtils.parseIntString("Player timed", record.lowerBound(), line, filename, "lower bound");
            if (record.lowerBound().isEmpty()) lowerBound = prevLowerBound;
            if (lowerBound < 0 || lowerBound > Short.MAX_VALUE) {
                String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                        "an out of bounds lower bound value: " + record.lowerBound();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            boolean nonStacking = false;
            if (updatePrevious) {
                if (previous.isNonStacking())
                    nonStacking = true;
            }
            // Only currently understands NONSTACKING flag
            for (String flag : record.flags()) {
                if (flag.equals("NONSTACKING")) {
                    nonStacking = true;
                } else {
                    String message = "Player timed starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal flag value: " + flag;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }

            if (updatePrevious)
                effects.remove(previous);
            
            effects.add(new PlayerTimedEffect(timedEffect, description, onEnd,
                    onIncrease, onDecrease, messageType, fails, grades,
                    onBeginEffects, onEndEffects, nonStacking, lowerBound,
                    synFlag, exactlySyn, resist, b, s));
        }

        return effects;
    }
    
    /**
     * Join the text an earlier record gave with the text a later record for the same name gives.
     *
     * <p>The stand-in for C's {@code string_append} on {@code desc}, {@code on_end},
     * {@code on_increase} and {@code on_decrease}: the later text follows the earlier with no
     * separator. If neither side has any text the answer is {@code null}, as C's pointer is still
     * {@code NULL}, and an empty string counts as no text.
     *
     * <p>Function updatePreviousStringAppend coded before 260915, commented in full on 261029.
     *
     * @param previousString the text built so far, or {@code null} or empty if there is none
     * @param currentString  the later record's text, or {@code null} or empty if it gave none
     * @return the joined text, the one side that has text, or {@code null} if neither does
     */
    private String updatePreviousStringAppend(String previousString, String currentString) {
        if ((previousString == null || previousString.isEmpty()) && (currentString == null || currentString.isEmpty())) 
            return null;
        
        if (currentString == null || currentString.isEmpty())
            return previousString;
        
        if (previousString == null || previousString.isEmpty())
            return currentString;
        
        return previousString + currentString;
    }
}