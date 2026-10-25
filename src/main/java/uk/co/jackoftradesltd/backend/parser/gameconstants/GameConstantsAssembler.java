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

package uk.co.jackoftradesltd.backend.parser.gameconstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.data.*;

import java.util.List;

/**
 * Turns the {@link GameConstantsParseRecord}s read from {@code lib/gamedata/constants.txt} into one
 * {@link GameConstantsData}, through a {@link GameConstantsData.GameConstantsBuilder}. This is the
 * Java counterpart of the seventeen {@code parse_constants_*()} handlers that
 * {@code init_parse_constants()} registers in {@code init.c}, plus the critical-level check in
 * {@code finish_parse_constants()}. The grammar has already split each line at its colons, so what is
 * left is what C's {@code parser_parse()} and the handlers do next: count the fields, read the
 * numbers, look up the message name and pick the setter by label.
 *
 * <p>Behaviour that matches C:
 * <ul>
 *   <li>A line with fewer fields than its directive registers is refused ({@code MISSING_FIELD}).
 *   Fields after the registered ones are ignored, because C's parser reads only the fields it was
 *   given. The one exception is the message of a critical-level line, a {@code str} that takes the
 *   rest of the line, colons included.</li>
 *   <li>Numbers are read through {@link ParseUtils#parseIntString}, which follows {@code strtol} with
 *   base 0, so {@code 010} is 8.</li>
 *   <li>The nine plain categories ({@code level-max}, {@code mon-gen}, {@code mon-play},
 *   {@code dun-gen}, {@code world}, {@code carry-cap}, {@code store}, {@code obj-make} and
 *   {@code player}) refuse a negative value ({@code INVALID_VALUE}). The four critical categories and
 *   the {@code cutoff}, {@code mult} and {@code add} of the standard level lines have no such check in
 *   C, and {@code melee-critical:chance-offset:-60} and the {@code -1} cutoff on the last level row
 *   in the shipped file depend on that.</li>
 *   <li>The {@code chance} and {@code dice} of the O-combat level lines are {@code uint} in C, so a
 *   leading {@code -} is refused even on {@code -0}, and a {@code chance} of 0 is refused.</li>
 *   <li>The message of a level line goes through {@code message_lookup_by_name()}, here
 *   {@link MessageType#lookupByName}. It accepts a name or a number, and refuses an unknown name and
 *   {@code NONE}, which C has no message for.</li>
 *   <li>A label set twice keeps the later value, as in C.</li>
 * </ul>
 *
 * <p>Differences from C worth knowing:
 * <ul>
 *   <li>A bad line throws a {@link DataFileParseException} at once, where C returns a
 *   {@code PARSE_ERROR_*} and then stops the load. Only two problems are reported through the
 *   {@code errors} list instead: an O-combat level row with a {@code chance} of 0, and critical
 *   cutoffs that do not strictly increase. {@code build} returns {@code null} when that list is not
 *   empty.</li>
 *   <li>Integers are clamped to the range of an {@code int} by {@link ParseUtils}, where C keeps the
 *   low bits of the {@code strtol} result.</li>
 *   <li>The builder also refuses a {@code -1} cutoff in the middle of the melee or ranged list, a
 *   Java-only rule confirmed on 261002 (see {@link GameConstantsData}).</li>
 *   <li>The file name for the messages is kept in an instance field, so one assembler must not be
 *   used by two threads at once.</li>
 * </ul>
 *
 * <p>Class GameConstantsAssembler coded before 260915, commented in full on 261025.
 *
 * @author Rowan Crowther
 */
public class GameConstantsAssembler implements Assembler<GameConstantsParseRecord, GameConstantsData> {
    /**
     * Receives the message of every {@link DataFileParseException} before it is thrown. C has no
     * counterpart: it reports through the parser error and {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261025.
     */
    private static final Logger logger = LogManager.getLogger(GameConstantsAssembler.class);

    /**
     * The name of the file being assembled, set at the start of each {@link #assemble} call and put
     * in every error message. C has no counterpart: its parser reports the line, and the caller names
     * the file.
     *
     * <p>Field filename coded before 260915, commented in full on 261025.
     */
    private String filename;

