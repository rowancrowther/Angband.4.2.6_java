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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.cave;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;

/**
 * A circular, singly-linked ring of the eight compass directions (plus centre)
 * in the C original's "keypad" scan order (the {@code ddd} ordering:
 * S, N, E, W, SE, SW, NE, NW, then centre). Iterating it reproduces the order in
 * which the C code tries directions when, for example, searching outward from a
 * grid. Implemented as a lazily-built singleton, like its clockwise sibling
 * {@link ClockwiseDirectionLoop}.
 *
 * @author ClaudeCode
 */
public class KeypadDirectionLoop {
    /**
     * The ring cursor: the direction node currently pointed at.
     *
     * @author ClaudeCode
     */
    private static DirectionNode keypadDirection;
    /**
     * The singleton instance (constructing it builds and links the ring).
     *
     * @author ClaudeCode
     */
    private static final KeypadDirectionLoop instance = new KeypadDirectionLoop();

    /**
     * Private constructor: builds and links the keypad-order direction ring.
     *
     * @author ClaudeCode
     */
    @CheckReturnValue
    @Contract(pure = true)
    private KeypadDirectionLoop() {
        createAndLinkKeypadDirection();
    }

    /**
     * @return the singleton keypad direction loop
     * @author ClaudeCode
     */
    public static KeypadDirectionLoop getLoop() {
        return instance;
    }

    /**
     * Build the nine direction nodes and link them into the keypad-order ring.
     *
     * @author ClaudeCode
     */
    private static void createAndLinkKeypadDirection() {
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
    }

    /**
     * @return the column step of the current direction
     * @author ClaudeCode
     */
    public static int getXOffset() {
        return keypadDirection.xOff;
    }

    /**
     * @return the row step of the current direction
     * @author ClaudeCode
     */
    public static int getYOffset() {
        return keypadDirection.yOff;
    }

    /**
     * @return the current direction's step as a {@link Loc}
     * @author ClaudeCode
     */
    @Contract(" -> new")
    public static @NotNull Loc getGrid() {
        return new Loc(getXOffset(), getYOffset());
    }

    /**
     * Advance the shared cursor to the next direction in the keypad-order ring.
     *
     * @author ClaudeCode
     */
    public static void moveNext() {
        keypadDirection = keypadDirection.getNext();
    }

    /**
     * One node in the circular direction ring: a direction with its step offsets
     * and a link to the next node in keypad order.
     *
     * @author ClaudeCode
     */
    private static class DirectionNode {
        /**
         * The direction this node represents.
         *
         * @author ClaudeCode
         */
        private final DirectionEnum dir;
        /**
         * Column step for this direction.
         *
         * @author ClaudeCode
         */
        private int xOff;
        /**
         * Row step for this direction.
         *
         * @author ClaudeCode
         */
        private int yOff;

        /**
         * The next node in the ring.
         *
         * @author ClaudeCode
         */
        private DirectionNode next;

        /**
         * Build a direction node from its direction and step offsets.
         *
         * @param direction the direction
         * @param xOffset   the column step
         * @param yOffset   the row step
         * @author ClaudeCode
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
         * @author ClaudeCode
         */
        public void setNext(@NotNull DirectionNode next) {
            this.next = next;
        }

        /**
         * @return the next node in the ring
         * @author ClaudeCode
         */
        @CheckReturnValue
        @Contract(pure = true)
        public DirectionNode getNext() {
            return next;
        }
    }
}
