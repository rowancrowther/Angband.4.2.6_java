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
import uk.co.jackoftrades.frontend.colour.VisualsCycler;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Folds the parsed {@code cycle:} blocks into a single {@link VisualsCycler}, bucketed group-then-name.
 * Each block's colour codes are resolved to {@link ColourType}s and stored as a {@link ColourCycle}
 * under its (group, name) key. Assembles the domain half of the C {@code visuals_cycler} loader
 * ({@code ui-visuals.c}).
 * <p>
 * <strong>List wrapping.</strong> As with the flicker assembler, the whole file collapses to one
 * aggregate but {@code GrammarDriver} only drives list-shaped assemblers, so the single cycler is
 * returned as a one-element list and the reader unwraps it with {@code items().getFirst()}.
 * <p>
 * <strong>Soft errors.</strong> A record with a malformed colour is reported into {@code errors} and
 * skipped whole (its group/name is never installed), leaving the rest to assemble - the
 * partial-results contract. In practice this branch is nearly unreachable from real data: see
 * {@link #assemble} for why.
 *
 * @author Rowan Crowther
 */
public class VisualsCycleAssembler implements Assembler<VisualsCycleParseRecord, List<VisualsCycler>> {
    /**
     * Build the cycler from every parsed block, resolving each colour code and bucketing by group
     * then name.
     * <p>
     * <strong>Reachability of the error paths.</strong> The grammar only lexes valid
     * {@code COLOUR_CODE} characters, so neither soft-error branch fires on real file input: an empty
     * colour string cannot be produced, and an unrecognised code is a hard parse error upstream. The
     * empty-colour guard is therefore defensive (reachable only by a hand-built record), and the
     * {@code catch} around {@link ColourType#findColourType(char)} is effectively dead because that
     * method returns {@code COLOUR_TYPE_DARK} on a miss rather than throwing - an unknown code is
     * silently mapped to Dark, not flagged.
     *
     * @param records the parsed {@code cycle:} blocks
     * @param errors  the shared soft-error sink; a malformed colour appends here and drops its record
     * @return a one-element list holding the assembled {@link VisualsCycler}
     * @author Rowan Crowther
     */
    @Override
    public List<VisualsCycler> assemble(@NotNull List<VisualsCycleParseRecord> records, @NotNull List<String> errors) {
        Map<String, Map<String, ColourCycle>> byGroup = new HashMap<>();

        for (VisualsCycleParseRecord record : records) {
            int line = record.line();
            String group = record.group();
            String name = record.name();
            List<ColourType> colours = new ArrayList<>();
            boolean illegalColour = false;
            for (String colour : record.colours()) {
                if (!colour.isEmpty()) {
                    try {
                        colours.add(ColourType.findColourType(colour.charAt(0)));
                    } catch (IllegalArgumentException e) {
                        errors.add("Visuals at line: " + line + " has " +
                                "an invalid colour: " + colour + " in group: " + group +
                                " and name: " + name);
                        illegalColour = true;
                    }
                } else {
                    errors.add("Visuals at line: " + line + " has an invalid colour");
                    illegalColour = true;
                }
            }
            if (illegalColour) continue;
            byGroup.computeIfAbsent(group, g -> new HashMap<>())
                    .put(name, new ColourCycle(name, colours));
        }

        return List.of(new VisualsCycler(byGroup));
    }
}
