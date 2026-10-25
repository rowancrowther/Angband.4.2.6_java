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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindAssembler;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindParseRecord;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourParseRecord;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link FlavourKindAssembler}'s reading of a numeric {@code fixed:} sval against C's
 * {@code lookup_sval} ({@code obj-util.c}) as {@code parse_flavor_flavor} ({@code init.c}) uses it.
 *
 * <p>C reads the symbol with {@code strtoul} in base 10. If any digits were read, the answer is the
 * number itself, with no check that a kind has it, provided the rest of the text is only spaces or
 * tabs and the number is below {@code INT_MAX}; otherwise the answer is -1. The result is stored in
 * the {@code uint8_t sval} of {@code struct flavor}, so it is reduced modulo 256 and -1 reads as 255.
 * Text with no leading digits is matched against kind names instead, and a miss is -1 again.
 *
 * <p>The object-kind table is seeded with one kind, {@code Ring of Power}, which
 * {@code addObjectKind} gives sval 0 (the index is keyed on that, so the sval is left alone), and
 * restored afterwards. Every other figure therefore has no kind behind it.
 *
 * <p>Class FlavourKindAssemblerNumericSvalTest coded on 261025, commented in full on 261025.
 *
 * @author Rowan Crowther
 */
class FlavourKindAssemblerNumericSvalTest {

    private Object savedObjectKinds;
    private Object savedKindsByTvalSval;

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    @BeforeEach
    void seed() throws Exception {
        savedObjectKinds = setStatic("objectKinds", new ArrayList<ObjectKind>());
        savedKindsByTvalSval = setStatic("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        ObjectKind ring = new ObjectKind(null, 0, 0, 0, 0, "Ring of Power", TValue.TV_RING,
                "Ring of Power", null, false);
        ObjectRegistry.addObjectKind(ring);
    }

    @AfterEach
    void restore() throws Exception {
        setStatic("objectKinds", savedObjectKinds);
        setStatic("kindsByTvalSval", savedKindsByTvalSval);
    }

    private static int svalOf(String symbol) {
        FlavourParseRecord fixed = new FlavourParseRecord("1", symbol, "Yellow", "Foo", 2);
        FlavourKindParseRecord record = new FlavourKindParseRecord("ring", "=", List.of(fixed), 1);
        List<FlavourKind> kinds = new FlavourKindAssembler().assemble(List.of(record), new ArrayList<>(),
                "flavor.txt");
        return kinds.getFirst().getFlavours().getFirst().getsVal();
    }

    /**
     * Plain digits: the number is the answer whether or not a kind carries it, then wrapped to eight
     * bits. 2147483646 is the largest accepted figure ({@code r < INT_MAX}), and 2147483647 and above
     * are -1, which is 255.
     */
    @ParameterizedTest(name = "digits {0} -> {1}")
    @CsvSource({
            "7,7",
            "5,5",
            "0,0",
            "007,7",
            "255,255",
            "256,0",
            "300,44",
            "2147483646,254",
            "2147483647,255",
            "2147483648,255",
            "4294967296,255",
            "99999999999999999999,255"
    })
    void aRunOfDigitsIsTheSvalWrappedToEightBits(String symbol, int expected) {
        assertEquals(expected, svalOf(symbol));
    }

    /**
     * Text that begins with a sign or digits is a number to {@code strtoul}: a leading {@code +} is
     * accepted, anything else after the digits makes the answer -1, and a leading {@code -} wraps
     * the unsigned result far above {@code INT_MAX}, which is -1 as well.
     */
    @ParameterizedTest(name = "[{0}] -> {1}")
    @CsvSource(value = {
            "+5|5",
            "12abc|255",
            "-1|255",
            "-5|255"
    }, delimiter = '|')
    void textStrtoulReadsAsANumber(String symbol, int expected) {
        assertEquals(expected, svalOf(symbol));
    }

    /**
     * Accepted deviation (2026-10-25, "A {@code fixed:} sval with trailing spaces or tabs is looked up
     * as a name" in {@code docs/Accepted_Deviations.md}). C's {@code contains_only_spaces} lets a tail
     * of spaces and tabs through, so {@code lookup_sval} answers 5 for {@code "5 "}; Java's
     * {@code Integer.parseInt} refuses the tail, the symbol is matched as a name, and the flavour
     * stores 255. The expected values here are Java's, not C's.
     */
    @ParameterizedTest(name = "[{0}] -> 255")
    @CsvSource(value = {
            "'5 '",
            "'5\t'"
    })
    void aTailOfSpacesOrTabsMakesTheSymbolANameNotANumber(String symbol) {
        assertEquals(255, svalOf(symbol));
    }
}
