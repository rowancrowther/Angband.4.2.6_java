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
 * <p>C declares an {@code el_info[ELEM_MAX]} array on eight different structs — in
 * {@code object.h} the object base, object kind, artifact, ego item and object, and in
 * {@code player.h} the player race, player shape and player state — so the same pair of fields
 * serves both "this armour ignores acid" and "this player resists fire". Instances here are
 * correspondingly held one per element by whatever owns them, keyed by {@code ElementEnum} rather
 * than indexed by it.
 *
 * <p>The two halves are not equally used. Every owner reads {@link #getResLevel()}; the flags are
 * an object-side concern only, describing what the <em>item</em> does when the element hits it
 * (burns up, shrugs it off, takes a random amount). That is why {@link KnownObject} — the port of
 * C's {@code p->obj_k}, which is the one place the two halves come apart — stores a bare boolean
 * per element instead of one of these: it needs only "is the resistance known", and the flags C
 * carries alongside it there are written to the savefile ({@code save.c}) and read back
 * ({@code load.c}) but never consulted.
 *
 * <p>C copies an {@code element_info} by plain struct assignment, so the port's only structural
 * addition is {@link #copy()}, which exists because the Java flag set is an object rather than a
 * byte inside the struct.
 *
 * <p>Class ElementInfo coded before 261009, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class ElementInfo {
    /**
     * The hates/ignores/random flags for this element — what happens to the object itself when
     * the element strikes it. C's {@code element_info.flags}.
     *
     * <p>Not final: {@link #copy()} replaces the whole set rather than copying into the existing
     * one, so that the copy and its source cannot share a {@link Flag}. Never null.
     *
     * <p>Field flags coded before 261009, commented in full on 261014.
     */
    private Flag<ElementInfoEnum> flags;
    /**
     * The resistance level against this element, C's {@code element_info.res_level} (an
     * {@code int16_t}; Java's {@code int} holds every value it can). Zero is neutral, 1 is a
     * resistance, 3 is immunity and -1 is a vulnerability — the three values {@code obj-info.c}
     * tests for — and in {@code player-calcs.c} a vulnerability lowers a player state's level by
     * one unless it is already immune. The scale is C's, so nothing here interprets the number
     * beyond passing it on.
     *
     * <p>Defaults to zero, as in C's zeroed struct.
     *
     * <p>Field resLevel coded before 261009, commented in full on 261014.
     */
    private int resLevel;

    /**
     * Build an element-info with an empty flag set and a resistance level of zero — the state of
     * one of C's zeroed {@code element_info} slots, which is how every {@code el_info} array
     * starts before the data files fill it in.
     *
     * <p>Constructor ElementInfo coded before 261009, commented in full on 261014.
     */
    public ElementInfo() {
        this.flags = new Flag<>(ElementInfoEnum.class);
    }

    /**
     * Reads the resistance level, C's {@code el_info[i].res_level}. See the field for the scale.
     *
     * <p>Method getResLevel coded before 261009, commented in full on 261014.
     *
     * @return the resistance level against this element
     */
    public int getResLevel() {
        return resLevel;
    }

    /**
     * Sets the resistance level against this element, C's {@code el_info[i].res_level = value}.
     * The value is stored as given: C's field is an {@code int16_t}, but nothing here narrows to
     * 16 bits, clamps or validates, so a caller passing a value outside C's -1..3 range gets it
     * back unchanged.
     *
     * <p>Method setResLevel coded before 261009, commented in full on 261014.
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
     * <p>Method getFlags coded before 261009, commented in full on 261014.
     *
     * @return the hates/ignores/random flags for this element, shared with this instance
     */
    public Flag<ElementInfoEnum> getFlags() {
        return flags;
    }

    /**
     * Returns a deep copy of this element info — the flag set is itself copied, so the returned
     * instance shares no mutable state with this one. This is the port of C's struct assignment
     * ({@code a = b} on an {@code element_info}, or the {@code el_info} array copy), which copies
     * the {@code bitflag} by value. Java needs it spelled out because the flag set is an object,
     * and copying the reference would leave two owners sharing one set.
     *
     * <p>Callers are every place the port clones something that owns an element map — an
     * {@link ObjectKind} or {@link Artifact} copy, an {@code EgoItem} or {@code PlayerRace} clone,
     * a {@code PlayerState} copy, the known-state build in {@code PlayerKnowledge}, and the
     * folding of an object base's defaults onto a synthesized kind — each of which must then be
     * free to diverge from its source.
     *
     * <p>Method copy coded before 261009, commented in full on 261014.
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
     * when the call changed something, false when the flag was already on. C's own writes are the
     * {@code |=} in {@code obj-init.c} ({@code grab_element_flag}) and {@code init.c}, which have
     * no return value to compare.
     *
     * <p>Method on coded before 261009, commented in full on 261014.
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
     * reference it has no use for. It is the port of {@code el_info[i].flags & EL_INFO_*}, as read
     * in {@code project-obj.c} and {@code obj-info.c}.
     *
     * <p>Method has coded before 261009, commented in full on 261014.
     *
     * @param info the flag to test
     * @return true if the flag is set
     * @see #on(ElementInfoEnum)
     */
    public boolean has(ElementInfoEnum info) {
        return flags.has(info);
    }
}
