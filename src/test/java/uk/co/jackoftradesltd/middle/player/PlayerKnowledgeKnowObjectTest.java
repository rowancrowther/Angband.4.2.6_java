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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.CurseData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.Slay;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerKnowledge#knowObject(Player, ItemObject)}, the port of C's
 * {@code player_know_object} for real objects — the step that rewrites an object's known
 * counterpart from what the player has learned to read.
 *
 * <p>Every expected value here is derived from C's function, not from the port. The shape C
 * guarantees is the thing under test: afterwards, each property on the counterpart is the object's
 * own property if and only if the player's knowledge entitles them to read it, and absent or zero
 * otherwise. Cases are therefore written as "the item carries X, the player knows Y, the counterpart
 * started as Z" and the answer is read off C's loops.
 *
 * <p><b>Brands and slays have the most cases</b> because they are where the port changed shape. C
 * walks every index and gates on the object's own array; the port walks the registry. The cases
 * that would tell the two apart are the ones that matter — a brand carried by an ego and not by
 * the kind, a stale entry already on the counterpart, a counterpart whose set is null, and a
 * brand learned through its twin of another strength.
 *
 * <p><b>The "On the ground" report is judged on the real level.</b> C tests the {@code cave} global,
 * not {@code p->cave}, so the cases build a level, put the object on the player's grid, and leave
 * the player's own known cave null on purpose.
 *
 * <p>The registries are swapped for the duration of each test and put back afterwards: they are
 * global static state, and a class that left brands behind would change what a later class sees.
 *
 * <p>Not covered here: the ego branch, which needs an {@code EgoItem} and is exercised through
 * {@code knowsEgo}'s own tests, and {@link Curse}-side knowledge, which is the private
 * {@code knowObject(Player, Curse)} overload.
 *
 * <p>Class PlayerKnowledgeKnowObjectTest coded on 261003, commented in full on 261003.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerKnowledgeKnowObjectTest {

    private static final Loc GRID = Loc.row(2).col(3);
    private final List<String> messages = new ArrayList<>();
    private Brand acidWeak;
    private Brand acidStrong;
    private Brand fire;
    private Slay evil2;
    private Slay evil3;
    private Slay animal;
    private Curse siren;
    private Curse cowardice;
    private Object savedBrands;
    private Object savedSlays;
    private Object savedCurses;
    private Chunk savedCave;
    private EventsHandler savedBus;
    private ObjectKind kind;
    private ItemObject item;
    private ItemObject known;
    private Player player;

    private static Flag<ObjectFlag> flags(ObjectFlag... flags) {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        if (flags.length > 0) result.set(List.of(flags));
        return result;
    }

    /**
     * Every element map the fixture builds includes the two sentinels, which {@code knowObject}
     * never puts on the counterpart — so an item carrying them could never read as fully known. A real
     * item's map holds only real elements.
     */
    private static Map<ElementEnum, ElementInfo> realElements() {
        Map<ElementEnum, ElementInfo> elements = ItemFixture.allElements();
        elements.remove(ElementEnum.ELEM_NONE);
        elements.remove(ElementEnum.ELEM_MAX);
        return elements;
    }

    private static Set<String> brandNames(ItemObject o) {
        Set<String> names = new TreeSet<>();
        for (Brand b : o.getBrands()) names.add(b.getCode());
        return names;
    }

    private static Set<String> slayCodes(ItemObject o) {
        Set<String> names = new TreeSet<>();
        for (Slay s : o.getSlays()) names.add(s.getCode());
        return names;
    }

    /**
     * Two strengths of one brand and a second brand, two strengths of one slay and a second slay,
     * and two curses — enough to tell "the item's" from "the kind's", "known" from "carried", and a
     * group from a single member. The knowledge object is built after the registries are seeded,
     * because it sizes its own maps from them.
     */
    @BeforeEach
    void setUp() {
        acidWeak = new Brand("ACID_2", "acid", "burns", null, null, 2, 2, 1);
        acidStrong = new Brand("ACID_3", "acid", "burns", null, null, 3, 3, 1);
        fire = new Brand("FIRE_2", "fire", "burns", null, null, 2, 2, 1);
        evil2 = new Slay("EVIL_2", "evil", null, "smite", "smite", MonsterRaceFlag.RF_EVIL, 2, 2, 1);
        evil3 = new Slay("EVIL_3", "evil", null, "smite", "smite", MonsterRaceFlag.RF_EVIL, 3, 3, 1);
        animal = new Slay("ANIMAL_2", "animal", null, "smite", "smite", MonsterRaceFlag.RF_ANIMAL, 2, 2, 1);
        siren = CurseFixture.curse("siren", List.of(), 0, null, flags(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), flags(), "wakes monsters", "The curse fires.", 0);
        cowardice = CurseFixture.curse("cowardice", List.of(), 0, null, flags(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), flags(), "unnerves the wearer", "The curse fires.", 1);

        savedBrands = ItemFixture.setStatic(ObjectRegistry.class, "brands",
                new ArrayList<>(List.of(acidWeak, acidStrong, fire)));
        savedSlays = ItemFixture.setStatic(ObjectRegistry.class, "slays",
                new ArrayList<>(List.of(evil2, evil3, animal)));
        savedCurses = ItemFixture.setStatic(ObjectRegistry.class, "curses",
                new ArrayList<>(List.of(siren, cowardice)));
        savedCave = GameState.getCave();
        savedBus = GameEngine.getEventsBusHandler();
        messages.clear();
        GameEngine.setEventsBusHandler(new MessageBus());
        GameState.setCave(null);

        player = new Player();
        player.setItemKnowledge(new KnownObject());
        buildItem(TValue.TV_SWORD);
    }

    @AfterEach
    void tearDown() {
        ItemFixture.setStatic(ObjectRegistry.class, "brands", savedBrands);
        ItemFixture.setStatic(ObjectRegistry.class, "slays", savedSlays);
        ItemFixture.setStatic(ObjectRegistry.class, "curses", savedCurses);
        GameState.setCave(savedCave);
        GameEngine.setEventsBusHandler(savedBus);
    }

    /**
     * An item of the given type with an assessed counterpart, both sharing one kind.
     */
    private void buildItem(TValue tValue) {
        kind = ItemFixture.kindWithDice(tValue);
        item = new ItemObject();
        item.setKind(kind);
        item.settValue(tValue);
        known = new ItemObject();
        known.setKind(kind);
        known.settValue(tValue);
        known.orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);
        item.setKnown(known);
    }

    /**
     * {@code PlayerKnowledge.knowObject} leaves every message it sends on the event bus, which is
     * the only way to see them from outside.
     */
    private final class MessageBus implements EventsHandler {
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
            if (data instanceof EventDataMessage m) messages.add(m.message());
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("guards and early returns")
    class Guards {

        @Test
        @DisplayName("a null object is nothing to do")
        void nullItem() {
            assertDoesNotThrow(() -> PlayerKnowledge.knowObject(player, null));
        }

        @Test
        @DisplayName("an object with no counterpart is nothing to do")
        void nullKnown() {
            item.setKnown(null);
            assertDoesNotThrow(() -> PlayerKnowledge.knowObject(player, item));
        }

        @Test
        @DisplayName("a counterpart of a different kind is left exactly as it was")
        void kindMismatch() {
            known.setKind(ItemFixture.kindWithDice(TValue.TV_SWORD));
            item.setDamageDice(2);
            known.setDamageDice(99);
            player.getItemKnowledge().setDD(1);

            PlayerKnowledge.knowObject(player, item);

            assertEquals(99, known.getDamageDice());
        }

        @Test
        @DisplayName("a distant object only gets base properties, not its enchantment")
        void distantObject() {
            // getNotice() hands out a copy, so the counterpart is rebuilt without the notice instead
            known = new ItemObject();
            known.setKind(kind);
            known.settValue(TValue.TV_SWORD);
            item.setKnown(known);
            item.setNumber(3);
            item.setToAC(7);
            known.setToAC(5);
            player.getItemKnowledge().learnToA();

            PlayerKnowledge.knowObject(player, item);

            assertEquals(3, known.getNumber());
            assertEquals(5, known.getToAC());
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("dice, armour class and combat bonuses")
    class Combat {

        @Test
        @DisplayName("dice are copied only for the half the player can read")
        void dice() {
            item.setDamageDice(2);
            item.setDamageSides(5);
            player.getItemKnowledge().setDD(1);
            player.getItemKnowledge().setDS(0);

            PlayerKnowledge.knowObject(player, item);

            assertEquals(2, known.getDamageDice());
            assertEquals(0, known.getDamageSides());
        }

        @Test
        @DisplayName("base armour class is shown only once the player can read it")
        void baseArmourClass() {
            item.setBaseAC(4);

            PlayerKnowledge.knowObject(player, item);
            assertEquals(0, known.getBaseAC());

            player.getItemKnowledge().setAC(1);
            PlayerKnowledge.knowObject(player, item);
            assertEquals(4, known.getBaseAC());
        }

        @Test
        @DisplayName("pval is copied whatever the player knows, except on chests")
        void pval() {
            item.setpValue(9);
            known.setpValue(0);
            PlayerKnowledge.knowObject(player, item);
            assertEquals(9, known.getpValue());

            buildItem(TValue.TV_CHEST);
            item.setpValue(9);
            known.setpValue(-1);
            PlayerKnowledge.knowObject(player, item);
            assertEquals(-1, known.getpValue());
        }

        @Test
        @DisplayName("to-AC, to-hit and to-damage follow their own runes independently")
        void bonuses() {
            item.setToAC(3);
            item.setToHit(5);
            item.setToDam(4);

            PlayerKnowledge.knowObject(player, item);
            assertEquals(0, known.getToAC());
            assertEquals(0, known.getToHit());
            assertEquals(0, known.getToDam());

            player.getItemKnowledge().learnToA();
            player.getItemKnowledge().learnToD();
            PlayerKnowledge.knowObject(player, item);
            assertEquals(3, known.getToAC());
            assertEquals(0, known.getToHit());
            assertEquals(4, known.getToDam());

            player.getItemKnowledge().learnToH();
            PlayerKnowledge.knowObject(player, item);
            assertEquals(5, known.getToHit());
        }

        @Test
        @DisplayName("a bonus the player can no longer read goes back to zero")
        void bonusIsRecomputedNotKept() {
            item.setToAC(3);
            known.setToAC(3);

            PlayerKnowledge.knowObject(player, item);

            assertEquals(0, known.getToAC());
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("modifiers, elements and flags")
    class Properties {

        @Test
        @DisplayName("only modifiers the player can read are shown, and the rest read zero")
        void modifiers() {
            item.putModifier(ObjectModifier.OM_STR, 2);
            item.putModifier(ObjectModifier.OM_DEX, 1);
            player.getItemKnowledge().learnModifier(ObjectModifier.OM_STR);

            PlayerKnowledge.knowObject(player, item);

            assertEquals(2, known.getModifiers().get(ObjectModifier.OM_STR));
            assertEquals(0, known.getModifiers().get(ObjectModifier.OM_DEX));
        }

        @Test
        @DisplayName("the modifier map has an entry for every real modifier and none for the sentinels")
        void modifierSentinels() {
            PlayerKnowledge.knowObject(player, item);

            assertFalse(known.getModifiers().containsKey(ObjectModifier.OM_NONE));
            assertFalse(known.getModifiers().containsKey(ObjectModifier.OM_MAX));
            assertTrue(known.getModifiers().containsKey(ObjectModifier.OM_STR));
        }

        @Test
        @DisplayName("a resistance is copied when learned and zeroed when not")
        void elements() {
            Map<ElementEnum, ElementInfo> elements = realElements();
            elements.get(ElementEnum.ELEM_FIRE).setResLevel(1);
            elements.get(ElementEnum.ELEM_COLD).setResLevel(-1);
            item.setElInfo(elements);
            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);

            PlayerKnowledge.knowObject(player, item);

            assertEquals(1, known.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
            assertEquals(0, known.getElInfo().get(ElementEnum.ELEM_COLD).getResLevel());
        }

        @Test
        @DisplayName("an element the player can read but the item never names is a blank entry")
        void elementTheItemLacks() {
            item.setElInfo(new LinkedHashMap<>());
            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_ACID);

            assertDoesNotThrow(() -> PlayerKnowledge.knowObject(player, item));

            assertEquals(0, known.getElInfo().get(ElementEnum.ELEM_ACID).getResLevel());
        }

        @Test
        @DisplayName("flags shown are the intersection of the player's and the item's")
        void flagsAreAnIntersection() {
            item.setFlagsTo(flags(ObjectFlag.OF_FREE_ACT, ObjectFlag.OF_SEE_INVIS));
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FREE_ACT);
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_SLOW_DIGEST);

            PlayerKnowledge.knowObject(player, item);

            assertTrue(known.hasFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(known.hasFlag(ObjectFlag.OF_SEE_INVIS));
            assertFalse(known.hasFlag(ObjectFlag.OF_SLOW_DIGEST));
        }

        @Test
        @DisplayName("narrowing the flags does not damage the player's own knowledge")
        void playersFlagsSurvive() {
            item.setFlagsTo(flags(ObjectFlag.OF_FREE_ACT));
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FREE_ACT);
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_SLOW_DIGEST);

            PlayerKnowledge.knowObject(player, item);

            assertTrue(player.getItemKnowledge().flagIsKnown(ObjectFlag.OF_SLOW_DIGEST));
        }

        @Test
        @DisplayName("an object with no kind stops after the flags")
        void curseObjectStopsAfterFlags() {
            known.setKind(null);
            item.setKind(null);
            item.setFlagsTo(flags(ObjectFlag.OF_FREE_ACT));
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FREE_ACT);
            ItemFixture.set(known, "brands", null);

            assertDoesNotThrow(() -> PlayerKnowledge.knowObject(player, item));

            assertTrue(known.hasFlag(ObjectFlag.OF_FREE_ACT));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("brands: the item's brands, filtered by what the player can read")
    class Brands {

        private Set<String> run(Set<Brand> onItem, Set<Brand> onCounterpart, Set<Brand> playerLearns) {
            for (Brand b : onItem) item.addBrand(b);
            for (Brand b : onCounterpart) known.addBrand(b);
            for (Brand b : playerLearns) player.getItemKnowledge().learnBrand(b);
            PlayerKnowledge.knowObject(player, item);
            return brandNames(known);
        }

        @Test
        @DisplayName("a carried, known brand is shown")
        void carriedAndKnown() {
            assertEquals(Set.of("FIRE_2"), run(Set.of(fire), Set.of(), Set.of(fire)));
        }

        @Test
        @DisplayName("a carried brand the player cannot read is not shown")
        void carriedNotKnown() {
            assertEquals(Set.of(), run(Set.of(fire), Set.of(), Set.of()));
        }

        @Test
        @DisplayName("a brand the kind does not list is still learned when the item carries it")
        void brandFromAnEgoNotTheKind() {
            assertFalse(kind.getBrands().contains(fire));
            assertEquals(Set.of("FIRE_2"), run(Set.of(fire), Set.of(), Set.of(fire)));
        }

        @Test
        @DisplayName("only the known one of two carried brands is shown")
        void oneOfTwoCarried() {
            assertEquals(Set.of("FIRE_2"), run(Set.of(fire, acidWeak), Set.of(), Set.of(fire)));
        }

        @Test
        @DisplayName("a brand on the counterpart that the item does not carry is removed even if known")
        void staleBrandIsRemoved() {
            assertEquals(Set.of("ACID_2"),
                    run(Set.of(acidWeak), Set.of(acidWeak, fire), Set.of(acidWeak, fire)));
        }

        @Test
        @DisplayName("a shown brand the player can no longer read is removed")
        void unknownBrandIsRemoved() {
            assertEquals(Set.of(), run(Set.of(acidWeak), Set.of(acidWeak), Set.of()));
        }

        @Test
        @DisplayName("an unknown carried brand comes off while a known one stays")
        void removesOnlyTheUnknown() {
            assertEquals(Set.of("FIRE_2"),
                    run(Set.of(acidWeak, fire), Set.of(acidWeak, fire), Set.of(fire)));
        }

        @Test
        @DisplayName("learning one strength of a brand reveals the other strength an item carries")
        void twinStrength() {
            assertEquals(Set.of("ACID_3"), run(Set.of(acidStrong), Set.of(), Set.of(acidWeak)));
        }

        @Test
        @DisplayName("an item with no brands clears whatever the counterpart held")
        void itemWithNoBrands() {
            assertEquals(Set.of(), run(Set.of(), Set.of(fire), Set.of(fire)));
        }

        @Test
        @DisplayName("a counterpart whose brand set is null is filled in without error")
        void nullCounterpartSet() {
            ItemFixture.set(known, "brands", null);
            assertEquals(Set.of("FIRE_2"), run(Set.of(fire), Set.of(), Set.of(fire)));
        }

        @Test
        @DisplayName("a null counterpart set with nothing to show is left empty without error")
        void nullCounterpartSetNothingKnown() {
            ItemFixture.set(known, "brands", null);
            assertEquals(Set.of(), run(Set.of(fire), Set.of(), Set.of()));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("slays: the item's slays, filtered by what the player can read")
    class Slays {

        private Set<String> run(Set<Slay> onItem, Set<Slay> onCounterpart, Set<Slay> playerLearns) {
            for (Slay s : onItem) item.addSlay(s);
            for (Slay s : onCounterpart) known.addSlay(s);
            for (Slay s : playerLearns) player.getItemKnowledge().learnSlay(s);
            PlayerKnowledge.knowObject(player, item);
            return slayCodes(known);
        }

        @Test
        @DisplayName("a carried, known slay is shown")
        void carriedAndKnown() {
            assertEquals(Set.of("EVIL_2"), run(Set.of(evil2), Set.of(), Set.of(evil2)));
        }

        @Test
        @DisplayName("a carried slay the player cannot read is not shown")
        void carriedNotKnown() {
            assertEquals(Set.of(), run(Set.of(evil2), Set.of(), Set.of()));
        }

        @Test
        @DisplayName("only the known one of two carried slays is shown")
        void oneOfTwoCarried() {
            assertEquals(Set.of("ANIMAL_2"), run(Set.of(evil2, animal), Set.of(), Set.of(animal)));
        }

        @Test
        @DisplayName("a slay on the counterpart that the item does not carry is removed even if known")
        void staleSlayIsRemoved() {
            assertEquals(Set.of("EVIL_2"),
                    run(Set.of(evil2), Set.of(evil2, animal), Set.of(evil2, animal)));
        }

        @Test
        @DisplayName("a shown slay the player can no longer read is removed")
        void unknownSlayIsRemoved() {
            assertEquals(Set.of(), run(Set.of(evil2), Set.of(evil2), Set.of()));
        }

        @Test
        @DisplayName("an unknown carried slay comes off while a known one stays")
        void removesOnlyTheUnknown() {
            assertEquals(Set.of("ANIMAL_2"),
                    run(Set.of(evil2, animal), Set.of(evil2, animal), Set.of(animal)));
        }

        @Test
        @DisplayName("learning one strength of a slay reveals the other strength an item carries")
        void twinStrength() {
            assertEquals(Set.of("EVIL_3"), run(Set.of(evil3), Set.of(), Set.of(evil2)));
        }

        @Test
        @DisplayName("an item with no slays clears whatever the counterpart held")
        void itemWithNoSlays() {
            assertEquals(Set.of(), run(Set.of(), Set.of(evil2), Set.of(evil2)));
        }

        @Test
        @DisplayName("a counterpart whose slay set is null is filled in without error")
        void nullCounterpartSet() {
            ItemFixture.set(known, "slays", null);
            assertEquals(Set.of("EVIL_2"), run(Set.of(evil2), Set.of(), Set.of(evil2)));
        }

        @Test
        @DisplayName("a null counterpart set with nothing to show is left empty without error")
        void nullCounterpartSetNothingKnown() {
            ItemFixture.set(known, "slays", null);
            assertEquals(Set.of(), run(Set.of(evil2), Set.of(), Set.of()));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("curses")
    class Curses {

        @Test
        @DisplayName("a recognised curse shows its real power with a zero timeout")
        void recognisedCurse() {
            item.addCurse(siren, 40, 7);
            player.getItemKnowledge().learnCurse(siren);

            PlayerKnowledge.knowObject(player, item);

            CurseData shown = known.getCurses().get(siren);
            assertNotNull(shown);
            assertEquals(40, shown.getPower());
            assertEquals(0, shown.getTimeout());
        }

        @Test
        @DisplayName("an unrecognised curse is not shown")
        void unrecognisedCurse() {
            item.addCurse(siren, 40, 7);

            PlayerKnowledge.knowObject(player, item);

            assertTrue(known.getCurses().isEmpty());
        }

        @Test
        @DisplayName("a curse of power zero is not shown even when recognised")
        void powerZero() {
            item.addCurse(siren, 0, 0);
            player.getItemKnowledge().learnCurse(siren);

            PlayerKnowledge.knowObject(player, item);

            assertFalse(known.getCurses().containsKey(siren));
        }

        @Test
        @DisplayName("only the recognised one of two curses is shown")
        void oneOfTwo() {
            item.addCurse(siren, 40, 0);
            item.addCurse(cowardice, 20, 0);
            player.getItemKnowledge().learnCurse(cowardice);

            PlayerKnowledge.knowObject(player, item);

            assertFalse(known.getCurses().containsKey(siren));
            assertEquals(20, known.getCurses().get(cowardice).getPower());
        }

        @Test
        @DisplayName("a curse on the counterpart that the item no longer carries is cleared")
        void staleCurse() {
            known.addCurse(siren, new CurseData(40, 0));

            PlayerKnowledge.knowObject(player, item);

            assertTrue(known.getCurses().isEmpty());
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("effect, awareness and the fully-known copy")
    class EffectAndAwareness {

        @Test
        @DisplayName("an unflavoured non-wearable shows its effect")
        void unflavouredNonWearable() {
            buildItem(TValue.TV_SCROLL);
            ItemFixture.set(kind, "flavour", null);

            PlayerKnowledge.knowObject(player, item);

            assertSame(item.getEffect(), known.getEffect());
        }

        @Test
        @DisplayName("an unaware wearable does not show its effect")
        void unawareWearable() {
            PlayerKnowledge.knowObject(player, item);

            assertNotSame(item.getEffect(), known.getEffect());
        }

        @Test
        @DisplayName("a fully known object shows every element's real flags, not the blank ones")
        void fullyKnownCopiesElementFlags() {
            Map<ElementEnum, ElementInfo> elements = realElements();
            elements.get(ElementEnum.ELEM_FIRE).getFlags().on(ElementInfoEnum.EL_INFO_HATES);
            item.setElInfo(elements);

            PlayerKnowledge.knowObject(player, item);

            assertTrue(item.isFullyKnown());
            assertTrue(known.getElInfo().get(ElementEnum.ELEM_FIRE).getFlags().has(ElementInfoEnum.EL_INFO_HATES));
        }

        @Test
        @DisplayName("an object that is not fully known keeps blank element flags")
        void notFullyKnownKeepsBlankElementFlags() {
            Map<ElementEnum, ElementInfo> elements = realElements();
            elements.get(ElementEnum.ELEM_FIRE).setResLevel(1);
            elements.get(ElementEnum.ELEM_FIRE).getFlags().on(ElementInfoEnum.EL_INFO_HATES);
            item.setElInfo(elements);

            PlayerKnowledge.knowObject(player, item);

            assertFalse(item.isFullyKnown());
            assertFalse(known.getElInfo().get(ElementEnum.ELEM_FIRE).getFlags().has(ElementInfoEnum.EL_INFO_HATES));
        }
    }

    // ------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("reporting newly seen objects")
    class Reporting {

        /**
         * A ring whose runes are all known and whose kind is aware but has never been seen: C's
         * jewellery arm then leaves {@code seen} false, which is what triggers the report.
         */
        private void unseenJewellery() {
            buildItem(TValue.TV_RING);
            kind.setAware(true);
        }

        private Chunk levelWithItemUnderPlayer(boolean onFloor) {
            Chunk level = new Chunk("level", 0, 1, 0, 0, 0, false, 10, 10, 0, 0, 0, 0, 0, 0, null);
            if (onFloor) level.getSquare(GRID).getObjectPile().insert(item);
            ItemFixture.set(player, "grid", GRID);
            return level;
        }

        @Test
        @DisplayName("an unseen object on the player's grid is reported as lying on the ground")
        void onTheGround() {
            unseenJewellery();
            GameState.setCave(levelWithItemUnderPlayer(true));

            PlayerKnowledge.knowObject(player, item);

            assertEquals(List.of("On the ground: {DESCRIPTION_TAG}."), messages);
        }

        @Test
        @DisplayName("the player's own known cave is irrelevant to the report")
        void playersKnownCaveIsIrrelevant() {
            unseenJewellery();
            GameState.setCave(levelWithItemUnderPlayer(true));
            player.setCave(new Chunk("known", 0, 1, 0, 0, 0, false, 10, 10, 0, 0, 0, 0, 0, 0, null));

            PlayerKnowledge.knowObject(player, item);

            assertEquals(List.of("On the ground: {DESCRIPTION_TAG}."), messages);
        }

        @Test
        @DisplayName("an unseen object not on the player's grid is not reported")
        void notOnTheGrid() {
            unseenJewellery();
            GameState.setCave(levelWithItemUnderPlayer(false));

            PlayerKnowledge.knowObject(player, item);

            assertTrue(messages.isEmpty());
        }

        @Test
        @DisplayName("with no level there is nothing to report and nothing breaks")
        void noLevel() {
            unseenJewellery();
            GameState.setCave(null);

            assertDoesNotThrow(() -> PlayerKnowledge.knowObject(player, item));
            assertTrue(messages.isEmpty());
        }

        @Test
        @DisplayName("an object that has been seen before is not reported")
        void alreadySeen() {
            unseenJewellery();
            ItemFixture.set(kind, "everseen", true);
            GameState.setCave(levelWithItemUnderPlayer(true));

            PlayerKnowledge.knowObject(player, item);

            assertTrue(messages.isEmpty());
        }
    }
}
