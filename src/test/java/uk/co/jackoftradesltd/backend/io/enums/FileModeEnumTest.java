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

package uk.co.jackoftradesltd.backend.io.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link FileModeEnum}, C's {@code file_mode} enum from {@code z-file.h}.
 * <p>
 * Expected values come from C. The declaration is {@code MODE_WRITE, MODE_READ, MODE_APPEND}, in that order, which
 * gives them the values 0, 1 and 2. {@code file_open} in {@code z-file.c} then maps each to an {@code fopen} mode:
 * write is {@code "wb"} (creates, truncates), read is {@code "rb"} (a missing file fails), and append is {@code "a+"}
 * (creates, keeps the contents, writes at the end).
 * <p>
 * Class FileModeEnumTest coded on 261017, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
class FileModeEnumTest {
    @TempDir
    Path dir;

    // ---- declaration ----

    /** C declares exactly three modes. */
    @Test
    void hasExactlyThreeConstants() {
        assertEquals(3, FileModeEnum.values().length);
    }

    /** The order is C's: write, read, append. */
    @Test
    void constantsAreInCOrder() {
        assertArrayEquals(
                new FileModeEnum[]{FileModeEnum.MODE_WRITE, FileModeEnum.MODE_READ, FileModeEnum.MODE_APPEND},
                FileModeEnum.values());
    }

    /** The ordinals are the values C's enum gives: 0, 1, 2. */
    @Test
    void ordinalsMatchCValues() {
        assertEquals(0, FileModeEnum.MODE_WRITE.ordinal());
        assertEquals(1, FileModeEnum.MODE_READ.ordinal());
        assertEquals(2, FileModeEnum.MODE_APPEND.ordinal());
    }

    /** The names are C's identifiers, which {@code valueOf} must find. */
    @Test
    void namesMatchCIdentifiers() {
        assertEquals(FileModeEnum.MODE_WRITE, FileModeEnum.valueOf("MODE_WRITE"));
        assertEquals(FileModeEnum.MODE_READ, FileModeEnum.valueOf("MODE_READ"));
        assertEquals(FileModeEnum.MODE_APPEND, FileModeEnum.valueOf("MODE_APPEND"));
    }

    /** C has no MODE_NONE or MODE_MAX, and the port adds neither. */
    @Test
    void hasNoNoneOrMax() {
        assertTrue(Arrays.stream(FileModeEnum.values()).noneMatch(m -> m.name().endsWith("NONE")
                || m.name().endsWith("MAX")));
    }

    // ---- what each mode does at file_open ----

    /** {@code "rb"} on a missing file fails and creates nothing. */
    @Test
    void readOnAMissingFileFailsWithoutCreatingIt() {
        Path p = dir.resolve("missing.txt");

        FileHandler fh = new FileHandler(p.toString());

        assertFalse(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertFalse(Files.exists(p));
    }

    /** {@code "rb"} on an existing file succeeds and reports read mode. */
    @Test
    void readOnAnExistingFileSucceeds() throws IOException {
        Path p = dir.resolve("a.txt");
        Files.writeString(p, "abc");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertEquals(FileModeEnum.MODE_READ, fh.getFileMode());
        fh.close();
    }

    /** {@code "wb"} on a missing file creates it, empty. */
    @Test
    void writeCreatesAMissingFile() throws IOException {
        Path p = dir.resolve("new.txt");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertEquals(FileModeEnum.MODE_WRITE, fh.getFileMode());
        fh.close();
        assertEquals(0, Files.size(p));
    }

    /** {@code "wb"} on an existing file truncates it. */
    @Test
    void writeTruncatesAnExistingFile() throws IOException {
        Path p = dir.resolve("old.txt");
        Files.writeString(p, "previous contents");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        fh.close();
        assertEquals(0, Files.size(p));
    }

    /** {@code "a+"} on a missing file creates it. */
    @Test
    void appendCreatesAMissingFile() {
        Path p = dir.resolve("log.txt");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));
        fh.close();
        assertTrue(Files.exists(p));
    }

    /** {@code "a+"} on an existing file keeps its contents and writes after them. */
    @Test
    void appendKeepsContentsAndWritesAtTheEnd() throws IOException {
        Path p = dir.resolve("log.txt");
        Files.writeString(p, "one\n");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("two\n"));
        fh.close();
        assertEquals("one\ntwo\n", Files.readString(p));
    }

    /** {@code FTYPE_SAVE} with write opens exclusively, so an existing file makes the open fail and is left alone. */
    @Test
    void writeWithSaveTypeFailsOnAnExistingFile() throws IOException {
        Path p = dir.resolve("save");
        Files.writeString(p, "keep me");

        FileHandler fh = new FileHandler(p.toString());

        assertFalse(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
        assertEquals("keep me", Files.readString(p));
    }
}
