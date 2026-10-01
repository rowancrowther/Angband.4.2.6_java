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

package uk.co.jackoftradesltd.middle.game.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests for {@link CommandCode}, the port of {@code enum cmd_code} in {@code cmd-core.h}. Counts
 * and spot ordinals are pinned from the C enum; the full name list is also compared against the
 * real header when the C tree is present.
 *
 * @author Rowan Crowther
 */
class CommandCodeTest {
    private static final Path C_HEADER = Path.of("/home/rowan/Desktop/Angband-4.2.6/src/cmd-core.h");

    @Test
    @DisplayName("116 codes, counting CMD_NULL and the last real command")
    void countMatchesC() {
        assertEquals(116, CommandCode.values().length);
    }

    @Test
    @DisplayName("ordinals at each section boundary equal C's enum values")
    void spotOrdinalsMatchC() {
        assertEquals(0, CommandCode.CMD_NULL.ordinal());
        assertEquals(1, CommandCode.CMD_LOADFILE.ordinal());
        assertEquals(3, CommandCode.CMD_BIRTH_INIT.ordinal());
        assertEquals(16, CommandCode.CMD_GO_UP.ordinal());
        assertEquals(56, CommandCode.CMD_SLEEP.ordinal());
        assertEquals(57, CommandCode.CMD_SELL.ordinal());
        assertEquals(61, CommandCode.CMD_SPOIL_ARTIFACT.ordinal());
        assertEquals(65, CommandCode.CMD_WIZ_ACQUIRE.ordinal());
        assertEquals(111, CommandCode.CMD_WIZ_WIZARD_LIGHT.ordinal());
        assertEquals(112, CommandCode.CMD_RETIRE.ordinal());
        assertEquals(115, CommandCode.CMD_COMMAND_MONSTER.ordinal());
    }

    @Test
    @DisplayName("names and order match the enum in the real cmd-core.h")
    void matchesCHeader() throws IOException {
        assumeTrue(Files.exists(C_HEADER), "C tree not present");
        String header = Files.readString(C_HEADER);
        int start = header.indexOf("typedef enum cmd_code {");
        int end = header.indexOf("} cmd_code;");
        assertTrue(start >= 0 && end > start);
        // Drop comments so words like "/or/" in them cannot be read as names.
        String body = header.substring(start, end).replaceAll("(?s)/\\*.*?\\*/", "");
        Matcher m = Pattern.compile("\\bCMD_[A-Z_]+\\b").matcher(body);
        List<String> expected = new ArrayList<>();
        while (m.find()) {
            expected.add(m.group());
        }
        List<String> actual = new ArrayList<>();
        for (CommandCode c : CommandCode.values()) {
            actual.add(c.name());
        }
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("only CMD_NULL, CMD_BROWSE_SPELL and CMD_IGNORE lack a dispatch row, as in game_cmds[]")
    void dispatchRowsMatchGameCmds() {
        for (CommandCode c : CommandCode.values()) {
            boolean rowless = c == CommandCode.CMD_NULL
                    || c == CommandCode.CMD_BROWSE_SPELL
                    || c == CommandCode.CMD_IGNORE;
            if (rowless) {
                assertFalse(CommandProcessor.containsCommand(c), c + " should have no row");
            } else {
                assertTrue(CommandProcessor.containsCommand(c), c + " should have a row");
            }
        }
    }
}
