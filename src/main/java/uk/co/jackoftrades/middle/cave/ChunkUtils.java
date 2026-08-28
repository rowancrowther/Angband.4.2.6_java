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

package uk.co.jackoftrades.middle.cave;

import org.jetbrains.annotations.NotNull;

/**
 * Static geometry helpers over a {@link Chunk} that answer questions about a level without
 * belonging to any one grid. At present that is {@link #los}, the port of C's {@code los}
 * ({@code src/cave-view.c}).
 * <p>
 * These live outside {@link Chunk} because they are pure functions of the level rather than
 * part of its state: they read grids through {@code Chunk}'s public predicates, hold nothing
 * of their own, and are equally applicable to any level passed in. Sight and projection logic
 * that does maintain state — the view update and the grid flags it sets — stays on
 * {@link Chunk} and {@link Square}.
 * <p>
 * The class is a namespace only: it has no instance state and is never constructed.
 *
 * @author Rowan Crowther
 */
public class ChunkUtils {
    /**
     * Test whether an unobstructed line of sight runs between two grids. The port of C's
     * {@code los} ({@code cave-view.c}), Joseph Hall's integer line-of-sight algorithm. Line of
     * sight holds when every grid the traced ray passes through, endpoints excepted, is
     * projectable.
     *
     * <p>This is only one of the three line-of-sight notions the original carries, and the one it
     * uses least: projection paths are traced by {@code project}, and what the player can actually
     * see is decided by the view update. Its own use is narrow — breaking a repeated command once
     * its target passes out of sight, and similar checks.
     *
     * <p>The trace is fixed-point rather than floating-point. Both the slope and the fractional
     * component of the shorter axis are scaled by {@code factor1}, twice the product of the two
     * absolute offsets, which makes the slope an exact integer and keeps the whole walk in integer
     * arithmetic. The walk then steps one grid at a time along the longer axis, starting on the
     * boundary between the first and second grids so that the initial fraction is half a slope,
     * and consults the fraction to decide when a step along the shorter axis is due.
     *
     * <p>Three details of the C are load-bearing and are reproduced exactly:
     * <ul>
     *   <li>The degenerate cases are settled first and separately — adjacent or identical grids
     *       are always in sight without any grid being examined, and the two axis-aligned cases
     *       walk a simple exclusive range.</li>
     *   <li>The knight's moves are deliberately generous. Where the offset is one by two, sight is
     *       granted if the single grid orthogonally beside the origin is projectable, even though
     *       the true ray clips the diagonal neighbour. C's comment gives the reason as gameplay
     *       feel, and notes that these cases are the sole place the function is not reflexive:
     *       swapping the endpoints can change the answer, because the grid consulted is chosen
     *       relative to {@code grid1}.</li>
     *   <li>When the fraction lands exactly on {@code factor2} the ray meets the corner of a grid
     *       rather than entering it, so the shorter-axis step is taken without the extra
     *       projectability check that the strictly-greater branch makes. Sight is not blocked by
     *       brushing a corner.</li>
     * </ul>
     *
     * <p>C works in {@code short}s here and warns that the arithmetic overflows once either offset
     * exceeds 90. Java's {@code int} lifts that limit well beyond any level size, so the warning
     * is recorded rather than reproduced.
     *
     * <p>Method los coded on 260828, commented in full on 260828.
     *
     * @param cave  the level the grids belong to
     * @param grid1 the grid sight is traced from; the knight's-move cases are judged relative to
     *              this end
     * @param grid2 the grid sight is traced to
     * @return {@code true} if the two grids can see each other
     */
    public static boolean los(@NotNull Chunk cave, @NotNull Loc grid1, @NotNull Loc grid2) {
        int deltaY = grid2.getY() - grid1.getY();
        int deltaX = grid2.getX() - grid1.getX();

        int absY = Math.abs(deltaY);
        int absX = Math.abs(deltaX);

        // Are squares adjacent
        if (absY < 2 && absX < 2) return true;

        // Directly north/south
        if (deltaX == 0) {
            if (deltaY > 0) { // South
                for (int tempY = grid1.getY() + 1; tempY < grid2.getY(); tempY++) {
                    if (!cave.squareIsProjectable(Loc.row(tempY).col(grid1.getX()))) return false;
                }
            } else { // North
                for (int tempY = grid1.getY() - 1; tempY > grid2.getY(); tempY--) {
                    if (!cave.squareIsProjectable(Loc.row(tempY).col(grid1.getX()))) return false;
                }
            }

            // Assume LoS
            return true;
        }

        // Directly east/west
        if (deltaY == 0) {
            if (deltaX > 0) { // East
                for (int tempX = grid1.getX() + 1; tempX < grid2.getX(); tempX++) {
                    if (!cave.squareIsProjectable(Loc.row(grid1.getY()).col(tempX))) return false;
                }
            } else { // West
                for (int tempX = grid1.getX() - 1; tempX > grid2.getX(); tempX--) {
                    if (!cave.squareIsProjectable(Loc.row(grid1.getY()).col(tempX))) return false;
                }
            }

            // Assume LoS
            return true;
        }

        // Get the signs
        int signX = (deltaX < 0) ? -1 : 1;
        int signY = (deltaY < 0) ? -1 : 1;

        // Vertical and horizontal 'knights'
        if (absX == 1 && absY == 2 && cave.squareIsProjectable(Loc.row(grid1.getY() + signY).col(grid1.getX()))) {
            return true;
        } else if (absX == 2 && absY == 1
                && cave.squareIsProjectable(Loc.row(grid1.getY()).col(grid1.getX() + signX))) {
            return true;
        }

        // calculate the scale factor div 2
        int factor2 = (absX * absY);

        // calculate the scale factor
        int factor1 = factor2 << 1;

        // Travel horizontally
        if (absX >= absY) {
            /* Let m = (absy / absx) * f1
             *       = (absy / absx) * 2 * (absy * absx)
             *       = 2 * absy * absy */
            int quotientY = absY * absY;
            int m = quotientY << 1;

            int tempX = grid1.getX() + signX;
            int tempY;

            // Consider special case where slope == 1
            if (quotientY == factor2) {
                tempY = grid1.getY() + signY;
                quotientY -= factor1;
            } else {
                tempY = grid1.getY();
            }

            // Note the case (quotientY == f2) where the LOS exactly meets the corner of a tile
            while (grid2.getX() - tempX != 0) {
                if (!cave.squareIsProjectable(Loc.row(tempY).col(tempX))) return false;

                quotientY += m;

                if (quotientY < factor2) {
                    tempX += signX;
                } else if (quotientY > factor2) {
                    tempY += signY;
                    if (!cave.squareIsProjectable(Loc.row(tempY).col(tempX))) return false;

                    quotientY -= factor1;
                    tempX += signX;
                } else {
                    tempY += signY;
                    quotientY -= factor1;
                    tempX += signX;
                }
            }
        } else { // Travel vertically
            // Similar calculation for m
            int quotientX = absX * absX;
            int m = quotientX << 1;

            int tempY = grid1.getY() + signY;
            int tempX;

            if (quotientX == factor2) {
                tempX = grid1.getX() + signX;
                quotientX -= factor1;
            } else {
                tempX = grid1.getX();
            }

            // Note the case (quotientX == f2) where the LOS exactly meets the corner of a tile
            while (grid2.getY() - tempY != 0) {
                if (!cave.squareIsProjectable(Loc.row(tempY).col(tempX))) return false;

                quotientX += m;

                if (quotientX < factor2) {
                    tempY += signY;
                } else if (quotientX > factor2) {
                    tempX += signX;
                    if (!cave.squareIsProjectable(Loc.row(tempY).col(tempX))) return false;
                    quotientX -= factor1;
                    tempY += signY;
                } else {
                    tempX += signX;
                    quotientX -= factor1;
                    tempY += signY;
                }
            }
        }

        // Assume LoS
        return true;
    }
}
