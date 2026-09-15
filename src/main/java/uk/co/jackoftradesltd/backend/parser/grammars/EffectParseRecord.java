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
 * @param typeInit              the effect name, still as text (e.g. {@code "DAMAGE"}); resolved
 *                              against {@code EffectEnum} with an {@code EF_} prefix
 * @param subTypeWrapper        the effect's second token, still as text; what it means depends on
 *                              the effect's {@code EffectSubTypeEnum} kind and is resolved by
 *                              {@link EffectAssembler#getWrapperSubType}, or empty if the line
 *                              gave none
 * @param radius                the radius parameter, still as text, or empty if absent (defaults
 *                              to {@code 0})
 * @param other                 the effect's generic extra parameter, still as text, or empty if
 *                              absent (defaults to {@code 0})
 * @param diceString            the raw, unevaluated dice expression (e.g. {@code "2d6"} or an
 *                              expression-bound string such as {@code "$B+1d8"}); kept as text
 *                              because a die roll needs live game state at the moment the effect
 *                              fires, not at load time
 * @param yVal                  the y-coordinate/offset parameter, still as text, or empty if
 *                              absent (defaults to {@code 0})
 * @param xVal                  the x-coordinate/offset parameter, still as text, or empty if
 *                              absent (defaults to {@code 0})
 * @param expressionChars       the {@code ^}-delimited single-character codes bound within
 *                              {@link #diceString}, or empty if the dice string uses none
 * @param expressionBases       the {@code ^}-delimited base-type names paired positionally with
 *                              {@link #expressionChars}, resolved against {@code EffectBaseType}
 *                              with an {@code EFB_} prefix
 * @param expressionOperations  the {@code ^}-delimited raw operation strings paired positionally
 *                              with {@link #expressionChars}
 * @param timeDiceString        the raw dice expression for the effect's duration, parsed (not
 *                              rolled) via {@code Random.parseStr}
 * @param effectMessage         the message text shown when the effect fires, or {@code null}/empty
 *                              if the line gave none
 * @param line                  the source line the {@code effect:} block starts on, for error
 *                              messages
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
