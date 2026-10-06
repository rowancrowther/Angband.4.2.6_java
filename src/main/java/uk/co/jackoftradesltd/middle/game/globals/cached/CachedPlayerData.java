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
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

/**
 * The player-level flag cache reused across a batch of UI-entry evaluations — the port of C's
 * {@code struct cached_player_data} ({@code ui-entry.c}). C splits a player's object flags into
 * two bitflag sets so {@code compute_ui_entry_values_for_player} can tell a permanent property
 * from a temporary one: {@link #untimed} holds what {@code player_flags} reports (race, class, and
 * the level-30 fear immunity of the classes that carry {@code PF_BRAVERY_30}), and {@link #timed}
 * holds what {@code player_flags_timed} reports, plus {@code OF_TRAP_IMMUNE} while
 * {@code TMD_TRAPSAFE} is running.
 *
 * <p>The {@code TMD_TRAPSAFE} step is not redundant with {@code player_flags_timed}.
 * {@code player.c} excludes that effect from {@code player_flags_timed} on purpose, so that a
 * player's flags can be tested for {@code OF_TRAP_IMMUNE} knowing it did not come from a timed
 * effect. {@code compute_ui_entry_values_for_player} wants the timed view to include it, so it adds
 * the flag itself, and {@link #populateFlags} does the same.
 *
 * <p>C fills the cache inline, in the {@code if (*cache == NULL) { ... }} block at the top of
 * {@code compute_ui_entry_values_for_player}, and hands the allocation back through
 * {@code **cache}. The port moves that block into {@link #populateFlags}, guarded by a flag rather
 * than a null pointer, and {@code UIEntryValueRegistry.computeForPlayer} calls it on every
 * evaluation. A caller that wants the cache to survive between evaluations must keep the
 * {@code CachedPlayerData} itself, since a Java method cannot replace its caller's null.
 *
 * <p>The per-flag accessors and getters build an empty set on demand when {@link #populateFlags}
 * has not yet run, so a cache can be assembled by hand without a {@code Player}; see
 * {@link #populateFlags} for how a later fill treats anything set that way. C has no counterpart,
 * since its struct always holds both arrays.
 *
 * <p>Class CachedPlayerData coded before 261006, commented in full on 261006.
 *
 * @author Rowan Crowther
 */
public class CachedPlayerData {
    /**
     * The player's permanent object flags (C: {@code cached_player_data.untimed}), as
     * {@code player_flags} reports them. {@code null} until {@link #populateFlags} or one of the
     * on-demand accessors creates it.
     *
     * <p>Field untimed coded before 261006, commented in full on 261006.
     */
    private Flag<ObjectFlag> untimed;
    /**
     * The player's currently active timed-effect object flags (C: {@code cached_player_data.timed}),
     * as {@code player_flags_timed} reports them plus {@code OF_TRAP_IMMUNE} while
     * {@code TMD_TRAPSAFE} runs. {@code null} until {@link #populateFlags} or one of the on-demand
     * accessors creates it.
     *
     * <p>Field timed coded before 261006, commented in full on 261006.
     */
    private Flag<ObjectFlag> timed;

    /**
     * Whether {@link #populateFlags} has already filled this cache from a player — the port's
     * stand-in for C testing {@code *cache == NULL}. Only {@link #populateFlags} sets it; the
     * on-demand accessors that create an empty set leave it {@code false}.
     *
     * <p>Field flagsSet coded before 261006, commented in full on 261006.
     */
    private boolean flagsSet = false;

