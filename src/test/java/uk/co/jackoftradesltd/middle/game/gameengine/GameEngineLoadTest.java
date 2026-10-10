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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.enums.CoreLifecycleEvent;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.enums.UILifecycleEvent;
import uk.co.jackoftradesltd.channel.messages.CoreMessage;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.messages.data.EventDataString;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsBusHandler;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.DungeonRegistry;
import uk.co.jackoftradesltd.middle.player.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link GameEngine}'s own contract: the two-step start-up, the event bus it owns, and the order in
 * which {@link GameEngine#loadGameConstants(Core)} runs what C's {@code init_angband()}
 * ({@code src/init.c}) runs.
 *
 * <p>Complements {@code GameEngineBootstrapTest}, which asserts that the load happened and the
 * registries came back non-empty. This class pins the things that test cannot see, each derived
 * from C rather than from the port:
 * <ul>
 *   <li>{@code init_angband()} signals {@code EVENT_ENTER_INIT} before anything else, so it must be
 *       the first event on the bus;</li>
 *   <li>{@code modules[]} runs {@code arrays_module}, then {@code player_module}, then
 *       {@code generate_module}, so the player exists by the time the three
 *       {@code run_template_parser()} progress messages are signalled, and those messages are the
 *       last three of the load, in the order dungeon profiles, room templates, vaults;</li>
 *   <li>{@code parse_vault_max_depth()} ({@code generate.c}) turns {@code max-depth:0} into
 *       {@code z_info->max_depth}, which {@code world.txt} makes 128, so no vault ends up with a
 *       maximum level of zero;</li>
 *   <li>{@code init_player()} ({@code player.c}) allocates a fresh player each time it runs, so a
 *       second load replaces the first rather than reusing it.</li>
 * </ul>
 *
 * <p>The stop path, a Java-only addition with no C counterpart, is pinned against the contract the
 * Javadoc states: a stop request read during the load abandons it before the player, level or
 * command queue are touched.
 *
 * <p><b>It disturbs every global it touches</b> - the singleton, the constants, the bus, and the
 * player, level and command queue in {@link GameState} - so all of them are saved up front and put
 * back afterwards.
 *
 * <p>Class GameEngineLoadTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 120, unit = TimeUnit.SECONDS)
class GameEngineLoadTest {

    /**
     * Defaults for every switch; the load reads none of them.
     */
    private static final StartupOptions NO_OPTIONS =
            new StartupOptions(false, false, false, false, "", "", List.of());

    /**
     * One recorded event: its type, the string it carried (null for payload-free events), and
     * whether {@link GameState} held a player the load had built at the moment it arrived.
     *
     * @param type          the event type
     * @param text          the {@code EventDataString} payload, or null
     * @param playerBuilt   true if the player differed from the one in place before the load
     */
    private record Seen(GameEventType type, String text, boolean playerBuilt) {
    }

    private static Object savedInstance;
    private static Object savedConstants;
    private static EventsHandler savedBus;
    private static Player savedPlayer;
    private static Chunk savedCave;
    private static CommandQueue savedQueue;

    /**
     * Events seen during the first full load, in the order they arrived.
     */
    private static final List<Seen> seen = new ArrayList<>();

    /**
     * The player the first full load built, and the one that was in place before it.
     */
    private static Player firstLoadPlayer;
    private static Player playerBeforeLoad;

    /**
     * Saves the globals, then performs one full load with a recorder on the bus. The recorder is
     * registered after {@code getGame()} - which replaces the bus - and before the load, in the
     * window the engine's two-step start-up exists to provide.
     */
    @BeforeAll
    static void loadOnceWithRecorder() throws Exception {
        savedInstance = field(GameEngine.class, "instance").get(null);
        savedConstants = field(GameConstants.class, "data").get(null);
        savedBus = GameEngine.getEventsBusHandler();
        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();
        savedQueue = GameState.getCommandQueue();

        field(GameEngine.class, "instance").set(null, null);
        playerBeforeLoad = savedPlayer;
        GameEngine engine = GameEngine.getGame();

        EventsHandler bus = GameEngine.getEventsBusHandler();
        List<GameEventType> watched = List.of(GameEventType.EVENT_ENTER_INIT, GameEventType.EVENT_INITSTATUS);
        EventHandlerInterface recorder = (type, data) -> seen.add(new Seen(type,
                data instanceof EventDataString s ? s.string() : null,
                GameState.getPlayer() != playerBeforeLoad));
        bus.eventAddHandlerSet(watched, recorder);

        boolean stopped;
        try {
            stopped = engine.loadGameConstants(coreWithEntriesLoaded());
        } finally {
            // Later tests load again on the same bus; only the first load is recorded.
            bus.eventRemoveHandlerSet(watched, recorder);
        }
        assertFalse(stopped, "the first load runs to the end");
        firstLoadPlayer = GameState.getPlayer();
    }

