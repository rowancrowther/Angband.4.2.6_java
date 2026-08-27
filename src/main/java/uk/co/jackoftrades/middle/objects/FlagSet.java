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

import uk.co.jackoftrades.middle.objects.enums.ObjectFlagType;

/**
 * One family of object flags that is worth more held together than separately — the port of C's
 * {@code struct flag_set} and its three-row table ({@code obj-power.c:64-75}).
 *
 * <p>Three rows: sustains, protections and miscellaneous abilities. An object counts towards a row
 * for every flag it carries whose subtype matches {@link #getType()}, and the count buys a quadratic
 * increment for holding several ({@code factor * count * count}) plus a flat bonus for holding the
 * whole family.
 *
 * <p>The element counterpart is {@link ElementSet}, which is the same idea one field wider: an
 * element can be held at several strengths, so it carries a required resistance level, where a flag
 * is simply present or absent.
 *
 * <p><b>Counted in place, and re-zeroed by each caller.</b> {@link #count} is working state, not
 * data: {@code ItemObject.flagsPower} clears every row, walks the object's flags incrementing rows
 * as it goes, and reads the counts back afterwards. C does the same on its own static table. The
 * rows are therefore shared mutable state and two power calculations must not interleave; they
 * cannot, because the curse recursion happens strictly after the counting is done.
 *
 * <p>Class FlagSet commented in full on 260827.
 *
 * @author Rowan Crowther
 */
public class FlagSet {
    /**
     * Which family of flags this row counts, matched against an
     * {@code ObjectProperty}'s subtype - sustains, protections or miscellaneous abilities.
     */
    private ObjectFlagType type;
    /**
     * Multiplier for the quadratic increment awarded for holding more than one of these -
     * {@code factor * count * count}. C's {@code flag_sets[].factor}.
     */
    private int factor;
    /**
     * Flat bonus for holding every flag in the family.
     */
    private int bonus;
    /**
     * How many flags make a full set - 5 sustains, 4 protections, 8 miscellaneous.
     */
    private int size;
    /**
     * How many matching flags the object being priced carries. Working state, zeroed by the caller
     * before each pass rather than data loaded once.
     */
    private int count;
    /**
     * The row's name as the power log spells it, e.g. {@code "sustains"}.
     */
    private String description;

    /**
     * Build one row of the flag set table.
     *
     * <p>Constructor FlagSet commented in full on 260827.
     *
     * @param type        the family of flags this row counts
     * @param factor      multiplier for the quadratic multiple-holding increment
     * @param bonus       flat bonus for a full set
     * @param size        how many flags make a full set
     * @param count       starting count, normally zero
     * @param description the row's name for the power log
     */
    public FlagSet(ObjectFlagType type, int factor, int bonus, int size, int count, String description) {
        this.type = type;
        this.factor = factor;
        this.bonus = bonus;
        this.size = size;
        this.count = count;
        this.description = description;
    }

    /**
     * @return the family of flags this row counts, matched against an object property's subtype
     */
    public ObjectFlagType getType() {
        return type;
    }

    /**
     * @return the multiplier for the quadratic increment awarded for holding several of these
     */
    public int getFactor() {
        return factor;
    }

    /**
     * @return the flat bonus for carrying every flag in the family
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * @return how many flags make a full set of this row
     */
    public int getSize() {
        return size;
    }

    /**
     * @return how many matching flags the object being priced carries - working state, valid only between the caller's zeroing pass and its read-back
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the running count of matching flags. Callers zero every row before a power pass and
     * increment as they walk the object's flags; nothing else should write it.
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
