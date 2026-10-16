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
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests for point 2 in {@code docs/FileHandler_port_findings.md}: only a {@code FTYPE_SAVE} write is created
 * {@code 0600}.
 * <p>
 * In C, {@code file_open} opens a {@code FTYPE_SAVE} write with {@code open(..., S_IRUSR | S_IWUSR)}. Every other
 * creation goes through {@code fopen}, which asks for {@code 0666} and leaves the umask to trim it. The expected value
 * for those is therefore whatever a plain {@link Files#createFile} gives under the current umask.
 * <p>
 * Class FileHandlerSavePermissionsTest coded on 261016, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
class FileHandlerSavePermissionsTest {
    @TempDir
    Path dir;

    private static void assumePosix(Path p) {
        assumeTrue(p.getFileSystem().supportedFileAttributeViews().contains("posix"));
    }

    private Set<PosixFilePermission> umaskDefault() throws IOException {
        Path probe = Files.createFile(dir.resolve("probe"));
        Set<PosixFilePermission> perms = Files.getPosixFilePermissions(probe);
        Files.delete(probe);
        return perms;
    }

    /** {@code S_IRUSR | S_IWUSR}: the owner reads and writes, nobody else does anything. */
    @Test
    void saveWriteCreatesFileOwnerOnly() throws IOException {
        assumePosix(dir);
        Path p = dir.resolve("hero.sav");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
        assertTrue(fh.close());

        assertEquals(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
                Files.getPosixFilePermissions(p));
    }

    /** {@code fopen("wb")} on a {@code FTYPE_TEXT} file: {@code 0666} minus the umask, not {@code 0600}. */
    @Test
    void textWriteCreatesFileWithUmaskDefault() throws IOException {
        assumePosix(dir);
        Set<PosixFilePermission> expected = umaskDefault();
        Path p = dir.resolve("a.txt");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.close());

        assertEquals(expected, Files.getPosixFilePermissions(p));
    }

    /** {@code fopen("wb")} on a {@code FTYPE_RAW} file is the same. */
    @Test
    void rawWriteCreatesFileWithUmaskDefault() throws IOException {
        assumePosix(dir);
        Set<PosixFilePermission> expected = umaskDefault();
        Path p = dir.resolve("a.raw");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_RAW));
        assertTrue(fh.close());

        assertEquals(expected, Files.getPosixFilePermissions(p));
    }

    /** {@code fopen("a+")} creates a missing file with the umask default, even when the type is {@code FTYPE_SAVE}. */
    @Test
    void appendCreatesFileWithUmaskDefault() throws IOException {
        assumePosix(dir);
        Set<PosixFilePermission> expected = umaskDefault();
        Path p = dir.resolve("hero.sav");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_SAVE));
        assertTrue(fh.close());

        assertEquals(expected, Files.getPosixFilePermissions(p));
    }

    /** Re-opening an existing file for write must not change its mode: C's {@code fopen} never calls chmod. */
    @Test
    void textWriteOnExistingFileKeepsItsMode() throws IOException {
        assumePosix(dir);
        Path p = dir.resolve("a.txt");
        Files.writeString(p, "old");
        Set<PosixFilePermission> before = Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE);
        Files.setPosixFilePermissions(p, before);
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.close());

        assertEquals(before, Files.getPosixFilePermissions(p));
    }
}
