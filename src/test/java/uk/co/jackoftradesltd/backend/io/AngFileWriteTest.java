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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the write methods of {@link AngFile}, against {@code file_writec}, {@code
 * file_write}, {@code file_put}, {@code file_putf} and {@code file_vputf} in {@code z-file.c}.
 * <p>
 * Expected values come from the C: {@code file_put} is {@code file_write(f, buf, strlen(buf))}
 * with no line ending added, {@code file_write} is {@code fwrite(...) == n} (so zero bytes
 * succeed), {@code file_writec} writes a {@code uint8_t}, and {@code file_vputf} formats into a
 * 1024-byte buffer, which keeps 1023 bytes. C has no unopened {@code ang_file}, so every write
 * before {@link AngFile#open(FileTypeEnum)} is refused.
 * <p>
 * Class AngFileWriteTest coded on 261016, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
class AngFileWriteTest {

    private static AngFile opened(Path path, FileModeEnum mode) {
        AngFile file = new AngFile(path.toString(), mode);
        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        return file;
    }

    private static byte[] repeat(char c, int count) {
        byte[] bytes = new byte[count];
        java.util.Arrays.fill(bytes, (byte) c);
        return bytes;
    }

    // ---- refusals ----

    @Test
    void everyWriteMethodRefusesBeforeOpenAndCreatesNothing(@TempDir Path root) {
        Path path = root.resolve("never-opened.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);

        assertFalse(file.writeChar('A'));
        assertFalse(file.write("A".getBytes(StandardCharsets.UTF_8)));
        assertFalse(file.putLine("A"));
        assertFalse(file.putFormattedLine("%s", "A"));
        assertFalse(file.isOpen());
        assertFalse(Files.exists(path));
    }

    @Test
    void putFormattedLineRefusesWhenTheFileWasCreatedForReading(@TempDir Path root)
            throws IOException {
        Path path = root.resolve("read-only.txt");
        Files.writeString(path, "keep");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertFalse(file.putFormattedLine("%s", "X"));
        file.close();

        assertEquals("keep", Files.readString(path));
    }

    @Test
    void writeMethodsRefuseOnAnOpenReadOnlyFile(@TempDir Path root) throws IOException {
        Path path = root.resolve("read-only.txt");
        Files.writeString(path, "keep");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertFalse(file.writeChar('X'));
        assertFalse(file.write("X".getBytes(StandardCharsets.UTF_8)));
        assertFalse(file.putLine("X"));
        file.close();

        assertEquals("keep", Files.readString(path));
    }

    // ---- file_writec ----

    /** {@code file_writec} takes a {@code uint8_t}, so only the low 8 bits reach the file. */
    @Test
    void writeCharWritesOnlyTheLowEightBits(@TempDir Path root) throws IOException {
        Path path = root.resolve("bits.bin");
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertTrue(file.writeChar(0x141));
        assertTrue(file.writeChar(255));
        assertTrue(file.writeChar(0));
        file.close();

        assertArrayEquals(new byte[] {0x41, (byte) 0xFF, 0}, Files.readAllBytes(path));
    }

    // ---- file_write ----

    /** {@code fwrite(buf, 1, 0, fh) == 0} is true, so writing no bytes succeeds. */
    @Test
    void writeOfAnEmptyArraySucceedsAndWritesNothing(@TempDir Path root) throws IOException {
        Path path = root.resolve("empty.bin");
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertTrue(file.write(new byte[0]));
        file.close();

        assertEquals(0, Files.size(path));
    }

    @Test
    void successiveWritesFollowEachOther(@TempDir Path root) throws IOException {
        Path path = root.resolve("two.bin");
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertTrue(file.write("AB".getBytes(StandardCharsets.UTF_8)));
        assertTrue(file.write("C".getBytes(StandardCharsets.UTF_8)));
        file.close();

        assertEquals("ABC", Files.readString(path));
    }

    @Test
    void writeStoresTheBytesWithoutEncodingThem(@TempDir Path root) throws IOException {
        Path path = root.resolve("raw.bin");
        byte[] raw = {0, (byte) 0x80, (byte) 0xC3, (byte) 0xFF, 10};
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertTrue(file.write(raw));
        file.close();

        assertArrayEquals(raw, Files.readAllBytes(path));
    }

    @Test
    void writeInAppendModeAddsToTheEnd(@TempDir Path root) throws IOException {
        Path path = root.resolve("append.bin");
        Files.writeString(path, "AB");
        AngFile file = opened(path, FileModeEnum.MODE_APPEND);

        assertTrue(file.write("C".getBytes(StandardCharsets.UTF_8)));
        file.close();

        assertEquals("ABC", Files.readString(path));
    }

    // ---- file_put ----

    /** {@code file_put} writes its buffer as given; the name {@code putLine} adds no newline. */
    @Test
    void putLineAddsNoLineEnding(@TempDir Path root) throws IOException {
        Path path = root.resolve("put.txt");
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertTrue(file.putLine("x"));
        assertTrue(file.putLine("y\n"));
        assertTrue(file.putLine(""));
        file.close();

        assertEquals("xy\n", Files.readString(path));
    }

    @Test
    void putLineWritesUtf8(@TempDir Path root) throws IOException {
        Path path = root.resolve("utf8.txt");
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertTrue(file.putLine("é"));
        file.close();

        assertArrayEquals(new byte[] {(byte) 0xC3, (byte) 0xA9}, Files.readAllBytes(path));
    }

    @Test
    void putLineInAppendModeAddsToTheEnd(@TempDir Path root) throws IOException {
        Path path = root.resolve("append.txt");
        Files.writeString(path, "one\n");
        AngFile file = opened(path, FileModeEnum.MODE_APPEND);

        assertTrue(file.putLine("two\n"));
        file.close();

        assertEquals("one\ntwo\n", Files.readString(path));
    }

    /** C writes everything through the one {@code FILE *}, so the order of calls is the order in the file. */
    @Test
    void writeAndPutLineKeepTheirOrderOnOneHandle(@TempDir Path root) throws IOException {
        Path path = root.resolve("mixed.txt");
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertTrue(file.write("A".getBytes(StandardCharsets.UTF_8)));
        assertTrue(file.putLine("B"));
        assertTrue(file.writeChar('C'));
        assertTrue(file.putFormattedLine("%s", "D"));
        assertTrue(file.write("E".getBytes(StandardCharsets.UTF_8)));
        file.close();

        assertEquals("ABCDE", Files.readString(path));
    }

    // ---- file_putf / file_vputf ----

    @Test
    void putFormattedLineAddsNoLineEnding(@TempDir Path root) throws IOException {
        Path path = root.resolve("fmt.txt");
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertTrue(file.putFormattedLine("%d%%", 5));
        assertTrue(file.putFormattedLine("[%s]\n", "x"));
        file.close();

        assertEquals("5%[x]\n", Files.readString(path));
    }

    /**
     * {@code vstrnfmt} always writes ASCII digits and a {@code '.'} decimal point, so the JVM's
     * default locale must not leak into the file. German writes {@code 1,50} and Arabic writes
     * non-ASCII digits.
     */
    @Test
    void putFormattedLineIgnoresTheDefaultLocale(@TempDir Path root) throws IOException {
        Locale saved = Locale.getDefault(Locale.Category.FORMAT);
        try {
            for (Locale locale : new Locale[] {Locale.GERMANY, Locale.forLanguageTag("ar-EG")}) {
                Locale.setDefault(Locale.Category.FORMAT, locale);
                Path path = root.resolve("fmt-" + locale.toLanguageTag() + ".txt");
                AngFile file = opened(path, FileModeEnum.MODE_WRITE);

                assertTrue(file.putFormattedLine("%.2f %d", 1.5, 1234567));
                file.close();

                assertEquals("1.50 1234567", Files.readString(path), locale.toLanguageTag());
            }
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, saved);
        }
    }

    /**
     * Accepted deviation (docs/Accepted_Deviations.md): {@code vstrnfmt} reads {@code %lu} but
     * {@link String#format} does not, so the call fails, writes nothing and leaves the file usable.
     */
    @Test
    void putFormattedLineRejectsACStyleLongFormatAndWritesNothing(@TempDir Path root)
            throws IOException {
        Path path = root.resolve("fmt.txt");
        AngFile file = opened(path, FileModeEnum.MODE_WRITE);

        assertFalse(file.putFormattedLine("%lu", 5L));
        assertTrue(file.putFormattedLine("ok"));
        file.close();

        assertEquals("ok", Files.readString(path));
    }

    /** {@code vstrnfmt(buf, 1024, ...)} keeps at most 1023 bytes. */
    @Test
    void putFormattedLineKeepsAtMost1023Bytes(@TempDir Path root) throws IOException {
        Path whole = root.resolve("whole.txt");
        AngFile file = opened(whole, FileModeEnum.MODE_WRITE);
        assertTrue(file.putFormattedLine("%s", new String(repeat('a', 1023), StandardCharsets.UTF_8)));
        file.close();
        assertEquals(1023, Files.size(whole));

        Path cut = root.resolve("cut.txt");
        file = opened(cut, FileModeEnum.MODE_WRITE);
        assertTrue(file.putFormattedLine("%s", new String(repeat('a', 1024), StandardCharsets.UTF_8)));
        file.close();
        assertEquals(1023, Files.size(cut));

        Path long_ = root.resolve("long.txt");
        file = opened(long_, FileModeEnum.MODE_WRITE);
        assertTrue(file.putFormattedLine("%s", new String(repeat('a', 3000), StandardCharsets.UTF_8)));
        file.close();
        assertEquals(1023, Files.size(long_));
    }

    /**
     * Accepted deviation (docs/Accepted_Deviations.md): C cuts at 1023 bytes whatever they are;
     * a cut that would split a two-byte character drops the whole character, so the line is
     * one byte shorter.
     */
    @Test
    void putFormattedLineDropsACharacterTheCutWouldSplit(@TempDir Path root) throws IOException {
        Path split = root.resolve("split.txt");
        AngFile file = opened(split, FileModeEnum.MODE_WRITE);
        assertTrue(file.putFormattedLine("%sé", new String(repeat('a', 1022), StandardCharsets.UTF_8)));
        file.close();
        assertEquals(1022, Files.size(split), "the 2-byte é would straddle byte 1023, so it is dropped");

        Path fits = root.resolve("fits.txt");
        file = opened(fits, FileModeEnum.MODE_WRITE);
        assertTrue(file.putFormattedLine("%sé", new String(repeat('a', 1021), StandardCharsets.UTF_8)));
        file.close();
        assertEquals(1023, Files.size(fits), "é ends exactly at byte 1023, so it is kept");
    }
}
