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

package uk.co.jackoftrades.backend.parser.slay;

/**
 * The raw parse record for one entry in {@code slay.txt} - an unresolved
 * snapshot of the directive values produced by {@code SlayGrammar} and consumed
 * by {@link SlayAssembler}. Following the suite convention, every field is a raw
 * {@link String} (no enum resolution, base lookup or integer parsing happens
 * here - that is the assembler's job), keeping the DTO decoupled from the
 * churning domain API.
 * <p>
 * The grammar defaults each absent directive to the empty string, so a slay that
 * targets a race flag arrives with {@code base == ""} and one that targets a
 * base arrives with {@code raceFlag == ""}. Which of the two is populated, and
 * the mutual-exclusion rule between them, are interpreted downstream in the
 * assembler, not here.
 *
 * @param code      the slay's cross-reference code, e.g. {@code EVIL_2}
 * @param name      the slain-creature name used in object descriptions
 * @param raceFlag  the target monster race flag (bare name, no {@code RF_}
 *                  prefix), or {@code ""} if the slay targets a base instead
 * @param base      the target monster base name, or {@code ""} if the slay
 *                  targets a race flag instead
 * @param mult      the standard-combat damage multiplier, as text
 * @param oMult     the O-combat damage multiplier, as text
 * @param power     the object-power weighting, as text
 * @param meleeVerb the verb shown when a susceptible monster is hit in melee
 * @param rangeVerb the verb shown when a susceptible monster is hit at range
 * @param line      the source line of the record's {@code code:} header, kept
 *                  so the assembler can report errors against the right record
 * @author Rowan Crowther
 */
public record SlayParseRecord(String code,
                              String name,
                              String raceFlag,
                              String base,
                              String mult,
                              String oMult,
                              String power,
                              String meleeVerb,
                              String rangeVerb,
                              int line) {
}
