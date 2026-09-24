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
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.entrybase.assembler.UIEntryBaseParseRecord;

import java.util.List;

/**
 * Static holders for the small lookup functions {@link uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryNameParameter}
 * and {@link uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme} wrap, the Java form of the
 * {@code count_func}/{@code ith_name_func} pairs in C's {@code name_parameters[]} and the
 * {@code priority} function in C's {@code priority_schemes[]} ({@code [C] ui-entry.c:129-161}).
 * Where C looks these functions up through a table of function pointers by matching a
 * {@code parameter:}/{@code priority:} directive's string against each scheme's {@code name}, the
 * port's two enums hold the equivalent method references directly as constructor arguments, so this
 * class only needs to supply the functions themselves.
 *
 * <p>{@link #getElementName(int)}/{@link #getElementCount()} are offset by one relative to their C
 * counterparts: {@link ElementEnum} carries a leading {@code ELEM_NONE} placeholder C's
 * {@code element_names[]} does not, so a caller working in {@link ElementEnum}'s own ordinal space
 * (as {@code UIEntryNameParameter}'s generic {@code count}/{@code index} loop does) needs the count
 * and the valid index range shifted up by the same one place to land on the same elements C's
 * {@code get_element_count()}/{@code get_element_name(i)} do. {@link #getStatName(int)}/
 * {@link #getStatCount()} carry no such offset, since {@code PlayerEventStatusUpdate}'s
 * {@code statString} snapshot has no placeholder of its own.
 *
 * <p>Class HelperFunctions coded before 260920, commented in full on 260920.
 *
 * @author Rowan Crowther
 */
public class HelperFunctions {
    /**
     * Logger for the index-range failures the {@code get*Name} methods below report before throwing.
     * Has no C counterpart - the C functions this class ports only {@code assert} their bounds, which
     * is compiled out entirely in a release build - but a silent out-of-range lookup here would be
     * far harder to trace back to the bad {@code parameter:}/{@code priority:} value that caused it.
     *
     * <p>Field logger coded before 260920, commented in full on 260920.
     */
    private static final Logger logger = LogManager.getLogger(HelperFunctions.class);

    /**
     * Returns the empty string regardless of {@code index}, the Java form of C's
     * {@code get_dummy_param_name} ({@code [C] ui-entry.c:1572-1576}), used as the
     * {@code ith_name_func} for {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE} - the scheme
     * selected when a record has no {@code parameter:} directive at all, so there is no per-index
     * name to produce.
     *
     * <p>Function getDummyParamName coded before 260920, commented in full on 260920.
     *
     * @param index ignored
     * @return the empty string
     */
    static String getDummyParamName(int index) {
        // index is never used in the dummy param name function
        return "";
    }

    /**
     * Returns {@code 1}, the Java form of C's {@code get_dummy_param_count}
     * ({@code [C] ui-entry.c:1565-1568}), used as the {@code count_func} for
     * {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE} - an unparameterised record still
     * "expands" into exactly one entry.
     *
     * <p>Function getDummyParamCount coded before 260920, commented in full on 260920.
     *
     * @return {@code 1}
     */
    static int getDummyParamCount() {
        return 1;
    }

    /**
     * Returns the name of the {@link ElementEnum} constant at {@code index}, the Java form of C's
     * {@code get_element_name} ({@code [C] ui-entry.c:1594-1599}), used as the {@code ith_name_func}
     * for {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT}. Valid indices are
     * {@code 1..ElementEnum.values().length - 2} inclusive - {@link ElementEnum#ELEM_ACID} through
     * the constant before {@link ElementEnum#ELEM_MAX} - the one-higher range C's own
     * {@code 0..get_element_count() - 1} occupies, shifted up to skip {@link ElementEnum#ELEM_NONE}
     * (see the class Javadoc).
     *
     * <p>Function getElementName coded before 260920, commented in full on 260920.
     *
     * @param index the element's position in {@link ElementEnum#values()}
     * @return the matching element's enum constant name
     * @throws IndexOutOfBoundsException if {@code index} is outside the valid range
     */
    static String getElementName(int index) {
        if (index < 1 || index >= ElementEnum.values().length - 1) {
            logger.error("Index out of bounds for element name in UI Entry parsing!");
            throw new IndexOutOfBoundsException("Index out of bounds for element name in UI Entry parsing!");
        }

        return ElementEnum.values()[index].name();
    }

