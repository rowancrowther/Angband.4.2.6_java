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

package uk.co.jackoftradesltd.backend.parser.visuals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.frontend.colour.ColourCycle;
import uk.co.jackoftradesltd.frontend.colour.VisualsCycler;

import java.util.*;

/**
 * Folds the parsed {@code cycle:} blocks of {@code visuals.txt} into a single {@link VisualsCycler},
 * bucketed group-then-name. Each block's colour codes are resolved to {@link ColourEnum}s and stored
 * as a {@link ColourCycle} under its (group, name) key. This is the Java form of the cycle half of
 * C's visuals loader in {@code ui-visuals.c}: functions {@code visuals_parse_cycle()} (one
 * {@code cycle:} line), {@code visuals_parse_cycle_color()} (one {@code cycle-color:} line) and
 * {@code visuals_parse_context_convert()} (the copy into the final cycler, run from
 * {@code visuals_file_parser_finish()}).
 * <p>
 * <strong>List wrapping.</strong> As with the flicker assembler, the whole file collapses to one
 * aggregate but {@code GrammarDriver} only drives list-shaped assemblers, so the single cycler is
 * returned as a one-element list and the reader unwraps it with {@code items().getFirst()}.
 * <p>
 * <strong>C's capacity limits.</strong> C sizes its parse context from three constants, and the
 * port keeps all three:
 * <ul>
 *   <li>{@code VISUALS_STEPS_MAX} (32) colours per cycle - a 33rd {@code cycle-color:} line stops
 *       the load ({@code PARSE_ERROR_TOO_MANY_ENTRIES});</li>
 *   <li>{@code VISUALS_GROUPS_MAX} (8) groups - a ninth group name is silently dropped, with every
 *       cycle under it;</li>
 *   <li>{@code VISUALS_CYCLES_MAX} (64) names per group - a 65th new name is silently dropped,
 *       though a repeat of a name already present still replaces it.</li>
 * </ul>
 * C's cycle array has {@code 8 * 64 + 1} slots and starts at 1, so it holds 512 {@code cycle:}
 * lines, counting repeated and dropped ones; the 513th stops the load. Java counts records from 0
 * with no blank slot (the accepted base-0 deviation), so the same limit reads as "more than 512".
 * <p>
 * <strong>Fatal errors.</strong> Each throws a {@link DataFileParseException} after a fatal log line;
 * nothing is reported into {@code errors}. In order of checking: more than 512 records; an empty
 * colour string (C's {@code PARSE_ERROR_INVALID_COLOR}); more than 32 colours in one block; and,
 * after every record is placed, no group at all (C's {@code visuals_cycler_new()} returns NULL for
 * a zero group count) or a cycle with no colours still installed (C's
 * {@code visuals_color_cycle_copy()} gets NULL for a zero step count). Either of the last two makes
 * {@code visuals_file_parser_finish()} answer {@code PARSE_ERROR_INTERNAL}. The no-group error is
 * reachable from a real file, since the grammar accepts one made only of {@code flicker:} blocks.
 * The grammar admits only single valid colour codes and at least one {@code cycle-color:} per block,
 * so the empty-colour and empty-cycle errors are reachable only from a hand-built record.
 * <p>
 * <strong>Ordering.</strong> C keeps groups and cycles in file-order slots; the port uses
 * {@link HashMap}s with no order. Every lookup is by group and name, so nothing observes the
 * difference.
 *
 * <p>Class VisualsCycleAssembler coded before 260915, reworked to fatal errors and C's capacity
 * limits on 261030, commented in full on 261030.
 *
 * @author Rowan Crowther
 */
public class VisualsCycleAssembler implements Assembler<VisualsCycleParseRecord, List<VisualsCycler>> {
    /**
     * Logger for the fatal line this class writes before each {@link DataFileParseException} it
     * throws (see the class comment for the list). The line carries the same text as the exception,
     * so the cause is in the log even if a caller swallows the exception. C has no counterpart: it
     * reports through the parser's own error text.
     *
     * <p>Field logger coded before 260915, commented in full on 261030.
     */
    private static final Logger logger = LogManager.getLogger(VisualsCycleAssembler.class);

