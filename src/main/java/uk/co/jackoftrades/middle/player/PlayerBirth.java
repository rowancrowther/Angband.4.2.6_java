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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The character-creation machinery - the port of C's {@code player-birth.c}, minus its parsing, its
 * command handlers and the point-buy interface.
 *
 * <p>C's file is mostly the birth screen: the stat roller, the point costs, the {@code do_cmd_*}
 * handlers behind each key the player presses, and the roman-numeral suffixes on a reused character
 * name. None of that belongs to the model, and none of it is here. What is left is the part that
 * builds a character out of a race and a class, and so far that is one method,
 * {@link #embody}.
 *
 * <p>The methods are static and take the player, as C's take {@code struct player *p}: a character
 * is being built, so there is nothing yet to be a method on. The class is a namespace.
 *
 * <p>Class PlayerBirth commented in full on 260901.
 *
 * @author Rowan Crowther
 */
public class PlayerBirth {
    private static final Logger logger = LogManager.getLogger(PlayerBirth.class);

    /**
     * Gives this player the body their race is built with — the slots they can wear things in.
     *
     * <p><b>Copies rather than shares.</b> A race's body is a template held once and used by every
     * member of that race; a player's body holds the items actually worn. Taking the reference
     * instead of a copy would have every character of a race wearing the same equipment.
     *
     * <p>Returns quietly if the player has no race yet, which happens during character creation
     * before a race is chosen.
     *
     * <p>Function embody commented in full on 260820.
     *
     * @param player
     */
    public static void embody(Player player) {
        if (player.getRace() == null)
            return;

        player.setBody(player.getRace().getBody().copy());
    }
}
