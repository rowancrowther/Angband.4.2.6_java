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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.EventDataMessage;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.KnownObject;
import uk.co.jackoftrades.middle.objects.ObjectProperty;
import uk.co.jackoftrades.middle.objects.Rune;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.CombatRunes;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.RuneVariety;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player}'s object-knowledge path — {@link Player#learnRune}, the wrappers over it,
 * and the two small high-water-mark updaters that sit beside them.
 *
 * <p>{@code learnRune} is the port of C's {@code player_learn_rune} ({@code src/obj-knowledge.c}),
 * and its job is threefold: dispatch to the right corner of {@link KnownObject}, announce a genuine
 * discovery once, and say nothing at all when there was nothing to learn. The dispatch is the part
 * a reader is most likely to doubt, because C reaches its seven cases through a {@code switch} on
 * {@code r->variety} followed by an {@code int} index whose meaning changes per case, while this
 * port matches record patterns over a sealed interface. Every variety is therefore exercised, and
 * each is checked to have touched its own corner and no other.
 *
 * <p>The message assertions matter as much as the state ones. C prints "You have learned the rune
 * of %s." only when {@code learned} came back true, so a learner that reported novelty wrongly
 * would either announce the same rune on every blow or discover it in silence — neither of which
 * any assertion about the knowledge itself would catch.
 *
 * @author ClaudeCode
 */
class PlayerRuneLearningTest {

    /**
     * The registry fields this suite overwrites. Saved and restored by reflection because they are
     * null until something loads them, which the accessors cannot report — see the same note in
     * {@code KnownObjectTest}.
     */
    private static final List<String> SAVED_FIELDS =
            List.of("brands", "slays", "curses", "allRunes", "brandMax", "slayMax", "curseMax");

    private static final Map<String, Object> SAVED = new HashMap<>();

    private static Brand weakAcid;
    private static Brand strongAcid;
    private static Slay evil3;
    private static Slay evil5;
    private static Curse siren;
    private static ObjectProperty strengthProperty;

    private Player player;
    private KnownObject knowledge;
    private CapturingBus bus;
    private EventsHandler realBus;

