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
 * ordered <em>clockwise</em> (N → NE → E → SE → S → SW → W → NW → centre → N…).
 * Walking this ring reproduces the C original's clockwise neighbour-scanning used
 * during level generation and monster movement. Implemented as a lazily-built
 * singleton; {@link #moveNext()} advances the shared cursor and the offset
 * accessors report the current direction's step.
 *
 * @author ClaudeCode
 */
public class ClockwiseDirectionLoop {
    /**
     * The ring cursor: the direction node currently pointed at.
     *
     * @author ClaudeCode
     */
    private static ClockwiseDirectionLoop.DirectionNode keypadDirection;
    /**
     * The singleton instance (constructing it builds and links the ring).
     *
     * @author ClaudeCode
     */
    private static final ClockwiseDirectionLoop instance = new ClockwiseDirectionLoop();

    /**
     * Private constructor: builds and links the clockwise direction ring.
     *
     * @author ClaudeCode
     */
    @CheckReturnValue
    @Contract(pure = true)
    private ClockwiseDirectionLoop() {
        createAndLinkKeypadDirection();
    }

    /**
     * @return the singleton clockwise direction loop
     * @author ClaudeCode
     */
    public static ClockwiseDirectionLoop getLoop() {
        return instance;
    }

    /**
     * Build the nine direction nodes and link them into the clockwise ring.
     *
     * @author ClaudeCode
     */
    private static void createAndLinkKeypadDirection() {
        ClockwiseDirectionLoop.DirectionNode south = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_S, DirectionEnum.DIR_S.ddx(), DirectionEnum.DIR_S.ddy());
        ClockwiseDirectionLoop.DirectionNode north = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_N, DirectionEnum.DIR_N.ddx(), DirectionEnum.DIR_N.ddy());
        ClockwiseDirectionLoop.DirectionNode west = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_W, DirectionEnum.DIR_W.ddx(), DirectionEnum.DIR_W.ddy());
        ClockwiseDirectionLoop.DirectionNode east = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_E, DirectionEnum.DIR_E.ddx(), DirectionEnum.DIR_E.ddy());
        ClockwiseDirectionLoop.DirectionNode northeast = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_NE, DirectionEnum.DIR_NE.ddx(), DirectionEnum.DIR_NE.ddy());
        ClockwiseDirectionLoop.DirectionNode southeast = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_SE, DirectionEnum.DIR_SE.ddx(), DirectionEnum.DIR_SE.ddy());
        ClockwiseDirectionLoop.DirectionNode northwest = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_NW, DirectionEnum.DIR_NW.ddx(), DirectionEnum.DIR_NW.ddy());
        ClockwiseDirectionLoop.DirectionNode southwest = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_SW, DirectionEnum.DIR_SW.ddx(), DirectionEnum.DIR_SW.ddy());
        ClockwiseDirectionLoop.DirectionNode centre = new ClockwiseDirectionLoop.DirectionNode(DirectionEnum.DIR_NONE, DirectionEnum.DIR_NONE.ddx(), DirectionEnum.DIR_NONE.ddy());

        north.setNext(northeast);
        northeast.setNext(east);
        east.setNext(southeast);
        southeast.setNext(south);
        south.setNext(southwest);
        southwest.setNext(west);
        west.setNext(northwest);
        northwest.setNext(centre);
        centre.setNext(north);
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
     * Advance the shared cursor to the next direction in the clockwise ring.
     *
     * @author ClaudeCode
     */
    public static void moveNext() {
        keypadDirection = keypadDirection.getNext();
    }

    /**
     * One node in the circular direction ring: a direction with its step offsets
     * and a link to the next node clockwise.
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
         * The next node clockwise in the ring.
         *
         * @author ClaudeCode
         */
        private ClockwiseDirectionLoop.DirectionNode next;

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
        public void setNext(@NotNull ClockwiseDirectionLoop.DirectionNode next) {
            this.next = next;
        }

        /**
         * @return the next node clockwise in the ring
         * @author ClaudeCode
         */
        @CheckReturnValue
        @Contract(pure = true)
        public ClockwiseDirectionLoop.DirectionNode getNext() {
            return next;
        }
    }
}