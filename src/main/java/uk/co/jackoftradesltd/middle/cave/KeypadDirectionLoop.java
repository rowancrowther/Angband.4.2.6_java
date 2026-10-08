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
 * A circular, singly-linked ring of the eight compass directions (plus centre)
 * in the C original's "keypad" scan order (the {@code ddd} ordering:
 * S, N, E, W, SE, SW, NE, NW, then centre). Iterating it reproduces the order in
 * which the C code tries directions when, for example, searching outward from a
 * grid.
 * <p>
 * It is the Java form of the C original's paired arrays {@code ddgrid_ddd[]} (and the older
 * {@code ddx_ddd[]} / {@code ddy_ddd[]}) in {@code cave.c}, which hold the nine offsets in
 * {@code ddd[]} order. The C code walks them with a caller-owned index, typically
 * {@code for (d = 0; d < 9; d++)}. This class replaces the index with a cursor that
 * {@link #moveNext()} advances. The nine steps are S (0,1), N (0,-1), E (1,0), W (-1,0),
 * SE (1,1), SW (-1,1), NE (1,-1), NW (-1,-1) and the centre (0,0); after the centre the
 * cursor wraps to S, which C would need an explicit {@code % 9} for.
 * <p>
 * The ring of nodes is built once, when the class loads, and is never changed afterwards, so all
 * loops share it safely. Each {@code new KeypadDirectionLoop()} owns only its cursor, which starts
 * at S, so any number of loops can run at once without affecting each other. This is the Java
 * equivalent of each C caller holding its own index into the shared const arrays, and matches
 * its sibling {@link ClockwiseDirectionLoop}.
 * <p>
 * coded on 260930 / commented in full on 260930
 *
 * @author Rowan Crowther
 */
public class KeypadDirectionLoop {
    /**
     * The south node of the shared ring, built once when the class loads. Every loop's cursor
     * starts here, as {@code ddd[0]} is south in C. The nodes are linked once and never modified,
     * so sharing them between loops is safe.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    private static final KeypadDirectionLoop.DirectionNode south = createAndLinkKeypadDirection();

    /**
     * This loop's cursor: the node whose offsets {@link #getXOffset()}, {@link #getYOffset()} and
     * {@link #getGrid()} report. It is an instance field, so each loop has its own position; it
     * starts at south and only {@link #moveNext()} changes it. Never null.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    private DirectionNode keypadDirection;

    /**
     * Create a loop with its own cursor, pointing at south. The ring itself is shared and already
     * built, so construction is cheap and does not touch any other loop.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    public KeypadDirectionLoop() {
        keypadDirection = south;
    }

    /**
     * Build the nine direction nodes, link them into the keypad ring in the order of C's
     * {@code ddgrid_ddd[]} (S, N, E, W, SE, SW, NE, NW, centre, back to S) and return the south
     * node. Called once, to initialize the static ring. Each node takes its offsets from
     * {@link DirectionEnum#ddx()} and {@link DirectionEnum#ddy()}; the centre node uses
     * {@link DirectionEnum#DIR_NONE}, whose offsets are (0, 0), matching the last entry
     * {@code {0, 0}} of the C array.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    private static KeypadDirectionLoop.DirectionNode createAndLinkKeypadDirection() {
        DirectionNode south = new DirectionNode(DirectionEnum.DIR_S, DirectionEnum.DIR_S.ddx(), DirectionEnum.DIR_S.ddy());
        DirectionNode north = new DirectionNode(DirectionEnum.DIR_N, DirectionEnum.DIR_N.ddx(), DirectionEnum.DIR_N.ddy());
        DirectionNode west = new DirectionNode(DirectionEnum.DIR_W, DirectionEnum.DIR_W.ddx(), DirectionEnum.DIR_W.ddy());
        DirectionNode east = new DirectionNode(DirectionEnum.DIR_E, DirectionEnum.DIR_E.ddx(), DirectionEnum.DIR_E.ddy());
        DirectionNode northeast = new DirectionNode(DirectionEnum.DIR_NE, DirectionEnum.DIR_NE.ddx(), DirectionEnum.DIR_NE.ddy());
        DirectionNode southeast = new DirectionNode(DirectionEnum.DIR_SE, DirectionEnum.DIR_SE.ddx(), DirectionEnum.DIR_SE.ddy());
        DirectionNode northwest = new DirectionNode(DirectionEnum.DIR_NW, DirectionEnum.DIR_NW.ddx(), DirectionEnum.DIR_NW.ddy());
        DirectionNode southwest = new DirectionNode(DirectionEnum.DIR_SW, DirectionEnum.DIR_SW.ddx(), DirectionEnum.DIR_SW.ddy());
        DirectionNode centre = new DirectionNode(DirectionEnum.DIR_NONE, DirectionEnum.DIR_NONE.ddx(), DirectionEnum.DIR_NONE.ddy());

        south.setNext(north);
        north.setNext(east);
        east.setNext(west);
        west.setNext(southeast);
        southeast.setNext(southwest);
        southwest.setNext(northeast);
        northeast.setNext(northwest);
        northwest.setNext(centre);
        centre.setNext(south);

        return south;
    }

    /**
     * The column step of the direction the cursor is on: the {@code x} of the matching
     * {@code ddgrid_ddd[]} entry in C.
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
     * {@code ddgrid_ddd[]} entry in C.
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
     * {@code ddgrid_ddd[d]} in C. The centre entry gives (0, 0).
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
     * Advance this loop's cursor one step along the keypad ring, the equivalent of
     * {@code d = (d + 1) % 9} over {@code ddgrid_ddd[]}. The centre is the ninth entry, so a
     * caller that wants only compass headings must skip it. After the centre the cursor wraps
     * to south.
     * <p>
     * coded on 260930 / commented in full on 260930
     */
    public void moveNext() {
        keypadDirection = keypadDirection.getNext();
    }

    /**
     * One node in the circular direction ring: a direction with its step offsets and a link to
     * the next node in keypad order.
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
         * Column step for this direction (C's {@code x} in {@code ddgrid_ddd[]}).
         */
        private int xOff;
        /**
         * Row step for this direction (C's {@code y} in {@code ddgrid_ddd[]}).
         */
        private int yOff;

        /**
         * The next node in keypad order; set by {@link #setNext(DirectionNode)} when the ring is
         * linked, and never null afterwards.
         */
        private DirectionNode next;

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
        public void setNext(@NotNull DirectionNode next) {
            this.next = next;
        }

        /**
         * @return the next node in the ring
         */
        @CheckReturnValue
        @Contract(pure = true)
        public DirectionNode getNext() {
            return next;
        }
    }
}
