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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AngFile}, the port of C's {@code ang_file} abstraction
 * ({@code src/z-file.c}).
 * <p>
 * Expected values are derived from the C originals: {@code fopen(buf, "rb")} fails without
 * creating anything on a missing file, while {@code "wb"}/{@code "a+"} create one ({@code
 * file_open}); {@code dir_exists} requires the {@code S_IFDIR} bit from {@code stat}, not mere
 * existence; and {@code file_skip} is a plain {@code fseek(..., SEEK_CUR)}, which allows a
 * negative offset to seek backwards.
 * <p>
 * Class AngFileTest coded on 260914, commented in full on 260914.
 *
 * @author Rowan Crowther
 */
class AngFileTest {

    @Test
    void openCreatesAndOpensAMissingFileWhenModeIsWrite(@TempDir Path root) {
        Path path = root.resolve("new.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);

        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertTrue(Files.exists(path), "file_open(\"wb\") creates a missing file");
        assertTrue(file.isOpen());
    }

    @Test
    void openCreatesAndOpensAMissingFileWhenModeIsAppend(@TempDir Path root) {
        Path path = root.resolve("new.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_APPEND);

        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertTrue(Files.exists(path), "file_open(\"a+\") creates a missing file");
    }

    @Test
    void openFailsAndDoesNotCreateAMissingFileWhenModeIsRead(@TempDir Path root) {
        Path path = root.resolve("missing.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);

        assertFalse(file.open(FileTypeEnum.FTYPE_TEXT),
                "fopen(buf, \"rb\") fails on a missing file rather than creating one");
        assertFalse(Files.exists(path));
    }

    @Test
    void openOpensAnExistingFileDirectlyWithoutRecreatingIt(@TempDir Path root) throws IOException {
        Path path = root.resolve("existing.txt");
        Files.writeString(path, "hello");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertEquals("hello", file.getLine());
    }

    @Test
    void writeCharLazilyOpensWithThisFilesOwnModeNotAHardcodedOne(@TempDir Path root)
            throws IOException {
        Path path = root.resolve("append.txt");
        Files.writeString(path, "AB");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_APPEND);
        assertTrue(file.writeChar('C'));
        file.close();

        assertEquals("ABC", Files.readString(path),
                "MODE_APPEND must append on its first lazy open, not truncate as MODE_WRITE would");
    }

    @Test
    void writeCharRefusesAndTouchesNothingWhenModeIsRead(@TempDir Path root) {
        Path path = root.resolve("missing.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);

        assertFalse(file.writeChar('A'));
        assertFalse(Files.exists(path));
    }

    @Test
    void readCharDistinguishesARealByteFromEndOfFile(@TempDir Path root) {
        Path path = root.resolve("chars.bin");
        AngFile writer = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertTrue(writer.writeChar('A'));
        writer.close();

        AngFile reader = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertEquals(Optional.of((int) 'A'), reader.readChar());
        assertEquals(Optional.of(-1), reader.readChar(), "fgetc()'s EOF sentinel is -1");
    }

    @Test
    void readCharDistinguishesANulByteFromEndOfFile(@TempDir Path root) {
        Path path = root.resolve("nul.bin");
        AngFile writer = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertTrue(writer.writeChar(0));
        writer.close();

        AngFile reader = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertEquals(Optional.of(0), reader.readChar(),
                "a NUL byte (0) must not be confused with the -1 EOF/failure sentinel");
        assertEquals(Optional.of(-1), reader.readChar());
    }

    @Test
    void readReturnsTheRequestedByteCountFromAnExistingFile(@TempDir Path root)
            throws IOException {
        Path path = root.resolve("hello.txt");
        Files.writeString(path, "Hello World");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertEquals("Hello", file.read(5));
    }

    @Test
    void writeThenGetLineRoundTrips(@TempDir Path root) {
        Path path = root.resolve("line.txt");
        AngFile writer = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertTrue(writer.write("first line"));
        writer.close();

        AngFile reader = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertEquals("first line", reader.getLine());
    }

    @Test
    void writeRefusesAndTouchesNothingWhenModeIsRead(@TempDir Path root) {
        Path path = root.resolve("missing.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);

        assertFalse(file.write("should not be written"));
        assertFalse(Files.exists(path));
    }

    @Test
    void putLineRefusesAndTouchesNothingWhenModeIsRead(@TempDir Path root) {
        Path path = root.resolve("missing.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);

        assertFalse(file.putLine("should not be written"));
        assertFalse(Files.exists(path));
    }

    @Test
    void putFormattedLineFormatsThenWrites(@TempDir Path root) {
        Path path = root.resolve("formatted.txt");
        AngFile writer = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertTrue(writer.putFormattedLine("%s scored %d", "Rowan", 42));
        writer.close();

        AngFile reader = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertEquals("Rowan scored 42", reader.getLine());
    }

    @Test
    void dirExistsIsFalseForAPlainFile(@TempDir Path root) throws IOException {
        Path path = root.resolve("plain.txt");
        Files.writeString(path, "not a directory");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        assertFalse(file.dirExists(path.toString()),
                "dir_exists() requires the S_IFDIR bit, not mere existence");
    }

    @Test
    void dirExistsIsTrueForADirectory(@TempDir Path root) {
        AngFile file = new AngFile(root.toString(), FileModeEnum.MODE_READ);
        assertTrue(file.dirExists(root.toString()));
    }

    @Test
    void dirExistsIsFalseForAMissingPath(@TempDir Path root) {
        AngFile file = new AngFile(root.toString(), FileModeEnum.MODE_READ);
        assertFalse(file.dirExists(root.resolve("does-not-exist").toString()));
    }

    @Test
    void dirCreateCreatesMissingParentDirectories(@TempDir Path root) {
        Path nested = root.resolve("a").resolve("b").resolve("c");
        AngFile file = new AngFile(root.toString(), FileModeEnum.MODE_READ);

        assertTrue(file.dirCreate(nested.toString()));
        assertTrue(Files.isDirectory(nested));
    }

    @Test
    void fileSkipSkipsForwardPastBytesInAnOpenStream(@TempDir Path root) {
        Path path = root.resolve("skip.bin");
        AngFile writer = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        for (char c : "ABCDE".toCharArray()) writer.writeChar(c);
        writer.close();

        AngFile reader = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        reader.readChar();
        assertTrue(reader.fileSkip(2));
        assertEquals(Optional.of((int) 'D'), reader.readChar());
    }

    @Test
    void fileSkipCanSeekBackwardsLikeCsFseek(@TempDir Path root) {
        Path path = root.resolve("skip-back.bin");
        AngFile writer = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        for (char c : "ABCDE".toCharArray()) writer.writeChar(c);
        writer.close();

        AngFile reader = new AngFile(path.toString(), FileModeEnum.MODE_READ);
        reader.readChar();
        reader.readChar();
        assertTrue(reader.fileSkip(-1),
                "file_skip() is fseek(f->fh, bytes, SEEK_CUR); a negative offset seeks backwards");
        assertEquals(Optional.of((int) 'B'), reader.readChar());
    }
}
