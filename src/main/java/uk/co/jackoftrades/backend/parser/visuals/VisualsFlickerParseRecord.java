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

import java.util.List;

/**
 * The raw, unassembled form of one {@code flicker:} block from visuals.txt. A block is a
 * {@code flicker:<code>:<name>} header followed by one or more {@code flicker-color:<code>} step
 * lines; this record carries those parts verbatim so {@code VisualsFlickerAssembler} can resolve the
 * codes to {@code ColourType}s and install the cycle in the {@code FlickerTable} under its base
 * attribute.
 * <p>
 * Note the two colour-bearing fields are <em>not</em> the same axis. {@code colourChar} is the
 * block's <strong>base attribute</strong> - the {@code flicker:} header's own code - and becomes the
 * table's lookup key (the static colour a flickering monster would otherwise show). {@code colours}
 * are the animation <strong>steps</strong> cycled through for that base attribute. Ports the
 * parse-time half of the C {@code visuals_flicker} loader ({@code ui-visuals.c}).
 *
 * @param name       the free-text label after the base code in the {@code flicker:} header (e.g.
 *                   {@code "black light-dark light-red"}); descriptive only, not a lookup key
 * @param colourChar the base-attribute colour code (the header's own code); the {@code FlickerTable}
 *                   key, still as a {@code String} awaiting {@code ColourType} resolution
 * @param colours    the ordered {@code flicker-color:} step codes, one single-character colour code
 *                   per entry, still as {@code String}s
 * @param line       the source line of the {@code flicker:} header, used for error reporting
 * @author Rowan Crowther
 */
public record VisualsFlickerParseRecord(String name,
                                        String colourChar,
                                        List<String> colours,
                                        int line) {
}
