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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AngDir}, the port of C's {@code ang_dir}/{@code my_dopen}/
 * {@code alter_ang_dir_only_files}/{@code my_dread} ({@code src/z-file.c}).
 * <p>
 * Expected values are derived from the POSIX branch of {@code my_dread} ({@code z-file.c}, the
 * {@code HAVE_DIRENT_H} arm), not from {@link AngDir} itself: files-only iteration skips anything
 * {@code S_ISDIR} reports as a directory, a {@code stat()} failure (modelled here with a broken
 * symlink) is skipped silently rather than surfaced, and once {@code only_files} is cleared the two
 * synthetic {@code "."}/{@code ".."} entries appear alongside sub-directories.
 * <p>
 * Class AngDirTest coded on 260914, commented in full on 260914.
 *
 * @author Rowan Crowther
 */
class AngDirTest {

    /**
     * Reads every entry from {@code dir} until it reports exhaustion.
     *
     * @param dir the handle to drain
     * @return every name {@link AngDir#read()} produced, in order
     */
    private static List<String> drain(AngDir dir) {
        List<String> names = new ArrayList<>();
        String name = dir.read();

        while (!name.isEmpty()) {
            names.add(name);
            name = dir.read();
        }

        return names;
    }

    @Test
    void filesOnlyByDefaultSkipsSubdirectories(@TempDir Path root) throws IOException {
        Files.createFile(root.resolve("a.txt"));
        Files.createFile(root.resolve("b.txt"));
        Files.createDirectory(root.resolve("sub"));

        AngDir dir = AngDir.angDirFactory(root.toString());
        assertNotNull(dir);

        assertEquals(Set.of("a.txt", "b.txt"), new HashSet<>(drain(dir)));
    }

    @Test
    void readReturnsBareFileNamesNotFullPaths(@TempDir Path root) throws IOException {
        Files.createFile(root.resolve("a.txt"));

        AngDir dir = AngDir.angDirFactory(root.toString());
        assertNotNull(dir);

        String name = dir.read();
        assertEquals("a.txt", name);
        assertFalse(name.contains(root.toString()));
    }

    @Test
    void exhaustedDirectoryKeepsReturningEmptyString(@TempDir Path root) throws IOException {
        Files.createFile(root.resolve("a.txt"));

        AngDir dir = AngDir.angDirFactory(root.toString());
        assertNotNull(dir);

        assertEquals("a.txt", dir.read());
        assertEquals("", dir.read());
        assertEquals("", dir.read());
    }

    @Test
    void aBrokenSymlinkIsSkippedRatherThanReturned(@TempDir Path root) throws IOException {
        Files.createFile(root.resolve("a.txt"));
        Files.createSymbolicLink(root.resolve("dangling"), root.resolve("does-not-exist"));

        AngDir dir = AngDir.angDirFactory(root.toString());
        assertNotNull(dir);

        assertEquals(List.of("a.txt"), drain(dir));
    }

    @Test
    void alterOnlyFilesReturnsThePreviousValue(@TempDir Path root) {
        AngDir dir = AngDir.angDirFactory(root.toString());
        assertNotNull(dir);

        assertTrue(dir.alterOnlyFiles(false), "onlyFiles defaults to true, matching my_dopen()");
        assertFalse(dir.alterOnlyFiles(true));
    }

    @Test
    void clearingOnlyFilesYieldsDotAndDotDotFirst(@TempDir Path root) throws IOException {
        Files.createFile(root.resolve("a.txt"));

        AngDir dir = AngDir.angDirFactory(root.toString());
        assertNotNull(dir);
        dir.alterOnlyFiles(false);

        assertEquals(".", dir.read());
        assertEquals("..", dir.read());
        assertEquals("a.txt", dir.read());
        assertEquals("", dir.read());
    }

    @Test
    void clearingOnlyFilesAlsoYieldsSubdirectories(@TempDir Path root) throws IOException {
        Files.createDirectory(root.resolve("sub"));

        AngDir dir = AngDir.angDirFactory(root.toString());
        assertNotNull(dir);
        dir.alterOnlyFiles(false);

        List<String> names = drain(dir);
        assertTrue(names.contains("sub"), "sub-directories should survive once only_files is false");
    }

    @Test
    void angDirFactoryReturnsNullForAMissingDirectory(@TempDir Path root) {
        AngDir dir = AngDir.angDirFactory(root.resolve("does-not-exist").toString());

        assertNull(dir, "my_dopen() returns NULL when opendir() fails; the factory mirrors that");
    }
}
