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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.parser.grammars.projection.Projections;
import uk.co.jackoftrades.backend.parser.grammars.projection.ProjectionsLexer;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.combat.enums.ProjectionType;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.Projection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link Projection} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author ClaudeCode
 */
public class ProjectionReader implements Reader<Projection> {
    /**
     * Logger used to report file-loading failures
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     * @throws IOException if there is an issue with creating the CharStream
     * @author Rowan Crowther
     */
    @NotNull
    @Contract("_ -> !null")
    @Override
    public List<Projection> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Run the parser and generate the ArrayList from the file
     * logging all errors that occur during the run. Once the parse
     * has been complete, change the incoming values to values
     * acceptable to the data format of the stored values
     *
     * @param filename The name of the file to parse
     * @return A {@link ParseResult} of type {@link Projection}
     * @throws IOException when there is a problem finding or reading
     *                     the file
     * @author Rowan Crowther
     */
    public ParseResult<Projection> parseWithResults(@NotNull String filename) throws IOException {
        List<List<String>> projectionRecords;
        int recordCount = 0;

        ParseErrors errorCatcher = null;

        List<Projection> projections = new ArrayList<>();

        try {
            CharStream input = CharStreams.fromFileName(filename);
            ProjectionsLexer lexer = new ProjectionsLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Projections parser = new Projections(tokens);

            // install the error catcher
            errorCatcher = ParseErrors.install(lexer, parser, filename);

            Projections.FileContext output = parser.file();
            projectionRecords = output.projections;

            errorCatcher.throwIfAny();

            try {
                recordCount = Integer.parseInt(output.records);
            } catch (NumberFormatException e) {
                errorCatcher.add("Invalid number format for declared record count");
                recordCount = -1;
            }

            if (recordCount != -1 && recordCount != projectionRecords.size()) {
                errorCatcher.add("record-count header declares " + recordCount +
                        " but file contains " + projectionRecords.size());
            }

            for (List<String> record : projectionRecords) {
                int line;
                try {
                    line = Integer.parseInt(record.getLast());
                } catch (NumberFormatException e) {
                    errorCatcher.add("Invalid number format for last entry in records List. " +
                            "Record code: " + record.get(0) + ". Line number: " +
                            record.getLast());
                    line = -1;
                }

                String codeString = record.get(0);
                ProjectionEnum projCode = resolveCode(line, codeString, errorCatcher);
                String projName = record.get(1);
                String type = record.get(2);
                ProjectionType projType = resolveType(line, type, codeString, errorCatcher);
                String projDesc = record.get(3);
                String projPlayerDesc = record.get(4);
                String projBlindDesc = record.get(5);
                String projLashDesc = record.get(6);
                int projNumerator = -1;
                try {
                    projNumerator = Integer.parseInt(record.get(7));
                } catch (NumberFormatException e) {
                    projNumerator = -1;
                }
                int projDenominator = -1;
                Random projDenominatorDice = null;
                try {
                    projDenominator = Integer.parseInt(record.get(8));
                } catch (NumberFormatException e) {
                    projDenominatorDice = Random.parseStr(record.get(8));
                }
                int projDivisor = -1;
                try {
                    projDivisor = Integer.parseInt(record.get(9));
                } catch (NumberFormatException e) {
                    projDivisor = -1;
                }
                int projDamageCap = -1;
                try {
                    projDamageCap = Integer.parseInt(record.get(10));
                } catch (NumberFormatException e) {
                    projDamageCap = -1;
                }
                String msgt = record.get(11);
                MessageType projMsgt = msgt.isEmpty() ? null : resolveMsgt(line, msgt, codeString, errorCatcher);
                boolean projObvious = record.get(12).equals("1");
                boolean projWillWake = record.get(13).equals("1");
                String colour = record.get(14);
                ColourType projColour = resolveColour(line, colour, codeString, errorCatcher);
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

                projections.add(proj);
            }
            errorCatcher.throwIfAny();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        } catch (ParseCancellationException e) {
            return new ParseResult<>(List.of(), errorCatcher.getErrors());
        }

        return new ParseResult<>(projections, errorCatcher.getErrors());
    }

