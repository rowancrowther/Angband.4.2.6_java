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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link FileHandler#skipBytes(long)}, the port of C's {@code file_skip}
 * ({@code src/z-file.c}).
 * <p>
 * Expected values come from C's {@code file_skip}, which is {@code fseek(fh, n, SEEK_CUR) == 0}:
 * a forward in-bounds skip always succeeds and lands exactly {@code n} bytes on, whatever the
 * stdio buffer holds. The file is 100000 bytes where byte {@code i} is {@code i % 251}, so the
 * byte read after a skip identifies the position.
 * <p>
 * Class FileHandlerSkipBytesTest coded on 261016.
 *
 * @author Rowan Crowther
 */
class FileHandlerSkipBytesTest {
    private static final int SIZE = 100_000;

    @TempDir
    Path dir;

    private FileHandler open() throws IOException {
        byte[] content = new byte[SIZE];
        for (int i = 0; i < SIZE; i++) content[i] = (byte) (i % 251);
        Path p = dir.resolve("data.bin");
        Files.write(p, content);
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        return fh;
    }

    /** Reads {@code reads} bytes, skips {@code n}, and checks the next byte is the one at the expected position. */
    private void check(int reads, long n) throws IOException {
        FileHandler fh = open();
        for (int i = 0; i < reads; i++) fh.readCharacter();
        assertTrue(fh.skipBytes(n), "skip " + n + " after " + reads + " reads should succeed");
        long expectedPos = reads + n;
        assertEquals((int) (expectedPos % 251), fh.readCharacter(),
                "wrong position after reads=" + reads + " skip=" + n);
    }

    @Test
    void skipOnFreshStream() throws IOException {
        check(0, 9000);
    }

    @Test
    void skipWithinTheBufferedRemainder() throws IOException {
        check(1, 5000);
    }

    @Test
    void skipJustPastTheBufferedRemainder() throws IOException {
        check(1, 9000);
    }

    @Test
    void skipSpanningTwoRefills() throws IOException {
        check(1, 20000);
    }

    @Test
    void skipSpanningManyRefills() throws IOException {
        check(1, 50000);
    }

    @Test
    void skipSpanningManyRefillsAfterManyReads() throws IOException {
        check(300, 60000);
    }

    @Test
    void skipTwiceInARow() throws IOException {
        FileHandler fh = open();
        fh.readCharacter();
        assertTrue(fh.skipBytes(20000));
        assertTrue(fh.skipBytes(20000));
        assertEquals((int) (40001 % 251), fh.readCharacter());
    }

    @Test
    void skipToExactlyTheLastByte() throws IOException {
        FileHandler fh = open();
        fh.readCharacter();
        assertTrue(fh.skipBytes(SIZE - 2));
        assertEquals((int) ((SIZE - 1) % 251), fh.readCharacter());
    }

    @Test
    void skipToExactlyTheEnd() throws IOException {
        FileHandler fh = open();
        fh.readCharacter();
        assertTrue(fh.skipBytes(SIZE - 1));
        assertEquals(-1, fh.readCharacter());
    }

    /** C's fseek succeeds past the end, so a skip past it is true whether or not a byte has been read. */
    @Test
    void skipPastTheEndSucceedsOnFreshAndReadStreams() throws IOException {
        FileHandler fresh = open();
        assertTrue(fresh.skipBytes(SIZE + 5000));
        assertEquals(-1, fresh.readCharacter());

        FileHandler read = open();
        read.readCharacter();
        assertTrue(read.skipBytes(SIZE + 5000));
        assertEquals(-1, read.readCharacter());
    }
}
