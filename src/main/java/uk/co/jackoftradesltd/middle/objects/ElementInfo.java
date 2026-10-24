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
 * an object-side concern only, and say what happens to the <em>item</em> when the element hits it.
 * {@code EL_INFO_HATES} makes the item liable to be destroyed ({@code project-obj.c}) and
 * {@code EL_INFO_IGNORE} makes it immune to the damage, so an item that both hates and ignores an
 * element survives it. {@code EL_INFO_RANDOM} is different in kind: {@code obj-make.c} sets it,
 * together with {@code EL_INFO_IGNORE} and a resistance level of 1, on a resistance it picked at
 * random for an ego item, and nothing in C ever tests it. That is why {@link KnownObject} — the
 * port of C's {@code p->obj_k} — stores a bare boolean per element instead of one of these: it
 * needs only "is the resistance known", and the flags C carries alongside it there are written to
 * the savefile ({@code save.c}) and read back ({@code load.c}) but never consulted.
 *
 * <p>C copies an {@code element_info} by plain struct assignment, so the port's only structural
 * addition is {@link #copy()}, which exists because the Java flag set is an object rather than a
 * byte inside the struct.
 *
 * <p>Class ElementInfo coded before 261009, commented in full on 261024.
 *
 * @author Rowan Crowther
 */
public class ElementInfo {
    /**
     * The hates/ignores/random flags for this element — what happens to the object itself when
     * the element strikes it, plus the marker for a resistance rolled at random. C's
     * {@code element_info.flags}, a {@code bitflag} (one {@code uint8_t}) holding
     * {@code EL_INFO_HATES}, {@code EL_INFO_IGNORE} and {@code EL_INFO_RANDOM}.
     *
     * <p>Not final: {@link #copy()} replaces the whole set rather than copying into the existing
     * one, so that the copy and its source cannot share a {@link Flag}. Never null.
     *
     * <p>Field flags coded before 261009, commented in full on 261024.
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
     * <p>Defaults to zero, as in C's zeroed struct. Held as an {@code int}, but {@link #getResLevel()}
     * narrows it to a {@code short} on the way out, which is the width C's field has.
     *
     * <p>Field resLevel coded before 261009, commented in full on 261024.
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
     * <p>The stored {@code int} is cast to {@code short}, matching C's {@code int16_t}, so a value
     * set outside the 16-bit range is truncated here rather than returned as given. Every value C
     * itself produces, down to the {@code -32768} that {@code obj-curse.c} writes, survives the
     * cast.
     *
     * <p>Method getResLevel coded before 261009, commented in full on 261024.
     *
     * @return the resistance level against this element, narrowed to 16 bits
     */
    public short getResLevel() {
        return (short)resLevel;
    }

    /**
     * Sets the resistance level against this element, C's {@code el_info[i].res_level = value}.
     * The value is stored as given, as an {@code int}: nothing here clamps or validates it, so a
     * caller can store a value outside C's usual -1..3 range. Only {@link #getResLevel()} narrows
     * to the 16 bits of C's {@code int16_t}, so a value outside that range is stored whole but read
     * back truncated.
     *
     * <p>Method setResLevel coded before 261009, commented in full on 261024.
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
     * ({@code a = b} on an {@code element_info}, as in {@code obj-randart.c}, or the
     * {@code memcpy} of an {@code el_info} array in {@code obj-init.c}), which copies the
     * {@code bitflag} by value. Java needs it spelled out because the flag set is an object, and
     * copying the reference would leave two owners sharing one set.
     *
     * <p>Callers are the places the port clones something that owns an element map — copies of an
     * {@link ObjectKind}, {@link Artifact}, {@link ItemObject}, {@code EgoItem}, {@code PlayerRace}
     * or {@code PlayerState}, the known-state builds in {@code PlayerKnowledge}, the default
     * resists {@code ObjectUtils} gives a new object from its kind, and the folding of an object
     * base's defaults onto a synthesized {@link ObjectKind} — each of which must then be free to
     * diverge from its source.
     *
     * <p>Method copy coded before 261009, commented in full on 261024.
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
     * Sets one of this element's flags, the port of {@code el_info[i].flags |= EL_INFO_*}. C has
     * no {@code of_on}-style helper for this field: {@code flags} is a single byte, and every
     * write is a bare {@code |=} — in {@code obj-init.c} ({@code grab_element_flag}, and an
     * artifact's default of ignoring every base element), {@code init.c} (a dungeon book ignoring
     * the base elements), {@code obj-make.c} (a random ego resistance) and {@code obj-randart.c}.
     * Those have no return value; this method returns true when the call changed something and
     * false when the flag was already on, because it delegates to {@link Flag#on}.
     *
     * <p>Method on coded before 261009, commented in full on 261024.
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
     * in {@code project-obj.c}, {@code obj-info.c}, {@code obj-pile.c} (stacking), {@code obj-gear.c},
     * {@code obj-power.c} and {@code ui-entry.c}.
     *
     * <p>Method has coded before 261009, commented in full on 261024.
     *
     * @param info the flag to test
     * @return true if the flag is set
     * @see #on(ElementInfoEnum)
     */
    public boolean has(ElementInfoEnum info) {
        return flags.has(info);
    }
}