    /**
     * Puts every global back, including the singleton, so a later test builds its own engine.
     */
    @AfterAll
    static void restoreGlobals() throws Exception {
        field(GameEngine.class, "instance").set(null, savedInstance);
        field(GameConstants.class, "data").set(null, savedConstants);
        GameEngine.setEventsBusHandler(savedBus);
        GameState.setPlayer(savedPlayer);
        GameState.setCave(savedCave);
        GameState.setCommandQueue(savedQueue);
    }

    private static Field field(Class<?> type, String name) throws Exception {
        Field f = type.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * Mirrors the front end's side of the handshake: loads the UI entries and pre-queues the
     * {@code UIEntriesLoaded} message the load blocks on part-way through.
     */
    private static Core coreWithEntriesLoaded() throws Exception {
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> specs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            specs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }
        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(specs));
        return new Core(channels.coreChannel(), NO_OPTIONS);
    }

    /**
     * {@code init_angband()} begins with {@code event_signal(EVENT_ENTER_INIT)}, so that event is
     * the first thing any handler registered before the load sees. This is the whole reason the
     * bus is built before the load and handlers are registered in between.
     */
    @Test
    void enterInitIsTheFirstEventOfTheLoad() {
        assertFalse(seen.isEmpty(), "the recorder, registered before the load, saw events");
        assertEquals(GameEventType.EVENT_ENTER_INIT, seen.get(0).type());
        assertEquals(1, seen.stream().filter(s -> s.type() == GameEventType.EVENT_ENTER_INIT).count(),
                "and it is signalled once, not once per module");
    }

    /**
     * {@code generate_module}'s {@code run_template_parser()} signals these three strings, in this
     * order, and being the last module of the three this method ports, they close the load.
     */
    @Test
    void templateParsersAnnounceThemselvesLastAndInOrder() {
        List<String> status = seen.stream()
                .filter(s -> s.type() == GameEventType.EVENT_INITSTATUS)
                .map(Seen::text)
                .toList();

        assertTrue(status.size() >= 3);
        assertEquals(List.of(
                        "Initializing arrays... (dungeon profiles)",
                        "Initializing arrays... (room templates)",
                        "Initializing arrays... (vaults)"),
                status.subList(status.size() - 3, status.size()));
    }

    /**
     * {@code modules[]} puts {@code player_module} after {@code arrays_module} and before
     * {@code generate_module}: every progress message from the data files (arrays) is signalled
     * with the old player still in place, and every one of the three template messages after
     * {@code init_player()} has replaced it.
     */
    @Test
    void playerIsBuiltAfterTheArraysAndBeforeTheTemplates() {
        List<Seen> status = seen.stream()
                .filter(s -> s.type() == GameEventType.EVENT_INITSTATUS)
                .toList();
        int templates = status.size() - 3;

        for (int i = 0; i < templates; i++) {
            assertFalse(status.get(i).playerBuilt(),
                    "arrays_module message '" + status.get(i).text() + "' precedes init_player()");
        }
        for (int i = templates; i < status.size(); i++) {
            assertTrue(status.get(i).playerBuilt(),
                    "generate_module message '" + status.get(i).text() + "' follows init_player()");
        }
    }

    /**
     * The three things the load leaves standing: a player, the stub level (which points at itself
     * as the current level), and a command queue. The queue is port-only; C's is file-scope in
     * {@code cmd-core.c}.
     */
    @Test
    void loadLeavesPlayerLevelAndQueue() {
        assertNotNull(GameState.getPlayer());
        assertNotSame(playerBeforeLoad, GameState.getPlayer(), "init_player() allocated a new one");
        assertNotNull(GameState.getCave());
        assertSame(GameState.getCave(), GameState.getCave().getCurrentLevel());
        assertNotNull(GameState.getCommandQueue());
    }

    /**
     * {@code init_player()} zero-allocates its timed-effect table ({@code mem_zalloc}), so a fresh
     * player has every timed effect at 0 - none of the game's start-up leaks a countdown onto it.
     */
    @Test
    void freshPlayerHasNoTimedEffects() {
        for (var effect : uk.co.jackoftradesltd.middle.player.enums.TimedEffect.values()) {
            assertEquals(0, GameState.getPlayer().getTimedEffect(effect), effect.name());
        }
    }

    /**
     * {@code parse_vault_max_depth()} writes {@code z_info->max_depth} where the file says
     * {@code max-depth:0}. {@code vault.txt} says that 129 times, and {@code world.txt} makes the
     * depth 128, so at least 129 vaults must carry 128 and none may carry 0.
     */
    @Test
    @SuppressWarnings("unchecked")
    void vaultsWithNoMaximumGetTheWorldDepth() throws Exception {
        List<Vault> vaults = (List<Vault>) field(DungeonRegistry.class, "vaultTemplates").get(null);

        assertNotNull(vaults);
        assertEquals(128, GameConstants.getWorldMaxDepth());
        assertTrue(vaults.stream().noneMatch(v -> v.getMaxLevel() == 0), "no vault has max level 0");
        assertTrue(vaults.stream().filter(v -> v.getMaxLevel() == 128).count() >= 129,
                "every max-depth:0 became 128");
    }

    /**
     * Room templates and dungeon profiles are both filled by the same load, and the profile count
     * the engine records ({@code z_info->profile_max}) equals the number of profiles.
     */
    @Test
    @SuppressWarnings("unchecked")
    void templateRegistriesAreFilled() throws Exception {
        List<Object> profiles = (List<Object>) field(DungeonRegistry.class, "caveProfiles").get(null);
        List<Object> rooms = (List<Object>) field(DungeonRegistry.class, "roomTemplates").get(null);

        assertNotNull(profiles);
        assertFalse(profiles.isEmpty());
        assertEquals(profiles.size(), GameConstants.getCaveProfileMax());
        assertNotNull(rooms);
        assertFalse(rooms.isEmpty());
    }

    /**
     * A second load on the same engine builds a new player, level and queue and discards the first
     * set, as {@code init_player()} allocates afresh every time it runs.
     */
    @Test
    void secondLoadReplacesPlayerLevelAndQueue() throws Exception {
        Chunk caveBefore = GameState.getCave();
        CommandQueue queueBefore = GameState.getCommandQueue();
        Player playerBefore = GameState.getPlayer();

        boolean stopped = GameEngine.getGame().loadGameConstants(coreWithEntriesLoaded());

        assertFalse(stopped);
        assertNotSame(playerBefore, GameState.getPlayer());
        assertNotSame(caveBefore, GameState.getCave());
        assertNotSame(queueBefore, GameState.getCommandQueue());
    }

    /**
     * A stop request waiting in the core's inbox is read by the load's wait for
     * {@code UIEntriesLoaded}; the load is abandoned, {@code true} comes back, {@code STOPPED} has
     * been sent, and the player, level and queue are exactly as they were.
     */
    @Test
    void stopRequestAbandonsTheLoadBeforeAnythingIsBuilt() throws Exception {
        Player playerBefore = GameState.getPlayer();
        Chunk caveBefore = GameState.getCave();
        CommandQueue queueBefore = GameState.getCommandQueue();

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.LifecycleUIMessage(UILifecycleEvent.SAVE_AND_STOP));
        Core core = new Core(channels.coreChannel(), NO_OPTIONS);

        boolean stopped = GameEngine.getGame().loadGameConstants(core);

        assertTrue(stopped, "the caller is told to end the thread");
        assertSame(playerBefore, GameState.getPlayer());
        assertSame(caveBefore, GameState.getCave());
        assertSame(queueBefore, GameState.getCommandQueue());
        assertEquals(new CoreMessage.LifecycleCoreMessage(CoreLifecycleEvent.STOPPED),
                channels.uiChannel().uiReceiver().receive(),
                "STOPPED was sent before the load returned");
    }

    /**
     * The bus is never null, even before an engine exists, and a bus installed through the setter
     * is the one the getter returns.
     */
    @Test
    void busRoundTripsThroughTheSetter() {
        EventsHandler previous = GameEngine.getEventsBusHandler();
        try {
            assertNotNull(previous);
            EventsHandler mine = new EventsBusHandler();
            GameEngine.setEventsBusHandler(mine);
            assertSame(mine, GameEngine.getEventsBusHandler());
        } finally {
            GameEngine.setEventsBusHandler(previous);
        }
    }

    /**
     * Building a new engine replaces whatever bus was installed, so handlers from before are gone;
     * the singleton, once built, hands back the same engine and leaves the bus alone.
     */
    @Test
    void buildingAnEngineInstallsAFreshBus() throws Exception {
        Object keep = field(GameEngine.class, "instance").get(null);
        EventsHandler previous = GameEngine.getEventsBusHandler();
        try {
            EventsHandler mine = new EventsBusHandler();
            GameEngine.setEventsBusHandler(mine);

            GameEngine existing = GameEngine.getGame();
            assertSame(mine, GameEngine.getEventsBusHandler(), "an existing engine does not touch the bus");

            field(GameEngine.class, "instance").set(null, null);
            GameEngine built = GameEngine.getGame();

            assertNotSame(existing, built);
            assertNotSame(mine, GameEngine.getEventsBusHandler(), "a new engine installs its own bus");
            assertSame(built, GameEngine.getGame(), "and is then the singleton");
        } finally {
            field(GameEngine.class, "instance").set(null, keep);
            GameEngine.setEventsBusHandler(previous);
        }
    }
}
