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

package uk.co.jackoftrades.backend.parser.objectproperty;

import java.util.Map;

/**
 * The raw, unresolved fields of one {@code object_property.txt} record, as pulled
 * straight from the parse tree by {@code ObjectPropertyGrammar} before any
 * type-checking. Every scalar is a raw {@link String} (or {@code null} for an
 * absent optional): the grammar does extraction only, and
 * {@code ObjectPropertyAssembler} turns these into the typed
 * {@link uk.co.jackoftrades.middle.objects.ObjectProperty} — parsing the integer
 * fields, resolving {@code type}/{@code subtype}/{@code idType}/{@code code} to
 * their enums, and looking the {@code bindui} target up in the UI-entry registry.
 *
 * <p>This is the parse-time counterpart to C's {@code struct obj_property}
 * ({@code src/obj-properties.h}); the split into a raw record plus a separate
 * assembler mirrors the reader → ParseRecord → Assembler pipeline used across the
 * grammar suite.
 *
 * @param name         the property's {@code name:} (may contain spaces)
 * @param type         the {@code type:} token (e.g. {@code stat}, {@code flag},
 *                     {@code resistance}), resolved to {@code ObjPropertyType}
 * @param subtype      the {@code subtype:} token, or {@code ""} if absent (only
 *                     flags carry one); resolved to {@code ObjectFlagType}
 * @param idType       the {@code id-type:} token, or {@code ""} if absent; resolved
 *                     to {@code ObjectFlagID}. A single value, not a list — the
 *                     directive appears at most once and C replaces rather than
 *                     accumulates
 * @param code         the {@code code:} symbol, resolved against a table chosen by
 *                     {@code type} (obj_mods / obj_flags / element_names)
 * @param power        the {@code power:} integer, unparsed ({@code ""} if absent)
 * @param mult         the {@code mult:} integer, unparsed ({@code ""} if absent)
 * @param typeMult     the {@code type-mult:} lines as tval-name → multiplier, both
 *                     unparsed (the tval name may contain a space, e.g.
 *                     {@code dragon armor})
 * @param adjective    the {@code adjective:} text ({@code ""} if absent)
 * @param negAdjective the {@code neg-adjective:} text ({@code ""} if absent)
 * @param msg          the {@code msg:} text ({@code ""} if absent)
 * @param bindui       the {@code bindui} target entry name, or {@code ""} if the
 *                     record has no {@code bindui} line
 * @param tag          the {@code <TAG>} decorating the bindui name (e.g.
 *                     {@code STR}, {@code ACID}), or {@code ""} if none; forms part
 *                     of the entry's lookup key, not a separate value
 * @param value        the optional third {@code bindui} parameter (the explicit UI
 *                     value), or {@code null} when absent — {@code null} preserves
 *                     C's {@code parser_hasval} "use the natural value" case
 * @param aux          the mandatory second {@code bindui} parameter (the auxiliary
 *                     flag), unparsed
 * @param desc         the {@code desc:} text ({@code ""} if absent)
 * @param line         the 1-based source line the record started on, for error
 *                     reporting
 * @author Rowan Crowther
 */
public record ObjectPropertyParseRecord(String name,
                                        String type,
                                        String subtype,
                                        String idType,
                                        String code,
                                        String power,
                                        String mult,
                                        Map<String, String> typeMult,
                                        String adjective,
                                        String negAdjective,
                                        String msg,
                                        String bindui,
                                        String tag,
                                        String value,
                                        String aux,
                                        String desc,
                                        int line) {
}
