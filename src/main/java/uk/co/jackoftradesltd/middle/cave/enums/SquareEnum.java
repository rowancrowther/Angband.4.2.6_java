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

package uk.co.jackoftradesltd.middle.cave.enums;

/**
 * The per-grid boolean flags a dungeon square can carry — memory, visibility,
 * room/vault membership, generation hints, movement/teleport restrictions, etc.
 *
 * <p>This is the Java port of the {@code SQUARE_*} flags. In C the list lives in
 * {@code list-square-flags.h} as {@code SQUARE(name, description)} lines, which
 * {@code cave.h} expands into an anonymous enum ending in {@code SQUARE_MAX}. The
 * constants here follow that header in order, so every ordinal equals the C value:
 * {@code SQUARE_NONE} is 0, {@code SQUARE_CLOSE_PLAYER} is 21 and {@code SQUARE_MAX}
 * is 22. Each constant's description is the header's second field, verbatim.
 *
 * <p>{@code SQUARE_NONE} and {@code SQUARE_MAX} are sentinels rather than real
 * flags: {@code SQUARE_MAX} is the size of the flag set (C's {@code SQUARE_SIZE}
 * is derived from it), and neither should be set on a grid.
 *
 * <p>Class SquareEnum coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public enum SquareEnum {
    /**
     * No flag; index 0 of the C list, with an empty description.
     *
     * <p>Constant SQUARE_NONE coded before 260930, commented in full on 260930.
     */
    SQUARE_NONE(""),
    /**
     * The player has memorized the feature on this grid.
     *
     * <p>Constant SQUARE_MARK coded before 260930, commented in full on 260930.
     */
    SQUARE_MARK("memorized feature"),
    /**
     * The grid is self-illuminating.
     *
     * <p>Constant SQUARE_GLOW coded before 260930, commented in full on 260930.
     */
    SQUARE_GLOW("self-illuminating"),
    /**
     * The grid is part of a vault.
     *
     * <p>Constant SQUARE_VAULT coded before 260930, commented in full on 260930.
     */
    SQUARE_VAULT("part of a vault"),
    /**
     * The grid is part of a room.
     *
     * <p>Constant SQUARE_ROOM coded before 260930, commented in full on 260930.
     */
    SQUARE_ROOM("part of a room"),
    /**
     * The "seen" flag of the view calculation.
     *
     * <p>Constant SQUARE_SEEN coded before 260930, commented in full on 260930.
     */
    SQUARE_SEEN("seen flag"),
    /**
     * The "view" flag of the view calculation.
     *
     * <p>Constant SQUARE_VIEW coded before 260930, commented in full on 260930.
     */
    SQUARE_VIEW("view flag"),
    /**
     * The grid was seen before the current update pass began. The description
     * was corrected on 260930: it had read {@code "previously seen (during update),"}
     * with a stray trailing comma, where the C header has no comma.
     *
     * <p>Constant SQUARE_WASSEEN coded before 260930, commented in full on 260930.
     */
    SQUARE_WASSEEN("previously seen (during update)"),
    /**
     * The grid is one of the hidden points that trigger level feelings.
     *
     * <p>Constant SQUARE_FEEL coded before 260930, commented in full on 260930.
     */
    SQUARE_FEEL("hidden points to trigger feelings"),
    /**
     * The grid holds a known trap.
     *
     * <p>Constant SQUARE_TRAP coded before 260930, commented in full on 260930.
     */
    SQUARE_TRAP("square containing a known trap"),
    /**
     * The grid holds an unknown (not yet detected) trap.
     *
     * <p>Constant SQUARE_INVIS coded before 260930, commented in full on 260930.
     */
    SQUARE_INVIS("square containing an unknown trap"),
    /**
     * Generation flag marking an inner wall.
     *
     * <p>Constant SQUARE_WALL_INNER coded before 260930, commented in full on 260930.
     */
    SQUARE_WALL_INNER("inner wall generation flag"),
    /**
     * Generation flag marking an outer wall.
     *
     * <p>Constant SQUARE_WALL_OUTER coded before 260930, commented in full on 260930.
     */
    SQUARE_WALL_OUTER("outer wall generation flag"),
    /**
     * Generation flag marking a solid wall.
     *
     * <p>Constant SQUARE_WALL_SOLID coded before 260930, commented in full on 260930.
     */
    SQUARE_WALL_SOLID("solid wall generation flag"),
    /**
     * No random monster may be placed on this grid.
     *
     * <p>Constant SQUARE_MON_RESTRICT coded before 260930, commented in full on 260930.
     */
    SQUARE_MON_RESTRICT("no random monster flag"),
    /**
     * The player cannot teleport away from this grid.
     *
     * <p>Constant SQUARE_NO_TELEPORT coded before 260930, commented in full on 260930.
     */
    SQUARE_NO_TELEPORT("player can't teleport from this square"),
    /**
     * The grid cannot be magically mapped.
     *
     * <p>Constant SQUARE_NO_MAP coded before 260930, commented in full on 260930.
     */
    SQUARE_NO_MAP("square can't be magically mapped"),
    /**
     * Telepathy does not work on this grid.
     *
     * <p>Constant SQUARE_NO_ESP coded before 260930, commented in full on 260930.
     */
    SQUARE_NO_ESP("telepathy doesn't work on this square"),
    /**
     * The grid is marked for projection processing.
     *
     * <p>Constant SQUARE_PROJECT coded before 260930, commented in full on 260930.
     */
    SQUARE_PROJECT("marked for projection processing"),
    /**
     * The grid lies inside a trap-detected area.
     *
     * <p>Constant SQUARE_DTRAP coded before 260930, commented in full on 260930.
     */
    SQUARE_DTRAP("trap detected square"),
    /**
     * Stairs may not be placed on this grid.
     *
     * <p>Constant SQUARE_NO_STAIRS coded before 260930, commented in full on 260930.
     */
    SQUARE_NO_STAIRS("square is not suitable for placing stairs"),
    /**
     * The grid is seen and inside the player's light radius or UNLIGHT detection radius.
     *
     * <p>Constant SQUARE_CLOSE_PLAYER coded before 260930, commented in full on 260930.
     */
    SQUARE_CLOSE_PLAYER("square is seen and in player's light radius or UNLIGHT detection radius"),
    /**
     * Sentinel equal to the number of flags, C's {@code SQUARE_MAX}; not a real
     * flag. Its ordinal is 22 and its description is empty.
     *
     * <p>Constant SQUARE_MAX coded before 260930, commented in full on 260930.
     */
    SQUARE_MAX("");

    /**
     * Human-readable description of what this square flag means: the second
     * field of the flag's {@code SQUARE()} line in {@code list-square-flags.h}.
     *
     * <p>Field description coded before 260930, commented in full on 260930.
     */
    private final String description;

    /**
     * Bind a square flag to its description.
     *
     * <p>Constructor SquareEnum coded before 260930, commented in full on 260930.
     *
     * @param description the flag's human-readable description
     */
    SquareEnum(String description) {
        this.description = description;
    }

    /**
     * Returns the description text from the C header, e.g. {@code "seen flag"}
     * for {@code SQUARE_SEEN}. It is empty for {@code SQUARE_NONE} and
     * {@code SQUARE_MAX}.
     *
     * <p>Function getDescription coded before 260930, commented in full on 260930.
     *
     * @return this flag's human-readable description
     */
    public String getDescription() {
        return description;
    }
}