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

package uk.co.jackoftrades.backend.parser.playerproperty;

import java.util.List;

/**
 * Raw, unresolved parse record for one {@code player_property.txt} entry, as emitted
 * by the {@code PlayerPropertyGrammar} parser. Every field is kept as the plain text
 * the grammar matched; all interpretation (mapping strings to enums, resolving UI
 * entries, parsing numbers) is deferred to {@link PlayerPropertyAssembler}, so this
 * type stays decoupled from the churning domain API - the grammar-extracts,
 * assembler-resolves split used across the parser suite.
 *
 * @param type   the record's {@code type:} value - {@code player}, {@code object} or
 *               {@code element}.
 * @param code   the {@code code:} value: the flag name to resolve ({@code PF_}/
 *               {@code OF_} is prepended by the assembler). Empty for {@code element}
 *               records, which carry no code.
 * @param bindui the {@code bindui:} lines, each as a four-element list in field order
 *               {@code [entryName, tag, aux, value]}: the UI-entry base name, its
 *               {@code <TAG>} (empty when absent), the {@code aux} flag ({@code "0"}
 *               or not), and the value ({@code "special"} or an integer). The outer
 *               list holds one entry per {@code bindui:} line (may be empty).
 * @param name   the {@code name:} display text.
 * @param desc   the {@code desc:} text (concatenated if the file spread it over
 *               several {@code desc:} lines).
 * @param value  the {@code value:} text - {@code 1}/{@code 3}/{@code -1} for
 *               resistance/immunity/vulnerability, empty otherwise.
 * @param line   the 1-based source line the record started on, used to anchor any
 *               soft-error messages the assembler raises.
 * @author Rowan Crowther
 */
public record PlayerPropertyParseRecord(String type, String code,
                                        List<List<String>> bindui, String name,
                                        String desc, String value, int line) {
}
