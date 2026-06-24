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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.utils;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

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
 * @author ClaudeCode
 */
public class Flag<E extends Enum<E>> {
    /**
     * The flags currently switched on.
     *
     * @author ClaudeCode
     */
    private final EnumSet<E> flagSet;
    /**
     * The full set of every possible flag, cached for full/negate/mask operations.
     *
     * @author ClaudeCode
     */
    private final EnumSet<E> all;
    /**
     * The enum class, retained so new {@link EnumSet}s can be built generically
     * (e.g. in {@link #copy()} and {@link #mask}).
     *
     * @author ClaudeCode
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
     * @return The next set flag, or the last flag in the enum if currentFLag isn't set, or there are no more set flags
     * after currentFlag
     */
    @Contract(pure = true)
    @CheckReturnValue
    public E next(@NotNull E currentFlag) {
        boolean found = false;

        for (E flag : flagSet) {
            if (found) return flag;
            if (flag.equals(currentFlag)) found = true;
        }

        return Collections.max(all);
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
     * Prepare a flag set with the same flags set as this one
     *
     * @return a flag set with the same flags set as this one
     */
    @Contract(pure = true)
    @CheckReturnValue
    public Flag<E> copy() {
        Flag<E> result = new Flag<>(eClass);

        for (E flag : flagSet) {
            result.on(flag);
        }

        return result;
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
     * @param other the set to make this an intersection of
     * @return true if any changes were made, false otherwise
     */
    @Contract(mutates = "this")
    public boolean inter(@NotNull Flag<E> other) {
        boolean changesMade = false;

        Flag<E> copy = other.copy();
        copy.negate();

        for (E flag : copy.flagSet) {
            if (flagSet.contains(flag)) {
                flagSet.remove(flag);
                changesMade = true;
            }
        }

        return changesMade;
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