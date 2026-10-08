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
 * The grouping of object flags by purpose — sustains, protections, light, melee,
 * "bad" (negative) flags, digging, throwing, curse-only, etc. Used to organize
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
 * line receives (matching C's zero-initialized default).
 *
 * <p>The grouping is stored in {@code ObjectProperty}'s {@code subtype} (C's
 * {@code obj_property.subtype}). C's knowledge code skips {@link #OFT_NONE},
 * {@link #OFT_LIGHT}, {@link #OFT_DIG}, {@link #OFT_THROW} and {@link #OFT_CURSE_ONLY}
 * when it works out which flags a new character already knows, and passes
 * {@link #OFT_MAX} to {@code create_obj_flag_mask} to mean "every grouping".
 *
 * <p>coded on 2026-10-08 / commented in full on 2026-10-08
 *
 * @author Rowan Crowther
 */
public enum ObjectFlagType {
    /** Placeholder: a property with no {@code subtype:} line. C's {@code OFT_NONE = 0}. */
    OFT_NONE(""),
    /** Sustains a stat. */
    OFT_SUST("sustain"),
    /** Protection from an effect. */
    OFT_PROT("protection"),
    /** A good property, suitable for ego items. */
    OFT_MISC("misc ability"),
    /** Applicable only to light sources. */
    OFT_LIGHT("light"),
    /** Applicable only to melee weapons. */
    OFT_MELEE("melee"),
    /** An undesirable flag. */
    OFT_BAD("bad"),
    /** Applicable only to diggers. */
    OFT_DIG("dig"),
    /** Applicable only to throwables. */
    OFT_THROW("throw"),
    /** Only relevant as part of a curse. */
    OFT_CURSE_ONLY("curse-only"),
    /**
     * Count sentinel, not a real grouping; its empty token is never reached by
     * {@link #getFlagTypeFromSubtype} because {@link #OFT_NONE} matches first.
     */
    OFT_MAX("");

    /**
     * The {@code subtype:} token this constant is written as in the data file.
     * Empty for the two non-data placeholders ({@link #OFT_NONE}, {@link #OFT_MAX}).
     * C has no counterpart: the tokens live only in the {@code streq} chain of
     * {@code parse_object_property_subtype} ({@code obj-init.c}).
     *
     * <p>coded on 2026-10-08 / commented in full on 2026-10-08
     */
    private final String subtypeText;

    /**
     * Bind a flag grouping to its data-file token. Java-only: C's enum carries no
     * payload.
     *
     * <p>coded on 2026-10-08 / commented in full on 2026-10-08
     *
     * @param subtypeText the {@code subtype:} token
     */
    ObjectFlagType(String subtypeText) {
        this.subtypeText = subtypeText;
    }

    /**
     * Resolve a data-file {@code subtype:} token to its grouping. Case-sensitive and
     * exact, mirroring C's {@code streq} dispatch.
     *
     * <p>The empty string resolves to {@link #OFT_NONE} (the no-{@code subtype:}
     * case) because {@code OFT_NONE} is declared before {@code OFT_MAX}, the other
     * constant carrying an empty token — first match wins. Any other spelling,
     * including a different case or surrounding whitespace, is unrecognized.
     *
     * <p>Replaces the {@code streq} chain in C's {@code parse_object_property_subtype}
     * ({@code obj-init.c}). C writes the result straight into the record and returns
     * the error; this returns {@code null} and leaves the error to the caller
     * ({@code ObjectPropertyAssembler}).
     *
     * <p>coded on 2026-10-08 / commented in full on 2026-10-08
     *
     * @param subtype the token from {@code object_property.txt} (or {@code ""} when
     *                the record has no {@code subtype:} line)
     * @return the matching grouping, or {@code null} if the token is unrecognized
     * (C returns {@code PARSE_ERROR_INVALID_SUBTYPE})
     */
    public static ObjectFlagType getFlagTypeFromSubtype(String subtype) {
        return Arrays.stream(values())
                .filter(o -> o.getSubtypeText().equals(subtype))
                .findFirst().orElse(null);
    }

    /**
     * The {@code subtype:} token for this grouping, the inverse of
     * {@link #getFlagTypeFromSubtype}. Empty for {@link #OFT_NONE} and {@link #OFT_MAX}.
     *
     * <p>coded on 2026-10-08 / commented in full on 2026-10-08
     *
     * @return the {@code subtype:} token for this grouping
     */
    public String getSubtypeText() {
        return subtypeText;
    }
}
