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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.ObjectUtils;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.setStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the three capacity helpers behind {@link ObjectUtils#combinePack} —
 * {@link ObjectUtils#packSlotsUsed}, {@link ObjectUtils#preferredQuiverSlot} and
 * {@code quiverAbsorbNum} — the ports of C's {@code pack_slots_used} ({@code obj-gear.c:257}),
 * {@code preferred_quiver_slot} ({@code obj-gear.c:1396}) and {@code quiver_absorb_num}
 * ({@code obj-gear.c:649}).
 *
 * <p>All three used to be instance methods on {@link Player} and were reached here by reflection.
 * They now live on {@link ObjectUtils} as statics taking the player as their first argument, so the
 * first two are called directly; only {@code quiverAbsorbNum} is still private and still reflected
 * on, along with the {@code SplitBetweenPackAndQuiver} record it passes its pair of counts in.
 *
 * <p>They are worth testing at that cost because they are the arithmetic
 * {@code invenCanStackPartial} rests on, and because none of them has a visible failure mode: a
 * wrong slot count does not throw, it just quietly decides that a quiver with room in it is full, or
 * that a full one has room. The player sees ammunition that will not combine, which looks like
 * nothing at all.
 *
 * <p><b>What the quiver is.</b> C indexes {@code p->upkeep->quiver[i]} over a fixed
 * {@code z_info->quiver_size} slots and reads a {@code NULL} entry as an empty one. The empty slots
 * are not padding — {@code quiver_absorb_num} counts them, and a displaced stack can only be moved
 * aside if one is free. Several tests below turn on exactly that, so they are also what pins the
 * quiver to a fixed array rather than a list that grows.
 *
 * <p><b>Two constants, one letter apart in meaning.</b> {@code carry-cap:quiver-size} is the number
 * of slots (10) and {@code carry-cap:quiver-slot-size} is how many fit in one (40). Every capacity
 * figure here wants the second. The tests use sizes above 10 throughout, so that reaching for the
 * first shows up as a failure rather than as an answer that happens to agree.
 *
 * <p>Class PlayerQuiverCapacityTest coded on 260824, commented in full on 260824, retargeted
 * from {@link Player} to {@link ObjectUtils} on 260901.
 *
 * @author Rowan Crowther
 */
@DisplayName("ObjectUtils quiver and pack capacity")
class PlayerQuiverCapacityTest {

    /**
     * The real {@code carry-cap:pack-size}.
     */
    private static final int PACK_SIZE = 23;

    /**
     * The real {@code carry-cap:quiver-size}: how many slots the quiver has.
     */
    private static final int QUIVER_SLOTS = 10;

    /**
     * The real {@code carry-cap:quiver-slot-size}: how many items one slot holds.
     */
    private static final int SLOT_SIZE = 40;

    /**
     * The real {@code carry-cap:thrown-quiver-mult}.
     */
    private static final int THROWN_MULT = 5;

    private static Player savedPlayer;
    private static Object savedConstants;
    private static Object savedBodies;
    private static Object savedRaces;
    private static ObjectKind kind;

    private Player player;

    @BeforeAll
    static void seedGlobals() throws Exception {
        savedConstants = setStatic(GameConstants.class, "data", new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(PACK_SIZE, QUIVER_SLOTS, SLOT_SIZE, THROWN_MULT, 16),
                null, null, null, null, null, null, null, null, null, null, null));

        savedBodies = registryField("playerBodies").get(null);
        savedRaces = registryField("playerRaces").get(null);
        PlayerBody humanoid = new PlayerBody("test",
                List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        registryField("playerBodies").set(null, new ArrayList<>(List.of(humanoid)));
        registryField("playerRaces").set(null, new ArrayList<>(List.of(testRace(humanoid))));

        savedPlayer = GameState.getPlayer();

        kind = ItemFixture.kindWithBase(TValue.TV_ARROW, "arrow", 99);
    }

    @AfterAll
    static void restoreGlobals() throws Exception {
        GameState.setPlayer(savedPlayer);
        setStatic(GameConstants.class, "data", savedConstants);
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
    }

    /**
     * Builds a stack of arrows, sharing the fixture kind so that stacks are mutually stackable.
     *
     * @param number the stack size
     * @return the stack
     */
    private static ItemObject arrows(int number) {
        return item(TValue.TV_ARROW, number, false);
    }

    /**
     * Builds a stack of throwing flasks, charged at the thrown multiplier.
     *
     * @param number the stack size
     * @return the stack
     */
    private static ItemObject thrown(int number) {
        return item(TValue.TV_FLASK, number, true);
    }

    /**
     * Builds a potion — neither ammunition nor throwing, so it can never reach the quiver.
     *
     * @param number the stack size
     * @return the stack
     */
    private static ItemObject potion(int number) {
        return item(TValue.TV_POTION, number, false);
    }

    /**
     * @param item the item to inscribe
     * @param note the inscription
     * @return the same item, inscribed
     */
    private static ItemObject inscribed(ItemObject item, String note) {
        item.setNote(note);
        return item;
    }

    /**
     * Builds an item with the fields the capacity helpers read.
     *
     * @param tValue   the item type
     * @param number   the stack size
     * @param throwing whether to raise {@code OF_THROWING}
     * @return the item
     */
    private static ItemObject item(TValue tValue, int number, boolean throwing) {
        ItemFixture fixture = ItemFixture.item(tValue).kind(kind).number(number);
        if (throwing) {
            fixture.flags(ObjectFlag.OF_THROWING);
        }
        // quiverAbsorbNum asks objectStackable, and so similar, about every occupied slot. similar
        // walks the element and modifier maps, the brands and the slays in full without null checks,
        // which is why the fixture fills them rather than leaving them out.
        return fixture.build();
    }

    /**
     * Builds the one race the seeded registry holds; only its body is read.
     *
     * @param body the body the race presents
     * @return the race
     */
    private static PlayerRace testRace(PlayerBody body) {
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body,
                Map.of(), Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, Map.of());
    }

    /**
     * @param fieldName the registry field to reach
     * @return that private static field, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field registryField(String fieldName) throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * A fresh player with empty gear and an empty quiver, installed as the live player because the
     * code under test reaches through {@link GameState}.
     */
    @BeforeEach
    void buildPlayer() {
        player = new Player();
        set(player, "gear", new ArrayList<ItemObject>());
        GameState.setPlayer(player);
    }

    /**
     * Calls {@link ObjectUtils#packSlotsUsed} against the player under test.
     *
     * @return the number of pack slots in use
     */
    private int packSlotsUsed() {
        return ObjectUtils.packSlotsUsed(player);
    }

    /**
     * Calls {@link ObjectUtils#preferredQuiverSlot} against the player under test.
     *
     * <p>The player is a parameter of the method rather than its receiver, and it is read: the fire
     * key the inscription is matched against depends on the roguelike-keyset option.
     *
     * @param item the object whose inscription is read
     * @return the slot asked for, or {@code -1}
     */
    private int preferredQuiverSlot(ItemObject item) {
        return ObjectUtils.preferredQuiverSlot(player, item);
    }

    /**
     * Calls the private {@code quiverAbsorbNum}, building the {@code SplitBetweenPackAndQuiver} it
     * takes and unpacking the one it returns.
     *
     * <p>The record is private to {@link ObjectUtils} too, so it is reached by name through the
     * enclosing class's declared types rather than imported. The method is static now, so the
     * receiver passed to {@code invoke} is {@code null} and the player travels as an argument.
     *
     * @param item        the object being offered to the quiver
     * @param numToQuiver the incoming quiver figure, which the method does not read
     * @param noToPack    how many extra pack slots the quiver may claim
     * @return the number admitted, then the pack slots left unspent
     */
    private int[] quiverAbsorbNum(ItemObject item, int numToQuiver, int noToPack) {
        try {
            Class<?> splitType = null;
            for (Class<?> declared : ObjectUtils.class.getDeclaredClasses()) {
                if (declared.getSimpleName().equals("SplitBetweenPackAndQuiver")) {
                    splitType = declared;
                }
            }
            if (splitType == null) {
                throw new AssertionError("ObjectUtils.SplitBetweenPackAndQuiver no longer exists");
            }
            var ctor = splitType.getDeclaredConstructor(int.class, int.class);
            ctor.setAccessible(true);
            Object in = ctor.newInstance(numToQuiver, noToPack);

            Method method = ObjectUtils.class.getDeclaredMethod("quiverAbsorbNum", Player.class,
                    ItemObject.class, splitType);
            method.setAccessible(true);
            Object out = method.invoke(null, player, item, in);

            Method quiverPart = splitType.getDeclaredMethod("numToQuiver");
            Method packPart = splitType.getDeclaredMethod("noToPack");
            quiverPart.setAccessible(true);
            packPart.setAccessible(true);
            return new int[]{(Integer) quiverPart.invoke(out), (Integer) packPart.invoke(out)};
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException cause) {
                throw cause;
            }
            throw new AssertionError(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(
                    "ObjectUtils.quiverAbsorbNum is no longer callable by reflection", e);
        }
    }

    /**
     * @return the player's gear list, the same list {@code buildPlayer} installed
     */
    private List<ItemObject> gear() {
        return player.getGear();
    }

    /**
     * @return the player's quiver slots
     */
    private ItemObject[] quiver() {
        return player.getPlayerUpkeep().getQuiver();
    }

    /**
     * Raises one of the player's boolean options.
     *
     * <p>{@link PlayerOptions} exposes only {@code has} — the options are set during birth and read
     * thereafter — so the flag set behind it is reached directly.
     *
     * @param option the option to switch on
     */
    @SuppressWarnings("unchecked")
    private void switchOn(PlayerOptionEnum option) {
        try {
            Field field = PlayerOptions.class.getDeclaredField("options");
            field.setAccessible(true);
            ((Flag<PlayerOptionEnum>) field.get(player.getPlayerOptions())).on(option);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("PlayerOptions.options is no longer reachable by reflection", e);
        }
    }

    /**
     * Puts an item in the body's one equipment slot, so {@code itemIsEquipped} finds it.
     *
     * <p>{@link EquipSlot} has no setter — wielding goes through the equip commands — so the field
     * is written directly rather than dragging that machinery into a capacity test.
     *
     * @param item the item to equip
     */
    private void equip(ItemObject item) {
        set(player.getPlayerBody().getSlots().get(0), "item", item);
    }

    /**
     * The pack-slot count, which is what tells {@code invenCanStackPartial} how much room is left.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("packSlotsUsed")
    class PackSlots {

        @Test
        @DisplayName("empty gear uses no slots")
        void emptyGearUsesNothing() {
            assertEquals(0, packSlotsUsed());
        }

        @Test
        @DisplayName("each ordinary item costs one slot")
        void ordinaryItemsCostOneEach() {
            gear().add(potion(1));
            gear().add(potion(5));

            assertEquals(2, packSlotsUsed());
        }

        /**
         * Equipped items are carried on the body, not in the pack, so they cost nothing. An inverted
         * test here counts equipment instead of pack contents, and the free-slot figure the caller
         * derives from it becomes unrelated to the pack.
         */
        @Test
        @DisplayName("equipped items cost nothing")
        void equippedItemsCostNothing() {
            ItemObject sword = potion(1);
            gear().add(sword);
            equip(sword);

            assertEquals(0, packSlotsUsed());
        }

        /**
         * A stack in the quiver is charged by volume rather than by the slot it sits in: 40 arrows
         * are one slot's worth, so one pack slot.
         */
        @Test
        @DisplayName("a full quiver slot costs one pack slot")
        void fullQuiverSlotCostsOne() {
            ItemObject shafts = arrows(SLOT_SIZE);
            gear().add(shafts);
            quiver()[0] = shafts;

            assertEquals(1, packSlotsUsed());
        }

        /**
         * A part-used slot still costs a whole pack slot, which is the remainder clause. Both the
         * division and the remainder have to be taken over the slot size; taking one over the slot
         * count and the other over the slot size is the shape of the bug, and it shows here as 5
         * rather than 2.
         */
        @Test
        @DisplayName("a part-used quiver slot still costs a whole pack slot")
        void partUsedQuiverSlotCostsOne() {
            ItemObject shafts = arrows(SLOT_SIZE + 1);
            gear().add(shafts);
            quiver()[0] = shafts;

            assertEquals(2, packSlotsUsed());
        }

        /**
         * Being ammunition is not enough — the item has to be in the quiver. Arrows sitting in the
         * pack are an ordinary pack item, so a scan that acted on the first quiver entry without
         * checking it is the item in hand would charge these by volume instead of at one slot.
         */
        @Test
        @DisplayName("ammunition not in the quiver costs one pack slot like anything else")
        void looseAmmoCostsOneSlot() {
            ItemObject loose = arrows(SLOT_SIZE);
            ItemObject quivered = arrows(SLOT_SIZE);
            gear().add(loose);
            gear().add(quivered);
            quiver()[0] = quivered;

            // One for the quivered stack's slot, one for the loose stack.
            assertEquals(2, packSlotsUsed());
        }

        /**
         * Thrown weapons are charged {@code thrown-quiver-mult} apiece, so 8 of them fill a slot.
         */
        @Test
        @DisplayName("thrown weapons are charged at the thrown multiplier")
        void thrownChargedAtMultiplier() {
            ItemObject flasks = thrown(SLOT_SIZE / THROWN_MULT);
            gear().add(flasks);
            quiver()[0] = flasks;

            assertEquals(1, packSlotsUsed());
        }
    }

    /**
     * The inscription that asks for a particular quiver slot.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("preferredQuiverSlot")
    class PreferredSlot {

        @Test
        @DisplayName("an uninscribed stack prefers no slot")
        void uninscribedPrefersNothing() {
            assertEquals(-1, preferredQuiverSlot(arrows(1)));
        }

        @Test
        @DisplayName("@f3 asks for slot 3")
        void fireTagNamesSlot() {
            assertEquals(3, preferredQuiverSlot(inscribed(arrows(1), "@f3")));
        }

        @Test
        @DisplayName("@v2 asks for slot 2, the throw key")
        void throwTagNamesSlot() {
            assertEquals(2, preferredQuiverSlot(inscribed(arrows(1), "@v2")));
        }

        /**
         * The scan restarts from each {@code @} in turn rather than giving up after the first, so a
         * quiver tag still counts when an unrelated tag comes before it.
         */
        @Test
        @DisplayName("a quiver tag after another tag is still found")
        void laterTagIsFound() {
            assertEquals(4, preferredQuiverSlot(inscribed(arrows(1), "@m1@f4")));
        }

        @Test
        @DisplayName("an inscription with no quiver tag asks for nothing")
        void unrelatedInscriptionPrefersNothing() {
            assertEquals(-1, preferredQuiverSlot(inscribed(arrows(1), "!d")));
        }

        /**
         * Under the roguelike keyset the fire key is {@code t} rather than {@code f}, so the same
         * inscription means different things to two players.
         */
        @Test
        @DisplayName("the roguelike keyset reads @t as the fire tag and @f as nothing")
        void roguelikeKeysetSwapsTheFireKey() {
            switchOn(PlayerOptionEnum.OP_rogue_like_commands);

            assertEquals(5, preferredQuiverSlot(inscribed(arrows(1), "@t5")));
            assertEquals(-1, preferredQuiverSlot(inscribed(arrows(1), "@f5")));
        }

        /**
         * Only ammunition and thrown weapons can ask, since nothing else may go in the quiver at
         * all. A potion inscribed {@code @f1} is asking for something that cannot happen.
         */
        @Test
        @DisplayName("a non-quiver item prefers no slot however it is inscribed")
        void nonQuiverItemPrefersNothing() {
            assertEquals(-1, preferredQuiverSlot(inscribed(potion(1), "@f1")));
        }
    }

    /**
     * How much of a stack the quiver could take, and what it would cost the pack.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("quiverAbsorbNum")
    class QuiverAbsorb {

        /**
         * A potion cannot go in the quiver at all, so nothing is admitted and the offered pack slots
         * come back untouched.
         */
        @Test
        @DisplayName("a non-quiver item is refused and the offered slots are handed back")
        void nonQuiverItemRefused() {
            int[] split = quiverAbsorbNum(potion(10), 0, 3);

            assertEquals(0, split[0]);
            assertEquals(3, split[1]);
        }

        /**
         * An empty quiver with a pack slot to spend takes a whole slot's worth. The first slot is
         * free — {@code quiverCount % slotSize} is zero, so nothing is available without paying —
         * and the one offered pack slot buys the 40.
         */
        @Test
        @DisplayName("one pack slot buys one slot's worth of arrows")
        void onePackSlotBuysOneQuiverSlot() {
            int[] split = quiverAbsorbNum(arrows(SLOT_SIZE), 0, 1);

            assertEquals(SLOT_SIZE, split[0]);
            assertEquals(0, split[1]);
        }

        /**
         * With no pack slots on offer and nothing part-used in the quiver, there is nowhere to put
         * anything: every quiver slot has to be paid for out of the pack.
         */
        @Test
        @DisplayName("with no pack slots offered, an empty quiver admits nothing")
        void noPackSlotsAdmitsNothing() {
            int[] split = quiverAbsorbNum(arrows(SLOT_SIZE), 0, 0);

            assertEquals(0, split[0]);
            assertEquals(0, split[1]);
        }

        /**
         * The part-used slot at the top of the quiver is free, because the pack has already been
         * charged for it. Ten arrows already there leave 30 that cost nothing.
         */
        @Test
        @DisplayName("the remainder of a part-used slot is free")
        void partUsedSlotIsFree() {
            ItemObject existing = arrows(10);
            quiver()[0] = existing;

            int[] split = quiverAbsorbNum(arrows(SLOT_SIZE), 0, 0);

            assertEquals(SLOT_SIZE - 10, split[0]);
        }

        /**
         * A stack cannot take more than it has, however much room there is.
         */
        @Test
        @DisplayName("no more is admitted than the stack holds")
        void neverMoreThanTheStack() {
            int[] split = quiverAbsorbNum(arrows(3), 0, 5);

            assertEquals(3, split[0]);
        }

        /**
         * Thrown weapons cost the multiplier apiece, so the same free slot takes 8 of them rather
         * than 40.
         *
         * <p>The stack has to name the slot it wants. C lets ammunition fall into any empty slot but
         * restricts a non-ammo thrown item to its preferred one, so an uninscribed flask has nowhere
         * to go even in a wholly empty quiver — which is the next test.
         */
        @Test
        @DisplayName("thrown weapons are admitted at the thrown multiplier")
        void thrownAdmittedAtMultiplier() {
            int[] split = quiverAbsorbNum(inscribed(thrown(SLOT_SIZE), "@v0"), 0, 1);

            assertEquals(SLOT_SIZE / THROWN_MULT, split[0]);
        }

        /**
         * The restriction itself. An empty quiver is not room for a thrown weapon that has not said
         * where it wants to sit, because thrown weapons would otherwise scatter through the quiver
         * and displace the ammunition the player is relying on finding by slot number.
         */
        @Test
        @DisplayName("an uninscribed thrown weapon has no slot to go in")
        void uninscribedThrownHasNoSlot() {
            int[] split = quiverAbsorbNum(thrown(SLOT_SIZE), 0, 1);

            assertEquals(0, split[0]);
        }

        /**
         * A slot holding something that does not stack with the offered object is not room, so a
         * full quiver of other ammunition admits nothing.
         *
         * <p>This is also the test that needs the quiver to be fixed slots holding nulls. If it were
         * a list, "all ten slots occupied" could not be expressed, and the empty-slot count the
         * method derives its free space from would be meaningless.
         */
        @Test
        @DisplayName("a quiver full of other stacks has no room")
        void fullQuiverHasNoRoom() {
            for (int slot = 0; slot < QUIVER_SLOTS; slot++) {
                ItemObject other = arrows(SLOT_SIZE);
                // A separate kind, so nothing offered will stack with it.
                set(other, "kind", new ObjectKind());
                quiver()[slot] = other;
            }

            int[] split = quiverAbsorbNum(arrows(10), 0, 5);

            assertEquals(0, split[0]);
        }

        /**
         * C asserts that no slot holds more than a slot's worth, and the port throws rather than
         * returning a sentinel — a {@code -1} here would reach the caller's
         * {@code numToQuiver <= 0} test and be read as an ordinary "the quiver is full", which is
         * indistinguishable from the real answer.
         */
        @Test
        @DisplayName("a slot holding more than a slot's worth throws rather than answering")
        void overfullSlotThrows() {
            ItemObject impossible = arrows(SLOT_SIZE + 1);
            set(impossible, "kind", kind);
            quiver()[0] = impossible;

            assertThrows(RuntimeException.class, () -> quiverAbsorbNum(arrows(1), 0, 1));
        }

        /**
         * A legitimately full slot is not that error. Forty arrows is exactly a slot's worth, and
         * the method has to answer "no room" rather than throw — a check written with the wrong
         * comparison, or against the slot count instead of the slot size, trips here.
         */
        @Test
        @DisplayName("a legitimately full slot answers no room rather than throwing")
        void fullSlotIsNotAnError() {
            ItemObject full = arrows(SLOT_SIZE);
            set(full, "kind", kind);
            quiver()[0] = full;

            int[] split = quiverAbsorbNum(arrows(1), 0, 0);

            assertEquals(0, split[0]);
        }
    }
}
