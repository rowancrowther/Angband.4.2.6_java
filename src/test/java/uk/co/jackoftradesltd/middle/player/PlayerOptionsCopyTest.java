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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerOptions#copy}, the port of the plain struct assignment C uses to save and
 * restore {@code player->opts} around birth ({@code opts_save = p->opts}, {@code player-birth.c:403}).
 *
 * <p>{@code PlayerOptions} exposes no setters for its fields beyond the option flags, so the
 * fixtures here reach the private fields directly by reflection, the same way
 * {@code PlayerBodyCopyTest} reaches into {@code EquipSlot}.
 *
 * <p>Class PlayerOptionsCopyTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class PlayerOptionsCopyTest {

    private PlayerOptions source;

    @SuppressWarnings("unchecked")
    private static Flag<PlayerOptionEnum> flagOf(PlayerOptions options) throws Exception {
        Field field = PlayerOptions.class.getDeclaredField("options");
        field.setAccessible(true);
        return (Flag<PlayerOptionEnum>) field.get(options);
    }

    private static void setIntField(PlayerOptions options, String name, int value) throws Exception {
        Field field = PlayerOptions.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setInt(options, value);
    }

    private static int getIntField(PlayerOptions options, String name) throws Exception {
        Field field = PlayerOptions.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(options);
    }

    @BeforeEach
    void setUp() throws Exception {
        source = new PlayerOptions();
        flagOf(source).on(PlayerOptionEnum.OP_rogue_like_commands);
        flagOf(source).on(PlayerOptionEnum.OP_birth_randarts);
        setIntField(source, "hitpointWarn", 3);
        setIntField(source, "lazymoveDelay", 20);
        setIntField(source, "delayFactor", 40);
        setIntField(source, "nameSuffix", 2);
    }

    @Nested
    class TheSettingsAreCarriedAcross {

        @Test
        void optionsThatAreOnAreCopiedOn() {
            PlayerOptions copy = source.copy();

            assertTrue(copy.has(PlayerOptionEnum.OP_rogue_like_commands));
            assertTrue(copy.has(PlayerOptionEnum.OP_birth_randarts));
        }

        @Test
        void optionsThatAreOffAreCopiedOff() {
            PlayerOptions copy = source.copy();

            assertFalse(copy.has(PlayerOptionEnum.OP_use_sound));
            assertFalse(copy.has(PlayerOptionEnum.OP_birth_no_recall));
        }

        @Test
        void theNumericSettingsAreCopied() throws Exception {
            PlayerOptions copy = source.copy();

            assertEquals(3, getIntField(copy, "hitpointWarn"));
            assertEquals(20, getIntField(copy, "lazymoveDelay"));
            assertEquals(40, getIntField(copy, "delayFactor"));
            assertEquals(2, getIntField(copy, "nameSuffix"));
        }
    }

    @Nested
    class NothingIsSharedWithTheSource {

        @Test
        void theCopyIsADifferentObject() {
            assertNotSame(source, source.copy());
        }

        @Test
        void theFlagSetIsNotShared() throws Exception {
            PlayerOptions copy = source.copy();

            assertNotSame(flagOf(source), flagOf(copy));
        }

        @Test
        void switchingAnOptionOnTheCopyDoesNotAffectTheSource() throws Exception {
            PlayerOptions copy = source.copy();

            flagOf(copy).on(PlayerOptionEnum.OP_use_sound);

            assertTrue(copy.has(PlayerOptionEnum.OP_use_sound));
            assertFalse(source.has(PlayerOptionEnum.OP_use_sound),
                    "the source must not gain an option switched on the copy");
        }

        @Test
        void switchingAnOptionOnTheSourceDoesNotAffectAnEarlierCopy() throws Exception {
            PlayerOptions copy = source.copy();

            flagOf(source).on(PlayerOptionEnum.OP_use_sound);

            assertFalse(copy.has(PlayerOptionEnum.OP_use_sound),
                    "a copy taken earlier must not see options switched on the source afterwards");
        }

        @Test
        void twoCopiesOfOneSourceAreIndependent() throws Exception {
            PlayerOptions first = source.copy();
            PlayerOptions second = source.copy();

            flagOf(first).on(PlayerOptionEnum.OP_use_sound);

            assertTrue(first.has(PlayerOptionEnum.OP_use_sound));
            assertFalse(second.has(PlayerOptionEnum.OP_use_sound));
        }

        @Test
        void aCopyOfACopyIsStillIndependent() throws Exception {
            PlayerOptions copy = source.copy().copy();

            flagOf(copy).on(PlayerOptionEnum.OP_use_sound);

            assertFalse(source.has(PlayerOptionEnum.OP_use_sound));
            assertTrue(copy.has(PlayerOptionEnum.OP_rogue_like_commands));
        }
    }
}
