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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the open, close, read, skip and directory methods of {@link AngFile}, against
 * {@code file_open}, {@code file_close}, {@code file_skip}, {@code file_readc}, {@code file_read},
 * {@code dir_exists} and {@code dir_create} in {@code z-file.c}.
 * <p>
 * Expected values come from the C: {@code "rb"} fails on a missing file and creates nothing,
 * {@code "wb"} truncates, {@code "a+"} keeps the contents, {@code FTYPE_SAVE} opens with {@code
 * O_CREAT | O_EXCL}, {@code fgetc} and {@code fread} return raw bytes, and {@code dir_create}
 * returns true for an existing directory and false when the path is a plain file.
 * <p>
 * Class AngFileOpenReadTest coded on 261016, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
class AngFileOpenReadTest {

    private static AngFile reader(Path path) {
        return new AngFile(path.toString(), FileModeEnum.MODE_READ);
    }

    // ---- file_open ----

    /**
     * {@code file_open} with {@code MODE_WRITE} and {@code FTYPE_SAVE} calls {@code open} with
     * {@code O_EXCL}, which fails when the file exists: a savefile is never overwritten.
     */
    @Test
    void openSaveTypeFailsOnAnExistingFileAndLeavesItAlone(@TempDir Path root) throws IOException {
        Path path = root.resolve("save.dat");
        Files.writeString(path, "keep me");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertFalse(file.open(FileTypeEnum.FTYPE_SAVE));
        assertFalse(file.isOpen());
        assertEquals("keep me", Files.readString(path));
    }

    @Test
    void openSaveTypeCreatesAMissingFile(@TempDir Path root) {
        Path path = root.resolve("save.dat");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertTrue(file.open(FileTypeEnum.FTYPE_SAVE));
        assertTrue(Files.exists(path));
    }

    /** {@code fopen(buf, "wb")} truncates an existing file. */
    @Test
    void openWriteTruncatesAnExistingFile(@TempDir Path root) throws IOException {
        Path path = root.resolve("old.txt");
        Files.writeString(path, "old contents");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertTrue(file.close());
        assertEquals("", Files.readString(path));
    }

