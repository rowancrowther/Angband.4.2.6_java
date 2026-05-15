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
import org.jspecify.annotations.NonNull;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;

public class KeypadDirectionLoop {
    private static DirectionNode keypadDirection;
    private static final KeypadDirectionLoop instance = new KeypadDirectionLoop();

    @CheckReturnValue
    @Contract(pure = true)
    private KeypadDirectionLoop() {
        createAndLinkKeypadDirection();
    }

    public static KeypadDirectionLoop getLoop() {
        return instance;
    }

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

    public static int getXOffset() {
        return keypadDirection.xOff;
    }

    public static int getYOffset() {
        return keypadDirection.yOff;
    }

    @Contract(" -> new")
    public static @NonNull Loc getGrid() {
        return new Loc(getXOffset(), getYOffset());
    }

    public static void moveNext() {
        keypadDirection = keypadDirection.getNext();
    }

    private static class DirectionNode {
        private final DirectionEnum dir;
        private int xOff;
        private int yOff;

        private DirectionNode next;

        public DirectionNode(@NotNull DirectionEnum direction, int xOffset, int yOffset) {
            dir = direction;
            xOff = xOffset;
            yOff = yOffset;
        }

        public void setNext(@NotNull DirectionNode next) {
            this.next = next;
        }

        @CheckReturnValue
        @Contract(pure = true)
        public DirectionNode getNext() {
            return next;
        }
    }
}
