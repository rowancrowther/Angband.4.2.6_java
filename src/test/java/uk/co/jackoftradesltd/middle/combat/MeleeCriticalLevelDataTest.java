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

package uk.co.jackoftradesltd.middle.combat;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.GameConstantsParseResult;
import uk.co.jackoftradesltd.backend.parser.GameConstantsReader;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.MeleeCriticalLevelData;
import uk.co.jackoftradesltd.middle.game.globals.data.RangedCriticalLevelData;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the Java form of C's {@code struct critical_level}: {@link MeleeCriticalLevelData} and
 * {@link RangedCriticalLevelData}, which replaced the unused {@code CriticalLevel} class. Expected values are the
 * rows of {@code melee-critical-level} and {@code ranged-critical-level} in the shipped
 * {@code lib/gamedata/constants.txt}, not read back off the Java implementation.
 *
 * @author Rowan Crowther
 */
class MeleeCriticalLevelDataTest {

    private static GameConstantsData load() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader().parseWithResults("lib/gamedata/constants.txt");
        return result.getData();
    }

    @Test
    void meleeRowsKeepFileOrderAndValues() throws IOException {
        var rows = load().meleeCriticalLevel();
        assertEquals(new MeleeCriticalLevelData(400, 2, 5, MessageType.MSG_HIT_GOOD), rows.get(0));
        assertEquals(new MeleeCriticalLevelData(700, 2, 10, MessageType.MSG_HIT_GREAT), rows.get(1));
        assertEquals(new MeleeCriticalLevelData(900, 3, 15, MessageType.MSG_HIT_SUPERB), rows.get(2));
        assertEquals(new MeleeCriticalLevelData(1300, 3, 20, MessageType.MSG_HIT_HI_GREAT), rows.get(3));
        // The last cut-off is -1 and is ignored by C, but it is still stored.
        assertEquals(new MeleeCriticalLevelData(-1, 4, 20, MessageType.MSG_HIT_HI_SUPERB), rows.get(4));
    }

    @Test
    void rangedRowsKeepFileOrderAndValues() throws IOException {
        var rows = load().rangedCriticalLevel();
        assertEquals(new RangedCriticalLevelData(500, 2, 5, MessageType.MSG_HIT_GOOD), rows.get(0));
        assertEquals(new RangedCriticalLevelData(1000, 2, 10, MessageType.MSG_HIT_GREAT), rows.get(1));
        assertEquals(new RangedCriticalLevelData(-1, 3, 15, MessageType.MSG_HIT_SUPERB), rows.get(2));
    }
}
