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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ChestTrapCode} against the figures C produces: the {@code code:} lines of
 * {@code chest_trap.txt} in file order, and the pval that {@code parse_chest_trap_name} in
 * {@code obj-chest.c} gives each record (1 for the first, then double the previous one).
 *
 * <p>{@code ChestTrapTest} already pins the generic properties (distinct bits, successive powers of
 * two, {@code NO_TRAP} first). This class pins the concrete table, so a reordered or inserted
 * constant fails by name rather than only by arithmetic.
 *
 * <p>Class ChestTrapCodeTest coded on 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class ChestTrapCodeTest {

    /** The {@code code:} tokens of {@code chest_trap.txt}, in the order the file lists them. */
    private static final List<String> FILE_CODES =
            List.of("NO_TRAP", "POISON", "LOSE_STR", "LOSE_CON", "SUMMON", "PARALYZE", "EXPLODE");

    @Test
    void constantsAreTheFileCodesInFileOrder() {
        assertEquals(FILE_CODES, Arrays.stream(ChestTrapCode.values()).map(Enum::name).toList());
    }

    @Test
    void eachConstantCarriesTheBitCGivesItsRecord() {
        // C: first record pval = 1, each later record = previous * 2.
        assertEquals(1, ChestTrapCode.NO_TRAP.getPval());
        assertEquals(2, ChestTrapCode.POISON.getPval());
        assertEquals(4, ChestTrapCode.LOSE_STR.getPval());
        assertEquals(8, ChestTrapCode.LOSE_CON.getPval());
        assertEquals(16, ChestTrapCode.SUMMON.getPval());
        assertEquals(32, ChestTrapCode.PARALYZE.getPval());
        assertEquals(64, ChestTrapCode.EXPLODE.getPval());
    }

    @Test
    void valueOfResolvesEveryFileCodeAndRejectsOthers() {
        for (String code : FILE_CODES) {
            assertEquals(code, ChestTrapCode.valueOf(code).name());
        }
        assertThrows(IllegalArgumentException.class, () -> ChestTrapCode.valueOf("poison"));
        assertThrows(IllegalArgumentException.class, () -> ChestTrapCode.valueOf(""));
    }

    @Test
    void maxTrapsIsTheFourteenOfTheDataFileHeader() {
        // chest_trap.txt: "There should be no more than 14 traps total".
        assertEquals(14, ChestTrapCode.getMaxTraps());
        assertEquals(7, ChestTrapCode.values().length);
    }

    @Test
    void fourteenTrapsStillFitTheSignedSixteenBitPval() {
        // The boundary of the cap: bits 0..13 are 1..8192, and all fourteen together are 16383,
        // inside int16_t (the chest pval) with the top bits clear. A fifteenth would be 16384, still
        // positive, but the sixteenth bit would be the sign - the reason for the header's limit.
        int fourteenth = 1 << (ChestTrapCode.getMaxTraps() - 1);
        assertEquals(8192, fourteenth);
        assertEquals(16383, (fourteenth << 1) - 1);
        assertTrue((fourteenth << 1) - 1 <= Short.MAX_VALUE);
    }

    @Test
    void aChestPvalOfExactlyOneIsLockedAndUntrapped() {
        // is_trapped_chest: pval <= 0 -> false; pval == 1 -> false; otherwise true.
        assertEquals(1, ChestTrapCode.NO_TRAP.getPval());
        for (ChestTrapCode code : ChestTrapCode.values()) {
            if (code != ChestTrapCode.NO_TRAP) {
                assertTrue(code.getPval() > 1, code::name);
            }
        }
    }

    @Test
    void allBitsOrTogetherToOneBelowTheNextPowerOfTwo() {
        int all = Arrays.stream(ChestTrapCode.values()).mapToInt(ChestTrapCode::getPval)
                .reduce(0, (a, b) -> a | b);
        assertEquals(127, all);
    }
}
