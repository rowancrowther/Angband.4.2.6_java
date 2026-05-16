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

public class ClockwiseDirectionLoop {
    private static ClockwiseDirectionLoop.DirectionNode keypadDirection;
    private static final ClockwiseDirectionLoop instance = new ClockwiseDirectionLoop();

    @CheckReturnValue
    @Contract(pure = true)
    private ClockwiseDirectionLoop() {
        createAndLinkKeypadDirection();
    }

    public static ClockwiseDirectionLoop getLoop() {
        return instance;
    }

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

    public static int getXOffset() {
        return keypadDirection.xOff;
    }

    public static int getYOffset() {
        return keypadDirection.yOff;
    }

    @Contract(" -> new")
    public static @NotNull Loc getGrid() {
        return new Loc(getXOffset(), getYOffset());
    }

    public static void moveNext() {
        keypadDirection = keypadDirection.getNext();
    }

    private static class DirectionNode {
        private final DirectionEnum dir;
        private int xOff;
        private int yOff;

        private ClockwiseDirectionLoop.DirectionNode next;

        public DirectionNode(@NotNull DirectionEnum direction, int xOffset, int yOffset) {
            dir = direction;
            xOff = xOffset;
            yOff = yOffset;
        }

        public void setNext(@NotNull ClockwiseDirectionLoop.DirectionNode next) {
            this.next = next;
        }

        @CheckReturnValue
        @Contract(pure = true)
        public ClockwiseDirectionLoop.DirectionNode getNext() {
            return next;
        }
    }
}