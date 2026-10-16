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

package uk.co.jackoftradesltd.backend.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.enums.FileModeEnum;
import uk.co.jackoftradesltd.backend.io.enums.FileTypeEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for open issue 3 in {@code docs/FileHandler_open_issues.md}: {@link FileHandler#open}
 * must return {@code false} when the stream could not be opened.
 * <p>
 * In C, {@code file_open} returns {@code NULL} when {@code fopen} fails, and every caller checks
 * it. The Java {@code boolean} stands in for that {@code NULL}.
 * <p>
 * Class FileHandlerOpenFailureTest coded on 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
class FileHandlerOpenFailureTest {
    @TempDir
    Path dir;

    /** {@code fopen(missing, "rb")} fails in C. */
    @Test
    void readMissingFileFails() {
        FileHandler fh = new FileHandler(dir.resolve("missing.txt").toString());

        assertFalse(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertFalse(fh.isOpen());
    }

    /** {@code fopen} on a directory path with "wb" fails in C (EISDIR). */
    @Test
    void writeToDirectoryFails() {
        FileHandler fh = new FileHandler(dir.toString());

        assertFalse(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));

        assertFalse(fh.isOpen());
    }

    /** {@code fopen} on a directory path with "a+" fails in C (EISDIR). */
    @Test
    void appendToDirectoryFails() {
        FileHandler fh = new FileHandler(dir.toString());

        assertFalse(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));

        assertFalse(fh.isOpen());
    }

    /** {@code fopen} into a directory that does not exist fails in C (ENOENT). */
    @Test
    void writeIntoMissingDirectoryFails() {
        FileHandler fh = new FileHandler(dir.resolve("nodir").resolve("a.txt").toString());

        assertFalse(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));

        assertFalse(fh.isOpen());
    }

    /** A failed open leaves close() able to succeed. */
    @Test
    void closeAfterFailedOpenIsTrue() {
        FileHandler fh = new FileHandler(dir.resolve("missing.txt").toString());
        assertFalse(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.close());
    }

    /** Successful opens still return true (guards against over-correcting). */
    @Test
    void successfulOpensStillTrue() throws IOException {
        Path p = dir.resolve("a.txt");
        Files.writeString(p, "alpha\n");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertEquals('a', fh.readCharacter());
        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.close());
    }

    /**
     * Issue 5, not issue 3: C's "wb" and "a+" create a missing file in an existing directory.
     * Guards against {@code toRealPath()} returning to the output path, since it throws on a missing file.
     */
    @Test
    void writeAndAppendCreateMissingFile() {
        FileHandler w = new FileHandler(dir.resolve("w.txt").toString());
        assertTrue(w.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT), "MODE_WRITE should create");
        assertTrue(w.close());
        assertTrue(Files.exists(dir.resolve("w.txt")));

        FileHandler a = new FileHandler(dir.resolve("a.txt").toString());
        assertTrue(a.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT), "MODE_APPEND should create");
        assertTrue(a.close());
        assertTrue(Files.exists(dir.resolve("a.txt")));
    }
}
