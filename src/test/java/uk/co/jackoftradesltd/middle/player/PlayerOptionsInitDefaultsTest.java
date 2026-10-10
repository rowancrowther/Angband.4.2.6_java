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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@code PlayerOptions.initDefaults()}, the port of C's {@code options_init_defaults}
 * ({@code option.c}).
 *
 * <p>Expected values come from {@code list-options.h} and the C function: every option takes the
 * {@code normal} column of its row, the player's customized BIRTH and INTERFACE pages are laid
 * over the top, the delay factor becomes 40 and the hitpoint warning 3, and nothing else
 * (movement delay, name suffix) is touched. The set of normally-on options is written out below
 * from {@code list-options.h} rather than read back from {@link PlayerOptionEnum#isNormal()}.
 *
 * <p>The customized-options files are redirected to a JUnit {@code @TempDir}; the event bus is
 * replaced by a silent double so that a stray message cannot reach the real engine.
 *
 * <p>Class PlayerOptionsInitDefaultsTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class PlayerOptionsInitDefaultsTest {

    /**
     * The rows of {@code list-options.h} whose last column is {@code true}.
     */
    private static final Set<PlayerOptionEnum> NORMALLY_ON = EnumSet.of(
            PlayerOptionEnum.OP_pickup_inven,
            PlayerOptionEnum.OP_show_target,
            PlayerOptionEnum.OP_disturb_near,
            PlayerOptionEnum.OP_hp_changes_color,
            PlayerOptionEnum.OP_mouse_movement,
            PlayerOptionEnum.OP_birth_connect_stairs,
            PlayerOptionEnum.OP_birth_stacking,
            PlayerOptionEnum.OP_birth_feelings,
            PlayerOptionEnum.OP_birth_no_selling,
            PlayerOptionEnum.OP_birth_start_kit,
            PlayerOptionEnum.OP_birth_ai_learn);

    private String originalUserDir;
    private EventsHandler realBus;

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

    private static void writeCustomizedFile(String pageName, String... lines) throws IOException {
        Path file = Path.of(AngbandDirs.ANGBAND_DIRS.USER.getPath()
                + "customized_" + pageName + "_options.txt");
        Files.write(file, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void redirectUserDirAndBus(@TempDir Path tempDir) {
        originalUserDir = AngbandDirs.ANGBAND_DIRS.USER.getPath();
        AngbandDirs.ANGBAND_DIRS.USER.setPath(tempDir.toString() + java.io.File.separator);

        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(new SilentBus());
    }

    @AfterEach
    void restoreUserDirAndBus() {
        AngbandDirs.ANGBAND_DIRS.USER.setPath(originalUserDir);
        GameEngine.setEventsBusHandler(realBus);
    }

    @Test
    void everyOptionTakesItsNormalValueWhenThereAreNoCustomizedFiles() throws Exception {
        PlayerOptions options = new PlayerOptions();

        options.initDefaults();

        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            assertEquals(NORMALLY_ON.contains(option), options.has(option), option.name());
        }
    }

    @Test
    void theDelayFactorBecomesFortyAndTheHitpointWarningThree() throws Exception {
        PlayerOptions options = new PlayerOptions();

        options.initDefaults();

        assertEquals(40, getIntField(options, "delayFactor"));
        assertEquals(3, options.hitPointWarn());
    }

    /**
     * C's function sets only {@code delay_factor} and {@code hitpoint_warn} among the numeric
     * members.
     */
    @Test
    void theMovementDelayAndNameSuffixAreLeftAlone() throws Exception {
        PlayerOptions options = new PlayerOptions();
        setIntField(options, "lazymoveDelay", 17);
        setIntField(options, "nameSuffix", 5);

        options.initDefaults();

        assertEquals(17, getIntField(options, "lazymoveDelay"));
        assertEquals(5, getIntField(options, "nameSuffix"));
    }

    @Test
    void earlierSettingsAreReplacedNotKept() throws Exception {
        PlayerOptions options = new PlayerOptions();
        flagOf(options).on(PlayerOptionEnum.OP_rogue_like_commands);
        flagOf(options).on(PlayerOptionEnum.OP_cheat_live);
        flagOf(options).off(PlayerOptionEnum.OP_pickup_inven);
        setIntField(options, "delayFactor", 99);
        setIntField(options, "hitpointWarn", 9);

        options.initDefaults();

        assertFalse(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        assertFalse(options.has(PlayerOptionEnum.OP_cheat_live));
        assertTrue(options.has(PlayerOptionEnum.OP_pickup_inven));
        assertEquals(40, getIntField(options, "delayFactor"));
        assertEquals(3, options.hitPointWarn());
    }

    @Test
    void aCustomizedBirthFileOverridesTheBirthPageOnly() throws Exception {
        writeCustomizedFile(PlayerOptionTypes.BIRTH.getName(),
                "option:birth_randarts:yes",
                "option:birth_connect_stairs:no");
        PlayerOptions options = new PlayerOptions();

        options.initDefaults();

        assertTrue(options.has(PlayerOptionEnum.OP_birth_randarts));
        assertFalse(options.has(PlayerOptionEnum.OP_birth_connect_stairs));
        assertFalse(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        assertTrue(options.has(PlayerOptionEnum.OP_pickup_inven));
    }

    @Test
    void aCustomizedInterfaceFileOverridesTheInterfacePageOnly() throws Exception {
        writeCustomizedFile(PlayerOptionTypes.INTERFACE.getName(),
                "option:rogue_like_commands:yes",
                "option:pickup_inven:no");
        PlayerOptions options = new PlayerOptions();

        options.initDefaults();

        assertTrue(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        assertFalse(options.has(PlayerOptionEnum.OP_pickup_inven));
        assertFalse(options.has(PlayerOptionEnum.OP_birth_randarts));
        assertTrue(options.has(PlayerOptionEnum.OP_birth_connect_stairs));
    }

    /**
     * C restores customized defaults for the BIRTH and INTERFACE pages only, so a file for any
     * other page is never read.
     */
    @Test
    void aCustomizedCheatFileIsNeverRead() throws Exception {
        writeCustomizedFile(PlayerOptionTypes.CHEAT.getName(), "option:cheat_hear:yes");
        PlayerOptions options = new PlayerOptions();

        options.initDefaults();

        assertFalse(options.has(PlayerOptionEnum.OP_cheat_hear));
    }

    @Test
    void theCheatAndScorePagesStayOff() throws Exception {
        PlayerOptions options = new PlayerOptions();

        options.initDefaults();

        for (PlayerOptionEnum option : PlayerOptionEnum.values()) {
            PlayerOptionTypes type = option.getPlayerOptionType();
            if (type == PlayerOptionTypes.CHEAT || type == PlayerOptionTypes.SCORE) {
                assertFalse(options.has(option), option.name());
            }
        }
    }

    /**
     * An unreadable customized file makes C's function return {@code false}, but
     * {@code options_init_defaults} ignores the result and carries on to the numeric settings.
     */
    @Test
    void anUnreadableCustomizedFileDoesNotStopTheNumericDefaults() throws Exception {
        Files.createDirectory(Path.of(AngbandDirs.ANGBAND_DIRS.USER.getPath()
                + "customized_birth_options.txt"));
        PlayerOptions options = new PlayerOptions();

        options.initDefaults();

        assertEquals(40, getIntField(options, "delayFactor"));
        assertEquals(3, options.hitPointWarn());
    }

    /**
     * Swallows every event; these tests do not look at messages.
     */
    private static final class SilentBus implements EventsHandler {
        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
        }
    }
}
