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

package uk.co.jackoftrades.middle.objects.enums;

import java.util.Arrays;

/**
 * How an object flag becomes known to the player — never, by normal use, after a
 * timed delay, or immediately on wielding. Mirrors the C original's
 * {@code OFID_*} identification categories ({@code enum object_flag_id} in
 * {@code src/obj-properties.h}). {@code OFID_NONE} is the zero placeholder a data
 * file cannot name; unlike {@link ObjectFlagType} there is no {@code OFID_MAX}.
 *
 * <p>The string beside each constant is the exact {@code id-type:} token used in
 * {@code object_property.txt}, matched by {@link #getFlagID}; these mirror the
 * {@code streq} chain in C's {@code parse_object_property_id_type}
 * ({@code obj-init.c}). {@code on effect} → {@link #OFID_NORMAL} is the one
 * mapping that shares nothing with the constant name, so it cannot be derived.
 * {@code id-type:} is only ever set on {@code type:flag} properties.
 *
 * @author Rowan Crowther
 */
public enum ObjectFlagID {
    /**
     * The flag is never identified this way. @author Rowan Crowther
     */
    OFID_NONE(""),
    /**
     * Identified through normal use. @author Rowan Crowther
     */
    OFID_NORMAL("on effect"),
    /** Identified after a timed delay of use. @author Rowan Crowther */
    OFID_TIMED("timed"),
    /** Identified immediately on wielding. @author Rowan Crowther */
    OFID_WIELD("on wield");

    /**
     * The {@code id-type:} token this constant is written as in the data file.
     *
     * @author Rowan Crowther
     */
    private final String idMethod;

    /**
     * Bind an identification category to its data-file token.
     *
     * @param idMethod the {@code id-type:} token
     * @author Rowan Crowther
     */
    private ObjectFlagID(String idMethod) {
        this.idMethod = idMethod;
    }

    /**
     * @return the {@code id-type:} token for this category
     * @author Rowan Crowther
     */
    public String getIdMethod() {
        return this.idMethod;
    }

    /**
     * Resolve a data-file {@code id-type:} token to its category. Case-sensitive and
     * exact, mirroring C's {@code streq} dispatch.
     *
     * @param idMethod the token from {@code object_property.txt}
     * @return the matching category, or {@code null} if the token is unrecognised
     * (C returns {@code PARSE_ERROR_INVALID_ID_TYPE})
     * @author Rowan Crowther
     */
    public static ObjectFlagID getFlagID(String idMethod) {
        return Arrays.stream(values()).filter(o -> o.getIdMethod().equals(idMethod))
                .findFirst().orElse(null);
    }
}
