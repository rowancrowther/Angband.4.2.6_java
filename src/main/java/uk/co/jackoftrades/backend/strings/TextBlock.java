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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.io.AngFile;
import uk.co.jackoftrades.backend.io.enums.FileTypeEnum;
import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;

import java.util.ArrayList;
import java.util.List;

/**
 * T block of text to be displayed straight to the screen.
 */
public class TextBlock {
    private final ArrayList<AngbandDisplayCharacter> textAndAttributes;
    private static final Logger logger = LogManager.getLogger();

    /**
     * Constructor
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public TextBlock() {
        textAndAttributes = new ArrayList<>();
    }

    /**
     * Converts this to a string
     *
     * @return the characters of the AngbandDisplayCharacter values stored in this class
     */
    @Contract(pure = true)
    @CheckReturnValue
    private @NotNull String convertToString() {
        StringBuilder output = new StringBuilder();
        for (AngbandDisplayCharacter adc : textAndAttributes) {
            output.append(adc.getCharacter());
        }

        return output.toString();
    }

    /**
     * Add a character/colour combination to the end of the text block
     *
     * @param c   the character we are adding in
     * @param col the AttributeColour we are adding in
     */
    @Contract(mutates = "this")
    public void append(char c, @NotNull AttributeColour col) {
        AngbandDisplayCharacter a = new AngbandDisplayCharacter(c, col);
        textAndAttributes.add(a);
    }

    /**
     * Add a new AngbandDisplayCharacter to the end of the text block
     *
     * @param a the AngbandDisplayCharacter to append
     */
    @Contract(mutates = "this")
    public void append(@NotNull AngbandDisplayCharacter a) {
        textAndAttributes.add(a);
    }

    /**
     * Append an entire text block to this one
     *
     * @param toAppend the text block we wish to append to this one.
     */
    @Contract(mutates = "this")
    public void append(@NotNull TextBlock toAppend) {

        for (AngbandDisplayCharacter textAndAttribute : toAppend.textAndAttributes) {
            append(textAndAttribute);
        }
    }

    /**
     * Append a formatted string entirely in COLOUR_WHITE
     *
     * @param fmt     the string to format and then append
     * @param objects the objects to format the string with
     */
    @Contract(mutates = "this")
    public void append(@NotNull String fmt, Object... objects) {
        append(AttributeColour.COLOUR_WHITE, fmt, objects);
    }

    /**
     * Append a formatted string entirely in a specific AttributeColour
     *
     * @param col     the AttributeColour to use in appending this string
     * @param fmt     the string to format
     * @param objects the objects to format the string with
     */
    @Contract(mutates = "this")
    public void append(@NotNull AttributeColour col, @NotNull String fmt, Object... objects) {
        String formattedString = fmt.formatted(objects);
        for (char ch : formattedString.toCharArray()) {
            append(ch, col);
        }
    }

    /**
     * Get the current length of this TextBlock
     *
     * @return The current size of the array in this TextBlock
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int length() {
        return textAndAttributes.size();
    }

    /**
     * Work out where the next non-whitespace character is
     *
     * @param index The index of the textAndAttributes list we are starting at
     * @return the first non-whitespace character after index
     */
    @Contract(pure = true)
    @CheckReturnValue
    private int skipWhiteSpace(int index) {
        char ch = textAndAttributes.get(index).getCharacter();

        while (Character.isWhitespace(ch) && index < textAndAttributes.size()) {
            index++;
            ch = textAndAttributes.get(index).getCharacter();
        }

        return index;
    }

    /**
     * Skip non white space characters and return the index of the next white space
     *
     * @param index the current index of the line
     * @return the index of the next white space in the line
     */
    @CheckReturnValue
    @Contract(pure = true)
    private int skipNonWhiteSpace(int index) {
        char ch = textAndAttributes.get(index).getCharacter();

        while (!Character.isWhitespace(ch) && index < textAndAttributes.size()) {
            index++;
            ch = textAndAttributes.get(index).getCharacter();
        }

        return index;
    }

    /**
     * Turn a TextBlock into wrapped lines of length width
     *
     * @param width The width of the wrapped lines
     * @return An array of TextBlocks
     */
    @CheckReturnValue
    @Contract(pure = true)
    public List<TextBlock> calculateLines(int width) {
        String valueToWrap = convertToString();
        String[] tokens = valueToWrap.split("\\s");
        int currentLength = 0;
        int tokenLength;
        List<TextBlock> output = new ArrayList<>();
        TextBlock currentLine = new TextBlock();
        boolean splitWord;

        for (String token : tokens) {
            splitWord = false;
            tokenLength = token.length();
            if (currentLength + tokenLength + 1 > width) {
                if (tokenLength > width) {
                    if (currentLine.length() != 0) output.add(currentLine);

                    while (!token.isEmpty()) {
                        currentLine = new TextBlock();
                        currentLine.append(token.substring(0, Math.min(width, token.length())));
                        if (token.length() > width) {
                            output.add(currentLine);
                        }
                        token = token.substring(Math.min(width, token.length()));
                        splitWord = true;
                    }
                    if (token.length() >= 0) currentLine.append(token + " ");

                    if (currentLine.length() >= 0 && !splitWord) {
                        output.add(currentLine);
                        currentLine = new TextBlock();
                    }

                } else {
                    output.add(currentLine);
                    currentLine = new TextBlock();
                    currentLength = 0;
                }
            }

            if (!splitWord) {
                currentLength += tokenLength + 1;
                currentLine.append(token + " ");
            }
        }

        if (currentLine.length() != 0) output.add(currentLine);

        return output;
    }

    /**
     * Appending a string and an array of colours
     *
     * @param string  The string we are appending to this text block
     * @param colours The colours we are using to append, linked to the characters in the string
     */
    @Contract(mutates = "this")
    public void append(@NotNull String string, @NotNull List<AttributeColour> colours) {
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
     * Writes the current TB into a file with a given indent and space wrapping
     *
     * @param angFile The file to write this to
     * @param indent  The indent to put in front of each line
     * @param wrapAt  Where we are wrapping the lines
     */
    @Contract(pure = true)
    public void toFile(@NotNull AngFile angFile, int indent, int wrapAt) {
        String indentString;
        StringBuilder builder = new StringBuilder();
        builder.repeat(" ", indent);
        indentString = builder.toString();

        List<TextBlock> blocks = calculateLines(wrapAt - indent);

        angFile.open(FileTypeEnum.FTYPE_TEXT);

        for (TextBlock tb : blocks) {
            angFile.putLine(indentString + tb.toString());
        }
        angFile.close();
    }

    /**
     * Overrides the toString method to return the text string made up of the textAndAttributes characters
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
}