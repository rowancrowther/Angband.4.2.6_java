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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.cave.profilers.dungeon.CaveProfile;

import java.util.List;

/**
 * Holds the level-generation data loaded from {@code lib/gamedata} for the rest of the game to
 * read — the port's home for what C keeps in the file-scope globals of {@code generate.c}.
 *
 * <p>Populated once at start-up by {@link uk.co.jackoftrades.middle.game.globals.loaders.DungeonLoader}.
 *
 * @author Rowan Crowther
 */
public class DungeonRegistry {
    private static final Logger logger = LogManager.getLogger(DungeonRegistry.class);

    /**
     * Every level style the game may build, in file order — C's {@code cave_profiles} array.
     */
    private static List<CaveProfile> caveProfiles;

    /**
     * Install the level styles read from {@code dungeon_profile.txt}.
     *
     * <p>Replaces whatever was held before, so calling this twice discards the first set.
     *
     * @param caveProfiles the profiles to install, in file order
     * @author Rowan Crowther
     */
    public static void setCaveProfiles(List<CaveProfile> caveProfiles) {
        DungeonRegistry.caveProfiles = caveProfiles;
    }
}
