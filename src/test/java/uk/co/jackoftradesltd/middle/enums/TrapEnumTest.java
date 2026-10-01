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

package uk.co.jackoftradesltd.middle.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Checks {@link TrapEnum} against {@code list-trap-flags.h} in the C original. The table below is
 * the C list transcribed in C order (name, description), so the expected values come from C and
 * not from the Java enum; one test also reads the header itself when the C tree is present.
 *
 * <p>Class TrapEnumTest coded on 261001, commented in full on 261001.
 */
class TrapEnumTest {

    private static final Path C_HEADER = Path.of("/home/rowan/Desktop/Angband-4.2.6/src/list-trap-flags.h");

    /**
     * The {@code TRF(name, descr)} rows of {@code list-trap-flags.h}, in C order.
     */
    private static final String[][] C_LIST = {
            {"NONE", ""},
            {"GLYPH", "Is a glyph"},
            {"TRAP", "Is a player trap"},
            {"VISIBLE", "Is visible"},
            {"INVISIBLE", "Is invisible"},
            {"FLOOR", "Can be set on a floor"},
            {"DOWN", "Takes the player down a level"},
            {"PIT", "Moves the player onto the trap"},
            {"ONETIME", "Disappears after being activated"},
            {"MAGICAL", "Has magical activation (absence of this flag means physical)"},
            {"SAVE_THROW", "Allows a save from all effects by standard saving throw"},
            {"SAVE_ARMOR", "Allows a save from all effects due to AC"},
            {"LOCK", "Is a door lock"},
            {"DELAY", "Has a delayed effect"},
            {"WEB", "Is a web"},
    };

    @Test
    @DisplayName("every row matches the C table in name, order and description")
    void matchesCTable() {
        TrapEnum[] all = TrapEnum.values();
        for (int i = 0; i < C_LIST.length; i++) {
            assertEquals("TRF_" + C_LIST[i][0], all[i].name(), "name at index " + i);
            assertEquals(i, all[i].ordinal(), "C numbering of " + C_LIST[i][0]);
            assertEquals(C_LIST[i][1], all[i].getDescription(), "description of " + C_LIST[i][0]);
        }
    }

    @Test
    @DisplayName("TRF_MAX is the sentinel: ordinal equals the row count, description empty")
    void maxSentinel() {
        assertEquals(C_LIST.length + 1, TrapEnum.values().length);
        assertEquals(C_LIST.length, TrapEnum.TRF_MAX.ordinal());
        assertEquals("", TrapEnum.TRF_MAX.getDescription());
    }

    @Test
    @DisplayName("TRF_MAGICAL keeps C's parenthesis and has no stray comma")
    void magicalDescription() {
        String d = TrapEnum.TRF_MAGICAL.getDescription();
        assertTrue(d.contains("(absence of this flag means physical)"));
        assertTrue(d.endsWith(")"));
        assertFalse(d.contains("_"));
    }

    @Test
    @DisplayName("every row of the real list-trap-flags.h agrees with the enum")
    void matchesRealHeader() throws IOException {
        assumeTrue(Files.exists(C_HEADER), "C source tree not present");
        Pattern row = Pattern.compile("^TRF\\((\\w+),\\s*\"(.*)\"\\)");
        List<String[]> rows = new ArrayList<>();
        for (String line : Files.readAllLines(C_HEADER)) {
            Matcher m = row.matcher(line);
            if (m.find()) rows.add(new String[]{m.group(1), m.group(2)});
        }
        assertEquals(rows.size() + 1, TrapEnum.values().length);
        for (int i = 0; i < rows.size(); i++) {
            assertEquals("TRF_" + rows.get(i)[0], TrapEnum.values()[i].name());
            assertEquals(rows.get(i)[1], TrapEnum.values()[i].getDescription());
        }
    }

    @Test
    @DisplayName("valueOf(\"TRF_\" + name) resolves, as TrapAssembler does for trap.txt flags")
    void valueOfPrefix() {
        for (String[] r : C_LIST) {
            assertEquals(r[0], TrapEnum.valueOf("TRF_" + r[0]).name().substring(4));
        }
        assertThrows(IllegalArgumentException.class, () -> TrapEnum.valueOf("TRF_BOGUS"));
    }
}
