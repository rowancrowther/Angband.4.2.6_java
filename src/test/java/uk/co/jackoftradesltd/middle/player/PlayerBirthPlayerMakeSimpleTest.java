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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandQueue;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#playerMakeSimple(Player, String, String, String)}, the port of C's
 * {@code player_make_simple} ({@code player-birth.c:523-577}).
 *
 * <p>The C function resolves each name to a menu-choice index by walking its {@code races}/
 * {@code classes} linked lists and then rewriting the found position with
 * {@code ir = nr - ir - 1} ({@code player-birth.c:543}), because those lists are built by
 * <em>prepending</em> each parsed entry — a forward walk from the head finds entries in the
 * reverse of the edit file's order. The port's lists are built by <em>appending</em>
 * ({@code PlayerRaceAssembler}, {@code PlayerClassAssembler}), so a forward scan already lands on
 * the file-order index and needs no rewrite. Every expected index below is the position of the
 * fixture entry in a small, explicitly file-ordered list — the number C's rewrite would also
 * produce for an equivalent linked list — so a wrong index shows up as a wrong number rather than
 * a coincidentally-passing test.
 *
 * <p>The queued commands themselves are inspected via a {@link RecordingCommandQueue} that
 * overrides only {@link CommandQueue#execute}, replacing dispatch with plain draining so the
 * commands and their arguments can be read back after the call — {@link PlayerBirth} 's own
 * {@code push}/{@code peek} calls run unmodified. This sidesteps a fact discovered while writing
 * this suite: every {@code CMD_BIRTH_*} handler in {@code CommandProcessor} is still {@code null}
 * ({@code CommandProcessor.java:63-75}), so a real {@link CommandQueue#execute} would drain the
 * queue into no-ops rather than run the birth machinery — there would be nothing to assert on.
 *
 * <p>The {@code player} parameter is passed as {@code null} throughout: the method's own Javadoc
 * records that it is never read in the body, only forwarded to command handlers that do not exist
 * yet.
 *
 * <p>Class PlayerBirthPlayerMakeSimpleTest coded on 260904, commented in full on 260904.
 *
 * @author Rowan Crowther
 */
class PlayerBirthPlayerMakeSimpleTest {

    /**
     * The race list's file order for this suite: index 0 first, index 2 last, matching C's ridx.
     */
    private static final List<String> RACE_NAMES = List.of("Human", "Half-Troll", "Dwarf");
    /**
     * The class list's file order for this suite.
     */
    private static final List<String> CLASS_NAMES = List.of("Warrior", "Mage", "Priest");
    private Object savedRaces;
    private Object savedClasses;
    private CommandQueue savedCommandQueue;
    private RecordingCommandQueue queue;

    private static Field accessibleField(String name) throws ReflectiveOperationException {
        Field field = PlayerRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static PlayerRace race(String name) {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        return new PlayerRace(name, 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, null,
                stats, skills, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, new HashMap<>());
    }

    private static PlayerClass playerClass(String name) {
        Map<Stats, Integer> stats = new HashMap<>();
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        return new PlayerClass(name, new ArrayList<>(), stats, skills, new HashMap<>(skills),
                0, 0, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                1, 1, 1, new ArrayList<>(), null);
    }

    @BeforeEach
    void seed() throws Exception {
        savedRaces = accessibleField("playerRaces").get(null);
        savedClasses = accessibleField("playerClasses").get(null);
        savedCommandQueue = GameState.getCommandQueue();

        PlayerRegistry.setPlayerRaces(RACE_NAMES.stream().map(PlayerBirthPlayerMakeSimpleTest::race).toList());
        PlayerRegistry.setPlayerClasses(
                CLASS_NAMES.stream().map(PlayerBirthPlayerMakeSimpleTest::playerClass).toList());

        queue = new RecordingCommandQueue();
        GameState.setCommandQueue(queue);
    }

    @AfterEach
    void restore() throws Exception {
        accessibleField("playerRaces").set(null, savedRaces);
        accessibleField("playerClasses").set(null, savedClasses);
        GameState.setCommandQueue(savedCommandQueue);
    }

    /**
     * Finds the one queued command with the given code, failing the test if none was queued.
     *
     * @param code the command code to find
     * @return the matching command
     */
    private Command executed(CommandCode code) {
        return queue.executedCommands.stream()
                .filter(c -> c.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new AssertionError(code + " was never queued"));
    }

    /**
     * A {@link CommandQueue} that keeps every pushed command instead of dispatching it, so a test
     * can read back what {@link PlayerBirth#playerMakeSimple} queued without a real handler
     * consuming it first.
     */
    private static final class RecordingCommandQueue extends CommandQueue {
        private final List<Command> executedCommands = new ArrayList<>();
        private CommandContext capturedContext;

        RecordingCommandQueue() {
            super(null);
        }

        @Override
        public void execute(CommandContext context) {
            capturedContext = context;
            Command next;
            while ((next = getNextCommand()) != null) {
                executedCommands.add(next);
            }
        }
    }

    /**
     * The command-queue sequence, defaulting and context - C's {@code cmdq_push} calls and their
     * order ({@code player-birth.c:564-574}), independent of which race/class/name is chosen.
     */
    @Nested
    @DisplayName("the command sequence")
    class CommandSequence {

        @Test
        @DisplayName("pushes the six birth commands in C's order")
        void pushesInOrder() {
            assertTrue(PlayerBirth.playerMakeSimple(null, null, null, null));

            List<CommandCode> codes = queue.executedCommands.stream().map(Command::getCode).toList();
            assertEquals(List.of(CommandCode.CMD_BIRTH_INIT, CommandCode.CMD_BIRTH_RESET,
                    CommandCode.CMD_CHOOSE_RACE, CommandCode.CMD_CHOOSE_CLASS,
                    CommandCode.CMD_NAME_CHOICE, CommandCode.CMD_ACCEPT_CHARACTER), codes);
        }

        @Test
        @DisplayName("executes under CTX_BIRTH, matching C's cmdq_execute(CTX_BIRTH)")
        void executesUnderBirthContext() {
            PlayerBirth.playerMakeSimple(null, null, null, null);

            assertEquals(CommandContext.CTX_BIRTH, queue.capturedContext);
        }

        @Test
        @DisplayName("a null race, class and name default to index 0 and \"Simple\"")
        void nullsDefaultToFirstEntryAndSimpleName() {
            assertTrue(PlayerBirth.playerMakeSimple(null, null, null, null));

            assertEquals(0, executed(CommandCode.CMD_CHOOSE_RACE).getArgChoice("choice").orElseThrow());
            assertEquals(0, executed(CommandCode.CMD_CHOOSE_CLASS).getArgChoice("choice").orElseThrow());
            assertEquals("Simple", executed(CommandCode.CMD_NAME_CHOICE).getArgString("name").orElseThrow());
        }

        @Test
        @DisplayName("a given name is used as-is instead of the \"Simple\" default")
        void givenNameIsUsed() {
            assertTrue(PlayerBirth.playerMakeSimple(null, null, null, "Grendel"));

            assertEquals("Grendel", executed(CommandCode.CMD_NAME_CHOICE).getArgString("name").orElseThrow());
        }
    }

    /**
     * Resolving {@code raceName} to a choice index - C's {@code ir} after its reverse-list
     * rewrite, which the port's forward scan already produces directly (see the class Javadoc).
     */
    @Nested
    @DisplayName("race resolution")
    class RaceResolution {

        @Test
        @DisplayName("a matched name resolves to its position in the edit file")
        void matchedNameResolvesToItsIndex() {
            assertTrue(PlayerBirth.playerMakeSimple(null, "Half-Troll", null, null));

            assertEquals(1, executed(CommandCode.CMD_CHOOSE_RACE).getArgChoice("choice").orElseThrow());
        }

        @Test
        @DisplayName("the last entry in the file resolves without an off-by-one")
        void lastEntryResolves() {
            assertTrue(PlayerBirth.playerMakeSimple(null, "Dwarf", null, null));

            assertEquals(2, executed(CommandCode.CMD_CHOOSE_RACE).getArgChoice("choice").orElseThrow());
        }

        @Test
        @DisplayName("an unmatched name is refused before anything is queued")
        void unmatchedNameRefusedBeforeQueueing() {
            assertFalse(PlayerBirth.playerMakeSimple(null, "Elf", null, null));

            assertTrue(queue.executedCommands.isEmpty(),
                    "C returns false before its first cmdq_push when nrace is not found");
        }
    }

    /**
     * Resolving {@code className} to a choice index - the port of C's equivalent {@code ic}
     * lookup, and the regression case for the {@code PlayerBirth.java:656} fix (the loop
     * previously compared against {@code playerName} instead of {@code className}).
     */
    @Nested
    @DisplayName("class resolution")
    class ClassResolution {

        @Test
        @DisplayName("a matched name resolves to its position in the edit file")
        void matchedNameResolvesToItsIndex() {
            assertTrue(PlayerBirth.playerMakeSimple(null, null, "Mage", "Tester"));

            assertEquals(1, executed(CommandCode.CMD_CHOOSE_CLASS).getArgChoice("choice").orElseThrow());
        }

        @Test
        @DisplayName("the class is matched against className, not the unrelated playerName")
        void matchesClassNameNotPlayerName() {
            assertTrue(PlayerBirth.playerMakeSimple(null, null, "Priest", "Priest is not my name"));

            assertEquals(2, executed(CommandCode.CMD_CHOOSE_CLASS).getArgChoice("choice").orElseThrow());
        }

        @Test
        @DisplayName("an unmatched name is refused before anything is queued")
        void unmatchedNameRefusedBeforeQueueing() {
            assertFalse(PlayerBirth.playerMakeSimple(null, null, "Ranger", null));

            assertTrue(queue.executedCommands.isEmpty(),
                    "C returns false before its first cmdq_push when nclass is not found");
        }

        @Test
        @DisplayName("a matched race does not rescue an unmatched class")
        void matchedRaceDoesNotRescueUnmatchedClass() {
            assertFalse(PlayerBirth.playerMakeSimple(null, "Human", "Ranger", null));

            assertTrue(queue.executedCommands.isEmpty());
        }
    }
}
