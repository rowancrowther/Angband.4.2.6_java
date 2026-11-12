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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Runs a range of {@code pain:} values through {@link MonsterBaseReader} and compares each with what
 * C's {@code parse_mon_base_pain} does with it: {@code parser_getuint} cuts the field at {@code :},
 * reads it with {@code strtoul} in base 0 (so a {@code 0x} prefix is hex, a leading {@code 0} is
 * octal, and the first unusable character ends the number with the rest ignored), refuses a field
 * whose first character is {@code -} or that holds no digit, and then refuses an index at or above
 * {@code mp_max} (13 for the shipped {@code pain.txt}, whose types run 1 to 12).
 *
 * <p>Each case is its own dynamic test, so a run reports every value rather than stopping at the
 * first. Expected values are C's, not the port's: a case that fails is a difference from C. Two are
 * left out on purpose because they are recorded deviations: index {@code 0} (C's blank slot 0, see
 * {@code MonsterRegistry}) and numbers outside the {@code int} range.
 *
 * <p>Test class MonsterBasePainValueTest coded on 261112.
 *
 * @author Rowan Crowther
 */
class MonsterBasePainValueTest {

    private static final String PAIN_FILE = "lib/gamedata/pain.txt";

    private static Object savedMonsterPains;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void loadPainSets() throws Exception {
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();
        Field field = MonsterRegistry.class.getDeclaredField("monsterPains");
        field.setAccessible(true);
        savedMonsterPains = field.get(null);
        field.set(null, pains);
    }

    @AfterAll
    static void restorePainSets() throws Exception {
        Field field = MonsterRegistry.class.getDeclaredField("monsterPains");
        field.setAccessible(true);
        field.set(null, savedMonsterPains);
    }

    /**
     * One {@code pain:} value and what C does with it: the index it lands on, or {@code null} when
     * C refuses the line.
     */
    private record Case(String text, Integer cIndex) {
    }

    private static int painIndexOf(MonsterBase base) throws Exception {
        Field field = MonsterBase.class.getDeclaredField("pain");
        field.setAccessible(true);
        return ((MonsterPain) field.get(base)).getPainIndex();
    }

    private ParseResult<MonsterBase> load(String painText) throws Exception {
        Path file = tempDir.resolve("pain-value-" + Math.abs(painText.hashCode()) + ".txt");
        Files.writeString(file, "name:orc\nglyph:o\npain:" + painText + "\nflags:ANIMAL\ndesc:Orc\n");
        return new MonsterBaseReader().parseWithResults(file.toString());
    }

    @TestFactory
    List<DynamicTest> everyPainValueReadsAsCReadsIt() {
        List<Case> cases = List.of(
                // plain decimal, the only form the shipped data uses
                new Case("1", 1),
                new Case("12", 12),
                new Case("13", null),          // mp_max is 13, so >= mp_max is out of bounds
                // sign
                new Case("+5", 5),
                new Case("-5", null),          // a leading '-' is refused
                new Case("-0", null),          // refused too, though its value is 0
                // radix, as strtoul base 0
                new Case("0x5", 5),
                new Case("0X0c", 12),
                new Case("010", 8),            // leading 0 is octal
                new Case("012", 10),
                new Case("0x10", null),        // 16 is out of bounds
                // tail: strtoul stops at the first unusable character and ignores the rest
                new Case("5abc", 5),
                new Case("5 ", 5),             // a trailing blank, which shipped files do carry
                new Case("5\t", 5),
                new Case("7z", 7),
                new Case("08", null),          // C reads 0 (octal stops at '8'), the blank slot the port refuses
                // no digit at all: strtoul finds nothing to use in base 0, so C refuses
                new Case("ff", null),
                new Case("abc", null)
        );

        List<DynamicTest> tests = new ArrayList<>();
        for (Case c : cases) {
            tests.add(DynamicTest.dynamicTest("pain:" + c.text().replace("\t", "<TAB>"), () -> {
                if (c.cIndex() == null) {
                    assertThrows(Exception.class, () -> load(c.text()),
                            "C refuses pain:" + c.text() + " but the port loaded it");
                } else {
                    ParseResult<MonsterBase> result = load(c.text());
                    assertEquals(1, result.items().size());
                    assertEquals(c.cIndex(), painIndexOf(result.items().get(0)),
                            "C reads pain:" + c.text() + " as " + c.cIndex());
                }
            }));
        }
        return tests;
    }
}
