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
 * Verification of the three amend methods of {@link EffectParseRecord}: each returns a new record,
 * leaves the original unchanged, replaces only the components it names and carries every other
 * component, {@code line} included, across. The record is built with twelve distinct values so a
 * swapped or dropped argument in the twelve-field copy shows as a failure here, not as a distant
 * reader failure. The expected values come from the Javadoc contract and from C's handlers
 * ({@code effect-yx} sets {@code y} and {@code x} together, {@code string_append} joins with
 * nothing between), not from what the Java returns.
 *
 * <p>Class EffectParseRecordTest coded on 261125, commented in full on 261125.
 *
 * @author Rowan Crowther
 */
class EffectParseRecordTest {

    private static EffectParseRecord base() {
        return new EffectParseRecord("TYPE", "SUB", "RAD", "OTH", "DICE", "Y", "X",
                "CH", "BASE", "OPS", "MSG", 42);
    }

    /**
     * {@code effect-yx} replaces {@code y} and {@code x} together; the other ten components and the
     * original are untouched.
     *
     * <p>Function amendYXReplacesOnlyYAndX coded on 261125, commented in full on 261125.
     */
    @Test
    void amendYXReplacesOnlyYAndX() {
        EffectParseRecord original = base();

        EffectParseRecord amended = original.amendYX("NEWY", "NEWX");

        assertEquals(new EffectParseRecord("TYPE", "SUB", "RAD", "OTH", "DICE", "NEWY", "NEWX",
                "CH", "BASE", "OPS", "MSG", 42), amended);
        assertEquals(base(), original);
        assertNotSame(original, amended);
    }

    /**
     * {@code dice:} and {@code expr:} replace the dice string and the three expression fields as a
     * set, not added to; the other eight components and the original are untouched.
     *
     * <p>Function amendExpressionReplacesTheDiceAndExpressionFieldsAsASet coded on 261125,
     * commented in full on 261125.
     */
    @Test
    void amendExpressionReplacesTheDiceAndExpressionFieldsAsASet() {
        EffectParseRecord original = base();

        EffectParseRecord amended = original.amendExpression("NEWDICE", "NEWCH", "NEWBASE", "NEWOPS");

        assertEquals(new EffectParseRecord("TYPE", "SUB", "RAD", "OTH", "NEWDICE", "Y", "X",
                "NEWCH", "NEWBASE", "NEWOPS", "MSG", 42), amended);
        assertEquals(base(), original);
        assertNotSame(original, amended);
    }

    /**
     * {@code effect-msg} adds the text after the message already held, with nothing between
     * ({@code string_append}); the other eleven components and the original are untouched.
     *
     * <p>Function appendMessageJoinsOntoTheHeldMessageWithNoSeparator coded on 261125, commented in
     * full on 261125.
     */
    @Test
    void appendMessageJoinsOntoTheHeldMessageWithNoSeparator() {
        EffectParseRecord original = base();

        EffectParseRecord amended = original.appendMessage("MORE");

        assertEquals(new EffectParseRecord("TYPE", "SUB", "RAD", "OTH", "DICE", "Y", "X",
                "CH", "BASE", "OPS", "MSGMORE", 42), amended);
        assertEquals(base(), original);
        assertNotSame(original, amended);
    }

    /**
     * With no message yet ({@code null}, as most grammars start it) the copy holds the text alone,
     * as {@code string_append} copies {@code s2} when {@code s1} is NULL.
     *
     * <p>Function appendMessageOnANullMessageHoldsTheTextAlone coded on 261125, commented in full
     * on 261125.
     */
    @Test
    void appendMessageOnANullMessageHoldsTheTextAlone() {
        EffectParseRecord noMessage = new EffectParseRecord("TYPE", "SUB", "RAD", "OTH", "DICE",
                "Y", "X", "CH", "BASE", "OPS", null, 42);

        assertEquals("TEXT", noMessage.appendMessage("TEXT").effectMessage());
        assertNull(noMessage.effectMessage());
    }

    /**
     * With an empty message (as timed effects and the extra effects of traps start it) the copy
     * also holds the text alone, because joining onto nothing changes nothing.
     *
     * <p>Function appendMessageOnAnEmptyMessageHoldsTheTextAlone coded on 261125, commented in full
     * on 261125.
     */
    @Test
    void appendMessageOnAnEmptyMessageHoldsTheTextAlone() {
        EffectParseRecord emptyMessage = new EffectParseRecord("TYPE", "SUB", "RAD", "OTH", "DICE",
                "Y", "X", "CH", "BASE", "OPS", "", 42);

        assertEquals("TEXT", emptyMessage.appendMessage("TEXT").effectMessage());
    }
}
