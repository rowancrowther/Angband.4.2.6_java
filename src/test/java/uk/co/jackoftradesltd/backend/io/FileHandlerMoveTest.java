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
 * Unit tests for {@link FileHandler#fileMove(Path)}, the port of C's {@code file_move}
 * ({@code src/z-file.c}).
 * <p>
 * Expected values come from C's {@code file_move}, which is {@code rename(buf, aux) == 0} on a
 * plain copy of each path ({@code path_parse} does no expansion in 4.2.6). On POSIX
 * {@code rename(2)} replaces an existing target atomically, succeeds as a no-op when both names
 * are the same file, and fails (returning false) when the source is missing. The savefile, score,
 * textblock and prefs callers all rely on the replace.
 * <p>
 * Class FileHandlerMoveTest coded on 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
class FileHandlerMoveTest {
    @TempDir
    Path dir;

    private Path write(String name, String content) throws IOException {
        Path p = dir.resolve(name);
        Files.writeString(p, content);
        return p;
    }

    /**
     * A free target: the file arrives under the new name, the old name is gone, and the handler
     * follows the file.
     */
    @Test
    void movesToFreeTarget() throws IOException {
        Path src = write("a.txt", "alpha");
        Path dst = dir.resolve("b.txt");
        FileHandler fh = new FileHandler(src.toString());

        assertTrue(fh.fileMove(dst));

        assertFalse(Files.exists(src));
        assertEquals("alpha", Files.readString(dst));
        assertTrue(fh.fileExists(), "handler must now point at the new name");
    }

    /**
     * The point 7 case: an existing target is replaced, as {@code rename} does, not refused.
     */
    @Test
    void replacesExistingTarget() throws IOException {
        Path src = write("new.sav", "new");
        Path dst = write("live.sav", "old");
        FileHandler fh = new FileHandler(src.toString());

        assertTrue(fh.fileMove(dst));

        assertFalse(Files.exists(src));
        assertEquals("new", Files.readString(dst));
    }

    /**
     * A missing source fails, as {@code rename} gives ENOENT, and the handler keeps its old path.
     */
    @Test
    void missingSourceFailsAndKeepsPath() throws IOException {
        Path src = dir.resolve("nothing.txt");
        Path dst = dir.resolve("b.txt");
        FileHandler fh = new FileHandler(src.toString());

        assertFalse(fh.fileMove(dst));

        assertFalse(Files.exists(dst));
        assertFalse(fh.fileExists());
        // the handler still refers to the source: creating it makes the handler see it
        Files.writeString(src, "late");
        assertTrue(fh.fileExists());
    }

    /**
     * Source and target the same file: {@code rename} succeeds and changes nothing.
     */
    @Test
    void sameFileIsSuccessfulNoOp() throws IOException {
        Path src = write("a.txt", "alpha");
        FileHandler fh = new FileHandler(src.toString());

        assertTrue(fh.fileMove(src));

        assertEquals("alpha", Files.readString(src));
        assertTrue(fh.fileExists());
    }

    /**
     * A null target cannot be renamed to, so it is false rather than an exception.
     */
    @Test
    void nullTargetIsFalse() throws IOException {
        Path src = write("a.txt", "alpha");
        FileHandler fh = new FileHandler(src.toString());

        assertFalse(fh.fileMove(null));

        assertEquals("alpha", Files.readString(src));
    }

    /**
     * A non-empty directory as target: {@code rename} fails with ENOTEMPTY, so the move is false
     * and the source is untouched.
     */
    @Test
    void nonEmptyDirectoryTargetFails() throws IOException {
        Path src = write("a.txt", "alpha");
        Path dstDir = Files.createDirectory(dir.resolve("d"));
        Files.writeString(dstDir.resolve("inner"), "x");
        FileHandler fh = new FileHandler(src.toString());

        assertFalse(fh.fileMove(dstDir));

        assertEquals("alpha", Files.readString(src));
        assertTrue(fh.fileExists());
    }

    /**
     * Moving while open: the move succeeds, and the handler must not report itself open afterwards
     * because the streams were closed to do it.
     */
    @Test
    void movingAnOpenHandlerLeavesItClosed() throws IOException {
        Path src = write("a.txt", "alpha");
        Path dst = dir.resolve("b.txt");
        FileHandler fh = new FileHandler(src.toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.isOpen());

        assertTrue(fh.fileMove(dst));

        assertEquals("alpha", Files.readString(dst));
        assertFalse(fh.isOpen(), "streams were closed for the move, so isOpen must be false");
    }
}
