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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.testsupport.CurseFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the curse overload of {@code PlayerKnowledge.knowObject}, the port of
 * {@code player_know_object()} ({@code obj-knowledge.c}) run over a curse's own object, which is
 * how {@code update_player_object_knowledge()} reaches {@code curses[i].obj}.
 *
 * <p>The overload is private, so each case teaches the player some runes, calls
 * {@link PlayerKnowledge#updateObjectKnowledge} with one curse in the registry and reads what
 * landed on that curse's known object. Expected values are worked out from the C: every combat,
 * modifier, element and flag figure on the known object is the real figure multiplied by, or gated
 * on, the player's one-or-zero knowledge of it.
 *
 * <p>The case the earlier object-side tests cannot reach is the modifier map's keys. C's loop
 * runs {@code 0 .. OBJ_MOD_MAX - 1}, so the known object's modifiers hold exactly the real
 * modifiers and nothing for {@code OM_NONE} or {@code OM_MAX}.
 *
 * <p>Class PlayerKnowledgeKnowCurseObjectTest coded on 261007, commented in full on 261007.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerKnowledgeKnowCurseObjectTest {

    private static final int REAL_MODIFIERS = ObjectModifier.values().length - 2;

    private Object savedCurses;
    private Chunk savedCave;
    private EventsHandler savedBus;
    private Curse curse;
    private Player player;

    private static Flag<ObjectFlag> flags(ObjectFlag... flags) {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        if (flags.length > 0) result.set(List.of(flags));
        return result;
    }

    private static ElementInfo resist(int level) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(level);
        return info;
    }

    /**
     * One curse carrying a to-armour penalty of 3, a to-hit penalty of 5, a to-damage penalty of 4,
     * a strength penalty of 2, a dexterity bonus of 3, vulnerability to fire and the {@code AFRAID}
     * and {@code FREE_ACT} flags — a figure in every block the overload runs.
     */
    @BeforeEach
    void setUp() {
        Map<ObjectModifier, Integer> modifiers = new HashMap<>();
        modifiers.put(ObjectModifier.OM_STR, -2);
        modifiers.put(ObjectModifier.OM_DEX, 3);
        Map<ElementEnum, ElementInfo> elements = new HashMap<>();
        elements.put(ElementEnum.ELEM_FIRE, resist(-1));

        curse = CurseFixture.curse("weakness", List.of(), 0, null,
                flags(ObjectFlag.OF_AFRAID, ObjectFlag.OF_FREE_ACT), modifiers, elements,
                -5, -4, -3, List.of(), flags(), "weakens the wearer", "The curse fires.", 0);

        savedCurses = ItemFixture.setStatic(ObjectRegistry.class, "curses", new ArrayList<>(List.of(curse)));
        savedCave = GameState.getCave();
        savedBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(new QuietBus());
        GameState.setCave(null);

        player = new Player();
        player.setItemKnowledge(new KnownObject());
    }

    @AfterEach
    void tearDown() {
        ItemFixture.setStatic(ObjectRegistry.class, "curses", savedCurses);
        GameState.setCave(savedCave);
        GameEngine.setEventsBusHandler(savedBus);
    }

    private ItemObject known() {
        PlayerKnowledge.updateObjectKnowledge(player);
        return curse.getItemObject().getKnown();
    }

    @Test
    @DisplayName("a player who knows nothing sees zeros, no flags and no resistances")
    void nothingKnown() {
        ItemObject known = known();

        assertAll(
                () -> assertEquals(0, known.getToAC()),
                () -> assertEquals(0, known.getToHit()),
                () -> assertEquals(0, known.getToDam()),
                () -> assertEquals(0, known.getModifierValue(ObjectModifier.OM_STR)),
                () -> assertEquals(0, known.getModifierValue(ObjectModifier.OM_DEX)),
                () -> assertEquals(0, known.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel()),
                () -> assertFalse(known.hasFlag(ObjectFlag.OF_AFRAID)),
                () -> assertFalse(known.hasFlag(ObjectFlag.OF_FREE_ACT)));
    }

    @Test
    @DisplayName("each combat figure shows only when its own rune is known")
    void combatRunesAreIndependent() {
        player.getItemKnowledge().learnToA();
        ItemObject known = known();

        assertAll(
                () -> assertEquals(-3, known.getToAC()),
                () -> assertEquals(0, known.getToHit()),
                () -> assertEquals(0, known.getToDam()));

        player.getItemKnowledge().learnToD();
        player.getItemKnowledge().learnToH();
        ItemObject allKnown = known();

        assertAll(
                () -> assertEquals(-3, allKnown.getToAC()),
                () -> assertEquals(-5, allKnown.getToHit()),
                () -> assertEquals(-4, allKnown.getToDam()));
    }

    @Test
    @DisplayName("a known modifier shows its real value and an unknown one reads zero")
    void modifiersAreGatedOnKnowledge() {
        player.getItemKnowledge().learnModifier(ObjectModifier.OM_STR);
        ItemObject known = known();

        assertAll(
                () -> assertEquals(-2, known.getModifierValue(ObjectModifier.OM_STR)),
                () -> assertEquals(0, known.getModifierValue(ObjectModifier.OM_DEX)));
    }

    @Test
    @DisplayName("the modifier map holds exactly the real modifiers, never OM_NONE or OM_MAX")
    void modifierMapExcludesSentinels() {
        player.getItemKnowledge().learnModifier(ObjectModifier.OM_STR);
        Map<ObjectModifier, Integer> modifiers = known().getModifiers();

        assertAll(
                () -> assertFalse(modifiers.containsKey(ObjectModifier.OM_NONE)),
                () -> assertFalse(modifiers.containsKey(ObjectModifier.OM_MAX)),
                () -> assertEquals(REAL_MODIFIERS, modifiers.size()),
                () -> assertTrue(modifiers.containsKey(ObjectModifier.OM_DEX)));
    }

    @Test
    @DisplayName("a known resistance copies the curse's level and every other element stays zero")
    void elementsAreGatedOnKnowledge() {
        player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
        Map<ElementEnum, ElementInfo> elements = known().getElInfo();

        assertAll(
                () -> assertEquals(-1, elements.get(ElementEnum.ELEM_FIRE).getResLevel()),
                () -> assertEquals(0, elements.get(ElementEnum.ELEM_COLD).getResLevel()),
                () -> assertFalse(elements.containsKey(ElementEnum.ELEM_NONE)),
                () -> assertFalse(elements.containsKey(ElementEnum.ELEM_MAX)));
    }

    @Test
    @DisplayName("an element the player can read but the curse never names is a blank entry")
    void knownElementTheCurseOmitsIsBlank() {
        player.getItemKnowledge().learnResistance(ElementEnum.ELEM_COLD);
        Map<ElementEnum, ElementInfo> elements = known().getElInfo();

        assertEquals(0, elements.get(ElementEnum.ELEM_COLD).getResLevel());
    }

    @Test
    @DisplayName("only flags the player knows and the curse carries reach the known object")
    void flagsAreTheIntersection() {
        player.getItemKnowledge().learnFlag(ObjectFlag.OF_AFRAID);
        player.getItemKnowledge().learnFlag(ObjectFlag.OF_SLOW_DIGEST);
        ItemObject known = known();

        assertAll(
                () -> assertTrue(known.hasFlag(ObjectFlag.OF_AFRAID)),
                () -> assertFalse(known.hasFlag(ObjectFlag.OF_FREE_ACT)),
                () -> assertFalse(known.hasFlag(ObjectFlag.OF_SLOW_DIGEST)));
    }

    @Test
    @DisplayName("running the update again after learning more only adds to what is shown")
    void repeatedUpdateTracksKnowledge() {
        player.getItemKnowledge().learnModifier(ObjectModifier.OM_STR);
        known();
        player.getItemKnowledge().learnModifier(ObjectModifier.OM_DEX);
        ItemObject known = known();

        assertAll(
                () -> assertEquals(-2, known.getModifierValue(ObjectModifier.OM_STR)),
                () -> assertEquals(3, known.getModifierValue(ObjectModifier.OM_DEX)),
                () -> assertEquals(REAL_MODIFIERS, known.getModifiers().size()));
    }

    private static final class QuietBus implements EventsHandler {
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
        }
    }
}
