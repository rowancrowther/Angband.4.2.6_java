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

package uk.co.jackoftradesltd.middle.combat.enums;

/**
 * The flags passed to {@code projectable()} and {@code project()} to say how a projection behaves.
 * Ports the anonymous {@code PROJECT_*} enum of {@code project.h}, whose members are bits of an
 * {@code int} mask in C. Here each is a plain constant and a combination is a {@code Flag<ProjectEnum>}
 * (an {@link java.util.EnumSet}), so the bit values are not carried; the constants keep C's names and order.
 *
 * <p>{@code PROJECT_NONE} is {@code 0x0000} in C, meaning an empty mask. It is deliberately kept here as a real
 * constant, like the other enums in this port, so a set built from it is not empty; see {@link #PROJECT_NONE}.
 *
 * <p>Enum ProjectEnum coded before 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public enum ProjectEnum {
    /**
     * No flags. In C this is {@code 0x0000}, the name given to an empty mask, and every C caller of {@code projectable()} that wants no flags passes it. Here it is kept as a real constant, as the other enums in this port do, so a {@code Flag<ProjectEnum>} "with no flags" built from it holds this one member rather than being empty: {@code count()} is 1 and {@code isEmpty()} is false, unlike C's zero mask. Nothing tests it, so the difference is currently harmless; do not rely on it being an empty set.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_NONE,
    /**
     * Jump directly to the target location without following a path. C bit {@code 0x0001}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_JUMP,
    /**
     * Work as a beam weapon, affecting every grid passed through. C bit {@code 0x0002}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_BEAM,
    /**
     * May continue through the target, used for bolts and beams. C bit {@code 0x0004}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_THRU,
    /**
     * Stop as soon as a monster is hit, used for bolts. C bit {@code 0x0008}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_STOP,
    /**
     * May affect terrain in the blast area in some way. C bit {@code 0x0010}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_GRID,
    /**
     * May affect objects in the blast area in some way. C bit {@code 0x0020}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_ITEM,
    /**
     * May affect monsters in the blast area in some way. C bit {@code 0x0040}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_KILL,
    /**
     * Disable visual feedback from the projection. C bit {@code 0x0080}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_HIDE,
    /**
     * The effects are already obvious to the player. C bit {@code 0x0100}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_AWARE,
    /**
     * Does not affect monsters of the same race as the caster. C bit {@code 0x0200}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_SAFE,
    /**
     * The projection is a sector of a circle radiating from the caster. C bit {@code 0x0400}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_ARC,
    /**
     * May affect the player. C bit {@code 0x0800}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_PLAY,
    /**
     * Use the believed (remembered) map rather than the true map, for player UI. C bit {@code 0x1000}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_INFO,
    /**
     * Use one quarter of the maximum range. C bit {@code 0x2000}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_SHORT,
    /**
     * May affect the player, even when cast by the player. C bit {@code 0x4000}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_SELF,
    /**
     * Make a path through rock without stopping at wall grids, as level generation does. {@code project.h}'s doc comment gives it no description; the meaning is taken from {@code project_path()} in {@code project.c}, as ported in {@code ChunkUtils.projectionPath}. C bit {@code 0x8000}.
     *
     * <p>Constant coded before 261001, commented in full on 261001.
     */
    PROJECT_ROCK
}
