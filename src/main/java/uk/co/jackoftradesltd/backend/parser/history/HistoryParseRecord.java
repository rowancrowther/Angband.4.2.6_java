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

package uk.co.jackoftradesltd.backend.parser.history;

/**
 * Immutable extraction record for one {@code history.txt} entry: the raw, still-unresolved
 * fields of a background-history chart link, later turned into the {@code PlayerHistoryChart}
 * domain type by {@link HistoryAssembler}.
 *
 * @param currentChart the chart number this entry belongs to, still as text
 * @param nextChart    the successor chart number, still as text ({@code "0"} marks the end of a
 *                     chain); every record for the same {@link #currentChart} must agree on this
 * @param percentage   the entry's roll weight, still as text
 * @param phrase       the background text this entry contributes when rolled
 * @param line         the source line this record was parsed from, for error messages
 * @author Rowan Crowther
 */
public record HistoryParseRecord(String currentChart,
                                 String nextChart,
                                 String percentage,
                                 String phrase,
                                 int line) {
}
