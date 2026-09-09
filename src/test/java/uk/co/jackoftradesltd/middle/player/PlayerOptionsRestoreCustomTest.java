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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerOptions.restoreCustom(PlayerOptionTypes)}, the port of C's
 * {@code options_restore_custom} ({@code option.c:225-333}).
 *
 * <p>Two bugs were fixed in the method ahead of these tests, and both get a dedicated case:
 * {@link WhenTheCustomizedFileIsMissing#reportsSuccessWhenNoFileExists} pins the return value C
 * gives for the missing-file case ({@code true}, not {@code false}), and
 * {@link WhenTheFileIsWellFormed#aNoLineTurnsAnOptionOff} pins the "no" branch, which previously
 * called {@code options.on} instead of {@code options.off} and so could never switch anything off.
 *
 * <p>The method is private, so every test reaches it through reflection, following the pattern in
 * {@code PlayerBirthIntToRomanTest}. The customized-options file it reads is redirected to a JUnit
 * {@code @TempDir} for the run, since {@code AngbandDirs.ANGBAND_DIRS.USER} is a process-wide,
 * mutable path that every test must put back afterwards.
 *
 * <p>Class PlayerOptionsRestoreCustomTest coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
class PlayerOptionsRestoreCustomTest {

    private String originalUserDir;

    private static boolean restoreCustom(PlayerOptions options, PlayerOptionTypes type) throws Exception {
        Method method = PlayerOptions.class.getDeclaredMethod("restoreCustom", PlayerOptionTypes.class);
        method.setAccessible(true);
        return (boolean) method.invoke(options, type);
    }

    @SuppressWarnings("unchecked")
    private static void switchOn(PlayerOptions options, PlayerOptionEnum option) throws Exception {
        Field field = PlayerOptions.class.getDeclaredField("options");
        field.setAccessible(true);
        ((Flag<PlayerOptionEnum>) field.get(options)).on(option);
    }

    /**
     * Writes {@code customized_<page>_options.txt} into the redirected user directory, in the
     * format C's {@code options_save_custom} produces ({@code option.c:200-203}).
     */
    private static void writeCustomizedFile(PlayerOptionTypes type, String... lines) throws IOException {
        String filename = AngbandDirs.ANGBAND_DIRS.USER.getPath()
                + "customized_" + type.getName() + "_options.txt";
        Files.write(Path.of(filename), String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void redirectUserDir(@TempDir Path tempDir) {
        originalUserDir = AngbandDirs.ANGBAND_DIRS.USER.getPath();
        AngbandDirs.ANGBAND_DIRS.USER.setPath(tempDir.toString() + java.io.File.separator);
    }

    @AfterEach
    void restoreUserDir() {
        AngbandDirs.ANGBAND_DIRS.USER.setPath(originalUserDir);
    }

    @Nested
    class WhenTheCustomizedFileIsMissing {

        /**
         * C's own doc comment for {@code options_restore_custom} says success "includes the case
         * where no customized defaults are available"; the pre-fix port returned {@code false}
         * here instead.
         */
        @Test
        void reportsSuccessWhenNoFileExists() throws Exception {
            assertTrue(restoreCustom(new PlayerOptions(), PlayerOptionTypes.INTERFACE));
        }

        @Test
        void fallsBackToTheMaintainerDefaults() throws Exception {
            PlayerOptions options = new PlayerOptions();

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_pickup_inven), "normally-on option");
            assertFalse(options.has(PlayerOptionEnum.OP_rogue_like_commands), "normally-off option");
        }

        @Test
        void leavesOtherPagesUntouched() throws Exception {
            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_birth_randarts);

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_birth_randarts),
                    "restoring the interface page must not touch birth options");
        }
    }

    @Nested
    class WhenTheFileIsWellFormed {

        @Test
        void aYesLineTurnsAnOptionOn() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, "option:rogue_like_commands:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertTrue(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        }

        /**
         * Pins the fixed bug: {@code OP_pickup_inven} is normally on, and a "no" line for it must
         * switch it off.
         */
        @Test
        void aNoLineTurnsAnOptionOff() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, "option:pickup_inven:no");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertFalse(options.has(PlayerOptionEnum.OP_pickup_inven));
        }

        @Test
        void anOptionNotMentionedInTheFileIsUnaffected() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, "option:rogue_like_commands:yes");
            PlayerOptions options = new PlayerOptions();

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(options.has(PlayerOptionEnum.OP_show_target));
        }

        @Test
        void blankLinesAndCommentsAreSkippedWithoutError() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "# a comment",
                    "",
                    "option:rogue_like_commands:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertTrue(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        }

        @Test
        void yesAndNoAreCaseInsensitive() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, "option:rogue_like_commands:YES");
            PlayerOptions options = new PlayerOptions();

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        }
    }

    @Nested
    class WhenTheFileHasBadLines {

        @Test
        void aLineWithoutTheOptionTagFailsTheLoadButKeepsParsing() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "not-an-option-line",
                    "option:rogue_like_commands:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(result);
            assertTrue(options.has(PlayerOptionEnum.OP_rogue_like_commands));
        }

        @Test
        void aLineWithTheWrongNumberOfColonsFailsTheLoadButKeepsParsing() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "option:rogue_like_commands:yes:extra",
                    "option:use_sound:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(result);
            assertFalse(options.has(PlayerOptionEnum.OP_rogue_like_commands),
                    "the malformed line's option must not be touched");
            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
        }

        @Test
        void aValueThatIsNotYesOrNoFailsTheLoadButKeepsParsing() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "option:rogue_like_commands:maybe",
                    "option:use_sound:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(result);
            assertFalse(options.has(PlayerOptionEnum.OP_rogue_like_commands));
            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
        }

        @Test
        void anUnknownOptionNameFailsTheLoadButKeepsParsing() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "option:not_a_real_option:yes",
                    "option:use_sound:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(result);
            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
        }

        @Test
        void anUnreadableFileFailsTheLoad() throws Exception {
            String filename = AngbandDirs.ANGBAND_DIRS.USER.getPath()
                    + "customized_" + PlayerOptionTypes.INTERFACE.getName() + "_options.txt";
            Files.createDirectory(Path.of(filename));
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(result, "a path that exists but cannot be opened as a file must fail the load");
        }
    }
}
