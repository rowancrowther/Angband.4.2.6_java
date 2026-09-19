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

package uk.co.jackoftradesltd.channel.enums;

import java.util.Arrays;

/**
 * Whether a UI entry's parameter refers to a player stat or a damage
 * element. Mirrors the {@code "stat"} / {@code "element"} strings the data
 * file's {@code parameter:} field accepts, matched against C's
 * {@code name_parameters} table ({@code ui-entry.c}).
 *
 * <p>Class StatElemType coded before 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public enum StatElemType {
    /**
     * No parameter is set; the entry's value does not vary by stat or
     * element. Has no direct C counterpart of its own — C's dummy
     * {@code ""} entry in {@code name_parameters} exists to make an unset
     * {@code parameter:} field resolve to a valid index rather than to
     * carry a distinct meaning.
     *
     * <p>Constant NONE coded before 260916, commented in full on 260916.
     */
    NONE(""),
    /**
     * The parameter is a player stat (STR, INT, …).
     *
     * <p>Constant STAT coded before 260916, commented in full on 260916.
     */
    STAT("stat"),
    /**
     * The parameter is a damage element (fire, cold, …).
     *
     * <p>Constant ELEMENT coded before 260916, commented in full on 260916.
     */
    ELEMENT("element");

    private final String value;

    StatElemType(String value) {
        this.value = value;
    }

    /**
     * Resolves the data file's {@code parameter:} field text to a
     * {@link StatElemType} constant. Ports the linear scan in C's
     * {@code parse_entry_parameter} ({@code ui-entry.c}), which walks
     * {@code name_parameters[]} until the name matches; this returns
     * {@code null} where C reports {@code PARSE_ERROR_INVALID_VALUE}.
     *
     * <p>Function fromValue coded before 260916, commented in full on 260916.
     *
     * @param value the data-file parameter name ({@code "stat"}, {@code "element"}, or {@code ""})
     * @return the matching constant, or {@code null} if none matches
     */
    public static StatElemType fromValue(String value) {
        return Arrays.stream(StatElemType.values()).filter(s -> s.value.equals(value))
                .findFirst().orElse(null);
    }
}
