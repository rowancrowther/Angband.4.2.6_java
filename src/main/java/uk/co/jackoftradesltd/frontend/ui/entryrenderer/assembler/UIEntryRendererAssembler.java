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

package uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/**
 * Second stage of the {@code ui_entry_renderer.txt} pipeline: turns the raw string
 * {@link UIEntryRendererParseRecord}s from the grammar into resolved {@link UIEntryRenderer}s, one
 * per renderer name, in the order each name first appears.
 * <p>
 * In C the work is split between the {@code parse_renderer_*} callbacks and
 * {@code finish_parse_ui_entry_renderer} in {@code ui-entry-renderers.c}. The callbacks write each
 * directive into a shared {@code struct renderer_info}, found by name in
 * {@code parse_renderer_name}, so a second {@code name:} block with the same name edits the first
 * record in place. The finishing pass then fills the gaps from the record's backend: colours, label
 * colours and symbols shorter than the backend defaults are padded with the rest of those defaults
 * ({@code augment_colors}, {@code augment_symbols}), and an unset {@code ndigit} or {@code sign}
 * takes the backend's default.
 * <p>
 * This port does both steps for each record as it arrives. To get the same result as C's single
 * finishing pass over the merged record, each {@link UIEntryRenderer} keeps the values as given
 * (the {@code raw} fields, which play the part of C's unfinished struct) beside the resolved ones.
 * A later block with the same name starts from the earlier block's raw values, not its resolved
 * ones, so a block that changes the {@code code:} is padded and defaulted from the new backend, as
 * C would pad it. The new {@link UIEntryRenderer} replaces the earlier one at the earlier one's
 * position in the list.
 * <p>
 * A renderer with no {@code code:} line has no backend in C ({@code backend} is {@code NULL}) and
 * the finishing pass skips it. Here it carries {@link UIEntryRendererEnum#UI_ENTRY_RENDERER_NONE},
 * and its fields are left as given: no padding, an empty {@code nDigit} (C leaves
 * {@code ndigit} at {@code INT_MIN}) and a sign of {@link UIEntryEnum#UI_ENTRY_SIGN_DEFAULT}.
 * <p>
 * Assembly is fail-fast, as a failed {@code parse_renderer_*} callback ends {@code parse_file} in C:
 * an unknown {@code code:}, a {@code code:} of {@code NONE}, an {@code ndigit:} below 1, or a
 * {@code sign:} other than {@code NO_SIGN}, {@code ALWAYS_SIGN} or {@code NEGATIVE_SIGN} ends the
 * load with a {@link DataFileParseException}.
 * <p>
 * C's {@code combine:}, {@code units:} and {@code combined-renderer:} directives are not carried by
 * {@link UIEntryRendererParseRecord}, so they are not assembled here. The shipped
 * {@code ui_entry_renderer.txt} uses none of them.
 *
 * <p>Class UIEntryRendererAssembler coded before 261104, commented in full on 261104.
 *
 * @author Rowan Crowther
 */
