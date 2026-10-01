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

package uk.co.jackoftradesltd.middle.game;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.hint.HintAssembler;
import uk.co.jackoftradesltd.backend.parser.hint.HintParseRecord;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Hint}, the port of C {@code struct hint} ({@code hint.h}): one line of text, with
 * the C {@code next} pointer replaced by list membership.
 *
 * <p>Test class written on 261001.
 *
 * @author Rowan Crowther
 */
class HintTest {

    /**
     * C {@code parse_hint} stores the {@code H:} text unchanged ({@code string_make}); the getter
     * must hand back exactly what the constructor was given.
     *
     * <p>Test written on 261001.
     */
    @Test
    void getHintReturnsConstructorTextVerbatim() {
        String text = "Staves can be used even when confused and blinded.";

        assertEquals(text, new Hint(text).getHint());
    }

    /**
     * {@code string_make} copies without trimming, and a real line in {@code hints.txt} ends in a
     * trailing space ("...from being scared. "); whitespace must survive.
     *
     * <p>Test written on 261001.
     */
    @Test
    void trailingWhitespaceIsPreserved() {
        assertEquals("scared. ", new Hint("scared. ").getHint());
    }

    /**
     * An empty string is a legal text for a {@code string_make} copy; no null or default substitution.
     *
     * <p>Test written on 261001.
     */
    @Test
    void emptyTextIsKept() {
        assertEquals("", new Hint("").getHint());
    }

    /**
     * C builds its list by prepending (reverse file order) but {@code random_hint} is a uniform
     * reservoir sample, so order is immaterial there; the port's contract is file order, one hint per
     * record, with no merging of duplicates.
     *
     * <p>Test written on 261001.
     */
    @Test
    void assemblerKeepsFileOrderAndDuplicates() {
        List<HintParseRecord> records = new ArrayList<>();
        records.add(new HintParseRecord("one", 1));
        records.add(new HintParseRecord("two", 1));
        records.add(new HintParseRecord("one", 1));

        List<Hint> hints = new HintAssembler().assemble(records, new ArrayList<>());

        assertEquals(3, hints.size());
        assertEquals("one", hints.get(0).getHint());
        assertEquals("two", hints.get(1).getHint());
        assertEquals("one", hints.get(2).getHint());
    }
}
