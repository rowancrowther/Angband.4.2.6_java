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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AngFile#getLine()} against {@code file_getl} in {@code z-file.c}, called
 * with the 1024-byte buffer its callers pass.
 * <p>
 * Expected values were traced by hand through the C loop: {@code \r\n}, {@code \n} and a lone
 * {@code \r} end a line; a tab becomes spaces up to the next multiple of {@code TAB_COLUMNS} (4),
 * and a tab whose stop reaches {@code len} ends the line at once; a line stops after {@code
 * len - 1} = 1023 bytes without reading the byte after it; end of file with nothing read is false.
 * <p>
 * Class AngFileGetLineTest coded on 261016, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
class AngFileGetLineTest {

    private static AngFile reading(Path root, String name, byte[] bytes) throws IOException {
        Path path = root.resolve(name);
        Files.write(path, bytes);
        return new AngFile(path.toString(), FileModeEnum.MODE_READ);
    }

    private static AngFile reading(Path root, String content) throws IOException {
        return reading(root, "lines.txt", content.getBytes(StandardCharsets.UTF_8));
    }

    private static List<String> allLines(AngFile file) {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = file.getLine()) != null) {
            lines.add(line);
            if (lines.size() > 100) throw new AssertionError("getLine never reached the end of the file");
        }
        return lines;
    }

    private static String x(int count) {
        return "x".repeat(count);
    }

    // ---- line endings ----

    /** {@code \r\n} ends one line; a lone {@code \r} ends a line and the next byte is not lost. */
    @Test
    void crLfLfAndLoneCrAllEndALine(@TempDir Path root) throws IOException {
        assertEquals(List.of("a", "b", "c", "d"), allLines(reading(root, "a\r\nb\rc\nd")));
    }

    @Test
    void blankLinesComeBackEmpty(@TempDir Path root) throws IOException {
        assertEquals(List.of("a", "", "b"), allLines(reading(root, "a\n\nb\n")));
    }

    /** {@code seen_cr} stays set across a second {@code \r}, so {@code \r\r\n} is a single ending. */
    @Test
    void aDoubledCrBeforeLfIsOneLineEnding(@TempDir Path root) throws IOException {
        assertEquals(List.of("a", "b"), allLines(reading(root, "a\r\r\nb")));
    }

    @Test
    void aLoneCrAtTheEndOfTheFileEndsTheLastLine(@TempDir Path root) throws IOException {
        assertEquals(List.of("a"), allLines(reading(root, "a\r")));
    }

    /** A file of only {@code \r}: nothing is stored, so end of file finds {@code i == 0} and C returns false. */
    @Test
    void aFileOfOnlyACrHasNoLines(@TempDir Path root) throws IOException {
        assertEquals(List.of(), allLines(reading(root, "\r")));
    }

    @Test
    void anEmptyFileHasNoLines(@TempDir Path root) throws IOException {
        assertNull(reading(root, "").getLine());
    }

    @Test
    void aFinalLineWithNoEndingIsStillReturned(@TempDir Path root) throws IOException {
        assertEquals(List.of("a", "b"), allLines(reading(root, "a\nb")));
    }

    // ---- tabs ----

    /** Index 1: the next stop is (1 + 4) / 4 * 4 = 4, so three spaces. */
    @Test
    void aTabFillsToTheNextMultipleOfFour(@TempDir Path root) throws IOException {
        assertEquals(List.of("a   b", "    x", "abcd    e"),
                allLines(reading(root, "a\tb\n\tx\nabcd\te\n")));
    }

    /** Index 1020 gives a stop of 1024, which is not below {@code len}, so the line ends there. */
    @Test
    void aTabWhoseStopReachesTheBufferEndsTheLineAndIsConsumed(@TempDir Path root)
            throws IOException {
        assertEquals(List.of(x(1020), "y"), allLines(reading(root, x(1020) + "\ty\n")));
    }

    /** Index 1019 gives a stop of 1020, which fits, so one space is stored and the line goes on. */
    @Test
    void aTabThatStillFitsIsExpandedAndTheLineContinues(@TempDir Path root) throws IOException {
        assertEquals(List.of(x(1019) + " y"), allLines(reading(root, x(1019) + "\ty\n")));
    }

    // ---- the 1023-byte limit ----

    @Test
    void aLongLineIsCutAt1023BytesAndTheRestComesBackNext(@TempDir Path root) throws IOException {
        assertEquals(List.of(x(1023), x(7)), allLines(reading(root, x(1030) + "\n")));
    }

    /** The loop stops at 1023 bytes without reading the newline, so the newline gives an empty line. */
    @Test
    void aLineOfExactly1023BytesIsFollowedByAnEmptyLine(@TempDir Path root) throws IOException {
        assertEquals(List.of(x(1023), ""), allLines(reading(root, x(1023) + "\n")));
    }

    @Test
    void aLineOf1022BytesIsNotFollowedByAnEmptyLine(@TempDir Path root) throws IOException {
        assertEquals(List.of(x(1022)), allLines(reading(root, x(1022) + "\n")));
    }

    // ---- refusals and mixing ----

    @Test
    void getLineIsNullForAMissingFileAndCreatesNothing(@TempDir Path root) {
        Path path = root.resolve("missing.txt");

        assertNull(new AngFile(path.toString(), FileModeEnum.MODE_READ).getLine());
        assertFalse(Files.exists(path));
    }

    @Test
    void getLineIsNullWhenTheFileWasCreatedForWriting(@TempDir Path root) throws IOException {
        Path path = root.resolve("write-only.txt");
        Files.writeString(path, "keep\n");

        assertNull(new AngFile(path.toString(), FileModeEnum.MODE_WRITE).getLine());
        assertEquals("keep\n", Files.readString(path), "the refusal must not truncate the file");
    }

    /** {@code file_getl} is built on {@code file_readc}, so the two can be mixed on one handle. */
    @Test
    void readCharContinuesAfterTheLineGetLineReturned(@TempDir Path root) throws IOException {
        AngFile file = reading(root, "ab\ncd\n");

        assertEquals("ab", file.getLine());
        assertEquals(Optional.of((int) 'c'), file.readChar());
        assertEquals("d", file.getLine());
    }

    /** A character after a lone {@code \r} is pushed back with {@code fseek(-1)}, so it is still there. */
    @Test
    void theByteAfterALoneCrIsStillReadable(@TempDir Path root) throws IOException {
        AngFile file = reading(root, "a\rb");

        assertEquals("a", file.getLine());
        assertEquals(Optional.of((int) 'b'), file.readChar());
    }

    @Test
    void nonAsciiTextIsDecodedAsUtf8(@TempDir Path root) throws IOException {
        assertEquals(List.of("naïve café", "日本"), allLines(reading(root, "naïve café\n日本\n")));
    }
}
