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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verification of {@link EffectParseRecordType}: {@code convertToRecord} drops the chain tag and
 * carries the other twelve components across in order, and each amend method returns a new record,
 * leaves the original unchanged, replaces only the components it names and carries every other
 * component, {@code type} and {@code line} included. The record is built with thirteen distinct
 * values, so a swapped or dropped argument in the thirteen-field copy shows as a failure here; the
 * reader tests cannot see a swap of {@code radius} and {@code other}, because
 * {@code PlayerTimedGrammar.g4} always leaves both empty. The expected values come from the
 * Javadoc contract and from {@code player-timed.c} ({@code effect-yx} sets {@code y} and {@code x}
 * together, {@code string_append} joins with nothing between, and a second {@code effect-dice}
 * replaces the dice object, which frees the expressions bound to the old one), not from what the
 * Java returns.
 *
 * <p>Class EffectParseRecordTypeTest coded on 261125, commented in full on 261125.
 *
 * @author Rowan Crowther
 */
class EffectParseRecordTypeTest {

    private static EffectParseRecordType base() {
        return new EffectParseRecordType("begin", "TYPE", "SUB", "RAD", "OTH", "DICE", "Y", "X",
                "CH", "BASE", "OPS", "MSG", 42);
    }

    /**
     * The chain tag is dropped and the other twelve components land in the matching slots of the
     * plain record.
     *
     * <p>Function convertToRecordDropsTheTagAndCarriesTheRest coded on 261125, commented in full on
     * 261125.
     */
    @Test
    void convertToRecordDropsTheTagAndCarriesTheRest() {
        EffectParseRecordType original = base();

        EffectParseRecord plain = original.convertToRecord();

        assertEquals(new EffectParseRecord("TYPE", "SUB", "RAD", "OTH", "DICE", "Y", "X",
                "CH", "BASE", "OPS", "MSG", 42), plain);
        assertEquals(base(), original);
    }

    /**
     * Whichever tag the effect holds, the plain record is the same: the tag is not part of the
     * effect.
     *
     * <p>Function convertToRecordGivesTheSameRecordForEitherTag coded on 261125, commented in full
     * on 261125.
     */
    @Test
    void convertToRecordGivesTheSameRecordForEitherTag() {
        EffectParseRecordType end = new EffectParseRecordType("end", "TYPE", "SUB", "RAD", "OTH",
                "DICE", "Y", "X", "CH", "BASE", "OPS", "MSG", 42);

        assertEquals(base().convertToRecord(), end.convertToRecord());
    }

    /**
     * A second {@code effect-dice} replaces the dice object in C, so the new dice carry none of the
     * expressions bound to the old ones: the three expression fields are cleared. The other ten
     * components, the tag included, and the original are untouched.
     *
     * <p>Function amendDiceReplacesTheDiceAndClearsTheExpressions coded on 261125, commented in full
     * on 261125.
     */
    @Test
    void amendDiceReplacesTheDiceAndClearsTheExpressions() {
        EffectParseRecordType original = base();

        EffectParseRecordType amended = original.amendDice("NEWDICE");

        assertEquals(new EffectParseRecordType("begin", "TYPE", "SUB", "RAD", "OTH", "NEWDICE",
                "Y", "X", "", "", "", "MSG", 42), amended);
        assertEquals(base(), original);
        assertNotSame(original, amended);
    }

    /**
     * {@code effect-yx} replaces {@code y} and {@code x} together; the other eleven components, the
     * tag included, and the original are untouched.
     *
     * <p>Function amendYXReplacesOnlyYAndX coded on 261125, commented in full on 261125.
     */
    @Test
    void amendYXReplacesOnlyYAndX() {
        EffectParseRecordType original = base();

        EffectParseRecordType amended = original.amendYX("NEWY", "NEWX");

        assertEquals(new EffectParseRecordType("begin", "TYPE", "SUB", "RAD", "OTH", "DICE",
                "NEWY", "NEWX", "CH", "BASE", "OPS", "MSG", 42), amended);
        assertEquals(base(), original);
        assertNotSame(original, amended);
    }

    /**
     * {@code effect-expr} replaces the dice string and the three expression fields as a set; the
     * other nine components, the tag included, and the original are untouched.
     *
     * <p>Function amendExpressionReplacesTheDiceAndExpressionFieldsAsASet coded on 261125,
     * commented in full on 261125.
     */
    @Test
    void amendExpressionReplacesTheDiceAndExpressionFieldsAsASet() {
        EffectParseRecordType original = base();

        EffectParseRecordType amended = original.amendExpression("NEWDICE", "NEWCH", "NEWBASE", "NEWOPS");

        assertEquals(new EffectParseRecordType("begin", "TYPE", "SUB", "RAD", "OTH", "NEWDICE",
                "Y", "X", "NEWCH", "NEWBASE", "NEWOPS", "MSG", 42), amended);
        assertEquals(base(), original);
        assertNotSame(original, amended);
    }

    /**
     * {@code effect-msg} adds the text after the message already held, with nothing between
     * ({@code string_append}); the other twelve components, the tag included, and the original are
     * untouched.
     *
     * <p>Function appendMessageJoinsOntoTheHeldMessageWithNoSeparator coded on 261125, commented in
     * full on 261125.
     */
    @Test
    void appendMessageJoinsOntoTheHeldMessageWithNoSeparator() {
        EffectParseRecordType original = base();

        EffectParseRecordType amended = original.appendMessage("MORE");

        assertEquals(new EffectParseRecordType("begin", "TYPE", "SUB", "RAD", "OTH", "DICE",
                "Y", "X", "CH", "BASE", "OPS", "MSGMORE", 42), amended);
        assertEquals(base(), original);
        assertNotSame(original, amended);
    }

    /**
     * With an empty message ({@code PlayerTimedGrammar.g4} seeds it that way) the copy holds the
     * text alone, because joining onto nothing changes nothing.
     *
     * <p>Function appendMessageOnAnEmptyMessageHoldsTheTextAlone coded on 261125, commented in full
     * on 261125.
     */
    @Test
    void appendMessageOnAnEmptyMessageHoldsTheTextAlone() {
        EffectParseRecordType emptyMessage = new EffectParseRecordType("end", "TYPE", "SUB", "RAD",
                "OTH", "DICE", "Y", "X", "CH", "BASE", "OPS", "", 42);

        assertEquals("TEXT", emptyMessage.appendMessage("TEXT").effectMessage());
    }

    /**
     * With no message at all ({@code null}) the copy holds the text alone, as {@code string_append}
     * copies the second string when the first is NULL, and the original keeps its {@code null}.
     *
     * <p>Function appendMessageOnANullMessageHoldsTheTextAlone coded on 261125, commented in full
     * on 261125.
     */
    @Test
    void appendMessageOnANullMessageHoldsTheTextAlone() {
        EffectParseRecordType noMessage = new EffectParseRecordType("end", "TYPE", "SUB", "RAD",
                "OTH", "DICE", "Y", "X", "CH", "BASE", "OPS", null, 42);

        assertEquals("TEXT", noMessage.appendMessage("TEXT").effectMessage());
        assertNull(noMessage.effectMessage());
    }
}