    /**
     * Seeds the registries with a fixture small enough to reason about: two strengths of one brand,
     * two of one slay, one curse, and the runes covering them. The rune list is what the wrappers
     * resolve through, so it has to agree with the brand and slay lists or
     * {@link Rune#runeIndex(Brand)} will find nothing.
     *
     * @author ClaudeCode
     */
    @BeforeAll
    static void seed() throws Exception {
        for (String name : SAVED_FIELDS) {
            SAVED.put(name, field(name).get(null));
        }

        weakAcid = new Brand("ACID_2", "acid", "burns", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 17, 3, 15);
        strongAcid = new Brand("ACID_3", "acid", "burns", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 17, 3, 15);
        evil3 = new Slay("EVIL_3", "evil", null, "smites", "smites", MonsterRaceFlag.RF_EVIL,
                17, 3, 15);
        evil5 = new Slay("EVIL_5", "evil", null, "smites", "smites", MonsterRaceFlag.RF_EVIL,
                17, 3, 15);
        siren = new Curse("siren", List.of(), 0, null, List.of(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), List.of(), "wakes monsters", "The curse fires.");

        strengthProperty = new ObjectProperty(null, null, null, null, 0, 0, null,
                "strength", null, null, null, null, null);

        ObjectRegistry.setBrands(List.of(weakAcid, strongAcid));
        ObjectRegistry.setSlays(List.of(evil3, evil5));
        ObjectRegistry.setCurses(List.of(siren));

        // One rune per group, holding whichever member is the representative - which is what the
        // real initRunes produces, and what makes the runeIndex lookups worth testing at all.
        ObjectRegistry.setRunes(new ArrayList<>(List.of(
                new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)),
                new Rune(new RuneVariety.BrandKey(weakAcid)),
                new Rune(new RuneVariety.SlayKey(evil3)),
                new Rune(new RuneVariety.CurseKey(siren)))));
    }

    /**
     * @author ClaudeCode
     */
    @AfterAll
    static void restore() throws Exception {
        for (String name : SAVED_FIELDS) {
            field(name).set(null, SAVED.get(name));
        }
    }

    private static Field field(String name) throws NoSuchFieldException {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static void set(Player target, String name, Object value) throws Exception {
        Field f = Player.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static Object get(Player target, String name) throws Exception {
        Field f = Player.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }

    /**
     * A player with a fresh knowledge set, and a bus to catch what the learning announces.
     *
     * <p>{@link Player}'s constructor leaves {@code itemKnowledge} null, matching C, where
     * {@code p->obj_k} is allocated later in {@code init_player} once the registries can size it.
     * Birth would fill it in; this stands in for birth with the one field under test.
     *
     * @author ClaudeCode
     */
    @BeforeEach
    void setUp() throws Exception {
        player = new Player();
        knowledge = new KnownObject();
        set(player, "itemKnowledge", knowledge);

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    /**
     * @author ClaudeCode
     */
    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * Catches what the learning signals. {@link uk.co.jackoftrades.middle.Message} reaches the bus
     * through {@link GameEngine#getEventsBusHandler()}, which is static, so the real one is put
     * back after each test.
     *
     * @author ClaudeCode
     */
    private static final class CapturingBus implements EventsHandler {
        private final List<EventDataMessage> messages = new ArrayList<>();

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
            if (eventType != GameEventType.EVENT_MESSAGE) return;

            assertInstanceOf(EventDataMessage.class, data);
            messages.add((EventDataMessage) data);
        }
    }

    /**
     * Dispatch: each of the seven varieties reaching its own corner of the knowledge, and no other.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("learnRune dispatch")
    class Dispatch {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a combat rune learns only its own bonus")
        void combat() {
            player.learnRune(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)), false);

            assertTrue(knowledge.toHIsKnown());
            assertFalse(knowledge.toDIsKnown());
            assertFalse(knowledge.toAIsKnown());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("each combat rune reaches a different bonus")
        void combatVarietiesAreDistinct() {
            player.learnRune(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)), false);
            assertTrue(knowledge.toDIsKnown());
            assertFalse(knowledge.toAIsKnown());

            player.learnRune(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A)), false);
            assertTrue(knowledge.toAIsKnown());
        }

        /**
         * The sentinel is not a rune. C's chain of {@code if}/{@code else if} falls off the end for
         * it with {@code learned} still false, and nothing is learned or said.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("the combat sentinel learns nothing and says nothing")
        void combatSentinel() {
            player.learnRune(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_MAX)), true);

            assertFalse(knowledge.toHIsKnown());
            assertFalse(knowledge.toDIsKnown());
            assertFalse(knowledge.toAIsKnown());
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a modifier rune learns a modifier")
        void modifier() {
            player.learnRune(new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, strengthProperty)),
                    false);

            assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR));
            assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_INT));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a resist rune learns a resistance")
        void resist() {
            player.learnRune(new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null)), false);

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
            assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_COLD));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a flag rune learns a flag")
        void flag() {
            player.learnRune(new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, strengthProperty)),
                    false);

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_INT));
        }

        /**
         * The rune holds one member of the group, and learning it must reveal the whole group —
         * otherwise a player who has read the acid rune still cannot see a strong acid brand.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a brand rune learns the brand's whole group")
        void brand() {
            player.learnRune(new Rune(new RuneVariety.BrandKey(weakAcid)), false);

            assertTrue(knowledge.brandIsKnown(weakAcid));
            assertTrue(knowledge.brandIsKnown(strongAcid));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a slay rune learns the slay's whole group")
        void slay() {
            player.learnRune(new Rune(new RuneVariety.SlayKey(evil3)), false);

            assertTrue(knowledge.slayIsKnown(evil3));
            assertTrue(knowledge.slayIsKnown(evil5));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a curse rune learns a curse")
        void curse() {
            player.learnRune(new Rune(new RuneVariety.CurseKey(siren)), false);

            assertTrue(knowledge.curseIsKnown(siren));
        }

        /**
         * Null stands in for C's {@code assert} on the rune index — a lookup that found nothing.
         * Being a data-driven failure rather than a programming one, it is logged and dropped
         * instead of thrown, so nothing is learned and the turn survives.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a null rune is ignored rather than thrown on")
        void nullRune() {
            player.learnRune(null, true);

            assertTrue(knowledge.getFlags().isEmpty());
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * The discovery message, which is the only externally visible consequence of a learn returning
     * true.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("the discovery message")
    class Announcement {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("names the rune and is tagged MSG_RUNE")
        void namesTheRune() {
            player.learnRune(new Rune(new RuneVariety.BrandKey(weakAcid)), true);

            assertEquals(1, bus.messages.size());
            assertEquals(MessageType.MSG_RUNE, bus.messages.get(0).type());
            assertEquals("You have learned the rune of acid brand.", bus.messages.get(0).message());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("uses the variety's own name for each kind of rune")
        void usesTheVarietyName() {
            player.learnRune(new Rune(new RuneVariety.SlayKey(evil3)), true);
            player.learnRune(new Rune(new RuneVariety.CurseKey(siren)), true);
            player.learnRune(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)), true);

            assertEquals(List.of(
                            "You have learned the rune of slay evil.",
                            "You have learned the rune of siren curse.",
                            "You have learned the rune of enchantment to hit."),
                    bus.messages.stream().map(EventDataMessage::message).toList());
        }

        /**
         * Learning the same rune again is not a discovery. This is what the {@code if (!learned)
         * return} guard buys, and without it a branded weapon would announce its rune on every blow.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("is not repeated when the rune is already known")
        void notRepeated() {
            player.learnRune(new Rune(new RuneVariety.BrandKey(weakAcid)), true);
            player.learnRune(new Rune(new RuneVariety.BrandKey(weakAcid)), true);

            assertEquals(1, bus.messages.size());
        }

        /**
         * And not for another member of a group already learned, since the group was marked whole.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("is not repeated for another member of a known group")
        void notRepeatedForTheGroup() {
            player.learnRune(new Rune(new RuneVariety.BrandKey(weakAcid)), true);
            player.learnRune(new Rune(new RuneVariety.BrandKey(strongAcid)), true);

            assertEquals(1, bus.messages.size());
        }

        /**
         * The flag exists for the paths that learn in bulk — {@code player_learn_innate_runes} and
         * the equipment sweeps — which would otherwise bury the player under one message per rune.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("is suppressed when the caller asks for silence")
        void suppressed() {
            player.learnRune(new Rune(new RuneVariety.BrandKey(weakAcid)), false);

            assertTrue(knowledge.brandIsKnown(weakAcid));
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * The wrappers, which are the intended way in: each resolves its property to the rune for the
     * property's <em>group</em> before learning it.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("the learn wrappers")
    class Wrappers {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnBrand learns the brand and announces it")
        void learnBrand() {
            player.learnBrand(weakAcid);

            assertTrue(player.knowsBrand(weakAcid));
            assertEquals(1, bus.messages.size());
        }

        /**
         * The reason the wrapper cannot be skipped. {@link Rune#runeIndex(Brand)} matches by name,
         * so a strong acid brand finds the acid rune even though the rune holds the weak one; a
         * caller that built its own rune from the brand it had would learn only that brand.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnBrand resolves a strength that no rune holds")
        void learnBrandResolvesTheGroup() {
            player.learnBrand(strongAcid);

            assertTrue(player.knowsBrand(strongAcid));
            assertTrue(player.knowsBrand(weakAcid));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnBrand does nothing the second time")
        void learnBrandIsIdempotent() {
            player.learnBrand(weakAcid);
            player.learnBrand(weakAcid);

            assertEquals(1, bus.messages.size());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("knowsBrand reports the knowledge, not the item")
        void knowsBrand() {
            assertFalse(player.knowsBrand(weakAcid));

            player.learnBrand(weakAcid);

            assertTrue(player.knowsBrand(weakAcid));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnCurse learns the curse and announces it")
        void learnCurse() {
            player.learnCurse(siren);

            assertTrue(knowledge.curseIsKnown(siren));
            assertEquals(1, bus.messages.size());
            assertEquals("You have learned the rune of siren curse.", bus.messages.get(0).message());
        }

        /**
         * C resolves the curse by name rather than by identity, so one rebuilt from a savefile or a
         * parser is still recognised. An identity match would pass every other test here and fail
         * only in the game.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnCurse matches by name, not identity")
        void learnCurseMatchesByName() {
            Curse rebuilt = new Curse("siren", List.of(), 0, null, List.of(), Map.of(), Map.of(),
                    0, 0, 0, List.of(), List.of(), "wakes monsters", "The curse fires.");

            player.learnCurse(rebuilt);

            assertTrue(knowledge.curseIsKnown(siren));
        }

        /**
         * A curse with no rune reaches {@link Player#learnRune} as null, where C's guard is
         * {@code index >= 0}. Nothing is learned and nothing is said.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnCurse survives a curse with no rune")
        void learnCurseWithoutARune() {
            Curse unknown = new Curse("nowhere", List.of(), 0, null, List.of(), Map.of(), Map.of(),
                    0, 0, 0, List.of(), List.of(), "does nothing", "Nothing happens.");

            player.learnCurse(unknown);

            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * The two high-water marks that sit beside the learning code. Both only ever move one way,
     * which is the whole of their behaviour and the easiest thing to get backwards.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("high-water marks")
    class HighWaterMarks {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("max level rises to the current level")
        void maxLevelRises() throws Exception {
            set(player, "level", 12);

            player.updateMaxLevel();

            assertEquals(12, get(player, "maxLevel"));
        }

        /**
         * Draining a level must not cost the player the record of having reached it — that is what
         * makes it a maximum rather than a copy.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("max level does not fall when the level does")
        void maxLevelDoesNotFall() throws Exception {
            set(player, "level", 12);
            player.updateMaxLevel();

            set(player, "level", 9);
            player.updateMaxLevel();

            assertEquals(12, get(player, "maxLevel"));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("descending moves both the deepest mark and the recall depth")
        void depthMovesRecall() {
            player.setDepth(15);

            player.updateDungeonDepth();

            assertEquals(15, player.getMaxDepth());
            assertEquals(15, player.getRecallDepth());
        }

        /**
         * Climbing back up leaves both where they were, so recall still returns the player to the
         * deepest point rather than to wherever they happen to be standing.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("climbing back up moves neither")
        void climbingLeavesBoth() {
            player.setDepth(15);
            player.updateDungeonDepth();

            player.setDepth(4);
            player.updateDungeonDepth();

            assertEquals(15, player.getMaxDepth());
            assertEquals(15, player.getRecallDepth());
        }

        /**
         * The guard is strictly-greater, so returning to the deepest level already reached is not a
         * new record and changes nothing.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("returning to the same depth changes nothing")
        void equalDepthIsNotARecord() {
            player.setDepth(15);
            player.updateDungeonDepth();
            player.updateDungeonDepth();

            assertEquals(15, player.getMaxDepth());
            assertEquals(15, player.getRecallDepth());
        }
    }
}
