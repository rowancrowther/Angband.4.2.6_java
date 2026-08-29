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

package uk.co.jackoftrades.middle.strings;

public enum MessageTag {
    MSG_TAG_NONE(1),
    MSG_TAG_NAME(5),
    MSG_TAG_KIND(5),
    MSG_TAG_VERB(2),
    MSG_TAG_VERB_IS(3);

    private final int size;

    MessageTag(int size) {
        this.size = size;
    }

    public static MessageTag getTag(String tag) {
        if (tag.startsWith("name}"))
            return MSG_TAG_NAME;
        if (tag.startsWith("kind}"))
            return MSG_TAG_KIND;
        if (tag.startsWith("s}"))
            return MSG_TAG_VERB;
        if (tag.startsWith("is}"))
            return MSG_TAG_VERB_IS;

        return MSG_TAG_NONE;
    }

    public int getSize() {
        return size;
    }
}
