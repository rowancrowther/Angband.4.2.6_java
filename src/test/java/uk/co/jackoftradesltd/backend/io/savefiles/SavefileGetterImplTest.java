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

package uk.co.jackoftradesltd.backend.io.savefiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SavefileGetterImpl}, the port of C's {@code struct
 * savefile_getter_impl} together with {@code got_savefile()} ({@code
 * src/ui-game.c}).
 * <p>
 * Expected values are derived from {@code got_savefile()} itself ({@code
 * ui-game.c:1187-1256}), not from the Java implementation: a directory that
 * fails to open returns {@code false} immediately and keeps doing so on
 * every later call ({@code ui-game.c:1198-1200}, the {@code *pg == NULL}
 * set-up branch); an opened-but-empty directory breaks out of the read loop
 * on the first {@code my_dread()} and, once closed, also returns {@code
 * false} from then on ({@code ui-game.c:1230-1231, 1251-1255}); each
 * present entry is reported {@code true} once before the next call moves on
 * to the following entry ({@code ui-game.c:1241-1249}); sub-directories are
 * skipped, since {@link AngDir} defaults to files-only iteration matching
 * {@code my_dread()}'s {@code S_ISDIR} check ({@code z-file.c}). Every test
 * points {@code AngbandDirs.ANGBAND_DIRS.SAVE} at a {@code @TempDir} and
 * restores the original path afterwards, since that path is process-wide
 * state.
 * <p>
 * Class SavefileGetterImplTest coded on 260915, commented in full on
 * 260915.
 *
 * @author Rowan Crowther
 */
class SavefileGetterImplTest {

    private String originalSavePath;

    private static void pointSaveDirAt(Path dir) {
        AngbandDirs.ANGBAND_DIRS.SAVE.setPath(dir.toString() + File.separator);
    }

    @BeforeEach
    void rememberOriginalSavePath() {
        originalSavePath = AngbandDirs.ANGBAND_DIRS.SAVE.getPath();
    }

    @AfterEach
    void restoreOriginalSavePath() {
        AngbandDirs.ANGBAND_DIRS.SAVE.setPath(originalSavePath);
    }

    @Test
    void gotSavefileReturnsFalseRepeatedlyWhenTheDirectoryCannotBeOpened(@TempDir Path root) {
        pointSaveDirAt(root.resolve("does-not-exist"));

        SavefileGetterImpl getter = new SavefileGetterImpl();

        assertFalse(getter.gotSavefile(),
                "my_dopen() failing returns false immediately (ui-game.c:1198-1200)");
        assertFalse(getter.gotSavefile(),
                "a repeat call must still return false, not throw (ui-game.c:1211-1215)");
    }

    @Test
    void gotSavefileReturnsFalseRepeatedlyForAnEmptyDirectory(@TempDir Path root) {
        pointSaveDirAt(root);

        SavefileGetterImpl getter = new SavefileGetterImpl();

        assertFalse(getter.gotSavefile(),
                "no_entry on the first my_dread() breaks the loop (ui-game.c:1230-1231)");
        assertFalse(getter.gotSavefile(),
                "a repeat call after exhaustion must not touch the closed directory again "
                        + "(ui-game.c:1211-1215)");
    }

    @Test
    void gotSavefileReturnsTrueOnceForEachSavefileThenFalse(@TempDir Path root) throws IOException {
        Files.createFile(root.resolve("1.PlayerOne"));
        Files.createFile(root.resolve("2.PlayerTwo"));
        pointSaveDirAt(root);

        SavefileGetterImpl getter = new SavefileGetterImpl();

        assertTrue(getter.gotSavefile(), "first entry found (ui-game.c:1241-1249)");
        assertTrue(getter.gotSavefile(), "second entry found (ui-game.c:1241-1249)");
        assertFalse(getter.gotSavefile(), "directory drained after both entries (ui-game.c:1230-1231)");
    }

    @Test
    void gotSavefileSkipsSubdirectoriesInTheSaveDirectory(@TempDir Path root) throws IOException {
        Files.createDirectory(root.resolve("sub"));
        pointSaveDirAt(root);

        SavefileGetterImpl getter = new SavefileGetterImpl();

        assertFalse(getter.gotSavefile(),
                "AngDir defaults to files-only, matching my_dread()'s S_ISDIR skip (z-file.c)");
    }
}
