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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;

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
 * <p>C's parser aborts the whole file on the first bad line; this port instead follows the shared
 * soft-error contract for most faults. A record with an unknown code, a negative or non-numeric
 * numerator, divisor, damage cap, {@code obvious} or {@code wake}, an invalid denominator or an
 * unresolvable {@code msgt} is reported to {@code errors} and skipped, and the rest of the file
 * still loads. An unknown colour is not a fault: it falls back to white, as C does.
 *
 * <p>Two faults are fatal, because C subscripts its projection array by element value and a
 * damaged element block would silently misalign every lookup:
 * <ul>
 *   <li>the first 25 records are not the 25 elements in {@code list-elements.h} order, which
 *       throws a {@link ParseCancellationException} at the first mismatch - the check C makes in
 *       {@code parse_projection_code()};</li>
 *   <li>the number of records of type {@code element} that survive assembly is not 25, which
 *       discards every record and reports the count - the check C makes in
 *       {@code finish_parse_projection()}. A dropped element record therefore fails the file,
 *       where C would already have aborted on the bad line.</li>
 * </ul>
 *
 * <p>Class ProjectionAssembler coded before 260915, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public class ProjectionAssembler implements Assembler<ProjectionParseRecord, List<Projection>> {
    /**
     * Logs the fatal element-order fault before it is thrown, since {@code GrammarDriver}
     * turns the exception into an empty result and its message does not reach the builder.
     *
     * <p>Field logger coded on 261001, commented in full on 261001.
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
     * including ones whose code later fails to resolve, because C's {@code index} counts every
     * record. While the position is 25 or less, the code's ordinal must equal it
     * ({@link ProjectionEnum#PROJ_ACID} is ordinal 1, as {@code PROJ_NONE} takes 0), otherwise the
     * file is rejected by throwing. The remaining fields are then parsed and any fault skips the
     * record with an entry in {@code errors}; a skipped record of type {@code element} also gets a
     * "Dropped projection element" entry.
     *
     * <p>After the loop the surviving element records are counted from the result itself, not
     * from a running tally, so a dropped element row cannot slip through. If the count is not
     * 25 the method adds an "Invalid number of elements" entry and returns an empty list.
     *
     * <p>An absent {@code msgt} gives {@code MSG_GENERIC}, an absent {@code colour} gives
     * {@code COLOUR_DARK}, and absent numeric or boolean fields give 0 and {@code false}, matching
     * the zero-filled struct C allocates for each record.
     *
     * <p>Function assemble coded before 260915, commented in full on 261001.
     *
     * @param records the parsed records, in file order
     * @param errors  receives a message for every skipped record and every other soft fault; it is
     *                returned to the builder alongside the result
     * @return the assembled projections, or an empty list if the element count is not 25
     * @throws ParseCancellationException if one of the first 25 records is not the element at
     *                                    its position; the message carries the errors gathered so far
     */
    @Override
    public List<Projection> assemble(@NotNull List<ProjectionParseRecord> records, @NotNull List<String> errors) {
        List<Projection> result = new ArrayList<>();
        int currentRecord = 0;
        for (ProjectionParseRecord record : records) {
            int line = record.lineNumber();
            String codeString = record.code();
            ProjectionEnum projCode = resolveCode(line, codeString, errors);
            currentRecord++;
            if (projCode == null) continue;
            int projOrdinal = projCode.ordinal();
            String type = record.type();
            ProjectionType projType = resolveType(line, type, codeString, errors);
            if (currentRecord <= EXPECTED_ELEMENT_COUNT && currentRecord != projOrdinal) {
                String message = "Projection at line: " + line + " is out of order.\n" +
                        "Input ordinal: " + projOrdinal + " is not equal to expected " +
                        "ordinal: " + currentRecord;
                logger.fatal(message);
                for (String error : errors) {
                    message = String.format("%s\n%s", message, error);
                }
                throw new ParseCancellationException(message);
            }
            String projName = record.name();
            String projDesc = record.desc();
            String projPlayerDesc = record.playerDesc();
            String projBlindDesc = record.blindDesc();
            String projLashDesc = record.lashDesc();
            int projNumerator = 0;
            if (!record.numerator().isEmpty()) {
                try {
                    projNumerator = Integer.parseInt(record.numerator());
                    if (projNumerator < 0) {
                        errors.add("Projection at line: " + line + " has " +
                                "a negative numerator: " + record.numerator());
                        if (projType == ProjectionType.PT_ELEMENT)
                            errors.add("Dropped projection element: " + projName);
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Projection at line: " + line + " has " +
                            "an invalid numerator: " + record.numerator());
                    if (projType == ProjectionType.PT_ELEMENT)
                        errors.add("Dropped projection element: " + projName);
                    continue;
                }
            }
            int projDenominator = 0;
            Random projDenominatorDice = null;
            if (!record.denominator().isEmpty()) {
                try {
                    projDenominator = Integer.parseInt(record.denominator());
                } catch (NumberFormatException e) {
                    projDenominatorDice = Random.parseStr(record.denominator());
                    if (projDenominatorDice == null) {
                        errors.add("Projection at line: " + line + " has " +
                                "an invalid denominator: " + record.denominator());
                        if (projType == ProjectionType.PT_ELEMENT)
                            errors.add("Dropped projection element: " + projName);
                        continue;
                    }
                }
            }
            int projDivisor = 0;
            if (!record.divisor().isEmpty()) {
                try {
                    projDivisor = Integer.parseInt(record.divisor());
                    if (projDivisor < 0) {
                        errors.add("Projection at line: " + line + " has " +
                                "a negative divisor: " + record.divisor());
                        if (projType == ProjectionType.PT_ELEMENT)
                            errors.add("Dropped projection element: " + projName);
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Projection at line: " + line + " has " +
                            "an invalid divisor: " + record.divisor());
                    if (projType == ProjectionType.PT_ELEMENT)
                        errors.add("Dropped projection element: " + projName);
                    continue;
                }
            }
            int projDamageCap = 0;
            if (!record.damageCap().isEmpty()) {
                try {
                    projDamageCap = Integer.parseInt(record.damageCap());
                    if (projDamageCap < 0) {
                        errors.add("Projection at line: " + line + " has " +
                                "a negative damageCap: " + record.damageCap());
                        if (projType == ProjectionType.PT_ELEMENT)
                            errors.add("Dropped projection element: " + projName);
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Projection at line: " + line + " has " +
                            "an invalid damageCap: " + record.damageCap());
                    if (projType == ProjectionType.PT_ELEMENT)
                        errors.add("Dropped projection element: " + projName);
                    continue;
                }
            }
            String msgt = record.msgt();
            MessageType projMsgt = msgt.isEmpty() ? MessageType.MSG_GENERIC : resolveMsgt(line, msgt, codeString, errors);
            boolean projObvious;
            int boolValue;
            if (!record.obvious().isEmpty()) {
                try {
                    boolValue = Integer.parseInt(record.obvious());
                    if (boolValue < 0) {
                        errors.add("Projection at line: " + line + " has " +
                                "a negative obvious value: " + record.obvious());
                        if (projType == ProjectionType.PT_ELEMENT)
                            errors.add("Dropped projection element: " + projName);
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Projection at line: " + line + " has " +
                            "a non-numeric obvious value: " + record.obvious());
                    if (projType == ProjectionType.PT_ELEMENT)
                        errors.add("Dropped projection element: " + projName);
                    continue;
                }
                projObvious = boolValue == 1;
            } else {
                projObvious = false;
            }
            boolean projWillWake;
            if (!record.willWake().isEmpty()) {
                try {
                    boolValue = Integer.parseInt(record.willWake());
                    if (boolValue < 0) {
                        errors.add("Projection at line: " + line + " has " +
                                "a negative will wake value: " + record.willWake());
                        if (projType == ProjectionType.PT_ELEMENT)
                            errors.add("Dropped projection element: " + projName);
                        continue;
                    }
                } catch (NumberFormatException e) {
                    errors.add("Projection at line: " + line + " has " +
                            "a non-numeric will wake value: " + record.willWake());
                    if (projType == ProjectionType.PT_ELEMENT)
                        errors.add("Dropped projection element: " + projName);
                    continue;
                }
                projWillWake = boolValue == 1;
            } else {
                projWillWake = false;
            }
            String colour = record.colour();
            ColourEnum projColour = resolveColour(line, colour, codeString, errors);

            if (projColour == null || projMsgt == null) {
                if (projType == ProjectionType.PT_ELEMENT)
                    errors.add("Dropped projection element: " + projName);
                continue;
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
            errors.add("Invalid number of elements - expected " + EXPECTED_ELEMENT_COUNT + " got " + kept +
                    " dropped " + dropped);
            return new ArrayList<>();
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
     * check in {@link #assemble} compares ordinals.
     *
     * <p>Function resolveCode coded before 260915, commented in full on 261001.
     *
     * @param line   the line of the projection block in the {@code lib/gamedata} file, used in the
     *               error message
     * @param code   the tag from the {@code code:} line, without its {@code PROJ_} prefix
     * @param errors receives an "Unknown projection code" entry when no constant matches
     * @return the matching constant, or {@code null} if there is none
     */
    @CheckReturnValue
    @Nullable
    private ProjectionEnum resolveCode(int line, @NotNull String code,
                                       @NotNull List<String> errors) {
        try {
            return ProjectionEnum.valueOf("PROJ_" + code);
        } catch (IllegalArgumentException e) {
            errors.add("Line: " + line + ": Unknown projection code in this Projection block: " + code);
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
     * type survives as data. It never reports a fault, so {@code line}, {@code code} and
     * {@code errors} are unused.
     *
     * <p>Function resolveType coded before 260915, commented in full on 261001.
     *
     * @param line   the line of the projection block in the {@code lib/gamedata} file; unused
     * @param type   the tag from the {@code type:} line
     * @param code   the {@code code:} tag of the block; unused
     * @param errors unused
     * @return the matching constant, or {@code PT_NONE} if the tag is not one of the three
     */
    @CheckReturnValue
    @NotNull
    @Contract(pure = true)
    private ProjectionType resolveType(int line, @NotNull String type, @NotNull String code,
                                       @NotNull List<String> errors) {
        if ("element".equals(type))
            return ProjectionType.PT_ELEMENT;
        else if ("environs".equals(type))
            return ProjectionType.PT_ENVIRONS;
        else if ("monster".equals(type))
            return ProjectionType.PT_MONSTER;
        return ProjectionType.PT_NONE;
    }

    /**
     * Resolves the {@code msgt:} value of a projection block to a {@link MessageType}, following
     * {@code message_lookup_by_name()} in the C original's {@code message.c}. The value is trimmed,
     * then read as either a name or a number:
     * <ul>
     *   <li>a number is accepted only if it is at least 0 and below {@code MSG_MAX}, and selects
     *       the constant at that ordinal, so {@code MSG_MAX} itself and anything beyond are
     *       rejected;</li>
     *   <li>anything else is a name, matched case-insensitively against the constants after a
     *       {@code MSG_} prefix is added.</li>
     * </ul>
     *
     * <p>The caller supplies {@code MSG_GENERIC} for an empty value, so an absent {@code msgt:}
     * line never reaches this method.
     *
     * <p>Function resolveMsgt coded before 260915, commented in full on 261001.
     *
     * @param line   the line of the projection block in the {@code lib/gamedata} file, used in the
     *               error messages
     * @param msgt   the value from the {@code msgt:} line, as a name without {@code MSG_} or a
     *               number
     * @param code   the {@code code:} tag of the block, used in the error messages
     * @param errors receives an entry when the number is out of range or the name is unknown
     * @return the matching constant, or {@code null} if there is none
     */
    @CheckReturnValue
    @Nullable
    private MessageType resolveMsgt(int line, @NotNull String msgt, @NotNull String code,
                                    @NotNull List<String> errors) {
        MessageType result;
        msgt = msgt.trim();
        int msgOrdinal = 0;
        boolean isString = false;
        try {
            try {
                msgOrdinal = Integer.parseInt(msgt);
            } catch (NumberFormatException e) {
                isString = true;
            }
            if (isString)
                result = MessageType.valueOf("MSG_" + msgt.toUpperCase());
            else {
                if (msgOrdinal < 0 || msgOrdinal >= MessageType.MSG_MAX.ordinal()) {
                    errors.add("Projection message type in Projection block: " + code +
                            " Starting at line: " + line + " is out of range of acceptable values.");
                    return null;
                }
                result = MessageType.values()[msgOrdinal];
            }
            return result;
        } catch (IllegalArgumentException e) {
            errors.add("Unknown projection message type in Projection block: " + code +
                    " Starting at line: " + line);
            return null;
        }
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
     * <p>The method never returns {@code null} and never writes to {@code errors}, despite the
     * {@link Nullable} annotation. The test against {@link ColourEnum#basicColours} is a guard for
     * a table longer than the named colours; every {@link ColourEnum} constant currently passes it.
     *
     * <p>Function resolveColour coded before 260915, commented in full on 261001.
     *
     * @param line   the line of the projection block in the {@code lib/gamedata} file; unused
     * @param colour the value from the {@code color:} line
     * @param code   the {@code code:} tag of the block; unused
     * @param errors unused
     * @return the matching colour, {@code COLOUR_DARK} for an empty or blank value, or
     *         {@code COLOUR_WHITE} if nothing matches
     */
    @CheckReturnValue
    @Nullable
    private ColourEnum resolveColour(int line, @NotNull String colour, @NotNull String code,
                                     @NotNull List<String> errors) {
        ColourEnum result;

        if (colour.isEmpty()) {
            return ColourEnum.COLOUR_DARK;
        } else if (" ".equals(colour)) {
            return ColourEnum.COLOUR_DARK;
        } else {
            result = ColourEnum.fromCode(colour);
            if (result == null)
                return ColourEnum.COLOUR_WHITE;
            if (result.ordinal() > ColourEnum.basicColours)
                return ColourEnum.COLOUR_WHITE;
        }
        return result;
    }
}