    /**
     * Build the cycler from every parsed block, resolving each colour code and bucketing by group
     * then name, in the same order of steps C takes line by line.
     * <p>
     * <strong>Record count.</strong> More than 512 records stops the load before any is read - C's
     * {@code visuals_parse_cycle()} refuses the 513th {@code cycle:} line. C would first have
     * refused an earlier block with 33 colours; both are fatal, so only the message differs.
     * <p>
     * <strong>Colours.</strong> Each code goes through {@link ColourEnum#fromCode(String)}, which for
     * a single character is C's {@code color_char_to_attr()}: a space is {@code COLOUR_DARK}, and a
     * code that matches nothing ({@code null}) becomes {@code COLOUR_WHITE}, C's "we assume white".
     * An empty string is fatal, as C's {@code strlen} check in {@code visuals_parse_cycle_color()};
     * so is a 33rd colour, which C refuses against the cycle's {@code max_steps} of 32.
     * <p>
     * <strong>Placement.</strong> As in C, the cycle is placed whether or not it has colours: an
     * empty block still claims its group (counting towards the eight) and still replaces an earlier
     * cycle of the same name, stored as the {@code null} that {@link ColourCycle#colourCycle} returns
     * for an empty list. A ninth group, or a 65th new name in a group, is skipped without error, the
     * way C's slot searches fall through when no slot is free.
     * <p>
     * <strong>No groups.</strong> Once every record is placed, an empty map stops the load - C's
     * {@code visuals_parse_context_convert()} counts the groups first and
     * {@code visuals_cycler_new()} refuses a count of 0. Only an empty {@code records} list gets
     * here, since the first eight groups are always placed.
     * <p>
     * <strong>Second pass.</strong> After that, any {@code null} cycle left in the
     * map stops the load - C's {@code visuals_parse_context_convert()} fails copying a zero-step
     * cycle. An empty cycle that a later same-named block replaced, or that the group or name limit
     * dropped, is never seen here, just as C never copies it.
     *
     * <p>Function assemble coded before 260915, reworked to fatal errors and C's capacity limits on
     * 261030, commented in full on 261030.
     *
     * @param records  the parsed {@code cycle:} blocks, in file order
     * @param errors   the shared soft-error sink; unused, since every error here is fatal
     * @param filename the data file's name, quoted in each error message
     * @return a one-element list holding the assembled {@link VisualsCycler}
     * @throws DataFileParseException for any of the fatal errors listed in the class comment
     */
    @Override
    public List<VisualsCycler> assemble(@NotNull List<VisualsCycleParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        Map<String, Map<String, ColourCycle>> byGroup = new HashMap<>();
        
        if (records.size() > 512) {
            String message = "Visuals cycler file: " + filename + " has " +
                    "too many total cycle lines: " + records.size();
            logger.fatal(message);
            throw new DataFileParseException(message);
        }

        for (VisualsCycleParseRecord record : records) {
            int line = record.line();
            String group = record.group();
            String name = record.name();
            List<ColourEnum> colours = new ArrayList<>();
            for (String colour : record.colours()) {
                if (!colour.isEmpty()) {
                    ColourEnum colourType = ColourEnum.fromCode(colour);
                    if (colourType == null) {
                        colours.add(ColourEnum.COLOUR_WHITE);
                    } else {
                        colours.add(colourType);
                    }
                } else {
                    String message = "Visuals cycler starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal empty visual cycler string value";
                    logger.fatal(message);
                    throw new DataFileParseException(message);                    
                }
                
                if (colours.size() > 32) {
                    String message = "Visuals cycler starting at line: " + line + " of file: " + filename + " has " +
                            "an illegal number of entries: " + colours.size();
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
            ColourCycle entry = ColourCycle.colourCycle(name, colours);
            
            if (byGroup.size() == 8 && !byGroup.containsKey(group)) 
                continue;
            
            Map<String, ColourCycle> groups = new HashMap<>();
            
            if (byGroup.containsKey(group))
                groups = byGroup.get(group);
            
            Set<String> keys = groups.keySet();
            if (!keys.contains(name) && keys.size() == 64)
                continue;
            
            groups.put(name, entry);
            
            byGroup.put(group, groups);
        }
        
        if (byGroup.isEmpty()) {
            String message = "Visuals cycler of file: " + filename +
                    " is empty";
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        
        // Second pass - make sure all the visual cycle entries are not null
        for (Map<String, ColourCycle> entries : byGroup.values()) {
            for (String entry : entries.keySet()) {
                if (entries.get(entry) == null) {
                    String message = "Visuals cycler " + entry + " of file: " + filename +
                            " is null";
                    logger.fatal(message);
                    throw new DataFileParseException(message);
                }
            }
        }

        return List.of(new VisualsCycler(byGroup));
    }
}
