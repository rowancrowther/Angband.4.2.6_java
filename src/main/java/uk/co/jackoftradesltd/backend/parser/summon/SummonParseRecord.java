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

package uk.co.jackoftradesltd.backend.parser.summon;

import java.util.List;

/**
 * The raw parse record for one entry in {@code summon.txt} - an unresolved
 * snapshot of the directive values produced by {@code SummonGrammar} and
 * consumed by {@code SummonAssembler}. Following the suite convention, every
 * value is raw text, a {@link String} or a list of them (no message-type/base/flag
 * resolution or integer parsing happens here - that is the assembler's job),
 * keeping the DTO decoupled from the churning domain API.
 * <p>
 * Every directive but {@code name:} may be left out of a record, and may come in any
 * order, as in C. The grammar fills an absent one with an empty value: {@code ""}
 * for {@code fallback} and {@code desc}, and an empty list for {@code msgType},
 * {@code uniques}, {@code bases} and {@code raceFlag}. The assembler turns each
 * empty value into C's default, so a record holding only a {@code name} is legal.
 * {@code fallback} and {@code desc} hold the text of the last line when a directive
 * is repeated, as C's handlers overwrite. The four lists hold every line, in file
 * order, because C refuses an unresolvable {@code msgt:}, {@code base:} or
 * {@code race-flag:} line when it reads it, even if a later line would replace its
 * value; the assembler therefore needs to see each one.
 *
 * <p>Record SummonParseRecord coded before 260915, commented in full on 261112 (updated for
 * directives in any order, repeated or left out, and for {@code uniques} as a list; updated on
 * 261113 for {@code msgType} and {@code raceFlag} as lists).
 *
 * @param name     the summon type's identifier/code, e.g. {@code HI_UNDEAD}
 * @param msgType  the message-type symbols, e.g. {@code SUM_MONSTER}, from every
 *                 {@code msgt:} line, in file order; empty if there is none. The
 *                 summon uses the last one
 * @param uniques  the text of every {@code uniques:} line, in file order, each a
 *                 number such as {@code 0} or {@code 1}; empty if there is none. The
 *                 summon allows uniques if any of them is non-zero, which is the
 *                 assembler's reading
 * @param bases    the allowed monster base names from every {@code base:} line, in
 *                 file order, whatever lines came between them; empty if unrestricted
 * @param raceFlag the allowed monster race flags (bare names, no {@code RF_}
 *                 prefix), from every {@code race-flag:} line, in file order; empty
 *                 if the summon has no flag restriction. The summon uses the last one
 * @param fallback the name of the summon type to fall back to on failure, from the
 *                 last {@code fallback:} line, or {@code ""} if there is none
 * @param desc     the summon type's description used in messages, from the last
 *                 {@code desc:} line, or {@code ""} if there is none (C leaves a NULL
 *                 string)
 * @param line     the source line of the record's {@code name:} header, kept so
 *                 the assembler can report errors against the right record
 * @author Rowan Crowther
 */
public record SummonParseRecord(String name, List<String> msgType, List<String> uniques,
                                List<String> bases, List<String> raceFlag, String fallback,
                                String desc, int line) {
}
