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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.combat.Target;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.gameinput.DefaultGameInput;
import uk.co.jackoftradesltd.middle.gameinput.GameInputHolder;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the two places {@link Command} had to be made to follow C's integer view of
 * directions and pointers: the value 5, and stored {@code null}s. Expected values come from
 * {@code cmd_get_direction}, {@code cmd_get_target}, {@code cmd_get_item}, {@code cmd_get_string},
 * {@code cmd_get_arg_item} and {@code cmd_get_arg_string} in cmd-core.c, from {@code DIR_NONE} and
 * {@code DIR_TARGET} both being 5 in cave.h, from {@code string_make(NULL)} returning
 * {@code NULL} in z-virt.c, and from cmd-wizard.c, which stores {@code NULL} under
 * {@code "original_item"} and reads it back as {@code CMD_OK && orig_obj}.
 *
 * <p>C's {@code cmd_get_direction} rejects a stored 5, so both {@code DIR_NONE} and
 * {@code DIR_TARGET} must send the player to the prompt; {@code cmd_get_target} runs
 * {@code target_okay()} on a stored 5, so both constants get the same validity check. C direction
 * and target are ints, so a {@code null} for either has no C meaning and is refused; an item or
 * string pointer may be {@code NULL}, so those are accepted and read back as absent.
 *
 * <p>Not covered: a stale monster target, which needs a level with a monster and is the business
 * of {@code TargetTest}; here a live target is a plain non-zero grid.
 *
 * <p>Class CommandNullAndFiveTest coded on 261012, commented in full on 261012.
 */
@ExtendWith(SeededPlayerRegistry.class)
class CommandNullAndFiveTest {

