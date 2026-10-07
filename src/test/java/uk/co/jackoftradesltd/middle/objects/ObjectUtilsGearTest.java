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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandQueue;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.LevelMaxData;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.read;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.setStatic;

/**
 * Tests the gear routines of {@link ObjectUtils} that had no direct coverage: {@code combinePack}
 * and the partial-stacking check beneath it, {@code gearInsertEnd}, {@code gearToLabel},
 * {@code isCarried}, and the end-of-string case of {@code preferredQuiverSlot}. Their ports are C's
 * {@code combine_pack}, {@code inven_can_stack_partial}, {@code gear_insert_end},
 * {@code gear_to_label}, {@code object_is_carried} and {@code preferred_quiver_slot}, all in
 * {@code obj-gear.c} bar the carried test, which is in {@code obj-util.c}.
 *
 * <p><b>The order is the point.</b> C's gear runs head to tail, {@code gear_insert_end} adds at the
 * tail, and {@code combine_pack} starts {@code obj1} at the tail and scans {@code obj2} from the
 * head, so the stack added <em>first</em> is always the one that absorbs. {@link Pile} stores that
 * list backwards, which is what made the port's first loops run the wrong way: the count after a
 * whole merge is the same either way, so every test here that cares about direction asserts
 * <em>which</em> object survived, or which stack was filled, and not only the totals. The expected
 * figures are worked by hand from the C source, walking {@code combine_pack} on the gear in
 * insertion order.
 *
 * <p>The partial cases use a stack limit of 40, so that stacks of 35 and 10 cannot merge whole but
 * can still shift items. The inventory and quiver are set by hand in the label tests, because
 * {@code gear_to_label} only reads the arrays that {@code calc_inventory} fills; going through the
 * real rebuild would test its sort order as well.
 *
 * <p>Class ObjectUtilsGearTest coded on 261005, commented in full on 261005.
 *
 * @author Rowan Crowther
 */
@DisplayName("ObjectUtils gear routines")
class ObjectUtilsGearTest {

    private static final int PACK_SIZE = 23;
    private static final int QUIVER_SIZE = 10;
    private static final int SLOT_SIZE = 40;
    private static final int THROWN_MULT = 5;

    /**
     * The greatest number of one kind that may share a pack stack.
     */
    private static final int MAX_STACK = 40;

    private static Object savedConstants;
    private static Object savedBodies;
    private static Object savedRaces;
    private static Object savedCurses;
    private static Object savedTimedEffects;
    private static Player savedGamePlayer;
    private static Chunk savedGameCave;
    private static CommandQueue savedQueue;

    /**
     * The kind the stacks share. {@code similar} compares kinds by identity, so one instance is
     * what makes separately built stacks mutually stackable.
     */
    private static ObjectKind potionKind;

    /**
     * A second potion kind, for stacks that must not merge with the first.
     */
    private static ObjectKind otherKind;

    /**
     * An ammunition kind, for the inscription tests.
     */
    private static ObjectKind arrowKind;

    private static PlayerBody body;

    private Player player;
    private Chunk level;
    private Chunk known;
    private EventsHandler realBus;
    private CapturingBus bus;
    private CommandQueue queue;

