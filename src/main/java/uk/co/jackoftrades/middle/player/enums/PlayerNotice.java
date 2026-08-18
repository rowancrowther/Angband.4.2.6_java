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

package uk.co.jackoftrades.middle.player.enums;

/**
 * "Notice" flags identifying pending housekeeping the engine must carry out before the next
 * command — the {@code PN_*} set from the C original ({@code player-calcs.h}).
 *
 * <p>Third sibling to {@link PlayerUpdateEnum} ({@code PU_*}) and {@link PlayerRedraw}
 * ({@code PR_*}). Where {@code PU_*} marks parts of the <em>model</em> needing recomputation and
 * {@code PR_*} marks parts of the <em>screen</em> needing a repaint, {@code PN_*} marks discrete
 * <em>actions</em> queued up to run once — reordering or combining the pack, applying ignore
 * rules, flushing buffered monster messages. Code raises the relevant flag when state changes and
 * a later notice pass performs each pending action and clears it, so the work happens once at a
 * safe point rather than repeatedly mid-mutation.
 *
 * <p>Held as a {@link uk.co.jackoftrades.channel.utils.Flag} over this enum in the port, in place
 * of C's packed {@code uint32_t} bitflags, for the same type-safety reason as the other flag
 * families.
 *
 * @author Rowan Crowther
 */
public enum PlayerNotice {
    /**
     * Combine (merge stackable items within) the pack.
     */
    PN_COMBINE,
    /**
     * Re-apply ignore rules to newly-changed items.
     */
    PN_IGNORE,
    /**
     * Flush the buffered monster pain / status messages.
     */
    PN_MON_MESSAGE
}
