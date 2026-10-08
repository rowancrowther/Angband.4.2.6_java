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
import uk.co.jackoftradesltd.channel.utils.Flag;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GetItemFlags} against the twelve {@code #define}s under "Bit flags for get_item()
 * function" in {@code game-input.h}: the names, their order, and the bit each one holds in C's
 * {@code int} mode (0x0001 for {@code USE_EQUIP} doubling up to 0x0800 for {@code SHOW_THROWING}).
 *
 * <p>The Java enum stores no mask, so the declaration order is the only record of the bit; these tests
 * pin it, and pin that a {@link Flag} over the enum behaves like C's {@code mode & ~(...)} when
 * {@code cmd_get_item} strips the container bits from a shapechanged player.
 *
 * <p>Class GetItemFlagsTest coded on 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class GetItemFlagsTest {

    /** The names in {@code game-input.h}, in the order the header lists them. */
    private static final List<String> HEADER_NAMES = List.of(
            "USE_EQUIP", "USE_INVEN", "USE_FLOOR", "USE_QUIVER", "IS_HARMLESS", "SHOW_PRICES",
            "SHOW_FAIL", "SHOW_QUIVER", "SHOW_EMPTY", "QUIVER_TAGS", "SHOW_RECHARGE", "SHOW_THROWING");

    /** The values in {@code game-input.h}, in the same order. */
    private static final int[] HEADER_BITS = {
            0x0001, 0x0002, 0x0004, 0x0008, 0x0010, 0x0020, 0x0040, 0x0080, 0x0100, 0x0200, 0x0400, 0x0800};

    @Test
    void constantsAreTheHeaderNamesInHeaderOrder() {
        assertEquals(HEADER_NAMES, Arrays.stream(GetItemFlags.values()).map(Enum::name).toList());
    }

    @Test
    void ordinalIsTheBitPositionOfTheCDefine() {
        for (GetItemFlags flag : GetItemFlags.values()) {
            assertEquals(HEADER_BITS[flag.ordinal()], 1 << flag.ordinal(), flag.name());
        }
        assertEquals(0x0001, 1 << GetItemFlags.USE_EQUIP.ordinal());
        assertEquals(0x0008, 1 << GetItemFlags.USE_QUIVER.ordinal());
        assertEquals(0x0400, 1 << GetItemFlags.SHOW_RECHARGE.ordinal());
        assertEquals(0x0800, 1 << GetItemFlags.SHOW_THROWING.ordinal());
    }

    @Test
    void twelveFlagsOccupyTheLowTwelveBitsOfTheCMode() {
        assertEquals(12, GetItemFlags.values().length);
        assertEquals(0x0FFF, Arrays.stream(HEADER_BITS).reduce(0, (a, b) -> a | b));
    }

    @Test
    void valueOfRejectsOtherSpellings() {
        assertEquals(GetItemFlags.SHOW_EMPTY, GetItemFlags.valueOf("SHOW_EMPTY"));
        assertThrows(IllegalArgumentException.class, () -> GetItemFlags.valueOf("show_empty"));
        assertThrows(IllegalArgumentException.class, () -> GetItemFlags.valueOf("USE_ALL"));
    }

    @Test
    void shapechangedStripLeavesTheFloorAndTheDisplayBits() {
        // C: mode &= ~(USE_EQUIP | USE_INVEN | USE_QUIVER) from do_cmd_inscribe's whole-mode argument.
        Flag<GetItemFlags> mode = new Flag<>(GetItemFlags.class);
        mode.on(GetItemFlags.USE_EQUIP);
        mode.on(GetItemFlags.USE_INVEN);
        mode.on(GetItemFlags.USE_QUIVER);
        mode.on(GetItemFlags.USE_FLOOR);
        mode.on(GetItemFlags.IS_HARMLESS);

        mode.off(GetItemFlags.USE_EQUIP);
        mode.off(GetItemFlags.USE_INVEN);
        mode.off(GetItemFlags.USE_QUIVER);

        assertFalse(mode.has(GetItemFlags.USE_EQUIP));
        assertFalse(mode.has(GetItemFlags.USE_INVEN));
        assertFalse(mode.has(GetItemFlags.USE_QUIVER));
        assertTrue(mode.has(GetItemFlags.USE_FLOOR));
        assertTrue(mode.has(GetItemFlags.IS_HARMLESS));
    }
}
