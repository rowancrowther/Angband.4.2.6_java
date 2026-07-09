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

package uk.co.jackoftrades.middle.game.event;

import uk.co.jackoftrades.middle.enums.Stats;

import java.util.HashMap;

/**
 * {@link GameEventData} payload for the point-buy stage of character creation —
 * the points currently spent per stat, the cost to increment each stat, and the
 * points still available. Drives the stat point-buy UI.
 *
 * @author Rowan Crowther
 */
public class EventDataBirthPoints implements GameEventData {
    /**
     * Points currently allocated to each stat.
     *
     * @author Rowan Crowther
     */
    private HashMap<Stats, Integer> points;
    /**
     * Cost to increment each stat by one.
     *
     * @author Rowan Crowther
     */
    private HashMap<Stats, Integer> incPoints;
    /**
     * Points still available to spend.
     *
     * @author Rowan Crowther
     */
    private int remaining;

    /**
     * Build a birth point-buy payload.
     *
     * @param points    points allocated per stat
     * @param incPoints increment cost per stat
     * @param remaining points still available
     * @author Rowan Crowther
     */
    public EventDataBirthPoints(HashMap<Stats, Integer> points, HashMap<Stats, Integer> incPoints, int remaining) {
        this.points = points;
        this.incPoints = incPoints;
        this.remaining = remaining;
    }

    /**
     * @return the points allocated per stat
     * @author Rowan Crowther
     */
    public HashMap<Stats, Integer> getPoints() {
        return points;
    }

    /**
     * @return the increment cost per stat
     * @author Rowan Crowther
     */
    public HashMap<Stats, Integer> getIncPoints() {
        return incPoints;
    }

    /**
     * @return the points still available
     * @author Rowan Crowther
     */
    public int getRemaining() {
        return remaining;
    }
}
