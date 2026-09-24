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

/**
 * The automatic-priority schemes a {@code priority:} directive can select instead of a literal
 * number, one constant per row of C's {@code priority_schemes[]} table
 * ({@code [C] ui-entry.c:157-161}), matched by the directive's raw text against {@link #getName()}.
 * C's table holds one {@code priority} function pointer per row; this port holds the equivalent
 * {@link HelperFunctions} method reference directly as a constructor argument, so no separate
 * lookup-by-name step is needed once a {@code priority:} string has been matched to its constant.
 *
 * <p>Class UIEntryPriorityScheme coded before 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
public enum UIEntryPriorityScheme {
    /**
     * A literal priority number, or none at all - the Java form of C's {@code priority_schemes[0]}
     * ({@code [C] ui-entry.c:158}: {@code { "", get_dummy_priority }}), whose {@code priority}
     * function always returns {@code 0} regardless of the parameter index.
     */
    PRIORITY_SCHEME_NONE("", HelperFunctions::getDummyPriority),
    /**
     * {@code priority:index} - the Java form of C's {@code priority_schemes[1]}
     * ({@code [C] ui-entry.c:159}: {@code { "index", get_priority_from_index }}), which ranks a
     * parameterised entry's rows in the same order as the parameter they were generated from.
     */
    PRIORITY_SCHEME_INDEX("index", HelperFunctions::getPriorityFromIndex),
    /**
     * {@code priority:negative_index} - the Java form of C's {@code priority_schemes[2]}
     * ({@code [C] ui-entry.c:160}: {@code { "negative_index", get_priority_from_negative_index }}),
     * which ranks a parameterised entry's rows in the reverse of the order they were generated in.
     */
    PRIORITY_SCHEME_NEGATIVE_INDEX("negative_index", HelperFunctions::getPriorityFromNegativeIndex);

    /**
     * The raw {@code priority:} text this constant matches. The Java form of C's
     * {@code ui_entry_priority_scheme.name} ({@code [C] ui-entry.c:149}).
     *
     * <p>Field name coded before 260924, commented in full on 260924.
     */
    private final String name;
    /**
     * Resolves a parameter index to the priority this scheme assigns it. The Java form of C's
     * {@code priority} function pointer ({@code [C] ui-entry.c:150}).
     *
     * <p>Field priorityResolver coded before 260924, commented in full on 260924.
     */
    private final Function<Integer, Integer> priorityResolver;

    /**
     * Build a priority scheme from its {@code priority:} text and its priority resolver. The Java
     * form of one row of C's {@code priority_schemes[]} table ({@code [C] ui-entry.c:157-161}).
     *
     * <p>Function UIEntryPriorityScheme coded before 260924, commented in full on 260924.
     *
     * @param name             the raw {@code priority:} text this constant matches
     * @param priorityResolver resolves a parameter index to the priority this scheme assigns it
     */
    UIEntryPriorityScheme(String name, Function<Integer, Integer> priorityResolver) {
        this.name = name;
        this.priorityResolver = priorityResolver;
    }

    /**
     * Returns the raw {@code priority:} text this constant matches.
     *
     * <p>Function getName coded before 260924, commented in full on 260924.
     *
     * @return this scheme's {@code priority:} text
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the priority this scheme assigns to the value at {@code index}. Corresponds to calling
     * C's {@code priority} function with {@code index} ({@code [C] ui-entry.c:150}).
     *
     * <p>Function getPriority coded before 260924, commented in full on 260924.
     *
     * @param index the parameter index to convert to a priority
     * @return the priority this scheme assigns to {@code index}
     */
    public int getPriority(int index) {
        return priorityResolver.apply(index);
    }
}
