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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.middle.objects.enums.ResType;

/**
 * One combination of elemental protections that is worth more together than separately — the port of
 * C's {@code struct element_set} and its three-row table ({@code obj-power.c:84-99}).
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
 * not interleave. They cannot: the curse recursion happens strictly after the counting has finished.
 *
 * <p>Compare {@link FlagSet}, which does the same job for object flags and has no {@code resLevel}
 * because a flag is either present or not.
 *
 * <p>Class ElementSet commented in full on 260827.
 *
 * @author Rowan Crowther
 */
public class ElementSet {
    /**
     * Which group of elements this row counts - low or high resists. Matched against
     * {@link ElementPowers#getType()}.
     */
    private ResType type;
    /**
     * The level of protection an element must reach to count towards this row: 3 for the immunities
     * row, 1 for the two resist rows. An element at a higher level than this still counts, which is
     * why an immunity also counts as a resist.
     */
    private int resLevel;
    /**
     * Multiplier for the quadratic increment awarded for holding more than one of these -
     * {@code factor * count * count}. C's {@code element_sets[].factor}.
     */
    private int factor;
    /**
     * Flat bonus for holding the full set. {@code INHIBIT_POWER} on the immunities row, which stops
     * such an object being generated rather than pricing it.
     */
    private int bonus;
    /**
     * How many elements make a full set - 4 immunities, 4 low resists, 9 high resists.
     */
    private int size;
    /**
     * How many elements the object being priced holds at this row's level. Working state, zeroed by
     * the caller before each pass rather than data loaded once.
     */
    private int count;
    /**
     * The row's name as the power log spells it, e.g. {@code "low resists"}.
     */
    private String description;

    /**
     * Build one row of the element set table.
     *
     * <p>Constructor ElementSet commented in full on 260827.
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
     * @return the group of elements this row counts, matched against {@link ElementPowers#getType()}
     */
    public ResType getType() {
        return type;
    }

    /**
     * @return the protection level an element must reach to count towards this row; a higher level also counts
     */
    public int getResLevel() {
        return resLevel;
    }

    /**
     * @return the multiplier for the quadratic increment awarded for holding several of these
     */
    public int getFactor() {
        return factor;
    }

    /**
     * @return the flat bonus for holding the full set - {@code INHIBIT_POWER} on the immunities row
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * @return how many elements make a full set of this row
     */
    public int getSize() {
        return size;
    }

    /**
     * @return how many qualifying elements the object being priced holds - working state, valid only between the caller's zeroing pass and its read-back
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the running count of qualifying elements. Callers zero every row before a power pass and
     * increment as they walk the elements; nothing else should write it.
     *
     * @param count the new count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the row's name as the power log spells it
     */
    public String getDescription() {
        return description;
    }
}
