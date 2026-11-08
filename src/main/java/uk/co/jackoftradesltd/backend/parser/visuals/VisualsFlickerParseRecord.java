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

import java.util.List;

/**
 * The raw, unassembled form of one {@code flicker:} block from visuals.txt. A block is a
 * {@code flicker:<code>:<name>} header followed by one or more {@code flicker-color:<code>} step
 * lines; this record carries those parts verbatim so {@code VisualsFlickerAssembler} can resolve the
 * codes to {@code ColourEnum}s and install the cycle in the {@code FlickerTable} under its base
 * attribute.
 * <p>
 * Note the two colour-bearing fields are <em>not</em> the same axis. {@code colourChar} is the
 * block's <strong>base attribute</strong> - the {@code flicker:} header's own code - and becomes the
 * table's lookup key (the static colour a flickering monster would otherwise show). {@code colours}
 * are the animation <strong>steps</strong> cycled through for that base attribute. Ports the
 * parse-time half of the C {@code visuals_flicker} loader ({@code ui-visuals.c}).
 * <p>
 * <strong>Colour text is raw, not a single character.</strong> {@code VisualsLexer} reads the
 * header's colour and every step's as a {@code COLOUR_LINE}: any run of characters up to a colon or
 * the end of the line, with no check against the colour alphabet. So {@code colourChar} and each
 * entry of {@code colours} hold that whole run exactly as written ({@code "d"} in the shipped
 * {@code visuals.txt}, but {@code "Red"}, {@code "q"} or a single blank are equally possible), and
 * nothing here has been resolved or validated. {@code VisualsFlickerAssembler} takes the first
 * character of each and resolves that through {@code ColourEnum.fromCode(char)}, as C's handlers do
 * with {@code color_code[0]}. The grammar guarantees each is at least one character long; a
 * hand-built record with an empty string would break the assembler.
 *
 * <p>Record VisualsFlickerParseRecord updated on 261108 for the raw {@code COLOUR_LINE} colour
 * fields.
 *
 * @param name       the free text after the second colon in the {@code flicker:} header, to the end
 *                   of the line (e.g. {@code "black light-dark light-red"}); descriptive only, not a
 *                   lookup key, and never read by C or by the assembler
 * @param colourChar the base-attribute colour text (the header's own colour field), whole and
 *                   unresolved; only its first character decides the {@code FlickerTable} key, and
 *                   the whole text is also used as the stored cycle's label
 * @param colours    the ordered {@code flicker-color:} step texts, one whole {@code COLOUR_LINE} per
 *                   entry and at least one character each, still as {@code String}s; only the
 *                   first character of each is used, and only the first three steps are kept
 * @param line       the source line of the {@code flicker:} header, as the grammar records it;
 *                   {@code VisualsFlickerAssembler} no longer reads it, since that assembler has
 *                   no failing path
 * @author Rowan Crowther
 */
public record VisualsFlickerParseRecord(String name,
                                        String colourChar,
                                        List<String> colours,
                                        int line) {
}
