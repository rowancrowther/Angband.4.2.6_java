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
 * Tests for open issue 2 in {@code docs/FileHandler_open_issues.md}: switching a
 * {@link FileHandler} between its read and write sides must leave {@link FileHandler#isOpen()}
 * and {@link FileHandler#close()} telling the truth.
 * <p>
 * In C a {@code ang_file} holds one {@code FILE *}, so it is open or it is not, and
 * {@code file_close} closes it once. The Java handler has two streams, so the equivalent contract
 * is: after any {@code open}, exactly one side is live; {@code close()} always succeeds on a
 * handler in any state and leaves {@code isOpen()} false.
 * <p>
 * Class FileHandlerStreamSwitchTest coded on 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
class FileHandlerStreamSwitchTest {
    @TempDir
    Path dir;

    private Path file() throws IOException {
        Path p = dir.resolve("a.txt");
        Files.writeString(p, "alpha\n");
        return p;
    }

    /** The reported case: write, then read, then close. */
    @Test
    void writeThenReadThenCloseLeavesClosed() throws IOException {
        FileHandler fh = new FileHandler(file().toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.close());

        assertFalse(fh.isOpen());
    }

    /** The mirror case: read, then write, then close. */
    @Test
    void readThenWriteThenCloseLeavesClosed() throws IOException {
        FileHandler fh = new FileHandler(file().toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.close());

        assertFalse(fh.isOpen());
    }

    /** A read-only handler closes: only the input side exists. */
    @Test
    void closeAfterReadOnlyOpen() throws IOException {
        FileHandler fh = new FileHandler(file().toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.close(), "close() must not fail because the output side is null");

        assertFalse(fh.isOpen());
    }

    /** A write-only handler closes: only the output side exists. */
    @Test
    void closeAfterWriteOnlyOpen() throws IOException {
        FileHandler fh = new FileHandler(file().toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.close(), "close() must not fail because the input side is null");

        assertFalse(fh.isOpen());
    }

    /** After a switch the old side is gone straight away, before any close. */
    @Test
    void switchingSidesLeavesOneLive() throws IOException {
        FileHandler fh = new FileHandler(file().toString());
        // APPEND, not WRITE: "wb" truncates in C too, which would leave nothing to read
        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertEquals('a', fh.readCharacter(), "the read side must be the live one");
    }

    /** Closing a handler that was never opened, and closing twice, are both fine. */
    @Test
    void closeOnClosedIsTrue() throws IOException {
        FileHandler fh = new FileHandler(file().toString());
        assertTrue(fh.close());

        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.close());
        assertTrue(fh.close());
    }

    /** {@code delete()} routes through close(), so it must delete a read-open file. */
    @Test
    void deleteWhileReadOpen() throws IOException {
        Path p = file();
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.delete());

        assertFalse(Files.exists(p));
        assertFalse(fh.isOpen());
    }

    /** A buffered writer's data is flushed to disk by the switch to reading. */
    @Test
    void putLineThenReadSeesTheLine() throws IOException {
        Path p = dir.resolve("b.txt");
        Files.createFile(p);
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("hello"));

        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertEquals("hello", fh.getLine(80));
    }
}
