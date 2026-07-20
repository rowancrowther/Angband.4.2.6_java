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

package uk.co.jackoftrades.backend.parser.visuals;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.frontend.colour.ColourCycle;
import uk.co.jackoftrades.frontend.colour.FlickerTable;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Folds the parsed {@code flicker:} blocks into a single {@link FlickerTable}. Each block's base
 * attribute (its {@code flicker:} header code) becomes a map key, and its {@code flicker-color:}
 * steps become the {@link ColourCycle} shown while a monster of that base colour flickers. Assembles
 * the domain half of the C {@code visuals_flicker} loader ({@code ui-visuals.c}).
 * <p>
 * <strong>List wrapping.</strong> The whole file collapses to one aggregate, but
 * {@code GrammarDriver} only drives list-shaped assemblers ({@code Assembler<R, List<T>>}), so the
 * single table is returned as a one-element list and the reader unwraps it with
 * {@code items().getFirst()}.
 * <p>
 * <strong>Base attribute vs. steps.</strong> {@code record.colourChar()} is the key (the base
 * colour), whereas {@code record.colours()} are the animation frames - two different colour axes off
 * the same record. Colliding base attributes overwrite (last block wins), matching a map put.
 *
 * @author Rowan Crowther
 */
public class VisualsFlickerAssembler implements Assembler<VisualsFlickerParseRecord, List<FlickerTable>> {
    /**
     * Build the flicker table from every parsed block.
     * <p>
     * Each step colour and the base-attribute key are resolved through
     * {@link ColourType#getColourType}, which returns {@code null} on an unrecognised code; such a
     * record is flagged into {@code errors} and skipped. In practice the grammar only lexes valid
     * {@code COLOUR_CODE} characters, so this soft-error path is defensive - unreachable from a real,
     * grammar-checked file - but it keeps the assembler safe against a hand-built record.
     *
     * @param records the parsed {@code flicker:} blocks
     * @param errors  the soft-error sink; an unrecognised colour appends here and drops its record
     * @return a one-element list holding the assembled {@link FlickerTable}
     * @author Rowan Crowther
     */
    @Override
    public List<FlickerTable> assemble(@NotNull List<VisualsFlickerParseRecord> records, @NotNull List<String> errors) {
        Map<ColourType, ColourCycle> byAttr = new HashMap<>();

        for (VisualsFlickerParseRecord record : records) {
            List<ColourType> steps = new ArrayList<>();
            boolean illegalColour = false;
            for (String colour : record.colours()) {
                ColourType colourType = ColourType.getColourType(colour);
                if (colourType == null) {
                    errors.add("Flicker table at line: " + record.line() + " has " +
                            "an illegal colour: " + colour + " name: " +
                            record.name());
                    illegalColour = true;
                } else {
                    steps.add(ColourType.getColourType(colour));
                }
            }
            if (illegalColour) continue;
            ColourType key = ColourType.getColourType(record.colourChar());
            if (key == null) {
                errors.add("Flicker table at line: " + record.line() + " has " +
                        "an illegal colour: " + record.colourChar());
                continue;
            }
            ColourCycle value = new ColourCycle(record.colourChar(), steps);
            byAttr.put(key, value);
        }

        return List.of(new FlickerTable(byAttr));
    }
}
