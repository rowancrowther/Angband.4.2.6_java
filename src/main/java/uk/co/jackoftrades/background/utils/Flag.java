package uk.co.jackoftrades.background.utils;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class Flag<E extends Enum<E>> {
    private EnumSet<E> set;
    private final EnumSet<E> all;
    private final Class<E> eClass;

    /**
     * Constructor, as this is a generic class, the type of flag set we are using has to be passed in
     *
     * @param eClass The class of the enum
     */
    public Flag(Class<E> eClass) {
        this.eClass = eClass;
        set = EnumSet.noneOf(this.eClass);
        all = EnumSet.allOf(this.eClass);
    }

    /**
     * Returns true if the incoming parameter is part of the flag set, and false otherwise
     *
     * @param flag The flag we are testing
     * @return true if flag is in set, false otherwise
     */
    public boolean has(E flag) {
        return set.contains(flag);
    }

    /**
     * Gets the next flag which is set in this bitfield. The flags are assumed to be ordered in the order which they are
     * defined in the relevant enum classes
     *
     * @param currentFlag The current flag we are counting from
     * @return The next set flag, or the last flag in the enum if currentFLag isn't set, or there are no more set flags
     * after currentFlag
     */
    public E next(E currentFlag) {
        if (!set.contains(currentFlag)) return getLast();

        Object[] setToArray = set.toArray();

        for (int index = 0; index < set.size(); index++) {
            if (setToArray[index] == currentFlag) {
                if (index == set.size() - 1)
                    return getLast();

                //noinspection unchecked
                return (E) setToArray[index + 1];
            }
        }

        return getLast();
    }

    private E getLast() {
        Iterator<E> allFlags = all.iterator();
        E flag = null;

        while (allFlags.hasNext()) flag = allFlags.next();

        return flag;
    }

    /**
     * Counts the number of flags which are set in this flag set
     *
     * @return The size of the set of flags which are on
     */
    public int count() {
        return set.size();
    }

    /**
     * Returns true if the set is empty, i.e. no flags are set to on, and false if one or more flags are set to be on.
     * Note, we do not set FLAG_MAX on all flag sets.
     *
     * @return True if there are no flags set on, false otherwise
     */
    public boolean isEmpty() {
        return set.isEmpty();
    }

    /**
     * Returns true if all flags in this set are set to on, in other words, set is the set of all flags
     *
     * @return true if all the flags in set are on, false otherwise
     */
    public boolean isFull() {
        return set.size() == all.size();
    }

    /**
     * Returns true if there is at least one element that exists in both this and other, and false otherwise
     *
     * @param other the set we are comparing against
     * @return true if at least one flag is set in both this.set and other.set
     */
    public boolean isInter(@NonNull Flag<E> other) {
        for (E flag : other.set) {
            if (set.contains(flag))
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
    public boolean isSubset(@NonNull Flag<E> other) {
        for (E flag : other.set)
            if (!set.contains(flag))
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
    public boolean isEqual(@NonNull Flag<E> other) {
        return other.isSubset(this) && isSubset(other);
    }

    /**
     * Sets the flag in this flag set
     *
     * @param flag the flag to set
     * @return false if the flag was already set, true otherwise
     */
    public boolean on(E flag) {
        if (set.contains(flag))
            return false;

        set.add(flag);
        return true;
    }

    /**
     * Removes a flag (switches it off) from this set
     *
     * @param flag the flag to remove
     * @return true if the flag was there before the remove, false otherwise
     */
    public boolean off(E flag) {
        if (set.contains(flag)) {
            set.remove(flag);
            return true;
        }
        return false;
    }

    /**
     * Sets all the flags in this set to off
     */
    public void wipe() {
        set = EnumSet.noneOf(eClass);
    }

    /**
     * Set all the flags in this set to on
     */
    public void setAll() {
        set = EnumSet.allOf(eClass);
    }

    /**
     * Toggle the state of all the flags in the set
     */
    public void negate() {
        for (E flag : all) {
            if (set.contains(flag))
                set.remove(flag);
            else
                set.add(flag);
        }
    }

    /**
     * Prepare a flag set with the same flags set as this one
     *
     * @return a flag set with the same flags set as this one
     */
    public Flag<E> copy() {
        Flag<E> result = new Flag<>(eClass);

        for (E flag : set) {
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
    public boolean union(@NonNull Flag<E> other) {
        boolean changesMade = false;

        for (E flag : other.set) {
            if (!set.contains(flag)) {
                set.add(flag);
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
    public boolean inter(@NonNull Flag<E> other) {
        boolean changesMade = false;

        Flag<E> copy = other.copy();
        copy.negate();

        for (E flag : copy.set) {
            if (set.contains(flag)) {
                set.remove(flag);
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
    public boolean diff(@NonNull Flag<E> other) {
        boolean changesMade = false;

        for (E flag : other.set) {
            if (set.contains(flag)) {
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
    @Contract(pure = true)
    @SafeVarargs
    public final boolean test(E @NonNull ... flags) {
        for (E flag : flags) {
            if (set.contains(flag))
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
    @Contract(pure = true)
    public final boolean test(@NonNull List<E> flags) {
        for (E flag : flags) {
            if (set.contains(flag))
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
    @SafeVarargs
    @Contract(pure = true)
    public final boolean testAll(E @NonNull ... flags) {
        for (E flag : flags) {
            if (!set.contains(flag))
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
    @Contract(pure = true)
    public final boolean testAll(@NonNull List<E> flags) {
        for (E flag : flags) {
            if (!set.contains(flag))
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
    @SafeVarargs
    public final boolean clear(E @NonNull ... flags) {
        boolean changesMade = false;

        for (E flag : flags) {
            if (set.contains(flag))
                changesMade = true;

            set.remove(flag);
        }

        return changesMade;
    }

    /**
     * Clears a number of flags from the set, and returns true if any changes were made
     *
     * @param flags the flags to clear from the set
     * @return true if any of the flags were set before this was called, false otherwise
     */
    public final boolean clear(@NonNull List<E> flags) {
        boolean changesMade = false;

        for (E flag : flags) {
            if (set.contains(flag))
                changesMade = true;

            set.remove(flag);
        }

        return changesMade;
    }

    /**
     * Sets a number of different flags from a variable argument list
     *
     * @param flags the flags to add
     * @return true if changes were made, i.e. at least one of the flags was set to be off, false otherwise
     */
    @SafeVarargs
    public final boolean set(E @NonNull ... flags) {
        boolean changesMade = false;

        for (E flag : flags) {
            if (!set.contains(flag))
                changesMade = true;

            set.add(flag);
        }

        return changesMade;
    }

    /**
     * Sets a number of different flags from a list
     *
     * @param flags the flags to add
     * @return true if changes were made, i.e. at least one of the flags was set to be off, false otherwise
     */
    public boolean set(@NonNull List<E> flags) {
        boolean changesMade = false;

        for (E flag : flags) {
            if (!set.contains(flag))
                changesMade = true;

            set.add(flag);
        }

        return changesMade;
    }

    /**
     * Clear this set and then set a number of flags in a variable argument list to be on
     * @param flags The set of flags to initialise the cleared Flag to
     */
    @SafeVarargs
    public final void init(E @NonNull ... flags) {
        set = EnumSet.noneOf(eClass);

        set.addAll(Arrays.stream(flags).toList());
    }

    /**
     * Clear this set and then set a number of flags in a list to be on
     * @param flags The set of flags to initialise the cleared Flag to
     */
    public void init(@NonNull List<E> flags) {
        set = EnumSet.noneOf(eClass);

        Collections.addAll(flags);
    }

    /**
     * Computes the intersection of a set and multiple flags. The flags NOT specified are cleared in this, and true is
     * returned if any changes were made, false otherwise
     *
     * @param flags A set of flags where the compliment of them is checked against this to remove those which occur in
     *              the compliment and this
     * @return true if any changes were made, false otherwise.
     */
    @SafeVarargs
    public final boolean mask(E @NonNull ... flags) {
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
    public boolean mask(@NonNull List<E> flags) {
        Flag<E> mask = new Flag<>(eClass);
        mask.init(flags);

        return inter(mask);
    }
}