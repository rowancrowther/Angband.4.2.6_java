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
 * Tests for point 3 in {@code docs/FileHandler_port_findings.md}: a second write-mode
 * {@link FileHandler#open} must not leave a stale {@code bufferedWriter} behind, and must not
 * throw when no {@code bufferedWriter} was ever created.
 * <p>
 * In C, reopening is {@code file_open} on a fresh {@code FILE *}; there is no earlier writer to go
 * stale. The Java contract is: after the second open, writes land in the new stream.
 * <p>
 * Class FileHandlerReopenWriteTest coded on 261016, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
class FileHandlerReopenWriteTest {
    @TempDir
    Path dir;

    /** The reported case: a putLine created the writer, then the file is reopened for write. */
    @Test
    void putLineThenReopenThenPutLineWritesToNewStream() throws IOException {
        Path p = dir.resolve("a.txt");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("first\n"));

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("second\n"));
        assertTrue(fh.close());

        // the second open truncated, as "wb" does in C
        assertEquals("second\n", Files.readString(p));
    }

    /** No writer was ever created (nothing went through putLine), so there is nothing to close. */
    @Test
    void reopenWithoutAnyPutLineDoesNotThrow() throws IOException {
        Path p = dir.resolve("b.txt");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));

        assertDoesNotThrow(() -> fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("x\n"));
        assertTrue(fh.close());

        assertEquals("x\n", Files.readString(p));
    }

    /** writeCharacter writes straight to the stream, never creating a writer. */
    @Test
    void writeCharacterThenReopenThenPutLine() throws IOException {
        Path p = dir.resolve("c.txt");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.writeCharacter('a'));

        assertDoesNotThrow(() -> fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("b\n"));
        assertTrue(fh.close());

        assertEquals("ab\n", Files.readString(p));
    }

    /** Reopening in append mode after a putLine keeps the earlier text and adds the new. */
    @Test
    void putLineThenReopenAppendKeepsBoth() throws IOException {
        Path p = dir.resolve("d.txt");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("one\n"));

        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("two\n"));
        assertTrue(fh.close());

        assertEquals("one\ntwo\n", Files.readString(p));
    }
}
