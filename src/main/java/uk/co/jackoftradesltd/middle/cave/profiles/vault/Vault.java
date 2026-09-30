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
 * One vault the game may place — the port of C's {@code struct vault} in {@code generate.h}.
 *
 * <p>Covers all seven of the room-builder types {@code vault.txt} carries, not just the vaults
 * proper: the three sizes, their newer variants, and the interesting rooms. Which one this is, and
 * so how it may be placed, is {@link #getType()}.
 *
 * <p>C chains its vaults through a {@code next} pointer; that field has no counterpart here, because
 * {@code VaultAssembler} hands back a {@code List<Vault>} in file order instead. C's {@code typ}
 * is a string matched against the room-builder names; here it is already resolved to a
 * {@link RoomType}. C's {@code rat}, {@code hgt}, {@code wid}, {@code min_lev} and {@code max_lev}
 * are all {@code uint8_t}; here they are plain {@code int}s, so a value past 255 is held as
 * written rather than wrapping.
 *
 * <p>The layout is held in both the shapes a caller might want it in. {@link #getMapLines()} is
 * the flat {@code width * height} string C keeps in {@code vault.text} and indexes as
 * {@code text[y * width + x]}; {@link #getMap()} is the same grid split one entry per row. The
 * assembler rejects any record with a row that is not exactly {@code width} long, or with a row
 * count other than {@code height}, so in a {@code Vault} that got this far both shapes are
 * rectangular and can be read positionally without a bounds surprise. The trailing spaces that
 * pad a short row out to {@code width} are written into {@code vault.txt} itself and kept as read;
 * nothing here pads a row.
 *
 * <p>{@link #getMaxLevel()} is never 0. The data file writes {@code max-depth:0} to mean "no
 * maximum", and the assembler turns that into the world's maximum depth while building this, the
 * same rewrite C's {@code parse_vault_max_depth} does at parse time. {@link #getMinLevel()} needs
 * no such treatment — 0 there is already the right floor.
 *
 * <p>Class coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class Vault {
    /**
     * The vault's name, from {@code name:} — how the data file and error messages refer to it.
     * C's {@code name}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private String name;

    /**
     * Which room builder lays this out, from {@code type:}; also the source of the size caps the
     * assembler checked {@link #height} and {@link #width} against. C's {@code typ}, resolved from
     * its string to the enum.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private RoomType type;

    /**
     * The layout as one flat {@code width * height} string — C's {@code text}, which C builds by
     * appending each {@code D:} line in turn.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private String mapLines;

    /**
     * The layout as one string per row, in top-to-bottom order. The same characters as
     * {@link #mapLines}; C has no equivalent, since it only keeps the flat form.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private List<String> map;

    /**
     * The room flags from any {@code flags:} directives, OR'd together; empty rather than
     * {@code null} when the record had none. C's {@code flags}, a {@code ROOMF_SIZE} bitflag
     * array.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private Flag<RoomFlags> flags;

    /**
     * What placing this vault adds to a level's danger component when the level feeling is
     * calculated, from {@code rating:}. C's {@code rat}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private int rating;

    /**
     * Rows in the layout, from {@code rows:} — C's {@code hgt}. Already checked against the
     * {@linkplain RoomType#getMaxHeight() height cap} of {@link #type}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private int height;

    /**
     * Columns in the layout, from {@code columns:} — C's {@code wid}, and the length of every row.
     * Already checked against the {@linkplain RoomType#getMaxWidth() width cap} of {@link #type}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private int width;

    /**
     * Shallowest depth this vault may appear at, from {@code min-depth:}; 0 means no minimum. C's
     * {@code min_lev}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private int minLevel;

    /**
     * Deepest depth this vault may appear at, from {@code max-depth:}. Never 0 — see the class
     * comment. C's {@code max_lev}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private int maxLevel;

    /**
     * Holds an already-validated vault record. Does no checking of its own: the caps on
     * {@code height} and {@code width}, the row lengths, the flag names and the
     * {@code max-depth:0} rewrite are all the assembler's job, so the arguments are stored as given.
     *
     * <p>Constructor coded before 260930, commented in full on 260930.
     *
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
     * Accessor for {@link #name}.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return the vault's name, from {@code name:}
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor for {@link #type}.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return the room builder that lays this out
     */
    public RoomType getType() {
        return type;
    }

    /**
     * Accessor for {@link #mapLines}.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return the layout as one flat {@code width * height} string, indexed as
     *         {@code charAt(y * width + x)}
     */
    public String getMapLines() {
        return mapLines;
    }

    /**
     * Accessor for {@link #map}. Returns the list itself, not a copy.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return the layout as one string per row, in top-to-bottom order
     */
    public List<String> getMap() {
        return map;
    }

    /**
     * Accessor for {@link #flags}. Returns the set itself, not a copy.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return the room flags; empty rather than {@code null} when the record had none
     */
    public Flag<RoomFlags> getFlags() {
        return flags;
    }

    /**
     * Accessor for {@link #rating}.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return what placing this adds to a level's danger component
     */
    public int getRating() {
        return rating;
    }

    /**
     * Accessor for {@link #height}.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return rows in the layout
     */
    public int getHeight() {
        return height;
    }

    /**
     * Accessor for {@link #width}.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return columns in the layout, and the length of every row
     */
    public int getWidth() {
        return width;
    }

    /**
     * Accessor for {@link #minLevel}.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return the shallowest depth this may appear at; 0 means no minimum
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * Accessor for {@link #maxLevel}.
     *
     * <p>Method coded before 260930, commented in full on 260930.
     *
     * @return the deepest depth this may appear at; never 0
     */
    public int getMaxLevel() {
        return maxLevel;
    }
}
