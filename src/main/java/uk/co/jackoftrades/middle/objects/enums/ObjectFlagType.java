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
 * The grouping of object flags by purpose — sustains, protections, light, melee,
 * "bad" (negative) flags, digging, throwing, curse-only, etc. Used to organise
 * flags in displays. Mirrors the C original's {@code OFT_*} flag types
 * ({@code enum object_flag_type} in {@code src/obj-properties.h}). {@code OFT_MAX}
 * is the count sentinel and {@code OFT_NONE} the zero placeholder.
 *
 * <p>The string beside each constant is the exact {@code subtype:} token used in
 * {@code object_property.txt}, matched by {@link #getFlagTypeFromSubtype}; these
 * mirror the {@code streq} chain in C's {@code parse_object_property_subtype}
 * ({@code obj-init.c}). Note that three tokens do not transliterate from the
 * constant name — {@code sustain}/{@code protection}/{@code misc ability} map to
 * {@code OFT_SUST}/{@code OFT_PROT}/{@code OFT_MISC} — so the mapping has to be
 * spelled out rather than derived.
 *
 * <p>{@code subtype:} is only ever set on {@code type:flag} properties; the empty
 * string maps to {@link #OFT_NONE}, which is what a property with no {@code subtype:}
 * line receives (matching C's zero-initialised default).
 *
 * @author Rowan Crowther
 */
public enum ObjectFlagType {
    OFT_NONE(""),
    OFT_SUST("sustain"),
    OFT_PROT("protection"),
    OFT_MISC("misc ability"),
    OFT_LIGHT("light"),
    OFT_MELEE("melee"),
    OFT_BAD("bad"),
    OFT_DIG("dig"),
    OFT_THROW("throw"),
    OFT_CURSE_ONLY("curse-only"),
    OFT_MAX("");

    /**
     * The {@code subtype:} token this constant is written as in the data file.
     * Empty for the two non-data placeholders ({@link #OFT_NONE}, {@link #OFT_MAX}).
     *
     * @author Rowan Crowther
     */
    private final String subtypeText;

    /**
     * Bind a flag grouping to its data-file token.
     *
     * @param subtypeText the {@code subtype:} token
     * @author Rowan Crowther
     */
    ObjectFlagType(String subtypeText) {
        this.subtypeText = subtypeText;
    }

    /**
     * @return the {@code subtype:} token for this grouping
     * @author Rowan Crowther
     */
    public String getSubtypeText() {
        return subtypeText;
    }

    /**
     * Resolve a data-file {@code subtype:} token to its grouping. Case-sensitive and
     * exact, mirroring C's {@code streq} dispatch.
     *
     * <p>The empty string resolves to {@link #OFT_NONE} (the no-{@code subtype:}
     * case) because {@code OFT_NONE} is declared before {@code OFT_MAX}, the other
     * constant carrying an empty token — first match wins.
     *
     * @param subtype the token from {@code object_property.txt} (or {@code ""} when
     *                the record has no {@code subtype:} line)
     * @return the matching grouping, or {@code null} if the token is unrecognised
     * (C returns {@code PARSE_ERROR_INVALID_SUBTYPE})
     * @author Rowan Crowther
     */
    public static ObjectFlagType getFlagTypeFromSubtype(String subtype) {
        return Arrays.stream(values())
                .filter(o -> o.getSubtypeText().equals(subtype))
                .findFirst().orElse(null);
    }
}
