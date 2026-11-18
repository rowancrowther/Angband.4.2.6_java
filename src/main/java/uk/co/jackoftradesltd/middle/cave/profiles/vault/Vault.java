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
 * <p>Covers every room-builder type {@code vault.txt} may name, not just the vaults proper. The
 * shipped file uses seven (the three sizes of vault, their newer variants, and the interesting
 * rooms), but {@code VaultAssembler} accepts any of C's nineteen builder names, as C does. Which
 * one this is, and so how it may be placed, is {@link #getType()}.
 *
 * <p>C chains its vaults through a {@code next} pointer; that field has no counterpart here, because
 * {@code VaultAssembler} hands back a {@code List<Vault>} instead, in reverse file order as C's
 * chain runs. C's {@code typ} is a string matched against the room-builder names; here it is
 * already resolved to a {@link RoomType}. C's {@code rat}, {@code hgt}, {@code wid},
 * {@code min_lev} and {@code max_lev} are all {@code uint8_t}; here they are plain {@code int}s,
 * and this class stores whatever it is given without wrapping, so keeping them inside 0 to 255 is
 * the caller's job ({@code VaultAssembler} narrows each one to 0 to 255 before it stores it).
 *
 * <p>The layout is held in both the shapes a caller might want it in. {@link #getMapLines()} is
 * the flat string C keeps in {@code vault.text} and indexes as {@code text[y * width + x]};
 * {@link #getMap()} is the same grid split one entry per row. C checks each {@code D:} line against
 * {@code width} as it stands when the line is read, so every row here is as long as the width was
 * then (which is {@code width} unless a later {@code columns:} changed it), but it never
 * compares the number of lines with {@code height}, so neither does the assembler: a vault can come
 * through with a row count other than {@code height}, and a read of {@code text[y * width + x]} must
 * stay within the string. The trailing spaces that pad a short row out to {@code width} are written
 * into {@code vault.txt} itself and kept as read; nothing here pads a row.
 *
 * <p>C's {@code max_lev} is never 0. The data file writes {@code max-depth:0} to mean "no
 * maximum", and C's {@code parse_vault_max_depth} rewrites it to {@code z_info->max_depth} as it
 * reads the line. This class does not make that rewrite, so a 0 passed to it stays 0; the rewrite
 * belongs to the assembler, and {@code VaultAssembler.parseMaxDepth()} makes it: a declared
 * {@code max-depth:0} reaches here as the world maximum depth, narrowed to 0 to 255 as C's
 * {@code uint8_t} would hold it. {@link #getMinLevel()} needs no such treatment — 0 there is
 * already the right floor.
 *
 * <p>Class coded before 260930, commented in full on 261118 (first commented 260930; corrected
 * 261118 for the list order, the row-count check and the {@code max-depth:0} rewrite; the
 * "Outstanding" note on that rewrite removed 261118 once the assembler made it).
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
     * Which room builder lays this out, from {@code type:}; also the source of the size caps
     * {@link #height} and {@link #width} are checked against when the assembler reads {@code rows:}
     * and {@code columns:}.
     * C's {@code typ}, resolved from its string to the enum.
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
     * Rows in the layout, from {@code rows:} — C's {@code hgt}. The assembler checks it against the
     * {@linkplain RoomType#getMaxHeight() height cap} of {@link #type}; this class does not.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private int height;

    /**
     * Columns in the layout, from {@code columns:} — C's {@code wid}, and the length of every row.
     * The assembler checks it against the {@linkplain RoomType#getMaxWidth() width cap} of
     * {@link #type}; this class does not.
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
     * Deepest depth this vault may appear at, from {@code max-depth:}. C's {@code max_lev}, which is
     * never 0 there; see the class comment for who makes the 0 rewrite here.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private int maxLevel;

    /**
     * Holds the fields of one vault, as C's {@code struct vault} does. Does no checking of its own:
     * the caps on {@code height} and {@code width}, the row lengths, the flag names, the
     * {@code max-depth:0} rewrite and the narrowing to {@code uint8_t} range are all the assembler's
     * job, so the arguments are stored as given, including {@code null}s. The assembler itself
     * starts each record from an all-empty vault ({@code null} references, zero numbers, an empty
     * flag set and map list) and fills that same vault in through the setters, one directive at a
     * time, as C fills its zeroed {@code struct vault}.
     *
     * <p>Constructor coded before 260930, commented in full on 261118 (first commented 260930;
     * assembler description corrected 261118).
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
     * @param maxLevel deepest depth this may appear at; C holds a declared 0 as the world maximum,
     *                 but this constructor does not make that change
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
     * @return the deepest depth this may appear at; C's value is never 0, but this class returns
     *         whatever it was given (see the class comment)
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Replaces {@link #flags}. Stores the set itself, not a copy, and checks nothing.
     *
     * <p>Method setFlags coded before 261118, commented in full on 261118.
     *
     * @param flags the room flags; use an empty set rather than {@code null} for none
     */
    public void setFlags(Flag<RoomFlags> flags) {
        this.flags = flags;
    }

    /**
     * Replaces {@link #height}. Stores the value as given, with no check against the room
     * builder's height cap.
     *
     * <p>Method setHeight coded before 261118, commented in full on 261118.
     *
     * @param height rows in the layout
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Replaces {@link #map}. Stores the list itself, not a copy; it is not kept in step with
     * {@link #mapLines}, so a caller changing one must change the other.
     *
     * <p>Method setMap coded before 261118, commented in full on 261118.
     *
     * @param map the layout as one string per row, in top-to-bottom order
     */
    public void setMap(List<String> map) {
        this.map = map;
    }

    /**
     * Replaces {@link #mapLines}. Not kept in step with {@link #map}, so a caller changing one must
     * change the other.
     *
     * <p>Method setMapLines coded before 261118, commented in full on 261118.
     *
     * @param mapLines the layout as one flat string, indexed as {@code charAt(y * width + x)}
     */
    public void setMapLines(String mapLines) {
        this.mapLines = mapLines;
    }

    /**
     * Replaces {@link #maxLevel}. Stores the value as given; a 0 is not turned into the world
     * maximum depth here.
     *
     * <p>Method setMaxLevel coded before 261118, commented in full on 261118.
     *
     * @param maxLevel the deepest depth this may appear at
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * Replaces {@link #minLevel}. Stores the value as given.
     *
     * <p>Method setMinLevel coded before 261118, commented in full on 261118.
     *
     * @param minLevel the shallowest depth this may appear at; 0 means no minimum
     */
    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    /**
     * Replaces {@link #name}.
     *
     * <p>Method setName coded before 261118, commented in full on 261118.
     *
     * @param name the vault's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Replaces {@link #rating}. Stores the value as given; C's {@code uint8_t} wrap is not applied
     * here.
     *
     * <p>Method setRating coded before 261118, commented in full on 261118.
     *
     * @param rating what placing this adds to a level's danger component
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Replaces {@link #type}. Does not re-check {@link #height} or {@link #width} against the new
     * type's caps.
     *
     * <p>Method setType coded before 261118, commented in full on 261118.
     *
     * @param type the room builder that lays this out
     */
    public void setType(RoomType type) {
        this.type = type;
    }

    /**
     * Replaces {@link #width}. Stores the value as given, with no check against the room builder's
     * width cap or the row lengths.
     *
     * <p>Method setWidth coded before 261118, commented in full on 261118.
     *
     * @param width columns in the layout, and the length of every row
     */
    public void setWidth(int width) {
        this.width = width;
    }
}