    /**
     * Returns the number of valid indices {@link #getElementName(int)} accepts, the Java form of
     * C's {@code get_element_count} ({@code [C] ui-entry.c:1588-1591}), used as the {@code count_func}
     * for {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT}. {@link ElementEnum} carries two
     * placeholders C's {@code element_names[]} does not — {@code ELEM_NONE} and {@code ELEM_MAX} —
     * so {@link ElementEnum#values()}{@code .length} is two higher than C's own count; subtracting
     * both back off (rather than the one {@link #getElementName(int)}'s shifted bounds subtract) is
     * what lands this on the same figure {@code get_element_count} returns.
     *
     * <p>Function getElementCount coded before 260920, commented in full on 260924.
     *
     * @return the number of real elements {@link ElementEnum} carries, the same figure C's
     * {@code get_element_count} returns
     */
    static int getElementCount() {
        return ElementEnum.values().length - 2;
    }

    /**
     * Returns the name of the stat at {@code index} - {@code "STR"}, {@code "INT"}, {@code "WIS"},
     * {@code "DEX"} or {@code "CON"} - from {@code PlayerEventStatusUpdate}'s {@code statString}
     * snapshot, the Java form of C's {@code get_stat_name} ({@code [C] ui-entry.c:1617-1623}), used
     * as the {@code ith_name_func} for {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT}. Unlike
     * {@link #getElementName(int)}, valid indices are the plain {@code 0..4} C's own
     * {@code stat_names[]} uses, since {@code statString} carries no leading placeholder to offset
     * for.
     *
     * <p>Function getStatName coded before 260920, commented in full on 260920.
     *
     * @param index the stat's position, {@code 0} ({@code STR}) through {@code 4} ({@code CON})
     * @return the matching stat's short name
     * @throws IndexOutOfBoundsException if {@code index} is outside {@code 0..4}
     */
    static String getStatName(int index) {
        if (index < 0 || index >= 5) {
            logger.error("Index out of bounds for stat name in UI Entry parsing!");
            throw new IndexOutOfBoundsException("Index out of bounds for stat name in UI Entry parsing!");
        }

        return PlayerEventStatusUpdate.getPlayerStatusView().statString()[index];
    }

    /**
     * Returns {@code 5}, the Java form of C's {@code get_stat_count} ({@code [C]
     * ui-entry.c:1610-1614}), used as the {@code count_func} for
     * {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT}.
     *
     * <p>Function getStatCount coded before 260920, commented in full on 260920.
     *
     * @return {@code 5}, the number of player stats
     */
    static int getStatCount() {
        return 5;
    }

    /**
     * Returns {@code 0} regardless of {@code index}, the Java form of C's {@code get_dummy_priority}
     * ({@code [C] ui-entry.c:1630-1633}), used as the {@code priority} function for
     * {@code UIEntryPriorityScheme.PRIORITY_SCHEME_NONE} - the scheme selected when a record has no
     * {@code priority:} directive, or one giving a literal number rather than a scheme name.
     *
     * <p>Function getDummyPriority coded before 260920, commented in full on 260920.
     *
     * @param index ignored
     * @return {@code 0}
     */
    static int getDummyPriority(int index) {
        // index is never used in the dummy priority function
        return 0;
    }

    /**
     * Returns {@code index} unchanged, the Java form of C's {@code get_priority_from_index}
     * ({@code [C] ui-entry.c:1640-1643}), used as the {@code priority} function for
     * {@code UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX} - a {@code priority:index} directive, which
     * ranks a parameterised entry's rows in the same order as the parameter they were generated from.
     *
     * <p>Function getPriorityFromIndex coded before 260920, commented in full on 260920.
     *
     * @param index the parameter index to convert to a priority
     * @return {@code index}
     */
    static int getPriorityFromIndex(int index) {
        return index;
    }

    /**
     * Returns the negation of {@code index}, the Java form of C's
     * {@code get_priority_from_negative_index} ({@code [C] ui-entry.c:1650-1653}), used as the
     * {@code priority} function for {@code UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX} - a
     * {@code priority:negative_index} directive, which ranks a parameterised entry's rows in the
     * reverse of the order they were generated in.
     *
     * <p>Function getPriorityFromNegativeIndex coded before 260920, commented in full on 260920.
     *
     * @param index the parameter index to convert to a priority
     * @return {@code -index}
     */
    static int getPriorityFromNegativeIndex(int index) {
        return -index;
    }
}