    @BeforeAll
    static void seedGlobals() throws Exception {
        savedConstants = setStatic(GameConstants.class, "data", new GameConstantsData(
                new LevelMaxData(1024), null, null, null, null,
                new CarryCapData(PACK_SIZE, QUIVER_SIZE, SLOT_SIZE, THROWN_MULT, PACK_SIZE),
                null, null, null, null, null, null, null, null, null, null, null));

        savedBodies = registryField("playerBodies").get(null);
        savedRaces = registryField("playerRaces").get(null);
        body = new PlayerBody("test", List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        registryField("playerBodies").set(null, new ArrayList<>(List.of(body)));
        registryField("playerRaces").set(null, new ArrayList<>(List.of(plainRace(body))));

        Field curses = ObjectRegistry.class.getDeclaredField("curses");
        curses.setAccessible(true);
        savedCurses = curses.get(null);
        ObjectRegistry.setCurses(new ArrayList<>());

        savedTimedEffects = registryField("playerTimedEffects").get(null);
        registryField("playerTimedEffects").set(null, new ArrayList<>());

        savedGamePlayer = GameState.getPlayer();
        savedGameCave = GameState.getCave();
        savedQueue = GameState.getCommandQueue();

        potionKind = ItemFixture.kindWithBase(TValue.TV_POTION, "potion", MAX_STACK);
        otherKind = ItemFixture.kindWithBase(TValue.TV_POTION, "other potion", MAX_STACK);
        arrowKind = ItemFixture.kindWithBase(TValue.TV_ARROW, "arrow", 99);
    }

    @AfterAll
    static void restoreGlobals() throws Exception {
        GameState.setPlayer(savedGamePlayer);
        GameState.setCave(savedGameCave);
        GameState.setCommandQueue(savedQueue);
        setStatic(GameConstants.class, "data", savedConstants);
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
        Field curses = ObjectRegistry.class.getDeclaredField("curses");
        curses.setAccessible(true);
        curses.set(null, savedCurses);
        registryField("playerTimedEffects").set(null, savedTimedEffects);
    }

    private static PlayerRace plainRace(PlayerBody body) {
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
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body,
                stats, skills, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), null, Map.of());
    }

    private static Field registryField(String fieldName) throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * A fresh player built through {@link CalcBonusesFixture}, so {@code calcInventory} runs for
     * real at the end of {@code combinePack}, with the two caves {@code objectAbsorb} deletes from,
     * a capturing event bus, and a command queue whose repeat flag the tests can read.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @BeforeEach
    void buildPlayer() throws Exception {
        player = new Player();
        CalcBonusesFixture.plainCharacter(player).calculate();

        level = new Chunk("level", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        known = new Chunk("known", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        GameState.setCave(level);
        level.setCurrentLevel(level);
        known.setCurrentLevel(level);
        player.setCave(known);
        GameState.setPlayer(player);

        queue = new CommandQueue(player);
        GameState.setCommandQueue(queue);

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * A potion stack, identified, listed in both caves so that {@code objectAbsorb} can delete it.
     *
     * @param kind   the kind to build it on
     * @param number the stack size
     * @return the stack
     * @throws Exception if a fixture field cannot be reached
     */
    private ItemObject stack(ObjectKind kind, int number) throws Exception {
        ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(kind).number(number)
                .origin(ObjectOriginEnum.ORIGIN_FLOOR, 1, null).fullyKnown().build();
        listIn(level, item);
        listIn(known, item.getKnown());
        return item;
    }

    @SuppressWarnings("unchecked")
    private void listIn(Chunk chunk, ItemObject item) throws Exception {
        Field field = Chunk.class.getDeclaredField("objects");
        field.setAccessible(true);
        ((List<ItemObject>) field.get(chunk)).add(item);
    }

    private Pile gear() {
        return player.getGear();
    }

    /**
     * Runs {@code combinePack} under a timeout, because the failure to guard against is a loop that
     * never advances.
     */
    private void combine() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> ObjectUtils.combinePack(player));
    }

    /**
     * Records every dispatch so the announcement can be checked.
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<GameEventData> payloads = new ArrayList<>();

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
            payloads.add(data);
        }

        private List<String> messages() {
            List<String> result = new ArrayList<>();
            for (GameEventData data : payloads) {
                if (data instanceof EventDataMessage message) result.add(message.message());
            }
            return result;
        }
    }

    /**
     * Whole merges: C's {@code obj2}, the stack nearer the head, absorbs.
     */
    @Nested
    @DisplayName("combinePack, whole merges")
    class WholeMerges {

        @Test
        @DisplayName("the stack added first absorbs the one added second")
        void firstAddedStackSurvives() throws Exception {
            ItemObject first = stack(potionKind, 5);
            ItemObject second = stack(potionKind, 3);
            ObjectUtils.gearInsertEnd(player, first);
            ObjectUtils.gearInsertEnd(player, second);

            combine();

            assertEquals(1, gear().size());
            assertSame(first, gear().get(0), "C: obj2 is the earlier stack, so it survives");
            assertEquals(8, first.getNumber());
            assertEquals(8, first.getKnown().getNumber());
            assertEquals(1, player.getGearKnown().size(), "the known list loses the absorbed half too");
            assertSame(first.getKnown(), player.getGearKnown().get(0));
        }

        @Test
        @DisplayName("three stacks all end up in the first")
        void threeStacksCollapseIntoTheFirst() throws Exception {
            ItemObject a = stack(potionKind, 5);
            ItemObject b = stack(potionKind, 3);
            ItemObject c = stack(potionKind, 2);
            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, b);
            ObjectUtils.gearInsertEnd(player, c);

            combine();

            assertEquals(1, gear().size());
            assertSame(a, gear().get(0));
            assertEquals(10, a.getNumber());
            assertEquals(1, player.getGearKnown().size());
        }

        @Test
        @DisplayName("an unmergeable stack between two mergeable ones is left alone")
        void unmergeableStackIsSkippedOver() throws Exception {
            ItemObject a = stack(potionKind, 5);
            ItemObject other = stack(otherKind, 4);
            ItemObject c = stack(potionKind, 2);
            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, other);
            ObjectUtils.gearInsertEnd(player, c);

            combine();

            assertEquals(2, gear().size());
            assertEquals(7, a.getNumber());
            assertEquals(4, other.getNumber());
            assertFalse(gear().contains(c), "the last stack was absorbed into the first");
        }

        @Test
        @DisplayName("a whole merge is announced and stops the command repeat")
        void wholeMergeIsAnnouncedAndDisablesRepeat() throws Exception {
            set(queue, "repeatPrevAllowed", true);
            ObjectUtils.gearInsertEnd(player, stack(potionKind, 5));
            ObjectUtils.gearInsertEnd(player, stack(potionKind, 3));

            combine();

            assertTrue(bus.messages().contains("You combine some items in your pack."));
            assertEquals(false, read(queue, "repeatPrevAllowed"));
        }
    }

    /**
     * Partial merges: the stack nearer the head is the leading one and is filled first.
     */
    @Nested
    @DisplayName("combinePack, partial merges")
    class PartialMerges {

        @Test
        @DisplayName("the earlier stack is filled to the limit and the later keeps the remainder")
        void earlierStackIsMaximised() throws Exception {
            ItemObject a = stack(potionKind, 35);
            ItemObject b = stack(potionKind, 10);
            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, b);

            combine();

            assertEquals(2, gear().size(), "a partial merge removes nothing");
            assertEquals(MAX_STACK, a.getNumber(), "C: the leading stack obj2 is maximised");
            assertEquals(5, b.getNumber());
            assertEquals(MAX_STACK, a.getKnown().getNumber());
            assertEquals(5, b.getKnown().getNumber());
        }

        @Test
        @DisplayName("a partial merge is not announced and leaves the command repeat alone")
        void partialMergeIsSilent() throws Exception {
            set(queue, "repeatPrevAllowed", true);
            ObjectUtils.gearInsertEnd(player, stack(potionKind, 35));
            ObjectUtils.gearInsertEnd(player, stack(potionKind, 10));

            combine();

            assertFalse(bus.messages().contains("You combine some items in your pack."));
            assertEquals(true, read(queue, "repeatPrevAllowed"));
        }

        @Test
        @DisplayName("three stacks of 30 become 40, 30 and 20 in insertion order")
        void threeThirtiesSettleInInsertionOrder() throws Exception {
            // Worked from combine_pack: obj1 = c, obj2 = a: 60 > 40 so no whole merge; a is not at
            // the limit, so a takes 10 from c. obj1 = b, obj2 = a: a is at the limit, so nothing.
            ItemObject a = stack(potionKind, 30);
            ItemObject b = stack(potionKind, 30);
            ItemObject c = stack(potionKind, 30);
            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, b);
            ObjectUtils.gearInsertEnd(player, c);

            combine();

            assertEquals(3, gear().size());
            assertEquals(40, a.getNumber());
            assertEquals(30, b.getNumber());
            assertEquals(20, c.getNumber());
        }

        @Test
        @DisplayName("a full leading stack takes nothing")
        void fullLeadingStackTakesNothing() throws Exception {
            ItemObject a = stack(potionKind, MAX_STACK);
            ItemObject b = stack(potionKind, 10);
            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, b);

            combine();

            assertEquals(2, gear().size());
            assertEquals(MAX_STACK, a.getNumber());
            assertEquals(10, b.getNumber());
        }
    }

    /**
     * C ends each merge with "Ensure numbers align", copying the real stack's count onto its known
     * half. It is described there as unnecessary, which is only true while the two halves already
     * agree, so these start them out of step.
     */
    @Nested
    @DisplayName("combinePack, known counts are realigned")
    class KnownCountsAlign {

        @Test
        @DisplayName("a whole merge copies the surviving stack's count onto its known half")
        void wholeMergeRealignsTheSurvivor() throws Exception {
            // C: object_absorb(obj2->known, obj1->known) first gives the known half 99 + 3 capped at
            // the stack limit, then obj2->known->number = obj2->number puts it back to 5 + 3.
            ItemObject first = stack(potionKind, 5);
            ItemObject second = stack(potionKind, 3);
            first.getKnown().setNumber(99);
            ObjectUtils.gearInsertEnd(player, first);
            ObjectUtils.gearInsertEnd(player, second);

            combine();

            assertEquals(8, first.getNumber());
            assertEquals(8, first.getKnown().getNumber());
        }

        @Test
        @DisplayName("a partial merge copies both stacks' counts onto their known halves")
        void partialMergeRealignsBoth() throws Exception {
            // C: after object_absorb_partial on both halves, obj2->known->number = obj2->number and
            // obj1->known->number = obj1->number, whatever the known halves held before.
            ItemObject a = stack(potionKind, 35);
            ItemObject b = stack(potionKind, 10);
            a.getKnown().setNumber(1);
            b.getKnown().setNumber(2);
            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, b);

            combine();

            assertEquals(MAX_STACK, a.getNumber());
            assertEquals(5, b.getNumber());
            assertEquals(MAX_STACK, a.getKnown().getNumber());
            assertEquals(5, b.getKnown().getNumber());
        }
    }

    /**
     * Cases where nothing merges, which must still finish.
     */
    @Nested
    @DisplayName("combinePack, nothing to do")
    class NothingToDo {

        @Test
        @DisplayName("empty gear finishes")
        void emptyGearFinishes() {
            combine();

            assertEquals(0, gear().size());
            assertTrue(bus.messages().isEmpty());
        }

        @Test
        @DisplayName("a single stack finishes untouched")
        void singleStackFinishes() throws Exception {
            ItemObject only = stack(potionKind, 5);
            ObjectUtils.gearInsertEnd(player, only);

            combine();

            assertEquals(1, gear().size());
            assertSame(only, gear().get(0));
            assertEquals(5, only.getNumber());
        }

        @Test
        @DisplayName("interleaved kinds pair off, each into the earlier stack of its kind")
        void interleavedKindsPairOff() throws Exception {
            ItemObject a = stack(potionKind, 5);
            ItemObject b = stack(otherKind, 3);
            ItemObject c = stack(potionKind, 7);
            ItemObject d = stack(otherKind, 2);
            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, b);
            ObjectUtils.gearInsertEnd(player, c);
            ObjectUtils.gearInsertEnd(player, d);

            combine();

            // Worked from combine_pack: obj1 = d folds into b (the first stack of its kind it meets),
            // obj1 = c folds into a, and obj1 = b finds nothing but a, which is the wrong kind.
            assertEquals(2, gear().size());
            assertSame(a, gear().reversed().get(0));
            assertSame(b, gear().reversed().get(1));
            assertEquals(12, a.getNumber());
            assertEquals(5, b.getNumber());
        }
    }

    /**
     * {@code gear_insert_end}: the object joins the tail of C's list, which is index 0 here.
     */
    @Nested
    @DisplayName("gearInsertEnd")
    class GearInsertEnd {

        @Test
        @DisplayName("later insertions sit nearer the front of the backing list")
        void laterInsertionsComeFirst() throws Exception {
            ItemObject a = stack(potionKind, 1);
            ItemObject b = stack(otherKind, 1);

            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, b);

            assertSame(b, gear().get(0), "C's tail is index 0");
            assertSame(a, gear().get(1));
            assertSame(a, gear().reversed().get(0), "reversed() is C's head-to-tail order");
            assertSame(b, gear().reversed().get(1));
        }

        @Test
        @DisplayName("the known half joins the known list in the same place")
        void knownHalfJoinsTheParallelList() throws Exception {
            ItemObject a = stack(potionKind, 1);
            ItemObject b = stack(otherKind, 1);

            ObjectUtils.gearInsertEnd(player, a);
            ObjectUtils.gearInsertEnd(player, b);

            assertEquals(2, player.getGearKnown().size());
            assertSame(b.getKnown(), player.getGearKnown().get(0));
            assertSame(a.getKnown(), player.getGearKnown().get(1));
        }
    }

    /**
     * {@code gear_to_label}: equipment by slot, quiver by digit, pack by letter.
     */
    @Nested
    @DisplayName("gearToLabel")
    class GearToLabel {

        @Test
        @DisplayName("a worn item is labelled by its slot")
        void wornItemTakesItsSlotLetter() throws Exception {
            ItemObject sword = stack(potionKind, 1);
            set(player.getPlayerBody().getSlots().get(0), "item", sword);

            assertEquals('a', ObjectUtils.gearToLabel(player, sword));
        }

        @Test
        @DisplayName("a quivered item is labelled by its quiver index from zero")
        void quiverItemTakesADigit() throws Exception {
            ItemObject arrows = stack(potionKind, 1);
            player.getPlayerUpkeep().getQuiver()[3] = arrows;

            assertEquals('3', ObjectUtils.gearToLabel(player, arrows));
        }

        @Test
        @DisplayName("a pack item is labelled by its inventory index")
        void packItemTakesALetter() throws Exception {
            ItemObject item = stack(potionKind, 1);
            player.getPlayerUpkeep().getInventory()[2] = item;

            assertEquals('c', ObjectUtils.gearToLabel(player, item));
        }

        @Test
        @DisplayName("the alphabet skips h, j, k and l")
        void roguelikeMovementKeysAreSkipped() throws Exception {
            ItemObject seventh = stack(potionKind, 1);
            ItemObject eighth = stack(otherKind, 1);
            player.getPlayerUpkeep().getInventory()[6] = seventh;
            player.getPlayerUpkeep().getInventory()[7] = eighth;

            // C: "abcdefgimnop...": index 6 is g, then h is skipped, so index 7 is i
            assertEquals('g', ObjectUtils.gearToLabel(player, seventh));
            assertEquals('i', ObjectUtils.gearToLabel(player, eighth));
        }

        @Test
        @DisplayName("past the skipped keys the letters carry on at m")
        void lettersResumeAtM() throws Exception {
            ItemObject item = stack(potionKind, 1);
            player.getPlayerUpkeep().getInventory()[8] = item;

            assertEquals('m', ObjectUtils.gearToLabel(player, item));
        }

        @Test
        @DisplayName("an item the player does not hold has no label")
        void absentItemHasNoLabel() throws Exception {
            assertEquals('\0', ObjectUtils.gearToLabel(player, stack(potionKind, 1)));
        }

        @Test
        @DisplayName("null has no label")
        void nullHasNoLabel() {
            assertEquals('\0', ObjectUtils.gearToLabel(player, null));
        }
    }

    /**
     * {@code object_is_carried}: a containment test on the gear.
     */
    @Nested
    @DisplayName("isCarried")
    class IsCarried {

        @Test
        @DisplayName("an object in the gear is carried and one outside is not")
        void carriedOnlyWhenInTheGear() throws Exception {
            ItemObject held = stack(potionKind, 1);
            ItemObject floor = stack(otherKind, 1);
            ObjectUtils.gearInsertEnd(player, held);

            assertTrue(ObjectUtils.isCarried(player, held));
            assertFalse(ObjectUtils.isCarried(player, floor));
        }

        @Test
        @DisplayName("an equal-looking copy is not the carried object")
        void identityNotLookalike() throws Exception {
            ItemObject held = stack(potionKind, 1);
            ItemObject twin = stack(potionKind, 1);
            ObjectUtils.gearInsertEnd(player, held);

            assertNotSame(held, twin);
            assertFalse(ObjectUtils.isCarried(player, twin));
        }
    }

    /**
     * The input where the port and C give different, equally unusable, answers.
     */
    @Nested
    @DisplayName("preferredQuiverSlot, tag at the end of the inscription")
    class PreferredQuiverSlotEnd {

        private ItemObject arrows(String note) {
            ItemObject item = ItemFixture.item(TValue.TV_ARROW).kind(arrowKind).number(1).build();
            item.setNote(note);
            return item;
        }

        @Test
        @DisplayName("a complete tag names its slot")
        void completeTagNamesItsSlot() {
            assertEquals(2, ObjectUtils.preferredQuiverSlot(player, arrows("@f2")));
        }

        @Test
        @DisplayName("a tag with no digit answers -1, where C reads the NUL and answers -48")
        void tagWithNoDigitAnswersMinusOne() {
            assertEquals(-1, ObjectUtils.preferredQuiverSlot(player, arrows("@f")));
            assertEquals(-1, ObjectUtils.preferredQuiverSlot(player, arrows("@v")));
        }
    }
}
