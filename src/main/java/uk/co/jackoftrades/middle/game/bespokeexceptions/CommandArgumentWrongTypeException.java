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

package uk.co.jackoftrades.middle.game.bespokeexceptions;

/**
 * Thrown when a command argument is requested as one type but was stored as another - the port's
 * counterpart to C returning {@code CMD_ARG_WRONG_TYPE}.
 *
 * <p>Because the port models argument values as a sealed {@link
 * uk.co.jackoftrades.middle.game.gameengine.CommandArgumentData} hierarchy, a genuine type mismatch
 * is a programming error rather than an expected control-flow value, so it surfaces as an unchecked
 * exception.
 *
 * @author Rowan Crowther
 */
public class CommandArgumentWrongTypeException extends RuntimeException {
    /**
     * @param message description of the mismatch (which name was requested, and the types involved)
     */
    public CommandArgumentWrongTypeException(String message) {
        super(message);
    }
}
