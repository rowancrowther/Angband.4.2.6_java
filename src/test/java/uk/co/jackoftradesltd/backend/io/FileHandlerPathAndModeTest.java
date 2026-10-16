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
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests for the parts of {@link FileHandler} that the other FileHandler test classes leave open: the path-level
 * helpers {@code file_delete}, {@code file_exists} and {@code file_newer}, the mode and state queries, the edges of
 * {@code file_open} that the Javadoc records, and the byte rules of {@code file_readc}, {@code file_writec},
 * {@code file_put} and {@code file_getl}.
 * <p>
 * Expected values come from {@code z-file.c}. {@code file_delete} is {@code remove()}, which deletes a file or an
 * empty directory and fails on anything else. {@code file_exists} is {@code stat()}, so a directory exists.
 * {@code file_newer} is false when the first file is missing, true when only the second is missing, and otherwise a
 * strict "later than" on {@code st_mtime}. {@code file_writec} writes a {@code uint8_t}, so only the low eight bits of
 * the argument reach the file. {@code file_put} is {@code strlen} plus {@code file_write}. The tests that pin a
 * difference from C say so in their own comment.
 * <p>
 * Class FileHandlerPathAndModeTest coded on 261016, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
class FileHandlerPathAndModeTest {
    @TempDir
    Path dir;

    private Path write(String name, byte[] content) throws IOException {
        Path p = dir.resolve(name);
        Files.write(p, content);
        return p;
    }

    private Path write(String name, String content) throws IOException {
        Path p = dir.resolve(name);
        Files.writeString(p, content);
        return p;
    }

    // ---- file_exists ----

    /** {@code stat()} succeeds on an ordinary file. */
    @Test
    void fileExistsIsTrueForAFile() throws IOException {
        assertTrue(new FileHandler(write("a.txt", "x").toString()).fileExists());
    }

    /** {@code stat()} fails on a missing path. */
    @Test
    void fileExistsIsFalseForAMissingPath() {
        assertFalse(new FileHandler(dir.resolve("none.txt").toString()).fileExists());
    }

    /** The {@code stat()} form of {@code file_exists} does not care what kind of thing the path is. */
    @Test
    void fileExistsIsTrueForADirectory() {
        assertTrue(new FileHandler(dir.toString()).fileExists());
    }

    /** Constructing a handler looks at nothing and creates nothing, where C's {@code file_open} opens at once. */
    @Test
    void constructorTouchesNothingOnDisk() {
        Path p = dir.resolve("never.txt");

        FileHandler fh = new FileHandler(p.toString());

        assertFalse(fh.isOpen());
        assertFalse(Files.exists(p));
    }

    // ---- file_delete ----

    /** {@code remove()} of an existing file succeeds and the file is gone. */
    @Test
    void deleteRemovesAFile() throws IOException {
        Path p = write("a.txt", "x");

        assertTrue(new FileHandler(p.toString()).delete());
        assertFalse(Files.exists(p));
    }

    /** {@code remove()} of a missing path fails. */
    @Test
    void deleteOfAMissingFileIsFalse() {
        assertFalse(new FileHandler(dir.resolve("none.txt").toString()).delete());
    }

    /** {@code remove()} removes an empty directory, as it does a file. */
    @Test
    void deleteRemovesAnEmptyDirectory() throws IOException {
        Path d = Files.createDirectory(dir.resolve("empty"));

        assertTrue(new FileHandler(d.toString()).delete());
        assertFalse(Files.exists(d));
    }

    /** {@code remove()} cannot remove a directory that has something in it. */
    @Test
    void deleteOfANonEmptyDirectoryIsFalse() throws IOException {
        Path d = Files.createDirectory(dir.resolve("full"));
        Files.writeString(d.resolve("inside.txt"), "x");

        assertFalse(new FileHandler(d.toString()).delete());
        assertTrue(Files.exists(d));
    }

    // ---- file_newer ----

    private static void setMtime(Path p, long millis) throws IOException {
        Files.setLastModifiedTime(p, FileTime.fromMillis(millis));
    }

    /** C: "If the first doesn't exist, the first is not newer", whatever the second is. */
    @Test
    void fileNewerIsFalseWhenTheFirstIsMissing() throws IOException {
        Path second = write("second.txt", "x");
        FileHandler fh = new FileHandler(second.toString());

        assertFalse(fh.fileNewer(dir.resolve("none.txt").toString(), second.toString()));
    }

