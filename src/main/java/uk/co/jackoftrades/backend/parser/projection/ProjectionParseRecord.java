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

package uk.co.jackoftrades.backend.parser.projection;

/**
 * @param code        Computer code for this projection used by the game. Must
 *                    match an {@link uk.co.jackoftrades.middle.combat.enums.ProjectionEnum}
 * @param name        Human readable name for this projection.
 *                    {@link uk.co.jackoftrades.middle.objects.Brand} is linked
 *                    by this
 * @param type        Either 'element', 'environs', or 'monstor'. Stored as a
 *                    {@link uk.co.jackoftrades.middle.combat.enums.ProjectionType}
 * @param desc        Descriptive name of this projection, used in object descriptions
 * @param playerDesc  Similar to 'desc', used primarily when describing: ball, orb...
 * @param blindDesc   How the projection is described when the player can't see it
 * @param lashDesc    Description used for the "lash" effect, i.e. spit or whip
 * @param numerator   Numerator of fraction of damage allowed through a player
 *                    resistance to an element
 * @param denominator Denominator of fraction of damage allowed through a player
 *                    resistance to an element. If this evaluates to 0, all damage
 *                    goes through. Can either be a single integer, or a simple
 *                    dice string
 * @param divisor     HP divisor used to divide the attacking creature's HP to
 *                    determine breath damage
 * @param damageCap   Maximum amount of damage regardless of monster HP before
 *                    resistance is applied
 * @param msgt        Message type {@link uk.co.jackoftrades.middle.enums.MessageType}
 *                    used by the message system.
 * @param obvious     Indicates whether the nature of the projection effect is
 *                    obvious to the player. Should be "1" if it is, or any other
 *                    non-negative number if it is not (normally "0")
 * @param willWake    Indicates whether this projection with automaticallly wake
 *                    effected monsters. Should be "1" if it will, or any other
 *                    non-negative number if it will not (normally "0")
 * @param colour      Colour of the projection. Use either the colour char or full
 *                    colour name (case-insensitive)
 * @param lineNumber  Line number on which this projection record starts in
 *                    lib/gamedata/projection.txt
 * @author Rowan Crowther
 */
public record ProjectionParseRecord(String code, String name, String type, String desc,
                                    String playerDesc, String blindDesc, String lashDesc,
                                    String numerator, String denominator, String divisor,
                                    String damageCap, String msgt, String obvious,
                                    String willWake, String colour, int lineNumber) {
}