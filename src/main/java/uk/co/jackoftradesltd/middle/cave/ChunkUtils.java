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

package uk.co.jackoftradesltd.middle.cave;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectEnum;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.registry.DungeonRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Static geometry helpers over a {@link Chunk} that answer questions about a level without
 * belonging to any one grid. At present that is {@link #los}, the port of C's {@code los}
 * ({@code src/cave-view.c}), {@link #isProjectable}, and {@link #projectionPath}, the port of
 * C's {@code project_path} ({@code src/project.c}).
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

    /**
     * Decide whether a bolt cast from one grid will arrive at another, assuming no monster gets in
     * the way. The port of C's {@code projectable} ({@code project.c}), which uses
     * {@link #projectionPath} to trace the route. The player uses it to judge whether a grid can
     * easily be targeted, and monsters use it to judge whether they can target the player.
     *
     * <p>The check runs in this order:
     * <ol>
     *   <li>the range is the player's maximum range, cut to a quarter (integer division) when
     *       {@code PROJECT_SHORT} is set and the player has {@code TMD_COVERTRACKS};</li>
     *   <li>the path is traced with {@link #projectionPath}, and an empty path answers
     *       {@code false}, because no grid is ever projectable from itself;</li>
     *   <li>the last grid on the path must be passable, so a bolt that ends in a wall fails;</li>
     *   <li>the last grid on the path must be {@code toGrid}, so a bolt that is stopped short by
     *       the range limit or by an obstruction fails.</li>
     * </ol>
     *
     * <p>C takes its flags as an {@code int} bitmask. Java takes a {@link Flag} set, so several
     * {@code ProjectEnum} values can be combined and are handed on to {@link #projectionPath}
     * unchanged. Callers with no flags to give pass a set holding {@code PROJECT_NONE}.
     *
     * <p>Method isProjectable coded on 261001, commented in full on 261001.
     *
     * @param cave     the level the grids belong to
     * @param fromGrid the grid the bolt is cast from; it is never part of the traced path
     * @param toGrid   the grid the bolt is meant to reach
     * @param flag     the projection flags; {@code PROJECT_SHORT} also shortens the range when the
     *                 player has covered their tracks
     * @return {@code true} if the bolt would end on {@code toGrid} and that grid is passable
     */
    public static boolean isProjectable(Chunk cave, Loc fromGrid, Loc toGrid, Flag<ProjectEnum> flag) {
        List<Loc> gridSquares;
        int maxRange = GameConstants.getPlayerMaxRange();

        // Check for shortened projection range
        if (flag.has(ProjectEnum.PROJECT_SHORT)
                && GameState.getPlayer().getTimedEffect(TimedEffect.TMD_COVERTRACKS) != 0) {
            maxRange /= 4;
        }

        // check projection path
        gridSquares = projectionPath(cave, maxRange, fromGrid, toGrid, flag);

        // No grid is projectable from itself
        if (gridSquares.isEmpty()) return false;

        int lastSquare = (gridSquares.size() - 1);
        if (lastSquare < 0) lastSquare = 0;

        // may not end in a wall
        if (!cave.getSquare(gridSquares.get(lastSquare)).featIsPassable()) return false;

        // May not end in a unrequested grid
        if (!gridSquares.get(lastSquare).equals(toGrid)) return false;

        // AssumeOK
        return true;
    }

    /**
     * Traces the path a projectile, bolt or beam takes between two grids. The port of C's
     * {@code project_path} ({@code project.c}). The initial grid is never part of the path, not
     * even when it is also the final grid.
     *
     * <p>C fills a caller-supplied {@code struct loc} array and returns the count. Java returns
     * the grids as a new {@link List} instead, so the count is {@code size()} and the empty list
     * stands for C's return of 0. As in C, the list is empty if and only if the two grids are
     * equal.
     *
     * <p>The trace is fixed-point, like {@link #los}. {@code half} is the product of the two
     * absolute offsets and {@code full} is twice that. The walk steps one grid at a time along the
     * longer axis, and the fraction decides when a step along the shorter axis is due. The vertical,
     * horizontal and diagonal cases are separate branches, as in C.
     *
     * <p>After each grid is saved the walk stops, in this order:
     * <ol>
     *   <li>at the range limit;</li>
     *   <li>at the finish grid, unless {@code PROJECT_THRU} is set;</li>
     *   <li>at a grid that is not projectable, unless {@code PROJECT_ROCK} is set. With
     *       {@code PROJECT_INFO} the test is {@link Chunk#squareIsBelievedWall}, so a path drawn
     *       for targeting does not leak what the player has not learned;</li>
     *   <li>with {@code PROJECT_STOP}, at a grid holding a monster or the player, or at the
     *       decoy.</li>
     * </ol>
     * The first grid is saved before any of these tests, so none of them can leave it out.
     *
     * <p>The range limit is the part to read carefully. C counts {@code n}, the grids saved, and
     * {@code k}, the steps taken along the shorter axis, and stops when
     * {@code n + (k >> 1) >= range}. The vertical and horizontal branches track {@code k} in
     * {@code steps}, incremented only when the fraction overflows. A straight line therefore has
     * {@code k = 0} and runs for {@code range} grids. The diagonal branch has no shorter axis, and C
     * uses {@code n + (n >> 1)} there, so a diagonal path is about two thirds as long as a straight
     * one. The path normally uses fewer than {@code range} grids, so its length must never be
     * compared with {@code range} directly.
     *
     * <p>C tests the flags with {@code flg & PROJECT_X}. Java takes a {@link Flag} and tests with
     * {@link Flag#has}, which is the same for a single bit. {@code PROJECT_JUMP} is documented in C
     * as unimplemented and is ignored here too.
     *
     * <p>Method projectionPath coded on 261001, commented in full on 261001.
     *
     * @param cave     the level to trace through
     * @param maxRange the range limit, C's {@code range}
     * @param fromGrid the grid the path starts from; never included in the result
     * @param toGrid   the grid the path aims at
     * @param flag     the {@code PROJECT_} flags that modify the trace
     * @return the grids of the path in order, empty if {@code fromGrid} equals {@code toGrid}
     */
    public static List<Loc> projectionPath(Chunk cave, int maxRange, Loc fromGrid, Loc toGrid,
                                           Flag<ProjectEnum> flag) {
        List<Loc> result = new ArrayList<>();
        int absY;
        int absX;
        int signY;
        int signX;
        int steps = 0;

        // Possible decoy
        Loc decoy = cave.caveFindDecoy();

        // No path necessary (or allowed)
        if (fromGrid.equals(toGrid)) return result;

        // Analyse dy
        if (toGrid.getY() < fromGrid.getY()) {
            absY = fromGrid.getY() - toGrid.getY();
            signY = -1;
        } else {
            absY = toGrid.getY() - fromGrid.getY();
            signY = 1;
        }

        // Analyse dx
        if (toGrid.getX() < fromGrid.getX()) {
            absX = fromGrid.getX() - toGrid.getX();
            signX = -1;
        } else {
            absX = toGrid.getX() - fromGrid.getX();
            signX = 1;
        }

        // Number of units in one half grid
        int half = (absX * absY);

        // Number of units in one full grid
        int full = half << 1;

        int frac;
        int m;
        int y;
        int x;
        int resultSize;

        // vertical
        if (absY > absX) {
            // start at tile edge
            frac = absX * absX;

            // m = ((dx/dy) * full) = (dx * dx * 2) = frac * 2)
            m = frac << 1;

            // Start
            y = fromGrid.getY() + signY;
            x = fromGrid.getX();

            while (true) {
                Loc newGrid = Loc.row(y).col(x);
                result.add(newGrid);
                resultSize = result.size();

                // Check maximum range
                if (resultSize + (steps >> 1) >= maxRange) break;

                // sometimes stop at finish grid
                if (!flag.has(ProjectEnum.PROJECT_THRU))
                    if (newGrid.equals(toGrid)) break;

                // Don't stop if making paths through rock for generation
                if (!flag.has(ProjectEnum.PROJECT_ROCK)) {
                    // Stop at non-initial wall grids, except where that would
                    // leak info during targetting
                    if (!flag.has(ProjectEnum.PROJECT_INFO)) {
                        if (resultSize > 0 && !cave.squareIsProjectable(newGrid)) {
                            break;
                        }
                    } else if (resultSize > 0 && cave.squareIsBelievedWall(newGrid))
                        break;
                }

                // Sometime stop at non-initial monsters/players/decoys
                if (flag.has(ProjectEnum.PROJECT_STOP)) {
                    if (resultSize > 0 && cave.getSquare(newGrid).getMonsterIndex() != 0)
                        break;
                    if (newGrid.equals(decoy)) break;
                }

                // Slant
                if (m != 0) {
                    // Advance (x) part 1
                    frac += m;

                    // Horizontal change;
                    if (frac >= half) {
                        // Advance (x) part 2
                        x += signX;

                        // Advance (x) part 3
                        frac -= full;
                        steps++;
                    }
                }
                // Advance (y)
                y += signY;
            }

            // Horizontal
        } else if (absX > absY) {
            // Start at tile edge
            frac = absY * absY;

            // m = ((dy/xy) * full) = (dy * dy * 2) = frac * 2)
            m = frac << 1;

            // Start
            y = fromGrid.getY();
            x = fromGrid.getX() + signX;

            // create the projection path
            while (true) {
                Loc newGrid = Loc.row(y).col(x);

                // Save the grid
                result.add(newGrid);
                resultSize = result.size();

                // check maximum distance
                if (resultSize + (steps >> 1) >= maxRange) break;

                // Sometimes stop at finish grid
                if (!flag.has(ProjectEnum.PROJECT_THRU))
                    if (newGrid.equals(toGrid)) break;

                // Don't stop if making paths through rock for generation
                if (!flag.has(ProjectEnum.PROJECT_ROCK)) {
                    // Stop on non-initial wall grids, except where that would 
                    // leak info during targetting
                    if (!flag.has(ProjectEnum.PROJECT_INFO)) {
                        if (resultSize > 0 && !cave.squareIsProjectable(newGrid)) {
                            break;
                        }
                    } else if (resultSize > 0 && cave.squareIsBelievedWall(newGrid)) {
                        break;
                    }
                }

                // Sometimes stop at non-initial monsters/players/decoys
                if (flag.has(ProjectEnum.PROJECT_STOP)) {
                    if (resultSize > 0 && cave.getSquare(newGrid).getMonsterIndex() != 0)
                        break;
                    if (newGrid.equals(decoy)) break;
                }

                // Slant
                if (m != 0) {
                    // Advance (Y) part 1
                    frac += m;

                    // vertical change
                    if (frac >= half) {
                        // advance (Y) part 2
                        y += signY;

                        // Advance (Y) part 3
                        frac -= full;
                        steps++;
                    }
                }

                // Advance (X)
                x += signX;
            }

            // Diagonal
        } else {
            // Start
            y = fromGrid.getY() + signY;
            x = fromGrid.getX() + signX;

            // Create the projection path
            while (true) {
                Loc newGrid = Loc.row(y).col(x);
                result.add(newGrid);
                resultSize = result.size();

                // Check maximum range
                if (resultSize + (resultSize >> 1) >= maxRange) break;

                // Sometimes stp[ at finish grid
                if (!flag.has(ProjectEnum.PROJECT_THRU))
                    if (newGrid.equals(toGrid)) break;

                // Don't stop if making paths through rock for generation
                if (!flag.has(ProjectEnum.PROJECT_ROCK)) {
                    // Stop at non-initial wall grids, except where that would 
                    // leak info during targetting
                    if (!flag.has(ProjectEnum.PROJECT_INFO)) {
                        if (resultSize > 0 && !cave.squareIsProjectable(newGrid)) {
                            break;
                        }
                    } else if (resultSize > 0 && cave.squareIsBelievedWall(newGrid)) {
                        break;
                    }
                }

                // Sometimes stop at non-initial monsters/players, decoys
                if (flag.has(ProjectEnum.PROJECT_STOP)) {
                    if (resultSize > 0 && cave.getSquare(newGrid).getMonsterIndex() != 0)
                        break;
                    if (newGrid.equals(decoy)) break;
                }

                // Advance
                y += signY;
                x += signX;
            }
        }

        return result;
    }
}
