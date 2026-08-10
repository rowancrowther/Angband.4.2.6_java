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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A type-safe wrapper around an {@link EnumSet} that emulates Angband's C
 * "flag set" bit-arrays (the {@code flag_*} family of macros in
 * {@code src/z-bitflag.c}). The C game stores collections of boolean traits —
 * monster flags, object flags, etc. — as packed bit arrays and manipulates them
 * with set/clear/test/union/intersection operations; this class provides the
 * same operations over a Java enum so callers get the same semantics with
 * compile-time safety instead of raw bit indices.
 *
 * @param <E> the enum type whose constants are the individual flags
 * @author Rowan Crowther
 */
public class Flag<E extends Enum<E>> implements Iterable<E> {
    /**
     * The flags currently switched on.
     *
     * @author Rowan Crowther
     */
    private final EnumSet<E> flagSet;

    /**
     * The full set of every possible flag, cached for full/negate/mask operations.
     *
     * @author Rowan Crowther
     */
    private final EnumSet<E> all;
    /**
     * The enum class, retained so new {@link EnumSet}s can be built generically
     * (e.g. in {@link #copy()} and {@link #mask}).
     *
     * @author Rowan Crowther
     */
    private final Class<E> eClass;

    /**
     * Constructor, as this is a generic class, the type of flag set we are using has to be passed in
     *
     * @param eClass The class of the enum
     */
    @Contract(mutates = "this")
    public Flag(@NotNull Class<E> eClass) {
        this.eClass = eClass;
        flagSet = EnumSet.noneOf(this.eClass);
        all = EnumSet.allOf(this.eClass);
    }

    /**
     * Returns true if the incoming parameter is part of the flag set, and false otherwise
     *
     * @param flag The flag we are testing
     * @return true if flag is in set, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean has(@NotNull E flag) {
        return flagSet.contains(flag);
    }

    /**
     * Gets the next flag which is set in this bitfield. The flags are assumed to be ordered in the order which they are
     * defined in the relevant enum classes
     *
     * @param currentFlag The current flag we are counting from
     * @return The next set flag, or the last flag in the enum if currentFlag isn't set, or there are no more set flags
     * after currentFlag
     * @deprecated Use the enhanced-for loop given by {@link #iterator()} instead. This method
     * cannot faithfully reproduce {@code flag_next} ({@code z-bitflag.c}) and should not be
     * relied on. C's version starts <em>at</em> the given flag rather than after it, and
     * signals exhaustion by returning the {@code FLAG_END} sentinel — a value that is not a
     * real flag. A Java enum has no such sentinel, so this returns the last declared constant
     * instead, which is a perfectly ordinary flag and so cannot be told apart from a genuine
     * hit. The C idiom this existed to support, {@code for (f = flag_next(fs, sz, FLAG_START);
     * f != FLAG_END; f = flag_next(fs, sz, f + 1))} ({@code datafile.c}), is expressed in this
     * port by iterating the set directly.
     */
    @Contract(pure = true)
    @CheckReturnValue
    @Deprecated
    public E next(@NotNull E currentFlag) {
        boolean found = false;

        for (E flag : flagSet) {
            if (found) return flag;
            if (flag.equals(currentFlag)) found = true;
        }

        return Collections.max(all);
    }

    /**
     * Returns an iterator over only the flags currently switched on, in enum
     * declaration order. This is what makes a {@code Flag} usable in an
     * enhanced-for loop (e.g. {@code for (E flag : someFlag)}), letting callers
     * visit the set flags directly instead of probing every enum constant with
     * {@link #has(Enum)}. The backing set is wrapped read-only, so the returned
     * iterator cannot mutate this {@code Flag} (its {@code remove()} throws).
     *
     * @return a read-only iterator over the flags that are on
     * @author Rowan Crowther
     */
    @Override
    @CheckReturnValue
    @Contract(pure = true)
    public Iterator<E> iterator() {
        return Collections.unmodifiableSet(flagSet).iterator();
    }

