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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.enums.DirectionEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.gameinput.DefaultGameInput;
import uk.co.jackoftradesltd.middle.gameinput.EffectChoice;
import uk.co.jackoftradesltd.middle.gameinput.GameInputHolder;
import uk.co.jackoftradesltd.middle.objects.enums.GetItemFlags;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Command}: the by-name argument store, and the "queued argument first, else
 * prompt" getters ({@code cmd_get_direction}, {@code cmd_get_target}, {@code cmd_get_quantity},
 * {@code cmd_get_string}, {@code cmd_get_item}, {@code cmd_get_effect_from_list}) of {@code cmd-core.c}.
 *
 * <p>Expected values come from reading the C: a stored value short-circuits the prompt, a stored
 * {@code DIR_NONE} does not, a prompt result of zero is an abort for a quantity, an accepted empty
 * string is not an abort for a string, and strings are cut to the 79 characters C's 80-byte
 * buffer holds. The UI is a scripted {@link DefaultGameInput} that counts how often it was asked.
 *
 * <p>Not covered: {@code cmd_get_spell} (needs a caster with a book list built by hand), the
 * shapechange mask in {@code getItem} (needs a hand-built {@code PlayerShape}), and the
 * {@code DIR_TARGET} stale-target branch of {@code getTarget}, because {@code GameState.targetOkay}
 * is still a stub that always answers true.
 *
 * <p>Class CommandTest coded on 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class CommandTest {

    private ScriptedInput ui;
    private Player player;
    private Player realPlayer;
    private CommandQueue realQueue;
    private Command cmd;

    @BeforeEach
    void setUp() {
        ui = new ScriptedInput();
        GameInputHolder.setInstance(ui);
        player = new Player();
        realPlayer = GameState.getPlayer();
        realQueue = GameState.getCommandQueue();
        GameState.setPlayer(player);
        GameState.setCommandQueue(new CommandQueue(player));
        cmd = new Command(CommandContext.CTX_GAME, CommandCode.CMD_NULL, 0, 0, new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        GameInputHolder.resetInstance();
        GameState.setPlayer(realPlayer);
        GameState.setCommandQueue(realQueue);
    }

    /**
     * A scripted UI that answers each request from a field and counts the requests.
     */
    private static final class ScriptedInput extends DefaultGameInput {
        Optional<DirectionEnum> repDir = Optional.empty();
        Optional<DirectionEnum> aimDir = Optional.empty();
        Optional<Integer> quantity = Optional.empty();
        Optional<String> string = Optional.empty();
        Optional<uk.co.jackoftradesltd.middle.objects.ItemObject> item = Optional.empty();
        EffectChoice effect = new EffectChoice.Aborted();

        int prompts = 0;
        Boolean allow5Seen;
        String initialSeen;
        Flag<GetItemFlags> modeSeen;
        int maxSeen;

        @Override
        public Optional<DirectionEnum> getRepDir(boolean allow5) {
            prompts++;
            allow5Seen = allow5;
            return repDir;
        }

        @Override
        public Optional<DirectionEnum> getAimDir() {
            prompts++;
            return aimDir;
        }

        @Override
        public Optional<Integer> getQuantity(String prompt, int max) {
            prompts++;
            maxSeen = max;
            return quantity;
        }

        @Override
        public Optional<String> getString(String prompt, String userString) {
            prompts++;
            initialSeen = userString;
            return string;
        }

        @Override
        public Optional<uk.co.jackoftradesltd.middle.objects.ItemObject> getItem(String prompt, String errorMessage,
                                                                                 CommandCode code, Predicate<uk.co.jackoftradesltd.middle.objects.ItemObject> filter,
                                                                                 Flag<GetItemFlags> mode) {
            prompts++;
            modeSeen = mode;
            return item;
        }

        @Override
        public EffectChoice getEffectFromList(String prompt, List<Effect> effects, int count,
                                              boolean allowRandom) {
            prompts++;
            return effect;
        }
    }

    /**
     * The by-name store behind {@code cmd_set_arg} / {@code cmd_get_arg}.
     */
    @Nested
    @DisplayName("argument store")
    class Store {

        @Test
        @DisplayName("a value set under a name reads back as that type")
        void roundTrip() {
            cmd.setArgChoice("c", 3);
            cmd.setArgNumber("n", 7);
            cmd.setArgDirection("d", DirectionEnum.DIR_N);
            cmd.setArgTarget("t", DirectionEnum.DIR_TARGET);
            cmd.setArgString("s", "hi");
            cmd.setArgPoint("p", Loc.zero.offset(4, 5));

            assertEquals(Optional.of(3), cmd.getArgChoice("c"));
            assertEquals(Optional.of(7), cmd.getArgNumber("n"));
            assertEquals(Optional.of(DirectionEnum.DIR_N), cmd.getArgDirection("d"));
            assertEquals(Optional.of(DirectionEnum.DIR_TARGET), cmd.getArgTarget("t"));
            assertEquals(Optional.of("hi"), cmd.getArgString("s"));
            assertEquals(4, cmd.getArgPoint("p").orElseThrow().getX());
        }

        @Test
        @DisplayName("an unset name is empty (CMD_ARG_NOT_PRESENT)")
        void notPresent() {
            assertTrue(cmd.getArgNumber("nope").isEmpty());
            assertTrue(cmd.getArgString("nope").isEmpty());
        }

        @Test
        @DisplayName("a name holding another type is empty (CMD_ARG_WRONG_TYPE)")
        void wrongType() {
            cmd.setArgNumber("x", 9);
            assertTrue(cmd.getArgChoice("x").isEmpty());
            assertTrue(cmd.getArgString("x").isEmpty());
            assertTrue(cmd.getArgDirection("x").isEmpty());
        }

        @Test
        @DisplayName("setting an existing name overwrites in place, even with another type")
        void overwrite() {
            cmd.setArgNumber("x", 9);
            cmd.setArgNumber("x", 10);
            assertEquals(1, cmd.getArgs().size());
            assertEquals(Optional.of(10), cmd.getArgNumber("x"));

            cmd.setArgChoice("x", 2);
            assertEquals(1, cmd.getArgs().size());
            assertEquals(CommandArgumentType.arg_CHOICE, cmd.getArgs().get(0).getType());
            assertTrue(cmd.getArgNumber("x").isEmpty());
        }

        @Test
        @DisplayName("clone is independent: overwriting the copy leaves the original alone")
        void cloneIsIndependent() {
            cmd.setArgString("s", "one");
            cmd.setNrepeats(5);
            Command copy = cmd.clone();

            copy.setArgString("s", "two");
            copy.repeated();

            assertEquals(Optional.of("one"), cmd.getArgString("s"));
            assertEquals(Optional.of("two"), copy.getArgString("s"));
            assertEquals(5, cmd.getNrepeats());
            assertEquals(4, copy.getNrepeats());
            assertEquals(cmd.getCode(), copy.getCode());
        }
    }

    /**
     * {@code cmd_get_direction}.
     */
    @Nested
    @DisplayName("getDirection")
    class Direction {

        @Test
        @DisplayName("a stored real direction is returned without prompting")
        void stored() {
            cmd.setArgDirection("d", DirectionEnum.DIR_E);
            assertEquals(Optional.of(DirectionEnum.DIR_E), cmd.getDirection("d", false));
            assertEquals(0, ui.prompts);
        }

        @Test
        @DisplayName("a stored DIR_NONE is treated as absent and prompts")
        void storedNoneRePrompts() {
            cmd.setArgDirection("d", DirectionEnum.DIR_NONE);
            ui.repDir = Optional.of(DirectionEnum.DIR_S);

            assertEquals(Optional.of(DirectionEnum.DIR_S), cmd.getDirection("d", true));
            assertEquals(1, ui.prompts);
            assertTrue(ui.allow5Seen, "allow5 is passed through");
            assertEquals(Optional.of(DirectionEnum.DIR_S), cmd.getArgDirection("d"), "answer is stored");
        }

        @Test
        @DisplayName("an abort returns empty and stores nothing")
        void abort() {
            assertTrue(cmd.getDirection("d", false).isEmpty());
            assertTrue(cmd.getArgDirection("d").isEmpty());
        }
    }

    /**
     * {@code cmd_get_target}.
     */
    @Nested
    @DisplayName("getTarget")
    class Target {

        @Test
        @DisplayName("a stored DIR_UNKNOWN re-prompts")
        void unknownRePrompts() {
            cmd.setArgTarget("t", DirectionEnum.DIR_UNKNOWN);
            ui.aimDir = Optional.of(DirectionEnum.DIR_NE);

            assertEquals(Optional.of(DirectionEnum.DIR_NE), cmd.getTarget("t"));
            assertEquals(1, ui.prompts);
            assertEquals(Optional.of(DirectionEnum.DIR_NE), cmd.getArgTarget("t"));
        }

        @Test
        @DisplayName("a stored real direction is trusted without prompting")
        void stored() {
            cmd.setArgTarget("t", DirectionEnum.DIR_W);
            assertEquals(Optional.of(DirectionEnum.DIR_W), cmd.getTarget("t"));
            assertEquals(0, ui.prompts);
        }

        @Test
        @DisplayName("an aborted aim returns empty")
        void abort() {
            assertTrue(cmd.getTarget("t").isEmpty());
            assertEquals(1, ui.prompts);
        }
    }

    /**
     * {@code cmd_get_quantity}.
     */
    @Nested
    @DisplayName("getQuantity")
    class Quantity {

        @Test
        @DisplayName("a stored number is trusted, even zero or negative")
        void storedNotChecked() {
            cmd.setArgNumber("q", 0);
            assertEquals(Optional.of(0), cmd.getQuantity("q", 10));
            cmd.setArgNumber("q", -4);
            assertEquals(Optional.of(-4), cmd.getQuantity("q", 10));
            assertEquals(0, ui.prompts);
        }

        @Test
        @DisplayName("a prompted positive amount is returned and stored")
        void prompted() {
            ui.quantity = Optional.of(3);
            assertEquals(Optional.of(3), cmd.getQuantity("q", 10));
            assertEquals(10, ui.maxSeen);
            assertEquals(Optional.of(3), cmd.getArgNumber("q"));
        }

        @Test
        @DisplayName("a prompted zero is an abort and is not stored")
        void promptedZero() {
            ui.quantity = Optional.of(0);
            assertTrue(cmd.getQuantity("q", 10).isEmpty());
            assertTrue(cmd.getArgNumber("q").isEmpty());
        }

        @Test
        @DisplayName("a UI abort is an abort")
        void uiAbort() {
            assertTrue(cmd.getQuantity("q", 10).isEmpty());
        }
    }

    /**
     * {@code cmd_get_string}.
     */
    @Nested
    @DisplayName("getString")
    class Strings {

        @Test
        @DisplayName("a stored string is returned without prompting")
        void stored() {
            cmd.setArgString("s", "kept");
            assertEquals(Optional.of("kept"), cmd.getString("s", "init", "title", "prompt"));
            assertEquals(0, ui.prompts);
        }

        @Test
        @DisplayName("an accepted empty string is a valid answer, not an abort")
        void emptyIsValid() {
            ui.string = Optional.of("");
            assertEquals(Optional.of(""), cmd.getString("s", null, "title", "prompt"));
            assertEquals(Optional.of(""), cmd.getArgString("s"));
        }

        @Test
        @DisplayName("an abort returns empty")
        void abort() {
            assertTrue(cmd.getString("s", null, "title", "prompt").isEmpty());
        }

        @Test
        @DisplayName("the initial text and the answer are both cut to 79 characters")
        void truncation() {
            ui.string = Optional.of("y".repeat(100));
            Optional<String> got = cmd.getString("s", "x".repeat(100), "title", "prompt");

            assertEquals(79, ui.initialSeen.length());
            assertEquals(79, got.orElseThrow().length());
            assertEquals(79, cmd.getArgString("s").orElseThrow().length());
        }

        @Test
        @DisplayName("a null initial value reaches the UI as an empty string")
        void nullInitial() {
            ui.string = Optional.of("a");
            cmd.getString("s", null, "title", "prompt");
            assertEquals("", ui.initialSeen);
        }
    }

    /**
     * {@code cmd_get_item}, the paths that do not need a shapechanged player.
     */
    @Nested
    @DisplayName("getItem")
    class Items {

        @Test
        @DisplayName("an abort returns empty and stores nothing")
        void abort() {
            Flag<GetItemFlags> mode = new Flag<>(GetItemFlags.class);
            assertTrue(cmd.getItem("i", "p", "r", null, mode).isEmpty());
            assertTrue(cmd.getArgItem("i").isEmpty());
        }

        @Test
        @DisplayName("an unshapechanged player's mode reaches the UI intact, and as a copy")
        void modePassedThrough() {
            Flag<GetItemFlags> mode = new Flag<>(GetItemFlags.class);
            mode.on(GetItemFlags.USE_INVEN);
            mode.on(GetItemFlags.USE_FLOOR);

            cmd.getItem("i", "p", "r", null, mode);

            assertTrue(ui.modeSeen.has(GetItemFlags.USE_INVEN));
            assertTrue(ui.modeSeen.has(GetItemFlags.USE_FLOOR));
            assertNotSame(mode, ui.modeSeen);
        }
    }

    /**
     * {@code cmd_get_effect_from_list}.
     */
    @Nested
    @DisplayName("getEffectFromList")
    class Effects {

        private final List<Effect> three = new ArrayList<>(java.util.Collections.nCopies(3, (Effect) null));

        @Test
        @DisplayName("a stored in-range index is used without prompting")
        void storedIndex() {
            cmd.setArgChoice("e", 2);
            assertEquals(new EffectChoice.Index(2), cmd.getEffectFromList("e", null, three, -1, false));
            assertEquals(0, ui.prompts);
        }

        @Test
        @DisplayName("a stored -2 is the random choice only when random is allowed")
        void storedRandom() {
            cmd.setArgChoice("e", -2);
            assertEquals(new EffectChoice.Random(), cmd.getEffectFromList("e", null, three, -1, true));
            assertEquals(0, ui.prompts);

            ui.effect = new EffectChoice.Index(1);
            assertEquals(new EffectChoice.Index(1), cmd.getEffectFromList("e", null, three, -1, false));
            assertEquals(1, ui.prompts, "-2 with random disallowed falls through to the prompt");
        }

        @Test
        @DisplayName("a stored index at or past count falls through; count -1 means the list size")
        void storedOutOfRange() {
            cmd.setArgChoice("e", 3);
            ui.effect = new EffectChoice.Index(0);
            assertEquals(new EffectChoice.Index(0), cmd.getEffectFromList("e", null, three, -1, false));
            assertEquals(1, ui.prompts);
        }

        @Test
        @DisplayName("an explicit count narrows the valid range below the list size")
        void explicitCount() {
            cmd.setArgChoice("e", 2);
            ui.effect = new EffectChoice.Index(1);
            assertEquals(new EffectChoice.Index(1), cmd.getEffectFromList("e", null, three, 2, false));
            assertEquals(1, ui.prompts);
        }

        @Test
        @DisplayName("a prompted answer is stored back under the name")
        void promptedStored() {
            ui.effect = new EffectChoice.Index(1);
            cmd.getEffectFromList("e", null, three, -1, false);
            assertEquals(Optional.of(1), cmd.getArgChoice("e"));
        }

        @Test
        @DisplayName("an invalid prompted answer is an abort and is not stored")
        void promptedInvalid() {
            ui.effect = new EffectChoice.Index(7);
            assertEquals(new EffectChoice.Aborted(), cmd.getEffectFromList("e", null, three, -1, false));
            assertTrue(cmd.getArgChoice("e").isEmpty());

            ui.effect = new EffectChoice.Random();
            assertEquals(new EffectChoice.Aborted(), cmd.getEffectFromList("e", null, three, -1, false),
                    "random answer when random is disallowed");
        }

        @Test
        @DisplayName("a UI abort is an abort")
        void uiAbort() {
            assertEquals(new EffectChoice.Aborted(), cmd.getEffectFromList("e", null, three, -1, true));
        }
    }
}
