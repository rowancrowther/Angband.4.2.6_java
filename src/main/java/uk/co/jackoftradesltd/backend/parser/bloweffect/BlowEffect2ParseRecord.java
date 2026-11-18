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

package uk.co.jackoftradesltd.backend.parser.bloweffect;

import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.unused.BlowEffectParseRecord;

import java.util.List;

/**
 * The raw, unresolved contents of one {@code blow_effects.txt} record: one {@link Directive} per
 * line, in the order the lines appeared, and nothing else. Nothing in it has been checked, parsed
 * or resolved into an enum or a domain object; that is the assembler's work.
 *
 * <p>The list is the point of this shape. C's {@code parse_eff_*} handlers in {@code mon-init.c}
 * read the record as it stands when each line arrives. {@code parse_eff_resist} looks at the
 * {@code effect_type} that an earlier {@code effect-type:} line stored, and reads it as
 * {@code element} (the resist is a projection index) or {@code flag} (the resist is an object-flag
 * index); any other value returns {@code PARSE_ERROR_MISSING_BLOW_EFF_TYPE}. A {@code resist:}
 * line above the {@code effect-type:} line sees the zero-filled record's null type, which C
 * compares without checking. A record that kept one final value per field lost that order, so the
 * assembler could not tell the two cases apart. A list keeps the order, and keeps a repeated line
 * whole, so the assembler can do what each C handler does: a repeated {@code desc:} is appended
 * ({@code string_append}), and every other repeated directive overwrites the earlier one.
 *
 * <p><b>Shape.</b> The first entry is meant to be the {@code name:} line, which is the one
 * handler ({@code parse_eff_name}) that starts a record, and the list is meant never to be
 * {@code null}. Every other directive may appear in any order, any number of times, or not at
 * all. An absent directive is simply not in the list, so there is no empty string to test for. The
 * record has no line of its own: it starts at the line of its first entry, and each
 * {@link Directive} carries the line it was read from, which is the line the assembler's error
 * messages name. The record does not enforce any of this; it holds whatever list it is given.
 *
 * <p><b>Values.</b> Each {@link Directive} carries a name and its one value as raw text. The data
 * file's keywords are C's ({@code init_parse_eff}): {@code name:}, {@code power:}, {@code eval:},
 * {@code desc:}, {@code lore-color-base:}, {@code lore-color-resist:}, {@code lore-color-immune:},
 * {@code effect-type:}, {@code resist:} and {@code lash-type:}. The {@code Directive} names are the
 * same for {@code name}, {@code power}, {@code eval}, {@code desc} and {@code resist}, and differ for
 * the other five: {@code base-colour}, {@code resist-colour}, {@code immune-colour}, {@code effect}
 * and {@code lash}. That is an accepted deviation (see {@code docs/Accepted_Deviations.md}, Blow
 * effect), because only {@code BlowEffect2Grammar} writes the name and only
 * {@code BlowEffect2Assembler} reads it. The figures of {@code power} and {@code eval} are
 * unparsed, and the colours are unresolved.
 *
 * <p>This is the parse-time counterpart to C's {@code struct blow_effect}. It is the ordered
 * counterpart of {@link BlowEffectParseRecord}, which keeps one final value per field, as
 * {@code ObjectProperty2ParseRecord} is to {@code ObjectPropertyParseRecord}. The
 * {@code blowEffect} rule of {@code BlowEffect2Grammar} builds it and
 * {@code BlowEffect2Assembler} reads it.
 *
 * <p>Record BlowEffect2ParseRecord coded on 261116, commented in full on 261116, directive names
 * reworded on 261116.
 *
 * @param directives the record's directive lines in file order, starting with its
 *                   {@code name:} line
 * @author Rowan Crowther
 */
public record BlowEffect2ParseRecord(List<Directive> directives) {
}
