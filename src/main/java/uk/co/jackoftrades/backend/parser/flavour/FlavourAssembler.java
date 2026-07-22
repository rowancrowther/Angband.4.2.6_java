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

package uk.co.jackoftrades.backend.parser.flavour;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.objects.Flavour;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link FlavourParseRecord}s of one {@code kind:} block into
 * {@link Flavour} domain objects. It handles the per-line leg only — parsing the
 * index and resolving the colour — and dispatches on whether the record is a
 * {@code fixed:} line (non-null sval) or a plain {@code flavor:} line.
 *
 * <p>It deliberately does <em>not</em> resolve a fixed record's sval symbol: that
 * needs the block's tval, which lives on the enclosing {@link FlavourKindParseRecord},
 * so it is done one level up in {@link FlavourKindAssembler}. Any record whose
 * index is non-numeric or whose colour is unknown is dropped with a soft error
 * rather than aborting the block.
 *
 * @author Rowan Crowther
 */
public class FlavourAssembler implements Assembler<FlavourParseRecord, List<Flavour>> {
    /**
     * Assembles the flavours of one kind block.
     *
     * @param records the raw flavour/fixed records of a single {@code kind:} block
     * @param errors  the soft-error sink, appended to when a record is dropped
     * @return the assembled flavours (fixed ones still awaiting sval resolution)
     * @author Rowan Crowther
     */
    @Override
    public List<Flavour> assemble(@NotNull List<FlavourParseRecord> records, @NotNull List<String> errors) {
        List<Flavour> flavours = new ArrayList<>();

        for (FlavourParseRecord record : records) {
            int line = record.line();
            if (record.sVal() == null) {
                // Flavour
                int index = 0;
                try {
                    index = Integer.parseInt(record.index());
                } catch (NumberFormatException e) {
                    errors.add("Flavour at line: " + line + " has " +
                            "a malformed integer: " + record.index());
                    continue;
                }
                ColourType colour = ColourType.getColourType(record.attr());
                if (colour == null) {
                    errors.add("Flavour at line: " + line + " has " +
                            "an unknown colour: " + record.attr());
                    continue;
                }
                String text = record.text();
                flavours.add(new Flavour(text, colour, index));
            } else {
                // Fixed
                int index = 0;
                try {
                    index = Integer.parseInt(record.index());
                } catch (NumberFormatException e) {
                    errors.add("Fixed at line: " + line + " has " +
                            "a malformed integer: " + record.index());
                    continue;
                }
                ColourType colour = ColourType.getColourType(record.attr());
                if (colour == null) {
                    errors.add("Fixed at line: " + line + " has " +
                            "an unknown colour: " + record.attr());
                    continue;
                }
                String text = record.text();
                String sVal = record.sVal();
                flavours.add(new Flavour(text, sVal, colour, index));
            }
        }

        return flavours;
    }
}
