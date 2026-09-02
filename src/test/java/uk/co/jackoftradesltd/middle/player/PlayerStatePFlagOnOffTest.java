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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerState#playerFlagOn(PlayerFlag)} and
 * {@link PlayerState#playerFlagOff(PlayerFlag)}, the ports of C's {@code pf_on} and {@code pf_off}
 * ({@code player.h:60-61}) over {@code state.pflags}.
 *
 * <p>The expected values here come from {@code flag_on} and {@code flag_off} themselves
 * ({@code z-bitflag.c:198-210} and {@code 240-252}), not from the Java. Both C functions return
 * <em>whether the call changed anything</em>, not whether the flag ends up in the state the name
 * suggests: {@code flag_on} answers {@code false} when the bit was already set and it wrote
 * nothing, and {@code flag_off} answers {@code false} when the bit was already clear. The two
 * readings only differ on the repeat call, which is why every test below makes the call twice.
 *
 * <p>The other thing worth pinning is that the two act on a single flag and leave the rest of the
 * set alone — C reaches one byte of the bit array with a mask, so a neighbouring flag being
 * disturbed would be a real bug and a silent one.
 */
@DisplayName("PlayerState player-flag on/off")
class PlayerStatePFlagOnOffTest {

    private static void assertEqualsCount(int expected, PlayerState state) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, state.getPlayerFlag().count(),
                "only the flags explicitly set are on");
    }

    /**
     * {@code flag_on} takes the "already set" exit before it writes, and returns {@code false}
     * ({@code z-bitflag.c:205}). The flag is on either way; only the answer differs.
     */
    @Test
    @DisplayName("playerFlagOn reports the change, and only the first call is one")
    void playerFlagOnReportsChange() {
        PlayerState state = new PlayerState();

        assertAll(
                () -> assertFalse(state.hasPFlag(PlayerFlag.PF_UNLIGHT),
                        "a fresh state holds no player flags"),
                () -> assertTrue(state.playerFlagOn(PlayerFlag.PF_UNLIGHT),
                        "the flag was off, so setting it is a change"),
                () -> assertTrue(state.hasPFlag(PlayerFlag.PF_UNLIGHT),
                        "and it is set afterwards"),
                () -> assertFalse(state.playerFlagOn(PlayerFlag.PF_UNLIGHT),
                        "the second set writes nothing, so C answers false"),
                () -> assertTrue(state.hasPFlag(PlayerFlag.PF_UNLIGHT),
                        "the flag is still on regardless of that answer"));
    }

    /**
     * The mirror: {@code flag_off} clears the bit and answers {@code true} only when there was a
     * bit to clear ({@code z-bitflag.c:247}). Clearing a flag that is already off is not an error.
     */
    @Test
    @DisplayName("playerFlagOff reports the change, and only the first call is one")
    void playerFlagOffReportsChange() {
        PlayerState state = new PlayerState();
        state.playerFlagOn(PlayerFlag.PF_EVIL);

        assertAll(
                () -> assertTrue(state.playerFlagOff(PlayerFlag.PF_EVIL),
                        "the flag was held, so clearing it is a change"),
                () -> assertFalse(state.hasPFlag(PlayerFlag.PF_EVIL),
                        "and it is gone afterwards"),
                () -> assertFalse(state.playerFlagOff(PlayerFlag.PF_EVIL),
                        "the second clear changes nothing, so C answers false"),
                () -> assertFalse(state.hasPFlag(PlayerFlag.PF_EVIL),
                        "the flag stays off"));
    }

    /**
     * Clearing a flag that was never set at all — the case a caller reading the return as "is it
     * off now" would get wrong on the very first call.
     */
    @Test
    @DisplayName("playerFlagOff on a flag that was never set answers false")
    void playerFlagOffOnUnsetFlag() {
        PlayerState state = new PlayerState();

        assertAll(
                () -> assertFalse(state.playerFlagOff(PlayerFlag.PF_COMBAT_REGEN),
                        "nothing to clear, so nothing changed"),
                () -> assertFalse(state.hasPFlag(PlayerFlag.PF_COMBAT_REGEN)));
    }

    /**
     * C masks a single bit in a single byte of the array, so setting one flag and clearing another
     * must leave every other flag exactly as it was.
     */
    @Test
    @DisplayName("on and off touch one flag and leave the rest of the set alone")
    void neighbouringFlagsAreUndisturbed() {
        PlayerState state = new PlayerState();
        state.playerFlagOn(PlayerFlag.PF_EVIL);
        state.playerFlagOn(PlayerFlag.PF_UNLIGHT);
        state.playerFlagOn(PlayerFlag.PF_COMBAT_REGEN);

        state.playerFlagOff(PlayerFlag.PF_UNLIGHT);

        assertAll(
                () -> assertTrue(state.hasPFlag(PlayerFlag.PF_EVIL)),
                () -> assertFalse(state.hasPFlag(PlayerFlag.PF_UNLIGHT)),
                () -> assertTrue(state.hasPFlag(PlayerFlag.PF_COMBAT_REGEN)),
                () -> assertEqualsCount(2, state));
    }

    /**
     * The pair writes through to the same set {@link PlayerState#getPlayerFlag()} hands out and
     * {@link PlayerState#hasPFlag(PlayerFlag)} reads — in C they are all the one {@code pflags}
     * array, so a port that copied anywhere would break {@code calcBonuses}.
     */
    @Test
    @DisplayName("the pair writes through to the live pflags set")
    void writesThroughToTheLiveSet() {
        PlayerState state = new PlayerState();

        state.playerFlagOn(PlayerFlag.PF_FAST_SHOT);
        assertTrue(state.getPlayerFlag().has(PlayerFlag.PF_FAST_SHOT),
                "the getter sees what playerFlagOn wrote");

        state.getPlayerFlag().on(PlayerFlag.PF_BLESS_WEAPON);
        assertTrue(state.playerFlagOff(PlayerFlag.PF_BLESS_WEAPON),
                "and playerFlagOff sees what the getter wrote");
    }

    /**
     * {@link PlayerState#wipe()} empties the set, so the first set after it counts as a change
     * again — the guarantee {@code calcBonuses} rests on, rebuilding rather than accumulating.
     */
    @Test
    @DisplayName("after a wipe the first playerFlagOn is a change again")
    void wipeResetsTheChangeAnswer() {
        PlayerState state = new PlayerState();
        state.playerFlagOn(PlayerFlag.PF_UNLIGHT);

        state.wipe();

        assertAll(
                () -> assertFalse(state.hasPFlag(PlayerFlag.PF_UNLIGHT)),
                () -> assertTrue(state.playerFlagOn(PlayerFlag.PF_UNLIGHT)));
    }
}
