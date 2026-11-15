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

package uk.co.jackoftradesltd.backend.parser.brand;

import java.util.List;

/**
 * The raw parse record for one entry in {@code brand.txt} - an unresolved
 * snapshot of the directive values produced by {@code BrandGrammar} and consumed
 * by {@link BrandAssembler}. Following the suite convention, every value is raw
 * text (no enum resolution or integer parsing happens here - that is the
 * assembler's job), keeping the DTO decoupled from the churning domain API.
 * <p>
 * Only {@code code:} is required. C registers the other seven directives as
 * independent handlers, so a record may leave any of them out or repeat them, in
 * any order. {@code name} and {@code verb} hold the last line written, or
 * {@code null} when the record has none, as C leaves the field {@code NULL}.
 * The other five hold the text of every line the record wrote, in file order, and
 * an empty list when it wrote none. They are lists rather than the last value
 * because C checks each number and flag as it reads the line, so the assembler
 * must see a bad earlier line that a good later one would have overwritten.
 * <p>
 * {@code resistFlag} and {@code vulnFlag} are independent - a brand may carry a
 * resist flag, a vuln flag, or both, or neither.
 * <p>
 * Record BrandParseRecord commented in full on 261113, when its multiplier,
 * o-multiplier, power, resist-flag and vuln-flag became lists.
 *
 * @param code        the brand's cross-reference code, e.g. {@code FIRE_3}
 * @param name        the element's display name, e.g. {@code lightning} (need
 *                    not equal the element code), or {@code null} if the record
 *                    has no {@code name:} line
 * @param multiplier  every {@code multiplier:} line, as text, in file order: the
 *                    standard-combat damage multipliers, or empty if there are none
 * @param oMultiplier every {@code o-multiplier:} line, as text, in file order: the
 *                    O-combat damage multipliers, or empty if there are none
 * @param power       every {@code power:} line, as text, in file order: the
 *                    object-power weightings, or empty if there are none
 * @param verb        the verb shown when a susceptible monster is hit, or
 *                    {@code null} if the record has no {@code verb:} line
 * @param resistFlag  every {@code resist-flag:} line in file order: the resisting
 *                    monster race flags (bare names, no {@code RF_} prefix), or
 *                    empty if there are none
 * @param vulnFlag    every {@code vuln-flag:} line in file order: the vulnerability
 *                    monster race flags (bare names), or empty if there are none
 * @param line        the source line of the record's {@code code:} header, kept
 *                    so the assembler can report errors against the right record
 * @author Rowan Crowther
 */
public record BrandParseRecord(String code,
                               String name,
                               List<String> multiplier,
                               List<String> oMultiplier,
                               List<String> power,
                               String verb,
                               List<String> resistFlag,
                               List<String> vulnFlag,
                               int line) {
}
