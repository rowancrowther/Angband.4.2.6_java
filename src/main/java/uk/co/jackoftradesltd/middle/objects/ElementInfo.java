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

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;

/**
 * How something relates to a single damage element: the {@link ElementInfoEnum} flags
 * (hates/ignores/random) plus a resistance level. The Java port of C's
 * {@code struct element_info} ({@code src/object.h}), which is two fields — an
 * {@code int16_t res_level} and a {@code bitflag flags} holding {@code EL_INFO_HATES},
 * {@code EL_INFO_IGNORE} and {@code EL_INFO_RANDOM}.
 *
 * <p>C declares an {@code el_info[ELEM_MAX]} array on five different structs — object kind,
 * ego item, artifact, object and player state — so the same pair of fields serves both "this
 * armour ignores acid" and "this player resists fire". Instances here are correspondingly held
 * one per element by whatever owns them, keyed by {@code ElementEnum} rather than indexed by it.
 *
 * <p>The two halves are not equally used. Every owner reads {@link #getResLevel()}; the flags are
 * an object-side concern only, describing what the <em>item</em> does when the element hits it
 * (burns up, shrugs it off, takes a random amount). That is why {@link KnownObject} — the port of
 * C's {@code p->obj_k}, which is the one place the two halves come apart — stores a bare boolean
 * per element instead of one of these: it needs only "is the resistance known", and the flags C
 * carries alongside it there are written to the savefile and read back but never consulted.
 *
 * @author Rowan Crowther
 */
public class ElementInfo {
    /**
     * The hates/ignores/random flags for this element — what happens to the object itself when
     * the element strikes it. C's {@code element_info.flags}.
     *
     * <p>Not final: {@link #copy()} replaces the whole set rather than copying into the existing
     * one, so that the copy and its source cannot share a {@link Flag}.
     */
    private Flag<ElementInfoEnum> flags;
    /**
     * The resistance level against this element, C's {@code element_info.res_level}. Zero is
     * neutral, positive resists and negative is a vulnerability; the scale is C's, so nothing
     * here interprets the number beyond passing it on.
     */
    private int resLevel;

    /**
     * Build an element-info with an empty flag set.
     */
    public ElementInfo() {
        this.flags = new Flag<>(ElementInfoEnum.class);
    }

    /**
     * @return the resistance level against this element
     */
    public int getResLevel() {
        return resLevel;
    }

    /**
     * Sets the resistance level against this element.
     *
     * @param resLevel the resistance level to store
     */
    public void setResLevel(int resLevel) {
        this.resLevel = resLevel;
    }
    /**
     * Returns the live flag set, not a copy — callers can mutate this element info through the
     * value they get back. That is deliberate, and matches C, where {@code el_info[i].flags} is
     * a bitflag sitting inside the owning struct that callers set and clear in place. Use
     * {@link #copy()} when an independent set is what is wanted; the contrast between the two is
     * the whole reason {@code copy()} exists.
     *
     * @return the hates/ignores/random flags for this element, shared with this instance
     */
    public Flag<ElementInfoEnum> getFlags() {
        return flags;
    }

    /**
     * Returns a deep copy of this element info — the flag set is itself copied, so the returned
     * instance shares no mutable state with this one. Used when a base's per-element defaults are
     * folded onto a derived kind (e.g. a synthesised spellbook) that must then be free to diverge.
     *
     * @return an independent copy of this element info
     */
    public ElementInfo copy() {
        ElementInfo ei = new ElementInfo();
        Flag<ElementInfoEnum> copy = new Flag<>(ElementInfoEnum.class);
        copy.copyFrom(flags);
        ei.flags = copy;
        ei.resLevel = this.resLevel;
        return ei;
    }

    /**
     * Sets one of this element's flags, the port of an {@code of_on} against
     * {@code el_info[i].flags}. Delegates to {@link Flag#on}, so the return value is C's: true
     * when the call changed something, false when the flag was already on.
     *
     * @param info the flag to set
     * @return true if the flag was newly set, false if it was already set
     * @see #has(ElementInfoEnum)
     */
    public boolean on(ElementInfoEnum info) {
        return flags.on(info);
    }

    /**
     * Tests one of this element's flags. Convenience for {@code getFlags().has(info)}, present so
     * that the common read does not have to reach through {@link #getFlags()} and take a mutable
     * reference it has no use for.
     *
     * @param info the flag to test
     * @return true if the flag is set
     * @see #on(ElementInfoEnum)
     */
    public boolean has(ElementInfoEnum info) {
        return flags.has(info);
    }
}
