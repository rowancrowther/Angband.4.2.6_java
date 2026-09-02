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

package uk.co.jackoftradesltd.middle.cave.profiles.vault;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import java.util.List;

/**
 * One vault the game may place — the port of C's {@code struct vault} ([C] src/generate.h:264).
 *
 * <p>Covers all seven of the room-builder types {@code vault.txt} carries, not just the vaults
 * proper: the three sizes, their newer variants, and the interesting rooms. Which one this is, and
 * so how it may be placed, is {@link #getType()}.
 *
 * <p>The layout is held in both the shapes a caller might want it in. {@link #getMapLines()} is
 * the flat {@code width * height} string C keeps in {@code vault.text} and indexes as
 * {@code text[y * width + x]}; {@link #getMap()} is the same grid split one entry per row. Both
 * keep the trailing spaces that pad a short row out to {@code width}, so either can be read
 * positionally without a bounds surprise.
 *
 * <p>{@link #getMaxLevel()} is never 0. The data file writes {@code max-depth:0} to mean "no
 * maximum", and the assembler turns that into the world's maximum depth while building this, the
 * same rewrite C does at parse time ([C] src/generate.c:561). {@link #getMinLevel()} needs no such
 * treatment — 0 there is already the right floor.
 *
 * @author Rowan Crowther
 */
public class Vault {
    /**
     * The vault's name, from {@code name:} — how the data file and error messages refer to it.
     */
    private String name;

    /**
     * Which room builder lays this out, from {@code type:}; also the source of the size caps the
     * assembler checked {@link #height} and {@link #width} against.
     */
    private RoomType type;

    /**
     * The layout as one flat {@code width * height} string — C's {@code vault.text}.
     */
    private String mapLines;

    /**
     * The layout as one string per row, in top-to-bottom order.
     */
    private List<String> map;

    /**
     * The room flags from any {@code flags:} directives; empty rather than {@code null} when the
     * record had none.
     */
    private Flag<RoomFlags> flags;

    /**
     * What placing this vault adds to a level's danger component when the level feeling is
     * calculated.
     */
    private int rating;

    /**
     * Rows in the layout, from {@code rows:} — C's {@code hgt}.
     */
    private int height;

    /**
     * Columns in the layout, from {@code columns:} — C's {@code wid}, and the length of every row.
     */
    private int width;

    /**
     * Shallowest depth this vault may appear at; 0 means no minimum.
     */
    private int minLevel;

    /**
     * Deepest depth this vault may appear at. Never 0 — see the class comment.
     */
    private int maxLevel;

    /**
     * @param name     the vault's name, from {@code name:}
     * @param type     the room builder that lays this out, from {@code type:}
     * @param mapLines the layout as one flat {@code width * height} string
     * @param map      the layout as one string per row, in top-to-bottom order
     * @param flags    the room flags; empty rather than {@code null} when there were none
     * @param rating   what this adds to a level's danger component
     * @param height   rows in the layout
     * @param width    columns in the layout, and the length of every row
     * @param minLevel shallowest depth this may appear at; 0 for no minimum
     * @param maxLevel deepest depth this may appear at, already resolved from a declared 0 to the
     *                 world maximum
     */
    public Vault(String name, RoomType type, String mapLines, List<String> map,
                 Flag<RoomFlags> flags, int rating, int height, int width, int minLevel, int maxLevel) {
        this.name = name;
        this.type = type;
        this.mapLines = mapLines;
        this.map = map;
        this.flags = flags;
        this.rating = rating;
        this.height = height;
        this.width = width;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    /**
     * @return the vault's name, from {@code name:}
     */
    public String getName() {
        return name;
    }

    /**
     * @return the room builder that lays this out
     */
    public RoomType getType() {
        return type;
    }

    /**
     * @return the layout as one flat {@code width * height} string, indexed as
     *         {@code charAt(y * width + x)}
     */
    public String getMapLines() {
        return mapLines;
    }

    /**
     * @return the layout as one string per row, in top-to-bottom order
     */
    public List<String> getMap() {
        return map;
    }

    /**
     * @return the room flags; empty rather than {@code null} when the record had none
     */
    public Flag<RoomFlags> getFlags() {
        return flags;
    }

    /**
     * @return what placing this adds to a level's danger component
     */
    public int getRating() {
        return rating;
    }

    /**
     * @return rows in the layout
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return columns in the layout, and the length of every row
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the shallowest depth this may appear at; 0 means no minimum
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * @return the deepest depth this may appear at; never 0
     */
    public int getMaxLevel() {
        return maxLevel;
    }
}
