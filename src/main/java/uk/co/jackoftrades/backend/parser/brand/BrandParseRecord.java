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

package uk.co.jackoftrades.backend.parser.brand;

/**
 * The raw parse record for one entry in {@code brand.txt} - an unresolved
 * snapshot of the directive values produced by {@code BrandGrammar} and consumed
 * by {@link BrandAssembler}. Following the suite convention, every field is a raw
 * {@link String} (no enum resolution or integer parsing happens here - that is
 * the assembler's job), keeping the DTO decoupled from the churning domain API.
 * <p>
 * {@code vulnFlag} is the empty string when the record omits the optional
 * {@code vuln-flag:} directive (the grammar defaults it); {@code resistFlag} is
 * present on every shipped brand. The two are independent - a brand may carry a
 * resist flag, a vuln flag, or both.
 *
 * @param code        the brand's cross-reference code, e.g. {@code FIRE_3}
 * @param name        the element's display name, e.g. {@code lightning} (need
 *                    not equal the element code)
 * @param multiplier  the standard-combat damage multiplier, as text
 * @param oMultiplier the O-combat damage multiplier, as text
 * @param power       the object-power weighting, as text
 * @param verb        the verb shown when a susceptible monster is hit
 * @param resistFlag  the resisting monster race flag (bare name, no {@code RF_}
 *                    prefix)
 * @param vulnFlag    the vulnerability monster race flag (bare name), or
 *                    {@code ""} if the record omits {@code vuln-flag:}
 * @param line        the source line of the record's {@code code:} header, kept
 *                    so the assembler can report errors against the right record
 * @author Rowan Crowther
 */
public record BrandParseRecord(String code,
                               String name,
                               String multiplier,
                               String oMultiplier,
                               String power,
                               String verb,
                               String resistFlag,
                               String vulnFlag,
                               int line) {
}
