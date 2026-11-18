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

package uk.co.jackoftradesltd.unused;

import java.util.Map;

/**
 * The raw, unresolved fields of one {@code object_property.txt} record, as pulled
 * straight from the parse tree by {@code ObjectPropertyGrammar} before any
 * type-checking. Every scalar is a raw {@link String} (or {@code null} for an
 * absent optional): the grammar does extraction only, and
 * {@code ObjectPropertyAssembler} turns these into the typed
 * {@link uk.co.jackoftradesltd.middle.objects.ObjectProperty} — parsing the integer
 * fields, resolving {@code type}/{@code subtype}/{@code idType}/{@code code} to
 * their enums, and building the {@code bindui} binding, whose UI entry is matched
 * later by {@code GameConstants}.
 *
 * <p>This is the parse-time counterpart to C's {@code struct obj_property}
 * ({@code src/obj-properties.h}); the split into a raw record plus a separate
 * assembler mirrors the reader → ParseRecord → Assembler pipeline used across the
 * grammar suite.
 *
 * <p><b>Absent and repeated lines.</b> Every directive after {@code name:} may come in any
 * order, any number of times, or not at all. An absent text field is {@code ""}, so a record
 * with no {@code type:} line arrives with {@code type} empty and the assembler gives it
 * {@code OBJ_PROPERTY_NONE}, as C's zero-filled allocation does. A repeated line replaces the
 * earlier one, which is what C does for every field it stores once. There are two exceptions:
 * {@code msg:} and {@code desc:} are joined where C keeps the last, and a repeated
 * {@code bindui:} keeps only the last where C binds every line. The record keeps each field's
 * final value and nothing about the order the lines arrived in, so the assembler cannot tell
 * that a {@code code:} or {@code bindui:} came before the {@code type:}, which C refuses.
 *
 * <p>Record ObjectPropertyParseRecord coded before 260902, commented in full on 261113.
 *
 * @param name         the property's {@code name:} (may contain spaces)
 * @param type         the {@code type:} token (e.g. {@code stat}, {@code flag},
 *                     {@code resistance}), or {@code ""} if absent; resolved to
 *                     {@code ObjPropertyType}, with {@code ""} giving {@code OBJ_PROPERTY_NONE}
 * @param subtype      the {@code subtype:} token, or {@code ""} if absent (only
 *                     flags carry one in the shipped data); resolved to {@code ObjectFlagType}
 * @param idType       the {@code id-type:} token, or {@code ""} if absent; resolved
 *                     to {@code ObjectFlagID}. A single value, not a list — a repeated
 *                     line replaces the earlier one, as in C
 * @param code         the {@code code:} symbol, or {@code ""} if absent; resolved against a
 *                     table chosen by {@code type} (obj_mods / obj_flags / element_names)
 * @param power        the {@code power:} integer, unparsed ({@code ""} if absent)
 * @param mult         the {@code mult:} integer, unparsed ({@code ""} if absent)
 * @param typeMult     the {@code type-mult:} lines as tval-name → multiplier, both
 *                     unparsed (the tval name may contain a space, e.g.
 *                     {@code dragon armor}); empty if there are none
 * @param adjective    the {@code adjective:} text ({@code ""} if absent)
 * @param negAdjective the {@code neg-adjective:} text ({@code ""} if absent)
 * @param msg          the {@code msg:} text ({@code ""} if absent); repeated lines are
 *                     joined, where C keeps the last
 * @param bindui       the {@code bindui} target entry name, or {@code ""} if the
 *                     record has no {@code bindui} line
 * @param tag          the {@code <TAG>} decorating the bindui name (e.g.
 *                     {@code STR}, {@code ACID}), or {@code ""} if none; forms part
 *                     of the entry's lookup key, not a separate value
 * @param value        the optional third {@code bindui} parameter (the explicit UI
 *                     value), or {@code null} when absent — {@code null} preserves
 *                     C's {@code parser_hasval} "use the natural value" case
 * @param aux          the mandatory second {@code bindui} parameter (the auxiliary
 *                     flag), unparsed; {@code ""} if the record has no {@code bindui} line
 * @param desc         the {@code desc:} text ({@code ""} if absent); repeated lines are
 *                     joined, where C keeps the last
 * @param line         the 1-based source line of the record's {@code name:} line, for
 *                     error reporting
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
