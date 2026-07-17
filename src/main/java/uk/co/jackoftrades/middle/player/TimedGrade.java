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

import uk.co.jackoftrades.frontend.colour.enums.ColourType;

/**
 * One severity grade of a timed effect — a named band within a status's range, with its own
 * colour and onset/fade messages.
 *
 * <p>Ports the C {@code struct timed_grade} ({@code player-timed.h}), built from the
 * {@code grade:} lines in {@code player_timed.txt}. A timed effect (see
 * {@link uk.co.jackoftrades.middle.player.enums.TimedEffect}) is divided into ordered grades —
 * for example stun escalates "stun" → "heavy stun" → "knocked out" — each covering up to a
 * maximum value and showing its own coloured label and transition text.
 *
 * <p><b>Why {@code max} matters:</b> grades partition the effect's numeric range; the active
 * grade is the lowest one whose {@link #max} the current value does not exceed, which is how a
 * single counter maps to a descriptive severity. The up/down messages fire when the player
 * crosses into a stronger or weaker grade.
 *
 * @author Rowan Crowther
 */
public record TimedGrade(int Grade,
                         ColourType colour,
                         int max,
                         String status,
                         String upMsg,
                         String downMsg) {
}