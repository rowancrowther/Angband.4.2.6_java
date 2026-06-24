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

package uk.co.jackoftrades.middle.player;

import org.jetbrains.annotations.Contract;

/**
 * A single named bare-handed melee attack ("blow") available to a player class.
 *
 * <p>Ports the C {@code struct player_blow} ({@code player.h}), originally a singly
 * linked list of {@code {next, name}} nodes naming the martial-arts strikes a
 * barehanded class can land — e.g. a Monk's "punch", "kick" or "round kick". Each
 * node carries only the display name of one strike; in combat the game selects one
 * at random (scaled by character level) when an unarmed blow is dealt.
 *
 * <p><b>Why a value holder rather than a list node:</b> the C original folds the
 * collection into the type via its {@code next} pointer. This port keeps each blow
 * as a standalone value and lets an ordinary collection on the owning
 * {@link PlayerClass} provide the "list", so the model carries no hand-rolled
 * linked-list plumbing.
 *
 * @author ClaudeCode
 */
public class PlayerBlow {
    /**
     * Display name of this unarmed strike, e.g. {@code "punch"} (C: {@code player_blow.name}).
     */
    private String blowName;

    /**
     * Creates a blow with the given display name.
     *
     * @param blowName the strike name as it appears in {@code class.txt} and in combat messages
     */
    @Contract(pure = true)
    public PlayerBlow(String blowName) {
        this.blowName = blowName;
    }

    /**
     * Returns this strike's display name.
     *
     * @return the blow name supplied at construction
     */
    @Contract(pure = true)
    public String getBlowName() {
        return blowName;
    }
}
