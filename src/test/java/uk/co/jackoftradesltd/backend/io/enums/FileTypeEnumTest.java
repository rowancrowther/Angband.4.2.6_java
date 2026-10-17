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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftradesltd.backend.io.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests for {@link FileTypeEnum}, C's {@code file_type} enum from {@code z-file.h}.
 * <p>
 * Expected values come from C. The declaration is {@code FTYPE_TEXT = 1, FTYPE_SAVE, FTYPE_RAW, FTYPE_HTML}. In {@code
 * file_open} in {@code z-file.c} the type is read in one place: {@code MODE_WRITE} with {@code FTYPE_SAVE} calls {@code
 * open(O_CREAT | O_EXCL | O_WRONLY, S_IRUSR | S_IWUSR)}, which fails on an existing file and creates a new one owner-only.
 * Every other combination goes to {@code fopen} ({@code "wb"}, {@code "rb"}, {@code "a+"}) and ignores the type; the
 * type is otherwise only handed to {@code file_open_hook}, which the port does not have.
 * <p>
 * Class FileTypeEnumTest coded on 261017, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
class FileTypeEnumTest {
    @TempDir
    Path dir;

    // ---- declaration ----

    /** C declares four types; the port adds one placeholder after them. */
    @Test
    void hasCsFourTypesPlusOnePlaceholder() {
        assertEquals(5, FileTypeEnum.values().length);
    }

    /** The first four are C's, in C's order: text, save, raw, html. */
    @Test
    void firstFourConstantsAreInCOrder() {
        FileTypeEnum[] v = FileTypeEnum.values();

        assertEquals(FileTypeEnum.FTYPE_TEXT, v[0]);
        assertEquals(FileTypeEnum.FTYPE_SAVE, v[1]);
        assertEquals(FileTypeEnum.FTYPE_RAW, v[2]);
        assertEquals(FileTypeEnum.FTYPE_HTML, v[3]);
    }

    /** The placeholder is not one of C's types, so it comes after all four. */
    @Test
    void placeholderIsLast() {
        FileTypeEnum[] v = FileTypeEnum.values();

        assertEquals(FileTypeEnum.FTYPE_NULL, v[v.length - 1]);
    }

    /** The names are C's identifiers, which {@code valueOf} must find. */
    @Test
    void namesMatchCIdentifiers() {
        assertEquals(FileTypeEnum.FTYPE_TEXT, FileTypeEnum.valueOf("FTYPE_TEXT"));
        assertEquals(FileTypeEnum.FTYPE_SAVE, FileTypeEnum.valueOf("FTYPE_SAVE"));
        assertEquals(FileTypeEnum.FTYPE_RAW, FileTypeEnum.valueOf("FTYPE_RAW"));
        assertEquals(FileTypeEnum.FTYPE_HTML, FileTypeEnum.valueOf("FTYPE_HTML"));
    }

    // ---- the types C treats alike: a plain "wb" / "rb" / "a+" ----

