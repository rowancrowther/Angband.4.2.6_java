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

package uk.co.jackoftradesltd.middle.game.globals.cached;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

/**
 * The player-level flag cache reused across a batch of UI-entry evaluations — the port of C's
 * {@code struct cached_player_data} ({@code ui-entry.c:50-53}). C splits a player's object flags
 * into two bitflag sets so {@code compute_ui_entry_values_for_player} can tell a permanent property
 * from a temporary one: {@link #untimed} holds what {@code player_flags} reports (race, class,
 * equipment, shapechange — everything that isn't a timed effect), and {@link #timed} holds what
 * {@code player_flags_timed} reports, with {@code TMD_TRAPSAFE} folded in as
 * {@code OF_TRAP_IMMUNE} to match {@code player-timed.c}'s handling of that effect. This class is
 * the plain data holder only; the population that fills {@link #untimed}/{@link #timed} the way C's
 * {@code if (*cache == NULL) { ... }} block does lives in
 * {@code UIEntryValueRegistry.computeForPlayer}, matching where C itself does the work — inline in
 * {@code compute_ui_entry_values_for_player}, not in a constructor of its own.
 *
 * <p>Both flag sets are created lazily, on first access, rather than up front — C's
 * {@code mem_alloc} always allocates both bitflag arrays together, but a cache built for one
 * {@code ui_entry} loop may only ever be queried for one of the two, so the port defers the second
 * allocation until it is actually asked for.
 *
 * @author Rowan Crowther
 */
public class CachedPlayerData {
    /**
     * The player's permanent object flags (C: {@code cached_player_data.untimed}).
     */
    private Flag<ObjectFlag> untimed;
    /**
     * The player's currently active timed-effect object flags (C: {@code cached_player_data.timed}).
     */
    private Flag<ObjectFlag> timed;

    /**
     * Sets {@code flag} in {@link #timed} (C: {@code of_on((*cache)->timed, flag)}), creating an
     * empty {@link #timed} first if it does not yet exist.
     *
     * @param flag the flag to set
     * @return false if the flag was already set, true otherwise
     */
    public boolean onTimedFlag(ObjectFlag flag) {
        if (timed == null) {
            timed = new Flag<>(ObjectFlag.class);
        }
        return timed.on(flag);
    }

    /**
     * Sets {@code flag} in {@link #untimed}, creating an empty {@link #untimed} first if it does
     * not yet exist.
     *
     * @param flag the flag to set
     * @return false if the flag was already set, true otherwise
     */
    public boolean onUntimedFlag(ObjectFlag flag) {
        if (untimed == null) {
            untimed = new Flag<>(ObjectFlag.class);
        }
        return untimed.on(flag);
    }

    /**
     * Removes {@code flag} from {@link #timed}. Unlike {@link #onTimedFlag(ObjectFlag)} this does
     * not allocate {@link #timed} on demand: with nothing there yet, the flag cannot be set, so the
     * answer is {@code false} without needing a set to ask it of.
     *
     * @param flag the flag to remove
     * @return true if the flag was there before the remove, false otherwise (including when
     * {@link #timed} does not yet exist)
     */
    public boolean offTimedFlag(ObjectFlag flag) {
        if (timed == null) {
            return false;
        }
        return timed.off(flag);
    }

    /**
     * Removes {@code flag} from {@link #untimed}, with the same no-allocation-on-empty behaviour as
     * {@link #offTimedFlag(ObjectFlag)}.
     *
     * @param flag the flag to remove
     * @return true if the flag was there before the remove, false otherwise (including when
     * {@link #untimed} does not yet exist)
     */
    public boolean offUntimedFlag(ObjectFlag flag) {
        if (untimed == null) {
            return false;
        }
        return untimed.off(flag);
    }

    /**
     * Tests whether {@code flag} is set in {@link #timed} (C: {@code of_has((*cache)->timed, flag)}),
     * without allocating {@link #timed} if it does not yet exist — an absent set has nothing set in
     * it, so the answer is {@code false} either way.
     *
     * @param flag the flag to test
     * @return true if the flag is set
     */
    public boolean hasTimedFlag(ObjectFlag flag) {
        if (timed == null) {
            return false;
        }
        return timed.has(flag);
    }

    /**
     * Tests whether {@code flag} is set in {@link #untimed}, with the same no-allocation-on-empty
     * behaviour as {@link #hasTimedFlag(ObjectFlag)}.
     *
     * @param flag the flag to test
     * @return true if the flag is set
     */
    public boolean hasUntimedFlag(ObjectFlag flag) {
        if (untimed == null) {
            return false;
        }
        return untimed.has(flag);
    }

    /**
     * @return this cache's untimed (permanent) flag set, creating it empty first if it does not yet
     * exist
     */
    public @NotNull Flag<ObjectFlag> getUntimedFlags() {
        if (untimed == null) untimed = new Flag<>(ObjectFlag.class);
        return untimed;
    }

    /**
     * @return this cache's timed (active-effect) flag set, creating it empty first if it does not
     * yet exist
     */
    public @NotNull Flag<ObjectFlag> getTimedFlags() {
        if (timed == null) timed = new Flag<>(ObjectFlag.class);
        return timed;
    }
}
