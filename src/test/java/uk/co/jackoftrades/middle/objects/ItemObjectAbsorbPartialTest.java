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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.CarryCapData;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectStackEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.EquipSlot;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerBody;
import uk.co.jackoftrades.middle.player.PlayerRace;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static uk.co.jackoftrades.testsupport.ItemFixture.setStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link ItemObject#objectAbsorbPartial}, the port of C's {@code object_absorb_partial}
 * ({@code obj-pile.c:624}) — the split that moves as much of one stack onto another as the limits
 * allow, leaving both alive.
 *
 * <p><b>Conservation is the property under test.</b> Every case below asserts the two new counts
 * individually <em>and</em> asserts that they still add up to what went in. That second assertion is
 * the one that earns its place: the four branches each compute the pair from {@code smallest},
 * {@code largest} and a limit, and getting a variable wrong in one of them produces counts that look
 * plausible in isolation while quietly creating or destroying items on every pack shuffle. A player
 * would see their arrows multiply, not an exception.
 *
 * <p>The four branches are the four ways the two stacks can sit relative to the quiver, and they do
 * not compute the same thing: the quiver limit is per slot and depends on whether the object is
 * ammunition, while the pack limit is the kind's own maximum. The mixed cases are the awkward pair,
 * because the limit belongs to one stack and the overflow lands on the other, so both orders are
 * tested rather than assuming symmetry.
 *
 * <p>The three impossible states are tested too. C asserts on them; the port throws, and it must —
 * a quiet return would leave the caller believing a split had happened when neither count was
 * touched, and {@code combinePack} would then write its "just in case" count alignment over stacks
 * that were never split. So the tests assert a throw, not a no-op.
 *
 * <p><b>On the fixture.</b> Neither item is given a {@code known} half, so
 * {@code objectAbsorbMerge}'s knowledge branch stays out of the way; the arithmetic is what is being
 * measured. Both are given the same origin and origin race, because {@code originCombine} runs at
 * the end of every call and dereferences {@code originRace} without a null check.
 *
 * <p>Class ItemObjectAbsorbPartialTest coded on 260824, commented in full on 260824.
 *
 * @author Rowan Crowther
 */
@DisplayName("ItemObject objectAbsorbPartial")
class ItemObjectAbsorbPartialTest {

    /**
     * The real {@code carry-cap:quiver-slot-size}: how much one quiver slot holds.
     */
    private static final int SLOT_SIZE = 40;

    /**
     * The real {@code carry-cap:thrown-quiver-mult}: what a thrown weapon costs per item.
     */
    private static final int THROWN_MULT = 5;

    /**
     * The kind's maximum stack. Well clear of {@link #SLOT_SIZE} in both directions, so a test that
     * means to hit the quiver limit cannot pass by hitting this one.
     */
    private static final int MAX_STACK = 99;

    /**
     * A quiver slot's worth of thrown weapons: 40 / 5.
     */
    private static final int THROWN_LIMIT = SLOT_SIZE / THROWN_MULT;

    private static Player savedPlayer;
    private static Object savedConstants;
    private static Object savedBodies;
    private static Object savedRaces;

    /**
     * The kind both fixture items share, carrying the base whose max stack the pack branch reads.
     */
    private static ObjectKind kind;

    /**
     * The origin race both fixture items share, so {@code originCombine} takes its no-change path.
     */
    private static MonsterRace origin;

