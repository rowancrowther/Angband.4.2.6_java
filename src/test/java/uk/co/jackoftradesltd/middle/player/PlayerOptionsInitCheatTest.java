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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerOptions.optionsInitCheat()}, the port of C's {@code options_init_cheat}
 * ({@code option.c:133-143}).
 *
 * <p>C clears {@code opt[i]} and {@code opt[i + 1]} for every cheat-type index {@code i}, relying
 * on the {@code list-options.h:7} convention that every cheat option is immediately followed by
 * its paired score option. Expected values here are derived from that pairing
 * ({@code cheat_hear}/{@code score_hear}, {@code cheat_room}/{@code score_room},
 * {@code cheat_xtra}/{@code score_xtra}, {@code cheat_live}/{@code score_live}), not from the
 * Java implementation.
 *
 * <p>The private {@code options} field is reached directly to set up starting state, the same
 * way {@code PlayerOptionsRestoreMaintainerTest} does.
 *
 * <p>Class PlayerOptionsInitCheatTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class PlayerOptionsInitCheatTest {

    @SuppressWarnings("unchecked")
    private static Flag<PlayerOptionEnum> flagOf(PlayerOptions options) throws Exception {
        Field field = PlayerOptions.class.getDeclaredField("options");
        field.setAccessible(true);
        return (Flag<PlayerOptionEnum>) field.get(options);
    }

    private static void setAllOn(PlayerOptions options) throws Exception {
        Flag<PlayerOptionEnum> flag = flagOf(options);
        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            flag.on(option);
        }
    }

    @Nested
    class ItClearsEachCheatScorePair {

        @ParameterizedTest
        @EnumSource(value = PlayerOptionEnum.class, names = {
                "OP_cheat_hear", "OP_cheat_room", "OP_cheat_xtra", "OP_cheat_live"})
        void clearsTheCheatOptionItself(PlayerOptionEnum cheat) throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertFalse(options.has(cheat), cheat + " must be cleared");
        }

        @Test
        void clearsScoreHearWhenCheatHearIsOn() throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertFalse(options.has(PlayerOptionEnum.OP_score_hear));
        }

        @Test
        void clearsScoreRoomWhenCheatRoomIsOn() throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertFalse(options.has(PlayerOptionEnum.OP_score_room));
        }

        @Test
        void clearsScoreXtraWhenCheatXtraIsOn() throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertFalse(options.has(PlayerOptionEnum.OP_score_xtra));
        }

        @Test
        void clearsScoreLiveWhenCheatLiveIsOn() throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertFalse(options.has(PlayerOptionEnum.OP_score_live));
        }

        @Test
        void clearsAScoreOptionEvenWhenItsCheatOptionWasAlreadyOff() throws Exception {
            PlayerOptions options = new PlayerOptions();
            flagOf(options).on(PlayerOptionEnum.OP_score_hear);

            options.optionsInitCheat();

            assertFalse(options.has(PlayerOptionEnum.OP_score_hear),
                    "C clears opt[i + 1] unconditionally once opt[i] is a cheat index");
        }
    }

    @Nested
    class ItLeavesUnrelatedPagesAlone {

        @Test
        void doesNotTouchTheInterfaceOptionImmediatelyBeforeTheFirstCheatPair() throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertTrue(options.has(PlayerOptionEnum.OP_effective_speed),
                    "prevIsCheat must start false, not leak from the option before the first cheat");
        }

        @Test
        void doesNotTouchTheBirthOptionImmediatelyAfterTheLastScorePair() throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertTrue(options.has(PlayerOptionEnum.OP_birth_randarts),
                    "prevIsCheat must reset after each score option, not carry past the last pair");
        }

        @Test
        void doesNotTouchOtherInterfaceOptions() throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertTrue(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        }

        @Test
        void doesNotTouchOtherBirthOptions() throws Exception {
            PlayerOptions options = new PlayerOptions();
            setAllOn(options);

            options.optionsInitCheat();

            assertTrue(options.has(PlayerOptionEnum.OP_birth_know_runes));
        }
    }

    @Nested
    class ItIsIdempotentWhenAlreadyClear {

        @Test
        void doingNothingWhenAllCheatAndScoreOptionsAreAlreadyOff() {
            PlayerOptions options = new PlayerOptions();

            options.optionsInitCheat();

            for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
                if (option.getPlayerOptionType() == PlayerOptionTypes.CHEAT
                        || option.getPlayerOptionType() == PlayerOptionTypes.SCORE) {
                    assertFalse(options.has(option), option + " should remain off");
                }
            }
        }
    }
}
