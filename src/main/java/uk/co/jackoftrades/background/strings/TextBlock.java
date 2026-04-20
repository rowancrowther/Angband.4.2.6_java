package uk.co.jackoftrades.background.strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.background.io.AngFile;
import uk.co.jackoftrades.background.io.enums.FileTypeEnum;
import uk.co.jackoftrades.background.strings.enums.AttributeColour;

import java.util.ArrayList;
import java.util.List;

/**
 * T block of text to be displayed straight to the screen.
 */
public class TextBlock {
    private List<AngbandDisplayCharacter> textAndAttributes;
    private Logger logger = LogManager.getLogger();

    /**
     * Constructor
     */
    public TextBlock() {
        textAndAttributes = new ArrayList<>();
    }

    /**
     * Add a character/colour combination to the end of the text block
     *
     * @param c   the character we are adding in
     * @param col the AttributeColour we are adding in
     */
    public void append(char c, AttributeColour col) {
        AngbandDisplayCharacter a = new AngbandDisplayCharacter(c, col);
        textAndAttributes.add(a);
    }

    /**
     * Add a new AngbandDisplayCharacter to the end of the text block
     *
     * @param a the AngbandDisplayCharacter to append
     */
    public void append(AngbandDisplayCharacter a) {
        textAndAttributes.add(a);
    }

    /**
     * Append an entire text block to this one
     *
     * @param toAppend the text block we wish to append to this one.
     */
    public void append(@NonNull TextBlock toAppend) {

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
    public void append(@NonNull String fmt, Object... objects) {
        append(AttributeColour.COLOUR_WHITE, fmt, objects);
    }

    /**
     * Append a formatted string entirely in a specific AttributeColour
     *
     * @param col     the AttributeColour to use in appending this string
     * @param fmt     the string to format
     * @param objects the objects to format the string with
     */
    public void append(@NonNull AttributeColour col, @NonNull String fmt, Object... objects) {
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
    public int length() {
        return textAndAttributes.size();
    }

    /**
     * Work out where the next non-whitespace character is
     *
     * @param index The index of the textAndAttributes list we are starting at
     * @return the first non-whitespace character after index
     */
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
    private int skipNonWhiteSpace(int index) {
        char ch = textAndAttributes.get(index).getCharacter();

        while (!Character.isWhitespace(ch) && index < textAndAttributes.size()) {
            index++;
            ch = textAndAttributes.get(index).getCharacter();
        }

        return index;
    }

    /**
     * TODO: Debug this into existance
     * Turn a TextBlock into wrapped lines of length width
     *
     * @param width The width of the wrapped lines
     * @return An array of TextBlocks
     */
    public List<TextBlock> calculateLines(int width) {
        int index = 0;
        int currentTokenStart = 0;
        int currentWSStart = 0;
        TextBlock currentLine = new TextBlock();
        int currentLineLength = 0;
        ArrayList<TextBlock> output = new ArrayList<>();

        while (index < textAndAttributes.size()) {
            index = skipWhiteSpace(index);
            currentTokenStart = index;
            index = skipNonWhiteSpace(index);
            currentWSStart = index;

            // at this point we have indices flanking the token - strip out the token and associated colours and append
            // them to the current line.
            for (int i = currentTokenStart; i < currentWSStart; i++) {
                currentLineLength = currentLine.length();
                if (currentLineLength + currentWSStart - currentTokenStart > width) {
                    output.add(currentLine);
                    currentLine = new TextBlock();
                }
                currentLine.append(textAndAttributes.get(i));
                currentLine.append(' ', AttributeColour.COLOUR_WHITE);
            }
        }

        if (currentLine.length() > 0)
            output.add(currentLine);

        return output;
    }

    /**
     * Appending a string and an array of colours
     *
     * @param string  The string we are appending to this text block
     * @param colours The colours we are using to append, linked to the characters in the string
     */
    public void append(@NonNull String string, @NonNull List<AttributeColour> colours) {
        if (string.length() != colours.size()) {
            logger.error("Invalid string/list input to TextBlock creator. String was of length " + string.length() +
                    " and colours array was of length " + colours.size());
            throw new IllegalArgumentException("Invalid string/list input to TextBlock creator. String was of length "
                    + string.length() + " and colours array was of length " + colours.size());
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
    public void toFile(AngFile angFile, int indent, int wrapAt) {
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
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        for (AngbandDisplayCharacter ch : textAndAttributes)
            output.append(ch.getCharacter());

        return output.toString();
    }
}