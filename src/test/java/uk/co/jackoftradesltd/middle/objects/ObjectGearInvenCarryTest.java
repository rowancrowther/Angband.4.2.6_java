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
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerCalcs;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerNotice;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.setStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code ObjectGear.invenCarry} - the port of C's {@code inven_carry}
 * ({@code obj-gear.c:832}).
 *
 * <p>Four clauses drifted from C while this port was being written, all invisibly - none of them
 * throws on its own, they each either throw on the ordinary case instead of the exceptional one, or
 * silently change what a later read sees. Every nested class below is named for one of the four:
 * {@link Paranoia} pins the over-capacity guard's boundary, {@link Combining} pins that the local
 * {@code obj} ends up aliasing the real gear entry rather than a detached copy, {@link
 * FlavourAwareOnPickup} pins the negation on the auto-identify guard, and {@link AggregateMessage}
 * pins the two asserts in the message block that had their comparisons inverted.
 *
 * <p>The fixture borrows two proven shapes rather than inventing a third: the registry/constants
 * seeding and the {@code potion} builder come from {@link ObjectGearPackTotalTest}, and the two-cave
 * wiring {@code objectAbsorb} needs comes from {@link ItemObjectAbsorbTest}. {@code invenCarry} calls
 * {@code updateStuff} unconditionally, so the player is built through {@link CalcBonusesFixture} as
 * well, which is what lets {@code calcInventory} and {@code calcBonuses} run for real instead of
 * throwing on a half-built character.
 *
 * <p>Class ObjectGearInvenCarryTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@DisplayName("ObjectGear.invenCarry")
class ObjectGearInvenCarryTest {

    /**
     * Small enough that two or three plain items exercise the paranoia guard without a large fixture.
     */
    private static final int PACK_SIZE = 2;

    private static final int QUIVER_SLOTS = 2;
    private static final int SLOT_SIZE = 1;
    private static final int THROWN_MULT = 1;
    private static final int QUIVER_SIZE = 2;

    /**
     * The kind's maximum stack - high enough that no test here is limited by it.
     */
    private static final int MAX_STACK = 40;

    private static Object savedConstants;
    private static Object savedBodies;
    private static Object savedRaces;
    private static Object savedCurses;
    private static Object savedTimedEffects;
    private static Player savedGamePlayer;
    private static Chunk savedGameCave;

    /**
     * The kind every potion fixture shares. {@code similar} compares kinds by identity, so sharing
     * one instance is what makes separately-built stacks mutually stackable.
     */
    private static ObjectKind potionKind;

    private static PlayerBody body;

    private Player player;
    private Chunk level;
    private Chunk known;
    private EventsHandler realBus;
    private CapturingBus bus;

    @BeforeAll
    static void seedGlobals() throws Exception {
        savedConstants = setStatic(GameConstants.class, "data", new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(PACK_SIZE, QUIVER_SLOTS, SLOT_SIZE, THROWN_MULT, QUIVER_SIZE),
                null, null, null, null, null, null, null, null, null, null, null));

