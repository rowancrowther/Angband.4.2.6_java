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
 * A parse-time working copy of an {@link EffectParseRecord} that also says which chain the effect
 * belongs to, so one list can hold the effects of two chains in the order the file gave them.
 *
 * <p>{@code PlayerTimedGrammar.g4} needs this: a timed effect has an {@code on-begin-effect:} chain
 * and an {@code on-end-effect:} chain, and C's {@code effect-dice:} handler puts the dice on the
 * effect opened last, whichever chain that is, and C's {@code effect-yx:}, {@code effect-expr:} and
 * {@code effect-msg:} handlers do the same. The grammar therefore keeps every effect in one list,
 * each tagged with its chain in {@link #type}, attaches each of those four directives to the last
 * entry with {@link #amendDice}, {@link #amendYX}, {@link #amendExpression} and
 * {@link #appendMessage}, and at the end of the record splits the list into the two chains with
 * {@link #convertToRecord}, which drops the tag.
 *
 * <p>The components other than {@link #type} are those of {@link EffectParseRecord}, and the amend
 * methods follow its rules, except that {@link #amendDice} also clears the expression fields (see
 * there): each returns a new record and leaves this one unchanged, so the caller has to write the
 * result back over the old one. Nothing here is validated or resolved.
 *
 * <p>Class EffectParseRecordType coded on 261119, updated on 261125, commented in full on 261125.
 *
 * @param type                  which chain the effect belongs to: {@code "begin"} for an
 *                              {@code on-begin-effect:} line and {@code "end"} for an
 *                              {@code on-end-effect:} line. {@code PlayerTimedGrammar.g4} sends
 *                              {@code "end"} to the end chain and anything else to the begin chain.
 *                              It is not part of the effect and does not survive
 *                              {@link #convertToRecord}
 * @param typeInit              the effect name, still as text (e.g. {@code "SCRAMBLE_STATS"}), as
 *                              {@link EffectParseRecord#typeInit}; not to be confused with {@link #type}
 * @param subTypeWrapper        the effect's second token, still as text, or empty if the line gave none
 * @param radius                the radius parameter, still as text, or empty if absent
 * @param other                 the effect's generic extra parameter, still as text, or empty if absent
 * @param diceString            the raw dice string, still as text, or empty if no {@code effect-dice:}
 *                              line has reached the effect
 * @param yVal                  the y parameter, still as text, or empty if absent
 * @param xVal                  the x parameter, still as text, or empty if absent
 * @param expressionChars       the {@code ^}-delimited codes bound within the dice string, or empty
 * @param expressionBases       the {@code ^}-delimited base-type names, paired with the codes
 * @param expressionOperations  the {@code ^}-delimited operation strings, paired with the codes
 * @param effectMessage         the message text, or {@code null}/empty if there is none
 * @param line                  the source line the effect is reported against in the assembler's
 *                              errors. {@code PlayerTimedGrammar.g4} passes the line of the record's
 *                              {@code name:}, not the effect's own line
 * @author Rowan Crowther
 */
public record EffectParseRecordType(String type,
                                    String typeInit,
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
     * The plain {@link EffectParseRecord} for this effect: every field except {@link #type} carried
     * over as it stands. This is the step that puts an effect onto its own chain, because the tag
     * that said which chain it was on is not part of the result.
     *
     * <p>Function convertToRecord coded on 261119, commented in full on 261119.
     *
     * @return the effect without its chain tag
     */
    public EffectParseRecord convertToRecord() {
        return new EffectParseRecord(this.typeInit(), this.subTypeWrapper(), this.radius(),
                this.other(), this.diceString(), this.yVal(), this.xVal(), this.expressionChars(),
                this.expressionBases(), this.expressionOperations(), this.effectMessage(), this.line());
    }
    
    /**
     * A copy of this effect with the dice string replaced, for an {@code effect-dice:} line. A second
     * {@code effect-dice:} for the same effect replaces the first, as C's handler frees the old dice
     * and keeps the new. The three expression fields are cleared to empty, because the dice C frees
     * take their bound expressions with them and the new dice start with none: an
     * {@code effect-expr:} line after the second {@code effect-dice:} binds afresh.
     *
     * <p>Function amendDice coded on 261119, updated on 261125, commented in full on 261125.
     *
     * @param diceString the dice string, still as text
     * @return the amended copy; this record is unchanged
     */
    public EffectParseRecordType amendDice(String diceString) {
        // The "" arguments are the expression fields: C's replacement dice carry no bound expressions.
        return new EffectParseRecordType(this.type(), this.typeInit(), this.subTypeWrapper(), this.radius(),
                this.other(), diceString, this.yVal(), this.xVal(), "",
                "", "", this.effectMessage(), this.line());
    }
    
    /**
     * A copy of this effect with the y and x parameters replaced together, for an {@code effect-yx:}
     * line, as {@link EffectParseRecord#amendYX} does. {@code PlayerTimedGrammar.g4} calls it for
     * its {@code effect-yx:} directive.
     *
     * <p>Function amendYX coded on 261119, updated on 261125, commented in full on 261125.
     *
     * @param yVal the new y parameter, still as text
     * @param xVal the new x parameter, still as text
     * @return the amended copy; this record is unchanged
     */
    public EffectParseRecordType amendYX(String yVal, String xVal) {
        return new EffectParseRecordType(this.type(), this.typeInit(), this.subTypeWrapper(), this.radius(),
                this.other(), this.diceString(), yVal, xVal, this.expressionChars(),
                this.expressionBases(), this.expressionOperations(), this.effectMessage(), this.line());
    }
    
    /**
     * A copy of this effect with the dice string and the three expression fields replaced as a set,
     * for an {@code effect-expr:} line, as {@link EffectParseRecord#amendExpression} does: the fields
     * are replaced, not added to, so a caller binding a further expression passes back what is held
     * with the new part joined on with {@code ^}. {@code PlayerTimedGrammar.g4} calls it for its
     * {@code effect-expr:} directive, and only once the effect holds dice.
     *
     * <p>Function amendExpression coded on 261119, updated on 261125, commented in full on 261125.
     *
     * @param diceStr             the dice string, still as text
     * @param expressionChar      the {@code ^}-delimited codes bound within the dice string
     * @param expressionBase      the {@code ^}-delimited base-type names, paired with the codes
     * @param expressionOperation the {@code ^}-delimited operation strings, paired with the codes
     * @return the amended copy; this record is unchanged
     */
    public EffectParseRecordType amendExpression(String diceStr,
                                                 String expressionChar,
                                                 String expressionBase,
                                                 String expressionOperation) {
        return new EffectParseRecordType(this.type(), this.typeInit(), this.subTypeWrapper(), this.radius(),
                this.other(), diceStr, this.yVal(), this.xVal(),
                expressionChar, expressionBase, expressionOperation, this.effectMessage(), this.line());
    }
    
    /**
     * A copy of this effect with {@code text} added to the end of the message, for an
     * {@code effect-msg:} line, as {@link EffectParseRecord#appendMessage} does: joined on with no
     * separator, and the text alone when there is no message yet. {@code PlayerTimedGrammar.g4}
     * calls it for its {@code effect-msg:} directive.
     *
     * <p>Function appendMessage coded on 261119, updated on 261125, commented in full on 261125.
     *
     * @param text the message text of the line, to add after any already held
     * @return the amended copy; this record is unchanged
     */
    public EffectParseRecordType appendMessage(String text) {
        // The null branch is not reached from PlayerTimedGrammar.g4, which seeds the message as "".
        // It stays to match EffectParseRecord.appendMessage and C's string_append on a NULL message.
        String joined = (this.effectMessage() == null) ? text : this.effectMessage() + text;
        return new EffectParseRecordType(this.type(), this.typeInit(), this.subTypeWrapper(), this.radius(),
                this.other(), this.diceString(), this.yVal(), this.xVal(),
                this.expressionChars(), this.expressionBases(), this.expressionOperations(), 
                joined, this.line());
    }
}
