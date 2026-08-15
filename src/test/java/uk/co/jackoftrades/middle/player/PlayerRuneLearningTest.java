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
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
    private static ObjectProperty sustainProperty;

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
        sustainProperty = new ObjectProperty(null, null, null, null, 0, 0, null,
                "sustain strength", null, null, null, null, null);

        ObjectRegistry.setBrands(List.of(weakAcid, strongAcid));
        ObjectRegistry.setSlays(List.of(evil3, evil5));
        ObjectRegistry.setCurses(List.of(siren));

        // One rune per group, holding whichever member is the representative - which is what the
        // real initRunes produces, and what makes the runeIndex lookups worth testing at all.
        // The resist and flag runes are here for the wrappers that resolve through them; the
        // elements and flags with no rune are as real as the ones with, so not everything the
        // learning code walks past is listed.
        ObjectRegistry.setRunes(new ArrayList<>(List.of(
                new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)),
                new Rune(new RuneVariety.BrandKey(weakAcid)),
                new Rune(new RuneVariety.SlayKey(evil3)),
                new Rune(new RuneVariety.CurseKey(siren)),
                new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null)),
                new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, sustainProperty)))));
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

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnSlay learns the slay and announces it")
        void learnSlay() {
            player.learnSlay(evil3);

            assertTrue(player.knowsSlay(evil3));
            assertEquals(1, bus.messages.size());
            assertEquals("You have learned the rune of slay evil.", bus.messages.get(0).message());
        }

        /**
         * The slay counterpart of {@code learnBrandResolvesTheGroup}, and the reason
         * {@link Rune#runeIndex(Slay)} cannot match on a name: the rune holds {@code evil3}, so a
         * player who has just been bitten by {@code evil5} finds it only through
         * {@link Slay#sameMonsterSlain}.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnSlay resolves a strength that no rune holds")
        void learnSlayResolvesTheGroup() {
            player.learnSlay(evil5);

            assertTrue(player.knowsSlay(evil5));
            assertTrue(player.knowsSlay(evil3));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnSlay does nothing the second time")
        void learnSlayIsIdempotent() {
            player.learnSlay(evil3);
            player.learnSlay(evil3);

            assertEquals(1, bus.messages.size());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("knowsSlay reports the knowledge, not the weapon")
        void knowsSlay() {
            assertFalse(player.knowsSlay(evil3));

            player.learnSlay(evil3);

            assertTrue(player.knowsSlay(evil3));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("knowsCurse reports the knowledge, not the item")
        void knowsCurse() {
            assertFalse(player.knowsCurse(siren));

            player.learnCurse(siren);

            assertTrue(player.knowsCurse(siren));
        }

        /**
         * Curses are never grouped, so unlike a brand or a slay there is no second curse to reveal.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("knowsCurse does not answer for a curse never learned")
        void knowsCurseIsNotShared() {
            Curse other = new Curse("teleportation", List.of(), 0, null, List.of(), Map.of(),
                    Map.of(), 0, 0, 0, List.of(), List.of(), "teleports", "The curse fires.");

            player.learnCurse(siren);

            assertFalse(player.knowsCurse(other));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnFlag learns the flag and announces it")
        void learnFlag() {
            player.learnFlag(ObjectFlag.OF_SUST_STR);

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertEquals(1, bus.messages.size());
            assertEquals("You have learned the rune of sustain strength.",
                    bus.messages.get(0).message());
        }

        /**
         * C's {@code player_learn_flag} is the one wrapper with no already-known guard, relying on
         * {@code of_on} to report whether anything changed. The guard this port adds must not
         * change that answer — a flag learned twice is still announced once, either way.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnFlag does nothing the second time")
        void learnFlagIsIdempotent() {
            player.learnFlag(ObjectFlag.OF_SUST_STR);
            player.learnFlag(ObjectFlag.OF_SUST_STR);

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertEquals(1, bus.messages.size());
        }

        /**
         * Not every flag is a learnable property — {@code init_rune} skips the placeholder
         * subtypes, the ones describing the object rather than the player, and the curse-only ones.
         * Asking about one of those is expected rather than exceptional, since the learning code
         * walks whole flag sets, so it answers with silence instead of a throw. C hands
         * {@code rune_index}'s {@code -1} straight to {@code rune_list[-1]}.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnFlag survives a flag with no rune")
        void learnFlagWithoutARune() {
            player.learnFlag(ObjectFlag.OF_FEATHER);

            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_FEATHER));
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learnFlag touches only the flag it was given")
        void learnFlagIsNarrow() {
            player.learnFlag(ObjectFlag.OF_SUST_STR);

            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_INT));
        }
    }

    /**
     * {@link Player#knowsRune}, the mirror of {@link Player#learnRune} and the port of C's
     * {@code player_knows_rune}. Each variety is asked before and after the matching learn, because
     * an arm wired to the wrong corner of the knowledge would answer correctly for whichever
     * property happens to be unlearned and wrongly for the rest.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("knowsRune")
    class KnowsRune {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a combat rune is unknown until learned")
        void combat() {
            Rune toHit = new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H));

            assertFalse(player.knowsRune(toHit));

            player.learnRune(toHit, false);

            assertTrue(player.knowsRune(toHit));
        }

        /**
         * Each of the three enchantments answers for itself; C compares {@code r->index} against
         * three constants in a chain, which is easy to write with two arms reaching the same field.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("the three combat runes answer separately")
        void combatRunesAreDistinct() {
            player.learnRune(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)), false);

            assertAll(
                    () -> assertTrue(player.knowsRune(
                            new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)))),
                    () -> assertFalse(player.knowsRune(
                            new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)))),
                    () -> assertFalse(player.knowsRune(
                            new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A)))));
        }

        /**
         * The sentinel is not a rune and has nothing to know. C's {@code if} chain falls through it
         * to {@code return false}.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("the combat sentinel is never known")
        void combatSentinel() {
            player.learnRune(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)), false);

            assertFalse(player.knowsRune(
                    new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_MAX))));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a modifier rune is unknown until learned")
        void modifier() {
            Rune strength = new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, strengthProperty));

            assertFalse(player.knowsRune(strength));

            player.learnRune(strength, false);

            assertTrue(player.knowsRune(strength));
            assertFalse(player.knowsRune(
                    new Rune(new RuneVariety.ModKey(ObjectModifier.OM_INT, strengthProperty))));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a resist rune is unknown until learned")
        void resist() {
            Rune fire = new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null));

            assertFalse(player.knowsRune(fire));

            player.learnRune(fire, false);

            assertTrue(player.knowsRune(fire));
            assertFalse(player.knowsRune(
                    new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_COLD, null))));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a flag rune is unknown until learned")
        void flag() {
            Rune sustain = new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, sustainProperty));

            assertFalse(player.knowsRune(sustain));

            player.learnRune(sustain, false);

            assertTrue(player.knowsRune(sustain));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a curse rune is unknown until learned")
        void curse() {
            Rune sirenRune = new Rune(new RuneVariety.CurseKey(siren));

            assertFalse(player.knowsRune(sirenRune));

            player.learnRune(sirenRune, false);

            assertTrue(player.knowsRune(sirenRune));
        }

        /**
         * Learning one member of a group makes the group's rune readable whichever member the rune
         * is asked about — which is the whole point of doing the fan-out on the learning side.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a brand rune is known through any member of its group")
        void brandGroup() {
            player.learnBrand(weakAcid);

            assertAll(
                    () -> assertTrue(player.knowsRune(new Rune(new RuneVariety.BrandKey(weakAcid)))),
                    () -> assertTrue(player.knowsRune(new Rune(new RuneVariety.BrandKey(strongAcid)))));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a slay rune is known through any member of its group")
        void slayGroup() {
            player.learnSlay(evil3);

            assertAll(
                    () -> assertTrue(player.knowsRune(new Rune(new RuneVariety.SlayKey(evil3)))),
                    () -> assertTrue(player.knowsRune(new Rune(new RuneVariety.SlayKey(evil5)))));
        }

        /**
         * A rune of one variety must not be answered from another variety's corner of the
         * knowledge. Learning everything but the brand is the arrangement that catches an arm
         * reading the wrong field, because only one answer should still be false.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("each variety answers from its own corner")
        void varietiesDoNotCrossTalk() {
            player.learnRune(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)), false);
            player.learnRune(new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, strengthProperty)), false);
            player.learnRune(new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null)), false);
            player.learnRune(new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, sustainProperty)), false);
            player.learnRune(new Rune(new RuneVariety.CurseKey(siren)), false);
            player.learnRune(new Rune(new RuneVariety.SlayKey(evil3)), false);

            assertFalse(player.knowsRune(new Rune(new RuneVariety.BrandKey(weakAcid))));
        }
    }

    /**
     * {@link Player#learnInnate}, the port of C's {@code player_learn_innate} — the birth-time pass
     * that gives a character the runes for the properties of their own body.
     *
     * <p>The race is installed by reflection because {@link Player} has no setter for it: C assigns
     * {@code p->race} during birth, which this port has not reached yet.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("learnInnate")
    class Innate {

        private PlayerRace race(Map<ElementEnum, ElementInfo> resists, ObjectFlag... innateFlags) {
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            for (ObjectFlag f : innateFlags) {
                oFlags.on(f);
            }

            return new PlayerRace("Test-Race", 1, 10, 100, 20, 10, 70, 6, 150, 25, 0, null,
                    Map.<Stats, Integer>of(), Map.<PlayerSkill, Integer>of(), oFlags,
                    new Flag<>(PlayerFlag.class), null, resists);
        }

        private Map<ElementEnum, ElementInfo> resistMap(ElementEnum element, int level) {
            Map<ElementEnum, ElementInfo> map = new HashMap<>();
            ElementInfo info = new ElementInfo();
            info.setResLevel(level);
            map.put(element, info);

            return map;
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("an innate resistance is learned")
        void resistance() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 1)));

            player.learnInnate();

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
        }

        /**
         * C's test is {@code res_level != 0}, so a race that burns easily has learned as much about
         * fire as one that does not.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("an innate vulnerability is learned too")
        void vulnerability() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, -1)));

            player.learnInnate();

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("an innate flag is learned")
        void flag() throws Exception {
            set(player, "race", race(new HashMap<>(), ObjectFlag.OF_SUST_STR));

            player.learnInnate();

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("elements and flags are learned in the same pass")
        void both() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 1), ObjectFlag.OF_SUST_STR));

            player.learnInnate();

            assertAll(
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR)));
        }

        /**
         * This runs at birth, where one message per innate property would bury the character sheet
         * before the player had seen it. C passes {@code false} for the same reason, and it is the
         * only thing the parameter exists for.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("learns in silence")
        void silent() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 1), ObjectFlag.OF_SUST_STR));

            player.learnInnate();

            assertTrue(bus.messages.isEmpty());
        }

        /**
         * Most races resist nothing and carry no innate flags, so this is the ordinary case, not an
         * edge one — and it is the case that walks every element past a map with no entry for any
         * of them.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a race with no innate properties learns nothing and survives the walk")
        void nothingInnate() throws Exception {
            set(player, "race", race(new HashMap<>()));

            assertDoesNotThrow(() -> player.learnInnate());

            assertAll(
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(knowledge.getFlags().isEmpty()),
                    () -> assertTrue(bus.messages.isEmpty()));
        }

        /**
         * An entry present but zero is "mentioned, no stake in it", and must not be learned.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("a resistance level of zero is not learned")
        void zeroLevel() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 0)));

            player.learnInnate();

            assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
        }

        /**
         * Nothing else is swept up on the way past. The walk visits every element and every flag,
         * so an arm that learned what it was iterating over rather than what the race confers would
         * show here and nowhere else.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("only the race's own properties are learned")
        void nothingElse() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 1), ObjectFlag.OF_SUST_STR));

            player.learnInnate();

            assertAll(
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_COLD)),
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_INT)),
                    () -> assertFalse(knowledge.brandIsKnown(weakAcid)),
                    () -> assertFalse(knowledge.toHIsKnown()));
        }

        /**
         * Not every element has a resistance rune — C bounds {@code init_rune} at
         * {@code ELEM_HIGH_MAX} — so a race with a stake in one of the others resolves to nothing.
         * C would index {@code rune_list[-1]} here; this port logs and carries on, which matters
         * because the element loop must still reach the elements after it.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("an element with no rune is stepped over, not fallen at")
        void elementWithoutARune() throws Exception {
            Map<ElementEnum, ElementInfo> resists = resistMap(ElementEnum.ELEM_FIRE, 1);
            ElementInfo shards = new ElementInfo();
            shards.setResLevel(1);
            resists.put(ElementEnum.ELEM_SHARD, shards);

            set(player, "race", race(resists));

            assertDoesNotThrow(() -> player.learnInnate());

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
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