    /** C: "If the second doesn't exist, the first is always newer." */
    @Test
    void fileNewerIsTrueWhenOnlyTheSecondIsMissing() throws IOException {
        Path first = write("first.txt", "x");
        FileHandler fh = new FileHandler(first.toString());

        assertTrue(fh.fileNewer(first.toString(), dir.resolve("none.txt").toString()));
    }

    /** The first-missing test comes first, so two missing files give false and not true. */
    @Test
    void fileNewerIsFalseWhenBothAreMissing() {
        FileHandler fh = new FileHandler(dir.resolve("any.txt").toString());

        assertFalse(fh.fileNewer(dir.resolve("a.txt").toString(), dir.resolve("b.txt").toString()));
    }

    /** A later modification time on the first file is newer: {@code st_mtime} first {@code >} second. */
    @Test
    void fileNewerIsTrueWhenTheFirstIsLater() throws IOException {
        Path first = write("first.txt", "x");
        Path second = write("second.txt", "x");
        setMtime(first, 1_700_000_100_000L);
        setMtime(second, 1_700_000_000_000L);

        assertTrue(new FileHandler(first.toString()).fileNewer(first.toString(), second.toString()));
    }

    /** An earlier modification time is not newer. */
    @Test
    void fileNewerIsFalseWhenTheFirstIsEarlier() throws IOException {
        Path first = write("first.txt", "x");
        Path second = write("second.txt", "x");
        setMtime(first, 1_700_000_000_000L);
        setMtime(second, 1_700_000_100_000L);

        assertFalse(new FileHandler(first.toString()).fileNewer(first.toString(), second.toString()));
    }

    /** The test is strict, so equal times are not newer. */
    @Test
    void fileNewerIsFalseWhenTheTimesAreEqual() throws IOException {
        Path first = write("first.txt", "x");
        Path second = write("second.txt", "x");
        setMtime(first, 1_700_000_000_000L);
        setMtime(second, 1_700_000_000_000L);

        assertFalse(new FileHandler(first.toString()).fileNewer(first.toString(), second.toString()));
    }

    /** A file is not newer than itself. */
    @Test
    void fileNewerOfTheSameFileIsFalse() throws IOException {
        Path p = write("a.txt", "x");

        assertFalse(new FileHandler(p.toString()).fileNewer(p.toString(), p.toString()));
    }

    /**
     * Pins the accepted difference from C. Two times 300 ms apart in the same second are equal to C's whole-second
     * {@code st_mtime}, so C answers false; Java compares at full precision and answers true.
     */
    @Test
    void fileNewerSeesAGapInsideOneSecondUnlikeC() throws IOException {
        Path first = write("first.txt", "x");
        Path second = write("second.txt", "x");
        setMtime(first, 1_700_000_000_300L);
        setMtime(second, 1_700_000_000_000L);

        assertTrue(new FileHandler(first.toString()).fileNewer(first.toString(), second.toString()));
    }

    // ---- getFileMode, isOpen and the state they report ----

    @Test
    void closedHandlerHasNoMode() throws IOException {
        FileHandler fh = new FileHandler(write("a.txt", "x").toString());

        assertFalse(fh.isOpen());
        assertNull(fh.getFileMode());
    }

