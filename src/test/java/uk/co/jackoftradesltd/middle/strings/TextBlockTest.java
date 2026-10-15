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

package uk.co.jackoftradesltd.middle.strings;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.AngFile;
import uk.co.jackoftradesltd.backend.io.enums.FileModeEnum;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TextBlock}, the port of {@code struct textblock} and its functions in
 * {@code src/z-textblock.c}.
 * <p>
 * The append tests follow C's own {@code tests/z-textblock/textblock.c}: {@code "Hello"} then
 * {@code "%d"} with 20 gives {@code "Hello20"}; a green {@code "two"} carries three green
 * attributes; thirty-two appends of {@code "1234567"} stay in order; and appending a block
 * leaves the source untouched. Self-append is added because C's {@code memcpy}s double the
 * text and the Java port has to do the same without throwing.
 * <p>
 * C's suite has nothing for {@code textblock_calculate_lines()} or {@code textblock_to_file()},
 * so those expected values were worked out by hand-tracing the C loop - the scan, the backtrack
 * to the last space, the hard break, the newline arm and the single trailing-line trim - rather
 * than by running the Java. Several of the cases pin quirks that are C behaviour, not good
 * design, and should not be "fixed": a blank line after a line that fills exactly before a
 * {@code '\n'}, a space kept at the start of a line after a hard break, and a space in the
 * first cell never counting as a break point.
 * <p>
 * Two divergences are pinned deliberately: a negative {@code indent} is clamped to zero
 * instead of widening the wrap (decided 261015), and a failed write throws where C ignores it.
 * <p>
 * Class TextBlockTest coded on 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
class TextBlockTest {

    /**
     * Builds a block of the given text, all white.
     */
    private static TextBlock white(String text) {
        TextBlock tb = new TextBlock();
        tb.append(text);
        return tb;
    }

    /**
     * The text of each wrapped line, without colours, in order.
     */
    private static List<String> lineText(List<TextBlock> lines) {
        List<String> out = new ArrayList<>();
        for (TextBlock line : lines) {
            out.add(line.toString());
        }
        return out;
    }

    @Nested
    class Appending {

        /**
         * C {@code test_append}: a new block is empty, and formatted appends concatenate.
         */
        @Test
        void formattedAppendsConcatenate() {
            TextBlock tb = new TextBlock();
            assertEquals("", tb.toString());
            assertEquals(0, tb.length());

            tb.append("Hello");
            assertEquals("Hello", tb.toString());

            tb.append("%d", 20);
            assertEquals("Hello20", tb.toString());
            assertEquals(7, tb.length());
        }

        /**
         * C {@code test_length}: thirty-two appends of the same string all land, in order.
         */
        @Test
        void repeatedAppendsStayInOrder() {
            TextBlock tb = new TextBlock();
            for (int i = 0; i < 32; i++) {
                tb.append("1234567");
            }

            assertEquals(32 * 7, tb.length());
            assertEquals("1234567".repeat(32), tb.toString());
        }

        /**
         * An append with no colour is white, as {@code textblock_append()} passes
         * {@code COLOUR_WHITE}; the explicit-colour form uses the colour it is given.
         */
        @Test
        void plainAppendIsWhiteAndColouredAppendUsesItsColour() {
            TextBlock tb = new TextBlock();
            tb.append("ab");
            tb.append(ColourEnum.COLOUR_LIGHT_GREEN, "two");

            List<ColourEnum> colours = colours(tb);
            assertEquals(List.of(
                    ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_WHITE,
                    ColourEnum.COLOUR_LIGHT_GREEN, ColourEnum.COLOUR_LIGHT_GREEN,
                    ColourEnum.COLOUR_LIGHT_GREEN), colours);
        }

        /**
         * C {@code test_append_textblock}: text and attributes both append, in order, and the
         * source block is unchanged.
         */
        @Test
        void appendingABlockCopiesTextAndColoursAndLeavesTheSourceAlone() {
            TextBlock tb1 = new TextBlock();
            TextBlock tb2 = new TextBlock();
            tb1.append(ColourEnum.COLOUR_LIGHT_BLUE, "Hey");
            tb2.append(ColourEnum.COLOUR_LIGHT_GREEN, " you");

            tb1.append(tb2);

            assertEquals("Hey you", tb1.toString());
            assertEquals(List.of(
                    ColourEnum.COLOUR_LIGHT_BLUE, ColourEnum.COLOUR_LIGHT_BLUE,
                    ColourEnum.COLOUR_LIGHT_BLUE,
                    ColourEnum.COLOUR_LIGHT_GREEN, ColourEnum.COLOUR_LIGHT_GREEN,
                    ColourEnum.COLOUR_LIGHT_GREEN, ColourEnum.COLOUR_LIGHT_GREEN), colours(tb1));
            assertEquals(" you", tb2.toString());
            assertEquals(List.of(
                    ColourEnum.COLOUR_LIGHT_GREEN, ColourEnum.COLOUR_LIGHT_GREEN,
                    ColourEnum.COLOUR_LIGHT_GREEN, ColourEnum.COLOUR_LIGHT_GREEN), colours(tb2));
        }

