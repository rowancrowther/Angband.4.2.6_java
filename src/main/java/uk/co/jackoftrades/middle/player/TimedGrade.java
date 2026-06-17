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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.frontend.colour.enums.ColourType;

public class TimedGrade {
    private int grade;
    private ColourType colour;
    int max;
    String name;
    String upMsg;
    String downMsg;

    public TimedGrade(int grade, ColourType colour, int max, String name, String upMsg, String downMsg) {
        this.grade = grade; // handled in the parser, hard coded to 0 in the parser when read in.
        this.colour = colour;
        this.max = max;
        this.name = name;
        this.upMsg = upMsg;
        this.downMsg = downMsg;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}