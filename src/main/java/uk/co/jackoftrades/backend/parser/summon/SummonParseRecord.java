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

package uk.co.jackoftrades.backend.parser.summon;

import java.util.List;

/**
 * The raw parse record for one entry in {@code summon.txt} - an unresolved
 * snapshot of the directive values produced by {@code SummonGrammar} and
 * consumed by {@code SummonAssembler}. Following the suite convention, every
 * scalar field is a raw {@link String} (no message-type/base/flag resolution or
 * integer parsing happens here - that is the assembler's job), keeping the DTO
 * decoupled from the churning domain API.
 * <p>
 * {@code bases} is a list because a summon may list several {@code base:} lines
 * (it is empty when the summon has no base restriction). {@code raceFlag} and
 * {@code fallback} are the empty string when their optional directive is
 * omitted.
 *
 * @param name     the summon type's identifier/code, e.g. {@code HI_UNDEAD}
 * @param msgType  the message-type symbol, e.g. {@code SUM_MONSTER}
 * @param uniques  whether uniques may be summoned, as text ({@code 0} or {@code 1})
 * @param bases    the allowed monster base names; empty if unrestricted
 * @param raceFlag the allowed monster race flag (bare name, no {@code RF_}
 *                 prefix), or {@code ""} if the summon has no flag restriction
 * @param fallback the name of the summon type to fall back to on failure, or
 *                 {@code ""} if there is none
 * @param desc     the summon type's description used in messages
 * @param line     the source line of the record's {@code name:} header, kept so
 *                 the assembler can report errors against the right record
 * @author Rowan Crowther
 */
public record SummonParseRecord(String name, String msgType, String uniques,
                                List<String> bases, String raceFlag, String fallback,
                                String desc, int line) {
}
