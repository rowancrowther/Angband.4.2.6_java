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
 * <p>A grammar builds the record when the effect line arrives. The name, sub-type, radius and other
 * parameter are what that line gave. The dice, {@code effect-yx:} and expression fields are what the
 * lines straight after it gave: the shared {@code effectBlock} rule takes one {@code effect-yx:}
 * and one {@code dice:} with its {@code expr:} lines at that point. Each is the empty string where
 * the data file gave none, and the message starts as {@code null}. Two builders start from
 * the empty string instead: {@code PlayerTimedGrammar.g4}, which gives the name and sub-type and
 * leaves every other field empty (the message included), and the extra effects of
 * {@code TrapGrammar.g4}, whose message is empty.
 *
 * <p>The lines that come later fill in the rest: {@code effect-yx:} through {@link #amendYX},
 * {@code dice:} and {@code expr:} through {@link #amendExpression}, and {@code effect-msg:} through
 * {@link #appendMessage}. Each of those
 * returns a new record and leaves this one unchanged, so the caller has to write the result back over
 * the old one; dropping it loses the line. Nothing here is validated or resolved: a bad name, a
 * non-numeric {@code radius} or a malformed dice string is only found when {@link EffectAssembler}
 * assembles the record.
 *
 * <p>Class EffectParseRecord coded before 261119, commented in full on 261119.
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
 * @param effectMessage         the message text shown when the effect fires, or {@code null}/empty
 *                              if the line gave none
 * @param line                  the source line the assembler's errors cite for this effect. Every
 *                              grammar passes the line the {@code effect:} block starts on, except
 *                              {@code PlayerTimedGrammar.g4}, which passes the line of the
 *                              record's {@code name:} (carried through
 *                              {@link EffectParseRecordType#convertToRecord}), so an error in a
 *                              timed effect names that line
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
                                String effectMessage,
                                int line) {
    
    /**
     * A copy of this record with the y and x parameters replaced, for an {@code effect-yx:} line.
     * Both are replaced together, as C's {@code effect-yx} handlers set {@code y} and {@code x} in one
     * step, and everything else is carried over.
     *
     * <p>Function amendYX coded before 261119, commented in full on 261119.
     *
     * @param yVal the new y parameter, still as text
     * @param xVal the new x parameter, still as text
     * @return the amended copy; this record is unchanged
     */
    public EffectParseRecord amendYX(String yVal, String xVal) {
        return new EffectParseRecord(this.typeInit(), this.subTypeWrapper(), this.radius(),
                this.other(), this.diceString(), yVal, xVal, this.expressionChars(),
                this.expressionBases(), this.expressionOperations(), this.effectMessage(), this.line);
    }
    
    /**
     * A copy of this record with the dice string and the three expression fields replaced as a set,
     * for a {@code dice:} or {@code expr:} line. The fields are replaced, not added to: a caller that
     * is binding a further expression to the dice already held passes that dice string back and the
     * three expression fields already held with the new letter, base and operations joined on
     * with {@code ^}, as {@code ChestTrapGrammar.g4} does.
     *
     * <p>Function amendExpression coded before 261119, commented in full on 261119.
     *
     * @param diceStr             the dice string, still as text
     * @param expressionChar      the {@code ^}-delimited codes bound within the dice string
     * @param expressionBase      the {@code ^}-delimited base-type names, paired with the codes
     * @param expressionOperation the {@code ^}-delimited operation strings, paired with the codes
     * @return the amended copy; this record is unchanged
     */
    public EffectParseRecord amendExpression(String diceStr, 
                                             String expressionChar,
                                             String expressionBase,
                                             String expressionOperation) {
        return new EffectParseRecord(this.typeInit(), this.subTypeWrapper(), this.radius(),
                this.other(), diceStr, this.yVal(), this.xVal(),
                expressionChar, expressionBase, expressionOperation, this.effectMessage(), this.line);
    }
    
    /**
     * A copy of this record with {@code text} added to the end of the message, for an
     * {@code effect-msg:} line. The text is joined on with no separator, as C's {@code string_append}
     * does for a repeated line, and when the record has no message yet the copy holds {@code text}
     * alone.
     *
     * <p>Function appendMessage coded before 261119, commented in full on 261119.
     *
     * @param text the message text of the line, to add after any already held
     * @return the amended copy; this record is unchanged
     */
    public EffectParseRecord appendMessage(String text) {
        String joined = (this.effectMessage() == null) ? text : this.effectMessage() + text;
        return new EffectParseRecord(this.typeInit(), this.subTypeWrapper(), this.radius(),
                this.other(), this.diceString(), this.yVal(), this.xVal(),
                this.expressionChars(), this.expressionBases(), this.expressionOperations(), 
                joined, this.line);
    }
}
