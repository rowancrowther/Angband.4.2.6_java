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

package uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;

public class HelperFunctions {
    private static final Logger logger = LogManager.getLogger(HelperFunctions.class);

    static String getDummyParamName(int index) {
        // index is never used in the dummy param name function
        return "";
    }

    static int getDummyParamCount() {
        return 1;
    }

    static String getElementName(int index) {
        if (index < 1 || index >= ElementEnum.values().length - 1) {
            logger.error("Index out of bounds for element name in UI Entry parsing!");
            throw new IndexOutOfBoundsException("Index out of bounds for element name in UI Entry parsing!");
        }

        return ElementEnum.values()[index].name();
    }

    static int getElementCount() {
        return ElementEnum.values().length - 1;
    }

    static String getStatName(int index) {
        if (index < 1 || index >= 6) {
            logger.error("Index out of bounds for stat name in UI Entry parsing!");
            throw new IndexOutOfBoundsException("Index out of bounds for stat name in UI Entry parsing!");
        }

        return "STR";
    }

    static int getStatCount() {
        return 5;
    }

    static int getDummyPriority(int index) {
        // index is never used in the dummy priority function
        return 0;
    }

    static int getPriorityFromIndex(int index) {
        return index;
    }

    static int getPriorityFromNegativeIndex(int index) {
        return -index;
    }
}