    /**
     * Assembles a {@link GameConstantsData} from the parsed lines of {@code constants.txt}. Each record
     * goes to {@link #dispatch}, which stores it in a {@link GameConstantsData.GameConstantsBuilder}
     * or throws. The builder is then asked to check the melee and ranged critical cutoffs, and the
     * result is whatever {@link GameConstantsData.GameConstantsBuilder#build(List)} makes of it.
     *
     * <p>This is all of C's {@code constants_parser} once the file has been read: the
     * {@code parse_constants_*()} handlers run in file order, then {@code finish_parse_constants()}
     * runs {@code check_critical_levels()} on the melee list and then the ranged list, failing with
     * {@code PARSE_ERROR_NON_SEQUENTIAL_RECORDS}. A constant that no line sets is 0, as with the
     * {@code mem_zalloc}ed struct in C.
     *
     * <p>A bad line throws a {@link DataFileParseException} out of {@link #dispatch}, so nothing is
     * returned. Two problems are added to {@code errors} instead, and the remaining lines are still
     * read: an O-combat level row with a {@code chance} of 0, and cutoffs that do not strictly
     * increase. Because {@code build} returns {@code null} whenever {@code errors} is not empty, so
     * does this method, including when the caller passed in a list that already held a message.
     *
     * <p>Function assemble coded before 260915, commented in full on 261025.
     *
     * @param records  The list of records to create the {@link GameConstantsData} from
     * @param errors   The list the soft errors are added to; any entry makes the result {@code null}
     * @param filename The name of the file the records came from, used in the error messages
     * @return the {@link GameConstantsData} containing the data, or {@code null} if {@code errors}
     * is not empty
     */
    @Override
    @Nullable
    @CheckReturnValue
    public GameConstantsData assemble(@NotNull List<GameConstantsParseRecord> records,
                                      @NotNull List<String> errors,
                                      @NotNull String filename) {
        this.filename = filename;
        GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
        for (GameConstantsParseRecord record : records) {
            dispatch(record, b, errors);
        }
        String result = b.checkCriticalLevelDataLists();
        if (!result.isEmpty()) {
            errors.add(result);
        }

        return b.build(errors);
    }