    private Player player;
    private Player realPlayer;
    private CommandQueue realQueue;
    private Command cmd;
    private int prompts;
    private Optional<DirectionEnum> repDir;
    private Optional<DirectionEnum> aimDir;
    private Optional<String> userString;
    private Optional<ItemObject> userItem;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        prompts = 0;
        repDir = Optional.of(DirectionEnum.DIR_N);
        aimDir = Optional.of(DirectionEnum.DIR_S);
        userString = Optional.of("typed");
        userItem = Optional.of(new ItemObject());
        GameInputHolder.setInstance(new DefaultGameInput() {
            @Override
            public Optional<DirectionEnum> getRepDir(boolean allow5) {
                prompts++;
                return repDir;
            }

            @Override
            public Optional<DirectionEnum> getAimDir() {
                prompts++;
                return aimDir;
            }

            @Override
            public Optional<String> getString(String prompt, String userString) {
                prompts++;
                return CommandNullAndFiveTest.this.userString;
            }

            @Override
            public Optional<ItemObject> getItem(String prompt, String errorMessage, CommandCode code,
                                                java.util.function.Predicate<ItemObject> filter,
                                                uk.co.jackoftradesltd.channel.utils.Flag<uk.co.jackoftradesltd.middle.objects.enums.GetItemFlags> mode) {
                prompts++;
                return userItem;
            }
        });
        player = new Player();
        realPlayer = GameState.getPlayer();
        realQueue = GameState.getCommandQueue();
        GameState.setPlayer(player);
        GameState.setCommandQueue(new CommandQueue(player));
        cmd = new Command(CommandContext.CTX_GAME, CommandCode.CMD_NULL, 0, 0, new ArrayList<>());
        Target.setTargetMonster(null);
    }

    @AfterEach
    void tearDown() {
        Target.setTargetMonster(null);
        GameInputHolder.resetInstance();
        GameState.setPlayer(realPlayer);
        GameState.setCommandQueue(realQueue);
    }

    /**
     * Makes {@code target_okay()} true the way C's non-monster branch does: a target is set and its
     * grid has non-zero row and column.
     */
    private static void liveGridTarget() throws ReflectiveOperationException {
        Field set = Target.class.getDeclaredField("targetSet");
        set.setAccessible(true);
        set.setBoolean(null, true);
        Target.setGrid(Loc.row(3).col(4));
        assertTrue(Target.targetOkay(), "fixture: the grid target must be okay");
    }

    @Nested
    @DisplayName("cmd_get_direction: a stored 5 is rejected")
    class Direction {

        @Test
        @DisplayName("a stored DIR_NONE goes to the prompt")
        void storedNone() {
            cmd.setArgDirection("d", DirectionEnum.DIR_NONE);

            assertEquals(Optional.of(DirectionEnum.DIR_N), cmd.getDirection("d", false));
            assertEquals(1, prompts);
        }

        @Test
        @DisplayName("a stored DIR_TARGET goes to the prompt, as it is the same 5 in C")
        void storedTarget() {
            cmd.setArgDirection("d", DirectionEnum.DIR_TARGET);

            assertEquals(Optional.of(DirectionEnum.DIR_N), cmd.getDirection("d", false));
            assertEquals(1, prompts);
        }

        @Test
        @DisplayName("the prompted direction replaces the stored 5")
        void promptedReplacesStored() {
            cmd.setArgDirection("d", DirectionEnum.DIR_TARGET);
            cmd.getDirection("d", false);

            assertEquals(Optional.of(DirectionEnum.DIR_N), cmd.getArgDirection("d"));
        }

        @Test
        @DisplayName("a stored DIR_UNKNOWN (0) is accepted without a prompt, as 0 != 5")
        void storedUnknown() {
            cmd.setArgDirection("d", DirectionEnum.DIR_UNKNOWN);

            assertEquals(Optional.of(DirectionEnum.DIR_UNKNOWN), cmd.getDirection("d", false));
            assertEquals(0, prompts);
        }

        @Test
        @DisplayName("every real direction is accepted without a prompt")
        void storedRealDirections() {
            for (DirectionEnum dir : DirectionEnum.surroundingDirections()) {
                cmd.setArgDirection("d", dir);

                assertEquals(Optional.of(dir), cmd.getDirection("d", false), dir.name());
            }
            assertEquals(0, prompts);
        }
    }

    @Nested
    @DisplayName("cmd_get_target: a stored 5 gets the target_okay() check")
    class TargetCode {

        @Test
        @DisplayName("a stored DIR_UNKNOWN goes to the aim prompt")
        void storedUnknown() {
            cmd.setArgTarget("t", DirectionEnum.DIR_UNKNOWN);

            assertEquals(Optional.of(DirectionEnum.DIR_S), cmd.getTarget("t"));
            assertEquals(1, prompts);
        }

        @Test
        @DisplayName("with no live target, a stored DIR_TARGET goes to the aim prompt")
        void staleTargetCode() {
            cmd.setArgTarget("t", DirectionEnum.DIR_TARGET);

            assertEquals(Optional.of(DirectionEnum.DIR_S), cmd.getTarget("t"));
            assertEquals(1, prompts);
        }

        @Test
        @DisplayName("with no live target, a stored DIR_NONE goes to the aim prompt, as it is the same 5")
        void staleNoneCode() {
            cmd.setArgTarget("t", DirectionEnum.DIR_NONE);

            assertEquals(Optional.of(DirectionEnum.DIR_S), cmd.getTarget("t"));
            assertEquals(1, prompts);
        }

        @Test
        @DisplayName("with a live target, a stored DIR_TARGET is kept without a prompt")
        void liveTargetCode() throws ReflectiveOperationException {
            liveGridTarget();
            cmd.setArgTarget("t", DirectionEnum.DIR_TARGET);

            assertEquals(Optional.of(DirectionEnum.DIR_TARGET), cmd.getTarget("t"));
            assertEquals(0, prompts);
        }

        @Test
        @DisplayName("with a live target, a stored DIR_NONE is kept without a prompt")
        void liveNoneCode() throws ReflectiveOperationException {
            liveGridTarget();
            cmd.setArgTarget("t", DirectionEnum.DIR_NONE);

            assertEquals(Optional.of(DirectionEnum.DIR_NONE), cmd.getTarget("t"));
            assertEquals(0, prompts);
        }

        @Test
        @DisplayName("a stored real direction needs no live target and no prompt")
        void storedRealDirection() {
            cmd.setArgTarget("t", DirectionEnum.DIR_W);

            assertEquals(Optional.of(DirectionEnum.DIR_W), cmd.getTarget("t"));
            assertEquals(0, prompts);
        }

        @Test
        @DisplayName("an aborted aim returns empty and stores nothing")
        void abortedAim() {
            aimDir = Optional.empty();

            assertTrue(cmd.getTarget("t").isEmpty());
            assertTrue(cmd.getArgTarget("t").isEmpty());
        }
    }

    @Nested
    @DisplayName("direction and target: null has no C meaning and is refused")
    class RefusedNull {

        @Test
        @DisplayName("setArgDirection(null) throws and stores nothing")
        void directionSetter() {
            assertThrows(IllegalArgumentException.class, () -> cmd.setArgDirection("d", null));

            assertTrue(cmd.getArgs().isEmpty());
            assertTrue(cmd.getArgDirection("d").isEmpty());
        }

        @Test
        @DisplayName("setArgTarget(null) throws and stores nothing")
        void targetSetter() {
            assertThrows(IllegalArgumentException.class, () -> cmd.setArgTarget("t", null));

            assertTrue(cmd.getArgs().isEmpty());
            assertTrue(cmd.getArgTarget("t").isEmpty());
        }

        @Test
        @DisplayName("a refused null leaves an earlier direction under the same name untouched")
        void directionOverwriteKeepsOld() {
            cmd.setArgDirection("d", DirectionEnum.DIR_E);

            assertThrows(IllegalArgumentException.class, () -> cmd.setArgDirection("d", null));

            assertEquals(Optional.of(DirectionEnum.DIR_E), cmd.getArgDirection("d"));
        }

        @Test
        @DisplayName("a refused null leaves an earlier target under the same name untouched")
        void targetOverwriteKeepsOld() {
            cmd.setArgTarget("t", DirectionEnum.DIR_E);

            assertThrows(IllegalArgumentException.class, () -> cmd.setArgTarget("t", null));

            assertEquals(Optional.of(DirectionEnum.DIR_E), cmd.getArgTarget("t"));
        }

        @Test
        @DisplayName("every constant, DIR_UNKNOWN and the two 5s included, is stored and read back")
        void everyConstantAccepted() {
            for (DirectionEnum dir : DirectionEnum.values()) {
                cmd.setArgDirection("d", dir);
                cmd.setArgTarget("t", dir);

                assertEquals(Optional.of(dir), cmd.getArgDirection("d"), dir.name());
                assertEquals(Optional.of(dir), cmd.getArgTarget("t"), dir.name());
            }
        }
    }

    @Nested
    @DisplayName("item and string: NULL is legal in C and reads back as absent")
    class AcceptedNull {

        @Test
        @DisplayName("a stored null item reads back empty, as cmd-wizard.c's original_item check treats NULL")
        void nullItemEmpty() {
            cmd.setArgItem("original_item", null);

            assertTrue(cmd.getArgItem("original_item").isEmpty());
        }

        @Test
        @DisplayName("resetting an item to null, as cmd-wizard.c does, replaces the earlier item")
        void resetItemToNull() {
            ItemObject item = new ItemObject();
            cmd.setArgItem("original_item", item);
            assertSame(item, cmd.getArgItem("original_item").orElseThrow());

            cmd.setArgItem("original_item", null);

            assertTrue(cmd.getArgItem("original_item").isEmpty());
            assertEquals(1, cmd.getArgs().size());
        }

        @Test
        @DisplayName("a stored null item sent to getItem is treated as absent and prompts")
        void nullItemPrompts() {
            cmd.setArgItem("item", null);

            Optional<ItemObject> got = cmd.getItem("item", "Which?", "None.", null,
                    new uk.co.jackoftradesltd.channel.utils.Flag<>(uk.co.jackoftradesltd.middle.objects.enums.GetItemFlags.class));

            assertSame(userItem.orElseThrow(), got.orElseThrow());
            assertEquals(1, prompts);
        }

        @Test
        @DisplayName("a stored null string reads back empty")
        void nullStringEmpty() {
            cmd.setArgString("s", null);

            assertTrue(cmd.getArgString("s").isEmpty());
        }

        @Test
        @DisplayName("an empty string is a present value, not absent")
        void emptyStringPresent() {
            cmd.setArgString("s", "");

            assertEquals(Optional.of(""), cmd.getArgString("s"));
        }

        @Test
        @DisplayName("a null string replaces an earlier string under the same name")
        void nullOverwritesString() {
            cmd.setArgString("s", "old");
            cmd.setArgString("s", null);

            assertTrue(cmd.getArgString("s").isEmpty());
            assertEquals(1, cmd.getArgs().size());
        }

        @Test
        @DisplayName("a stored null string sent to getString is treated as absent and prompts")
        void nullStringPrompts() {
            cmd.setArgString("s", null);

            assertEquals(Optional.of("typed"), cmd.getString("s", "", "Title", "Prompt"));
            assertEquals(1, prompts);
        }

        @Test
        @DisplayName("a null item and a null string survive clone() as null, and still read as absent")
        void nullsSurviveClone() {
            cmd.setArgItem("i", null);
            cmd.setArgString("s", null);

            Command copy = cmd.clone();

            assertTrue(copy.getArgItem("i").isEmpty());
            assertTrue(copy.getArgString("s").isEmpty());
            assertEquals(2, copy.getArgs().size());
        }

        @Test
        @DisplayName("a null item or string is still the wrong type for any other reader")
        void nullKeepsItsType() {
            cmd.setArgItem("i", null);
            cmd.setArgString("s", null);

            assertTrue(cmd.getArgNumber("i").isEmpty());
            assertTrue(cmd.getArgString("i").isEmpty());
            assertTrue(cmd.getArgItem("s").isEmpty());
            assertTrue(cmd.getArgChoice("s").isEmpty());
        }
    }
}
