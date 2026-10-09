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

package uk.co.jackoftradesltd.middle.objects;

import uk.co.jackoftradesltd.middle.objects.enums.ResType;

/**
 * One combination of elemental protections that is worth more together than separately — the port of
 * C's {@code struct element_set} and its three-row table {@code element_sets[]} ({@code obj-power.c}).
 *
 * <p>Three rows: immunities, low resists and high resists. Each names a group of elements
 * ({@link #getType()}) and a level of protection ({@link #getResLevel()}), and an object counts
 * towards a row for every element it holds at that level or better. The count then buys two things:
 * a quadratic increment for holding several ({@code factor * count * count}) and a flat bonus for
 * holding the whole set.
 *
 * <p>The immunities row's bonus is {@code INHIBIT_POWER}, which is not a price but a refusal — an
 * object with all four low immunities is not meant to be generated at all.
 *
 * <p><b>Counted in place, and re-zeroed by each caller.</b> {@link #count} is working state, not
 * data: {@code ItemObject.elementPower} clears every row, walks the elements incrementing rows as it
 * goes, and only then reads the counts back. C does exactly the same on its own static table, and
 * the port keeps the shape — so the rows are shared mutable state, and two power calculations must
 * not interleave. They cannot: {@code ItemObject.objectPower} calls {@code elementPower} to
 * completion before it calls {@code cursePower}, and the curse recursion is the only way a second
 * power calculation can start.
 *
 * <p><b>Differences from C.</b> The table is {@code ObjectRegistry.elementSets}, a list of the three
 * rows in C's order, which {@code GameConstants} builds in code as C builds its initializer. The
 * {@code type} column is a {@link ResType} rather than C's anonymous {@code T_LRES}/{@code T_HRES}
 * enum, and C's {@code res_level} and {@code desc} are {@code resLevel} and {@code description}. C
 * keeps the struct and its table private to {@code obj-power.c}; here the class is public so that
 * {@code ItemObject} can reach the rows through the registry.
 *
 * <p>Compare {@link FlagSet}, which does the same job for object flags and has no {@code resLevel}
 * because a flag is either present or not, and {@link ElementPowers}, whose {@code type} column this
 * class's rows are matched against.
 *
 * <p>Class ElementSet coded before 260827, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class ElementSet {
    /**
     * Which group of elements this row counts - low or high resists. Matched against
     * {@link ElementPowers#getType()}, as C compares {@code element_sets[].type} with
     * {@code el_powers[].type}.
     *
     * <p>Field type coded before 261009, commented in full on 261009.
     */
    private ResType type;
    /**
     * The level of protection an element must reach to count towards this row: 3 for the immunities
     * row, 1 for the two resist rows. An element at a higher level than this still counts, which is
     * why an immunity also counts as a resist. C's {@code element_sets[].res_level}, compared with
     * {@code <=} against the object's resistance level for the element.
     *
     * <p>Field resLevel coded before 261009, commented in full on 261009.
     */
    private int resLevel;
    /**
     * Multiplier for the quadratic increment awarded for holding more than one of these -
     * {@code factor * count * count}. C's {@code element_sets[].factor}: 6 for immunities, 1 for low
     * resists and 2 for high resists.
     *
     * <p>Field factor coded before 261009, commented in full on 261009.
     */
    private int factor;
    /**
     * Flat bonus for holding the full set. {@code INHIBIT_POWER} on the immunities row, which stops
     * such an object being generated rather than pricing it; 10 on each resist row.
     *
     * <p>Field bonus coded before 261009, commented in full on 261009.
     */
    private int bonus;
    /**
     * How many elements make a full set - 4 immunities, 4 low resists, 9 high resists. The bonus is
     * awarded when the count equals this exactly.
     *
     * <p>Field size coded before 261009, commented in full on 261009.
     */
    private int size;
    /**
     * How many elements the object being priced holds at this row's level. Working state, zeroed by
     * the caller before each pass rather than data loaded once.
     *
     * <p>Field count coded before 261009, commented in full on 261009.
     */
    private int count;
    /**
     * The row's name as the power log spells it, e.g. {@code "low resists"}. C's
     * {@code element_sets[].desc}, used in the "multiple" and "full set" log lines of
     * {@code element_power}.
     *
     * <p>Field description coded before 261009, commented in full on 261009.
     */
    private String description;

    /**
     * Build one row of the element set table. The argument order is C's initializer order, so a row
     * reads the same as its line in {@code element_sets[]}.
     *
     * <p>Constructor ElementSet coded before 260827, commented in full on 261009.
     *
     * @param type        the group of elements this row counts
     * @param resLevel    the protection level an element must reach to count
     * @param factor      multiplier for the quadratic multiple-holding increment
     * @param bonus       flat bonus for a full set
     * @param size        how many elements make a full set
     * @param count       starting count, normally zero
     * @param description the row's name for the power log
     */
    public ElementSet(ResType type, int resLevel, int factor, int bonus, int size, int count, String description) {
        this.type = type;
        this.resLevel = resLevel;
        this.factor = factor;
        this.bonus = bonus;
        this.size = size;
        this.count = count;
        this.description = description;
    }

    /**
     * The group of elements this row counts. {@code element_power} compares it with each element's
     * {@link ElementPowers#getType()} when it tracks combinations.
     *
     * <p>Function getType coded before 261009, commented in full on 261009.
     *
     * @return the group of elements this row counts, matched against {@link ElementPowers#getType()}
     */
    public ResType getType() {
        return type;
    }

    /**
     * The protection level an element must reach to count towards this row. The comparison in
     * {@code element_power} is {@code resLevel <= the object's level}, so a higher level also counts.
     *
     * <p>Function getResLevel coded before 261009, commented in full on 261009.
     *
     * @return the protection level an element must reach to count towards this row; a higher level also counts
     */
    public int getResLevel() {
        return resLevel;
    }

    /**
     * The multiplier in the quadratic increment {@code factor * count * count}, which
     * {@code element_power} awards only when the count is above one.
     *
     * <p>Function getFactor coded before 261009, commented in full on 261009.
     *
     * @return the multiplier for the quadratic increment awarded for holding several of these
     */
    public int getFactor() {
        return factor;
    }

    /**
     * The flat bonus {@code element_power} adds when the count equals {@link #getSize()}.
     *
     * <p>Function getBonus coded before 261009, commented in full on 261009.
     *
     * @return the flat bonus for holding the full set - {@code INHIBIT_POWER} on the immunities row
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * The count that makes a full set. {@code element_power} compares it for equality, so a count
     * that somehow passed it would earn no bonus.
     *
     * <p>Function getSize coded before 261009, commented in full on 261009.
     *
     * @return how many elements make a full set of this row
     */
    public int getSize() {
        return size;
    }

    /**
     * The running count of qualifying elements. It is only meaningful between the caller's zeroing
     * pass and its read-back; outside that window it holds whatever the last object left behind.
     *
     * <p>Function getCount coded before 261009, commented in full on 261009.
     *
     * @return how many qualifying elements the object being priced holds - working state, valid only between the caller's zeroing pass and its read-back
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the running count of qualifying elements. Callers zero every row before a power pass and
     * increment as they walk the elements; nothing else should write it. C does the same with direct
     * writes to {@code element_sets[].count}.
     *
     * <p>Function setCount coded before 261009, commented in full on 261009.
     *
     * @param count the new count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * The row's name as the power log spells it - C's {@code element_sets[].desc}, used in the
     * "Add %d power for multiple %s" and "Add %d power for full set of %s" log lines.
     *
     * <p>Function getDescription coded before 261009, commented in full on 261009.
     *
     * @return the row's name as the power log spells it
     */
    public String getDescription() {
        return description;
    }
}