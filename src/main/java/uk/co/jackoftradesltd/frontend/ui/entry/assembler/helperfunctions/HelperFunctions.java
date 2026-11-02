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
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;

/**
 * Static holders for the small lookup functions {@link uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryNameParameter}
 * and {@link uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions.UIEntryPriorityScheme} wrap, the Java form of the
 * {@code count_func}/{@code ith_name_func} pairs in C's {@code name_parameters[]} and the
 * {@code priority} function in C's {@code priority_schemes[]}, both in {@code ui-entry.c}.
 * Where C looks these functions up through a table of function pointers by matching a
 * {@code parameter:}/{@code priority:} directive's string against each scheme's {@code name}, the
 * port's two enums hold the equivalent method references directly as constructor arguments, so this
 * class only needs to supply the functions themselves.
 *
 * <p>Every lookup is 0-based and bounded exactly as in C. {@link ElementEnum} carries two
 * placeholders C's {@code element_names[]} does not, {@code ELEM_NONE} first and {@code ELEM_MAX}
 * last, so {@link #getElementCount()} subtracts both from {@link ElementEnum#values()}{@code .length}
 * to give C's 25, and {@link #getElementName(int)} adds one to its index to skip {@code ELEM_NONE}
 * internally. The caller never sees the shift: index {@code 0} is {@code ACID} here as in C.
 * {@link #getStatName(int)}/{@link #getStatCount()} need no adjustment at all.
 *
 * <p>C guards each index with an {@code assert}, which is compiled out of a release build; here an
 * out-of-range index always throws, so a bad {@code parameter:} value cannot pass unnoticed.
 *
 * <p>Class HelperFunctions coded before 260920, commented in full on 261102.
 *
 * @author Rowan Crowther
 */