    /**
     * Fills both flag sets from {@code player} the first time it is called, and does nothing on any
     * later call. Port of the {@code if (*cache == NULL)} block in C's
     * {@code compute_ui_entry_values_for_player} ({@code ui-entry.c}).
     *
     * <p>The fill has three steps, as in C: {@link #timed} receives {@code player_flags_timed}
     * ({@link Player#flagsTimed}); {@code OF_TRAP_IMMUNE} is added to it when the player's
     * {@code TMD_TRAPSAFE} counter is non-zero, because {@code player_flags_timed} leaves that effect
     * out; and {@link #untimed} receives {@code player_flags} ({@link Player#playerFlags}). C
     * performs the untimed step first and wipes {@code timed} before its step; the port allocates
     * each set empty instead, and the order cannot be observed since the two sets share no state.
     * {@link Player#playerFlags} is given {@link Player#getPlayerState()} for the
     * {@code PF_BRAVERY_30} test, where C reads {@code p->state} directly.
     *
     * <p>The cache is a snapshot. C documents that {@code *cache} is only valid for the state of the
     * player it was built from, and the guard here enforces the other half of that: a second call
     * with the same or a different player, or after the player's timed effects have changed, leaves
     * the first fill untouched, so a stale cache stays stale until it is replaced.
     *
     * <p>The first call <b>replaces</b> both sets with new ones, discarding anything put there
     * earlier through {@link #onTimedFlag}, {@link #onUntimedFlag}, {@link #getTimedFlags} or
     * {@link #getUntimedFlags}. A {@link Flag} reference obtained from a getter before that call no
     * longer belongs to this cache afterwards. C has no such sequence, since it never touches the
     * arrays before the fill.
     *
     * <p>Function populateFlags coded before 261006, commented in full on 261006.
     *
     * @param player the player whose flags are cached; must not be {@code null}, the caller having
     *               already answered the C {@code if (!p)} case
     */
    public void populateFlags(Player player) {
        if (!flagsSet) {
            timed = new Flag<>(ObjectFlag.class);
            player.flagsTimed(timed);

            // Trap safe setting of OF_TRAP_IMMUNE
            if (player.getTimedEffect(TimedEffect.TMD_TRAPSAFE) != 0)
                timed.on(ObjectFlag.OF_TRAP_IMMUNE);

            untimed = new Flag<>(ObjectFlag.class);
            player.playerFlags(player.getPlayerState(), untimed);
            flagsSet = true;
        }
    }

    /**
     * Sets {@code flag} in {@link #timed} (C: {@code of_on((*cache)->timed, flag)}), creating an
     * empty {@link #timed} first if it does not yet exist.
     *
     * <p>Function onTimedFlag coded before 261006, commented in full on 261006.
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
     * <p>Function onUntimedFlag coded before 261006, commented in full on 261006.
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
     * <p>Function offTimedFlag coded before 261006, commented in full on 261006.
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
     * <p>Function offUntimedFlag coded before 261006, commented in full on 261006.
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
     * <p>Function hasTimedFlag coded before 261006, commented in full on 261006.
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
     * behaviour as {@link #hasTimedFlag(ObjectFlag)}. C reads it as
     * {@code of_has((*cache)->untimed, flag)}.
     *
     * <p>Function hasUntimedFlag coded before 261006, commented in full on 261006.
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
     * Returns the live untimed flag set (C: {@code (*cache)->untimed}), creating it empty first if
     * it does not yet exist. The set is the cache's own, not a copy, so changes made through it
     * change the cache; a first {@link #populateFlags} call replaces it with a new set.
     *
     * <p>Function getUntimedFlags coded before 261006, commented in full on 261006.
     *
     * @return this cache's untimed (permanent) flag set
     */
    public @NotNull Flag<ObjectFlag> getUntimedFlags() {
        if (untimed == null) untimed = new Flag<>(ObjectFlag.class);
        return untimed;
    }

    /**
     * Returns the live timed flag set (C: {@code (*cache)->timed}), creating it empty first if it
     * does not yet exist. The set is the cache's own, not a copy, so changes made through it change
     * the cache; a first {@link #populateFlags} call replaces it with a new set.
     *
     * <p>Function getTimedFlags coded before 261006, commented in full on 261006.
     *
     * @return this cache's timed (active-effect) flag set
     */
    public @NotNull Flag<ObjectFlag> getTimedFlags() {
        if (timed == null) timed = new Flag<>(ObjectFlag.class);
        return timed;
    }
}
