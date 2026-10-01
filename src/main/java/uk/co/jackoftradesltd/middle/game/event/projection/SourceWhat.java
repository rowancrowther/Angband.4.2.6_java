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

package uk.co.jackoftradesltd.middle.game.event.projection;

/**
 * Discriminator that says what kind of thing an effect came from. It is the Java form of the anonymous
 * {@code what} enum inside {@code struct source} in {@code source.h}, and it selects which payload of
 * {@link Source} is meaningful.
 *
 * <p>The constants are declared in the same order as the C enum, so {@link #ordinal()} matches the C
 * integer value of each one.
 *
 * <p>Enum SourceWhat coded on 260829, commented in full on 261001.
 */
public enum SourceWhat {
    /**
     * No source: the effect has no originator. Carries no payload.
     */
    SRC_NONE,
    /** The effect came from a trap on the floor. */
    SRC_TRAP,
    /** The effect came from the player. */
    SRC_PLAYER,
    /** The effect came from a monster. */
    SRC_MONSTER,
    /** The effect came from an object. */
    SRC_OBJECT,
    /** The effect came from a trap on a chest. */
    SRC_CHEST_TRAP
}
