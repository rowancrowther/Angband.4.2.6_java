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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that {@link Curse#isFullyKnown()} and the two map getters cope with a curse built with
 * {@code null} modifier and element maps, as the guard on {@link Curse#getElInfo()} promises.
 *
 * <p>C has no counterpart: its curse object holds full arrays, so there is nothing to be null.
 * The test pins the port's own contract, which is that an absent map reads as an empty one.
 */
@DisplayName("Curse null maps")
class CurseNullMapsTest {

    /**
     * A curse with every combat figure zero and the given maps, which may be {@code null}.
     */
    private static Curse curse(Map<ObjectModifier, Integer> modifiers,
                               Map<ElementEnum, ElementInfo> elInfo) {
        return new Curse("test curse", List.of(), new ItemObject(), 0, null, new Flag<>(ObjectFlag.class),
                modifiers, elInfo, 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    @Test
    @DisplayName("both getters answer an empty map for a null field")
    void gettersAbsorbNull() {
        Curse curse = curse(null, null);

        assertTrue(curse.getModifiers().isEmpty());
        assertTrue(curse.getElInfo().isEmpty());
    }

    @Test
    @DisplayName("isFullyKnown does not throw with null maps, and a bare curse is fully known")
    void fullyKnownWithNullMaps() {
        Curse curse = curse(null, null);

        assertDoesNotThrow(curse::isFullyKnown);
        assertTrue(curse.isFullyKnown());
    }

    @Test
    @DisplayName("a real resistance the player has not learned stops it being fully known")
    void unlearnedResistanceBlocks() {
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        Curse curse = curse(null, Map.of(ElementEnum.ELEM_FIRE, fire));

        assertFalse(curse.isFullyKnown());
    }

    @Test
    @DisplayName("learning that resistance makes it fully known")
    void learnedResistanceCompletes() {
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        Curse curse = curse(null, Map.of(ElementEnum.ELEM_FIRE, fire));
        curse.putKnownElementInfo(ElementEnum.ELEM_FIRE, fire.copy());

        assertTrue(curse.isFullyKnown());
    }

    @Test
    @DisplayName("setKnownElInfo(null) empties the known view and leaves the real element map alone")
    void nullKnownElInfoLeavesRealMapAlone() {
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        Curse curse = curse(null, Map.of(ElementEnum.ELEM_FIRE, fire));
        curse.putKnownElementInfo(ElementEnum.ELEM_FIRE, fire.copy());

        curse.setKnownElInfo(null);

        assertTrue(curse.getKnownElInfo().isEmpty(), "the known view is reset");
        assertEquals(1, curse.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel(),
                "the curse's real resistance survives");
        assertFalse(curse.isFullyKnown(), "so the forgotten resistance is unknown again");
    }

    @Test
    @DisplayName("an unlearned modifier stops it being fully known; learning it completes")
    void modifierKnowledge() {
        Curse curse = curse(Map.of(ObjectModifier.OM_STEALTH, -3), null);

        assertFalse(curse.isFullyKnown());

        curse.setKnownModifiers(Map.of(ObjectModifier.OM_STEALTH, -3));

        assertEquals(true, curse.isFullyKnown());
    }
}
