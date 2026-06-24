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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.TValue;

/**
 * A possible item drop for a monster race: an item type (and optionally a
 * specific {@link ObjectKind}), the percentage chance it drops, and the quantity
 * range. When no specific kind is given the drop is "by base type". This is the
 * Java port of the C original's {@code struct monster_drop} ({@code src/monster.h}).
 *
 * @author ClaudeCode
 */
public class MonsterDrop {
    /**
     * The item type (tval) this drop produces.
     *
     * @author ClaudeCode
     */
    private TValue tVal;
    /**
     * The specific object kind dropped, or {@code null} for a by-base-type drop.
     *
     * @author ClaudeCode
     */
    private ObjectKind kind;
    /**
     * Percentage chance the drop occurs.
     *
     * @author ClaudeCode
     */
    private int percentChance;
    /**
     * Minimum quantity dropped.
     *
     * @author ClaudeCode
     */
    private int min;
    /**
     * Maximum quantity dropped.
     *
     * @author ClaudeCode
     */
    private int max;

    /**
     * Build a drop of a specific object kind.
     *
     * @param tVal          the item type
     * @param kind          the specific object kind
     * @param percentChance the drop chance
     * @param min           minimum quantity
     * @param max           maximum quantity
     * @author ClaudeCode
     */
    public MonsterDrop(TValue tVal, ObjectKind kind, int percentChance, int min, int max) {
        this.tVal = tVal;
        this.kind = kind;
        this.percentChance = percentChance;
        this.min = min;
        this.max = max;
    }

    /**
     * Build a by-base-type drop (no specific object kind).
     *
     * @param tVal          the item type
     * @param percentChance the drop chance
     * @param min           minimum quantity
     * @param max           maximum quantity
     * @author ClaudeCode
     */
    public MonsterDrop(TValue tVal, int percentChance, int min, int max) {
        this.tVal = tVal;
        this.kind = null;
        this.percentChance = percentChance;
        this.min = min;
        this.max = max;
    }

    /**
     * @return true if this is a by-base-type drop (no specific kind)
     * @author ClaudeCode
     */
    private boolean isBase() {
        return this.kind == null;
    }
}