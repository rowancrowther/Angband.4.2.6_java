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

package uk.co.jackoftradesltd.channel.messages.data;

import uk.co.jackoftradesltd.middle.enums.Stats;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link GameEventData} payload for the point-buy stage of character creation —
 * the points currently spent per stat, the cost to increment each stat, and the
 * points still available. Drives the stat point-buy UI.
 * <p>
 * Port of C's anonymous {@code birthpoints} struct nested in {@code game_event_data}
 * ({@code game-event.h:133-138}), populated and dispatched by
 * {@code event_signal_birthpoints} ({@code game-event.c:194}). C holds the two point
 * arrays as {@code const int *}, each indexed {@code 0 <= i < STAT_MAX}; the port
 * carries the same data as {@code Map<Stats, Integer>} so a caller reads a stat by
 * its {@link Stats} constant rather than by the raw index C uses to walk the array.
 * That is purely a representational change — the C side never mutates the struct
 * through the pointers it hands over, so there is no aliasing behaviour lost by
 * copying into a map instead of borrowing the caller's arrays.
 * <p>
 * Class EventDataBirthPoints coded before 260915, commented in full on 260915.
 *
 * @author Rowan Crowther
 */
public class EventDataBirthPoints implements GameEventData {
    /**
     * Points currently allocated to each stat, keyed the way C's {@code points[i]}
     * is indexed — one entry per real stat, {@code STAT_STR} through {@code STAT_CON}.
     * <p>
     * Field points coded before 260915, commented in full on 260915.
     */
    private Map<Stats, Integer> points;
    /**
     * Cost to increment each stat by one, the port of C's {@code inc_points[i]} —
     * how many more points the next increase of that stat would cost.
     * <p>
     * Field incPoints coded before 260915, commented in full on 260915.
     */
    private Map<Stats, Integer> incPoints;
    /**
     * Points still available to spend, the direct port of C's {@code remaining}.
     * <p>
     * Field remaining coded before 260915, commented in full on 260915.
     */
    private int remaining;

    /**
     * Build a birth point-buy payload, the port of {@code event_signal_birthpoints}'s
     * three parameters ({@code game-event.c:194-203}) collected into one payload
     * object rather than assigned into a shared union field by field.
     *
     * @param points    points allocated per stat
     * @param incPoints increment cost per stat
     * @param remaining points still available
     *                  <p>
     *                  Constructor EventDataBirthPoints coded before 260915, commented in full on 260915.
     */
    public EventDataBirthPoints(Map<Stats, Integer> points, Map<Stats, Integer> incPoints, int remaining) {
        this.points = points;
        this.incPoints = incPoints;
        this.remaining = remaining;
    }

    /**
     * The points allocated per stat, the port of C's {@code data->birthpoints.points}.
     * <p>
     * Method getPoints coded before 260915, commented in full on 260915.
     *
     * @return the points allocated per stat
     */
    public Map<Stats, Integer> getPoints() {
        return points;
    }

    /**
     * The increment cost per stat, the port of C's {@code data->birthpoints.inc_points}.
     * <p>
     * Method getIncPoints coded before 260915, commented in full on 260915.
     *
     * @return the increment cost per stat
     */
    public Map<Stats, Integer> getIncPoints() {
        return incPoints;
    }

    /**
     * The points still available to spend, the port of C's
     * {@code data->birthpoints.remaining}.
     * <p>
     * Method getRemaining coded before 260915, commented in full on 260915.
     *
     * @return the points still available
     */
    public int getRemaining() {
        return remaining;
    }
}