    /**
     * Counts the number of flags which are set in this flag set
     *
     * @return The size of the set of flags which are on
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int count() {
        return flagSet.size();
    }

    /**
     * Returns true if the set is empty, i.e. no flags are set to on, and false if one or more flags are set to be on.
     * Note, we do not set FLAG_MAX on all flag sets.
     *
     * @return True if there are no flags set on, false otherwise
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isEmpty() {
        return flagSet.isEmpty();
    }

    /**
     * Returns true if all flags in this set are set to on, in other words, set is the set of all flags
     *
     * <p><b>Deliberate divergence from the C original.</b> {@code flag_is_full}
     * ({@code z-bitflag.c}) compares each byte of the bit array against {@code -1}, so it
     * also requires the <em>padding</em> bits above the last named flag to be set. When the
     * number of flags is not a multiple of eight, C therefore answers false for a set in
     * which every named flag is on, and answers true only after {@code flag_setall} has
     * written {@code 255} across the whole array. This port has no padding — the enum's
     * constants are exactly the flags — so it answers on the named flags alone, which is
     * what a caller would actually mean.
     *
     * <p>Nothing rests on the choice: {@code flag_is_full} is dead code in the C original.
     * It is called neither directly nor through any of the nine per-subsystem macros that
     * wrap it ({@code of_is_full}, {@code pf_is_full}, {@code rf_is_full}, ...), which are
     * themselves never invoked. It is carried here only so that the family is complete, and
     * so that a future caller has the sane semantics rather than the byte-padding one.
     *
     * @return true if all the flags in set are on, false otherwise
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isFull() {
        return flagSet.size() == all.size();
    }

    /**
     * Returns true if there is at least one element that exists in both this and other, and false otherwise
     *
     * @param other the set we are comparing against
     * @return true if at least one flag is set in both this.set and other.set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isInter(@NotNull Flag<E> other) {
        for (E flag : other.flagSet) {
            if (flagSet.contains(flag))
                return true;
        }

        return false;
    }

    /**
     * Compares to sets and returns true if the other is a subset of this
     *
     * @param other the other set to compare
     * @return False if there exists one flag set in other which is not set in this, true otherwise
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isSubset(@NotNull Flag<E> other) {
        for (E flag : other.flagSet)
            if (!flagSet.contains(flag))
                return false;

        return true;
    }

    /**
     * Returns true if this and other have exactly the same flags set, and all flags which are not set in one is set in
     * the other and vice versa. I.e. the sets are equal
     *
     * @param other the set we are comparing
     * @return True if both this and other have exactly the same pattern of flags set.
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isEqual(@NotNull Flag<E> other) {
        return other.isSubset(this) && isSubset(other);
    }

    /**
     * Sets the flag in this flag set
     *
     * @param flag the flag to set
     * @return false if the flag was already set, true otherwise
     */
    @Contract(mutates = "this")
    public boolean on(@NotNull E flag) {
        if (flagSet.contains(flag))
            return false;

        flagSet.add(flag);
        return true;
    }

    /**
     * Removes a flag (switches it off) from this set
     *
     * @param flag the flag to remove
     * @return true if the flag was there before the remove, false otherwise
     */
    @Contract(mutates = "this")
    public boolean off(@NotNull E flag) {
        if (flagSet.contains(flag)) {
            flagSet.remove(flag);
            return true;
        }
        return false;
    }

    /**
     * Sets all the flags in this set to off
     */
    @Contract(mutates = "this")
    public void wipe() {
        flagSet.clear();
    }

    /**
     * Set all the flags in this set to on
     */
    @Contract(mutates = "this")
    public void setAll() {
        flagSet.addAll(all);
    }

    /**
     * Toggle the state of all the flags in the set
     */
    @Contract(mutates = "this")
    public void negate() {
        for (E flag : all) {
            if (flagSet.contains(flag))
                flagSet.remove(flag);
            else
                flagSet.add(flag);
        }
    }

