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

package uk.co.jackoftradesltd.backend.parser.projection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link ProjectionParseRecord}s from {@code projection.txt} into domain
 * {@link Projection}s - the Java form of the {@code parse_projection_*} functions and
 * {@code finish_parse_projection()} in the C original's {@code obj-init.c}. Every enum-shaped
 * field ({@code code}, {@code type}, {@code msgt}, {@code colour}) is resolved by its own
 * {@code resolveX} helper; the numeric fields are parsed inline, and {@code denominator} falls
 * back to a {@link Random} dice expression when it is not a plain integer.
 *
 * <p>C's parser quits on the first bad line, and so does this port: every fault stops the load by
 * throwing a {@link DataFileParseException}, and no record is skipped. A fault is a negative or
 * non-numeric numerator, divisor, damage cap, {@code obvious} or {@code wake}, a denominator that
 * is neither a number nor a dice string, an unresolvable {@code msgt}, or a bad code or element
 * count (below). The {@code errors} list is never written to; it stays in the signature because
 * {@link Assembler} requires it. An unknown colour is not a fault: it falls back to white, as C
 * does.
 *
 * <p>C reads the file a line at a time and quits on the first bad line; this port has the whole
 * record, so it checks the code first, then the numerator, denominator, divisor, damage cap,
 * {@code obvious} and {@code wake}, and the {@code msgt} last. A record with two faults can
 * therefore name a different one than C does, but the same record stops the load. The four
 * {@code uint} fields refuse a leading {@code -} (so {@code -0} too), as {@code parser_parse()}
 * does, and are read with base detection like {@code strtoul} in base 0. {@code obvious} and
 * {@code wake} are true only for exactly 1.
 *
 * <p>Accepted deviation (decided 261029, recorded in {@code docs/Accepted_Deviations.md}): a
 * {@code denominator:} made only of digits, with an optional sign, is read in base 10 and clamped
 * to the {@code int} range. C's {@code parse_random()} refuses any figure above {@code INT_MAX},
 * so {@code 2147483648} loads here as {@code 2147483647} where C quits. In-range figures agree.
 *
 * <p>A record's {@code code} is only read where C reads it. {@code parse_projection_code()}
 * compares the code with {@code element_names[index]} while {@code index < ELEM_MAX} and ignores it
 * after that, so a code that is not a projection is stored as {@link ProjectionEnum#PROJ_NONE},
 * with no error, from the 26th record on.
 *
 * <p>Three faults concern the element block. C subscripts its projection array by element value,
 * and a damaged element block would silently misalign every lookup:
 * <ul>
 *   <li>one of the first 25 records has an unknown or empty code, or a code that is not the
 *       element at its position in {@code list-elements.h} order - the check C makes in
 *       {@code parse_projection_code()}, where it returns
 *       {@code PARSE_ERROR_ELEMENT_NAME_MISMATCH};</li>
 *   <li>a record past the 25th has an empty code - C's {@code parser_parse} returns
 *       {@code PARSE_ERROR_MISSING_FIELD} for a {@code code:} with nothing after it;</li>
 *   <li>the number of records of type {@code element} is not 25 - the check C makes in
 *       {@code finish_parse_projection()}. Because no record is skipped, this fires only when the
 *       file holds too few or too many records whose {@code type:} is {@code element}.</li>
 * </ul>
 *
 * <p>The exception is not caught by {@code GrammarDriver}, so it reaches the caller of
 * {@code ProjectionReader.parseWithResults}. The empty-code stop is reachable only from hand-built
 * records: the grammar rejects a {@code code:} with no value before the assembler sees it. The
 * grammar also makes {@code code}, {@code type}, {@code desc}, {@code blind-desc}, {@code obvious}
 * and {@code color} mandatory, so only the other fields of a record can be {@code null}.
 *
 * <p>The assembled list is in file order, base 0, with no blank slot: C's {@code projections}
 * array holds the same records, and {@code z_info->projection_max} is their count.
 *
 * <p>Class ProjectionAssembler coded before 260915, commented in full on 261029; every fault now
 * stops the load, the denominator range deviation recorded, on 261029.
 *
 * @author Rowan Crowther
 */
public class ProjectionAssembler implements Assembler<ProjectionParseRecord, List<Projection>> {
    /**
     * Logs a fatal code fault before it is thrown, so the cause of a stopped load is on record even
     * if a caller catches the exception without printing its message.
     *
     * <p>Field logger coded on 261001, commented in full on 261001; reworded on 261021 because
     * {@code GrammarDriver} no longer turns the exception into an empty result.
     */
    private static final Logger logger = LogManager.getLogger(ProjectionAssembler.class);

    /**
     * The number of projections of type {@code element} the file must contain, and the number of
     * leading records whose position is checked: one per constant {@code ELEM(...)} in the C
     * original's {@code list-elements.h}. C derives this from {@code N_ELEMENTS(element_names)}
     * (which includes a terminating {@code NULL}, hence its {@code element_count + 1}
     * comparison in {@code finish_parse_projection()}); here it is stated outright.
     *
     * <p>Field EXPECTED_ELEMENT_COUNT coded on 261001, commented in full on 261001.
     */
    private static final int EXPECTED_ELEMENT_COUNT = 25;

    /**
     * Assembles the {@link Projection} list from the parsed {@code projection.txt} records.
     *
     * <p>Each record is handled in file order. Its position is counted from 1 for every record,
     * because C's {@code index} counts every record. The code is read in two ways:
     * <ul>
     *   <li>while the position is 25 or less it must resolve to a {@link ProjectionEnum} constant
     *       whose ordinal equals the position ({@link ProjectionEnum#PROJ_ACID} is ordinal 1, as
     *       {@code PROJ_NONE} takes 0). An unknown or empty code, or a different ordinal, stops the
     *       load, as C's {@code PARSE_ERROR_ELEMENT_NAME_MISMATCH} does;</li>
     *   <li>from the 26th record on, C does not read the code. An empty one still stops the load. A
     *       code that resolves is kept, and one that does not is stored as
     *       {@link ProjectionEnum#PROJ_NONE} with no message. The order of these records is not
     *       checked.</li>
     * </ul>
     * The remaining fields are then parsed in this order: {@code numerator}, {@code denominator},
     * {@code divisor}, {@code damage-cap}, {@code obvious}, {@code wake}, then {@code color}, and
     * {@code msgt} is resolved after the numerators but checked last. Any fault stops the load.
     * <ul>
     *   <li>{@code numerator}, {@code divisor}, {@code damage-cap}, {@code obvious} and
     *       {@code wake} are C's {@code uint} fields: a leading {@code -} is refused (so is
     *       {@code -0}), and the digits are read with base detection like {@code strtoul} in base
     *       0, so {@code 010} is 8. A figure outside the {@code int} range is clamped, the
     *       deviation recorded under ParseUtils in {@code docs/Accepted_Deviations.md};</li>
     *   <li>{@code obvious} and {@code wake} are true only for exactly 1, as in C;</li>
     *   <li>{@code denominator} made only of digits, after an optional sign, is read in base 10
     *       ({@code 010} is 10, as in {@code parse_random()}) into the integer denominator.
     *       Anything else goes to {@link Random#parseStr(String)} and is stored as dice; a
     *       {@code null} result stops the load. Accepted deviation (decided 261029, recorded under
     *       Projection in {@code docs/Accepted_Deviations.md}): a digit-only figure above
     *       {@code INT_MAX} is clamped here, where C's {@code parse_random()} refuses it and the
     *       load quits. The dice path was not examined in this pass.</li>
     * </ul>
     *
     * <p>After the loop the records of type {@code element} are counted from the result itself. If
     * the count is not 25 the method stops the load with the expected, found and short counts in
     * the message.
     *
     * <p>An absent {@code msgt} gives {@code MSG_GENERIC}, an absent {@code color} gives
     * {@code COLOUR_DARK}, and absent numeric or boolean fields give 0 and {@code false}, matching
     * the zero-filled struct C allocates for each record.
     *
     * <p>Function assemble coded before 260915, commented in full on 261029.
     *
     * @param records  the parsed records, in file order
     * @param errors   never written to; it is required by the {@link Assembler} signature, and a
     *                 fault stops the load by exception instead
     * @param filename the data file being assembled, used in the stop-the-load messages
     * @return the assembled projections, in file order, always with exactly 25 of type
     *         {@code element}
     * @throws DataFileParseException if one of the first 25 records has an unknown or empty code or
     *                                is not the element at its position, if a later record has an
     *                                empty code, if a numeric field is negative or malformed, if
     *                                the denominator is not a number or a dice string, if the
     *                                {@code msgt} names no message type, or if the number of
     *                                element records is not 25
     */
    @Override
    public List<Projection> assemble(@NotNull List<ProjectionParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<Projection> result = new ArrayList<>();
        int currentRecord = 0;
        for (ProjectionParseRecord record : records) {
            int line = record.lineNumber();
            String codeString = record.code();
            String type = record.type();
            ProjectionType projType = resolveType(line, type, errors);
            currentRecord++;
            ProjectionEnum projCode;
            if (currentRecord <= EXPECTED_ELEMENT_COUNT) {
                projCode = resolveCode(line, codeString, errors, true, filename);
                if (projCode == null) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + 
                            " is out of order\nInput string: " + record.code() + " is invalid";
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                int projOrdinal = projCode.ordinal();
                if (currentRecord != projOrdinal) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + 
                            " is out of order\nInput ordinal: " + projOrdinal + " is not equal to " +
                            "expected ordinal: " + currentRecord;
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            } else {
                if (codeString.isEmpty()) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an empty code";
                    logger.fatal(message);
                    throw new DataFileParseException(message);                    
                }
                
                projCode = resolveCode(line, codeString, errors, false, filename);
                if (projCode == null) projCode = ProjectionEnum.PROJ_NONE;
            }
            String projName = record.name();
            String projDesc = record.desc();
            String projPlayerDesc = record.playerDesc();
            String projBlindDesc = record.blindDesc();
            String projLashDesc = record.lashDesc();
            int projNumerator = 0;
            if (record.numerator() != null) {
                if (ParseUtils.minusFound(record.numerator())) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative numerator value: " + record.numerator();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                projNumerator = ParseUtils.parseIntString("Projection", record.numerator(), line, filename, "numerator");
                if (projNumerator < 0) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal numerator value: " + record.numerator();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            int projDenominator = 0;
            Random projDenominatorDice = null;
            if (record.denominator() != null) {
                String den = record.denominator();
                int sign = ParseUtils.signFound(den);
                if (sign != 0) den = den.substring(1);
                if (ParseUtils.onlyDigits(den)) {
                    projDenominator = ParseUtils.parseIntString("Projection", record.denominator(), line, filename, "denominator", ParseUtils.Radix.DECIMAL);
                } else {
                    projDenominatorDice = Random.parseStr(record.denominator());
                    if (projDenominatorDice == null) {
                        String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal denominator die value: " + record.denominator();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                }
            }
            int projDivisor = 0;
            if (record.divisor() != null) {
                if (ParseUtils.minusFound(record.divisor())) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative divisor value: " + record.divisor();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                projDivisor = ParseUtils.parseIntString("Projection", record.divisor(), line, filename, "divisor");
                if (projDivisor < 0) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal divisor value: " + record.divisor();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            int projDamageCap = 0;
            if (record.damageCap() != null) {
                if (ParseUtils.minusFound(record.damageCap())) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative damage cap value: " + record.damageCap();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                projDamageCap = ParseUtils.parseIntString("Projection", record.damageCap(), line, filename, "damage cap"); 
                if (projDamageCap < 0) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal damage cap value: " + record.damageCap();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            String msgt = record.msgt();
            MessageType projMsgt = msgt == null ? MessageType.MSG_GENERIC : resolveMsgt(line, msgt, codeString, errors, filename);
            boolean projObvious;
            int boolValue;
            if (record.obvious() != null) {
                if (ParseUtils.minusFound(record.obvious())) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative obvious value: " + record.obvious();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                boolValue = ParseUtils.parseIntString("Projection", record.obvious(), line, filename, "obvious");
                if (boolValue < 0) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative obvious value: " + record.obvious();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                projObvious = boolValue == 1;
            } else {
                projObvious = false;
            }
            boolean projWillWake;
            if (record.willWake() != null) {
                if (ParseUtils.minusFound(record.willWake())) {
                    String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal negative will wake value: " + record.willWake();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                boolValue = ParseUtils.parseIntString("Projection", record.willWake(), line, filename, "will wake");
                    if (boolValue < 0) {
                        String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal negative will wake value: " + record.willWake();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                        
                    }
                projWillWake = boolValue == 1;
            } else {
                projWillWake = false;
            }
            String colour = record.colour();
            ColourEnum projColour = resolveColour(line, colour, codeString, errors);

            if (projMsgt == null) {
                String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal message value: " + record.msgt();
                logger.fatal(message);
                throw new DataFileParseException(message);
            }

            Projection proj;

            if (projDenominatorDice != null)
                proj = new Projection(projCode, projName, projType, projDesc, projPlayerDesc,
                        projBlindDesc, projLashDesc, projNumerator, projDenominatorDice,
                        projDivisor, projDamageCap, projMsgt, projObvious, projWillWake,
                        projColour);
            else
                proj = new Projection(projCode, projName, projType, projDesc, projPlayerDesc,
                        projBlindDesc, projLashDesc, projNumerator, projDenominator,
                        projDivisor, projDamageCap, projMsgt, projObvious, projWillWake,
                        projColour);

            result.add(proj);
        }

        int kept = (int) result.stream().filter(p -> p.getType() == ProjectionType.PT_ELEMENT).count();
        int dropped = EXPECTED_ELEMENT_COUNT - kept;
        if (dropped < 0) dropped = 0;

        if (kept != EXPECTED_ELEMENT_COUNT) {
            String message = "Invalid number of Projection elements - expected: " + EXPECTED_ELEMENT_COUNT + " got: "
                    + kept + " dropped: " + dropped + " in file: " + filename;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        return result;
    }

    /**
     * Resolves the {@code code:} tag of a projection block to its {@link ProjectionEnum} constant
     * by prefixing {@code PROJ_} and looking the name up exactly, so the tag is case-sensitive.
     *
     * <p>C does not look the code up at all: for the first 25 records it only compares the tag with
     * {@code element_names[index]} ({@code parse_projection_code()} in {@code obj-init.c}), and
     * beyond that it stores nothing from the code. The lookup is needed here because the position
     * check in {@link #assemble} compares ordinals, and because the port keeps the constant for
     * later lookups. An empty tag finds no constant, like any other unknown one.
     *
     * <p>For one of the first 25 records an unknown tag is a fault, as C's mismatch with
     * {@code element_names[index]} is: the method logs it and throws, so it never returns
     * {@code null} there. From the 26th record on it returns {@code null} quietly and the caller
     * stores {@link ProjectionEnum#PROJ_NONE}. The tags {@code NONE} and {@code MAX} resolve to
     * their constants; the ordinal check in {@link #assemble} rejects them in the element block.
     *
     * <p>Function resolveCode coded before 260915, commented in full on 261029.
     *
     * @param line        the line of the projection block in the {@code lib/gamedata} file, used in
     *                    the error message
     * @param code        the tag from the {@code code:} line, without its {@code PROJ_} prefix
     * @param errors      unused
     * @param insideCount true for one of the first 25 records, where C reads the code and an unknown
     *                    one is a fault; false from the 26th on, where C does not read it
     * @param filename    the data file being assembled, used in the error message
     * @return the matching constant, or {@code null} if there is none and {@code insideCount} is
     *         false
     * @throws DataFileParseException if no constant matches and {@code insideCount} is true
     */
    @CheckReturnValue
    @Nullable
    private ProjectionEnum resolveCode(int line, @NotNull String code,
                                       @NotNull List<String> errors, boolean insideCount, String filename) {
        try {
            return ProjectionEnum.valueOf("PROJ_" + code);
        } catch (IllegalArgumentException e) {
            if (insideCount) {
                String message = "Projection starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal code value: " + code;
                logger.fatal(message, e);
                throw new DataFileParseException(message);
            }
            
            return null;
        }
    }

    /**
     * Resolves the {@code type:} tag of a projection block: {@code element} gives
     * {@link ProjectionType#PT_ELEMENT}, {@code environs} gives {@link ProjectionType#PT_ENVIRONS},
     * {@code monster} gives {@link ProjectionType#PT_MONSTER}, and anything else, including an
     * empty tag, gives {@link ProjectionType#PT_NONE}. The match is case-sensitive.
     *
     * <p>C keeps the tag as a string and only ever tests it against {@code "element"}, in
     * {@code finish_parse_projection()} in {@code obj-init.c}; the enum is a Java addition so the
     * type survives as data. It never reports a fault, so {@code line} and {@code errors} are
     * unused. The grammar makes {@code type:} mandatory, so the tag is never {@code null}; a
     * hand-built record with a {@code null} type would throw a {@link NullPointerException} from
     * the {@code switch}, where C's zeroed record would hold NULL and count as no element.
     *
     * <p>Function resolveType coded before 260915, commented in full on 261029.
     *
     * @param line   the line of the projection block in the {@code lib/gamedata} file; unused
     * @param type   the tag from the {@code type:} line
     * @param errors unused
     * @return the matching constant, or {@code PT_NONE} if the tag is not one of the three
     */
    @CheckReturnValue
    @NotNull
    @Contract(pure = true)
    private ProjectionType resolveType(int line, @NotNull String type, 
                                       @NotNull List<String> errors) {
        return switch (type) {
            case "element" -> ProjectionType.PT_ELEMENT;
            case "environs" -> ProjectionType.PT_ENVIRONS;
            case "monster" -> ProjectionType.PT_MONSTER;
            default -> ProjectionType.PT_NONE;
        };
    }

    /**
     * Resolves the {@code msgt:} value of a projection block to a {@link MessageType}, following
     * {@code message_lookup_by_name()} in the C original's {@code message.c}. The whole job is
     * handed to {@link MessageType#lookupByName(String)}, which holds the rules: a number selects
     * a message type by position and a name is matched case-insensitively, and the accepted
     * non-ASCII case-folding deviation is recorded there.
     *
     * <p>The caller supplies {@code MSG_GENERIC} for an absent {@code msgt:} line, so it never
     * reaches this method. The method reports nothing itself: a {@code null} return is turned
     * into a stop-the-load exception by {@link #assemble}, after the colour is resolved, where C
     * returns {@code PARSE_ERROR_INVALID_MESSAGE} at the {@code msgt:} line.
     *
     * <p>Function resolveMsgt coded before 260915, commented in full on 261029.
     *
     * @param line     the line of the projection block; unused
     * @param msgt     the value from the {@code msgt:} line, as a name without {@code MSG_} or a
     *                 number
     * @param code     the {@code code:} tag of the block; unused
     * @param errors   unused
     * @param filename the data file being assembled; unused
     * @return the matching constant, or {@code null} if there is none
     */
    @CheckReturnValue
    @Nullable
    private MessageType resolveMsgt(int line, @NotNull String msgt, @NotNull String code,
                                    @NotNull List<String> errors, String filename) {
        return MessageType.lookupByName(msgt);
    }

    /**
     * Resolves the {@code color:} value of a projection block to a {@link ColourEnum}, following
     * {@code parse_projection_color()} in the C original's {@code obj-init.c} and the lookups it
     * calls in {@code z-color.c}, {@code color_text_to_attr()} and {@code color_char_to_attr()}:
     * <ul>
     *   <li>an empty value or a single space gives {@code COLOUR_DARK}, as does an absent line in
     *       C, where the zero-filled struct leaves colour 0;</li>
     *   <li>a value longer than one character is a colour name, matched case-insensitively;</li>
     *   <li>a single character is an index character, matched case-sensitively;</li>
     *   <li>a value that matches nothing falls back to {@code COLOUR_WHITE}, never to an error.</li>
     * </ul>
     *
     * <p>{@link ColourEnum#COLOUR_SHADE} is never returned. {@link ColourEnum#fromCode(String)}
     * matches the name {@code Shade}, a Java addition, but C's colour table has no usable row for
     * it, so the name falls back to white here like any unknown one. The method never returns
     * {@code null} and never writes to {@code errors}.
     *
     * <p>Function resolveColour coded before 260915, commented in full on 261029.
     *
     * @param line   the line of the projection block in the {@code lib/gamedata} file; unused
     * @param colour the value from the {@code color:} line
     * @param code   the {@code code:} tag of the block; unused
     * @param errors unused
     * @return the matching colour, {@code COLOUR_DARK} for an empty or blank value, or
     *         {@code COLOUR_WHITE} if nothing matches
     */
    @CheckReturnValue
    @NotNull
    private ColourEnum resolveColour(int line, String colour, @NotNull String code,
                                     @NotNull List<String> errors) {
        if (colour == null || colour.isEmpty())
            return ColourEnum.COLOUR_DARK;

        ColourEnum result = ColourEnum.fromCode(colour);
        
        if (result == null || result == ColourEnum.COLOUR_SHADE)
            return ColourEnum.COLOUR_WHITE;
        
        return result;
    }
}
