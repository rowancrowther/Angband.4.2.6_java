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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffect2ParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests of {@link BlowEffect2ParseRecord}: that it hands back the {@link Directive}s it was given,
 * in the order given, with a repeated line kept whole. It holds a list and nothing else, so these
 * tests build the list by hand; no grammar writes this record yet.
 *
 * <p>The expected values come from C's {@code mon-init.c}. {@code init_parse_eff} registers ten
 * directives ({@code name}, {@code power}, {@code eval}, {@code desc}, {@code lore-color-base},
 * {@code lore-color-resist}, {@code lore-color-immune}, {@code effect-type}, {@code resist} and
 * {@code lash-type}). {@code parse_eff_resist} reads the {@code effect_type} that an earlier line
 * stored, so a {@code resist:} line above {@code effect-type:} is a different record from the
 * reverse. {@code parse_eff_desc} appends with {@code string_append}, so two {@code desc:} lines
 * are two lines to the record, not one.
 *
 * <p>Class BlowEffect2ParseRecordTest coded on 261116, commented in full on 261116.
 *
 * @author Rowan Crowther
 */
class BlowEffect2ParseRecordTest {

    /**
     * Builds one directive with a single value, as every {@code blow_effects.txt} directive has.
     *
     * <p>Function directive coded on 261116, commented in full on 261116.
     *
     * @param name  the directive's name as C registers it
     * @param value the raw value
     * @param line  the 1-based line
     * @return the directive
     */
    private static Directive directive(String name, String value, int line) {
        return new Directive(name, List.of(value), line);
    }

    /**
     * The record returns the very list it was given, so nothing is copied, reordered or dropped.
     *
     * <p>Function theRecordHoldsTheListItWasGiven coded on 261116, commented in full on 261116.
     */
    @Test
    @DisplayName("the record holds the list it was given, in the order given")
    void theRecordHoldsTheListItWasGiven() {
        List<Directive> lines = List.of(
                directive("name", "ACID", 1),
                directive("power", "0", 2),
                directive("eval", "20", 3),
                directive("desc", "acid", 4));

        BlowEffect2ParseRecord record = new BlowEffect2ParseRecord(lines);

        assertSame(lines, record.directives());
        assertEquals(List.of("name", "power", "eval", "desc"),
                record.directives().stream().map(Directive::name).toList());
    }

    /**
     * {@code resist:} above and below {@code effect-type:} give two different records, because C's
     * {@code parse_eff_resist} reads the type stored so far.
     *
     * <p>Function resistBeforeAndAfterEffectTypeAreDifferentRecords coded on 261116, commented in
     * full on 261116.
     */
    @Test
    @DisplayName("resist: above effect-type: is a different record from resist: below it")
    void resistBeforeAndAfterEffectTypeAreDifferentRecords() {
        BlowEffect2ParseRecord after = new BlowEffect2ParseRecord(List.of(
                directive("name", "ACID", 1),
                directive("effect-type", "element", 2),
                directive("resist", "ACID", 3)));
        BlowEffect2ParseRecord before = new BlowEffect2ParseRecord(List.of(
                directive("name", "ACID", 1),
                directive("resist", "ACID", 2),
                directive("effect-type", "element", 3)));

        assertEquals(List.of("name", "effect-type", "resist"),
                after.directives().stream().map(Directive::name).toList());
        assertEquals(List.of("name", "resist", "effect-type"),
                before.directives().stream().map(Directive::name).toList());
    }

    /**
     * Two {@code desc:} lines are two entries, in file order, where one final value would have
     * lost the first. C appends the second to the first.
     *
     * <p>Function aRepeatedDescLineIsKeptWhole coded on 261116, commented in full on 261116.
     */
    @Test
    @DisplayName("a repeated desc: is two entries, each with its own line")
    void aRepeatedDescLineIsKeptWhole() {
        BlowEffect2ParseRecord record = new BlowEffect2ParseRecord(List.of(
                directive("name", "FIRE", 10),
                directive("desc", "burn", 11),
                directive("desc", " you", 12)));

        List<Directive> descs = record.directives().stream()
                .filter(d -> d.name().equals("desc")).toList();
        assertEquals(2, descs.size());
        assertEquals("burn", descs.get(0).values().getFirst());
        assertEquals(11, descs.get(0).line());
        assertEquals(" you", descs.get(1).values().getFirst());
        assertEquals(12, descs.get(1).line());
    }

    /**
     * Every directive C registers for a blow effect can sit in the list with its own name, one
     * value and its line.
     *
     * <p>Function everyDirectiveCRegistersIsCarriedByName coded on 261116, commented in full on
     * 261116.
     */
    @Test
    @DisplayName("each of the ten directives C registers is carried with its name, value and line")
    void everyDirectiveCRegistersIsCarriedByName() {
        String[] names = {"name", "power", "eval", "desc", "lore-color-base", "lore-color-resist",
                "lore-color-immune", "effect-type", "resist", "lash-type"};
        List<Directive> lines = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            lines.add(directive(names[i], "v" + i, i + 1));
        }

        BlowEffect2ParseRecord record = new BlowEffect2ParseRecord(lines);

        assertEquals(10, record.directives().size());
        for (int i = 0; i < names.length; i++) {
            Directive d = record.directives().get(i);
            assertEquals(names[i], d.name());
            assertEquals(List.of("v" + i), d.values());
            assertEquals(i + 1, d.line());
        }
    }
}