        savedBodies = registryField("playerBodies").get(null);
        savedRaces = registryField("playerRaces").get(null);
        body = new PlayerBody("test", List.of(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")));
        registryField("playerBodies").set(null, new ArrayList<>(List.of(body)));
        registryField("playerRaces").set(null, new ArrayList<>(List.of(plainRace(body, new Flag<>(PlayerFlag.class)))));

        savedCurses = objectRegistryField("curses").get(null);
        ObjectRegistry.setCurses(new ArrayList<>());

        savedTimedEffects = registryField("playerTimedEffects").get(null);
        registryField("playerTimedEffects").set(null, new ArrayList<>());

        savedGamePlayer = GameState.getPlayer();
        savedGameCave = GameState.getCave();

        potionKind = ItemFixture.kindWithBase(TValue.TV_POTION, "potion", MAX_STACK);
    }

    @AfterAll
    static void restoreGlobals() throws Exception {
        GameState.setPlayer(savedGamePlayer);
        GameState.setCave(savedGameCave);
        setStatic(GameConstants.class, "data", savedConstants);
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
        objectRegistryField("curses").set(null, savedCurses);
        registryField("playerTimedEffects").set(null, savedTimedEffects);
    }

    /**
     * A race that grants a given set of player flags and nothing else - for the flavour-aware tests,
     * which need {@code PF_KNOW_MUSHROOM} or {@code PF_KNOW_ZAPPER} to reach {@code player}'s
     * calculated state.
     *
     * @param body      the body the race presents
     * @param raceFlags the player flags the race grants
     * @return the race
     */
    private static PlayerRace plainRace(PlayerBody body, Flag<PlayerFlag> raceFlags) {
        Map<Stats, Integer> stats = new java.util.HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new java.util.HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
        }
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body,
                stats, skills, new Flag<>(uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag.class),
                raceFlags, null, Map.of());
    }

    private static Field registryField(String fieldName) throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    private static Field objectRegistryField(String fieldName) throws Exception {
        Field field = ObjectRegistry.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * A fresh player, built through {@link CalcBonusesFixture} so {@code calcBonuses} and
     * {@code calcInventory} - both reached through {@code invenCarry}'s unconditional
     * {@code updateStuff} call - have a character they can run against instead of a half-built one.
     * Also wires the two caves {@code objectAbsorb} needs, and installs the live player as
     * {@link GameState}'s, since {@code similar}, {@code objectStackable} and {@code objectAbsorb}
     * all reach their checks through it.
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

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * Grants the live player one race flag, by rebuilding its race and re-running the real
     * {@code updateBonuses} dispatch so the flag reaches {@code player.hasPlayerFlag} the same way
     * C's {@code calc_bonuses} would - {@code invenCarry} reads the flag before it recalculates
     * anything itself, so the state has to already be right going in.
     *
     * <p>{@code calcBonuses} itself only fills the {@link uk.co.jackoftradesltd.middle.player.PlayerState}
     * it is handed; it is {@link PlayerCalcs#updateBonuses} that copies the result onto
     * {@code player.getPlayerState()}, which is what {@code hasPlayerFlag} actually reads.
     *
     * @param flag the flag to grant
     * @throws Exception if a fixture field cannot be reached
     */
    private void grantRaceFlag(PlayerFlag flag) throws Exception {
        Flag<PlayerFlag> raceFlags = new Flag<>(PlayerFlag.class);
        raceFlags.on(flag);
        set(player, "race", plainRace(body, raceFlags));
        PlayerCalcs.updateBonuses(player);
    }

    /**
     * A bare potion stack, with a known half attached, that never touches the caves - the shape the
     * ordinary insertion path needs, since it does not call {@code objectAbsorb}.
     *
     * @param number the stack size
     * @return the stack
     */
    private ItemObject potion(int number) {
        return ItemFixture.item(TValue.TV_POTION).kind(potionKind).number(number).fullyKnown().build();
    }

    /**
     * A potion stack listed in both caves, the shape {@code objectAbsorb} needs for a clean merge -
     * the combining path's counterpart to {@link #potion(int)}.
     *
     * @param number the stack size
     * @return the stack
     * @throws Exception if a fixture field cannot be reached
     */
    private ItemObject mergeableStack(int number) throws Exception {
        ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(potionKind).number(number)
                .origin(ObjectOriginEnum.ORIGIN_FLOOR, 1, null).fullyKnown().build();
        listInLevel(item);
        listInKnown(item.getKnown());
        return item;
    }

    @SuppressWarnings("unchecked")
    private void listInLevel(ItemObject item) throws Exception {
        Field field = Chunk.class.getDeclaredField("objects");
        field.setAccessible(true);
        ((List<ItemObject>) field.get(level)).add(item);
    }

    @SuppressWarnings("unchecked")
    private void listInKnown(ItemObject item) throws Exception {
        Field field = Chunk.class.getDeclaredField("objects");
        field.setAccessible(true);
        ((List<ItemObject>) field.get(known)).add(item);
    }

    private Pile gear() {
        return player.getGear();
    }

    /**
     * Records every dispatch, mirroring {@code MessageTest}'s capture, so assertions can be made
     * about what {@code invenCarry} announced.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<GameEventType> types = new ArrayList<>();
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
            types.add(eventType);
            payloads.add(data);
        }

        private EventDataMessage lastMessage() {
            return (EventDataMessage) payloads.get(payloads.size() - 1);
        }
    }

    /**
     * The ordinary path: no combine found (or none attempted), so the item is appended to the gear.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("insertion")
    class Insertion {

        @Test
        @DisplayName("the item joins the gear and its weight is added")
        void itemJoinsGearAndWeightIsAdded() {
            ItemObject item = potion(3);
            int before = player.getPlayerUpkeep().getTotalWeight();

            ObjectGear.invenCarry(player, item, false, false);

            assertTrue(gear().contains(item));
            assertEquals(before + item.getNumber() * item.objectWeightOne(),
                    player.getPlayerUpkeep().getTotalWeight());
        }

        @Test
        @DisplayName("cave placement is cleared and the pack notice is raised")
        void cavePlacementClearedAndNoticeRaised() {
            ItemObject item = potion(1);
            item.setGrid(Loc.zero.offset(3, 4));
            item.getKnown().setGrid(Loc.zero.offset(3, 4));

            ObjectGear.invenCarry(player, item, false, false);

            assertTrue(item.getGrid().isZero());
            assertTrue(item.getKnown().getGrid().isZero());
            assertTrue(player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_COMBINE));
        }

        @Test
        @DisplayName("the inventory redraw flag is left set for the caller's redrawStuff pass")
        void redrawFlagIsLeftSet() {
            ObjectGear.invenCarry(player, potion(1), false, false);

            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_INVEN));
        }
    }

    /**
     * The paranoia guard - C's {@code assert(pack_slots_used(p) <= z_info->pack_size)} - and the
     * off-by-one it was fixed for: a pack sitting exactly at capacity must still accept one more
     * insert, since a single over-fill is the caller's job to handle, not this guard's.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("paranoia guard")
    class Paranoia {

        @Test
        @DisplayName("a pack sitting exactly at capacity still accepts the insert")
        void exactlyAtCapacityIsAccepted() {
            gear().insertEnd(potion(1));
            gear().insertEnd(potion(1));
            assertEquals(PACK_SIZE, ObjectUtils.packSlotsUsed(player));

            ItemObject item = potion(1);
            ObjectGear.invenCarry(player, item, false, false);

            assertTrue(gear().contains(item));
        }

        @Test
        @DisplayName("a pack already over capacity refuses the insert")
        void overCapacityIsRefused() {
            gear().insertEnd(potion(1));
            gear().insertEnd(potion(1));
            gear().insertEnd(potion(1));
            assertTrue(ObjectUtils.packSlotsUsed(player) > PACK_SIZE);

            assertThrows(RuntimeException.class,
                    () -> ObjectGear.invenCarry(player, potion(1), false, false));
        }
    }

    /**
     * The combine path, and the fix at its heart: the local {@code obj} is reassigned to alias the
     * gear entry it was merged into ({@code obj = combineObj;}), not a copy of it - a copy would
     * still pass the count assertions below, since a copy's fields agree with the original's, but
     * would desynchronise the reference-identity checks the rest of the method and
     * {@link ObjectGearPackTotalTest} both rely on.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("combining")
    class Combining {

        @Test
        @DisplayName("a mergeable stack absorbs the item instead of a new gear entry appearing")
        void matchingStackIsMergedNotInserted() throws Exception {
            ItemObject existing = mergeableStack(3);
            gear().insertEnd(existing);
            ItemObject incoming = mergeableStack(2);
            int before = player.getPlayerUpkeep().getTotalWeight();

            ObjectGear.invenCarry(player, incoming, true, false);

            assertEquals(1, gear().size(), "the incoming item never gets its own gear entry");
            assertSame(existing, gear().get(0));
            assertEquals(5, existing.getNumber());
            assertEquals(before + 2 * incoming.objectWeightOne(),
                    player.getPlayerUpkeep().getTotalWeight(),
                    "the weight added is the incoming stack's own count, added before the merge");
        }

        @Test
        @DisplayName("an equipped match is skipped, so the item is inserted instead of combined")
        void equippedMatchIsNotCombinedInto() throws Exception {
            ItemObject equipped = mergeableStack(3);
            gear().insertEnd(equipped);
            set(player.getPlayerBody().getSlots().get(0), "item", equipped);
            ItemObject incoming = potion(2);

            ObjectGear.invenCarry(player, incoming, true, false);

            assertEquals(2, gear().size());
            assertTrue(gear().contains(incoming));
            assertEquals(3, equipped.getNumber(), "the equipped stack was never touched");
        }

        @Test
        @DisplayName("the paranoia guard does not run on the combining path")
        void paranoiaGuardIsSkippedWhenCombining() throws Exception {
            // A different kind from potionKind, so these fillers occupy pack slots without being a
            // merge candidate themselves - the combine loop must reach past them to "existing".
            ObjectKind fillerKind = ItemFixture.kindWithBase(TValue.TV_POTION, "filler", MAX_STACK);
            for (int i = 0; i < 3; i++) {
                gear().insertEnd(ItemFixture.item(TValue.TV_POTION).kind(fillerKind).number(1).fullyKnown().build());
            }
            assertTrue(ObjectUtils.packSlotsUsed(player) > PACK_SIZE, "the pack is already over capacity");

            ItemObject existing = mergeableStack(1);
            gear().insertEnd(existing);
            ItemObject incoming = mergeableStack(1);

            ObjectGear.invenCarry(player, incoming, true, false);

            assertEquals(2, existing.getNumber(), "the merge still ran, over-capacity pack notwithstanding");
        }
    }

    /**
     * Hobbits identify an unknown mushroom, and gnomes an unknown wand or staff, the moment either is
     * picked up - gated by C's {@code if (!object_flavor_is_aware(obj))}, whose negation the port
     * dropped and then restored.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("flavour-aware on pickup")
    class FlavourAwareOnPickup {

        @Test
        @DisplayName("a hobbit becomes aware of an unknown mushroom and is told so")
        void hobbitIdentifiesUnknownMushroomAndMessages() throws Exception {
            grantRaceFlag(PlayerFlag.PF_KNOW_MUSHROOM);
            ObjectKind mushroomKind = ItemFixture.kindWithBase(TValue.TV_MUSHROOM, "mushroom", MAX_STACK);
            ItemObject mushroom = ItemFixture.item(TValue.TV_MUSHROOM).kind(mushroomKind).fullyKnown().build();
            assertFalse(mushroomKind.isAware());

            ObjectGear.invenCarry(player, mushroom, false, false);

            assertTrue(mushroomKind.isAware());
            assertEquals("Mushrooms for breakfast!", bus.lastMessage().message());
        }

        @Test
        @DisplayName("an already-aware mushroom is not re-announced")
        void mushroomAlreadyAwareIsNotReannounced() throws Exception {
            grantRaceFlag(PlayerFlag.PF_KNOW_MUSHROOM);
            ObjectKind mushroomKind = ItemFixture.kindWithBase(TValue.TV_MUSHROOM, "mushroom", MAX_STACK);
            mushroomKind.setAware(true);
            ItemObject mushroom = ItemFixture.item(TValue.TV_MUSHROOM).kind(mushroomKind).fullyKnown().build();

            ObjectGear.invenCarry(player, mushroom, false, false);

            assertTrue(bus.payloads.isEmpty(), "no breakfast message, and nothing else, was raised");
        }

        @Test
        @DisplayName("a gnome becomes aware of an unknown wand without a message")
        void gnomeIdentifiesUnknownZapperSilently() throws Exception {
            grantRaceFlag(PlayerFlag.PF_KNOW_ZAPPER);
            ObjectKind wandKind = ItemFixture.kindWithBase(TValue.TV_WAND, "wand", MAX_STACK);
            ItemObject wand = ItemFixture.item(TValue.TV_WAND).kind(wandKind).fullyKnown().build();

            ObjectGear.invenCarry(player, wand, false, false);

            assertTrue(wandKind.isAware());
            assertTrue(bus.payloads.isEmpty(), "the wand case never raises a message of its own");
        }

        @Test
        @DisplayName("without the race flag, an unknown mushroom stays unknown")
        void withoutTheRaceFlagNothingHappens() throws Exception {
            ObjectKind mushroomKind = ItemFixture.kindWithBase(TValue.TV_MUSHROOM, "mushroom", MAX_STACK);
            ItemObject mushroom = ItemFixture.item(TValue.TV_MUSHROOM).kind(mushroomKind).fullyKnown().build();

            ObjectGear.invenCarry(player, mushroom, false, false);

            assertFalse(mushroomKind.isAware());
        }
    }

    /**
     * The message block's two translated asserts - C's {@code assert(first && total >=
     * first->number)} and {@code assert(first == obj)} - which had their comparisons inverted and so
     * threw on the ordinary case (a single matching stack, reporting itself) instead of never firing
     * there at all.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("aggregate message")
    class AggregateMessage {

        @Test
        @DisplayName("the everyday single-stack pickup reports without throwing")
        void singleStackReportsWithoutThrowing() {
            ItemObject item = potion(1);

            ObjectGear.invenCarry(player, item, false, true);

            assertEquals(1, bus.types.size());
            assertEquals(GameEventType.EVENT_MESSAGE, bus.types.get(0));
            assertTrue(bus.lastMessage().message().startsWith("You have "));
            assertFalse(bus.lastMessage().message().contains("1st"),
                    "one stack reporting itself does not get the \"1st\" prefix");
        }
    }
}