    /** {@code "wb"} on a missing file creates it, empty, whichever of these types is passed. */
    @ParameterizedTest
    @EnumSource(value = FileTypeEnum.class, names = {"FTYPE_TEXT", "FTYPE_RAW", "FTYPE_HTML"})
    void plainWriteCreatesAMissingFile(FileTypeEnum type) throws IOException {
        Path p = dir.resolve("new.out");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, type));
        fh.close();
        assertEquals(0, Files.size(p));
    }

    /** {@code "wb"} on an existing file truncates it, whichever of these types is passed. */
    @ParameterizedTest
    @EnumSource(value = FileTypeEnum.class, names = {"FTYPE_TEXT", "FTYPE_RAW", "FTYPE_HTML"})
    void plainWriteTruncatesAnExistingFile(FileTypeEnum type) throws IOException {
        Path p = dir.resolve("old.out");
        Files.writeString(p, "previous contents");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, type));
        fh.close();
        assertEquals(0, Files.size(p));
    }

    /** {@code "rb"} ignores the type, so every C type reads an existing file. */
    @ParameterizedTest
    @EnumSource(value = FileTypeEnum.class, names = {"FTYPE_TEXT", "FTYPE_SAVE", "FTYPE_RAW", "FTYPE_HTML"})
    void readIgnoresTheType(FileTypeEnum type) throws IOException {
        Path p = dir.resolve("a.out");
        Files.writeString(p, "abc");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_READ, type));
        fh.close();
        assertEquals("abc", Files.readString(p));
    }

    /** {@code "rb"} on a missing file fails whichever type is passed, and creates nothing. */
    @ParameterizedTest
    @EnumSource(value = FileTypeEnum.class, names = {"FTYPE_TEXT", "FTYPE_SAVE", "FTYPE_RAW", "FTYPE_HTML"})
    void readOnAMissingFileFailsForEveryType(FileTypeEnum type) {
        Path p = dir.resolve("missing.out");

        FileHandler fh = new FileHandler(p.toString());

        assertFalse(fh.open(FileModeEnum.MODE_READ, type));
        assertFalse(Files.exists(p));
    }

    /** {@code "a+"} ignores the type: a missing file is created and an existing one keeps its contents. */
    @ParameterizedTest
    @EnumSource(value = FileTypeEnum.class, names = {"FTYPE_TEXT", "FTYPE_SAVE", "FTYPE_RAW", "FTYPE_HTML"})
    void appendIgnoresTheType(FileTypeEnum type) throws IOException {
        Path created = dir.resolve("created.out");
        Path existing = dir.resolve("existing.out");
        Files.writeString(existing, "one\n");

        FileHandler fresh = new FileHandler(created.toString());
        FileHandler old = new FileHandler(existing.toString());

        assertTrue(fresh.open(FileModeEnum.MODE_APPEND, type));
        fresh.close();
        assertTrue(Files.exists(created));
        assertTrue(old.open(FileModeEnum.MODE_APPEND, type));
        assertTrue(old.putLine("two\n"));
        old.close();
        assertEquals("one\ntwo\n", Files.readString(existing));
    }

    // ---- FTYPE_SAVE: the one type that changes the open ----

    /** {@code O_EXCL}: a save write on a missing file creates it. */
    @Test
    void saveWriteCreatesAMissingFile() throws IOException {
        Path p = dir.resolve("hero.sav");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
        fh.close();
        assertEquals(0, Files.size(p));
    }

    /** {@code O_EXCL}: a save write on an existing file fails, and the file is left as it was. */
    @Test
    void saveWriteFailsOnAnExistingFileAndLeavesItAlone() throws IOException {
        Path p = dir.resolve("hero.sav");
        Files.writeString(p, "keep me");

        FileHandler fh = new FileHandler(p.toString());

        assertFalse(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
        assertEquals("keep me", Files.readString(p));
    }

    /** The same existing file opens for a plain write of any other type, which is the contrast with the test above. */
    @ParameterizedTest
    @EnumSource(value = FileTypeEnum.class, names = {"FTYPE_TEXT", "FTYPE_RAW", "FTYPE_HTML"})
    void otherTypesDoNotRefuseAnExistingFile(FileTypeEnum type) throws IOException {
        Path p = dir.resolve("hero.sav");
        Files.writeString(p, "replace me");

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, type));
        fh.close();
        assertEquals(0, Files.size(p));
    }

    /** {@code S_IRUSR | S_IWUSR}: a save write creates the file readable and writable by its owner only. */
    @Test
    void saveWriteCreatesTheFileOwnerOnly() throws IOException {
        Path p = dir.resolve("hero.sav");
        assumeTrue(p.getFileSystem().supportedFileAttributeViews().contains("posix"));

        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
        fh.close();
        assertEquals(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
                Files.getPosixFilePermissions(p));
    }
}
