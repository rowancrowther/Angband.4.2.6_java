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

package uk.co.jackoftradesltd.middle.objects.enums;

import java.util.Arrays;

/**
 * How an object flag becomes known to the player — never, by normal use, after a
 * timed delay, or immediately on wielding. Mirrors the C original's
 * {@code OFID_*} identification categories ({@code enum object_flag_id} in
 * {@code src/obj-properties.h}). {@code OFID_NONE} is the zero placeholder; unlike
 * {@link ObjectFlagType} there is no {@code OFID_MAX}, and C's enum is not built from
 * an X-macro list, so the four values are simply spelled out in order
 * ({@code OFID_NONE = 0} up to {@code OFID_WIELD = 3}).
 *
 * <p>The string beside each constant is the exact {@code id-type:} token used in
 * {@code object_property.txt}, matched by {@link #getFlagID}; these mirror the
 * {@code streq} chain in C's {@code parse_object_property_id_type}
 * ({@code obj-init.c}). {@code on effect} → {@link #OFID_NORMAL} is the one
 * mapping that shares nothing with the constant name, so it cannot be derived.
 * {@code id-type:} is only ever set on {@code type:flag} properties.
 *
 * <p>The empty string maps to {@link #OFID_NONE}, which is what a property with no
 * {@code id-type:} line receives (matching C's zero-initialized
 * {@code obj_property.id_type}). A data file cannot name {@code OFID_NONE}
 * explicitly: an {@code id-type:} line with nothing after it is a syntax error here
 * and a missing-field error in C's parser, and C's chain has no branch for it.
 *
 * <p>The category is stored in {@code ObjectProperty}'s {@code idType} (C's
 * {@code obj_property.id_type}). C's {@code create_obj_flag_mask}
 * ({@code obj-properties.c}) selects flags by category when it is given a non-zero
 * {@code id}, and {@code obj-knowledge.c} asks for {@link #OFID_WIELD} (what a
 * wielded object makes obvious) and {@link #OFID_TIMED} (what is learned over time).
 *
 * <p>coded on 2026-10-08 / commented in full on 2026-10-14
 *
 * @author Rowan Crowther
 */
public enum ObjectFlagID {
    /**
     * Never shown: the flag is not identified by any of the three methods, and is the
     * value a property with no {@code id-type:} line receives. C's
     * {@code OFID_NONE = 0}. Its token is the empty string.
     */
    OFID_NONE(""),
    /**
     * Identified through normal use: the flag becomes known when its effect is noticed.
     * C's {@code OFID_NORMAL}; the data-file token is {@code on effect}.
     */
    OFID_NORMAL("on effect"),
    /**
     * Identified after a timed delay of use: the flag becomes obvious after a while.
     * C's {@code OFID_TIMED}; the data-file token is {@code timed}.
     */
    OFID_TIMED("timed"),
    /**
     * Identified immediately on wielding: the flag is obvious as soon as the object is
     * worn. C's {@code OFID_WIELD}; the data-file token is {@code on wield}.
     */
    OFID_WIELD("on wield");

    /**
     * The {@code id-type:} token this constant is written as in the data file. Empty for
     * {@link #OFID_NONE}.
     *
     * <p>Field idMethod coded on 2026-10-08, commented in full on 2026-10-14.
     */
    private final String idMethod;

    /**
     * Bind an identification category to its data-file token.
     *
     * <p>coded on 2026-10-08 / commented in full on 2026-10-14
     *
     * @param idMethod the {@code id-type:} token
     */
    private ObjectFlagID(String idMethod) {
        this.idMethod = idMethod;
    }

    /**
     * The {@code id-type:} token for this category, the inverse of {@link #getFlagID}.
     * Empty for {@link #OFID_NONE}.
     *
     * <p>coded on 2026-10-08 / commented in full on 2026-10-14
     *
     * @return the {@code id-type:} token for this category
     */
    public String getIdMethod() {
        return this.idMethod;
    }

    /**
     * Resolve a data-file {@code id-type:} token to its category. Case-sensitive and
     * exact, mirroring C's {@code streq} dispatch in {@code parse_object_property_id_type}
     * ({@code obj-init.c}): {@code "on effect"}, {@code "timed"} and {@code "on wield"}
     * give {@link #OFID_NORMAL}, {@link #OFID_TIMED} and {@link #OFID_WIELD}, and any
     * other text, including a different case or stray whitespace, is rejected. The empty
     * string gives {@link #OFID_NONE}, the default for a property with no
     * {@code id-type:} line; C never reaches its chain in that case. A {@code null}
     * token matches nothing and gives {@code null}.
     *
     * <p>coded on 2026-10-08 / commented in full on 2026-10-14
     *
     * @param idMethod the token from {@code object_property.txt}
     * @return the matching category, or {@code null} if the token is unrecognized
     * (C returns {@code PARSE_ERROR_INVALID_ID_TYPE})
     */
    public static ObjectFlagID getFlagID(String idMethod) {
        return Arrays.stream(values()).filter(o -> o.getIdMethod().equals(idMethod))
                .findFirst().orElse(null);
    }
}
