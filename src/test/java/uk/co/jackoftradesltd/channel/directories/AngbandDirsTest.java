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

package uk.co.jackoftradesltd.channel.directories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AngbandDirs} - the port of C's {@code ANGBAND_DIR_*} globals
 * ({@code [C] src/init.c}) and of the name/pointer table {@code change_path_values}
 * ({@code [C] src/main.c}) that backs the {@code -d} switch.
 *
 * <p>The expected default paths are derived from the C build this port models: no
 * {@code PRIVATE_USER_PATH} (not UNIX), {@code USE_PRIVATE_PATHS} defined and
 * {@code GAMEDATA_IN_LIB} not, which is what {@code src/win/vs2019/Angband.vcxproj} sets and what
 * matches the {@code lib/} tree actually shipped alongside the C source - {@code lib/customize/}
 * and {@code lib/user/{scores,save,panic,archive}/}, not {@code lib/config/customize/} or
 * {@code lib/user/archives/}. Every expected suffix below is typed from that tree, not copied out
 * of {@link AngbandDirs}, so a future slip in the port's arithmetic has something independent to
 * fail against.
 *
 * <p>{@link AngbandDirs.ANGBAND_DIRS} constants are process-wide and mutable, so any test that
 * calls {@link AngbandDirs.ANGBAND_DIRS#setPath} restores what it changed - the same discipline
 * {@code MainTest} follows for the same reason.
 *
 * @author Rowan Crowther
 */
class AngbandDirsTest {

    private static final String SEP = File.separator;

    // ---- default paths -----------------------------------------------------
    /**
     * Whichever constant a test below repoints, restored once the test ends - these constants
     * are process-wide, so a test that moves one has to put it back, the same rule
     * {@code MainTest.aValidOptionChangesNothing} depends on for {@code Main}.
     */
    private String savedPath;

    /**
     * Every directory's default path is the working directory plus the C-derived suffix for its
     * name - built from {@code init_file_paths()} ({@code [C] src/init.c}) under the Windows
     * build's flags, and cross-checked against the {@code lib/} directories actually on disk.
     *
     * <p>This is the regression test for both bugs the previous port-check pass found:
     * {@code CUSTOMIZE} had gained a spurious {@code config} segment, and {@code ARCHIVE} was
     * built from {@code "archives"} where C and the shipped tree both use {@code "archive"}.
     *
     * @param dir the directory constant under test
     */
    @ParameterizedTest
    @EnumSource(AngbandDirs.ANGBAND_DIRS.class)
    void theDefaultPathIsBuiltFromTheCDerivedSuffix(AngbandDirs.ANGBAND_DIRS dir) {
        String suffix = switch (dir.getName()) {
            case "icons" -> "lib" + SEP + "icons" + SEP;
            case "sounds" -> "lib" + SEP + "sounds" + SEP;
            case "tiles" -> "lib" + SEP + "tiles" + SEP;
            case "fonts" -> "lib" + SEP + "fonts" + SEP;
            case "screens" -> "lib" + SEP + "screens" + SEP;
            case "help" -> "lib" + SEP + "help" + SEP;
            case "gamedata" -> "lib" + SEP + "gamedata" + SEP;
            case "pref" -> "lib" + SEP + "customize" + SEP;
            case "user" -> "lib" + SEP + "user" + SEP;
            case "panic" -> "lib" + SEP + "user" + SEP + "panic" + SEP;
            case "save" -> "lib" + SEP + "user" + SEP + "save" + SEP;
            case "scores" -> "lib" + SEP + "user" + SEP + "scores" + SEP;
            case "archive" -> "lib" + SEP + "user" + SEP + "archive" + SEP;
            default -> fail("no expected suffix recorded for " + dir.getName());
        };

        assertEquals(AngbandDirs.BASE_DIR + SEP + suffix, dir.getPath(),
                dir.getName() + " must resolve to the C-derived default path");
    }

    // ---- name vs enum constant ---------------------------------------------

    /**
     * Every default path ends in a separator, so a caller may concatenate a file name directly -
     * the contract {@link AngbandDirs}'s class Javadoc states outright.
     *
     * @param dir the directory constant under test
     */
    @ParameterizedTest
    @EnumSource(AngbandDirs.ANGBAND_DIRS.class)
    void everyDefaultPathEndsInASeparator(AngbandDirs.ANGBAND_DIRS dir) {
        assertTrue(dir.getPath().endsWith(SEP),
                dir.getName() + "'s path must end in a separator: " + dir.getPath());
    }

    // ---- getDirectory / contains -------------------------------------------

    /**
     * {@code PREF}'s data-file name is {@code "pref"}, not {@code "PREF"} or
     * {@code "CUSTOMIZE"} - C's history left the switch name and the directory it points at
     * differing, and {@link AngbandDirs.ANGBAND_DIRS#getName()} is what carries the switch
     * spelling forward.
     */
    @Test
    void prefsNameDiffersFromItsEnumConstantName() {
        assertEquals("pref", AngbandDirs.ANGBAND_DIRS.PREF.getName());
        assertNotEquals(AngbandDirs.ANGBAND_DIRS.PREF.name(), AngbandDirs.ANGBAND_DIRS.PREF.getName());
        assertEquals(AngbandDirs.ANGBAND_DIR_CUSTOMIZE, AngbandDirs.ANGBAND_DIRS.PREF.getPath());
    }

    /**
     * Every name in {@code change_path_values} ({@code [C] src/main.c}) resolves back to its
     * constant, matched case-insensitively like C's {@code my_stricmp}.
     *
     * @param dir the directory constant under test
     */
    @ParameterizedTest
    @EnumSource(AngbandDirs.ANGBAND_DIRS.class)
    void getDirectoryFindsEveryKnownNameCaseInsensitively(AngbandDirs.ANGBAND_DIRS dir) {
        assertSame(dir, AngbandDirs.ANGBAND_DIRS.getDirectory(dir.getName()));
        assertSame(dir, AngbandDirs.ANGBAND_DIRS.getDirectory(dir.getName().toUpperCase()));
    }

    /**
     * A name that is not one of the game's directories resolves to {@code null} rather than
     * throwing - the plural {@code "archives"} is used deliberately, as the exact slip the
     * previous port-check pass found in {@code ANGBAND_DIR_ARCHIVE}'s default path.
     *
     * @param name an unknown directory name
     */
    @ParameterizedTest
    @ValueSource(strings = {"archives", "nosuchdir", ""})
    void getDirectoryReturnsNullForAnUnknownName(String name) {
        assertNull(AngbandDirs.ANGBAND_DIRS.getDirectory(name));
    }

    /**
     * {@link AngbandDirs.ANGBAND_DIRS#contains} answers the same question as
     * {@link AngbandDirs.ANGBAND_DIRS#getDirectory}, for every known name.
     *
     * @param dir the directory constant under test
     */
    @ParameterizedTest
    @EnumSource(AngbandDirs.ANGBAND_DIRS.class)
    void containsIsTrueForEveryKnownName(AngbandDirs.ANGBAND_DIRS dir) {
        assertTrue(AngbandDirs.ANGBAND_DIRS.contains(dir.getName()));
        assertTrue(AngbandDirs.ANGBAND_DIRS.contains(dir.getName().toUpperCase()));
    }

    // ---- mutation: setPath / setDirectory ----------------------------------

    /**
     * {@code contains} is false for a name no constant uses.
     */
    @Test
    void containsIsFalseForAnUnknownName() {
        assertFalse(AngbandDirs.ANGBAND_DIRS.contains("archives"));
    }

    @AfterEach
    void restoreSave() {
        if (savedPath != null) {
            AngbandDirs.ANGBAND_DIRS.SAVE.setPath(savedPath);
            savedPath = null;
        }
    }

    /**
     * {@link AngbandDirs.ANGBAND_DIRS#setPath} repoints the one copy
     * {@link AngbandDirs.ANGBAND_DIRS#getPath()} reads, in place.
     */
    @Test
    void setPathRepointsGetPath() {
        savedPath = AngbandDirs.ANGBAND_DIRS.SAVE.getPath();

        AngbandDirs.ANGBAND_DIRS.SAVE.setPath("/somewhere/else/");

        assertEquals("/somewhere/else/", AngbandDirs.ANGBAND_DIRS.SAVE.getPath());
    }

    /**
     * {@link AngbandDirs#setDirectory} looks the name up and writes through the constant it
     * finds - the port of C rewriting an {@code ANGBAND_DIR_*} buffer in {@code main()}
     * ({@code [C] src/main.c}).
     */
    @Test
    void setDirectoryWritesThroughTheMatchingConstant() {
        savedPath = AngbandDirs.ANGBAND_DIRS.SAVE.getPath();

        AngbandDirs.setDirectory("save", "/somewhere/else/");

        assertEquals("/somewhere/else/", AngbandDirs.ANGBAND_DIRS.SAVE.getPath());
    }

    /**
     * {@link AngbandDirs#setDirectory} matches the name case-insensitively, the same as
     * {@link AngbandDirs.ANGBAND_DIRS#getDirectory}.
     */
    @Test
    void setDirectoryMatchesTheNameCaseInsensitively() {
        savedPath = AngbandDirs.ANGBAND_DIRS.SAVE.getPath();

        AngbandDirs.setDirectory("SAVE", "/somewhere/else/");

        assertEquals("/somewhere/else/", AngbandDirs.ANGBAND_DIRS.SAVE.getPath());
    }
}
