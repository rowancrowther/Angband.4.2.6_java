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

package uk.co.jackoftradesltd.backend.parser.objectproperty;

import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

import java.util.List;

/**
 * The raw, unresolved contents of one {@code object_property.txt} record, as read by
 * {@code ObjectProperty2Grammar}: one {@link Directive} per line, in the order the lines
 * appeared, and nothing else. The grammar does extraction only, so every value is raw text
 * and nothing has been checked, parsed or resolved; that is the assembler's work.
 *
 * <p>The list is the point of this shape. C's {@code parse_object_property_*} handlers read the
 * record as it stands when each line arrives. {@code code:} and {@code bindui:} look at the type
 * held at that moment and refuse a record whose type is still 0
 * ({@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE}), {@code code:} is resolved against the table that
 * type selects, and a later {@code type:} only overwrites the type. A record that kept one final
 * value per field lost that order. A list keeps it, and keeps a repeated line whole, so the
 * assembler can do what C does with each directive: a repeated {@code msg:} or {@code desc:}
 * replaces the earlier text, and every {@code bindui:} binds.
 *
 * <p><b>Shape.</b> As the grammar builds it, the first entry is always the {@code name:} line,
 * and the list is never {@code null}. Every other directive may appear in any order, any number
 * of times, or not at all. An absent directive is simply not in the list, so there is no empty
 * string to test for, and a record with no {@code type:} line has no {@code type} entry; C
 * leaves such a record at {@code OBJ_PROPERTY_NONE}. The record has no line of its own: it starts
 * at the line of its first entry, and each {@link Directive} carries the line it was read from,
 * which is the line the assembler's error messages name.
 *
 * <p><b>Values.</b> Each {@link Directive} carries the directive's name as C spells it and its
 * values as raw text:
 * <ul>
 *   <li>{@code name}, {@code type}, {@code subtype}, {@code id-type}, {@code code},
 *       {@code power}, {@code mult}, {@code adjective}, {@code neg-adjective}, {@code msg} and
 *       {@code desc}: one value, the text after the colon. The figures of {@code power} and
 *       {@code mult} are unparsed.</li>
 *   <li>{@code type-mult}: two values, the tval name as written (it may contain a space, such as
 *       {@code dragon armor}) and the multiplier, unparsed.</li>
 *   <li>{@code bindui}: four values, always: the entry name, the {@code <TAG>} or {@code ""} if
 *       there is none, the aux figure, and the explicit value or {@code null} if there is none.
 *       The {@code null}, rather than {@code ""}, keeps C's {@code parser_hasval} distinction
 *       between "no value" and a real 0.</li>
 * </ul>
 *
 * <p>This is the parse-time counterpart to C's {@code struct obj_property}
 * ({@code src/obj-properties.h}), in the reader, record, assembler pipeline used across the
 * grammar suite.
 *
 * <p>Record ObjectProperty2ParseRecord coded on 261114, commented in full on 261114 (the sentence
 * on the record's line corrected 261114).
 *
 * @param directives the record's directive lines in file order, starting with its
 *                   {@code name:} line
 * @author Rowan Crowther
 */
public record ObjectProperty2ParseRecord (List<Directive> directives) {
}
