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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler class - takes a list of UIEntryRendererParseRecords and converts them
 * to a List of UIEntryRenderers.
 *
 * @author Rowan Crowther
 */
public class UIEntryRendererAssembler
        implements Assembler<UIEntryRendererParseRecord, List<UIEntryRenderer>> {
    /**
     * Assemble the record from the thin DTO ParseRecords and return them in a list
     *
     * @param records List of UIEntryRendererParseRecord objects
     * @param errors  List of errors as string messages
     * @return result of assembling list of UIEntryRenderer objects
     */
    @Override
    public List<UIEntryRenderer> assemble(@NotNull List<UIEntryRendererParseRecord> records, @NotNull List<String> errors) {
        List<UIEntryRenderer> results = new ArrayList<>();
        for (UIEntryRendererParseRecord record : records) {
            int line = Integer.parseInt(record.lineNumber());

            String name = record.name();
            UIEntryRendererEnum code = null;
            String outputColours = null;
            String outputLabelColours = null;
            String outputSymbols = null;
            int nDigits = 1;
            UIEntryEnum sign = null;

            if (!record.code().isEmpty()) {
                try {
                    code = UIEntryRendererEnum.valueOf("UI_ENTRY_RENDERER_" + record.code());
                } catch (IllegalArgumentException e) {
                    errors.add("Block starting on line: " + line
                            + " has illegal code enum value: " + record.code());
                    continue;
                }

                int maxPalette = UIRegistry.MAX_PALETTE;

                String inputColours = record.colours();
                String defaultColours = code.getDefaultColours();
                if (inputColours.length() > maxPalette) inputColours = inputColours.substring(0, maxPalette);
                if (inputColours.length() > defaultColours.length()) outputColours = inputColours;
                else outputColours = inputColours + defaultColours.substring(inputColours.length());

                String inputLabelColours = record.labelColours();
                String defaultLabelColours = code.getDefaultLabelColours();
                if (inputLabelColours.length() > maxPalette)
                    inputLabelColours = inputLabelColours.substring(0, maxPalette);
                if (inputLabelColours.length() > defaultLabelColours.length()) outputLabelColours = inputLabelColours;
                else outputLabelColours = inputLabelColours + defaultLabelColours.substring(inputLabelColours.length());

                String inputSymbols = record.symbols();
                String defaultSymbols = code.getDefaultSymbols();
                if (inputSymbols.length() > maxPalette) inputSymbols = inputSymbols.substring(0, maxPalette);
                if (inputSymbols.length() > defaultSymbols.length()) outputSymbols = inputSymbols;
                else outputSymbols = inputSymbols + defaultSymbols.substring(inputSymbols.length());

                int parsedNDigits = record.nDigits().isEmpty() ? 1 : Integer.parseInt(record.nDigits());
                if (parsedNDigits < 1) {
                    errors.add("Block starting on line: " + line + " has " +
                            "a zero or negative nDigits value:" + record.nDigits());
                    continue;
                }

                nDigits = record.nDigits().isEmpty() ? code.getDefaultDigits() : Integer.parseInt(record.nDigits());

                if (record.sign().isEmpty())
                    sign = code.getEntry();
                else {
                    try {
                        sign = UIEntryEnum.valueOf("UI_ENTRY_" + record.sign());
                    } catch (IllegalArgumentException e) {
                        errors.add("Block starting on line: " + line
                                + " has illegal sign enum value: " + record.sign());
                        continue;
                    }
                }
            }

            // UIEntryRenderer old = results.stream().filter(r -> r.getName().equals(name)
            //        .findFirst().orElse(null);

            results.add(new UIEntryRenderer(name, code, outputColours, outputLabelColours, outputSymbols, nDigits, sign));
        }

        return results;
    }
}