    /**
     * Sends a record to the handler for its category, which is the first token of the line. The
     * seventeen categories are the ones {@code init_parse_constants()} registers in {@code init.c}
     * with {@code parser_reg()}: the nine plain ones, then the melee, ranged, O-melee and O-ranged
     * critical constants, each with its own {@code -level} line.
     *
     * <p>An unknown category throws. C's {@code parser_parse()} finds no hook for it and returns
     * {@code PARSE_ERROR_UNDEFINED_DIRECTIVE}. The match is exact and case-sensitive in both.
     *
     * <p>Function dispatch coded before 260915, commented in full on 261025.
     *
     * @param rec    The record from the constants.txt file
     * @param b      The builder to build the records from
     * @param errors The list of current errors
     */
    private void dispatch(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                          @NotNull List<String> errors) {
        switch (rec.getCategory()) {
            case "level-max" -> levelMax(rec, b, errors);
            case "mon-gen" -> monGen(rec, b, errors);
            case "mon-play" -> monPlay(rec, b, errors);
            case "dun-gen" -> dunGen(rec, b, errors);
            case "world" -> world(rec, b, errors);
            case "carry-cap" -> carryCap(rec, b, errors);
            case "store" -> store(rec, b, errors);
            case "obj-make" -> objGen(rec, b, errors);
            case "player" -> player(rec, b, errors);
            case "melee-critical" -> meleeCritical(rec, b, errors);
            case "melee-critical-level" -> meleeCriticalLevel(rec, b, errors);
            case "ranged-critical" -> rangedCritical(rec, b, errors);
            case "ranged-critical-level" -> rangedCriticalLevel(rec, b, errors);
            case "o-melee-critical" -> oMeleeCritical(rec, b, errors);
            case "o-melee-critical-level" -> oMeleeCriticalLevel(rec, b, errors);
            case "o-ranged-critical" -> oRangedCritical(rec, b, errors);
            case "o-ranged-critical-level" -> oRangedCriticalLevel(rec, b, errors);
            default -> {
                String msg = "Game constant starting at line: " + rec.getLineNumber() + " of file: " + filename + " has " +
                        "an unknown category: " + rec.getCategory();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code o-ranged-critical-level:<chance>:<dice>:<message>} line and appends it to the
     * builder's list of O-combat ranged critical levels. Port of
     * {@code parse_constants_o_ranged_critical_level()} in {@code init.c}, whose directive is
     * {@code uint chance uint dice str msg}.
     *
     * <p>The line is refused, by throwing, if it has fewer than three fields, if the {@code chance}
     * or {@code dice} text starts with {@code -} (even {@code -0}, which C's parser refuses for a
     * {@code uint}; {@link ParseUtils#minusFound} looks at the raw text because the number reader
     * loses the sign of a zero), or if the message is unknown. The message is every field from the
     * third on joined with {@code :}, because C's {@code str} takes the rest of the line, and is
     * looked up with {@link MessageType#lookupByName}. That returns {@code null} for an unknown name
     * and for {@code NONE}, as C returns {@code -1}.
     *
     * <p>A {@code chance} of 0 is not thrown here. The builder's {@code addORangedCriticalLevel}
     * returns a message, which goes into {@code errors}, and the row is not added. C refuses it
     * with {@code INVALID_VALUE} before it looks at the message, so a line with both faults reports
     * the message here. A {@code dice} of 0 is accepted.
     *
     * <p>Function oRangedCriticalLevel coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void oRangedCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                      @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 3) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of o-ranged-critical-level fields. Expected 3 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        Integer chance = coerceInt(f.get(0), line, errors);
        Integer dice = coerceInt(f.get(1), line, errors);
        if (chance == null || dice == null)
            return;

        if (ParseUtils.minusFound(f.get(0)) || ParseUtils.minusFound(f.get(1))) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal chance or dice value: " + rec.getCategory() + ":" + f.get(0) + ":" + f.get(1) +
                    ":" + f.get(2);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        if (chance < 0 || dice < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative o-ranged-critical field value: " + rec.getCategory() + ":" + f.get(0) + ":"
                    + f.get(1) + ":" + f.get(2);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int index = 2; index < f.size(); index++) {
            messageBuilder.append(f.get(index));
            messageBuilder.append(":");
        }

        String messageType = messageBuilder.substring(0, messageBuilder.toString().length() - 1);
        MessageType messageTypeEnum;

        messageTypeEnum = MessageType.lookupByName(messageType);
        if (messageTypeEnum == null) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal message type value: " + f.get(2);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        ORangedCriticalLevelData record = new ORangedCriticalLevelData(chance, dice, messageTypeEnum);
        String result = b.addORangedCriticalLevel(record);
        if (!result.isEmpty()) {
            errors.add(result);
        }
    }

    /**
     * Reads one {@code o-ranged-critical:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_o_ranged_critical()} in {@code init.c} ({@code sym label int value}).
     * Fewer than two fields throws; fields after the value are ignored. An unknown label throws
     * ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>There is no sign check, as in C, so a negative value is stored. The labels and the C fields
     * they set are {@code debuff-toh} ({@code o_r_crit_debuff_toh}),
     * {@code power-launched-toh-scale-numerator} and {@code -denominator}
     * ({@code o_r_crit_power_launched_toh_scl_num} and {@code _den}),
     * {@code power-thrown-toh-scale-numerator} and {@code -denominator}
     * ({@code o_r_crit_power_thrown_toh_scl_num} and {@code _den}),
     * {@code chance-power-scale-numerator} and {@code -denominator}
     * ({@code o_r_crit_chance_power_scl_num} and {@code _den}) and {@code chance-add-denominator}
     * ({@code o_r_crit_chance_add_den}).
     *
     * <p>Function oRangedCritical coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void oRangedCritical(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                 @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of o-ranged-critical fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "debuff-toh" -> {
                b.setORangedCriticalDebuffToh(value);
            }
            case "power-launched-toh-scale-numerator" -> {
                b.setORangedCriticalPowerLaunchedTohScaleNumerator(value);
            }
            case "power-launched-toh-scale-denominator" -> {
                b.setORangedCriticalPowerLaunchedTohScaleDenominator(value);
            }
            case "power-thrown-toh-scale-numerator" -> {
                b.setORangedCriticalPowerThrownTohScaleNumerator(value);
            }
            case "power-thrown-toh-scale-denominator" -> {
                b.setORangedCriticalPowerThrownTohScaleDenominator(value);
            }
            case "chance-power-scale-numerator" -> {
                b.setORangedCriticalChancePowerScaleNumerator(value);
            }
            case "chance-power-scale-denominator" -> {
                b.setORangedCriticalChancePowerScaleDenominator(value);
            }
            case "chance-add-denominator" -> {
                b.setORangedCriticalChanceAddDenominator(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal o-ranged-critical value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code o-melee-critical-level:<chance>:<dice>:<message>} line and appends it to the
     * builder's list of O-combat melee critical levels. Port of
     * {@code parse_constants_o_melee_critical_level()} in {@code init.c}, whose directive is
     * {@code uint chance uint dice str msg}.
     *
     * <p>The line is refused, by throwing, if it has fewer than three fields, if the {@code chance}
     * or {@code dice} text starts with {@code -} (even {@code -0}, which C's parser refuses for a
     * {@code uint}; {@link ParseUtils#minusFound} looks at the raw text because the number reader
     * loses the sign of a zero), or if the message is unknown. The message is every field from the
     * third on joined with {@code :}, because C's {@code str} takes the rest of the line, and is
     * looked up with {@link MessageType#lookupByName}. That returns {@code null} for an unknown name
     * and for {@code NONE}, as C returns {@code -1}.
     *
     * <p>A {@code chance} of 0 is not thrown here. The builder's {@code addOMeleeCriticalLevelData}
     * returns a message, which goes into {@code errors}, and the row is not added. C refuses it
     * with {@code INVALID_VALUE} before it looks at the message, so a line with both faults reports
     * the message here. A {@code dice} of 0 is accepted.
     *
     * <p>Function oMeleeCriticalLevel coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void oMeleeCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                     @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 3) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of o-melee-critical-level fields. Expected 3 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        Integer chance = coerceInt(f.get(0), line, errors);
        Integer dice = coerceInt(f.get(1), line, errors);
        
        if (ParseUtils.minusFound(f.get(0)) || ParseUtils.minusFound(f.get(1))) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal chance or dice value: " + rec.getCategory() + ":" + f.get(0) + ":" + f.get(1) +
                    ":" + f.get(2);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int index = 2; index < f.size(); index++) {
            messageBuilder.append(f.get(index));
            messageBuilder.append(":");
        }

        String messageType = messageBuilder.substring(0, messageBuilder.toString().length() - 1);
        MessageType messageTypeEnum;

        if (chance == null || dice == null) {
            return;
        }

        messageTypeEnum = MessageType.lookupByName(messageType);
        if (messageTypeEnum == null) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal message type value: " + f.get(2);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        OMeleeCriticalLevelData record = new OMeleeCriticalLevelData(chance, dice, messageTypeEnum);
        String result = b.addOMeleeCriticalLevelData(record);
        if (!result.isEmpty()) {
            errors.add(result);
        }
    }

    /**
     * Reads one {@code o-melee-critical:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_o_melee_critical()} in {@code init.c} ({@code sym label int value}).
     * Fewer than two fields throws; fields after the value are ignored. An unknown label throws
     * ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>There is no sign check, as in C, so a negative value is stored. The labels and the C fields
     * they set are {@code debuff-toh} ({@code o_m_crit_debuff_toh}),
     * {@code power-toh-scale-numerator} and {@code -denominator}
     * ({@code o_m_crit_power_toh_scl_num} and {@code _den}),
     * {@code chance-power-scale-numerator} and {@code -denominator}
     * ({@code o_m_crit_chance_power_scl_num} and {@code _den}) and {@code chance-add-denominator}
     * ({@code o_m_crit_chance_add_den}).
     *
     * <p>Function oMeleeCritical coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void oMeleeCritical(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of o-melee-critical fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "debuff-toh" -> {
                b.setOMeleeCriticalDebuffToh(value);
            }
            case "power-toh-scale-numerator" -> {
                b.setOMeleeCriticalPowerTohScaleNumerator(value);
            }
            case "power-toh-scale-denominator" -> {
                b.setOMeleeCriticalPowerTohScaleDenominator(value);
            }
            case "chance-power-scale-numerator" -> {
                b.setOMeleeCriticalChancePowerScaleNumerator(value);
            }
            case "chance-power-scale-denominator" -> {
                b.setOMeleeCriticalChancePowerScaleDenominator(value);
            }
            case "chance-add-denominator" -> {
                b.setOMeleeCriticalChanceAddDenominator(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal o-melee-critical value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code ranged-critical-level:<cutoff>:<mult>:<add>:<message>} line and appends it to
     * the builder's list of standard ranged critical levels. Port of
     * {@code parse_constants_ranged_critical_level()} in {@code init.c}, whose directive is
     * {@code int cutoff int mult int add str msg}.
     *
     * <p>Fewer than four fields throws. The three numbers are signed and have no sign check, as in C,
     * so the {@code -1} cutoff that ends the shipped list loads. The message is every field from the
     * fourth on joined with {@code :}, because C's {@code str} takes the rest of the line, and is
     * looked up with {@link MessageType#lookupByName}. An unknown name, or {@code NONE}, throws, as
     * C's {@code INVALID_MESSAGE} does.
     *
     * <p>The row is added in file order. Whether the cutoffs strictly increase is checked once every
     * line is in, by the builder's {@code checkCriticalLevelDataLists}, which {@link #assemble} calls.
     *
     * <p>Function rangedCriticalLevel coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void rangedCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                     @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 4) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of ranged-critical fields. Expected 4 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        Integer cutoff = coerceInt(f.get(0), line, errors);
        Integer damageMultiplier = coerceInt(f.get(1), line, errors);
        Integer damageAdded = coerceInt(f.get(2), line, errors);
        if (cutoff == null || damageMultiplier == null || damageAdded == null)
            return;

        StringBuilder messageBuilder = new StringBuilder();
        for (int index = 3; index < f.size(); index++) {
            messageBuilder.append(f.get(index));
            messageBuilder.append(":");
        }

        String messageType = messageBuilder.substring(0, messageBuilder.toString().length() - 1);
        MessageType messageTypeEnum;

        messageTypeEnum = MessageType.lookupByName(messageType);
        if (messageTypeEnum == null) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal message type value: " + f.get(3);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        b.addRangedCriticalLevelData(new RangedCriticalLevelData(cutoff, damageMultiplier, damageAdded, messageTypeEnum));
    }

    /**
     * Reads one {@code ranged-critical:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_ranged_critical()} in {@code init.c} ({@code sym label int value}).
     * Fewer than two fields throws; fields after the value are ignored. An unknown label throws
     * ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>There is no sign check, as in C, so a negative value is stored. The labels and the C fields
     * they set (all {@code r_crit_} fields) are {@code debuff-toh} ({@code debuff_toh}),
     * {@code chance-weight-scale} ({@code chance_weight_scl}), {@code chance-toh-scale}
     * ({@code chance_toh_scl}), {@code chance-level-scale} ({@code chance_level_scl}),
     * {@code chance-launched-toh-skill-scale} ({@code chance_launched_toh_skill_scl}),
     * {@code chance-thrown-toh-skill-scale} ({@code chance_thrown_toh_skill_scl}),
     * {@code chance-offset} ({@code chance_offset}), {@code chance-range} ({@code chance_range}),
     * {@code power-weight-scale} ({@code power_weight_scl}) and {@code power-random}
     * ({@code power_random}).
     *
     * <p>Function rangedCritical coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void rangedCritical(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of ranged-critical fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "debuff-toh" -> {
                b.setRangedCriticalDebuffToh(value);
            }
            case "chance-weight-scale" -> {
                b.setRangedCriticalChanceWeightScale(value);
            }
            case "chance-toh-scale" -> {
                b.setRangedCriticalChanceTohScale(value);
            }
            case "chance-level-scale" -> {
                b.setRangedCriticalChanceLevelScale(value);
            }
            case "chance-launched-toh-skill-scale" -> {
                b.setRangedCriticalChanceLaunchedTohSkillScale(value);
            }
            case "chance-thrown-toh-skill-scale" -> {
                b.setRangedCriticalChanceThrownTohSkillScale(value);
            }
            case "chance-offset" -> {
                b.setRangedCriticalChanceOffset(value);
            }
            case "chance-range" -> {
                b.setRangedCriticalChanceRange(value);
            }
            case "power-weight-scale" -> {
                b.setRangedCriticalPowerWeightScale(value);
            }
            case "power-random" -> {
                b.setRangedCriticalPowerRandom(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal ranged-critical value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }


    /**
     * Reads one {@code melee-critical-level:<cutoff>:<mult>:<add>:<message>} line and appends it to
     * the builder's list of standard melee critical levels. Port of
     * {@code parse_constants_melee_critical_level()} in {@code init.c}, whose directive is
     * {@code int cutoff int mult int add str msg}.
     *
     * <p>Fewer than four fields throws. The three numbers are signed and have no sign check, as in C,
     * so the {@code -1} cutoff that ends the shipped list loads. The message is every field from the
     * fourth on joined with {@code :}, because C's {@code str} takes the rest of the line, and is
     * looked up with {@link MessageType#lookupByName}. An unknown name, or {@code NONE}, throws, as
     * C's {@code INVALID_MESSAGE} does.
     *
     * <p>The row is added in file order. Whether the cutoffs strictly increase is checked once every
     * line is in, by the builder's {@code checkCriticalLevelDataLists}, which {@link #assemble} calls.
     *
     * <p>Function meleeCriticalLevel coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void meleeCriticalLevel(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                                    @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 4) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of melee critical fields. Expected 4 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        Integer cutoff = coerceInt(f.get(0), line, errors);
        Integer damageMultiplier = coerceInt(f.get(1), line, errors);
        Integer damageAddition = coerceInt(f.get(2), line, errors);
        if (cutoff == null || damageMultiplier == null || damageAddition == null) return;

        StringBuilder messageBuilder = new StringBuilder();
        for (int index = 3; index < f.size(); index++) {
            messageBuilder.append(f.get(index));
            messageBuilder.append(":");
        }

        String messageType = messageBuilder.substring(0, messageBuilder.toString().length() - 1);
        MessageType messageTypeEnum;

        messageTypeEnum = MessageType.lookupByName(messageType);
        if (messageTypeEnum == null) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal message type value: " + f.get(3);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        MeleeCriticalLevelData meleeCriticalLevelData = new MeleeCriticalLevelData(cutoff, damageMultiplier,
                damageAddition, messageTypeEnum);

        b.addMeleeCriticalLevelData(meleeCriticalLevelData);
    }

    /**
     * Reads one {@code melee-critical:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_melee_critical()} in {@code init.c} ({@code sym label int value}).
     * Fewer than two fields throws; fields after the value are ignored. An unknown label throws
     * ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>There is no sign check, as in C, and the shipped file needs that for
     * {@code melee-critical:chance-offset:-60}. The labels and the C fields they set (all
     * {@code m_crit_} fields) are {@code debuff-toh} ({@code debuff_toh}),
     * {@code chance-weight-scale} ({@code chance_weight_scl}), {@code chance-toh-scale}
     * ({@code chance_toh_scl}), {@code chance-level-scale} ({@code chance_level_scl}),
     * {@code chance-toh-skill-scale} ({@code chance_toh_skill_scl}), {@code chance-offset}
     * ({@code chance_offset}), {@code chance-range} ({@code chance_range}),
     * {@code power-weight-scale} ({@code power_weight_scl}) and {@code power-random}
     * ({@code power_random}).
     *
     * <p>Function meleeCritical coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void meleeCritical(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                               @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of melee-critical fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        switch (label) {
            case "debuff-toh" -> {
                b.setMeleeCriticalDebuffToh(value);
            }
            case "chance-weight-scale" -> {
                b.setMeleeCriticalChanceWeightScale(value);
            }
            case "chance-toh-scale" -> {
                b.setMeleeCriticalChanceTohScale(value);
            }
            case "chance-level-scale" -> {
                b.setMeleeCriticalChanceLevelScale(value);
            }
            case "chance-toh-skill-scale" -> {
                b.setMeleeCriticalChanceTohSkillScale(value);
            }
            case "chance-offset" -> {
                b.setMeleeCriticalChanceOffset(value);
            }
            case "chance-range" -> {
                b.setMeleeCriticalChanceRange(value);
            }
            case "power-weight-scale" -> {
                b.setMeleeCriticalPowerWeightScale(value);
            }
            case "power-random" -> {
                b.setMeleeCriticalPowerRandom(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal melee-critical value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code player:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_player()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws; fields after the value are ignored. A negative value throws
     * ({@code INVALID_VALUE}) and so does an unknown label ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>The labels and the C fields they set are {@code max-sight} ({@code max_sight}),
     * {@code max-range} ({@code max_range}), {@code start-gold} ({@code start_gold}) and
     * {@code food-value} ({@code food_value}).
     *
     * <p>Function player coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void player(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                        @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of player fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (value < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative player field value: " + f.get(0) + ":" + f.get(1);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        switch (label) {
            case "max-sight" -> {
                b.setPlayerMaxSight(value);
            }
            case "max-range" -> {
                b.setPlayerMaxRange(value);
            }
            case "start-gold" -> {
                b.setPlayerStartGold(value);
            }
            case "food-value" -> {
                b.setPlayerFoodValue(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal player value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code obj-make:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_obj_make()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws; fields after the value are ignored. A negative value throws
     * ({@code INVALID_VALUE}) and so does an unknown label ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>The labels and the C fields they set are {@code max-depth} ({@code max_obj_depth}),
     * {@code great-obj} ({@code great_obj}), {@code great-ego} ({@code great_ego}),
     * {@code fuel-torch} ({@code fuel_torch}), {@code fuel-lamp} ({@code fuel_lamp}) and
     * {@code default-lamp} ({@code default_lamp}).
     *
     * <p>Function objGen coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void objGen(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                        @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of obj-make fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (value < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative obj-make field value: " + f.get(0) + ":" + f.get(1);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        switch (label) {
            case "max-depth" -> {
                b.setObjMakeMaxDepth(value);
            }
            case "great-obj" -> {
                b.setObjGreatObj(value);
            }
            case "great-ego" -> {
                b.setObjGreatEgo(value);
            }
            case "fuel-torch" -> {
                b.setObjFuelTorch(value);
            }
            case "fuel-lamp" -> {
                b.setObjFuelLamp(value);
            }
            case "default-lamp" -> {
                b.setObjDefaultLamp(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal obj-make value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code store:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_store()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws; fields after the value are ignored. A negative value throws
     * ({@code INVALID_VALUE}) and so does an unknown label ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>The labels and the C fields they set are {@code inven-max} ({@code store_inven_max}),
     * {@code turns} ({@code store_turns}), {@code shuffle} ({@code store_shuffle}) and
     * {@code magic-level} ({@code store_magic_level}).
     *
     * <p>Function store coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void store(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                       @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of store fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (value < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative store field value: " + f.get(0) + ":" + f.get(1);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        switch (label) {
            case "inven-max" -> {
                b.setStoreInvenMax(value);
            }
            case "turns" -> {
                b.setStoreTurns(value);
            }
            case "shuffle" -> {
                b.setStoreShuffle(value);
            }
            case "magic-level" -> {
                b.setStoreMagicLevel(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal store value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code carry-cap:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_carry_cap()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws; fields after the value are ignored. A negative value throws
     * ({@code INVALID_VALUE}) and so does an unknown label ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>The labels and the C fields they set are {@code pack-size} ({@code pack_size}),
     * {@code quiver-size} ({@code quiver_size}), {@code quiver-slot-size}
     * ({@code quiver_slot_size}), {@code thrown-quiver-mult} ({@code thrown_quiver_mult}) and
     * {@code floor-size} ({@code floor_size}).
     *
     * <p>Function carryCap coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void carryCap(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                          @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of carry-cap fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (value < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative carry-cap field value: " + f.get(0) + ":" + f.get(1);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        switch (label) {
            case "pack-size" -> {
                b.setCarryCapPackSize(value);
            }
            case "quiver-size" -> {
                b.setCarryCapQuiverSize(value);
            }
            case "quiver-slot-size" -> {
                b.setCarryCapQuiverSlotSize(value);
            }
            case "thrown-quiver-mult" -> {
                b.setCarryCapThrownQuiverMult(value);
            }
            case "floor-size" -> {
                b.setCarryCapFloorSize(value);
            }
            default -> {

                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal carry-cap value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code world:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_world()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws; fields after the value are ignored, so {@code world:max-depth:5:6} loads
     * 5 as it does in C. A negative value throws ({@code INVALID_VALUE}) and so does an unknown
     * label ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>The labels and the C fields they set are {@code max-depth} ({@code max_depth}),
     * {@code day-length} ({@code day_length}), {@code dungeon-hgt} ({@code dungeon_hgt}),
     * {@code dungeon-wid} ({@code dungeon_wid}), {@code town-hgt} ({@code town_hgt}),
     * {@code town-wid} ({@code town_wid}), {@code feeling-total} ({@code feeling_total}),
     * {@code feeling-need} ({@code feeling_need}), {@code stair-skip} ({@code stair_skip}) and
     * {@code move-energy} ({@code move_energy}).
     *
     * <p>Function world coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void world(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                       @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of world fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (value < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative world field value: " + f.get(0) + ":" + f.get(1);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        switch (label) {
            case "max-depth" -> {
                b.setWorldMaxDepth(value);
            }
            case "day-length" -> {
                b.setWorldDayLength(value);
            }
            case "dungeon-hgt" -> {
                b.setWorldDungeonHgt(value);
            }
            case "dungeon-wid" -> {
                b.setWorldDungeonWid(value);
            }
            case "town-hgt" -> {
                b.setWorldTownHgt(value);
            }
            case "town-wid" -> {
                b.setWorldTownWid(value);
            }
            case "feeling-total" -> {
                b.setWorldFeelingTotal(value);
            }
            case "feeling-need" -> {
                b.setWorldFeelingNeed(value);
            }
            case "stair-skip" -> {
                b.setWorldStairSkip(value);
            }
            case "move-energy" -> {
                b.setWorldMoveEnergy(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal world value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code dun-gen:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_dun_gen()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws; fields after the value are ignored. A negative value throws
     * ({@code INVALID_VALUE}) and so does an unknown label ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>The labels and the C fields they set are {@code cent-max} ({@code level_room_max}),
     * {@code door-max} ({@code level_door_max}), {@code wall-max} ({@code wall_pierce_max}),
     * {@code tunn-max} ({@code tunn_grid_max}), {@code amt-room} ({@code room_item_av}),
     * {@code amt-item} ({@code both_item_av}), {@code amt-gold} ({@code both_gold_av}) and
     * {@code pit-max} ({@code level_pit_max}).
     *
     * <p>Function dunGen coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void dunGen(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                        @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of dun-gen fields. Expected 2, got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (value < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative dun-gen field value: " + f.get(0) + ":" + f.get(1);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        switch (label) {
            case "cent-max" -> {
                b.setDunGenCentMax(value);
            }
            case "door-max" -> {
                b.setDunGenDoorMax(value);
            }
            case "wall-max" -> {
                b.setDunGenWallMax(value);
            }
            case "tunn-max" -> {
                b.setDunGenTunnMax(value);
            }
            case "amt-room" -> {
                b.setDunGenAmtRoom(value);
            }
            case "amt-item" -> {
                b.setDunGenAmtItem(value);
            }
            case "amt-gold" -> {
                b.setDunGenAmtGold(value);
            }
            case "pit-max" -> {
                b.setDunGenPitMax(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal dun-gen value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code mon-play:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_mon_play()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws, so {@code mon-play:flee-range} with no value is refused as C's
     * {@code MISSING_FIELD}; fields after the value are ignored. A negative value throws
     * ({@code INVALID_VALUE}) and so does an unknown label ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>The labels and the C fields they set are {@code break-glyph} ({@code glyph_hardness}),
     * {@code mult-rate} ({@code repro_monster_rate}), {@code life-drain}
     * ({@code life_drain_percent}), {@code flee-range} ({@code flee_range}) and {@code turn-range}
     * ({@code turn_range}).
     *
     * <p>Function monPlay coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void monPlay(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                         @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of mon-play fields. Expected 2, got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (value < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative mon-play field value: " + f.get(0) + ":" + f.get(1);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        switch (label) {
            case "break-glyph" -> {
                b.setMonPlayBreakGlyph(value);
            }
            case "mult-rate" -> {
                b.setMonPlayMultRate(value);
            }
            case "life-drain" -> {
                b.setMonPlayLifeDrain(value);
            }
            case "flee-range" -> {
                b.setMonPlayFleeRange(value);
            }
            case "turn-range" -> {
                b.setMonPlayTurnRange(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal mon-play value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code mon-gen:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_mon_gen()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws; fields after the value are ignored. A negative value throws
     * ({@code INVALID_VALUE}) and so does an unknown label ({@code UNDEFINED_DIRECTIVE}).
     *
     * <p>The labels and the C fields they set are {@code chance} ({@code alloc_monster_chance}),
     * {@code level-min} ({@code level_monster_min}), {@code town-day} ({@code town_monsters_day}),
     * {@code town-night} ({@code town_monsters_night}), {@code repro-max}
     * ({@code repro_monster_max}), {@code ood-chance} ({@code ood_monster_chance}),
     * {@code ood-amount} ({@code ood_monster_amount}), {@code group-max}
     * ({@code monster_group_max}) and {@code group-dist} ({@code monster_group_dist}).
     *
     * <p>Function monGen coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void monGen(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                        @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of mon-gen fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (value < 0) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal negative mon-gen field value: " + f.get(0) + ":" + f.get(1);
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        switch (label) {
            case "chance" -> {
                b.setMonGenChance(value);
            }
            case "level-min" -> {
                b.setMonGenLevelMin(value);
            }
            case "town-day" -> {
                b.setMonGenTownDay(value);
            }
            case "town-night" -> {
                b.setMonGenTownNight(value);
            }
            case "repro-max" -> {
                b.setMonGenReproMax(value);
            }
            case "ood-chance" -> {
                b.setMonGenOodChance(value);
            }
            case "ood-amount" -> {
                b.setMonGenOodAmount(value);
            }
            case "group-max" -> {
                b.setMonGenGroupMax(value);
            }
            case "group-dist" -> {
                b.setMonGenGroupDist(value);
            }
            default -> {
                String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal mon-gen value: " + label;
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
        }
    }

    /**
     * Reads one {@code level-max:<label>:<value>} line and stores the value. Port of
     * {@code parse_constants_level_max()} in {@code init.c} ({@code sym label int value}). Fewer than
     * two fields throws; fields after the value are ignored. The one label is {@code monsters}
     * ({@code level_monster_max}).
     *
     * <p>C returns {@code INVALID_VALUE} for a negative value and {@code UNDEFINED_DIRECTIVE} for any
     * other label. Here both throw from the same branch, so the message names the label and the
     * value together ({@code monsters:-1} or {@code foo:5}) and the reader has to look at which one
     * is wrong.
     *
     * <p>Function levelMax coded before 260915, commented in full on 261025.
     *
     * @param rec    The entry from the list
     * @param b      The builder responsible for building the game data
     * @param errors The list of current errors
     */
    private void levelMax(@NotNull GameConstantsParseRecord rec, @NotNull GameConstantsData.GameConstantsBuilder b,
                          @NotNull List<String> errors) {
        List<String> f = rec.getFields();
        int line = rec.getLineNumber();

        if (f.size() < 2) {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal number of mon-max fields. Expected 2 got " + f.size();
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }

        String label = f.get(0);
        Integer value = coerceInt(f.get(1), line, errors);
        if (value == null) return;

        if (label.equals("monsters") && value >= 0) {
            b.setLevelMaxMonsters(value);
        } else {
            String msg = "Game constant starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal max monster value: " + f.get(0) + ":" + f.get(1);
            ;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
    }

    /**
     * Reads one integer field of a {@code constants.txt} line through
     * {@link ParseUtils#parseIntString}, which follows C's {@code strtol} with base 0: {@code 010}
     * is 8, {@code 0x10} is 16, and a value outside the range of an {@code int} is clamped to it.
     * This stands in for the {@code PARSE_T_INT} (and, with {@link ParseUtils#minusFound} in the
     * caller, {@code PARSE_T_UINT}) branch of {@code parser_parse()} in {@code parser.c}.
     *
     * <p>Despite the {@link Nullable} annotation it never returns {@code null}. Text with no digits
     * makes {@code parseIntString} throw a {@link DataFileParseException}, as C returns
     * {@code PARSE_ERROR_NOT_NUMBER}. The {@code errors} list is not used, and the
     * {@code == null} tests in the handlers cannot be true.
     *
     * <p>Function coerceInt coded before 260915, commented in full on 261025.
     *
     * @param raw    The string expression of the integer
     * @param line   The line this string was found on in the datafile
     * @param errors The list of current errors; not used
     * @return the value of {@code raw}, never {@code null}
     * @throws DataFileParseException if {@code raw} has no digits
     */
    @Nullable
    @CheckReturnValue
    private Integer coerceInt(@NotNull String raw, int line, @NotNull List<String> errors) {
        return ParseUtils.parseIntString("Game constant", raw, line, filename, "integer");
    }
}
