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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectUtils;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerCalcs#calcInventory}, the port of C's {@code calc_inventory}
 * ({@code player-calcs.c}): the rebuild of the pack and quiver views over the gear.
 *
 * <p>The carry capacities are those of {@link SeededPlayerRegistry}: a pack of 23, a quiver of 10
 * slots of 40, and a multiplier of 5 for thrown weapons in the quiver. The body has twelve slots, so
 * C's {@code n_max} is {@code 1 + 23 + 10 + 12 = 46}.
 *
 * <p>Ordering between two stacks of the same kind is by {@code sval}, lower first
 * ({@code earlier_object()}: "objects sort by increasing sval"), so the tests use the sval to put
 * stacks in a known order. The ordering test only reaches the sval for kinds the player is aware of
 * (C marks every flavourless kind aware at start-up), so each fixture item's kind is made aware.
 * Gear is filled with {@code gear_insert_end()}, so the gear order, walking from the head as C does,
 * is the order the items were added.
 *
 * <p>Not covered: the refusal to split a quiver stack once the pack is full, which turns on
 * {@code pack_slots_used()} and belongs with that function's own tests; and the guard on a split
 * that would not shrink the stack and the guard on an invalid split count, which C asserts on and
 * which cannot be reached with consistent capacities.
 *
 * <p>Class PlayerCalcInventoryTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCalcInventoryTest {

    /**
     * The pack size in the seeded constants.
     */
    private static final int PACK_SIZE = 23;

    /**
     * The character whose gear is rebuilt.
     */
    private Player player;

    /**
     * The messages the game said.
     */
    private CapturingBus bus;

    /**
     * The event bus before the test.
     */
    private EventsHandler realBus;

    /**
     * The player the game held before the test.
     */
    private Player savedPlayer;

    /**
     * Whether the dungeon flag was set before the test.
     */
    private boolean savedCharacterDungeon;

    /**
     * The fixture that built the player, kept so that a test can wield something.
     */
    private CalcBonusesFixture fixture;

    /**
     * Builds a plain character, makes it the game's player (the item ordering reads the global
     * player's ammunition type) and captures messages.
     *
     * @throws Exception if the fixture cannot reach a field
     */
    @BeforeEach
    void setUp() throws Exception {
        fixture = CalcBonusesFixture.plainCharacter();
        player = fixture.player();
        savedPlayer = GameState.getPlayer();
        GameState.setPlayer(player);
        savedCharacterDungeon = GameWorld.hasCharacterDungeon();
        GameWorld.setCharacterDungeon(false);

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    /**
     * Puts the globals back.
     */
    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
        GameState.setPlayer(savedPlayer);
        GameWorld.setCharacterDungeon(savedCharacterDungeon);
    }

    /**
     * Collects the text of every message the game says.
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * The messages, in order.
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
            if (data instanceof EventDataMessage message) messages.add(message.message());
        }
    }

    /**
     * A stack of arrows.
     *
     * @param number the number in the stack
     * @param sval   the sub-value, which orders stacks of the same type
     * @return the stack
     */
    private static ItemObject arrows(int number, int sval) {
        ItemObject stack = ItemFixture.item(TValue.TV_ARROW).number(number).fullyKnown().build();
        stack.setsValue(sval);
        stack.getKind().setAware(true);
        return stack;
    }

    /**
     * A stack of throwing daggers.
     *
     * @param number the number in the stack
     * @return the stack
     */
    private static ItemObject daggers(int number) {
        ItemObject stack = ItemFixture.item(TValue.TV_SWORD).number(number)
                .flags(ObjectFlag.OF_THROWING).fullyKnown().build();
        stack.setsValue(1);
        stack.getKind().setAware(true);
        return stack;
    }

    /**
     * A potion.
     *
     * @param sval the sub-value, which orders potions
     * @return the potion
     */
    private static ItemObject potion(int sval) {
        ItemObject potion = ItemFixture.item(TValue.TV_POTION).fullyKnown().build();
        potion.setsValue(sval);
        potion.getKind().setAware(true);
        return potion;
    }

    /**
     * Adds an item to the end of the gear, as C's {@code gear_insert_end()}.
     *
     * @param item the item to carry
     * @return the item
     */
    private ItemObject carry(ItemObject item) {
        ObjectUtils.gearInsertEnd(player, item);
        return item;
    }

    /**
     * Where ammunition goes: into the quiver, in order, never the pack.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("ammunition")
    class Ammunition {

        /**
         * One stack of arrows is placed in the first quiver slot and counted by its number; nothing
         * reaches the pack.
         */
        @Test
        @DisplayName("a stack of arrows fills the first quiver slot")
        void firstSlot() {
            ItemObject stack = carry(arrows(12, 1));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(stack, player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertEquals(12, player.getPlayerUpkeep().getQuiverCount()),
                    () -> assertEquals(0, player.getPlayerUpkeep().getInventoryCount()));
        }

        /**
         * Two stacks fill the slots in {@code earlier_object()} order, not gear order: the stack with
         * the lower sval is first even though it was added second.
         */
        @Test
        @DisplayName("stacks fill the slots in sorted order, not gear order")
        void sortedOrder() {
            ItemObject high = carry(arrows(10, 5));
            ItemObject low = carry(arrows(20, 2));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(low, player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertSame(high, player.getPlayerUpkeep().getQuiver()[1]),
                    () -> assertEquals(30, player.getPlayerUpkeep().getQuiverCount()));
        }

        /**
         * An inscription {@code @f3} asks for slot 3 and gets it; the uninscribed stack takes the
         * first empty slot, 0.
         */
        @Test
        @DisplayName("an inscribed stack takes its named slot")
        void inscribedSlot() {
            ItemObject plain = carry(arrows(10, 1));
            ItemObject inscribed = carry(arrows(20, 2));
            inscribed.setNote("@f3");

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(inscribed, player.getPlayerUpkeep().getQuiver()[3]),
                    () -> assertSame(plain, player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertNull(player.getPlayerUpkeep().getQuiver()[1]));
        }

        /**
         * If two stacks name the same slot, the first in gear order has it and the second falls
         * through to the ordinary fill, which puts it in the first empty slot, 1.
         */
        @Test
        @DisplayName("a second claim on a slot falls through to the first empty one")
        void contestedSlot() {
            ItemObject first = carry(arrows(10, 1));
            first.setNote("@f0");
            ItemObject second = carry(arrows(20, 2));
            second.setNote("@f0");

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(first, player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertSame(second, player.getPlayerUpkeep().getQuiver()[1]));
        }

        /**
         * A stack bigger than a slot holds 40 and the rest goes on to the next slot as a stack of
         * its own: 60 arrows are 40 in slot 0 and 20 in slot 1, and nothing reaches the pack.
         */
        @Test
        @DisplayName("an oversize stack is split across slots")
        void oversizeStack() {
            ItemObject stack = carry(arrows(60, 1));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(stack, player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertEquals(40, stack.getNumber()),
                    () -> assertEquals(20, player.getPlayerUpkeep().getQuiver()[1].getNumber()),
                    () -> assertEquals(60, player.getPlayerUpkeep().getQuiverCount()),
                    () -> assertEquals(0, player.getPlayerUpkeep().getInventoryCount()),
                    () -> assertEquals(2, player.getGear().size(), "the remainder joins the gear"));
        }

        /**
         * A stack of exactly the slot size is not split: the test is {@code number > slot_size}.
         */
        @Test
        @DisplayName("a stack of exactly the slot size is not split")
        void exactlyOneSlot() {
            ItemObject stack = carry(arrows(40, 1));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertEquals(40, stack.getNumber()),
                    () -> assertNull(player.getPlayerUpkeep().getQuiver()[1]),
                    () -> assertEquals(1, player.getGear().size()));
        }

        /**
         * The quiver is rebuilt, not added to: a second pass over the same gear gives the same
         * counts, not doubled ones.
         */
        @Test
        @DisplayName("recalculating does not double the quiver count")
        void idempotent() {
            carry(arrows(12, 1));

            PlayerCalcs.calcInventory(player);
            PlayerCalcs.calcInventory(player);

            assertEquals(12, player.getPlayerUpkeep().getQuiverCount());
        }
    }

    /**
     * Throwing weapons in the quiver: only by inscription, and weighing the multiplier.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("thrown weapons")
    class Thrown {

        /**
         * A throwing weapon with no inscription is not ammunition and goes to the pack.
         */
        @Test
        @DisplayName("an uninscribed throwing weapon goes to the pack")
        void uninscribed() {
            ItemObject stack = carry(daggers(3));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertNull(player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertSame(stack, player.getPlayerUpkeep().getInventory()[0]));
        }

        /**
         * With {@code @v0} the weapons go to the quiver, each counting 5 against the count: three
         * daggers are 15.
         */
        @Test
        @DisplayName("an inscribed throwing weapon counts five apiece")
        void inscribed() {
            ItemObject stack = carry(daggers(3));
            stack.setNote("@v0");

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(stack, player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertEquals(15, player.getPlayerUpkeep().getQuiverCount()),
                    () -> assertEquals(0, player.getPlayerUpkeep().getInventoryCount()));
        }

        /**
         * Ten daggers would count 50 against a slot of 40, so the slot takes {@code 40 / 5 = 8} and
         * the other two go to the pack as a stack of their own.
         */
        @Test
        @DisplayName("an oversize thrown stack leaves its remainder in the pack")
        void oversize() {
            ItemObject stack = carry(daggers(10));
            stack.setNote("@v0");

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(stack, player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertEquals(8, stack.getNumber()),
                    () -> assertEquals(40, player.getPlayerUpkeep().getQuiverCount()),
                    () -> assertEquals(1, player.getPlayerUpkeep().getInventoryCount()),
                    () -> assertEquals(2, player.getPlayerUpkeep().getInventory()[0].getNumber()));
        }

        /**
         * A split in the inscribed pass must not make the walk miss the next item. The older stack
         * of daggers is split to fit slot 0; the newer arrows, inscribed {@code @f3}, are the next
         * item C visits and must still get slot 3. Their position in the gear list moves when the
         * remainder is added, which is the trap.
         */
        @Test
        @DisplayName("a split does not make the inscribed pass skip the next item")
        void splitDoesNotSkipTheNextItem() {
            ItemObject thrown = carry(daggers(10));
            thrown.setNote("@v0");
            ItemObject fired = carry(arrows(10, 1));
            fired.setNote("@f3");

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(thrown, player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertSame(fired, player.getPlayerUpkeep().getQuiver()[3]),
                    () -> assertNull(player.getPlayerUpkeep().getQuiver()[1]));
        }

        /**
         * An inscription on something that is neither ammunition nor a throwing weapon is ignored:
         * a potion marked {@code @f0} stays in the pack.
         */
        @Test
        @DisplayName("an inscription on an ordinary item is ignored")
        void ordinaryItem() {
            ItemObject flask = carry(potion(1));
            flask.setNote("@f0");

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertNull(player.getPlayerUpkeep().getQuiver()[0]),
                    () -> assertSame(flask, player.getPlayerUpkeep().getInventory()[0]));
        }
    }

    /**
     * The pack: what is left once the quiver and the equipment are taken out.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the pack")
    class Pack {

        /**
         * Items of the same kind are ordered by sval, lower first.
         */
        @Test
        @DisplayName("the pack is sorted")
        void sorted() {
            ItemObject high = carry(potion(5));
            ItemObject low = carry(potion(2));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(low, player.getPlayerUpkeep().getInventory()[0]),
                    () -> assertSame(high, player.getPlayerUpkeep().getInventory()[1]),
                    () -> assertEquals(2, player.getPlayerUpkeep().getInventoryCount()));
        }

        /**
         * Items that {@code earlier_object()} cannot separate keep the order C walks the gear in,
         * oldest first: the strict "is the new one earlier" test never displaces the first one met.
         * Two potions of the same sval, added in turn, come out in the order they were added.
         */
        @Test
        @DisplayName("equal items keep the order they were picked up in")
        void equalItemsKeepGearOrder() {
            ItemObject older = carry(potion(3));
            ItemObject newer = carry(potion(3));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertSame(older, player.getPlayerUpkeep().getInventory()[0]),
                    () -> assertSame(newer, player.getPlayerUpkeep().getInventory()[1]));
        }

        /**
         * An equipped item is in the gear but in neither view.
         *
         * @throws Exception if the fixture cannot place the item
         */
        @Test
        @DisplayName("worn equipment is in neither view")
        void equipmentExcluded() throws Exception {
            ItemObject sword = carry(ItemFixture.item(TValue.TV_SWORD).fullyKnown().build());
            fixture.wear("weapon", sword);
            ItemObject flask = carry(potion(1));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertEquals(1, player.getPlayerUpkeep().getInventoryCount()),
                    () -> assertSame(flask, player.getPlayerUpkeep().getInventory()[0]));
        }

        /**
         * The pack may be overfull by one: 24 items give 24 entries, the last in the extra slot.
         */
        @Test
        @DisplayName("the pack may hold one more than its size")
        void overfullByOne() {
            for (int i = 0; i <= PACK_SIZE; i++) carry(potion(i));

            PlayerCalcs.calcInventory(player);

            assertAll(
                    () -> assertEquals(PACK_SIZE + 1, player.getPlayerUpkeep().getInventoryCount()),
                    () -> assertNotNull(player.getPlayerUpkeep().getInventory()[PACK_SIZE]));
        }

        /**
         * But no more than that: a twenty-fifth item is left out of the view, because C's fill
         * loop runs to {@code pack_size} inclusive and stops.
         */
        @Test
        @DisplayName("an item past the extra slot is left out")
        void overfullByTwo() {
            for (int i = 0; i < PACK_SIZE + 2; i++) carry(potion(i));

            PlayerCalcs.calcInventory(player);

            assertEquals(PACK_SIZE + 1, player.getPlayerUpkeep().getInventoryCount());
        }

        /**
         * More items than C's {@code n_max} is a bug in the caller, and is thrown, where C asserts.
         */
        @Test
        @DisplayName("more items than the gear can hold throws")
        void tooMuchGear() {
            for (int i = 0; i < 47; i++) carry(potion(i));

            assertThrows(RuntimeException.class, () -> PlayerCalcs.calcInventory(player));
        }
    }

    /**
     * The re-arrange messages: given only when there is a dungeon, and only when something that
     * was already there has moved.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("re-arrange messages")
    class Messages {

        /**
         * Two potions in sval order, then the first one's sval is lowered below the other's, so the
         * same two items change places: same count, different order, and so a message.
         */
        @Test
        @DisplayName("a changed pack order is announced")
        void packReordered() {
            GameWorld.setCharacterDungeon(true);
            ItemObject a = carry(potion(5));
            carry(potion(2));
            PlayerCalcs.calcInventory(player);
            bus.messages.clear();

            a.setsValue(1);
            PlayerCalcs.calcInventory(player);

            assertTrue(bus.messages.contains("You re-arrange your pack."), bus.messages.toString());
        }

        /**
         * The same pass with nothing changed says nothing.
         */
        @Test
        @DisplayName("an unchanged pack is not announced")
        void packUnchanged() {
            GameWorld.setCharacterDungeon(true);
            carry(potion(5));
            carry(potion(2));
            PlayerCalcs.calcInventory(player);
            bus.messages.clear();

            PlayerCalcs.calcInventory(player);

            assertTrue(bus.messages.isEmpty(), bus.messages.toString());
        }

        /**
         * Before the dungeon exists — character creation stocking the pack — the reorder is silent.
         */
        @Test
        @DisplayName("nothing is said before the dungeon exists")
        void silentBeforeDungeon() {
            ItemObject a = carry(potion(5));
            carry(potion(2));
            PlayerCalcs.calcInventory(player);

            a.setsValue(1);
            PlayerCalcs.calcInventory(player);

            assertTrue(bus.messages.isEmpty(), bus.messages.toString());
        }

        /**
         * If the count changed, the pack is not compared: a third potion that shuffles the others
         * is a change the player can already see.
         */
        @Test
        @DisplayName("a changed count is not announced as a re-arrangement")
        void countChanged() {
            GameWorld.setCharacterDungeon(true);
            carry(potion(5));
            carry(potion(2));
            PlayerCalcs.calcInventory(player);
            bus.messages.clear();

            carry(potion(1));
            PlayerCalcs.calcInventory(player);

            assertTrue(bus.messages.isEmpty(), bus.messages.toString());
        }

        /**
         * An item that left the pack because it was wielded does not count as moved: the old pack
         * was [1, 2] and the new one is [1, 3] with the 2 now worn, and C skips the equipped old
         * entry.
         *
         * @throws Exception if the fixture cannot place the item
         */
        @Test
        @DisplayName("an item that was wielded is not counted as moved")
        void wieldedNotMoved() throws Exception {
            GameWorld.setCharacterDungeon(true);
            carry(potion(1));
            ItemObject worn = carry(potion(2));
            PlayerCalcs.calcInventory(player);
            bus.messages.clear();

            fixture.wear("light", worn);
            carry(potion(3));
            PlayerCalcs.calcInventory(player);

            assertFalse(bus.messages.contains("You re-arrange your pack."), bus.messages.toString());
        }

        /**
         * A quiver whose slots change owner is announced.
         */
        @Test
        @DisplayName("a changed quiver order is announced")
        void quiverReordered() {
            GameWorld.setCharacterDungeon(true);
            ItemObject a = carry(arrows(10, 5));
            carry(arrows(10, 2));
            PlayerCalcs.calcInventory(player);
            bus.messages.clear();

            a.setsValue(1);
            PlayerCalcs.calcInventory(player);

            assertTrue(bus.messages.contains("You re-arrange your quiver."), bus.messages.toString());
        }

        /**
         * An unchanged quiver is not.
         */
        @Test
        @DisplayName("an unchanged quiver is not announced")
        void quiverUnchanged() {
            GameWorld.setCharacterDungeon(true);
            carry(arrows(10, 5));
            carry(arrows(10, 2));
            PlayerCalcs.calcInventory(player);
            bus.messages.clear();

            PlayerCalcs.calcInventory(player);

            assertTrue(bus.messages.isEmpty(), bus.messages.toString());
        }
    }
}
