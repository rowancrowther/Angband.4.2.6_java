package uk.co.jackoftrades.frontend.screen.hooks;

import uk.co.jackoftrades.background.colour.enums.AttributeColour;

public interface TextOutHook {
    void output(AttributeColour attribute, String string);

    /**
     * Output the incoming string formatted with the objects in the given colour to this hook
     *
     * @param colour   the colour of this entire text string
     * @param toFormat the string to format
     * @param objects  the objects to format this string with
     */
    void out(AttributeColour colour, String toFormat, Object... objects);

    /**
     * Output the incoming string in COLOUR_WHITE, formatted with the objects to this hook
     *
     * @param toFormat the string to format
     * @param objects  the objects to format this string with
     */
    void out(String toFormat, Object... objects);
}
