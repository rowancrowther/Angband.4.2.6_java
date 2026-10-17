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

package uk.co.jackoftradesltd.backend.io.savefiles;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Savefile#savefileGetDescription(Path)}, the port of {@code savefile_get_description} in
 * {@code savefile.c}.
 * <p>
 * Expected values are derived from the C function. It opens the path with {@code file_open} and returns
 * {@code NULL} when that fails. Otherwise it blanks its static buffer, runs {@code check_header}, and for a file
 * whose header is bad fills the buffer with {@code "Invalid savefile"}. Only the open step is ported, so the
 * tests that need the header check and block scan are {@link Disabled} and record the C result they are waiting
 * for.
 * <p>
 * Class SavefileTest coded on 261017, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
class SavefileTest {

    @TempDir
    Path tempDir;

    @Test
    void missingFileReturnsNullAsCReturnsNull() {
        Path missing = tempDir.resolve("no-such-savefile");

        assertNull(Savefile.savefileGetDescription(missing),
                "file_open fails on a missing file in MODE_READ, so C returns NULL");
    }

    @Test
    void missingFileIsNotCreatedByTheLookup() {
        Path missing = tempDir.resolve("no-such-savefile");

        Savefile.savefileGetDescription(missing);

        assertFalse(Files.exists(missing),
                "MODE_READ never creates the file in C, so the lookup must not either");
    }

    @Test
    void existingFileReturnsAStringNotNull() throws IOException {
        Path file = tempDir.resolve("1.Player");
        Files.write(file, new byte[]{1, 2, 3, 4});

        assertNotNull(Savefile.savefileGetDescription(file),
                "C returns the static buffer, never NULL, once the file has opened");
    }

    @Test
    void emptyExistingFileReturnsAStringNotNull() throws IOException {
        Path file = tempDir.resolve("empty");
        Files.write(file, new byte[0]);

        assertNotNull(Savefile.savefileGetDescription(file),
                "an empty file opens, so C goes on to check_header and returns the buffer, not NULL");
    }

    @Test
    void lookupLeavesTheFileUnchanged() throws IOException {
        Path file = tempDir.resolve("1.Player");
        byte[] content = {9, 8, 7, 6, 5};
        Files.write(file, content);

        Savefile.savefileGetDescription(file);

        assertArrayEquals(content, Files.readAllBytes(file),
                "the lookup only reads");
    }

    @Test
    @Disabled("check_header is not ported yet")
    void garbageFileGivesInvalidSavefile() throws IOException {
        Path file = tempDir.resolve("garbage");
        Files.write(file, "this is not a savefile".getBytes());

        assertEquals("Invalid savefile", Savefile.savefileGetDescription(file),
                "a file that fails check_header gets my_strcpy(savefile_desc, \"Invalid savefile\")");
    }

    @Test
    @Disabled("check_header is not ported yet")
    void emptyFileGivesInvalidSavefile() throws IOException {
        Path file = tempDir.resolve("empty");
        Files.write(file, new byte[0]);

        assertEquals("Invalid savefile", Savefile.savefileGetDescription(file),
                "an empty file cannot supply the header bytes, so check_header fails");
    }
}
