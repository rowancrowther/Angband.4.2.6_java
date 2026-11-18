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

package uk.co.jackoftradesltd.backend.parser.vault;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link VaultParseRecord}, the raw carrier of one {@code vault.txt} entry's
 * directive lines. C has no equivalent object (its {@code parse_vault_*} handlers fill the
 * {@code struct vault} line by line), so these pin only what the record promises: it keeps the
 * directives it was given, in order, untouched and unchecked.
 *
 * <p>Class VaultParseRecordTest coded on 261118, commented in full on 261118.
 *
 * @author Rowan Crowther
 */
class VaultParseRecordTest {

    private static Directive d(String name, int line, String... values) {
        return new Directive(name, List.of(values), line);
    }

    /**
     * The directives come back in the order given, and the list is the one passed in.
     */
    @Test
    void keepsTheDirectivesInFileOrder() {
        List<Directive> lines = List.of(d("name", 1, "Round"), d("type", 2, "Lesser vault"),
                d("D", 3, "####"));

        VaultParseRecord record = new VaultParseRecord(lines);

        assertSame(lines, record.directives());
        assertEquals("name", record.directives().get(0).name());
        assertEquals("D", record.directives().get(2).name());
    }

    /**
     * A repeated directive is kept every time: C appends each {@code flags:} line to the same set
     * and each {@code D:} line to the same text, so the record may not collapse them.
     */
    @Test
    void repeatedDirectivesAreAllKept() {
        VaultParseRecord record = new VaultParseRecord(List.of(d("name", 1, "Round"),
                d("flags", 2, "MAX"), d("flags", 3, "FEW_ENTRANCES"),
                d("D", 4, "##"), d("D", 5, "##")));

        assertEquals(2, record.directives().stream().filter(x -> x.name().equals("flags")).count());
        assertEquals(2, record.directives().stream().filter(x -> x.name().equals("D")).count());
    }

    /**
     * The record checks nothing: an empty list, or a record with no {@code name:}, is held as is,
     * leaving the decision to the assembler.
     */
    @Test
    void checksNothingAboutTheListItHolds() {
        assertTrue(new VaultParseRecord(List.of()).directives().isEmpty());
        assertEquals(1, new VaultParseRecord(List.of(d("rows", 4, "3"))).directives().size());
    }

    /**
     * Records are value objects: two over equal lists are equal.
     */
    @Test
    void equalListsGiveEqualRecords() {
        assertEquals(new VaultParseRecord(List.of(d("name", 1, "Round"))),
                new VaultParseRecord(List.of(d("name", 1, "Round"))));
        assertNotEquals(new VaultParseRecord(List.of(d("name", 1, "Round"))),
                new VaultParseRecord(List.of(d("name", 2, "Round"))));
    }
}
