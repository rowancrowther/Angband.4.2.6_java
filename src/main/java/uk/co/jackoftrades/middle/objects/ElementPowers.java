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

import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ResType;

/**
 * What one element is worth to an object that ignores, resists, is immune to, or is vulnerable to it
 * — the port of C's {@code struct element_powers} and its thirteen-row table
 * ({@code obj-power.c:103-127}).
 *
 * <p>Four separate prices, because the four relationships are worth different amounts and not all of
 * them apply to every element. Acid, electricity, fire and cold can be ignored (the object survives
 * the attack) and can be resisted or made immune; the high elements — poison, light, dark, sound,
 * shards, nexus, nether, chaos and disenchantment — can only be resisted, and their ignore, vuln and
 * immunity figures are zero.
 *
 * <p>{@link #getType()} sorts the row into the low or high group, and that grouping is what
 * {@link ElementSet} counts: several low resists together are worth more than the sum of their
 * parts, and a full set more still.
 *
 * <p><b>An immunity is priced as immunity plus resistance</b>, not immunity alone, because an
 * immunity subsumes the resistance it replaces. {@code ItemObject.elementPower} adds the two
 * together, as C does at {@code obj-power.c:676}.
 *
 * <p><b>Keyed, not indexed.</b> C relies on this table and an object's {@code el_info} array sharing
 * an index. The port keys both by {@link ElementEnum}, so the two cannot silently desynchronise.
 *
 * <p>Class ElementPowers commented in full on 260827.
 *
 * @author Rowan Crowther
 */
public class ElementPowers {
    /**
     * Which element this row prices. C reaches the same row by array index; the port keys by this.
     */
    private ElementEnum element;
    /**
     * The element's name as it appears in the power log, e.g. {@code "acid"}. C's
     * {@code el_powers[].name}.
     */
    private String name;
    /**
     * Whether this element belongs to the low or high resistance group - C's
     * {@code el_powers[].type}. {@link ElementSet} counts rows by this to price combinations.
     */
    private ResType type;
    /**
     * Power for an object that ignores this element - it takes no damage from it itself. Zero for
     * every high element. C's {@code ignore_power}.
     */
    private int ignorePower;
    /**
     * Power for a vulnerability to this element, and so negative where it is set at all. Zero for
     * every high element. C's {@code vuln_power}.
     */
    private int vulnPower;
    /**
     * Power for resisting this element. The only one of the four that is set on every row. C's
     * {@code res_power}.
     */
    private int resPower;
    /**
     * Power for immunity to this element, on top of {@link #resPower} rather than instead of it -
     * the caller adds the two. Zero for every high element. C's {@code im_power}.
     */
    private int imPower;

    /**
     * Build one row of the element power table.
     *
     * <p>Constructor ElementPowers commented in full on 260827.
     *
     * @param elementEnum the element this row prices
     * @param name        the element's name for the power log
     * @param type        the low or high resistance group this element belongs to
     * @param ignorePower power for ignoring the element
     * @param vulnPower   power for a vulnerability to it, normally negative
     * @param resPower    power for resisting it
     * @param imPower     power for immunity, added to {@code resPower} by the caller
     */
    public ElementPowers(ElementEnum elementEnum, String name, ResType type, int ignorePower, int vulnPower, int resPower, int imPower) {
        this.element = elementEnum;
        this.name = name;
        this.type = type;
        this.ignorePower = ignorePower;
        this.vulnPower = vulnPower;
        this.resPower = resPower;
        this.imPower = imPower;
    }

    /**
     * @return the element this row prices - the port's key into the table, where C uses an index
     */
    public ElementEnum getElement() {
        return element;
    }

    /**
     * @return the element's name as the power log spells it
     */
    public String getName() {
        return name;
    }

    /**
     * @return the low or high resistance group this element belongs to, which is what {@link ElementSet} counts
     */
    public ResType getType() {
        return type;
    }

    /**
     * @return power for ignoring this element; zero on every high element
     */
    public int getIgnorePower() {
        return ignorePower;
    }

    /**
     * @return power for a vulnerability to this element, normally negative; zero on every high element
     */
    public int getVulnPower() {
        return vulnPower;
    }

    /**
     * @return power for resisting this element - the one figure set on every row
     */
    public int getResPower() {
        return resPower;
    }

    /**
     * @return power for immunity to this element, to be added to {@link #getResPower()} rather than used alone
     */
    public int getImPower() {
        return imPower;
    }
}
