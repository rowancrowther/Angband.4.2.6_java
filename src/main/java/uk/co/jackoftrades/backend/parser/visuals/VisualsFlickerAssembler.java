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
     * Unknown colour codes cannot reach here: the grammar only lexes valid {@code COLOUR_CODE}
     * characters, so a bad code is a hard parse error upstream, not a soft error. Consequently this
     * assembler has no soft-error path and never appends to {@code errors}.
     *
     * @param records the parsed {@code flicker:} blocks
     * @param errors  the shared soft-error sink (unused here; see above)
     * @return a one-element list holding the assembled {@link FlickerTable}
     * @author Rowan Crowther
     */
    @Override
    public List<FlickerTable> assemble(@NotNull List<VisualsFlickerParseRecord> records, @NotNull List<String> errors) {
        Map<ColourType, ColourCycle> byAttr = new HashMap<>();

        for (VisualsFlickerParseRecord record : records) {
            List<ColourType> steps = new ArrayList<>();
            for (String colour : record.colours()) {
                steps.add(ColourType.getColourType(colour));
            }
            ColourType key = ColourType.getColourType(record.colourChar());
            ColourCycle value = new ColourCycle(record.colourChar(), steps);
            byAttr.put(key, value);
        }

        return List.of(new FlickerTable(byAttr));
    }
}
