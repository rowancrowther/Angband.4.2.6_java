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

package uk.co.jackoftradesltd.backend.parser.grammars;

/**
 * Immutable extraction record for one parsed {@code effect:} block (the shared effect/dice
 * grammar reused across many data files): the raw, still-unresolved fields, later turned into
 * the {@code Effect} domain type by {@link EffectAssembler}.
 *
 * @author Rowan Crowther
 */
public record EffectParseRecord(String typeInit,
                                String subTypeWrapper,
                                String radius,
                                String other,
                                String diceString,
                                String yVal,
                                String xVal,
                                String expressionChars,
                                String expressionBases,
                                String expressionOperations,
                                String timeDiceString,
                                String effectMessage,
                                int line) {
}
