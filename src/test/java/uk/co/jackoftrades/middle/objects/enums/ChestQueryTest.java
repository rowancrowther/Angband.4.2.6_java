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

package uk.co.jackoftrades.middle.objects.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link ChestQuery}, the port of C's {@code enum chest_query} ({@code obj-chest.h:26-30}).
 *
 * <p>A three-constant enum with no behaviour, so what is testable is its shape — and the shape is
 * what matters here. C's {@code chest_check} switches on this value ({@code obj-chest.c:434-447})
 * and the port will do the same, so a constant added, removed or reordered changes which branch a
 * caller means. Pinning the set and the order makes that a test failure rather than a silent change
 * of behaviour in whatever switches on it later.
 *
 * <p>Class ChestQueryTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class ChestQueryTest {

    /**
     * Three constants, in C's declaration order. The three are genuinely different questions —
     * anything, still worth opening, and known to be trapped — so an extra or missing one would mean
     * the port had drifted from the switch it mirrors.
     */
    @Test
    @DisplayName("the constants match C's enum, in order")
    void constantsMatchC() {
        assertAll(
                () -> assertEquals(3, ChestQuery.values().length),
                () -> assertEquals(ChestQuery.CHEST_ANY, ChestQuery.values()[0]),
                () -> assertEquals(ChestQuery.CHEST_OPENABLE, ChestQuery.values()[1]),
                () -> assertEquals(ChestQuery.CHEST_TRAPPED, ChestQuery.values()[2]));
    }
}
