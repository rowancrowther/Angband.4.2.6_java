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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that {@link ItemObject#isFullyKnown()} and the two map getters cope with a curse object
 * given {@code null} modifier and element maps, as the guard on {@link ItemObject#getElInfo()}
 * promises.
 *
 * <p>C has no counterpart: its curse object holds full arrays, so there is nothing to be null.
 * The test pins the port's own contract, which is that an absent map reads as an empty one.
 */
@DisplayName("Curse null maps")
class CurseNullMapsTest {

    /**
     * Gives {@link ObjectRegistry} an empty curse list. {@code ItemObject.isFullyKnown()} compares
     * the curses of the object and its known twin by walking the registry's curses, and the registry
     * holds {@code null} until something loads or sets them, so a test that reached this class
     * first in the JVM threw a {@link NullPointerException} while one that ran after another test
     * had seeded it passed. None of these tests puts a curse on an item, so empty is the right list.
     */
    @BeforeEach
    void seedRegistry() {
        ObjectRegistry.setCurses(new ArrayList<>());
    }

    /**
     * A curse with every combat figure zero and the given maps, which may be {@code null}.
     */
    private static Curse curse(Map<ObjectModifier, Integer> modifiers,
                               Map<ElementEnum, ElementInfo> elInfo) {
        ItemObject curseObject = new ItemObject();
        curseObject.setModifiers(modifiers);
        curseObject.setElInfo(elInfo);
        curseObject.setKnown(new ItemObject());
        return new Curse("test curse", List.of(), curseObject, List.of(), new Flag<>(ObjectFlag.class), "", 0);
    }

    @Test
    @DisplayName("both getters answer an empty map for a null field")
    void gettersAbsorbNull() {
        Curse curse = curse(null, null);

        assertTrue(curse.getItemObject().getModifiers().isEmpty());
        assertTrue(curse.getItemObject().getElInfo().isEmpty());
    }

    @Test
    @DisplayName("isFullyKnown does not throw with null maps, and a bare curse is fully known")
    void fullyKnownWithNullMaps() {
        Curse curse = curse(null, null);

        assertDoesNotThrow(() -> curse.getItemObject().isFullyKnown());
        assertTrue(curse.getItemObject().isFullyKnown());
    }

    @Test
    @DisplayName("a real resistance the player has not learned stops it being fully known")
    void unlearnedResistanceBlocks() {
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        Curse curse = curse(null, Map.of(ElementEnum.ELEM_FIRE, fire));

        assertFalse(curse.getItemObject().isFullyKnown());
    }

    @Test
    @DisplayName("learning that resistance makes it fully known")
    void learnedResistanceCompletes() {
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        Curse curse = curse(null, Map.of(ElementEnum.ELEM_FIRE, fire));
        curse.getItemObject().getKnown().putElInfo(ElementEnum.ELEM_FIRE, fire.copy());

        assertTrue(curse.getItemObject().isFullyKnown());
    }

    @Test
    @DisplayName("setElInfo(null) empties the known view and leaves the real element map alone")
    void nullKnownElInfoLeavesRealMapAlone() {
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        Curse curse = curse(null, Map.of(ElementEnum.ELEM_FIRE, fire));
        curse.getItemObject().getKnown().putElInfo(ElementEnum.ELEM_FIRE, fire.copy());

        curse.getItemObject().getKnown().setElInfo(null);

        assertTrue(curse.getItemObject().getKnown().getElInfo().isEmpty(), "the known view is reset");
        assertEquals(1, curse.getItemObject().getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel(),
                "the curse's real resistance survives");
        assertFalse(curse.getItemObject().isFullyKnown(), "so the forgotten resistance is unknown again");
    }

    @Test
    @DisplayName("an unlearned modifier stops it being fully known; learning it completes")
    void modifierKnowledge() {
        Curse curse = curse(Map.of(ObjectModifier.OM_STEALTH, -3), null);

        assertFalse(curse.getItemObject().isFullyKnown());

        curse.getItemObject().getKnown().setModifiers(Map.of(ObjectModifier.OM_STEALTH, -3));

        assertEquals(true, curse.getItemObject().isFullyKnown());
    }
}
