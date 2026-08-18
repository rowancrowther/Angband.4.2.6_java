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

package uk.co.jackoftrades.channel.utils;

import java.util.List;

/**
 * The read-only half of a flag set — everything that can be asked of a {@link Flag} without
 * being able to change it.
 *
 * <p>There is no counterpart to this in the C original, and that is the point. C passes flag
 * arrays as {@code const bitflag *} where a function only reads them, so the compiler enforces
 * the split at every call site; a Java caller handed a {@link Flag} can set and clear it
 * whatever the method meant to allow. This interface restores the distinction: a parameter or
 * return type declared as a {@code FlagView} says the flags are to be read, and nothing about
 * the type lets the holder do otherwise.
 *
 * <p>The methods here are exactly the {@code const}-taking half of {@code z-bitflag.c} —
 * {@code flag_has}, {@code flag_count}, {@code flag_is_empty}, {@code flag_is_full},
 * {@code flag_is_inter}, {@code flag_is_subset}, {@code flag_is_equal} and the
 * {@code flags_test} pair. The mutating operations ({@code flag_on}, {@code flag_off},
 * {@code flag_wipe}, {@code flag_union}, {@code flag_inter}, {@code flag_diff}, …) stay on
 * {@link Flag} itself.
 *
 * <p>Extending {@link Iterable} is what makes an enhanced-for loop over a view possible, and it
 * is also how the comparison methods below are written — they take a {@code FlagView} rather
 * than a {@code Flag} precisely because iterating the other set is all they need.
 *
 * <p><b>The line is drawn at the argument, not at the method.</b> {@link Flag#union},
 * {@link Flag#inter}, {@link Flag#diff} and {@link Flag#copyFrom} all mutate — but each mutates
 * its <em>receiver</em> and only reads its argument, so each takes a {@code FlagView} too. What
 * belongs on {@link Flag} and nowhere else is the operation whose target is the flag set itself:
 * {@code on}, {@code off}, {@code wipe}, {@code setAll}, {@code negate}, {@code set},
 * {@code clear}, {@code init} and {@code mask}.
 *
 * <p>A view is not an immutable value. It withholds mutation from whoever holds the view; it says
 * nothing about whoever holds the underlying {@link Flag}, who can still change it underneath.
 * Where a caller needs a value that cannot change at all, the copy is still what provides it —
 * see {@link uk.co.jackoftrades.middle.player.enums.TimedEffect#getRedrawFlags}.
 *
 * <p>Interface FlagView coded on 260818, commented in full on 260818, the argument/receiver note
 * added the same day once the four mutators took views.
 *
 * @param <E> the enum type whose constants are the individual flags
 * @author Rowan Crowther
 */
public interface FlagView<E extends Enum<E>> extends Iterable<E> {
    /**
     * Reports whether a single flag is switched on — the port of C's {@code flag_has}.
     *
     * @param flag the flag to test
     * @return {@code true} if the flag is on
     */
    boolean has(E flag);

    /**
     * Returns the next flag switched on after the given one.
     *
     * @param currentFlag the flag to count from
     * @return the next flag that is on
     * @deprecated cannot faithfully reproduce C's {@code flag_next}, which has a
     * {@code FLAG_END} sentinel that a Java enum has no equivalent of. See
     * {@link Flag#next} for the full explanation and for the iteration to use instead.
     */
    @Deprecated
    E next(E currentFlag);

    /**
     * Counts the flags currently switched on — the port of C's {@code flag_count}.
     *
     * @return the number of flags that are on
     */
    int count();

    /**
     * Reports whether no flag at all is switched on — the port of C's {@code flag_is_empty}.
     *
     * @return {@code true} if every flag is off
     */
    boolean isEmpty();

    /**
     * Reports whether every flag is switched on — the port of C's {@code flag_is_full}.
     *
     * <p>See {@link Flag#isFull} for the deliberate divergence here: C also requires the
     * padding bits above the last named flag to be set, which this port has no equivalent of.
     *
     * @return {@code true} if every flag is on
     */
    boolean isFull();

    /**
     * Reports whether this set and another share at least one flag — the port of C's
     * {@code flag_is_inter}. Tests for an intersection; it does not form one.
     *
     * @param other the set to compare against
     * @return {@code true} if some flag is on in both sets
     */
    boolean isInter(FlagView<E> other);

    /**
     * Reports whether {@code other} is a subset of this set — the port of C's
     * {@code flag_is_subset}. Note the direction: it is the argument that must be contained,
     * which is the same way round as the C function's two array parameters.
     *
     * @param other the set that must be contained in this one
     * @return {@code true} if every flag on in {@code other} is also on here
     */
    boolean isSubset(FlagView<E> other);

    /**
     * Reports whether two sets have exactly the same flags on — the port of C's
     * {@code flag_is_equal}.
     *
     * @param other the set to compare against
     * @return {@code true} if the two sets match flag for flag
     */
    boolean isEqual(FlagView<E> other);

    /**
     * Reports whether <em>any</em> of the given flags is switched on — the port of C's
     * {@code flags_test}.
     *
     * @param flags the flags to look for
     * @return {@code true} if at least one of them is on
     */
    boolean test(List<E> flags);

    /**
     * Reports whether <em>all</em> of the given flags are switched on — the port of C's
     * {@code flags_test_all}. The all-or-nothing counterpart to {@link #test}.
     *
     * @param flags the flags that must all be on
     * @return {@code true} if every one of them is on
     */
    boolean testAll(List<E> flags);
}
