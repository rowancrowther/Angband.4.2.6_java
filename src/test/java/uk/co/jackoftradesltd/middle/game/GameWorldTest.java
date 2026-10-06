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

package uk.co.jackoftradesltd.middle.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.backend.parser.GameConstantsReader;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandQueue;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.StoreData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.CurseData;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerNotice;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.PlayerOptions;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.PlayerUtils;
import uk.co.jackoftradesltd.middle.player.TimedGrade;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.CurseFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the clock and per-turn machinery in {@link GameWorld}, the port of C's {@code game-world.c}.
 *
 * <p>Every expected figure is derived from the C source or the shipped data, not read back off the
 * port: the {@code extract_energy} table is copied from {@code game-world.c} into
 * {@link #C_EXTRACT_ENERGY}, and the clock figures come from {@code constants.txt}, which carries
 * {@code world:day-length:10000}, {@code world:move-energy:100} and {@code store:turns:1000}. The
 * constants are parsed from the shipped file rather than hand-seeded, so the numbers are the ones
 * the game runs on; a few tests then swap in a different {@link WorldData} to reach the divisions
 * the shipped values hide (an integer division by a {@code move_energy} other than 100, a shorter
 * day).
 *
 * <p>Most of what is tested is {@code private} in the port, because C keeps it {@code static}, so
 * the tests reach it by reflection through {@link #call}. The collaborators that are still stubs
 * (the monster turn, level generation, the player-utility upkeep) are not exercised: the tests
 * stay on what {@code GameWorld} itself decides, and watch the effects that can be seen without
 * them, namely fields on the player, flags on monsters, and events on a capturing bus.
 *
 * <p>{@code GameWorld} keeps {@code dayCount} and {@code characterDungeon} in static fields, and the
 * tests touch the turn counter, the player, the cave, the event bus and the constants table, so
 * each is saved before every test and put back afterwards.
 *
 * <p>Class GameWorldTest coded on 261006, commented in full on 261006.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class GameWorldTest {

    /**
     * C's {@code extract_energy[200]}, copied from {@code game-world.c} row by row.
     */
    private static final int[] C_EXTRACT_ENERGY = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 3, 3, 3,
            3, 3, 3, 3, 3, 4, 4, 4, 4, 4,
            5, 5, 5, 5, 6, 6, 7, 7, 8, 9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35, 36, 36, 37, 37,
            38, 38, 39, 39, 40, 40, 40, 41, 41, 41,
            42, 42, 42, 43, 43, 43, 44, 44, 44, 44,
            45, 45, 45, 45, 45, 46, 46, 46, 46, 46,
            47, 47, 47, 47, 47, 48, 48, 48, 48, 48,
            49, 49, 49, 49, 49, 49, 49, 49, 49, 49,
            49, 49, 49, 49, 49, 49, 49, 49, 49, 49,
    };

    /**
     * The constants table the game held before the test replaced it.
     */
    private Object savedConstants;

    /**
     * The turn counter before the test.
     */
    private int savedTurn;

    /**
     * The player the game held before the test.
     */
    private Player savedPlayer;

    /**
     * The cave the game held before the test.
     */
    private Chunk savedCave;

    /**
     * The event bus the engine held before the test.
     */
    private EventsHandler savedBus;

    /**
     * {@code GameWorld.dayCount} before the test.
     */
    private int savedDayCount;

    /**
     * {@code GameWorld.characterDungeon} before the test.
     */
    private boolean savedCharacterDungeon;

    /**
     * The timed-effect list the registry held before the test.
     */
    private Object savedTimedEffects;

    /**
     * The player {@code PlayerUtils} had cached before the test.
     */
    private Object savedUtilsPlayer;

    /**
     * The command queue the game held before the test.
     */
    private CommandQueue savedQueue;

    /**
     * The character under test.
     */
    private Player player;

    /**
     * The level the character is on.
     */
    private Chunk level;

    /**
     * The world under test.
     */
    private GameWorld world;

    /**
     * The bus that records what the world signals.
     */
    private CapturingBus bus;

    /**
     * Reaches a private field.
     *
     * @param owner the declaring class
     * @param name  the field
     * @return the accessible field
     * @throws ReflectiveOperationException if it cannot be reached
     */
    private static Field field(Class<?> owner, String name) throws ReflectiveOperationException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /**
     * Parses the shipped {@code constants.txt}.
     *
     * @return the data the game runs on
     * @throws Exception if the file cannot be read
     */
    private static GameConstantsData shippedConstants() throws Exception {
        return new GameConstantsReader().parseWithResults("lib/gamedata/constants.txt").getData();
    }

    /**
     * Publishes the shipped constants with the {@code world} section changed.
     *
     * @param change what to do to the shipped {@link WorldData}
     * @throws Exception if the table cannot be reached
     */
    private static void alterWorld(UnaryOperator<WorldData> change) throws Exception {
        GameConstantsData s = shippedConstants();
        GameConstantsData altered = new GameConstantsData(
                s.levelMax(), s.monGen(), s.monPlay(), s.dunGen(), change.apply(s.world()), s.carryCap(),
                s.store(), s.objMake(), s.player(), s.meleeCritical(), s.meleeCriticalLevel(),
                s.rangedCritical(), s.rangedCriticalLevel(), s.oMeleeCritical(), s.oMeleeCriticalLevel(),
                s.oRangedCritical(), s.oRangedCriticalLevel());
        field(GameConstants.class, "data").set(null, altered);
    }

    /**
     * One grade of a timed effect.
     *
     * @param grade  the grade number
     * @param max    the largest counter value in the grade
     * @param status the grade's name
     * @return the grade
     */
    private static TimedGrade grade(int grade, int max, String status) {
        return new TimedGrade(grade, ColourEnum.COLOUR_GREEN, max, status, "up", "down");
    }

    /**
     * A timed effect with the given grades and nothing else.
     *
     * @param name       the effect
     * @param lowerBound the lowest value the effect can be driven to
     * @param grades     its grades
     * @return the effect
     */
    private static PlayerTimedEffect effect(TimedEffect name, int lowerBound, TimedGrade... grades) {
        return new PlayerTimedEffect(name, "test", null, null, null, null, List.of(), List.of(grades),
                (Effect) null, (Effect) null, false, lowerBound, ObjectFlag.OF_NONE, false,
                ElementEnum.ELEM_NONE, null, null);
    }

    /**
     * The timed effects the world pass needs to find in the registry: {@code FOOD} with the six
     * grades of {@code player_timed.txt}, whose percentages {@code 1 / 4 / 8 / 15 / 90 / 100} the
     * parser multiplies by the shipped {@code food-value} of 100, and a one-grade {@code HEAL}.
     *
     * @return the list
     */
    private static List<PlayerTimedEffect> timedEffects() {
        List<PlayerTimedEffect> list = new ArrayList<>();
        list.add(effect(TimedEffect.TMD_FOOD, 1,
                grade(1, 100, "Starving"), grade(2, 400, "Faint"), grade(3, 800, "Weak"),
                grade(4, 1500, "Hungry"), grade(5, 9000, "Fed"), grade(6, 10000, "Full")));
        list.add(effect(TimedEffect.TMD_HEAL, 0, grade(1, 10000, "Heal")));
        list.add(effect(TimedEffect.TMD_CUT, 0,
                grade(1, 10, "Graze"), grade(2, 25, "Light Cut"), grade(3, 50, "Bad Cut"),
                grade(4, 100, "Nasty Cut"), grade(5, 200, "Severe Cut"), grade(6, 1000, "Deep Gash"),
                grade(7, 10000, "Mortal Wound")));
        list.add(effect(TimedEffect.TMD_POISONED, 0, grade(1, 10000, "Poisoned")));
        list.add(effect(TimedEffect.TMD_STUN, 0,
                grade(1, 50, "Stun"), grade(2, 150, "Heavy Stun"), grade(3, 10000, "Knocked Out")));
        return list;
    }

    /**
     * Calls one of {@link GameWorld}'s private methods.
     *
     * @param target the instance, or {@code null} for a static method
     * @param name   the method
     * @param types  its parameter types
     * @param args   the arguments
     * @return what it returned
     * @throws Exception whatever the method threw, unwrapped
     */
    private static Object call(Object target, String name, Class<?>[] types, Object... args) throws Exception {
        Method method = GameWorld.class.getDeclaredMethod(name, types);
        method.setAccessible(true);
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception cause) throw cause;
            throw e;
        }
    }

    /**
     * C's {@code turn_energy(speed)}.
     *
     * @param speed the speed
     * @return what the port answers
     * @throws Exception whatever the method threw
     */
    private static int turnEnergy(int speed) throws Exception {
        return (int) call(null, "turnEnergy", new Class<?>[]{int.class}, speed);
    }

    /**
     * A race that counts as live, with the given race flags.
     *
     * @param flags the race flags to set
     * @return the race
     */
    private static MonsterRace race(MonsterRaceFlag... flags) {
        MonsterRace race = new MonsterRace("test", "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                null, null, List.of(), 0, 0, null, null,
                List.of(), List.of(), List.of(), List.of(), List.of(), 0, null);
        Flag<MonsterRaceFlag> set = new Flag<>(MonsterRaceFlag.class);
        for (MonsterRaceFlag flag : flags) set.on(flag);
        ItemFixture.set(race, "flags", set);
        return race;
    }

    /**
     * Wires a character, a level, a world and a capturing bus, over the shipped constants.
     *
     * @throws Exception if the static state cannot be reached
     */
    @BeforeEach
    void setUp() throws Exception {
        savedConstants = field(GameConstants.class, "data").get(null);
        field(GameConstants.class, "data").set(null, shippedConstants());
        savedTurn = GameState.getTurn();
        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();
        savedBus = GameEngine.getEventsBusHandler();
        savedDayCount = (int) field(GameWorld.class, "dayCount").get(null);
        savedCharacterDungeon = GameWorld.hasCharacterDungeon();
        savedTimedEffects = field(PlayerRegistry.class, "playerTimedEffects").get(null);
        savedUtilsPlayer = field(PlayerUtils.class, "player").get(null);
        savedQueue = GameState.getCommandQueue();

        player = new Player();
        CalcBonusesFixture.plainCharacter(player);
        level = new Chunk("level", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        player.setCave(new Chunk("known", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player));
        GameState.setPlayer(player);
        GameState.setCave(level);
        level.setCurrentLevel(level);
        GameState.setTurn(0);

        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
        field(PlayerUtils.class, "player").set(null, player);
        field(PlayerRegistry.class, "playerTimedEffects").set(null, timedEffects());
        GameState.setCommandQueue(new CommandQueue(player));

        world = new GameWorld();
    }

    /**
     * Puts back everything {@link #setUp()} replaced.
     *
     * @throws Exception if the static state cannot be reached
     */
    @AfterEach
    void tearDown() throws Exception {
        field(GameConstants.class, "data").set(null, savedConstants);
        GameState.setTurn(savedTurn);
        GameState.setPlayer(savedPlayer);
        GameState.setCave(savedCave);
        GameEngine.setEventsBusHandler(savedBus);
        field(GameWorld.class, "dayCount").set(null, savedDayCount);
        GameWorld.setCharacterDungeon(savedCharacterDungeon);
        field(PlayerRegistry.class, "playerTimedEffects").set(null, savedTimedEffects);
        field(PlayerUtils.class, "player").set(null, savedUtilsPlayer);
        GameState.setCommandQueue(savedQueue);
    }

    /**
     * Runs a no-argument private method of the world under test.
     *
     * @param name the method
     * @throws Exception whatever it threw
     */
    private void run(String name) throws Exception {
        call(world, name, new Class<?>[0]);
    }

    /**
     * Switches {@code use_sound} on for the character, the one option {@code sound()} reads.
     *
     * @throws Exception if the option set cannot be reached
     */
    private void soundOn() throws Exception {
        PlayerOptions opts = new PlayerOptions();
        @SuppressWarnings("unchecked")
        Flag<PlayerOptionEnum> flags = (Flag<PlayerOptionEnum>) field(PlayerOptions.class, "options").get(opts);
        flags.on(PlayerOptionEnum.OP_use_sound);
        field(Player.class, "options").set(player, opts);
    }

    /**
     * Puts a live monster on the level in the given slot.
     *
     * @param slot  its index in the monster array
     * @param race  its race
     * @param flags the monster flags it starts with
     * @return the monster
     */
    private Monster monster(int slot, MonsterRace race, MonsterFlag... flags) {
        Flag<MonsterFlag> set = new Flag<>(MonsterFlag.class);
        for (MonsterFlag flag : flags) set.on(flag);
        Monster monster = new Monster(race, null, Loc.row(2).col(3), 0, 0, null, 0, 0, 0, set,
                null, null, null, null, null, null, null, 0, 0);
        level.getMonsters()[slot] = monster;
        return monster;
    }

    /**
     * Installs a scripted queue as the game's command queue.
     *
     * @param uses    how many pops hand out an energy-spending command
     * @param repeats the repeat count to report
     * @return the queue
     */
    private ScriptedQueue scripted(int uses, int repeats) {
        ScriptedQueue queue = new ScriptedQueue(player, uses, repeats);
        GameState.setCommandQueue(queue);
        return queue;
    }

    /**
     * The events among the given types, in the order they were signalled.
     *
     * @param wanted the types to keep
     * @return the filtered sequence
     */
    private List<GameEventType> eventsOf(GameEventType... wanted) {
        List<GameEventType> keep = List.of(wanted);
        List<GameEventType> seen = new ArrayList<>();
        for (GameEventType type : bus.types) if (keep.contains(type)) seen.add(type);
        return seen;
    }

    /**
     * An event bus that records what is signalled.
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * Every event type seen, in order.
         */
        private final List<GameEventType> types = new ArrayList<>();

        /**
         * The sound events' message types, in order.
         */
        private final List<MessageType> sounds = new ArrayList<>();

        /**
         * The text of every message event, in order.
         */
        private final List<String> messages = new ArrayList<>();

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
            types.add(eventType);
            if (eventType == GameEventType.EVENT_SOUND && data instanceof EventDataMessage message)
                sounds.add(message.type());
            if (eventType == GameEventType.EVENT_MESSAGE && data instanceof EventDataMessage message)
                messages.add(message.message());
        }

        /**
         * @param type an event type
         * @return how many times it was signalled
         */
        int count(GameEventType type) {
            return (int) types.stream().filter(t -> t == type).count();
        }
    }

    /**
     * A command queue that stands in for the player's input: the first {@code uses} pops each
     * "execute" a command that spends 100 energy, and later pops find the queue empty. That is the
     * smallest thing that lets {@code runGameLoop} take a real player turn without the command
     * table, which belongs to a later chapter.
     */
    private static final class ScriptedQueue extends CommandQueue {

        /**
         * The character whose upkeep records the energy used.
         */
        private final Player scriptedPlayer;
        /**
         * What {@code getNrepeats} answers.
         */
        private final int repeats;
        /**
         * How many pops will still hand out a command.
         */
        private int uses;

        /**
         * @param player  the character
         * @param uses    how many pops hand out a command
         * @param repeats the repeat count to report
         */
        ScriptedQueue(Player player, int uses, int repeats) {
            super(player);
            this.scriptedPlayer = player;
            this.uses = uses;
            this.repeats = repeats;
        }

        @Override
        public boolean commandPop(CommandContext commandContext) {
            if (uses <= 0) return false;
            uses--;
            scriptedPlayer.getPlayerUpkeep().setEnergyUse(100);
            return true;
        }

        @Override
        public int getNrepeats() {
            return repeats;
        }
    }

    /**
     * C's {@code turn_energy}: {@code extract_energy[speed] * z_info->move_energy / 100}.
     */
    @Nested
    @DisplayName("turnEnergy")
    class TurnEnergy {

        /**
         * With the shipped {@code move_energy} of 100 the division is by itself, so every speed
         * answers the table entry unchanged. All 200 are checked against the table copied from C.
         */
        @Test
        @DisplayName("every speed answers the C table at the shipped move_energy")
        void everySpeedMatchesTheTable() throws Exception {
            for (int speed = 0; speed < 200; speed++)
                assertEquals(C_EXTRACT_ENERGY[speed], turnEnergy(speed), "speed " + speed);
        }

        /**
         * The figures the table's own comment promises: normal speed 110 banks 10, +10 doubles it,
         * and the curve flattens at 49.
         */
        @Test
        @DisplayName("normal speed is 10, +10 doubles it, and the top caps at 49")
        void landmarkFigures() throws Exception {
            assertEquals(1, turnEnergy(0));
            assertEquals(9, turnEnergy(109));
            assertEquals(10, turnEnergy(110));
            assertEquals(20, turnEnergy(120));
            assertEquals(30, turnEnergy(130));
            assertEquals(47, turnEnergy(170));
            assertEquals(48, turnEnergy(179));
            assertEquals(49, turnEnergy(180));
            assertEquals(49, turnEnergy(199));
        }

        /**
         * C divides in integers, so a smaller {@code move_energy} truncates: 1 * 30 / 100 is 0, not
         * a fraction, and 19 * 30 / 100 is 5.
         */
        @Test
        @DisplayName("a smaller move_energy truncates by integer division")
        void integerDivisionTruncates() throws Exception {
            alterWorld(w -> new WorldData(w.maxDepth(), w.dayLength(), w.dungeonHgt(), w.dungeonWid(),
                    w.townHgt(), w.townWid(), w.feelingTotal(), w.feelingNeed(), w.stairSkip(), 30));

            assertEquals(0, turnEnergy(0));
            assertEquals(3, turnEnergy(110));
            assertEquals(3, turnEnergy(111));
            assertEquals(5, turnEnergy(119));
            assertEquals(14, turnEnergy(199));
        }

        /**
         * A larger {@code move_energy} scales every figure up in proportion, so the cost of a move
         * is data-driven while the speed curve stays fixed.
         */
        @Test
        @DisplayName("a larger move_energy scales the figures up")
        void largerMoveEnergyScalesUp() throws Exception {
            alterWorld(w -> new WorldData(w.maxDepth(), w.dayLength(), w.dungeonHgt(), w.dungeonWid(),
                    w.townHgt(), w.townWid(), w.feelingTotal(), w.feelingNeed(), w.stairSkip(), 200));

            assertEquals(2, turnEnergy(0));
            assertEquals(20, turnEnergy(110));
            assertEquals(98, turnEnergy(199));
        }

        /**
         * The table has 200 entries; C reads past the array for anything else, and the port throws.
         * The test pins that the port does not quietly clamp.
         */
        @Test
        @DisplayName("a speed outside 0..199 is not clamped")
        void outOfRangeThrows() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> turnEnergy(200));
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> turnEnergy(-1));
        }
    }

    /**
     * C's {@code is_daytime}: the first half of each {@code 10 * day_length} cycle is day.
     */
    @Nested
    @DisplayName("isDaytime")
    class IsDaytime {

        /**
         * With {@code day_length} 10000 a cycle is 100000 turns: 0..49999 day, 50000..99999 night.
         */
        @Test
        @DisplayName("day and night either side of the half-cycle boundary")
        void boundaries() {
            int[][] cases = {
                    {0, 1}, {1, 1}, {49999, 1}, {50000, 0}, {50001, 0}, {99999, 0},
                    {100000, 1}, {149999, 1}, {150000, 0}, {199999, 0}, {200000, 1},
            };
            for (int[] c : cases) {
                GameState.setTurn(c[0]);
                assertEquals(c[1] == 1, GameWorld.isDaytime(), "turn " + c[0]);
            }
        }

        /**
         * The modulus is taken in {@code long}, so a turn near the top of the {@code int32} range
         * still lands in the right half: 2147483647 mod 100000 is 83647, which is night.
         */
        @Test
        @DisplayName("the largest turn count does not overflow the arithmetic")
        void largestTurn() {
            GameState.setTurn(Integer.MAX_VALUE);
            assertFalse(GameWorld.isDaytime());

            GameState.setTurn(12345678); // 45678 into the cycle: day
            assertTrue(GameWorld.isDaytime());
        }

        /**
         * The cycle is {@code 10 * day_length}, so a shorter day moves the boundary: with
         * {@code day_length} 100 it is 1000 turns, day for 0..499.
         */
        @Test
        @DisplayName("a shorter day_length moves the boundary")
        void shorterDay() throws Exception {
            alterWorld(w -> new WorldData(w.maxDepth(), 100, w.dungeonHgt(), w.dungeonWid(),
                    w.townHgt(), w.townWid(), w.feelingTotal(), w.feelingNeed(), w.stairSkip(), w.moveEnergy()));

            int[][] cases = {{0, 1}, {499, 1}, {500, 0}, {999, 0}, {1000, 1}, {1499, 1}, {1500, 0}};
            for (int[] c : cases) {
                GameState.setTurn(c[0]);
                assertEquals(c[1] == 1, GameWorld.isDaytime(), "turn " + c[0]);
            }
        }
    }

    /**
     * What the constructor and {@code getDaycount} do with the static day counter.
     */
    @Nested
    @DisplayName("construction and the day counter")
    class Construction {

        /**
         * C initialises {@code daycount} to 0; a world built after days were banked starts again.
         */
        @Test
        @DisplayName("building a world zeroes the banked days")
        void constructorZeroesDayCount() throws Exception {
            field(GameWorld.class, "dayCount").set(null, 7);
            assertEquals(7, GameWorld.getDaycount());

            new GameWorld();

            assertEquals(0, GameWorld.getDaycount());
        }

        /**
         * {@link GameState#getDaycount()} forwards to the same counter.
         */
        @Test
        @DisplayName("GameState reads the same counter")
        void gameStateForwards() throws Exception {
            field(GameWorld.class, "dayCount").set(null, 3);

            assertEquals(3, GameState.getDaycount());
        }

        /**
         * The constructor caches the game's current character and level, C's {@code player} and
         * {@code cave} globals.
         */
        @Test
        @DisplayName("the constructor caches the game's player and cave")
        void cachesPlayerAndCave() throws Exception {
            assertSame(player, field(GameWorld.class, "player").get(world));
            assertSame(level, field(GameWorld.class, "currentCave").get(world));
        }

        /**
         * The constructor leaves {@code character_dungeon} alone; only the setter writes it.
         */
        @Test
        @DisplayName("the constructor does not touch character_dungeon")
        void leavesCharacterDungeonAlone() {
            GameWorld.setCharacterDungeon(true);
            new GameWorld();
            assertTrue(GameWorld.hasCharacterDungeon());

            GameWorld.setCharacterDungeon(false);
            new GameWorld();
            assertFalse(GameWorld.hasCharacterDungeon());
        }
    }

    /**
     * C's {@code play_ambient_sound}: day or night in town, then bands of twenty levels.
     */
    @Nested
    @DisplayName("playAmbientSound")
    class PlayAmbientSound {

        /**
         * In town it is the time of day that decides, and the boundary is the half-day.
         */
        @Test
        @DisplayName("in town the time of day picks day or night")
        void townDayAndNight() throws Exception {
            soundOn();
            player.setDepth(0);

            GameState.setTurn(49999);
            run("playAmbientSound");
            GameState.setTurn(50000);
            run("playAmbientSound");

            assertEquals(List.of(MessageType.MSG_AMBIENT_DAY, MessageType.MSG_AMBIENT_NITE), bus.sounds);
        }

        /**
         * In the dungeon the bands are inclusive at the top: 20 is still the first, 21 the second.
         */
        @Test
        @DisplayName("the dungeon bands are twenty levels wide, inclusive at the top")
        void dungeonBands() throws Exception {
            soundOn();
            int[] depths = {1, 20, 21, 40, 41, 60, 61, 80, 81, 127};
            MessageType[] expected = {
                    MessageType.MSG_AMBIENT_DNG1, MessageType.MSG_AMBIENT_DNG1,
                    MessageType.MSG_AMBIENT_DNG2, MessageType.MSG_AMBIENT_DNG2,
                    MessageType.MSG_AMBIENT_DNG3, MessageType.MSG_AMBIENT_DNG3,
                    MessageType.MSG_AMBIENT_DNG4, MessageType.MSG_AMBIENT_DNG4,
                    MessageType.MSG_AMBIENT_DNG5, MessageType.MSG_AMBIENT_DNG5,
            };
            for (int i = 0; i < depths.length; i++) {
                bus.sounds.clear();
                player.setDepth(depths[i]);
                run("playAmbientSound");
                assertEquals(List.of(expected[i]), bus.sounds, "depth " + depths[i]);
            }
        }

        /**
         * Depth does not matter for the time of day: a dungeon level at night is still the dungeon
         * sound.
         */
        @Test
        @DisplayName("time of day is ignored below the town")
        void dungeonIgnoresTimeOfDay() throws Exception {
            soundOn();
            player.setDepth(5);
            GameState.setTurn(50000);

            run("playAmbientSound");

            assertEquals(List.of(MessageType.MSG_AMBIENT_DNG1), bus.sounds);
        }

        /**
         * With {@code use_sound} off nothing is signalled, as C's {@code sound()} returns early.
         */
        @Test
        @DisplayName("nothing is signalled with use_sound off")
        void silentWhenOptionOff() throws Exception {
            player.setDepth(5);

            run("playAmbientSound");

            assertTrue(bus.sounds.isEmpty());
        }
    }

    /**
     * C's {@code process_player_cleanup}.
     */
    @Nested
    @DisplayName("processPlayerCleanup")
    class ProcessPlayerCleanup {

        /**
         * The ordinary path: the energy the command used comes off the player's energy and goes on
         * the running total.
         */
        @Test
        @DisplayName("energy used is deducted and added to the total")
        void energyIsDeductedAndTotalled() throws Exception {
            player.setEnergy(100);
            player.setTotalEnergy(5);
            player.getPlayerUpkeep().setEnergyUse(30);

            run("processPlayerCleanup");

            assertEquals(70, player.getEnergy());
            assertEquals(35, player.getTotalEnergy());
        }

        /**
         * A command that used no energy changes neither figure.
         */
        @Test
        @DisplayName("no energy used leaves energy and total alone")
        void noEnergyUsedLeavesFiguresAlone() throws Exception {
            player.setEnergy(100);
            player.setTotalEnergy(5);
            player.getPlayerUpkeep().setEnergyUse(0);

            run("processPlayerCleanup");

            assertEquals(100, player.getEnergy());
            assertEquals(5, player.getTotalEnergy());
        }

        /**
         * When energy was used the skip-coercion counter counts down, but never below zero:
         * 2 to 1, 1 to 0, and 0 stays 0.
         */
        @Test
        @DisplayName("with energy used the skip counter counts down to zero")
        void skipCounterCountsDown() throws Exception {
            int[][] cases = {{2, 1}, {1, 0}, {0, 0}};
            for (int[] c : cases) {
                player.setSkipCmdCoercion(c[0]);
                player.getPlayerUpkeep().setEnergyUse(10);

                run("processPlayerCleanup");

                assertEquals(c[1], player.getSkipCmdCoercion(), "started at " + c[0]);
            }
        }

        /**
         * With no energy used the counter is only ever pulled back from above one to one: a
         * background command ran while the check was skipped. One and zero are left alone.
         */
        @Test
        @DisplayName("with no energy used a counter above one is reset to one")
        void skipCounterResetToOne() throws Exception {
            int[][] cases = {{2, 1}, {5, 1}, {1, 1}, {0, 0}};
            for (int[] c : cases) {
                player.setSkipCmdCoercion(c[0]);
                player.getPlayerUpkeep().setEnergyUse(0);

                run("processPlayerCleanup");

                assertEquals(c[1], player.getSkipCmdCoercion(), "started at " + c[0]);
            }
        }

        /**
         * The drop status is reset whatever happened, whether or not energy was used.
         */
        @Test
        @DisplayName("the drop status is always reset")
        void dropStatusAlwaysReset() throws Exception {
            player.getPlayerUpkeep().setDropping(true);
            player.getPlayerUpkeep().setEnergyUse(0);
            run("processPlayerCleanup");
            assertFalse(player.getPlayerUpkeep().getDropping());

            player.getPlayerUpkeep().setDropping(true);
            player.getPlayerUpkeep().setEnergyUse(10);
            run("processPlayerCleanup");
            assertFalse(player.getPlayerUpkeep().getDropping());
        }

        /**
         * The per-turn SHOW flag is cleared on every monster in every case: energy used, none
         * used, and an auto-drop.
         */
        @Test
        @DisplayName("SHOW is cleared on every monster in every case")
        void showAlwaysCleared() throws Exception {
            Monster a = monster(1, race(), MonsterFlag.MFLAG_SHOW);
            Monster b = monster(2, race(), MonsterFlag.MFLAG_SHOW);

            player.getPlayerUpkeep().setEnergyUse(0);
            run("processPlayerCleanup");
            assertFalse(a.hasMonsterFlag(MonsterFlag.MFLAG_SHOW));
            assertFalse(b.hasMonsterFlag(MonsterFlag.MFLAG_SHOW));

            a.monsterFlagOn(MonsterFlag.MFLAG_SHOW);
            player.getPlayerUpkeep().setEnergyUse(10);
            player.getPlayerUpkeep().setDropping(true);
            run("processPlayerCleanup");
            assertFalse(a.hasMonsterFlag(MonsterFlag.MFLAG_SHOW));
        }

        /**
         * NICE is cleared only on the energy-used, not-dropping arm.
         */
        @Test
        @DisplayName("NICE is cleared only when energy was used and nothing was auto-dropped")
        void niceClearedOnlyOnTheFullArm() throws Exception {
            Monster monster = monster(1, race(), MonsterFlag.MFLAG_NICE);

            player.getPlayerUpkeep().setEnergyUse(0);
            run("processPlayerCleanup");
            assertTrue(monster.hasMonsterFlag(MonsterFlag.MFLAG_NICE), "no energy used");

            player.getPlayerUpkeep().setEnergyUse(10);
            player.getPlayerUpkeep().setDropping(true);
            run("processPlayerCleanup");
            assertTrue(monster.hasMonsterFlag(MonsterFlag.MFLAG_NICE), "auto-drop");

            player.getPlayerUpkeep().setEnergyUse(10);
            player.getPlayerUpkeep().setDropping(false);
            run("processPlayerCleanup");
            assertFalse(monster.hasMonsterFlag(MonsterFlag.MFLAG_NICE), "full arm");
        }

        /**
         * A marked monster that is not shown loses the mark; one that is shown keeps it (SHOW
         * itself is cleared at the end, so the mark survives one more turn).
         */
        @Test
        @DisplayName("MARK is dropped only from a monster that is not SHOWn")
        void markDroppedOnlyWhenNotShown() throws Exception {
            Monster unshown = monster(1, race(), MonsterFlag.MFLAG_MARK);
            Monster shown = monster(2, race(), MonsterFlag.MFLAG_MARK, MonsterFlag.MFLAG_SHOW);
            player.getPlayerUpkeep().setEnergyUse(10);

            run("processPlayerCleanup");

            assertFalse(unshown.hasMonsterFlag(MonsterFlag.MFLAG_MARK));
            assertTrue(shown.hasMonsterFlag(MonsterFlag.MFLAG_MARK));
            assertFalse(shown.hasMonsterFlag(MonsterFlag.MFLAG_SHOW));
        }

        /**
         * With no energy used the mark logic is not reached at all.
         */
        @Test
        @DisplayName("MARK is untouched when no energy was used")
        void markUntouchedWithoutEnergy() throws Exception {
            Monster unshown = monster(1, race(), MonsterFlag.MFLAG_MARK);
            player.getPlayerUpkeep().setEnergyUse(0);

            run("processPlayerCleanup");

            assertTrue(unshown.hasMonsterFlag(MonsterFlag.MFLAG_MARK));
        }

        /**
         * A multi-hued monster's grid is relit so it shimmers, once per such monster, and only on
         * the full arm. A monster of an ordinary race is not relit.
         */
        @Test
        @DisplayName("multi-hued monsters shimmer on the full arm only")
        void multiHuedMonstersShimmer() throws Exception {
            monster(1, race(MonsterRaceFlag.RF_ATTR_MULTI));
            monster(2, race(MonsterRaceFlag.RF_ATTR_MULTI));
            monster(3, race());

            player.getPlayerUpkeep().setEnergyUse(10);
            run("processPlayerCleanup");
            assertEquals(2, bus.count(GameEventType.EVENT_MAP));

            bus.types.clear();
            player.getPlayerUpkeep().setEnergyUse(0);
            run("processPlayerCleanup");
            assertEquals(0, bus.count(GameEventType.EVENT_MAP), "no energy used");

            player.getPlayerUpkeep().setEnergyUse(10);
            player.getPlayerUpkeep().setDropping(true);
            run("processPlayerCleanup");
            assertEquals(0, bus.count(GameEventType.EVENT_MAP), "auto-drop");
        }

        /**
         * Hallucination redraws the map every turn that used energy, so the monsters keep
         * changing; it is not done for an auto-drop.
         */
        @Test
        @DisplayName("hallucination signals a map redraw on the full arm only")
        void hallucinationRedrawsMap() throws Exception {
            // redrawStuff does nothing until a character exists, and it is what turns the
            // PR_MAP flag into an EVENT_MAP signal that can be seen on the bus.
            boolean wasGenerated = GameState.getCharacterGenerated();
            GameState.setCharacterGenerated(true);
            try {
                player.putTimed(TimedEffect.TMD_IMAGE, 10);

                player.getPlayerUpkeep().setEnergyUse(10);
                run("processPlayerCleanup");
                assertEquals(1, bus.count(GameEventType.EVENT_MAP));

                bus.types.clear();
                player.getPlayerUpkeep().setEnergyUse(10);
                player.getPlayerUpkeep().setDropping(true);
                run("processPlayerCleanup");
                assertEquals(0, bus.count(GameEventType.EVENT_MAP), "auto-drop");

                player.getPlayerUpkeep().setEnergyUse(0);
                run("processPlayerCleanup");
                assertEquals(0, bus.count(GameEventType.EVENT_MAP), "no energy used");
            } finally {
                GameState.setCharacterGenerated(wasGenerated);
            }
        }
    }

    /**
     * The parts of C's {@code process_world} that {@code GameWorld} decides for itself: the day
     * counter, the town's dawn and dusk, and the monster-list upkeep. The player is set resting so
     * the noise and scent pass, which needs a real map, is not reached.
     */
    @Nested
    @DisplayName("processWorld")
    class ProcessWorld {

        /**
         * Puts the character into the state the world pass can run on: resting, so no noise or
         * scent is laid.
         */
        @BeforeEach
        void resting() {
            player.getPlayerUpkeep().setRestingCounter(5);
        }

        /**
         * In the dungeon one day is banked every {@code 10 * store_turns} game turns, which with the
         * shipped {@code store:turns:1000} is every 10000 turns, and only on the exact multiple.
         */
        @Test
        @DisplayName("the dungeon banks a day on every 10 * store_turns turns")
        void dungeonBanksADay() throws Exception {
            player.setDepth(3);

            GameState.setTurn(9990);
            run("processWorld");
            assertEquals(0, GameWorld.getDaycount(), "turn 9990");

            GameState.setTurn(10000);
            run("processWorld");
            assertEquals(1, GameWorld.getDaycount(), "turn 10000");

            GameState.setTurn(10010);
            run("processWorld");
            assertEquals(1, GameWorld.getDaycount(), "turn 10010");

            GameState.setTurn(20000);
            run("processWorld");
            assertEquals(2, GameWorld.getDaycount(), "turn 20000");
        }

        /**
         * In town the stores are kept current, so nothing is banked, even on the multiple.
         */
        @Test
        @DisplayName("the town banks nothing")
        void townBanksNothing() throws Exception {
            player.setDepth(0);

            GameState.setTurn(10000);
            run("processWorld");

            assertEquals(0, GameWorld.getDaycount());
        }

        /**
         * The change of store day follows {@code store_turns}, not {@code day_length}: with a
         * 500-turn store day the multiple moves to 5000.
         */
        @Test
        @DisplayName("the banked day follows store_turns")
        void followsStoreTurns() throws Exception {
            player.setDepth(3);
            GameConstantsData s = shippedConstants();
            GameConstantsData altered = new GameConstantsData(
                    s.levelMax(), s.monGen(), s.monPlay(), s.dunGen(), s.world(), s.carryCap(),
                    new StoreData(s.store().invenMax(), 500, s.store().shuffle(), s.store().magicLevel()),
                    s.objMake(), s.player(), s.meleeCritical(), s.meleeCriticalLevel(),
                    s.rangedCritical(), s.rangedCriticalLevel(), s.oMeleeCritical(), s.oMeleeCriticalLevel(),
                    s.oRangedCritical(), s.oRangedCriticalLevel());
            field(GameConstants.class, "data").set(null, altered);

            GameState.setTurn(5000);
            run("processWorld");

            assertEquals(1, GameWorld.getDaycount());
        }

        /**
         * The town announces sunrise on the full cycle and sunset on the half cycle, and says
         * nothing on any other multiple of ten turns.
         */
        @Test
        @DisplayName("the town announces dawn on the cycle and dusk on the half cycle")
        void townAnnouncesDawnAndDusk() throws Exception {
            player.setDepth(0);

            GameState.setTurn(100000);
            run("processWorld");
            GameState.setTurn(150000);
            run("processWorld");
            GameState.setTurn(125000);
            run("processWorld");

            assertEquals(2, bus.count(GameEventType.EVENT_MESSAGE));
        }

        /**
         * The same boundary in the dungeon announces nothing: only the town has a sun.
         */
        @Test
        @DisplayName("the dungeon announces no dawn or dusk")
        void dungeonHasNoSun() throws Exception {
            player.setDepth(4);

            GameState.setTurn(150000);
            run("processWorld");

            assertEquals(0, bus.count(GameEventType.EVENT_MESSAGE));
        }

        /**
         * An ambient sound plays every quarter of a day, {@code 10 * day_length / 4} turns: 25000
         * with the shipped data, so turns 25000 and 50000 sound and 30000 does not.
         */
        @Test
        @DisplayName("an ambient sound plays every quarter day")
        void ambientEveryQuarterDay() throws Exception {
            soundOn();
            player.setDepth(3);

            GameState.setTurn(30000);
            run("processWorld");
            assertTrue(bus.sounds.isEmpty(), "turn 30000");

            GameState.setTurn(25000);
            run("processWorld");
            GameState.setTurn(50000);
            run("processWorld");
            assertEquals(2, bus.sounds.size());
        }
    }

    /**
     * The food, healing and delayed-teleport parts of C's {@code process_world}.
     *
     * <p>The character is resting on level 3 at normal speed with 5000 food, which is the "Fed"
     * grade: not full enough to be gorged and not hungry. The shipped {@code food-value} is 100, so
     * {@code 100 / food_value} is 1 and the figures below are the C arithmetic spelled out.
     */
    @Nested
    @DisplayName("processWorld: food, healing and delayed teleports")
    class ProcessWorldFood {

        /**
         * Sets the starting state described on the class.
         *
         * @throws Exception if a field cannot be reached
         */
        @BeforeEach
        void fedAndResting() throws Exception {
            player.getPlayerUpkeep().setRestingCounter(5);
            player.setDepth(3);
            player.putTimed(TimedEffect.TMD_FOOD, 5000);
            setSpeed(110);
        }

        /**
         * Sets the calculated speed.
         *
         * @param speed the speed
         */
        private void setSpeed(int speed) {
            ItemFixture.set(player.getPlayerState(), "speed", speed);
        }

        /**
         * Turns an object flag on in the calculated state.
         *
         * @param flag the flag
         */
        @SuppressWarnings("unchecked")
        private void stateHas(ObjectFlag flag) {
            ((Flag<ObjectFlag>) ItemFixture.read(player.getPlayerState(), "flags")).on(flag);
        }

        /**
         * Digestion happens only when the turn is a multiple of 100, and costs {@code turn_energy}
         * of the player's speed: 10 at normal speed.
         */
        @Test
        @DisplayName("normal digestion costs turn_energy, every 100th turn")
        void normalDigestion() throws Exception {
            GameState.setTurn(150);
            run("processWorld");
            assertEquals(5000, player.getTimedEffect(TimedEffect.TMD_FOOD), "turn 150");

            GameState.setTurn(100);
            run("processWorld");
            assertEquals(4990, player.getTimedEffect(TimedEffect.TMD_FOOD), "turn 100");
        }

        /**
         * Digestion scales with speed: +10 speed is 20 energy, so twice the food.
         */
        @Test
        @DisplayName("a faster player digests faster")
        void speedScalesDigestion() throws Exception {
            setSpeed(120);
            GameState.setTurn(100);

            run("processWorld");

            assertEquals(4980, player.getTimedEffect(TimedEffect.TMD_FOOD));
        }

        /**
         * Regeneration doubles the cost, slow digestion halves it, and the two together cancel.
         */
        @Test
        @DisplayName("regeneration doubles, slow digestion halves, both cancel")
        void regenAndSlowDigest() throws Exception {
            GameState.setTurn(100);

            stateHas(ObjectFlag.OF_REGEN);
            run("processWorld");
            assertEquals(4980, player.getTimedEffect(TimedEffect.TMD_FOOD), "regeneration");

            player.putTimed(TimedEffect.TMD_FOOD, 5000);
            stateHas(ObjectFlag.OF_SLOW_DIGEST);
            run("processWorld");
            assertEquals(4990, player.getTimedEffect(TimedEffect.TMD_FOOD), "both");
        }

        /**
         * Slow digestion alone halves 10 to 5.
         */
        @Test
        @DisplayName("slow digestion alone halves the cost")
        void slowDigestionAlone() throws Exception {
            GameState.setTurn(100);
            stateHas(ObjectFlag.OF_SLOW_DIGEST);

            run("processWorld");

            assertEquals(4995, player.getTimedEffect(TimedEffect.TMD_FOOD));
        }

        /**
         * Digestion never rounds down to nothing: at the slowest speed {@code turn_energy} is 1, and
         * halving it gives 0, which is raised to 1.
         */
        @Test
        @DisplayName("digestion is never less than one")
        void minimalDigestion() throws Exception {
            setSpeed(0);
            GameState.setTurn(100);
            run("processWorld");
            assertEquals(4999, player.getTimedEffect(TimedEffect.TMD_FOOD), "slowest speed");

            player.putTimed(TimedEffect.TMD_FOOD, 5000);
            stateHas(ObjectFlag.OF_SLOW_DIGEST);
            run("processWorld");
            assertEquals(4999, player.getTimedEffect(TimedEffect.TMD_FOOD), "slowest and slow digestion");
        }

        /**
         * A gorged player burns {@code 5000 / food_value} every time the world is processed,
         * whatever the turn, and is flagged for a bonus recalculation; the normal digestion is not
         * also applied on a multiple of 100.
         */
        @Test
        @DisplayName("a gorged player burns 5000 / food_value on every pass")
        void gorgedDigestion() throws Exception {
            player.putTimed(TimedEffect.TMD_FOOD, 9500);
            GameState.setTurn(150);

            run("processWorld");
            assertEquals(9450, player.getTimedEffect(TimedEffect.TMD_FOOD), "turn 150");
            assertTrue(updateFlags().has(PlayerUpdateEnum.PU_BONUS));

            GameState.setTurn(100);
            run("processWorld");
            assertEquals(9400, player.getTimedEffect(TimedEffect.TMD_FOOD), "turn 100: only the gorged cost");
        }

        /**
         * The boundary of "Full": 9000 is still "Fed" and digests normally, 9001 is "Full".
         */
        @Test
        @DisplayName("9000 is Fed and 9001 is gorged")
        void gorgedBoundary() throws Exception {
            player.putTimed(TimedEffect.TMD_FOOD, 9000);
            GameState.setTurn(150);
            run("processWorld");
            assertEquals(9000, player.getTimedEffect(TimedEffect.TMD_FOOD), "9000 on a non-digesting turn");

            player.putTimed(TimedEffect.TMD_FOOD, 9001);
            run("processWorld");
            assertEquals(8951, player.getTimedEffect(TimedEffect.TMD_FOOD), "9001");
        }

        /**
         * Fast metabolism burns {@code 8 * food_value} each pass, and ends the healing if that
         * leaves the player hungry; healing that is not ended still ticks down by one.
         */
        @Test
        @DisplayName("fast metabolism burns 800 and ends healing below Hungry")
        void fastMetabolism() throws Exception {
            player.putTimed(TimedEffect.TMD_HEAL, 5);
            player.putTimed(TimedEffect.TMD_FOOD, 3000);
            GameState.setTurn(150);

            run("processWorld");
            assertEquals(2200, player.getTimedEffect(TimedEffect.TMD_FOOD));
            assertEquals(4, player.getTimedEffect(TimedEffect.TMD_HEAL), "still healing, aged by one");

            player.putTimed(TimedEffect.TMD_FOOD, 2000);
            run("processWorld");
            assertEquals(1200, player.getTimedEffect(TimedEffect.TMD_FOOD));
            assertEquals(0, player.getTimedEffect(TimedEffect.TMD_HEAL), "below Hungry ends the healing");
        }

        /**
         * Healing with exactly enough food to stay at the Hungry line keeps going: the test is
         * strictly less than 1500.
         */
        @Test
        @DisplayName("landing exactly on Hungry does not end healing")
        void healingSurvivesExactlyHungry() throws Exception {
            player.putTimed(TimedEffect.TMD_HEAL, 5);
            player.putTimed(TimedEffect.TMD_FOOD, 2300);
            GameState.setTurn(150);

            run("processWorld");

            assertEquals(1500, player.getTimedEffect(TimedEffect.TMD_FOOD));
            assertEquals(4, player.getTimedEffect(TimedEffect.TMD_HEAL));
        }

        /**
         * Word of Recall counts down one per pass and fires on reaching zero: from the dungeon the
         * player is sent to the town and a new level is requested.
         */
        @Test
        @DisplayName("recall counts down, then yanks a dungeon player up to town")
        void recallUp() throws Exception {
            field(Player.class, "wordRecall").set(player, 2);
            GameState.getCommandQueue().push(CommandCode.CMD_SLEEP);

            run("processWorld");
            assertEquals(1, player.getWordRecall());
            assertEquals(3, player.getDepth());
            assertTrue(bus.messages.isEmpty());
            assertEquals(1, queuedCommands(), "the queue is only flushed when the recall fires");

            run("processWorld");
            assertEquals(0, queuedCommands(), "the recall flushes the queue to save an action");
            assertEquals(0, player.getWordRecall());
            assertEquals(0, player.getDepth());
            assertTrue(player.getPlayerUpkeep().generateLevel());
            assertTrue(bus.messages.contains("You feel yourself yanked upwards!"));
        }

        /**
         * From the town the same recall goes down, to the recall depth.
         */
        @Test
        @DisplayName("recall yanks a town player down to the recall depth")
        void recallDown() throws Exception {
            player.setDepth(0);
            field(Player.class, "recallDepth").set(player, 7);
            field(Player.class, "wordRecall").set(player, 1);

            run("processWorld");

            assertEquals(0, player.getWordRecall());
            assertEquals(7, player.getDepth());
            assertTrue(bus.messages.contains("You feel yourself yanked downwards!"));
        }

        /**
         * Recall is suspended in an arena, so the counter does not move.
         */
        @Test
        @DisplayName("recall is suspended in the arena")
        void recallSuspendedInArena() throws Exception {
            player.getPlayerUpkeep().setArenaLevel(true);
            field(Player.class, "wordRecall").set(player, 1);

            run("processWorld");

            assertEquals(1, player.getWordRecall());
            assertEquals(3, player.getDepth());
        }

        /**
         * With no recall running nothing happens.
         */
        @Test
        @DisplayName("no recall running leaves the player where they are")
        void noRecall() throws Exception {
            run("processWorld");

            assertEquals(3, player.getDepth());
            assertFalse(player.getPlayerUpkeep().generateLevel());
        }

        /**
         * Deep Descent counts down, and on reaching zero takes the player
         * {@code 4 / stair_skip + 1} levels below their <em>maximum</em> depth (not the current
         * one): with the shipped stair-skip of 1 that is five levels, from 10 to 15.
         */
        @Test
        @DisplayName("deep descent goes five levels below the maximum depth")
        void deepDescent() throws Exception {
            field(Player.class, "maxDepth").set(player, 10);
            field(Player.class, "deepDescent").set(player, 2);

            run("processWorld");
            assertEquals(1, player.getDeepDescent());
            assertEquals(3, player.getDepth());

            run("processWorld");
            assertEquals(0, player.getDeepDescent());
            assertEquals(15, player.getDepth());
            assertTrue(bus.messages.contains("The floor opens beneath you!"));
        }

        /**
         * The increment is an integer division: with stair-skip 3, {@code 4 / 3} is 1, so two
         * levels of three, 10 + 6 = 16.
         */
        @Test
        @DisplayName("the descent increment is an integer division by stair_skip")
        void deepDescentWithStairSkip() throws Exception {
            alterWorld(w -> new WorldData(w.maxDepth(), w.dayLength(), w.dungeonHgt(), w.dungeonWid(),
                    w.townHgt(), w.townWid(), w.feelingTotal(), w.feelingNeed(), 3, w.moveEnergy()));
            field(Player.class, "maxDepth").set(player, 10);
            field(Player.class, "deepDescent").set(player, 1);

            run("processWorld");

            assertEquals(16, player.getDepth());
        }

        /**
         * If the target is not deeper than where the player already is, the floor does not open:
         * the player is thrown back in an explosion and the depth is unchanged. The dungeon's
         * bottom, {@code max_depth - 1}, is where the clamp lands.
         */
        @Test
        @DisplayName("at the bottom of the dungeon the descent is an explosion")
        void deepDescentAtTheBottom() throws Exception {
            player.setDepth(127);
            field(Player.class, "maxDepth").set(player, 127);
            field(Player.class, "deepDescent").set(player, 1);

            run("processWorld");

            assertEquals(127, player.getDepth());
            assertTrue(bus.messages.contains("You are thrown back in an explosion!"));
            assertFalse(bus.messages.contains("The floor opens beneath you!"));
        }

        /**
         * How many commands are waiting in the game's command queue.
         *
         * @return the pending count
         */
        private int queuedCommands() {
            return ((java.util.Collection<?>) ItemFixture.read(GameState.getCommandQueue(), "commandQueue")).size();
        }

        /**
         * The update flags the world pass sets.
         *
         * @return the live flag set
         */
        @SuppressWarnings("unchecked")
        private Flag<PlayerUpdateEnum> updateFlags() {
            return (Flag<PlayerUpdateEnum>) ItemFixture.read(player.getPlayerUpkeep(), "updateFlags");
        }
    }

    /**
     * C's {@code recharge_objects} and {@code recharged_notice}.
     *
     * <p>The recharge arithmetic itself lives in {@code ItemObject} (its own test covers it), so
     * what is checked here is what {@code GameWorld} adds: which items are visited, which flags it
     * raises for each of the three groups, and when the player is told. The player's options and
     * the items' inscriptions decide whether a notice is spoken; the object name comes from
     * {@code ObjectUtils.objectDesc}, which is still a stub, so the wording is checked with the name
     * left as a wildcard.
     */
    @Nested
    @DisplayName("rechargeObjects and rechargedNotice")
    class Recharge {

        /**
         * A rod stack: ten turns to recharge each item, the given pooled timeout and stack size.
         *
         * @param timeout the pooled timeout
         * @param number  the stack size
         * @return the rod
         */
        private ItemObject rod(int timeout, int number) {
            ItemObject rod = ItemFixture.item(TValue.TV_ROD).kind(ItemFixture.kindWithDice(TValue.TV_ROD))
                    .number(number).timeout(timeout).build();
            ItemFixture.set(rod, "time", new Random(10, 0, 0, 0, false));
            return rod;
        }

        /**
         * An item of some other type with the same recharge figures.
         *
         * @param tValue  its type
         * @param timeout the timeout
         * @return the item
         */
        private ItemObject other(TValue tValue, int timeout) {
            ItemObject item = ItemFixture.item(tValue).kind(ItemFixture.kindWithDice(tValue))
                    .number(1).timeout(timeout).build();
            ItemFixture.set(item, "time", new Random(10, 0, 0, 0, false));
            return item;
        }

        /**
         * Carries an item in the pack.
         *
         * @param item the item
         * @return the item
         */
        private ItemObject carry(ItemObject item) {
            player.getGear().insertEnd(item);
            return item;
        }

        /**
         * Wears an item: it is both in the gear and in the first equipment slot.
         *
         * @param item the item
         * @return the item
         */
        private ItemObject wear(ItemObject item) {
            player.getGear().insertEnd(item);
            player.getPlayerBody().getSlots().get(0).setItem(item);
            return item;
        }

        /**
         * Lays an item on the level's floor.
         *
         * @param item the item
         * @return the item
         */
        @SuppressWarnings("unchecked")
        private ItemObject drop(ItemObject item) {
            ((List<ItemObject>) ItemFixture.read(level, "objects")).add(item);
            return item;
        }

        /**
         * Whether the combine notice is raised.
         *
         * @return the answer
         */
        private boolean combineRaised() {
            return player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_COMBINE);
        }

        /**
         * Switches {@code notify_recharge} on.
         *
         * @throws Exception if the option set cannot be reached
         */
        private void notifyOn() throws Exception {
            PlayerOptions opts = new PlayerOptions();
            @SuppressWarnings("unchecked")
            Flag<PlayerOptionEnum> flags = (Flag<PlayerOptionEnum>) field(PlayerOptions.class, "options").get(opts);
            flags.on(PlayerOptionEnum.OP_notify_recharge);
            field(Player.class, "options").set(player, opts);
        }

        /**
         * Calls {@code rechargedNotice}.
         *
         * @param item the item
         * @param all  whether the whole stack is ready
         * @throws Exception whatever it threw
         */
        private void notice(ItemObject item, boolean all) throws Exception {
            call(world, "rechargedNotice", new Class<?>[]{ItemObject.class, boolean.class}, item, all);
        }

        /**
         * A carried rod ticks down one turn per charging item, and a tick that does not finish an
         * item raises nothing: three charging rods at timeout 25 go to 22, still three charging.
         */
        @Test
        @DisplayName("a carried rod that has not finished raises nothing")
        void carriedRodTicksQuietly() {
            ItemObject rod = carry(rod(25, 3));

            world.rechargeObjects();

            assertEquals(22, rod.getTimeout());
            assertFalse(combineRaised());
            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_INVEN));
        }

        /**
         * When one rod of a stack comes ready the pack is flagged to be combined and redrawn, even
         * though the whole stack is not ready: two rods, timeout 11, go to 9, one still charging.
         */
        @Test
        @DisplayName("a rod that finishes asks for a combine and an inventory redraw")
        void finishedRodRaisesFlags() {
            ItemObject rod = carry(rod(11, 2));

            world.rechargeObjects();

            assertEquals(9, rod.getTimeout());
            assertTrue(combineRaised());
            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_INVEN));
            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_EQUIP));
        }

        /**
         * Only a rod is touched in the pack: any other type keeps its timeout, whatever its
         * recharge figures say.
         */
        @Test
        @DisplayName("a carried item that is not a rod is left alone")
        void carriedNonRodIsLeftAlone() {
            ItemObject wand = carry(other(TValue.TV_WAND, 1));

            world.rechargeObjects();

            assertEquals(1, wand.getTimeout());
            assertFalse(combineRaised());
        }

        /**
         * Worn items recharge with no type test: a worn sword with an activation timeout counts down
         * like a rod, and its ready raises the equipment redraw, not the pack's.
         */
        @Test
        @DisplayName("a worn item recharges whatever its type and raises the equipment redraw")
        void wornItemRecharges() {
            ItemObject sword = wear(other(TValue.TV_SWORD, 1));

            world.rechargeObjects();

            assertEquals(0, sword.getTimeout());
            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_EQUIP));
            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_INVEN));
            assertFalse(combineRaised());
        }

        /**
         * A worn item that has not finished ticks down and raises nothing.
         */
        @Test
        @DisplayName("a worn item that has not finished raises nothing")
        void wornItemTicksQuietly() {
            ItemObject sword = wear(other(TValue.TV_SWORD, 15));

            world.rechargeObjects();

            assertEquals(14, sword.getTimeout());
            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_EQUIP));
        }

        /**
         * An item with no kind is skipped: C asserts on it, and the port steps over it rather than
         * failing.
         */
        @Test
        @DisplayName("an item with no kind is skipped")
        void kindlessItemIsSkipped() {
            ItemObject ghost = ItemFixture.item(TValue.TV_ROD).kind(null).number(1).timeout(5).build();
            ItemFixture.set(ghost, "time", new Random(10, 0, 0, 0, false));
            carry(ghost);

            world.rechargeObjects();

            assertEquals(5, ghost.getTimeout());
        }

        /**
         * Rods on the floor tick down too, silently and without any flag, so a dropped rod is not
         * frozen; other floor objects are untouched.
         */
        @Test
        @DisplayName("rods on the floor tick down silently, other floor items do not")
        void floorRodsTick() throws Exception {
            notifyOn();
            ItemObject floorRod = drop(rod(11, 2));
            ItemObject floorWand = drop(other(TValue.TV_WAND, 7));

            world.rechargeObjects();

            assertEquals(9, floorRod.getTimeout());
            assertEquals(7, floorWand.getTimeout());
            assertTrue(bus.messages.isEmpty(), "nothing is announced for the floor");
            assertFalse(combineRaised());
        }

        /**
         * The player is told nothing by default: with the option off and no inscription the
         * notice returns before describing the item.
         */
        @Test
        @DisplayName("a notice is silent without the option or an inscription")
        void noticeSilentByDefault() throws Exception {
            notice(rod(0, 1), true);
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * The inscription test is a search for two exclamation marks in a row anywhere, so the
         * bare marks and a longer inscription containing them both ask to be told, while single or
         * separated marks do not.
         */
        @Test
        @DisplayName("an inscription containing !! asks for a notice; ! and !x! do not")
        void inscriptionRequestsNotice() throws Exception {
            ItemObject item = rod(0, 1);

            for (String note : new String[]{"!!", "@w1!!", "!!kill", "x!!y"}) {
                bus.messages.clear();
                item.setNote(note);
                notice(item, true);
                assertEquals(1, bus.messages.size(), note);
            }
            for (String note : new String[]{"!", "!x!", "no marks", ""}) {
                bus.messages.clear();
                item.setNote(note);
                notice(item, true);
                assertTrue(bus.messages.isEmpty(), note);
            }
        }

        /**
         * With {@code notify_recharge} on every recharge is announced, with no inscription at all.
         */
        @Test
        @DisplayName("the notify_recharge option announces every recharge")
        void optionAnnouncesEverything() throws Exception {
            notifyOn();

            notice(rod(0, 1), true);

            assertEquals(1, bus.messages.size());
        }

        /**
         * The four wordings: a stack that is all ready, a stack with only one ready, a single
         * ordinary item, and a single artifact.
         */
        @Test
        @DisplayName("the wording depends on stack size, readiness and artifact status")
        void wordingCases() throws Exception {
            notifyOn();

            notice(rod(0, 3), true);
            notice(rod(0, 3), false);
            notice(rod(0, 1), true);
            ItemObject artifact = rod(0, 1);
            ItemFixture.set(artifact, "artifact", artifactInstance());
            notice(artifact, true);

            assertEquals(4, bus.messages.size());
            assertTrue(bus.messages.get(0).matches("Your .*have recharged\\."), bus.messages.get(0));
            assertTrue(bus.messages.get(1).matches("One of your .*has recharged\\."), bus.messages.get(1));
            assertTrue(bus.messages.get(2).matches("Your .*has recharged\\."), bus.messages.get(2));
            assertTrue(bus.messages.get(3).matches("The .*has recharged\\."), bus.messages.get(3));
        }

        /**
         * A single item is "Your ...", never "One of your ...", even when {@code all} is false:
         * the stack-size test comes first.
         */
        @Test
        @DisplayName("a single item ignores the 'all' argument")
        void singleItemIgnoresAll() throws Exception {
            notifyOn();

            notice(rod(0, 1), false);

            assertTrue(bus.messages.get(0).matches("Your .*has recharged\\."), bus.messages.get(0));
        }

        /**
         * An artifact stack of more than one still uses the stack wording, as C tests the number
         * before the artifact.
         */
        @Test
        @DisplayName("a stack of artifacts uses the stack wording")
        void artifactStackUsesStackWording() throws Exception {
            notifyOn();
            ItemObject artifact = rod(0, 2);
            ItemFixture.set(artifact, "artifact", artifactInstance());

            notice(artifact, true);

            assertTrue(bus.messages.get(0).matches("Your .*have recharged\\."), bus.messages.get(0));
        }

        /**
         * A bare {@link Artifact}: the notice only asks whether the item has one.
         *
         * @return an instance with no state
         * @throws Exception if one cannot be allocated
         */
        private Artifact artifactInstance() throws Exception {
            Field unsafeField = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);
            return (Artifact) unsafe.getClass().getMethod("allocateInstance", Class.class)
                    .invoke(unsafe, Artifact.class);
        }
    }

    /**
     * C's {@code decrease_timeouts}: every running timed effect loses one turn, apart from the
     * special cases, and every active curse on worn equipment counts down.
     *
     * <p>The faster recovery rate is {@code adj_con_fix[stat_ind[STAT_CON]] + 1}, so the CON index
     * is the interesting input; the table is copied from {@code player-calcs.c}. The commanded
     * monster arm is not tested: it needs {@code MonsterUtils.getCommandMonster}, which walks the
     * whole monster array without skipping the empty slots, so it cannot run on a level that has
     * any.
     */
    @Nested
    @DisplayName("decreaseTimeouts")
    class DecreaseTimeouts {

        /**
         * C's {@code adj_con_fix[STAT_RANGE]}, copied from {@code player-calcs.c}.
         */
        private static final int[] ADJ_CON_FIX = {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 1, 1, 1,
                2, 2, 2, 2, 2,
                3, 3, 3, 3, 3,
                4, 4,
                5, 6, 6, 7, 7, 8, 8, 8, 9, 9, 9,
        };

        /**
         * Sets the CON stat index the recovery rate is read from.
         *
         * @param index the index into the stat tables
         */
        @SuppressWarnings("unchecked")
        private void conIndex(int index) {
            Map<Stats, Integer> map = (Map<Stats, Integer>) ItemFixture.read(player.getPlayerState(), "statInd");
            if (map == null) {
                map = new EnumMap<>(Stats.class);
                ItemFixture.set(player.getPlayerState(), "statInd", map);
            }
            map.put(Stats.STAT_CON, index);
        }

        /**
         * Turns a player flag on in the calculated state.
         *
         * @param flag the flag
         */
        @SuppressWarnings("unchecked")
        private void stateHas(PlayerFlag flag) {
            ((Flag<PlayerFlag>) ItemFixture.read(player.getPlayerState(), "pflags")).on(flag);
        }

        /**
         * An ordinary effect loses one turn, an effect that is not running is not touched.
         */
        @Test
        @DisplayName("an ordinary effect loses one turn")
        void ordinaryEffect() throws Exception {
            conIndex(0);
            player.putTimed(TimedEffect.TMD_HEAL, 5);

            run("decreaseTimeouts");

            assertEquals(4, player.getTimedEffect(TimedEffect.TMD_HEAL));
            assertEquals(0, player.getTimedEffect(TimedEffect.TMD_CUT));
        }

        /**
         * Hunger is aged by {@code process_world}, so it is decremented by zero here.
         */
        @Test
        @DisplayName("food is left for the world pass")
        void foodIsLeftAlone() throws Exception {
            conIndex(0);
            player.putTimed(TimedEffect.TMD_FOOD, 5000);

            run("decreaseTimeouts");

            assertEquals(5000, player.getTimedEffect(TimedEffect.TMD_FOOD));
        }

        /**
         * Poison, stun and cuts recover at {@code adj_con_fix[CON index] + 1}. Every index is
         * checked against the table copied from C, so a different edge of any band is caught.
         */
        @Test
        @DisplayName("poison, stun and cuts recover at adj_con_fix + 1 for every CON index")
        void conRecoveryRate() throws Exception {
            for (int index = 0; index < ADJ_CON_FIX.length; index++) {
                int rate = ADJ_CON_FIX[index] + 1;
                conIndex(index);
                player.putTimed(TimedEffect.TMD_POISONED, 100);
                player.putTimed(TimedEffect.TMD_STUN, 100);
                player.putTimed(TimedEffect.TMD_CUT, 100);

                run("decreaseTimeouts");

                assertEquals(100 - rate, player.getTimedEffect(TimedEffect.TMD_POISONED), "poison, index " + index);
                assertEquals(100 - rate, player.getTimedEffect(TimedEffect.TMD_STUN), "stun, index " + index);
                assertEquals(100 - rate, player.getTimedEffect(TimedEffect.TMD_CUT), "cut, index " + index);
            }
        }

        /**
         * A mortal wound does not heal by itself. The edge is the grade boundary: 1000 is still a
         * deep gash and heals, 1001 is a mortal wound and does not.
         */
        @Test
        @DisplayName("a mortal wound does not bleed down, a deep gash does")
        void mortalWound() throws Exception {
            conIndex(0);
            player.putTimed(TimedEffect.TMD_CUT, 1000);
            run("decreaseTimeouts");
            assertEquals(999, player.getTimedEffect(TimedEffect.TMD_CUT), "deep gash");

            player.putTimed(TimedEffect.TMD_CUT, 1001);
            run("decreaseTimeouts");
            assertEquals(1001, player.getTimedEffect(TimedEffect.TMD_CUT), "mortal wound");
        }

        /**
         * Rock players just maintain their cuts, but still recover from poison and stun.
         */
        @Test
        @DisplayName("a rock player does not bleed down, but recovers from poison")
        void rockPlayer() throws Exception {
            conIndex(0);
            stateHas(PlayerFlag.PF_ROCK);
            player.putTimed(TimedEffect.TMD_CUT, 100);
            player.putTimed(TimedEffect.TMD_POISONED, 100);

            run("decreaseTimeouts");

            assertEquals(100, player.getTimedEffect(TimedEffect.TMD_CUT));
            assertEquals(99, player.getTimedEffect(TimedEffect.TMD_POISONED));
        }

        /**
         * A curse with power counts its timeout down by one each pass without firing.
         */
        @Test
        @DisplayName("a curse's timeout counts down by one")
        void curseCountsDown() throws Exception {
            conIndex(0);
            Curse curse = curse();
            CurseData data = new CurseData(1, 3);
            wearCursed(curse, data);

            run("decreaseTimeouts");

            assertEquals(2, data.getTimeout());
        }

        /**
         * When the timeout reaches zero the curse fires, and the timeout is re-rolled from the
         * curse object's {@code time} dice: a flat 10 here.
         */
        @Test
        @DisplayName("a curse that reaches zero is re-rolled from its interval")
        void curseFiresAndRerolls() throws Exception {
            conIndex(0);
            Curse curse = curse();
            CurseData data = new CurseData(1, 1);
            wearCursed(curse, data);

            run("decreaseTimeouts");

            assertEquals(10, data.getTimeout());
        }

        /**
         * A curse with no power is dormant: its timeout is not touched.
         */
        @Test
        @DisplayName("a curse with no power is not counted down")
        void dormantCurse() throws Exception {
            conIndex(0);
            Curse curse = curse();
            CurseData data = new CurseData(0, 3);
            wearCursed(curse, data);

            run("decreaseTimeouts");

            assertEquals(3, data.getTimeout());
        }

        /**
         * A timeout already at zero goes to -1 and does not fire, because C tests for exactly
         * zero after the decrement; it is not re-rolled.
         */
        @Test
        @DisplayName("a timeout already at zero goes negative and does not fire")
        void zeroTimeoutGoesNegative() throws Exception {
            conIndex(0);
            Curse curse = curse();
            CurseData data = new CurseData(1, 0);
            wearCursed(curse, data);

            run("decreaseTimeouts");

            assertEquals(-1, data.getTimeout());
        }

        /**
         * A curse on an item that is only carried, not worn, is left alone: only the equipment
         * slots are walked.
         */
        @Test
        @DisplayName("a curse on a carried item is not counted down")
        void carriedItemIsIgnored() throws Exception {
            conIndex(0);
            Curse curse = curse();
            CurseData data = new CurseData(1, 3);
            ItemObject item = ItemFixture.item(TValue.TV_SWORD).kind(ItemFixture.kindWithDice(TValue.TV_SWORD))
                    .curse(curse, data).build();
            player.getGear().insertEnd(item);

            run("decreaseTimeouts");

            assertEquals(3, data.getTimeout());
        }

        /**
         * Every worn item is walked, not only the first slot.
         */
        @Test
        @DisplayName("curses in every slot are counted down")
        void everySlot() throws Exception {
            conIndex(0);
            Curse curse = curse();
            CurseData first = new CurseData(1, 3);
            CurseData last = new CurseData(1, 5);
            List<uk.co.jackoftradesltd.middle.player.EquipSlot> slots = player.getPlayerBody().getSlots();
            slots.get(0).setItem(ItemFixture.item(TValue.TV_SWORD).kind(ItemFixture.kindWithDice(TValue.TV_SWORD))
                    .curse(curse, first).build());
            slots.get(slots.size() - 1).setItem(ItemFixture.item(TValue.TV_BOOTS)
                    .kind(ItemFixture.kindWithDice(TValue.TV_BOOTS)).curse(curse, last).build());

            run("decreaseTimeouts");

            assertEquals(2, first.getTimeout());
            assertEquals(4, last.getTimeout());
        }

        /**
         * A curse whose object rolls a flat ten turns between firings.
         *
         * @return the curse
         */
        private Curse curse() {
            Curse curse = CurseFixture.curse("test curse", List.of(), 0, null, null, null, null,
                    0, 0, 0, List.of(), null, "a test curse", "", 0);
            ItemFixture.set(curse.getItemObject(), "time", new Random(10, 0, 0, 0, false));
            return curse;
        }

        /**
         * Puts a cursed sword in the first equipment slot.
         *
         * @param curse the curse
         * @param data  its per-object data
         */
        private void wearCursed(Curse curse, CurseData data) {
            ItemObject sword = ItemFixture.item(TValue.TV_SWORD).kind(ItemFixture.kindWithDice(TValue.TV_SWORD))
                    .curse(curse, data).build();
            player.getPlayerBody().getSlots().get(0).setItem(sword);
        }
    }

    /**
     * C's {@code make_noise} and {@code update_scent} on a 6 by 6 level.
     *
     * <p>The expected grids were produced by running C's two algorithms, transcribed from
     * {@code game-world.c} line for line, over the same layouts, rather than by reading the port's
     * answer back. {@code make_noise} gives each reachable grid its flood distance from the player
     * (a grid that is never reached stays 0, as does the player's own grid), and
     * {@code update_scent} first ages the interior and then stamps the 5 by 5 template around the
     * player where the adjacency rule lets it. That rule reads grids written earlier in the same
     * pass, so on a fresh level the top and left edges of the template are skipped; the matrices
     * show that quirk.
     *
     * <p>{@code Chunk.resetNoise} is still a stub, so each noise test starts from a level whose
     * noise map is freshly zero and runs one pass.
     */
    @Nested
    @DisplayName("makeNoise and updateScent")
    class NoiseAndScent {

        /**
         * Puts the player on the given grid.
         *
         * @param y the row
         * @param x the column
         */
        private void playerAt(int y, int x) {
            ItemFixture.set(player, "grid", Loc.row(y).col(x));
        }

        /**
         * A terrain feature with only the given flags.
         *
         * @param flags the terrain flags
         * @return the feature
         */
        private Feature terrain(TerrainFeatureFlags... flags) {
            Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
            for (TerrainFeatureFlags flag : flags) set.on(flag);
            return new Feature(TerrainFlags.FEAT_GRANITE, "test wall", "", null, 0, 0, set, null, "", "", "",
                    "", "", "", "", new Flag<>(uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag.class), 0);
        }

        /**
         * Gives a grid a terrain feature.
         *
         * @param y       the row
         * @param x       the column
         * @param feature the feature
         */
        private void terrainAt(int y, int x, Feature feature) {
            ItemFixture.set(level.getSquare(Loc.row(y).col(x)), "feat", feature);
        }

        /**
         * Reads a heat map into a matrix.
         *
         * @param map the map
         * @return its values, row by row
         */
        private int[][] read(uk.co.jackoftradesltd.middle.cave.Heatmap map) {
            int[][] grid = new int[6][6];
            for (int y = 0; y < 6; y++)
                for (int x = 0; x < 6; x++)
                    grid[y][x] = map.getValue(y, x);
            return grid;
        }

        /**
         * Asserts a heat map holds exactly the expected matrix.
         *
         * @param expected the matrix
         * @param map      the map
         */
        private void assertGrid(int[][] expected, uk.co.jackoftradesltd.middle.cave.Heatmap map) {
            int[][] actual = read(map);
            for (int y = 0; y < 6; y++)
                assertEquals(java.util.Arrays.toString(expected[y]), java.util.Arrays.toString(actual[y]), "row " + y);
        }

        /**
         * With nothing in the way the flood is the king-move distance from the player: one step in
         * any of the eight directions costs one, the player's own grid is 0, and the flood reaches
         * the border ring because only {@code TF_NO_FLOW} terrain stops it.
         */
        @Test
        @DisplayName("noise on an open level is the distance from the player")
        void openNoise() throws Exception {
            playerAt(2, 2);

            run("makeNoise");

            assertGrid(new int[][]{
                    {2, 2, 2, 2, 2, 3},
                    {2, 1, 1, 1, 2, 3},
                    {2, 1, 0, 1, 2, 3},
                    {2, 1, 1, 1, 2, 3},
                    {2, 2, 2, 2, 2, 3},
                    {3, 3, 3, 3, 3, 3},
            }, level.getNoise());
        }

        /**
         * A wall that does not carry sound blocks the flood: the wall's own grids stay 0, the far
         * side is reached only through the gap at the bottom, and it is further away than the
         * straight line would say. Grid (row 3, column 4) is three steps by way of (4, 3).
         */
        @Test
        @DisplayName("noise goes round a wall through the gap")
        void noiseThroughAGap() throws Exception {
            Feature wall = terrain(TerrainFeatureFlags.TF_NO_FLOW);
            for (int y = 0; y <= 3; y++) terrainAt(y, 3, wall);
            playerAt(2, 2);

            run("makeNoise");

            assertGrid(new int[][]{
                    {2, 2, 2, 0, 6, 6},
                    {2, 1, 1, 0, 5, 5},
                    {2, 1, 0, 0, 4, 4},
                    {2, 1, 1, 0, 3, 4},
                    {2, 2, 2, 2, 3, 4},
                    {3, 3, 3, 3, 3, 4},
            }, level.getNoise());
        }

        /**
         * A player who is covering their tracks makes noise in steps of four, so every distance is
         * four times as large and the monsters' hearing reaches a quarter as far.
         */
        @Test
        @DisplayName("covered tracks quadruple the noise steps")
        void coveredTracksNoise() throws Exception {
            player.putTimed(TimedEffect.TMD_COVERTRACKS, 5);
            playerAt(2, 2);

            run("makeNoise");

            assertGrid(new int[][]{
                    {8, 8, 8, 8, 8, 12},
                    {8, 4, 4, 4, 8, 12},
                    {8, 4, 0, 4, 8, 12},
                    {8, 4, 4, 4, 8, 12},
                    {8, 8, 8, 8, 8, 12},
                    {12, 12, 12, 12, 12, 12},
            }, level.getNoise());
        }

        /**
         * On a fresh level the first pass stamps the template wherever the adjacency rule allows:
         * the centre is always valid, the inner ring finds the empty grids at 0, but the outer ring
         * finds no neighbour holding a 1 yet at the top and left, so those edges stay empty.
         */
        @Test
        @DisplayName("the first scent pass on a fresh level skips the top and left of the template")
        void freshScent() throws Exception {
            playerAt(2, 2);

            run("updateScent");

            assertGrid(new int[][]{
                    {0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 2, 0},
                    {2, 1, 0, 1, 2, 0},
                    {2, 1, 1, 1, 2, 0},
                    {2, 2, 2, 2, 2, 0},
                    {0, 0, 0, 0, 0, 0},
            }, level.getScent());
        }

        /**
         * A grid whose terrain carries no scent is skipped, and the grids around it are laid as
         * usual: the wall at (row 2, column 3) stays 0 while its neighbours are stamped.
         */
        @Test
        @DisplayName("terrain that carries no scent is skipped")
        void noScentTerrain() throws Exception {
            terrainAt(2, 3, terrain(TerrainFeatureFlags.TF_NO_SCENT));
            playerAt(2, 2);

            run("updateScent");

            assertGrid(new int[][]{
                    {0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 2, 0},
                    {2, 1, 0, 0, 2, 0},
                    {2, 1, 1, 1, 2, 0},
                    {2, 2, 2, 2, 2, 0},
                    {0, 0, 0, 0, 0, 0},
            }, level.getScent());
        }

        /**
         * A second pass from the same grid ages the interior and then restamps the template, so
         * the picture is unchanged.
         */
        @Test
        @DisplayName("a second pass from the same grid restores the same picture")
        void secondPassSameGrid() throws Exception {
            playerAt(2, 2);

            run("updateScent");
            run("updateScent");

            assertGrid(new int[][]{
                    {0, 0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 2, 0},
                    {2, 1, 0, 1, 2, 0},
                    {2, 1, 1, 1, 2, 0},
                    {2, 2, 2, 2, 2, 0},
                    {0, 0, 0, 0, 0, 0},
            }, level.getScent());
        }

        /**
         * Moving leaves an aged trail: the old grid ages to a stale 0 or a larger value and the
         * new template is laid where the adjacency rule allows, including a stale 0 left where the
         * old centre was.
         */
        @Test
        @DisplayName("moving leaves an aged trail behind")
        void movingLeavesATrail() throws Exception {
            playerAt(2, 2);
            run("updateScent");
            playerAt(3, 3);
            run("updateScent");

            assertGrid(new int[][]{
                    {0, 0, 0, 0, 0, 0},
                    {0, 2, 2, 2, 3, 0},
                    {2, 2, 0, 1, 1, 2},
                    {2, 2, 1, 0, 1, 2},
                    {2, 2, 1, 1, 1, 2},
                    {0, 2, 2, 2, 2, 2},
            }, level.getScent());
        }

        /**
         * A player covering their tracks lays nothing new, but the existing scent still ages, and
         * only in the interior: the border is not aged, and a grid with no scent stays at 0.
         */
        @Test
        @DisplayName("covered tracks lay no scent but old scent still ages, inside the border only")
        void coveredTracksScent() throws Exception {
            player.putTimed(TimedEffect.TMD_COVERTRACKS, 5);
            playerAt(2, 2);
            level.getScent().setValue(1, 1, 2);
            level.getScent().setValue(3, 3, 5);
            level.getScent().setValue(0, 0, 4);

            run("updateScent");

            assertGrid(new int[][]{
                    {4, 0, 0, 0, 0, 0},
                    {0, 3, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 6, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
            }, level.getScent());
        }
    }

    /**
     * C's {@code on_new_level} and {@code on_leave_level}.
     */
    @Nested
    @DisplayName("onNewLevel and onLeaveLevel")
    class LevelChange {

        /**
         * Arriving signals the flush, the new-level display and the refresh, in that order.
         */
        @Test
        @DisplayName("arrival signals flush, new-level display, refresh in order")
        void arrivalSignals() throws Exception {
            player.setDepth(3);

            run("onNewLevel");

            assertEquals(List.of(GameEventType.EVENT_MESSAGE_FLUSH, GameEventType.EVENT_NEW_LEVEL_DISPLAY,
                            GameEventType.EVENT_REFRESH),
                    eventsOf(GameEventType.EVENT_MESSAGE_FLUSH, GameEventType.EVENT_NEW_LEVEL_DISPLAY,
                            GameEventType.EVENT_REFRESH));
        }

        /**
         * A player arriving with less than a move's worth of energy is topped up to exactly that,
         * one with more keeps it (a savefile for a level in progress), and the boundary value is
         * unchanged.
         */
        @Test
        @DisplayName("arrival raises energy to move_energy but never lowers it")
        void energyFloor() throws Exception {
            player.setDepth(3);
            int[][] cases = {{0, 100}, {99, 100}, {100, 100}, {101, 101}, {250, 250}};
            for (int[] c : cases) {
                player.setEnergy(c[0]);

                run("onNewLevel");

                assertEquals(c[1], player.getEnergy(), "started at " + c[0]);
            }
        }

        /**
         * The floor applies in the town as well as the dungeon: only the level feeling is
         * conditional on depth.
         */
        @Test
        @DisplayName("the energy floor applies in town too")
        void energyFloorInTown() throws Exception {
            player.setDepth(0);
            player.setEnergy(10);

            run("onNewLevel");

            assertEquals(100, player.getEnergy());
        }

        /**
         * An arena is not a real level change: the function returns before the energy floor, so a
         * player with none arrives with none.
         */
        @Test
        @DisplayName("an arena arrival does not raise energy")
        void arenaKeepsEnergy() throws Exception {
            player.setDepth(3);
            player.getPlayerUpkeep().setArenaLevel(true);
            player.setEnergy(10);

            run("onNewLevel");

            assertEquals(10, player.getEnergy());
        }

        /**
         * A real arrival plays the ambient sound; an arena arrival does not.
         */
        @Test
        @DisplayName("the ambient sound plays on a real arrival only")
        void ambientSoundOnRealArrival() throws Exception {
            soundOn();
            player.setDepth(3);

            run("onNewLevel");
            assertEquals(List.of(MessageType.MSG_AMBIENT_DNG1), bus.sounds);

            bus.sounds.clear();
            player.getPlayerUpkeep().setArenaLevel(true);
            run("onNewLevel");
            assertTrue(bus.sounds.isEmpty());
        }

        /**
         * The arena still tracks the maximum level and flushes and refreshes the display; only the
         * sound, target, health bar, feeling, search and energy are skipped.
         */
        @Test
        @DisplayName("an arena arrival still signals the display and tracks the maximum level")
        void arenaStillSignals() throws Exception {
            player.setDepth(3);
            player.getPlayerUpkeep().setArenaLevel(true);
            ItemFixture.set(player, "level", 9);
            ItemFixture.set(player, "maxLevel", 4);

            run("onNewLevel");

            assertEquals(9, ItemFixture.read(player, "maxLevel"));
            assertEquals(1, bus.count(GameEventType.EVENT_NEW_LEVEL_DISPLAY));
        }

        /**
         * The maximum level follows a rise in level but not a fall.
         */
        @Test
        @DisplayName("the maximum level is raised, never lowered")
        void maxLevelTracking() throws Exception {
            player.setDepth(3);
            ItemFixture.set(player, "level", 9);
            ItemFixture.set(player, "maxLevel", 4);
            run("onNewLevel");
            assertEquals(9, ItemFixture.read(player, "maxLevel"));

            ItemFixture.set(player, "level", 2);
            run("onNewLevel");
            assertEquals(9, ItemFixture.read(player, "maxLevel"));
        }

        /**
         * A new deepest level moves the recall depth with it; a shallower arrival leaves both
         * alone, so recall still takes the player to the deepest point.
         */
        @Test
        @DisplayName("a new deepest depth moves the recall depth too")
        void maxDepthTracking() throws Exception {
            ItemFixture.set(player, "maxDepth", 5);
            ItemFixture.set(player, "recallDepth", 1);

            player.setDepth(8);
            run("onNewLevel");
            assertEquals(8, player.getMaxDepth());
            assertEquals(8, player.getRecallDepth());

            player.setDepth(2);
            run("onNewLevel");
            assertEquals(8, player.getMaxDepth());
            assertEquals(8, player.getRecallDepth());
        }

        /**
         * Arrival at exactly the current maximum is not a new deepest level: the recall depth is
         * left as it was.
         */
        @Test
        @DisplayName("arriving at exactly the maximum depth leaves the recall depth alone")
        void equalDepthLeavesRecall() throws Exception {
            ItemFixture.set(player, "maxDepth", 5);
            ItemFixture.set(player, "recallDepth", 3);
            player.setDepth(5);

            run("onNewLevel");

            assertEquals(5, player.getMaxDepth());
            assertEquals(3, player.getRecallDepth());
        }

        /**
         * Leaving a level flushes the messages, once.
         */
        @Test
        @DisplayName("leaving a level flushes the messages once")
        void leaveFlushes() throws Exception {
            run("onLeaveLevel");

            assertEquals(1, bus.count(GameEventType.EVENT_MESSAGE_FLUSH));
        }
    }

    /**
     * C's {@code process_player}.
     */
    @Nested
    @DisplayName("processPlayer")
    class ProcessPlayer {

        /**
         * A character who is playing: a new {@code Player} is not, and the loop stops short of
         * the cleanup for one who is not.
         */
        @BeforeEach
        void playing() {
            player.getPlayerUpkeep().setPlaying(true);
        }

        /**
         * The check-for-interrupts event is signalled once, before the loop, however many times
         * the loop turns.
         */
        @Test
        @DisplayName("the interrupt check is signalled once")
        void interruptCheckOnce() throws Exception {
            scripted(0, 0);

            run("processPlayer");

            assertEquals(1, bus.count(GameEventType.EVENT_CHECK_INTERRUPT));
        }

        /**
         * Each pass assumes a free turn first, so energy left over from the last command is
         * cleared when the queue has nothing to give.
         */
        @Test
        @DisplayName("each pass starts by assuming a free turn")
        void assumesFreeTurn() throws Exception {
            scripted(0, 0);
            player.getPlayerUpkeep().setEnergyUse(40);

            run("processPlayer");

            assertEquals(0, player.getPlayerUpkeep().getEnergyUse());
        }

        /**
         * A command that spends energy is followed by the cleanup, which takes the energy off the
         * player and ends the loop.
         */
        @Test
        @DisplayName("a command that spends energy ends the loop after the cleanup")
        void energySpendingCommand() throws Exception {
            scripted(5, 0);
            player.setEnergy(100);

            run("processPlayer");

            assertEquals(0, player.getEnergy());
            assertEquals(100, player.getPlayerUpkeep().getEnergyUse());
        }

        /**
         * If the command stops the game the cleanup is skipped, so the energy is not taken off.
         */
        @Test
        @DisplayName("a command that stops the game skips the cleanup")
        void stoppedGameSkipsCleanup() throws Exception {
            scripted(5, 0);
            player.setEnergy(100);
            player.getPlayerUpkeep().setPlaying(false);

            run("processPlayer");

            assertEquals(100, player.getEnergy());
        }

        /**
         * A paralysed player gets {@code CMD_SLEEP} pushed on the queue; a healthy one does not.
         */
        @Test
        @DisplayName("paralysis queues a sleep command")
        void paralysisQueuesSleep() throws Exception {
            scripted(0, 0);
            run("processPlayer");
            assertEquals(0, queued());

            player.putTimed(TimedEffect.TMD_PARALYZED, 3);
            run("processPlayer");
            assertEquals(1, queued());
        }

        /**
         * Stun puts the player out only at the "Knocked Out" grade: 150 is still "Heavy Stun",
         * 151 is knocked out.
         */
        @Test
        @DisplayName("only the Knocked Out stun grade queues a sleep command")
        void knockedOutQueuesSleep() throws Exception {
            scripted(0, 0);

            player.putTimed(TimedEffect.TMD_STUN, 150);
            run("processPlayer");
            assertEquals(0, queued(), "heavy stun");

            player.putTimed(TimedEffect.TMD_STUN, 151);
            run("processPlayer");
            assertEquals(1, queued(), "knocked out");
        }

        /**
         * While a command is repeating, the repeat event is signalled instead of the plain refresh.
         */
        @Test
        @DisplayName("a repeating command signals the repeat event, not a refresh")
        void repeatSignalsRepeat() throws Exception {
            scripted(0, 3);
            bus.types.clear();

            run("processPlayer");

            assertEquals(1, bus.count(GameEventType.EVENT_COMMAND_REPEAT));
        }

        /**
         * When nothing is repeating a tracked monster's recall is flagged for redraw.
         */
        @Test
        @DisplayName("a tracked monster flags the recall for redraw when nothing repeats")
        void monsterRecallRedraw() throws Exception {
            scripted(0, 0);
            ItemFixture.set(player.getPlayerUpkeep(), "monsterRace", race());

            run("processPlayer");

            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_MONSTER));
        }

        /**
         * While a command is repeating the recall is not flagged, because the repeat branch
         * replaces the whole block.
         */
        @Test
        @DisplayName("a repeating command does not flag the recall")
        void repeatSkipsRecall() throws Exception {
            scripted(0, 2);
            ItemFixture.set(player.getPlayerUpkeep(), "monsterRace", race());

            run("processPlayer");

            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_MONSTER));
        }

        /**
         * How many commands are waiting in the game's queue.
         *
         * @return the pending count
         */
        private int queued() {
            return ((java.util.Collection<?>) ItemFixture.read(GameState.getCommandQueue(), "commandQueue")).size();
        }
    }

    /**
     * C's {@code run_game_loop}: scheduling only, driven by a scripted queue and the real energy
     * arithmetic. The monster turn and level generation are stubs, so what is checked is when
     * the clock moves and when the loop hands control back.
     */
    @Nested
    @DisplayName("runGameLoop")
    class RunGameLoop {

        /**
         * A resting character on level 3, with a scripted queue giving one command.
         */
        @BeforeEach
        void ready() throws Exception {
            player.setDepth(3);
            player.getPlayerUpkeep().setRestingCounter(5);
            player.getPlayerUpkeep().setPlaying(true);
            ItemFixture.set(player.getPlayerState(), "speed", 110);
        }

        /**
         * A player who still has a full move of energy after the command takes the next turn
         * straight away, so the clock does not move.
         */
        @Test
        @DisplayName("a player with energy to spare acts again without the clock moving")
        void energyToSpare() throws Exception {
            scripted(1, 0);
            player.setEnergy(250);
            GameState.setTurn(500);

            world.runGameLoop();

            assertEquals(150, player.getEnergy());
            assertEquals(500, GameState.getTurn());
        }

        /**
         * With nothing left after the command the world runs until the player has a move again:
         * at the normal 10 energy a turn that is ten turns, and the player ends on exactly 100.
         */
        @Test
        @DisplayName("the clock runs until the player has banked a move again")
        void clockRunsUntilEnergyBanked() throws Exception {
            scripted(1, 0);
            player.setEnergy(100);
            GameState.setTurn(21);

            world.runGameLoop();

            assertEquals(100, player.getEnergy());
            assertEquals(31, GameState.getTurn());
        }

        /**
         * A faster player banks energy faster: at +10 speed it is 20 a turn, so half as many turns.
         */
        @Test
        @DisplayName("a faster player waits fewer turns")
        void fasterPlayerWaitsLess() throws Exception {
            ItemFixture.set(player.getPlayerState(), "speed", 120);
            scripted(1, 0);
            player.setEnergy(100);
            GameState.setTurn(21);

            world.runGameLoop();

            assertEquals(26, GameState.getTurn());
            assertEquals(100, player.getEnergy());
        }

        /**
         * The leftover energy shortens the wait: 150 less a command's 100 is 50, so five turns of
         * ten bring the player back to a full move.
         */
        @Test
        @DisplayName("leftover energy shortens the wait")
        void leftoverShortensWait() throws Exception {
            scripted(1, 0);
            player.setEnergy(150);
            GameState.setTurn(21);

            world.runGameLoop();

            assertEquals(26, GameState.getTurn());
            assertEquals(100, player.getEnergy());
        }

        /**
         * The world is processed on turns that are a multiple of ten, before the counter moves. From
         * turn 9995, ten turns pass, crossing 10000, where a dungeon day is banked: exactly one.
         */
        @Test
        @DisplayName("the world is processed on multiples of ten turns")
        void worldProcessedOnTens() throws Exception {
            scripted(1, 0);
            player.setEnergy(100);
            GameState.setTurn(9995);

            world.runGameLoop();

            assertEquals(10005, GameState.getTurn());
            assertEquals(1, GameWorld.getDaycount());
        }

        /**
         * Starting on a multiple of ten the world is processed at the start of the first of the
         * ten turns, and again only when the clock reaches the next multiple, which is the end of
         * the wait: turn 10000 banks a day, and the clock stops at 10010 before turn 10010 is
         * processed.
         */
        @Test
        @DisplayName("a multiple of ten at the start is processed, the one at the end is not")
        void startIsProcessedEndIsNot() throws Exception {
            scripted(1, 0);
            player.setEnergy(100);
            GameState.setTurn(10000);

            world.runGameLoop();

            assertEquals(10010, GameState.getTurn());
            assertEquals(1, GameWorld.getDaycount());
        }

        /**
         * When the game is not being played the loop returns at the first check with the clock
         * untouched, and signals one refresh.
         */
        @Test
        @DisplayName("a stopped game returns without moving the clock")
        void stoppedGame() throws Exception {
            scripted(0, 0);
            player.getPlayerUpkeep().setPlaying(false);
            player.setEnergy(0);
            GameState.setTurn(21);
            bus.types.clear();

            world.runGameLoop();

            assertEquals(21, GameState.getTurn());
            assertEquals(1, bus.count(GameEventType.EVENT_REFRESH));
        }

        /**
         * When the first player pass spends no energy there is nothing to schedule: the loop
         * hands control straight back, leaving the clock and energy as they were.
         */
        @Test
        @DisplayName("a pass that spends no energy hands control straight back")
        void noEnergyHandsBack() throws Exception {
            scripted(0, 0);
            player.setEnergy(60);
            GameState.setTurn(21);

            world.runGameLoop();

            assertEquals(21, GameState.getTurn());
            assertEquals(60, player.getEnergy());
        }

        /**
         * A requested new level does not advance the clock: the level is made, the request is
         * cleared and the player, given a full move on arrival, acts at once.
         */
        @Test
        @DisplayName("a level change does not advance the clock")
        void levelChangeKeepsClock() throws Exception {
            scripted(1, 0);
            player.setEnergy(100);
            player.getPlayerUpkeep().setGenerateLevel(true);
            GameState.setTurn(21);

            world.runGameLoop();

            assertFalse(player.getPlayerUpkeep().generateLevel());
            assertEquals(21, GameState.getTurn());
            assertEquals(100, player.getEnergy());
            assertEquals(1, bus.count(GameEventType.EVENT_NEW_LEVEL_DISPLAY));
        }

        /**
         * The old level is left only when there is one: with {@code character_dungeon} set, leaving
         * flushes the messages once more than arriving alone does.
         */
        @Test
        @DisplayName("the old level is left only when the character has one")
        void leavesOldLevelOnlyIfThereIsOne() throws Exception {
            scripted(1, 0);
            player.setEnergy(100);
            player.getPlayerUpkeep().setGenerateLevel(true);
            GameWorld.setCharacterDungeon(false);
            world.runGameLoop();
            assertEquals(1, bus.count(GameEventType.EVENT_MESSAGE_FLUSH), "no level to leave");

            bus.types.clear();
            scripted(1, 0);
            player.setEnergy(100);
            player.getPlayerUpkeep().setGenerateLevel(true);
            GameWorld.setCharacterDungeon(true);
            world.runGameLoop();
            assertEquals(2, bus.count(GameEventType.EVENT_MESSAGE_FLUSH), "leave and arrive");
        }

        /**
         * Leaving the arena is not a level change in the usual sense: the arrival does not top up
         * the player's energy, so they have to bank a move before they can act, and the arena flag
         * is cleared afterwards. From no energy that is ten turns of ten.
         */
        @Test
        @DisplayName("leaving the arena clears the arena flag and gives no free energy")
        void leavingTheArena() throws Exception {
            Chunk arena = new Chunk("arena", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
            GameState.setCave(arena);
            arena.setCurrentLevel(arena);
            GameWorld arenaWorld = new GameWorld();
            GameWorld.setCharacterDungeon(true);
            scripted(1, 0);
            player.setEnergy(100);
            player.getPlayerUpkeep().setArenaLevel(true);
            player.getPlayerUpkeep().setGenerateLevel(true);
            GameState.setTurn(21);

            arenaWorld.runGameLoop();

            assertFalse(player.getPlayerUpkeep().isArenaLevel());
            assertEquals(31, GameState.getTurn());
            assertEquals(100, player.getEnergy());
        }

        /**
         * A level that is not the arena does not clear the arena flag.
         */
        @Test
        @DisplayName("a level that is not the arena leaves the arena flag alone")
        void notTheArena() throws Exception {
            scripted(1, 0);
            player.setEnergy(100);
            GameWorld.setCharacterDungeon(true);
            player.getPlayerUpkeep().setArenaLevel(true);
            player.getPlayerUpkeep().setGenerateLevel(true);
            GameState.setTurn(21);

            world.runGameLoop();

            assertTrue(player.getPlayerUpkeep().isArenaLevel());
        }
    }
}
