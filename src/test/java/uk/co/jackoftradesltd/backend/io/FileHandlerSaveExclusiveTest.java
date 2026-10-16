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
 * Tests for open issue 4 in {@code docs/FileHandler_open_issues.md}: {@link FileHandler#open} with
 * {@code MODE_WRITE} and {@code FTYPE_SAVE} must fail when the file already exists.
 * <p>
 * In C, {@code file_open} opens a {@code FTYPE_SAVE} write with {@code O_CREAT | O_EXCL}, so an
 * existing savefile makes it return {@code NULL}. Every other mode and file type goes through
 * {@code fopen}, which truncates ("wb") or appends ("a+") and never checks the type.
 * <p>
 * Class FileHandlerSaveExclusiveTest coded on 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
class FileHandlerSaveExclusiveTest {
    @TempDir
    Path dir;

    /** {@code O_CREAT} still creates the file when it is missing. */
    @Test
    void saveWriteOnMissingFileCreatesIt() {
        Path p = dir.resolve("hero.sav");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));

        assertTrue(Files.exists(p));
        assertTrue(fh.isOpen());
        assertTrue(fh.close());
    }

    /** {@code O_EXCL} fails on an existing file, and the open must report it. */
    @Test
    void saveWriteOnExistingFileFails() throws IOException {
        Path p = dir.resolve("hero.sav");
        Files.writeString(p, "precious");
        FileHandler fh = new FileHandler(p.toString());

        assertFalse(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));

        assertFalse(fh.isOpen());
    }

    /** The whole point of {@code O_EXCL}: the existing savefile is left untouched, not truncated. */
    @Test
    void failedSaveWriteLeavesContentIntact() throws IOException {
        Path p = dir.resolve("hero.sav");
        Files.writeString(p, "precious");
        FileHandler fh = new FileHandler(p.toString());

        fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE);
        fh.close();

        assertEquals("precious", Files.readString(p));
    }

    /** Only {@code FTYPE_SAVE} is exclusive: {@code FTYPE_TEXT} still truncates ("wb"). */
    @Test
    void textWriteOnExistingFileStillTruncates() throws IOException {
        Path p = dir.resolve("a.txt");
        Files.writeString(p, "old content");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.close());

        assertEquals(0, Files.size(p));
    }

    /** {@code FTYPE_RAW} is also not exclusive. */
    @Test
    void rawWriteOnExistingFileStillTruncates() throws IOException {
        Path p = dir.resolve("a.raw");
        Files.writeString(p, "old content");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_RAW));
        assertTrue(fh.close());

        assertEquals(0, Files.size(p));
    }

    /** C's {@code MODE_APPEND} is {@code fopen("a+")} whatever the type, so a savefile can be appended to. */
    @Test
    void saveAppendOnExistingFileSucceedsAndKeepsContent() throws IOException {
        Path p = dir.resolve("hero.sav");
        Files.writeString(p, "precious");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_SAVE));
        assertTrue(fh.close());

        assertEquals("precious", Files.readString(p));
    }

    /** C's {@code MODE_READ} is {@code fopen("rb")} whatever the type. */
    @Test
    void saveReadOnExistingFileSucceeds() throws IOException {
        Path p = dir.resolve("hero.sav");
        Files.writeString(p, "precious");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_SAVE));
        assertEquals('p', fh.readCharacter());
        assertTrue(fh.close());
    }

    /** A zero-length existing file still counts as existing: {@code O_EXCL} does not look at size. */
    @Test
    void saveWriteOnEmptyExistingFileFails() throws IOException {
        Path p = dir.resolve("hero.sav");
        Files.createFile(p);
        FileHandler fh = new FileHandler(p.toString());

        assertFalse(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
    }

    /** Opening a savefile, closing it, then opening it again for write must fail the second time. */
    @Test
    void secondSaveWriteAfterFirstFails() {
        Path p = dir.resolve("hero.sav");
        FileHandler first = new FileHandler(p.toString());
        assertTrue(first.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
        assertTrue(first.close());

        FileHandler second = new FileHandler(p.toString());
        assertFalse(second.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
    }

    /**
     * {@code O_EXCL} is a single atomic step in C. The Java check is exists-then-open, which has a
     * gap, so this only runs the check from two handles in turn: the second must lose.
     */
    @Test
    void twoHandlersOneSavefileOnlyOneWins() {
        Path p = dir.resolve("hero.sav");
        FileHandler a = new FileHandler(p.toString());
        FileHandler b = new FileHandler(p.toString());

        assertTrue(a.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
        assertFalse(b.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));

        assertTrue(a.close());
    }
}