    @BeforeAll
    static void seedGlobals() throws Exception {
        savedConstants = setStatic(GameConstants.class, "data", new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, SLOT_SIZE, THROWN_MULT, 16),
                null, null, null, null, null, null, null, null, null, null, null));

        savedBodies = registryField("playerBodies").get(null);
        savedRaces = registryField("playerRaces").get(null);
        PlayerBody humanoid = new PlayerBody("test",
                List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        registryField("playerBodies").set(null, new ArrayList<>(List.of(humanoid)));
        registryField("playerRaces").set(null, new ArrayList<>(List.of(testRace(humanoid))));

        savedPlayer = GameState.getPlayer();
        GameState.setPlayer(new Player());

        kind = ItemFixture.kindWithBase(TValue.TV_ARROW, "arrow", MAX_STACK);
        origin = new MonsterRace();
    }

    @AfterAll
    static void restoreGlobals() throws Exception {
        GameState.setPlayer(savedPlayer);
        setStatic(GameConstants.class, "data", savedConstants);
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
    }

    /**
     * Builds a stack of arrows — ammunition, costing one slot-unit apiece.
     *
     * @param number the stack size
     * @return the stack
     */
    private static ItemObject arrows(int number) {
        return item(TValue.TV_ARROW, number, false);
    }

    /**
     * Builds a stack of throwing flasks — not ammunition, so each costs
     * {@code carry-cap:thrown-quiver-mult} of a slot.
     *
     * @param number the stack size
     * @return the stack
     */
    private static ItemObject thrown(int number) {
        return item(TValue.TV_FLASK, number, true);
    }

    /**
     * Builds a stack with the fields the split and its tail read: a kind for {@code max_stack}, a
     * tval to decide the quiver multiplier, a count, and the origin pair {@code originCombine}
     * dereferences.
     *
     * @param tValue   the item type
     * @param number   the stack size
     * @param throwing whether to raise {@code OF_THROWING}
     * @return the stack
     */
    private static ItemObject item(TValue tValue, int number, boolean throwing) {
        ItemFixture fixture = ItemFixture.item(tValue).kind(kind).number(number)
                // originCombine runs at the end of every call and reads both without a null check.
                // Sharing one race and one origin sends it down its no-change path, which is what
                // keeps these tests about the arithmetic.
                .origin(ObjectOriginEnum.ORIGIN_FLOOR, 1, origin);
        if (throwing) {
            fixture.flags(ObjectFlag.OF_THROWING);
        }
        return fixture.build();
    }

    /**
     * @return a flag set holding only {@code OSTACK_PACK}
     */
    private static Flag<ObjectStackEnum> pack() {
        return mode(ObjectStackEnum.OSTACK_PACK);
    }

    /**
     * @return a flag set holding only {@code OSTACK_QUIVER}
     */
    private static Flag<ObjectStackEnum> quiver() {
        return mode(ObjectStackEnum.OSTACK_QUIVER);
    }

    /**
     * @return a flag set holding only {@code OSTACK_STORE}
     */
    private static Flag<ObjectStackEnum> store() {
        return mode(ObjectStackEnum.OSTACK_STORE);
    }

    /**
     * @param stackMode the mode to raise
     * @return a flag set with that one mode on
     */
    private static Flag<ObjectStackEnum> mode(ObjectStackEnum stackMode) {
        Flag<ObjectStackEnum> flags = new Flag<>(ObjectStackEnum.class);
        flags.on(stackMode);
        return flags;
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
     * Neither stack in the quiver, so the kind's own maximum is the only limit.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("both stacks in the pack")
    class PackToPack {

        /**
         * The ordinary case. The larger stack fills to {@code max_stack} and the smaller keeps what
         * is left over — 90 + 20 becomes 99 + 11.
         */
        @Test
        @DisplayName("the receiving stack fills to max stack and the rest stays behind")
        void fillsToMaxStack() {
            ItemObject receiver = arrows(90);
            ItemObject donor = arrows(20);

            receiver.objectAbsorbPartial(donor, pack(), pack());

            assertAll(
                    () -> assertEquals(MAX_STACK, receiver.getNumber()),
                    () -> assertEquals(110 - MAX_STACK, donor.getNumber()),
                    () -> assertEquals(110, receiver.getNumber() + donor.getNumber()));
        }

        /**
         * The same split when the donor is the larger of the two. The method works from
         * {@code smallest} and {@code largest} rather than from which argument is which, so the
         * answer must not depend on the order.
         */
        @Test
        @DisplayName("the split does not depend on which stack is larger")
        void orderDoesNotMatter() {
            ItemObject receiver = arrows(20);
            ItemObject donor = arrows(90);

            receiver.objectAbsorbPartial(donor, pack(), pack());

            assertAll(
                    () -> assertEquals(MAX_STACK, receiver.getNumber()),
                    () -> assertEquals(110 - MAX_STACK, donor.getNumber()),
                    () -> assertEquals(110, receiver.getNumber() + donor.getNumber()));
        }

        /**
         * The conservation property on its own, stated over a spread of sizes. A branch computing
         * the second size from the wrong variable passes the individual assertions above for some
         * inputs and fails this for nearly all of them.
         */
        @Test
        @DisplayName("no items are created or destroyed, whatever the sizes")
        void countIsConserved() {
            for (int donorSize = 1; donorSize <= 40; donorSize += 7) {
                ItemObject receiver = arrows(80);
                ItemObject donor = arrows(donorSize);
                int total = 80 + donorSize;

                receiver.objectAbsorbPartial(donor, pack(), pack());

                assertEquals(total, receiver.getNumber() + donor.getNumber(),
                        "total changed when splitting 80 and " + donorSize);
            }
        }
    }

    /**
     * Both stacks in the quiver, where the per-slot limit replaces the kind's maximum.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("both stacks in the quiver")
    class QuiverToQuiver {

        /**
         * Ammunition is charged one slot-unit apiece, so the receiving stack fills to the full 40. A
         * port reading {@code carry-cap:quiver-size} here would stop at 10.
         */
        @Test
        @DisplayName("ammunition fills the slot to quiver-slot-size")
        void ammoFillsSlot() {
            ItemObject receiver = arrows(30);
            ItemObject donor = arrows(25);

            receiver.objectAbsorbPartial(donor, quiver(), quiver());

            assertAll(
                    () -> assertEquals(SLOT_SIZE, receiver.getNumber()),
                    () -> assertEquals(55 - SLOT_SIZE, donor.getNumber()),
                    () -> assertEquals(55, receiver.getNumber() + donor.getNumber()));
        }

        /**
         * A thrown weapon costs {@code thrown-quiver-mult} of the slot per item, so the same slot
         * holds 8 of them rather than 40.
         */
        @Test
        @DisplayName("thrown weapons fill the slot to slot-size over the thrown multiplier")
        void thrownFillsSlot() {
            ItemObject receiver = thrown(6);
            ItemObject donor = thrown(5);

            receiver.objectAbsorbPartial(donor, quiver(), quiver());

            assertAll(
                    () -> assertEquals(THROWN_LIMIT, receiver.getNumber()),
                    () -> assertEquals(11 - THROWN_LIMIT, donor.getNumber()),
                    () -> assertEquals(11, receiver.getNumber() + donor.getNumber()));
        }
    }

    /**
     * One stack in the quiver and one outside it — the two branches where the limit belongs to one
     * stack and the overflow lands on the other.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("one stack in the quiver")
    class MixedModes {

        /**
         * Receiver in the quiver: it takes exactly a slot's worth and the donor keeps the remainder.
         */
        @Test
        @DisplayName("a quiver receiver takes exactly one slot's worth")
        void quiverReceiverTakesASlot() {
            ItemObject receiver = arrows(30);
            ItemObject donor = arrows(25);

            receiver.objectAbsorbPartial(donor, quiver(), pack());

            assertAll(
                    () -> assertEquals(SLOT_SIZE, receiver.getNumber()),
                    () -> assertEquals(55 - SLOT_SIZE, donor.getNumber()),
                    () -> assertEquals(55, receiver.getNumber() + donor.getNumber()));
        }

        /**
         * The mirror case: the donor is the quiver stack, so it is the one held to a slot's worth
         * and the receiver takes everything above it.
         */
        @Test
        @DisplayName("a quiver donor is the one held to one slot's worth")
        void quiverDonorKeepsASlot() {
            ItemObject receiver = arrows(30);
            ItemObject donor = arrows(25);

            receiver.objectAbsorbPartial(donor, pack(), quiver());

            assertAll(
                    () -> assertEquals(55 - SLOT_SIZE, receiver.getNumber()),
                    () -> assertEquals(SLOT_SIZE, donor.getNumber()),
                    () -> assertEquals(55, receiver.getNumber() + donor.getNumber()));
        }

        /**
         * The limit follows the stack the quiver mode applies to, not the other one. Here the quiver
         * stack is thrown and so is held to 8, even though the pair would fit 40 as ammunition.
         */
        @Test
        @DisplayName("the limit is taken from whichever stack is in the quiver")
        void limitFollowsTheQuiverStack() {
            ItemObject receiver = thrown(6);
            ItemObject donor = thrown(5);

            receiver.objectAbsorbPartial(donor, quiver(), pack());

            assertAll(
                    () -> assertEquals(THROWN_LIMIT, receiver.getNumber()),
                    () -> assertEquals(11 - THROWN_LIMIT, donor.getNumber()),
                    () -> assertEquals(11, receiver.getNumber() + donor.getNumber()));
        }

        /**
         * The overflow has to fit the pack, and when it does not this is an impossible state rather
         * than a case to handle: {@code invenCanStackPartial} should never have let the caller get
         * here. C asserts; the port throws.
         *
         * <p>The two sizes are chosen so that the part landing in the pack — everything above one
         * quiver slot — exceeds {@code max_stack}. A port checking the other of the two sizes would
         * be looking at the slot limit, which cannot exceed the maximum, and so would never fire.
         */
        @Test
        @DisplayName("an overflow larger than the kind's max stack throws")
        void packOverflowThrows() {
            ItemObject receiver = arrows(MAX_STACK);
            ItemObject donor = arrows(SLOT_SIZE + 1);

            assertThrows(RuntimeException.class,
                    () -> receiver.objectAbsorbPartial(donor, quiver(), pack()));
        }

        @Test
        @DisplayName("the same overflow throws with the modes the other way round")
        void packOverflowThrowsEitherOrder() {
            ItemObject receiver = arrows(MAX_STACK);
            ItemObject donor = arrows(SLOT_SIZE + 1);

            assertThrows(RuntimeException.class,
                    () -> receiver.objectAbsorbPartial(donor, pack(), quiver()));
        }
    }

    /**
     * The precondition the caller is required to guarantee.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("store mode")
    class StoreMode {

        /**
         * A store stack has no size limit, so there is nothing for a partial split to compute
         * against and the caller has made a mistake. Throwing is what makes that visible; returning
         * quietly would leave both counts untouched and the caller none the wiser.
         */
        @Test
        @DisplayName("store mode on the first stack throws")
        void storeFirstThrows() {
            ItemObject receiver = arrows(10);
            ItemObject donor = arrows(10);

            assertThrows(RuntimeException.class,
                    () -> receiver.objectAbsorbPartial(donor, store(), pack()));
        }

        @Test
        @DisplayName("store mode on the second stack throws")
        void storeSecondThrows() {
            ItemObject receiver = arrows(10);
            ItemObject donor = arrows(10);

            assertThrows(RuntimeException.class,
                    () -> receiver.objectAbsorbPartial(donor, pack(), store()));
        }

        /**
         * The counts must be left exactly as they were. This is the assertion that separates a throw
         * from a half-done split — the throw happens before anything is written, so a caller
         * catching it finds both stacks intact.
         */
        @Test
        @DisplayName("a refused split leaves both counts untouched")
        void refusedSplitChangesNothing() {
            ItemObject receiver = arrows(10);
            ItemObject donor = arrows(7);

            assertThrows(RuntimeException.class,
                    () -> receiver.objectAbsorbPartial(donor, store(), pack()));

            assertAll(
                    () -> assertEquals(10, receiver.getNumber()),
                    () -> assertEquals(7, donor.getNumber()));
        }
    }
}
