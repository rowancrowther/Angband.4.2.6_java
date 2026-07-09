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
public class TimedGrade {
    /**
     * This grade's ordinal within its effect; assigned by the parser after construction (see {@link #setGrade}).
     */
    private int grade;
    /** Colour used to render this grade's label. */
    private ColourType colour;
    /** Upper bound of the effect value covered by this grade (C: {@code timed_grade.max}). */
    int max;
    /** Display name of this grade, e.g. {@code "heavy stun"}. */
    String name;
    /** Message shown when the player worsens <em>into</em> this grade. */
    String upMsg;
    /** Message shown when the player improves <em>out of</em> this grade. */
    String downMsg;

    /**
     * Creates a grade band for a timed effect.
     *
     * @param grade   the grade ordinal (the parser passes a placeholder here — see field note)
     * @param colour  display colour for the label
     * @param max     upper bound of the effect value this grade covers
     * @param name    display name
     * @param upMsg   message on worsening into this grade
     * @param downMsg message on improving out of this grade
     */
    public TimedGrade(int grade, ColourType colour, int max, String name, String upMsg, String downMsg) {
        this.grade = grade; // handled in the parser, hard coded to 0 in the parser when read in.
        this.colour = colour;
        this.max = max;
        this.name = name;
        this.upMsg = upMsg;
        this.downMsg = downMsg;
    }

    /**
     * @return this grade's ordinal within its effect
     */
    public int getGrade() {
        return grade;
    }

    /**
     * Assigns this grade's ordinal.
     *
     * <p>Used by the parser, which constructs each grade with a placeholder {@code 0} and then
     * sets the real sequential index once the grade's position in the effect is known.
     *
     * @param grade the ordinal to assign
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }
}