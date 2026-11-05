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

package uk.co.jackoftradesltd.channel.parser;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct unit tests for {@link ParseResult}, the generic result holder every list-shaped
 * {@code lib/gamedata} reader wraps its output in.
 *
 * <p>There is no single C original for this record. C's {@code parse_file()} in
 * {@code datafile.c} stops at the first parser error and never returns a partial list; the
 * records reach the game only through each {@code finish} hook. With soft errors now fatal,
 * the Java holder matches that: it carries the items and nothing else. These tests pin that
 * contract - the record has exactly one component, hands back the very list it was given
 * (no defensive copy, no reordering), and imposes no null check of its own.
 *
 * @author Rowan Crowther
 */
class ParseResultTest {
    @Test
    void recordHasOnlyAnItemsComponent() {
        var components = ParseResult.class.getRecordComponents();

        assertEquals(1, components.length);
        assertEquals("items", components[0].getName());
        assertEquals(List.class, components[0].getType());
    }

    @Test
    void itemsReturnsTheSameListInstance() {
        List<String> items = List.of("a", "b", "c");

        ParseResult<String> result = new ParseResult<>(items);

        assertSame(items, result.items());
    }

    @Test
    void itemsKeepsAssemblerOrder() {
        List<String> items = List.of("third", "first", "second");

        assertEquals(List.of("third", "first", "second"), new ParseResult<>(items).items());
    }

    @Test
    void itemsIsNotADefensiveCopy() {
        List<String> items = new ArrayList<>(List.of("a"));
        ParseResult<String> result = new ParseResult<>(items);

        items.add("b");

        assertEquals(List.of("a", "b"), result.items());
    }

    @Test
    void emptyItemsAreHeldAsEmpty() {
        ParseResult<String> result = new ParseResult<>(Collections.emptyList());

        assertTrue(result.items().isEmpty());
    }

    @Test
    void nullItemsAreAcceptedUnchecked() {
        assertNull(new ParseResult<String>(null).items());
    }

    @Test
    void equalItemsGiveEqualResults() {
        ParseResult<String> a = new ParseResult<>(List.of("x", "y"));
        ParseResult<String> b = new ParseResult<>(new ArrayList<>(List.of("x", "y")));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new ParseResult<>(List.of("y", "x")));
    }
}
