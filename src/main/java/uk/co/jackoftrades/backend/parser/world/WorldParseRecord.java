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

package uk.co.jackoftrades.backend.parser.world;

/**
 * Thin parse-DTO produced by the World reader for a single
 * level's values in lib/gamedata/world.txt.
 *
 * <p>This class is the boundary between the grammar layer and the reader layer.
 * The grammar's only job is to extract raw text from the data file and populate
 * this record — no enum resolution, no domain-object construction, no calls to
 * GameConstants. All of that work happens in {@link WorldAssembler},
 * which converts a {@code List<WorldParseRecord>} into the real domain
 * objects once the domain API is stable enough to use.</p>
 *
 * <p>A DTO rather than returning the domain values directly
 * from the grammar, as the domain model is still being ported and its
 * constructors change frequently. Design decision to un-couple a grammar
 * from a changing constructor. The DTO decouples the two: the grammar is
 * "done" once it parses correctly, regardless of whether the domain class
 * it feeds is ready.</p>
 *
 * <p>Field types are kept as primitives and Strings wherever possible.
 *
 * @author Rowan Crowther
 */
public class WorldParseRecord {
    /**
     * Holder for the line that any error occurred on
     */
    private final String lineNumber;

    /**
     * Holder for the level number for this level, i.e. 0
     */
    private final String levelNumber;

    /**
     * Holder for the level name for this level
     */
    private final String levelName;

    /**
     * Holder for the name of the level one up from this
     * one, either a level name, or null for the town
     * level
     */
    private final String up;

    /**
     * Holder for the name of the level one down from this
     * one, either a level name, or null for the bottom
     * level of the dungeon
     */
    private final String down;

    /**
     * Constructor for a world level
     *
     * @param levelNumber The number of the level, the town is
     *                    always level 0
     * @param levelName   The name of the level, i.e. 'Town',
     *                    'Angband 7'
     * @param up          The name of the level one up from this
     *                    either the level name, or null if
     *                    this level is the town
     * @param down        The name of the level one down from this
     *                    either the level name, or null if
     *                    this level is the bottom of the dungeon
     * @param lineNumber  The line number in the world.txt file
     *                    which contains the input for this
     *                    line
     * @author Rowan Crowther
     */
    public WorldParseRecord(String levelNumber, String levelName, String up,
                            String down, String lineNumber) {
        this.lineNumber = lineNumber;
        this.levelNumber = levelNumber;
        this.levelName = levelName;
        this.up = up;
        this.down = down;
    }

    /**
     * Accessor
     *
     * @return the line number of the line in the World.txt file this
     * world was found on
     * @author Rowan Crowther
     */
    public String getLineNumber() {
        return lineNumber;
    }

    /**
     * Accessor
     *
     * @return the number of this level, from 0 (town) to the maximum
     * number of levels in this dungeon - 1
     * @author Rowan Crowther
     */
    public String getLevelNumber() {
        return levelNumber;
    }

    /**
     * Accessor
     *
     * @return the name of this level
     */
    public String getLevelName() {
        return levelName;
    }

    /**
     * Accessor
     *
     * @return the level one up from this level
     */
    public String getUp() {
        return up;
    }

    /**
     * Accessor
     *
     * @return the level one down from this level
     */
    public String getDown() {
        return down;
    }
}