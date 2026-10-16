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
 * Unit tests for {@link FileHandler#read(int)}, the port of C's {@code file_read}
 * ({@code src/z-file.c}).
 * <p>
 * Expected values come from C's {@code file_read}, which is {@code fread(buf, 1, n, f->fh)}: raw
 * bytes with no interpretation, a count that is short when the file runs out, and zero at the
 * end of the file. C's -1 (a stream error) is represented here by {@code null}.
 * <p>
 * Class FileHandlerReadBytesTest coded on 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
class FileHandlerReadBytesTest {
    @TempDir
    Path dir;

    private FileHandler openForRead(byte[] content) throws IOException {
        Path p = dir.resolve("data.bin");
        Files.write(p, content);
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        return fh;
    }

    /**
     * Every byte value from 0 to 255 comes back unchanged. A decode with the platform charset
     * would turn the invalid UTF-8 sequences into U+FFFD.
     */
    @Test
    void allByteValuesRoundTrip() throws IOException {
        byte[] all = new byte[256];
        for (int i = 0; i < 256; i++) all[i] = (byte) i;

        FileHandler fh = openForRead(all);

        assertArrayEquals(all, fh.read(256));
    }

    /**
     * Lone Latin-1 bytes above 127 (not valid UTF-8) survive, and the count equals the number of
     * bytes asked for.
     */
    @Test
    void highBytesAreNotMangled() throws IOException {
        byte[] data = {(byte) 0xE9, (byte) 0xFF, (byte) 0x80, 0x41};

        byte[] got = openForRead(data).read(4);

        assertEquals(4, got.length);
        assertArrayEquals(data, got);
    }

    /**
     * A valid multi-byte UTF-8 sequence still counts as its bytes, not as one character, so
     * {@code read(2)} on a two-byte character returns two bytes.
     */
    @Test
    void multiByteSequenceCountsBytes() throws IOException {
        byte[] data = "é".getBytes(java.nio.charset.StandardCharsets.UTF_8);

        byte[] got = openForRead(data).read(2);

        assertEquals(2, got.length);
        assertArrayEquals(data, got);
    }

    /**
     * Asking for more than remains returns the short remainder, as {@code fread} does.
     */
    @Test
    void shortReadAtEndOfFile() throws IOException {
        FileHandler fh = openForRead(new byte[]{1, 2, 3});

        assertArrayEquals(new byte[]{1, 2, 3}, fh.read(10));
    }

    /**
     * At the end of the file the result is zero bytes, which is C's return of 0 — distinct from
     * the {@code null} that stands in for C's -1.
     */
    @Test
    void atEndOfFileReturnsEmptyArray() throws IOException {
        FileHandler fh = openForRead(new byte[]{1, 2});
        fh.read(2);

        byte[] got = fh.read(5);

        assertNotNull(got);
        assertEquals(0, got.length);
    }

    /**
     * Successive reads continue from where the last one stopped.
     */
    @Test
    void successiveReadsAdvance() throws IOException {
        FileHandler fh = openForRead(new byte[]{10, 20, 30, 40});

        assertArrayEquals(new byte[]{10, 20}, fh.read(2));
        assertArrayEquals(new byte[]{30, 40}, fh.read(2));
    }

    /**
     * With no input stream open there is nothing to read, so the result is {@code null}.
     */
    @Test
    void notOpenReturnsNull() {
        FileHandler fh = new FileHandler(dir.resolve("never.bin").toString());

        assertNull(fh.read(4));
    }
}
