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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.frontend.colour.ColourCycle;
import uk.co.jackoftradesltd.frontend.colour.FlickerTable;

import java.util.*;

/**
 * Folds the parsed {@code flicker:} blocks into a single {@link FlickerTable}. Each block's base
 * attribute (its {@code flicker:} header code) becomes a map key, and its {@code flicker-color:}
 * steps become the {@link ColourCycle} shown while a monster of that base colour flickers. Java
 * port of the load half of C's legacy flicker table ({@code ui-visuals.c}): the row handlers
 * {@code visuals_parse_flicker()} and {@code visuals_parse_flicker_color()}, and the table writes
 * they make through {@code visuals_flicker_set_color()}.
 * <p>
 * <strong>Fixed width of three.</strong> {@code ui_visuals_module_init()} builds C's table with
 * {@code colors_per_cycle} of 3, and every cycle is read back with {@code frame % 3}. So every cycle
 * here holds exactly three steps: a fourth or later {@code flicker-color:} is dropped, as
 * {@code visuals_flicker_set_color()} ignores an out-of-range index, and a short block is padded
 * with {@link ColourEnum#COLOUR_DARK}, the zero C's {@code mem_zalloc} leaves in an unwritten slot.
 * This is the one place a {@link ColourCycle} does not hold only its declared steps.
 * <p>
 * <strong>Repeated base attribute.</strong> C's {@code visuals_parse_flicker()} resets only the
 * step index, so a second block for the same base colour writes over the earlier block step by step
 * from step 0, and any step it does not reach keeps the earlier value: {@code r,o,y} then {@code g}
 * gives {@code [g,o,y]}. This assembler does the same; it is not a last-block-wins replace.
 * <p>
 * <strong>List wrapping.</strong> The whole file collapses to one aggregate, but
 * {@code GrammarDriver} only drives list-shaped assemblers ({@code Assembler<R, List<T>>}), so the
 * single table is returned as a one-element list and the reader unwraps it with
 * {@code items().getFirst()}.
 * <p>
 * <strong>Base attribute vs. steps.</strong> {@code record.colourChar()} is the key (the base
 * colour), whereas {@code record.colours()} are the animation frames - two different colour axes off
 * the same record. The {@code name} on the {@code flicker:} line is ignored, as in C, which never
 * reads it.
 * <p>
 * <strong>Colour text arrives raw.</strong> {@code VisualsLexer} reads every colour field, the
 * {@code flicker:} header's and each {@code flicker-color:} step's, as a {@code COLOUR_LINE}: any
 * run of characters up to a colon or the end of the line. The {@code Colours} fragments are not
 * applied to it, so the grammar never checks that the text is a colour code. Despite the name
 * {@code colourChar}, the record holds that whole run, not one character, and the colour is decided
 * here, from its first character only.
 *
 * <p>Class VisualsFlickerAssembler coded before 2026-09-15, commented in full on 2026-10-30.
 *
 * @author Rowan Crowther
 */