        /**
         * C's {@code memcpy}s read a region that ends where the destination begins, so
         * {@code textblock_append_textblock(tb, tb)} doubles the block. The port must not throw
         * a {@link java.util.ConcurrentModificationException}.
         */
        @Test
        void appendingABlockToItselfDoublesIt() {
            TextBlock tb = new TextBlock();
            tb.append(ColourEnum.COLOUR_RED, "a");
            tb.append(ColourEnum.COLOUR_BLUE, "b");

            assertDoesNotThrow(() -> tb.append(tb));

            assertEquals("abab", tb.toString());
            assertEquals(List.of(
                    ColourEnum.COLOUR_RED, ColourEnum.COLOUR_BLUE,
                    ColourEnum.COLOUR_RED, ColourEnum.COLOUR_BLUE), colours(tb));
        }

        /**
         * Appending an empty block, or to an empty block, changes nothing that was not there.
         */
        @Test
        void appendingEmptyBlocks() {
            TextBlock tb = white("x");
            tb.append(new TextBlock());
            assertEquals("x", tb.toString());

            TextBlock empty = new TextBlock();
            empty.append(tb);
            assertEquals("x", empty.toString());
        }

        /**
         * {@code textblock_append_pict()}: one cell, any glyph, no interpretation - a newline
         * and a space are stored like any other character.
         */
        @Test
        void singleCellAppendsStoreTheGlyphVerbatim() {
            TextBlock tb = new TextBlock();
            tb.append('\n', ColourEnum.COLOUR_RED);
            tb.append(' ', ColourEnum.COLOUR_BLUE);

            assertEquals("\n ", tb.toString());
            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_BLUE), colours(tb));
        }

        /**
         * The per-character colour form pairs character {@code i} with colour {@code i}.
         */
        @Test
        void stringWithColourListPairsCharacterWithColour() {
            TextBlock tb = new TextBlock();
            tb.append("abc", List.of(
                    ColourEnum.COLOUR_RED, ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_BLUE));

            assertEquals("abc", tb.toString());
            assertEquals(List.of(
                    ColourEnum.COLOUR_RED, ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_BLUE),
                    colours(tb));
        }

        /**
         * A length mismatch is rejected before any cell is added, so the block is unchanged.
         */
        @Test
        void stringWithColourListOfDifferentLengthIsRejectedAndAddsNothing() {
            TextBlock tb = white("keep");

            assertThrows(IllegalArgumentException.class,
                    () -> tb.append("abc", List.of(ColourEnum.COLOUR_RED)));
            assertThrows(IllegalArgumentException.class,
                    () -> tb.append("a", List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_BLUE)));

            assertEquals("keep", tb.toString());
        }

        /**
         * {@code copy()} gives an independent block: appending to the copy does not change the
         * original.
         */
        @Test
        void copyIsIndependentOfTheOriginal() {
            TextBlock original = white("ab");
            TextBlock copy = original.copy();

            copy.append("c");

            assertEquals("ab", original.toString());
            assertEquals("abc", copy.toString());
        }
    }

    @Nested
    class Wrapping {

        /**
         * Guards: a zero or negative width, or an empty block, gives no lines (C returns 0).
         */
        @Test
        void emptyBlockOrNonPositiveWidthGivesNoLines() {
            assertTrue(new TextBlock().calculateLines(10).isEmpty());
            assertTrue(white("abc").calculateLines(0).isEmpty());
            assertTrue(white("abc").calculateLines(-3).isEmpty());
        }

        /**
         * Text shorter than the width is one line.
         */
        @Test
        void shortTextIsOneLine() {
            assertEquals(List.of("hello"), lineText(white("hello").calculateLines(10)));
        }

        /**
         * Hand-trace: at offset 6 the line is full; the last space is at 3, so the line is
         * "aaa" (length 3), the next starts at 4, and the loop backtracks to offset 4. The
         * same again for "bbb"; "ccc" ends the text.
         */
        @Test
        void breaksAtTheLastSpaceAndDropsIt() {
            assertEquals(List.of("aaa", "bbb", "ccc"),
                    lineText(white("aaa bbb ccc").calculateLines(7)));
            assertEquals(List.of("hello", "world"),
                    lineText(white("hello world").calculateLines(8)));
        }

        /**
         * A word with no space is cut hard at the width: "abc", "def", then the remainder.
         */
        @Test
        void aWordWiderThanTheLineIsCutHard() {
            assertEquals(List.of("abc", "def", "gh"),
                    lineText(white("abcdefgh").calculateLines(3)));
            assertEquals(List.of("a", "b"), lineText(white("ab").calculateLines(1)));
        }

        /**
         * Text exactly the width long is one line: the empty line the break starts is the one
         * that gets trimmed.
         */
        @Test
        void textExactlyTheWidthIsOneLine() {
            assertEquals(List.of("abcd"), lineText(white("abcd").calculateLines(4)));
        }

        /**
         * {@code '\n'} ends a line and is not part of it; a trailing newline adds no line, but
         * a blank line in the middle is kept.
         */
        @Test
        void newlinesEndLinesAndOnlyTheFinalEmptyLineIsTrimmed() {
            assertEquals(List.of("ab"), lineText(white("ab\n").calculateLines(5)));
            assertEquals(List.of("ab", "cd"), lineText(white("ab\ncd").calculateLines(5)));
            assertEquals(List.of("ab", "", "cd"), lineText(white("ab\n\ncd").calculateLines(5)));
        }

        /**
         * A block that is only a newline is one empty line: two lines are started and only the
         * last is trimmed.
         */
        @Test
        void aBlockOfJustANewlineIsOneEmptyLine() {
            assertEquals(List.of(""), lineText(white("\n").calculateLines(5)));
        }

        /**
         * C quirk: a line that fills exactly before a newline is followed by an empty line,
         * because the newline then starts yet another.
         */
        @Test
        void aLineThatFillsExactlyBeforeANewlineLeavesABlankLine() {
            assertEquals(List.of("abcd", "", "ef"),
                    lineText(white("abcd\nef").calculateLines(4)));
        }

        /**
         * C quirk: a space in the first cell of a line is never a break point
         * ({@code breaking_char_offset > current_line_start} is strict), so " abc" at width 3
         * is cut hard and the space stays at the start of the first line.
         */
        @Test
        void aLeadingSpaceIsNeverABreakPoint() {
            assertEquals(List.of(" ab", "c"), lineText(white(" abc").calculateLines(3)));
        }

        /**
         * C quirk: after a hard break, the space that starts the next line is kept and is
         * likewise not a break point, so "abcd efgh" at width 4 gives " efg" with its space.
         */
        @Test
        void aSpaceLeftAtTheStartOfALineAfterAHardBreakIsKept() {
            assertEquals(List.of("abcd", " efg", "h"),
                    lineText(white("abcd efgh").calculateLines(4)));
        }

        /**
         * Only the last space is the break and only one space is dropped, so with two spaces
         * together the first stays on the earlier line.
         */
        @Test
        void onlyTheLastSpaceIsDroppedWhenSpacesRepeat() {
            assertEquals(List.of("ab ", "cd"), lineText(white("ab  cd").calculateLines(4)));
        }

        /**
         * Each cell keeps its colour through wrapping, and the source block is not changed.
         */
        @Test
        void colourSurvivesWrappingAndTheSourceIsUnchanged() {
            TextBlock tb = new TextBlock();
            tb.append(ColourEnum.COLOUR_RED, "ab ");
            tb.append(ColourEnum.COLOUR_BLUE, "cd");

            List<TextBlock> lines = tb.calculateLines(4);

            assertEquals(List.of("ab", "cd"), lineText(lines));
            assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_RED), colours(lines.get(0)));
            assertEquals(List.of(ColourEnum.COLOUR_BLUE, ColourEnum.COLOUR_BLUE), colours(lines.get(1)));
            assertEquals("ab cd", tb.toString());
            assertEquals(5, tb.length());
        }
    }

    @Nested
    class WritingToFile {

        private String written(Path path) throws IOException {
            return Files.exists(path) ? Files.readString(path) : "";
        }

        /**
         * Ordinary path: each wrapped line is written followed by a newline. This is
         * {@code file_putf(f, "\n")} after each line in {@code textblock_to_file()}.
         */
        @Test
        void writesEachWrappedLineWithATrailingNewline(@TempDir Path root) throws IOException {
            Path path = root.resolve("out.txt");
            AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);

            white("aaa bbb ccc").toFile(file, 0, 7);
            file.close();

            assertEquals("aaa\nbbb\nccc\n", written(path));
        }

        /**
         * C pads each line with {@code indent} spaces ({@code "%*c"}), and wraps at
         * {@code wrap_at - indent}: indent 2 at wrap 9 is width 7, as above.
         */
        @Test
        void indentsEachLineAndWrapsAtWrapMinusIndent(@TempDir Path root) throws IOException {
            Path path = root.resolve("out.txt");
            AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);

            white("aaa bbb ccc").toFile(file, 2, 9);
            file.close();

            assertEquals("  aaa\n  bbb\n  ccc\n", written(path));
        }

        /**
         * A blank line in the middle is written as an indent and nothing else, matching C
         * which pads before it knows the line is empty.
         */
        @Test
        void aBlankLineIsStillIndented(@TempDir Path root) throws IOException {
            Path path = root.resolve("out.txt");
            AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);

            white("ab\n\ncd").toFile(file, 1, 8);
            file.close();

            assertEquals(" ab\n \n cd\n", written(path));
        }

        /**
         * An empty block has no lines, so nothing is written.
         */
        @Test
        void anEmptyBlockWritesNothing(@TempDir Path root) throws IOException {
            Path path = root.resolve("out.txt");
            AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);

            assertDoesNotThrow(() -> new TextBlock().toFile(file, 0, 10));
            file.close();

            assertEquals("", written(path));
        }

        /**
         * C {@code assert(width > 0)}: a wrap column no wider than the indent is rejected,
         * and nothing is written first.
         */
        @Test
        void aWidthOfZeroOrLessIsRejected(@TempDir Path root) throws IOException {
            Path path = root.resolve("out.txt");
            AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);
            TextBlock tb = white("abc");

            assertThrows(IllegalArgumentException.class, () -> tb.toFile(file, 5, 5));
            assertThrows(IllegalArgumentException.class, () -> tb.toFile(file, 6, 5));
            assertThrows(IllegalArgumentException.class, () -> tb.toFile(file, 0, 0));
            file.close();

            assertEquals("", written(path));
        }

        /**
         * Accepted divergence (decided 261015): a negative indent is clamped to zero before
         * the wrap width is worked out. C would subtract the raw value and wrap at 12, giving
         * the single line "aaa bbb ccc"; the port wraps at 7 with no padding.
         */
        @Test
        void aNegativeIndentIsClampedToZeroUnlikeC(@TempDir Path root) throws IOException {
            Path path = root.resolve("out.txt");
            AngFile file = new AngFile(path.toString(), FileModeEnum.MODE_WRITE);

            white("aaa bbb ccc").toFile(file, -5, 7);
            file.close();

            assertEquals("aaa\nbbb\nccc\n", written(path));
        }

        /**
         * The width guard uses the raw indent, as C's does: indent -5 with wrap -2 is a
         * width of 3 and passes, even though the clamp then leaves nothing to wrap at.
         */
        @Test
        void theWidthGuardUsesTheRawIndent(@TempDir Path root) {
            AngFile file = new AngFile(root.resolve("out.txt").toString(), FileModeEnum.MODE_WRITE);

            assertDoesNotThrow(() -> white("abc").toFile(file, -5, -2));
        }

        /**
         * Divergence: C ignores a failed {@code file_putf()}; the port logs it and throws.
         * A file opened for reading refuses every write.
         */
        @Test
        void aFailedWriteThrows(@TempDir Path root) {
            AngFile file = new AngFile(root.resolve("readonly.txt").toString(), FileModeEnum.MODE_READ);

            assertThrows(RuntimeException.class, () -> white("abc").toFile(file, 0, 10));
        }
    }

    /**
     * The colours of a block, cell by cell. {@link TextBlock} has no accessor for them - C's
     * {@code textblock_attrs()} has no counterpart - so the test reads the private cell list
     * by reflection rather than widen the class's API for the sake of a test.
     */
    @SuppressWarnings("unchecked")
    private static List<ColourEnum> colours(TextBlock tb) {
        try {
            Field field = TextBlock.class.getDeclaredField("textAndAttributes");
            field.setAccessible(true);
            List<AngbandDisplayCharacter> cells = (List<AngbandDisplayCharacter>) field.get(tb);
            List<ColourEnum> out = new ArrayList<>();
            for (AngbandDisplayCharacter cell : cells) {
                out.add(cell.getAttributeColour());
            }
            return out;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("TextBlock.textAndAttributes is no longer readable", e);
        }
    }
}
