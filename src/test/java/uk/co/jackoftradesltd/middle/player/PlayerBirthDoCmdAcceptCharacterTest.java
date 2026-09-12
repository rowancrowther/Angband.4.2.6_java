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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.middle.cave.GenChunk;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandQueue;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.Rune;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerHistoryType;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#doCmdAcceptCharacter}, the port of C's {@code do_cmd_accept_character}
 * ({@code player-birth.c:1258-1343}) - the final step of character creation.
 *
 * <p><b>A real bootstrap, but a wiring-scoped fixture.</b> Unlike
 * {@code PlayerBirthDoCmdBirthInitTest}, which fakes out everything {@code doCmdBirthInit} touches,
 * this suite calls {@code GameConstants.init(CoreChannel)} once and lets {@code doCmdAcceptCharacter} run
 * against the real shipped data - a real "Human" {@link PlayerRace}, the real
 * {@code artifact.txt}/{@code flavour.txt}/{@code object_property.txt} tables - for every part of the
 * method this suite actually asserts on. {@link Store#storeReset()},
 * {@link uk.co.jackoftradesltd.backend.io.Datafile#deactivateRandartFile()} and
 * {@link uk.co.jackoftradesltd.middle.objects.ObjectRandart#doRandart} are all stubs today, so the
 * {@code birth_randarts} branch is exercised for control flow only - it cannot yet be told apart from
 * the branch not running by any observable effect.
 *
 * <p><b>Why the player's class has no start items.</b> A real class's start kit (food, a light, a
 * potion, armour…) drives {@code playerOutfit} into {@code wieldAll} → {@code objectSplit} →
 * {@code ItemObject.copy()}, which unconditionally calls {@code this.baseDamage.copy()}
 * ({@code ItemObject.java:2766}) with no null guard - unlike its neighbours {@code known} and
 * {@code location} two lines above, which are. Any non-weapon kind (food, potions, armour) has no
 * damage-dice string in {@code object.txt}, so {@code Random.parseStr("")} hands back {@code null}
 * ({@code Random.java:161}) and the very first real class this suite tried NPEs there. Both that gap
 * and a sibling one in {@code ObjectUtils.objectPrep} (several {@link ObjectKind} {@code Random}
 * fields, e.g. {@code pVal}, come back {@code null} for kinds with no matching data line, and
 * {@code objectPrep} calls {@code .randCalc(...)} on them unconditionally) are pre-existing gaps in
 * the item-construction pipeline, not in {@code doCmdAcceptCharacter} - neither is this suite's to
 * fix, and neither can be patched from outside the call the way the item-knowledge gap below can, so
 * {@link CalcBonusesFixture#plainClass()}'s empty start-item list is used instead, which keeps
 * {@code playerOutfit}'s per-item loop from running at all while leaving everything this suite does
 * assert on - the option guards, the closing state, the history clear - running against real data.
 *
 * <p>Class PlayerBirthDoCmdAcceptCharacterTest coded on 260908, commented in full on 260908.
 *
 * @author Rowan Crowther
 */
class PlayerBirthDoCmdAcceptCharacterTest {

    private Player player;
    private Player realPlayer;
    private CommandQueue realCommandQueue;
    private EventsHandler realBus;
    private boolean realCharacterGenerated;
    private CapturingBus bus;
    private Birther previousPrev;
    private Birther previousQuickstartPrev;

    @BeforeAll
    static void bootstrap() throws IOException {
        // Mirrors UILoop's EVENT_ENTER_INIT handler: the real init chain now blocks partway
        // through GameConstants.init() for this ack, so the front end's UIEntry load has to
        // actually happen before it is sent, or the assemblers below find an empty UIRegistry.
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.SimpleUIMessage(GameEventType.EVENT_ENTER_INIT));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);

        // Rune.initRunes() is public but, like PlayerBirth#playerInit, nothing in the port's
        // bootstrap calls it yet - GameConstants.init() never builds ObjectRegistry's rune list, so
        // without this, learnAllRunes would silently iterate zero runes and KnowRunesGuard's "on"
        // case could not tell itself apart from "off".
        Rune.initRunes();
    }

    /**
     * Matches {@code QuestReaderTest}/{@code PitReaderTest}'s own cleanup after a full
     * {@code GameConstants.init(CoreChannel)}: resets the object-kind tables so this heavy load does not leak
     * into order-sensitive suites running later in the same JVM.
     *
     * <p>{@link Rune#initRunes()} needs its own undo that those two do not: nothing else in the suite
     * calls it, so {@link ObjectRegistry#getRunes()} is empty everywhere else, and
     * {@code PlayerUpdateObjectKnowledgeTest}'s minimal {@code ItemObject} fixtures NPE the moment
     * {@code objectHasRune} actually has a rune to iterate. Restoring the empty list this class found
     * ({@code ObjectRegistry.java:315}'s own default) is what {@link Rune#initRunes()} has no reset
     * method of its own to do.
     */
    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
        ObjectRegistry.setRunes(List.of());
    }

    private static PlayerRace humanRace() {
        return PlayerRegistry.lookupPlayerRace("Human");
    }

    /**
     * A class with no start items - see the class Javadoc for why {@code playerOutfit} cannot yet be
     * run with a real one's.
     */
    private static PlayerClass emptyStartItemsClass() {
        return CalcBonusesFixture.plainClass();
    }

    @SuppressWarnings("unchecked")
    private static Flag<PlayerOptionEnum> birthOptions(Player player) throws ReflectiveOperationException {
        Field field = PlayerOptions.class.getDeclaredField("options");
        field.setAccessible(true);
        return (Flag<PlayerOptionEnum>) field.get(player.getPlayerOptions());
    }

    private static boolean repeatPrevAllowed(CommandQueue queue) throws ReflectiveOperationException {
        Field field = CommandQueue.class.getDeclaredField("repeatPrevAllowed");
        field.setAccessible(true);
        return field.getBoolean(queue);
    }

    private static void setRepeatPrevAllowed(CommandQueue queue, boolean value)
            throws ReflectiveOperationException {
        Field field = CommandQueue.class.getDeclaredField("repeatPrevAllowed");
        field.setAccessible(true);
        field.setBoolean(queue, value);
    }

    /**
     * A flavoured kind with no relation to the ordinary path's own outfit - any random-flavour tval
     * {@code flavourInit} assigns during the call under test. Used by {@link KnowFlavorsGuard} to
     * observe {@code flavourSetAllAware}'s effect, since {@code doCmdAcceptCharacter} itself hands
     * back nothing to assert against.
     */
    private static ObjectKind firstFlavouredKind() {
        return ObjectRegistry.getObjectKinds().stream()
                .filter(kind -> kind.getFlavour() != null)
                .findFirst()
                .orElseThrow();
    }

    @BeforeEach
    void seedFixture() throws ReflectiveOperationException {
        realPlayer = GameState.getPlayer();
        realCommandQueue = GameState.getCommandQueue();
        realBus = GameEngine.getEventsBusHandler();
        realCharacterGenerated = GameState.getCharacterGenerated();

        player = new Player();
        GameState.setPlayer(player);
        PlayerBirth.playerGenerate(player, humanRace(), emptyStartItemsClass(), false);

        // player_init() ([C] player-birth.c:396) is what normally sizes obj_k against the loaded
        // registries; nothing in the port's birth flow reaches it yet (PlayerBirth#playerInit's own
        // Javadoc records the gap), so this fixture does that one step of it by hand.
        KnownObject itemKnowledge = new KnownObject();
        itemKnowledge.initBrands();
        itemKnowledge.initSlays();
        itemKnowledge.initCurses();
        player.setItemKnowledge(itemKnowledge);

        CommandQueue queue = new CommandQueue(player);
        setRepeatPrevAllowed(queue, true);
        GameState.setCommandQueue(queue);

        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        previousPrev = PlayerBirthStateRegistry.getPrev();
        previousQuickstartPrev = PlayerBirthStateRegistry.getQuickstartPrev();
        Birther prev = new Birther();
        prev.setHistoryBirth("Stale previous history.");
        Birther quickstartPrev = new Birther();
        quickstartPrev.setHistoryBirth("Stale quickstart history.");
        PlayerBirthStateRegistry.setPrev(prev);
        PlayerBirthStateRegistry.setQuickstartPrev(quickstartPrev);

        // flavourSetAllAware is cumulative on the shared ObjectKind registry; start every test
        // from the same unaware baseline so KnowFlavorsGuard's "off" case cannot see a previous
        // test's "on" case leaking through.
        for (ObjectKind kind : ObjectRegistry.getObjectKinds()) {
            kind.setAware(false);
        }
    }

    @AfterEach
    void restoreFixture() {
        GameState.setPlayer(realPlayer);
        GameState.setCommandQueue(realCommandQueue);
        GameEngine.setEventsBusHandler(realBus);
        PlayerBirthStateRegistry.setPrev(previousPrev);
        PlayerBirthStateRegistry.setQuickstartPrev(previousQuickstartPrev);
        // doCmdAcceptCharacter sets this true and nothing else in the suite ever sets it back:
        // left alone, PlayerCalcs.redrawStuff's "do we have a character?" early return
        // (PlayerCalcs.java:887) stops being taken for every later test in the same JVM, which is
        // what broke PlayerSetTimedTest$Tail.notifyingRaisesStatus - it relies on that early return
        // to keep observing PR_STATUS still set.
        GameState.setCharacterGenerated(realCharacterGenerated);
    }

    /**
     * An {@link EventsHandler} that keeps every dispatch instead of delivering it, so
     * {@code EVENT_LEAVE_BIRTH} can be asserted without a display attached - the same shape
     * {@code PlayerBirthDoCmdBirthInitTest} uses. The three registration methods are unused here and
     * left empty.
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<GameEventType> types = new ArrayList<>();

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
        }

        private boolean hasDispatch(GameEventType type) {
            return types.contains(type);
        }
    }

    /**
     * The ordinary path: every {@code birth_*} option this method itself branches on left off.
     */
    @Nested
    @DisplayName("the ordinary path")
    class OrdinaryPath {

        @Test
        @DisplayName("clears is_dead, marks the character generated and playing, and fires EVENT_LEAVE_BIRTH")
        void completesTheBirth() {
            player.setIsDead(true);

            PlayerBirth.doCmdAcceptCharacter(null);

            assertFalse(player.isDead(), "player->is_dead = false");
            assertTrue(GameState.getCharacterGenerated(), "character_generated = true");
            assertTrue(player.getPlayerUpkeep().isPlaying(), "upkeep->playing = true");
            assertTrue(bus.hasDispatch(GameEventType.EVENT_LEAVE_BIRTH));
        }

        @Test
        @DisplayName("resets the dungeon chunk list and disables command repeat")
        void resetsDungeonAndRepeat() throws ReflectiveOperationException {
            PlayerBirth.doCmdAcceptCharacter(null);

            assertEquals(0, GenChunk.getChunkListMax(), "chunk_list_max = 0");
            assertFalse(repeatPrevAllowed(GameState.getCommandQueue()), "cmd_disable_repeat()");
        }

        @Test
        @DisplayName("sets the combat-value hack to 1 on to_a, to_d and to_h")
        void setsCombatValueHack() {
            PlayerBirth.doCmdAcceptCharacter(null);

            KnownObject knowledge = player.getItemKnowledge();
            assertEquals(1, knowledge.getToA(), "obj_k->to_a = 1");
            assertEquals(1, knowledge.getToD(), "obj_k->to_d = 1");
            assertEquals(1, knowledge.getToH(), "obj_k->to_h = 1");
        }

        @Test
        @DisplayName("clears the cached prev/quickstartPrev roller history now birth is done")
        void clearsCachedRollerHistory() {
            PlayerBirth.doCmdAcceptCharacter(null);

            assertEquals("", PlayerBirthStateRegistry.getPrev().getHistory(),
                    "prev.history = NULL");
            assertEquals("", PlayerBirthStateRegistry.getQuickstartPrev().getHistory(),
                    "quickstart_prev.history = NULL");
        }

        @Test
        @DisplayName("clears any earlier history and adds the birth entry")
        void addsBirthHistoryEntry() {
            player.getPlayerHistory().addEntry(
                    new HistoryInfo(new Flag<>(PlayerHistoryType.class), 0, 1, null, 0,
                            "Left over from a previous game."));

            PlayerBirth.doCmdAcceptCharacter(null);

            List<HistoryInfo> entries = player.getPlayerHistory().entries;
            assertEquals(1, entries.size(), "history_clear then a single history_add");
            assertEquals("Began the quest to destroy Morgoth.", entries.get(0).historyText);
        }
    }

    /**
     * {@code OP_birth_know_runes}, gating {@link PlayerKnowledge#learnAllRunes}. Fire resistance is
     * the discriminator, not a combat rune: {@code doCmdAcceptCharacter}'s own to-a/to-d/to-h hack
     * sets every combat rune's <em>value</em> to 1 unconditionally regardless of this option, and
     * {@link KnownObject#toAIsKnown()} reads that same value, so a combat rune is always "known"
     * whichever way this guard goes. {@code learnInnate}, which runs unconditionally, learns a
     * resistance only when the race itself carries it ({@code player-birth.c:1286-1300}), and the
     * shipped "Human" has no resistances at all, so fire resistance is known afterwards only if
     * {@code learnAllRunes} itself ran.
     */
    @Nested
    @DisplayName("OP_birth_know_runes")
    class KnowRunesGuard {

        @Test
        @DisplayName("on: every rune, including ones the race has no innate knowledge of, is learned")
        void onLearnsEveryRune() throws ReflectiveOperationException {
            birthOptions(player).on(PlayerOptionEnum.OP_birth_know_runes);

            PlayerBirth.doCmdAcceptCharacter(null);

            assertTrue(player.getItemKnowledge().resistanceIsKnown(ElementEnum.ELEM_FIRE),
                    "player_learn_all_runes ran");
        }

        @Test
        @DisplayName("off: fire resistance is left unknown")
        void offLeavesResistanceUnknown() throws ReflectiveOperationException {
            birthOptions(player).off(PlayerOptionEnum.OP_birth_know_runes);

            PlayerBirth.doCmdAcceptCharacter(null);

            assertFalse(player.getItemKnowledge().resistanceIsKnown(ElementEnum.ELEM_FIRE),
                    "learnInnate alone never learns it for a Human, who has no innate resistances");
        }
    }

    /**
     * {@code OP_birth_know_flavors}, gating {@link uk.co.jackoftradesltd.middle.objects.ObjectUtils#flavourSetAllAware}.
     * {@code flavourInit} runs unconditionally just above the guard, so any random-flavour kind is
     * available to check regardless of which branch this option takes.
     */
    @Nested
    @DisplayName("OP_birth_know_flavors")
    class KnowFlavorsGuard {

        @Test
        @DisplayName("on: every flavoured kind becomes aware")
        void onMarksFlavouredKindsAware() throws ReflectiveOperationException {
            birthOptions(player).on(PlayerOptionEnum.OP_birth_know_flavors);

            PlayerBirth.doCmdAcceptCharacter(null);

            assertTrue(firstFlavouredKind().isAware(), "flavor_set_all_aware ran");
        }

        @Test
        @DisplayName("off: flavoured kinds are left unaware")
        void offLeavesFlavouredKindsUnaware() throws ReflectiveOperationException {
            birthOptions(player).off(PlayerOptionEnum.OP_birth_know_flavors);

            PlayerBirth.doCmdAcceptCharacter(null);

            assertFalse(firstFlavouredKind().isAware(), "flavor_set_all_aware did not run");
        }
    }

    /**
     * {@code OP_birth_randarts}. {@link uk.co.jackoftradesltd.middle.objects.ObjectRandart#doRandart}
     * and {@link uk.co.jackoftradesltd.backend.io.Datafile#deactivateRandartFile()} are both stubs, so
     * this pins down only that the branch is reachable without throwing - there is no observable
     * effect yet for a passing test to tell apart from the branch not running.
     */
    @Nested
    @DisplayName("OP_birth_randarts")
    class RandartsGuard {

        @Test
        @DisplayName("on: the branch runs to completion")
        void onRunsWithoutThrowing() throws ReflectiveOperationException {
            birthOptions(player).on(PlayerOptionEnum.OP_birth_randarts);

            PlayerBirth.doCmdAcceptCharacter(null);

            assertTrue(GameState.getCharacterGenerated());
        }
    }
}
