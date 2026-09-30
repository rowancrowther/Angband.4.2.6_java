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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;

/**
 * A circular, singly-linked ring of the eight compass directions plus the centre,
 * ordered <em>clockwise</em> starting at north (N, NE, E, SE, S, SW, W, NW, centre, then
 * back to N). It is the Java form of the C original's paired arrays {@code clockwise_ddd[]}
 * and {@code clockwise_grid[]} in {@code cave.c}, which hold the same nine directions as keypad
 * numbers ({@code 8, 9, 6, 3, 2, 1, 4, 7, 5}) and as {@link Loc} offsets respectively.
 * <p>
 * The C code walks those arrays by index and takes {@code (d + 1) % 8} or {@code (d - 1 + 8) % 8}
 * to turn a heading one step either way; it uses them in {@code effect-handler-attack.c}, function
 * {@code effect_handler_MOVE_ATTACK()} (step towards a target, trying the neighbouring headings
 * if the way is blocked) and {@code effect_handler_SWEEP()} (attack all eight neighbours in
 * turn). This class replaces the index with a cursor that {@link #moveNext()} advances.
 * <p>
 * The ring of nodes is built once, when the class loads, and is never changed afterwards, so all
 * loops share it safely. Each {@code new ClockwiseDirectionLoop()} owns only its cursor, which
 * starts at north, so any number of loops can run at once without affecting each other. This is
 * the Java equivalent of each C caller holding its own index into the shared const arrays.
 * <p>
 * coded on 260930 / commented in full on 260930
 *
 * @author Rowan Crowther
 */
public class ClockwiseDirectionLoop {
    /**
     * The north node of the shared ring, built once when the class loads. Every loop's cursor
     * starts here. The nodes are linked once and never modified, so sharing them between loops is
     * safe.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    private static final ClockwiseDirectionLoop.DirectionNode north = createAndLinkKeypadDirection();
    /**
     * This loop's cursor: the node whose offsets {@link #getXOffset()}, {@link #getYOffset()} and
     * {@link #getGrid()} report. It is an instance field, so each loop has its own position; it
     * starts at north and only {@link #moveNext()} changes it. Never null.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    private ClockwiseDirectionLoop.DirectionNode keypadDirection;

    /**
     * Create a loop with its own cursor, pointing at north. The ring itself is shared and already
     * built, so construction is cheap and does not touch any other loop.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    @CheckReturnValue
    @Contract(pure = true)
    public ClockwiseDirectionLoop() {
        keypadDirection = north;
    }

    /**
     * Build the nine direction nodes, link them into the clockwise ring in the order of C's
     * {@code clockwise_grid[]} (N, NE, E, SE, S, SW, W, NW, centre, back to N) and return the
     * north node. Called once, to initialise the static ring. Each node takes its offsets from {@link DirectionEnum#ddx()} and
     * {@link DirectionEnum#ddy()}; the centre node uses {@link DirectionEnum#DIR_NONE}, whose
     * offsets are (0, 0), matching the last entry {@code {0, 0}} of the C array.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    private static ClockwiseDirectionLoop.DirectionNode createAndLinkKeypadDirection() {
        ClockwiseDirectionLoop.DirectionNode south = new DirectionNode(DirectionEnum.DIR_S, DirectionEnum.DIR_S.ddx(), DirectionEnum.DIR_S.ddy());
        ClockwiseDirectionLoop.DirectionNode north = new DirectionNode(DirectionEnum.DIR_N, DirectionEnum.DIR_N.ddx(), DirectionEnum.DIR_N.ddy());
        ClockwiseDirectionLoop.DirectionNode west = new DirectionNode(DirectionEnum.DIR_W, DirectionEnum.DIR_W.ddx(), DirectionEnum.DIR_W.ddy());
        ClockwiseDirectionLoop.DirectionNode east = new DirectionNode(DirectionEnum.DIR_E, DirectionEnum.DIR_E.ddx(), DirectionEnum.DIR_E.ddy());
        ClockwiseDirectionLoop.DirectionNode northeast = new DirectionNode(DirectionEnum.DIR_NE, DirectionEnum.DIR_NE.ddx(), DirectionEnum.DIR_NE.ddy());
        ClockwiseDirectionLoop.DirectionNode southeast = new DirectionNode(DirectionEnum.DIR_SE, DirectionEnum.DIR_SE.ddx(), DirectionEnum.DIR_SE.ddy());
        ClockwiseDirectionLoop.DirectionNode northwest = new DirectionNode(DirectionEnum.DIR_NW, DirectionEnum.DIR_NW.ddx(), DirectionEnum.DIR_NW.ddy());
        ClockwiseDirectionLoop.DirectionNode southwest = new DirectionNode(DirectionEnum.DIR_SW, DirectionEnum.DIR_SW.ddx(), DirectionEnum.DIR_SW.ddy());
        ClockwiseDirectionLoop.DirectionNode centre = new DirectionNode(DirectionEnum.DIR_NONE, DirectionEnum.DIR_NONE.ddx(), DirectionEnum.DIR_NONE.ddy());

        north.setNext(northeast);
        northeast.setNext(east);
        east.setNext(southeast);
        southeast.setNext(south);
        south.setNext(southwest);
        southwest.setNext(west);
        west.setNext(northwest);
        northwest.setNext(centre);
        centre.setNext(north);

        return north;
    }

    /**
     * The column step of the direction the cursor is on: the {@code x} of the matching
     * {@code clockwise_grid[]} entry in C.
     * <p>
     * coded on 260930 / commented in full on 260930
     *
     * @return the column step of the current direction
     */
    public int getXOffset() {
        return keypadDirection.xOff;
    }

