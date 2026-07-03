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

package uk.co.jackoftrades.backend.parser.uientryrenderer;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler class - takes a list of UIEntryRendererParseRecords and converts them
 * to a List of UIEntryRenderers.
 */
public class UIEntryRendererAssembler implements Assembler<UIEntryRendererParseRecord, List<UIEntryRenderer>> {
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

            UIEntryRendererEnum code;
            try {
                code = UIEntryRendererEnum.valueOf("UI_ENTRY_RENDERER_" + record.code());
            } catch (IllegalArgumentException e) {
                errors.add("Block starting on line: " + record.lineNumber()
                        + " has illegal code enum value: " + record.code());
                continue;
            }

            String name = record.name();
            String colours = record.colours().isEmpty() ? code.getDefaultColours() : record.colours();
            String labelColours = record.labelColours().isEmpty() ? code.getDefaultLabelColours() : record.labelColours();
            String symbols = record.symbols().isEmpty() ? code.getDefaultSymbols() : record.symbols();
            int nDigits = record.nDigits().isEmpty() ? code.getDefaultDigits() : Integer.parseInt(record.nDigits());
            UIEntryEnum sign;
            if (record.sign().isEmpty())
                sign = code.getEntry();
            else {
                try {
                    sign = UIEntryEnum.valueOf("UI_ENTRY_" + record.sign());
                } catch (IllegalArgumentException e) {
                    errors.add("Block starting on line: " + record.lineNumber()
                            + " has illegal sign enum value: " + record.sign());
                    continue;
                }
            }

            results.add(new UIEntryRenderer(name, code, colours, labelColours, symbols, nDigits, sign));
        }

        return results;
    }
}
