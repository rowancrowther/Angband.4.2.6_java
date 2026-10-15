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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.AngFile;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.util.ArrayList;
import java.util.List;

/**
 * A block of coloured text, built up by appending and wrapped on demand, ready to be
 * displayed straight to the screen or written to a file - the Java port of
 * {@code struct textblock} from {@code [C] src/z-textblock.c}.
 * <p>
 * The C original keeps two parallel arrays, {@code wchar_t *text} and {@code uint8_t *attrs},
 * plus a length and a capacity that it grows by hand. This class keeps a single list of
 * {@link AngbandDisplayCharacter} pairs instead, so the glyph and its colour cannot drift out
 * of step and there is no capacity to manage. That changes the shape of the API at the
 * boundary:
 * <ul>
 *   <li>{@code textblock_new()} and {@code textblock_free()} become the constructor and the
 *   garbage collector.</li>
 *   <li>{@code textblock_text()} becomes {@link #toString()}; {@code textblock_attrs()} has no
 *   direct counterpart, because the colours travel inside the cells and survive
 *   {@link #calculateLines(int)}.</li>
 *   <li>{@code textblock_calculate_lines()} returns the wrapped lines as {@code TextBlock}s
 *   rather than as parallel start and length arrays.</li>
 *   <li>The formatted appends use {@link String#formatted(Object...)}, so the format
 *   specifiers are Java's rather than those of Angband's own {@code vstrnfmt()}.</li>
 * </ul>
 * Text is held as UTF-16 {@code char}s, so a character outside the basic multilingual plane
 * counts as two cells when wrapping, where C's {@code wchar_t} counts it as one. The
 * {@code text_out()} family that shares the C file is not part of this class.
 *
 * <p>Class TextBlock coded before 261015, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
public class TextBlock {
    /**
     * Logger used to report malformed append calls (e.g. mismatched
     * string/colour lengths) and failures writing to a file.
     *
     * <p>Field logger coded before 261015, commented in full on 261015.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The ordered sequence of coloured characters making up this block; the
     * pairing of glyph and colour is what lets a block carry per-character
     * colouring through wrapping and output. This one list replaces the parallel
     * {@code text} and {@code attrs} arrays of {@code struct textblock} in
     * {@code [C] src/z-textblock.c}, and its size is the block's length.
     *
     * <p>Field textAndAttributes coded before 261015, commented in full on 261015.
     */
    private final ArrayList<AngbandDisplayCharacter> textAndAttributes;

    /**
     * Creates an empty text block, the counterpart of {@code textblock_new()} in
     * {@code [C] src/z-textblock.c}. C allocates an initial capacity of 128 characters and grows
     * it by hand; the backing list here needs neither.
     *
     * <p>Constructor TextBlock() coded before 261015, commented in full on 261015.
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public TextBlock() {
        textAndAttributes = new ArrayList<>();
    }

    /**
     * Adds one character in the given colour to the end of the text block. This is the Java
     * counterpart of {@code textblock_append_pict()} in {@code [C] src/z-textblock.c}, and is
     * also the primitive the formatted appends use to add each character of their output. The
     * character is stored as given, with no interpretation, so a {@code '\n'} or {@code ' '}
     * is kept and acted on later by {@link #calculateLines(int)}.
     * <p>
     * C takes the glyph as an {@code int} and casts it to {@code wchar_t}; here it is a
     * {@code char}, so a tile value above {@code 0xFFFF} cannot be passed.
     *
     * <p>Function append(char, ColourEnum) coded before 261015, commented in full on 261015.
     *
     * @param c   the character we are adding in
     * @param col the ColourEnum we are adding in
     */
    @Contract(mutates = "this")
    public void append(char c, @NotNull ColourEnum col) {
        AngbandDisplayCharacter a = new AngbandDisplayCharacter(c, col);
        textAndAttributes.add(a);
    }

    /**
     * Adds an existing glyph/colour cell to the end of the text block. The cell is stored by
     * reference, not copied; that is safe because {@link AngbandDisplayCharacter} is
     * immutable. {@link #append(TextBlock)}, {@link #copy()} and {@link #calculateLines(int)}
     * all rely on that to move cells between blocks cheaply.
     *
     * <p>Function append(AngbandDisplayCharacter) coded before 261015, commented in full on
     * 261015.
     *
     * @param a the AngbandDisplayCharacter to append
     */
    @Contract(mutates = "this")
    public void append(@NotNull AngbandDisplayCharacter a) {
        textAndAttributes.add(a);
    }

    /**
     * Appends every cell of another text block, text and colours together, to the end of this
     * one, as {@code textblock_append_textblock()} does in {@code [C] src/z-textblock.c}. The
     * argument is left unchanged.
     * <p>
     * The source is snapshotted with {@link #copy()} before any cell is added. C's copy is a
     * pair of {@code memcpy}s from a region that ends where the destination region begins, so
     * appending a block to itself simply doubles it; iterating the live list while adding to
     * it would throw {@link java.util.ConcurrentModificationException} instead, and the
     * snapshot is what keeps that case matching C.
     *
     * <p>Function append(TextBlock) coded before 261015, commented in full on 261015, updated
     * on 261015 to snapshot the source so that self-append matches C.
     *
     * @param toAppend the text block we wish to append to this one.
     */
    @Contract(mutates = "this")
    public void append(@NotNull TextBlock toAppend) {
        TextBlock toAdd = toAppend.copy();

        for (AngbandDisplayCharacter textAndAttribute : toAdd.textAndAttributes) {
            append(textAndAttribute);
        }
    }

    /**
     * Makes an independent block holding the same cells in the same order. The list is new,
     * but the cells themselves are shared, which is safe because
     * {@link AngbandDisplayCharacter} is immutable. There is no counterpart in
     * {@code [C] src/z-textblock.c}; it exists so {@link #append(TextBlock)} can read a stable
     * snapshot of its argument.
     *
     * <p>Function copy coded on 261015, commented in full on 261015.
     *
     * @return a new TextBlock with the same text and colours as this one
     */
    public TextBlock copy() {
        TextBlock copy = new TextBlock();
        
        for (AngbandDisplayCharacter textAndAttribute : textAndAttributes) {
            copy.append(textAndAttribute);
        }
        
        return copy;
    }

    /**
     * Formats a string and appends it in white, as {@code textblock_append()} does in
     * {@code [C] src/z-textblock.c}. It delegates to
     * {@link #append(ColourEnum, String, Object...)} with {@link ColourEnum#COLOUR_WHITE}.
     *
     * <p>Function append(String, Object...) coded before 261015, commented in full on 261015.
     *
     * @param fmt     the string to format and then append
     * @param objects the objects to format the string with
     */
    @Contract(mutates = "this")
    public void append(@NotNull String fmt, Object... objects) {
        append(ColourEnum.COLOUR_WHITE, fmt, objects);
    }

    /**
     * Formats a string and appends every character of the result in one colour, as
     * {@code textblock_append_c()} does in {@code [C] src/z-textblock.c}. The text is formatted
     * with {@link String#formatted(Object...)}, so the format specifiers are Java's rather than
     * Angband's {@code vstrnfmt()} ones. Each resulting {@code char} becomes one cell, so the
     * formatted text can be any length with no truncation.
     *
     * <p>Function append(ColourEnum, String, Object...) coded before 261015, commented in full
     * on 261015.
     *
     * @param col     the ColourEnum to use in appending this string
     * @param fmt     the string to format
     * @param objects the objects to format the string with
     */
    @Contract(mutates = "this")
    public void append(@NotNull ColourEnum col, @NotNull String fmt, Object... objects) {
        String formattedString = fmt.formatted(objects);
        for (char ch : formattedString.toCharArray()) {
            append(ch, col);
        }
    }

    /**
     * The number of cells in this block, the counterpart of the {@code strlen} field of
     * {@code struct textblock} in {@code [C] src/z-textblock.c}. Every character counts,
     * including {@code '\n'} and spaces, and each UTF-16 {@code char} is one cell.
     *
     * <p>Function length coded before 261015, commented in full on 261015.
     *
     * @return The current size of the array in this TextBlock
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int length() {
        return textAndAttributes.size();
    }

    /**
     * Splits this block into wrapped lines no wider than {@code width}, the port of
     * {@code textblock_calculate_lines()} in {@code [C] src/z-textblock.c}. C reports the lines as
     * parallel arrays of start offsets and lengths; here each line comes back as its own
     * {@code TextBlock}, with its colours intact. This block is not changed.
     * <p>
     * The rules, all inherited from C:
     * <ul>
     *   <li>A {@code '\n'} ends the line and is not part of it.</li>
     *   <li>When a line reaches {@code width} cells, it is broken at the last space on that
     *   line, and the space itself is dropped from both lines. Scanning resumes just after the
     *   space.</li>
     *   <li>A space in the first cell of a line is never used as a break point, so a line with
     *   no other space is cut hard at {@code width}.</li>
     *   <li>A line that fills exactly before a {@code '\n'} is followed by an empty line,
     *   because the newline then starts a fresh one. C does the same.</li>
     *   <li>Only the final line is trimmed, and only if it is empty, so a trailing {@code '\n'}
     *   adds no line but a blank line in the middle is kept.</li>
     * </ul>
     * A {@code width} of zero or less, or an empty block, gives an empty list, where C
     * returns 0 lines.
     *
     * <p>Function calculateLines coded before 261015, commented in full on 261015.
     *
     * @param width The width of the wrapped lines
     * @return An array of TextBlocks
     */
    @CheckReturnValue
    @Contract(pure = true)
    public List<TextBlock> calculateLines(int width) {
        if (width <= 0 || textAndAttributes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<StartAndLength> lineInfo;
        int textOffset = 0;
        int currentLineLength = 0;
        int breakingCharOffset = 0;
        
        // We have one line - start the storage thereof
        lineInfo = new ArrayList<>();
        StartAndLength firstLine = new StartAndLength(0, 0);
        lineInfo.add(firstLine);
        
        while (textOffset < length()) {
            if (textAndAttributes.get(textOffset).getCharacter() == '\n') {
                StartAndLength lastLine = lineInfo.getLast();
                StartAndLength newLastLine = new StartAndLength(lastLine.start(), currentLineLength);
                lineInfo.removeLast();
                lineInfo.add(newLastLine);
                StartAndLength nextLine = new StartAndLength(textOffset + 1, 0);
                lineInfo.add(nextLine);
                currentLineLength = 0;
            } else if (textAndAttributes.get(textOffset).getCharacter() == ' ') {
                breakingCharOffset = textOffset;
                currentLineLength++;
            } else {
                currentLineLength++;
            }
            
            // deal with needing to break the line
            if (currentLineLength == width) {
                int currentLineStart = lineInfo.getLast().start();
                int nextLineStartOffset = 0;
                int adjustedLineLength = 0;
                
                if (breakingCharOffset > currentLineStart) {
                    adjustedLineLength = breakingCharOffset - currentLineStart;
                    nextLineStartOffset = breakingCharOffset + 1;
                    textOffset = breakingCharOffset + 1;
                } else {
                    adjustedLineLength = width;
                    nextLineStartOffset = textOffset + 1;
                    textOffset++;
                }
                
                StartAndLength lastLine = lineInfo.getLast();
                StartAndLength newLastLine = new StartAndLength(lastLine.start(), adjustedLineLength);
                lineInfo.removeLast();
                lineInfo.add(newLastLine);
                
                StartAndLength nextLine = new StartAndLength(nextLineStartOffset, 0);
                lineInfo.add(nextLine);
                currentLineLength = 0;
            } else {
                StartAndLength lastLine = lineInfo.getLast();
                StartAndLength replacementLine = new StartAndLength(lastLine.start(), currentLineLength);
                lineInfo.removeLast();
                lineInfo.add(replacementLine);
                textOffset++;
            }
        }
        
        if (lineInfo.getLast().length() == 0)
            lineInfo.removeLast();
        
        List<TextBlock> lines = new ArrayList<>();
        
        for (StartAndLength lineInfoLine : lineInfo) {
            TextBlock newTB = new TextBlock();
            for (int index = 0; index < lineInfoLine.length(); index++) {
                int pointer = index + lineInfoLine.start();
                newTB.append(textAndAttributes.get(pointer));
            }
            
            lines.add(newTB);
        }

        return lines;
    }

    /**
     * Appends a string with a separate colour for each character: character {@code i} of the
     * string is drawn in colour {@code i} of the list. There is no single function for this in
     * {@code [C] src/z-textblock.c}, where the same effect takes several
     * {@code textblock_append_c()} calls; the method uses {@link #append(char, ColourEnum)} for
     * each pair.
     * <p>
     * The two inputs must be the same length. If they are not, the mismatch is logged and an
     * {@link IllegalArgumentException} is thrown before anything is appended, so a bad call
     * leaves the block unchanged.
     *
     * <p>Function append(String, List) coded before 261015, commented in full on 261015.
     *
     * @param string  The string we are appending to this text block
     * @param colours The colours we are using to append, linked to the characters in the string
     * @throws IllegalArgumentException if the string and the colour list differ in length
     */
    @Contract(mutates = "this")
    public void append(@NotNull String string, @NotNull List<ColourEnum> colours) {
        if (string.length() != colours.size()) {
            String message = "Invalid string/list input to TextBlock creator. String was of length " + string.length() +
                    " and colours array was of length " + colours.size();
            IllegalArgumentException e = new IllegalArgumentException(message);
            logger.error(message, e);
            throw e;
        }

        for (int index = 0; index < colours.size(); index++)
            append(string.charAt(index), colours.get(index));
    }

    /**
     * Wraps this block and writes it to a file, one wrapped line per file line, each preceded
     * by {@code indent} spaces. The port of {@code textblock_to_file()} in
     * {@code [C] src/z-textblock.c}. The lines are wrapped at {@code wrapAt - indent} using
     * {@link #calculateLines(int)}, and the colours are dropped on output. An empty block
     * writes nothing.
     * <p>
     * Accepted divergences from C:
     * <ul>
     *   <li><b>Negative indent</b> (decided 261015). The width guard uses the raw
     *   {@code indent}, as C's {@code assert(width > 0)} does, but a negative {@code indent}
     *   is then clamped to 0 before the wrap width is worked out. C subtracts the raw
     *   negative value instead, which would wrap wider than {@code wrapAt}. A negative indent
     *   has no meaning, and no C caller passes one.</li>
     *   <li><b>Failures.</b> The width assertion becomes an {@link IllegalArgumentException}.
     *   C ignores a failed write, but here a failed {@link AngFile#putLine(String)} is logged
     *   and thrown as a {@link RuntimeException}.</li>
     *   <li><b>Characters.</b> C converts each character to the locale encoding and writes a
     *   space for one it cannot convert. This method does no per-character substitution and
     *   leaves the encoding to {@code AngFile}.</li>
     * </ul>
     *
     * <p>Function toFile coded before 261015, commented in full on 261015.
     *
     * @param angFile The file to write this to
     * @param indent  The indent to put in front of each line
     * @param wrapAt  Where we are wrapping the lines
     * @throws IllegalArgumentException if {@code wrapAt - indent} is zero or less
     * @throws RuntimeException         if a line cannot be written
     */
    public void toFile(@NotNull AngFile angFile, int indent, int wrapAt) {
        if (wrapAt - indent <= 0) {
            String message = "Text passed to write to file has a width less than or equal to zero";
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
        if (indent < 0) indent = 0;
        
        String indentString;
        indentString = " ".repeat(indent);

        List<TextBlock> blocks = calculateLines(wrapAt - indent);

        for (TextBlock tb : blocks) {
            if (!angFile.putLine(indentString + tb.toString())) {
                String message = "Error writing to text block file.";
                logger.error(message);
                throw new RuntimeException(message);
            }
        }
    }

    /**
     * The text of this block with the colours left out, in order and including any
     * {@code '\n'} and spaces. This is the counterpart of {@code textblock_text()} in
     * {@code [C] src/z-textblock.c}, but it returns a new string each time instead of a pointer
     * into the block's own buffer.
     *
     * <p>Function toString coded before 261015, commented in full on 261015.
     *
     * @return A string showing the text from the text/attribute pairing
     */
    @CheckReturnValue
    @Contract(pure = true)
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (AngbandDisplayCharacter ch : textAndAttributes)
            output.append(ch.getCharacter());

        return output.toString();
    }

    /**
     * Where one wrapped line sits in {@link #textAndAttributes}: the offset of its first cell
     * and how many cells it holds. {@link #calculateLines(int)} uses it in place of the
     * parallel {@code line_starts} and {@code line_lengths} arrays of
     * {@code textblock_calculate_lines()} in {@code [C] src/z-textblock.c}. The record is
     * immutable, so updating a line means replacing it.
     *
     * <p>Record StartAndLength coded before 261015, commented in full on 261015.
     *
     * @param start  offset of the line's first cell in {@link #textAndAttributes}
     * @param length number of cells in the line
     */
    private record StartAndLength(int start, int length) {}
}