public class VisualsFlickerAssembler implements Assembler<VisualsFlickerParseRecord, List<FlickerTable>> {
    /**
     * Build the flicker table from every parsed block, in file order. Java port of the table writes
     * made by C's {@code visuals_parse_flicker()} and {@code visuals_parse_flicker_color()}
     * ({@code ui-visuals.c}).
     * <p>
     * <strong>Colours.</strong> For each block, the base-attribute key and every step colour go
     * through {@link ColourEnum#fromCode(char)} on the <em>first character</em> of the text only, as
     * C's two handlers pass just {@code color_code[0]} to {@code color_char_to_attr()}
     * ({@code z-color.c}). So {@code Red} is {@code R} (light red, not the colour named Red),
     * {@code Mauve} is {@code M} (mustard), and {@code r } with a trailing blank is {@code r}. A
     * character that matches no colour becomes {@link ColourEnum#COLOUR_WHITE}, C's "we assume
     * white", and a space is {@link ColourEnum#COLOUR_DARK}.
     * <p>
     * <strong>Reachable from a file.</strong> {@code COLOUR_LINE} in {@code VisualsLexer.g4} accepts
     * any value up to the colon or the end of the line, spaces included, so the white fallback is
     * reachable from a real file ({@code flicker-color:q}) and so is the dark one: a value of one
     * blank, {@code flicker-color: }, is a one-character {@code COLOUR_LINE} and gives
     * {@link ColourEnum#COLOUR_DARK}. C's refusal of an empty value is made by the grammar instead:
     * {@code COLOUR_LINE} needs at least one character, so an empty value produces no token and
     * {@code VisualsGrammar} finds a {@code FLICKER_COLOUR} or {@code FLICKER} with no colour after
     * it. A colon ends {@code COLOUR_LINE}, so on a {@code flicker-color:} line a colon after the
     * value is also refused by the grammar, whereas on the {@code flicker:} line it is the
     * separator before the name.
     * <p>
     * If the key is new, the first three steps are added in order and any shortfall is padded with
     * {@link ColourEnum#COLOUR_DARK}. If the key already has a cycle, the block's steps write over
     * that cycle's list from index 0, at most three of them, and the rest keep their earlier values,
     * as C's step-index reset does. A stored cycle always holds exactly three steps, so that overwrite
     * by index never runs off the end of the list. The cycle stored for the key is then rebuilt
     * around the list, named with the block's own raw base-colour text (the whole
     * {@code COLOUR_LINE}, so {@code Red} stays {@code Red} although the key is {@code R}) as a
     * label only.
     * Nothing here throws for a record the grammar produced; a hand-built record with an empty colour
     * string would throw {@link StringIndexOutOfBoundsException}.
     *
     * <p>Function assemble coded on 2026-10-30, commented in full on 2026-10-30, updated on 261105
     * for the removal of the soft-error path, updated on 261108 for the lenient colour field (first
     * character only), commented again on 261108 for the raw {@code COLOUR_LINE} treatment.
     *
     * @param records  the parsed {@code flicker:} blocks, in file order
     * @param filename the file being assembled; unused here
     * @return a one-element list holding the assembled {@link FlickerTable}
     */
    @Override
    public List<FlickerTable> assemble(@NotNull List<VisualsFlickerParseRecord> records,
                                    String filename) {
        Map<ColourEnum, ColourCycle> byAttr = new HashMap<>();

        for (VisualsFlickerParseRecord record : records) {
            List<ColourEnum> steps = new ArrayList<>();

            ColourEnum key = ColourEnum.fromCode(record.colourChar().charAt(0));
            if (key == null) {
                key = ColourEnum.COLOUR_WHITE;
            }
            boolean prevFound = false;
            if (byAttr.containsKey(key)) {
                prevFound = true;
                steps = new ArrayList<>(byAttr.get(key).getSteps());
            }
            int count = 0;
            for (String colour : record.colours()) {
                ColourEnum colourType = ColourEnum.fromCode(colour.charAt(0));
                if (count < 3) {
                    if (prevFound) {
                        steps.set(count, Objects.requireNonNullElse(colourType, ColourEnum.COLOUR_WHITE));
                    } else {
                        steps.add(Objects.requireNonNullElse(colourType, ColourEnum.COLOUR_WHITE));
                    }
                    count++;
                }
            }
            if (steps.size() < 3) {
                for (int i = steps.size(); i < 3; i++) {
                    steps.add(ColourEnum.COLOUR_DARK);
                }
            }
            
            ColourCycle value = ColourCycle.colourCycle(record.colourChar(), steps);
            byAttr.put(key, value);
        }

        FlickerTable result = new FlickerTable(byAttr);
        
        List<FlickerTable> tables = new ArrayList<>();
        tables.add(result);
        
        return tables;
    }
}
