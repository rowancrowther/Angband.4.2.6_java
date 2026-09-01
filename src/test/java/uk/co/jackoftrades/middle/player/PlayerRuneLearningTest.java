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
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftrades.middle.objects.Rune;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.CombatRunes;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.RuneVariety;

import uk.co.jackoftrades.middle.objects.CurseData;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

/**
 * Tests {@link Player}'s object-knowledge path — {@link PlayerKnowledge#learnRune}, the wrappers over it,
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
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerRuneLearningTest {

    /**
     * The registry fields this suite overwrites. Saved and restored by reflection because they are
     * null until something loads them, which the accessors cannot report — see the same note in
     * {@code KnownObjectTest}.
     */
    private static final List<String> SAVED_FIELDS =
            List.of("brands", "slays", "curses", "allRunes", "brandMax", "slayMax", "curseMax",
                    "objectProperties", "objectPropertyMax");

    private static final Map<String, Object> SAVED = new HashMap<>();

    private static Brand weakAcid;
    private static Brand strongAcid;
    private static Slay evil3;
    private static Slay evil5;
    private static Curse siren;
    private static Curse vulnerability;
    private static Curse enveloping;
    private static Curse cowardice;
    private static ObjectProperty strengthProperty;
    private static ObjectProperty sustainProperty;
    private static ObjectProperty fearProperty;
    private static ObjectProperty impairProperty;

    private Player player;
    private KnownObject knowledge;
    private CapturingBus bus;
    private EventsHandler realBus;

    /**
     * Seeds the registries with a fixture small enough to reason about: two strengths of one brand,
     * two of one slay, one curse, and the runes covering them. The rune list is what the wrappers
     * resolve through, so it has to agree with the brand and slay lists or
     * {@link Rune#runeIndex(Brand)} will find nothing.
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
        siren = new Curse("siren", List.of(), 0, null, objectFlags(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), objectFlags(), "wakes monsters", "The curse fires.");

        // Two curses that change the armour class, taken from curse.txt, and one (siren) that does
        // not. The signs are opposite on purpose: the rune names the enchantment, not its direction,
        // so a curse that makes armour worse teaches it exactly as one that makes it better does.
        vulnerability = new Curse("vulnerability", List.of(), 0, null, objectFlags(), Map.of(), Map.of(),
                0, 0, -50, List.of(), objectFlags(), "weakens armour", "The curse fires.");
        enveloping = new Curse("enveloping", List.of(), 0, null, objectFlags(), Map.of(), Map.of(),
                -5, -5, 20, List.of(), objectFlags(), "restricts movement", "The curse fires.");

        // A curse carrying object flags rather than combat figures, for the flag half of the
        // family. Two flags, so a test can name one of them and watch the intersection discard the
        // other.
        cowardice = new Curse("cowardice", List.of(), 0, null,
                objectFlags(ObjectFlag.OF_AFRAID, ObjectFlag.OF_IMPAIR_HP), Map.of(), Map.of(),
                0, 0, 0, List.of(), objectFlags(), "unnerves the wearer", "The curse fires.");

        strengthProperty = new ObjectProperty(null, null, null, null, 0, 0, null,
                "strength", null, null, null, null, null);
        sustainProperty = new ObjectProperty(null, null, null, null, 0, 0, null,
                "sustain strength", null, null, null, null, null);

        // Unlike the two above, these are looked up out of the registry rather than reached through
        // a rune: ItemObject.flagMessage asks lookupObjectProperty for the wording, so a property
        // with a payload and a msg is what makes a flag announce itself at all.
        fearProperty = new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, null, null,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_AFRAID),
                0, 0, null, "fear", null, null,
                "Your {name} makes you tremble.", null, null);
        // A property with no msg at all, which is the ordinary case: most flags are learned in
        // silence and that is not an error.
        impairProperty = new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, null, null,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_IMPAIR_HP),
                0, 0, null, "impaired hitpoint recovery", null, null,
                null, null, null);

        ObjectRegistry.setBrands(List.of(weakAcid, strongAcid));
        ObjectRegistry.setSlays(List.of(evil3, evil5));
        ObjectRegistry.setCurses(List.of(siren, vulnerability, enveloping, cowardice));
        ObjectRegistry.setObjectProperties(List.of(fearProperty, impairProperty));

        // One rune per group, holding whichever member is the representative - which is what the
        // real initRunes produces, and what makes the runeIndex lookups worth testing at all.
        // The resist and flag runes are here for the wrappers that resolve through them; the
        // elements and flags with no rune are as real as the ones with, so not everything the
        // learning code walks past is listed.
        ObjectRegistry.setRunes(new ArrayList<>(List.of(
                new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)),
                new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A)),
                new Rune(new RuneVariety.BrandKey(weakAcid)),
                new Rune(new RuneVariety.SlayKey(evil3)),
                new Rune(new RuneVariety.CurseKey(siren)),
                new Rune(new RuneVariety.CurseKey(vulnerability)),
                new Rune(new RuneVariety.CurseKey(enveloping)),
                new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null)),
                new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, sustainProperty)),
                new Rune(new RuneVariety.CurseKey(cowardice)),
                new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_AFRAID, fearProperty)),
                new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_IMPAIR_HP, impairProperty)))));
    }

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
     * Writes a field on anything, for the classes whose runtime state is filled in by machinery this
     * suite does not run. {@link EquipSlot} has no setter at all — wielding is what puts an item in a
     * slot — and {@link ItemObject}'s no-argument constructor leaves the curse map null, where a
     * parsed item would always have one.
     */
    private static void poke(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * A curse on an object, at a given power. The pairing is what C keeps in two arrays at one
     * subscript — {@code curses[i]} for the definition and {@code obj->curses[i]} for the power —
     * and what the port keeps as one entry of {@code obj->curses}, the curse mapped to its own
     * {@link CurseData}.
     *
     * <p>The timeout is always zero here. None of these tests advances a turn, so nothing counts it
     * down; the figure that matters to rune learning is the power, which is what every one of them
     * gates on.
     *
     * @param curse the curse definition
     * @param power the power it has on the object
     */
    private static Map.Entry<Curse, CurseData> cursed(Curse curse, int power) {
        return Map.entry(curse, new CurseData(power, 0));
    }

    /**
     * An item carrying the given curses and nothing else. Order is preserved so a test about two
     * curses can say which was met first.
     *
     * @param entries the curses, as {@link #cursed} pairs
     */
    @SafeVarargs
    private static ItemObject itemWith(Map.Entry<Curse, CurseData>... entries) throws Exception {
        ItemObject item = new ItemObject();
        Map<Curse, CurseData> curses = new LinkedHashMap<>();
        for (Map.Entry<Curse, CurseData> entry : entries) {
            curses.put(entry.getKey(), entry.getValue());
        }
        poke(item, "curses", curses);
        return item;
    }

    /**
     * An item carrying the given object flags and no curses, for the flag learning. Separate from
     * {@link #itemWith} because that one leaves {@code flags} null, which
     * {@link ItemObject#hasFlag} has no reason to tolerate — a parsed item always has a set.
     *
     * @param itemFlags the flags the item itself carries
     */
    private static ItemObject itemFlagged(ObjectFlag... itemFlags) throws Exception {
        ItemObject item = itemWith();
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        for (ObjectFlag f : itemFlags) {
            flags.on(f);
        }
        poke(item, "flags", flags);
        return item;
    }

    /**
     * An item carrying the given curses and an empty flag set, so that the flag learning can walk
     * past its own flags and reach its curses.
     */
    @SafeVarargs
    private static ItemObject itemCursed(Map.Entry<Curse, CurseData>... entries) throws Exception {
        ItemObject item = itemWith(entries);
        poke(item, "flags", new Flag<>(ObjectFlag.class));
        return item;
    }

    /**
     * Attaches a known counterpart to an item and hands it back, so the "the item does not have the
     * flag" arm has somewhere to record that the flag was ruled out.
     *
     * <p>The counterpart is deliberately built <em>not</em> to satisfy
     * {@link ItemObject#isFullyKnown}: its curses match (so the comparison gets past
     * {@code cursesAreEqual}) but its to-AC does not, which is the first field
     * {@code nonCurseRunesKnown} looks at. Anything that made the item fully known would skip the
     * arm under test, and lining up every field to do so on purpose would be a much longer fixture
     * for no more coverage.
     *
     * @param item the item to give a counterpart to
     * @return the counterpart; read its flags back through {@code item.getKnown().getFlags()}
     */
    private static ItemObject giveKnownCounterpart(ItemObject item) throws Exception {
        ItemObject known = new ItemObject();
        poke(known, "curses", new LinkedHashMap<Curse, CurseData>());
        poke(known, "flags", new Flag<>(ObjectFlag.class));
        poke(known, "toAC", 99);
        poke(item, "known", known);
        return known;
    }

    /**
     * An item carrying the given combat bonuses and no curses — the other half of what an equipped
     * item can teach, and the half {@link ItemObject} could not express while it held dice rather
     * than rolled figures.
     *
     * <p>The kind is left null on purpose. {@link ItemObject#hasStandardToH} answers true for a
     * kindless item, so the to-hit arm of the learning stays quiet unless a test sets a kind up
     * deliberately; that keeps a case about to-damage from accidentally being a case about to-hit
     * as well. {@code ItemObjectStandardToHTest} covers the kind branches on their own.
     *
     * @param toHit this item's rolled to-hit bonus
     * @param toDam this item's rolled to-damage bonus
     * @param toAC  this item's rolled to-AC bonus
     */
    private static ItemObject itemWithCombat(int toHit, int toDam, int toAC) throws Exception {
        ItemObject item = itemWith();
        poke(item, "toHit", toHit);
        poke(item, "toDam", toDam);
        poke(item, "toAC", toAC);
        return item;
    }

    /**
     * An item whose to-hit departs from what its kind prescribes, so that
     * {@link ItemObject#hasStandardToH} answers false and the to-hit arm of the melee learning has
     * something to find. A sword, so the body-armour branch is not in play.
     *
     * @param toHit this item's rolled to-hit bonus
     */
    private static ItemObject weaponWithToHit(int toHit) throws Exception {
        ItemObject item = itemWith();
        poke(item, "tValue", TValue.TV_SWORD);
        poke(item, "kind", new ObjectKind());
        poke(item, "toHit", toHit);
        return item;
    }

    /**
     * A one-slot body holding the given items in order; a null entry is an empty slot, which is what
     * most of a real body's slots are.
     */
    private static PlayerBody bodyWearing(ItemObject... items) throws Exception {
        List<EquipSlot> slots = new ArrayList<>();
        for (ItemObject item : items) {
            EquipSlot slot = new EquipSlot(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, "on your body");
            if (item != null) {
                poke(slot, "item", item);
            }
            slots.add(slot);
        }
        return new PlayerBody("humanoid", slots);
    }

    /**
     * A body of one empty slot. Spelled out rather than written {@code bodyWearing(null)}, which
     * varargs reads as no array at all.
     */
    private static PlayerBody emptyBody() throws Exception {
        return bodyWearing(new ItemObject[]{null});
    }

    /**
     * A shape whose only interesting property is its armour class.
     */
    private static PlayerShape shapeWithToAc(int toAc) {
        return shapeWith(toAc, 0, 0);
    }

    /**
     * A shape with the three combat figures set. Unlike an item's, these are flat parsed
     * {@code int}s on the definition — a bear's claws are simply better than a hand, with no dice
     * and no roll behind it.
     */
    private static PlayerShape shapeWith(int toAc, int toHit, int toDam) {
        return new PlayerShape("bear", toAc, toHit, toDam, Map.of(), null, null, Map.of(), Map.of(),
                List.of(), 0, List.of());
    }

    /**
     * A body of slots of the given types, each holding the item paired with it, in order. The
     * attack methods skip by slot type, so unlike {@link #bodyWearing} these tests have to say which
     * kind of slot each item is in.
     *
     * @param types the slot types, in order
     * @param items the items to place, one per type; a null entry leaves that slot empty
     */
    private static PlayerBody bodyOf(List<EquipmentSlotsEnum> types, ItemObject... items)
            throws Exception {
        List<EquipSlot> slots = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            EquipSlot slot = new EquipSlot(types.get(i), types.get(i).name());
            if (i < items.length && items[i] != null) {
                poke(slot, "item", items[i]);
            }
            slots.add(slot);
        }
        return new PlayerBody("humanoid", slots);
    }

    /**
     * Registers a to-damage rune for the duration of a test, and takes it away again.
     *
     * <p>The shared fixture deliberately leaves the to-damage rune out of the registry — the
     * {@code learnAllRunes} case "learns the runes that exist, not the ones that could" turns on its
     * absence, since the walk is over the registry's list rather than over the varieties. Anything
     * testing to-damage learning therefore has to put one there itself.
     *
     * <p>The registry is swapped rather than appended to: {@link ObjectRegistry#setRunes} copies
     * into an immutable list, so the one {@link ObjectRegistry#getRunes} hands back cannot be added
     * to. The old list comes back as the return value, to be put back afterwards.
     *
     * @return the rune list that was in place before, for restoring
     */
    private static List<Rune> withToDRune() {
        List<Rune> previous = ObjectRegistry.getRunes();
        List<Rune> extended = new ArrayList<>(previous);
        extended.add(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)));
        ObjectRegistry.setRunes(extended);
        return previous;
    }

    private List<String> announced() {
        return bus.messages.stream().map(EventDataMessage::message).toList();
    }

    /**
     * Puts the player in a started game, which is what {@code p->upkeep->playing} reports. It is
     * false on a fresh {@link PlayerUpkeep}, and {@link PlayerKnowledge#cursesFindFlags} gates its
     * message on it, so a test about messages has to say so.
     */
    private void startPlaying() throws Exception {
        poke(player.getPlayerUpkeep(), "playing", true);
    }

    /**
     * Empties {@link uk.co.jackoftrades.middle.Message}'s log, which is static and outlives a test.
     *
     * <p>C's {@code message_add} bumps a count in place when the newest entry repeats, and the port
     * decorates the outgoing text with {@code (x2)} when it does. The log is process-wide, so a test
     * class that has already announced "You have learned the rune of enchantment to hit." makes the
     * next class to announce it see the decorated form — a failure that depends on which classes ran
     * first, and so appears and disappears with the run order rather than with the code.
     */
    @SuppressWarnings("unchecked")
    private static void clearMessageLog() {
        try {
            Field field = Message.class.getDeclaredField("messageLog");
            field.setAccessible(true);
            ((java.util.Deque<Object>) field.get(null)).clear();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Message.messageLog is no longer reachable by reflection", e);
        }
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * A player with a fresh knowledge set, and a bus to catch what the learning announces.
     *
     * <p>{@link Player}'s constructor leaves {@code itemKnowledge} null, matching C, where
     * {@code p->obj_k} is allocated later in {@code init_player} once the registries can size it.
     * Birth would fill it in; this stands in for birth with the one field under test.
     *
     * <p>No player is installed in {@code GameState}. Between 260817 and the same day one was, because
     * {@code ItemObject.runesKnown} reached {@code nonCurseRunesKnown} through
     * {@code GameState.getPlayer()} and {@code equipLearnFlag}'s {@code isFullyKnown} call met a null
     * without it. That method reads only the item and its counterpart, so it became {@code static} and
     * the indirection went; these tests hold their own player and need nothing global.
     */
    @BeforeEach
    void setUp() throws Exception {
        player = new Player();
        knowledge = new KnownObject();
        set(player, "itemKnowledge", knowledge);

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
        clearMessageLog();
    }

    /**
     * Catches what the learning signals. {@link uk.co.jackoftrades.middle.Message} reaches the bus
     * through {@link GameEngine#getEventsBusHandler()}, which is static, so the real one is put
     * back after each test.
     *
     * @author Rowan Crowther
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
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("learnRune dispatch")
    class Dispatch {

        @Test
        @DisplayName("a combat rune learns only its own bonus")
        void combat() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)), false);

            assertTrue(knowledge.toHIsKnown());
            assertFalse(knowledge.toDIsKnown());
            assertFalse(knowledge.toAIsKnown());
        }

        @Test
        @DisplayName("each combat rune reaches a different bonus")
        void combatVarietiesAreDistinct() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)), false);
            assertTrue(knowledge.toDIsKnown());
            assertFalse(knowledge.toAIsKnown());

            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A)), false);
            assertTrue(knowledge.toAIsKnown());
        }

        /**
         * The sentinel is not a rune. C's chain of {@code if}/{@code else if} falls off the end for
         * it with {@code learned} still false, and nothing is learned or said.
         */
        @Test
        @DisplayName("the combat sentinel learns nothing and says nothing")
        void combatSentinel() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_MAX)), true);

            assertFalse(knowledge.toHIsKnown());
            assertFalse(knowledge.toDIsKnown());
            assertFalse(knowledge.toAIsKnown());
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("a modifier rune learns a modifier")
        void modifier() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, strengthProperty)),
                    false);

            assertTrue(knowledge.modifierIsKnown(ObjectModifier.OM_STR));
            assertFalse(knowledge.modifierIsKnown(ObjectModifier.OM_INT));
        }

        @Test
        @DisplayName("a resist rune learns a resistance")
        void resist() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null)), false);

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
            assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_COLD));
        }

        @Test
        @DisplayName("a flag rune learns a flag")
        void flag() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, strengthProperty)),
                    false);

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_INT));
        }

        /**
         * The rune holds one member of the group, and learning it must reveal the whole group —
         * otherwise a player who has read the acid rune still cannot see a strong acid brand.
         */
        @Test
        @DisplayName("a brand rune learns the brand's whole group")
        void brand() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.BrandKey(weakAcid)), false);

            assertTrue(knowledge.brandIsKnown(weakAcid));
            assertTrue(knowledge.brandIsKnown(strongAcid));
        }

        @Test
        @DisplayName("a slay rune learns the slay's whole group")
        void slay() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.SlayKey(evil3)), false);

            assertTrue(knowledge.slayIsKnown(evil3));
            assertTrue(knowledge.slayIsKnown(evil5));
        }

        @Test
        @DisplayName("a curse rune learns a curse")
        void curse() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CurseKey(siren)), false);

            assertTrue(knowledge.curseIsKnown(siren));
        }

        /**
         * Null stands in for C's {@code assert} on the rune index — a lookup that found nothing.
         * Being a data-driven failure rather than a programming one, it is logged and dropped
         * instead of thrown, so nothing is learned and the turn survives.
         */
        @Test
        @DisplayName("a null rune is ignored rather than thrown on")
        void nullRune() {
            PlayerKnowledge.learnRune(player, null, true);

            assertTrue(knowledge.getFlags().isEmpty());
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * The discovery message, which is the only externally visible consequence of a learn returning
     * true.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the discovery message")
    class Announcement {

        @Test
        @DisplayName("names the rune and is tagged MSG_RUNE")
        void namesTheRune() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.BrandKey(weakAcid)), true);

            assertEquals(1, bus.messages.size());
            assertEquals(MessageType.MSG_RUNE, bus.messages.get(0).type());
            assertEquals("You have learned the rune of acid brand.", bus.messages.get(0).message());
        }

        @Test
        @DisplayName("uses the variety's own name for each kind of rune")
        void usesTheVarietyName() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.SlayKey(evil3)), true);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CurseKey(siren)), true);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)), true);

            assertEquals(List.of(
                            "You have learned the rune of slay evil.",
                            "You have learned the rune of siren curse.",
                            "You have learned the rune of enchantment to hit."),
                    bus.messages.stream().map(EventDataMessage::message).toList());
        }

        /**
         * Learning the same rune again is not a discovery. This is what the {@code if (!learned)
         * return} guard buys, and without it a branded weapon would announce its rune on every blow.
         */
        @Test
        @DisplayName("is not repeated when the rune is already known")
        void notRepeated() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.BrandKey(weakAcid)), true);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.BrandKey(weakAcid)), true);

            assertEquals(1, bus.messages.size());
        }

        /**
         * And not for another member of a group already learned, since the group was marked whole.
         */
        @Test
        @DisplayName("is not repeated for another member of a known group")
        void notRepeatedForTheGroup() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.BrandKey(weakAcid)), true);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.BrandKey(strongAcid)), true);

            assertEquals(1, bus.messages.size());
        }

        /**
         * The flag exists for the paths that learn in bulk — {@code player_learn_innate_runes} and
         * the equipment sweeps — which would otherwise bury the player under one message per rune.
         */
        @Test
        @DisplayName("is suppressed when the caller asks for silence")
        void suppressed() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.BrandKey(weakAcid)), false);

            assertTrue(knowledge.brandIsKnown(weakAcid));
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * Builds a {@link Flag} set from a handful of object flags. The curse constructor took a
     * {@link java.util.List} when these tests were written and now takes a flag set; this keeps the
     * call sites reading the way they did.
     *
     * @param flags the flags to switch on
     * @return a flag set carrying exactly those flags
     */
    private static Flag<ObjectFlag> objectFlags(ObjectFlag... flags) {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        if (flags.length > 0) result.set(java.util.List.of(flags));
        return result;
    }

    /**
     * {@link PlayerKnowledge#knowsRune}, the mirror of {@link PlayerKnowledge#learnRune} and the port of C's
     * {@code player_knows_rune}. Each variety is asked before and after the matching learn, because
     * an arm wired to the wrong corner of the knowledge would answer correctly for whichever
     * property happens to be unlearned and wrongly for the rest.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("knowsRune")
    class KnowsRune {

        @Test
        @DisplayName("a combat rune is unknown until learned")
        void combat() {
            Rune toHit = new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H));

            assertFalse(PlayerKnowledge.knowsRune(player, toHit));

            PlayerKnowledge.learnRune(player, toHit, false);

            assertTrue(PlayerKnowledge.knowsRune(player, toHit));
        }

        /**
         * Each of the three enchantments answers for itself; C compares {@code r->index} against
         * three constants in a chain, which is easy to write with two arms reaching the same field.
         */
        @Test
        @DisplayName("the three combat runes answer separately")
        void combatRunesAreDistinct() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)), false);

            assertAll(
                    () -> assertTrue(PlayerKnowledge.knowsRune(
                            player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D)))),
                    () -> assertFalse(PlayerKnowledge.knowsRune(
                            player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)))),
                    () -> assertFalse(PlayerKnowledge.knowsRune(
                            player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A)))));
        }

        /**
         * The sentinel is not a rune and has nothing to know. C's {@code if} chain falls through it
         * to {@code return false}.
         */
        @Test
        @DisplayName("the combat sentinel is never known")
        void combatSentinel() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)), false);

            assertFalse(PlayerKnowledge.knowsRune(
                    player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_MAX))));
        }

        @Test
        @DisplayName("a modifier rune is unknown until learned")
        void modifier() {
            Rune strength = new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, strengthProperty));

            assertFalse(PlayerKnowledge.knowsRune(player, strength));

            PlayerKnowledge.learnRune(player, strength, false);

            assertTrue(PlayerKnowledge.knowsRune(player, strength));
            assertFalse(PlayerKnowledge.knowsRune(
                    player, new Rune(new RuneVariety.ModKey(ObjectModifier.OM_INT, strengthProperty))));
        }

        @Test
        @DisplayName("a resist rune is unknown until learned")
        void resist() {
            Rune fire = new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null));

            assertFalse(PlayerKnowledge.knowsRune(player, fire));

            PlayerKnowledge.learnRune(player, fire, false);

            assertTrue(PlayerKnowledge.knowsRune(player, fire));
            assertFalse(PlayerKnowledge.knowsRune(
                    player, new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_COLD, null))));
        }

        @Test
        @DisplayName("a flag rune is unknown until learned")
        void flag() {
            Rune sustain = new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, sustainProperty));

            assertFalse(PlayerKnowledge.knowsRune(player, sustain));

            PlayerKnowledge.learnRune(player, sustain, false);

            assertTrue(PlayerKnowledge.knowsRune(player, sustain));
        }

        @Test
        @DisplayName("a curse rune is unknown until learned")
        void curse() {
            Rune sirenRune = new Rune(new RuneVariety.CurseKey(siren));

            assertFalse(PlayerKnowledge.knowsRune(player, sirenRune));

            PlayerKnowledge.learnRune(player, sirenRune, false);

            assertTrue(PlayerKnowledge.knowsRune(player, sirenRune));
        }

        /**
         * Learning one member of a group makes the group's rune readable whichever member the rune
         * is asked about — which is the whole point of doing the fan-out on the learning side.
         */
        @Test
        @DisplayName("a brand rune is known through any member of its group")
        void brandGroup() {
            PlayerKnowledge.learnBrand(player, weakAcid);

            assertAll(
                    () -> assertTrue(PlayerKnowledge.knowsRune(player, new Rune(new RuneVariety.BrandKey(weakAcid)))),
                    () -> assertTrue(PlayerKnowledge.knowsRune(player, new Rune(new RuneVariety.BrandKey(strongAcid)))));
        }

        @Test
        @DisplayName("a slay rune is known through any member of its group")
        void slayGroup() {
            PlayerKnowledge.learnSlay(player, evil3);

            assertAll(
                    () -> assertTrue(PlayerKnowledge.knowsRune(player, new Rune(new RuneVariety.SlayKey(evil3)))),
                    () -> assertTrue(PlayerKnowledge.knowsRune(player, new Rune(new RuneVariety.SlayKey(evil5)))));
        }

        /**
         * A rune of one variety must not be answered from another variety's corner of the
         * knowledge. Learning everything but the brand is the arrangement that catches an arm
         * reading the wrong field, because only one answer should still be false.
         */
        @Test
        @DisplayName("each variety answers from its own corner")
        void varietiesDoNotCrossTalk() {
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)), false);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, strengthProperty)), false);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null)), false);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_SUST_STR, sustainProperty)), false);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.CurseKey(siren)), false);
            PlayerKnowledge.learnRune(player, new Rune(new RuneVariety.SlayKey(evil3)), false);

            assertFalse(PlayerKnowledge.knowsRune(player, new Rune(new RuneVariety.BrandKey(weakAcid))));
        }
    }

    /**
     * {@link PlayerKnowledge#learnInnate}, the port of C's {@code player_learn_innate} — the birth-time pass
     * that gives a character the runes for the properties of their own body.
     *
     * <p>The race is installed by reflection because {@link Player} has no setter for it: C assigns
     * {@code p->race} during birth, which this port has not reached yet.
     *
     * @author Rowan Crowther
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

        @Test
        @DisplayName("an innate resistance is learned")
        void resistance() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 1)));

            PlayerKnowledge.learnInnate(player);

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
        }

        /**
         * C's test is {@code res_level != 0}, so a race that burns easily has learned as much about
         * fire as one that does not.
         */
        @Test
        @DisplayName("an innate vulnerability is learned too")
        void vulnerability() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, -1)));

            PlayerKnowledge.learnInnate(player);

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
        }

        @Test
        @DisplayName("an innate flag is learned")
        void flag() throws Exception {
            set(player, "race", race(new HashMap<>(), ObjectFlag.OF_SUST_STR));

            PlayerKnowledge.learnInnate(player);

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
        }

        @Test
        @DisplayName("elements and flags are learned in the same pass")
        void both() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 1), ObjectFlag.OF_SUST_STR));

            PlayerKnowledge.learnInnate(player);

            assertAll(
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR)));
        }

        /**
         * This runs at birth, where one message per innate property would bury the character sheet
         * before the player had seen it. C passes {@code false} for the same reason, and it is the
         * only thing the parameter exists for.
         */
        @Test
        @DisplayName("learns in silence")
        void silent() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 1), ObjectFlag.OF_SUST_STR));

            PlayerKnowledge.learnInnate(player);

            assertTrue(bus.messages.isEmpty());
        }

        /**
         * Most races resist nothing and carry no innate flags, so this is the ordinary case, not an
         * edge one — and it is the case that walks every element past a map with no entry for any
         * of them.
         */
        @Test
        @DisplayName("a race with no innate properties learns nothing and survives the walk")
        void nothingInnate() throws Exception {
            set(player, "race", race(new HashMap<>()));

            assertDoesNotThrow(() -> PlayerKnowledge.learnInnate(player));

            assertAll(
                    () -> assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(knowledge.getFlags().isEmpty()),
                    () -> assertTrue(bus.messages.isEmpty()));
        }

        /**
         * An entry present but zero is "mentioned, no stake in it", and must not be learned.
         */
        @Test
        @DisplayName("a resistance level of zero is not learned")
        void zeroLevel() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 0)));

            PlayerKnowledge.learnInnate(player);

            assertFalse(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
        }

        /**
         * Nothing else is swept up on the way past. The walk visits every element and every flag,
         * so an arm that learned what it was iterating over rather than what the race confers would
         * show here and nowhere else.
         */
        @Test
        @DisplayName("only the race's own properties are learned")
        void nothingElse() throws Exception {
            set(player, "race", race(resistMap(ElementEnum.ELEM_FIRE, 1), ObjectFlag.OF_SUST_STR));

            PlayerKnowledge.learnInnate(player);

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
         */
        @Test
        @DisplayName("an element with no rune is stepped over, not fallen at")
        void elementWithoutARune() throws Exception {
            Map<ElementEnum, ElementInfo> resists = resistMap(ElementEnum.ELEM_FIRE, 1);
            ElementInfo shards = new ElementInfo();
            shards.setResLevel(1);
            resists.put(ElementEnum.ELEM_SHARD, shards);

            set(player, "race", race(resists));

            assertDoesNotThrow(() -> PlayerKnowledge.learnInnate(player));

            assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * {@link PlayerKnowledge#cursesFindToA} — learning a curse by being hurt by it.
     *
     * <p>The function reads two things that live apart: the power, which is on the item, and the
     * armour-class contribution, which is on the curse definition in the registry. C walks
     * {@code 1 .. curse_max} and subscripts both arrays alike; the port iterates the item's own
     * curse map, which is why the case that matters most here is a curse that exists in the registry
     * and carries armour class but is <em>not</em> on the item.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("cursesFindToA")
    class CursesFindToA {

        /**
         * Two runes, not one: that something is altering the armour class, and which curse is doing
         * it. C learns them in that order and so does this.
         */
        @Test
        @DisplayName("a curse that changes armour class teaches its own rune and the to-AC rune")
        void teachesBothRunes() throws Exception {
            ItemObject item = itemWith(cursed(vulnerability, 20));

            PlayerKnowledge.cursesFindToA(player, item);

            assertAll(
                    () -> assertTrue(knowledge.toAIsKnown()),
                    () -> assertTrue(knowledge.curseIsKnown(vulnerability)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of enchantment to armour.",
                                    "You have learned the rune of vulnerability curse."),
                            announced()));
        }

        /**
         * The sign is not the question. C tests {@code to_a != 0}, and a curse that makes armour
         * worse is as noticeable as one that makes it better.
         */
        @Test
        @DisplayName("a positive armour change teaches as readily as a negative one")
        void signDoesNotMatter() throws Exception {
            ItemObject item = itemWith(cursed(enveloping, 20));

            PlayerKnowledge.cursesFindToA(player, item);

            assertTrue(knowledge.toAIsKnown());
            assertTrue(knowledge.curseIsKnown(enveloping));
        }

        /**
         * A curse with no armour-class contribution is not evidence about armour class, however hard
         * it bites in other ways.
         */
        @Test
        @DisplayName("a curse with no armour change teaches nothing")
        void curseWithoutArmourChange() throws Exception {
            ItemObject item = itemWith(cursed(siren, 20));

            PlayerKnowledge.cursesFindToA(player, item);

            assertAll(
                    () -> assertFalse(knowledge.toAIsKnown()),
                    () -> assertFalse(knowledge.curseIsKnown(siren)),
                    () -> assertTrue(bus.messages.isEmpty()));
        }

        /**
         * Zero power is how a curse is removed ({@code CurseData.setPower(0)}), so a zeroed entry can
         * outlive the curse it describes. C reaches the same answer from the other side: its array is
         * dense, and zero power is what "the item does not have curse {@code i}" looks like.
         */
        @Test
        @DisplayName("a curse at zero power is not on the item")
        void zeroPowerIsAbsent() throws Exception {
            ItemObject item = itemWith(cursed(vulnerability, 0));

            PlayerKnowledge.cursesFindToA(player, item);

            assertFalse(knowledge.toAIsKnown());
            assertFalse(knowledge.curseIsKnown(vulnerability));
        }

        /**
         * <b>The regression this suite exists for.</b> {@code vulnerability} is in the registry and
         * changes armour class, but the item carries only {@code siren}. An implementation that
         * walked the registry rather than the item — as C's loop bounds invite — would teach the
         * player a rune for a curse they have never met, from an item that merely happens to be
         * cursed at all.
         */
        @Test
        @DisplayName("a curse the item does not carry teaches nothing")
        void curseNotOnTheItem() throws Exception {
            ItemObject item = itemWith(cursed(siren, 20));

            PlayerKnowledge.cursesFindToA(player, item);

            assertAll(
                    () -> assertFalse(knowledge.toAIsKnown()),
                    () -> assertFalse(knowledge.curseIsKnown(vulnerability)),
                    () -> assertFalse(knowledge.curseIsKnown(enveloping)));
        }

        @Test
        @DisplayName("an uncursed item teaches nothing")
        void uncursedItem() throws Exception {
            ItemObject item = itemWith();

            PlayerKnowledge.cursesFindToA(player, item);

            assertFalse(knowledge.toAIsKnown());
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * Both curse runes are learned, and the to-AC rune is announced once — the second attempt
         * finds it already known and {@code learnRune} returns before saying anything.
         *
         * <p>This is also where C's one real bug in the function would show. Its {@code index} is
         * declared outside the loop and reassigned to the curse's rune inside it, so on the second
         * qualifying curse it relearns the first curse instead of the to-AC rune. Harmless there only
         * because to-AC is known by then; the port resolves the rune once, before the loop, so the
         * mistake cannot be made.
         */
        @Test
        @DisplayName("two qualifying curses are both learned, and the to-AC rune announced once")
        void twoQualifyingCurses() throws Exception {
            ItemObject item = itemWith(cursed(vulnerability, 20), cursed(enveloping, 20));

            PlayerKnowledge.cursesFindToA(player, item);

            assertAll(
                    () -> assertTrue(knowledge.toAIsKnown()),
                    () -> assertTrue(knowledge.curseIsKnown(vulnerability)),
                    () -> assertTrue(knowledge.curseIsKnown(enveloping)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of enchantment to armour.",
                                    "You have learned the rune of vulnerability curse.",
                                    "You have learned the rune of enveloping curse."),
                            announced()));
        }
    }

    /**
     * {@link PlayerKnowledge#equipLearnOnDefend} — the occasion on which worn armour explains itself.
     *
     * <p>A property announces itself when it does its job, so an armour-class bonus is learned by
     * being hit rather than by being examined. Three sources are checked in order and the first
     * success ends the method: the item's own bonus, its curses, and the player's assumed shape. The
     * first of those was stubbed while {@link ItemObject} carried dice rather than a rolled figure;
     * it now reads {@link ItemObject#getToAC} and a plain non-zero test is the faithful port of C's
     * {@code if (obj->to_a)}.
     *
     * <p>Class EquipLearnOnDefend coded before 260815, commented in full before 260815, updated on
     * 260815 when the item's own bonus arm stopped being a stub.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("equipLearnOnDefend")
    class EquipLearnOnDefend {

        /**
         * Most of a real body's slots are empty most of the time, and C guards its whole loop body
         * with {@code if (obj)}. Without the equivalent the very first unworn slot ends the turn.
         */
        @Test
        @DisplayName("empty slots are stepped over, not fallen at")
        void emptySlotsAreSkipped() throws Exception {
            set(player, "body", bodyWearing(null, null));

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnOnDefend(player));

            assertFalse(knowledge.toAIsKnown());
        }

        @Test
        @DisplayName("a cursed item in a slot teaches the to-AC rune")
        void cursedItemTeaches() throws Exception {
            set(player, "body", bodyWearing(null, itemWith(cursed(vulnerability, 20))));

            PlayerKnowledge.equipLearnOnDefend(player);

            assertTrue(knowledge.toAIsKnown());
            assertTrue(knowledge.curseIsKnown(vulnerability));
        }

        /**
         * A plain item with no bonus and no curse has nothing to say. The blow landed exactly as
         * hard as it should have, which is not evidence of anything.
         */
        @Test
        @DisplayName("an uncursed item with no bonus teaches nothing")
        void uncursedItemWithNoBonusTeachesNothing() throws Exception {
            set(player, "body", bodyWearing(itemWith()));

            PlayerKnowledge.equipLearnOnDefend(player);

            assertFalse(knowledge.toAIsKnown());
        }

        /**
         * The item's own bonus arm, which replaced a stub once {@link ItemObject} began carrying the
         * to-AC it rolled rather than the dice it was rolled from. No curse is involved: the armour
         * itself is doing the work.
         */
        @Test
        @DisplayName("an uncursed item with a to-AC bonus teaches the rune")
        void uncursedItemWithABonusTeaches() throws Exception {
            set(player, "body", bodyWearing(itemWithCombat(0, 0, 5)));

            PlayerKnowledge.equipLearnOnDefend(player);

            assertTrue(knowledge.toAIsKnown());
        }

        /**
         * A penalty is as much evidence as a bonus — C tests {@code if (obj->to_a)}, not its sign,
         * and the rune names the enchantment rather than its direction.
         */
        @Test
        @DisplayName("a to-AC penalty teaches as readily as a bonus")
        void aPenaltyTeachesToo() throws Exception {
            set(player, "body", bodyWearing(itemWithCombat(0, 0, -5)));

            PlayerKnowledge.equipLearnOnDefend(player);

            assertTrue(knowledge.toAIsKnown());
        }

        /**
         * The leading guard. Nothing is left to learn about the armour class, so no slot is read and
         * the curse riding on the worn item goes unnoticed — which is C's behaviour, and the reason
         * a curse can stay hidden on a character who identified their armour long ago.
         */
        @Test
        @DisplayName("nothing is examined once the to-AC rune is already known")
        void alreadyKnownStopsAtTheDoor() throws Exception {
            knowledge.learnToA();
            set(player, "body", bodyWearing(itemWith(cursed(vulnerability, 20))));

            PlayerKnowledge.equipLearnOnDefend(player);

            assertFalse(knowledge.curseIsKnown(vulnerability));
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * The guard repeated at the foot of the loop. Once the first slot has taught the rune the
         * walk stops, so the second cursed item is never read and its curse stays unknown.
         */
        @Test
        @DisplayName("the walk stops at the first slot that teaches")
        void stopsAtTheFirstSlotThatTeaches() throws Exception {
            set(player, "body", bodyWearing(
                    itemWith(cursed(vulnerability, 20)),
                    itemWith(cursed(enveloping, 20))));

            PlayerKnowledge.equipLearnOnDefend(player);

            assertTrue(knowledge.toAIsKnown());
            assertTrue(knowledge.curseIsKnown(vulnerability));
            assertFalse(knowledge.curseIsKnown(enveloping));
        }

        /**
         * A bear's hide is an armour-class bonus like any other. Reached only when nothing worn has
         * already answered the question.
         */
        @Test
        @DisplayName("an assumed shape's armour class teaches the rune")
        void shapeTeaches() throws Exception {
            set(player, "body", emptyBody());
            set(player, "shape", shapeWithToAc(5));

            PlayerKnowledge.equipLearnOnDefend(player);

            assertTrue(knowledge.toAIsKnown());
        }

        @Test
        @DisplayName("a shape with no armour class teaches nothing")
        void shapeWithoutArmourClass() throws Exception {
            set(player, "body", emptyBody());
            set(player, "shape", shapeWithToAc(0));

            PlayerKnowledge.equipLearnOnDefend(player);

            assertFalse(knowledge.toAIsKnown());
        }

        /**
         * Being in no shape at all is the normal case, and C's {@code if (p->shape)} says so.
         */
        @Test
        @DisplayName("no shape is not an error")
        void noShape() throws Exception {
            set(player, "body", emptyBody());

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnOnDefend(player));

            assertFalse(knowledge.toAIsKnown());
        }
    }

    /**
     * {@link PlayerKnowledge#cursesFindToH} — the to-hit sibling of {@link PlayerKnowledge#cursesFindToA}.
     *
     * <p>The mechanics are covered under {@code cursesFindToA} and are not repeated: what is worth
     * pinning here is that the three functions read three different figures off the curse
     * definition, so a curse that changes one of them does not teach the other two. The fixture has
     * both kinds — {@code enveloping} carries a to-hit, a to-damage and an armour class, while
     * {@code vulnerability} carries only an armour class.
     *
     * <p>Class CursesFindToH coded on 260815, commented in full on 260815, call sites turned round
     * on 260815 when the method moved to Player.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("cursesFindToH")
    class CursesFindToH {

        @Test
        @DisplayName("a curse that changes to-hit teaches its own rune and the to-hit rune")
        void teachesBothRunes() throws Exception {
            ItemObject item = itemWith(cursed(enveloping, 20));

            PlayerKnowledge.cursesFindToH(player, item);

            assertAll(
                    () -> assertTrue(knowledge.toHIsKnown()),
                    () -> assertTrue(knowledge.curseIsKnown(enveloping)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of enchantment to hit.",
                                    "You have learned the rune of enveloping curse."),
                            announced()));
        }

        /**
         * The point of having three functions rather than one. {@code vulnerability} is a real curse
         * that really is on the item, and it changes the armour class by -50 — but it does nothing
         * to the player's aim, so a missed blow is no evidence of it.
         */
        @Test
        @DisplayName("a curse that changes only armour class teaches nothing here")
        void aToAOnlyCurseIsSilent() throws Exception {
            ItemObject item = itemWith(cursed(vulnerability, 20));

            PlayerKnowledge.cursesFindToH(player, item);

            assertAll(
                    () -> assertFalse(knowledge.toHIsKnown()),
                    () -> assertFalse(knowledge.curseIsKnown(vulnerability)),
                    () -> assertTrue(bus.messages.isEmpty()));
        }

        /**
         * A curse at zero power is not on the item at all — {@link CurseData#setPower} with a zero
         * is how a curse is removed, so a zeroed entry can outlive the curse it named.
         */
        @Test
        @DisplayName("a curse at zero power is not on the item")
        void zeroPowerIsNotACurse() throws Exception {
            ItemObject item = itemWith(cursed(enveloping, 0));

            PlayerKnowledge.cursesFindToH(player, item);

            assertFalse(knowledge.toHIsKnown());
        }

        @Test
        @DisplayName("an uncursed item teaches nothing")
        void uncursedItemIsSilent() throws Exception {
            PlayerKnowledge.cursesFindToH(player, itemWith());

            assertFalse(knowledge.toHIsKnown());
        }
    }

    /**
     * {@link PlayerKnowledge#cursesFindToD} — the to-damage sibling, and the one whose rune the shared
     * fixture does not hold, so each case registers it first. See {@link #withToDRune()}.
     *
     * <p>Class CursesFindToD coded on 260815, commented in full on 260815, call sites turned round
     * on 260815 when the method moved to Player.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("cursesFindToD")
    class CursesFindToD {

        private List<Rune> previousRunes;

        @BeforeEach
        void addRune() {
            previousRunes = withToDRune();
        }

        @AfterEach
        void removeRune() {
            ObjectRegistry.setRunes(previousRunes);
        }

        @Test
        @DisplayName("a curse that changes to-damage teaches its own rune and the to-damage rune")
        void teachesBothRunes() throws Exception {
            ItemObject item = itemWith(cursed(enveloping, 20));

            PlayerKnowledge.cursesFindToD(player, item);

            assertAll(
                    () -> assertTrue(knowledge.toDIsKnown()),
                    () -> assertTrue(knowledge.curseIsKnown(enveloping)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of enchantment to damage.",
                                    "You have learned the rune of enveloping curse."),
                            announced()));
        }

        @Test
        @DisplayName("a curse that changes only armour class teaches nothing here")
        void aToAOnlyCurseIsSilent() throws Exception {
            ItemObject item = itemWith(cursed(vulnerability, 20));

            PlayerKnowledge.cursesFindToD(player, item);

            assertAll(
                    () -> assertFalse(knowledge.toDIsKnown()),
                    () -> assertFalse(knowledge.curseIsKnown(vulnerability)),
                    () -> assertTrue(bus.messages.isEmpty()));
        }

        @Test
        @DisplayName("a curse at zero power is not on the item")
        void zeroPowerIsNotACurse() throws Exception {
            ItemObject item = itemWith(cursed(enveloping, 0));

            PlayerKnowledge.cursesFindToD(player, item);

            assertFalse(knowledge.toDIsKnown());
        }
    }

    /**
     * {@link PlayerKnowledge#equipLearnOnRangedAttack} — the occasion on which a shot explains itself.
     *
     * <p>Only to-hit is at stake: a missile's damage belongs to the launcher and the ammunition, so
     * a bowshot is no evidence about the player's own to-damage. What distinguishes this method from
     * its melee twin is which slots it refuses to read. C skips both {@code weapon} and
     * {@code shooting} — the sword at the belt cannot have helped the shot, and the launcher's
     * contribution cannot be told apart from the archer's own skill.
     *
     * <p>Class EquipLearnOnRangedAttack coded on 260815, commented in full on 260815.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("equipLearnOnRangedAttack")
    class EquipLearnOnRangedAttack {

        @Test
        @DisplayName("empty slots are stepped over, not fallen at")
        void emptySlotsAreSkipped() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES), (ItemObject) null));

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnOnRangedAttack(player));

            assertFalse(knowledge.toHIsKnown());
        }

        /**
         * The first of the two skips. A sword hanging at the belt took no part in the shot, so
         * whatever it is carrying stays unlearned — and the curse riding on it stays hidden with it.
         */
        @Test
        @DisplayName("the melee weapon is not read")
        void theWeaponSlotIsSkipped() throws Exception {
            ItemObject sword = itemWith(cursed(enveloping, 20));
            poke(sword, "toHit", 8);
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_WEAPON), sword));

            PlayerKnowledge.equipLearnOnRangedAttack(player);

            assertAll(
                    () -> assertFalse(knowledge.toHIsKnown()),
                    () -> assertFalse(knowledge.curseIsKnown(enveloping)));
        }

        /**
         * The second skip, and the less obvious one. The launcher is the thing that fired, but its
         * accuracy and the archer's are the same number as far as the shot is concerned, so C
         * declines to attribute the result to it.
         */
        @Test
        @DisplayName("the launcher is not read either")
        void theShootingSlotIsSkipped() throws Exception {
            ItemObject bow = itemWith(cursed(enveloping, 20));
            poke(bow, "toHit", 8);
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_BOW), bow));

            PlayerKnowledge.equipLearnOnRangedAttack(player);

            assertAll(
                    () -> assertFalse(knowledge.toHIsKnown()),
                    () -> assertFalse(knowledge.curseIsKnown(enveloping)));
        }

        /**
         * Every other slot is read, so a ring that steadies the hand is learned from a bowshot even
         * though it had nothing to do with the bow.
         */
        @Test
        @DisplayName("a curse in any other slot teaches the to-hit rune")
        void aCursedRingTeaches() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_RING),
                    itemWith(cursed(enveloping, 20))));

            PlayerKnowledge.equipLearnOnRangedAttack(player);

            assertAll(
                    () -> assertTrue(knowledge.toHIsKnown()),
                    () -> assertTrue(knowledge.curseIsKnown(enveloping)));
        }

        /**
         * The item's own bonus arm, which C reaches through
         * {@code !object_has_standard_to_h(obj)} — the same predicate the melee method uses. An
         * item sitting at exactly what its kind prescribes is evidence of nothing.
         */
        @Test
        @DisplayName("an item at its kind's to-hit teaches nothing")
        void anItemAtItsKindsToHitTeachesNothing() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_RING),
                    weaponWithToHit(0)));

            PlayerKnowledge.equipLearnOnRangedAttack(player);

            assertFalse(knowledge.toHIsKnown());
        }

        /**
         * The other half, and the one that says the arm is live at all: an item whose to-hit has
         * departed from its kind's is exactly what a truer-than-expected shot is evidence of.
         */
        @Test
        @DisplayName("an item off its kind's to-hit teaches the rune")
        void anItemOffItsKindsToHitTeaches() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_RING),
                    weaponWithToHit(5)));

            PlayerKnowledge.equipLearnOnRangedAttack(player);

            assertTrue(knowledge.toHIsKnown());
        }

        /**
         * The leading guard: nothing is examined once the rune is known, so the curse on the worn
         * ring goes unnoticed. C's behaviour, and the reason a curse can stay hidden on a character
         * who worked out their bonuses long ago.
         */
        @Test
        @DisplayName("nothing is examined once the to-hit rune is already known")
        void alreadyKnownStopsAtTheDoor() throws Exception {
            knowledge.learnToH();
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_RING),
                    itemWith(cursed(enveloping, 20))));

            PlayerKnowledge.equipLearnOnRangedAttack(player);

            assertFalse(knowledge.curseIsKnown(enveloping));
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * The guard repeated at the foot of the loop: the walk stops at the first slot that teaches,
         * so the second cursed item is never read.
         */
        @Test
        @DisplayName("the walk stops at the first slot that teaches")
        void stopsAtTheFirstSlotThatTeaches() throws Exception {
            set(player, "body", bodyOf(
                    List.of(EquipmentSlotsEnum.EQUIP_RING, EquipmentSlotsEnum.EQUIP_AMULET),
                    itemWith(cursed(enveloping, 20)),
                    itemWith(cursed(siren, 20))));

            PlayerKnowledge.equipLearnOnRangedAttack(player);

            assertAll(
                    () -> assertTrue(knowledge.toHIsKnown()),
                    () -> assertTrue(knowledge.curseIsKnown(enveloping)),
                    () -> assertFalse(knowledge.curseIsKnown(siren)));
        }

        /**
         * A shape's to-hit is a flat parsed {@code int}, and is reached only when nothing worn has
         * already answered the question.
         */
        @Test
        @DisplayName("an assumed shape's to-hit teaches the rune")
        void shapeTeaches() throws Exception {
            set(player, "body", emptyBody());
            set(player, "shape", shapeWith(0, 5, 0));

            PlayerKnowledge.equipLearnOnRangedAttack(player);

            assertTrue(knowledge.toHIsKnown());
        }

        /**
         * The shape's own to-damage is not consulted here, whatever it says. Only accuracy is on
         * offer from a bowshot.
         */
        @Test
        @DisplayName("a shape's to-damage is not learned from a shot")
        void shapeToDamageIsNotLearned() throws Exception {
            List<Rune> previousRunes = withToDRune();
            try {
                set(player, "body", emptyBody());
                set(player, "shape", shapeWith(0, 0, 7));

                PlayerKnowledge.equipLearnOnRangedAttack(player);

                assertFalse(knowledge.toDIsKnown());
            } finally {
                ObjectRegistry.setRunes(previousRunes);
            }
        }

        @Test
        @DisplayName("no shape is not an error")
        void noShape() throws Exception {
            set(player, "body", emptyBody());

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnOnRangedAttack(player));

            assertFalse(knowledge.toHIsKnown());
        }
    }

    /**
     * {@link PlayerKnowledge#equipLearnOnMeleeAttack} — the largest of the family, because it is the only
     * one pursuing two runes at once.
     *
     * <p>That pairing is what makes its guards different in kind: both the leading test and the one
     * at the foot of the loop are conjunctions, so a player who has already worked out their
     * weapon's damage keeps walking the remaining slots in the hope of learning accuracy from their
     * gloves. Only the launcher is skipped — the weapon is very much part of a sword-stroke, which
     * is precisely the slot {@link PlayerKnowledge#equipLearnOnRangedAttack} has to leave alone.
     *
     * <p>The two tests are also asymmetrical: to-damage is a plain non-zero check, while to-hit goes
     * through {@link ItemObject#hasStandardToH} because body armour carries a penalty as standard
     * equipment. {@code ItemObjectStandardToHTest} covers that predicate on its own; what is checked
     * here is that the melee learning asks it rather than testing the figure directly.
     *
     * <p>Class EquipLearnOnMeleeAttack coded on 260815, commented in full on 260815.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("equipLearnOnMeleeAttack")
    class EquipLearnOnMeleeAttack {

        private List<Rune> previousRunes;

        @BeforeEach
        void addRune() {
            previousRunes = withToDRune();
        }

        @AfterEach
        void removeRune() {
            ObjectRegistry.setRunes(previousRunes);
        }

        @Test
        @DisplayName("empty slots are stepped over, not fallen at")
        void emptySlotsAreSkipped() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES), (ItemObject) null));

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnOnMeleeAttack(player));

            assertAll(
                    () -> assertFalse(knowledge.toHIsKnown()),
                    () -> assertFalse(knowledge.toDIsKnown()));
        }

        /**
         * The one slot skipped, and the difference from the ranged method. A bow is no part of a
         * sword-stroke.
         */
        @Test
        @DisplayName("the launcher is not read")
        void theShootingSlotIsSkipped() throws Exception {
            ItemObject bow = itemWith(cursed(enveloping, 20));
            poke(bow, "toDam", 8);
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_BOW), bow));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertAll(
                    () -> assertFalse(knowledge.toHIsKnown()),
                    () -> assertFalse(knowledge.toDIsKnown()),
                    () -> assertFalse(knowledge.curseIsKnown(enveloping)));
        }

        /**
         * The weapon slot is read, unlike in the ranged method — the whole reason the two functions
         * differ.
         */
        @Test
        @DisplayName("the melee weapon is read")
        void theWeaponSlotIsRead() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_WEAPON),
                    itemWithCombat(0, 6, 0)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertTrue(knowledge.toDIsKnown());
        }

        /**
         * The to-damage arm on its own, from an item with no to-hit story to tell: the fixture item
         * has no kind, so {@link ItemObject#hasStandardToH} answers true and the to-hit arm stays
         * quiet.
         */
        @Test
        @DisplayName("an item's to-damage teaches the to-damage rune and not the to-hit one")
        void toDamageAloneTeachesOnlyItsOwnRune() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemWithCombat(0, 6, 0)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertAll(
                    () -> assertTrue(knowledge.toDIsKnown()),
                    () -> assertFalse(knowledge.toHIsKnown()));
        }

        /**
         * A to-damage penalty teaches as readily as a bonus — C tests {@code if (obj->to_d)}, not
         * its sign.
         */
        @Test
        @DisplayName("a to-damage penalty teaches as readily as a bonus")
        void aToDamagePenaltyTeaches() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemWithCombat(0, -6, 0)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertTrue(knowledge.toDIsKnown());
        }

        /**
         * An item with no bonus at all is silent in both arms. The blow landed exactly as it should
         * have, which is not evidence of anything.
         */
        @Test
        @DisplayName("an item with no bonus teaches nothing")
        void anUnremarkableItemTeachesNothing() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemWithCombat(0, 0, 0)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertAll(
                    () -> assertFalse(knowledge.toDIsKnown()),
                    () -> assertFalse(knowledge.toHIsKnown()));
        }

        /**
         * The to-hit arm, from a weapon whose to-hit has departed from what its kind prescribes.
         */
        @Test
        @DisplayName("a weapon off its kind's to-hit teaches the to-hit rune")
        void toHitAloneTeachesOnlyItsOwnRune() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_WEAPON),
                    weaponWithToHit(5)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertAll(
                    () -> assertTrue(knowledge.toHIsKnown()),
                    () -> assertFalse(knowledge.toDIsKnown()));
        }

        /**
         * The predicate the to-hit arm goes through, seen from the outside. An item sitting at
         * exactly what its kind prescribes is standard, so nothing is learned from it — which is
         * what stops a suit of chain mail teaching to-hit to everyone who swings a sword while
         * wearing one.
         */
        @Test
        @DisplayName("an item at its kind's to-hit teaches nothing")
        void anItemAtItsKindsToHitTeachesNothing() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_WEAPON),
                    weaponWithToHit(0)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertFalse(knowledge.toHIsKnown());
        }

        /**
         * The leading guard is a conjunction, so knowing one of the two is not enough to stop the
         * walk. A player who has worked out their weapon's damage still has accuracy to learn.
         */
        @Test
        @DisplayName("knowing to-damage alone does not stop the walk")
        void oneRuneKnownIsNotEnoughToStop() throws Exception {
            knowledge.learnToD();
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_WEAPON),
                    weaponWithToHit(5)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertTrue(knowledge.toHIsKnown());
        }

        /**
         * The other half of the same guard.
         */
        @Test
        @DisplayName("knowing to-hit alone does not stop the walk")
        void theOtherRuneKnownIsNotEnoughEither() throws Exception {
            knowledge.learnToH();
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemWithCombat(0, 6, 0)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertTrue(knowledge.toDIsKnown());
        }

        /**
         * Both known: now there is nothing left to learn, so no slot is read at all and the curse on
         * the worn item stays hidden.
         */
        @Test
        @DisplayName("nothing is examined once both runes are known")
        void bothKnownStopsAtTheDoor() throws Exception {
            knowledge.learnToH();
            knowledge.learnToD();
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_WEAPON),
                    itemWith(cursed(enveloping, 20))));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertFalse(knowledge.curseIsKnown(enveloping));
            assertTrue(bus.messages.isEmpty());
        }

        /**
         * The guard at the foot of the loop, which is the same conjunction. The first slot teaches
         * both runes at once — {@code enveloping} carries a to-hit and a to-damage — so the second
         * cursed item is never reached.
         */
        @Test
        @DisplayName("the walk stops at the first slot that teaches both")
        void stopsOnceBothAreKnown() throws Exception {
            set(player, "body", bodyOf(
                    List.of(EquipmentSlotsEnum.EQUIP_WEAPON, EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemWith(cursed(enveloping, 20)),
                    itemWith(cursed(siren, 20))));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertAll(
                    () -> assertTrue(knowledge.toHIsKnown()),
                    () -> assertTrue(knowledge.toDIsKnown()),
                    () -> assertTrue(knowledge.curseIsKnown(enveloping)),
                    () -> assertFalse(knowledge.curseIsKnown(siren)));
        }

        /**
         * A curse that changes only one of the two teaches only that one, and the walk goes on to
         * the next slot looking for the other.
         */
        @Test
        @DisplayName("a slot that teaches one rune does not end the walk")
        void aPartialSlotDoesNotEndTheWalk() throws Exception {
            set(player, "body", bodyOf(
                    List.of(EquipmentSlotsEnum.EQUIP_GLOVES, EquipmentSlotsEnum.EQUIP_BOOTS),
                    itemWithCombat(0, 6, 0),
                    weaponWithToHit(5)));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertAll(
                    () -> assertTrue(knowledge.toDIsKnown()),
                    () -> assertTrue(knowledge.toHIsKnown()));
        }

        /**
         * The shape's two figures are tested independently rather than as alternatives, so a shape
         * granting both teaches both.
         */
        @Test
        @DisplayName("an assumed shape teaches both its figures")
        void shapeTeachesBoth() throws Exception {
            set(player, "body", emptyBody());
            set(player, "shape", shapeWith(0, 5, 7));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertAll(
                    () -> assertTrue(knowledge.toHIsKnown()),
                    () -> assertTrue(knowledge.toDIsKnown()));
        }

        /**
         * And a shape granting one teaches only that one — the two branches are not an
         * if/else pair.
         */
        @Test
        @DisplayName("a shape with only one of the two teaches only that one")
        void shapeWithOneFigureTeachesOnlyIt() throws Exception {
            set(player, "body", emptyBody());
            set(player, "shape", shapeWith(0, 0, 7));

            PlayerKnowledge.equipLearnOnMeleeAttack(player);

            assertAll(
                    () -> assertTrue(knowledge.toDIsKnown()),
                    () -> assertFalse(knowledge.toHIsKnown()));
        }

        @Test
        @DisplayName("no shape is not an error")
        void noShape() throws Exception {
            set(player, "body", emptyBody());

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnOnMeleeAttack(player));

            assertAll(
                    () -> assertFalse(knowledge.toHIsKnown()),
                    () -> assertFalse(knowledge.toDIsKnown()));
        }
    }

    /**
     * The wrappers, which are the intended way in: each resolves its property to the rune for the
     * property's <em>group</em> before learning it.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the learn wrappers")
    class Wrappers {

        @Test
        @DisplayName("learnBrand learns the brand and announces it")
        void learnBrand() {
            PlayerKnowledge.learnBrand(player, weakAcid);

            assertTrue(PlayerKnowledge.knowsBrand(player, weakAcid));
            assertEquals(1, bus.messages.size());
        }

        /**
         * The reason the wrapper cannot be skipped. {@link Rune#runeIndex(Brand)} matches by name,
         * so a strong acid brand finds the acid rune even though the rune holds the weak one; a
         * caller that built its own rune from the brand it had would learn only that brand.
         */
        @Test
        @DisplayName("learnBrand resolves a strength that no rune holds")
        void learnBrandResolvesTheGroup() {
            PlayerKnowledge.learnBrand(player, strongAcid);

            assertTrue(PlayerKnowledge.knowsBrand(player, strongAcid));
            assertTrue(PlayerKnowledge.knowsBrand(player, weakAcid));
        }

        @Test
        @DisplayName("learnBrand does nothing the second time")
        void learnBrandIsIdempotent() {
            PlayerKnowledge.learnBrand(player, weakAcid);
            PlayerKnowledge.learnBrand(player, weakAcid);

            assertEquals(1, bus.messages.size());
        }

        @Test
        @DisplayName("knowsBrand reports the knowledge, not the item")
        void knowsBrand() {
            assertFalse(PlayerKnowledge.knowsBrand(player, weakAcid));

            PlayerKnowledge.learnBrand(player, weakAcid);

            assertTrue(PlayerKnowledge.knowsBrand(player, weakAcid));
        }

        @Test
        @DisplayName("learnCurse learns the curse and announces it")
        void learnCurse() {
            PlayerKnowledge.learnCurse(player, siren);

            assertTrue(knowledge.curseIsKnown(siren));
            assertEquals(1, bus.messages.size());
            assertEquals("You have learned the rune of siren curse.", bus.messages.get(0).message());
        }

        /**
         * C resolves the curse by name rather than by identity, so one rebuilt from a savefile or a
         * parser is still recognised. An identity match would pass every other test here and fail
         * only in the game.
         */
        @Test
        @DisplayName("learnCurse matches by name, not identity")
        void learnCurseMatchesByName() {
            Curse rebuilt = new Curse("siren", List.of(), 0, null, objectFlags(), Map.of(), Map.of(),
                    0, 0, 0, List.of(), objectFlags(), "wakes monsters", "The curse fires.");

            PlayerKnowledge.learnCurse(player, rebuilt);

            assertTrue(knowledge.curseIsKnown(siren));
        }

        /**
         * A curse with no rune reaches {@link PlayerKnowledge#learnRune} as null, where C's guard is
         * {@code index >= 0}. Nothing is learned and nothing is said.
         */
        @Test
        @DisplayName("learnCurse survives a curse with no rune")
        void learnCurseWithoutARune() {
            Curse unknown = new Curse("nowhere", List.of(), 0, null, objectFlags(), Map.of(), Map.of(),
                    0, 0, 0, List.of(), objectFlags(), "does nothing", "Nothing happens.");

            PlayerKnowledge.learnCurse(player, unknown);

            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("learnSlay learns the slay and announces it")
        void learnSlay() {
            PlayerKnowledge.learnSlay(player, evil3);

            assertTrue(PlayerKnowledge.knowsSlay(player, evil3));
            assertEquals(1, bus.messages.size());
            assertEquals("You have learned the rune of slay evil.", bus.messages.get(0).message());
        }

        /**
         * The slay counterpart of {@code learnBrandResolvesTheGroup}, and the reason
         * {@link Rune#runeIndex(Slay)} cannot match on a name: the rune holds {@code evil3}, so a
         * player who has just been bitten by {@code evil5} finds it only through
         * {@link Slay#sameMonsterSlain}.
         */
        @Test
        @DisplayName("learnSlay resolves a strength that no rune holds")
        void learnSlayResolvesTheGroup() {
            PlayerKnowledge.learnSlay(player, evil5);

            assertTrue(PlayerKnowledge.knowsSlay(player, evil5));
            assertTrue(PlayerKnowledge.knowsSlay(player, evil3));
        }

        @Test
        @DisplayName("learnSlay does nothing the second time")
        void learnSlayIsIdempotent() {
            PlayerKnowledge.learnSlay(player, evil3);
            PlayerKnowledge.learnSlay(player, evil3);

            assertEquals(1, bus.messages.size());
        }

        @Test
        @DisplayName("knowsSlay reports the knowledge, not the weapon")
        void knowsSlay() {
            assertFalse(PlayerKnowledge.knowsSlay(player, evil3));

            PlayerKnowledge.learnSlay(player, evil3);

            assertTrue(PlayerKnowledge.knowsSlay(player, evil3));
        }

        @Test
        @DisplayName("knowsCurse reports the knowledge, not the item")
        void knowsCurse() {
            assertFalse(PlayerKnowledge.knowsCurse(player, siren));

            PlayerKnowledge.learnCurse(player, siren);

            assertTrue(PlayerKnowledge.knowsCurse(player, siren));
        }

        /**
         * Curses are never grouped, so unlike a brand or a slay there is no second curse to reveal.
         */
        @Test
        @DisplayName("knowsCurse does not answer for a curse never learned")
        void knowsCurseIsNotShared() {
            Curse other = new Curse("teleportation", List.of(), 0, null, objectFlags(), Map.of(),
                    Map.of(), 0, 0, 0, List.of(), objectFlags(), "teleports", "The curse fires.");

            PlayerKnowledge.learnCurse(player, siren);

            assertFalse(PlayerKnowledge.knowsCurse(player, other));
        }

        @Test
        @DisplayName("learnFlag learns the flag and announces it")
        void learnFlag() {
            PlayerKnowledge.learnFlag(player, ObjectFlag.OF_SUST_STR);

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertEquals(1, bus.messages.size());
            assertEquals("You have learned the rune of sustain strength.",
                    bus.messages.get(0).message());
        }

        /**
         * C's {@code player_learn_flag} is the one wrapper with no already-known guard, relying on
         * {@code of_on} to report whether anything changed. The guard this port adds must not
         * change that answer — a flag learned twice is still announced once, either way.
         */
        @Test
        @DisplayName("learnFlag does nothing the second time")
        void learnFlagIsIdempotent() {
            PlayerKnowledge.learnFlag(player, ObjectFlag.OF_SUST_STR);
            PlayerKnowledge.learnFlag(player, ObjectFlag.OF_SUST_STR);

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR));
            assertEquals(1, bus.messages.size());
        }

        /**
         * Not every flag is a learnable property — {@code init_rune} skips the placeholder
         * subtypes, the ones describing the object rather than the player, and the curse-only ones.
         * Asking about one of those is expected rather than exceptional, since the learning code
         * walks whole flag sets, so it answers with silence instead of a throw. C hands
         * {@code rune_index}'s {@code -1} straight to {@code rune_list[-1]}.
         */
        @Test
        @DisplayName("learnFlag survives a flag with no rune")
        void learnFlagWithoutARune() {
            PlayerKnowledge.learnFlag(player, ObjectFlag.OF_FEATHER);

            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_FEATHER));
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("learnFlag touches only the flag it was given")
        void learnFlagIsNarrow() {
            PlayerKnowledge.learnFlag(player, ObjectFlag.OF_SUST_STR);

            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_INT));
        }
    }

    /**
     * {@link PlayerKnowledge#equipLearnFlag} — the occasion on which a flag gives itself away by doing
     * something.
     *
     * <p>The busiest member of the family upstream, called from some thirty places each naming the
     * flag its own event could have revealed. It is also the one that never stops early: its
     * siblings return the moment their rune is known, but every slot here has bookkeeping to do
     * whether or not anything was learned.
     *
     * <p>Class EquipLearnFlag coded on 260815, commented in full on 260815.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("equipLearnFlag")
    class EquipLearnFlag {

        /**
         * C's guard is {@code if (!flag) return;} against a flag index of zero. The enum equivalent
         * has to name the sentinel rather than test for null, and rejects the far end-marker on the
         * same grounds.
         */
        @Test
        @DisplayName("a null flag or a sentinel is ignored rather than walked")
        void sentinelsAreRefused() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemFlagged(ObjectFlag.OF_AFRAID)));

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnFlag(player, null));
            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_NONE));
            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_MAX));

            assertAll(
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(bus.messages.isEmpty()));
        }

        @Test
        @DisplayName("empty slots are stepped over, not fallen at")
        void emptySlotsAreSkipped() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES), (ItemObject) null));

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID));

            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID));
        }

        /**
         * The first arm: an item that has the flag announces it and teaches its rune. The message
         * comes before the rune here, which is C's order in this function and the opposite of
         * {@link PlayerKnowledge#cursesFindFlags}'s.
         */
        @Test
        @DisplayName("an item carrying the flag teaches and announces it")
        void anItemWithTheFlagTeaches() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemFlagged(ObjectFlag.OF_AFRAID)));

            PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID);

            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertEquals(List.of(
                                    "Your {DESCRIPTION_TAG} makes you tremble.",
                                    "You have learned the rune of fear."),
                            announced()));
        }

        /**
         * The inner guard. Three items with the same flag is an ordinary loadout, and being told
         * about it three times is not.
         */
        @Test
        @DisplayName("two items carrying the same flag announce it once")
        void theFlagIsAnnouncedOnce() throws Exception {
            set(player, "body", bodyOf(
                    List.of(EquipmentSlotsEnum.EQUIP_GLOVES, EquipmentSlotsEnum.EQUIP_BOOTS),
                    itemFlagged(ObjectFlag.OF_AFRAID),
                    itemFlagged(ObjectFlag.OF_AFRAID)));

            PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID);

            assertEquals(List.of(
                            "Your {DESCRIPTION_TAG} makes you tremble.",
                            "You have learned the rune of fear."),
                    announced());
        }

        /**
         * A flag the player already knows says nothing at all — the item has told them nothing they
         * did not have.
         */
        @Test
        @DisplayName("an already-known flag is not announced again")
        void anAlreadyKnownFlagIsSilent() throws Exception {
            knowledge.learnFlag(ObjectFlag.OF_AFRAID);
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemFlagged(ObjectFlag.OF_AFRAID)));

            PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID);

            assertTrue(bus.messages.isEmpty());
        }

        /**
         * The second arm, and the interesting one: an item that does <em>not</em> have the flag has
         * its absence recorded on the known counterpart. That is knowledge too — the item was worn
         * through an event that would have revealed the flag, so the property is now ruled out
         * rather than merely unseen, and enough such rulings identify the item without it ever
         * being examined.
         *
         * <p><b>Which of the two objects receives the flag is the whole point.</b> C writes
         * {@code of_on(obj->known->flags, flag)} — the counterpart, never the item. Writing it to the
         * item instead does not merely fail to record the ruling, it grants the item a property it
         * does not have, and this arm is entered precisely when the item lacks the flag; on the next
         * call the other arm would fire and teach the player a rune off a property that was never
         * there. The second assertion is what holds the two apart.
         *
         * <p>Rewritten on 260816 to read through {@code getKnown().getFlags()} after
         * {@code getKnownFlags} was withdrawn.
         */
        @Test
        @DisplayName("an item without the flag has the flag ruled out on its known counterpart")
        void anItemWithoutTheFlagRecordsTheAbsence() throws Exception {
            ItemObject item = itemFlagged();
            giveKnownCounterpart(item);
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES), item));

            PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID);

            assertAll(
                    () -> assertTrue(item.getKnown().getFlags().has(ObjectFlag.OF_AFRAID)),
                    () -> assertFalse(item.getFlags().has(ObjectFlag.OF_AFRAID)),
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(bus.messages.isEmpty()));
        }

        /**
         * An item with no known counterpart at all cannot record anything, and must not fall over
         * trying. C is entitled to dereference {@code obj->known} because
         * {@code assert(obj->known)} has just run; the port drops those asserts, so the null has to
         * be handled where it would be dereferenced.
         */
        @Test
        @DisplayName("an item with no known counterpart is stepped over, not fallen at")
        void aCounterpartlessItemIsSurvivable() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES), itemFlagged()));

            assertDoesNotThrow(() -> PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID));
        }

        /**
         * The third thing each slot does, and it happens whichever of the first two ran: the flag
         * may be riding on a curse rather than on the item, which is a different question from
         * both.
         */
        @Test
        @DisplayName("a curse carrying the flag is found even when the item does not have it")
        void cursesAreConsultedToo() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemCursed(cursed(cowardice, 20))));

            PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID);

            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(knowledge.curseIsKnown(cowardice)));
        }

        /**
         * The one-element set handed to the curses is built from the flag under discussion, so a
         * curse carrying some <em>other</em> flag is not given away by this event.
         */
        @Test
        @DisplayName("only the flag being learned is looked for on the curses")
        void theCurseSetHoldsOnlyThisFlag() throws Exception {
            set(player, "body", bodyOf(List.of(EquipmentSlotsEnum.EQUIP_GLOVES),
                    itemCursed(cursed(cowardice, 20))));

            PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID);

            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_IMPAIR_HP));
        }

        /**
         * The walk runs to the end of the body. Unlike the {@code equipLearnOn*} methods there is no
         * early return, so a curse in a later slot is still found after an earlier slot has already
         * taught the flag.
         */
        @Test
        @DisplayName("the walk does not stop at the slot that teaches")
        void everySlotIsVisited() throws Exception {
            set(player, "body", bodyOf(
                    List.of(EquipmentSlotsEnum.EQUIP_GLOVES, EquipmentSlotsEnum.EQUIP_BOOTS),
                    itemFlagged(ObjectFlag.OF_AFRAID),
                    itemCursed(cursed(cowardice, 20))));

            PlayerKnowledge.equipLearnFlag(player, ObjectFlag.OF_AFRAID);

            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(knowledge.curseIsKnown(cowardice)));
        }
    }

    /**
     * {@link PlayerKnowledge#learnAllRunes} — the debug, cheat and winner's-dump path.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("learnAllRunes")
    class LearnAllRunes {

        /**
         * Every rune in the registry, whatever its variety. The assertions reach into each corner of
         * the knowledge rather than counting, because "all of them" is the claim being made.
         */
        @Test
        @DisplayName("learns every rune the registry holds")
        void learnsEverything() {
            PlayerKnowledge.learnAllRunes(player);

            assertAll(
                    () -> assertTrue(knowledge.toHIsKnown()),
                    () -> assertTrue(knowledge.toAIsKnown()),
                    () -> assertTrue(knowledge.brandIsKnown(weakAcid)),
                    () -> assertTrue(knowledge.slayIsKnown(evil3)),
                    () -> assertTrue(knowledge.curseIsKnown(siren)),
                    () -> assertTrue(knowledge.curseIsKnown(vulnerability)),
                    () -> assertTrue(knowledge.curseIsKnown(enveloping)),
                    () -> assertTrue(knowledge.resistanceIsKnown(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR)));
        }

        /**
         * A rune the registry does not list is not learned by "all" — the to-damage enchantment has
         * no rune in this fixture, and the walk is over the list rather than over the varieties.
         */
        @Test
        @DisplayName("learns the runes that exist, not the ones that could")
        void onlyWhatTheRegistryHolds() {
            PlayerKnowledge.learnAllRunes(player);

            assertFalse(knowledge.toDIsKnown());
        }

        /**
         * Silent, and for a plainer reason than {@link PlayerKnowledge#learnInnate}'s: several hundred
         * discoveries announced one at a time is not a message but a wall.
         */
        @Test
        @DisplayName("says nothing at all")
        void saysNothing() {
            PlayerKnowledge.learnAllRunes(player);

            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("running it twice changes nothing and still says nothing")
        void isIdempotent() {
            PlayerKnowledge.learnAllRunes(player);
            PlayerKnowledge.learnAllRunes(player);

            assertTrue(knowledge.toAIsKnown());
            assertTrue(knowledge.curseIsKnown(siren));
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * The two high-water marks that sit beside the learning code. Both only ever move one way,
     * which is the whole of their behaviour and the easiest thing to get backwards.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("high-water marks")
    class HighWaterMarks {

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

    /**
     * {@link PlayerKnowledge#cursesFindFlags} — the flag member of the {@code object_curses_find_*}
     * family, and the only one that takes a set.
     *
     * <p>Its siblings each pursue one fixed property, so the caller has nothing to say. Flags are a
     * population, and the caller names which of them the occasion could plausibly have revealed;
     * the method intersects that set with the curse's own flags and works on what survives. Most of
     * what is worth testing here is about that intersection and about which of the two runes gets
     * learned when.
     *
     * <p>Class CursesFindFlags coded on 260815, commented in full on 260815, call sites turned round
     * on 260815 when the method moved to Player.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("cursesFindFlags")
    class CursesFindFlags {

        /**
         * A set naming the flags a test is asking about, standing in for C's {@code test_flags}.
         */
        private Flag<ObjectFlag> testing(ObjectFlag... flags) {
            Flag<ObjectFlag> set = new Flag<>(ObjectFlag.class);
            for (ObjectFlag f : flags) {
                set.on(f);
            }
            return set;
        }

        /**
         * Two runes, not one: the flag that has just shown itself, and the curse that was carrying
         * it.
         */
        @Test
        @DisplayName("a curse carrying a tested flag teaches the flag and the curse")
        void teachesBothRunes() throws Exception {
            ItemObject item = itemCursed(cursed(cowardice, 20));

            boolean learned = PlayerKnowledge.cursesFindFlags(player, item, testing(ObjectFlag.OF_AFRAID));

            assertAll(
                    () -> assertTrue(learned),
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertTrue(knowledge.curseIsKnown(cowardice)));
        }

        /**
         * The intersection, seen from the discarded side. {@code cowardice} carries
         * {@code OF_IMPAIR_HP} as well, but this occasion was not asking about it, so it stays
         * unknown — a curse does not give away everything it holds merely because it gave away one
         * thing.
         */
        @Test
        @DisplayName("only the flags asked about are learned")
        void theIntersectionIsRespected() throws Exception {
            ItemObject item = itemCursed(cursed(cowardice, 20));

            PlayerKnowledge.cursesFindFlags(player, item, testing(ObjectFlag.OF_AFRAID));

            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID)),
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_IMPAIR_HP)));
        }

        /**
         * The intersection runs on a copy, and this is the case that proves it. {@link Flag#inter}
         * is {@code retainAll}, and the flags it is called on belong to the {@link Curse}
         * definition parsed once and shared by every item carrying that curse. Intersecting them in
         * place would delete {@code OF_IMPAIR_HP} from the definition for the rest of the session,
         * so the second item — a different item, the same curse — would find nothing.
         */
        @Test
        @DisplayName("the curse definition survives being intersected against")
        void theCurseDefinitionIsNotMutated() throws Exception {
            PlayerKnowledge.cursesFindFlags(player, itemCursed(cursed(cowardice, 20)),
                    testing(ObjectFlag.OF_AFRAID));

            assertTrue(cowardice.getObjectFlags().has(ObjectFlag.OF_AFRAID));
            assertTrue(cowardice.getObjectFlags().has(ObjectFlag.OF_IMPAIR_HP));
            assertEquals(2, cowardice.getObjectFlags().count(),
                    "the intersection took a copy rather than narrowing the definition");

            PlayerKnowledge.cursesFindFlags(player, itemCursed(cursed(cowardice, 20)),
                    testing(ObjectFlag.OF_IMPAIR_HP));

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_IMPAIR_HP));
        }

        /**
         * The caller's set is not mutated either — {@code equip_learn_flag} builds one and hands it
         * to every slot in turn, so a method that consumed it would work on the first item and on
         * no other.
         */
        @Test
        @DisplayName("the caller's test set survives the call")
        void theTestSetIsNotMutated() throws Exception {
            Flag<ObjectFlag> testFlags = testing(ObjectFlag.OF_AFRAID, ObjectFlag.OF_SUST_STR);

            PlayerKnowledge.cursesFindFlags(player, itemCursed(cursed(cowardice, 20)), testFlags);

            assertTrue(testFlags.has(ObjectFlag.OF_SUST_STR));
        }

        /**
         * The curse's rune is learned inside the flag loop, not beside it. A curse whose flags miss
         * the test set entirely teaches nothing at all — not even that it exists — because the
         * player has had no evidence of it.
         */
        @Test
        @DisplayName("a curse whose flags miss the test set teaches nothing, not even itself")
        void aMissTeachesNothingAtAll() throws Exception {
            ItemObject item = itemCursed(cursed(cowardice, 20));

            boolean learned = PlayerKnowledge.cursesFindFlags(player, item, testing(ObjectFlag.OF_SUST_STR));

            assertAll(
                    () -> assertFalse(learned),
                    () -> assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_SUST_STR)),
                    () -> assertFalse(knowledge.curseIsKnown(cowardice)),
                    () -> assertTrue(bus.messages.isEmpty()));
        }

        /**
         * The other half of that placement: when the flag was already known the curse is still
         * learned, because meeting a curse is knowledge even when its effect was already
         * understood. The return value is about the flag, not the curse, so it is false.
         */
        @Test
        @DisplayName("an already-known flag still teaches the curse")
        void aKnownFlagStillTeachesTheCurse() throws Exception {
            knowledge.learnFlag(ObjectFlag.OF_AFRAID);
            ItemObject item = itemCursed(cursed(cowardice, 20));

            boolean learned = PlayerKnowledge.cursesFindFlags(player, item, testing(ObjectFlag.OF_AFRAID));

            assertAll(
                    () -> assertFalse(learned),
                    () -> assertTrue(knowledge.curseIsKnown(cowardice)));
        }

        /**
         * A curse at zero power is not on the item — {@link CurseData#setPower} with a zero is how
         * a curse is removed, so a zeroed entry can outlive the curse it names.
         */
        @Test
        @DisplayName("a curse at zero power is not on the item")
        void zeroPowerIsNotACurse() throws Exception {
            ItemObject item = itemCursed(cursed(cowardice, 0));

            assertFalse(PlayerKnowledge.cursesFindFlags(player, item, testing(ObjectFlag.OF_AFRAID)));
            assertFalse(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID));
        }

        @Test
        @DisplayName("an uncursed item teaches nothing")
        void uncursedItemIsSilent() throws Exception {
            assertFalse(PlayerKnowledge.cursesFindFlags(player, itemCursed(), testing(ObjectFlag.OF_AFRAID)));
        }

        /**
         * The message is gated on the game having started, where the learning is not. C wraps only
         * {@code flag_message} in {@code p->upkeep->playing}, so knowledge is recorded during
         * character generation and loading without anything being announced into a game that does
         * not yet exist.
         */
        @Test
        @DisplayName("the flag message waits for the game to be under way")
        void theMessageIsSuppressedBeforePlay() throws Exception {
            ItemObject item = itemCursed(cursed(cowardice, 20));

            PlayerKnowledge.cursesFindFlags(player, item, testing(ObjectFlag.OF_AFRAID));

            assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_AFRAID));
            assertEquals(List.of(
                            "You have learned the rune of fear.",
                            "You have learned the rune of cowardice curse."),
                    announced());
        }

        /**
         * And once play is under way, the property's own wording is sent, between the two runes —
         * this method learns then announces, where {@link PlayerKnowledge#equipLearnFlag} announces then
         * learns. Both match their own C originals, which differ.
         */
        @Test
        @DisplayName("in play the property's own wording is announced")
        void theMessageIsSentDuringPlay() throws Exception {
            startPlaying();
            ItemObject item = itemCursed(cursed(cowardice, 20));

            PlayerKnowledge.cursesFindFlags(player, item, testing(ObjectFlag.OF_AFRAID));

            assertEquals(List.of(
                            "You have learned the rune of fear.",
                            "Your {DESCRIPTION_TAG} makes you tremble.",
                            "You have learned the rune of cowardice curse."),
                    announced());
        }

        /**
         * A property with no {@code msg:} is not an error — most flags are learned in silence. The
         * rune is still learned and announced; only the flag's own wording is absent, because there
         * is none.
         */
        @Test
        @DisplayName("a flag whose property has no message is learned silently")
        void aFlagWithNoMessageIsStillLearned() throws Exception {
            startPlaying();
            ItemObject item = itemCursed(cursed(cowardice, 20));

            PlayerKnowledge.cursesFindFlags(player, item, testing(ObjectFlag.OF_IMPAIR_HP));

            assertAll(
                    () -> assertTrue(knowledge.flagIsKnown(ObjectFlag.OF_IMPAIR_HP)),
                    () -> assertEquals(List.of(
                                    "You have learned the rune of impaired hitpoint recovery.",
                                    "You have learned the rune of cowardice curse."),
                            announced()));
        }
    }

}
