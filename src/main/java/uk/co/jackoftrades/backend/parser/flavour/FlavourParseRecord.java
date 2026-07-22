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

/**
 * The raw, unresolved form of one {@code flavor:} or {@code fixed:} line from
 * {@code flavor.txt}, as produced by {@code FlavourGrammar}. Every field is kept
 * as its source string so the grammar stays free of domain concerns; the
 * {@link FlavourAssembler} parses the index, resolves the colour and (for fixed
 * records) resolves the sval symbol against the object-kind table.
 *
 * <p>A {@code fixed:} line and a plain {@code flavor:} line share this record;
 * they are told apart by {@link #sVal()} — non-null for a fixed record, null for
 * a plain flavour.
 *
 * @param index the flavour's numeric index ({@code fidx} in C), still as text
 * @param sVal  the sub-type symbol a {@code fixed:} line binds to (resolved the
 *              way C's {@code lookup_sval} does), or {@code null} for a plain
 *              {@code flavor:} line
 * @param attr  the colour, as a single-character code or a colour name
 * @param text  the flavour description, or {@code null} when the line omits it
 *              (scroll flavours, whose names are generated at runtime)
 * @param line  the source line number, retained for error messages
 * @author Rowan Crowther
 */
public record FlavourParseRecord(String index,
                                 String sVal,
                                 String attr,
                                 String text,
                                 int line) {
}