public class HelperFunctions {
    /**
     * The five stat short names in C's {@code list-stats.h} order - STR, INT, WIS, DEX, CON - the
     * Java form of C's {@code stat_names[]} in {@code ui-entry.c}. {@link #getStatName(int)} reads
     * it, and {@code UIEntryAssembler} iterates it directly when expanding a stat-parameterized
     * record. Not to be confused with C's {@code stat_names[]} in {@code ui-display.c}, which holds
     * the {@code "STR: "}-style display abbreviations.
     *
     * <p>Field stats coded before 260920, commented in full on 261102.
     */
    public static final String [] stats = {"STR", "INT", "WIS", "DEX", "CON"};
    
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
     * Returns the empty string for index {@code 0}, the Java form of C's
     * {@code get_dummy_param_name} in {@code ui-entry.c}, used as the {@code ith_name_func} for
     * {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE} - the scheme selected when a record has
     * no {@code parameter:} directive at all, so there is no per-index name to produce.
     *
     * <p>C {@code assert}s {@code i == 0}, which a release build drops, so C returns {@code ""} for
     * any index there. Here any other index is logged at fatal level and throws, matching the debug
     * build. It is unreachable in practice, since {@link #getDummyParamCount()} is {@code 1}.
     *
     * <p>Function getDummyParamName coded before 260920, commented in full on 261102.
     *
     * @param index the expansion index, which must be {@code 0}
     * @return the empty string
     * @throws DataFileParseException if {@code index} is not {@code 0}
     */
    static String getDummyParamName(int index) {
        if (index != 0) {
            String message = "HelperFunctions.getDummyParamName called with an illegal index value: " + index;
            logger.fatal(message);
            throw new DataFileParseException(message);
        }
        return "";
    }

    /**
     * Returns {@code 1}, the Java form of C's {@code get_dummy_param_count} in {@code ui-entry.c},
     * used as the {@code count_func} for {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE} - an
     * unparameterized record still "expands" into exactly one entry.
     *
     * <p>Function getDummyParamCount coded before 260920, commented in full on 261102.
     *
     * @return {@code 1}
     */
    static int getDummyParamCount() {
        return 1;
    }

    /**
     * Returns the name of the {@code index}th real element without its {@code ELEM_} prefix - for
     * example {@code "ACID"} or {@code "HOLY_ORB"} - the Java form of C's {@code get_element_name} in
     * {@code ui-entry.c}, used as the {@code ith_name_func} for
     * {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT}. Valid indices are
     * {@code 0..getElementCount() - 1}, the same range as C: index {@code 0} is
     * {@link ElementEnum#ELEM_ACID} and index {@code 24} is {@link ElementEnum#ELEM_ARROW}. The
     * method reads {@code ElementEnum.values()[index + 1]} to step over {@link ElementEnum#ELEM_NONE}.
     * C's table holds the bare token ({@code #x} on the {@code ELEM(x)} macro), so the prefix is
     * stripped to match.
     *
     * <p>C {@code assert}s the bound, which a release build drops; here an out-of-range index is
     * logged and throws.
     *
     * <p>Function getElementName coded before 260920, commented in full on 261102.
     *
     * @param index the element's 0-based position, {@code 0} ({@code ACID}) through {@code 24}
     *              ({@code ARROW})
     * @return the matching element's name with the {@code ELEM_} prefix removed
     * @throws IndexOutOfBoundsException if {@code index} is outside {@code 0..getElementCount() - 1}
     */
    static String getElementName(int index) {
        if (index < 0 || index >= ElementEnum.values().length - 2) {
            logger.error("Index out of bounds for element name in UI Entry parsing!");
            throw new IndexOutOfBoundsException("Index out of bounds for element name in UI Entry parsing!");
        }

        return ElementEnum.values()[index + 1].name().substring(5);
    }

    /**
     * Returns the number of valid indices {@link #getElementName(int)} accepts, the Java form of
     * C's {@code get_element_count} in {@code ui-entry.c}, used as the {@code count_func} for
     * {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT}. {@link ElementEnum} carries two
     * placeholders C's {@code element_names[]} does not - {@code ELEM_NONE} and {@code ELEM_MAX} -
     * so {@link ElementEnum#values()}{@code .length} is two higher than C's own count; subtracting
     * both is what lands this on the same 25 {@code get_element_count} returns.
     *
     * <p>Function getElementCount coded before 260920, commented in full on 261102.
     *
     * @return the number of real elements {@link ElementEnum} carries, the same figure C's
     * {@code get_element_count} returns
     */
    static int getElementCount() {
        return ElementEnum.values().length - 2;
    }

    /**
     * Returns the name of the stat at {@code index} - {@code "STR"}, {@code "INT"}, {@code "WIS"},
     * {@code "DEX"} or {@code "CON"} - the Java form of C's
     * {@code get_stat_name} in {@code ui-entry.c}, used as the
     * {@code ith_name_func} for {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT}. Valid
     * indices are the plain {@code 0..4} C's own {@code stat_names[]} uses, read straight from
     * {@link #stats}. C {@code assert}s the bound, which a release build drops; here an
     * out-of-range index is logged and throws.
     *
     * <p>Function getStatName coded before 260920, commented in full on 261102.
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

        return stats[index];
    }

    /**
     * Returns {@code 5}, the Java form of C's {@code get_stat_count} in {@code ui-entry.c}, used as
     * the {@code count_func} for {@code UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT}.
     *
     * <p>Function getStatCount coded before 260920, commented in full on 261102.
     *
     * @return {@code 5}, the number of player stats
     */
    static int getStatCount() {
        return 5;
    }

    /**
     * Returns {@code 0} regardless of {@code index}, the Java form of C's {@code get_dummy_priority}
     * in {@code ui-entry.c}, used as the {@code priority} function for
     * {@code UIEntryPriorityScheme.PRIORITY_SCHEME_NONE} - the scheme selected when a record has no
     * {@code priority:} directive, or one giving a literal number rather than a scheme name.
     *
     * <p>Function getDummyPriority coded before 260920, commented in full on 261102.
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
     * in {@code ui-entry.c}, used as the {@code priority} function for
     * {@code UIEntryPriorityScheme.PRIORITY_SCHEME_INDEX} - a {@code priority:index} directive, which
     * ranks a parameterized entry's rows in the same order as the parameter they were generated from.
     *
     * <p>Function getPriorityFromIndex coded before 260920, commented in full on 261102.
     *
     * @param index the parameter index to convert to a priority
     * @return {@code index}
     */
    static int getPriorityFromIndex(int index) {
        return index;
    }

    /**
     * Returns the negation of {@code index}, the Java form of C's
     * {@code get_priority_from_negative_index} in {@code ui-entry.c}, used as the
     * {@code priority} function for {@code UIEntryPriorityScheme.PRIORITY_SCHEME_NEGATIVE_INDEX} - a
     * {@code priority:negative_index} directive, which ranks a parameterized entry's rows in the
     * reverse of the order they were generated in.
     *
     * <p>Function getPriorityFromNegativeIndex coded before 260920, commented in full on 261102.
     *
     * @param index the parameter index to convert to a priority
     * @return {@code -index}
     */
    static int getPriorityFromNegativeIndex(int index) {
        return -index;
    }
}
