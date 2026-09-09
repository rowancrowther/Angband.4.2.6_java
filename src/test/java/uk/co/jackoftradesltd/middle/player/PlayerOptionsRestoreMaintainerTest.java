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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerOptions.restoreMaintainer(PlayerOptionTypes)}, the port of C's
 * {@code options_restore_maintainer} ({@code option.c:338-345}).
 *
 * <p>The method is private, so every test reaches it through reflection, following the pattern in
 * {@code PlayerBirthIntToRomanTest}. Since {@code PlayerOptions} exposes no setter for an
 * individual option, tests that need a non-default starting state reach the private {@code options}
 * field directly, the same way {@code PlayerBodyCopyTest} reaches into {@code EquipSlot}.
 *
 * <p>Class PlayerOptionsRestoreMaintainerTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class PlayerOptionsRestoreMaintainerTest {

    private static void restoreMaintainer(PlayerOptions options, PlayerOptionTypes type) throws Exception {
        Method method = PlayerOptions.class.getDeclaredMethod("restoreMaintainer", PlayerOptionTypes.class);
        method.setAccessible(true);
        method.invoke(options, type);
    }

    @SuppressWarnings("unchecked")
    private static Flag<PlayerOptionEnum> flagOf(PlayerOptions options) throws Exception {
        Field field = PlayerOptions.class.getDeclaredField("options");
        field.setAccessible(true);
        return (Flag<PlayerOptionEnum>) field.get(options);
    }

    @Nested
    class ItResetsEachOptionToItsOwnNormalValue {

        @Test
        void aNormallyOnOptionIsSwitchedOnFromEmpty() throws Exception {
            PlayerOptions options = new PlayerOptions();

            restoreMaintainer(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_pickup_inven));
        }

        @Test
        void aNormallyOffOptionThatWasSwitchedOnIsSwitchedBackOff() throws Exception {
            PlayerOptions options = new PlayerOptions();
            flagOf(options).on(PlayerOptionEnum.OP_rogue_like_commands);

            restoreMaintainer(options, PlayerOptionTypes.INTERFACE);

            assertFalse(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        }

        @Test
        void aNormallyOnOptionThatWasSwitchedOffIsSwitchedBackOn() throws Exception {
            PlayerOptions options = new PlayerOptions();
            flagOf(options).on(PlayerOptionEnum.OP_pickup_inven);
            flagOf(options).off(PlayerOptionEnum.OP_pickup_inven);

            restoreMaintainer(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_pickup_inven));
        }

        @Test
        void birthOptionsResetToTheirOwnNormals() throws Exception {
            PlayerOptions options = new PlayerOptions();
            flagOf(options).on(PlayerOptionEnum.OP_birth_randarts);

            restoreMaintainer(options, PlayerOptionTypes.BIRTH);

            assertTrue(options.has(PlayerOptionEnum.OP_birth_connect_stairs), "normally-on birth option");
            assertFalse(options.has(PlayerOptionEnum.OP_birth_randarts), "normally-off birth option");
        }
    }

    @Nested
    class ItLeavesOtherPagesAlone {

        @Test
        void resettingInterfaceDoesNotTouchBirthOptions() throws Exception {
            PlayerOptions options = new PlayerOptions();
            flagOf(options).on(PlayerOptionEnum.OP_birth_randarts);

            restoreMaintainer(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_birth_randarts),
                    "resetting one page must not touch another page's options");
        }

        @Test
        void resettingBirthDoesNotTouchInterfaceOptions() throws Exception {
            PlayerOptions options = new PlayerOptions();
            flagOf(options).on(PlayerOptionEnum.OP_rogue_like_commands);

            restoreMaintainer(options, PlayerOptionTypes.BIRTH);

            assertTrue(options.has(PlayerOptionEnum.OP_rogue_like_commands),
                    "resetting one page must not touch another page's options");
        }
    }
}
