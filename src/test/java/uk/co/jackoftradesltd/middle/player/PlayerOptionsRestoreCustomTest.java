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
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionTypes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@code PlayerOptions.restoreCustom(Flag, PlayerOptionTypes)}, the port of C's
 * {@code options_restore_custom} ({@code option.c}).
 *
 * <p>Expected values come from reading the C function, not from the Java:
 * <ul>
 *   <li>C returns {@code true} when the customized file is missing (after restoring the
 *       maintainer defaults silently), {@code true} after reading the whole file even if some
 *       lines were bad, and {@code false} only when the file cannot be opened or closed.</li>
 *   <li>Every bad line is reported with {@code msg()} and skipped; parsing carries on with the
 *       next line. A bad line never changes an option.</li>
 *   <li>An option name matches only whole, case-sensitively, and only if the option is on the
 *       page being restored. {@code yes} and {@code no} are case-sensitive and may be followed
 *       only by spaces or tabs. Leading spaces and tabs before {@code option:} are allowed, and
 *       so are indented {@code #} comments.</li>
 * </ul>
 *
 * <p>The method is private, so every test reaches it through reflection, following the pattern in
 * {@code PlayerBirthIntToRomanTest}. The customized-options file it reads is redirected to a JUnit
 * {@code @TempDir} for the run, since {@code AngbandDirs.ANGBAND_DIRS.USER} is a process-wide,
 * mutable path that every test must put back afterwards. The event bus is swapped for a capturing
 * double so that the player-visible messages ({@code msg()} in C) can be counted.
 *
 * <p>Class PlayerOptionsRestoreCustomTest coded on 260907, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
class PlayerOptionsRestoreCustomTest {

    private String originalUserDir;
    private EventsHandler realBus;
    private CapturingBus bus;

    private static boolean restoreCustom(PlayerOptions options, PlayerOptionTypes type) throws Exception {
        Method method = PlayerOptions.class.getDeclaredMethod("restoreCustom", Flag.class, PlayerOptionTypes.class);
        method.setAccessible(true);
        try {
            return (boolean) method.invoke(options, flagOf(options), type);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    @SuppressWarnings("unchecked")
    private static Flag<PlayerOptionEnum> flagOf(PlayerOptions options) throws Exception {
        Field field = PlayerOptions.class.getDeclaredField("options");
        field.setAccessible(true);
        return (Flag<PlayerOptionEnum>) field.get(options);
    }

    private static void switchOn(PlayerOptions options, PlayerOptionEnum option) throws Exception {
        flagOf(options).on(option);
    }

    private static Path customizedFile(PlayerOptionTypes type) {
        return Path.of(AngbandDirs.ANGBAND_DIRS.USER.getPath()
                + "customized_" + type.getName() + "_options.txt");
    }

    /**
     * Writes {@code customized_<page>_options.txt} into the redirected user directory, in the
     * format C's {@code options_save_custom} produces ({@code option:<name>:yes|no}).
     */
    private static void writeCustomizedFile(PlayerOptionTypes type, String... lines) throws IOException {
        Files.write(customizedFile(type), String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void redirectUserDirAndBus(@TempDir Path tempDir) {
        originalUserDir = AngbandDirs.ANGBAND_DIRS.USER.getPath();
        AngbandDirs.ANGBAND_DIRS.USER.setPath(tempDir.toString() + java.io.File.separator);

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void restoreUserDirAndBus() {
        AngbandDirs.ANGBAND_DIRS.USER.setPath(originalUserDir);
        GameEngine.setEventsBusHandler(realBus);
    }

    @Nested
    class WhenTheCustomizedFileIsMissing {

        /**
         * C's own doc comment for {@code options_restore_custom} says success "includes the case
         * where no customized defaults are available".
         */
        @Test
        void reportsSuccessWhenNoFileExists() throws Exception {
            assertTrue(restoreCustom(new PlayerOptions(), PlayerOptionTypes.INTERFACE));
        }

        @Test
        void fallsBackToTheMaintainerDefaults() throws Exception {
            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_rogue_like_commands);

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_pickup_inven), "normally-on option");
            assertFalse(options.has(PlayerOptionEnum.OP_rogue_like_commands),
                    "normally-off option is switched back off");
        }

        @Test
        void leavesOtherPagesUntouched() throws Exception {
            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_birth_randarts);

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_birth_randarts),
                    "restoring the interface page must not touch birth options");
        }

        /**
         * C's missing-file branch calls {@code options_restore_maintainer} and returns without a
         * {@code msg()}; {@code options_init_defaults} runs this twice at every new character.
         */
        @Test
        void saysNothingToThePlayer() throws Exception {
            restoreCustom(new PlayerOptions(), PlayerOptionTypes.INTERFACE);

            assertEquals(0, bus.messages.size());
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
            assertEquals(0, bus.messages.size());
        }

        /**
         * {@code OP_pickup_inven} is normally on, and a "no" line for it must switch it off.
         */
        @Test
        void aNoLineTurnsAnOptionOff() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, "option:pickup_inven:no");
            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_pickup_inven);

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertFalse(options.has(PlayerOptionEnum.OP_pickup_inven));
        }

        @Test
        void aBirthLineWorksOnTheBirthPage() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.BIRTH,
                    "option:birth_randarts:yes",
                    "option:birth_connect_stairs:no");
            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_birth_connect_stairs);

            boolean result = restoreCustom(options, PlayerOptionTypes.BIRTH);

            assertTrue(result);
            assertTrue(options.has(PlayerOptionEnum.OP_birth_randarts));
            assertFalse(options.has(PlayerOptionEnum.OP_birth_connect_stairs));
        }

        /**
         * C applies the file over whatever the options held; it does not reset the page first
         * (that is only the missing-file branch), so an unmentioned option keeps its value.
         */
        @Test
        void anOptionNotMentionedInTheFileKeepsItsCurrentValue() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, "option:rogue_like_commands:yes");
            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_show_target);

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_show_target));
            assertFalse(options.has(PlayerOptionEnum.OP_use_sound));
        }

        @Test
        void aLaterLineOverridesAnEarlierOneForTheSameOption() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "option:use_sound:yes",
                    "option:use_sound:no");
            PlayerOptions options = new PlayerOptions();

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(options.has(PlayerOptionEnum.OP_use_sound));
        }

        @Test
        void blankLinesAndCommentsAreSkippedWithoutAMessage() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "# a comment",
                    "",
                    "   ",
                    "option:rogue_like_commands:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertTrue(options.has(PlayerOptionEnum.OP_rogue_like_commands));
            assertEquals(0, bus.messages.size());
        }

        /**
         * The exact header and description comments C's {@code options_save_custom} writes.
         */
        @Test
        void aFileInTheShapeCWritesItIsReadWithoutAMessage() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "# These are customized defaults for the interface options.",
                    "# All lines begin with \"option:\" followed by the internal option name.",
                    "# After the name is a colon followed by yes or no for the option's state.",
                    "# Use sound",
                    "option:use_sound:yes",
                    "# Always pickup items matching inventory",
                    "option:pickup_inven:no");
            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_pickup_inven);

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
            assertFalse(options.has(PlayerOptionEnum.OP_pickup_inven));
            assertEquals(0, bus.messages.size());
        }
    }

    /**
     * C's {@code contains_only_spaces} accepts spaces and tabs, so these are all read as normal
     * option lines, with no message.
     */
    @Nested
    class WhenTheFileHasStrayWhitespace {

        @Test
        void leadingSpacesBeforeTheTagAreAllowed() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, "   option:use_sound:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
            assertEquals(0, bus.messages.size());
        }

        @Test
        void aLeadingTabIsAllowed() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, "\toption:use_sound:yes");
            PlayerOptions options = new PlayerOptions();

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
            assertEquals(0, bus.messages.size());
        }

        @Test
        void trailingSpacesAfterTheValueAreAllowed() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "option:use_sound:yes   ",
                    "option:pickup_inven:no\t");
            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_pickup_inven);

            restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
            assertFalse(options.has(PlayerOptionEnum.OP_pickup_inven));
            assertEquals(0, bus.messages.size());
        }

        @Test
        void anIndentedCommentIsSkippedSilently() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "    # an indented comment",
                    "option:use_sound:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
            assertEquals(0, bus.messages.size());
        }
    }

    /**
     * Each of these is a line C reports with {@code msg()} and skips. The return value stays
     * {@code true}, the options on the line are left alone, and the next good line is applied.
     */
    @Nested
    class WhenTheFileHasBadLines {

        private void assertSkippedWithOneMessage(String badLine, PlayerOptionEnum untouched)
                throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE, badLine, "option:use_sound:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result, "C returns true after bad lines");
            assertFalse(options.has(untouched), "'" + badLine + "' must not change " + untouched);
            assertTrue(options.has(PlayerOptionEnum.OP_use_sound), "the next good line still applies");
            assertEquals(1, bus.messages.size(), "one msg() for '" + badLine + "'");
        }

        @Test
        void aLineWithoutTheOptionTagIsSkipped() throws Exception {
            assertSkippedWithOneMessage("not-an-option-line", PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void aLineWithOnlyTheTagAndNoNameIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:", PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void aTextLineBeforeACommentIsSkipped() throws Exception {
            assertSkippedWithOneMessage("junk # option:rogue_like_commands:yes",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void anExtraColonAfterTheValueIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:rogue_like_commands:yes:extra",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        /**
         * After "yes", C's {@code contains_only_spaces} sees ":" and rejects the value.
         */
        @Test
        void aTrailingColonIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:rogue_like_commands:yes:",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void aValueThatIsNotYesOrNoIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:rogue_like_commands:maybe",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void anEmptyValueIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:rogue_like_commands:",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        /**
         * C compares with {@code strncmp("yes", ...)}, which is case-sensitive.
         */
        @Test
        void anUpperCaseValueIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:rogue_like_commands:YES",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void aValueWithTextAfterItIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:rogue_like_commands:yes # note",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void aSpaceBeforeTheValueIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:rogue_like_commands: yes",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void anUnknownOptionNameIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:not_a_real_option:yes",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        /**
         * C needs the whole name followed by ":", so a suffix of a real name is not a match.
         */
        @Test
        void aPartOfAnOptionNameIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:old_target:yes", PlayerOptionEnum.OP_use_old_target);
        }

        @Test
        void aNameWithAnExtraPrefixIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:OP_rogue_like_commands:yes",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        @Test
        void anEmptyNameIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option::yes", PlayerOptionEnum.OP_rogue_like_commands);
        }

        /**
         * C compares names with {@code strncmp}, which is case-sensitive.
         */
        @Test
        void anUpperCaseNameIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:ROGUE_LIKE_COMMANDS:yes",
                    PlayerOptionEnum.OP_rogue_like_commands);
        }

        /**
         * C only searches options whose type is the page being restored.
         */
        @Test
        void aBirthOptionInTheInterfaceFileIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:birth_randarts:yes", PlayerOptionEnum.OP_birth_randarts);
        }

        @Test
        void anInterfaceOptionInTheBirthFileIsSkipped() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.BIRTH,
                    "option:rogue_like_commands:yes",
                    "option:birth_randarts:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.BIRTH);

            assertTrue(result);
            assertFalse(options.has(PlayerOptionEnum.OP_rogue_like_commands));
            assertTrue(options.has(PlayerOptionEnum.OP_birth_randarts));
            assertEquals(1, bus.messages.size());
        }

        @Test
        void aCheatOptionInTheInterfaceFileIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:cheat_hear:yes", PlayerOptionEnum.OP_cheat_hear);
        }

        /**
         * The index-0 placeholder is type SPECIAL in {@code list-options.h}, so no page matches it.
         */
        @Test
        void thePlaceholderOptionNoneIsSkipped() throws Exception {
            assertSkippedWithOneMessage("option:none:yes", PlayerOptionEnum.OP_none);
        }

        @Test
        void everyBadLineGetsItsOwnMessage() throws Exception {
            writeCustomizedFile(PlayerOptionTypes.INTERFACE,
                    "first-bad-line",
                    "option:use_sound:yes",
                    "option:nothing:yes",
                    "option:show_damage:perhaps",
                    "option:use_old_target:yes");
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertTrue(result);
            assertEquals(3, bus.messages.size());
            assertTrue(options.has(PlayerOptionEnum.OP_use_sound));
            assertFalse(options.has(PlayerOptionEnum.OP_show_damage));
            assertTrue(options.has(PlayerOptionEnum.OP_use_old_target),
                    "a line after several bad ones is still read");
        }
    }

    @Nested
    class WhenTheFileCannotBeRead {

        /**
         * C returns {@code false} when {@code file_open} fails; a directory where the file should
         * be makes the open fail in Java too.
         */
        @Test
        void anUnreadableFileFailsTheLoad() throws Exception {
            Files.createDirectory(customizedFile(PlayerOptionTypes.INTERFACE));
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(result, "a path that exists but cannot be opened as a file must fail the load");
        }

        /**
         * A file that is not valid UTF-8 makes the Java reader throw part way through, which the
         * method reports as a failed read.
         */
        @Test
        void aFileThatIsNotValidUtf8FailsTheLoad() throws Exception {
            Files.write(customizedFile(PlayerOptionTypes.INTERFACE),
                    new byte[]{(byte) 0xFF, (byte) 0xFE, (byte) 0xFD, '\n'});
            PlayerOptions options = new PlayerOptions();

            boolean result = restoreCustom(options, PlayerOptionTypes.INTERFACE);

            assertFalse(result);
        }
    }

    /**
     * Records the text of every message signalled, which is what C's {@code msg()} would show.
     */
    private static final class CapturingBus implements EventsHandler {
        final List<String> messages = new ArrayList<>();

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
            if (data instanceof EventDataMessage message) {
                messages.add(message.message());
            }
        }
    }
}
