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
 * The categories an object property can belong to — a stat boost, a modifier, a
 * flag, an ignore, a resistance, a vulnerability or an immunity. Mirrors the C
 * original's {@code OBJ_PROPERTY_*} types ({@code enum obj_property_type} in
 * {@code src/obj-properties.h}). {@code OBJ_PROPERTY_MAX} is the count sentinel
 * and {@code OBJ_PROPERTY_NONE} the zero placeholder, neither of which a data
 * file can name.
 *
 * <p>The string beside each constant is the exact {@code type:} token used in
 * {@code object_property.txt}, matched by {@link #fromValue}. These deliberately
 * mirror C's {@code parse_object_property_type} ({@code obj-init.c}), which
 * {@code streq}s the full words — hence {@code "resistance"}/{@code "vulnerability"}/
 * {@code "immunity"} in full rather than abbreviations.
 *
 * <p>The category also decides which table a property's {@code code:} resolves
 * against: {@code STAT} and {@code MOD} both index {@code obj_mods}, {@code FLAG}
 * indexes {@code obj_flags}, and {@code IGNORE}/{@code RESIST}/{@code VULN}/
 * {@code IMM} all index {@code element_names}.
 *
 * @author Rowan Crowther
 */
public enum ObjPropertyType {
    OBJ_PROPERTY_NONE(""),
    OBJ_PROPERTY_STAT("stat"),
    OBJ_PROPERTY_MOD("mod"),
    OBJ_PROPERTY_FLAG("flag"),
    OBJ_PROPERTY_IGNORE("ignore"),
    OBJ_PROPERTY_RESIST("resistance"),
    OBJ_PROPERTY_VULN("vulnerability"),
    OBJ_PROPERTY_IMM("immunity"),
    OBJ_PROPERTY_MAX("max");

    /**
     * The {@code type:} token this constant is written as in the data file.
     *
     * @author Rowan Crowther
     */
    private final String value;

    /**
     * Bind a category to its data-file token.
     *
     * @param value the {@code type:} token
     * @author Rowan Crowther
     */
    ObjPropertyType(String value) {
        this.value = value;
    }

    /**
     * @return the {@code type:} token for this category
     * @author Rowan Crowther
     */
    private String getValue() {
        return value;
    }

    /**
     * Resolve a data-file {@code type:} token to its category. Case-sensitive and
     * exact, mirroring C's {@code streq} dispatch.
     *
     * @param value the token from {@code object_property.txt}
     * @return the matching category, or {@code null} if the token is unrecognised
     * (the caller reports it as a soft error, as C returns
     * {@code PARSE_ERROR_INVALID_PROPERTY})
     * @author Rowan Crowther
     */
    public static ObjPropertyType fromValue(String value) {
        return Arrays.stream(values())
                .filter(op -> op.getValue().equals(value))
                .findFirst().orElse(null);
    }
}
