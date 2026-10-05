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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.Rune;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerKnowledge#learnFlag}, the port of C's {@code player_learn_flag}, on the one
 * thing that distinguishes it from its sibling wrappers: it has no already-known guard, so C's
 * trailing {@code update_player_object_knowledge} is the call that does the work whenever the rune
 * was already known.
 *
 * <p>The scenario is the one C has a single caller for. {@code uncurse_object} in
 * {@code effect-handler-general.c} switches {@code OF_FRAGILE} on the object and only then calls
 * {@code player_learn_flag}. A player who learned the fragile rune on an earlier failure has nothing
 * new to learn, so {@code player_learn_rune} returns without updating, and the object's known copy
 * shows the new flag only because the wrapper updates once more afterwards. Every expected value
 * here is read off that sequence in C, not off the port.
 *
 * <p>The registries are replaced for the duration of each test and put back afterwards: they are
 * global static state, and a class that left runes behind would change what a later class sees.
 *
 * <p>Class PlayerKnowledgeLearnFlagTest coded on 261004, commented in full on 261004.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerKnowledgeLearnFlagTest {

    private final List<String> messages = new ArrayList<>();
    private Object savedRunes;
    private Object savedBrands;
    private Object savedSlays;
    private Object savedCurses;
    private Chunk savedCave;
    private EventsHandler savedBus;
    private int inventorySignals;

    private Player player;
    private ItemObject item;
    private ItemObject known;

    /**
     * Seeds a registry holding one flag rune, for {@code OF_FRAGILE}, and no brands, slays or curses,
     * then builds a player carrying one sword with an assessed counterpart. The knowledge object is
     * built after the registries are seeded because it sizes its own maps from them.
     */
    @BeforeEach
    void setUp() {
        ObjectProperty fragile = new ObjectProperty(null, null, null, null, 0, 0, null,
                "fragility", null, null, null, null, null);

        savedRunes = ItemFixture.setStatic(ObjectRegistry.class, "allRunes", new ArrayList<>(List.of(
                new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_FRAGILE, fragile)))));
        savedBrands = ItemFixture.setStatic(ObjectRegistry.class, "brands", new ArrayList<>());
        savedSlays = ItemFixture.setStatic(ObjectRegistry.class, "slays", new ArrayList<>());
        savedCurses = ItemFixture.setStatic(ObjectRegistry.class, "curses", new ArrayList<>());
        savedCave = GameState.getCave();
        savedBus = GameEngine.getEventsBusHandler();
        messages.clear();
        inventorySignals = 0;
        GameEngine.setEventsBusHandler(new CountingBus());
        GameState.setCave(null);

        player = new Player();
        player.setItemKnowledge(new KnownObject());

        ObjectKind kind = ItemFixture.kindWithDice(TValue.TV_SWORD);
        item = new ItemObject();
        item.setKind(kind);
        item.settValue(TValue.TV_SWORD);
        known = new ItemObject();
        known.setKind(kind);
        known.settValue(TValue.TV_SWORD);
        known.orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);
        item.setKnown(known);
        player.getGear().insert(item);
    }

    @AfterEach
    void tearDown() {
        ItemFixture.setStatic(ObjectRegistry.class, "allRunes", savedRunes);
        ItemFixture.setStatic(ObjectRegistry.class, "brands", savedBrands);
        ItemFixture.setStatic(ObjectRegistry.class, "slays", savedSlays);
        ItemFixture.setStatic(ObjectRegistry.class, "curses", savedCurses);
        GameState.setCave(savedCave);
        GameEngine.setEventsBusHandler(savedBus);
    }

    /**
     * The failed-uncurse sequence for a player who already knows the fragile rune. C's
     * {@code player_learn_rune} learns nothing and returns before updating, so the flag reaches the
     * object's known copy only through the wrapper's own trailing update.
     */
    @Test
    @DisplayName("a flag already known still reaches the known copy of an object that just gained it")
    void knownFlagRefreshesTheObject() {
        player.getItemKnowledge().learnFlag(ObjectFlag.OF_FRAGILE);
        item.setFlag(ObjectFlag.OF_FRAGILE);
        assertFalse(known.getFlags().has(ObjectFlag.OF_FRAGILE));

        PlayerKnowledge.learnFlag(player, ObjectFlag.OF_FRAGILE);

        assertTrue(known.getFlags().has(ObjectFlag.OF_FRAGILE));
    }

    /**
     * Nothing was learned, so C prints nothing: the message belongs to {@code player_learn_rune}
     * alone, and the trailing update announces nothing.
     */
    @Test
    @DisplayName("a flag already known is not announced again")
    void knownFlagIsSilent() {
        player.getItemKnowledge().learnFlag(ObjectFlag.OF_FRAGILE);
        item.setFlag(ObjectFlag.OF_FRAGILE);

        PlayerKnowledge.learnFlag(player, ObjectFlag.OF_FRAGILE);

        assertTrue(messages.isEmpty());
    }

    /**
     * One update, from the wrapper, when the rune was already known. C's second update is the only
     * one that runs, and it signals the inventory once.
     */
    @Test
    @DisplayName("a flag already known updates once, from the wrapper")
    void knownFlagUpdatesOnce() {
        player.getItemKnowledge().learnFlag(ObjectFlag.OF_FRAGILE);
        item.setFlag(ObjectFlag.OF_FRAGILE);

        PlayerKnowledge.learnFlag(player, ObjectFlag.OF_FRAGILE);

        assertEquals(1, inventorySignals);
    }

    /**
     * Where the rune is new, C updates twice: once inside {@code player_learn_rune} and once more
     * in the wrapper. The port keeps both, since only {@code player_learn_brand} and
     * {@code player_learn_slay} have a guard that makes the second redundant.
     */
    @Test
    @DisplayName("a new flag is learned, announced once, and updates twice as C does")
    void newFlagIsLearned() {
        item.setFlag(ObjectFlag.OF_FRAGILE);

        PlayerKnowledge.learnFlag(player, ObjectFlag.OF_FRAGILE);

        assertTrue(player.getItemKnowledge().flagIsKnown(ObjectFlag.OF_FRAGILE));
        assertEquals(List.of("You have learned the rune of fragility."), messages);
        assertEquals(2, inventorySignals);
        assertTrue(known.getFlags().has(ObjectFlag.OF_FRAGILE));
    }

    /**
     * The flag is known but the object does not carry it, so the known copy must stay without it:
     * the update is a recomputation of {@code obj_k->flags} intersected with {@code obj->flags}, not
     * a copy of the player's flags.
     */
    @Test
    @DisplayName("the known copy shows only the flags the object really carries")
    void knownCopyIsIntersected() {
        PlayerKnowledge.learnFlag(player, ObjectFlag.OF_FRAGILE);

        assertFalse(known.getFlags().has(ObjectFlag.OF_FRAGILE));
    }

    /**
     * A flag with no rune is expected rather than exceptional. {@code learnRune} declines the null
     * rune; the wrapper's update still runs, as C's does after {@code player_learn_rune}.
     */
    @Test
    @DisplayName("a flag with no rune learns nothing and says nothing")
    void flagWithoutARune() {
        PlayerKnowledge.learnFlag(player, ObjectFlag.OF_FEATHER);

        assertFalse(player.getItemKnowledge().flagIsKnown(ObjectFlag.OF_FEATHER));
        assertTrue(messages.isEmpty());
    }

    /**
     * Records the messages sent and counts the inventory signals, the two things
     * {@code update_player_object_knowledge} and {@code player_learn_rune} leave on the bus.
     */
    private final class CountingBus implements EventsHandler {
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
            if (eventType == GameEventType.EVENT_INVENTORY) inventorySignals++;
            if (data instanceof EventDataMessage m) messages.add(m.message());
        }
    }
}