    @Test
    void getFileModeReportsReadWriteAndAppend() throws IOException {
        Path p = write("a.txt", "x");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertEquals(FileModeEnum.MODE_READ, fh.getFileMode());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertEquals(FileModeEnum.MODE_WRITE, fh.getFileMode());
        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));
        assertEquals(FileModeEnum.MODE_APPEND, fh.getFileMode());
        assertTrue(fh.isOpen());
        assertTrue(fh.close());
        assertFalse(fh.isOpen());
        assertNull(fh.getFileMode());
    }

    /** Opening the read side clears the append flag, so a later write open is not reported as append. */
    @Test
    void openingTheReadSideAfterAppendReportsRead() throws IOException {
        FileHandler fh = new FileHandler(write("a.txt", "x").toString());
        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertEquals(FileModeEnum.MODE_READ, fh.getFileMode());
        assertFalse(fh.appendFile);
    }

    /**
     * Pins the Javadoc of {@code getFileMode}: a write side opened lazily appends to the file but reports
     * {@code MODE_WRITE}, since only {@code open} sets the append flag. C has no lazy open to compare with.
     */
    @Test
    void lazyWriteSideReportsWriteNotAppend() throws IOException {
        Path p = write("a.txt", "ab");
        FileHandler fh = new FileHandler(p.toString());

        assertTrue(fh.writeCharacter('c'));

        assertEquals(FileModeEnum.MODE_WRITE, fh.getFileMode());
        assertTrue(fh.close());
        assertEquals("abc", Files.readString(p));
    }

    // ---- file_open edges recorded in the Javadoc ----

    /**
     * Pins a difference from C that the Javadoc records. Linux {@code fopen(dir, "rb")} succeeds, and only a read
     * fails; Java refuses the open.
     */
    @Test
    void openReadOnADirectoryFails() {
        FileHandler fh = new FileHandler(dir.toString());

        assertFalse(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertFalse(fh.isOpen());
    }

    /**
     * Pins a difference from C that the Javadoc records. {@code "a+"} lets C read as well as append; a Java handler
     * opened for append is write-only, so a read finds nothing.
     */
    @Test
    void appendHandlerCannotRead() throws IOException {
        FileHandler fh = new FileHandler(write("a.txt", "abc\n").toString());
        assertTrue(fh.open(FileModeEnum.MODE_APPEND, FileTypeEnum.FTYPE_TEXT));

        assertEquals(-1, fh.readCharacter());
        assertNull(fh.getLine(1024));
        assertNull(fh.read(3));
    }

    /** A failed read open leaves the write side as it was, as the Javadoc of {@code openInputStream} says. */
    @Test
    void failedReadOpenLeavesTheWriteSideOpen() throws IOException {
        Path p = dir.resolve("a.txt");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        Files.delete(p);

        assertFalse(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.isOpen());
        assertEquals(FileModeEnum.MODE_WRITE, fh.getFileMode());
    }

    /**
     * A failed write open leaves the read side as it was, as the Javadoc of {@code openOutputStream} says. The
     * {@code FTYPE_SAVE} write fails because the file exists, which is C's {@code O_EXCL}.
     */
    @Test
    void failedWriteOpenLeavesTheReadSideOpen() throws IOException {
        FileHandler fh = new FileHandler(write("a.sav", "abc").toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_SAVE));

        assertFalse(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));

        assertEquals(FileModeEnum.MODE_READ, fh.getFileMode());
        assertEquals('a', fh.readCharacter());
    }

    /** A second read open closes the first and starts again at the start of the file, as a new {@code fopen} does. */
    @Test
    void secondReadOpenStartsAgainAtTheBeginning() throws IOException {
        FileHandler fh = new FileHandler(write("a.txt", "abc").toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertEquals('a', fh.readCharacter());
        assertEquals('b', fh.readCharacter());

        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertEquals('a', fh.readCharacter());
    }

    /**
     * The write methods refuse while the read side is open and leave the file as it was.
     */
    @Test
    void writeMethodsRefuseWhileTheReadSideIsOpen() throws IOException {
        Path p = write("a.txt", "abc");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertFalse(fh.writeCharacter('x'));
        assertFalse(fh.write(new byte[]{'y'}));
        assertFalse(fh.putLine("z"));

        assertTrue(fh.close());
        assertEquals("abc", Files.readString(p));
    }

    /**
     * A write method that opens the file lazily after a {@code FTYPE_SAVE} open makes the new file owner-only, as a
     * savefile is. C has no lazy open; the permissions are those of the {@code FTYPE_SAVE} branch of
     * {@code file_open}.
     */
    @Test
    void lazyWriteAfterASaveOpenCreatesAnOwnerOnlyFile() throws IOException {
        assumeTrue(dir.getFileSystem().supportedFileAttributeViews().contains("posix"));
        Path p = dir.resolve("hero.sav");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_SAVE));
        assertTrue(fh.close());
        Files.delete(p);

        assertTrue(fh.writeCharacter('x'));
        assertTrue(fh.close());

        assertEquals(Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
                Files.getPosixFilePermissions(p));
    }

    /**
     * C's {@code rename} touches no open handle, so a failed move leaves a read handler open, on its old path, and
     * still reading from where it was.
     */
    @Test
    void failedMoveLeavesAnOpenReadHandlerOpenOnItsOldPath() throws IOException {
        Path p = write("a.txt", "abc");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertEquals('a', fh.readCharacter());

        assertFalse(fh.fileMove(dir.resolve("no-such-dir").resolve("b.txt")));

        assertTrue(fh.isOpen());
        assertEquals(FileModeEnum.MODE_READ, fh.getFileMode());
        assertTrue(fh.fileExists());
        assertEquals('b', fh.readCharacter());
    }

    /** The same for a write handler: after a failed move it can still write to the file under its old name. */
    @Test
    void failedMoveLeavesAnOpenWriteHandlerWritable() throws IOException {
        Path p = dir.resolve("a.txt");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));
        assertTrue(fh.putLine("one\n"));

        assertFalse(fh.fileMove(dir.resolve("no-such-dir").resolve("b.txt")));

        assertTrue(fh.isOpen());
        assertTrue(fh.putLine("two\n"));
        assertTrue(fh.close());
        assertEquals("one\ntwo\n", Files.readString(p));
    }

    /** A missing source fails and, as in C, changes nothing: the handler stays closed on its path. */
    @Test
    void failedMoveOfAMissingSourceLeavesAClosedHandlerClosed() {
        Path p = dir.resolve("none.txt");
        FileHandler fh = new FileHandler(p.toString());

        assertFalse(fh.fileMove(dir.resolve("b.txt")));

        assertFalse(fh.isOpen());
        assertFalse(Files.exists(dir.resolve("b.txt")));
    }

    // ---- file_skip ----

    /** {@code fseek} works on any handle; the Java skip works on the read side only, so a closed handler is false. */
    @Test
    void skipOnAClosedHandlerIsFalse() throws IOException {
        assertFalse(new FileHandler(write("a.txt", "abc").toString()).skipBytes(1));
    }

    /** Only the read side can be skipped, so a write handler is false. */
    @Test
    void skipOnAWriteHandlerIsFalse() {
        FileHandler fh = new FileHandler(dir.resolve("a.txt").toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));

        assertFalse(fh.skipBytes(1));
    }

    /** The accepted refusal: a negative count is false and the position does not move. */
    @Test
    void negativeSkipIsFalseAndDoesNotMove() throws IOException {
        FileHandler fh = new FileHandler(write("a.txt", "abc").toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        assertEquals('a', fh.readCharacter());

        assertFalse(fh.skipBytes(-1));

        assertEquals('b', fh.readCharacter());
    }

    // ---- file_readc ----

    /** {@code file_readc} stores the byte in a {@code uint8_t}, so 0x80 to 0xFF come back as 128 to 255, never negative. */
    @Test
    void readCharacterReturnsBytesAsZeroToTwoFiftyFive() throws IOException {
        FileHandler fh = new FileHandler(write("a.bin", new byte[]{0, (byte) 0x80, (byte) 0xFF}).toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_RAW));

        assertEquals(0, fh.readCharacter());
        assertEquals(128, fh.readCharacter());
        assertEquals(255, fh.readCharacter());
        assertEquals(-1, fh.readCharacter());
        assertEquals(-1, fh.readCharacter());
    }

    @Test
    void readCharacterOnAClosedHandlerIsMinusOne() throws IOException {
        assertEquals(-1, new FileHandler(write("a.txt", "abc").toString()).readCharacter());
    }

    // ---- file_writec ----

    /** {@code file_writec} takes a {@code uint8_t}: 0x141 reaches the file as 0x41 and -1 as 0xFF. */
    @Test
    void writeCharacterWritesOnlyTheLowEightBits() throws IOException {
        Path p = dir.resolve("a.bin");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_RAW));

        assertTrue(fh.writeCharacter(0x141));
        assertTrue(fh.writeCharacter(-1));
        assertTrue(fh.writeCharacter(0));
        assertTrue(fh.close());

        assertArrayEquals(new byte[]{0x41, (byte) 0xFF, 0}, Files.readAllBytes(p));
    }

    // ---- file_read ----

    /** A zero-byte read is an empty array, not an error: {@code fread} of 0 returns 0. */
    @Test
    void readOfZeroBytesIsEmpty() throws IOException {
        FileHandler fh = new FileHandler(write("a.txt", "abc").toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_RAW));

        assertArrayEquals(new byte[0], fh.read(0));
        assertEquals('a', fh.readCharacter());
    }

    /**
     * Pins an edge the Javadoc records. C's size is a {@code size_t}; a negative Java size is refused with an
     * {@link IllegalArgumentException}.
     */
    @Test
    void readOfANegativeSizeThrows() throws IOException {
        FileHandler fh = new FileHandler(write("a.txt", "abc").toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_RAW));

        assertThrows(IllegalArgumentException.class, () -> fh.read(-1));
    }

    // ---- NUL handling, recorded as differences from C ----

    private byte[] putLineAndRead(String text) throws IOException {
        Path p = dir.resolve("nul.txt");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.putLine(text));
        assertTrue(fh.close());

        return Files.readAllBytes(p);
    }

    /** {@code file_put} is {@code file_write(f, buf, strlen(buf))}, so the text stops before the first NUL. */
    @Test
    void putLineStopsBeforeAnEmbeddedNul() throws IOException {
        assertArrayEquals(new byte[]{'a'}, putLineAndRead("a\0b"));
    }

    /** Several characters before the NUL all reach the file, and nothing after it does. */
    @Test
    void putLineKeepsEverythingBeforeTheNul() throws IOException {
        assertArrayEquals("hello".getBytes(), putLineAndRead("hello\0world\n"));
    }

    /** A NUL as the last character: {@code strlen} is the length of the text before it, so all of that is written. */
    @Test
    void putLineWithATrailingNulWritesTheRest() throws IOException {
        assertArrayEquals("ab".getBytes(), putLineAndRead("ab\0"));
    }

    /** A NUL first: {@code strlen} is 0, {@code fwrite} of 0 bytes succeeds, and the file is empty. */
    @Test
    void putLineBeginningWithANulWritesNothingAndSucceeds() throws IOException {
        assertArrayEquals(new byte[0], putLineAndRead("\0abc"));
    }

    /** A cut line does not disturb the next call: the following line is written whole. */
    @Test
    void putLineAfterACutLineWritesTheNextLineWhole() throws IOException {
        Path p = dir.resolve("two.txt");
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_WRITE, FileTypeEnum.FTYPE_TEXT));

        assertTrue(fh.putLine("one\0x\n"));
        assertTrue(fh.putLine("two\n"));
        assertTrue(fh.close());

        assertArrayEquals("onetwo\n".getBytes(), Files.readAllBytes(p));
    }

    /** Pins a difference from C. A C line ends at a NUL, since its buffer is a string; here the NUL stays in the line. */
    @Test
    void getLineKeepsAnEmbeddedNul() throws IOException {
        FileHandler fh = new FileHandler(write("a.bin", new byte[]{'a', 0, 'b', '\n', 'c', '\n'}).toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));

        assertEquals("a\0b", fh.getLine(1024));
        assertEquals("c", fh.getLine(1024));
    }

    // ---- file_get_savefile retry counts ----

    /**
     * C's retry name is {@code "%s%lu%u.%s"} with the count after the random number, and the count runs 1 to 100:
     * {@code while (file_exists(filename) && (count++ < 100))} adds one before the body runs. Filling every candidate
     * for one fixed seed in turn, the first call has no count and call {@code i} has count {@code i} in front of the
     * extension, up to 100. The numbers themselves are Java's own generator and are not compared.
     */
    @Test
    void retryCountsRunFromOneToOneHundred() throws IOException {
        String relativeBase = Path.of("").toAbsolutePath().relativize(dir.resolve("hero")).toString();
        FileHandler fh = new FileHandler(dir.resolve("anything.txt").toString());
        fh.debug = true;
        fh.seed = 7;

        for (int i = 0; i <= 100; i++) {
            String name = fh.fileGetSavefile(relativeBase, "sav");
            if (i == 0) {
                assertTrue(name.matches(java.util.regex.Pattern.quote(relativeBase) + "\\d{1,6}\\.sav"), name);
            } else {
                assertTrue(name.startsWith(relativeBase), name);
                assertTrue(name.endsWith(i + ".sav"), "call " + i + " gave " + name);
            }
            assertFalse(Files.exists(Path.of(name)), "call " + i + " gave an existing name: " + name);
            Files.createFile(Path.of(name));
        }
    }
}
