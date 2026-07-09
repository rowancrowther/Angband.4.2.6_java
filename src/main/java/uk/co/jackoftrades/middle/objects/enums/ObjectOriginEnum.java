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

/**
 * Where an object came from, used to build the "history" line in an item's
 * description (found on the floor, dropped by a monster, bought, an inheritance,
 * …). Each constant carries how many {@code %s} parameters its template needs and
 * the template itself. Mirrors the C original's {@code ORIGIN_*} list
 * ({@code src/list-origins.h}); the constants are self-describing and documented
 * collectively here.
 *
 * @author Rowan Crowther
 */
public enum ObjectOriginEnum {
    ORIGIN_NONE(-1, ""),
    ORIGIN_FLOOR(1, "Found lying on the floor %s"),
    ORIGIN_CHEST(1, "Taken from a chest found %s"),
    ORIGIN_SPECIAL(1, "Found lying on the floor of a special room %s"),
    ORIGIN_PIT(1, "Found lying on the floor in a pit %s"),
    ORIGIN_VAULT(1, "Found lying on the floor in a vault %s"),
    ORIGIN_LABYRINTH(1, "Found lying on the floor of a labyrinth %s"),
    ORIGIN_CAVERN(1, "Found lying on the floor in a cavern %s"),
    ORIGIN_RUBBLE(1, "Found under some rubble %s"),
    ORIGIN_MIXED(-1, ""),
    ORIGIN_DROP(2, "Dropped by %s %s"),
    ORIGIN_DROP_SPECIAL(2, "Dropped by %s %s"),
    ORIGIN_DROP_PIT(2, "Dropped by %s %s"),
    ORIGIN_DROP_VAULT(2, "Dropped by %s %s"),
    ORIGIN_STATS(-1, ""),
    ORIGIN_ACQUIRE(1, "Conjured forth by magic %s"),
    ORIGIN_STORE(0, "Bought from a store"),
    ORIGIN_STOLEN(-1, ""),
    ORIGIN_BIRTH(0, "An inheritance from your family"),
    ORIGIN_CHEAT(0, "Created by debug option"),
    ORIGIN_DROP_BREED(2, "Dropped by %s %s"),
    ORIGIN_DROP_SUMMON(2, "Dropped by %s %s"),
    ORIGIN_DROP_UNKNOWN(1, "Dropped by an unknown monster %s"),
    ORIGIN_DROP_POLY(2, "Dropped by %s %s"),
    ORIGIN_DROP_MIMIC(2, "Dropped by %s %s"),
    ORIGIN_DROP_WIZARD(2, "Dropped by %s %s");

    /**
     * Number of {@code %s} parameters the {@link #name} template expects
     * ({@code -1} for origins with no displayed history line).
     *
     * @author Rowan Crowther
     */
    private final int numParameters;
    /**
     * The history-line template for this origin (with {@code %s} placeholders).
     *
     * @author Rowan Crowther
     */
    private final String name;

    /**
     * Bind an origin to its parameter count and history template.
     *
     * @param numParameters number of template parameters
     * @param name          the history-line template
     * @author Rowan Crowther
     */
    ObjectOriginEnum(int numParameters, String name) {
        this.numParameters = numParameters;
        this.name = name;
    }
}
