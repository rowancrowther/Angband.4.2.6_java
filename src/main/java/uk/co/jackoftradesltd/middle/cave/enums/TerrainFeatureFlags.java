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
 * The intrinsic property flags of a terrain feature — line of sight, passability,
 * whether it can hold a trap or object, door/wall/stair classification, lighting,
 * and so on.
 *
 * <p>This is the Java port of the {@code TF_*} flags. In C the list lives in
 * {@code list-terrain-flags.h} as {@code TF(name, description)} lines, which
 * {@code cave.h} expands into an anonymous enum ending in {@code TF_MAX}, and which
 * {@code init.c} expands again into the {@code terrain_flags[]} name table used to
 * read the {@code flags:} lines of {@code terrain.txt}. The constants here follow that
 * header in order, so every ordinal equals the C value: {@code TF_NONE} is 0,
 * {@code TF_LOS} is 1, {@code TF_FIERY} is 31 and {@code TF_MAX} is 32. Each
 * constant's description is the header's second field, verbatim.
 *
 * <p>{@code TF_NONE} and {@code TF_MAX} are sentinels rather than real flags:
 * {@code TF_MAX} is the size of the flag set (C's {@code TF_SIZE} is derived from it),
 * and neither should be set on a feature.
 *
 * <p>Class TerrainFeatureFlags coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public enum TerrainFeatureFlags {
    /**
     * No flag; index 0 of the C list, with an empty description.
     *
     * <p>Constant TF_NONE coded before 260930, commented in full on 260930.
     */
    TF_NONE(""),
    /**
     * Allows line of sight.
     *
     * <p>Constant TF_LOS coded before 260930, commented in full on 260930.
     */
    TF_LOS("Allows line of sight"),
    /**
     * Allows projections to pass through.
     *
     * <p>Constant TF_PROJECT coded before 260930, commented in full on 260930.
     */
    TF_PROJECT("Allows projections to pass through"),
    /**
     * Can be passed through by all creatures.
     *
     * <p>Constant TF_PASSABLE coded before 260930, commented in full on 260930.
     */
    TF_PASSABLE("Can be passed through by all creatures"),
    /**
     * Is noticed on looking around.
     *
     * <p>Constant TF_INTERESTING coded before 260930, commented in full on 260930.
     */
    TF_INTERESTING("Is noticed on looking around"),
    /**
     * Is permanent.
     *
     * <p>Constant TF_PERMANENT coded before 260930, commented in full on 260930.
     */
    TF_PERMANENT("Is permanent"),
    /**
     * Is easily passed through.
     *
     * <p>Constant TF_EASY coded before 260930, commented in full on 260930.
     */
    TF_EASY("Is easily passed through"),
    /**
     * Can hold a trap.
     *
     * <p>Constant TF_TRAP coded before 260930, commented in full on 260930.
     */
    TF_TRAP("Can hold a trap"),
    /**
     * Cannot store scent.
     *
     * <p>Constant TF_NO_SCENT coded before 260930, commented in full on 260930.
     */
    TF_NO_SCENT("Cannot store scent"),
    /**
     * No flow through.
     *
     * <p>Constant TF_NO_FLOW coded before 260930, commented in full on 260930.
     */
    TF_NO_FLOW("No flow through"),
    /**
     * Can hold objects.
     *
     * <p>Constant TF_OBJECT coded before 260930, commented in full on 260930.
     */
    TF_OBJECT("Can hold objects"),
    /**
     * Becomes bright when torch-lit.
     *
     * <p>Constant TF_TORCH coded before 260930, commented in full on 260930.
     */
    TF_TORCH("Becomes bright when torch-lit"),
    /**
     * Can be found by searching.
     *
     * <p>Constant TF_HIDDEN coded before 260930, commented in full on 260930.
     */
    TF_HIDDEN("Can be found by searching"),
    /**
     * Contains treasure.
     *
     * <p>Constant TF_GOLD coded before 260930, commented in full on 260930.
     */
    TF_GOLD("Contains treasure"),
    /**
     * Can be closed.
     *
     * <p>Constant TF_CLOSABLE coded before 260930, commented in full on 260930.
     */
    TF_CLOSABLE("Can be closed"),
    /**
     * Is a clear floor.
     *
     * <p>Constant TF_FLOOR coded before 260930, commented in full on 260930.
     */
    TF_FLOOR("Is a clear floor"),
    /**
     * Is a solid wall.
     *
     * <p>Constant TF_WALL coded before 260930, commented in full on 260930.
     */
    TF_WALL("Is a solid wall"),
    /**
     * Is rocky.
     *
     * <p>Constant TF_ROCK coded before 260930, commented in full on 260930.
     */
    TF_ROCK("Is rocky"),
    /**
     * Is a granite rock wall.
     *
     * <p>Constant TF_GRANITE coded before 260930, commented in full on 260930.
     */
    TF_GRANITE("Is a granite rock wall"),
    /**
     * Is any door.
     *
     * <p>Constant TF_DOOR_ANY coded before 260930, commented in full on 260930.
     */
    TF_DOOR_ANY("Is any door"),
    /**
     * Is a closed door.
     *
     * <p>Constant TF_DOOR_CLOSED coded before 260930, commented in full on 260930.
     */
    TF_DOOR_CLOSED("Is a closed door"),
    /**
     * Is a shop.
     *
     * <p>Constant TF_SHOP coded before 260930, commented in full on 260930.
     */
    TF_SHOP("Is a shop"),
    /**
     * Is a jammed door.
     *
     * <p>Constant TF_DOOR_JAMMED coded before 260930, commented in full on 260930.
     */
    TF_DOOR_JAMMED("Is a jammed door"),
    /**
     * Is a locked door.
     *
     * <p>Constant TF_DOOR_LOCKED coded before 260930, commented in full on 260930.
     */
    TF_DOOR_LOCKED("Is a locked door"),
    /**
     * Is a magma seam.
     *
     * <p>Constant TF_MAGMA coded before 260930, commented in full on 260930.
     */
    TF_MAGMA("Is a magma seam"),
    /**
     * Is a quartz seam.
     *
     * <p>Constant TF_QUARTZ coded before 260930, commented in full on 260930.
     */
    TF_QUARTZ("Is a quartz seam"),
    /**
     * Is a stair.
     *
     * <p>Constant TF_STAIR coded before 260930, commented in full on 260930.
     */
    TF_STAIR("Is a stair"),
    /**
     * Is an up staircase.
     *
     * <p>Constant TF_UPSTAIR coded before 260930, commented in full on 260930.
     */
    TF_UPSTAIR("Is an up staircase"),
    /**
     * Is a down staircase.
     *
     * <p>Constant TF_DOWNSTAIR coded before 260930, commented in full on 260930.
     */
    TF_DOWNSTAIR("Is a down staircase"),
    /**
     * Should have smooth boundaries.
     *
     * <p>Constant TF_SMOOTH coded before 260930, commented in full on 260930.
     */
    TF_SMOOTH("Should have smooth boundaries"),
    /**
     * Is internally lit.
     *
     * <p>Constant TF_BRIGHT coded before 260930, commented in full on 260930.
     */
    TF_BRIGHT("Is internally lit"),
    /**
     * Is fire-based.
     *
     * <p>Constant TF_FIERY coded before 260930, commented in full on 260930.
     */
    TF_FIERY("Is fire-based"),
    /**
     * Sentinel equal to the number of flags, C's {@code TF_MAX}; not a real
     * flag. Its ordinal is 32 and its description is empty.
     *
     * <p>Constant TF_MAX coded before 260930, commented in full on 260930.
     */
    TF_MAX("");

    /**
     * Human-readable description of what this terrain-feature flag means: the second
     * field of the flag's {@code TF()} line in {@code list-terrain-flags.h}.
     *
     * <p>Field description coded before 260930, commented in full on 260930.
     */
    private final String description;

    /**
     * Bind a terrain-feature flag to its description.
     *
     * <p>Constructor TerrainFeatureFlags coded before 260930, commented in full on 260930.
     *
     * @param description the flag's human-readable description
     */
    TerrainFeatureFlags(String description) {
        this.description = description;
    }

    /**
     * Returns the description text from the C header, e.g. {@code "Allows line of sight"}
     * for {@code TF_LOS}. It is empty for {@code TF_NONE} and {@code TF_MAX}.
     *
     * <p>Function getDescription coded before 260930, commented in full on 260930.
     *
     * @return this flag's human-readable description
     */
    public String getDescription() {
        return description;
    }
}