    /**
     * Overwrites this flag set with the contents of another, so that afterwards this set has
     * exactly the flags that {@code flag} has. This is the port of {@code flag_copy}
     * ({@code z-bitflag.c}), which is a {@code memcpy} over the destination array — and it
     * is the direction of that copy which decides the shape of this method. C writes into
     * its first argument, so the twenty-seven call sites of the wrapper macros
     * ({@code rsf_copy}, {@code of_copy}, {@code pf_copy}, ...) copy into a flag set that
     * lives inside some longer-lived struct. This port holds those in {@code final} fields,
     * so a method returning a new object — the deprecated {@link #copy()} — has nothing the
     * caller can do with its result. Hence the copy happens in place.
     *
     * <p>Note that this <em>replaces</em> rather than merges: any flag set here but not in
     * {@code flag} is switched off, which is what distinguishes this from {@link #union}.
     * That is the easy mistake to make when reading a C {@code *_copy} call, because the
     * name suggests only addition.
     *
     * <p>Only the flags are taken from {@code flag}; the two sets share no state afterwards, so
     * later changes to either are invisible to the other. That guarantee is what the defensive-copy
     * accessors rely on — {@code TrapKind.getFlags}, {@code ElementInfo.copy},
     * {@code PlayerUpkeep.getRedrawFlags} — each of which allocates a fresh set and copies into it.
     *
     * <p>Returns {@code void}, like C's {@code flag_copy}. It briefly returned {@code this} to allow
     * chaining, which forced every call site into a {@code x = x.copyFrom(y)} self-assignment to
     * satisfy {@code @CheckReturnValue}; nothing needed the value, and the idiom actively misled —
     * {@link #inter} once read {@code Flag<E> copy = copyFrom(other)}, where the returned "copy" was
     * the receiver itself, silently wiping the set being intersected.
     *
     * @param flag the flag set to copy from, left unmodified
     * @author Rowan Crowther
     */
    public void copyFrom(Flag<E> flag) {
        wipe();
        union(flag);
    }

    /**
     * Make this set the union of this set and the other set
     *
     * @param other the set to make this set the union of
     * @return true if any changes were made, false otherwise
     */
    @Contract(mutates = "this")
    public boolean union(@NotNull Flag<E> other) {
        boolean changesMade = false;

        for (E flag : other.flagSet) {
            if (!flagSet.contains(flag)) {
                flagSet.add(flag);
                changesMade = true;
            }
        }

        return changesMade;
    }

    /**
     * Modify this to be an intersection of this and other
     *
     * <p><b>Deliberate divergence from the C original.</b> The return value here is a true
     * "were changes made" answer: it is only set when a flag was actually cleared from this
     * set. {@code flag_inter} ({@code z-bitflag.c}) is looser than its own documentation —
     * it raises its {@code delta} whenever the two arrays merely <em>differ</em>
     * ({@code if (!(flags1[i] == flags2[i])) delta = true;}), so it reports true for cases
     * where the intersection changes nothing at all: intersecting {@code 0b01} with
     * {@code 0b11} leaves {@code 0b01} untouched yet still answers true. This port follows
     * what the C comment promises rather than what the C code does. Nothing depends on the
     * difference — every call site of {@code flag_inter} and of its wrapper macros
     * ({@code rf_inter}, {@code rsf_inter}, {@code of_inter}, ...) discards the result, and
     * the only other consumer is {@code flags_mask}, ported as {@link #mask}, whose own
     * callers discard it in turn.
     *
     * @param other the set to make this an intersection of
     * @return true if any changes were made, false otherwise
     */
    @Contract(mutates = "this")
    public boolean inter(@NotNull Flag<E> other) {
        return flagSet.retainAll(other.flagSet);
    }

    /**
     * Compute the difference of two flag sets and store it in this. So for all set flags in other, clear them in this.
     *
     * @param other the other flag set to compare to this
     * @return true if any changes were made, false otherwise
     */
    @Contract(mutates = "this")
    public boolean diff(@NotNull Flag<E> other) {
        boolean changesMade = false;

        for (E flag : other.flagSet) {
            if (flagSet.contains(flag)) {
                if (this.off(flag)) changesMade = true;
            }
        }

        return changesMade;
    }

    /**
     * Tests to see if any of the flags in a variable number of flags is set
     *
     * @param flags the list of flags to test this against
     * @return true if any one of the flags in the list is set in this, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    @SafeVarargs
    public final boolean test(E @NotNull ... flags) {
        for (E flag : flags) {
            if (flagSet.contains(flag))
                return true;
        }

        return false;
    }

    /**
     * Tests to see if any of the flags in a list of flags is set
     *
     * @param flags the list of flags to test this against
     * @return true if any one of the flags in the list is set in this, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public final boolean test(@NotNull List<E> flags) {
        for (E flag : flags) {
            if (flagSet.contains(flag))
                return true;
        }

        return false;
    }

    /**
     * Test all the flags in a given variable argument list to see if they are set
     *
     * @param flags The flags to test the value of
     * @return true if ALL the flags are set, false otherwise
     */
    @CheckReturnValue
    @SafeVarargs
    @Contract(pure = true)
    public final boolean testAll(E @NotNull ... flags) {
        for (E flag : flags) {
            if (!flagSet.contains(flag))
                return false;
        }

        return true;
    }

