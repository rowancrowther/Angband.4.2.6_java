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

import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ResType;

/**
 * What one element is worth to an object that ignores, resists, is immune to, or is vulnerable to it
 * — the port of C's {@code struct element_powers} and its thirteen-row {@code el_powers[]} table
 * ({@code obj-power.c}).
 *
 * <p>Four separate prices, because the four relationships are worth different amounts and not all of
 * them apply to every element. Acid, electricity, fire and cold can be ignored (the object survives
 * the attack) and can be resisted or made immune; the high elements — poison, light, dark, sound,
 * shards, nexus, nether, chaos and disenchantment — can only be resisted, and their ignore, vuln and
 * immunity figures are zero. A zero figure is not a price of zero charged: {@code element_power}
 * skips the relationship altogether when its figure is zero.
 *
 * <p>{@link #getType()} sorts the row into the low or high group, and that grouping is what
 * {@link ElementSet} counts: several low resists together are worth more than the sum of their
 * parts, and a full set more still.
 *
 * <p><b>An immunity is priced as immunity plus resistance</b>, not immunity alone, because an
 * immunity subsumes the resistance it replaces. {@code ItemObject.elementPower} adds the two
 * together, as C does in {@code element_power} ({@code obj-power.c}).
 *
 * <p><b>Keyed, not indexed.</b> C relies on this table and an object's {@code el_info} array sharing
 * an index. The port keys both by {@link ElementEnum}, so the two cannot silently desynchronize.
 *
 * <p><b>Differences from C.</b> The row carries its own {@link ElementEnum} key, which C does not
 * hold. The table is {@code ObjectRegistry.elementPowers}, a list of exactly the thirteen real rows
 * in C's order; it has no entry for {@code ELEM_NONE} and none for the elements after
 * {@code ELEM_DISEN}, which C's table does not price either. The {@code type} column is a
 * {@link ResType} rather than C's anonymous {@code T_LRES}/{@code T_HRES} enum.
 *
 * <p>Class ElementPowers coded before 260827, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class ElementPowers {
    /**
     * Which element this row prices. C reaches the same row by array index; the port keys by this, and
     * {@code ItemObject.elementPower} uses it to fetch the matching {@code el_info} entry.
     *
     * <p>Field element coded before 261009, commented in full on 261009.
     */
    private ElementEnum element;
    /**
     * The element's name as it appears in the power log, e.g. {@code "acid"}. C's
     * {@code el_powers[].name}. It is player-facing prose only, so it is not used to find the row;
     * note that two spellings differ from the {@link ElementEnum} identifier ({@code "electricity"}
     * for {@code ELEM_ELEC}, {@code "shards"} for {@code ELEM_SHARD} and {@code "disenchantment"} for
     * {@code ELEM_DISEN}).
     *
     * <p>Field name coded before 261009, commented in full on 261009.
     */
    private String name;
    /**
     * Whether this element belongs to the low or high resistance group - C's
     * {@code el_powers[].type}. {@link ElementSet} counts rows by this to price combinations. Acid,
     * electricity, fire and cold are {@code T_LRES}; the other nine are {@code T_HRES}.
     *
     * <p>Field type coded before 261009, commented in full on 261009.
     */
    private ResType type;
    /**
     * Power for an object that ignores this element - it takes no damage from it itself. Zero for
     * every high element, and 3, 1, 3, 1 for acid, electricity, fire and cold. C's
     * {@code ignore_power}.
     *
     * <p>Field ignorePower coded before 261009, commented in full on 261009.
     */
    private int ignorePower;
    /**
     * Power for a vulnerability to this element, and so negative where it is set at all: -6 for each
     * of the four low elements. Zero for every high element, so a vulnerability to one of them costs
     * nothing. C's {@code vuln_power}.
     *
     * <p>Field vulnPower coded before 261009, commented in full on 261009.
     */
    private int vulnPower;
    /**
     * Power for resisting this element. The only one of the four that is set on every row, running
     * from 5 (acid) to 28 (poison). C's {@code res_power}.
     *
     * <p>Field resPower coded before 261009, commented in full on 261009.
     */
    private int resPower;
    /**
     * Power for immunity to this element, on top of {@link #resPower} rather than instead of it -
     * the caller adds the two. Set only for the four low elements (38, 35, 40 and 37); zero for every
     * high element, so an immunity to a high element earns nothing. C's {@code im_power}.
     *
     * <p>Field imPower coded before 261009, commented in full on 261009.
     */
    private int imPower;

    /**
     * Build one row of the element power table.
     *
     * <p>The arguments follow the column order of C's {@code el_powers[]} initializers, with the
     * port's element key added in front. Nothing is validated; {@code GameConstants} supplies the thirteen rows from C's table.
     *
     * <p>Constructor ElementPowers coded before 260827, commented in full on 261009.
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
     * The element this row prices - the port's key into the table, where C uses an index. The caller
     * passes it to {@code getElInfo().get(...)} to find the object's matching {@code el_info} entry.
     *
     * <p>Function getElement coded before 261009, commented in full on 261009.
     *
     * @return the element this row prices
     */
    public ElementEnum getElement() {
        return element;
    }

    /**
     * The element's name as the power log spells it - C's {@code el_powers[].name}, used in the
     * "Add %d power for ..." log lines of {@code element_power}.
     *
     * <p>Function getName coded before 261009, commented in full on 261009.
     *
     * @return the element's name as the power log spells it
     */
    public String getName() {
        return name;
    }

    /**
     * The low or high resistance group this element belongs to. {@code element_power} compares it
     * with each {@link ElementSet} row's type when it counts combinations.
     *
     * <p>Function getType coded before 261009, commented in full on 261009.
     *
     * @return the low or high resistance group this element belongs to, which is what {@link ElementSet} counts
     */
    public ResType getType() {
        return type;
    }

    /**
     * The power added when the object has {@code EL_INFO_IGNORE} set for this element. A zero return
     * means the caller adds nothing, as in C.
     *
     * <p>Function getIgnorePower coded before 261009, commented in full on 261009.
     *
     * @return power for ignoring this element; zero on every high element
     */
    public int getIgnorePower() {
        return ignorePower;
    }

    /**
     * The power added when the object's resistance level for this element is -1. A zero return means
     * the caller adds nothing, as in C.
     *
     * <p>Function getVulnPower coded before 261009, commented in full on 261009.
     *
     * @return power for a vulnerability to this element, normally negative; zero on every high element
     */
    public int getVulnPower() {
        return vulnPower;
    }

    /**
     * The power added when the object's resistance level for this element is 1. It is also the
     * second half of the immunity price, which is {@link #getImPower()} plus this.
     *
     * <p>Function getResPower coded before 261009, commented in full on 261009.
     *
     * @return power for resisting this element - the one figure set on every row
     */
    public int getResPower() {
        return resPower;
    }

    /**
     * The first half of the immunity price, added when the object's resistance level for this element
     * is 3. A zero return means the caller adds nothing at all for immunity - not even the
     * resistance figure - as in C.
     *
     * <p>Function getImPower coded before 261009, commented in full on 261009.
     *
     * @return power for immunity to this element, to be added to {@link #getResPower()} rather than used alone
     */
    public int getImPower() {
        return imPower;
    }
}