public class UIEntryRendererAssembler
        implements Assembler<UIEntryRendererParseRecord, List<UIEntryRenderer>> {
    /**
     * Receives the fatal message before each {@link DataFileParseException} is thrown.
     *
     * <p>Field logger coded before 261104, commented in full on 261104.
     */
    private static final Logger logger = LogManager.getLogger(UIEntryRendererAssembler.class);

    /**
     * Resolves the parse records into renderers, merging records that share a name. This is the
     * Java form of the {@code parse_renderer_*} callbacks together with
     * {@code finish_parse_ui_entry_renderer} in {@code ui-entry-renderers.c}.
     * <p>
     * For each record, in file order:
     * <ul>
     *   <li>A name not seen before is added at the end of the list, as {@code parse_renderer_name}
     *       appends a new {@code renderer_info}. A name already seen replaces the earlier renderer at
     *       its position, starting from the earlier renderer's raw values.</li>
     *   <li>{@code code:} names a backend by its {@link UIEntryRendererEnum} suffix, matched
     *       case-sensitively as {@code lookup_backend_by_name} does with {@code streq}. With no
     *       {@code code:} the earlier block's code is kept, or {@code NONE} for a new name.</li>
     *   <li>{@code colors:}, {@code labelcolors:} and {@code symbols:} are cut to
     *       {@link UIRegistry#MAX_PALETTE} (64) characters, as the matching {@code parse_renderer_*}
     *       callbacks cut them, then padded from the backend defaults by {@link #padString}. With none
     *       given, the earlier block's raw value is padded again from the code in effect.</li>
     *   <li>{@code ndigit:} is read in base 0 as C's {@code strtol} reads an {@code int} field, and
     *       a value below 1 is refused as in {@code parse_renderer_ndigit}. With none given here or
     *       earlier, the backend's default is used, or an empty value when there is no backend.</li>
     *   <li>{@code sign:} must be {@code NO_SIGN}, {@code ALWAYS_SIGN} or {@code NEGATIVE_SIGN}, as
     *       in {@code parse_renderer_sign}. {@code SIGN_DEFAULT} is the "not set" marker and is
     *       refused in a file. With none given here or earlier, the backend's
     *       {@link UIEntryRendererEnum#getEntry()} is used, or {@code SIGN_DEFAULT} when there
     *       is no backend.</li>
     * </ul>
     * Symbols are measured in Java {@code char}s where C's {@code text_mbstowcs} counts wide
     * characters. The two agree for the ASCII symbols in the shipped file.
     *
     * <p>Function assemble coded before 261104 (list placement and sign defaults fixed on 261104),
     * commented in full on 261104.
     *
     * @param records  the parse records from {@code ui_entry_renderer.txt}, in file order
     * @param errors   the shared error list; this assembler fails fast and never adds to it
     * @param filename the data file name, quoted in error messages
     * @return the resolved renderers, one per distinct name, in order of first appearance
     * @throws DataFileParseException if a record has an unknown or {@code NONE} code, an
     *                                {@code ndigit} below 1 or not a number, or a sign that is not
     *                                one of the three file values
     */
    @Override
    public List<UIEntryRenderer> assemble(@NotNull List<UIEntryRendererParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<UIEntryRenderer> results = new ArrayList<>();
        for (UIEntryRendererParseRecord record : records) {
            int line = Integer.parseInt(record.lineNumber());

            String name = record.name();

            UIEntryRenderer previous = results.stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
            UIEntryRenderer newRenderer = new UIEntryRenderer(name, null, null, null, null, OptionalInt.empty(), null);

            UIEntryRendererEnum code = null;
            String outputColours = null;
            String outputLabelColours = null;
            String outputSymbols = null;
            OptionalInt nDigits;
            UIEntryEnum sign = null;

            if (!record.code().isEmpty()) {
                try {
                    code = UIEntryRendererEnum.valueOf("UI_ENTRY_RENDERER_" + record.code());
                    if (code == UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE) {
                        String message = "UI entry renderer starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel code value: " + record.code();
                        logger.fatal(message);
                        throw new DataFileParseException(message);
                    }
                } catch (IllegalArgumentException e) {
                    String message = "UI entry renderer starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal code value: " + record.code();
                    logger.fatal(message, e);
                    throw new DataFileParseException(message);
                }

                newRenderer.setCode(code);
            } else {
                if (previous != null)
                    newRenderer.setCode(previous.getCode());
                else
                    newRenderer.setCode(UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE);
            }

            int maxPalette = UIRegistry.MAX_PALETTE;

            String inputColours = record.colours();
            String colours;
            String defaultColours = code == null ? "" : code.getDefaultColours();
            String rawColours;

            outputColours = padString(inputColours, defaultColours);

            if (inputColours.isEmpty()) {
                if (previous == null) {
                    colours = outputColours;
                    rawColours = "";
                } else {
                    rawColours = previous.getRawColours();
                    if (code == null) {
                        colours = previous.getColours();
                    } else {
                        colours = padString(previous.getRawColours(), defaultColours);
                    }
                }
            } else {
                rawColours = inputColours;
                if (rawColours.length() > maxPalette)
                    rawColours = rawColours.substring(0, maxPalette);
                colours = padString(rawColours, newRenderer.getCode().getDefaultColours());
            }

            newRenderer.setColours(colours);
            newRenderer.setRawColours(rawColours);

            String inputLabelColours = record.labelColours();
            String labelColours;
            String defaultLabelColours = code == null ? "" : code.getDefaultLabelColours();
            String rawLabelColours;
            
            outputLabelColours = padString(inputLabelColours, defaultLabelColours);

            if (inputLabelColours.isEmpty()) {
                if (previous == null) {
                    labelColours = outputLabelColours;
                    rawLabelColours = "";
                } else {
                    rawLabelColours = previous.getRawLabelColours();
                    if (code == null) {
                        labelColours = previous.getLabelColours();
                    } else {
                        labelColours = padString(previous.getRawLabelColours(), defaultLabelColours);
                    }
                }
            } else {
                rawLabelColours = inputLabelColours;
                if (rawLabelColours.length() > maxPalette)
                    rawLabelColours = rawLabelColours.substring(0, maxPalette);
                labelColours = padString(rawLabelColours, newRenderer.getCode().getDefaultLabelColours());
            }

            newRenderer.setLabelColours(labelColours);
            newRenderer.setRawLabelColours(rawLabelColours);

            String inputSymbols = record.symbols();
            String symbols;
            String defaultSymbols = code == null ? "" : code.getDefaultSymbols();
            String rawSymbols;
    
            outputSymbols = padString(inputSymbols, defaultSymbols);

            if (inputSymbols.isEmpty()) {
                if (previous == null) {
                    symbols = outputSymbols;
                    rawSymbols = "";
                } else {
                    rawSymbols = previous.getRawSymbols();
                    if (code == null) {
                        symbols = previous.getSymbols();
                    } else {
                        symbols = padString(previous.getRawSymbols(), defaultSymbols);
                    }
                }
            } else {
                rawSymbols = inputSymbols;
                if (rawSymbols.length() > maxPalette)
                    rawSymbols = rawSymbols.substring(0, maxPalette);
                symbols = padString(rawSymbols, newRenderer.getCode().getDefaultSymbols());
            }
            
            newRenderer.setSymbols(symbols);
            newRenderer.setRawSymbols(rawSymbols);
            
            OptionalInt rawNDigits;

            if (record.nDigits().isEmpty()) {
                if (previous == null) {
                    rawNDigits = OptionalInt.empty();
                } else {
                    rawNDigits = previous.getRawNDigit();
                }
            } else {
                String toParse = record.nDigits();
                int parsedValue = ParseUtils.parseIntString("UI entry renderer", toParse, line, filename, "number of digits");
                if (parsedValue < 1) {
                    String message = "UI entry renderer starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal number of digits value: " + record.nDigits();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }

                rawNDigits = OptionalInt.of(parsedValue);
            }
            
            if (rawNDigits.isPresent()) {
                nDigits = OptionalInt.of(rawNDigits.getAsInt());
            } else {
                UIEntryRendererEnum currentCode = newRenderer.getCode();
                if (currentCode == UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE)
                    nDigits = OptionalInt.empty();
                else 
                    nDigits = OptionalInt.of(currentCode.getDefaultDigits());
            }
            
            newRenderer.setnDigit(nDigits);
            newRenderer.setRawNDigit(rawNDigits);

            UIEntryEnum rawSign;
            
            if (record.sign().isEmpty()) {
                if (previous == null) {
                    rawSign = UIEntryEnum.UI_ENTRY_SIGN_DEFAULT;
                } else {
                    rawSign = previous.getRawSign();
                }
            } else {
                String signString = record.sign();
                try {
                    sign = UIEntryEnum.valueOf("UI_ENTRY_" + signString);
                    if (sign == UIEntryEnum.UI_ENTRY_SIGN_DEFAULT) {
                        String message = "UI entry renderer starting at line: " + line + " of file: " + filename + " has " +
                                "an illegal sentinel sign value: " + record.sign();
                        logger.fatal(message);
                        throw new DataFileParseException(message);    
                    }
                } catch (IllegalArgumentException e) {
                    String message = "UI entry renderer starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal sign value: " + record.sign();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
                
                rawSign = sign;
            }
            
            UIEntryRendererEnum currentCode = newRenderer.getCode();
            
            if (rawSign != UIEntryEnum.UI_ENTRY_SIGN_DEFAULT) {
                sign = rawSign;
            } else {
                if (currentCode == UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE) {
                    sign = UIEntryEnum.UI_ENTRY_SIGN_DEFAULT;
                } else {
                    sign = currentCode.getEntry();
                }
            }
            
            newRenderer.setSign(sign);
            newRenderer.setRawSign(rawSign);
            
            int index = results.size();
            
            if (previous != null) {
                index = results.indexOf(previous);
                results.remove(previous);
            }

            results.add(index, newRenderer);
        }

        return results;
    }
    
    /**
     * Pads a palette with the rest of a backend default. This is the Java form of
     * {@code augment_colors} and {@code augment_symbols} in {@code ui-entry-renderers.c}: when the
     * given palette is shorter than the default, the default's characters from the given length
     * onwards are appended. A palette as long as the default or longer is returned unchanged.
     * Text longer than {@link UIRegistry#MAX_PALETTE} (64) is cut to that length first, as the
     * {@code parse_renderer_*} callbacks cut it.
     * <p>
     * An empty {@code start} returns the whole default, and an empty {@code end} (no backend)
     * returns {@code start} as given.
     *
     * <p>Function padString coded before 261104, commented in full on 261104.
     *
     * @param start the palette as given in the file, possibly empty
     * @param end   the backend's default palette
     * @return {@code start} padded from {@code end}, at most 64 characters long
     */
    private String padString(String start, String end) {
        int maxPalette = UIRegistry.MAX_PALETTE;
        String result;
        
        if (start.length() > maxPalette) return start.substring(0, maxPalette);
        if (start.length() > end.length()) return start;
        
        result = start + end.substring(start.length());
        return result;
    }
}