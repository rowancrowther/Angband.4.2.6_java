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

package uk.co.jackoftrades.frontend.screen.hooks;

import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;

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
