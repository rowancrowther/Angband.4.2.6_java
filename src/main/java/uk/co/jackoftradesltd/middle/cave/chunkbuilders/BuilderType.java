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

package uk.co.jackoftradesltd.middle.cave.chunkbuilders;

import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.player.Player;

/**
 * The level builders the generator can choose between — the port of C's {@code cave_builders[]}
 * table, which is itself built from {@code list-dun-profiles.h}.
 *
 * <p>Every constant ties a name to the algorithm that lays out that style of level. The name is
 * the one {@code dungeon_profile.txt} uses, so the data file decides which builder runs; the
 * profile also supplies the depth range and allocation weight that make the choice.
 *
 * <p>Where C keeps the name and the function pointer in a static array and indexes into it, the
 * enum holds both on the constant, so the lookup C does by hand comes free.
 *
 * <p>Constants are declared in {@code list-dun-profiles.h} order, which is the order the profiles
 * are considered in.
 *
 * @author Rowan Crowther
 */
public enum BuilderType {
    /**
     * The town: shops around the edge, no dungeon layout to speak of.
     */
    TOWN("town", new TownBuilder()),
    /**
     * The standard dungeon level, and the basis several of the others build on.
     */
    MODIFIED("modified", new ModifiedBuilder()),
    /**
     * Oangband-style level of large, ragged, oval rooms, populated by Moria dwellers.
     */
    MORIA("moria", new MoriaBuilder()),
    /**
     * A modified level joined to a cavern full of themed monsters.
     */
    LAIR("lair", new LairBuilder()),
    /**
     * Two caverns separated by an unmappable labyrinth, with escape restricted.
     */
    GAUNTLET("gauntlet", new GauntletBuilder()),
    /**
     * A greater vault ringed by caverns.
     */
    HARD_CENTRE("hard centre", new HardCentreBuilder()),
    /**
     * A maze of corridors rather than rooms.
     */
    LABYRINTH("labyrinth", new LabyrinthBuilder()),
    /**
     * An open, cave-like level carved out rather than laid out.
     */
    CAVERN("cavern", new CavernBuilder()),
    /**
     * The original Angband level layout: rooms on a block grid, joined by tunnels.
     */
    CLASSIC("classic", new ClassicBuilder());

    /**
     * The name this builder is known by in {@code dungeon_profile.txt}.
     */
    private final String name;

    /**
     * The algorithm that lays out a level of this style.
     */
    private final CaveBuilder caveBuilder;

    /**
     * @param name        the name {@code dungeon_profile.txt} refers to this builder by
     * @param caveBuilder the algorithm that lays out a level of this style
     */
    BuilderType(String name, CaveBuilder caveBuilder) {
        this.name = name;
        this.caveBuilder = caveBuilder;
    }

    /**
     * @return the name this builder is known by in {@code dungeon_profile.txt}
     */
    public String getName() {
        return name;
    }

    /**
     * Build a level in this style, by handing off to the builder held on the constant.
     *
     * @param player    the player the level is being built for
     * @param minHeight the smallest acceptable level height, in grids
     * @param minWidth  the smallest acceptable level width, in grids
     * @return the newly built level, or {@code null} if this attempt failed
     * @see CaveBuilder#build
     */
    public Chunk build(Player player, int minHeight, int minWidth) {
        return caveBuilder.build(player, minHeight, minWidth);
    }
}