    /**
     * Test all the flags in a given list to see if they are set
     *
     * @param flags The flags to test the value of
     * @return true if ALL the flags are set, false otherwise
     */
    @CheckReturnValue
    @Contract(pure = true)
    public final boolean testAll(@NotNull List<E> flags) {
        for (E flag : flags) {
            if (!flagSet.contains(flag))
                return false;
        }

        return true;
    }

    /**
     * Clears a number of flags from the set, and returns true if any changes were made
     *
     * @param flags the flags to clear from the set
     * @return true if any of the flags were set before this was called, false otherwise
     */
    @Contract(mutates = "this")
    @SafeVarargs
    public final boolean clear(E @NotNull ... flags) {
        boolean changesMade = false;

        for (E flag : flags) {
            if (flagSet.contains(flag))
                changesMade = true;

            flagSet.remove(flag);
        }

        return changesMade;
    }

    /**
     * Clears a number of flags from the set, and returns true if any changes were made
     *
     * @param flags the flags to clear from the set
     * @return true if any of the flags were set before this was called, false otherwise
     */
    @Contract(mutates = "this")
    public final boolean clear(@NotNull List<E> flags) {
        boolean changesMade = false;

        for (E flag : flags) {
            if (flagSet.contains(flag))
                changesMade = true;

            flagSet.remove(flag);
        }

        return changesMade;
    }

    /**
     * Sets a number of different flags from a variable argument list
     *
     * @param flags the flags to add
     * @return true if changes were made, i.e. at least one of the flags was set to be off, false otherwise
     */
    @Contract(mutates = "this")
    @SafeVarargs
    public final boolean set(E @NotNull ... flags) {
        boolean changesMade = false;

        for (E flag : flags) {
            if (!flagSet.contains(flag))
                changesMade = true;

            flagSet.add(flag);
        }

        return changesMade;
    }

    /**
     * Sets a number of different flags from a list
     *
     * @param flags the flags to add
     * @return true if changes were made, i.e. at least one of the flags was set to be off, false otherwise
     */
    @Contract(mutates = "this")
    public boolean set(@NotNull List<E> flags) {
        boolean changesMade = false;

        for (E flag : flags) {
            if (!flagSet.contains(flag))
                changesMade = true;

            flagSet.add(flag);
        }

        return changesMade;
    }

    /**
     * Clear this set and then set a number of flags in a variable argument list to be on
     *
     * @param flags The set of flags to initialise the cleared Flag to
     */
    @SafeVarargs
    @Contract(mutates = "this")
    public final void init(E @NotNull ... flags) {
        flagSet.clear();

        flagSet.addAll(Arrays.stream(flags).toList());
    }

    /**
     * Clear this set and then set a number of flags in a list to be on
     *
     * @param flags The set of flags to initialise the cleared Flag to
     */
    @Contract(mutates = "this")
    public void init(@NotNull List<E> flags) {
        flagSet.clear();

        flagSet.addAll(flags);
    }

    /**
     * Computes the intersection of a set and multiple flags. The flags NOT specified are cleared in this, and true is
     * returned if any changes were made, false otherwise
     *
     * @param flags A set of flags where the compliment of them is checked against this to remove those which occur in
     *              the compliment and this
     * @return true if any changes were made, false otherwise.
     */
    @Contract(mutates = "this")
    @SafeVarargs
    public final boolean mask(E @NotNull ... flags) {
        Flag<E> mask = new Flag<>(eClass);
        mask.init(flags);

        return inter(mask);
    }

    /**
     * Computes the intersection of a set and multiple flags. The flags NOT specified are cleared in this, and true is
     * returned if any changes were made, false otherwise
     *
     * @param flags A set of flags where the compliment of them is checked against this to remove those which occur in
     *              the compliment and this
     * @return true if any changes were made, false otherwise.
     */
    @Contract(mutates = "this")
    public boolean mask(@NotNull List<E> flags) {
        Flag<E> mask = new Flag<>(eClass);
        mask.init(flags);

        return inter(mask);
    }
}