    /**
     * Resolve an uppercase string tag into a {@link ProjectionEnum} value.
     *
     * @param line         the line that the projection block with this code in resides
     *                     on in the lib/gamedata file.
     * @param code         the string tag to convert to a {@link ProjectionEnum} value.
     * @param errorCatcher a {@link ParseErrors} catcher to handle any
     *                     errors arising during the resolution process.
     * @return a {@link ProjectionEnum} value which is of the form
     * {@code "PROJ_" + code}
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    private ProjectionEnum resolveCode(int line, @NotNull String code,
                                       @NotNull ParseErrors errorCatcher) {
        ProjectionEnum result;
        try {
            result = ProjectionEnum.valueOf("PROJ_" + code);
            return result;
        } catch (IllegalArgumentException e) {
            errorCatcher.add("Line: " + line + ": Unknown projection code in this Projection block: " + code);
            return null;
        }
    }

    /**
     * Resolve an uppercase string tag into a {@link ProjectionType} value.
     *
     * @param line         the line that the projection block with this code in resides
     *                     on in the lib/gamedata file.
     * @param type         the string tag to convert to a {@link ProjectionType} value.
     * @param code         the string tag of the projection block in the lib/gamedata file
     *                     that this resolution occurs within.
     * @param errorCatcher a {@link ParseErrors} catcher to handle any
     *                     errors arising during the resolution process.
     * @return a {@link ProjectionType} value which is of the form
     * {@code "PT_" + type}
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    private ProjectionType resolveType(int line, @NotNull String type, @NotNull String code,
                                       @NotNull ParseErrors errorCatcher) {
        ProjectionType result;
        try {
            result = ProjectionType.valueOf("PT_" + type.toUpperCase());
            return result;
        } catch (IllegalArgumentException e) {
            errorCatcher.add("Line: " + line + ": Unknown projection type in Projection block: " + code);
            return null;
        }
    }

    /**
     * Resolve an uppercase string tag into a {@link MessageType} value.
     *
     * @param line         the line that the projection block with this code in resides
     *                     on in the lib/gamedata file.
     * @param msgt         the string tag to convert to a {@link MessageType} value.
     * @param code         the string tag of the projection block in the lib/gamedata file
     *                     that this resolution occurs within.
     * @param errorCatcher a {@link ParseErrors} catcher to handle any
     *                     errors arising during the resolution process.
     * @return a {@link MessageType} value which is of the form
     * {@code "MSG_" + msgt}
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    private MessageType resolveMsgt(int line, @NotNull String msgt, @NotNull String code,
                                    @NotNull ParseErrors errorCatcher) {
        MessageType result;
        try {
            result = MessageType.valueOf("MSG_" + msgt.toUpperCase());
            return result;
        } catch (IllegalArgumentException e) {
            errorCatcher.add("Unknown projection message type in Projection block: " + code +
                    " Starting at line: " + line);
            return null;
        }
    }

    /**
     * Resolve a potentially mixed case string tag into a
     * {@link ColourType} value.
     *
     * @param line         the line that the projection block with this code in resides
     *                     on in the lib/gamedata file.
     * @param colour       the string tag
     * @param code         the string tag of the projection block in the lib/gamedata file
     *                     that this resolution occurs within.
     * @param errorCatcher a {@link ParseErrors} catcher to handle any
     *                     errors arising during the resolution process.
     * @return a {@link ColourType} whose name matches the case-insensitive name
     * of a {@link ColourType}, or the case-sensitive single character colour
     * letter code, or {@code null} if neither match.
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    private ColourType resolveColour(int line, @NotNull String colour, @NotNull String code,
                                     @NotNull ParseErrors errorCatcher) {
        ColourType result;

        if (colour.length() == 1)
            result = ColourType.findColourType(colour.charAt(0));
        else
            result = ColourType.findColourType(colour.toLowerCase());

        if (result == null) {
            errorCatcher.add("Unknown projection colour in Projection block: " + code +
                    " starting on line " + line);
        }

        return result;
    }
}
