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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for open issue 8 in {@code docs/FileHandler_open_issues.md}: {@link FileHandler#fileGetSavefile}
 * must build the name the way C's {@code file_get_savefile} does.
 * <p>
 * In C the first candidate is {@code "%s%lu.%s"} (base, {@code Rand_simple(1000000)}, extension), and
 * each retry is {@code "%s%lu%u.%s"} with the retry count after the random number. {@code file_exists}
 * is called on that candidate as a full path, so the check is against the candidate itself, not
 * against anything related to the file the handler was constructed with.
 * <p>
 * Class FileHandlerSavefileNameTest coded on 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
class FileHandlerSavefileNameTest {
    @TempDir
    Path dir;

    /** {@code "%s%lu.%s"}: base, digits, dot, extension. */
    @Test
    void nameIsBaseThenNumberThenExtension() {
        String base = dir.resolve("hero").toString();
        FileHandler fh = new FileHandler(dir.resolve("anything.txt").toString());

        String name = assertDoesNotThrow(() -> fh.fileGetSavefile(base, "sav"));

        assertNotNull(name);
        assertTrue(Pattern.matches(Pattern.quote(base) + "\\d{1,6}\\.sav", name),
                "unexpected name: " + name);
    }

    /** The number is below 1000000, so at most six digits. */
    @Test
    void numberIsBelowOneMillion() {
        String base = dir.resolve("hero").toString();
        FileHandler fh = new FileHandler(dir.resolve("anything.txt").toString());

        for (int i = 0; i < 200; i++) {
            String name = fh.fileGetSavefile(base, "sav");
            String digits = name.substring(base.length(), name.length() - ".sav".length());
            assertTrue(Integer.parseInt(digits) < 1000000, name);
        }
    }

    /** "Resulting filename doesn't usually exist yet": the returned path is free. */
    @Test
    void returnedNameDoesNotExist() {
        String base = dir.resolve("hero").toString();
        FileHandler fh = new FileHandler(dir.resolve("anything.txt").toString());

        String name = fh.fileGetSavefile(base, "sav");

        assertFalse(Files.exists(Path.of(name)));
    }

    /**
     * Fixes the handler's generator. {@code fileGetSavefile} draws from its own {@link java.util.Random},
     * seeded from the clock on every call unless {@code debug} is true, in which case it uses the
     * {@code seed} field. Every call after this one therefore starts from the same sequence, which
     * is what forces a collision. The game's RNG plays no part, as in C.
     */
    private static void fixSeed(FileHandler fh, long seed) {
        fh.debug = true;
        fh.seed = seed;
    }

    /**
     * Forces a collision: with the seed fixed, the second call repeats the first draw, so once that
     * candidate exists the second call must retry and return a different, free name.
     */
    @Test
    void collisionRetriesWithADifferentName() throws Exception {
        String base = dir.resolve("hero").toString();
        FileHandler fh = new FileHandler(dir.resolve("anything.txt").toString());
        fixSeed(fh, 42);
        String first = fh.fileGetSavefile(base, "sav");
        Files.createFile(Path.of(first));

        String second = fh.fileGetSavefile(base, "sav");

        assertNotNull(second);
        assertNotEquals(first, second);
        assertFalse(Files.exists(Path.of(second)));
    }

    /**
     * The same collision with a relative {@code base}. C's {@code file_exists} tests the candidate
     * from the current directory, so the retry must still happen. A check made against the handler's
     * own path can never see the existing file and returns the colliding name.
     */
    @Test
    void collisionRetriesWithARelativeBase() throws Exception {
        String relativeBase = Path.of("").toAbsolutePath().relativize(dir.resolve("hero")).toString();
        FileHandler fh = new FileHandler(dir.resolve("anything.txt").toString());
        fixSeed(fh, 42);
        String first = fh.fileGetSavefile(relativeBase, "sav");
        Files.createFile(Path.of(first));

        String second = fh.fileGetSavefile(relativeBase, "sav");

        assertNotNull(second);
        assertNotEquals(first, second, "the existing file was not seen");
    }

    /**
     * C stops after 100 retries and leaves the last candidate in the buffer, so the caller gets a
     * name, never nothing. Fills every candidate for one seed (the first draw plus 100 retries) one
     * call at a time, then asks once more: the answer must be the 101st candidate, which exists.
     * A relative base is used so the final existence check is exercised too.
     */
    @Test
    void exhaustedRetriesReturnLastCandidate() throws Exception {
        String relativeBase = Path.of("").toAbsolutePath().relativize(dir.resolve("hero")).toString();
        FileHandler fh = new FileHandler(dir.resolve("anything.txt").toString());

        fixSeed(fh, 7);
        String last = null;
        for (int i = 0; i < 101; i++) {
            String name = fh.fileGetSavefile(relativeBase, "sav");
            assertNotNull(name, "call " + i + " returned null before the candidates ran out");
            assertFalse(Files.exists(Path.of(name)), "call " + i + " returned a name that exists: " + name);
            Files.createFile(Path.of(name));
            last = name;
        }

        String exhausted = fh.fileGetSavefile(relativeBase, "sav");

        assertNotNull(exhausted, "C returns the last candidate, not nothing");
        assertEquals(last, exhausted);
        assertTrue(Files.exists(Path.of(exhausted)));
    }

    /**
     * The handler's own path must play no part: the name comes from {@code base} alone, even when the
     * handler was built for an existing file in a different directory.
     */
    @Test
    void handlersOwnPathIsIrrelevant() throws Exception {
        Path other = Files.createDirectory(dir.resolve("other"));
        Path own = Files.writeString(other.resolve("own.txt"), "x");
        String base = dir.resolve("hero").toString();
        FileHandler fh = new FileHandler(own.toString());

        String name = fh.fileGetSavefile(base, "sav");

        assertNotNull(name);
        assertTrue(name.startsWith(base), "name left the base path: " + name);
    }
}