    /**
     * The row step of the direction the cursor is on: the {@code y} of the matching
     * {@code clockwise_grid[]} entry in C.
     * <p>
     * coded on 260930 / commented in full on 260930
     *
     * @return the row step of the current direction
     */
    public int getYOffset() {
        return keypadDirection.yOff;
    }

    /**
     * The step of the direction the cursor is on as a new {@link Loc}, the equivalent of reading
     * {@code clockwise_grid[d]} in C. The centre entry gives (0, 0).
     * <p>
     * coded on 260930 / commented in full on 260930
     *
     * @return the current direction's step as a {@link Loc}
     */
    @Contract(" -> new")
    public @NotNull Loc getGrid() {
        return Loc.row(getYOffset()).col(getXOffset());
    }

    /**
     * Advance this loop's cursor one step clockwise, the equivalent of {@code d = (d + 1) % 9}
     * over the nine entries (the eight compass directions and the centre). The C callers
     * take the modulus over eight instead, so they never visit the centre; here the centre is
     * part of the ring, and a caller that wants only compass headings must skip it. After the
     * centre the cursor wraps to north.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    public void moveNext() {
        keypadDirection = keypadDirection.getNext();
    }

    /**
     * One node in the circular direction ring: a direction with its step offsets and a link to
     * the next node clockwise.
     * <p>
     * coded on 260930 / commented in full on 260930
     *
     * @author Rowan Crowther
     */
    private static class DirectionNode {
        /**
         * The direction this node represents. Not read anywhere yet; the offsets below are what
         * callers use.
         */
        private final DirectionEnum dir;
        /**
         * Column step for this direction (C's {@code x} in {@code clockwise_grid[]}).
         */
        private int xOff;
        /**
         * Row step for this direction (C's {@code y} in {@code clockwise_grid[]}).
         */
        private int yOff;

        /**
         * The next node clockwise in the ring; set by {@link #setNext(DirectionNode)} when the
         * ring is linked, and never null afterwards.
         */
        private ClockwiseDirectionLoop.DirectionNode next;

        /**
         * Build a direction node from its direction and step offsets. The link to the next node
         * is left null until {@link #setNext(DirectionNode)} is called.
         *
         * @param direction the direction
         * @param xOffset   the column step
         * @param yOffset   the row step
         */
        public DirectionNode(@NotNull DirectionEnum direction, int xOffset, int yOffset) {
            dir = direction;
            xOff = xOffset;
            yOff = yOffset;
        }

        /**
         * Link this node to the next one in the ring.
         *
         * @param next the following node
         */
        public void setNext(@NotNull ClockwiseDirectionLoop.DirectionNode next) {
            this.next = next;
        }

        /**
         * @return the next node clockwise in the ring
         */
        @CheckReturnValue
        @Contract(pure = true)
        public ClockwiseDirectionLoop.DirectionNode getNext() {
            return next;
        }
    }
}