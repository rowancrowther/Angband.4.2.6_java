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

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The name-parameterisation schemes a {@code ui_entry.txt} record's {@code parameter:} directive can
 * select, one constant per row of C's {@code name_parameters[]} table
 * ({@code [C] ui-entry.c:142-146}), matched by the directive's raw text against {@link #getName()}.
 * C's table holds a {@code count_func}/{@code ith_name_func} function-pointer pair per row; this port
 * holds the equivalent {@link HelperFunctions} method references directly as constructor arguments,
 * so no separate lookup-by-name step is needed once a {@code parameter:} string has been matched to
 * its constant.
 *
 * <p>Class UIEntryNameParameter coded before 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
public enum UIEntryNameParameter {
    /**
     * No {@code parameter:} directive at all - the Java form of C's {@code name_parameters[0]}
     * ({@code [C] ui-entry.c:143}: {@code { "", get_dummy_param_count, get_dummy_param_name }}). An
     * unparameterised record still "expands" into exactly one entry, named as written.
     */
    ENTRY_NAME_PARAMETER_NONE("", HelperFunctions::getDummyParamCount,
            HelperFunctions::getDummyParamName),
    /**
     * {@code parameter:element} - the Java form of C's {@code name_parameters[1]}
     * ({@code [C] ui-entry.c:144}: {@code { "element", get_element_count, get_element_name }}). The
     * record expands once per resistible element.
     */
    ENTRY_NAME_PARAMETER_ELEMENT("element", HelperFunctions::getElementCount,
            HelperFunctions::getElementName),
    /**
     * {@code parameter:stat} - the Java form of C's {@code name_parameters[2]}
     * ({@code [C] ui-entry.c:145}: {@code { "stat", get_stat_count, get_stat_name }}). The record
     * expands once per player stat.
     */
    ENTRY_NAME_PARAMETER_STAT("stat", HelperFunctions::getStatCount,
            HelperFunctions::getStatName);

    /**
     * The raw {@code parameter:} text this constant matches. The Java form of C's
     * {@code ui_entry_name_parameter.name} ({@code [C] ui-entry.c:130}).
     *
     * <p>Field name coded before 260924, commented in full on 260924.
     */
    private final String name;
    /**
     * Resolves an expansion index to that value's name (e.g. an element or stat's short name). The
     * Java form of C's {@code ith_name_func} ({@code [C] ui-entry.c:132}).
     *
     * <p>Field nameResolver coded before 260924, commented in full on 260924.
     */
    private final Function<Integer, String> nameResolver;
    /**
     * Returns how many entries this scheme expands a record into. The Java form of C's
     * {@code count_func} ({@code [C] ui-entry.c:131}).
     *
     * <p>Field countResolver coded before 260924, commented in full on 260924.
     */
    private final Supplier<Integer> countResolver;

    /**
     * Build a name-parameterisation scheme from its {@code parameter:} text and its count/name
     * resolvers. The Java form of one row of C's {@code name_parameters[]} table
     * ({@code [C] ui-entry.c:142-146}).
     *
     * <p>Function UIEntryNameParameter coded before 260924, commented in full on 260924.
     *
     * @param s               the raw {@code parameter:} text this constant matches
     * @param dummyParamCount resolves how many entries this scheme expands a record into
     * @param dummyParamName  resolves an expansion index to that value's name
     */
    UIEntryNameParameter(String s, Supplier<Integer> dummyParamCount, Function<Integer, String> dummyParamName) {
        this.name = s;
        this.nameResolver = dummyParamName;
        this.countResolver = dummyParamCount;
    }

    /**
     * Returns the raw {@code parameter:} text this constant matches.
     *
     * <p>Function getName coded before 260924, commented in full on 260924.
     *
     * @return this scheme's {@code parameter:} text
     */
    public String getName() {
        return name;
    }

    /**
     * Returns how many entries this scheme expands a record into. Corresponds to calling C's
     * {@code count_func} with no arguments ({@code [C] ui-entry.c:131}).
     *
     * <p>Function getCount coded before 260924, commented in full on 260924.
     *
     * @return the number of entries a record using this scheme expands into
     */
    public int getCount() {
        return countResolver.get();
    }

    /**
     * Returns the name of the value at {@code index} within this scheme's expansion. Corresponds to
     * calling C's {@code ith_name_func} with {@code index} ({@code [C] ui-entry.c:132}).
     *
     * <p>Function getParamName coded before 260924, commented in full on 260924.
     *
     * @param index the expansion index to resolve
     * @return the name of the value at {@code index}
     */
    public String getParamName(int index) {
        return nameResolver.apply(index);
    }
}