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

package uk.co.jackoftradesltd.middle.strings;

/**
 * The tags that can appear in braces in a custom message, the port of C's {@code msg_tag_t}
 * ({@code obj-util.c}), as read by {@code ItemObject#printCustomMessage}.
 *
 * <p>{@link #MSG_TAG_NONE} stands for any tag that is not recognized; the message printer drops it.
 * A tag is recognized by its opening letters, not the whole word, which {@link #getTag} implements.
 * The constants are in C's order, with {@code MSG_TAG_NONE} first as it is there.
 *
 * <p>Enum MessageTag coded on 261003 / commented in full on 261009.
 */
public enum MessageTag {
    /**
     * Any tag that is not recognized. C's {@code switch} has no arm for it, so the message printer
     * writes nothing for it and resumes after the closing brace. Its size of 1 is a placeholder, as
     * it has no name to measure.
     *
     * <p>Constant MSG_TAG_NONE coded on 261003 / commented in full on 261009.
     */
    MSG_TAG_NONE(1),
    /**
     * {@code {name}}: the object's name with its quantity prefix, described under
     * {@code ODESC_PREFIX | ODESC_BASE} so with none of the extras that {@code ODESC_FULL} adds, or
     * {@code hands} with no object.
     *
     * <p>Constant MSG_TAG_NAME coded on 261003 / commented in full on 261009.
     */
    MSG_TAG_NAME(5),
    /**
     * {@code {kind}}: the kind's name alone, as {@code object_kind_name} gives it with
     * {@code easy_know} set, or {@code hands} with no object.
     *
     * <p>Constant MSG_TAG_KIND coded on 261003 / commented in full on 261009.
     */
    MSG_TAG_KIND(5),
    /**
     * {@code {s}}: the verb ending, {@code s} for a single object and nothing for a pile or with no
     * object.
     *
     * <p>Constant MSG_TAG_VERB coded on 261003 / commented in full on 261009.
     */
    MSG_TAG_VERB(2),
    /**
     * {@code {is}}: {@code is} for a single object, {@code are} for a pile or no object.
     *
     * <p>Constant MSG_TAG_VERB_IS coded on 261003 / commented in full on 261009.
     */
    MSG_TAG_VERB_IS(3);

    /**
     * The length of the whole braced tag, from the first letter to the closing brace, for the
     * whole-tag matching that used to skip by it. Nothing reads it now: the message printer skips
     * past the closing brace it found, as C does, because a tag matched by its prefix can be longer
     * than its name.
     *
     * <p>Field size coded on 261003 / commented in full on 261009.
     */
    private final int size;

    /**
     * Builds one constant with the length of its whole braced tag. C's {@code msg_tag_t} has no
     * such number; it is the Java port's own, kept for {@link #getSize}.
     *
     * <p>Constructor MessageTag coded on 261003 / commented in full on 261009.
     *
     * @param size the tag's length from its first letter to its closing brace
     */
    MessageTag(int size) {
        this.size = size;
    }

    /**
     * Looks a tag up by its opening letters, the port of C's {@code msg_tag_lookup}
     * ({@code obj-util.c}).
     *
     * <p>Tests {@code name}, then {@code kind}, then {@code s}, then {@code is}, in C's order, with
     * {@code startsWith} where C uses {@code strncmp} over the length of the name. So
     * {@code names} is {@link #MSG_TAG_NAME}, {@code sx} and {@code size} are {@link #MSG_TAG_VERB},
     * and {@code isn} is {@link #MSG_TAG_VERB_IS}, while {@code nam} and the empty string are
     * {@link #MSG_TAG_NONE}. Matching is case sensitive, as {@code strncmp} is.
     *
     * <p>The caller passes the letters only, with no closing brace, where C passes a pointer to the
     * rest of the whole message. The two agree because C's caller has already checked that the
     * letters run unbroken to a closing brace, so the brace that ends C's argument can never be
     * mistaken for a letter of a name, and a tag cut short by it fails in both versions.
     *
     * <p>Function getTag coded on 261003 / commented in full on 261009.
     *
     * @param tag the letters between the braces
     * @return the matching tag, or {@link #MSG_TAG_NONE}
     */
    public static MessageTag getTag(String tag) {
        if (tag.startsWith("name"))
            return MSG_TAG_NAME;
        if (tag.startsWith("kind"))
            return MSG_TAG_KIND;
        if (tag.startsWith("s"))
            return MSG_TAG_VERB;
        if (tag.startsWith("is"))
            return MSG_TAG_VERB_IS;

        return MSG_TAG_NONE;
    }

    /**
     * Returns the length of the whole braced tag. Not called anywhere now; see {@link #size}.
     *
     * <p>Method getSize coded on 261003 / commented in full on 261009.
     *
     * @return the tag's length from its first letter to its closing brace
     */
    public int getSize() {
        return size;
    }
}