    /** {@code fopen(buf, "a+")} leaves the existing contents in place. */
    @Test
    void openAppendKeepsAnExistingFilesContents(@TempDir Path root) throws IOException {
        Path path = root.resolve("log.txt");
        Files.writeString(path, "old contents");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_APPEND);
        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertTrue(file.close());
        assertEquals("old contents", Files.readString(path));
    }

    @Test
    void openFailsWhenTheParentDirectoryIsMissing(@TempDir Path root) {
        Path path = root.resolve("no-such-dir").resolve("file.txt");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertFalse(file.open(FileTypeEnum.FTYPE_TEXT), "fopen returns NULL, so file_open does");
        assertFalse(file.isOpen());
    }

    @Test
    void theConstructorOpensAndCreatesNothing(@TempDir Path root) {
        Path path = root.resolve("untouched.txt");

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertFalse(file.isOpen());
        assertFalse(Files.exists(path));
    }

    // ---- file_close ----

    @Test
    void isOpenFollowsOpenAndClose(@TempDir Path root) {
        Path path = root.resolve("state.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);

        assertFalse(file.isOpen());
        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertTrue(file.isOpen());
        assertTrue(file.close());
        assertFalse(file.isOpen());
    }

    @Test
    void closingAFileThatIsNotOpenSucceeds(@TempDir Path root) {
        AngFile file = new AngFile(root.resolve("never.txt").toString(), FileModeEnum.MODE_WRITE);

        assertTrue(file.close());
    }

    @Test
    void aClosedAppendFileCanBeOpenedAgainAndKeepsAppending(@TempDir Path root) throws IOException {
        Path path = root.resolve("again.txt");
        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_APPEND);

        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertTrue(file.writeChar('A'));
        assertTrue(file.close());
        assertTrue(file.open(FileTypeEnum.FTYPE_TEXT));
        assertTrue(file.writeChar('B'));
        assertTrue(file.close());

        assertEquals("AB", Files.readString(path));
    }

    // ---- file_readc ----

    @Test
    void readCharOpensTheFileItselfAndWalksToTheEnd(@TempDir Path root) throws IOException {
        Path path = root.resolve("two.bin");
        Files.write(path, new byte[] {'A', 'B'});

        AngFile file = reader(path);
        assertFalse(file.isOpen());
        assertEquals(Optional.of((int) 'A'), file.readChar());
        assertTrue(file.isOpen());
        assertEquals(Optional.of((int) 'B'), file.readChar());
        assertEquals(Optional.of(-1), file.readChar());
        assertEquals(Optional.of(-1), file.readChar(), "fgetc keeps returning EOF");
    }

    /** {@code fgetc} returns an unsigned value, so 0xFF reads as 255 and never as the EOF -1. */
    @Test
    void readCharReturnsAHighByteAsAnUnsignedValue(@TempDir Path root) throws IOException {
        Path path = root.resolve("high.bin");
        Files.write(path, new byte[] {(byte) 0xFF, (byte) 0x80});

        AngFile file = reader(path);
        assertEquals(Optional.of(255), file.readChar());
        assertEquals(Optional.of(128), file.readChar());
    }

    @Test
    void readCharIsEmptyOnAMissingFileAndCreatesNothing(@TempDir Path root) {
        Path path = root.resolve("missing.bin");

        assertEquals(Optional.empty(), reader(path).readChar());
        assertFalse(Files.exists(path));
    }

    @Test
    void readCharIsEmptyWhenTheFileWasCreatedForWriting(@TempDir Path root) throws IOException {
        Path path = root.resolve("write-only.bin");
        Files.write(path, new byte[] {'A'});

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
        assertEquals(Optional.empty(), file.readChar());
        assertFalse(file.isOpen(), "the refusal must not have opened, and so truncated, the file");
        assertEquals(1, Files.size(path));
    }

    // ---- file_read ----

    @Test
    void readReturnsRawBytesAboveSevenBitAscii(@TempDir Path root) throws IOException {
        Path path = root.resolve("raw.bin");
        byte[] raw = {0, 1, 0x7F, (byte) 0x80, (byte) 0xC3, (byte) 0xFF};
        Files.write(path, raw);

        assertArrayEquals(raw, reader(path).read(raw.length),
                "fread copies the bytes untouched, with no character decoding");
    }

    @Test
    void readReturnsFewerBytesWhenTheFileRunsOut(@TempDir Path root) throws IOException {
        Path path = root.resolve("short.bin");
        Files.write(path, "abc".getBytes(StandardCharsets.UTF_8));

        AngFile file = reader(path);
        assertArrayEquals("abc".getBytes(StandardCharsets.UTF_8), file.read(10));
    }

    /** {@code fread} at end of file returns 0 with no error, so the count is 0 and not -1. */
    @Test
    void readAtTheEndOfTheFileGivesAnEmptyArrayNotNull(@TempDir Path root) throws IOException {
        Path path = root.resolve("one.bin");
        Files.write(path, new byte[] {'A'});

        AngFile file = reader(path);
        assertEquals(1, file.read(5).length);
        assertEquals(0, file.read(5).length);
    }

    @Test
    void readOfZeroBytesGivesAnEmptyArray(@TempDir Path root) throws IOException {
        Path path = root.resolve("zero.bin");
        Files.write(path, new byte[] {'A'});

        AngFile file = reader(path);
        assertEquals(0, file.read(0).length);
        assertEquals(Optional.of((int) 'A'), file.readChar(), "nothing was consumed");
    }

    @Test
    void readContinuesWhereReadCharStopped(@TempDir Path root) throws IOException {
        Path path = root.resolve("mixed.bin");
        Files.write(path, "ABCDE".getBytes(StandardCharsets.UTF_8));

        AngFile file = reader(path);
        assertEquals(Optional.of((int) 'A'), file.readChar());
        assertArrayEquals("BCD".getBytes(StandardCharsets.UTF_8), file.read(3));
        assertEquals(Optional.of((int) 'E'), file.readChar());
    }

    @Test
    void readIsNullOnAMissingFileAndCreatesNothing(@TempDir Path root) {
        Path path = root.resolve("missing.bin");

        assertNull(reader(path).read(4));
        assertFalse(Files.exists(path));
    }

    @Test
    void readIsNullWhenTheFileWasCreatedForWriting(@TempDir Path root) throws IOException {
        Path path = root.resolve("write-only.bin");
        Files.write(path, new byte[] {'A'});

        AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_APPEND);
        assertNull(file.read(1), "fread on a write-only handle fails, which file_read reports as -1");
        assertEquals(1, Files.size(path));
    }

    // ---- file_skip ----

    @Test
    void fileSkipDoesNotOpenTheFileItself(@TempDir Path root) throws IOException {
        Path path = root.resolve("skip.bin");
        Files.write(path, "ABCDE".getBytes(StandardCharsets.UTF_8));

        AngFile file = reader(path);
        assertFalse(file.fileSkip(2));
        assertFalse(file.isOpen());
    }

    @Test
    void fileSkipThenReadSeesTheByteAfterTheSkippedOnes(@TempDir Path root) throws IOException {
        Path path = root.resolve("skip.bin");
        Files.write(path, "ABCDEFGH".getBytes(StandardCharsets.UTF_8));

        AngFile file = reader(path);
        assertEquals(Optional.of((int) 'A'), file.readChar());
        assertTrue(file.fileSkip(3));
        assertArrayEquals("EF".getBytes(StandardCharsets.UTF_8), file.read(2));
    }

    @Test
    void fileSkipOfExactlyTheRemainingBytesSucceedsAndLeavesEndOfFile(@TempDir Path root)
            throws IOException {
        Path path = root.resolve("skip.bin");
        Files.write(path, "ABC".getBytes(StandardCharsets.UTF_8));

        AngFile file = reader(path);
        assertEquals(Optional.of((int) 'A'), file.readChar());
        assertTrue(file.fileSkip(2));
        assertEquals(Optional.of(-1), file.readChar());
    }

    /**
     * C's {@code fseek} succeeds past the end of a regular file, and the next read then reports
     * the end of the file. A skip past the end returns true whether or not the stream has been read.
     */
    @Test
    void fileSkipPastTheEndReturnsTrueOnceTheStreamHasBeenRead(@TempDir Path root)
            throws IOException {
        Path path = root.resolve("skip.bin");
        Files.write(path, "ABC".getBytes(StandardCharsets.UTF_8));

        AngFile file = reader(path);
        assertEquals(Optional.of((int) 'A'), file.readChar());
        assertTrue(file.fileSkip(5));
        assertEquals(Optional.of(-1), file.readChar());
    }

    // ---- dir_exists / dir_create ----

    @Test
    void dirExistsIsFalseForNullAndEmptyPaths() {
        assertFalse(AngFile.dirExists(null));
        assertFalse(AngFile.dirExists(""));
    }

    @Test
    void dirCreateReturnsTrueForADirectoryThatAlreadyExists(@TempDir Path root) {
        assertTrue(AngFile.dirCreate(root.toString()), "dir_create starts with dir_exists");
    }

    /** C's final {@code mkdir} fails with {@code EEXIST} on a plain file, so {@code dir_create} is false. */
    @Test
    void dirCreateReturnsFalseWhenThePathIsAPlainFile(@TempDir Path root) throws IOException {
        Path path = root.resolve("plain.txt");
        Files.writeString(path, "x");

        assertFalse(AngFile.dirCreate(path.toString()));
        assertTrue(Files.isRegularFile(path));
    }

    /** A parent that is a plain file makes C's {@code mkdir} of the child fail with {@code ENOTDIR}. */
    @Test
    void dirCreateReturnsFalseWhenAParentIsAPlainFile(@TempDir Path root) throws IOException {
        Path blocker = root.resolve("blocker");
        Files.writeString(blocker, "x");

        assertFalse(AngFile.dirCreate(blocker.resolve("child").toString()));
    }

    @Test
    void dirCreateReturnsFalseForNullAndEmptyPaths() {
        assertFalse(AngFile.dirCreate(null));
        assertFalse(AngFile.dirCreate(""));
    }

    @Test
    void dirCreateMakesASingleMissingDirectoryAndDirExistsSeesIt(@TempDir Path root) {
        Path dir = root.resolve("fresh");
        assertFalse(AngFile.dirExists(dir.toString()));

        assertTrue(AngFile.dirCreate(dir.toString()));
        assertTrue(AngFile.dirExists(dir.toString()));
    }

    /**
     * Accepted deviation (docs/Accepted_Deviations.md): for a fresh path ending in a separator C's
     * loop creates the directory and the final {@code mkdir} then fails with {@code EEXIST}, so C
     * returns false; here the directories exist, so the result is true.
     */
    @Test
    void dirCreateReturnsTrueForAFreshPathWithATrailingSeparator(@TempDir Path root) {
        Path dir = root.resolve("x").resolve("y");

        assertTrue(AngFile.dirCreate(dir + java.io.File.separator));
        assertTrue(Files.isDirectory(dir));
    }

    @Test
    void dirCreateCalledTwiceReturnsTrueBothTimes(@TempDir Path root) {
        Path dir = root.resolve("a").resolve("b");

        assertTrue(AngFile.dirCreate(dir.toString()));
        assertTrue(AngFile.dirCreate(dir.toString()));
    }
}
