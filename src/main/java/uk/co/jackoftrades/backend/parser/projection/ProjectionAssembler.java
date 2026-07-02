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

package uk.co.jackoftrades.backend.parser.projection;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.combat.enums.ProjectionType;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.Projection;

import java.util.ArrayList;
import java.util.List;

public class ProjectionAssembler {
    /**
     * Assembles the data from the list of {@link ProjectionParseRecord} to create
     * a List of {@link Projection} objects
     *
     * @param records The list of records to create the {@link Projection} list from
     * @param errors  A list of errors which is returned to the builder
     * @return a List of {@link Projection} objects
     * @author Rowan Crowther
     */
    public List<Projection> assemble(List<ProjectionParseRecord> records, List<String> errors) {
        List<Projection> result = new ArrayList<>();
        for (ProjectionParseRecord record : records) {
            int line = record.lineNumber();
            String codeString = record.code();
            ProjectionEnum projCode = resolveCode(line, codeString, errors);
            String projName = record.name();
            String type = record.type();
            ProjectionType projType = resolveType(line, type, codeString, errors);
            String projDesc = record.desc();
            String projPlayerDesc = record.playerDesc();
            String projBlindDesc = record.blindDesc();
            String projLashDesc = record.lashDesc();
            int projNumerator = -1;
            try {
                projNumerator = Integer.parseInt(record.numerator());
            } catch (NumberFormatException e) {
                projNumerator = -1;
            }
            int projDenominator = -1;
            Random projDenominatorDice = null;
            try {
                projDenominator = Integer.parseInt(record.denominator());
            } catch (NumberFormatException e) {
                projDenominatorDice = Random.parseStr(record.denominator());
            }
            int projDivisor = -1;
            try {
                projDivisor = Integer.parseInt(record.divisor());
            } catch (NumberFormatException e) {
                projDivisor = -1;
            }
            int projDamageCap = -1;
            try {
                projDamageCap = Integer.parseInt(record.damageCap());
            } catch (NumberFormatException e) {
                projDamageCap = -1;
            }
            String msgt = record.msgt();
            MessageType projMsgt = msgt.isEmpty() ? null : resolveMsgt(line, msgt, codeString, errors);
            boolean projObvious = record.obvious().equals("1");
            boolean projWillWake = record.willWake().equals("1");
            String colour = record.colour();
            ColourType projColour = resolveColour(line, colour, codeString, errors);

            if (projCode == null || projType == null || projColour == null)
                continue;

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

        return result;
    }

    /**
     * Resolve an uppercase string tag into a {@link ProjectionEnum} value.
     *
     * @param line   the line that the projection block with this code in resides
     *               on in the lib/gamedata file.
     * @param code   the string tag to convert to a {@link ProjectionEnum} value.
     * @param errors a list of errors to handle any further
     *               errors arising during the resolution process.
     * @return a {@link ProjectionEnum} value which is of the form
     * {@code "PROJ_" + code}
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    private ProjectionEnum resolveCode(int line, @NotNull String code,
                                       @NotNull List<String> errors) {
        ProjectionEnum result;
        try {
            result = ProjectionEnum.valueOf("PROJ_" + code);
            return result;
        } catch (IllegalArgumentException e) {
            errors.add("Line: " + line + ": Unknown projection code in this Projection block: " + code);
            return null;
        }
    }

    /**
     * Resolve an uppercase string tag into a {@link ProjectionType} value.
     *
     * @param line   the line that the projection block with this code in resides
     *               on in the lib/gamedata file.
     * @param type   the string tag to convert to a {@link ProjectionType} value.
     * @param code   the string tag of the projection block in the lib/gamedata file
     *               that this resolution occurs within.
     * @param errors a list of errors to handle any further
     *               errors arising during the resolution process.
     * @return a {@link ProjectionType} value which is of the form
     * {@code "PT_" + type}
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    private ProjectionType resolveType(int line, @NotNull String type, @NotNull String code,
                                       @NotNull List<String> errors) {
        ProjectionType result;
        try {
            result = ProjectionType.valueOf("PT_" + type.toUpperCase());
            return result;
        } catch (IllegalArgumentException e) {
            errors.add("Line: " + line + ": Unknown projection type in Projection block: " + code);
            return null;
        }
    }

    /**
     * Resolve an uppercase string tag into a {@link MessageType} value.
     *
     * @param line   the line that the projection block with this code in resides
     *               on in the lib/gamedata file.
     * @param msgt   the string tag to convert to a {@link MessageType} value.
     * @param code   the string tag of the projection block in the lib/gamedata file
     *               that this resolution occurs within.
     * @param errors a list of errors to handle any further
     *               errors arising during the resolution process.
     * @return a {@link MessageType} value which is of the form
     * {@code "MSG_" + msgt}
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    private MessageType resolveMsgt(int line, @NotNull String msgt, @NotNull String code,
                                    @NotNull List<String> errors) {
        MessageType result;
        try {
            result = MessageType.valueOf("MSG_" + msgt.toUpperCase());
            return result;
        } catch (IllegalArgumentException e) {
            errors.add("Unknown projection message type in Projection block: " + code +
                    " Starting at line: " + line);
            return null;
        }
    }

    /**
     * Resolve a potentially mixed case string tag into a
     * {@link ColourType} value.
     *
     * @param line   the line that the projection block with this code in resides
     *               on in the lib/gamedata file.
     * @param colour the string tag
     * @param code   the string tag of the projection block in the lib/gamedata file
     *               that this resolution occurs within.
     * @param errors a list of errors to handle any further
     *               errors arising during the resolution process.
     * @return a {@link ColourType} whose name matches the case-insensitive name
     * of a {@link ColourType}, or the case-sensitive single character colour
     * letter code, or {@code null} if neither match.
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    private ColourType resolveColour(int line, @NotNull String colour, @NotNull String code,
                                     @NotNull List<String> errors) {
        ColourType result;

        if (colour.length() == 1)
            result = ColourType.findColourType(colour.charAt(0));
        else
            result = ColourType.findColourType(colour.toLowerCase());

        if (result == null) {
            errors.add("Unknown projection colour in Projection block: " + code +
                    " starting on line " + line);
        }

        return result;
    }
}
