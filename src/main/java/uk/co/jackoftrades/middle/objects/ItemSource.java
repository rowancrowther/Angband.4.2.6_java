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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

/**
 * The {@link BonusSource} view of a worn {@link ItemObject} — the first pass of
 * {@code calcBonuses}' equipment walk, C's {@code obj = slot_object(p, i)}
 * ({@code player-calcs.c:1927}).
 *
 * <p>Almost every method is a straight delegation, because an item is exactly what C's loop body
 * expects to be looking at. The only work done here is guarding the two references C treats as
 * always present: {@code obj->known}, which the port allows to be absent, and the tval, which
 * C stores as a plain integer. Both guards answer "nothing known" and "not a digger" rather than
 * throwing, matching what a zeroed known object would have given.
 *
 * <p>Class ItemSource coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 * @see CurseSource
 */
public class ItemSource implements BonusSource {
    /**
     * The worn item this source reports on; never {@code null}.
     */
    private final ItemObject item;

    /**
     * Wraps one worn item as a bonus source.
     *
     * @param item the item, which must be a real one — an empty slot is skipped by the caller
     *             rather than wrapped, as C's {@code while (obj)} skips it
     */
    public ItemSource(@NotNull ItemObject item) {
        this.item = item;
    }

    /**
     * {@inheritDoc}
     *
     * <p>{@link ItemObject#getFlags} already returns a copy, so the caller cannot reach the item's
     * own set through this.
     */
    @Override
    public Flag<ObjectFlag> flags() {
        return item.getFlags();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int modifier(ObjectModifier modifier) {
        return item.getModifierValue(modifier);
    }

    /**
     * {@inheritDoc}
     *
     * <p>An element the item has no entry for reads as zero, which is what C's fixed
     * {@code el_info} array gives for an element nothing has touched.
     */
    @Override
    public int resLevel(ElementEnum element) {
        ElementInfo elementInfo = item.getElInfo().getOrDefault(element, null);
        if (elementInfo == null) return 0;
        return elementInfo.getResLevel();
    }

    /**
     * {@inheritDoc}
     *
     * <p>An item with no known counterpart at all answers zero, so nothing about it is treated as
     * learned. C has no equivalent case — it dereferences {@code obj->known} unguarded.
     */
    @Override
    public int knownResLevel(ElementEnum element) {
        if (item.getKnown() == null) return 0;
        ElementInfo elementInfo = item.getKnown().getElInfo().getOrDefault(element, null);
        if (elementInfo == null) return 0;
        return elementInfo.getResLevel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int baseAC() {
        return item.getBaseAC();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int toAC() {
        return item.getToAC();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int knownToAC() {
        if (item.getKnown() == null) return 0;
        return item.getKnown().getToAC();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int toHit() {
        return item.getToHit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int knownToHit() {
        if (item.getKnown() == null) return 0;
        return item.getKnown().getToHit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int toDam() {
        return item.getToDam();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int knownToDam() {
        if (item.getKnown() == null) return 0;
        return item.getKnown().getToDam();
    }

    /**
     * {@inheritDoc}
     *
     * <p>An item with no tval is not a digger. C's {@code tval_is_digger} compares an integer
     * field that is always set, so the null case is the port's alone.
     */
    @Override
    public boolean isDigger() {
        if (item.gettValue() == null) return false;
        return item.gettValue().isDigger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Flag<ObjectFlag> flagsKnown() {
        return item.flagsKnown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean flagSet(ObjectFlag flag) {
        return item.getFlags().has(flag);
    }
}
