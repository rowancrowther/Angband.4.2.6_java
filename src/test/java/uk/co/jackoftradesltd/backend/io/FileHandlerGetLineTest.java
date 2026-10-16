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
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.enums.FileModeEnum;
import uk.co.jackoftradesltd.backend.io.enums.FileTypeEnum;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FileHandler#getLine(int)}, the port of C's {@code file_getl}
 * ({@code src/z-file.c}), and for the way it shares one file position with
 * {@link FileHandler#readCharacter()}.
 * <p>
 * Expected values are worked out from C's {@code file_getl}, which reads one byte at a time with
 * {@code file_readc}: {@code \r} is swallowed and any following byte other than {@code \n} ends the
 * line and is put back; {@code \n} ends the line; a tab becomes spaces up to the next multiple of
 * {@code TAB_COLUMNS} (4); the line stops after {@code len - 1} bytes whatever comes next; end of
 * file with nothing read returns false, which is {@code null} here.
 * <p>
 * The tests under "UTF-8 cut-off" pin the accepted divergence in {@code Accepted_Deviations.md}
 * (a cut multi-byte character is pushed back instead of split), so they do not match C.
 * <p>
 * Every call in the line and cut-off tests uses a {@code len} of at least 5, because
 * {@code getLine} throws {@link IllegalArgumentException} below that (a buffer under 5 bytes cannot
 * hold the longest UTF-8 character); the tests under "len below 5 is refused" pin that rule.
 * <p>
 * Class FileHandlerGetLineTest coded on 261016, updated on 261016 to use a {@code len} of at least
 * 5 and to pin the refusal of smaller ones, commented in full on 261016.
 *
 * @author Rowan Crowther
 */
@Timeout(10)
class FileHandlerGetLineTest {
    @TempDir
    Path dir;

    private FileHandler openForRead(byte[] content) throws IOException {
        Path p = dir.resolve("data.txt");
        Files.write(p, content);
        FileHandler fh = new FileHandler(p.toString());
        assertTrue(fh.open(FileModeEnum.MODE_READ, FileTypeEnum.FTYPE_TEXT));
        return fh;
    }

    private FileHandler openForRead(String content) throws IOException {
        return openForRead(content.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] bytes(int... values) {
        byte[] b = new byte[values.length];
        for (int i = 0; i < values.length; i++) b[i] = (byte) values[i];
        return b;
    }

    // ---- line endings -------------------------------------------------------------------------

    @Test
    void newlineLines() throws IOException {
        FileHandler fh = openForRead("abc\ndef\n");

        assertEquals("abc", fh.getLine(1024));
        assertEquals("def", fh.getLine(1024));
        assertNull(fh.getLine(1024));
    }

    @Test
    void crLfLines() throws IOException {
        FileHandler fh = openForRead("abc\r\ndef\r\n");

        assertEquals("abc", fh.getLine(1024));
        assertEquals("def", fh.getLine(1024));
        assertNull(fh.getLine(1024));
    }

    @Test
    void lineEndingBareCr() throws IOException {
        FileHandler fh = openForRead("a\rb");

        assertEquals("a", fh.getLine(1024));
        assertEquals("b", fh.getLine(1024));
        assertNull(fh.getLine(1024));
    }

    @Test
    void doubleCrBeforeLfIsSwallowed() throws IOException {
        FileHandler fh = openForRead("a\r\r\nb");

        assertEquals("a", fh.getLine(1024));
        assertEquals("b", fh.getLine(1024));
    }

    @Test
    void loneCrAtEndOfFileReturnsNull() throws IOException {
        FileHandler fh = openForRead("\r");

        assertNull(fh.getLine(1024));
    }

    @Test
    void blankLinesComeBackEmpty() throws IOException {
        FileHandler fh = openForRead("\n\nx\n");

        assertEquals("", fh.getLine(1024));
        assertEquals("", fh.getLine(1024));
        assertEquals("x", fh.getLine(1024));
        assertNull(fh.getLine(1024));
    }

    @Test
    void lastLineWithoutNewlineIsReturned() throws IOException {
        FileHandler fh = openForRead("abc\ndef");

        assertEquals("abc", fh.getLine(1024));
        assertEquals("def", fh.getLine(1024));
        assertNull(fh.getLine(1024));
    }

    @Test
    void emptyFileReturnsNull() throws IOException {
        FileHandler fh = openForRead(new byte[0]);

        assertNull(fh.getLine(1024));
    }

    // ---- tabs ---------------------------------------------------------------------------------

    @Test
    void tabAtStartExpandsToFourSpaces() throws IOException {
        FileHandler fh = openForRead("\tx\n");

        assertEquals("    x", fh.getLine(1024));
    }

    @Test
    void tabMovesToNextMultipleOfFour() throws IOException {
        FileHandler fh = openForRead("ab\tc\nabcd\te\nabc\tf\n");

        assertEquals("ab  c", fh.getLine(1024));
        assertEquals("abcd    e", fh.getLine(1024));
        assertEquals("abc f", fh.getLine(1024));
    }

    @Test
    void twoTabsInARow() throws IOException {
        FileHandler fh = openForRead("\t\tx\n");

        assertEquals("        x", fh.getLine(1024));
    }

    // ---- the len - 1 limit --------------------------------------------------------------------

    @Test
    void lineIsCutAtLenMinusOneAndTheRestComesNext() throws IOException {
        FileHandler fh = openForRead("abcdefgh\n");

        assertEquals("abcd", fh.getLine(5));
        assertEquals("efgh", fh.getLine(5));
        assertEquals("", fh.getLine(5));
        assertNull(fh.getLine(5));
    }

    @Test
    void lineOfExactlyLenMinusOneLeavesAnEmptyLineForTheNewline() throws IOException {
        FileHandler fh = openForRead("abcd\nxy\n");

        assertEquals("abcd", fh.getLine(5));
        assertEquals("", fh.getLine(5));
        assertEquals("xy", fh.getLine(5));
    }

    @Test
    void tabThatDoesNotFitEndsTheLineAndIsConsumed() throws IOException {
        // len 6: after "abcd" the next tab stop is 8, which is >= 6, so C breaks out of the loop.
        // The tab has already been read, so it is gone and the next line starts at "x".
        FileHandler fh = openForRead("abcd\tx\n");

        assertEquals("abcd", fh.getLine(6));
        assertEquals("x", fh.getLine(6));
    }

    @Test
    void tabFillingTheBufferExactlyLeavesTheRestForTheNextCall() throws IOException {
        // len 5: the tab stop from column 0 is 4, which is < 5, so it expands. That fills the four
        // bytes of buffer, the loop ends, and "x" is not read until the next call.
        FileHandler fh = openForRead("\tx\n");

        assertEquals("    ", fh.getLine(5));
        assertEquals("x", fh.getLine(5));
    }

    // ---- sharing one file position ------------------------------------------------------------

    @Test
    void readCharacterAfterGetLineContinuesFromTheEndOfThatLine() throws IOException {
        FileHandler fh = openForRead("abc\nXYZ\n");

        assertEquals("abc", fh.getLine(1024));
        assertEquals('X', fh.readCharacter());
        assertEquals("YZ", fh.getLine(1024));
    }

    @Test
    void skipBytesAfterGetLineContinuesFromTheEndOfThatLine() throws IOException {
        FileHandler fh = openForRead("abc\nXYZ\n");

        assertEquals("abc", fh.getLine(1024));
        assertTrue(fh.skipBytes(2));
        assertEquals("Z", fh.getLine(1024));
    }

    @Test
    void readAfterGetLineContinuesFromTheEndOfThatLine() throws IOException {
        FileHandler fh = openForRead("abc\nXYZ\n");

        assertEquals("abc", fh.getLine(1024));
        assertEquals("XYZ", new String(fh.read(3), StandardCharsets.ISO_8859_1));
    }

    @Test
    void getLineAfterReadCharacterContinuesFromThere() throws IOException {
        FileHandler fh = openForRead("QRS\nTUV\n");

        assertEquals('Q', fh.readCharacter());
        assertEquals("RS", fh.getLine(1024));
        assertEquals("TUV", fh.getLine(1024));
    }

    @Test
    void crPutBackIsSeenByReadCharacter() throws IOException {
        // "a\rb": getLine stops at the 'b' after the CR and puts it back.
        FileHandler fh = openForRead("a\rb");

        assertEquals("a", fh.getLine(1024));
        assertEquals('b', fh.readCharacter());
        assertEquals(-1, fh.readCharacter());
    }

    // ---- UTF-8 ------------------------------------------------------------------------------

    @Test
    void multiByteCharacterIsDecoded() throws IOException {
        FileHandler fh = openForRead("Drúedain\n");

        assertEquals("Drúedain", fh.getLine(1024));
    }

    // ---- UTF-8 cut-off (accepted divergence from C) ----------------------------------------

    @Test
    void twoByteCharacterCutByTheLimitIsPushedBack() throws IOException {
        // 61 62 63 c3 ba 0a, len 5: the buffer would hold 61 62 63 c3, half of u-acute.
        FileHandler fh = openForRead(bytes(0x61, 0x62, 0x63, 0xc3, 0xba, 0x0a));

        assertEquals("abc", fh.getLine(5));
        assertEquals("ú", fh.getLine(5));
        assertNull(fh.getLine(5));
    }

    @Test
    void threeByteCharacterCutAfterTwoBytesIsPushedBack() throws IOException {
        // 61 62 e2 82 ac (euro sign), len 5: the buffer would hold 61 62 e2 82.
        FileHandler fh = openForRead(bytes(0x61, 0x62, 0xe2, 0x82, 0xac));

        assertEquals("ab", fh.getLine(5));
        assertEquals("€", fh.getLine(5));
        assertNull(fh.getLine(5));
    }

    @Test
    void completeTwoByteCharacterAtTheLimitIsKept() throws IOException {
        // 61 62 c3 ba, len 5: the buffer holds exactly 61 62 c3 ba.
        FileHandler fh = openForRead(bytes(0x61, 0x62, 0xc3, 0xba, 0x0a));

        assertEquals("abú", fh.getLine(5));
    }

    @Test
    void completeFourByteCharacterAtTheLimitIsKept() throws IOException {
        // f0 9f 98 80 is U+1F600, len 5: the buffer holds all four bytes.
        FileHandler fh = openForRead(bytes(0xf0, 0x9f, 0x98, 0x80, 0x0a));

        assertEquals("😀", fh.getLine(5));
    }

    @Test
    void fourByteCharacterCutAfterThreeBytesIsPushedBack() throws IOException {
        // 61 f0 9f 98 80, len 5: the buffer would hold 61 f0 9f 98.
        FileHandler fh = openForRead(bytes(0x61, 0xf0, 0x9f, 0x98, 0x80));

        assertEquals("a", fh.getLine(5));
        assertEquals("😀", fh.getLine(5));
    }

    @Test
    void asciiAtTheLimitIsNotTouched() throws IOException {
        FileHandler fh = openForRead("abcdef\n");

        assertEquals("abcd", fh.getLine(5));
        assertEquals("ef", fh.getLine(5));
    }

    @Test
    void consecutiveFourByteCharactersAtTheSmallestLenAlwaysAdvance() throws IOException {
        // Two U+1F600 back to back, len 5 (the smallest allowed): each call must take one whole
        // character, so the pair cannot loop on an empty line.
        FileHandler fh = openForRead("😀😀\n");

        assertEquals("😀", fh.getLine(5));
        assertEquals("😀", fh.getLine(5));
        assertEquals("", fh.getLine(5));
        assertNull(fh.getLine(5));
    }

    @Test
    void cutCharacterAtEndOfFileIsNotPushedBack() throws IOException {
        // The file simply ends mid-character: that is bad data, not a limit cut-off.
        FileHandler fh = openForRead(bytes(0x61, 0xc3));

        assertEquals("a�", fh.getLine(1024));
        assertNull(fh.getLine(1024));
    }

    // ---- len below 5 is refused (deliberate divergence from C) -------------------------------

    /**
     * C accepts any {@code len}, but a buffer under 5 bytes cannot hold a 4-byte UTF-8 character,
     * so the cut-off pushback could return an empty line without consuming anything. Java refuses
     * those sizes instead. The check comes before the open-stream test, so it does not depend on
     * the state of the handler.
     */
    @Test
    void lenBelowFiveThrows() throws IOException {
        FileHandler fh = openForRead("abc\n");

        assertThrows(IllegalArgumentException.class, () -> fh.getLine(4));
        assertThrows(IllegalArgumentException.class, () -> fh.getLine(1));
        assertThrows(IllegalArgumentException.class, () -> fh.getLine(0));
        assertThrows(IllegalArgumentException.class, () -> fh.getLine(-1));
    }

    @Test
    void refusedLenDoesNotConsumeAnything() throws IOException {
        FileHandler fh = openForRead("abc\n");

        assertThrows(IllegalArgumentException.class, () -> fh.getLine(4));
        assertEquals("abc", fh.getLine(1024));
    }

    @Test
    void lenBelowFiveThrowsOnAHandlerThatIsNotOpen() {
        FileHandler fh = new FileHandler(dir.resolve("never-opened.txt").toString());

        assertThrows(IllegalArgumentException.class, () -> fh.getLine(4));
        assertThrows(IllegalArgumentException.class, () -> fh.getLine(0));
    }

    @Test
    void lenOfFiveIsAccepted() throws IOException {
        FileHandler fh = openForRead("abcd\n");

        assertEquals("abcd", fh.getLine(5));
    }

    @Test
    void validLenOnAHandlerThatIsNotOpenReturnsNull() {
        FileHandler fh = new FileHandler(dir.resolve("never-opened.txt").toString());

        assertNull(fh.getLine(5));
    }

    // ---- edge cases at the limit ------------------------------------------------------------

    @Test
    void bufferOfOnlyContinuationBytesIsReturnedWithoutThrowing() throws IOException {
        // 80 80 80 80 is four stray continuation bytes; there is no lead byte to walk back to.
        FileHandler fh = openForRead(bytes(0x80, 0x80, 0x80, 0x80, 0x0a));

        assertEquals("����", fh.getLine(5));
        assertEquals("", fh.getLine(5));
        assertNull(fh.getLine(5));
    }

    @Test
    void runOfMoreThanThreeContinuationBytesIsLeftAlone() throws IOException {
        // 41 then four continuation bytes cannot be one character; nothing is pushed back.
        FileHandler fh = openForRead(bytes(0x41, 0x80, 0x80, 0x80, 0x80, 0x0a));

        assertEquals("A����", fh.getLine(6));
        assertEquals("", fh.getLine(6));
        assertNull(fh.getLine(6));
    }

    // ---- close ------------------------------------------------------------------------------

    @Test
    void closeClosesTheReadStream() throws IOException {
        FileHandler fh = openForRead("abc\n");
        assertEquals("abc", fh.getLine(1024));

        assertTrue(fh.close());

        assertFalse(fh.isOpen());
        assertNull(fh.getLine(1024));
    }
}
